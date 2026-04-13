## 📝 Full-Stack Task Management Application (To-Do App)

Modern, secure, and production-ready To-Do application built with **Spring Boot 3** and **React**. The project demonstrates a stateless architecture using **JWT** for authentication and **Docker** for containerization.

## 🚀 Key Features

* **Secure Authentication:** Stateless login and registration using Spring Security and JWT.
* **Role-Based Access Control (RBAC):** Separation of user privileges (USER/ADMIN).
* **Data Integrity:** Each user has private access only to their own tasks.
* **Task Management (CRUD):** Create, view, update, and delete personal tasks with priority and status levels.
* **Database Migrations:** Version control for database schema using **Flyway**.
* **Validation:** Strict DTO validation and global exception handling (`@ControllerAdvice`).
* **Reliability:** Comprehensive unit testing with **JUnit 5** and **Mockito**.

## 🛠 Tech Stack

### Backend
* **Java 17**
* **Spring Boot 3.5.6** (Data JPA, Security, Validation)
* **PostgreSQL** (Main database)
* **Flyway** (Database migrations)
* **JJWT** (JSON Web Token implementation)
* **Lombok & MapStruct** (Code simplification & mapping)

### Frontend
* **React** (with Vite)
* **React Router Dom** (Navigation & Protected Routes)
* **CSS3** (Responsive design)

### Infrastructure
* **Docker & Docker Compose** (Containerization)
* **Nginx** (Serving React static files & API Proxy)

## 📦 Installation & Setup

### Prerequisites
* Docker & Docker Compose installed.

### Launching the App
1. Clone the repository:
   ```bash
   git clone [https://github.com/mountainPompeii/todolist-app.git](https://github.com/mountainPompeii/todolist-app.git)
   cd todoapp
   ```

2. Create a `.env` file in the root directory and fill in your secrets:
   ```env
   DB_USER=postgres
   DB_PASSWORD=your_password
   JWT_SECRET_KEY=your_very_long_and_secure_random_key_here
   ```

3. Run the application using Docker Compose:
   ```bash
   docker-compose up --build
   ```

Once the containers are running:
* **Frontend:** `http://localhost:3000`
* **Backend API:** `http://localhost:8080/api`

## 🏗 Project Structure

```text
├── src/                # Spring Boot Backend source code
│   └── main/resources/db/migration  # Flyway SQL scripts
├── frontend/           # React Frontend source code (Vite)
│   ├── src/            # Components, Pages, Services
│   └── nginx.conf      # Nginx configuration for Docker
├── docker-compose.yml  # Docker orchestration (DB, API, Web)
├── Dockerfile          # Multi-stage build for Java JAR
├── .env                # Environment variables (IGNORED BY GIT)
└── pom.xml             # Maven dependencies & build config
```

## 🔐 Security Note
The application uses **stateless JWT authentication**.
* Passwords are encrypted using **BCrypt**.
* Tokens are stored in `LocalStorage` on the client-side.
* Protected routes on the frontend and security filters on the backend ensure data privacy.

## 📝 License
This project is open-source and available under the **Apache License 2.0**.

---
*Developed by [Arsenii Sidorovych](https://github.com/sleepwalker746)*
