# PRD Quality Review — bmad-expense-tracker

## Overall verdict

This is an unusually honest, well-load-bearing solo/portfolio PRD: trade-offs are named with what was given up (not smoothed to neutral), Non-Goals does real exclusion work, and almost every FR carries testable consequences with concrete thresholds rather than adjectives. The two things genuinely at risk are mechanical, not strategic: FR-9 has no testable consequences at all, and two duplicated cross-references (§7/§8 mislabeled as Success Metrics/Open Questions, which are actually §9/§10) undercut the doc's own claim to be source-extractable section-by-section. Fix those two and this PRD is ready to hand to architecture/story creation as-is.

## Decision-readiness — strong

Trade-offs are stated as decisions with an explicit cost, not buried as neutral "considerations." §1 Vision's "Note on an evolved promise" names that the PRFAQ's literal promise ("no red banners") was overridden by UX to a visible red status color, and explains what survived the override rather than pretending nothing changed. §5.1 FR-2's Notes go further: "The required-description rule deliberately overrides market and domain research's 'keep description optional to protect logging speed' recommendation" — a real trade-off (typing cost vs. search capability), named plainly rather than dressed up as consensus.

Open Questions are genuinely open, not rhetorical. §10 OQ3 (Koody competitive overlap) states outright "This product has no evidence yet that it wins on the non-shaming axis specifically" — no answer smuggled into the next sentence. §10 OQ4, tagged `[NOTE FOR PM]`, is a striking piece of self-critical honesty: "the formal usability-testing gate has been planned and not executed across 3 prior sessions... This PRD replaces it with self-dogfooding..., which is lower-friction but carries the same risk of being 'planned' without follow-through." That is a `[NOTE FOR PM]` sitting at a real tension, exactly where the rubric wants it — not at a safe checkpoint.

No findings — this dimension needs none.

## Substance over theater — strong

Single persona (Sam), and every UJ she carries maps directly to specific FRs — not persona decoration. NFRs in §6 are specific enough to be theater-proof: exact contrast ratios ("green/red ~7:1+, amber ~6.8:1"), exact zoom/reflow bounds ("200% zoom without truncation," "320px/400% zoom"), named ARIA attributes tied to named UI states — this is the opposite of "system must be scalable/secure/reliable" boilerplate. Innovation claims are self-checked rather than asserted: §10 OQ3 explicitly concedes a near-exact competitor (Koody) exists and that the product's differentiation is unproven, which is the anti-theater move — a lesser PRD would have kept the differentiation claim and left the competitor out.

No findings — this dimension needs none.

## Strategic coherence — strong

The thesis is stated plainly in §1: "The answer here is structural, not motivational" — trustworthy-by-construction totals, zero-typing quick-add, and an identical return-after-gap experience are all traceable back to that one sentence. §8.2's cut order (charts/insights first, category-edit polish second, core loop "never cut regardless of timeline pressure") follows the thesis rather than ease of build. Success Metrics avoid the classic tell: SM-1 (sustained personal use) and SM-2 (sub-5-second quick-add) measure the thesis directly, and SM-C1 is a genuine counter-metric ("do not optimize for 'more capability' if it costs Quick Add its speed or simplicity") rather than a token one.

No findings — this dimension needs none.

## Done-ness clarity — adequate

Nine of ten FRs carry a "Consequences (testable)" block with concrete, verifiable conditions — thresholds (§5.2 FR-4: "<80%... 80–100%... >100%"), explicit blocking rules (§5.3 FR-6: "Zero, negative, or non-numeric entry is blocked inline"), explicit absence-of-caching rules (§5.2 FR-5). This is the dimension the rubric says to be unforgiving on, and one FR breaks the pattern cleanly enough to be a real finding rather than a nitpick.

### Findings

- **high** FR-9 has no testable consequences (§5.4, FR-9) — The entire FR reads: "User can rename or change the icon of any Category, including the 5 defaults. Realizes UJ-5." Every other FR in the document (FR-1 through FR-8, FR-10) has a "Consequences (testable)" block; FR-9 does not. An implementing engineer has no answer for: does renaming apply the same empty/duplicate-name validation FR-7 specifies for adds? Can two categories end up with the same name after a rename? Does renaming a default category (e.g. "Food" → something else) have any effect on data referencing it by name elsewhere? *Fix:* add a Consequences block to FR-9 mirroring FR-7's validation rule, and state explicitly whether duplicate-name blocking applies to renames of defaults too.

- **low** FR-4 doesn't disclose its own no-budget exception (§5.2 FR-4 vs §5.3 FR-6) — FR-4's consequences describe Budget Status as always computing and displaying on the Dashboard, but the "no Budget set yet" case (neutral prompt instead of a status card) is only defined two sections later, in FR-6's consequences ("The Dashboard shows a neutral prompt to set one instead of a Budget Status card until it exists"). A reader extracting FR-4 alone would not know this case exists. *Fix:* add a one-line consequence to FR-4 cross-referencing FR-6's no-budget state, or state the exception inline.

## Scope honesty — strong

