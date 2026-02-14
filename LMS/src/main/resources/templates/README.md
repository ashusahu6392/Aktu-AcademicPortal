Templates folder structure

This project organizes Thymeleaf templates under src/main/resources/templates/.

Current structure

- templates/
  - admin/        -> admin pages (login, dashboard, branches, courses, subjects)
  - teacher/      -> instructor/teacher auth & dashboards
  - student/      -> student views
  - learner/      -> learner views
  - fragments/    -> reusable Thymeleaf fragments (navbar, sidebar)
  - home/         -> general site pages (home, learning materials, add pages, notes)

Naming conventions & best practices

- Template logical names: controllers should return logical view names that reflect their folder, e.g. "home/home" for src/main/resources/templates/home/home.html.
- Keep fragments in `fragments/` and include them using Thymeleaf's th:replace/th:insert for consistency:
  <div th:replace="fragments/navbar :: navbar"></div>
- Keep static assets under src/main/resources/static/ (css, js, images). Use absolute paths in templates with Thymeleaf: th:href='@{/css/style.css}' or th:src='@{/js/script.js}'.
- Prefer clear names: use kebab-case for files (e.g., instructor-login.html) and avoid spaces.
- Keep templates small and componentized: break large pages into fragments to reuse sections like headers, footers, and sidebars.
- Document any redirects/legacy pages: if you remove or move a template, either leave a redirect page or update controllers and remove the old file (we removed top-level duplicates and updated controllers accordingly).

When to seed data

- Do not seed production credentials or sensitive accounts in source code. If admin seeding is required in dev/test environments, create a separate configuration activated by a Spring profile (e.g., @Profile("dev")) and include clear documentation.

How to add new pages

1. Add the template under the appropriate folder (e.g., templates/home/new-page.html).
2. Add any static assets under static/css or static/js and reference them in the template.
3. Update the controller to return the logical view name matching the folder (e.g., return "home/new-page").
4. Run mvn package (or your build pipeline) to verify compilation and resources.

Contact

- If you want a different folder layout (for example splitting teacher and instructor or adding more roles), update this README and I can migrate files and controllers accordingly.
