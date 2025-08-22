# API User Registration Application

## Description
Simple **CRUD application** for user management built with **Spring Boot** and **H2 Database** (in-memory).

## Technologies
- Java 17+
- Spring Boot
- Spring Data JPA
- H2 Database
- Lombok

## Endpoints

| Method | Endpoint | Description | Params / Body |
|--------|---------|------------|---------------|
| POST   | /user   | Create user | JSON: `{"name": "Name", "email": "email@example.com"}` |
| GET    | /user   | Get user by email | Query param: `email` |
| PUT    | /user   | Update user by ID | Query param: `id`, JSON body optional: `{"name":"...","email":"..."}` |
| DELETE | /user   | Delete user by email | Query param: `email` |


## H2 Database Console
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:user
Username: root
Password: root

## Running the App
```bash
git clone <repo_url>
cd user-registration-application
mvn spring-boot:run
