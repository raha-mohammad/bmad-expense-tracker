# Reconciliation — PRFAQ (prfaq-bmad-expense-tracker.md + distillate)

## Carried forward faithfully
- "Log a purchase in under 5 seconds," one/two taps, five default categories, frequent-expenses shelf pre-populated with prior repeats — matches Flow 1 and Component Patterns almost verbatim (down to the "coffee already sitting one tap away" image).
- No reminder, no backlog view, no "you have N unlogged days" counter on reopen — matches State Patterns' "Reopen after a gap" row and the Banned list exactly.
- Status goes amber then red, "never hidden," but expressed in plain non-moralizing language ("about ₹X over" vs. a scolding banner) — matches Voice and Tone table and the amber/red budget-status treatment.
- Single-user, backend-persisted data (PostgreSQL, not per-browser local storage) so switching devices doesn't lose data — matches EXPERIENCE.md Foundation ("Data is backend-persisted via the Spring Boot API... not browser local storage") almost word for word.
- Multi-month charts/insights deferred ("just this month for now") — consistent by omission; Dashboard scope in EXPERIENCE.md never implies multi-month history.
- Frequent-expenses shelf ordering/cap explicitly flagged in the PRFAQ as "not fully decided yet... a near-term implementation decision" — correctly resolved in EXPERIENCE.md as "ordering/cap left as an implementation detail, not user-visible contract," which is the right level of resolution (decided-to-not-decide-in-the-spec, not silently forgotten).

## Deliberate overrides (confirmed documented in spec)
- Currency: PRFAQ's illustrative copy uses $ ("about $40 left at this pace," "$47 on coffee?!"); EXPERIENCE.md correctly and consistently substitutes ₹ with Indian grouping throughout, and this substitution is explicitly named as a decision in the Foundation section, not a silent copy change.
- Required description field: not present in the PRFAQ's "How It Works" walkthrough at all (PRFAQ's flow is amount → category only) — EXPERIENCE.md's addition of a required description is documented via the same Component Patterns/Flow-2 reconciliation noted in reconcile-brief.md. Confirmed present and clear, not contradicting the PRFAQ silently.

## Dropped or under-represented (flag for awareness, not necessarily a defect)
- The PRFAQ's Internal FAQ treats **"a transaction's period is set by its transaction date, not entry date... logging Tuesday's coffee on Thursday correctly updates Tuesday's period"** as a "genuine strength, not a workaround," and the Verdict section calls this domain architecture one of the things "forged in steel." This claim is only meaningful if a user can actually enter a non-today date somewhere. EXPERIENCE.md's Quick Add never exposes a date field/picker (see reconcile-brief.md for the same finding) — so the UX spec doesn't carry forward the one interaction surface that would let this praised architectural strength actually manifest to a user. Reasonable to leave for a fast-follow, but currently unflagged as a cut.
- Almost all of the PRFAQ's Internal FAQ content (testing-gate status, hosting/backup decision, timeline, Koody/Finny competitive positioning) is strategy/process material correctly out of scope for a UX spec — its absence from DESIGN.md/EXPERIENCE.md is expected, not a gap.

## Undocumented drift (real concern — spec differs from source with no override rationale visible)
- Same date-field gap as above: the PRFAQ explicitly relies on backdating being possible (it's used to answer a hard customer question and is called out as a verified strength), yet no UI surface for entering a non-today date exists anywhere in DESIGN.md or EXPERIENCE.md, and no note documents this as an intentional V1 cut. This is the one item worth raising back to the user before implementation.
