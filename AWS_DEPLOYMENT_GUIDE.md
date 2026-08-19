# AWS Full-Stack Deployment Guide
## ACME Salary Management System (Angular 16 + Spring Boot 3 + MySQL 8.0)

This guide walks you through deploying the complete Salary Management System to **Amazon Web Services (AWS)** using **Option A (AWS EC2 + Docker Compose + Nginx Reverse Proxy)**.

---

## 🏗️ Architecture Overview

```
                          [ Internet / Browser User ]
                                      │
                               (HTTP Port 80 / HTTPS 443)
                                      ▼
                        ┌───────────────────────────┐
                        │   AWS EC2 (Ubuntu Linux)  │
                        │                           │
                        │  [ Nginx Container :80 ]  │
                        │  - Serves Angular 16 SPA  │
                        │  - Proxies /api/v1/       │
                        │             │             │
                        │             ▼             │
                        │ [ Spring Boot 3 :8080 ]   │
                        │  - Java 17 REST APIs      │
                        │  - Tax & Payroll Engine   │
                        │             │             │
                        │             ▼             │
                        │   [ MySQL 8.0 :3306 ]     │
                        │  - 10k Seeded Database    │
                        │  - Persistent EBS Volume  │
                        └───────────────────────────┘
```

---

## 🚀 Option 1: Deploy via AWS Management Console (Recommended)

Follow these simple steps in the AWS Web Console:

### Step 1: Launch an EC2 Instance
1. Open the **[AWS Management Console](https://console.aws.amazon.com/ec2/)** and navigate to **EC2** > **Instances** > **Launch Instances**.
2. **Name**: `salary-management-system`
3. **Application and OS Images (AMI)**: Select **Ubuntu 22.04 LTS** (or Amazon Linux 2023).
4. **Instance Type**: Select **`t3.small`** (Recommended, 2 vCPUs, 2 GiB RAM for in-memory 10k calculations) or **`t2.micro`** (Free Tier eligible).
5. **Key Pair**: Select your existing key pair or create a new one (e.g. `sms-keypair.pem`) to allow SSH access.

---

### Step 2: Configure Network & Security Group
Under **Network Settings**, check the following boxes:
- ✅ **Allow SSH traffic from**: `Anywhere` (0.0.0.0/0) or `My IP`
- ✅ **Allow HTTP traffic from the internet**: (Port 80)
- ✅ **Allow HTTPS traffic from the internet**: (Port 443)

---

### Step 3: Configure Storage
- Change storage size to **`20 GiB`** of **`gp3`** SSD storage.

---

### Step 4: Inject User Data (Automated Setup)
Scroll down and expand **Advanced Details** at the bottom of the page. In the **User data** text box, paste the following bootstrap script:

```bash
#!/bin/bash
set -e

# Update and install Docker
apt-get update -y
apt-get install -y ca-certificates curl gnupg lsb-release git

mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg --yes
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null

apt-get update -y
apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

systemctl enable docker
systemctl start docker

# Enable 2GB swap space for stability
fallocate -l 2G /swapfile || dd if=/dev/zero of=/swapfile bs=1M count=2048
chmod 600 /swapfile
mkswap /swapfile
swapon /swapfile
echo '/swapfile none swap sw 0 0' >> /etc/fstab

# Clone repository & start full stack
mkdir -p /opt/app
git clone https://github.com/ppal45983/salary-management-system.git /opt/app
cd /opt/app
docker compose up -d --build
```

---

### Step 5: Launch & Access the Application
1. Click **Launch Instance**.
2. Wait **2 to 3 minutes** for the instance to initialize and Docker containers to build.
3. Copy your EC2 **Public IPv4 address** from the EC2 Console.
4. Open your browser and go to:
   ```
   http://<YOUR-EC2-PUBLIC-IP>
   ```
5. **Sign in** with:
   - **Username**: `hr_manager@acme.com` (or click *Auto-Fill Demo Credentials*)
   - **Password**: `admin123`

---

## ⚡ Option 2: 1-Click Deploy via AWS CloudFormation

If you prefer Infrastructure-as-Code:

1. Open **[AWS CloudFormation Console](https://console.aws.amazon.com/cloudformation/)**.
2. Click **Create Stack** > **With new resources (standard)**.
3. Select **Upload a template file** and choose [`aws-cloudformation.yml`](aws-cloudformation.yml).
4. Click **Next**, name the stack (e.g. `salary-management-stack`), select your EC2 **KeyName**, and enter your GitHub repository URL.
5. Click **Next** through the defaults, and click **Submit**.
6. When the stack status changes to `CREATE_COMPLETE`, click on the **Outputs** tab to view your live **ApplicationURL**!

---

## 🛠️ Managing Your EC2 Deployment via SSH

To check status, view logs, or update code on your EC2 instance:

### 1. Connect to EC2:
```bash
ssh -i "sms-keypair.pem" ubuntu@<YOUR-EC2-PUBLIC-IP>
```

### 2. Check Running Containers:
```bash
cd /opt/app
sudo docker compose ps
```
You will see 3 healthy containers:
- `sms-frontend` (Nginx on Port 80)
- `sms-backend` (Spring Boot on Port 8080)
- `sms-mysql` (MySQL on Port 3306)

### 3. View Live Logs:
```bash
# View backend Spring Boot logs
sudo docker compose logs -f backend

# View frontend Nginx access/error logs
sudo docker compose logs -f frontend

# View database logs
sudo docker compose logs -f db
```

### 4. Pull Latest Code & Re-deploy:
```bash
cd /opt/app
git pull origin main
sudo docker compose up -d --build
```

---

## 🔒 Optional: Add Custom Domain & Free SSL (HTTPS)

To add your own domain with free SSL via Let's Encrypt:

1. In your domain provider (Route 53, GoDaddy, Namecheap), add an **A Record** pointing your domain (e.g., `salary.yourdomain.com`) to your **EC2 Public IP**.
2. Connect to your EC2 instance via SSH:
   ```bash
   sudo apt-get install -y certbot python3-certbot-nginx
   sudo certbot --nginx -d salary.yourdomain.com
   ```
3. Certbot will automatically issue an SSL certificate and configure HTTPS auto-renewal!

---

## 🎯 Verification Checklist

- [x] Angular 16 Production build compiled with zero errors.
- [x] Nginx Reverse Proxy forwards `/api/v1/*` to backend service.
- [x] MySQL database automatically seeds schema and 10,000 realistic employee dataset.
- [x] Single entry point on Port 80 accessible via public internet IP.
