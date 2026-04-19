# User Management System

A backend REST API application built using **Java, Spring Boot, Spring Security, MySQL, and Swagger** for user authentication, administration, and role-based access control.

## Features

- User Registration
- User Login Authentication
- Admin Module
- Role-Based Access Control (Admin/User)
- User CRUD Operations
- Secure Password Encryption using BCrypt
- REST APIs
- Input Validation and Global Exception Handling
- API Documentation using Swagger
- MySQL Database Integration

## Admin Module Features

- Manage Users
- View All Users
- Update User Details
- Delete Users
- Role Management
- Access Protected Admin Endpoints

## Tech Stack

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL
- Swagger / OpenAPI
- Maven

## Project Structure

src/main/java
├── controller
├── service
├── repository
├── entity
├── dto
├── config
├── security
└── exception
```

## API Endpoints

### Authentication APIs
POST /register
POST /login

### User APIs
GET    /users
GET    /users/{id}
PUT    /users/{id}
DELETE /users/{id}

### Admin APIs
GET    /admin/users
PUT    /admin/users/{id}
DELETE /admin/users/{id}

## Swagger API Documentation
Access Swagger UI:
http://localhost:8080/swagger-ui/index.html

## Database Configuration

Update application.properties:
properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update

## Run the Application



## Security Features
- BCrypt Password Encoding
- Spring Security Authentication
- Role-Based Authorization
- Protected Admin Endpoints

GitHub:
https://github.com/sldprasad022

LinkedIn:
https://www.linkedin.com/in/durgaprasadsunkara/
