---
title: bmad-expense-tracker PRD
status: final
created: 2026-07-03
updated: 2026-07-03
---

# PRD: bmad-expense-tracker
*No separate product/brand name chosen yet — the project name doubles as the working title, consistent with every upstream artifact. Not a blocker for a solo personal-use MVP.*

## 0. Document Purpose

This PRD defines the MVP for a personal expense tracker: a solo, portfolio-quality product Raha intends to keep using long-term, not just a demo. It builds on completed upstream work rather than duplicating it — the Working-Backwards PRFAQ (`_bmad-output/planning-artifacts/prfaq-bmad-expense-tracker.md`) supplies vision and positioning, the finalized UX spec (`DESIGN.md` + `EXPERIENCE.md` at `_bmad-output/planning-artifacts/ux-designs/ux-bmad-expense-tracker-2026-07-02/`) supplies validated flows and interaction rules, and domain/market/technical research supplies the domain model and constraints. This document's structure is Glossary-anchored: Features are grouped with Functional Requirements (FRs) nested and globally numbered, User Journeys (UJs) numbered globally and referenced by FRs, and inferred content tagged `[ASSUMPTION]` and indexed in §11.

## 1. Vision

A personal expense tracker for people who want to log spending in seconds and see an honest month-to-date picture — without bank-linking, receipt scanning, or guilt. It targets the moment budgeting apps actually fail: not day one, but the day after a missed entry, when red banners, streak breaks, and catch-up burdens push people to quietly stop opening the app.

The answer here is structural, not motivational. Category and budget totals are always computed live from transactions — never cached, never independently editable — so the numbers are trustworthy by construction. Quick-add takes under 5 seconds via one-tap frequent-expense chips for repeat purchases. And returning after a gap looks exactly like using the app any other day: no backlog, no lecture, just today's numbers.

*Note on an evolved promise:* the PRFAQ's original pitch was literally "no red banners." UX design later overrode this to a visible green/amber/red status color (see FR-4) — what survived isn't the absence of red, it's the deeper rule: color may signal severity because color alone doesn't moralize, but the copy stays factual and non-judgmental at every severity level, because language is where shaming actually happens. Full rationale in `addendum.md`.

## 2. Target User

### 2.1 Jobs To Be Done

- **Functional:** Log an expense in under 5 seconds, ideally with zero typing (frequent-expense chip path).
- **Functional:** See at a glance whether I'm on track for the month without doing mental math.
- **Emotional:** Not feel bad about money-tracking after missing a few days.
- **Builder's own JTBD** *(the real motivation, per PRFAQ Internal FAQ)*: a tool Raha will actually keep using long-term, not a portfolio demo abandoned after review.

### 2.2 Non-Users (v1)

- Multi-user or shared households (no auth/accounts in v1 — single implicit user).
- Anyone wanting bank-sync or automatic transaction import/categorization.
- Anyone needing receipt capture or OCR.

### 2.3 Key User Journeys

*Protagonist: **Sam**, 20, first-time budgeter managing her own money — representative of the "Anxious Starter" segment from market research. Mirrors the 6 flows validated in `EXPERIENCE.md`; UJ IDs below are this PRD's own numbering.*

- **UJ-1. Sam logs a coffee run in one tap.**
  - **Persona + context:** Sam just bought coffee, wants to log it before she forgets, doesn't want to type anything.
  - **Entry state:** App open, on Dashboard, no prior action this session.
  - **Path:** Tap FAB → tap frequent-expense chip ("☕ Coffee · ₹150") → done.
  - **Climax:** Instant return to Dashboard; status card and totals update immediately.
  - **Resolution:** Sam closes the app in under 5 seconds, zero typing.
  - **Edge case:** Connection lost mid-save — chip values retained, inline error shown, retry available; nothing silently lost.

- **UJ-2. Sam logs a one-off purchase.**
  - **Persona + context:** Sam bought something that isn't a repeat/frequent item.
  - **Entry state:** App open, taps FAB from anywhere.
  - **Path:** Keypad amount → pick category (overrides last-used default) → date defaults to "Today" (tap to backdate) → type required description → Save (disabled until amount > 0, category selected, description non-empty).
  - **Climax:** Save succeeds; Dashboard totals reflect the new transaction, assigned to the period of the transaction date (not entry date) even if backdated.
  - **Resolution:** Transaction appears in the correct period regardless of when it was logged.
  - **Edge case:** Tapping Save while disabled is a no-op; focus and an audible "required" state move to the description field.

