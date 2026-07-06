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

## Deferred from: code review of 1-3-frequent-expense-chip-logging (2026-07-06)

- AC5's visual distinctness (pill shape/color vs. category buttons) was never verified with a real rendered browser view (`apps/web/components/frequent-expense-shelf.tsx`) — deferred, no browser-automation tool was available this session; only logic (JSDOM/Vitest) and a live `curl`-based backend check were done. Revisit with a spot-check once browser tooling is available.
- Unbounded full-table aggregation on every Quick Add load, no index/window discussion (`TransactionRepository.findFrequentExpenseCombinations`) — deferred, matches AD-1's explicit "no caching, always computed live" mandate and is negligible at this app's single-user personal scale; revisit only if real usage ever shows a performance problem.
- "Habitual" has no recency window or decay — a combination that repeated years ago stays eligible forever (`TransactionRepository.findFrequentExpenseCombinations`) — deferred, the PRD/architecture explicitly left the ranking algorithm's time window open; this story made a documented "all-time, no decay" choice within that discretion. Revisit only if real usage shows stale chips.
