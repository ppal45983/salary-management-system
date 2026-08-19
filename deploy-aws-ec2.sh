#!/usr/bin/env bash
# ==============================================================================
# AWS EC2 UserData / Automated Deployment Script
# Salary Management System (Angular 16 + Spring Boot 3 + MySQL 8.0)
# ==============================================================================
set -e

echo "=== [1/6] Updating OS packages and installing prerequisites ==="
if [ -f /etc/debian_version ]; then
    # Ubuntu / Debian
    sudo apt-get update -y
    sudo apt-get install -y ca-certificates curl gnupg lsb-release git
    
    # Install Docker
    sudo mkdir -p /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg --yes
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    sudo apt-get update -y
    sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
else
    # Amazon Linux 2023 / RHEL
    sudo dnf update -y
    sudo dnf install -y docker git
    sudo systemctl enable docker
    sudo systemctl start docker
    # Install docker compose plugin
    sudo mkdir -p /usr/local/lib/docker/cli-plugins
    sudo curl -SL "https://github.com/docker/compose/releases/download/v2.24.5/docker-compose-linux-$(uname -m)" -o /usr/local/lib/docker/cli-plugins/docker-compose
    sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose
fi

echo "=== [2/6] Configuring Docker & User Permissions ==="
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker $USER || true

echo "=== [3/6] Setting up Swap Space for Stability (2GB) ==="
if [ ! -f /swapfile ]; then
    sudo fallocate -l 2G /swapfile || sudo dd if=/dev/zero of=/swapfile bs=1M count=2048
    sudo chmod 600 /swapfile
    sudo mkswap /swapfile
    sudo swapon /swapfile
    echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
fi

echo "=== [4/6] Setting up Application Directory ==="
APP_DIR="/opt/salary-management-system"
if [ ! -d "$APP_DIR" ]; then
    sudo mkdir -p "$APP_DIR"
    sudo chown -R $USER:$USER "$APP_DIR"
    # Clone repository (replace with your repository URL if running standalone)
    if [ -n "$REPO_URL" ]; then
        git clone "$REPO_URL" "$APP_DIR"
    fi
fi

if [ -d "$APP_DIR" ]; then
    cd "$APP_DIR"
    echo "=== [5/6] Building & Starting Docker Containers on AWS EC2 ==="
    sudo docker compose down || true
    sudo docker compose up -d --build
fi

echo "=== [6/6] Deployment Complete! ==="
echo "Access the application at: http://$(curl -s ifconfig.me)"
