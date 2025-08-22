# User Registration Application

## Description
User Registration Application is a **CRUD system** for managing users, developed with **Spring Boot**.  
The application allows creating, reading, updating, and deleting users through a **RESTful API**.  
It uses **H2 in-memory database** for storage, making it easy to run without external dependencies.  
The project demonstrates the use of **Spring Data JPA**, **Lombok**, and best practices for service and controller layers.

---

## Technologies
- Java 17+
- Spring Boot
- Spring Data JPA
- H2 Database (in-memory)
- Lombok (for getters, setters, builders, constructors)
- Maven

---

## Features
- **Create User:** Register a new user with unique email.
- **Read User:** Retrieve user details by email.
- **Update User:** Update user data by ID. Only provided fields are updated.
- **Delete User:** Remove user by email.
- **H2 Console:** Web interface to view database contents.

---

## Project Structure

com.diegobrsantosdev.user_registration_application
│
├─ UserRegistrationApplication.java # Main application class
├─ controller/
│ └─ UserController.java # REST endpoints
├─ business/
│ └─ UserService.java # Service layer (business logic)
├─ infrastructure/
│ ├─ entitys/
│ │ └─ User.java # User entity (JPA)
│ └─ repository/
│ └─ UserRepository.java # Database access interface
└─ resources/
└─ application.properties # Application configuration

---

## Endpoints

| Method | Endpoint | Description | Params / Body |
|--------|---------|------------|---------------|
| POST   | /user   | Create a new user | JSON body: `{"name": "Name", "email": "email@example.com"}` |
| GET    | /user   | Get user by email | Query param: `email` |
| PUT    | /user   | Update user by ID | Query param: `id`, JSON body optional: `{"name":"...","email":"..."}` |
| DELETE | /user   | Delete user by email | Query param: `email` |

---

## Running the Application

Access the application via API at http://localhost:8080/user

H2 Database Console (optional)
URL: http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:user

Username: root

Password: root