- **UJ-3. Sam checks the dashboard — including after a gap.**
  - **Persona + context:** Sam opens the app with no logging intent, just to see where she stands. Two entry-state variants matter equally: a routine daily check, and reopening after 9 days away.
  - **Entry state:** App open to Dashboard; last-visit gap is irrelevant to what she sees.
  - **Path:** Open app → Dashboard renders status card (green/amber/red), running total, category breakdown — zero taps required.
  - **Climax:** The numbers are current and honest, computed live from all transactions regardless of when they were logged.
  - **Resolution:** By design, the routine-check and the after-a-gap variants resolve **identically** — no backlog counter, no reminder, no streak break, no different copy. This identical resolution is the product's core untested hypothesis (see §9 Success Metrics, §10 Open Questions): that removing all shame-coded signals from the return-after-lapse moment is what earlier tools got wrong.
  - **Edge case:** Cold load shows a skeleton state, not a blank screen.

- **UJ-4. Sam sets her first monthly budget.**
  - **Persona + context:** Sam has already logged a few transactions and wants a budget to check against.
  - **Entry state:** Dashboard shows a neutral prompt to set a budget (first use), or Sam taps the budget figure inline (later edits).
  - **Path:** Navigate to Budget Settings → enter amount → Save.
  - **Climax:** Status card immediately recalculates against previously-logged transactions — no re-entry of past spending needed.
  - **Resolution:** Budget is live and retroactively applied to the current period's existing transactions.
  - **Edge case:** Invalid entry (zero, negative, non-numeric) blocked inline: "Enter an amount greater than ₹0."

- **UJ-5. Sam manages categories.**
  - **Persona + context:** Sam's spending doesn't fit the 5 defaults, or she's cleaning up a category she no longer needs.
  - **Entry state:** Settings → Categories, or the category row from Dashboard.
  - **Path (add):** "+ Add category" → name + icon → Save → immediately available in Quick Add and Search & Filter. **Path (delete):** select a custom category → delete → its transactions auto-reassign to "Uncategorized," no confirmation step.
  - **Climax:** Category list reflects Sam's actual spending categories without breaking any historical transaction.
  - **Resolution:** Defaults (Food, Transport, Shopping, Bills, Entertainment) can be renamed/re-iconed but never deleted; only custom categories are deletable.
  - **Edge case:** Empty or duplicate category name blocked inline, field retains focus.

- **UJ-6. Sam searches for past transactions.**
  - **Persona + context:** Sam wants to check how much she's spent on something specific.
  - **Entry state:** Opens Search & Filter from Dashboard.
  - **Path:** Set date range + category + optional keyword → list and running total update together instantly.
  - **Climax:** Sam finds what she's looking for without pagination or a separate "search" step.
  - **Resolution:** No matches shows "No matching transactions," filters remain visible and editable.

## 3. Glossary

- **Transaction** — A single logged expense: amount, Category, transaction date, description. Immutable once saved (edits/deletes not in MVP scope — see §8). The sole source of truth for every Derived Total.
- **Category** — A label grouping Transactions for reporting. Exactly one Category per Transaction (no splits). Budgeting is tracked at the overall level (see Budget), not per-Category, so category totals are informational, not budgeted individually. 5 non-deletable defaults (Food, Transport, Shopping, Bills, Entertainment) plus user-added custom categories (fully deletable).
- **Uncategorized** — Implicit system Category a Transaction is auto-reassigned to when its custom Category is deleted. Not user-creatable, not user-deletable.
- **Budget Period (Period)** — The time window (MVP: calendar month) a Budget and its Derived Totals are measured against. A Transaction's Period is determined by its transaction date, never its entry/logging date.
- **Budget** — A single ₹ amount set for the current Period (not per-Category) that total spend is measured against to compute Budget Status. Category breakdown on the Dashboard is informational only, not separately budgeted.
- **Derived Total** — Any total (category total, period total, budget spent/remaining) computed live by summing Transactions at read time. Never stored, cached, or independently edited — the domain's core integrity invariant.
- **Budget Status** — The green/amber/red state shown on the Dashboard: green (<80% of Budget spent), amber (80–100%), red (>100%, "exceeded"). Color communicates severity; copy never does.
- **Frequent-Expenses Shelf** — The set of chips on Quick Add. Ordering/cap mechanism is an open implementation detail (§10).
- **Chip** — A single item on the Frequent-Expenses Shelf, pre-loaded with a preset amount, Category, and description, that logs a Transaction dated today in one tap with zero further input.
- **Quick Add** — The transaction-entry surface, reachable via a FAB from anywhere in the app. Two paths: Frequent-Expenses Shelf chip (one tap) or manual entry (amount, Category, description, optional backdate).
- **Rollover** — Carrying an unspent or overspent Budget balance into the next Period rather than resetting to zero. Not implemented in MVP (§8); if added later, must be an explicit, visible rule, never silent.

