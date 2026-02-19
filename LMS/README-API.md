# AKTU Academic Portal - LMS (API & Quick README)

This README provides a concise overview of the project, how to run it locally, and a detailed list of the web and API endpoints available in the application.

## Project Overview

A simple Learning Management System (LMS) built with Spring Boot. It supports instructor registration/login, admin management pages (courses, branches, subjects), instructor-facing pages (dashboard, add/edit materials), learner-facing views for browsing subjects, units and topics, and basic REST APIs for instructor registration and subject retrieval.

This repository contains both server-side rendered web pages (Thymeleaf templates under src/main/resources/templates) and a small JSON API under /api.

## Run locally

Requirements:
- Java 17+ (project uses modern Spring Boot)
- Maven

From repository root:

1. Build:

   mvn clean package

2. Run:

   mvn spring-boot:run

Or run the generated jar in target/:

   java -jar target/LMS-0.0.1-SNAPSHOT.jar

The app starts on port 8080 by default. Configure via `src/main/resources/application.properties`.

## Authentication and Sessions

- The web UI uses simple session attributes to track logins:
  - Instructor login sets `instructorId` session attribute.
  - Admin login sets `isAdmin` and `adminId` session attributes.
- There is no central Spring Security configuration in place for session protection in the controllers shown; the app uses manual session checks in controllers.
- The REST API endpoints under `/api` (e.g., `/api/instructor/register`) are public in the controller shown.

## Endpoints

Below is a consolidated list of endpoints found in the controllers with their HTTP method, path, expected parameters, and a short description.

- Admin (web pages) - base path: /admin
  - GET /admin
    - Description: Admin dashboard. Requires admin session (`isAdmin`).
  - GET /admin/courses
    - Description: Manage courses page. Requires admin session.
  - GET /admin/branches
    - Description: Manage branches page. Requires admin session.
  - GET /admin/subjects
    - Description: Manage subjects page. Requires admin session.
  - POST /admin/course
    - Parameters: CourseDto (form)
    - Description: Create a course.
  - POST /admin/branch
    - Parameters: BranchDto (form)
    - Description: Create a branch.
  - POST /admin/subject
    - Parameters: SubjectDto (form)
    - Description: Create a subject and assign to a branch.
  - POST /admin/course/delete
    - Parameters: id (form param)
    - Description: Delete course.
  - POST /admin/branch/delete
    - Parameters: id (form param)
    - Description: Delete branch.
  - POST /admin/subject/delete
    - Parameters: code (form param)
    - Description: Delete subject.
  - GET /admin/login
    - Description: Admin login form.
  - POST /admin/login
    - Parameters: email, password (form)
    - Description: Authenticate admin (checks Instructor role == ROLE_ADMIN), sets session attributes.
  - GET /admin/logout
    - Description: Clears admin session and redirects to home.

- Instructor (web pages) - base path: /instructor
  - GET /instructor/register
    - Description: Show instructor registration form.
  - POST /instructor/register
    - Parameters: InstructorRegisterDto (form)
    - Description: Register instructor (server-side validation present).
  - GET /instructor/login
    - Description: Show instructor login form.
  - POST /instructor/login
    - Parameters: email, password (form)
    - Description: Authenticate instructor, stores `instructorId` in session.
  - GET /instructor/dashboard
    - Description: Instructor dashboard. Requires session.
  - POST /instructor/dashboard/save
    - Parameters: LearningMaterial form, subjectCode (form)
    - Description: Save learning material from dashboard.
  - GET /instructor/material/add
    - Description: Show add material form (requires session).
  - GET /instructor/materials
    - Description: List materials uploaded by instructor.
  - GET /instructor/material/{id}/edit
    - Path params: id (material id)
    - Description: Show edit form for a material (must be owner).
  - POST /instructor/material/{id}/update
    - Path params: id
    - Parameters: subjectId (form), file (multipart, optional), LearningMaterial form data
    - Description: Update an existing material. Files are not stored in controllers but original filename is recorded.
  - POST /instructor/material/save
    - Parameters: LearningMaterial form, subjectId (form), file (multipart optional)
    - Description: Save new material from add form.
  - GET /instructor/logout
    - Description: Invalidates session and redirects to home.
  - GET /instructor/overview
    - Description: Instructor overview (aggregated summaries). Requires session.
  - GET /instructor/courses
    - Query param: showAdd (optional int)
    - Description: Shows the same overview but with courses nav active; can show add modal.
  - POST /instructor/subjects/add
    - Parameters: subjectCode, subjectName (form)
    - Description: Adds a subject and associates it with the current instructor.

- Learning Materials (web pages) - base path: /learning-materials
  - GET /learning-materials
    - Description: List all learning materials (home view).
  - GET /learning-materials/add
    - Description: Show add material form (requires instructors & subjects lists).
  - POST /learning-materials/save
    - Parameters: LearningMaterial form, instructorId, subjectCode (form)
    - Description: Save a material with explicit instructor and subject selected in form.

- Learner (web pages) - base path: /learner
  - GET /learner or /learner/
    - Description: Learner home - lists subjects, no selected subject.
  - GET /learner/subject/{subjectCode}
    - Path params: subjectCode
    - Description: Show a subject with units computed from materials' unitNo.
  - GET /learner/subject/{subjectCode}/unit/{unitNo}
    - Path params: subjectCode, unitNo
    - Description: Show topics for the given unit of a subject.
  - GET /learner/topic/{id}
    - Path params: id (material id)
    - Description: Show a single topic (material) content; builds sidebar state.

- Instructor API (JSON)
  - GET /api/instructor/subjects
    - Response: JSON list of Subject entities (used for dropdowns in the UI).
  - POST /api/instructor/register
    - Body: InstructorRegisterDto (JSON)
    - Response: Registered Instructor entity (201/200). Uses InstructorService.register().

## Notes, caveats, and recommended improvements

- Passwords are hashed using a PasswordEncoder where controllers compare raw with encoded via `passwordEncoder.matches(...)`.
- File uploads are accepted but not persisted to disk in controllers - only the original filename is recorded. A dedicated storage service is recommended.
- Session handling is manual; consider integrating Spring Security for robust authentication and authorization.
- The REST API surface is minimal; if a public API is desired, design versioned endpoints (e.g., /api/v1) and add DTOs for stable contracts.
- Input validation is basic; DTOs use @Valid in some controllers but not consistently across all endpoints.

## Quick curl examples

- Get subjects (JSON):

  curl http://localhost:8080/api/instructor/subjects

- Register instructor (JSON):

  curl -X POST -H "Content-Type: application/json" -d '{"name":"Alice","email":"a@example.com","password":"secret","subjects":[]}' http://localhost:8080/api/instructor/register

- Login instructor (web form):

  Use a browser to POST a form to /instructor/login with fields `email` and `password`.

## Where to look in code

- Controllers: src/main/java/com/app/controller
- Services: src/main/java/com/app/service
- Repositories: src/main/java/com/app/repository
- Templates: src/main/resources/templates

---

If you'd like, I can also:
- Generate a full API reference in Markdown with example request/response bodies for each endpoint.
- Add a small Postman collection or OpenAPI (swagger) spec generated from the controllers.
