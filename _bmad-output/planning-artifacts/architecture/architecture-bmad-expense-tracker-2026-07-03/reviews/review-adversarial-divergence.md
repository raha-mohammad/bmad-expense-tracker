---
title: Adversarial Divergence Review — ARCHITECTURE-SPINE.md
target: _bmad-output/planning-artifacts/architecture/architecture-bmad-expense-tracker-2026-07-03/ARCHITECTURE-SPINE.md
method: 'construct two spine-compliant units one level down that build incompatibly'
created: 2026-07-03
status: draft
---

# Adversarial Divergence Review — bmad-expense-tracker Architecture Spine

## Method

For each candidate gap, this review constructs **two units** (imagine two future stories/epics, built by two different agents who each read only `ARCHITECTURE-SPINE.md` and its bound PRD/UX sources) that each satisfy every AD **to the letter**, yet produce incompatible systems — a clashing shared-data shape, two owners of one entity, conflicting mutation paths, or wording that supports two different implementations. Every genuine pair is a hole in the spine, not a hole in the imagined builders' competence.

Grounding checked beyond the spine itself: `prd.md`, `addendum.md`, `EXPERIENCE.md`, `project-context.md`. None of them mention a system timezone, a DB unique constraint strategy, or a request-level concurrency control — so gaps found here are not resolved elsewhere in the artifact set; they are real spine gaps.

---

## Finding 1 — AD-6: no single owner of "create the Period row," no concurrency control [HIGH]

**AD text:** "When a new Period's Dashboard is first loaded and no row exists for it yet, the Service creates one by copying the amount from the most recent prior Period that has one (if any)."

**Unit A — Dashboard/BudgetService builder.** Implements the copy-forward inside the method backing `GET /api/budget/status` (the endpoint the Dashboard calls per FR-4: "Status recalculates on every Dashboard load"). Logic: `SELECT budget WHERE period_start = :current`; if absent, `SELECT` most recent prior row, `INSERT` a copy, return it. This is a literal, correct reading of AD-6's own words ("Dashboard is first loaded").

**Unit B — Budget Settings builder.** Budget Settings needs to prefill the edit field with the current Period's amount (UJ-4: "Sam taps the budget figure inline... later edits"; FR-6: "edit it later"). Nothing in AD-6 or the Capability Map says Budget Settings' `GET /api/budget` may only ever receive an already-existing row — the AD's trigger condition ("no row exists for it yet... the Service creates one") is written as a general Service-layer rule, not scoped to one specific endpoint. Unit B's `BudgetService.getCurrentBudget()` therefore performs the *identical* SELECT-then-INSERT-if-missing sequence, independently implemented, because Budget Settings must never show a blank/error state before Sam has typed anything (UJ-4 gives no such "wait for Dashboard to visit first" precondition).

