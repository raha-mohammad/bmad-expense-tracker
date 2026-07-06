---
baseline_commit: f857e96d6b5e7d9654f48b2aedbdcc932069df0c
---

# Story 1.3: Frequent-Expense Chip Logging

Status: done

<!-- Note: Validation is optional. Run validate-create-story for quality check before dev-story. -->

## Story

As Sam,
I want to log a repeat purchase in one tap via a Frequent-Expenses Shelf chip,
so that I can record spending in under 5 seconds with zero typing.

## Acceptance Criteria

1. **Given** Quick Add is open, **when** the Frequent-Expenses Shelf renders, **then** it's populated from a dedicated server-side endpoint (`GET /api/transactions/frequent`) ranking Sam's habitual purchases — never derived client-side (FR-1, AD-1).
2. **Given** a chip (e.g. "☕ Coffee · ₹150"), **when** I tap it, **then** a Transaction is created immediately dated today with the chip's preset amount/Category/description, zero further input, no backdating option on this path (AD-9 — `transactionDate` omitted from the request, server stamps "today" in `Asia/Kolkata`).
3. **Given** the tap succeeds, **when** the Transaction saves, **then** the app returns to Dashboard instantly and totals/Budget Status reflect it with no loading spinner. (Dashboard's own rendering is Story 1.4/1.6's job — this story only owns "chip tap creates the row that those stories will render.")
4. **Given** the tap fails, **when** save fails, **then** the chip's values are retained, an inline retry-capable error shows (`aria-live="polite"`), and retapping retries without silently duplicating.
5. **Given** the shelf renders as a horizontally-scrollable pill shape (`bg-chip-bg`), **when** compared to Category icon buttons, **then** it's visually distinct in shape (pill vs. rounded-square, `category-icon-button.tsx`) so the two fast paths never look identical.
6. **Given** Sam has no habitual purchases yet (first use, or no Transaction has ever repeated), **when** the Frequent-Expenses Shelf would render, **then** the shelf section is hidden entirely rather than showing empty or placeholder chips — Quick Add falls back to the manual-entry path only, with no broken or perpetually-loading shelf area.
7. **Given** a chip tap is already in flight (save request pending), **when** Sam taps the same chip again (or a different chip, or manual Save) before the first request resolves, **then** the second action is a no-op — retapping never creates a duplicate Transaction.

## Tasks / Subtasks

