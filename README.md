# Job Recruitment Platform

A Spring Boot REST API application for managing job postings and applications with automated resume parsing capabilities.

## Features

- **User Authentication**: JWT-based authentication with role-based access control (Admin/Applicant)
- **Job Management**: Admins can create and manage job postings
- **Application System**: Applicants can browse and apply to jobs
- **Resume Parser**: Automatic extraction of skills, education, and experience from uploaded resumes (PDF/DOCX)
- **Profile Management**: Structured applicant profiles with parsed resume data

## Tech Stack

- **Backend**: Spring Boot 3.x, Spring Security
- **Database**: PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)
- **Password Encryption**: BCrypt
- **File Storage**: Local filesystem
- **Resume Parsing**: APILayer Resume Parser API
- **Build Tool**: Maven

## Database Schema

- **Users**: Stores admin and applicant details
- **Jobs**: Job postings with company info
- **Profiles**: Applicant resume data (skills, education, experience)
- **Job Applications**: Many-to-many relationship between jobs and applicants

## API Endpoints

### Authentication
- `POST /signup` - Register new user
- `POST /login` - Login and get JWT token

### Jobs
- `GET /jobs` - List all jobs (authenticated)
- `GET /jobs/apply?job_id={id}` - Apply to job (applicants only)

### Admin
- `POST /admin/job` - Create job posting
- `GET /admin/job/{id}` - Get job details with applicants
- `GET /admin/applicants` - List all applicants
- `GET /admin/applicant/{id}` - Get applicant profile

### Applicant
- `POST /uploadResume` - Upload and parse resume (PDF/DOCX)

## Setup Instructions

### 1. Database Setup
```sql
CREATE DATABASE recruitment_db;
```

### 2. Configure Application

Update `src/main/resources/application.properties`:
```properties
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
```

### 3. Run Application
```bash
mvn spring-boot:run
```

### 4. Access API

Base URL: `http://localhost:8080`

## Sample Usage

### Register Admin
```json
POST /signup
{
  "name": "Admin User",
  "email": "admin@example.com",
  "password": "password123",
  "userType": "ADMIN",
  "profileHeadline": "HR Manager",
  "address": "123 Main St"
}
```

### Login
```json
POST /login
{
  "email": "admin@example.com",
  "password": "password123"
}
```

### Create Job (Admin)
```json
POST /admin/job
Authorization: Bearer {token}

{
  "title": "Senior Java Developer",
  "description": "Looking for experienced developer...",
  "companyName": "Tech Corp"
}
```

### Upload Resume (Applicant)
```
POST /uploadResume
Authorization: Bearer {token}
Content-Type: multipart/form-data

file: resume.pdf
```

## Security

- Passwords hashed using BCrypt (strength: 12)
- JWT tokens expire after 24 hours
- Role-based access control (ADMIN/APPLICANT)
- Stateless authentication (no server-side sessions)
