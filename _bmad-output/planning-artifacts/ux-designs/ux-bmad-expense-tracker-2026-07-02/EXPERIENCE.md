---
name: bmad-expense-tracker
status: final
sources:
  - '{planning_artifacts}/briefs/brief-bmad-expense-tracker-2026-07-02/brief.md'
  - '{planning_artifacts}/briefs/brief-bmad-expense-tracker-2026-07-02/addendum.md'
  - '{planning_artifacts}/prfaq-bmad-expense-tracker.md'
  - '{planning_artifacts}/prfaq-bmad-expense-tracker-distillate.md'
  - '{planning_artifacts}/research/domain-expense-tracking-research-2026-07-02.md'
  - '{planning_artifacts}/research/market-personal-expense-tracking-app-market-research-2026-07-02.md'
  - '{planning_artifacts}/research/technical-nextjs-spring-boot-postgresql-stack-research-2026-07-02.md'
  - '_bmad-output/design-thinking-2026-07-02.md'
  - '_bmad-output/innovation-strategy-2026-07-02.md'
updated: 2026-07-03
---

# bmad-expense-tracker — Experience Spine

> Single-surface responsive web, mobile-first. shadcn/ui on Next.js + Tailwind. Paired with `DESIGN.md` ("Calm Harbor" direction). No PRD exists yet — this spine is a pre-PRD input built directly from brief/PRFAQ/research per user request; the PRD should treat these flows as validated UX intent, not re-derive them.

## Foundation

Single-surface responsive web app, mobile-first (primary target ~375–414px viewport) with desktop-capable responsive layout — not a native app, no app-store install. shadcn/ui on Next.js 15+ with Tailwind CSS; the component library does most of the work — brand discipline is "respect shadcn defaults except where `DESIGN.md`'s brand layer overrides them." `DESIGN.md` is the visual identity reference; this spine is the experience.

No authentication in V1 — single-user application. Data is backend-persisted via the Spring Boot API (PostgreSQL), not browser local storage, so the same data is available regardless of device/browser. Currency is ₹ (INR) with Indian digit grouping (e.g. `₹1,00,000`) throughout. English only for V1. Light mode only — no dark mode. Motion is minimal and near-instant; no decorative animation anywhere.

**Primary persona — Sam, 20, college sophomore/young professional.** First time managing money independently. No budgeting experience, no patience for setup, mildly anxious about "finding out" they've overspent. Every flow below is written from Sam's vantage point.

**Derived-totals invariant (structural, not just behavioral):** category totals and budget-remaining are always computed from transactions at read time — no totals/remaining column exists anywhere to accidentally cache. This spine assumes that architectural guarantee; every "instant update" moment described below is a recompute, not a write to a cached figure.

**Transaction-date invariant:** a transaction's budget period is determined by its **transaction date**, not the date it was entered — logging Tuesday's coffee on Thursday must still count in Tuesday's period. Domain research treats this as a non-negotiable rule and the PRFAQ cites it as a verified strength; Quick Add's date field (see Component Patterns) exists specifically so this holds in practice, not just in the data model.

## Information Architecture

| Surface | Reached from | Purpose |
|---|---|---|
| Dashboard | App open (default, no login) | Month total, budget status (green/amber/red), category-wise breakdown |
| Quick Add | Persistent `+` FAB, any screen | Log a new expense — frequent-expense shelf or manual entry |
| Budget Settings | Tap the budget figure on Dashboard, or Settings | Set monthly budget (first use) or edit it (later) |
| Categories | Settings, or a category row on Dashboard | Add, rename/re-icon, or delete (custom only) categories |
| Search & Filter | Nav/icon from Dashboard | Filter past transactions by date range, category, and keyword; see a running total |
| Settings | Gear icon, Dashboard header | Entry points to Budget Settings and Categories; trust/privacy note |

No tab bar and no sidebar drawer — Dashboard is home, everything else is one level deep from it, reached via the gear icon or direct taps on Dashboard elements (the budget figure, a category row). Modal/sheet stacks never go two levels deep.

