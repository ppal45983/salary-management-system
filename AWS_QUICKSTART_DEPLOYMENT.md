# 🚀 AWS EC2 Quickstart Deployment Guide

This guide shows you how to deploy the **Spring Boot backend** and **MySQL database** to **AWS EC2 (Free Tier)** and connect it with your **Vercel frontend** so that your live application has full dynamic CRUD capabilities.

---

## 📋 Step 1: Launch an AWS EC2 Instance (5 minutes)

1. Open the [AWS EC2 Console](https://console.aws.amazon.com/ec2/).
2. Click **Launch Instance**.
3. Configure the following settings:
   - **Name**: `salary-management-backend`
   - **OS Image (AMI)**: `Ubuntu Server 24.04 LTS` or `22.04 LTS` (x86_64, Free tier eligible).
   - **Instance Type**: `t2.micro` or `t3.micro` (Free tier eligible).
   - **Key Pair**: Select your existing key pair or create a new one (download the `.pem` file, e.g., `sms-key.pem`).
   - **Network Settings / Security Group**:
     - Check **Allow SSH traffic from** -> `My IP` (Port 22).
     - Check **Allow HTTP traffic from the internet** (Port 80).
     - Check **Allow HTTPS traffic from the internet** (Port 443).
     - Add **Custom TCP Rule**: Port `8080`, Source `0.0.0.0/0` (Anywhere).
4. Click **Launch Instance**.
5. Once launched, copy the **Public IPv4 address** (e.g. `54.210.120.45`).

---

## 💻 Step 2: Deploy Backend & Database on EC2 (One-Command)

1. Open your terminal on your computer and connect to your EC2 instance:
   ```bash
   ssh -i "sms-key.pem" ubuntu@<YOUR_EC2_PUBLIC_IP>
   ```

2. Clone your GitHub repository onto the EC2 instance:
   ```bash
   git clone https://github.com/ppal45983/Salary-Management-System.git
   cd Salary-Management-System
   ```

3. Run the automated deployment script:
   ```bash
   chmod +x deploy-aws-ec2.sh
   ./deploy-aws-ec2.sh
   ```

   **What the script does automatically**:
   - Installs Docker Engine & Docker Compose.
   - Configures a 2GB swap file so Spring Boot builds & runs smoothly on micro instances.
   - Launches the **MySQL 8** container and runs the initial schemas (`01_initial_schema.sql`, `02_seed_tax_brackets.sql`).
   - Builds and starts the **Spring Boot 3** container (`app.jar`).
   - Auto-seeds **10,000 employees**, progressive tax brackets, departments, and admin credentials.
   - Verifies the health check at `http://<YOUR_EC2_PUBLIC_IP>:8080/api/v1/actuator/health`.

---

## 🔗 Step 3: Connect Vercel to Your AWS Backend

1. In your local project repository, open `vercel.json`:
   ```json
   {
     "version": 2,
     "rewrites": [
       {
         "source": "/api/v1/:path*",
         "destination": "http://<YOUR_EC2_PUBLIC_IP>:8080/api/v1/:path*"
       },
       {
         "source": "/(.*)",
         "destination": "/index.html"
       }
     ]
   }
   ```
   *(Replace `<YOUR_EC2_PUBLIC_IP>` with your actual EC2 public IP, e.g. `54.210.120.45`)*.

2. Commit and push the update to GitHub:
   ```bash
   git add .
   git commit -m "feat: link Vercel frontend with AWS EC2 backend"
   git push origin main
   ```

3. Vercel will automatically redeploy the frontend in seconds!

---

## 🔑 Login Credentials

Once connected, open your Vercel URL:
👉 `https://salary-management-system-793w3p9r5-ppal45983s-projects.vercel.app/dashboard`

Log in using the pre-seeded admin account:
- **Username**: `hr_manager`
- **Password**: `admin123`

---

## 🛠️ Useful Management Commands on EC2

```bash
# View backend live logs
sudo docker compose logs -f backend

# View database logs
sudo docker compose logs -f db

# Restart services
sudo docker compose restart

# Stop all services
sudo docker compose down
```
