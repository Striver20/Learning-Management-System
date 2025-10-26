# 🎓 Learning Management System (LMS)

A full-stack Learning Management System built with **Spring Boot** and **React**, featuring JWT authentication, role-based access control, course management, and AWS S3 integration for file storage.

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen?style=flat-square&logo=springboot)
![React](https://img.shields.io/badge/React-18.2.0-61DAFB?style=flat-square&logo=react)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql)
![AWS S3](https://img.shields.io/badge/AWS-S3-FF9900?style=flat-square&logo=amazonaws)

---

## 🌐 Live Demo

**Frontend (React):** [http://lms-frontend-ashit.s3-website-ap-southeast-2.amazonaws.com](http://lms-frontend-ashit.s3-website-ap-southeast-2.amazonaws.com)

**Backend API:** Deployment in progress - will be available soon!

**Note:** The application is currently deployed on AWS infrastructure (S3 for frontend, EC2 + RDS for backend). Full deployment documentation available in repository.

---

## 📖 About This Project

I built this Learning Management System to demonstrate my ability to design and develop a production-ready full-stack application. The system handles user authentication, role-based authorization, course creation, content management, student enrollments, and progress tracking—all while following industry best practices for security and scalability.

**What makes this project stand out:**

- Clean, layered architecture with separation of concerns
- JWT-based stateless authentication
- Real-time progress tracking with persistent storage
- AWS S3 integration with local fallback for development
- Responsive, modern UI built with React and Tailwind CSS
- Comprehensive API documentation with Swagger UI

---

## ✨ Features

### 🔐 Authentication & Authorization

- **JWT-based authentication** for secure, stateless sessions
- **Role-based access control** with 3 user roles: Admin, Teacher, Student
- **BCrypt password hashing** for secure credential storage
- Protected API endpoints with Spring Security

### 👨‍🏫 Teacher Features

- Create and manage courses with title, description, and category
- Upload course content (PDFs, documents) to AWS S3
- Organize lessons with order and content types
- View all courses created by the instructor
- Delete course content

### 👨‍🎓 Student Features

- Browse and enroll in available courses
- Track course progress with lesson completion
- View personalized dashboard with enrolled courses
- Resume courses from where they left off
- Progress automatically saved to backend

### 👨‍💼 Admin Features

- View all users, courses, and enrollments
- Assign/remove roles dynamically
- Delete users and courses
- Monitor enrollment statistics
- Update enrollment statuses
- System-wide analytics dashboard

### 📦 Course Management

- Full CRUD operations for courses
- Content upload with AWS S3 integration
- Local file storage fallback for development
- Ordered lesson structure
- Category-based course organization
- Progress tracking per student per course

### 📊 Progress Tracking

- Lesson-level completion tracking
- Overall course progress calculation
- Persistent storage across sessions
- Real-time UI updates
- Backend synchronization

---

## 🛠️ Tech Stack

### Backend

- **Spring Boot 3.3.4** - Core framework
- **Spring Security** - Authentication & authorization
- **JWT (JSON Web Tokens)** - Stateless authentication
- **JPA/Hibernate** - ORM for database operations
- **MySQL 8.0** - Relational database
- **AWS SDK for Java** - S3 file storage
- **Swagger/OpenAPI 3** - API documentation
- **Lombok** - Reduce boilerplate code
- **Maven** - Dependency management

### Frontend

- **React 18.2.0** - UI library
- **React Router** - Client-side routing
- **Axios** - HTTP client
- **Tailwind CSS** - Utility-first styling
- **Local Storage** - Token persistence

### DevOps & Tools

- **Git** - Version control
- **IntelliJ IDEA** - Development IDE
- **Postman** - API testing
- **MySQL Workbench** - Database management

---

## 🏗️ Architecture

The application follows a **layered architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│         React Frontend (Port 3000)      │
│  (Login, Dashboard, Course Management)  │
└─────────────────┬───────────────────────┘
                  │ HTTP/REST API
┌─────────────────▼───────────────────────┐
│      Spring Boot Backend (Port 8080)    │
├─────────────────────────────────────────┤
│  Controllers → Services → Repositories  │
│     ↓              ↓            ↓       │
│  JWT Filter   Business Logic   JPA      │
└─────────────────┬───────────────────────┘
                  │
    ┌─────────────┼─────────────┐
    ▼             ▼             ▼
┌────────┐  ┌──────────┐  ┌─────────┐
│ MySQL  │  │  AWS S3  │  │ Local   │
│   DB   │  │  Bucket  │  │ Storage │
└────────┘  └──────────┘  └─────────┘
```

### Request Flow

1. **User Action** → React sends HTTP request with JWT token
2. **Security Filter** → Backend validates JWT token
3. **Authorization** → Check user roles and permissions
4. **Controller** → Receives request and delegates to service
5. **Service Layer** → Implements business logic
6. **Repository** → Interacts with database via JPA
7. **Response** → Data returned as JSON to frontend

---

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.6+**
- **Node.js 16+** and **npm**
- **MySQL 8.0+**
- **AWS Account** (optional, for S3 integration)

### Backend Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/Striver20/Learning-Management-System.git
   cd Learning-Management-System
   ```

2. **Create MySQL database**

   ```sql
   CREATE DATABASE lmsdb;
   ```

3. **Configure environment variables**

   Set these environment variables in your IDE or terminal:

   ```bash
   DB_USERNAME=root
   DB_PASSWORD=your_mysql_password
   AWS_ACCESS_KEY=your_aws_access_key    # Optional
   AWS_SECRET_KEY=your_aws_secret_key    # Optional
   ```

4. **Run the backend**

   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

   The backend will start on `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory**

   ```bash
   cd frontend
   ```

2. **Install dependencies**

   ```bash
   npm install
   ```

3. **Start the development server**

   ```bash
   npm start
   ```

   The frontend will start on `http://localhost:3000`

---

## 📚 API Documentation

Once the backend is running, access the **Swagger UI** for interactive API documentation:

**URL:** `http://localhost:8080/swagger-ui.html`

### Quick Test Flow

1. **Register a new user**

   - `POST /api/auth/register`
   - Provide: fullName, email, password, role (ROLE_STUDENT, ROLE_TEACHER, ROLE_ADMIN)

2. **Login**

   - `POST /api/auth/login`
   - Get your JWT token from the response

3. **Authorize in Swagger**

   - Click the green "Authorize" button
   - Enter: `Bearer <your-jwt-token>`

4. **Test protected endpoints**
   - Create courses, upload content, enroll students, track progress

### Key Endpoints

| Method   | Endpoint                | Description       | Auth Required |
| -------- | ----------------------- | ----------------- | ------------- |
| `POST`   | `/api/auth/register`    | Register new user | No            |
| `POST`   | `/api/auth/login`       | Login and get JWT | No            |
| `GET`    | `/api/courses`          | Get all courses   | Yes           |
| `POST`   | `/api/courses`          | Create course     | Teacher       |
| `POST`   | `/api/contents/upload`  | Upload content    | Teacher       |
| `POST`   | `/api/enrollments`      | Enroll in course  | Student       |
| `POST`   | `/api/progress/update`  | Update progress   | Student       |
| `GET`    | `/api/admin/users`      | Get all users     | Admin         |
| `DELETE` | `/api/admin/users/{id}` | Delete user       | Admin         |

---

## 📁 Project Structure

```
Learning-Management-System/
│
├── backend/
│   ├── src/main/java/com/example/lms/
│   │   ├── config/           # Security, CORS, S3 configuration
│   │   ├── controller/       # REST API endpoints
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── entity/           # JPA entities (User, Course, etc.)
│   │   ├── repository/       # Database repositories
│   │   ├── service/          # Business logic layer
│   │   ├── security/         # JWT authentication
│   │   └── exception/        # Custom exceptions
│   │
│   ├── src/main/resources/
│   │   └── application.properties  # Configuration
│   │
│   └── pom.xml               # Maven dependencies
│
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/       # Reusable UI components
│   │   ├── pages/            # Page components
│   │   │   ├── Login.js
│   │   │   ├── Register.js
│   │   │   ├── StudentHome.js
│   │   │   ├── TeacherDashboard.js
│   │   │   ├── ManageCourse.js
│   │   │   ├── CourseDetail.js
│   │   │   └── AdminDashboard.js
│   │   │
│   │   ├── services/         # API calls (axios)
│   │   ├── App.js            # Main app with routing
│   │   └── index.js          # Entry point
│   │
│   └── package.json          # npm dependencies
│
└── README.md
```

---

## 🎯 Key Technical Decisions

### Why JWT?

I chose JWT over session-based authentication for:

- **Stateless architecture** - Easier to scale horizontally
- **Cross-domain support** - Frontend and backend can be on different domains
- **Mobile-ready** - Token can be used in mobile apps
- **Performance** - No server-side session storage needed

### Why Layered Architecture?

Separation of concerns makes the codebase:

- **Maintainable** - Each layer has a single responsibility
- **Testable** - Easy to unit test individual layers
- **Scalable** - Can replace components without affecting others
- **Professional** - Industry-standard design pattern

### Why Local Storage Fallback?

For the file upload system, I implemented AWS S3 with a local storage fallback:

- **Development flexibility** - Works without AWS credentials locally
- **Cost-effective** - Avoid S3 charges during development
- **Graceful degradation** - System continues to function if S3 fails
- **Production-ready** - Seamlessly switches to S3 when configured

---

## 🔒 Security Features

- **BCrypt Password Hashing** - Passwords are never stored in plain text
- **JWT Token Expiration** - Tokens expire after a set period
- **Role-Based Access Control** - Endpoints protected by user role
- **CORS Configuration** - Prevents unauthorized cross-origin requests
- **SQL Injection Prevention** - JPA parameterized queries
- **XSS Protection** - Input validation and sanitization

---

## 🎨 UI/UX Highlights

- **Responsive Design** - Works on desktop, tablet, and mobile
- **Consistent Styling** - Unified design language across all pages
- **Loading States** - User feedback during async operations
- **Error Handling** - Clear error messages and validation
- **Intuitive Navigation** - Easy to find and use features
- **Progress Indicators** - Visual feedback for course completion
- **Modern UI** - Built with Tailwind CSS for a clean, professional look

---

## 🧪 Testing

### Manual Testing

- All API endpoints tested via Swagger UI
- Frontend flows tested for each user role
- File upload/download tested with various file types
- Progress tracking verified across sessions

### Test Credentials

```
Admin:   admin2@gmail.com / 123456
Teacher: teacher1@gmail.com / password
Student: student1@gmail.com / password
```

---

## 🚀 Deployment Ready

The application is designed for cloud deployment:

### AWS Infrastructure

- **EC2** - Host Spring Boot application
- **RDS MySQL** - Production database
- **S3** - File storage for course content
- **CloudFront** (optional) - CDN for static assets

### Environment Configuration

Create production profile with:

```properties
spring.datasource.url=jdbc:mysql://your-rds-endpoint:3306/lmsdb
spring.jpa.hibernate.ddl-auto=validate
aws.s3.bucket=your-production-bucket
```

---

## 💡 What I Learned

Building this project taught me:

- How to design and implement a **secure, scalable REST API**
- Working with **JWT authentication** and Spring Security
- Managing **many-to-many relationships** in JPA (Users ↔ Roles, Students ↔ Courses)
- Integrating **AWS services** into a Spring Boot application
- Building a **responsive React frontend** with modern design patterns
- Implementing **persistent state management** across frontend and backend
- Handling **file uploads** with multiple storage strategies
- Creating **comprehensive API documentation** with Swagger

---

## 🔮 Future Enhancements

Features I'd like to add:

- [ ] Email notifications for course enrollments
- [ ] Real-time chat between students and teachers
- [ ] Video content support with streaming
- [ ] Quiz and assignment modules
- [ ] Certificate generation on course completion
- [ ] Mobile app (React Native)
- [ ] Dark mode toggle
- [ ] Multi-language support

---

## 📸 Screenshots

### Student Dashboard

Students can view enrolled courses and track their progress.

### Teacher Dashboard

Teachers can create courses, upload content, and manage lessons.

### Admin Panel

Admins have full control over users, courses, and enrollments.

---

## 🤝 Contributing

This is a portfolio project, but I'm open to feedback and suggestions!

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - feel free to use it for learning or your own portfolio!

---

## 👨‍💻 Author

**Ashit Verma**

- GitHub: [@Striver20](https://github.com/Striver20)
- LinkedIn: [Ashit Verma](https://www.linkedin.com/in/ashit-verma-6b7769337)
- Email: ashitverma56@gmail.com

---

## 🙏 Acknowledgments

Thanks to:

- The Spring Boot and React communities for excellent documentation
- Stack Overflow for helping me debug tricky issues
- AWS for their comprehensive SDK documentation

---

**⭐ If you find this project helpful, please give it a star!**

_Built with ☕ and lots of debugging_
