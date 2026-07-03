---
title: PRD ↔ UX Reconciliation
purpose: >
  Input-reconciliation pass for prd.md against its source UX spec
  (DESIGN.md + EXPERIENCE.md, ux-bmad-expense-tracker-2026-07-02).
  Finds material content present in the UX spec that did not survive
  condensation into the PRD's UJ-1..UJ-6 / FR-1..FR-10 / Cross-Cutting NFRs.
created: 2026-07-03
---

# PRD ↔ UX Reconciliation

**Inputs compared:**
- PRD: `_bmad-output/planning-artifacts/prds/prd-bmad-expense-tracker-2026-07-03/prd.md`
- UX spec: `_bmad-output/planning-artifacts/ux-designs/ux-bmad-expense-tracker-2026-07-02/DESIGN.md` + `EXPERIENCE.md`

**Method:** Read both UX documents in full (Foundation, IA, Voice & Tone, Component Patterns, State Patterns, Interaction Primitives, Accessibility Floor, Responsive & Platform, all 6 Key Flows including failure/variant branches) and cross-checked every behavioral commitment against the PRD's Vision, User Journeys, Functional Requirements, Cross-Cutting NFRs, and Non-Goals sections.

Overall the condensation is unusually faithful — nearly every structural invariant (derived totals, transaction-date period assignment, non-deletable defaults, no-confirmation category delete, factual-copy-at-every-severity, no-caching guardrail) made it through intact, often verbatim. The gaps below are the exceptions.

---

## Gap 1 — "Days left in period" is a stated Dashboard content element, not just example copy

**Source:** EXPERIENCE.md Voice and Tone table and both Flow 1 and Flow 3 give budget-status copy as:
- Flow 1 climax: *"₹3,550 of ₹8,000 this month — on pace, 12 days left."*
- Flow 3 climax: *"₹6,800 of ₹8,000 this month — 85% used, 5 days left."*
- Voice/Tone "Do" example: *"₹6,800 of ₹8,000 this month — 85% used, 5 days left"*

The same "days left" phrase recurs independently across two separate flow walkthroughs and the voice/tone table — three independent sources — which suggests it's an intended content element of the status card (days remaining in the current Budget Period), not incidental flavor text picked once and reused.

**PRD:** FR-4 (Live budget status) defines the three severity bands (green/amber/red, thresholds, factual copy) and FR-5 (Derived totals) defines what's computed (category totals, period totals, spent/remaining) — neither mentions days-remaining-in-period as a value the system computes or displays. The Glossary's Budget Period definition doesn't reference it either.

**Risk:** Without an explicit FR/consequence, a builder implementing FR-4 literally would produce a status card showing only "₹X of ₹Y — Zed" with no days-left figure, silently dropping content that appeared in the majority of the UX spec's worked examples.

**Recommendation:** Either add "days remaining in current Period" as a stated Derived Total under FR-5, or explicitly note in Open Questions that it's decorative/optional if that was the intent.

---

## Gap 2 — Disabled-Save accessible-reason rule dropped from Cross-Cutting NFRs

**Source:** EXPERIENCE.md Accessibility Floor is explicit and singles this out:
> "Disabled save button exposes *why* to assistive tech, not a silent disabled state — use `aria-describedby` (pointing at 'Add a description to save') or an equally descriptive `aria-label`; pick one convention and apply it everywhere a control is conditionally disabled."

This is reinforced independently in the State Patterns table ("Save button — empty description... Disabled, with an accessible reason exposed to assistive tech (not just a visually greyed button)") and in Flow 2's failure branch ("announced ... rather than a silent no-op").

**PRD:** §6 Cross-Cutting NFRs' accessibility bullet lists keyboard operability, focus indicators, color-never-sole-signal (with Budget Status and Category selection called out by name), `aria-live` on inline messages, contrast ratios, tap targets, and zoom/reflow — but has no bullet for exposing *why* a disabled control is disabled. FR-2's consequence "Save is disabled until amount > 0..." states the condition but not the AT-facing requirement. UJ-2's edge case ("focus and an audible 'required' state move to the description field") addresses the description-focus behavior but not the disabled-button's own accessible name/description before that point.

**Risk:** This is one of only three accessibility rules the UX spec calls out with a specific implementation mechanism (`aria-describedby`/`aria-label`) rather than a general principle — its specificity suggests it was hard-won (likely from the same friction point that motivated the category-selection checkmark badge). Losing it from the PRD's testable NFR list makes it easy to skip during build.

