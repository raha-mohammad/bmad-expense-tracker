# Domain Reconciliation Pass — PRD vs. Domain Research

**PRD:** `_bmad-output/planning-artifacts/prds/prd-bmad-expense-tracker-2026-07-03/prd.md`
**Source input:** `_bmad-output/planning-artifacts/research/domain-expense-tracking-research-2026-07-02.md`
**Date:** 2026-07-03
**Scope:** Verify the five domain rules/invariants from research are correctly and precisely reflected in PRD §3 (Glossary) and §5 (Features/FRs), and assess whether PRD §8.2's "no rollover for MVP" decision is consistent with the research's framing of rollover as "the one domain concept genuinely worth a deliberate decision."

---

## 1. Rule-by-Rule Traceability

| # | Research invariant | Research source | PRD reflection | Verdict |
|---|---|---|---|---|
| 1 | Transaction → Category → Period → Total model | Research Synthesis, Key Findings | §3 Glossary defines Transaction, Category, Budget Period, Budget, Derived Total as a coherent chain; §5 FRs implement each stage | **Reflected precisely** |
| 2 | One category per transaction, no splits | Essential Business Rules #1 | §3 Category: "Exactly one Category per Transaction (no splits)"; FR-2 Out of Scope explicitly excludes multi-category splits | **Reflected precisely** |
| 3 | Transaction's period is determined by transaction date, never entry date | Essential Business Rules #2 | §3 Budget Period: "determined by its transaction date, never its entry/logging date"; FR-3 is a dedicated FR with the Tuesday/Thursday-style backdating consequence spelled out; UJ-2 climax/resolution restates it | **Reflected precisely — strengthened**, not just matched. FR-3 goes further than research by covering the "closed period" edge case explicitly. |
| 4 | Totals are always derived (sum of transactions), never stored or independently edited | Essential Business Rules #3, Best Practices ("keep totals always-derived... single rule most worth enforcing") | §3 Derived Total: "computed live... Never stored, cached, or independently edited — the domain's core integrity invariant"; FR-5 consequence: "No total... is ever stored as its own column or field"; §6 NFR "Data integrity guardrail" elevates this to a schema-level constraint (no totals column exists at all) | **Reflected precisely — strengthened.** PRD converts the domain rule into an architectural guardrail beyond what research asked for. |
| 5 | Rollover, if supported, must be an explicit, deliberate, visible rule — never silent | Essential Business Rules #5, Research Synthesis ("the one domain concept genuinely worth a deliberate decision") | §3 Glossary Rollover entry; §8.2 decision; §10 Open Question 5 resolution | **Reflected, but see §2 below for a precision gap on "visible."** |

Rules 1–4 are faithfully and in several cases more rigorously captured than the source research required. The one rule warranting closer scrutiny is #5 (rollover), addressed next.

---

## 2. Rollover Decision Consistency Check

Research frames rollover uniquely among the five rules: it's the only concept explicitly called out in the Research Synthesis as "genuinely worth a deliberate decision even in an MVP (carry forward vs. reset)." The underlying business rule (#5) has two components:

1. **Deliberateness** — the carry-forward-vs-reset choice must be made consciously, not defaulted into by omission.
2. **Visibility** — per the rule's own text, "the domain requires this to be a deliberate, visible decision rather than silently applied."

**Deliberateness — satisfied.** The PRD does not silently omit rollover. It surfaces the question, resolves it explicitly, and leaves a paper trail: §8.2 states "confirmed: no rollover for MVP," §10 Open Question 5 is struck through with "resolved: no rollover for MVP (§8.2)," and §3 Glossary defines Rollover on its own terms even though it isn't built. This is exactly the "deliberate decision, not an accident" the research calls for, at the PM-artifact level.

**Visibility — only partially satisfied.** Here is the precision gap: the PRD's Glossary applies the "explicit, visible rule, never silent" language *only* to a hypothetical future rollover feature — "**Rollover** — ... Not implemented in MVP (§8); if added later, must be an explicit, visible rule, never silent." It does not extend the visibility requirement to the reset-to-zero behavior that MVP actually ships with. Research rule #5 frames the choice as binary — "a period's leftover/overage either carries forward **or resets**" — and requires *that choice*, whichever branch is taken, to be visible rather than silently applied. "Resets to zero" is itself one of the two branches, not a neutral default outside the rule's scope.

