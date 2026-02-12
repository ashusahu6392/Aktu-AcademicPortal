# AKTU Academic Portal (LMS) — Refactor Notes

This repository contains a Spring Boot LMS application. This README documents a refactor that renamed several domain concepts and updated controllers, services, repositories and UI templates accordingly.

Summary of renames

- Entities
  - `Note` → `LearningMaterial` (entity class: `com.app.entity.LearningMaterial`)
  - `Teacher` → `Instructor` (entity class: `com.app.entity.Instructor`)
  - `Student` → `Learner` (UI/controller name: `LearnerController`, templates under `templates/learner`)

- Repositories / Services / Controllers / DTOs
  - `NoteRepository` → `LearningMaterialRepository`
  - `NoteService` → `LearningMaterialService`
  - `NoteServiceImpl` → `LearningMaterialServiceImpl`
  - `NoteController` → `LearningMaterialController` (URL base changed)

  - `TeacherRepository` → `InstructorRepository`
  - `TeacherService` → `InstructorService`
  - `TeacherServiceImpl` → `InstructorServiceImpl`
  - `TeacherController` → `InstructorController` (API)
  - `TeacherWebController` → `InstructorWebController` (web views)
  - `TeacherRegisterDto` → `InstructorRegisterDto`

  - `StudentController` → `LearnerController` (web views)

- Template and URL changes (major ones)
  - `/notes` → `/learning-materials`
    - Template: `templates/learning-materials.html` (previously `notes.html`)
    - Add form: `templates/add-learning-material.html` (previously `add-note.html`)
  - `/teacher/*` → `/instructor/*` (register/login/dashboard endpoints updated)
    - Templates: `templates/instructor-register.html`, `templates/instructor-login.html`, `templates/instructor-dashboard.html`
  - `/student/*` → `/learner/*` (UI pages moved under `templates/learner`)

What I changed (high level)

- Added/updated JPA entities: `Instructor` and `LearningMaterial` with `@Table` names (defaults set to `instructor` and `learning_material`).
- Created matching Spring Data repositories, services and service implementations.
- Updated controllers (REST and web) and their request mappings.
- Updated and created templates (Thymeleaf): instructor, learner and learning-material pages.
- Replaced legacy files with placeholders/redirects to avoid duplicate public type compilation errors during refactor. These placeholders can be removed from VCS when you are ready.

Build & run (Windows / cmd.exe)

Open a Command Prompt at the project root (where `mvnw.cmd` is) and run:

```batch
cd C:\Users\Office\Desktop\GitHub\Aktu-AcademicPortal\LMS
.\mvnw.cmd -DskipTests package
```

To run the application locally:

```batch
cd C:\Users\Office\Desktop\GitHub\Aktu-AcademicPortal\LMS
.\mvnw.cmd spring-boot:run
```

Quick smoke test (after app starts)

- Instructor register (web): http://localhost:8080/instructor/register
- Instructor login (web): http://localhost:8080/instructor/login
- Instructor dashboard (web): http://localhost:8080/instructor/dashboard
- List learning materials: http://localhost:8080/learning-materials
- Learner (student) home: http://localhost:8080/learner

Notes on database/table names

- New entities use these table names by default (via `@Table`):
  - `Instructor` → `instructor`
  - `LearningMaterial` → `learning_material`

If your existing database already used `teacher` and `note` tables and you want the new entities to reuse the older table names, change the `@Table(name = "teacher")` or `@Table(name = "note")` accordingly in the `Instructor` and `LearningMaterial` classes.

Cleaning up legacy files

- During the refactor I left a small placeholder comment in some old files (for example old `Note.java` and `Teacher.java`) to avoid accidental duplicate type errors during the multi-step migration. If you are ready to remove them from your repository, run:

```batch
cd C:\Users\Office\Desktop\GitHub\Aktu-AcademicPortal\LMS
git rm src\main\java\com\app\entity\Teacher.java src\main\java\com\app\entity\Note.java
# also remove any old controller/service files that remain as placeholders if desired
git commit -m "Remove legacy placeholder files after refactor"
```

Search tips

- To find any remaining old name usage you can run a workspace-wide search for `Note`, `Teacher`, or `/teacher/` and `/student/` in templates; I updated the majority of occurrences.

Known considerations & next steps

- I intentionally kept public HTTP paths for web flows updated to `/instructor` and `/learner`. If you have external integrations that rely on the old paths (`/teacher` or `/student`) consider adding temporary redirects or keep the old endpoints redirecting to the new ones (I added meta redirect pages for old templates to ease transition).
- If you want me to fully remove legacy files from the repository I can do that next (I left placeholders instead of hard deletes to be conservative).
- If you prefer different table names (reuse `teacher` / `note`) I can change the `@Table` annotations to exactly map to your DB.

Contact points in the code (quick map)

- Entity classes: `src/main/java/com/app/entity/Instructor.java`, `src/main/java/com/app/entity/LearningMaterial.java`
- Repositories: `src/main/java/com/app/repository/InstructorRepository.java`, `src/main/java/com/app/repository/LearningMaterialRepository.java`
- Services: `src/main/java/com/app/service/InstructorService.java`, `src/main/java/com/app/service/LearningMaterialService.java`
- Controllers (web/API):
  - `src/main/java/com/app/controller/InstructorWebController.java` (web)
  - `src/main/java/com/app/controller/InstructorController.java` (API)
  - `src/main/java/com/app/controller/LearningMaterialController.java`
  - `src/main/java/com/app/controller/LearnerController.java`
- Templates: `src/main/resources/templates/instructor-*.html`, `src/main/resources/templates/learning-materials.html`, `src/main/resources/templates/add-learning-material.html`, `src/main/resources/templates/learner/*`

If you'd like I can now:

- Remove the legacy placeholder files from the repo (I can stage and commit deletions), or
- Update `@Table` names to reuse old DB table names, or
- Start the application and perform a short smoke test (automated curl requests) and share the results.

Tell me which of those you'd like me to do next and I will proceed.