**Recommendation:** Add to §6's accessibility bullet: "Conditionally-disabled controls (Quick Add Save) expose their disabled reason to assistive tech via `aria-describedby` or `aria-label`, not a silent disabled state."

---

## Gap 3 — "No budget set" is explicitly skippable; "no transactions yet" empty state undefined in PRD

**Source:** EXPERIENCE.md State Patterns table, two rows:
- "No budget set (first use)" → Dashboard: "Neutral prompt in the status-card position... **Skippable — Sam can log expenses with no budget set at all.**"
- "No transactions yet" → Dashboard: "Category breakdown area shows a simple empty message; status card still shows the no-budget or ₹0-of-budget state as applicable. No onboarding tour, no empty-state illustration."

**PRD:** UJ-4's entry-state line mentions the neutral prompt exists ("Dashboard shows a neutral prompt to set a budget (first use)") but never states it's skippable, i.e. that Quick Add and logging work fully with zero budget configured indefinitely. Nor does any FR describe the zero-transactions empty state (what the category-breakdown area shows, and that there's deliberately no onboarding tour/illustration). Neither of these has a testable "Consequence" the way UJ-4's other edge case (invalid budget entry) does.

**Risk:** "Skippable" matters because it's a product-level claim (no forced setup step, contrary to how most budgeting apps behave) — exactly the kind of thing this PRD elsewhere insists on making explicit and testable (cf. FR-4's "no push notification... interrupts the user"). Left implicit, a builder could reasonably gate Quick Add behind budget setup.

**Recommendation:** Add an explicit consequence under FR-6 or a UJ-4 edge case: "Logging transactions requires no budget to be set; Quick Add and Dashboard function fully with budget unset, indefinitely." Add a one-line FR-5 (or new) consequence for the zero-transactions empty state.

---

## Gap 4 — Banned gesture vocabulary omitted from Non-Goals

**Source:** EXPERIENCE.md Interaction Primitives, stated as a deliberate design decision:
> "**No swipe gestures, no long-press menus, no drag-to-reorder** — kept out deliberately; this is a low-gesture-vocabulary product."

**PRD:** §7 Non-Goals (Explicit) is a fairly exhaustive parallel list of comparably fine-grained bans (no bank-linking, no OCR, no push/toast/modal alerts, no multi-user, no offline sync, no dark mode, no native app, no voice/NLP capture, no lapse-recovery screen/streaks, no decorative animation) — but omits the gesture-vocabulary ban entirely, even though it's stated with the same "deliberately kept out" framing as the items that did make the list.

**Risk:** Low implementation risk (nothing in the FRs implies swipe/long-press/drag), but it's a real product constraint (e.g. it rules out "swipe to delete a category" as a future shortcut) that a later contributor wouldn't know was a considered-and-rejected decision rather than an oversight, since it's missing from the one section designed to capture exactly that.

**Recommendation:** Add a Non-Goals bullet: "No swipe gestures, long-press menus, or drag-to-reorder anywhere in the product."

---

## Gap 5 — Frequent-expense chip's "no date edit" constraint softened in FR-1

**Source:** EXPERIENCE.md Component Patterns is explicit that the chip path is single-purpose and time-locked:
> "Tapping a chip logs immediately with **today's date**... zero further input, **no date edit available** (chips are for 'log this right now')."
> Date field row, separately: "Not present on the chip fast-path."

**PRD:** FR-1's consequence states "Tapping a chip creates a Transaction dated today with the chip's preset amount, Category, and description — no further input required." This correctly conveys "dated today" and "no further input," but doesn't carry forward the explicit, named constraint that backdating is categorically unavailable on this path (as opposed to, say, "defaults to today but is editable like the manual path"). UJ-1 doesn't mention it either.

**Risk:** Low-to-moderate — "no further input required" arguably implies no date picker appears, but the UX spec treats this as a deliberate, named design rule (chips = "right now" semantics only) worth stating positively rather than inferring. Worth a one-line addition for unambiguous build guidance, especially since UJ-2's backdating behavior is spelled out in detail for the manual path and the asymmetry could otherwise read as an oversight rather than intent.

**Recommendation:** Add to FR-1: "The chip path has no date-edit affordance; backdating is only available via manual entry (FR-2)."

