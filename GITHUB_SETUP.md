# GitHub Setup Guide for Salary Management System

## Prerequisites

Before proceeding with GitHub setup, you need to install Git on your Windows machine.

### Step 1: Install Git

1. **Download Git for Windows**:
   - Visit: https://git-scm.com/download/win
   - Click "Click here to download manually" to download the latest version (2.42+)

2. **Install Git**:
   - Run the installer (.exe file)
   - Use default options OR:
     - Choose "Git Bash" and "Git GUI" during installation
     - Select "Use Git from Git Bash only" or "Use Git from the Windows Command Prompt" (latter is recommended)
     - Choose "Use the native Windows Secure Channel library" for HTTPS
   - Complete the installation

3. **Verify Installation**:
   ```powershell
   git --version
   ```
   You should see something like: `git version 2.42.0.windows.1`

4. **Configure Git** (First time only):
   ```powershell
   git config --global user.name "Your Name"
   git config --global user.email "your.email@example.com"
   ```

---

## Step 2: Create GitHub Repository

### Option A: Using GitHub Web UI (Recommended)

1. **Go to GitHub.com**:
   - Log in to your GitHub account
   - Click `+` icon (top right) → `New repository`

2. **Repository Settings**:
   - **Repository name**: `salary-management-system`
   - **Description**: `Enterprise salary management platform for 10,000+ employees`
   - **Visibility**: Choose `Private` (for assessment) or `Public`
   - **Initialize repository**: Leave UNCHECKED (we have local files already)
   - Click `Create repository`

3. **Copy the Repository URL**:
   - On the repository page, click `Code` button
   - Copy the HTTPS URL: `https://github.com/your-username/salary-management-system.git`

---

## Step 3: Initialize Git Locally

Navigate to your project directory and initialize Git:

```powershell
cd "C:\Users\sheel\OneDrive\Documents\Salary-Management-System"

# Initialize local repository
git init

# Set remote repository
git remote add origin https://github.com/your-username/salary-management-system.git

# Verify remote was added
git remote -v
```

**Output should show**:
```
origin  https://github.com/your-username/salary-management-system.git (fetch)
origin  https://github.com/your-username/salary-management-system.git (push)
```

---

## Step 4: Make Initial Commit

### Stage All Files

```powershell
cd "C:\Users\sheel\OneDrive\Documents\Salary-Management-System"

# Check status
git status

# Add all files
git add .

# Verify staging
git status
```

### Create First Commit

```powershell
git commit -m "Initial setup: requirements doc, schema design, API specification, and folder structure"
```

**Commit Message Breakdown**:
- `Initial setup:` - Type/category
- `requirements doc, schema design, API specification, and folder structure` - What was added

---

## Step 5: Push to GitHub

### First Time Push

```powershell
git branch -M main

git push -u origin main
```

- `-u origin main`: Sets `origin/main` as default upstream
- This tells Git that future pushes on `main` go to origin

### Subsequent Pushes

```powershell
git push
```

---

## Step 6: Verify on GitHub

1. Go to your GitHub repository: `https://github.com/your-username/salary-management-system`
2. You should see:
   - All files and folders from your local repository
   - The commit message in the commit history
   - Branch indicator showing `main`

---

## Troubleshooting

### Error: "fatal: not a git repository"
- Make sure you're in the correct directory: `C:\Users\sheel\OneDrive\Documents\Salary-Management-System`
- Run `git init` to initialize

### Error: "fatal: remote origin already exists"
- Remove existing remote: `git remote remove origin`
- Add new remote: `git remote add origin https://github.com/your-username/salary-management-system.git`

### Error: "Permission denied (publickey)"
- Use HTTPS URL instead of SSH
- Or set up SSH key: https://docs.github.com/en/authentication/connecting-to-github-with-ssh

### Error: "Could not resolve host"
- Check internet connection
- May be firewall/proxy issue
- Try: `git config --global http.proxy [proxy-url]`

---

## Best Practices: Commit Message Format

For this assessment, use clear commit messages following this format:

```
<type>: <description>

<optional-body>
```

**Types**:
- `feat:` - New feature
- `fix:` - Bug fix
- `refactor:` - Code refactoring
- `docs:` - Documentation only
- `test:` - Tests only
- `chore:` - Build/dependencies/config
- `perf:` - Performance improvement

**Examples**:
```
feat: add employee CRUD API endpoints

refactor: extract tax calculation to separate service

docs: update API documentation with new endpoints

chore: configure Maven dependencies

test: add unit tests for TaxCalculationService
```

---

## Incremental Commits Strategy

After Phase A setup is complete, follow this commit pattern for each phase:

### Phase B: Backend Infrastructure
```
git add backend/pom.xml backend/src
git commit -m "setup: configure Spring Boot with Maven and MySQL"

git add backend/sql
git commit -m "database: create schema with migrations"

git add backend/src/main/java/com/sms/entity
git commit -m "feat: implement JPA entity models"
```

### Phase C: Backend APIs
```
git commit -m "feat: implement employee management API"
git commit -m "feat: implement salary management API"
git commit -m "feat: add tax calculation service"
```

This shows clear evolution of the solution!

---

## View Commit History

```powershell
# View commit log
git log

# View in one line
git log --oneline

# View last 5 commits
git log -5

# View commits for a specific file
git log -- backend/pom.xml
```

---

## Common Git Commands

```powershell
# Check status
git status

# Stage specific file
git add <file-path>

# Stage all changes
git add .

# Create commit
git commit -m "message"

# Push to remote
git push

# Pull from remote
git pull

# Create new branch
git checkout -b feature/your-feature

# Switch branch
git checkout main

# Merge branch
git merge feature/your-feature

# View branches
git branch -a

# Delete branch
git branch -d feature/your-feature
```

---

## SSH Key Setup (Optional, for easier authentication)

If you want to avoid entering password each time:

1. **Generate SSH Key**:
   ```powershell
   ssh-keygen -t ed25519 -C "your.email@example.com"
   ```
   - Press Enter when asked for passphrase (leave empty)
   - Key saved to: `~/.ssh/id_ed25519`

2. **Add to SSH Agent**:
   ```powershell
   # Start SSH agent
   Start-Service ssh-agent

   # Add key
   ssh-add ~/.ssh/id_ed25519
   ```

3. **Add Public Key to GitHub**:
   - Copy public key:
     ```powershell
     cat ~/.ssh/id_ed25519.pub | clip
     ```
   - Go to GitHub Settings → SSH and GPG keys
   - Click "New SSH key"
   - Paste and save

4. **Update Remote URL**:
   ```powershell
   git remote set-url origin git@github.com:your-username/salary-management-system.git
   ```

---

## Next Steps

After Git is set up:

1. ✅ Make initial commit with Phase A deliverables
2. 🔄 Start Phase B: Backend Infrastructure
3. 🔄 Continue with incremental commits for each phase
4. 📊 Show clear commit history demonstrating project evolution

---

**Document Version**: 1.0  
**Last Updated**: 2026-08-14  
**Status**: Ready for Git setup
