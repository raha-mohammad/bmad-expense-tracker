---
stepsCompleted: [1, 2, 6]
inputDocuments: []
workflowType: 'research'
lastStep: 6
research_type: 'domain'
research_topic: 'Expense Tracking domain — for a simple manual expense tracker MVP'
research_goals: 'Build enough domain knowledge to inform a simple MVP: domain overview, core concepts & terminology, typical user workflow, essential business rules, and best practices for a simple MVP. Explicitly excludes competitor analysis, feature suggestions, implementation details, and deep technical discussions.'
user_name: 'Raha'
date: '2026-07-02'
web_research_enabled: true
source_verification: true
---

# Research Report: domain

**Date:** 2026-07-02
**Author:** Raha
**Research Type:** domain

---

## Research Overview

This research builds domain knowledge for a simple, manual-first personal expense tracker. Per user direction, scope was deliberately narrowed to five areas — domain overview, core concepts/terminology, typical user workflow, essential business rules, and MVP best practices — and explicitly excluded industry/competitor analysis, regulatory review, and technology-trend research, none of which the skill's default template areas were relevant to this goal.

The core domain insight: expense tracking is a lighter discipline than accounting — it needs accurate transaction capture, category grouping, and period totals, not double-entry bookkeeping. The workflow (Log → Categorize → Accumulate → Review → Adjust) only produces value if logging is near-zero-effort, and the domain's essential business rules (one category per transaction, transaction-date-based periods, always-derived totals) exist specifically to keep the tracker accurate without adding manual overhead.

See **Research Synthesis** below for how these findings translate directly into MVP guidance.

## Domain Research Scope Confirmation

**Research Topic:** Expense Tracking domain — for a simple manual expense tracker MVP
**Research Goals:** Build enough domain knowledge to inform a simple MVP: domain overview, core concepts & terminology, typical user workflow, essential business rules, and best practices for a simple MVP.

**Domain Research Scope (reduced, per user direction):**

- Domain Overview
- Core Concepts & Terminology
- Typical User Workflow
- Essential Business Rules
- Best Practices for a Simple MVP

**Explicitly excluded:** competitor/industry analysis, regulatory environment, technology trends, economic factors, supply chain analysis, feature suggestions, implementation details.

**Scope Confirmed:** 2026-07-02

---

## Domain Analysis: Expense Tracking

### Domain Overview

