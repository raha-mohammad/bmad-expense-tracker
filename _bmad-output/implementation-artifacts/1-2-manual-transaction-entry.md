---
baseline_commit: 952b096e5db36ca7ef8f31c61f27ea423e737260
---

# Story 1.2: Manual Transaction Entry

Status: done

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As Sam,
I want to log a one-off expense manually with an amount, category, description, and date,
so that I can track purchases that aren't part of my regular repeat spending.

## Acceptance Criteria

1. **Given** Quick Add is open, **when** I enter an amount > ₹0, select a Category, and type a non-empty description, **then** the Save button becomes enabled.
2. **Given** Save is enabled, **when** I tap it, **then** a Transaction is created (amount as `NUMERIC(12,2)`/`BigDecimal`) dated today by default, persisted via `TransactionController → TransactionService → TransactionRepository` (AD-4) returning a DTO (AD-3), and the app returns to Dashboard instantly.
3. **Given** I tap the date field (showing "Today"), **when** I pick an earlier date, **then** the Transaction saves against that date and its Budget Period is computed from the transaction date, not entry time, fixed to `Asia/Kolkata` (FR-3, AD-9).
4. **Given** amount is 0/empty, no Category is selected, or description is empty, **when** I tap the disabled Save button, **then** nothing happens; for the description case, focus moves there with an accessible "required" state exposed via `aria-describedby`/`aria-label`, not just a visual disabled style.
5. **Given** Category selection uses icon buttons (never a dropdown, ≥60×60px), **when** Quick Add opens, **then** the last-used Category is pre-selected, and the selected state shows primary border + tint fill + checkmark badge + `aria-pressed="true"`.
6. **Given** the save fails (e.g. connection lost), **when** I tap Save, **then** entered values are retained, an inline retry-capable error shows via `aria-live="polite"` ("Couldn't save — check your connection"), and nothing is silently lost.
7. **And** every focusable element in Quick Add shows the visible `{colors.ring}` focus indicator (3px/2px offset) on keyboard focus, never suppressed.

## Tasks / Subtasks