→ Composition reference: `mockups/calm-harbor-dashboard-quickadd.html` (Dashboard + Quick Add, all three budget states, save-button disabled/enabled states). Spine wins on conflict.

## Voice and Tone

Microcopy. Brand voice and aesthetic posture live in `DESIGN.md.Brand & Style`.

| Do | Don't |
|---|---|
| "₹6,800 of ₹8,000 this month — 85% used, 5 days left" | "You're almost out of money!" |
| "Budget exceeded by ₹250" | "YOU'RE OVER BUDGET" / "You failed your budget" |
| "Today's picture" (on reopening after a gap) | "You have 4 unlogged days" / any backlog framing |
| "You're always in control of what's tracked" | Apologizing for not having bank sync ("we don't support that yet") |
| "We never connect to your bank." (trust note, Settings) | Silence on the manual-only nature — Sam should never have to wonder |
| Factual, complete sentences, even at the red state | Exclamation marks, streak language, "gotcha" itemization ("$47 on coffee?!") |

Severity is carried by **color + icon** (see `DESIGN.md.Colors`), never by escalating the copy's tone — the amber and red messages stay exactly as matter-of-fact as the green one.

## Component Patterns

Behavioral. Visual specs live in `DESIGN.md.Components`.

| Component | Use | Behavioral rules |
|---|---|---|
| Budget status card | Dashboard (top), Quick Add post-save echo | Same position/layout across all 3 states. Green < 80% of budget, amber 80–100%, red > 100%. Updates instantly on save/edit — no toast, no modal, no push notification (all budget-status communication stays inside this one persistent component). |
| Category icon button | Quick Add, Categories | Last-used category pre-selected by default on Quick Add. Tap to change among the current category set. Never a dropdown. |
| Frequent-expense chip | Quick Add shelf | Tapping a chip logs immediately with **today's date** and the chip's preset amount, category, and description — zero further input, no date edit available (chips are for "log this right now"). Shelf shows Sam's most habitual purchases (ordering/cap left as an implementation detail, not user-visible contract). |
| Date field | Quick Add (manual-entry path only) | Defaults to today, shown as plain text (e.g. "Today, 3 Jul 2026"). Tapping it opens a date picker so Sam can backdate an expense entered late — the transaction-date invariant (see Foundation) depends on this existing. Not present on the chip fast-path. |
| Quick-add save button | Quick Add | Disabled until amount > 0, a category is selected, and description is non-empty. Manual-entry path requires typing the description; the frequent-expense-chip path satisfies it automatically via the chip's preset description. |
| Category row | Categories screen | Tap to rename/re-icon (all categories) or delete (custom categories only — the 5 defaults show no delete affordance at all, not just a disabled one). Deleting a category reassigns its existing transactions to "Uncategorized" automatically, no confirmation-with-reassignment step. |
| Search result row + running total | Search & Filter | Filtering by date range / category / keyword updates the list and the total together, instantly. No pagination-vs-infinite-scroll decision needed at MVP scale (single user, bounded data). |
| FAB (`+`) | Dashboard (persistent) | Always visible, thumb-reachable, never tucked into a menu. |

## State Patterns

