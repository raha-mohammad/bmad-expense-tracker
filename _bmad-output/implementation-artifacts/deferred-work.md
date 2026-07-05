# Deferred Work

## Deferred from: code review of 1-1-project-scaffolding-and-default-categories (2026-07-05)

- `GlobalExceptionHandler` flattens all failures to 500, masking real HTTP semantics (e.g. a future 405/400 would still come back as 500) — deferred because no validated endpoints exist yet in this story; relevant once Epic 2/Story 1.2 add real validation failures.
- Potential case-folding mismatch between JPA's `findByNameIgnoreCase` and the SQL `lower(name)` unique index for non-ASCII category names — deferred as theoretical only; current seed data is ASCII-only and the product is English-only (UX-DR18).
- Unmapped/404 paths under `/api/**` fall through to Spring's default error shape instead of this app's `{error:{code,message}}` — deferred as arguably outside AC4's literal "given any controller throws" scope; revisit if a future story wants uniform error shaping for all responses including 404s.
- `Category` entity's constructor doesn't null-guard `name`/`icon`/`kind` — deferred as unreachable today (only called with hardcoded literals in `CategoryBootstrapRunner`); becomes relevant once Epic 2's `CategoryService.create()` accepts real user input.
