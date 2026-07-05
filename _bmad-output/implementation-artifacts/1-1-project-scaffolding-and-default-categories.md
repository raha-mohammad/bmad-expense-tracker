---
baseline_commit: b468686bf2b3290e40c038d4b111b594037b621f
---

# Story 1.1: Project Scaffolding & Default Categories

Status: done

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As a developer,
I want the Next.js and Spring Boot apps scaffolded with CORS, DTO conventions, a centralized exception handler, and CI wired up, with the 5 default categories and the Uncategorized system category seeded in PostgreSQL,
so that every subsequent story has a working, testable full-stack foundation instead of rebuilding plumbing story by story.

## Acceptance Criteria

1. **Given** a fresh clone of the repo, **when** a developer runs the local dev setup (`docker-compose up` for Postgres, `next dev`, Spring Boot dev mode), **then** `apps/web` loads a placeholder page and `apps/api` responds `200` on `/actuator/health`.
2. **Given** `apps/api` is running, **when** `apps/web` calls any `/api/*` endpoint, **then** the request succeeds without a CORS error (AD-2).
3. **Given** the database is freshly migrated, **when** the `categories` table is queried, **then** it contains exactly 5 `DEFAULT` categories (Food, Transport, Shopping, Bills, Entertainment) and exactly 1 `SYSTEM` category (Uncategorized), seeded idempotently — re-running startup never duplicates rows (AD-5).
4. **Given** any controller throws an exception, **when** it propagates, **then** a single `@ControllerAdvice` returns `{error:{code,message}}` (AD-8), never a raw stack trace.
5. **Given** `apps/web` loads, **when** no login screen exists, **then** it renders directly to a screen with no auth prompt of any kind (NFR-3).
6. **Given** a push or PR, **when** GitHub Actions CI runs, **then** it executes backend + frontend tests/lint and blocks merge on failure.
7. **And** `apps/web` deploys to Vercel and `apps/api` + Postgres deploy to Railway via native git integration on merge to `main`, with Postgres on a persistent (non-ephemeral) volume, not a free-tier setup that expires (NFR-7).

## Tasks / Subtasks