---

## Additional smaller items (noted, not ranked as top gaps)

- **Semantic HTML / no placeholder-only labels.** EXPERIENCE.md Accessibility Floor requires real `<button>`/`<label>`/`<input>` elements (not `<div onclick>`) and visible `<label>`s on all form fields (amount, description, budget figure, category name) — "not placeholder-only labeling." PRD §6 doesn't restate either rule. Likely fine to leave as implementation guidance rather than a PRD-level FR, but flagging since it's stated as a hard requirement in the source, not a suggestion.
- **Keyboard `Enter` submits the focused form.** Listed under EXPERIENCE.md Interaction Primitives; PRD's "full keyboard operability" NFR is general enough to cover it but doesn't say it explicitly.
- **Differentiated category-name validation copy.** Source gives two distinct messages ("Give this category a name" for empty vs. "You already have a category called that" for duplicate); PRD FR-7 collapses both into one generic "blocked inline" consequence. Minor — PRD generally paraphrases copy elsewhere too (e.g. FR-6's budget message is quoted verbatim while FR-7/FR-10's are not), so this is stylistic inconsistency more than a dropped requirement.
- **Contrast-ratio precision.** PRD §6 states "AA contrast (~7:1+) on all three Budget Status pairs." DESIGN.md's own computed ratios are green ≈7.3:1, amber ≈6.8:1, red ≈7.6:1 — amber is technically just under 7:1. The PRD's "~7:1+" is approximate language so this isn't a contradiction, but a literal reading of "7:1+" as a floor would fail amber. Worth a tighter NFR wording (e.g. "≥6.5:1, exceeding the 4.5:1 AA minimum with margin") if this NFR is meant to be a testable acceptance bar rather than a rough characterization.
- **"Budget status card... Quick Add post-save echo."** DESIGN.md's component token list places the budget-status-card component at two locations: "Dashboard (top), Quick Add post-save echo," implying the status card may render briefly on the Quick Add screen itself immediately after save (an "echo") before/as the user returns to Dashboard. Every PRD UJ (UJ-1, UJ-2) and every EXPERIENCE.md flow narrative (Flow 1, Flow 2) instead describes an instantaneous, full return to Dashboard with the status card updating there — no mention of an interstitial echo on the Quick Add screen. This is a minor internal ambiguity in the UX spec itself (component table vs. flow narratives disagree on where the echo lives, if it exists at all) that the PRD silently resolved in favor of the flow-narrative reading (no separate echo state) without calling out that a resolution was made. Not a blocker, but worth a one-line confirmation during UX handoff to architecture/build.
- **Responsive breakpoint table (640px / 1024px behavior).** EXPERIENCE.md's Responsive & Platform table gives specific tablet/desktop behavior (content max-width, centering, category grid gaining columns). The PRD has no equivalent Cross-Cutting NFR — only the WCAG-driven "reflow to 320px/400% zoom" bullet, which covers the zoom-accessibility case but not the general "how does this look on a tablet/desktop browser" question. Likely fine to leave to the architecture/UX build phase as visual-layer detail (consistent with how the PRD explicitly defers other DESIGN.md visual specifics), so not counted among the top 5, but flagged since it's the one entire EXPERIENCE.md section with no PRD-side counterpart at all.

---

## What did *not* need reconciling (verified faithful)

For completeness, these EXPERIENCE.md/DESIGN.md commitments that looked like plausible drop-candidates were checked and confirmed present in the PRD:
- Transaction-date (not entry-date) period assignment — FR-3, UJ-2.
- Category button never a dropdown, last-used pre-selected — FR-2.
- Checkmark badge + `aria-pressed` for category selection (not just border color) — §6.
- No confirmation step on category delete, auto-reassignment to Uncategorized — FR-8.
- No delete affordance at all (not just disabled) for the 5 default categories — FR-8 Out of Scope.
- `aria-live="polite"` on inline validation/status/search-total messages — §6.
- Save failure retains entered values, inline retry-capable error, no offline queueing — §6 Connectivity, FR-1.
- No push/toast/modal for budget-status changes, ever — FR-4, §7 Non-Goals.
- Trust/privacy note surfaced via Settings — §4 Information Architecture.
- Cold-load skeleton state (not blank screen/spinner) — UJ-3 edge case.
- Search & Filter: no matches copy, filters stay visible/editable, no pagination — FR-10.