**The clash:** two independent, AD-6-compliant code paths both do "SELECT-then-INSERT" for the same `(period_start)` key. `budgets` has no stated `UNIQUE` constraint on `period_start` anywhere in the ERD or Consistency Conventions. If Dashboard and Budget Settings are hit close together — a realistic case even for a single implicit user (mobile browser prefetch, a second tab, React effects double-firing in dev, a retry after a flaky network per the NFR's "failed save shows inline retry-capable error") — both requests can observe "no row," both `INSERT`, and either (a) a duplicate-row constraint violation surfaces as an unhandled 500 (AD-8's single error shape doesn't cover an integrity-constraint exception any more gracefully than any other), or (b) if no constraint exists at all, two budget rows silently exist for one Period and AD-6's own premise — "one row per Period, never a single mutable value" — is violated by the very mechanism meant to guarantee it.

**Why both builders are innocent:** AD-6 assigns the responsibility to "the Service" in the abstract, not to a named method or endpoint, and nothing in the spine states a uniqueness/locking strategy for the table AD-6 itself introduces.

**Suggested tightening:** name exactly one Service method (e.g. `BudgetService.getOrCreateForPeriod(periodStart)`) as the *sole* call site allowed to perform the copy-forward, require every other read path (Budget Settings, any future caller) to call through it, and add `UNIQUE (period_start)` to `budgets` plus a stated conflict-resolution rule (`ON CONFLICT DO NOTHING` + re-SELECT, or a transaction-level advisory lock) so a race degrades to "second writer discards its insert," not a 500 or a duplicate row.

---

## Finding 2 — AD-6: "if any" is ambiguous between *no row created* and *row created with amount 0* [HIGH]

**AD text:** "...the Service creates one by copying the amount from the most recent prior Period that has one (**if any**)."

**Unit A — reads "if any" as gating row creation itself.** No prior Period has a budget (e.g. brand-new deployment, or Sam never set one in month 1) → there is nothing to copy → no row is created at all. `GET /api/budget/status` returns an absent/null budget, and the Dashboard renders FR-6's "neutral prompt... instead of a Budget Status card" by checking *row absence*. This reading is consistent with FR-6: "A Budget is optional... The Dashboard shows a neutral prompt to set one instead of a Budget Status card **until it exists**" — "exists" reads naturally as "row exists."

**Unit B — reads "if any" as only qualifying the copy source, not whether a row is made.** AD-6's title is "Budget is a per-Period row" and its own "Prevents" clause is about builders disagreeing on "no budget vs. last month's figure" — Unit B concludes the intent is that a row *always* exists once a Period is touched, defaulting `amount = 0.00` when there's no prior Period to copy from (also the only sane choice if `budgets.amount` is `NOT NULL NUMERIC(12,2)`, which nothing in the spine says it isn't). The Dashboard then must distinguish "no budget set" from "budget set to ₹0" some other way (e.g. a boolean, or treating `0` as sentinel-for-unset) — a rule the spine never states.

**The clash:** this is a wire-contract fork, not a cosmetic one. `GET /api/budget/status`'s response shape — does "no budget" mean HTTP 404 / `null` field, or a normal 200 with `amount: 0.00`? — determines how `apps/web`'s Dashboard component branches (neutral prompt vs. status card showing "₹0 of ₹0 — 0%, exceeded," which would be a genuine, factual-copy-violating bug under FR-4's "never blame-framed" rule if amount-0-means-unset is misread as amount-0-means-exceeded). A frontend builder reading only the spine cannot know which of Unit A or Unit B the backend implements, and both are defensible readings of "if any."

**Suggested tightening:** state explicitly: "If no prior Period has a budget row, no row is created for the new Period either — absence of a row is the sole signal for 'no budget set,' and `amount` is never used as an unset-sentinel." Pin the `GET /api/budget/status` contract (404 vs. `null` field vs. `{ hasBudget: false }`) in the spine or an explicit companion, not left to whoever writes the DTO first.

---

## Finding 3 — AD-1/AD-6/AD-7: no stated clock/timezone authority for "today" and "current Period" [HIGH]

**Grounding:** confirmed by full-text search — the words "timezone," "UTC," "IST," and "Asia/Kolkata" appear **nowhere** in the spine, the PRD, the addendum, `EXPERIENCE.md`, or `project-context.md`. AD-7 states "All dates are ISO-8601" — but a date-only ISO string (`yyyy-MM-dd`, per the Consistency Conventions table) has no timezone offset; "today" is inherently relative to *someone's* clock, and the spine never says whose.

**Unit A — backend builder.** Computes "today" and "current Period" (`period_start = first-of-month`) using the JVM's default zone on the server: `LocalDate.now()` inside `TransactionService`/`BudgetService`. Railway containers default to UTC unless a `TZ` env var is set, which nothing in the Stack table or Deployment section mentions. This is used both to stamp the frequent-expense-chip Transaction's date (FR-1: "creates a Transaction dated today... no further input required" — the AD doesn't say client-supplied or server-stamped) and to compute which Period's Budget Status the Dashboard's default (no-param) request should return.

**Unit B — frontend builder.** The Quick Add screen (India-only product per the NFR: "₹ (INR) only, Indian digit grouping") computes "Today" for the manual-entry date-field default, and for the chip tap, using the browser's local clock (IST, `UTC+5:30`, since Sam is physically in India) — `new Date()` in the client, formatted to `yyyy-MM-dd`, sent as `transaction_date` in the request body. This matches FR-2's UX ("Date defaults to today; tapping it opens a picker") and is the only value actually available to the client without an extra round-trip.

**The clash:** IST is 5.5 hours ahead of UTC. Consider Sam tapping a Frequent-Expense chip at 00:15 IST on Aug 1 (still 18:45 UTC on Jul 31). Unit B's client sends `transaction_date: "2026-08-01"` (her local today) — correctly filed into the August Period per FR-3 ("assigns every Transaction to the Budget Period matching its transaction date"). But if the Dashboard's default "current Period" (which the same request or the next `GET /api/budget/status` call resolves) is computed server-side via Unit A's `LocalDate.now()` in UTC, the server still believes "today" is Jul 31 — the Dashboard keeps showing **July's** Budget Status and total, unaware a Transaction was just filed into August, directly contradicting FR-4's "Status recalculates... immediately after any Transaction... change — no caching." The Transaction silently "reappears" in August's numbers only once the server's own UTC clock crosses into August — 5.5 hours after Sam experienced it as a new month. Both builders followed the letter of AD-1 ("computed... at read time"), AD-6 ("per Period"), and AD-7 ("ISO-8601") — neither AD says which clock is authoritative for what "the current Period" *is* at a given instant.

**Suggested tightening:** name one authority for date-only "now" (e.g. "the server, pinned to `Asia/Kolkata` via `TZ`/`spring.jackson.time-zone`, is the sole authority for 'today' and Period boundaries; the client's date picker only supplies explicit backdates, never an implicit 'now'"), and state it applies uniformly to chip-logging, manual-entry defaults, and Period-boundary math for Budget Status and "days remaining" (FR-4).

---

## Finding 4 — AD-5: "exactly one [SYSTEM] row" is declared but its enforcement point is never assigned [MEDIUM-HIGH]

**AD text:** "`SYSTEM` (exactly one row: Uncategorized — seeded at startup, never shown in any create/edit/delete UI)." Note precisely what this constrains: it is a **UI-visibility** rule ("never shown in any... UI"), not a stated **API/DB-layer** rule.

**Unit A — CategoryController/Service builder implementing FR-9.** FR-9 says the user "can rename or change the icon of **any** Category, including the 5 defaults," and its only stated validation is FR-7's empty/duplicate-name check. Unit A implements `PATCH /api/categories/{id}` generically over all `kind`s, because AD-5 only told the frontend not to *render* an edit affordance for `SYSTEM` — it never told the Service to reject a rename by `kind`. Nothing in AD-3/AD-4 mandates a kind-based guard either (they only mandate DTOs and layering, not this particular validation). Unit A ships without one, reasoning "the UI already prevents this; that satisfies AD-5's stated rule."

**Unit B — seeding/reassignment builder.** Reads "exactly one row" as an absolute system invariant and, for performance, resolves the `SYSTEM` category's ID once (a cached singleton bean, or a hardcoded seed ID from the startup migration) and reuses it everywhere `AD-5`'s single reassignment path needs it: `UPDATE transactions SET category_id = :systemId WHERE category_id = :deletedId`. This builder's code is correct *only if* "exactly one row, permanently named Uncategorized" actually holds.

**The clash, two ways:**
1. **Rename drift.** If Unit A's generic PATCH endpoint is ever hit for the `SYSTEM` row's id (a stale frontend build, a future Settings screen, a direct API call, a QA script iterating all category IDs) — nothing server-side stops it. `Uncategorized` silently becomes some other label, breaking every downstream assumption ("auto-reassigned to Uncategorized," FR-8) and Unit B's cached name-based UI copy, while both units remain individually AD-5-compliant.
2. **Cardinality drift.** AD-5 asserts "exactly one row" but assigns no enforcement mechanism — no `UNIQUE` partial index (`WHERE kind = 'SYSTEM'`) is named anywhere in the ERD or Consistency Conventions, and no Service-layer count-check is mandated. A builder adding a `CommandLineRunner`/`@PostConstruct` idempotent seeder ("if none exists, create one") is fully AD-5-compliant in isolation; so is a second builder's Flyway/Liquibase migration insert. Under a rolling-deploy on Railway (two app instances briefly live), both boot-time paths can race the same way as Finding 1 — and nothing in the spine would catch a resulting second `SYSTEM` row, since AD-5 never states *where* "exactly one" is enforced.

**Suggested tightening:** extend AD-5's rule to explicitly bind the Service layer, not just the UI: "`CategoryService` rejects any mutation (rename/re-icon/delete) targeting a `SYSTEM`-kind row with a 4xx, regardless of caller" and "`categories` has a partial unique index enforcing at most one `SYSTEM` row at the DB layer, not just an idempotent seeder."

---

## Finding 5 — Deferred: Frequent-Expense chip logic has no assigned owner, and AD-1's wording lets a frontend builder defensibly claim it's exempt [MEDIUM]

**Spine text (Deferred):** "Frequent-Expenses Shelf ordering/cap mechanism — left as an implementation detail per the PRD (§10); resolve during `TransactionService` build without a schema change (likely: top-N by frequency over a trailing window, computed, not stored — consistent with AD-1's spirit)." Note the hedge: "**likely**" — this is explicitly not a binding AD, and the Capability Map's FR-1 row lists both `apps/web` Quick Add *and* `apps/api` `TransactionService` as where the capability "lives," without saying which owns the ranking computation specifically.

**Unit A — backend builder**, working `apps/api`. Takes the Deferred note's "likely... `TransactionService`" at face value and ships `GET /api/transactions/frequent-shelf`, a Service-layer `GROUP BY (amount, category_id, description)` query returning a ranked top-N DTO list, computed fresh per request (no stored ranking) — respects AD-1's letter and its own stated preference.

**Unit B — frontend builder**, working `apps/web` Quick Add, in parallel and without waiting on Unit A. Reads AD-1's rule narrowly and literally: "No table or column stores **a total, a remaining amount, or a spent amount**... Every category total, period total, and budget-remaining figure is computed by a Service-layer query." A chip-frequency ranking is none of those three enumerated things — it is not money, not a total. The Consistency Convention row ("only a Service method may compute a derived total") uses the same word, "total." Unit B concludes the chip-ranking sits outside AD-1's scope entirely, and — since `/api/transactions` already exists for Search & Filter (FR-10) and returns raw transactions — builds `lib/frequentExpenses.ts` to fetch recent transactions and compute the top-N grouping client-side, avoiding a new backend endpoint and an extra round trip on the Quick Add hot path (a defensible speed argument given FR-1/SM-2's "under 5 seconds" goal).

**The clash:** two independently-shipped, spine-compliant implementations of "what counts as a repeat expense" — almost certainly with different grouping keys, different trailing-window definitions, and different tie-breaking — either (a) both get built and something downstream has to arbitrarily pick one, wasting the other, or (b) worse, both ship behind slightly different call sites (e.g. Dashboard's "recent chips" widget calls Unit A's endpoint while Quick Add's shelf uses Unit B's local computation) and the two surfaces silently disagree on which items count as "frequent" for the same underlying data — a shared-concept fork the spine's own paradigm statement ("apps/web... never computes a total") was clearly trying to prevent, but doesn't clearly *say* applies here because "total" is never defined broadly enough to cover a ranking.

**Suggested tightening:** either promote the Deferred note to an AD ("all Frequent-Expense ranking is Service-computed, exposed via one endpoint; `apps/web` never independently aggregates the transaction list for this or any other purpose"), or explicitly broaden AD-1/AD-4's "total" language to "any aggregation over `transactions`," closing the loophole Unit B's reading exploits.

---

## Summary Table

| # | AD(s) implicated | Clash type | Severity |
| --- | --- | --- | --- |
| 1 | AD-6 | Two owners of "create Period row"; no concurrency control | High |
| 2 | AD-6 | Ambiguous wording → divergent wire contract (row-absent vs. amount-0) | High |
| 3 | AD-1, AD-6, AD-7 | No clock/timezone authority → Period-boundary disagreement | High |
| 4 | AD-5 | "Exactly one row" invariant has no assigned enforcement layer | Medium-High |
| 5 | AD-1 (Deferred note) | Two owners of chip-ranking logic; "total" wording loophole | Medium |

All five are genuine two-builder divergences: in every case, both constructed units cite specific AD text or FR wording that a literal, good-faith implementer could rely on, and no other bound source (PRD, addendum, UX docs, project-context) resolves the ambiguity.