| State | Surface | Treatment |
|---|---|---|
| No budget set (first use) | Dashboard | Neutral prompt in the status-card position: "Set a monthly budget to see your status" + a CTA to Budget Settings. Skippable — Sam can log expenses with no budget set at all. |
| No transactions yet | Dashboard | Category breakdown area shows a simple empty message; status card still shows the no-budget or ₹0-of-budget state as applicable. No onboarding tour, no empty-state illustration. |
| Cold app load | Dashboard | Status card and category list show a lightweight loading skeleton matching their final shape; resolves in place on data — never a blank screen or a full-page spinner. |
| Reopen after a gap | Dashboard | Shows only today's/this-month's current picture. No "you missed N days," no confrontation, no catch-up prompt. |
| Save button — empty description (manual path) | Quick Add | Disabled, with an accessible reason exposed to assistive tech (not just a visually greyed button) — see Accessibility Floor. |
| Save button — filled | Quick Add | Enabled, `{colors.primary}` fill. |
| Save succeeds | Quick Add → Dashboard | Returns to Dashboard instantly; status card and category totals reflect the new transaction with no loading spinner (near-instant per `DESIGN.md` motion stance). |
| Save fails (connection lost) | Quick Add | Plain inline message ("Couldn't save — check your connection."), announced via `aria-live` (see Accessibility Floor). Entry is retained so Sam can retry. No offline queueing/sync (always-online assumption). |
| Invalid budget entry (zero, negative, non-numeric) | Budget Settings | Blocked inline with a plain validation message ("Enter an amount greater than ₹0"), announced via `aria-live`. No submit-then-error round trip. |
| Invalid category name (empty or duplicate) | Categories | Save blocked inline with a plain validation message ("Give this category a name" / "You already have a category called that"); the name field keeps focus. |
| Search — no matches | Search & Filter | "No matching transactions." No suggested filters; filters stay visible and editable. |
| Category deleted, had transactions | Categories → Dashboard/Search | Those transactions now show under an "Uncategorized" row/filter option — no dead links, nothing orphaned. |
| Focus (keyboard) | Any interactive element | Visible `{colors.ring}` focus indicator (see `DESIGN.md.Components.focus-ring`) on every button, input, and category tile — never suppressed. |

## Interaction Primitives

- **Tap-first, mobile-first.** Every primary action (log, save, pick category, tap a chip) is a single tap; the numeric keypad is the only place requiring multiple inputs in sequence.
- **Keyboard-operable throughout** for the desktop-responsive case: `Tab` order follows visual/reading order; `Enter` submits the focused form; category buttons and chips are real, focusable `<button>` elements, not `<div>`s.
- **No swipe gestures, no long-press menus, no drag-to-reorder** — kept out deliberately; this is a low-gesture-vocabulary product.
- **Banned:** push notifications, toast/banner interruptions for budget alerts, streak counters or badges, decorative open/close animations, dropdowns for category selection, "unlogged days" or backlog counters, voice-to-log input (deferred, not V1), freeform natural-language quick-capture parsing (deferred to a future major version, not V1), a dedicated lapse-recovery/re-entry screen (deferred — gated behind unrun user testing per research).

## Accessibility Floor

Behavioral. Visual contrast lives in `DESIGN.md`. Target: **WCAG 2.1 AA** (not AAA).

- Full keyboard navigation: every interactive element (FAB, category buttons, chips, keypad keys, save button, filters) is reachable and operable via keyboard alone.
- Visible focus indicators on every focusable element, using `{colors.ring}` (`DESIGN.md.Components.focus-ring` — 3px, 2px offset) — no `outline: none` without that replacement.
- Semantic HTML: real `<button>`, `<label>`, `<input>`, and heading levels that reflect IA structure — not `<div onclick>` patterns.
- Color is never the only signal: the budget status card pairs color with an icon and explicit text on every state (e.g., a screen-reader user gets "Budget exceeded by ₹250," not just a red region); the selected-category state pairs its border/fill change with a checkmark badge (`DESIGN.md.Components`) and `aria-pressed`.
- Disabled save button exposes *why* to assistive tech, not a silent disabled state — use `aria-describedby` (pointing at "Add a description to save") or an equally descriptive `aria-label`; pick one convention and apply it everywhere a control is conditionally disabled.
- Inline status/error messages that appear without a route change or a focus move — save failure, budget validation, category-name validation, and the Search & Filter running total updating — use `aria-live="polite"` so screen-reader users off-focus still hear them (WCAG 4.1.3).
- Tap targets meet common target-size guidance (see `DESIGN.md.Components.category-icon-button` for exact sizing).
- Form fields (amount, description, budget figure, category name) carry visible `<label>`s, not placeholder-only labeling.
- Contrast: all budget-status text/background pairs and body text against `DESIGN.md.Colors.background` meet AA contrast ratios (computed ratios recorded in `DESIGN.md.Colors`).
- Text scales via relative units (`rem`) up to 200% zoom / OS text-scaling without truncation or overlapping controls (WCAG 1.4.4) — see `DESIGN.md.Typography`.

## Responsive & Platform

