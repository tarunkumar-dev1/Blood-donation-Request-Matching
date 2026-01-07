# Form Validation & Error Handling Summary

## Forms Audited & Fixed

### 1. **Registration Form** (`signup.html`)
**Issues Found & Fixed:**
- ✅ Frontend validates Indian phone format before sending
- ✅ Phone is normalized to E.164 (`+91XXXXXXXXXX`) before backend call
- ✅ Password sent via `auth.register()` with BCrypt hashing on backend
- ✅ **FIXED:** After account creation, now redirects to `donor.html` instead of staying on signup
- ✅ Full name stored in localStorage along with phone

**Backend Flow:**
- `AuthServlet.doPost()` with `login=false` creates new user in `user_credentials` table
- Password hashed via BCrypt before storage
- Session created on success

**Success Response:**
```json
{"phone":"​+91XXXXXXXXXX","token":"<uuid>"}
```

---

### 2. **Donor Registration Form** (`donor.html`)
**Issues Found & Fixed:**
- ✅ Form requires: name, date of birth, blood type, phone, email, address, pincode, preferred center
- ✅ **FIXED:** Modal now calls `auth.login()` instead of `auth.register()` for existing users
- ✅ **FIXED:** After login via modal, redirects to `donor.html` (stays on form to complete registration)
- ✅ All form fields sent as `application/x-www-form-urlencoded` to `/api/donors`

**Backend Validation (`DonorServlet`):**
- Required: `name`, `phone`, `email`, `bloodType`
- Optional: `dob`, `address`, `pincode`, `center`, `history`, `surgeries`
- Automatically finds matching blood requests after registration
- Returns donor record + count of admin matches created

**Success Response:**
```json
{
  "message":"Donor saved",
  "donor":{...},
  "matchesFound": 2
}
```

**Common Errors:**
- **400 Bad Request**: Missing required field (name, phone, email, bloodType)
- **500 Internal Server Error**: Database connection issue — check H2 or Oracle credentials

---

### 3. **Blood Match Request Form** (`bmatch.html`)
**Issues Found & Fixed:**
- ✅ Form requires: fullname, email, bloodtype, pincode (location optional)
- ✅ **FIXED:** Modal now normalizes phone to E.164 before `auth.login()`
- ✅ Email validated with regex pattern: `^[A-Za-z0-9+_.-]+@(.+)$`
- ✅ **FIXED:** After login via modal, redirects to `bmatch.html` (stays on form to submit match request)
- ✅ On success, displays matching donors in results box

**Backend Validation (`MatchServlet`):**
- Required: `fullname`, `email`, `bloodtype`
- Optional: `pincode`, `location`, `note`
- Validates email format strictly
- Finds all registered donors matching blood type + pincode
- Creates admin match records for tracking

**Success Response:**
```json
{
  "success":true,
  "request":{...},
  "matches":[{donor1}, {donor2}, ...],
  "adminMatchesCreated": 2,
  "message":"Request submitted successfully"
}
```

**Common Errors:**
- **400 Bad Request**: Missing fullname, email, or bloodtype
- **400 Bad Request**: Invalid email format (must contain `@`)
- **500 Internal Server Error**: Database issue

---

## Login Flow (Used by All Forms)

### User Login Modal (on donor.html, bmatch.html, about.html)
**Flow:**
1. User enters phone and password
2. Frontend validates Indian phone format
3. Normalizes to E.164: `+91XXXXXXXXXX`
4. Calls `POST /api/auth` with `login=true`
5. Backend verifies password via BCrypt
6. Session created with `userPhone` + `token`
7. localStorage updated: `bd_user = {phone}`
8. User badge updated; login button hidden
9. Redirects to appropriate page (donor.html, bmatch.html, etc.)

**Error Cases:**
- **Missing phone or password**: Caught on frontend
- **Invalid Indian number**: Rejected on frontend
- **Invalid credentials**: `401 Unauthorized` with `{"error":"Invalid credentials"}`
- **Database error**: `500 Internal Server Error`

---

