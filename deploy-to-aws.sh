#!/bin/bash

# 🚀 AWS Deployment Script for LMS
# This script automates the deployment process to AWS

set -e

echo "================================================"
echo "🚀 LMS AWS Deployment Script"
echo "================================================"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if required variables are set
if [ -z "$EC2_HOST" ]; then
    echo -e "${RED}Error: EC2_HOST environment variable not set${NC}"
    echo "Usage: export EC2_HOST=ec2-user@your-ec2-ip"
    exit 1
fi

if [ -z "$EC2_KEY" ]; then
    echo -e "${RED}Error: EC2_KEY environment variable not set${NC}"
    echo "Usage: export EC2_KEY=/path/to/lms-key.pem"
    exit 1
fi

echo -e "${GREEN}✓${NC} Environment variables configured"
echo ""

# Step 1: Build Backend
echo "================================================"
echo "📦 Step 1: Building Backend JAR..."
echo "================================================"
cd backend
mvn clean package -DskipTests
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓${NC} Backend build successful"
else
    echo -e "${RED}✗${NC} Backend build failed"
    exit 1
fi
cd ..
echo ""

# Step 2: Transfer JAR to EC2
echo "================================================"
echo "📤 Step 2: Transferring JAR to EC2..."
echo "================================================"
scp -i "$EC2_KEY" backend/target/lms-0.0.1-SNAPSHOT.jar "$EC2_HOST":~/lms-app/
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓${NC} JAR transferred successfully"
else
    echo -e "${RED}✗${NC} JAR transfer failed"
    exit 1
fi
echo ""

# Step 3: Restart Backend Service
echo "================================================"
echo "🔄 Step 3: Restarting Backend Service..."
echo "================================================"
ssh -i "$EC2_KEY" "$EC2_HOST" "sudo systemctl restart lms && sudo systemctl status lms"
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓${NC} Backend service restarted"
else
    echo -e "${RED}✗${NC} Backend service restart failed"
    exit 1
fi
echo ""

# Step 4: Build Frontend
echo "================================================"
echo "📦 Step 4: Building Frontend..."
echo "================================================"
cd frontend
npm run build
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓${NC} Frontend build successful"
else
    echo -e "${RED}✗${NC} Frontend build failed"
    exit 1
fi
cd ..
echo ""

# Step 5: Deploy Frontend to S3
if [ ! -z "$S3_BUCKET" ]; then
    echo "================================================"
    echo "📤 Step 5: Deploying Frontend to S3..."
    echo "================================================"
    aws s3 sync frontend/build/ s3://"$S3_BUCKET"/ --delete
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓${NC} Frontend deployed to S3"
    else
        echo -e "${RED}✗${NC} Frontend deployment failed"
        exit 1
    fi
else
    echo -e "${YELLOW}⚠${NC} S3_BUCKET not set, skipping frontend deployment"
fi
echo ""

echo "================================================"
echo "🎉 Deployment Complete!"
echo "================================================"
echo ""
echo "Backend API: http://$(echo $EC2_HOST | cut -d'@' -f2):8080/api"
echo "Swagger UI: http://$(echo $EC2_HOST | cut -d'@' -f2):8080/swagger-ui.html"
if [ ! -z "$S3_BUCKET" ]; then
    echo "Frontend: http://$S3_BUCKET.s3-website-ap-southeast-2.amazonaws.com"
fi
echo ""
echo "To view backend logs:"
echo "ssh -i $EC2_KEY $EC2_HOST 'sudo journalctl -u lms -f'"
echo ""

