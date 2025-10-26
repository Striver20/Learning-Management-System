# ⚡ Quick Start - AWS Deployment (30 Minutes)

Follow these steps to get your LMS live on AWS ASAP!

---

## 📋 What You'll Need

- [ ] AWS Account
- [ ] AWS CLI installed (`aws --version`)
- [ ] Your AWS Access Keys ready
- [ ] 30-45 minutes

---

## 🚀 Deployment Steps

### Part 1: AWS RDS Database (10 min)

1. **AWS Console → RDS → Create Database**
   - Engine: MySQL 8.0
   - Template: Free tier
   - Instance: db.t3.micro
   - Username: `admin`
   - Password: Create strong password (save it!)
   - Initial database name: `lmsdb`
   - Public access: YES
   - VPC Security Group: Create new `lms-db-sg`
   
2. **Wait 5-10 minutes** for RDS to be available

3. **Configure Security Group:**
   - EC2 → Security Groups → `lms-db-sg`
   - Add Inbound Rule: MySQL/Aurora (3306) from `My IP`

4. **Save Connection Details:**
   ```
   Endpoint: lms-database.xxxxx.ap-southeast-2.rds.amazonaws.com
   Port: 3306
   Username: admin
   Password: [your password]
   Database: lmsdb
   ```

---

### Part 2: AWS S3 Buckets (5 min)

#### Bucket 1: Course Content Storage

1. **S3 → Create Bucket**
   - Name: `lms-content-[your-name]`
   - Region: ap-southeast-2 (Sydney)
   - Uncheck "Block all public access"
   
2. **Permissions → CORS:**
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

3. **Bucket Policy:**
   ```json
   {
     "Version": "2012-10-17",
     "Statement": [{
       "Effect": "Allow",
       "Principal": "*",
       "Action": "s3:GetObject",
       "Resource": "arn:aws:s3:::lms-content-[your-name]/*"
     }]
   }
   ```

#### Bucket 2: Frontend Hosting

1. **S3 → Create Bucket**
   - Name: `lms-frontend-[your-name]`
   - Region: ap-southeast-2
   - Uncheck "Block all public access"
   - Enable "Static website hosting"
   - Index: `index.html`
   - Error: `index.html`

2. **Add same Bucket Policy** (change bucket name)

---

### Part 3: AWS EC2 Instance (10 min)

1. **EC2 → Launch Instance**
   - Name: `lms-backend`
   - AMI: Amazon Linux 2023
   - Instance type: t2.micro (Free tier)
   - Key pair: Create new → `lms-key.pem` (Download!)
   - Auto-assign public IP: Enable

2. **Security Group Rules:**
   - SSH (22) from My IP
   - HTTP (80) from Anywhere
   - HTTPS (443) from Anywhere
   - Custom TCP (8080) from Anywhere

3. **Launch Instance** (takes ~2 minutes)

4. **Connect to EC2:**
   ```bash
   chmod 400 lms-key.pem
   ssh -i lms-key.pem ec2-user@[YOUR-EC2-IP]
   ```

5. **Install Java & Maven:**
   ```bash
   sudo yum update -y
   sudo yum install java-17-amazon-corretto-headless -y
   sudo yum install maven -y
   java -version
   ```

---

### Part 4: Deploy Backend (10 min)

1. **Build JAR Locally:**
   ```bash
   cd backend
   mvn clean package -DskipTests
   ```

2. **Transfer to EC2:**
   ```bash
   scp -i lms-key.pem backend/target/lms-0.0.1-SNAPSHOT.jar ec2-user@[EC2-IP]:~/
   ```

3. **On EC2, create app directory:**
   ```bash
   mkdir ~/lms-app
   mv lms-0.0.1-SNAPSHOT.jar ~/lms-app/
   cd ~/lms-app
   ```

