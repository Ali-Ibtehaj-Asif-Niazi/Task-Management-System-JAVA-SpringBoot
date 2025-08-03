# Task-Management-System-JAVA-Spring-Boot

## Table of Contents
- [Technologies Installed](#tech)
- [Backend (Spring Boot Microservices)](#backend)
    - [User Service](#user-service)
    - [Project Service](#project-service)
    - [Task Service](#task-service)


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
    
    Linux
    ```bash
    curl -X POST http://localhost:8080/users \
    -H "Content-Type: application/json" \
    -d '{"name":"Ali","email":"ali@example.com"}'
    ```

    Windows CMD
    ```bash
    curl -X POST http://localhost:8080/users -H "Content-Type: application/json" -d "{\"name\":\"TestName\",\"email\":\"test@example.com\"}"
    ```
2. Get All Users
    ```bash
    curl http://localhost:8080/users
    ```
3. Get User by ID
    ```bash
    curl http://localhost:8080/users/<id>
    ```

<a id="project-service"></a>
### Project Service
- Project management (create, retrieve)
- RESTful API endpoints
- MongoDB persistence
- Layered architecture (Controller → Service → Repository)

### API Endpoints

| Method | Endpoint        | Description                  |
|--------|-----------------|------------------------------|
| POST   | /projects       | Create a new project         |
| GET    | /projects       | Get all projects             |
| GET    | /projects/{id}  | Get a specific project by ID |

### Installation
1. Navigate to the project directory:
   ```bash
   cd Task-Management-System-JAVA-Spring-Boot/project-service
   ```
2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Usage Examples

1. Create Project:
   - **Linux**
     ```bash
     curl -X POST http://localhost:8081/projects      -H "Content-Type: application/json"      -d '{"name":"Test Project","description":"Project description","userId":"<userId>"}'
     ```
   - **Windows CMD**
     ```bash
     curl -X POST http://localhost:8081/projects -H "Content-Type: application/json" -d "{\"name\":\"Test Project\",\"description\":\"Project description\",\"userId\":\"<userId>\"}"
     ```
2. Get All Projects:
   ```bash
   curl http://localhost:8081/projects
   ```
3. Get Project by ID:
   ```bash
   curl http://localhost:8081/projects/<id>
   ```

---

<a id="task-service"></a>
### Task Service
- Task management (create, retrieve)
- RESTful API endpoints
- MongoDB persistence
- Layered architecture (Controller → Service → Repository)

### API Endpoints

| Method | Endpoint      | Description                |
|--------|---------------|----------------------------|
| POST   | /tasks        | Create a new task          |
| GET    | /tasks        | Get all tasks              |
| GET    | /tasks/{id}   | Get a specific task by ID  |

### Installation
1. Navigate to the project directory:
   ```bash
   cd Task-Management-System-JAVA-Spring-Boot/task-service
   ```
2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Usage Examples

1. Create Task:
   - **Linux**
     ```bash
     curl -X POST http://localhost:8082/tasks      -H "Content-Type: application/json"      -d '{"title":"First Task","description":"Do something","projectId":"<projectId>","userId":"<userId>"}'
     ```
   - **Windows CMD**
     ```bash
     curl -X POST http://localhost:8082/tasks -H "Content-Type: application/json" -d "{\"title\":\"First Task\",\"description\":\"Do something\",\"projectId\":\"<projectId>\",\"userId\":\"<userId>\"}"
     ```
2. Get All Tasks:
   ```bash
   curl http://localhost:8082/tasks
   ```
3. Get Task by ID:
   ```bash
   curl http://localhost:8082/tasks/<id>
   ```
