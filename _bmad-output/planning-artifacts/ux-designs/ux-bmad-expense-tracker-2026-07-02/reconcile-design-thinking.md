# Reconciliation — Design Thinking Session (design-thinking-2026-07-02.md)

## Carried forward faithfully
- One-tap quick-add with smart defaults (today prefilled, last-used category pre-selected as a big icon button, numeric-keypad-first amount entry) — Top Concept 1, realized directly in Flow 2 and Component Patterns.
- Frequent-expenses shelf, "like frequently-contacted people in a messaging app," chips like "Coffee $5" — Top Concept 3, realized in Flow 1 (updated to ₹ currency, a documented override).
- Neutral/non-judgmental budget-status framing: traffic-light color + "days left at this pace" reframing instead of raw over/under numbers — Top Concept 2, and the exact phrase pattern ("₹340 of ₹800 this month — on pace, 12 days left" template) survives essentially verbatim in EXPERIENCE.md's Voice and Tone and Flow 3.
- Big category icon buttons over dropdowns — carried (also independently reinforced by market research).
- Trust microcopy ("we never connect to your bank"; manual entry framed as a feature, "you're always in control") — carried into Voice and Tone Do's.
- Rejected/deferred ideas correctly kept out: voice-to-log (explicitly named "stretch/wild idea" here, explicitly deferred in EXPERIENCE.md), home-screen widget ("wild — likely V2+" here, "Rejected" in EXPERIENCE.md), chill/strict-mode tone toggle (wild/stretch idea here, "Rejected — adds setup-time configuration" in EXPERIENCE.md), weekly-only "gentle check-in" cadence (idea 9 here, "Rejected" in EXPERIENCE.md with matching rationale).
- The reusable summary-card-with-color-state-prop concept (carried into this session from the prior 2026-07-01 brainstorm) is realized directly as the Budget status card component (single position, three color states, in DESIGN.md/EXPERIENCE.md).

## Deliberate overrides (confirmed documented in spec)
- Streak counter (idea 16, explicitly framed here as "light gamification, not punitive," distinct from lapse-recovery) is not built — EXPERIENCE.md's Banned list flatly excludes "streak counters or badges." This wasn't one of the session's three selected Top Concepts, so its exclusion doesn't need a special reconciliation note beyond the general Gate 2 (untested lapse/guilt hypothesis) reasoning already documented elsewhere in EXPERIENCE.md — acceptable.
- Swipe-gesture category picking (idea 4) — not selected as a Top Concept originally; EXPERIENCE.md goes further and explicitly bans all swipe gestures. No conflict, since it was never promoted past the ideation list.

## Dropped or under-represented (flag for awareness, not necessarily a defect)
- **"Duplicate last expense" (idea 14)** is a distinct pattern from the frequent-expenses shelf (idea 15): it re-logs the single most recent transaction and explicitly "prompts only for date confirmation" — implying both a different interaction (single most-recent item, not a multi-chip shelf) and a date-confirmation step that the frequent-expenses shelf doesn't have. EXPERIENCE.md only builds idea 15 (the shelf); idea 14 isn't mentioned as either "carried forward," "merged into the shelf," or "rejected" anywhere in the Inspiration & Anti-patterns section. Worth a quick explicit note either way, especially since the "date confirmation" detail is the same date-handling gap flagged in reconcile-brief.md/reconcile-domain-research.md/reconcile-prfaq.md.
- "Most likely category" time-of-day prediction (idea 12) — not carried, reasonably simplified down to plain last-used-category default; unflagged as a simplification but low-stakes.
- The session's open testing question ("do people log in-the-moment or prefer end-of-day batch entry?") is a research question, not a UX-spec obligation — its absence from EXPERIENCE.md is correct, not a drop.

## Undocumented drift (real concern — spec differs from source with no override rationale visible)
- None found rising to the level of a contradiction — the "duplicate last expense" item above is better read as an unflagged simplification than a drift, since the shelf concept plausibly subsumes it.