## 4. Information Architecture

Six screens, all one level deep from Dashboard — no tab bar, no sidebar, modal/sheet stacks never nest two levels: **Dashboard** (home, no login) → **Quick Add** (FAB, reachable from anywhere) → **Budget Settings** → **Categories** → **Search & Filter** → **Settings** (entry point to Budget Settings/Categories, plus a trust/privacy note). Full screen-level detail lives in `EXPERIENCE.md`.

**Aesthetic and tone:** Visual direction "Calm Harbor" — soft blue-and-white, built on shadcn/ui defaults with a brand-layer delta (primary blue `#2563EB` reserved for brand/nav/selection, never for Budget meaning; the three Budget Status colors reserved exclusively for severity, never reused elsewhere including destructive actions). Full token set in `DESIGN.md`. Copy tone is factual and non-judgmental at every Budget Status severity level — a hard requirement (FR-4), not a style preference.

## 5. Features

### 5.1 Quick Add
**Description:** The primary entry point for logging spending, reachable via a FAB from anywhere in the app. Two paths trade off speed against flexibility: a one-tap Frequent-Expenses Shelf chip for repeat purchases, or manual entry for anything else. Realizes UJ-1, UJ-2.

**Functional Requirements:**

#### FR-1: Frequent-expense chip logging
User can log a Transaction in one tap via a Frequent-Expenses Shelf chip. Realizes UJ-1.

**Consequences (testable):**
- Tapping a chip creates a Transaction dated today with the chip's preset amount, Category, and description — no further input required.
- The chip path is time-locked to today: unlike manual entry (FR-2), there is no backdating option on this path.
- Dashboard totals and Budget Status update immediately on save.
- If save fails (e.g. connection lost), the chip's values are retained and an inline, retry-capable error is shown — no silent data loss.

#### FR-2: Manual transaction entry
User can log a Transaction manually with amount, Category, description, and date. Realizes UJ-2.

**Consequences (testable):**
- Save is disabled until amount > 0, a Category is selected, and description is non-empty.
- Category selection uses a button/icon control, never a dropdown, and pre-selects the last-used Category by default.
- Date defaults to today; tapping it opens a picker for backdating.

**Out of Scope:**
- Splitting one Transaction across multiple Categories.

**Notes:** The required-description rule deliberately overrides market and domain research's "keep description optional to protect logging speed" recommendation — added specifically to support keyword search (Search & Filter, FR-10). Speed is preserved for the repeat-purchase case via the Frequent-Expenses Shelf (FR-1), where description is pre-filled from the chip's preset; only new/one-off manual entries pay the typing cost.

#### FR-3: Transaction-date period assignment
System assigns every Transaction to the Budget Period matching its transaction date, never its entry date. Realizes UJ-2.

**Consequences (testable):**
- A Transaction backdated into a prior Period is included in that Period's Derived Totals and Budget Status immediately on save, even if the user experiences that Period as "closed."

### 5.2 Dashboard & Budget Status
**Description:** The home screen and default landing surface — shows current standing with zero taps required. Its behavior when reopened after a gap is identical to any other visit, which is the product's central, unvalidated bet (§9, §10). Realizes UJ-3.

**Functional Requirements:**

#### FR-4: Live budget status
System computes and displays Budget Status — green (<80% of Budget spent), amber (80–100%), red (>100%, "exceeded") — on the Dashboard. Realizes UJ-3.

**Consequences (testable):**
- Status recalculates on every Dashboard load and immediately after any Transaction or Budget change — no caching, no manual refresh.
- Status copy is factual and non-judgmental at every severity level (e.g. "Budget exceeded by ₹250," never blame-framed). Color, not language, communicates severity.
- No push notification, toast, or modal interrupts the user when status changes — visible only on the Dashboard.
- Dashboard also displays days remaining in the current Period alongside Budget Status, computed live (not stored).
- If no Budget has been set for the current Period, the Dashboard shows a neutral prompt to set one instead of a Budget Status card (see FR-6).

#### FR-5: Derived totals
System computes all category totals, period totals, and budget spent/remaining live from Transactions at read time. Realizes UJ-3, UJ-6.

