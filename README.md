User Registration Application
Description

Simple CRUD application for user management built with Spring Boot and H2 Database (in-memory).

Technologies

Java 17+

Spring Boot

Spring Data JPA

H2 Database

Lombok

Features

Create user (POST /user)

Get user by email (GET /user?email={email})

Update user by ID (PUT /user?id={id})

Delete user by email (DELETE /user?email={email})

Running the App
git clone <repo_url>
cd user-registration-application
mvn spring-boot:run


H2 Console (optional): http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:user

Username: root

Password: root

Endpoints
Method	Endpoint	Description	Params / Body
POST	/user	Create user	JSON: {"name": "Name", "email": "email@example.com"}
GET	/user	Get user by email	email param
PUT	/user	Update user by ID	id param, JSON body optional: {"name":"...","email":"..."}
DELETE	/user	Delete user by email	email param

Notes

Data is stored in-memory; it resets on restart.

Email uniqueness is enforced by the database.
