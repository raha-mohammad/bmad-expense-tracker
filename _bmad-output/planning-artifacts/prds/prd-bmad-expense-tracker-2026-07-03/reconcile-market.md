---
title: PRD Input Reconciliation — Market Research
prd: prd-bmad-expense-tracker-2026-07-03/prd.md
input: research/market-personal-expense-tracking-app-market-research-2026-07-02.md
date: 2026-07-03
---

# Reconciliation: PRD vs. Market Research

## Scope

Checks whether the PRD's competitive framing (§10, item 3) and target-user framing (§1 Vision, §2, FR-4) are consistent with `market-personal-expense-tracking-app-market-research-2026-07-02.md`. Traced "Koody" and "Finny" across the full artifact chain (market research → PRFAQ → PRD) to establish provenance, since the market research itself does not name either as an analyzed competitor.

---

## 1. "Koody" framing (PRD §10, item 3) is not traceable to the market research input

PRD §10 item 3 reads: *"Competitive differentiation vs. Koody — remains an unproven hypothesis; no competitor currently combines fast manual entry with genuinely non-shaming lapse recovery..."*

The market research's **Competitive Landscape** section reviews a specific, named set of apps: YNAB, Monarch, Rocket Money, Copilot, PocketGuard, Money Manager, Monefy, DailyBean, Spendee, Toshl. **Koody appears nowhere in the document** — not in the competitor list, not in the Market Gap/Opportunity section, not in the Strategic Recommendations. The document's own differentiation conclusion ("no competitor combines fast manual entry with genuinely non-shaming lapse recovery") is a **market-wide** synthesis across that whole set (speed-first: Monefy/DailyBean; shame-reduction-first: YNAB/The Budgeting App) — it is explicitly not about any single named competitor.

Tracing further: Koody was introduced later, via a **separate, ad hoc web-research pass done during the PRFAQ's Stage 1** (see `ux-designs/ux-bmad-expense-tracker-2026-07-02/reconcile-prfaq.md`, "Surprising finding from live web research" — Koody is described there as a "new/repositioned competitor" that "emerged since" the 2026-07-02 market research was written). That finding was folded into the PRFAQ's Customer FAQ and Internal FAQ, but **never folded back into the market-research document itself**.

This matters because PRD §0 explicitly frames the market research as one of the documents that "supply the domain model and constraints" for this PRD. A reader auditing PRD §10 item 3 against the named market-research input would find no support for it there — not because the claim is fabricated, but because its actual source (PRFAQ Stage 1 supplementary research) isn't cited or distinguished from the formal market-research artifact. The provenance chain is real but undocumented in the PRD.

**Recommendation:** Either (a) cite the PRFAQ's Stage 1 web research explicitly as the source for the Koody claim in §10, rather than implying it flows from the market research, or (b) fold the Koody finding back into the market-research document as a dated addendum so the "market research" label stays accurate for future readers.

## 2. The differentiation claim in §10 may already be stale relative to the PRD's own source trail

This compounds finding #1. The market research's conclusion — "no competitor combines fast manual entry with non-shaming recovery" — was accurate for the app set it reviewed. But the very research that surfaced Koody (PRFAQ Stage 1) also concluded Koody is a **"near-exact positioning match to 'manual-first, no bank sync,' closer than Finny was at brief-writing time"** and explicitly noted the team then chose to *de-emphasize* manual-first as a headline claim because "Koody now occupies that ground too squarely to lead with it" (`reconcile-prfaq.md`).

PRD §10 item 3 preserves the market research's original, broader, more confident framing ("no competitor currently combines...") almost verbatim, without surfacing that the PRD's own upstream research chain already found a competitor that narrows this gap. The open question as written reads more optimistic about whitespace than the full research trail (market research + PRFAQ Stage 1 together) actually supports.

**Recommendation:** Tighten §10 item 3's wording to acknowledge Koody specifically narrows (not closes) the gap, consistent with how the PRFAQ itself already hedges this ("the concept no longer has a clean 'why not Koody' answer beyond an unproven design bet").