**Consequences (testable):**
- No total, remaining, or spent value is ever stored as its own column or field — every read recomputes from Transactions.

**Notes:** The no-stored-totals rule is an architectural guardrail as much as a functional one — full rationale carried to `addendum.md` for the architecture phase.

### 5.3 Budget Management
**Description:** Lets the user set the number Budget Status is measured against, and change it later without re-entering history. Realizes UJ-4.

**Functional Requirements:**

#### FR-6: Set and edit budget
User can set a single overall Budget amount for the current Period, and edit it later. Realizes UJ-4.

**Consequences (testable):**
- Editing the Budget amount immediately recalculates Budget Status against already-logged Transactions in the current Period — no re-entry of past spending.
- Zero, negative, or non-numeric entry is blocked inline: "Enter an amount greater than ₹0."
- A Budget is optional: Quick Add, category totals, and Search & Filter all function fully before any Budget is ever set. The Dashboard shows a neutral prompt to set one instead of a Budget Status card until it exists.

### 5.4 Category Management
**Description:** Keeps the Category set matched to how the user actually spends, without ever orphaning historical data. Realizes UJ-5.

**Functional Requirements:**

#### FR-7: Add custom category
User can create a custom Category with a name and icon. Realizes UJ-5.

**Consequences (testable):**
- New Category is immediately available in Quick Add's Category selector and Search & Filter.
- Empty or duplicate Category name is blocked inline; field retains focus.

#### FR-8: Delete custom category
User can delete a Category they created. Realizes UJ-5.

**Consequences (testable):**
- Deletion is immediate, no confirmation step.
- Every Transaction previously assigned to the deleted Category is automatically reassigned to Uncategorized — never blocked, never requiring manual reassignment.

**Out of Scope:**
- Deleting any of the 5 default Categories — no delete affordance is shown for them at all (not merely disabled).

#### FR-9: Rename or re-icon any category
User can rename or change the icon of any Category, including the 5 defaults. Realizes UJ-5.

**Consequences (testable):**
- The same empty/duplicate-name validation FR-7 specifies for new Categories applies to renames, including renames of the 5 defaults — so a rename can only end up sharing a name with another Category if that validation is bypassed, and it isn't.
- Renaming or re-iconing a Category does not affect its historical Transactions or any Derived Total — only the label/icon changes going forward.

### 5.5 Search & Filter
**Description:** Lets the user answer "how much did I spend on X" without leaving the app's single-screen-depth model. Realizes UJ-6.

**Functional Requirements:**

#### FR-10: Filter transactions
User can filter Transactions by date range, Category, and optional keyword (matched against description). Realizes UJ-6.

**Consequences (testable):**
- The filtered list and its running total update together, instantly, on every filter change.
- No matches shows "No matching transactions" while filters remain visible and editable.

**Out of Scope:**
- Pagination or infinite-scroll (not needed at MVP data scale).

## 6. Cross-Cutting NFRs

- **Accessibility — WCAG 2.1 AA floor** (not AAA): full keyboard operability; visible focus indicator on every focusable element; color is never the sole signal (Budget Status pairs color+icon+text, Category selection pairs border/fill+checkmark+`aria-pressed`); a disabled Save button exposes *why* via `aria-describedby`/`aria-label`, not just a disabled visual state; `aria-live="polite"` on inline status/validation messages; AA contrast on all three Budget Status pairs (green/red ~7:1+, amber ~6.8:1) and body text; tap targets meet standard minimum size; text scales to 200% zoom without truncation; single-column reflow to 320px/400% zoom, no 2D scrolling.
- **Connectivity** — Always-online assumption; no offline queueing or sync. A failed save shows an inline, retry-capable error ("Couldn't save — check your connection"); entered values are retained, never silently lost.
- **Identity** — Single implicit user, no authentication, no accounts in v1; backend-persisted (not local/browser storage).
- **Platform** — Mobile-first responsive web; desktop scales up from the same design, no separate desktop redesign. No native app, no PWA install.
- **Currency** — ₹ (INR) only, Indian digit grouping (e.g. `₹1,00,000`). No multi-currency support.
- **Data integrity guardrail** — The Derived Total rule (FR-5) is enforced structurally: no totals/remaining column exists in the schema at all, so a service that accidentally caches a total becomes impossible to write, not just discouraged. Full technical rationale carried to `addendum.md`.
- **Persistence** — System runs on persistent, non-ephemeral storage suitable for long-term personal use — not a free-tier setup that expires or resets. Specific hosting provider/choice is an architecture-phase decision, not fixed here.

## 7. Non-Goals (Explicit)

