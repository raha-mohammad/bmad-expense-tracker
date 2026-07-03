# Reconciliation — Product Brief (brief.md + addendum.md)

## Carried forward faithfully
- Core loop (log <5s → see category totals/budget status → return tomorrow) — realized across Key Flows 1–3.
- Default categories (Food, Transport, Shopping, Bills, Entertainment), full CRUD for custom categories, non-deletable defaults — Component Patterns / Flow 5, matches brief Scope section exactly.
- Frequent-expenses shelf for one-tap re-logging — Component Patterns, Flow 1.
- Neutral traffic-light budget status with non-judgmental copy ("days left at this pace," not "OVER BUDGET") — Voice and Tone table, Flow 3.
- Persistent add button, never buried in a menu — FAB, IA table.
- Single-user, no accounts/login; explicitly-excluded features (bank linking, receipt capture, push notifications, multi-user auth) — Foundation, Banned list.
- Derived-totals invariant ("never independently stored or hand-edited") — promoted almost verbatim into EXPERIENCE.md's Foundation as a named "structural, not just behavioral" guarantee.
- Deferred-not-built items (charts/insights, lapse-recovery, rollover) — consistent with brief's Scope; lapse-recovery's deferral is explicitly re-affirmed and correctly attributed to the (still ungated) testing requirement.
- Next.js + Spring Boot + PostgreSQL, backend-persisted (not local storage) — Foundation section matches brief's "Platform direction" paragraph and addendum's technical notes.

## Deliberate overrides (confirmed documented in spec)
- **Required description field** (brief/addendum's minimal-fields model was date+amount+category only): EXPERIENCE.md documents this clearly — Component Patterns' "Quick-add save button" row states the save gate (amount>0, category, non-empty description), and Flow 2 narrates the friction explicitly (manual path requires typing; chip path auto-satisfies via preset description). Correctly reconciled, not silent.
- **Currency ₹ INR / Indian digit grouping** (brief/addendum use $ throughout): stated explicitly in EXPERIENCE.md Foundation ("Currency is ₹ (INR)... supersedes $ used in research docs"). Clearly documented.
- **shadcn/ui as the frontend component system** (brief only commits to "Next.js frontend," no UI library named): DESIGN.md/EXPERIENCE.md correctly frame this as an additive, inherited-by-default layer ("the product inherits shadcn/ui defaults wholesale... this DESIGN.md specifies only the brand-layer delta") rather than a parallel system contradicting the brief — this is a gap-fill, not an override of anything the brief actually asserted.

## Dropped or under-represented (flag for awareness, not necessarily a defect)
- **Transaction date editability.** The brief's scope line is "Log an expense: amount, category, date (defaults to today)" — implying date is a real, changeable field. EXPERIENCE.md's Quick Add IA, Component Patterns table, and both Add-Expense flows (1 and 2) never show a date field, date picker, or any way to log something other than "today." As written, the spec gives no way to backdate an entry at all. This is worth flagging prominently — see the matching note in reconcile-prfaq.md and reconcile-domain-research.md, since PRFAQ and domain research both treat backdating as load-bearing.
- **No edit/delete-transaction flow.** The brief doesn't demand this explicitly, but nothing in EXPERIENCE.md's six Key Flows covers correcting or removing an already-saved transaction. Reasonable to cut for MVP, but currently un-flagged as a cut (it simply isn't mentioned).
- Addendum's two secondary segments (Overwhelmed Early Professional, Privacy-Conscious Manual Tracker) are correctly out of focus — brief itself says V1 isn't built to court them, so their absence from Sam-centric flows is expected, not a drop.

## Undocumented drift (real concern — spec differs from source with no override rationale visible)
- The missing date field/picker in Quick Add (see above) reads as an undocumented drift rather than a deliberate scope cut: nothing in DESIGN.md/EXPERIENCE.md states "backdating is out of V1 scope," yet the brief's own field list implies it should exist, and it silently disappears from every relevant surface (IA, Components, both Add-Expense flows).