Concretely: no FR or NFR in the PRD requires the product to communicate to Sam that unspent budget does not carry over. FR-4 (Live Budget Status) and FR-6 (Set and edit budget) both cover computation and validation, but neither includes a consequence like "Budget Status or copy indicates the budget does not carry over between Periods." A first-time user has no in-product signal that a ₹2,000 surplus this month disappears on the 1st rather than rolling forward — the decision is fully visible in the PRD/spec artifacts, but not necessarily visible to the end user inside the product itself, which is the layer the research rule is actually protecting ("silently applied" describes user-facing silence, not spec silence).

This is a precision gap, not a scope error — "no rollover for MVP" remains a defensible, well-documented product decision. The gap is that the PRD's own articulation of the "visible, not silent" requirement (in the Glossary) scopes that requirement narrower than the research rule it's paraphrasing.

---

## 3. Additional Gaps Found

Beyond the five explicitly named invariants, two further reconciliation issues surfaced while reading Glossary/FRs against the full research document (not just the Essential Business Rules subsection):

### 3.1 Mandatory description field contradicts research's "optional" best practice
Research's "Best Practices for a Simple MVP" section is explicit: *"Minimize fields per entry: date, amount, category is the practical minimum; everything else (notes, description) should be optional. Entry that takes longer than ~30 seconds causes people to defer logging, which breaks the domain's core requirement of consistent capture."*

The PRD does the opposite for its manual-entry path. §3 Glossary lists description as a core Transaction attribute (not flagged optional), and FR-2's consequence states plainly: *"Save is disabled until amount > 0, a Category is selected, and description is non-empty."* UJ-2's Path also lists "type required description" as a mandatory step. This directly conflicts with the research's explicit MVP guidance to keep notes/description optional to protect logging speed — the same speed goal the PRD itself centers in §1 Vision ("Quick-add takes under 5 seconds"). The chip path (FR-1) avoids this by pre-filling description, but the manual-entry path — the one path research's field-minimization advice is most relevant to — requires typing a description on every save.

### 3.2 Category Glossary entry is inconsistent with the resolved single-overall-budget model
§3 Glossary defines **Category** as "A label grouping Transactions for reporting **and budgeting**." But §3's own **Budget** entry, §5.3 FR-6, and §11 Assumptions Index all confirm the opposite: budgeting in this MVP happens at the Period level only, not per-Category — "Category breakdown on the Dashboard is informational only, not separately budgeted" (§5.2), and §11 explicitly notes the "single overall budget per Period" assumption was confirmed during drafting.

This is an internal Glossary inconsistency (Category's own definition claims a "budgeting" role that Budget's definition, two lines later, explicitly denies), and it's also a soft divergence from the research's Essential Business Rule #4 framing, which describes budgets as applying "to a category over a defined period." The PRD's single-overall-budget choice is a legitimate, documented MVP simplification — but the Category Glossary entry wasn't updated to match it precisely.

### 3.3 Minor: §8.2 cut-priority framing blurs "rejected" vs. "deferred"
§8.2 lists Rollover as "Second to cut if timeline slips" in the same ranked list as Charts/insights ("deferred, not rejected... First to cut if timeline slips"). But Rollover isn't a candidate that ships-unless-time-runs-out — it's already confirmed absent from MVP regardless of timeline. Grouping a "confirmed: never happening in MVP" decision into a timeline-contingent cut-priority list alongside genuinely deferred items is a structural inconsistency (not a domain-accuracy issue) worth a copyedit pass — e.g. separating "decisions already made" from "scope that may be cut under time pressure."

---

## 4. Summary Verdict

- Rules 1–4 (transaction-category-period-total model, one-category-per-transaction, transaction-date-determines-period, derived-never-stored totals) are all reflected **correctly and precisely** in §3 and §5 — in the case of rules 3 and 4, the PRD exceeds the research's bar by turning them into testable FR consequences and an architectural NFR guardrail.
- Rule 5 (rollover must be explicit/deliberate/visible) is **deliberately** decided (satisfies half the rule) but the PRD's own "visible, not silent" language is scoped only to a *future* rollover feature, not applied to the *current* reset-to-zero behavior users will actually experience — a precision gap worth closing with one added FR consequence (e.g., Budget Status or Budget Settings copy noting budgets reset each Period).
- Two additional gaps outside the five named rules: the mandatory-description requirement in FR-2 contradicts research's explicit "keep notes/description optional" MVP best practice, and the Category Glossary entry's "for reporting and budgeting" phrasing is inconsistent with the PRD's own resolved single-overall-budget model.
