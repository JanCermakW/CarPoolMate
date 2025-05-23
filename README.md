# 🚗 CarPoolMate – Ride Sharing Web Application

**CarPoolMate** is a modern web application designed to connect drivers and passengers heading in the same direction. Whether commuting to work or heading to a festival, CarPoolMate helps people save money, reduce their carbon footprint, and build community.

---

## 🧭 Project Goals & Features

- Match drivers with passengers based on route, time, price, and preferences
- Support for recurring rides and driver verification
- Role-based functionality (admin, driver, user)
- Rating & review system for trust building
- Integrated email notifications for account and ride events

---

## 🧱 System Architecture

CarPoolMate follows a classic multi-layered architecture:

- **Frontend:** Thymeleaf, Bootstrap, HTML templates
- **Backend:** Spring Boot REST + MVC
- **Database:** MySQL with JPA/Hibernate
- **Security:** Spring Security with role hierarchy and password encoding

---

## 🔐 Security Mechanisms

The application uses multiple security features:

- **Spring Security:** Protects all endpoints
- **Role-Based Access Control (RBAC):**
    - `ROLE_ADMIN` > `ROLE_DRIVER` > `ROLE_USER`
- **Password Encryption:** `BCryptPasswordEncoder`
- **Custom Login Page & Session Handling**
- **Email Verification & Password Reset (with token)**

---

## 🧪 API Validation & Feedback Examples
- Password validation is enforced during registration and password change

- Duplicate registration attempts are rejected with clear redirects (?error, ?errorPasswd)

- Users can only leave a review once every 2 months per person

- Full feedback via redirect attributes and UI error/success banners

---

## 📈 Logging & Monitoring
- The system uses built-in Spring Boot logging and Actuator:

- Hibernate SQL logs for dev debugging

- Logs stored in: logs/myapp.log

- Application monitoring with:

  - health, info, metrics exposed via Actuator

application.properties:

logging.file.name=logs/myapp.log
management.endpoints.web.exposure.include=health,info,metrics
spring.jpa.hibernate.ddl-auto=update

---

## 📚 API Documentation
- OpenAPI/Swagger documentation is integrated using springdoc-openapi:

- Swagger UI: http://localhost:8080/swagger-ui.html

- Includes all public-facing and internal endpoints (annotated with @Operation, @ApiResponse, etc.)

---

## 📁 Folder Structure Overview

| **Layer**     | **Packages / Classes**                                   |
|---------------|----------------------------------------------------------|
| `Model`       | `User`, `Ride`, `Review`, `RideRequest`, `Role`, etc.    |
| `Service`     | `UserService`, `RideService`, `ReviewService`, etc.      |
| `Repository`  | `UserRepository`, `RideRepository`, etc.                 |
| `Web`         | Controllers, DTOs (`UserRegistrationDto`)                |
| `Security`    | Custom config in `SecurityConfiguration.java`            |


---

## ✅ Technologies Used
- Java 21+
- Spring Boot 6.x
- Spring Security
- Spring Data JPA
- MySQL
- Thymeleaf
- Bootstrap 5
- Swagger (springdoc-openapi)

---

## 👤 Default Admin Account

Upon first launch, a default admin user is automatically created:

- **Email:** `admin@example.com`
- **Password:** `admin`

This account has full access to all administrative features of the application.  
It is recommended to change the password upon first login.
