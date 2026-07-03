---
stepsCompleted: [step-01-validate-prerequisites, step-02-design-epics, step-03-create-stories]
inputDocuments:
  - _bmad-output/planning-artifacts/prds/prd-bmad-expense-tracker-2026-07-03/prd.md
  - _bmad-output/planning-artifacts/prds/prd-bmad-expense-tracker-2026-07-03/addendum.md
  - _bmad-output/planning-artifacts/architecture/architecture-bmad-expense-tracker-2026-07-03/ARCHITECTURE-SPINE.md
  - _bmad-output/planning-artifacts/architecture/architecture-bmad-expense-tracker-2026-07-03/SOLUTION-DESIGN.md
  - _bmad-output/planning-artifacts/ux-designs/ux-bmad-expense-tracker-2026-07-02/DESIGN.md
  - _bmad-output/planning-artifacts/ux-designs/ux-bmad-expense-tracker-2026-07-02/EXPERIENCE.md
---

# bmad-expense-tracker - Epic Breakdown

## Overview

This document provides the complete epic and story breakdown for bmad-expense-tracker, decomposing the requirements from the PRD, UX Design, and Architecture into implementable stories.

## Requirements Inventory

### Functional Requirements

FR-1: User can log a Transaction in one tap via a Frequent-Expenses Shelf chip. Tapping a chip creates a Transaction dated today with the chip's preset amount, Category, and description — no further input required, no backdating on this path. Dashboard totals and Budget Status update immediately on save. If save fails, chip values are retained and an inline, retry-capable error is shown.

FR-2: User can log a Transaction manually with amount, Category, description, and date. Save is disabled until amount > 0, a Category is selected, and description is non-empty. Category selection uses a button/icon control (never a dropdown), pre-selecting the last-used Category by default. Date defaults to today; tapping it opens a picker for backdating. Splitting one Transaction across multiple Categories is out of scope.

FR-3: System assigns every Transaction to the Budget Period matching its transaction date, never its entry date. A Transaction backdated into a prior Period is included in that Period's Derived Totals and Budget Status immediately on save.

FR-4: System computes and displays Budget Status — green (<80% of Budget spent), amber (80–100%), red (>100%, "exceeded") — on the Dashboard. Status recalculates on every Dashboard load and immediately after any Transaction or Budget change, no caching. Status copy is factual and non-judgmental at every severity level; color, not language, communicates severity. No push notification, toast, or modal interrupts the user when status changes. Dashboard also displays days remaining in the current Period, computed live. If no Budget has been set for the current Period, the Dashboard shows a neutral prompt instead of a Budget Status card.

FR-5: System computes all category totals, period totals, and budget spent/remaining live from Transactions at read time. No total, remaining, or spent value is ever stored as its own column or field — every read recomputes from Transactions.

FR-6: User can set a single overall Budget amount for the current Period, and edit it later. Editing the Budget amount immediately recalculates Budget Status against already-logged Transactions in the current Period. Zero, negative, or non-numeric entry is blocked inline ("Enter an amount greater than ₹0"). A Budget is optional — Quick Add, category totals, and Search & Filter all function fully before any Budget is set.

FR-7: User can create a custom Category with a name and icon. New Category is immediately available in Quick Add's Category selector and Search & Filter. Empty or duplicate Category name is blocked inline; field retains focus.

FR-8: User can delete a Category they created. Deletion is immediate, no confirmation step. Every Transaction previously assigned to the deleted Category is automatically reassigned to Uncategorized. Deleting any of the 5 default Categories is out of scope — no delete affordance is shown for them at all.

FR-9: User can rename or change the icon of any Category, including the 5 defaults. The same empty/duplicate-name validation as FR-7 applies to renames. Renaming or re-iconing a Category does not affect its historical Transactions or any Derived Total — only the label/icon changes going forward.

FR-10: User can filter Transactions by date range, Category, and optional keyword (matched against description). The filtered list and its running total update together, instantly, on every filter change. No matches shows "No matching transactions" while filters remain visible and editable. Pagination/infinite-scroll is out of scope.

