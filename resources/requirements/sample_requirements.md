# Requirements Document — PROJ-101

## Ticket: PROJ-101 — User Authentication

### UI Requirements
- REQ-UI-001: User can log in with valid email and password
- REQ-UI-002: On successful login, user must be redirected to /dashboard
- REQ-UI-003: Invalid credentials should show error message "Invalid credentials"
- REQ-UI-004: Login button must be visible and enabled on page load

### API Requirements
- REQ-API-001: POST /api/login must return 200 with JWT token for valid credentials
- REQ-API-002: POST /api/login must return 401 for invalid credentials
- REQ-API-003: GET /api/users must return list of users with status 200

### Accessibility Requirements
- REQ-A11Y-001: Login form fields must have ARIA labels (WCAG AA)
- REQ-A11Y-002: Login button must be keyboard accessible via Tab and Enter
- REQ-A11Y-003: Color contrast on login page must meet WCAG AA 4.5:1
