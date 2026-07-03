---
title: "PRD Input-Reconciliation Pass: PRFAQ → PRD"
target: "prd.md (prd-bmad-expense-tracker-2026-07-03)"
sources:
  - "_bmad-output/planning-artifacts/prfaq-bmad-expense-tracker.md"
  - "_bmad-output/planning-artifacts/prfaq-bmad-expense-tracker-distillate.md"
cross-referenced:
  - "_bmad-output/project-context.md"
  - "_bmad-output/planning-artifacts/prds/prd-bmad-expense-tracker-2026-07-03/.memlog.md"
created: "2026-07-03"
---

# Reconciliation: PRFAQ → PRD

## Method

Read the full PRFAQ (press release, Customer FAQ, Internal FAQ, Verdict, and all four stages of coaching notes) and the distillate side by side against the finalized PRD. Checked not just "is this fact present somewhere" but "does the PRD's framing carry the same intent, nuance, and rationale the PRFAQ established." Cross-referenced `project-context.md` and the PRD's own `.memlog.md` to distinguish *deliberate, confirmed* deviations from *silent* drops.

## Overall Assessment

The PRD is a faithful, disciplined distillation of the PRFAQ's functional content — the domain model (derived totals, transaction-date period assignment), scope boundaries, cut order, and most open risks all survive intact, several verbatim. The gaps below are concentrated exactly where the task brief predicted: qualitative rationale, rejected alternatives, and one significant undisclosed tension between the PRFAQ's public positioning claim and what the PRD (correctly, per later UX decisions) actually specifies.

---

## Gap 1 (High) — The PRD never discloses that it reverses the PRFAQ's headline "no red" claim

The PRFAQ's central marketing claim, repeated three times, is the literal absence of red/alarm framing:
- Title dek: *"...no red banners, no guilt-trip copy..."*
- Press release body: *"...without a single red warning telling them they've failed."*
- Customer FAQ (Koody question): *"...no red 'OVER BUDGET' banners, no pass/fail framing..."*

This was later, legitimately overridden during UX design — `project-context.md` line 44 explicitly logs it as an **OVERRIDE**: *"budget status now uses visible green/amber/red... This deliberately reverses the earlier 'no red banners' market-research recommendation... color communicates severity, language never does."*

The PRD correctly encodes the *override* (FR-4: "red (>100%, 'exceeded')"; §4 IA: "the three Budget Status colors reserved exclusively for severity") — that part is technically faithful to the latest upstream decision. But the PRD **never states that this is a reversal of the PRFAQ's public claim**, unlike `project-context.md`, which flags it explicitly with an "OVERRIDE" callout and a superseded-rule note. The PRD's own Vision (§1) still reads *"...without bank-linking, receipt scanning, or guilt"* with no caveat.

Read on its own, the PRD gives no signal that the product will, in fact, show a red status when over budget — the exact thing the PRFAQ tells prospective users it deliberately does *not* do. Anyone validating the PRD against the PRFAQ (e.g., a future reviewer, or Raha revisiting the press release before "launch") would find an unreconciled contradiction. This is the most consequential finding of this pass: not a missing fact, but a missing acknowledgment that a fact changed.

**Recommendation:** Add an explicit note near FR-4 (or in §1 Vision) stating that visible red is now used for the "exceeded" state, that this reverses the PRFAQ's "no red banner" framing, and that the non-shaming intent is instead carried entirely by copy tone — mirroring the honesty `project-context.md` already models.

---

## Gap 2 (Medium-High) — The shame-vs-guilt distinction (the actual design rationale) is dropped

The PRFAQ's Stage 1 coaching notes carry a specific, non-obvious psychological finding from the design-thinking work: *"shame produces stronger avoidance than guilt"* and the problem is framed as *"a shame spiral (identity judgment, not just guilt/behavior note)."* This distinction — shame as identity-level judgment vs. guilt as behavior-level feedback — is the actual reasoning behind why status copy must be "factual and non-judgmental" rather than merely polite or encouraging.

The PRD's copy-tone requirement (FR-4 Consequences, §4 IA) preserves the *rule* ("factual and non-judgmental at every severity level... never blame-framed") but not the *rationale*. A future implementer calibrating edge-case copy (e.g., a stern-adjacent phrasing that isn't overtly "blame" but still reads as identity judgment) has no record of the shame/guilt distinction to reason from — only the surface rule.

**Recommendation:** A one-line addition to §4 or FR-4's Notes — "copy must avoid identity-level judgment (shame), not just overt blame; behavior-level factual statements like 'exceeded by ₹250' are the target, per design-thinking research" — would preserve the calibration standard, not just the outcome.

---

## Gap 3 (Medium) — Gate 2's future-facing decision rule for Lapse Recovery isn't carried forward

The PRFAQ Internal FAQ names its *"load-bearing decision"* as committing to two literal blocking gates: Gate 1 (recruit 5–7 real testers, dogfooding only as a fallback if recruiting fails) and Gate 2 (*"only build Lapse Recovery if testers independently name lapse-guilt themselves, unprompted"*).

