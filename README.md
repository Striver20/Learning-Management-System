# Learning Management System (LMS)

A Spring Boot backend application for managing courses, enrollments, and content delivery. Built with modern Java technologies and AWS integration.

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen?style=for-the-badge&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![AWS](https://img.shields.io/badge/AWS-S3-orange?style=for-the-badge&logo=amazon-aws)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

## About This Project

I developed this Learning Management System as a comprehensive backend solution to showcase enterprise-level Java development skills. The system handles user authentication, course management, student enrollments, and file storage with a focus on security and scalability.

**Key highlights:**

- JWT-based authentication with role-based access control
- AWS S3 integration for file storage
- Automated daily reminders using Quartz Scheduler
- Swagger documentation for easy API testing
- Clean architecture following Spring Boot best practices

## Features

### Security & Authentication

- JWT-based authentication for stateless token management
- Role-based access control with three user types (Admin, Teacher, Student)
- Spring Security integration with BCrypt password encryption
- Secure API endpoints with proper authorization

### Course Management

- Complete CRUD operations for courses
- Instructor assignment and course metadata management
- Admin controls for course deletion
- Course content organization and delivery

### User Management

- Multi-role user system supporting different access levels
- User registration and profile management
- Dynamic role assignment by administrators
- Secure user data handling

### Enrollment & Progress Tracking

- Student course enrollment system
- Progress tracking and completion status
- Enrollment history and management
- Real-time progress updates

### Cloud Integration

- AWS S3 integration for file storage
- Automatic file management with unique key generation
- Direct URL generation for stored content
- Proper cleanup handlers for file deletion

### Automation

- Quartz Scheduler for daily student reminders
- Cron-based job scheduling (9:00 AM daily)
- Extensible design for additional scheduled tasks
- Robust error handling and logging

### API Documentation

- Swagger UI for interactive API testing
- OpenAPI 3.0 specification
- JWT token integration for authenticated testing
- Auto-generated documentation

## Architecture

The application follows a layered architecture pattern with clear separation of concerns:

```
Client Layer (React Frontend, API Testing Tools)
    ↓
API Gateway (Spring Boot Controllers, JWT Filter)
    ↓
Business Logic (Service Layer, Security Manager)
    ↓
Data Access (JPA Repositories, Entity Models)
    ↓
External Services (MySQL Database, AWS S3, Quartz Scheduler)
```

### Request Flow

1. Client Request → REST API Controller
2. JWT Authentication → Security Filter validates token
3. Authorization Check → Role-based access verification
4. Business Logic → Service layer processing
5. Data Persistence → JPA/Hibernate → MySQL
6. File Operations → AWS S3 for content storage
7. Scheduled Tasks → Quartz triggers background jobs

## Tech Stack

| Layer             | Technology            | Purpose                        |
| ----------------- | --------------------- | ------------------------------ |
| **Framework**     | Spring Boot 3.3.4     | Application foundation         |
| **Language**      | Java 17               | Core development language      |
| **Database**      | MySQL 8.0             | Relational data storage        |
| **ORM**           | JPA/Hibernate         | Database abstraction           |
| **Security**      | Spring Security + JWT | Authentication & authorization |
| **Cloud Storage** | AWS S3 SDK            | File storage                   |
| **Scheduling**    | Quartz 2.3+           | Background job management      |
| **API Docs**      | Swagger/OpenAPI 3     | Interactive documentation      |
| **Build Tool**    | Maven                 | Dependency management          |
| **Utilities**     | Lombok                | Boilerplate reduction          |

## Getting Started

### Prerequisites

You'll need the following installed on your system:

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- AWS Account (for S3 integration)

### Installation Steps

1. **Clone the repository**

   ```bash
   git clone https://github.com/Striver20/Learning-Management-System.git
   cd Learning-Management-System
   ```

2. **Set up MySQL Database**

   ```sql
   CREATE DATABASE lmsdb;
   CREATE USER 'lmsuser'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON lmsdb.* TO 'lmsuser'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Configure Environment Variables**

   Create a `.env` file in the `backend` directory:

   ```env
   DB_USERNAME=lmsuser
   DB_PASSWORD=your_password
   AWS_ACCESS_KEY=your_aws_access_key
   AWS_SECRET_KEY=your_aws_secret_key
   ```

4. **Build and Run**
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

### Testing with Swagger

Navigate to `http://localhost:8080/swagger-ui.html` to test the API:

1. Register a new user using `POST /api/auth/register`
2. Login with `POST /api/auth/login` to get your JWT token
3. Click the "Authorize" button and enter: `Bearer <your-jwt-token>`
4. Test the protected endpoints

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token
- `GET /api/auth/users` - Get all users (Admin only)

### Courses

- `POST /api/courses` - Create course (Teacher)
- `GET /api/courses` - Get all courses
- `GET /api/courses/{id}` - Get course by ID
- `GET /api/courses/instructor` - Get courses by instructor
- `DELETE /api/courses/{id}` - Delete course (Admin)

### Content

- `POST /api/contents/upload` - Upload content to AWS S3 (Teacher)
- `POST /api/contents` - Add content to course
- `GET /api/contents` - Get course contents
- `DELETE /api/contents/{id}` - Delete content (Teacher)

### Enrollments

- `POST /api/enrollments` - Enroll student in course
- `GET /api/enrollments/student` - Get student enrollments

### Progress

- `POST /api/progress/update` - Update student progress
- `GET /api/progress` - Get student progress

### Admin

- `GET /api/admin/users` - Get all users
- `POST /api/admin/users/{userId}/role` - Assign role
- `DELETE /api/admin/users/{userId}` - Delete user
- `GET /api/admin/courses` - Get all courses
- `DELETE /api/admin/courses/{courseId}` - Delete course
- `GET /api/admin/enrollments` - Get all enrollments
- `PUT /api/admin/enrollments/{enrollmentId}/status` - Update enrollment status

## Project Structure

```
backend/
├── config/          - Security, CORS, S3, Quartz configuration
├── controller/      - REST API endpoints
├── dto/             - Data Transfer Objects
├── entity/          - JPA entities
├── repository/      - Database repositories
├── service/         - Business logic
├── security/        - JWT authentication
├── scheduler/       - Quartz jobs
└── exception/       - Exception handling
```

## Deployment

The application is production-ready and designed for AWS deployment. Currently running locally with AWS S3 integration configured.

### AWS Infrastructure Setup

The application is built to deploy on:

- EC2 Instance (t2.micro for Spring Boot)
- RDS MySQL for production database
- S3 Bucket for course content storage
- Region: Asia Pacific (Sydney)

### Local Development Setup

```bash
# Currently running on
Server: localhost:8080
Database: MySQL 8.0 (local)
File Storage: AWS S3 (ap-southeast-2)
```

### Deployment Features

- JAR packaging configured
- Environment variable management
- AWS S3 SDK integrated
- Production-grade security
- Cloud-native architecture

### Quick Deployment Steps

1. Build production JAR: `mvn clean package -DskipTests`
2. Set up AWS RDS MySQL
3. Deploy to EC2 instance
4. Configure S3 bucket permissions

This is a portfolio project demonstrating enterprise application development with clean architecture, production-ready code quality, and AWS integration capabilities.

## Future Enhancements

Planned features for future development:

- Email notifications using AWS SES
- Real-time updates with WebSocket support
- Analytics dashboard for student performance
- Video streaming with AWS MediaConvert
- Multi-language support
- Payment integration for paid courses
- Discussion forums for student-teacher interaction
- Mobile API optimization

## Contributing

This is primarily a portfolio project, but I welcome suggestions and improvements!

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

**Ashit Verma**

- GitHub: [@Striver20](https://github.com/Striver20)
- LinkedIn: [Ashit Verma](https://www.linkedin.com/in/ashit-verma-6b7769337)

## Acknowledgments

- Spring Boot team for the excellent framework
- Baeldung and Spring.io for great documentation
- AWS for cloud infrastructure
- The open-source community