### NonFunctional Requirements

NFR-1 (Accessibility): WCAG 2.1 AA floor — full keyboard operability; visible focus indicator on every focusable element; color is never the sole signal (Budget Status pairs color+icon+text, Category selection pairs border/fill+checkmark+`aria-pressed`); disabled Save button exposes *why* via `aria-describedby`/`aria-label`; `aria-live="polite"` on inline status/validation messages; AA contrast on all three Budget Status pairs (green/red ~7:1+, amber ~6.8:1) and body text; tap targets meet standard minimum size; text scales to 200% zoom without truncation; single-column reflow to 320px/400% zoom, no 2D scrolling.

NFR-2 (Connectivity): Always-online assumption; no offline queueing or sync. A failed save shows an inline, retry-capable error ("Couldn't save — check your connection"); entered values are retained, never silently lost.

NFR-3 (Identity): Single implicit user, no authentication, no accounts in v1; backend-persisted (not local/browser storage).

NFR-4 (Platform): Mobile-first responsive web; desktop scales up from the same design, no separate desktop redesign. No native app, no PWA install.

NFR-5 (Currency): ₹ (INR) only, Indian digit grouping (e.g. `₹1,00,000`). No multi-currency support.

NFR-6 (Data integrity guardrail): The Derived Total rule (FR-5) is enforced structurally — no totals/remaining column exists in the schema at all, so a service that accidentally caches a total becomes impossible to write.

NFR-7 (Persistence): System runs on persistent, non-ephemeral storage suitable for long-term personal use — not a free-tier setup that expires or resets.

### Additional Requirements

- **No starter/scaffolding template named in Architecture** — Epic 1 Story 1 initializes `apps/web` (Next.js 16.2 LTS) and `apps/api` (Spring Boot 4.1.0 / Spring Framework 7, Java 25 LTS) from scratch as a monorepo, per `ARCHITECTURE-SPINE.md`'s Source Tree — not a named starter kit.
- Configure CORS on `apps/api` before the first `apps/web` call ships (AD-2 wiring pitfall, not bolted on after integration breaks).
- All controllers accept/return DTOs only — no `@Entity`-annotated class is ever serialized directly in a response (AD-3). Entity → DTO mapping happens in the Service layer.
- Controller → Service → Repository layering enforced even for trivial endpoints; only a Service may call a Repository (AD-4).
- Add a centralized exception handler (`@ControllerAdvice`) early, returning a single consistent error shape `{ "error": { "code": "...", "message": "..." } }` (AD-8). No controller catches and reshapes its own exceptions.
- `categories` table has a `kind` enum (`DEFAULT` | `CUSTOM` | `SYSTEM`); exactly one `SYSTEM` "Uncategorized" row, guaranteed by a partial unique index (`WHERE kind='SYSTEM'`), seeded idempotently at startup, never created on demand (AD-5). `CategoryService` rejects any create/rename/re-icon/delete request targeting the SYSTEM row. `Category.name` unique case-insensitive among non-deleted categories, enforced at DB and DTO.
- `budgets` table is one row per Period (`period_start DATE UNIQUE`, `amount NUMERIC(12,2)`), never a single mutable value (AD-6). `BudgetService.getOrCreateCurrentPeriodBudget()` is the single idempotent creation path — if no prior Period has a budget row, no row is created (returns "no budget set"); if a prior Period's row exists, its amount is copied into a new row for the current Period. `period_start` UNIQUE + upsert (`INSERT … ON CONFLICT DO NOTHING`) makes concurrent first-touch requests safe.
- No totals/remaining/spent column anywhere in the schema — all Derived Totals computed by a Service-layer query at read time (AD-1). `GET /api/transactions` (filtered) returns `{results, total}` with `total` server-computed over the filtered set; the client never sums.
- All amounts `NUMERIC(12,2)` in PostgreSQL / `BigDecimal` in Java end to end, never float/double (AD-7). All dates ISO-8601 (`yyyy-MM-dd`), always resolved server-side fixed to `Asia/Kolkata` (AD-9) — the client never computes a date boundary itself, only sends a picked date or omits it for "today."
- `transactions` table supports only `POST /api/transactions` (create) and `GET /api/transactions` (read/filter) — no update or delete endpoint exists in MVP scope (AD-10).
- Auto-increment `BIGINT` primary keys everywhere — no UUIDs.
- Dedicated server-side endpoint (e.g. `GET /api/transactions/frequent`) computes the Frequent-Expenses Shelf ranking — never derived client-side from the raw transaction list.
- Last-used category default for Quick Add is derived server-side from the most recent transaction's category — not client-cached.
- REST resources are plural nouns under `/api/*` (`/api/transactions`, `/api/categories`, `/api/budget`) — no `/v1` prefix until a breaking contract change is actually needed.
- Spring Boot Actuator's `/actuator/health` enabled from day one (Railway's healthcheck depends on it). Structured JSON logs to stdout — no external log aggregator/APM for MVP.
- Backend testing: `@WebMvcTest` per endpoint (status → content-type → payload) plus direct unit tests on the derived-totals and budget-status calculation specifically.
- Frontend testing: component tests (Vitest/Testing Library) on Quick Add and Budget Status. No e2e for MVP.
- GitHub Actions CI runs backend + frontend tests/lint on every push/PR; a red run blocks merge.
- Deploys happen via each platform's native git integration (Vercel for `apps/web`, Railway for `apps/api` + Postgres) after merge to `main`, not as a separate manual step.
- `docker-compose.yml` provides local Postgres 18.4 for dev; `apps/web` and `apps/api` run natively via `next dev` / Spring Boot dev mode locally. No staging environment at solo-MVP scale — the CI gate substitutes for one.