- [x] Task 1: Wire Calm Harbor design tokens into `apps/web/app/globals.css` (AC: all — this is the first story to render real product UI; Story 1.1's scaffold left shadcn's default grayscale theme untouched)
  - [x] In `:root`, override shadcn's defaults with `DESIGN.md`'s values: `background` (#F5F9FC), `primary` (#2563EB), `primary-foreground` (#FFFFFF), `card` (#FFFFFF), `card-foreground` (#111827), `border` (#D1D5DB), `ring` (rgba(37,99,235,.35)).
  - [x] Add the not-yet-consumed tokens too while you're in this file — `chip-bg` (#EFF6FF), `category-selected-bg` (#DBEAFE), `budget-safe`/`budget-safe-bg`, `budget-warning`/`budget-warning-bg`, `budget-danger`/`budget-danger-bg` (see `DESIGN.md` colors block for exact hex) — Stories 1.3/1.6 will consume these; defining them once now avoids three separate stories re-deriving the same hex values.
  - [x] Add matching entries under `@theme inline` (e.g. `--color-category-selected-bg: var(--category-selected-bg);`) so Tailwind utility classes like `bg-category-selected-bg` work.
  - [x] Override `--radius-sm`/`--radius-md`/`--radius-lg` to fixed 10px/16px/20px per `DESIGN.md`'s rounded scale (replacing the current `calc(var(--radius) * n)` derivation, which doesn't match Calm Harbor's scale). `rounded-full` already works natively in Tailwind — no token needed for it.
  - [x] Do not touch `.dark` block styling beyond what's already there — no dark mode in v1 (NFR/UX-DR18).

- [x] Task 2: Add shadcn `Input` and `Label` components (AC: #1, #4, #5)
  - [x] Add via the shadcn CLI (`npx shadcn add input label`) or hand-write following `components/ui/button.tsx`'s existing conventions exactly (`data-slot` attribute, `cn()` utility, no bespoke styling outside the established pattern).

- [x] Task 3: `transactions` table — `Transaction` entity + repository (AC: #2, #3)
  - [x] Create `Transaction` JPA entity (`entity/Transaction.java`): `@Table(name = "transactions")` (Hibernate's default naming strategy does **not** pluralize — omitting this would create a table literally named `transaction`, breaking the `snake_case plural` convention `categories` already follows). Fields: `id` (`BIGINT`, auto-increment), `category` (`@ManyToOne`, `@JoinColumn(name = "category_id", nullable = false)` — reuse the existing `Category` entity, do not duplicate it), `amount` (`BigDecimal`, column `NUMERIC(12,2)`), `transactionDate` (`LocalDate`, `NOT NULL`), `description` (`String`, `NOT NULL`). Mirror `Category.java`'s immutability style: protected no-arg constructor for JPA, one public constructor, getters only, no setters (AD-10: transactions are immutable once saved).
  - [x] Let `spring.jpa.hibernate.ddl-auto=update` create the table + FK, per the schema-management approach Story 1.1 already decided — no native SQL/`JdbcTemplate` needed here (unlike `categories`, there's no partial/functional index requirement for `transactions`).
  - [x] Do **not** add any totals/remaining/spent/period column to this entity or table — AD-1's structural guarantee depends on no such column ever existing. Budget Period is always derived later from `transaction_date` at read time (Story 1.4/1.5/1.6's job), never stored here.
  - [x] Create `TransactionRepository extends JpaRepository<Transaction, Long>` with one query method: `Optional<Transaction> findTopByOrderByTransactionDateDescIdDesc()` (used by the last-used-category lookup in Task 5).

- [x] Task 4: Transaction DTOs + validation (AC: #1, #4)
  - [x] `dto/CreateTransactionRequest.java` (request record): `amount` (`BigDecimal`, `@NotNull @DecimalMin(value = "0.0", inclusive = false)`), `categoryId` (`Long`, `@NotNull`), `description` (`String`, `@NotBlank`), `transactionDate` (`LocalDate`, nullable/optional — omitted means "today," see Task 5). `spring-boot-starter-validation` is already a `pom.xml` dependency from Story 1.1 (added in anticipation of this) — no build-file change needed to use `@Valid`/`@NotNull`/`@NotBlank`/`@DecimalMin`.
  - [x] `dto/TransactionDto.java` (response record): `id`, `categoryId`, `amount`, `transactionDate`, `description` — never serialize the `Transaction` entity directly (AD-3).
  - [x] `dto/LastUsedCategoryDto.java` (response record): single field `categoryId` (`Long`, nullable — `null` means no transaction has ever been logged yet).

- [x] Task 5: `TransactionService` (AC: #2, #3, #4, #5)
  - [x] `createTransaction(CreateTransactionRequest request)`: look up the `Category` by `request.categoryId()` via `CategoryRepository.findById(...)` (a plain existence/fetch lookup — calling another aggregate's Repository directly from a Service still satisfies AD-4's "only a Service may call a Repository" rule; no need to route through `CategoryService` for this). If not found, throw a new `CategoryNotFoundException` (place it in the existing `service/` package alongside `TransactionService` — no new top-level package needed, keeping the Source Tree exactly as `ARCHITECTURE-SPINE.md` defines it) — this is an edge case the epics ACs don't cover explicitly (the UI only ever offers real categories), but the backend must not 500 or silently corrupt data on a bad/stale `categoryId`.
  - [x] Default `transactionDate` server-side when the request omits it: `LocalDate.now(ZoneId.of("Asia/Kolkata"))` (AD-9 — be explicit with the zone rather than relying on the JVM-default pin in `ExpenseTrackerApiApplication`'s static initializer; both will agree, but explicit is more robust and self-documenting). When the request supplies a date (backdating), use it exactly as given — no future-date restriction exists in any spec, don't invent one.
  - [x] Persist via `TransactionRepository.save(...)`, map to `TransactionDto`.
  - [x] `getLastUsedCategoryId()`: call `transactionRepository.findTopByOrderByTransactionDateDescIdDesc()`; return its `category().getId()` wrapped appropriately, or `null`/empty when no transaction exists yet (first-ever use of the app — a real edge case, not just a hypothetical).

- [x] Task 6: `TransactionController` (AC: #1, #2, #3, #4, #5)
  - [x] `POST /api/transactions` — `@Valid @RequestBody CreateTransactionRequest`, delegates to `TransactionService.createTransaction(...)`, returns `201 Created` + `TransactionDto`.
  - [x] `GET /api/transactions/last-category` — delegates to `TransactionService.getLastUsedCategoryId()`, returns `200` + `LastUsedCategoryDto`. This endpoint is a deliberate scope addition beyond what `epics.md` literally names for this story — same pattern Story 1.1 used for `GET /api/categories`. It exists because `ARCHITECTURE-SPINE.md`'s Capability Map explicitly requires the last-used-category default to be "server-derived, not client-cached," and AC5 requires Quick Add to pre-select it on open; a small dedicated read endpoint is the cleanest way to satisfy both without inventing a bigger `GET /api/transactions` (that's FR-10/Epic 3's job — do not build a general list/filter endpoint here). Naming follows the same `/api/transactions/{sub-resource}` convention `ARCHITECTURE-SPINE.md`'s Deferred section already anticipates for Story 1.3's `/api/transactions/frequent`.
  - [x] Controller only calls `TransactionService` (AD-4); returns DTOs only (AD-3). No update/delete endpoint — only `POST` (create) and the two `GET`s exist for transactions in MVP scope (AD-10).

- [x] Task 7: Harden `GlobalExceptionHandler` for validation and not-found errors (AC: #4) — resolves a real, previously-deferred gap
  - [x] `deferred-work.md` flagged from Story 1.1's review: *"`GlobalExceptionHandler` flattens all failures to 500 ... relevant once Epic 2/Story 1.2 add real validation failures."* This story is that moment — `@Valid` on `CreateTransactionRequest` will throw `MethodArgumentNotValidException`, and right now the catch-all `@ExceptionHandler(Exception.class)` would turn a client validation mistake into a misleading `500`.
  - [x] Add `@ExceptionHandler(MethodArgumentNotValidException.class)` returning `400` in the existing `{error:{code,message}}` shape (AD-8) — e.g. `ErrorResponse.of("VALIDATION_ERROR", <first field error's message>)`.
  - [x] Add `@ExceptionHandler(CategoryNotFoundException.class)` (from Task 5) returning `400` in the same shape, e.g. `ErrorResponse.of("CATEGORY_NOT_FOUND", ex.getMessage())`.
  - [x] Keep the existing catch-all `Exception` handler as the last resort (still `500` for truly unexpected errors); log at `warn` (not `error`) for these two new, expected-client-error cases, since they aren't application bugs.

- [x] Task 8: Frontend API client (AC: #1, #2, #3, #6) — no `apps/web → apps/api` HTTP client exists yet; Story 1.1 only proved CORS at the backend test level
  - [x] Create `lib/api.ts` with `getCategories()`, `getLastUsedCategory()`, `createTransaction(payload)` — thin `fetch` wrappers against a configurable base URL. Add `NEXT_PUBLIC_API_BASE_URL` (e.g. `http://localhost:8080` for local dev) as an env var (`.env.local` for local dev, not committed; document it in `apps/web/README.md` or an `.env.example`), since Next.js only exposes `NEXT_PUBLIC_`-prefixed vars to client code.
  - [x] On a failed `fetch` (network error or non-2xx), the client functions should surface a plain error the caller can catch — don't swallow failures silently (needed for AC6).

- [x] Task 9: Category icon mapping helper (AC: #5)
  - [x] `lib/category-icons.ts`: a lookup table from the current placeholder icon keys (`"food"`, `"transport"`, `"shopping"`, `"bills"`, `"entertainment"`, `"help-circle"`) to `lucide-react` icon components, with a generic fallback (e.g. `Circle`) for any unmapped key. **This mapping is explicitly a placeholder**, not the final icon set — `ARCHITECTURE-SPINE.md`'s Deferred section and Story 1.1's Completion Notes both flag the real icon-set decision for Epic 2's Categories screen. Don't over-invest here; the fallback path matters more than getting each icon "right."

- [x] Task 10: `CategoryIconButton` component + Quick Add form (AC: #1, #2, #3, #4, #5, #6, #7)
  - [x] `components/category-icon-button.tsx` (named in `ARCHITECTURE-SPINE.md`'s own Source Tree comment) — ≥60×60px, icon + short label, selected state = `{colors.primary}` border + `{colors.category-selected-bg}` fill + a checkmark badge in the corner + `aria-pressed`. A real focusable `<button>`, never a `<div onclick>` or a `<select>`/dropdown.
  - [x] `components/quick-add-form.tsx` — a **Client Component** (`"use client"`; needs interactive state, and Vitest can't test async Server Components anyway):
    - Amount: a real `<input inputMode="decimal">` (numeric mobile keyboard) styled with `amount-display` typography (40px/bold per `DESIGN.md`) — **not** the composition mock's custom on-screen keypad. `EXPERIENCE.md`'s Component Patterns table doesn't govern a keypad as a distinct component (only the mockup shows one); a native numeric input satisfies FR-2 with far less complexity and no custom-keypad accessibility burden. (If a pixel-exact keypad is wanted later, that's a follow-up, not this story.)
    - Date: defaults to today, shown as text (e.g. "Today, 5 Jul 2026"); use a native `<input type="date">` for the picker — simplest AD-9-compliant choice (client only ever sends a picked date or omits it; no calendar library is installed and none is needed).
    - Category row: fetch `GET /api/categories` and `GET /api/transactions/last-category` on mount; pre-select the last-used category. **When `last-category` returns `null` (no transaction has ever been logged — a real first-use state)**, pre-select the first category from the `GET /api/categories` response as a sensible default instead of leaving nothing selected — not spelled out in `epics.md`/`EXPERIENCE.md`, but avoids Save being permanently gated on a category no one ever explicitly picks.
    - Description: a labeled `Input` (Task 2), required.
    - Save button: circular (`rounded-full`), disabled until `amount > 0 && categoryId != null && description.trim() !== ''`. Expose the disabled reason via **`aria-describedby`** pointing at visible hint text (e.g. "Add a description to save") — `EXPERIENCE.md` says pick one convention and apply it everywhere; this story establishes `aria-describedby` as that convention since none existed before.
    - On tap with Save enabled: `POST` via `lib/api.ts`; on success, navigate back to `/` (see Task 11 — there is no real Dashboard yet, so "returns to Dashboard" from AC2 means the current placeholder root route for now; Story 1.4 replaces it with the real Dashboard, don't build a fake one here).
    - On failure: keep all entered field values as-is (no reset), show an inline message ("Couldn't save — check your connection.") in an `aria-live="polite"` region.
    - Every focusable element (amount input, date input, category buttons, description input, save button) must show the visible focus ring — confirm `components/ui/button.tsx`'s `cva` classes include a `2px` offset (`focus-visible:ring-offset-2` or equivalent); currently only ring width/color are set, add the offset if missing, matching `DESIGN.md`'s focus-ring spec (3px width / 2px offset) exactly.
    - Do **not** build the Frequent-Expenses Shelf here — that's Story 1.3 (FR-1). This story's Quick Add is manual-entry only.

- [x] Task 11: Temporary entry point from the root page (AC: #2) — Dashboard doesn't exist until Story 1.4
  - [x] Update `apps/web/app/page.tsx` (currently Story 1.1's placeholder) to add a plain link/button to `/quick-add`. Don't build a fake FAB or Dashboard shell here — that's explicitly Story 1.4/1.6's scope; keep this a minimal, honest placeholder-plus-link.
  - [x] `apps/web/app/quick-add/page.tsx` — a thin route file that renders `<QuickAddForm />` (Task 10). Keep logic in the component, not the page, so it stays independently testable.

- [x] Task 12: Frontend test infrastructure (Vitest + Testing Library) + Quick Add component tests (AC: #1, #4, #5, #6, #7) — `ARCHITECTURE-SPINE.md`'s Consistency Conventions table names Quick Add explicitly as requiring frontend component tests, but Story 1.1 never installed the framework to write them
  - [x] Add devDependencies: `vitest`, `@vitejs/plugin-react`, `jsdom`, `@testing-library/react`, `@testing-library/dom`, `@testing-library/jest-dom`, `vite-tsconfig-paths`.
  - [x] Add `apps/web/vitest.config.mts`: `jsdom` test environment, `tsconfigPaths()` + `react()` plugins (so `@/...` imports resolve in tests same as in the app).
  - [x] Add a `"test": "vitest run"` script to `package.json`.
  - [x] `components/quick-add-form.test.tsx` (mock `lib/api.ts`'s fetch calls), covering at minimum:
    - Save button is disabled when amount is 0/empty, and when description is empty (with `aria-describedby` present pointing at visible hint text).
    - Save button becomes enabled once amount > 0, a category is selected, and description is non-empty.
    - Tapping a category button updates its `aria-pressed`/selected visual state (and deselects the previous one).
    - On a successful save (mocked), navigation to `/` is triggered.
    - On a failed save (mocked rejection), the entered amount/description/category remain unchanged in the form and an `aria-live` error message appears.

- [x] Task 13: CI — add the frontend test step (AC: all) — `ARCHITECTURE-SPINE.md` requires "backend + frontend tests/lint on every push/PR"; Story 1.1's CI has no frontend test step because none existed
  - [x] In `.github/workflows/ci.yml`'s `frontend` job, add `npm test` (or `npx vitest run`) as a step, gating merge alongside lint/build.

- [x] Task 14: Backend tests (AC: #1, #2, #3, #4, #5)
  - [x] `@WebMvcTest(TransactionController.class)` (mock `TransactionService` via `@MockitoBean`, same pattern as `CategoryControllerTest`): `POST` with valid body → `201` + `TransactionDto` JSON shape; `POST` with blank description → `400` with `{error:{code:"VALIDATION_ERROR",...}}`; `POST` with amount `0` → `400`; `POST` with an unknown `categoryId` (service mocked to throw `CategoryNotFoundException`) → `400` with `{error:{code:"CATEGORY_NOT_FOUND",...}}`; `GET /api/transactions/last-category` → `200` with a `categoryId` (and separately, a `200` with `categoryId: null` when the service reports none).
  - [x] `TransactionService` unit/integration test: omitting `transactionDate` in the request defaults to "today" in `Asia/Kolkata` (AD-9); a supplied backdate is persisted exactly as given; `amount` round-trips as `BigDecimal`/`NUMERIC(12,2)`, never `float`/`double` (AD-7).
  - [x] `getLastUsedCategoryId()`: returns empty/`null` against an empty `transactions` table; returns the most recently dated transaction's `categoryId` once transactions exist (and ties on the same date break by highest `id`, per `findTopByOrderByTransactionDateDescIdDesc()`).

### Review Findings

Three parallel adversarial layers (Blind Hunter — diff only, no project context; Edge Case Hunter — diff + project read access; Acceptance Auditor — diff + spec + architecture docs) reviewed this story's diff. Raw findings were deduplicated and fact-checked against ground truth already established during implementation; see below for the full accounting.

All 10 patches below (including the money-precision item, resolved by user decision to adopt string-typed money project-wide) were applied, covered by new/updated tests (2 new backend `@WebMvcTest` cases for oversized amount/description, 1 for a malformed date; 3 new/updated frontend Vitest cases for the double-click guard, cleared-date normalization, and the string-typed amount payload), and re-verified live via a second Playwright browser session against the real backend — confirming the date field now shows "Today, 5 Jul 2026" as visible text, the Save button's accessible name is "Save expense," and last-used-category pre-selection still works correctly after the `Promise.all` rewrite. Full regression: 24/24 backend tests, 9/9 frontend tests, lint/typecheck/build all clean.

- [x] [Review][Patch] **[Decision resolved: adopt string-typed money project-wide]** Money is represented as a JS `number` in the frontend/wire contract, not a string — `apps/web/lib/api.ts`'s `CreateTransactionPayload.amount`/`TransactionDto.amount` are typed `number`, and `quick-add-form.tsx` computes `amountValue = parseFloat(amount)` before sending. AD-9's Dev Notes bullet says "amount is `BigDecimal`/`NUMERIC(12,2)` end to end, never `float`/`double`" — that's only actually true on the Java side. User decided this is a project-wide pattern to establish now, not a one-story fix: every future money-touching story (Budget Settings, Dashboard totals, Search & Filter running total) must follow the same convention. Fix: `TransactionDto.amount` gets `@JsonFormat(shape = JsonFormat.Shape.STRING)` (forces the wire response to a JSON string, e.g. `"150.00"`); `CreateTransactionRequest.amount` stays plain `BigDecimal` (Jackson already deserializes a quoted JSON string into `BigDecimal` by default, no annotation needed for reads — only the write/response shape needs forcing). Frontend: `CreateTransactionPayload.amount`/`TransactionDto.amount` become `string`; `quick-add-form.tsx` sends the trimmed raw input string directly instead of `parseFloat`'s result (parsing is still used for client-side validation only). Update `TransactionControllerTest`'s JSON bodies/assertions to match the new string shape.

- [x] [Review][Patch] Malformed or empty `transactionDate` reaches the server unhandled and returns `500`, not `400` [`apps/api/src/main/java/com/bmad/expensetracker/config/GlobalExceptionHandler.java`, `apps/web/components/quick-add-form.tsx`] — Jackson fails to deserialize a garbage date string (or an empty string, which a cleared native date input's `onChange` produces — `pickedDate ?? undefined` doesn't catch `""` since nullish coalescing ignores empty strings) before `@Valid` ever runs, throwing `HttpMessageNotReadableException`, uncaught by either of Task 7's new handlers, falling to the generic `500`. Directly undermines this story's own Task 7 goal. Fix: add an `@ExceptionHandler(HttpMessageNotReadableException.class)` → `400` `VALIDATION_ERROR`, and normalize the date `onChange` to store `null` instead of `""`.
- [x] [Review][Patch] The date field never visibly shows "Today, 5 Jul 2026" as text — violates AC3 and Task 10's own subtask [`apps/web/components/quick-add-form.tsx`] — the friendly label from `formatDateLabel()` is only ever passed into the bare native `<input type="date">`'s `aria-label`, never rendered as visible text; sighted users see only the word "Date" and the browser's own native date-input rendering. That same `aria-label` also overrides the wrapping `<label>Date</label>` text for screen readers, and it changes on every date change, over-announcing on each interaction. Needs a visible text element showing the friendly date string, with `aria-label` reconciled rather than duplicating/overriding it.
- [x] [Review][Patch] Several type sizes use fixed `px` instead of `DESIGN.md`'s mandated `rem` units [`apps/web/components/quick-add-form.tsx` (`text-[40px]`, `text-[17px]`), `apps/web/components/category-icon-button.tsx` (`text-[9px]`)] — `DESIGN.md`'s Typography section explicitly requires relative units so text scales with browser zoom/OS text-scaling (WCAG 1.4.4). The `9px` category-label size is also not part of the defined type scale at all (smallest defined role is `label` at 12px) — an unspecified size was invented rather than reusing a spec'd role.
- [x] [Review][Patch] The Save button's only accessible name is the literal "✓" glyph — no `aria-label` [`apps/web/components/quick-add-form.tsx`] — a screen reader announces a checkmark symbol, not "Save." Add `aria-label="Save expense"` (or similar).
- [x] [Review][Patch] `Promise.all([getCategories(), getLastUsedCategory()])` fails as a single unit [`apps/web/components/quick-add-form.tsx`] — if only the narrow `last-category` endpoint errors while categories load fine, the whole form falls into `loadError` and never renders anything, when falling back to "ignore last-used, default to first category" (the same fallback already used for the null-case) would be far more resilient.
- [x] [Review][Patch] No backend guard against amount exceeding `NUMERIC(12,2)` or description exceeding the entity's implicit 255-char column [`apps/api/src/main/java/com/bmad/expensetracker/dto/CreateTransactionRequest.java`] — both overflow cases throw an uncaught `DataIntegrityViolationException` at save time, surfacing as `500` instead of `400`. Add `@Digits(integer = 10, fraction = 2)` on `amount` and `@Size(max = 255)` on `description`.
- [x] [Review][Patch] Frontend error handling swallows the real failure reason and always shows "check your connection" [`apps/web/lib/api.ts`, `apps/web/components/quick-add-form.tsx`] — `request()` throws a generic `Error` with just the HTTP status; a real structured `400` (`{error:{code,message}}`) gets the same misleading connection-lost message as an actual network failure, and `handleSave`'s `catch` block logs nothing, making a real production failure indistinguishable from a network blip without inspecting the network tab.
- [x] [Review][Patch] No guard against a rapid double-tap on Save creating duplicate transactions [`apps/web/components/quick-add-form.tsx`] — `aria-disabled` (chosen deliberately over native `disabled` so AC4's tap-to-focus behavior works) means the button never truly blocks a second click; `isSaving` is state-derived, not checked via a ref, leaving a narrow window for two concurrent `createTransaction` calls before React re-renders.
- [x] [Review][Patch] `todayIso()` derives "today" from the browser's local timezone, not `Asia/Kolkata` [`apps/web/components/quick-add-form.tsx`] — this story's own Dev Notes call AD-9 ("`Asia/Kolkata` is the only clock") a hard constraint; the displayed/default date value should be derived the same way, even though it's display-only today (the actual value sent is either omitted or exactly what the user picks).

- [x] [Review][Defer] Only the first Bean Validation field error is surfaced to the client when multiple fields are invalid at once [`apps/api/src/main/java/com/bmad/expensetracker/config/GlobalExceptionHandler.java`] — deferred, low-value given client-side gating already prevents most real occurrences (Save stays disabled until all fields pass client validation).
- [x] [Review][Defer] A date picked before the `Asia/Kolkata` midnight boundary can go stale if the session stays open across it [`apps/web/components/quick-add-form.tsx`] — deferred, extremely narrow edge case for a personal single-user app.
- [x] [Review][Defer] No defensive check if a last-used `categoryId` is absent from the freshly-fetched category list [`apps/web/components/quick-add-form.tsx`] — deferred, currently unreachable (Epic 2's category deletion doesn't exist yet).
- [x] [Review][Defer] No retry affordance on the initial category-load error [`apps/web/components/quick-add-form.tsx`] — deferred, minor UX polish, user can reload the page today.

**Dismissed as noise or refuted (7):**
- `CategoryNotFoundException.getMessage()` piped into the public API's `message` field — consistent with the existing AD-8 free-text message precedent already used for validation errors, not a new gap.
- `TransactionServiceTest`'s full `@SpringBootTest` + hardcoded seed-category-name coupling — matches `CategoryBootstrapRunnerTest`'s own established precedent from Story 1.1.
- `CategoryDto.kind` unused in this story — mirrors the existing `GET /api/categories` response contract; intentional forward-compatibility for Epic 2, not dead code.
- No entity-level Bean Validation on `Transaction` — consistent with `Category.java`'s existing precedent; DTO-level validation is the project's established, sufficient pattern.
- "Completion notes are self-reported claims with no attached evidence" (Blind Hunter, zero project context) — refuted: the dev agent directly executed and observed all 21 backend + 7 frontend test runs and the live Playwright verification in the same session, not fabricated.
- `getLastUsedCategoryId()` `LazyInitializationException` risk (Edge Case Hunter) — refuted: Hibernate's lazy `@ManyToOne` proxy embeds the FK column value as the identifier at construction time, so a bare `.getId()` call never triggers initialization regardless of session/transaction state — this is documented Hibernate behavior, not a bug.
- Empty `categories` array leaving Save permanently disabled with a misleading hint (Blind Hunter + Edge Case Hunter) — structurally unreachable: AD-5's SYSTEM-row partial-unique-index guarantee means at least one category (Uncategorized) always exists and can never be deleted, so `GET /api/categories` can never return `[]`.

## Dev Notes

- **Architecture is final — every `AD-n` below is a hard constraint, not a suggestion** [Source: `ARCHITECTURE-SPINE.md`]:
  - **AD-1** — no totals/remaining/period column on `Transaction`, ever. Budget Period is always derived from `transaction_date` at read time by later stories.
  - **AD-3** — `TransactionController` returns `TransactionDto`/`LastUsedCategoryDto` only, never the `Transaction` entity.
  - **AD-4** — `TransactionController → TransactionService → TransactionRepository`. `TransactionService` may call `CategoryRepository` directly for the existence check (a Service calling a Repository still satisfies the rule; it doesn't have to be its own aggregate's repository) — but the Controller never touches any Repository directly.
  - **AD-7** — `amount` is `BigDecimal`/`NUMERIC(12,2)` end to end, never `float`/`double`.
  - **AD-8** — single `{error:{code,message}}` shape for every failure, including the new validation/not-found cases this story introduces (Task 7).
  - **AD-9** — `Asia/Kolkata` is the only clock that computes "today"; the client only ever sends a picked date or omits it.
  - **AD-10** — `Transaction` has no update/delete path in MVP; only `POST` (create) and reads exist.
- **This story is the first to render real product UI and the first to make a real `apps/web → apps/api` HTTP call.** Three foundational gaps fall on it because no prior story touched them: the Calm Harbor brand tokens were never wired into `globals.css` (Task 1), there's no API client yet (Task 8), and Vitest/Testing Library were never installed despite architecture explicitly naming Quick Add as needing component tests (Task 12). Don't skip these thinking they're "someone else's setup" — there is no one else, this is the first story that needs them.
- **`GlobalExceptionHandler` currently 500s on everything** (Story 1.1 built only the catch-all). `deferred-work.md` explicitly flagged this as relevant "once Epic 2/Story 1.2 add real validation failures" — that's now. Task 7 resolves it; don't leave `@Valid` failures returning a raw `500`.
- **The Frequent-Expenses Shelf is explicitly out of scope for this story** (Story 1.3 / FR-1) — Quick Add here is manual-entry only. Don't build shelf UI, even hidden/empty-state, since there's no `GET /api/transactions/frequent` endpoint yet either.
- **No real Dashboard exists yet** (Story 1.4). AC2's "returns to Dashboard instantly" means navigating back to `/` — currently Story 1.1's placeholder page, soon to become the real Dashboard. Don't build a fake FAB/status card/Dashboard shell in this story to make the navigation feel more complete than it is.
- **Category icon values are still placeholders** (`"food"`, `"transport"`, etc. — Story 1.1's Completion Notes already flagged this; the real icon set is Epic 2's decision). Task 9's lookup table is a pragmatic bridge, not a final design decision — don't over-invest in exact icon choices.
- **Reuse, don't duplicate:** `Category`, `CategoryRepository`, `CategoryService`, `CategoryController`, `GET /api/categories`, `CorsConfig`, `application.properties`'s datasource/timezone/CORS config, and `components/ui/button.tsx`'s conventions all already exist from Story 1.1 — extend/consume them, don't recreate parallel versions.
- **Testing conventions already established** (Story 1.1, follow exactly): `@WebMvcTest` + `@MockitoBean` (not `@MockBean` — Spring Boot 4.1 renamed it) from `org.springframework.test.context.bean.override.mockito.MockitoBean`; `@WebMvcTest` itself imports from `org.springframework.boot.webmvc.test.autoconfigure`. See `CategoryControllerTest.java` for the exact pattern to mirror.
- **Vitest + Next.js 16 / React 19 note:** async Server Components aren't supported by Vitest yet — this is exactly why Task 10 keeps `QuickAddForm` a `"use client"` component tested directly, with `app/quick-add/page.tsx` staying a trivial wrapper.
- **Money/date conventions** (already project-wide, not new to this story): all amounts `BigDecimal`/`NUMERIC(12,2)`; all dates ISO-8601 (`yyyy-MM-dd`), server-computed in `Asia/Kolkata`; no future-date validation exists anywhere in the spec — don't invent one.

### Project Structure Notes

- New backend files (under `apps/api/src/main/java/com/bmad/expensetracker/`): `entity/Transaction.java`, `repository/TransactionRepository.java`, `dto/CreateTransactionRequest.java`, `dto/TransactionDto.java`, `dto/LastUsedCategoryDto.java`, `service/TransactionService.java`, `service/CategoryNotFoundException.java`, `controller/TransactionController.java`. Modified: `config/GlobalExceptionHandler.java`.
- New frontend files (under `apps/web/`): `lib/api.ts`, `lib/category-icons.ts`, `components/ui/input.tsx`, `components/ui/label.tsx`, `components/category-icon-button.tsx`, `components/quick-add-form.tsx`, `components/quick-add-form.test.tsx`, `app/quick-add/page.tsx`, `vitest.config.mts`. Modified: `app/globals.css`, `app/page.tsx`, `package.json`.
- Modified: `.github/workflows/ci.yml` (frontend test step).
- No deviation expected from `ARCHITECTURE-SPINE.md`'s Source Tree — `entity`/`repository`/`dto`/`service`/`controller`/`config` on the backend, `app`/`components`/`lib` on the frontend, exactly as Story 1.1 established.

### References

- [Source: `_bmad-output/planning-artifacts/epics.md#Story 1.2: Manual Transaction Entry`] — original ACs
- [Source: `_bmad-output/planning-artifacts/architecture/architecture-bmad-expense-tracker-2026-07-03/ARCHITECTURE-SPINE.md`] — AD-1, AD-3, AD-4, AD-7, AD-8, AD-9, AD-10; Capability Map (FR-2, last-used-category row); Deferred (`/api/transactions/frequent` naming precedent)
- [Source: `_bmad-output/planning-artifacts/prds/prd-bmad-expense-tracker-2026-07-03/addendum.md`] — stack wiring pitfalls, testing approach guidance
- [Source: `_bmad-output/planning-artifacts/ux-designs/ux-bmad-expense-tracker-2026-07-02/DESIGN.md`] — colors, typography, radius scale, component specs (category-icon-button, quick-add-save-button, focus-ring)
- [Source: `_bmad-output/planning-artifacts/ux-designs/ux-bmad-expense-tracker-2026-07-02/EXPERIENCE.md`] — Component Patterns, State Patterns (save-fails, invalid description), Accessibility Floor, Flow 2 (manual entry walkthrough)
- [Source: `_bmad-output/planning-artifacts/ux-designs/ux-bmad-expense-tracker-2026-07-02/mockups/calm-harbor-dashboard-quickadd.html`] — composition reference (spines win on conflict; keypad shown here is illustrative, not mandated)
- [Source: `_bmad-output/implementation-artifacts/deferred-work.md`] — `GlobalExceptionHandler` 500-flattening gap, now resolved by Task 7
- [Source: `_bmad-output/implementation-artifacts/1-1-project-scaffolding-and-default-categories.md`] — established conventions (ddl-auto=update, AD-4 strict Service-owns-Repository precedent, Spring Boot 4.1 test package locations, icon placeholders)
- [Source: `_bmad-output/project-context.md#Story 1.1 Implemented`] — cross-story rules this story must not contradict

## Dev Agent Record

### Agent Model Used

claude-sonnet-5 (Claude Code)

### Debug Log References

- **`jackson-databind` not directly resolvable in the test classpath** under Spring Boot 4.1's granular `spring-boot-starter-webmvc-test` — an initial `TransactionControllerTest` sanity check that serialized a DTO directly via `ObjectMapper` failed to compile; removed it rather than add a dependency, since the same behavior is already exercised through the real controller-response `jsonPath` assertions.
- **`@vitejs/plugin-react@6.x`'s optional Rolldown integration requires `@babel/core@^8.0.0`**, conflicting with the `@babel/core@7.x` already pulled in by `shadcn`'s own toolchain — `npm install` failed with `ERESOLVE`. Pinned `@vitejs/plugin-react@5.2.0` (peers on `vite` only, no Babel 8 requirement) instead of forcing an unstable resolution with `--legacy-peer-deps`.
- **`@testing-library/react`'s auto-cleanup between tests silently never activated** — it only self-registers via a detected global `afterEach` (e.g. Vitest's `globals: true`), and this project imports test APIs explicitly instead. Every test after the first in `quick-add-form.test.tsx` inherited the previous test's already-rendered DOM (`getByRole` found duplicate elements) until `vitest.setup.ts` was given an explicit `afterEach(cleanup)`.
- **Live browser verification (Playwright) surfaced two real UI defects invisible to unit/component tests:**
  1. A Base UI dev-console warning: `Button`'s `render` prop defaults `nativeButton: true` (expects the replaced element to be a `<button>`), which doesn't fit `app/page.tsx`'s original `<Link>` (renders `<a>`). Fixing this the "recommended" way (`nativeButton={false}`) turned out to override the element's accessible role to `"button"` — semantically wrong for what is actually a real navigation link. Resolved by dropping the `Button`/`render` composition entirely for this simple case and applying `buttonVariants()` directly to a plain `<Link>`, keeping its native `"link"` role intact.
  2. The disabled Save button rendered as a low-opacity primary blue (`opacity-50` on `bg-primary`) — functionally correct but a direct violation of `DESIGN.md`'s own explicit instruction that the disabled state must be a distinct color pair (`#E5E7EB`/`#9CA3AF`), "not just low-opacity." Only visible once actually screenshotted; fixed by swapping to the exact DESIGN.md-specified disabled colors.
  3. Playwright's default `.click()` refuses to act on any element with `aria-disabled="true"`, treating it as non-actionable — this is Playwright's own actionability model, not a statement about real browsers (a real pointer click still reaches an `aria-disabled` element; only the native `disabled` attribute blocks dispatch). Verified the intended "tap disabled Save → focus moves to description" behavior (AC4) using `.click({ force: true })`.
- **Test transactions created during live browser verification were removed afterward** (`TRUNCATE TABLE transactions RESTART IDENTITY` against the local dev Postgres) so the shared dev database isn't left with placeholder rows.

### Completion Notes List

- All 14 tasks and all 7 ACs are complete and were manually verified end-to-end via a live Playwright-driven browser session against the real running backend (Spring Boot + the local dev Postgres), not just mocks: opening `/quick-add`, the initial disabled Save state (with `aria-describedby`), tapping disabled Save moving focus to the empty description field, filling amount/category/description enabling Save, backdating, saving for real (`POST /api/transactions` persisted a live row), redirecting to `/`, and — on reopening Quick Add — the last-used category (Transport) correctly pre-selected from `GET /api/transactions/last-category`, proving the server-derived default survives a real page reload, not just component state.
- Backend: 21/21 tests pass (8 pre-existing from Story 1.1 + 13 new — 6 `TransactionControllerTest` + 7 `TransactionServiceTest`). `TransactionServiceTest` wraps every test in `@Transactional` rollback specifically because, unlike `CategoryBootstrapRunnerTest`'s fixed seed data, this suite creates fresh `Transaction` rows that must not accumulate in the shared local dev database across runs.
- Frontend: 7/7 Vitest component tests pass; `npm run lint`, `npx tsc --noEmit`, and `npm run build` all clean; CI now runs `npm test` in the frontend job.
- `GET /api/transactions/last-category` and the two new `GlobalExceptionHandler` cases (`VALIDATION_ERROR`, `CATEGORY_NOT_FOUND`) are deliberate scope additions beyond `epics.md`'s literal text, following the same precedent Story 1.1 set for `GET /api/categories` — both are named explicitly in this story's Dev Notes as intentional, not scope creep.
- Task 1 wired the *full* `DESIGN.md` color palette into `globals.css`, not just the tokens this story consumes — `chip-bg` and the three `budget-*` pairs are defined but unused until Stories 1.3/1.6 need them, so those stories don't each independently re-derive the same hex values.
- Confirmed out of scope and not built, per the story's own Dev Notes: the Frequent-Expenses Shelf, any real Dashboard/FAB, and any transaction update/delete path.

### File List

**New files:**
- `apps/api/src/main/java/com/bmad/expensetracker/entity/Transaction.java`
- `apps/api/src/main/java/com/bmad/expensetracker/repository/TransactionRepository.java`
- `apps/api/src/main/java/com/bmad/expensetracker/dto/CreateTransactionRequest.java`
- `apps/api/src/main/java/com/bmad/expensetracker/dto/TransactionDto.java`
- `apps/api/src/main/java/com/bmad/expensetracker/dto/LastUsedCategoryDto.java`
- `apps/api/src/main/java/com/bmad/expensetracker/service/CategoryNotFoundException.java`
- `apps/api/src/main/java/com/bmad/expensetracker/service/TransactionService.java`
- `apps/api/src/main/java/com/bmad/expensetracker/controller/TransactionController.java`
- `apps/api/src/test/java/com/bmad/expensetracker/controller/TransactionControllerTest.java`
- `apps/api/src/test/java/com/bmad/expensetracker/service/TransactionServiceTest.java`
- `apps/web/lib/api.ts`
- `apps/web/lib/category-icons.ts`
- `apps/web/components/ui/input.tsx`
- `apps/web/components/ui/label.tsx`
- `apps/web/components/category-icon-button.tsx`
- `apps/web/components/quick-add-form.tsx`
- `apps/web/components/quick-add-form.test.tsx`
- `apps/web/app/quick-add/page.tsx`
- `apps/web/vitest.config.mts`
- `apps/web/vitest.setup.ts`

**Modified files:**
- `apps/api/src/main/java/com/bmad/expensetracker/config/GlobalExceptionHandler.java` (added `MethodArgumentNotValidException`/`CategoryNotFoundException` handlers)
- `apps/web/app/globals.css` (Calm Harbor design tokens)
- `apps/web/app/page.tsx` (temporary Quick Add entry-point link)
- `apps/web/components/ui/button.tsx` (added focus-ring offset)
- `apps/web/package.json` / `apps/web/package-lock.json` (Vitest + Testing Library devDependencies, `test` script)
- `apps/web/README.md` (documented `NEXT_PUBLIC_API_BASE_URL`)
- `.github/workflows/ci.yml` (frontend `npm test` step)
- `_bmad-output/project-context.md` (Story 1.2 learnings, added in a prior step)

**Not committed (gitignored, local dev only):**
- `apps/web/.env.local` (`NEXT_PUBLIC_API_BASE_URL=http://localhost:8080`)

## Change Log

- 2026-07-05: Implemented Story 1.2 in full: `transactions` table + `Transaction` entity/repository, `POST /api/transactions` + `GET /api/transactions/last-category`, hardened `GlobalExceptionHandler` (resolving the gap deferred from Story 1.1's review), Calm Harbor design tokens, a real `apps/web` API client, the Quick Add form (amount/date/category/description, accessible disabled-state handling, save-failure retry), and the first Vitest + Testing Library test infrastructure for the frontend. Manually verified end-to-end via a live Playwright browser session against the real backend, which caught and fixed two real UI defects (a Base UI accessible-role regression and a disabled-button color spec violation) that unit/component tests alone didn't surface. 21/21 backend tests and 7/7 frontend tests pass; lint, typecheck, and both builds are clean. Status set to `review`.
- 2026-07-05: Code review (Blind Hunter + Edge Case Hunter + Acceptance Auditor) surfaced 1 decision-needed and 9 patch findings, all resolved same-day. User decided to adopt string-typed money project-wide (amount now travels as a JSON string end to end, `@JsonFormat(shape=STRING)` on the response, `string` types on the frontend) rather than patch only this story's touchpoints. Also fixed: a malformed/cleared `transactionDate` returning `500` instead of `400`, the date field never visibly showing "Today, 5 Jul 2026" (AC3 violation), fixed-`px` type sizes instead of `DESIGN.md`'s mandated `rem`, the Save button's accessible name being the bare "✓" glyph, a `Promise.all` all-or-nothing category-load failure, missing backend overflow guards on amount/description, error handling that swallowed the real failure reason, a double-click Save race, and `todayIso()` using the browser's local timezone instead of `Asia/Kolkata`. 4 findings deferred (logged to `deferred-work.md`); 7 dismissed as noise or refuted (2 confirmed technically wrong via Hibernate/AD-5 analysis, 1 refuted via direct first-hand knowledge of the actual test runs). All fixes covered by new/updated tests and re-verified live via a second Playwright session. Full regression: 24/24 backend tests, 9/9 frontend tests, lint/typecheck/build all clean. Status set to `done`.
