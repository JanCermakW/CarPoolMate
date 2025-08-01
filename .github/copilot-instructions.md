# Copilot Instructions for CarPoolMate

## Project Overview
CarPoolMate is a full-stack carpooling web application with a Spring Boot backend (Java), thymeLeaf/Bootstrap frontend, and MySQL database. It enables users to register, offer/search rides, book seats, and review participants. Security is enforced via Spring Security with role-based access (ADMIN > DRIVER > USER).

## Architecture & Key Components
- **Backend:**
  - `src/main/java/com/carpoolmate/carpoolmate/`
    - `model/` – JPA entities: User, Ride, RideRequest, Review
    - `repository/` – Spring Data JPA repositories
    - `service/` – Business logic (UserService, RideService, ReviewService, EmailService)
    - `web/` – Controllers (e.g., `RideController`), HTTP endpoints, thymeLeaf view mapping
    - `config/` – Security, password encoding, role hierarchy
    - `exception/` – Custom exceptions
- **Frontend:**
  - `src/main/resources/templates/` – thymeLeaf HTML templates (user, ride, admin, fragments)
  - `src/main/resources/static/` – CSS, images, and frontend assets
- **Database:**
  - MySQL, schema auto-managed by JPA (`application.properties`)
  - Key tables: users, rides, ride_requests, reviews

## Developer Workflows
- **Build & Run:**
  - Use Maven wrapper: `./mvnw spring-boot:run` (Linux/macOS) or `mvnw.cmd spring-boot:run` (Windows)
  - Java version: 24 (see `pom.xml`)
- **Testing:**
  - Run all tests: `./mvnw test` or `mvnw.cmd test`
  - Tests use JUnit 5 and Spring Boot Test (`src/test/java/...`)
- **Database:**
  - Default: MySQL on localhost, credentials in `application.properties`
  - Schema auto-updates on startup (`spring.jpa.hibernate.ddl-auto=update`)
- **API Docs:**
  - Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Monitoring:**
  - Spring Boot Actuator: `/actuator/health`, `/actuator/info`, `/actuator/metrics`
  - Logs: `logs/myapp.log`

## Security & Roles
- Role hierarchy: ADMIN > DRIVER > USER
- Access rules in `SecurityConfiguration.java`:
  - `/admin/**` – ADMIN only
  - `/user/**`, `/api/user/**`, `/rides/book/**`, `/profiles/**` – USER or higher
  - `/rides/create/**` – DRIVER or higher
  - Public: `/registration/**`, `/forgotPasswd`, `/swagger-ui.html`, static assets
- Passwords encrypted with BCrypt
- Default admin: `admin@example.com` / `admin` (see `AdminUserInitializer`)

## Project Conventions & Patterns
- **Layered structure:** Model → Repository → Service → Web (Controller)
- **thymeLeaf** for all server-rendered views; no REST API for frontend JS (except AJAX for some actions)
- **Email notifications** via Spring Mail (SMTP config in `application.properties`)
- **No payment integration** yet (see SDD/SRS for future plans)
- **All business logic** in `service/` layer, controllers are thin
- **RBAC enforced** at both controller and service layers
- **Entity relationships**: see SDD.txt for ER diagram and field details

## Integration Points
- **Email:** SMTP (Gmail by default)
- **Swagger/OpenAPI:** via springdoc-openapi
- **Actuator:** for health/metrics
- **MySQL:** for persistent storage

## References
- `README.md`, `SRS.txt`, `SDD.txt` – for requirements, design, and architecture
- `Admin-guidelines.txt` – for admin-specific info
- `application.properties` – for config and credentials
- `pom.xml` – for dependencies and plugins

---

**For AI agents:**
- Follow the layered structure and use existing services for business logic.
- Use thymeLeaf templates for UI; do not introduce SPA frameworks.
- Respect RBAC and security conventions.
- When adding features, update Swagger docs and actuator endpoints if relevant.
- Reference SDD.txt for data model and flow details.
