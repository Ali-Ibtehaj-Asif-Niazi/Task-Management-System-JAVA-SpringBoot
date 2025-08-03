# Task-Management-System-JAVA-Spring-Boot

## Table of Contents
- [Technologies Installed](#tech)
- [Backend (Spring Boot Microservices)](#backend)
    - [User Service](#user-service)

<a id="backend"></a>
## Technologies Installed:
- **Java**: [Temurin JDK 21.0.8+9 (LTS)](https://adoptium.net/en-GB/temurin/releases?version=21)
- **Maven**: [Apache Maven 3.9.11](https://maven.apache.org/download.cgi)

<a id="backend"></a>
## Backend (Spring Boot Microservices):
<a id="user-service"></a>
### User Service
- User management (create, retrieve)
- RESTful API endpoints
- MongoDB persistence
- Layered architecture (Controller → Service → Repository)

### API Endpoints

| Method | Endpoint       | Description                |
|--------|----------------|----------------------------|
| POST   | /users         | Create a new user          |
| GET    | /users         | Get all users              |
| GET    | /users/{id}    | Get a specific user by ID  |

### Installation
1. Navigate to the project directory:
   ```bash
   cd Task-Management-System-JAVA-Spring-Boot/user-service
   ```
2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Usage Examples

1. Create user:
    ```bash
    curl -X POST http://localhost:8080/users \
    -H "Content-Type: application/json" \
    -d '{"name":"Ali","email":"ali@example.com"}'
    ```
2. Get All Users
    ```bash
    curl http://localhost:8080/users
    ```
3. Get User by ID
    ```bash
    curl http://localhost:8080/users/<id>
    ```
