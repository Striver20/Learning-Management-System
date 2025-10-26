# 🚀 AWS Deployment Guide - Learning Management System

Complete step-by-step guide to deploy your LMS on AWS infrastructure.

---

## 📋 Architecture Overview

```
┌─────────────────┐
│   CloudFront    │ ← Frontend (React)
│   + S3 Bucket   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   EC2 Instance  │ ← Backend (Spring Boot)
│  (t2.micro/med) │
└────────┬────────┘
         │
    ┌────┼────┐
    ▼         ▼
┌──────┐  ┌──────┐
│  RDS │  │  S3  │
│MySQL │  │Files │
└──────┘  └──────┘
```

**Total Setup Time: ~30-45 minutes**

---

## 🎯 Prerequisites

- [ ] AWS Account with billing enabled
- [ ] AWS CLI installed and configured
- [ ] Your domain (optional, can use AWS URLs)
- [ ] This repository cloned locally

---

## Part 1: AWS RDS MySQL Database Setup (10 min)

### Step 1.1: Create RDS MySQL Instance

1. **Go to AWS Console → RDS → Create Database**

2. **Choose Configuration:**
   ```
   Engine: MySQL 8.0
   Template: Free tier (or Dev/Test)
   Instance: db.t3.micro (Free tier eligible)
   Storage: 20 GB SSD
   ```

3. **Database Settings:**
   ```
   DB Instance Identifier: lms-database
   Master Username: admin
   Master Password: [Create a strong password]
   ```

4. **Connectivity:**
   ```
   VPC: Default VPC
   Public Access: YES (for now, secure later)
   VPC Security Group: Create new → lms-db-sg
   ```

5. **Additional Configuration:**
   ```
   Initial Database Name: lmsdb
   Backup: Enable automatic backups (1 day retention)
   ```

6. **Click "Create Database"** (Takes ~5-10 minutes)

### Step 1.2: Configure Security Group

1. Go to **EC2 → Security Groups → lms-db-sg**
2. **Edit Inbound Rules:**
   ```
   Type: MySQL/Aurora
   Protocol: TCP
   Port: 3306
   Source: My IP (your current IP)
   Description: Temporary access for setup
   ```

3. **Save Rules**

### Step 1.3: Get Connection Details

Once RDS is available, note these details:
```
Endpoint: lms-database.xxxxxxxxx.ap-southeast-2.rds.amazonaws.com
Port: 3306
Username: admin
Password: [your password]
Database: lmsdb
```

---

## Part 2: AWS S3 Buckets Setup (5 min)

### Step 2.1: Create S3 Bucket for Files (Course Content)

1. **Go to S3 → Create Bucket**
   ```
   Bucket Name: lms-course-content-[your-unique-id]
   Region: ap-southeast-2 (Sydney)
   Block Public Access: UNCHECK all (make public)
   ```

2. **Enable CORS** (Bucket → Permissions → CORS):
   ```json
   [
     {
       "AllowedHeaders": ["*"],
       "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
       "AllowedOrigins": ["*"],
       "ExposeHeaders": ["ETag"]
     }
   ]
   ```

3. **Add Bucket Policy** (Bucket → Permissions → Bucket Policy):
   ```json
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Sid": "PublicReadGetObject",
         "Effect": "Allow",
         "Principal": "*",
         "Action": "s3:GetObject",
         "Resource": "arn:aws:s3:::lms-course-content-[your-unique-id]/*"
       }
     ]
   }
   ```

### Step 2.2: Create S3 Bucket for Frontend

1. **Create another bucket:**
   ```
   Bucket Name: lms-frontend-[your-unique-id]
   Region: ap-southeast-2
   Block Public Access: UNCHECK all
   Enable Static Website Hosting: YES
   Index Document: index.html
   Error Document: index.html
   ```

2. **Add Bucket Policy:**
   ```json
   {
     "Version": "2012-10-17",
     "Statement": [
       {
         "Sid": "PublicReadGetObject",
         "Effect": "Allow",
         "Principal": "*",
         "Action": "s3:GetObject",
         "Resource": "arn:aws:s3:::lms-frontend-[your-unique-id]/*"
       }
     ]
   }
   ```

---

## Part 3: AWS EC2 Instance Setup (15 min)

### Step 3.1: Launch EC2 Instance

1. **Go to EC2 → Launch Instance**

2. **Configure Instance:**
   ```
   Name: lms-backend
   AMI: Amazon Linux 2023 (Free tier eligible)
   Instance Type: t2.micro (Free tier) or t2.small
   Key Pair: Create new → lms-key.pem (Download and save!)
   ```

3. **Network Settings:**
   ```
   VPC: Default
   Auto-assign Public IP: Enable
   Security Group: Create new → lms-backend-sg
   ```

