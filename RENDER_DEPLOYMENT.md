# Render Deployment Guide

## Backend Deployment (Spring Boot Java)

### Step 1: Set up MySQL Database on Render
1. Create a new PostgreSQL database on Render (MySQL also available)
2. Get the connection string
3. Note: Render also offers managed PostgreSQL, MySQL databases

### Step 2: Deploy Backend
1. Push code to GitHub
2. Create new Web Service on Render
3. Connect GitHub repo
4. Runtime: Java 17
5. Build Command: `./mvnw clean package`
6. Start Command: `java -jar target/resume-scanner-0.0.1-SNAPSHOT.jar`

### Step 3: Set Environment Variables on Render
In Render dashboard, add these environment variables:
```
SERVER_PORT=8080
DB_URL=your_mysql_connection_string_from_render
DB_USERNAME=your_username
DB_PASSWORD=your_password
CORS_ALLOWED_ORIGINS=https://your-frontend-url.onrender.com
```

## Frontend Deployment (React + Vite)

### Step 1: Deploy Frontend
1. Connect GitHub repo to Render (Static Site)
2. Build Command: `npm install && npm run build`
3. Publish Directory: `dist`

### Step 2: Environment Variables
Add to Render (Environment tab):
```
VITE_API_BASE_URL=https://your-backend-url.onrender.com/api/resume
```

## Important Notes:
- Change `CORS_ALLOWED_ORIGINS` to match your deployed frontend URL
- Update frontend API URL to match your deployed backend URL
- Test locally first with `.env.local` files before deploying
- Keep sensitive credentials ONLY in Render dashboard, never in code
