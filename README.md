# Blood Donation — Request Matching

Quick notes to run and use login reliably.

## Backend (Java, WAR)
- Build: `mvn -f "my project/backend/pom.xml" clean package`
- Run (Jetty): `mvn -f "my project/backend/pom.xml" jetty:run`
- API base: `http://localhost:8080/api`

## User Login
- Registration and login are handled via `/api/auth`.
- Phone numbers are normalized to E.164 `+91` with last 10 digits.
- Passwords are stored using BCrypt and verified on login.
- Frontend now sends requests with `credentials: 'include'` and backend echoes the `Origin` so sessions work across origins.

## Admin Login
- Endpoint: `/api/admin/login` (same origin; sessions enabled).
- Set environment variables before deployment:
	- `ADMIN_USER` (default: `admin`)
	- `ADMIN_PASS` (default: `admin007`)

## Troubleshooting
- If frontend is opened via `file://`, use `http://localhost:8080/api` as base; CORS is configured to allow credentials.
- For production, serve frontend from the same origin/domain as the backend for best reliability.