4. **Configure Security Group Rules:**
   ```
   Rule 1: SSH
     - Type: SSH
     - Port: 22
     - Source: My IP
   
   Rule 2: HTTP
     - Type: HTTP
     - Port: 80
     - Source: Anywhere (0.0.0.0/0)
   
   Rule 3: HTTPS
     - Type: HTTPS
     - Port: 443
     - Source: Anywhere (0.0.0.0/0)
   
   Rule 4: Spring Boot
     - Type: Custom TCP
     - Port: 8080
     - Source: Anywhere (0.0.0.0/0)
   ```

5. **Storage:** 8 GB (Free tier default)

6. **Launch Instance**

### Step 3.2: Connect to EC2 Instance

```bash
# Make key file secure
chmod 400 lms-key.pem

# Connect to EC2
ssh -i lms-key.pem ec2-user@[YOUR-EC2-PUBLIC-IP]
```

### Step 3.3: Install Java and MySQL Client on EC2

```bash
# Update system
sudo yum update -y

# Install Java 17
sudo yum install java-17-amazon-corretto-headless -y

# Verify Java installation
java -version

# Install MySQL client (to test RDS connection)
sudo yum install mysql -y

# Install Maven
sudo yum install maven -y

# Verify Maven
mvn -version
```

### Step 3.4: Test RDS Connection from EC2

```bash
mysql -h lms-database.xxxxxxxxx.ap-southeast-2.rds.amazonaws.com -u admin -p
# Enter your RDS password

# Once connected, verify database
SHOW DATABASES;
USE lmsdb;
EXIT;
```

---

## Part 4: Deploy Backend to EC2 (10 min)

### Step 4.1: Prepare Application on Local Machine

1. **Update CORS Configuration** (already done, but verify):
   - Check `backend/src/main/java/com/example/lms/config/CorsConfig.java`
   - Should allow your S3 frontend URL

2. **Build the JAR:**
   ```bash
   cd backend
   mvn clean package -DskipTests
   ```

3. **JAR will be created at:**
   ```
   backend/target/lms-0.0.1-SNAPSHOT.jar
   ```

### Step 4.2: Transfer JAR to EC2

```bash
# From your local machine (in project root)
scp -i lms-key.pem backend/target/lms-0.0.1-SNAPSHOT.jar ec2-user@[YOUR-EC2-IP]:~/
```

### Step 4.3: Create Environment File on EC2

```bash
# SSH into EC2
ssh -i lms-key.pem ec2-user@[YOUR-EC2-IP]

# Create application directory
mkdir -p ~/lms-app
mv lms-0.0.1-SNAPSHOT.jar ~/lms-app/

# Create environment script
nano ~/lms-app/start-lms.sh
```

**Paste this content:**
```bash
#!/bin/bash
export DB_USERNAME=admin
export DB_PASSWORD=[YOUR-RDS-PASSWORD]
export AWS_ACCESS_KEY=[YOUR-AWS-ACCESS-KEY]
export AWS_SECRET_KEY=[YOUR-AWS-SECRET-KEY]

java -jar \
  -Dspring.profiles.active=prod \
  -Dspring.datasource.url=jdbc:mysql://[YOUR-RDS-ENDPOINT]:3306/lmsdb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true \
  lms-0.0.1-SNAPSHOT.jar
```

**Make executable:**
```bash
chmod +x ~/lms-app/start-lms.sh
```

### Step 4.4: Create SystemD Service (Run as Background Service)

```bash
sudo nano /etc/systemd/system/lms.service
```

**Paste this:**
```ini
[Unit]
Description=Learning Management System Backend
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user/lms-app
ExecStart=/home/ec2-user/lms-app/start-lms.sh
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal

Environment="DB_USERNAME=admin"
Environment="DB_PASSWORD=[YOUR-RDS-PASSWORD]"
Environment="AWS_ACCESS_KEY=[YOUR-AWS-ACCESS-KEY]"
Environment="AWS_SECRET_KEY=[YOUR-AWS-SECRET-KEY]"
Environment="SPRING_DATASOURCE_URL=jdbc:mysql://[YOUR-RDS-ENDPOINT]:3306/lmsdb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"

[Install]
WantedBy=multi-user.target
```

**Start the service:**
```bash
sudo systemctl daemon-reload
sudo systemctl enable lms
sudo systemctl start lms

# Check status
sudo systemctl status lms

# View logs
sudo journalctl -u lms -f
```

### Step 4.5: Test Backend API

```bash
# From your local machine
curl http://[YOUR-EC2-PUBLIC-IP]:8080/swagger-ui.html

# Or open in browser:
http://[YOUR-EC2-PUBLIC-IP]:8080/swagger-ui.html
```