### UX Design Requirements

UX-DR1: Implement the Calm Harbor color token set on top of shadcn/ui defaults — `background` (#F5F9FC), `primary` (#2563EB) reserved for brand/nav/selection only, `card` (#FFFFFF), `border` (#D1D5DB), `chip-bg` (#EFF6FF), `category-selected-bg` (#DBEAFE), `ring` (rgba(37,99,235,.35)), and the three Budget Status pairs (`budget-safe` #065F46 on #ECFDF5, `budget-warning` #92400E on #FFFBEB, `budget-danger` #991B1B on #FEF2F2).

UX-DR2: Implement the typography token set with relative units (`rem`, not fixed `px`) so text scales to 200% zoom without truncation — `body` (14px/1.5 regular), `label` (12px bold, 0.02em tracking), `heading` (17px bold), `amount-display` (40px bold, used only on Quick Add's live amount and the Search & Filter running total).

UX-DR3: Implement the rounded-corner scale by role — `sm` (10px, inputs), `md` (16px, cards and category buttons), `lg` (20px, screen corners), `full` (pill shape for FAB, save button, and frequent-expense chips).

UX-DR4: Implement the single elevation step — cards float with a soft shadow (`0 2px 8px rgba(15,23,42,.08)`), never a hard border; the FAB carries a stronger shadow (`0 6px 14px rgba(0,0,0,.28)`). No other component elevates.

UX-DR5: Build the Budget Status Card component — same position/layout across safe/warning/danger states; only background tint, icon, and copy change; icon+text always paired with color, never color alone. Used on the Dashboard (top) and as the Quick Add post-save echo. Updates instantly on save/edit, no toast/modal/push notification.

UX-DR6: Build the Category Icon Button component — 60×60px minimum; icon + short label; selected state shows primary-color border + category-selected-bg fill + a checkmark badge in the corner (never relying on border-color change alone) + `aria-pressed`. Never a dropdown. Used in Quick Add and Categories.

UX-DR7: Build the Frequent-Expense Chip component — pill-shaped, horizontally-scrollable shelf, chip-bg fill, visually distinct shape from category icon buttons. Tapping logs immediately with today's date and the chip's preset amount/Category/description, zero further input, no date edit available.

UX-DR8: Build the Quick-Add Save Button component — circular (`rounded.full`). Disabled state (#E5E7EB / #9CA3AF) is a real, distinct visual state, not just low-opacity, and exposes *why* it's disabled to assistive tech via `aria-describedby` or `aria-label` (pick one convention, apply everywhere).

UX-DR9: Build the FAB component — persistent, bottom-right, circular, primary fill, shadow per UX-DR4, always visible on the Dashboard, thumb-reachable, never tucked into a menu.

UX-DR10: Implement the Focus Ring treatment — `ring` token, 3px width / 2px offset, solid outline or equivalent box-shadow, applied to every focusable element (buttons, inputs, category tiles, chips), never suppressed (no `outline: none` without this replacement).

UX-DR11: Build the Category Row component (Categories screen) and Search Result Row component (Search & Filter) — both share the generic card treatment (card background, `rounded.md`, the single soft-shadow elevation step); the Search & Filter running total uses `amount-display` typography, positioned above the result list.

UX-DR12: Enforce color-usage governance — `primary` never reused for Budget Status meaning; the three Budget Status colors never reused elsewhere in the product including destructive actions; destructive actions (e.g. "Delete category") use shadcn's default `destructive` token, kept visually distinct from `budget-danger` so "over budget" is never confused with "about to delete something."

UX-DR13: Implement the WCAG 2.1 AA accessibility floor across every screen — full keyboard navigation on every interactive element; visible focus indicators everywhere (UX-DR10); semantic HTML (real `<button>`, `<label>`, `<input>`, heading levels reflecting IA, no `<div onclick>`); color never the sole signal; `aria-live="polite"` on inline status/error/validation messages and the Search & Filter running-total update; form fields carry visible `<label>`s, not placeholder-only labeling; tap targets meet standard minimum size; text scales via `rem` to 200% zoom; content reflows to a single column with no 2D scrolling down to 320 CSS px / 400% zoom.

UX-DR14: Implement the full State Patterns catalog: no-budget-set neutral prompt with CTA to Budget Settings (skippable); no-transactions-yet empty message on category breakdown; cold-app-load skeleton matching final shape (never a blank screen or full-page spinner); reopen-after-a-gap shows only today's/this-month's current picture (no "you missed N days," no catch-up prompt); save-fails inline retry-capable error via `aria-live`, entry retained; invalid-budget-entry inline validation ("Enter an amount greater than ₹0") via `aria-live`; invalid-category-name inline validation (empty/duplicate) with field retaining focus; search-no-matches message with filters remaining visible/editable; category-deleted-with-transactions reflects as "Uncategorized" everywhere (Dashboard, Search & Filter) with no dead links.

UX-DR15: Implement the three responsive breakpoints — `<640px` (single column, FAB bottom-right, horizontally-scrollable category button row, full-width status card); `640–1024px` (same single-column structure, content max-width constrains and centers); `≥1024px` (centered comfortable reading width, category grid may add columns). No separate desktop redesign — same IA and components scale up.

UX-DR16: Enforce the Interaction Primitives contract — every primary action is a single tap; keyboard-operable throughout (Tab order follows visual/reading order, Enter submits the focused form, category buttons/chips are real focusable `<button>` elements); explicitly banned: swipe gestures, long-press menus, drag-to-reorder, push notifications, toast/banner interruptions for budget alerts, streak counters/badges, decorative open/close animations, dropdowns for category selection, "unlogged days"/backlog counters, voice-to-log input, freeform NL quick-capture parsing, a dedicated lapse-recovery/re-entry screen.

UX-DR17: Implement the copy-tone contract per the Voice and Tone table — factual, complete-sentence, non-judgmental microcopy at every Budget Status severity level (e.g. "₹6,800 of ₹8,000 this month — 85% used, 5 days left"; "Budget exceeded by ₹250"); "Today's picture" framing on reopen-after-a-gap, never backlog language; severity carried by color+icon only, the amber/red messages stay exactly as matter-of-fact as the green one; a trust note on Settings ("We never connect to your bank.").

UX-DR18: Enforce platform scope constraints — mobile-first responsive web only, no native app or app-store install, no dark mode (light mode only), English only for V1, minimal/near-instant motion with no decorative animation anywhere.

### FR Coverage Map

FR1: Epic 1 - Frequent-expense chip logging
FR2: Epic 1 - Manual transaction entry
FR3: Epic 1 - Transaction-date period assignment
FR4: Epic 1 - Live budget status
FR5: Epic 1 - Derived totals
FR6: Epic 1 - Set and edit budget
FR7: Epic 2 - Add custom category
FR8: Epic 2 - Delete custom category
FR9: Epic 2 - Rename or re-icon any category
FR10: Epic 3 - Filter transactions

## Epic List

### Epic 1: Core Expense Loop — Log, See, and Budget
Sam can set up the app, log an expense in seconds via a one-tap frequent-expense chip or manual entry, and immediately see an honest, live-computed picture of spending against an optional monthly budget. This is the entire "core loop" the PRFAQ identifies as the product's non-negotiable value proposition — never cut regardless of timeline pressure. Story 1.1 is project scaffolding (`apps/web` + `apps/api` skeleton, CORS, CI, seeded default categories + SYSTEM row) since no starter template was named in Architecture — foundational to the epic's first user-facing story, not a separate epic.
**FRs covered:** FR1, FR2, FR3, FR4, FR5, FR6

### Epic 2: Category Management
Sam can keep the category set matched to how they actually spend — adding custom categories, renaming/re-iconing any category (including the 5 defaults), and deleting custom categories — without ever orphaning historical transaction data (auto-reassignment to Uncategorized).
**FRs covered:** FR7, FR8, FR9

### Epic 3: Search & Filter
Sam can answer "how much did I spend on X" by filtering past transactions by date range, category, and keyword, seeing the matching list and its running total update together instantly.
**FRs covered:** FR10

## Epic 1: Core Expense Loop — Log, See, and Budget

Sam can set up the app, log an expense in seconds via a one-tap frequent-expense chip or manual entry, and immediately see an honest, live-computed picture of spending against an optional monthly budget. This is the entire "core loop" the PRFAQ identifies as the product's non-negotiable value proposition — never cut regardless of timeline pressure.

### Story 1.1: Project Scaffolding & Default Categories

As a developer,
I want the Next.js and Spring Boot apps scaffolded with CORS, DTO conventions, a centralized exception handler, and CI wired up, with the 5 default categories and the Uncategorized system category seeded in PostgreSQL,
So that every subsequent story has a working, testable full-stack foundation instead of rebuilding plumbing story by story.

**Acceptance Criteria:**

**Given** a fresh clone of the repo
**When** a developer runs the local dev setup (`docker-compose up` for Postgres, `next dev`, Spring Boot dev mode)
**Then** `apps/web` loads a placeholder page and `apps/api` responds `200` on `/actuator/health`

**Given** `apps/api` is running
**When** `apps/web` calls any `/api/*` endpoint
**Then** the request succeeds without a CORS error (AD-2)

**Given** the database is freshly migrated
**When** the `categories` table is queried
**Then** it contains exactly 5 `DEFAULT` categories (Food, Transport, Shopping, Bills, Entertainment) and exactly 1 `SYSTEM` category (Uncategorized), seeded idempotently — re-running startup never duplicates rows (AD-5)

**Given** any controller throws an exception
**When** it propagates
**Then** a single `@ControllerAdvice` returns `{error:{code,message}}` (AD-8), never a raw stack trace

**Given** apps/web loads
**When** no login screen exists
**Then** it renders directly to a screen with no auth prompt of any kind (NFR-3)

**Given** a push or PR
**When** GitHub Actions CI runs
**Then** it executes backend + frontend tests/lint and blocks merge on failure

**And** `apps/web` deploys to Vercel and `apps/api`+Postgres deploy to Railway via native git integration on merge to `main`, with Postgres on a persistent (non-ephemeral) volume, not a free-tier setup that expires (NFR-7)

### Story 1.2: Manual Transaction Entry

As Sam,
I want to log a one-off expense manually with an amount, category, description, and date,
So that I can track purchases that aren't part of my regular repeat spending.

**Acceptance Criteria:**

**Given** Quick Add is open
**When** I enter an amount > ₹0, select a Category, and type a non-empty description
**Then** the Save button becomes enabled

**Given** Save is enabled
**When** I tap it
**Then** a Transaction is created (amount as `NUMERIC(12,2)`/`BigDecimal`) dated today by default, persisted via `TransactionController → TransactionService → TransactionRepository` (AD-4) returning a DTO (AD-3), and the app returns to Dashboard instantly

**Given** I tap the date field (showing "Today")
**When** I pick an earlier date
**Then** the Transaction saves against that date and its Budget Period is computed from the transaction date, not entry time, fixed to `Asia/Kolkata` (FR-3, AD-9)

**Given** amount is 0/empty, no Category is selected, or description is empty
**When** I tap the disabled Save button
**Then** nothing happens; for the description case, focus moves there with an accessible "required" state exposed via `aria-describedby`/`aria-label`, not just a visual disabled style

**Given** Category selection uses icon buttons (never a dropdown, ≥60×60px)
**When** Quick Add opens
**Then** the last-used Category is pre-selected, and the selected state shows primary border + tint fill + checkmark badge + `aria-pressed="true"`

**Given** the save fails (e.g. connection lost)
**When** I tap Save
**Then** entered values are retained, an inline retry-capable error shows via `aria-live="polite"` ("Couldn't save — check your connection"), and nothing is silently lost

**And** every focusable element in Quick Add shows the visible `{colors.ring}` focus indicator (3px/2px offset) on keyboard focus, never suppressed

### Story 1.3: Frequent-Expense Chip Logging

As Sam,
I want to log a repeat purchase in one tap via a Frequent-Expenses Shelf chip,
So that I can record spending in under 5 seconds with zero typing.

**Acceptance Criteria:**

**Given** Quick Add is open
**When** the Frequent-Expenses Shelf renders
**Then** it's populated from a dedicated server-side endpoint (`GET /api/transactions/frequent`) ranking Sam's habitual purchases — never derived client-side

**Given** a chip (e.g. "☕ Coffee · ₹150")
**When** I tap it
**Then** a Transaction is created immediately dated today with the chip's preset amount/Category/description, zero further input, no backdating option on this path

**Given** the tap succeeds
**When** the Transaction saves
**Then** the app returns to Dashboard instantly and totals/Budget Status reflect it with no loading spinner

**Given** the tap fails
**When** save fails
**Then** the chip's values are retained, an inline retry-capable error shows, and retapping retries without silently duplicating

**Given** the shelf renders as a horizontally-scrollable pill shape (`{colors.chip-bg}`)
**When** compared to Category icon buttons
**Then** it's visually distinct in shape (pill vs. rounded-square) so the two fast paths never look identical

### Story 1.4: Dashboard — Derived Totals & Category Breakdown

As Sam,
I want to open the app and immediately see my month-to-date total and a category-wise breakdown, computed live from my logged transactions,
So that I know exactly where my money went without doing any mental math.

**Acceptance Criteria:**

**Given** transactions exist for the current Period
**When** Dashboard loads
**Then** it shows the period total and per-Category breakdown, computed live at read time (AD-1) — no total/remaining/spent value ever read from a stored column, and amounts render as ₹ with Indian digit grouping (e.g. `₹1,00,000`)

**Given** no transactions exist yet
**When** Dashboard loads
**Then** the category breakdown area shows a simple empty message — no onboarding tour or illustration

**Given** Dashboard is cold-loading
**When** data hasn't arrived
**Then** a lightweight skeleton matching the final shape shows — never a blank screen or full-page spinner

**Given** Sam reopens after a 9-day gap
**When** Dashboard loads
**Then** it shows only today's/this month's current picture — no "you missed N days," no backlog counter, identical to a routine daily check (UJ-3)

**Given** a Transaction is saved (Story 1.2/1.3)
**When** the app returns to Dashboard
**Then** totals and breakdown reflect it immediately, no manual refresh or caching

**And** the client never sums transactions itself — it only renders the number the backend already computed

### Story 1.5: Set and Edit Monthly Budget

As Sam,
I want to set a single overall monthly budget and edit it later,
So that I have a number to measure my spending against without re-entering my spending history.

**Acceptance Criteria:**

**Given** no Budget has ever been set
**When** I enter an amount > ₹0 in Budget Settings and save
**Then** a `budgets` row is created for the current Period (`period_start` UNIQUE, `amount NUMERIC(12,2)`) via `BudgetService.getOrCreateCurrentPeriodBudget()`

**Given** I enter zero, negative, or non-numeric
**When** I try to save
**Then** it's blocked inline ("Enter an amount greater than ₹0") via `aria-live="polite"`, no submit-then-error round trip

**Given** a Budget already exists for the current Period
**When** I edit and save
**Then** the existing row updates (no new row) and Budget Status recalculates immediately against already-logged Transactions — no re-entry needed

**Given** a new Period begins and a prior Period had a Budget
**When** the current Period's budget is first requested
**Then** `getOrCreateCurrentPeriodBudget()` copies the prior amount into a new row (a value copy, not a live reference) — editing the current Period never mutates history (AD-6)

**Given** a new Period begins and no prior Period ever had a Budget
**When** first requested
**Then** no row is created and the endpoint reports "no budget set" rather than defaulting to zero

**Given** concurrent first-touch requests for the same Period
**When** both call `getOrCreateCurrentPeriodBudget()`
**Then** the `period_start` UNIQUE constraint + `INSERT…ON CONFLICT DO NOTHING` ensures exactly one row is created

**And** `BudgetController` delegates to `BudgetService` (AD-4) and returns a DTO (AD-3)

### Story 1.6: Dashboard — Live Budget Status

As Sam,
I want to see a clear green/amber/red status on the Dashboard showing how I'm tracking against my budget, in plain factual language,
So that I know at a glance whether I'm on pace without feeling judged.

**Acceptance Criteria:**

**Given** a Budget is set and spend is <80% of it
**When** Dashboard loads
**Then** the status card shows "safe" (green) with factual copy (e.g. "₹3,550 of ₹8,000 this month — on pace, 12 days left") and a paired icon — color is never the sole signal

**Given** spend is 80–100% of Budget
**When** Dashboard loads
**Then** the card shows "warning" (amber) with equally factual, non-escalating copy (e.g. "₹6,800 of ₹8,000 this month — 85% used, 5 days left")

**Given** spend exceeds 100%
**When** Dashboard loads
**Then** the card shows "danger" (red) with factual, non-blaming copy (e.g. "Budget exceeded by ₹250") — never "you failed" framing

**Given** no Budget is set for the current Period
**When** Dashboard loads
**Then** a neutral prompt ("Set a monthly budget to see your status") with a CTA to Budget Settings shows instead of a colored card — Quick Add, category totals, and Search & Filter all work fully without one

**Given** the status card
**When** it renders
**Then** it also shows days remaining in the current Period, computed live server-side

**Given** a Transaction saves or Budget is edited
**When** either happens
**Then** status recalculates on next load — no caching, no push notification/toast/modal interruption

**And** all three states share identical position/layout (only tint, icon, copy change) and meet WCAG AA contrast (green/red ≈7:1+, amber ≈6.8:1)

## Epic 2: Category Management

Sam can keep the category set matched to how they actually spend — adding custom categories, renaming/re-iconing any category (including the 5 defaults), and deleting custom categories — without ever orphaning historical transaction data (auto-reassignment to Uncategorized).

### Story 2.1: Add Custom Category

As Sam,
I want to create a custom Category with a name and icon,
So that my category set matches the way I actually spend.

**Acceptance Criteria:**

**Given** the Categories screen
**When** I tap "+ Add category," enter a name, pick an icon, and save
**Then** a new `CUSTOM` Category is created and immediately available in Quick Add's Category selector and Search & Filter's category filter, with no separate sync step

**Given** I enter an empty name
**When** I try to save
**Then** it's blocked inline ("Give this category a name") and the name field retains focus

**Given** I enter a name matching an existing Category (case-insensitive)
**When** I try to save
**Then** it's blocked inline ("You already have a category called that") and the name field retains focus — enforced at both the DTO and a DB `UNIQUE` constraint among non-deleted categories (AD-5)

**And** `CategoryController` delegates to `CategoryService` (AD-4) and returns a DTO (AD-3); the new row's `kind` is `CUSTOM`

### Story 2.2: Rename or Re-icon Any Category

As Sam,
I want to rename or change the icon of any Category, including the 5 defaults,
So that I can keep category labels accurate over time without losing any history.

**Acceptance Criteria:**

**Given** any Category (default or custom)
**When** I tap it on the Categories screen and change its name and/or icon
**Then** the change saves and applies going forward

**Given** the same empty/duplicate-name validation as Story 2.1
**When** I try to rename to an empty or already-used name
**Then** it's blocked inline the same way, including for renames of the 5 defaults

**Given** a Category with existing Transactions
**When** I rename or re-icon it
**Then** none of its historical Transactions or any Derived Total changes — only the label/icon changes going forward

**And** `CategoryService` allows rename/re-icon on `DEFAULT` and `CUSTOM` kinds, but rejects any such request targeting the `SYSTEM` (Uncategorized) row (AD-5)

### Story 2.3: Delete Custom Category

As Sam,
I want to delete a Category I created,
So that I can clean up categories I no longer use without losing the transactions I logged under them.

**Acceptance Criteria:**

**Given** a `CUSTOM` Category
**When** I select it and tap delete
**Then** it's deleted immediately with no confirmation step

**Given** the deleted Category had existing Transactions
**When** deletion completes
**Then** every one of those Transactions is automatically reassigned to the `SYSTEM` Uncategorized row via a single FK reassignment (AD-5) — never blocked, never requiring manual reassignment — and they continue to show correctly in Dashboard totals and Search & Filter under "Uncategorized"

**Given** any of the 5 default Categories
**When** the Categories screen renders
**Then** no delete affordance is shown for them at all — not merely disabled

**And** `CategoryService` rejects any delete request targeting the `SYSTEM` row itself, even if attempted directly against the API

## Epic 3: Search & Filter

Sam can answer "how much did I spend on X" by filtering past transactions by date range, category, and keyword, seeing the matching list and its running total update together instantly.

### Story 3.1: Filter Transactions by Date, Category, and Keyword

As Sam,
I want to filter my past transactions by date range, category, and an optional keyword,
So that I can answer "how much did I spend on X" without leaving the app's single-screen-depth model.

**Acceptance Criteria:**

**Given** Search & Filter is open
**When** I set a date range and/or select a Category and/or type a keyword
**Then** the transaction list and its running total update together, instantly, on every filter change

**Given** a keyword is entered
**When** the filter applies
**Then** it matches against the transaction `description` field

**Given** the filtered list
**When** it renders
**Then** `GET /api/transactions` returns `{results, total}` with `total` computed server-side over the filtered set (AD-1) — the client never sums the displayed rows itself

**Given** no transactions match the current filter combination
**When** the list renders
**Then** it shows "No matching transactions" while all filters remain visible and editable so Sam can immediately loosen them

**Given** a Category was deleted (Epic 2, Story 2.3) and its transactions reassigned to Uncategorized
**When** Sam filters by category
**Then** "Uncategorized" appears as a selectable filter option and those transactions surface correctly under it

**And** the running total uses `amount-display` typography (per `DESIGN.md`), positioned above the result list; pagination/infinite-scroll is explicitly out of scope (not needed at MVP data scale)