§7 Non-Goals does real work — eleven explicit exclusions, not a token list, plus a `[NOTE FOR PM]` at the one genuinely deferred (not confirmed-dead) decision: a Lapse Recovery feature, gated on "unprompted, independent signal" from dogfooding rather than the untested hypothesis alone. §8.2 draws an honest, structurally distinct line between "Confirmed exclusion (not timeline-contingent)" — Rollover — and "Deferred, cut order if timeline slips" — charts/insights, then category-edit polish. That is exactly the de-scoping-proposed-honestly pattern the rubric asks for; a weaker PRD would have flattened both into one undifferentiated "out of scope" list.

Open-items density (4 live Open Questions, 3 `[NOTE FOR PM]` tags, 0 unresolved `[ASSUMPTION]`s) is proportionate to a low-stakes solo/portfolio PRD — the rubric explicitly treats this as acceptable, even a good sign of candor rather than a blocker.

No findings — this dimension needs none.

## Downstream usability — adequate

This PRD is explicitly mid-chain, not standalone: §0 states it's built to feed "the architecture phase," and the addendum is titled around exactly that. Glossary (§3) is comprehensive and its terms are used consistently in Title Case throughout the FR/UJ prose (Transaction, Category, Budget Period, Derived Total, Budget Status). FR/UJ IDs are contiguous with no gaps or duplicates (FR-1–FR-10, UJ-1–UJ-6), every UJ is realized by at least one FR and every FR realizes exactly one UJ, and all six UJs carry the same named protagonist (Sam) — no floating UJs.

### Findings

- **medium** Broken cross-references, duplicated (§2.3 UJ-3 and §5.2 FR-4-section description) — UJ-3's resolution says the identical-return-after-gap behavior is "the product's core untested hypothesis (see §7 Success Metrics, §8 Open Questions)," and §5.2's Dashboard description repeats the same pointer as "(§7, §8)." But §7 is actually **Non-Goals (Explicit)** and §8 is **MVP Scope** — Success Metrics is §9 and Open Questions is §10. The error is systemic (appears twice, same wrong pair both times), consistent with a section renumbering that wasn't propagated to these two cross-refs. A reader or downstream extraction agent following the citation lands on the wrong section content. *Fix:* update both instances to "(§9 Success Metrics, §10 Open Questions)."

## Shape fit — strong

This PRD is a deliberate hybrid and says so: the runtime user is a single implicit user (no auth, §2.2), but the *design* target is modeled as an external consumer persona (Sam, "Anxious Starter" segment) distinct from the builder's own motivation, which §2.1 names separately as "Builder's own JTBD... a tool Raha will actually keep using long-term, not a portfolio demo abandoned after review." That's the rubric's hobby/solo guidance ("rigor light, substance bar still applies") self-aware enough to justify why it's *not* going light — the heavier formalization (Glossary-anchored IDs, Assumptions Index, `[NOTE FOR PM]` tags) is deliberate because this is stated up front to be a portfolio artifact demonstrating PM rigor, not just a personal tool spec. UJs are load-bearing, not decorative — UJ edge cases (e.g. UJ-1's "connection lost mid-save") map directly onto FR consequences (FR-1's identical failure-mode clause), so they're doing real work rather than padding a template.

Worth noting as context rather than a defect: the UX spec was finalized *before* this PRD (§0: "the finalized UX spec... supplies validated flows"), an inversion of the usual PRD-then-UX order. This is disclosed up front, and the PRD treats its own UJs as a port of already-validated flows ("Mirrors the 6 flows validated in `EXPERIENCE.md`") rather than net-new discovery — a legitimate sequencing choice given the artifact chain, not a shape mismatch.

No findings — this dimension needs none.

## Mechanical notes

- **Broken cross-references (§7/§8 vs §9/§10)** — see Downstream usability finding above; this is the one mechanical issue with real reader-facing impact.
- **Glossary drift, minor** — "Frequent-Expenses Shelf" (the glossaried collection, §3) vs. "chip" / "frequent-expense chip" (an individual item on it, used throughout §2.3, §5.1) are used interchangeably in places without "chip" ever getting its own one-line glossary entry. The meaning is inferable from context and the two aren't actually synonyms (shelf = the set, chip = one item on it), so this is low severity — a one-line glossary addition for "chip" would close it.
- **Unresolved title flag not tracked as an Open Question** — line 9, "*Working title — confirm.*" sits under the H1 but isn't listed in §10 Open Questions or otherwise tracked, so it could be missed on a skim. Low severity; either resolve it or add it as OQ-6.
- **Assumptions Index roundtrip** — clean. §11 states zero unresolved `[ASSUMPTION]` tags remain, and no inline `[ASSUMPTION: …]` tag appears anywhere in the body that isn't accounted for — the one assumption mentioned (budget scope, §3/§5.3) is explicitly marked resolved rather than left dangling.
- **UJ protagonist naming** — clean. All six UJs (§2.3) carry the same named protagonist, Sam, introduced once and carried by context through every journey; no floating UJs.
- **ID continuity** — clean apart from the cross-ref issue above. FR-1–FR-10 and UJ-1–UJ-6 are both contiguous with no gaps or duplicates; SM-1/SM-2/SM-C1 numbering is consistent and the counter-metric prefix is used correctly.
- **Required sections** — all present and proportionate to a mid-chain, portfolio-quality solo PRD: Vision, Target User (JTBD + Non-Users + UJs), Glossary, Information Architecture, Features/FRs, Cross-Cutting NFRs, Non-Goals, MVP Scope, Success Metrics, Open Questions, Assumptions Index.
