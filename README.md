# User Registration Application

## Description
Simple **CRUD application** for user management built with **Spring Boot** and **H2 Database** (in-memory).

## Technologies
- Java 17+
- Spring Boot
- Spring Data JPA
- H2 Database
- Lombok

## Features
- **Create user** (`POST /user`)
- **Get user by email** (`GET /user?email={email}`)
- **Update user by ID** (`PUT /user?id={id}`)
- **Delete user by email** (`DELETE /user?email={email}`)

## Running the App
```bash
git clone <repo_url>
cd user-registration-applicat

## Database

H2 Console: http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:user

Username: root

Password: root