4. **Create start script:**
   ```bash
   nano start-lms.sh
   ```
   
   Paste:
   ```bash
   #!/bin/bash
   export DB_USERNAME=admin
   export DB_PASSWORD=[YOUR-RDS-PASSWORD]
   export AWS_ACCESS_KEY=[YOUR-AWS-KEY]
   export AWS_SECRET_KEY=[YOUR-AWS-SECRET]
   export SPRING_DATASOURCE_URL=jdbc:mysql://[RDS-ENDPOINT]:3306/lmsdb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   export CORS_ALLOWED_ORIGINS=http://lms-frontend-[your-name].s3-website-ap-southeast-2.amazonaws.com,http://localhost:3000
   export AWS_S3_BUCKET=lms-content-[your-name]
   
   java -jar -Dspring.profiles.active=prod lms-0.0.1-SNAPSHOT.jar
   ```

5. **Make executable and run:**
   ```bash
   chmod +x start-lms.sh
   ./start-lms.sh
   ```

6. **Test:** Open `http://[EC2-IP]:8080/swagger-ui.html`

---

### Part 5: Deploy Frontend (5 min)

1. **Create production env file:**
   ```bash
   # In frontend directory
   echo "REACT_APP_API_URL=http://[YOUR-EC2-IP]:8080/api" > .env.production
   ```

2. **Build frontend:**
   ```bash
   npm run build
   ```

3. **Deploy to S3:**
   ```bash
   aws configure  # Enter your AWS credentials if not done
   
   cd build
   aws s3 sync . s3://lms-frontend-[your-name]/ --delete
   ```

---

## 🎉 You're Live!

### Your URLs:
- **Frontend:** `http://lms-frontend-[your-name].s3-website-ap-southeast-2.amazonaws.com`
- **API:** `http://[EC2-IP]:8080/api`
- **Swagger:** `http://[EC2-IP]:8080/swagger-ui.html`

---

## ✅ Post-Deployment Checklist

1. **Test User Registration**
   - Go to frontend → Register as Student
   
2. **Test Login**
   - Login with registered credentials

3. **Test Teacher Functions**
   - Register as Teacher
   - Create a course
   - Upload content

4. **Test Admin Functions**
   - Login as admin2@gmail.com / 123456
   - View users and courses

5. **Update RDS Security**
   - Remove "My IP" from RDS security group
   - Add EC2 security group instead

---

## 🔧 Run Backend as Service (Optional but Recommended)

On EC2:

```bash
sudo nano /etc/systemd/system/lms.service
```

Paste:
```ini
[Unit]
Description=LMS Backend
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user/lms-app
ExecStart=/home/ec2-user/lms-app/start-lms.sh
Restart=always

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl daemon-reload
sudo systemctl enable lms
sudo systemctl start lms
sudo systemctl status lms
```

Now backend runs automatically even after reboot!

---

## 📝 Update README

Add these lines to your README:

```markdown
## 🌐 Live Demo

- **Application:** http://lms-frontend-[your-name].s3-website-ap-southeast-2.amazonaws.com
- **API Documentation:** http://[EC2-IP]:8080/swagger-ui.html

### Test Credentials:
- Admin: admin2@gmail.com / 123456
- Create your own Student/Teacher account via registration
```

---

## 🐛 Troubleshooting

### Backend won't start:
```bash
# Check logs
sudo journalctl -u lms -f

# Or if running manually:
tail -f nohup.out
```

### Frontend can't connect:
- Check `.env.production` has correct EC2 IP
- Verify CORS_ALLOWED_ORIGINS in start-lms.sh
- Check EC2 security group allows port 8080

### Database connection error:
- Verify RDS endpoint in SPRING_DATASOURCE_URL
- Check RDS security group allows connections
- Test: `mysql -h [RDS-ENDPOINT] -u admin -p` from EC2

---

## 💰 Cost

**Free Tier (First Year):**
- EC2 t2.micro: Free 750 hours/month
- RDS db.t3.micro: Free 750 hours/month
- S3: First 5GB free

**After Free Tier:** ~$15-25/month

---

## 🚀 Next Steps

1. Set up custom domain
2. Configure HTTPS with SSL certificate
3. Set up CloudFront CDN
4. Enable automated backups
5. Set up monitoring with CloudWatch

---

**Need detailed instructions?** See [AWS_DEPLOYMENT_GUIDE.md](./AWS_DEPLOYMENT_GUIDE.md)

**Good luck! 🎉**