## 3. "Finny" — confirmed non-issue for the PRD, but worth noting for the chain

Per the task framing: in the market research, "Finny" (getfinny.app) appears only as a **citation source** — the publisher of "Best Free Expense Tracker Apps" and "Best Simple Budget Apps" roundup articles used as references — not as an app analyzed in the competitive landscape itself. Confirmed: the PRD does not mention Finny anywhere, so there's no direct PRD-level inconsistency here. Flagging only because it shows the same pattern as Koody: PRFAQ Stage 1 later promoted Finny to an actual named/analyzed competitor ("Finny appears to have pivoted toward AI-parsed natural-language/voice entry"), again outside the formal market-research document. No PRD action needed, but if Finny is ever named in a future PRD revision, the same provenance caveat as Koody would apply.

## 4. FR-2's mandatory description field runs counter to the market research's "essential fields only" recommendation

Market research Strategic Recommendation #2: *"Keep manual entry to the essential fields. Amount and (optional, fast) category — defer notes, tags, and granular categories to be optional/skippable. The friction research... points to category selection, not the money-entry itself, as the main drop-off point."*

PRD FR-2 (Manual transaction entry) states: *"Save is disabled until amount > 0, a Category is selected, **and description is non-empty**."* This makes free-text description a hard, required blocker on every manual transaction — precisely the kind of extra required field the market research recommended deferring or making optional. The PRD does address the research's specific category-dropdown friction well (button/icon control, pre-selected last-used category), but the required-description decision isn't acknowledged anywhere as a deliberate deviation from the research's "essential fields only" guidance, and no rationale is given for why description was elevated to required rather than optional.

**Recommendation:** Either flag this in §9 Open Questions as a conscious trade-off (e.g., "required description improves searchability in FR-10 at the cost of the research's optional-field recommendation"), or reconsider making description optional for manual entry, consistent with the chip path (FR-1) already requiring zero typing.

## 5. Minor: guilt vs. shame distinction not fully threaded into itemized-data surfaces

Market research draws a specific distinction: raw itemized spend shown without context (e.g., "$47 on coffee") tends to trigger *shame* ("I am bad with money") more than *guilt* ("I overspent"), which produces a stronger avoidance response. Recommendation #3 favors "observational framing" over "pass/fail framing."

PRD FR-4 addresses this well for the **aggregate** Budget Status (color-coded but factual, non-blame copy: "Budget exceeded by ₹250"). But Search & Filter (FR-10) and the Dashboard's category breakdown expose **itemized/raw** transaction data with no equivalent framing guardrail specified. This is not necessarily a defect — item-level browsing may be a different UX context than status reporting — but the research's most specific psychological distinction (guilt vs. shame, driven by raw itemization) isn't explicitly carried into every surface where itemized data appears.

---

## Where the PRD reconciles well (confirmed, not gaps)

- **Vision (§1)** mirrors the market research's core insight almost verbatim: "not day one, but the day after a missed entry, when red banners, streak breaks, and catch-up burdens push people to quietly stop opening the app" directly reflects the guilt→avoidance→disengagement cycle and Recommendation #1 ("design for restart, not just entry... no catch-up burden, no guilt copy, no red backlog indicator").
- **UJ-3 and FR-4** operationalize this: the routine-check and after-a-gap variants resolve identically by design, with no push/toast/modal alerts — directly matching Recommendation #3 (avoid hard limits/red-flag warnings; observational over pass/fail framing) at the aggregate level.
- **Non-Goals (§7)** — "No dedicated lapse-recovery or re-entry screen, streak counters, or badges" — is close to a direct restatement of Recommendation #1.
- **Persona framing (§2.3)** — Sam, 20, first-time budgeter, explicitly tagged as representative of "the Anxious Starter segment" — matches the market research's Segment 1 definition (student, 18–22, low/irregular income, wants near-zero setup, forgiving feedback, high attrition risk if setup precedes value) closely, including age range.
- **UJ-4 sequencing** (budget setup happens only after the user has already logged transactions) matches Recommendation #4 ("Defer setup, not value").