- [x] Task 1: Scaffold the monorepo skeleton (AC: #1)
  - [x] Create `apps/web/` and `apps/api/` at repo root per the Source Tree below — no other top-level app folders.
  - [x] Add root `docker-compose.yml` running PostgreSQL 18.4 for local dev (named volume, not ephemeral tmpfs).
  - [x] Add `.github/workflows/` CI workflow file(s).
- [x] Task 2: Initialize `apps/web` (Next.js 16.2 LTS) (AC: #1, #5)
  - [x] Scaffold with Tailwind CSS + shadcn/ui per the pinned Stack (no other UI library).
  - [x] Add a single placeholder route under `app/` that renders directly with no login/auth gate of any kind.
  - [x] Avoid deep folder nesting under `app/`/`components/`/`lib/` before a later story needs it (PRD addendum wiring-pitfall list).
- [x] Task 3: Initialize `apps/api` (Spring Boot 4.1.0 / Spring Framework 7, Java 25 LTS) (AC: #1)
  - [x] Create the layered package skeleton: `controller/`, `service/`, `repository/`, `entity/`, `dto/`, `config/` (empty except what this story needs — AD-4).
  - [x] Enable Spring Boot Actuator; confirm `/actuator/health` returns `200`.
  - [x] Configure structured JSON logs to stdout (no external log aggregator/APM for MVP — Consistency Conventions "Observability" row).
- [x] Task 4: Configure CORS (AC: #2)
  - [x] Add CORS config in `apps/api/.../config/` allowing `apps/web`'s local/dev origin before any `/api/*` endpoint exists to consume it (AD-2, PRD addendum wiring pitfall — build in from day one, don't bolt on later).
  - [x] Make allowed origins configurable (e.g. via a property/env var) so the production Vercel origin can be added in Task 8 without a second CORS change later — don't hardcode `localhost` only.
- [x] Task 5: `categories` table + seed data (AC: #3)
  - [x] Create `Category` JPA entity: `id BIGINT` (auto-increment), `name` (unique case-insensitive among non-deleted rows), `icon`, `kind` enum (`DEFAULT` | `CUSTOM` | `SYSTEM`) — per AD-5's three-kind model. Only the `categories` table is created in this story; do **not** create `transactions` or `budgets` tables yet (they belong to Stories 1.2 and 1.5 respectively — incremental DB creation, not front-loaded).
  - [x] Add a partial unique index on `categories` (`WHERE kind = 'SYSTEM'`) so at most one SYSTEM row can ever exist (AD-5). **This index cannot be expressed via JPA/Hibernate annotations** — create it with native SQL (a migration file, or a startup SQL script) regardless of whether `ddl-auto` or a migration tool is chosen for the rest of the schema.
  - [x] Enforce case-insensitive name uniqueness at the DB level with a mechanism that's actually case-insensitive in Postgres — e.g. a functional unique index on `lower(name)`, or the `citext` extension. A plain `@Column(unique = true)` is case-sensitive and does not satisfy AD-5.
  - [x] Add an idempotent startup seeder (e.g. `CommandLineRunner` or migration) that inserts the 5 `DEFAULT` categories (Food, Transport, Shopping, Bills, Entertainment) and the 1 `SYSTEM` category (Uncategorized) only if they don't already exist — re-running startup must never duplicate rows.
  - [x] Create a minimal `CategoryRepository` (Spring Data JPA), a minimal read-only `CategoryService`, and a `CategoryController` exposing **`GET /api/categories`** returning DTOs of all seeded rows (AD-3, AD-4). This is a deliberate scope addition beyond what `epics.md` states for this story: it's the first real `/api/*` endpoint, needed to make AC2 (CORS) and AC4 (`@ControllerAdvice`) actually testable end-to-end, and Story 1.2 needs this same read endpoint to populate its category selector anyway. **Full CRUD (create/rename/delete) still belongs to Epic 2** — this story adds only the read path.
- [x] Task 6: Centralized exception handling (AC: #4)
  - [x] Add a single `@ControllerAdvice` in `config/` returning `{ "error": { "code": "...", "message": "..." } }` for uncaught exceptions (AD-8). No controller in this story catches/reshapes its own exceptions.
- [x] Task 7: CI pipeline (AC: #6) — **backend "lint" is `mvn test` only, no separate linter configured (none was in this story's scope)**; frontend lint is a real `npm run lint` step
  - [x] GitHub Actions workflow runs backend tests/lint (Maven/Gradle — match whatever build tool `apps/api` scaffolding produces) and frontend tests/lint (`apps/web`) on every push/PR; a red run blocks merge.
- [~] Task 8: Deploy wiring (AC: #7) — **documented, not provisioned** (see note below)
  - [ ] Configure Vercel to build `apps/web` from its subfolder via native git integration on merge to `main`. **Not done by this dev agent** — requires the project owner's own Vercel account/dashboard access, which this agent doesn't have. Documented in `docs/deployment.md`.
  - [ ] Configure Railway to build `apps/api` + provision PostgreSQL from its subfolder via native git integration on merge to `main`; confirm the Postgres instance uses a persistent volume (not a free tier that expires/resets — NFR-7 rules out Render's free Postgres for exactly this reason per Solution Design). **Not done by this dev agent** — same reason. Documented in `docs/deployment.md`.
  - [x] Railway injects `DATABASE_URL` as a URI (`postgres://user:pass@host:port/db`), **not** a Spring-compatible JDBC URL — either convert it at startup or set `SPRING_DATASOURCE_URL`/`SPRING_DATASOURCE_USERNAME`/`SPRING_DATASOURCE_PASSWORD` explicitly in Railway's env vars with a `jdbc:postgresql://` prefix. Confirm this before relying on Railway's default `DATABASE_URL` alone. — Documented as an explicit step in `docs/deployment.md`; the app itself already reads these three standard Spring properties, so no code change is needed once they're set.
- [x] Task 9: Tests for this story
  - [x] Backend: `@WebMvcTest`-style smoke test asserting `/actuator/health` → `200`.
  - [x] Backend: unit test asserting the categories seeder produces exactly 5 `DEFAULT` + 1 `SYSTEM` row on first run, and produces no duplicates when run again against an already-seeded DB.
  - [x] Backend: `@WebMvcTest` for `GET /api/categories` asserting `200`, a DTO payload (never the `@Entity`), and a CORS-allow header present when called with the configured dev origin (AC2).
  - [x] Backend: `@WebMvcTest` for `GET /api/categories` with `CategoryService` mocked (`@MockBean`) to throw, asserting the response matches `@ControllerAdvice`'s `{error:{code,message}}` shape — this is how AC4 is actually exercised, since this story has no other real controller to trigger it through.
  - [x] Frontend: no component tests required yet (Quick Add/Budget Status component tests start in Stories 1.2/1.6) — confirm the placeholder page renders and the build passes lint.

### Review Findings

Three parallel adversarial layers (Blind Hunter — diff only, no project context; Edge Case Hunter — diff + project read access; Acceptance Auditor — diff + spec + architecture docs) reviewed this story's diff. Several raw findings were fact-checked against ground truth already established during implementation (live verification runs, actual jar inspection) and refuted or dismissed as noise; see Completion Notes for the full accounting. Surviving findings:

- [x] [Review][Patch] `CategoryBootstrapRunner` bypasses the Service layer (AD-4) — **Resolved by user decision: route through `CategoryService` for strict AD-4 compliance**, not documented as an exception. Add `findByNameIgnoreCase`, `existsByKind`, and `save` pass-through methods to `CategoryService` and have `CategoryBootstrapRunner` call those instead of `CategoryRepository` directly [`apps/api/src/main/java/com/bmad/expensetracker/service/CategoryBootstrapRunner.java`, `apps/api/src/main/java/com/bmad/expensetracker/service/CategoryService.java`].
- [x] [Review][Patch] `GlobalExceptionHandler` logs nothing on unhandled exceptions [`apps/api/src/main/java/com/bmad/expensetracker/config/GlobalExceptionHandler.java:12-17`] — every 500 is currently invisible in logs/Railway's log viewer; add a `logger.error(...)` call before building the response.
- [x] [Review][Patch] `CategoryBootstrapRunner` has an unguarded concurrent-seed race [`apps/api/src/main/java/com/bmad/expensetracker/service/CategoryBootstrapRunner.java:35-59`] — the check-then-act pattern (`findByNameIgnoreCase(...).isEmpty()` / `existsByKind(...)` then `save()`) can double-insert if two instances start concurrently (rolling deploy, or two devs against a shared DB), and the resulting unique-index violation is uncaught, which would crash startup instead of being absorbed.
- [x] [Review][Patch] `CorsConfig` doesn't trim or filter allowed origins after splitting [`apps/api/src/main/java/com/bmad/expensetracker/config/CorsConfig.java:14`] — a naturally-typed value like `"https://a.com, https://b.com"` (space after comma, exactly what `docs/deployment.md` step 4 suggests for multi-origin config) leaves a leading space on the second origin, which then silently never matches a real `Origin` header.
- [x] [Review][Patch] GitHub Actions Postgres healthcheck could exhaust retries on a cold/slow runner [`.github/workflows/ci.yml:27-31`] — 10 retries × 5s (50s total) is usually enough for Postgres cold-start but has no safety margin; bump retries or add `--health-start-period`.
- [x] [Review][Patch] Task 7's checkbox claims full completion but backend "lint" isn't actually a separate step [`_bmad-output/implementation-artifacts/1-1-project-scaffolding-and-default-categories.md` Task 7] — CI's backend job only runs `mvn test`, no linter. This is already disclosed in prose in Completion Notes but the checkbox itself doesn't reflect it, unlike Task 8's honest `[~]` treatment for its own gap.
- [x] [Review][Patch] Dev Notes' AD-4 bullet is stale and self-contradicts Task 5 — it states "This story's `CategoryRepository` has no Service/Controller yet — that's expected, not a gap," but Task 5 (and the actual diff) added both `CategoryService` and `CategoryController`. Leftover text from before the validation pass expanded Task 5's scope.
- [x] [Review][Patch] No documented prerequisite that `docker-compose up` must be running before `mvn test` — `CategoryBootstrapRunnerTest`/`ExpenseTrackerApiApplicationTests` boot the full Spring context against `application.properties`'s hardcoded `localhost:5432` datasource with no Testcontainers; add a one-line note to `docs/deployment.md`'s Local dev section.
- [x] [Review][Defer] `GlobalExceptionHandler` flattens all failures to 500, masking real HTTP semantics — deferred, pre-existing scope boundary (no validated endpoints exist yet in this story; relevant once Epic 2/Story 1.2 add real validation failures)
- [x] [Review][Defer] Potential case-folding mismatch between JPA's `findByNameIgnoreCase` and the SQL `lower(name)` index for non-ASCII names — deferred, theoretical only; current seed data is ASCII-only and the product is English-only (UX-DR18)
- [x] [Review][Defer] Unmapped/404 paths under `/api/**` fall through to Spring's default error shape, not this app's `{error:{...}}` — deferred, arguably outside AC4's literal "given any controller throws" scope; revisit if a future story wants uniform shaping for all responses including 404s
- [x] [Review][Defer] `Category` entity's constructor doesn't null-guard `name`/`icon`/`kind` — deferred, unreachable today (only called with hardcoded literals in the seeder); becomes relevant once Epic 2's `CategoryService.create()` accepts real user input

## Dev Notes

- **This is the first story in the project — there is no existing code to preserve.** Everything under `apps/web/` and `apps/api/` is new; there is nothing to read/diff against.
- **Architecture is final, not a proposal** — treat every `AD-n` rule below as a hard constraint, not a suggestion: [Source: `ARCHITECTURE-SPINE.md`]
  - **AD-2** — `apps/web` talks to `apps/api` only over REST/JSON `/api/*`; no direct DB access from the frontend, ever.
  - **AD-3** — Controllers accept/return DTOs only; never serialize a `@Entity` class directly. (No controllers exist yet in this story, but any scaffolded endpoint stub must still follow this.)
  - **AD-4** — Controller → Service → Repository layering, even for trivial endpoints; only a Service may call a Repository, with no exception for startup/bootstrap code (`CategoryBootstrapRunner` calls `CategoryService`, never `CategoryRepository`/`JdbcTemplate` directly — confirmed during code review). Full CRUD (create/rename/delete) on `CategoryService`/`CategoryController` still belongs to Epic 2; this story's version is read-only.
  - **AD-5** — `categories.kind` is `DEFAULT` | `CUSTOM` | `SYSTEM`; exactly one `SYSTEM` row, enforced by a partial unique index, seeded idempotently at startup, never created on demand. This is the one piece of business logic this story actually implements.
  - **AD-8** — One `@ControllerAdvice`, one error shape, configured before the first real endpoint ships.
  - **AD-9** — Not yet exercised in this story (no dates/periods yet), but note for later: all "today" logic must be server-side, fixed to `Asia/Kolkata` — don't let a later story's controller compute dates client-side.
- **Stack versions are pinned, not to be second-guessed or "updated to latest" during scaffolding** [Source: `ARCHITECTURE-SPINE.md#Stack`, `SOLUTION-DESIGN.md#Stack, pinned`]: Next.js 16.2 LTS (16.2.10), Tailwind CSS + shadcn/ui (latest), Java 25 LTS, Spring Boot 4.1.0 (Spring Framework 7), PostgreSQL 18.4. These were verified during the architecture phase (see that folder's `.memlog.md` for the research trail) — don't independently "upgrade" any of them while scaffolding.
- **Schema management approach is an open implementation detail, not yet firmly decided** — the PRD addendum notes Hibernate `ddl-auto` is *likely* for dev, but explicitly flags this as unconfirmed: "confirm entity-driven schema generation doesn't accidentally reintroduce a stored-total column." [Source: `addendum.md#Derived-totals guardrail`] Pick an approach (Hibernate `ddl-auto=update` for local dev, or a migration tool) and apply it consistently — just don't let auto-schema-generation silently add a totals/remaining/spent column anywhere; none should ever exist (AD-1), though no such column is needed by this story's `categories` table anyway.
- **No auth exists or is being added** — NFR-3 confirms single implicit user, no accounts in v1. The placeholder page must not include any login/signup UI, redirect, or gate.
- **CORS and the exception handler are explicitly "build in from day one" items**, not to be deferred to when the first real feature breaks without them [Source: `addendum.md#Stack wiring pitfalls`].
- **Hosting decision is final:** Vercel (frontend, free tier) + Railway (backend + Postgres, ~$10–30/mo realistic cost, not the advertised $5 floor). Render was explicitly rejected because its free Postgres expires after 30 days, which would violate NFR-7. [Source: `SOLUTION-DESIGN.md#Split hosting, not one host`]
- **Why this story adds a `GET /api/categories` endpoint even though `epics.md` doesn't name one:** AC2 (CORS) and AC4 (`@ControllerAdvice`) both require a real `/api/*` endpoint to prove against, and this story otherwise creates zero controllers. A minimal read-only endpoint is the smallest addition that makes both ACs genuinely testable, and it's not wasted scope — Story 1.2's category selector needs this same read path. Do not add create/rename/delete here; that's Epic 2.

### Project Structure Notes

- Source tree is fixed by the architecture spine — follow it exactly, don't invent alternate top-level folders: [Source: `ARCHITECTURE-SPINE.md#Source tree`]

```text
bmad-expense-tracker/
  apps/
    web/                    # Next.js 16.2 app — deploys to Vercel
      app/                  # App Router pages
      components/           # shadcn/ui-based components
      lib/                  # API client (REST/JSON only — no DB access)
    api/                    # Spring Boot 4.1 app — deploys to Railway
      src/main/java/.../controller/
      src/main/java/.../service/
      src/main/java/.../repository/
      src/main/java/.../entity/
      src/main/java/.../dto/
      src/main/java/.../config/       # CORS, @ControllerAdvice (AD-8)
  .github/workflows/        # CI gate
  docker-compose.yml        # local Postgres 18.4 for dev
```

- No variance from this structure is expected in this story; if a deviation becomes necessary (e.g. Java package naming), keep it minimal and note it in Completion Notes for the next story to build on consistently.

### References

- [Source: `_bmad-output/planning-artifacts/epics.md#Story 1.1: Project Scaffolding & Default Categories`] — original ACs
- [Source: `_bmad-output/planning-artifacts/architecture/architecture-bmad-expense-tracker-2026-07-03/ARCHITECTURE-SPINE.md`] — AD-1 through AD-10, Stack, Source Tree, Consistency Conventions
- [Source: `_bmad-output/planning-artifacts/architecture/architecture-bmad-expense-tracker-2026-07-03/SOLUTION-DESIGN.md`] — hosting rationale, stack pinning rationale
- [Source: `_bmad-output/planning-artifacts/prds/prd-bmad-expense-tracker-2026-07-03/addendum.md`] — stack wiring pitfalls, schema-management open item, auth deferred-branch note
- [Source: `_bmad-output/project-context.md#Architecture Completed`, `#Epics & Stories Completed`] — cross-story rules this story must not contradict

## Dev Agent Record

### Agent Model Used

claude-sonnet-5 (Claude Code)

### Debug Log References

- **postgres:18.4 container crash-looped on first boot** — the official image's data-directory layout changed at major version 18 (pg_ctlcluster-compatible, major-version-specific subdirectories); mounting the named volume at `/var/lib/postgresql/data` (the old convention) now crashes the container on startup. Fixed by mounting at `/var/lib/postgresql` instead in `docker-compose.yml`.
- **Spring Boot app failed to connect to Postgres: `FATAL: invalid value for parameter "TimeZone": "Asia/Calcutta"`** — this JVM's `TimeZone.getDefault()` resolves to the deprecated IANA alias `Asia/Calcutta` (from the host OS), which this PostgreSQL image's tzdata rejects outright at connection time. This is exactly the class of bug AD-9 exists to prevent. Fixed with a static initializer in `ExpenseTrackerApiApplication` pinning `TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"))`, plus a matching Surefire `argLine` in `pom.xml` since `@SpringBootTest` loads the configuration class without ever calling `main()`.
- **Spring Boot 4.1 has real breaking changes vs. older training-data assumptions**, confirmed empirically rather than assumed: dependency artifact IDs are now granular (`spring-boot-starter-webmvc` / `-webmvc-test`, not `-web` / `spring-boot-starter-test`); `@MockBean` is replaced by `@MockitoBean` (`org.springframework.test.context.bean.override.mockito.MockitoBean`); `@WebMvcTest`/`@AutoConfigureMockMvc` moved to `org.springframework.boot.webmvc.test.autoconfigure`. Verified against the actual resolved jars in `~/.m2`, not guessed.
- Spring Initializr's generated `4.1.0.RELEASE` parent version doesn't exist on Maven Central (real coordinate is `4.1.0`, no `.RELEASE` suffix) — corrected in `pom.xml`.
- **Code review fact-checks (dismissed as refuted, not just "disagreed with"):** the Blind Hunter layer (diff-only, no project context) raised three claims directly contradicted by this session's own empirical work: (1) it called the granular Maven test-starter artifact names "suspicious," questioning whether they'd resolve — `mvn test` had already been run successfully with 8/8 passing before this claim was made; (2) it called the `docker-compose.yml` Postgres volume-mount comment's justification "fabricated" — the exact wording was reproduced verbatim from the real `postgres:18.4` container's own crash log; (3) it called `apps/web/AGENTS.md`'s reference to `node_modules/next/dist/docs/` a "wild goose chase" — that directory was directly inspected and contains real Next.js 16.2.10 documentation. Two more (missing `package-lock.json`/Maven wrapper files) were false positives caused by excluding those generated files from the reviewed diff to cut noise — they exist in the working tree. Recorded here so a future reviewer doesn't re-raise the same refuted claims.

### Completion Notes List

- All 9 implementation tasks (Tasks 1–7, 9, and the DATABASE_URL-conversion subtask of Task 8) are complete and verified live, not just written: `docker compose up` → healthy Postgres; `mvn spring-boot:run` → `/actuator/health` returns `200`; `GET /api/categories` returns exactly 5 `DEFAULT` + 1 `SYSTEM` DTOs; restarting the app a second time confirmed zero duplicate rows (idempotent seeding); CORS preflight succeeds for `http://localhost:3000` and is rejected (403) for an arbitrary origin; `npm run lint` and `npm run build` both pass; `mvn test` passes 8/8.
- **AC7 / Task 8 is only partially done, by design and by necessity**: actual Vercel/Railway account provisioning requires the project owner's own cloud accounts, which this agent has no access to and should not attempt. `docs/deployment.md` documents the exact manual steps (Root Directory settings, the three `SPRING_DATASOURCE_*` env vars replacing Railway's raw `DATABASE_URL`, the `APP_CORS_ALLOWED_ORIGINS` env var for the prod Vercel origin). The user confirmed moving this story to `review` with this gap flagged rather than blocking on it.
- Category icon values (`"food"`, `"transport"`, `"help-circle"`, etc.) seeded in `CategoryBootstrapRunner` are placeholders — the real icon set is an explicitly deferred architecture decision, to be resolved when `apps/web`'s Categories screen is built in Epic 2. Don't treat these strings as final.
- Schema-management approach decided: Hibernate `spring.jpa.hibernate.ddl-auto=update` for column/table creation, with the two AD-5 indexes (SYSTEM singleton, case-insensitive name uniqueness) applied as native SQL via `JdbcTemplate` in `CategoryBootstrapRunner` after context startup, since partial/functional indexes can't be expressed through JPA annotations. No migration tool (Flyway/Liquibase) was introduced — flagging this per the story's own Dev Notes so the next story that touches schema (1.2's `transactions` table) makes a deliberate choice to continue this approach or introduce a migration tool, rather than silently drifting.
- Backend "lint" is satisfied only by compilation + `mvn test`, not a dedicated linter/formatter (no Checkstyle/Spotless/etc. was configured, since none was in this story's explicit scope) — CI's backend job runs `mvn test` only. If a Java formatter is wanted, that's a deliberate addition for a future story.
- A Mockito/JDK dynamic-agent-loading warning appears during `mvn test` on this JDK 26 host ("Mockito is currently self-attaching..."); this is a forward-looking JDK deprecation notice, not a test failure — all 8 tests pass. Worth revisiting if a future JDK actually disallows dynamic agent loading.

### File List

**New files:**
- `docker-compose.yml`
- `docs/deployment.md`
- `.github/workflows/ci.yml`
- `apps/web/` — full Next.js 16.2.10 scaffold (`app/layout.tsx`, `app/page.tsx`, `app/globals.css`, `components.json`, `components/ui/button.tsx`, `lib/utils.ts`, `package.json`, `package-lock.json`, `tsconfig.json`, `eslint.config.mjs`, `next.config.ts`, `postcss.config.mjs`, `.gitignore`, `AGENTS.md`, `CLAUDE.md`, `README.md`, `app/favicon.ico`)
- `apps/api/` — full Spring Boot 4.1.0 Maven scaffold (`pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`, `.gitattributes`, `.gitignore`)
- `apps/api/src/main/java/com/bmad/expensetracker/ExpenseTrackerApiApplication.java`
- `apps/api/src/main/java/com/bmad/expensetracker/entity/Category.java`
- `apps/api/src/main/java/com/bmad/expensetracker/entity/CategoryKind.java`
- `apps/api/src/main/java/com/bmad/expensetracker/repository/CategoryRepository.java`
- `apps/api/src/main/java/com/bmad/expensetracker/dto/CategoryDto.java`
- `apps/api/src/main/java/com/bmad/expensetracker/dto/ErrorResponse.java`
- `apps/api/src/main/java/com/bmad/expensetracker/service/CategoryService.java`
- `apps/api/src/main/java/com/bmad/expensetracker/service/CategoryBootstrapRunner.java`
- `apps/api/src/main/java/com/bmad/expensetracker/controller/CategoryController.java`
- `apps/api/src/main/java/com/bmad/expensetracker/config/CorsConfig.java`
- `apps/api/src/main/java/com/bmad/expensetracker/config/GlobalExceptionHandler.java`
- `apps/api/src/main/resources/application.properties`
- `apps/api/src/test/java/com/bmad/expensetracker/ExpenseTrackerApiApplicationTests.java`
- `apps/api/src/test/java/com/bmad/expensetracker/controller/CategoryControllerTest.java`
- `apps/api/src/test/java/com/bmad/expensetracker/service/CategoryBootstrapRunnerTest.java`

**Modified files:**
- `_bmad-output/project-context.md` (Sprint Planning / Story 1.1 sections added in prior steps)
- `apps/api/src/main/java/com/bmad/expensetracker/service/CategoryService.java` (code review patch: added `ensureSchemaHardened`/`ensureDefaultCategoryExists`/`ensureSystemCategoryExists`, now owns all `CategoryRepository`/`JdbcTemplate` access)
- `apps/api/src/main/java/com/bmad/expensetracker/service/CategoryBootstrapRunner.java` (code review patch: rewritten to call only `CategoryService`, zero direct DB access, per resolved AD-4 decision)
- `apps/api/src/main/java/com/bmad/expensetracker/config/GlobalExceptionHandler.java` (code review patch: added `log.error(...)` on unhandled exceptions)
- `apps/api/src/main/java/com/bmad/expensetracker/config/CorsConfig.java` (code review patch: trim + filter blank origins after split)
- `.github/workflows/ci.yml` (code review patch: bumped Postgres healthcheck retries, added `--health-start-period`)
- `docs/deployment.md` (code review patch: added `mvn test` Postgres-prerequisite note)

## Change Log

- 2026-07-05: Implemented Story 1.1 in full: monorepo scaffold (`apps/web` Next.js 16.2.10, `apps/api` Spring Boot 4.1.0/Java 25), CORS, centralized `@ControllerAdvice` error shape, `categories` table with idempotent 5-`DEFAULT`+1-`SYSTEM` seeding and AD-5's two native-SQL indexes, `GET /api/categories`, GitHub Actions CI, and `docs/deployment.md`. Fixed two real environment/library bugs discovered during live verification (Postgres 18 volume-mount layout change; JVM default-timezone alias rejected by Postgres). AC7 (actual Vercel/Railway provisioning) intentionally left for the project owner — documented, not performed by this agent. Status set to `review` per user decision.
- 2026-07-05: Code review (Blind Hunter + Edge Case Hunter + Acceptance Auditor) surfaced 1 decision-needed and 7 patch findings, all resolved same-day. User decided `CategoryBootstrapRunner` should route through `CategoryService` for strict AD-4 compliance rather than document an exception — implemented, and `CategoryBootstrapRunner` now has zero direct `CategoryRepository`/`JdbcTemplate` access. Also fixed: missing exception logging, an unguarded concurrent-seed race (now caught via `DataIntegrityViolationException`), an unrimmed CORS origin split, a CI healthcheck retry margin, a stale Dev Notes bullet, an inconsistently-disclosed Task 7 checkbox, and a missing test-prerequisite doc note. 4 findings explicitly deferred (logged to `deferred-work.md`); 17 raw findings dismissed as noise or empirically refuted (see Debug Log References below and the Review Findings intro). Full regression re-run: 8/8 backend tests still pass. Status set to `done`.
