# AKTU Academic Portal - LMS

A lightweight Learning Management System (LMS) for academic notes built with Spring Boot and Thymeleaf.

This project provides a simple portal for teachers to register/login and upload notes (topics) organized by subjects and units, and for students to browse subjects, units, and view topics.

Project layout
- Java 17, Spring Boot (parent: 4.0.2)
- Thymeleaf templates for server-rendered UI located in `src/main/resources/templates`
- Static assets (CSS/JS) in `src/main/resources/static`
- JPA entities and repositories under `com.app.entity` and `com.app.repository`

Key pages
- `/` - public home page
- `/teacher/register` - teacher registration (web)
- `/teacher/login` - teacher login (web)
- `/teacher/dashboard` - teacher dashboard to add notes (requires login)
- `/notes` - list notes (web)
- `/notes/add` - add note form (web)
- `/student` - student index (list subjects)
- `/student/subject/{subjectCode}` - view a subject and its units
- `/student/subject/{subjectCode}/unit/{unitNo}` - view topics in a unit
- `/student/topic/{noteId}` - view a single topic/note

API endpoints
- `GET /api/teacher/subjects` - returns all subjects (JSON)
- `POST /api/teacher/register` - register a teacher (JSON body: `TeacherRegisterDto`)

Configuration (application.properties)
- Default datasource is MySQL:
  - URL: `jdbc:mysql://127.0.0.1:3306/aktu`
  - Username: `root`
  - Password: `boot`
- JPA is configured with `spring.jpa.hibernate.ddl-auto=update`
- A simple Spring Security user is provided in properties:
  - Username: `admin`
  - Password: `admin123`

Requirements
- Java 17
- Maven 3.6+ (or the included Maven Wrapper `mvnw` / `mvnw.cmd`)
- MySQL (or change datasource to another DB in `application.properties`)

Quick start
1. Update `src/main/resources/application.properties` with your DB credentials (or use the defaults for a local test MySQL instance):

```ini
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/aktu?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=boot
```

2. Build and run with the Maven wrapper (Windows):

```powershell
mvnw.cmd clean package
mvnw.cmd spring-boot:run
```

Or using your installed Maven:

```powershell
mvn clean package
mvn spring-boot:run
```

3. Open in browser: http://localhost:8080/

Developer notes
- The project provides a simple session-based login for teachers (not a production-ready auth system).
- Passwords are stored encoded by a `PasswordEncoder` via `TeacherService`.
- HTML content sanitization for notes is implemented using jsoup in `NoteServiceImpl`.
- Data model includes entities: `Teacher`, `Subject`, `Note`, `Course`, `Branch` (see `com.app.entity`).

Testing
- Unit tests (if present) are located under `src/test/java` and can be run with:

```powershell
mvnw.cmd test
```

Security & production notes
- This project is intended as a learning/demo application. For production use:
  - Replace the simple session handling with Spring Security properly configured.
  - Externalize configuration (don’t keep passwords in properties files).
  - Use HTTPS and secure cookies for sessions.
  - Harden authentication and authorization controls and validate inputs thoroughly.

Contributing
- Fork the repository, create a feature branch, and open a pull request with a clear description of changes.
- Run tests and ensure the application builds before submitting.

License
- No license specified in the repository. Add a LICENSE file if you intend to open-source this project.

Contact
- For questions or help, open an issue on the repository on GitHub.