## CORS & Session Persistence
- ✅ Frontend fetch includes `credentials: 'include'` for session cookies
- ✅ Backend `SecurityHeadersFilter` echoes `Origin` header for credentialed requests
- ✅ `CorsFilter` handles OPTIONS preflight with necessary headers
- ✅ Session cookies persist across requests (same browser)

---

## Field Mapping & Backend Expectations

| Form | Field Name (Frontend) | Backend Parameter | Endpoint | Validation |
|------|----------------------|-------------------|----------|-----------|
| **Signup** | fullname | — | `auth` | Required, trimmed |
| | phone | phone | `auth` | Indian format, normalized |
| | password | password | `auth` | Required, hashed via BCrypt |
| **Donor** | name | name | `/api/donors` | Required |
| | dob | dob | `/api/donors` | Optional |
| | bloodType | bloodType | `/api/donors` | Required |
| | phone | phone | `/api/donors` | Required |
| | email | email | `/api/donors` | Required |
| | address | address | `/api/donors` | Optional |
| | pincode | pincode | `/api/donors` | Optional |
| | center | center | `/api/donors` | Optional |
| | history | history | `/api/donors` | Optional |
| | surgeries | surgeries | `/api/donors` | Optional |
| **Match** | fullname | fullname | `/api/matches` | Required |
| | email | email | `/api/matches` | Required, email regex |
| | bloodtype | bloodtype | `/api/matches` | Required |
| | pincode | pincode | `/api/matches` | Optional |
| | note | note | `/api/matches` | Optional |

---

## Error Handling Best Practices (Already Implemented)

✅ **Frontend:**
- Phone number validation with user-friendly regex pattern
- Email regex pattern validation
- All required fields checked before submission
- Error messages displayed in alert() dialogs
- Session checked on page load; redirects if logged out

✅ **Backend:**
- Null/blank parameter checks with detailed error messages
- HTTP status codes: 400 (bad request), 401 (unauthorized), 500 (server error)
- Database exceptions caught and logged
- Standardized JSON error responses: `{"error":"..."}` or `{"success":false,"error":"..."}`

---

## Testing Checklist

- [ ] **Signup → Donor Flow**
  - Register with valid phone
  - Verify redirects to donor.html
  - Complete donor registration
  - Confirm donor record created in DB

- [ ] **Donor Modal Login**
  - Click "Login" on donor.html
  - Enter registered credentials
  - Verify modal closes and form is accessible
  - Submit donor registration
  - Confirm database entry

- [ ] **Match Modal Login**
  - Click "Login" on bmatch.html
  - Enter registered credentials
  - Verify modal closes and form is accessible
  - Submit match request
  - Verify matching donors returned

- [ ] **Cross-Origin CORS**
  - Load frontend from `http://localhost:3000` (different port)
  - Perform login; verify session persists
  - Verify no CORS errors in browser console

- [ ] **Error Cases**
  - Submit form with missing fields → validation error
  - Invalid email → error
  - Invalid phone → error
  - Wrong password → 401 Unauthorized
  - DB offline → 500 error

---

## Permanent Fixes Applied (This Session)

1. ✅ **AuthDao**: Phone matching uses exact E.164 lookup (no fragile regex)
2. ✅ **SecurityHeadersFilter**: Dynamic CORS origin support with credentials
3. ✅ **CorsFilter**: Enhanced preflight handling
4. ✅ **Frontend fetch**: Added `credentials: 'include'` for session persistence
5. ✅ **Donor modal**: Changed from `register()` to `login()` call
6. ✅ **Bmatch modal**: Added phone normalization + login redirection
7. ✅ **Signup redirect**: Now goes to donor.html after account creation
8. ✅ **Build**: All compile errors fixed; only lint warnings remain

---

## Notes

- Phone normalization is consistent: Indian numbers → `+91XXXXXXXXXX` (E.164)
- All forms use BCrypt for password hashing (cost 12)
- Session timeout: 30 minutes for admin; default for user sessions
- Database: H2 by default; Oracle via `DB_TYPE=oracle` env var
- Admin credentials: Set via `ADMIN_USER` and `ADMIN_PASS` env vars