The PRD's substitution of self-dogfooding for the testing gate (§9 SM-1, §10 item 4) is **not** a silent drop — `.memlog.md` confirms it as a deliberate, user-confirmed decision ("descoped from PRFAQ's stalled 5-7 user formal usability test to self-dogfooding... avoids a 4th stall"), and the PRD is self-aware enough to call it a "replacement" and flag the residual risk in §10 item 4. That part reconciles cleanly.

What's missing is narrower: Gate 2's specific *evidentiary bar for ever building Lapse Recovery* doesn't appear anywhere in the PRD — not in §7 Non-Goals, not in §8.2 Out of Scope, not in §10 Open Questions. Since Lapse Recovery is correctly out of MVP scope, this may look inconsequential today, but it was explicitly framed in the PRFAQ as governing *future* (v2+) work, not just MVP scope. A future PM revisiting "should we build lapse-recovery now" has no record that the prior commitment was "only if users independently, unprompted, name lapse-guilt" — a fairly specific and easy-to-forget bar.

**Recommendation:** Add a line to §10 Open Questions or §8.2: "If Lapse Recovery is considered for v2, the PRFAQ's Gate 2 criterion applies: build only if users independently and unprompted name lapse-guilt as a problem during dogfooding/testing — not simply because it seems like a natural next feature."

---

## Gap 4 (Low-Medium) — Two explicitly-rejected alternative concepts aren't recorded as "don't re-propose"

PRFAQ Stage 1 coaching notes name three ideas *"explicitly deferred past V1 during design-thinking ideation"*: a home-screen widget shortcut, a user-selectable budget-tone toggle ("chill mode"/"strict mode"), and voice-to-log parsing.

The PRD's Non-Goals (§7) captures voice/freeform capture ("No voice or freeform natural-language capture") but omits the other two entirely. Neither the home-screen widget nor the tone-toggle idea appears anywhere in the PRD (§7 Non-Goals, §8.2 Out of Scope, or §10 Open Questions).

This is lower stakes than Gaps 1–3 since both ideas are unlikely to resurface accidentally, but the PRD's own Non-Goals section exists precisely to prevent re-litigating settled scope decisions — and two of the three settled decisions from this research chain didn't make the list.

**Recommendation:** Add both to §7 Non-Goals for completeness, or note them in §10 as "previously considered and rejected, not to be re-proposed without new evidence."

---

## Gap 5 (Low) — Competitive nuance thinned to a single competitor; "pace" framing left unaddressed

The PRFAQ's competitive reasoning (why "manual-only" was rejected as the headline claim) rests on three findings: **Koody** (manual-only positioning now table-stakes), **Finny** (pivoted to AI-parsed/voice entry, changing what "fast" competitively means), and **PocketGuard's "Pace"** feature (an automation-first incumbent already iterating toward status/pace indicators, narrowing but not closing the differentiation window). The PRD's Open Question 3 discusses only Koody; Finny and PocketGuard — and the "narrowing, not closing" nuance — don't appear, so the fuller competitive picture behind the positioning choice is compressed.

Separately, and at lower confidence: the PRFAQ's illustrative copy — *"about $40 left at this pace"* — implies a rate/time-normalized budget status (spending velocity relative to how far into the period you are), echoed loosely in the PRD's own JTBD §2.1 ("see... whether I'm on track for the month"). The PRD's actual Budget Status rule (FR-4) is a flat percentage-of-budget-consumed threshold with no time-of-period normalization. This may simply be PRFAQ copywriting flourish that was never meant literally (both `project-context.md` and the UX spec also use flat percentage thresholds, suggesting the simplification happened upstream of the PRD, not within it) — but it's worth a deliberate sanity check that "on track" in the JTBD isn't quietly overpromising pace-awareness the feature doesn't deliver.

**Recommendation:** Optional — expand Open Question 3 to name Finny/PocketGuard if the fuller competitive rationale is useful context for future readers; confirm the JTBD's "on track" phrasing isn't read as implying pace-normalization that FR-4 doesn't provide.

---

## What Reconciled Cleanly (no action needed)

For completeness, these PRFAQ elements were checked and found faithfully carried into the PRD:
- Derived-totals-never-cached invariant and its structural enforcement (schema has no totals column) — §5.2, §6.
- Transaction-date-determines-period rule, including the backdating-into-a-closed-period case — Glossary, FR-3, UJ-2.
- Frequent-expenses shelf as one-tap zero-typing logging, ordering/cap left open — FR-1, §10 item 1.
- Category CRUD rules (5 non-deletable defaults, auto-reassignment to Uncategorized on delete, no confirmation step) — FR-7–FR-9.
- Cut order under timeline pressure (charts → rollover → category-edit polish; core loop never cut) — §8.2.
- Rollover: no rollover for MVP, confirmed — §8.2, Glossary.
- Hosting/persistence as a load-bearing (not purely demo) requirement, given the "tool I actually use" real end-goal from the Internal FAQ — §6 Persistence NFR.
- The "treat as demo" vs. "tool I actually use" tension named in the Verdict — resolved explicitly in PRD §1 ("Raha intends to keep using long-term, not just demo").
- No quantitative success metric existed upstream; the PRD is transparent that it's defining the first ones — §9 preamble.
- Discipline against implying an unbuilt forgiveness/lapse-recovery feature — §7's explicit "the non-shaming design goal is realized entirely through FR-4's status/copy rules... not a separate feature" mirrors the PRFAQ's own rejected-headline discipline.