| Breakpoint | Behavior |
|---|---|
| `< 640px` (primary target) | Single column, FAB bottom-right, category buttons in a horizontally-scrollable row, full-width status card. |
| `640–1024px` (tablet) | Same single-column structure, content max-width constrains and centers rather than stretching cards edge-to-edge. |
| `≥ 1024px` (desktop) | Content stays centered at a comfortable reading width (this is a manual-entry tool, not a dashboard-of-widgets); category grid may show more columns per row since horizontal space allows it without changing interaction model. |

No distinct "desktop layout" redesign — the same IA and components scale up rather than rearranging, keeping implementation effort low. Content reflows to a single column with no two-dimensional scrolling down to 320 CSS px / 400% zoom (WCAG 1.4.10) — the mobile-first single-column layout satisfies this by construction, not as a separate design.

## Inspiration & Anti-patterns

- **Lifted from the design-thinking pass:** the frequent-expenses shelf, directly justified by the observed behavior that Sam re-logs similar purchases (coffee, transit, lunch) often — the shelf is the fastest path for exactly that pattern.
- **Lifted from market research:** big icon category buttons over dropdowns — category selection, not amount entry, is the known drop-off point, and visual recognition beats reading a list.
- **Reconsidered from market research, by explicit user decision:** the original research recommendation was "no red banners, ever" (observational framing only). The user overrode this — red/amber/green **do** appear, because visible, actionable budget status was judged more valuable than avoiding red outright. What's preserved from the original research is the *reason* red was avoided (shaming tone) — the copy stays factual ("Budget exceeded by ₹250") never moralizing ("you failed"). Color signals severity; language never does.
- **Rejected — home-screen widget shortcut:** deferred idea from design-thinking, not built in V1.
- **Rejected — "chill mode / strict mode" budget-tone toggle:** adds setup-time configuration this product deliberately avoids.
- **Rejected — voice-to-log parsing:** out of scope for V1.
- **Rejected — weekly-only "gentle check-in" cadence:** the always-visible status card was chosen over a periodic check-in surface.
- **Deferred — freeform quick-capture parsing** ("coffee 5" → auto amount+category): named in innovation strategy as a genuinely differentiated future direction, explicitly H2/post-V1.
- **Deferred — dedicated lapse-recovery re-entry screen:** concept exists in innovation strategy but is gated behind user testing that has not yet run; not part of this spine's IA.

## Key Flows

### Flow 1 — Add Expense: the coffee run (Sam, 20, walking to a 9am lecture)

1. Sam grabs a coffee from the cart outside the library — ₹150, paid by tap. No time to sit down.
2. Opens the bookmarked tracker on their phone. It reopens straight to the Dashboard — no login, no loading spinner, just today's picture.
3. Taps the persistent `+` FAB, bottom-right, thumb-reachable.
4. Quick Add opens. Because Sam buys coffee often, the frequent-expenses shelf already shows a "☕ Coffee · ₹150" chip carrying its own preset description. Sam taps it — no keypad, no category picker, no typing.
5. **Climax:** The screen returns to Dashboard *instantly* — no spinner, no confirmation modal. The budget status card has already updated: *"₹3,550 of ₹8,000 this month — on pace, 12 days left."* Green, calm, no alarm. Sam pockets the phone and walks into class, expense logged before they've sat down.

Failure: the save fails (connection lost) → Quick Add stays open with the chip's values retained and shows "Couldn't save — check your connection." Sam taps the chip again to retry; no data is lost, no backlog created.

### Flow 2 — Add Expense: a new, one-off purchase (Sam, buying a textbook)

1. Sam buys a used textbook for ₹450 — not a habitual purchase, no matching shelf chip.
2. Taps `+`, ignores the shelf, taps the keypad: `4`, `5`, `0`.
3. Category defaults to the last-used category (Food, from this morning); Sam taps the "Shopping" icon button instead.
4. The date field already shows "Today," which is correct — Sam is logging this right after buying it, so there's nothing to change here.
5. The required Description field appears; the save button is visibly disabled (and, for screen-reader users, announced as needing a description). Sam types "Textbook."
6. Save button becomes enabled. Sam taps it.
7. **Climax:** Dashboard returns instantly; the status card and the Shopping category row both reflect the new total in the same glance — Sam sees exactly where the ₹450 landed, with no extra screen or confirmation step.