- [x] Task 1: `FrequentExpenseDto` (new file) (AC: #1)
  - [x] `apps/api/src/main/java/com/bmad/expensetracker/dto/FrequentExpenseDto.java` — a record with fields `categoryId` (`Long`), `amount` (`BigDecimal`, annotated `@JsonFormat(shape = JsonFormat.Shape.STRING)` to match `TransactionDto.amount`'s established string-typed-money convention), `description` (`String`). No `id` field — this isn't a persisted Transaction, just a ranking result. Mirror `LastUsedCategoryDto.java`'s minimal, single-purpose style and doc-comment convention.

- [x] Task 2: Ranking query in `TransactionRepository` (AC: #1, #6)
  - [x] Add to `apps/api/src/main/java/com/bmad/expensetracker/repository/TransactionRepository.java` (currently only has `findTopByOrderByTransactionDateDescIdDesc()` — this is an UPDATE, not a new file):
    ```java
    @Query("SELECT new com.bmad.expensetracker.dto.FrequentExpenseDto(t.category.id, t.amount, t.description) "
         + "FROM Transaction t "
         + "GROUP BY t.category.id, t.amount, t.description "
         + "HAVING COUNT(t) >= 2 "
         + "ORDER BY COUNT(t) DESC, MAX(t.transactionDate) DESC")
    List<FrequentExpenseDto> findFrequentExpenseCombinations(Pageable pageable);
    ```
  - [x] This is a **story-level implementation decision** the PRD/architecture deliberately left open (`prd.md` §10 Open Question #1; `ARCHITECTURE-SPINE.md` Deferred section: "the ranking *algorithm* ... is left as an implementation detail ... resolved during `TransactionService` build without a schema change"). The decision made here: a combo counts as "habitual" once it has repeated at least once (`HAVING COUNT(t) >= 2`); ties broken by most-recently-used; capped to top 5 via `Pageable` (Task 3). Document this choice in Completion Notes — don't silently pick something different without noting it, and don't add a schema column to support it (AD-1's no-stored-totals discipline extends here: no new "frequency count" column).
  - [x] Verify this JPQL actually compiles/executes against Hibernate (constructor-expression + GROUP BY + HAVING + an aggregate-only ORDER BY column not in the SELECT list is standard SQL but worth confirming empirically via the test in Task 5 — Story 1.2 hit a real, unanticipated classpath issue with a much simpler query, so don't assume this compiles without running it).
  - [x] No entity/table changes needed — this queries existing `transactions` columns only (`category_id`, `amount`, `description`, `transaction_date`).

- [x] Task 3: `TransactionService.getFrequentExpenses()` (AC: #1, #6)
  - [x] Add to `apps/api/src/main/java/com/bmad/expensetracker/service/TransactionService.java` (UPDATE, not new file): a `private static final int FREQUENT_EXPENSE_LIMIT = 5;` constant, and:
    ```java
    public List<FrequentExpenseDto> getFrequentExpenses() {
        return transactionRepository.findFrequentExpenseCombinations(PageRequest.of(0, FREQUENT_EXPENSE_LIMIT));
    }
    ```
  - [x] Follow the exact doc-comment precedent already on `getLastUsedCategoryId()`: server-derived, never client-cached, returns an empty list (not an error, not `null`) when zero transactions or no repeated combo exists — this is what makes AC6's "hidden shelf" possible on the frontend.
  - [x] No caching layer — Consistency Conventions' "State & mutation" rule (`ARCHITECTURE-SPINE.md` line 121) requires this computed live on every call, same as derived totals.

- [x] Task 4: `TransactionController` — `GET /api/transactions/frequent` (AC: #1)
  - [x] Add to `apps/api/src/main/java/com/bmad/expensetracker/controller/TransactionController.java` (UPDATE — currently has `POST` create and `GET /last-category`, in that order):
    ```java
    @GetMapping("/frequent")
    public List<FrequentExpenseDto> getFrequentExpenses() {
        return transactionService.getFrequentExpenses();
    }
    ```
  - [x] Thin controller delegating straight to the Service (AD-4); returns a DTO list only, never entities (AD-3); no request body/params, matching the `/last-category` precedent exactly. No new exception type needed — an empty result is a valid `200`, not an error, so `GlobalExceptionHandler` needs no changes for this endpoint.

- [x] Task 5: Backend tests (AC: #1, #6) — extend existing files, don't create new test files
  - [x] `TransactionControllerTest.java` (UPDATE): `GET /api/transactions/frequent` with `TransactionService` mocked (`@MockitoBean`, per established Spring Boot 4.1 convention) to return a populated list → `200` + JSON array matching `FrequentExpenseDto`'s shape (`amount` serialized as a **string**, e.g. `"150.00"`); mocked to return an empty list → `200` + `[]` (not `404`, not an error).
  - [x] `TransactionServiceTest.java` (UPDATE, real repository + `@Transactional` rollback per existing pattern — this suite creates real rows that must not accumulate in the shared dev DB): seed transactions where one (category, amount, description) combo repeats ≥2 times and another appears only once → `getFrequentExpenses()` returns only the repeating combo, not the one-off; seed 6+ distinct qualifying combos with different counts → result is capped at 5, ordered by count descending; empty `transactions` table → returns `[]` (not `null`, not an exception).

- [x] Task 6: `lib/api.ts` additions (AC: #1) — UPDATE, not new file
  - [x] Add `export interface FrequentExpenseDto { categoryId: number; amount: string; description: string; }` (mirrors the backend's string-typed-money convention exactly — same shape family as `LastUsedCategoryDto`/`TransactionDto` already in this file).
  - [x] Add `export function getFrequentExpenses(): Promise<FrequentExpenseDto[]> { return request<FrequentExpenseDto[]>("/api/transactions/frequent"); }` — identical shape to the existing `getLastUsedCategory()` wrapper (no params, GET, reuses the shared `request<T>` helper which already unwraps the AD-8 `{error:{code,message}}` shape and throws on non-2xx/network failure).

- [x] Task 7: `FrequentExpenseShelf` component (new file) (AC: #1, #5, #6)
  - [x] `apps/web/components/frequent-expense-shelf.tsx` — a presentational component modeled on `category-icon-button.tsx`'s composition pattern (typed props interface, plain `<button type="button">`, `cn()` for conditional classes, `getCategoryIcon()` reuse):
    ```tsx
    interface FrequentExpenseShelfProps {
      chips: FrequentExpenseDto[];
      categories: CategoryDto[];
      disabled: boolean;
      onTap: (chip: FrequentExpenseDto) => void;
    }
    ```
  - [x] Resolve each chip's `categoryId` against the already-fetched `categories` prop (passed down from `quick-add-form.tsx` — **do not** re-fetch categories inside this component) to get the icon key and category name. Filter out any chip whose `categoryId` isn't found in `categories` (defensive; category deletion doesn't exist until Epic 2, but costs nothing to guard, matching the "at least one category always exists" precedent's spirit without assuming it holds for arbitrary category IDs).
  - [x] **If the resolvable chip list is empty, render `null`** — this is the literal mechanism for AC6 ("hidden entirely," not an empty/placeholder state). Do this check inside the component itself (single source of truth) rather than in the parent.
  - [x] Visual spec (`DESIGN.md`): pill shape via `rounded-full`, `bg-chip-bg` (`#EFF6FF`, already defined in `globals.css`/`@theme inline` but **currently unused anywhere in the codebase** — this is the first consumer), text color `text-card-foreground`. Focus ring: `focus-visible:ring-3 focus-visible:ring-ring/50 focus-visible:ring-offset-2` (same classes already used on `category-icon-button.tsx` and form inputs — reuse verbatim, don't reinvent). Chip label text role isn't specified in `DESIGN.md` (only the shelf's section-eyebrow label is, at 12px/bold) — use `text-sm` (`body`, 14px) for the chip's own text by elimination, per the UX gap noted during research.
  - [x] **Disabled/in-flight state:** use the native `disabled` HTML attribute (epics.md's AC7 literally sanctions "button disabled" as an acceptable mechanism for chips — unlike Story 1.2's Save button, which specifically needed `aria-disabled` to keep receiving taps for its own AC4 focus-move requirement; chips have no equivalent need). **Do not use `opacity-50` for the disabled visual** — `DESIGN.md`'s disabled-state rule ("a real distinct color pair, `#E5E7EB`/`#9CA3AF`, not just low-opacity") was a Story 1.2 code-review finding for the Save button and has no chip-specific carve-out in the spec; extend the same literal color pair here (`disabled:bg-[#E5E7EB] disabled:text-[#9CA3AF]`) rather than repeating the opacity mistake.
  - [x] Section eyebrow label above the chip row: `"Frequent — tap to log instantly"` (exact copy from `DESIGN.md`'s Typography section example), styled with the `label` role (12px, bold, `uppercase tracking-wide text-muted-foreground` — matching the existing Category section label's classes in `quick-add-form.tsx` for visual consistency).
  - [x] Chip content: category icon (`getCategoryIcon(category.icon)`, `aria-hidden`) + `{description} · {formatted amount}`. **New small utility needed** — no INR currency-formatting helper exists anywhere in `apps/web` yet (verified: zero matches for `Intl.NumberFormat`/`toLocaleString` in the codebase). Add `formatInrAmount(amount: string): string` to `apps/web/lib/utils.ts` (UPDATE — currently just exports `cn()`) using `Intl.NumberFormat("en-IN", { style: "currency", currency: "INR", maximumFractionDigits: Number.isInteger(Number(amount)) ? 0 : 2 })`, so a whole-rupee chip reads "₹150" (matching the epics.md/PRD example literally) while a paise amount still renders correctly. **This is the first money-display formatting in the frontend — Stories 1.4, 1.6, and 3.1 will also need Indian digit grouping (`project-context.md`'s currency rule) and should reuse this function, not reinvent it.**
  - [x] Each chip's React `key` and tap-identity: no `id` exists on `FrequentExpenseDto` (it's a ranking result, not a persisted row) — use a derived composite key, e.g. `` `${chip.categoryId}-${chip.amount}-${chip.description}` ``.

- [x] Task 8: Wire the shelf into `quick-add-form.tsx` (AC: #2, #3, #4, #6, #7) — UPDATE, this is the central integration point
  - [x] Add state: `const [frequentExpenses, setFrequentExpenses] = useState<FrequentExpenseDto[] | null>(null);`
  - [x] Fetch `getFrequentExpenses()` in the existing mount `useEffect`, **as an independent sibling call**, not nested under the `getCategories()`/`getLastUsedCategory()` chain and not combined via `Promise.all` — this repeats the exact lesson from Story 1.2's code review ("don't let an optional enhancement block the essential path"): if this call fails, `.catch()` sets `frequentExpenses` to `[]` (hides the shelf via Task 7's own empty-check), it does **not** set `loadError` (that flag is reserved for the essential category-load failure). Respect the existing `cancelled` closure-guard pattern for unmount safety.
  - [x] Add a shared tap handler:
    ```ts
    async function handleChipTap(chip: FrequentExpenseDto) {
      if (isSavingRef.current) return; // AC7: no-op if ANY save (chip or manual) is already in flight
      isSavingRef.current = true;
      setIsSaving(true);
      setSaveError(false);
      try {
        await createTransaction({
          amount: chip.amount,
          categoryId: chip.categoryId,
          description: chip.description,
          transactionDate: undefined, // AC2: no backdating on this path — server stamps today (AD-9)
        });
        router.push("/");
      } catch (err) {
        console.error("Failed to save frequent expense", err);
        setSaveError(true);
      } finally {
        isSavingRef.current = false;
        setIsSaving(false);
      }
    }
    ```
  - [x] **Reuse the existing `isSavingRef`/`isSaving`/`saveError` state wholesale — do not introduce a second, parallel in-flight-guard mechanism for chips.** This is a deliberate simplification beyond AC7's literal minimum (which only requires guarding *the same* chip): one global in-flight guard also blocks manual Save while a chip-tap is pending and vice versa, which is simpler to reason about and test than per-chip guards, and matches this app's "one save operation at a time" nature. `isSavingRef` is exactly the synchronous ref-based guard Story 1.2's code review established (a `useState`-only guard has a race window before React commits the re-render) — apply that same precedent here instead of re-deriving it.
  - [x] Reuse the existing `aria-live="polite"` `saveError` region for chip-tap failures too — same copy ("Couldn't save — check your connection."), same mechanism (AD-8's `{error:{code,message}}` shape already unwrapped by `lib/api.ts`'s `request()`). Don't add a second error banner.
  - [x] Render placement: `<FrequentExpenseShelf chips={frequentExpenses ?? []} categories={categories ?? []} disabled={isSaving} onTap={handleChipTap} />`, gated on `categories != null && frequentExpenses != null` (need categories loaded to resolve chip icons/labels). Place it directly below the `<h1>Add Expense</h1>` heading, above the Amount field — the only layout signal in the UX spec is `EXPERIENCE.md` Flow 2's "ignores the shelf, taps the keypad" phrasing, which implies the shelf is seen first, with the manual-entry form following. (Neither `DESIGN.md` nor `EXPERIENCE.md` states this explicitly in prose — `mockups/calm-harbor-dashboard-quickadd.html` is the authoritative composition reference if this placement needs to be revisited; the mockup "wins on conflict" per `EXPERIENCE.md`.)
  - [x] Do **not** touch the existing manual-entry `handleSave` logic beyond what's needed to share `isSavingRef`/`isSaving`/`saveError` — it already works and is fully tested; this story adds a second, independent path to the same save action, not a rewrite of the first.

- [x] Task 9: Frontend tests (AC: #1, #2, #3, #4, #5, #6, #7)
  - [x] New file `apps/web/components/frequent-expense-shelf.test.tsx` (mirrors `quick-add-form.test.tsx`'s Vitest + Testing Library conventions — explicit `vi.mock`, `beforeEach` resets, `getByRole`/`aria-*` assertions, relies on the shared `vitest.setup.ts` `afterEach(cleanup)`): renders nothing (`container.firstChild` is `null` or equivalent) when `chips` is `[]`; renders one button per chip with the expected accessible name/text (icon + description + formatted amount, e.g. "Coffee · ₹150"); a chip with a `categoryId` not present in `categories` is silently omitted, not crashed on; tapping an enabled chip calls `onTap` with that exact chip; `disabled={true}` renders native-`disabled` buttons that don't fire `onTap` on click.
  - [x] `quick-add-form.test.tsx` (UPDATE): add `getFrequentExpenses` to the existing `vi.mock("@/lib/api", () => ({ ... }))` block (**required** — once the component calls it, the existing mocked module will throw `undefined is not a function` on every test unless this is added) with a `beforeEach` default `getFrequentExpensesMock.mockReset().mockResolvedValue([])` (so existing tests, which know nothing about chips, keep passing unmodified). Add new cases: tapping a frequent-expense chip (mock resolves a non-empty array) calls `createTransaction` with the chip's exact preset values and navigates to `/` on success; a second tap (on the same chip or a different one, or manual Save) while the first chip-tap's promise is still pending is a no-op — model this test directly on the existing "double-clicking Save before the request resolves creates only one transaction" test, since it's an explicit template for this exact scenario; the shelf section is absent from the rendered form when `getFrequentExpenses` resolves `[]` (no frequent-expense chips yet, AC6).

### Review Findings

- [x] [Review][Patch] **[Decision resolved: normalize description matching (case-fold + trim), display most-recently-used casing]** The ranking query's `GROUP BY t.category.id, t.amount, t.description` requires a byte-identical description match — "Coffee" and "coffee " (trailing space, different case) would never accumulate `COUNT(t) >= 2` together even though they're the same real-world repeat purchase. User decided: group by a normalized key (`LOWER(TRIM(t.description))`), but the chip's displayed/replayed description should be the exact literal string from the most-recently-dated transaction in that group (consistent with the existing `transactionDate DESC, id DESC` "most recent wins" convention already used by `findTopByOrderByTransactionDateDescIdDesc()`). Implementation: split into two repository queries — an aggregate query grouped by the normalized key (returning an internal `FrequentExpenseGroup` projection, not the public DTO), and a second lookup that resolves each group's normalized key back to its single most-recent transaction's real `description`. [`apps/api/src/main/java/com/bmad/expensetracker/repository/TransactionRepository.java`, `apps/api/src/main/java/com/bmad/expensetracker/service/TransactionService.java`]
- [x] [Review][Patch] **Ranking query's `ORDER BY` has no final deterministic tiebreaker** [`apps/api/src/main/java/com/bmad/expensetracker/repository/TransactionRepository.java`] — `ORDER BY COUNT(t) DESC, MAX(t.transactionDate) DESC` can leave two or more combinations tied on both count and most-recent-use date. SQL gives no guarantee which tied combo lands inside the top-5 window versus just outside it, so the shelf's chip set could nondeterministically flicker between page loads with zero underlying data change. None of the current tests seed a tie (all use strictly decreasing counts), so this has zero coverage. Fix folded into the same query rewrite above: add `MAX(t.id) DESC` as a final deterministic tiebreaker (unique per group, since a transaction belongs to exactly one group).
- [x] [Review][Patch] **`formatInrAmount()`'s actual purpose — Indian lakh/crore digit grouping — is never tested** [`apps/web/lib/utils.ts`, `apps/web/components/frequent-expense-shelf.test.tsx`] — every test amount used (₹150, ₹40) is small enough to render identically whether Indian or Western digit grouping is applied. Add a direct unit test asserting e.g. `formatInrAmount("150000")` → `"₹1,50,000"`, so a regression in the actual grouping logic would be caught.
- [x] [Review][Patch] **Chip disabled-state colors are duplicated magic hex instead of a shared token** [`apps/web/components/frequent-expense-shelf.tsx`, `apps/web/components/quick-add-form.tsx`] — `bg-[#E5E7EB] text-[#9CA3AF]` is now hardcoded as inline arbitrary-value classes in two separate components, contradicting this story's own repeated "reuse, don't duplicate" principle. Extract to a shared token (e.g. `--disabled-bg`/`--disabled-foreground` in `globals.css`, alongside the existing `chip-bg`/`category-selected-bg` tokens) and reference it from both components.
- [x] [Review][Patch] **`formatInrAmount()` has no guard against a non-numeric input** [`apps/web/lib/utils.ts`] — `Number(amount)` on a malformed string silently renders `"₹NaN"` instead of a sane fallback. Low risk today (the only caller's data is always a validated `NUMERIC(12,2)` from the database), but this is a newly shared utility explicitly flagged for reuse by Stories 1.4/1.6/3.1, whose callers haven't been written yet — add `if (Number.isNaN(value)) return amount;` (or similar) before those callers exist.
- [x] [Review][Patch] **Inconsistent test rigor between the two new controller tests** [`apps/api/src/test/java/com/bmad/expensetracker/controller/TransactionControllerTest.java`] — `getFrequentExpensesReturnsRankedList` asserts `content().contentType(MediaType.APPLICATION_JSON)`; its sibling `getFrequentExpensesReturnsEmptyListWhenNoneHabitual` (same endpoint) does not. Add the same content-type assertion to the empty-list test for consistency.
- [x] [Review][Patch] **Task 9's own enumerated no-op scenarios are only partially tested** [`apps/web/components/quick-add-form.test.tsx`] — Task 9 explicitly specifies a second tap "on the same chip **or a different one, or manual Save**" while a chip-tap is in flight should no-op. Only the same-chip case has a test; "different chip during an in-flight chip tap" and "manual Save during an in-flight chip tap" are untested, even though the shared `isSavingRef` guard in the actual code already correctly covers all three (confirmed by code inspection). Add the two missing test cases.

- [x] [Review][Defer] **AC5's visual distinctness was never verified with a real rendered view** [`apps/web/components/frequent-expense-shelf.tsx`] — deferred, no browser-automation tool was available this session; only logic (JSDOM/Vitest) and a live `curl`-based backend check were done. Already flagged in the story's own Dev Agent Record and `project-context.md` for a future spot-check once browser tooling is available.
- [x] [Review][Defer] **Unbounded full-table aggregation on every Quick Add load, no index/window discussion** [`apps/api/src/main/java/com/bmad/expensetracker/repository/TransactionRepository.java`] — deferred, this matches AD-1's explicit "no caching, always computed live" mandate and is negligible at this app's single-user personal scale; revisit only if real dogfooding usage ever shows a real performance problem.
- [x] [Review][Defer] **"Habitual" has no recency window or decay** [`apps/api/src/main/java/com/bmad/expensetracker/repository/TransactionRepository.java`] — deferred, the PRD/architecture explicitly left the ranking algorithm's time window as an open implementation detail; this story made a documented, deliberate "all-time, no decay" choice within that discretion. Worth reconsidering only if real usage shows stale chips.

**Dismissed as noise or refuted (4):**
- `getFrequentExpenses()` resolving to a non-array crashing `chips.map()` — structurally unreachable: `TransactionController`'s typed `List<FrequentExpenseDto>` return is always serialized as a JSON array (empty or populated) by Spring, and Spring Data JPA's `List`-returning finder methods never return `null`.
- Null/blank `description` producing a "fake" habitual chip — unreachable: `CreateTransactionRequest.description` is `@NotBlank`-validated on every write path (manual entry's client-side gating and the backend's own validation), so a blank description can never be persisted in the first place.
- Frontend test fixtures using `"150"` instead of `"150.00"` — not an actual bug: `formatInrAmount()` parses via `Number(...)`, which behaves identically for both representations; a test-realism nitpick with zero behavioral risk.
- "Bookkeeping bloat / overconfident tone" across the story file and `project-context.md` — a subjective critique of documentation verbosity/tone, not a functional defect in the diff; nothing to patch in code.

## Dev Notes

- **Architecture is final — every `AD-n` below is a hard constraint** [Source: `ARCHITECTURE-SPINE.md`]:
  - **AD-1** — no stored total/count column backing the ranking; it's a live `GROUP BY` query every call, never cached.
  - **AD-2** — `apps/web` → `apps/api` only via `/api/*` REST/JSON.
  - **AD-3** — `GET /api/transactions/frequent` returns `FrequentExpenseDto`, never a `Transaction`/`Category` entity.
  - **AD-4** — Controller → Service → Repository; the ranking query lives in `TransactionRepository`, called only from `TransactionService`, never from `TransactionController` directly.
  - **AD-7** — `amount` is `BigDecimal`/`NUMERIC(12,2)` end to end; per the Story 1.2 code-review decision (not in the spine's own text, but binding project-wide per `project-context.md`), the wire representation is a JSON **string** (`@JsonFormat(shape=STRING)`), matching `TransactionDto.amount` exactly.
  - **AD-8** — no new error path needed for this endpoint (a read with no invalid-input surface); reuse the existing `GlobalExceptionHandler`/`{error:{code,message}}` shape only for the chip-tap's `POST /api/transactions` failure path, which already works unmodified.
  - **AD-9** — chip taps omit `transactionDate` entirely (exactly like manual entry's default-to-today path); the server stamps "today" in `Asia/Kolkata` via the already-existing `TransactionService.createTransaction()` — **no changes needed there**, the chip path reuses it as-is.
  - **AD-10** — chip taps create a new row via the existing `POST /api/transactions`; never an update.
  - **Capability Map** (line 218): `FR-1 | apps/web Quick Add + apps/api TransactionService | AD-1, AD-2, AD-7`.
  - **Deferred section**, quoted exactly: *"Frequent-Expenses Shelf ordering/cap mechanism — the ranking algorithm (top-N by frequency, trailing window, tie-breaks) is left as an implementation detail per the PRD (§10), resolved during `TransactionService` build without a schema change. What's fixed now, not deferred: it is computed server-side by a dedicated endpoint (e.g. `GET /api/transactions/frequent`), never derived client-side."* This story makes that implementation decision (Task 2/3) — record it, don't leave it ambiguous for the next reader.
  - **No idempotency/double-submission protection exists at the architecture level for `transactions`** (unlike AD-6's `period_start` UNIQUE constraint for budgets — that mechanism has no analog here, since two identical purchases on the same day are legitimate). Double-tap protection is **UI-level only** (Task 8), same as Story 1.2 established for its own Save button.
- **FR/Story coverage:** FR-1 → this story (sole mapping); FR-3 (period assignment / `Asia/Kolkata`) → both 1.2 and 1.3, since both paths funnel through the same `TransactionService.createTransaction()`.
- **UX tokens for the chip** [Source: `DESIGN.md`]: `chip-bg` (`#EFF6FF`) and `rounded-full` are defined in `globals.css` already (Story 1.2 wired the *full* token set in anticipation of this story) but **unused anywhere until this story** — first consumer. No chip-specific tap-target minimum, disabled-state color, or text type-role is specified in `DESIGN.md`/`EXPERIENCE.md` — this story's Tasks make explicit, documented decisions for each (extend the Save button's disabled pair; use `body`/`text-sm` for chip text) rather than leaving them for the dev agent to guess inconsistently.
- **Accessibility** [Source: `EXPERIENCE.md`]: chips are real, focusable `<button>` elements (never `<div onclick>`), keyboard-operable, with the standard `{colors.ring}` focus-visible treatment already used everywhere else in Quick Add. The chip-tap failure path reuses the existing `aria-live="polite"` region (`EXPERIENCE.md`'s aria-live list explicitly includes "save failure," and chip-tap is just another save action funneling through the same state).
- **Testing** [Source: `ARCHITECTURE-SPINE.md` Consistency Conventions, "Testing" row]: `@WebMvcTest` for the controller, Vitest/Testing Library for the "Quick Add" component scope (explicitly named) — the shelf falls under that scope. The ranking query isn't literally "derived-totals or budget-status calculation" (the only logic the spine names as needing a *direct* unit test), but it's the same class of server-computed-not-cached logic AD-1 protects — give it one anyway (Task 5).
- **No new dependency/library is introduced by this story** — reuses the already-pinned stack (`@Query`/`Pageable`-based Spring Data JPA queries, Vitest/Testing Library, `lucide-react`). No web research flagged anything version-specific to verify beyond what Story 1.1/1.2 already surfaced (granular Spring Boot 4.1 starters, `@MockitoBean`, `org.springframework.boot.webmvc.test.autoconfigure` package paths — already correctly used in the existing test files this story extends).
- **Reuse, don't duplicate:** `TransactionService.createTransaction()`, `lib/api.ts`'s `request()` helper, `getCategoryIcon()`, the existing `isSavingRef` double-tap-guard pattern, the existing `aria-live` error region, and `category-icon-button.tsx`'s component-composition style all already exist — extend/consume them exactly as Task 8 describes, don't build parallel versions.
- **Previous story (1.2) explicitly deferred this scope**: its own Dev Notes state *"The Frequent-Expenses Shelf is explicitly out of scope for this story (Story 1.3 / FR-1) ... Don't build shelf UI, even hidden/empty-state, since there's no `GET /api/transactions/frequent` endpoint yet either."* — confirming Story 1.2's `quick-add-form.tsx` contains zero shelf-related code today; everything in Tasks 6–8 is new integration, not a partial implementation to complete.
- **What's still out of scope here, for later stories:** the real Dashboard (`app/page.tsx` stays Story 1.1's placeholder — chip-tap's `router.push("/")` lands there, same as manual Save today, per Story 1.4); actual Budget Status rendering (Story 1.6); Search & Filter (Epic 3). Don't build any of these to make the chip-tap flow "feel" more complete than it is.

### Project Structure Notes

- **New files:**
  - `apps/api/src/main/java/com/bmad/expensetracker/dto/FrequentExpenseDto.java`
  - `apps/web/components/frequent-expense-shelf.tsx`
  - `apps/web/components/frequent-expense-shelf.test.tsx`
- **Modified files:**
  - `apps/api/src/main/java/com/bmad/expensetracker/repository/TransactionRepository.java` (ranking query)
  - `apps/api/src/main/java/com/bmad/expensetracker/service/TransactionService.java` (`getFrequentExpenses()`)
  - `apps/api/src/main/java/com/bmad/expensetracker/controller/TransactionController.java` (`GET /frequent`)
  - `apps/api/src/test/java/com/bmad/expensetracker/controller/TransactionControllerTest.java`
  - `apps/api/src/test/java/com/bmad/expensetracker/service/TransactionServiceTest.java`
  - `apps/web/lib/api.ts` (`FrequentExpenseDto` type, `getFrequentExpenses()`)
  - `apps/web/lib/utils.ts` (`formatInrAmount()`)
  - `apps/web/components/quick-add-form.tsx` (shelf integration, shared save-guard reuse)
  - `apps/web/components/quick-add-form.test.tsx` (mock addition, new test cases)
- **No deviation** from `ARCHITECTURE-SPINE.md`'s Source Tree — same `controller`/`service`/`repository`/`dto` split on the backend, same `components`/`lib` split on the frontend, exactly as Stories 1.1/1.2 established. `GlobalExceptionHandler.java` needs no change (no new error path introduced).

### References

- [Source: `_bmad-output/planning-artifacts/epics.md#Story 1.3: Frequent-Expense Chip Logging`] — all 7 ACs verbatim (the last two — empty-shelf, double-tap — were added during the 2026-07-03 implementation-readiness review); FR Coverage Map (FR1→this story); "Additional Requirements" naming `GET /api/transactions/frequent` and the `/api/transactions/{sub-resource}` naming convention.
- [Source: `_bmad-output/planning-artifacts/prds/prd-bmad-expense-tracker-2026-07-03/prd.md`] — FR-1 (§5.1) and its Consequences; UJ-1 (§2.3); Glossary definitions of "Frequent-Expenses Shelf"/"Chip"/"Quick Add"; Open Question #1 (§10, ranking/cap explicitly undecided); FR-2's Notes on the required-description/chip-preset reconciliation.
- [Source: `_bmad-output/planning-artifacts/architecture/architecture-bmad-expense-tracker-2026-07-03/ARCHITECTURE-SPINE.md`] — AD-1, AD-2, AD-3, AD-4, AD-7, AD-8, AD-9, AD-10; Capability Map FR-1 row; Deferred section's `/api/transactions/frequent` paragraph (quoted in full above); Consistency Conventions' Testing row and State & mutation row; Source Tree.
- [Source: `_bmad-output/planning-artifacts/ux-designs/ux-bmad-expense-tracker-2026-07-02/DESIGN.md`] — `chip-bg` token, `rounded.full`, frequent-expense-chip component spec (pill shape, visually distinct from category buttons), typography roles, disabled-color-pair precedent.
- [Source: `_bmad-output/planning-artifacts/ux-designs/ux-bmad-expense-tracker-2026-07-02/EXPERIENCE.md`] — Component Patterns row (chip behavior, no date edit), State Patterns "No frequent-expense chips yet" row, Accessibility Floor (focusable buttons, aria-live), Flow 1 (chip-tap walkthrough) and Flow 2 (shelf-then-form ordering implication).
- [Source: `_bmad-output/implementation-artifacts/1-2-manual-transaction-entry.md`] — established conventions this story must follow: string-typed money, `isSavingRef` double-tap-guard pattern, `aria-live` error region, independent (non-`Promise.all`) optional-fetch pattern, `@MockitoBean`/`org.springframework.boot.webmvc.test.autoconfigure` test conventions, explicit confirmation that Story 1.2 built zero shelf-related code.
- [Source: `_bmad-output/implementation-artifacts/deferred-work.md`] — no items from prior reviews are directly relevant to this story's scope (all deferred items concern manual-entry-only edge cases).
- [Source: `_bmad-output/project-context.md#Story 1.2 Code-Reviewed and Done`] — the project-wide string-typed-money decision and its rationale.

## Dev Agent Record

### Agent Model Used

claude-sonnet-5 (Claude Code)

### Debug Log References

- Backend: `docker compose ps` initially failed (Docker Desktop daemon not running); started Docker Desktop, waited for the `postgres` service healthcheck to report `healthy`, then ran `mvnw test` — all 29 backend tests passed on the first run, including the new JPQL ranking query (constructor-expression + `GROUP BY` + `HAVING` + an aggregate-only `ORDER BY` column not in the `SELECT` list), confirming the query Task 2 flagged as "worth verifying empirically" compiles and executes correctly against Hibernate/PostgreSQL 18.4 with no changes needed.
- Live end-to-end verification against the real running backend + Postgres (no browser-automation tool was available this session, so this substitutes for the Playwright verification prior stories performed): started `mvnw spring-boot:run`, seeded real transactions via `curl` (`Coffee`/₹150 ×3, `Bus fare`/₹40 ×2, a `One-off purchase` ×1), then `GET /api/transactions/frequent` returned exactly `[{"categoryId":1,"amount":"150.00","description":"Coffee"},{"categoryId":2,"amount":"40.00","description":"Bus fare"}]` — correct ranking order, the one-off correctly excluded, and `amount` correctly serialized as a JSON string. Cleaned up afterward with `TRUNCATE TABLE transactions RESTART IDENTITY` (same precedent as Story 1.2) and stopped the temporary server process.
- Frontend: `npm test` (17/17 passed, up from 9 — 3 new cases in `quick-add-form.test.tsx`, 5 new in `frequent-expense-shelf.test.tsx`), `npm run lint` (one `eslint-disable` directive copied from `category-icon-button.tsx` turned out to be unnecessary here and was removed), `npx tsc --noEmit` (clean), `npm run build` (clean, `/quick-add` prerendered successfully as static content with the new shelf integrated).
- **Code review round**: adversarial review (Blind Hunter + Edge Case Hunter + Acceptance Auditor) surfaced 1 decision-needed and 6 patch findings, all resolved same-day. Re-ran `mvnw test` (31/31, up from 29) and `npm test` (23/23, up from 17) after applying all patches — both clean. Re-verified the normalized-matching fix live: seeded "Coffee" then " coffee " (different case/whitespace) via `curl` against the real running backend + Postgres — `GET /api/transactions/frequent` correctly grouped them as one repeated combo and displayed the most-recently-logged literal casing (" coffee "). Cleaned up test data and stopped the temporary server afterward, same as the initial implementation pass.

### Completion Notes List

- All 9 tasks and all 7 ACs are complete. Backend: 31/31 tests pass. Frontend: 23/23 Vitest tests pass; lint, typecheck, and build all clean.
- **Ranking algorithm decision (Task 2), confirmed working exactly as designed via both automated tests and live verification against the real database**: a `(category, amount, description)` combination is "habitual" once it has repeated at least once (`HAVING COUNT(t) >= 2`), ranked by descending frequency, capped to the top 5 (`FREQUENT_EXPENSE_LIMIT` in `TransactionService`). No schema change — a live `GROUP BY`/`HAVING` query every call, per AD-1.
- **New shared frontend utility**: `formatInrAmount()` added to `apps/web/lib/utils.ts` — Indian digit grouping via `Intl.NumberFormat("en-IN", ...)`, no decimals for whole-rupee amounts (matches the product's "₹150" example copy), still shows paise when present. Flagged in-code for Stories 1.4/1.6/3.1 to reuse rather than reinvent.
- **Double-tap guard (AC7) is a single shared `isSavingRef`/`isSaving`/`saveError` state reused wholesale between `handleSave` (manual entry) and the new `handleChipTap`** — deliberately broader than AC7's literal minimum (one global in-flight guard blocks manual Save and every chip while any one save is pending, rather than a per-chip guard).
- **Both optional fetches (`getLastUsedCategory`, `getFrequentExpenses`) fail independently and silently degrade** (no last-used category → falls back to first category; no frequent expenses → shelf stays hidden) — neither can block the essential `getCategories()` path, per the `Promise.all` lesson from Story 1.2's code review.
- **[Review] Description matching is normalized (case-fold + trim)**, resolved via a user decision during code review: the ranking query now groups by `LOWER(TRIM(t.description))` (a new internal `FrequentExpenseGroup` projection, not the public DTO), and `TransactionService` resolves each group's displayed description via a second query (`findMostRecentByNormalizedDescription`) to the exact literal casing of that group's single most-recently-dated transaction — consistent with the existing `transactionDate DESC, id DESC` "most recent wins" convention. The ranking query's `ORDER BY` also gained a final deterministic tiebreaker (`MAX(t.id) DESC`), fixing a real gap where two combinations tied on both count and most-recent-use date had no guaranteed stable order.
- **[Review] Disabled-state color pair (`#E5E7EB`/`#9CA3AF`) extracted to shared `globals.css` tokens** (`--disabled-bg`/`--disabled-foreground`, exposed as `bg-disabled`/`text-disabled-foreground`) — both `quick-add-form.tsx`'s Save button and `frequent-expense-shelf.tsx`'s chips now reference the same token instead of each hardcoding the hex pair independently.
- **[Review] `formatInrAmount()` hardened**: added a `Number.isNaN` guard (falls back to the raw string instead of rendering `"₹NaN"`) and a dedicated `apps/web/lib/utils.test.ts` exercising the function's actual reason for existing — Indian lakh/crore digit grouping on large amounts (e.g. `"150000"` → `"₹1,50,000"`), previously untested since every prior fixture used small amounts.
- No deviation from the story's own plan was needed on the initial pass — the JPQL query, the shared in-flight guard, and the shelf's hide-when-empty behavior all worked as specified. The review round's normalized-matching rewrite was the only substantive design change, and it was a user-directed decision, not a bug fix to prior work.

### File List

**New files:**
- `apps/api/src/main/java/com/bmad/expensetracker/dto/FrequentExpenseDto.java`
- `apps/api/src/main/java/com/bmad/expensetracker/repository/FrequentExpenseGroup.java`
- `apps/web/components/frequent-expense-shelf.tsx`
- `apps/web/components/frequent-expense-shelf.test.tsx`
- `apps/web/lib/utils.test.ts`

**Modified files:**
- `apps/api/src/main/java/com/bmad/expensetracker/repository/TransactionRepository.java`
- `apps/api/src/main/java/com/bmad/expensetracker/service/TransactionService.java`
- `apps/api/src/main/java/com/bmad/expensetracker/controller/TransactionController.java`
- `apps/api/src/test/java/com/bmad/expensetracker/controller/TransactionControllerTest.java`
- `apps/api/src/test/java/com/bmad/expensetracker/service/TransactionServiceTest.java`
- `apps/web/lib/api.ts`
- `apps/web/lib/utils.ts`
- `apps/web/app/globals.css`
- `apps/web/components/quick-add-form.tsx`
- `apps/web/components/quick-add-form.test.tsx`

## Change Log

- 2026-07-06: Implemented Story 1.3 in full: `GET /api/transactions/frequent` (new `FrequentExpenseDto`, a ranking `@Query` on `TransactionRepository`, `TransactionService.getFrequentExpenses()`, a thin controller method), a new `FrequentExpenseShelf` frontend component (pill-shaped chips, hidden entirely when empty), and full `quick-add-form.tsx` integration (independent non-blocking fetch, a `handleChipTap` action reusing the existing double-tap-guard/error-state machinery). Verified live end-to-end against the real backend + Postgres via seeded `curl` requests (no browser-automation tool available this session). 29/29 backend tests, 17/17 frontend tests, lint/typecheck/build all clean. Status set to `review`.
- 2026-07-06: Code review (Blind Hunter + Edge Case Hunter + Acceptance Auditor) surfaced 1 decision-needed and 6 patch findings, all resolved same-day. User decided description matching should be normalized (case-fold + trim) rather than left exact-match — the ranking query was rewritten into two steps (an aggregate grouped by normalized description via a new internal `FrequentExpenseGroup` projection, plus a lookup resolving each group to its most-recently-used literal description), which also fixed a non-deterministic tiebreaker gap in the same `ORDER BY`. Also fixed: `formatInrAmount()`'s actual Indian-grouping behavior was untested (added `apps/web/lib/utils.test.ts`) and had no non-numeric-input guard; the chip's disabled color was duplicated inline hex instead of a shared `globals.css` token; one controller test was missing a content-type assertion its sibling had; and Task 9's own "different chip"/"manual Save" no-op scenarios were untested despite the code already handling them. 3 findings deferred (visual verification without browser tooling, unbounded aggregation query cost, no recency window on "habitual") — all logged to `deferred-work.md`. 4 findings dismissed as noise or structurally unreachable given existing validation/type guarantees. All fixes covered by new/updated tests and re-verified live via `curl` against the real backend. Full regression: 31/31 backend tests, 23/23 frontend tests, lint/typecheck/build all clean. Status set to `done`.