Expense tracking is the systematic process of recording, categorizing, and monitoring what money leaves a person's (or business's) hands — capturing each spend as a discrete transaction, grouping transactions into meaningful categories, and reviewing the totals over a period of time to understand and manage spending. For a personal/manual tracker, the domain is intentionally narrower than full accounting: it doesn't need double-entry bookkeeping or a full general ledger — it needs an accurate, low-friction record of outflows tied to categories and time periods, from which simple totals and comparisons can be derived.
_Source: [What is Expense Tracking? — Navan](https://navan.com/resources/glossary/what-is-expense-tracking), [What is Expense Tracking? — FreshBooks](https://www.freshbooks.com/hub/accounting/expense-tracking)_

### Core Concepts & Terminology

- **Transaction (Expense Entry)**: A single recorded spend — minimally a date, an amount, and a category.
- **Category**: A label grouping similar transactions (e.g., Food, Transport) so spending can be summarized meaningfully; well-chosen categories should be "immediately obvious" for the vast majority of transactions — if a user has to stop and think which category something belongs in, the category set is wrong.
- **Fixed vs. Periodic vs. Variable expenses**: Fixed = predictable, recurring (rent); periodic = regular but less frequent than fixed (insurance); variable = irregular, changes each time (groceries, entertainment). This distinction is a domain concept, not a UI feature — it affects how a category's "normal" spending range should be interpreted.
- **Budget Period**: The time window (commonly monthly, sometimes weekly) against which spending is measured and compared. Aligning the period to how a person actually gets paid (weekly, biweekly) rather than defaulting to calendar-month is a recognized domain best practice, especially for irregular-income users.
- **Reconciliation** (from broader financial-tracking terminology): Verifying that recorded transactions match reality (e.g., against a bank/credit statement). This is a full-accounting concept — for a manual, non-bank-linked tracker, "reconciliation" isn't a formal domain requirement, but the underlying idea (the log should reflect real spending) still motivates why manual entry needs to be low-friction enough to actually happen.
- **Rollover**: Carrying an unspent (or overspent) category balance from one period into the next, either into the same category or a general pool. This exists specifically to handle "lumpy" non-monthly expenses (gifts, repairs) without requiring a full new budgeting method.
_Source: [Expense Tracking and Categorization — Quicken](https://www.quicken.com/blog/expense-tracking-and-categorization-for-small-businesses/), [What Is a Rollover Budget? — Lunch Money](https://lunchmoney.app/blog/what-is-a-budget-rollover), [Rollover budgeting — PocketSmith](https://learn.pocketsmith.com/article/1303-rollover-budgeting)_

### Typical User Workflow

The domain's standard workflow, independent of any specific app, is:

1. **Log** — record a transaction as close to the moment of spending as possible (amount + category at minimum; date defaults to today).
2. **Categorize** — assign the transaction to an existing category (creating a new one only when genuinely needed).
3. **Accumulate** — the system totals transactions by category and by period automatically; the user does not do this math manually.
4. **Review** — periodically (best practice: a short weekly check-in, not just a monthly deep-dive) look at category totals versus what's normal/budgeted.
5. **Adjust** — change future behavior, category budgets, or (rarely) reclassify past entries based on what the review shows.

The critical domain insight is that steps 1–3 must be near-zero-effort, because step 4 (review) only produces value if step 1 (logging) actually happened consistently — a workflow that's accurate but effortful to log will simply not be followed.
_Source: [8 Best Free Expense Tracker Templates — Tiller](https://tiller.com/5-steps-for-easily-tracking-expenses-in-a-spreadsheet/), [Monthly Expense Tracker 2026 — No More Debts](https://nomoredebts.org/financial-education/monthly-expense-tracker)_

### Essential Business Rules

Domain-level rules that should hold regardless of implementation:

1. Every transaction belongs to exactly one category (no split/multi-category entries needed for a simple tracker).
2. Every transaction belongs to exactly one budget period, determined by its date, not its entry date (a user logging Tuesday's coffee on Thursday should still see it counted in Tuesday's period).
3. Category and period totals are always derived (sum of transactions), never manually entered or independently edited — this is the domain's core integrity rule; a total that can drift from its underlying transactions defeats the purpose of tracking.
4. A budget (if set) applies to a category over a defined period; "spent," "remaining," and "over/under" are all computed from rule 3, not stored as separate facts.
5. Rollover (if supported) is an explicit rule choice, not an accident — a period's leftover/overage either carries forward or resets; the domain requires this to be a deliberate, visible decision rather than silently applied.
_Source: synthesized from terminology and rollover sources above._

### Best Practices for a Simple MVP

- **Minimize fields per entry**: date, amount, category is the practical minimum; everything else (notes, description) should be optional. Entry that takes longer than ~30 seconds causes people to defer logging, which breaks the domain's core requirement of consistent capture.
- **Make categories few and obvious**: a short, recognizable category list beats a granular one — granularity that forces a decision on every entry actively works against consistent logging.
- **Default to the common case**: today's date pre-filled, last-used or most-common category suggested — reduces the entry to essentially "type the amount, confirm."
- **Favor short, frequent review over rare, heavy review**: a brief periodic check-in (reviewing running totals) is more sustainable and more aligned with the domain's actual value (awareness) than an infrequent deep audit.
- **Keep totals always-derived**: never let a summary number be editable independently of its transactions — this is the single rule most worth enforcing even in a minimal MVP, since it's what keeps the tracker trustworthy.
_Source: [8 Best Free Expense Tracker Templates — Tiller](https://tiller.com/5-steps-for-easily-tracking-expenses-in-a-spreadsheet/), [10 Simplest Expense Trackers — The Bricks](https://www.thebricks.com/resources/simple-expense-tracker)_

---

## Research Synthesis

_Scope note: kept minimal per user direction — no market/tech/regulatory synthesis, since those sections were not researched by design._

### Key Findings

- Expense tracking is a **transaction → category → period → total** model; a personal manual tracker only needs this, not full accounting machinery.
- The workflow only works if **logging is near-frictionless** — every other domain step (review, adjust) depends on consistent capture happening first.
- The single non-negotiable business rule is that **totals must always be derived from transactions**, never independently stored/edited — this is what keeps a simple tracker trustworthy.
- **Rollover** is the one domain concept genuinely worth a deliberate decision even in an MVP (carry forward vs. reset) — everything else (reconciliation, fixed/periodic/variable typing) is optional depth for later.

### How This Applies to the MVP

- Transaction = date + amount + category (matches the already-defined MVP entry fields in the brainstorm intent).
- Category totals and budget remaining should be computed values, never separately stored/editable fields.
- Budget period should default to monthly but conceptually be tied to the category+period pairing, not hardcoded to calendar month only — worth keeping in mind if weekly/irregular-income users are ever a target.
- No rollover behavior is required for MVP, but if/when it's added, it must be an explicit, visible rule (not silent), per the domain's business rules above.

### Next Steps

- Carry the terminology and business rules from this document directly into the PRD/data-model definitions (transaction, category, budget period) so implementation stays consistent with domain logic.
- No further domain research is needed for the MVP scope; deeper research (e.g., rollover strategies, multi-currency, reconciliation) can be revisited if/when those features are considered for V2+.

---

**Research Completion Date:** 2026-07-02
**Source Verification:** All findings cited with sources inline throughout the document above.
**Scope:** Narrowed by user direction to domain overview, terminology, workflow, business rules, and MVP best practices only — industry, regulatory, technology-trend, and competitive analysis were intentionally excluded.