Failure: Sam taps the (disabled) save button before typing a description — nothing happens, and the description field receives focus with its "required" state visibly and audibly indicated, rather than a silent no-op.

Variant — backdating: two days later, Sam remembers they never logged a cash payment from Tuesday. They open Quick Add, enter the amount and category as usual, then tap the date field (showing "Today") and pick Tuesday's date instead. The transaction saves against Tuesday's period, not today's — the Dashboard's *this month* total updates, but Tuesday's category breakdown (if Sam looks at it via Search & Filter) is what actually shifts, not today's.

### Flow 3 — View Dashboard (Sam, evening, checking in unprompted)

1. Sam opens the app with no expense to log — just curious how the month looks.
2. Dashboard loads straight to the status card: *"₹6,800 of ₹8,000 this month — 85% used, 5 days left."* Amber.
3. Below it, without needing to tap anything, Sam sees the total spent and the category-wise breakdown (Food, Transport, Shopping, Bills, Entertainment) with amounts.
4. **Climax:** Sam gets the complete picture — status, total, and category detail — in one glance and zero taps. The amber tells them to ease off discretionary categories this week, phrased as pace information, not a scolding.

Failure: Dashboard data is still loading (cold app open) → see State Patterns, "Cold app load."

### Flow 4 — Set Monthly Budget (Sam, first use, then an edit weeks later)

1. **First use:** Sam has already logged a couple of expenses without ever being asked to set a budget. On Dashboard, the status-card area shows a neutral prompt: "Set a monthly budget to see your status," with a clear CTA.
2. Sam taps it, goes to Budget Settings, enters ₹8,000, saves.
3. **Climax (first use):** Returning to Dashboard, the status card now shows real status computed against the transactions Sam already logged before the budget even existed — nothing was lost or needs re-entry.
4. **Later edit:** A few weeks in, Sam's expenses have grown; they tap the ₹8,000 figure directly on the Dashboard (inline entry point into Budget Settings), change it to ₹9,000, save.
5. **Climax (edit):** The status card recalculates immediately against the new figure and the same underlying transactions — no re-import, no re-logging.

Failure: Sam enters a non-numeric or zero budget → see State Patterns, "Invalid budget entry."

### Flow 5 — Manage Categories (Sam adds "Subscriptions", later deletes it)

1. Sam wants to track a new Netflix-style subscription separately from Bills. Goes to Categories (via Settings).
2. Sees the 5 defaults listed with icons; taps "+ Add category," names it "Subscriptions," picks an icon, saves.
3. **Climax:** "Subscriptions" is immediately available everywhere a category appears — Quick Add's category row and Search & Filter's category filter — with no separate sync step.
4. Months later, Sam stops using the category and deletes it from the Categories screen.
5. **Climax:** Any past transactions that were tagged "Subscriptions" are automatically reassigned to "Uncategorized" — nothing is blocked, nothing needs manual cleanup, and those transactions still show up correctly in Dashboard totals and Search & Filter.

Failure: Sam tries to delete one of the 5 default categories (Food, Transport, Shopping, Bills, Entertainment) — no delete affordance is shown for defaults at all, so there's nothing to fail; renaming/re-iconing is the only edit path offered for them.

### Flow 6 — Search & Filter Expenses (Sam, checking Food spending "last week")

1. Sam wonders how much they spent on Food last week. Opens Search & Filter from the Dashboard.
2. Sets the date range to "Last 7 days" and the category filter to "Food"; optionally adds a keyword.
3. **Climax:** The matching transaction list and a running total appear together, instantly — Sam sees both the individual purchases (with their required descriptions, e.g. "Coffee," "Lunch with friends") and the total for that slice without doing any mental math.

Failure: no transactions match the filter combination → "No matching transactions," filters remain visible and editable so Sam can immediately loosen them (e.g. widen the date range) rather than hitting a dead end.
