# Reconciliation — Market Research (market-personal-expense-tracking-app-market-research-2026-07-02.md)

## Carried forward faithfully
- "Category selection, not amount entry, is the known drop-off point; big icon buttons beat dropdowns" — carried forward almost verbatim, and explicitly attributed in EXPERIENCE.md's "Inspiration & Anti-patterns" section ("Lifted from market research: big icon category buttons over dropdowns...").
- Guilt vs. shame distinction and "no raw itemized spend without context" — reflected in Voice and Tone's Do/Don't table (factual "Budget exceeded by ₹250" vs. banned "gotcha" itemization like "$47 on coffee?!").
- "Defer setup, not value" / no bank-linking or goal-setting gate before first log — Dashboard is the default no-login landing surface, FAB always available; no onboarding gauntlet anywhere in the IA.
- Category taxonomy kept shallow (research: "15–20+ categories cause friction") — five defaults, consistent.
- No hard limits / red-flag pass-fail framing — budget-status color changes but copy stays factual at every severity level ("stays exactly as matter-of-fact as the green one").
- Recency-based category suggestion as the friction-reduction mechanism inside manual entry (research's "smarter recency-based category suggestions" recommendation) — realized as "last-used category pre-selected by default."

## Deliberate overrides (confirmed documented in spec)
- **"No red banners, ever" (observational-framing-only recommendation)** — the user-approved override to allow visible green/amber/red budget-status colors is clearly and explicitly documented in EXPERIENCE.md's "Inspiration & Anti-patterns" section: "Reconsidered from market research, by explicit user decision... Color signals severity; language never does." This is exactly the kind of override-with-visible-rationale the reconciliation check is looking for — confirmed present and unambiguous.
- **Required description field** overrides market research's "keep manual entry to essential fields... defer notes/tags to be optional/skippable" recommendation. This is documented via the same Component Patterns/Flow-2 mechanism noted in reconcile-brief.md, and the interaction design directly answers the friction concern the market research raised (chip path stays zero-typing; only the manual, non-repeat path pays the typing cost).

## Dropped or under-represented (flag for awareness, not necessarily a defect)
- Market research names a specific combined differentiator — "fast *and* forgiving" re-entry after a lapse — as the one open positioning gap in the market. EXPERIENCE.md correctly implements "fast" and defers "forgiving lapse-recovery" as an explicit, well-labeled future item (consistent with the Gate 2 testing requirement from innovation strategy). Not a defect, but worth flagging that the market research's central strategic recommendation is only half-realized in this spine, by design — a downstream reader should not mistake the current spec as the differentiated version.

## Undocumented drift (real concern — spec differs from source with no override rationale visible)
- None found. The one point of tension (red/amber/green vs. "no red banners ever") is the known override and is clearly documented in the spec's own words.