- No bank-account linking or automatic transaction import.
- No receipt capture or OCR.
- No push notifications, toast, or modal budget alerts of any kind.
- No multi-user accounts or authentication.
- No offline queueing or background sync.
- No dark mode.
- No native app or app-store install (responsive web only).
- No voice or freeform natural-language capture.
- No swipe gestures, long-press menus, or drag-to-reorder — all interaction is tap-only by deliberate design choice.
- No home-screen widget shortcut. No "chill / strict mode" tone toggle for Budget Status copy.
- No dedicated lapse-recovery or re-entry screen, streak counters, or badges — the non-shaming design goal is realized entirely through FR-4's status/copy rules and UJ-3's Dashboard behavior, not a separate feature. `[NOTE FOR PM]` Only reconsider a dedicated Lapse Recovery feature if self-dogfooding (§9 SM-1) surfaces unprompted, independent signal that lapse-guilt is a real problem — not merely because the hypothesis remains untested.
- No decorative animation.

## 8. MVP Scope

### 8.1 In Scope
- Quick Add: frequent-expense chip logging (FR-1) and manual entry (FR-2, FR-3).
- Dashboard with live Budget Status and Derived Totals (FR-4, FR-5).
- Budget Management (FR-6).
- Category Management: add, delete (with auto-reassignment), rename/re-icon (FR-7–FR-9).
- Search & Filter (FR-10).
- WCAG 2.1 AA accessibility floor (§6).

### 8.2 Out of Scope for MVP

**Confirmed exclusion (not timeline-contingent):**
- **Rollover behavior** — no Rollover for MVP. Budgets reset to zero each Period. This is a firm decision, not a maybe-cut; revisit for v2 based on actual usage.

**Deferred, cut order if timeline slips:**
1. **Charts/insights** — first to cut.
2. **Category-edit polish beyond basic CRUD** — second to cut.

`[NOTE FOR PM]` Core loop (Quick Add, category totals, Budget Status, Frequent-Expenses Shelf) is never cut regardless of timeline pressure — this is the product's entire value proposition per the PRFAQ.

## 9. Success Metrics

*No quantitative success metric was ever defined upstream — the PRFAQ states explicitly that its success criteria were about execution and decision narrative, not adoption. This PRD sets the first real ones, scaled to a solo/portfolio product.*

**Primary**
- **SM-1**: Raha uses the app to log real spending at least weekly, 3+ months after MVP ships, without abandoning it. Validates FR-1–FR-10 collectively — the whole product succeeds or fails on continued personal use.

**Secondary**
- **SM-2**: Quick-add via a Frequent-Expenses Shelf chip completes in under 5 seconds in practice. Validates FR-1.

**Counter-metrics (do not optimize)**
- **SM-C1**: Feature count / scope breadth — do not optimize for "more capability" if it costs Quick Add its speed or simplicity. Counterbalances SM-1, SM-2.

## 10. Open Questions

1. **Frequent-Expenses Shelf ordering/cap** — mechanism undecided; deliberately left as an implementation detail for UX/architecture, not a PRD blocker.
2. **Category icon set** — the 5 default Category names are locked, but specific icons are unnamed in the UX spec; resolve during build, not a PRD blocker.
3. **Competitive differentiation vs. Koody** — market research (2026-07-02) found no competitor combining fast manual entry with genuinely non-shaming lapse recovery, but a later PRFAQ-stage research pass found Koody (a 2026 entrant) to be a near-exact positioning match on manual-only/no-bank-link/fast-entry — that finding was never folded back into the market research document itself. Finny (AI-parsed voice/NL entry) and PocketGuard's "Pace" (spend-rate alerts) further narrow the gap without closing it. This product has no evidence yet that it wins on the non-shaming axis specifically — that's exactly what self-dogfooding (§9 SM-1) is meant to surface. Not a pre-launch blocker.
4. `[NOTE FOR PM]` **Validation follow-through risk** — the formal usability-testing gate has been planned and not executed across 3 prior sessions (brainstorming → innovation strategy → PRFAQ). This PRD replaces it with self-dogfooding (§9), which is lower-friction but carries the same risk of being "planned" without follow-through. Worth a genuine gut-check at retrospective time on whether weekly use is actually happening.
5. ~~Rollover behavior~~ — resolved: no Rollover for MVP (§8.2).

## 11. Assumptions Index

*No unresolved `[ASSUMPTION]` tags remain in this document — the budget-scope assumption in §3/§5.3 was confirmed as single overall Budget per Period during drafting.*