---

## Part 5: Deploy Frontend to S3 (5 min)

### Step 5.1: Build Frontend for Production

```bash
# In your local machine
cd frontend

# Create environment file
echo "REACT_APP_API_URL=http://[YOUR-EC2-PUBLIC-IP]:8080/api" > .env.production

# Build for production
npm run build
```

### Step 5.2: Deploy to S3

```bash
# Install AWS CLI if not already installed
# Windows: Download from https://aws.amazon.com/cli/
# Mac: brew install awscli
# Linux: sudo apt install awscli

# Configure AWS CLI
aws configure
# Enter your AWS Access Key, Secret Key, Region (ap-southeast-2), format (json)

# Upload build to S3
cd build
aws s3 sync . s3://lms-frontend-[your-unique-id]/ --delete
```

### Step 5.3: Access Your Frontend

Your frontend is now live at:
```
http://lms-frontend-[your-unique-id].s3-website-ap-southeast-2.amazonaws.com
```

---

## Part 6: Update RDS Security Group (Important!)

Now that EC2 is running, update RDS security group:

1. **Go to RDS Security Group (lms-db-sg)**
2. **Remove "My IP" rule**
3. **Add new rule:**
   ```
   Type: MySQL/Aurora
   Port: 3306
   Source: Security Group → lms-backend-sg (EC2's security group)
   Description: Allow EC2 backend to access RDS
   ```

This ensures only your EC2 instance can access the database.

---

## 🎉 Deployment Complete!

### Your URLs:
- **Frontend:** `http://lms-frontend-[your-id].s3-website-ap-southeast-2.amazonaws.com`
- **Backend API:** `http://[EC2-IP]:8080/api`
- **Swagger UI:** `http://[EC2-IP]:8080/swagger-ui.html`

---

## 🔧 Useful Commands

### Check Backend Logs on EC2:
```bash
sudo journalctl -u lms -f
```

### Restart Backend Service:
```bash
sudo systemctl restart lms
```

### Update Backend (After Code Changes):
```bash
# 1. Build new JAR locally
mvn clean package -DskipTests

# 2. Transfer to EC2
scp -i lms-key.pem backend/target/lms-0.0.1-SNAPSHOT.jar ec2-user@[EC2-IP]:~/lms-app/

# 3. Restart service on EC2
ssh -i lms-key.pem ec2-user@[EC2-IP]
sudo systemctl restart lms
```

### Update Frontend:
```bash
# 1. Build locally
cd frontend
npm run build

# 2. Deploy to S3
cd build
aws s3 sync . s3://lms-frontend-[your-id]/ --delete
```

---

## 💰 Cost Estimation (Free Tier)

- **EC2 t2.micro:** Free for 750 hours/month (1st year)
- **RDS db.t3.micro:** Free for 750 hours/month (1st year)
- **S3 Storage:** Free for 5GB (always)
- **Data Transfer:** Free for 15GB/month (1st year)

**After free tier:** ~$15-25/month

---

## 🔒 Security Best Practices (Post-Deployment)

1. **Use HTTPS:**
   - Get SSL certificate from AWS Certificate Manager
   - Set up Application Load Balancer
   - Configure CloudFront for frontend

2. **Restrict Security Groups:**
   - RDS: Only allow EC2 security group
   - EC2: Restrict SSH to your IP only

3. **Environment Variables:**
   - Use AWS Systems Manager Parameter Store
   - Never commit credentials to Git

4. **Database Backups:**
   - Enable automated RDS backups
   - Set retention period to 7 days

5. **Monitoring:**
   - Enable CloudWatch for EC2 and RDS
   - Set up billing alerts

---

## 🐛 Troubleshooting

### Backend won't start:
```bash
# Check logs
sudo journalctl -u lms -n 100

# Common issues:
# - Wrong RDS endpoint
# - Wrong RDS password
# - RDS security group not allowing EC2
```

### Frontend can't connect to backend:
```bash
# Check CORS settings in backend
# Verify .env.production has correct EC2 IP
# Clear browser cache and reload
```

### Database connection failed:
```bash
# Test from EC2:
mysql -h [RDS-ENDPOINT] -u admin -p

# If fails:
# - Check RDS security group allows EC2 security group
# - Verify RDS is publicly accessible (or in same VPC)
```

---

## 📝 Next Steps

1. ✅ Test all functionality (register, login, create course, upload content)
2. ✅ Update README with live URLs
3. ✅ Set up custom domain (optional)
4. ✅ Configure HTTPS with SSL certificate
5. ✅ Set up CloudFront CDN for frontend
6. ✅ Configure automated backups

---

**Need Help?** Refer to AWS documentation or contact support.

**Good luck with your deployment! 🚀**

