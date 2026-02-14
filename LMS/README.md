# LMS (AKTU Academic Portal)

This is a small Spring Boot LMS sample project (AKTU AcademicPortal — `LMS`) that provides both web pages and REST APIs for managing courses, branches, subjects, instructors, learning materials and learners.

This README documents the project structure and lists all public controllers/endpoints found in the codebase with request paths, HTTP methods, parameters, and example payloads.

---

## Quick start

Requirements
- Java 17
- Maven
- MySQL (or configure another datasource in `src/main/resources/application.properties`)

Run locally
```bash
cd path/to/LMS
mvn -U clean package
java -jar target/LMS-0.0.1-SNAPSHOT.jar
```

Open the web UI at `http://localhost:8080`.

---

## Project structure (relevant folders)
- `src/main/java/com/app/controller` — web controllers and REST controllers
- `src/main/java/com/app/dto` — DTO classes used for requests and forms
- `src/main/java/com/app/entity` — JPA entities
- `src/main/resources/templates` — Thymeleaf templates (web pages)

---

## Controllers and Endpoints

Note: some controllers serve web pages (Thymeleaf) and some expose JSON REST endpoints. Authentication/session handling in this sample is minimal (session attributes used as placeholders).

### 1) Admin (web) — `AdminController`
Base path: `/admin`

- GET `/admin` — Admin dashboard (web page)
  - Response: HTML (Thymeleaf) `admin/dashboard`

- GET `/admin/courses` — Manage courses (page)
  - Model attributes: `courses`, `courseDto`
  - Response: `admin/courses`

- POST `/admin/course` — Add course (form POST)
  - Form object: `CourseDto` (see DTO section)
  - Validations: `@Valid` on `CourseDto`
  - Redirects back to `/admin/courses` with flash attributes `success` or `error`

- POST `/admin/course/delete` — Delete course
  - Parameters: `id` (Long, RequestParam)

- GET `/admin/branches` — Manage branches (page)
  - Model attributes: `branches`, `courses`, `branchDto`

- POST `/admin/branch` — Add branch (form POST)
  - Form object: `BranchDto`
  - Validations: `@Valid`

- POST `/admin/branch/delete` — Delete branch
  - Parameters: `id` (Long)

- GET `/admin/subjects` — Manage subjects (page)
  - Model attributes: `subjects`, `branches`, `subjectDto`

- POST `/admin/subject` — Add subject
  - Form object: `SubjectDto`

- POST `/admin/subject/delete` — Delete subject
  - Parameters: `code` (String)

- GET `/admin/test-login` — DEV only: set `isAdmin` session attribute to `true` and redirect to `/admin` (useful for testing the admin pages)

Authentication: simple placeholder session flag `isAdmin` must be true to access admin pages.

---

### 2) Instructor REST API — `InstructorController`
Base path: `/api/instructor`

- GET `/api/instructor/subjects` — Return all `Subject` entities
  - Response: JSON array of `Subject` objects
  - Example: `[{"subjectCode":"CS101","subjectName":"Intro to CS", ...}]`

- POST `/api/instructor/register` — Register instructor (JSON request)
  - Request JSON: `InstructorRegisterDto`
    ```json
    {
      "name": "Alice",
      "email": "alice@example.com",
      "password": "secret",
      "subjectCodes": ["CS101","CS102"]
    }
    ```
  - Response: `Instructor` (JSON) — created instructor object

---

### 3) Instructor Web UI — `InstructorWebController`
Base path: `/instructor`

- GET `/instructor/register` — Show registration form (Thymeleaf)
- POST `/instructor/register` — Submit registration form (`InstructorRegisterDto` from form)
  - Validates name, email contains `@`, and password length >= 6
  - Redirects to `/instructor/login` on success

- GET `/instructor/login` — Login form
- POST `/instructor/login` — Login with `email` and `password` (form fields)
  - On success: sets session attribute `instructorId` and redirects to `/instructor/dashboard`

- GET `/instructor/dashboard` — Instructor dashboard (requires `instructorId` in session)
  - Model attributes: `instructorProfile`, `subjects`, `material` (new LearningMaterial)

- POST `/instructor/dashboard/save` — Save learning material from dashboard
  - Form parameters: LearningMaterial model object (fields from `LearningMaterial` entity), `subjectCode` (String), `instructorId` retrieved from session
  - Redirects to `/instructor/dashboard`

- GET `/instructor/logout` — Invalidate session and redirect to `/`

Note: The `InstructorWebController` uses `PasswordEncoder` to validate stored password hashes.

---

### 4) Learning Materials (web) — `LearningMaterialController`
Base path: `/learning-materials`

- GET `/learning-materials` — List all learning materials (page `learning-materials`)
- GET `/learning-materials/add` — Form to add new learning material (page `add-learning-material`)
  - Model attributes: `material`, `instructors`, `subjects`
- POST `/learning-materials/save` — Save new learning material (form POST)
  - Parameters: `@ModelAttribute LearningMaterial lm`, `instructorId` (Long), `subjectCode` (String)
  - Behavior: fetches Instructor and Subject by id/code and links them to `lm` then saves via `learningMaterialService`

---

### 5) Learner Web UI — `LearnerController`
Base path: `/learner`

- GET `/learner` (or `/learner/`) — Learner homepage showing subject list
- GET `/learner/subject/{subjectCode}` — Show subject page with computed units
- GET `/learner/subject/{subjectCode}/unit/{unitNo}` — Show topics for a unit
- GET `/learner/topic/{id}` — Show a single topic content page

These are server-rendered Thymeleaf pages under `templates/learner/`.

---

## DTOs (request/form shapes)

- `InstructorRegisterDto`
  - name (String)
  - email (String)
  - password (String)
  - subjectCodes (Set<String>) — subject codes the instructor will teach

- `CourseDto` (used in admin form)
  - courseName (String)

- `BranchDto` (admin form)
  - courseId (Long)
  - branchName (String)

- `SubjectDto` (admin form)
  - branchId (Long)
  - subjectCode (String)
  - subjectName (String)

Validation annotations are used on DTOs (e.g. `@NotBlank`, `@NotNull`, `@Size`).

---

## Notes & caveats
- Authentication & authorization are minimal/placeholders (session attributes used to guard admin and instructor pages). This is not production-ready.
- Many controllers return Thymeleaf views; REST API controllers that return JSON are limited (`/api/instructor/*`).
- The POM currently includes `spring-boot-starter-validation` and an explicit `jakarta.validation-api` dependency — you can remove the explicit API artifact if you prefer (the starter already brings it transitively).

---

## How to contribute / extend
- Add REST endpoints in `com.app.controller` using `@RestController` and `ResponseEntity`.
- Add DTOs under `com.app.dto` and annotate with Jakarta Validation constraints.
- For data access, check repositories under `com.app.repository` and entities under `com.app.entity`.

---

## Verification
After changes, build and run tests (or skip tests) to verify:
```bash
mvn -U clean package -DskipTests
```

---

If you want, I can:
- Add example curl commands for each REST API endpoint.
- Generate an OpenAPI/Swagger spec by scanning controllers and producing a minimal YAML/JSON file.
- Replace the explicit `jakarta.validation-api` dependency in `pom.xml` as suggested earlier.

Tell me which of those you prefer and I'll proceed.