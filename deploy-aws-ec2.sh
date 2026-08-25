#!/usr/bin/env bash
# ==============================================================================
# AWS EC2 Automated Deployment Script
# Salary Management System (Spring Boot 3 + MySQL 8.0)
# ==============================================================================
set -e

echo "================================================================================"
echo " Starting Deployment: Salary Management System Backend on AWS EC2"
echo "================================================================================"

echo ">>> [1/5] Installing Prerequisites and Docker Engine..."
if [ -f /etc/debian_version ]; then
    # Ubuntu / Debian
    sudo apt-get update -y
    sudo apt-get install -y ca-certificates curl gnupg lsb-release git
    
    # Add Docker GPG key & repository
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
    sudo mkdir -p /usr/local/lib/docker/cli-plugins
    sudo curl -SL "https://github.com/docker/compose/releases/download/v2.24.5/docker-compose-linux-$(uname -m)" -o /usr/local/lib/docker/cli-plugins/docker-compose
    sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose
fi

echo ">>> [2/5] Enabling Docker Service..."
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker $USER || true

echo ">>> [3/5] Allocating 2GB Swap Memory (for t2.micro/t3.micro stability)..."
if [ ! -f /swapfile ]; then
    sudo fallocate -l 2G /swapfile || sudo dd if=/dev/zero of=/swapfile bs=1M count=2048
    sudo chmod 600 /swapfile
    sudo mkswap /swapfile
    sudo swapon /swapfile
    echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
    echo "Swap allocated successfully."
else
    echo "Swap already configured."
fi

echo ">>> [4/5] Building & Launching Docker Services (MySQL 8 + Spring Boot 3)..."
# If running inside repo directory:
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

sudo docker compose down || true
sudo docker compose up -d --build

echo ">>> [5/5] Waiting for Application Health Check..."
PUBLIC_IP=$(curl -s http://checkip.amazonaws.com || curl -s ifconfig.me || echo "YOUR_EC2_PUBLIC_IP")

for i in {1..20}; do
    if curl -s http://localhost:8080/api/v1/actuator/health | grep -q "UP"; then
        echo ""
        echo "================================================================================"
        echo " SUCCESS! Salary Management System Backend is running on AWS EC2!"
        echo "================================================================================"
        echo " Health Check: http://${PUBLIC_IP}:8080/api/v1/actuator/health"
        echo " API Base URL: http://${PUBLIC_IP}:8080/api/v1"
        echo ""
        echo " Next Step: Update 'vercel.json' in your repository with:"
        echo " \"destination\": \"http://${PUBLIC_IP}:8080/api/v1/:path*\""
        echo " Then push to GitHub to connect Vercel to your live backend!"
        echo "================================================================================"
        exit 0
    fi
    echo "Waiting for Spring Boot to start (attempt $i/20)..."
    sleep 6
done

echo "Container is running. Check logs using: sudo docker compose logs -f backend"
