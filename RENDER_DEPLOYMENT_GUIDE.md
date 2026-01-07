# Deployment Guide for Render.com

## Step 1: Create Render Account
1. Go to https://render.com
2. Click **Sign Up**
3. Connect your **GitHub account**
4. Authorize Render to access your repositories

---

## Step 2: Create New Service
1. Click **New +** → **Web Service**
2. Select your repository: `Blood-donation-Request-Matching`
3. Click **Connect**

---

## Step 3: Configure Service
Fill in the following:

| Field | Value |
|-------|-------|
| **Name** | blood-donation-api |
| **Environment** | Docker |
| **Region** | Choose closest to you |
| **Branch** | main |
| **Build Command** | `mvn -f "my project/backend/pom.xml" clean package` |
| **Start Command** | `java -cp "my project/backend/target/classes:my project/backend/target/dependency/*" -Dport=$PORT org.eclipse.jetty.ee10.maven.plugin.MavenWebAppContext` |
| **Plan** | Free (or Paid if needed) |

---

## Step 4: Set Environment Variables
1. Scroll to **Environment**
2. Click **Add Environment Variable**
3. Add each:

```
ADMIN_USER = bloodadmin
ADMIN_PASS = BloodApp2026!Secure
DB_TYPE = h2
PORT = $PORT (auto-filled)
```

---

## Step 5: Deploy
1. Click **Create Web Service**
2. Wait for build and deployment (5-10 minutes)
3. You'll see a URL like: `https://blood-donation-api.onrender.com`

---

## Step 6: Configure Frontend
Update your frontend files to use the deployed backend:

In `js/api-client.js` and `backend/src/main/webapp/js/api-client.js`:
```javascript
const defaultBase = 'https://blood-donation-api.onrender.com/api';
```

---

## Step 7: Verify Deployment
```powershell
# Test health endpoint
Invoke-WebRequest -Uri "https://blood-donation-api.onrender.com/api/health"

# Test registration
Invoke-WebRequest -Method POST `
  -Uri "https://blood-donation-api.onrender.com/api/auth" `
  -Body "phone=+919876543210&password=Test123&login=false" `
  -ContentType "application/x-www-form-urlencoded"
```

---

## Common Issues & Solutions

### Build Fails
- Check Maven version compatibility
- Ensure pom.xml is valid
- Check build logs in Render dashboard

### Database Not Persisting
- H2 database will be reset on redeploy
- Consider: PostgreSQL on Render (free tier available)
- Or: Use managed database service

### Slow First Request
- Free tier apps sleep after 15 minutes of inactivity
- First request after sleep takes 30 seconds
- Upgrade to paid for always-on

### CORS Issues
- Already configured in your code
- No changes needed

---

## Optional: Upgrade Database (Recommended)
1. Create **PostgreSQL** database on Render (free tier)
2. Set environment variables:
   ```
   DB_TYPE = oracle
   ORACLE_URL = postgresql://user:pass@host:5432/dbname
   ORACLE_USER = user
   ORACLE_PASS = password
   ```
3. Redeploy

---

## Monitoring Deployment
1. Go to Render Dashboard
2. Click your service: `blood-donation-api`
3. View:
   - **Logs** → Real-time server output
   - **Metrics** → CPU, Memory, Requests
   - **Events** → Deploy history

---

## After Deployment

### Update Frontend Base URL
Edit both frontend files:
- `my project/js/api-client.js`
- `my project/backend/src/main/webapp/js/api-client.js`

Change:
```javascript
const defaultBase = isFile ? 'https://blood-donation-api.onrender.com/api' : '/api';
```

### Deploy Frontend (Optional - Static Hosting)
1. Use **Netlify** (free tier)
2. Connect GitHub repository
3. Build command: (leave empty)
4. Publish directory: `my project/`

---

## Live URLs After Deployment

**Backend API:** `https://blood-donation-api.onrender.com/api`
**Admin Login:** `https://blood-donation-api.onrender.com/admin-login.html`
**Health Check:** `https://blood-donation-api.onrender.com/api/health`

---

## Support
- Render Docs: https://render.com/docs
- Issues: Check Logs in Render Dashboard
