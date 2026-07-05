# Deferred Work

## Deferred from: code review of 1-1-project-scaffolding-and-default-categories (2026-07-05)

- `GlobalExceptionHandler` flattens all failures to 500, masking real HTTP semantics (e.g. a future 405/400 would still come back as 500) — deferred because no validated endpoints exist yet in this story; relevant once Epic 2/Story 1.2 add real validation failures.
- Potential case-folding mismatch between JPA's `findByNameIgnoreCase` and the SQL `lower(name)` unique index for non-ASCII category names — deferred as theoretical only; current seed data is ASCII-only and the product is English-only (UX-DR18).
- Unmapped/404 paths under `/api/**` fall through to Spring's default error shape instead of this app's `{error:{code,message}}` — deferred as arguably outside AC4's literal "given any controller throws" scope; revisit if a future story wants uniform error shaping for all responses including 404s.
- `Category` entity's constructor doesn't null-guard `name`/`icon`/`kind` — deferred as unreachable today (only called with hardcoded literals in `CategoryBootstrapRunner`); becomes relevant once Epic 2's `CategoryService.create()` accepts real user input.

## Deferred from: code review of 1-2-manual-transaction-entry (2026-07-05)

- Only the first Bean Validation field error is surfaced to the client when multiple fields are invalid at once (`GlobalExceptionHandler`) — deferred, low-value given client-side gating already prevents most real occurrences (Save stays disabled until all fields pass client validation).
- A date picked before the `Asia/Kolkata` midnight boundary can go stale if the session stays open across it (`quick-add-form.tsx`) — deferred, extremely narrow edge case for a personal single-user app.
- No defensive check if a last-used `categoryId` is absent from the freshly-fetched category list (`quick-add-form.tsx`) — deferred, currently unreachable (Epic 2's category deletion doesn't exist yet).
- No retry affordance on the initial category-load error (`quick-add-form.tsx`) — deferred, minor UX polish, user can reload the page today.
