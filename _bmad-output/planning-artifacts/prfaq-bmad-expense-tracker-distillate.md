---
title: "PRFAQ Distillate: bmad-expense-tracker"
type: llm-distillate
source: "prfaq-bmad-expense-tracker.md"
created: "2026-07-02"
purpose: "Token-efficient context for downstream PRD creation"
---

## Concept Type & Framing
- Solo portfolio/practice project, not a funded commercial launch. Stakeholders = future employers/reviewers and the creator's own execution standard, not investors or a business team. PRD work should not introduce unit-economics or GTM framing.
- Real end goal (clarified in Internal FAQ, not stated in original brief): a tool the creator actually uses personally, not just a resume artifact. This raises the bar on hosting/reliability decisions beyond pure-demo scope, even though it doesn't block initial PRD/build work.

## Rejected Framings & Why
- Headline drafts "The Budget App With No Guilt Trip" and "An Expense Tracker That Never Makes You Feel Behind" were rejected — both implied an active forgiveness/streak-recovery *feature* that V1 does not build. Final headline ("A Budget Tracker Built for Speed, Not Guilt") was chosen specifically to describe absence of punitive UI, not a forgiveness mechanic.
- Leading the press release with "manual-first, no bank sync" was rejected — a new 2026 competitor (Koody) now occupies that positioning more squarely than when the original brief was written. Manual-only is treated as table stakes, not a differentiator.
- Reframing the PRFAQ to stop implying user-testing is imminent (a live alternative on the table) was rejected in favor of committing to Gate 1/Gate 2 (see Open Questions) as literal blocking gates.

## Requirements Signals (from coaching, not yet in any spec)
- Never add a stored totals/remaining column to the schema, even as an optimization — the derived-totals invariant should be enforced structurally (column doesn't exist) not procedurally (developer discipline). This is the single highest-value architectural guardrail surfaced across this whole process.
- Configure CORS and build the DTO layer + centralized `@ControllerAdvice` exception handler into the initial Spring Boot skeleton before writing the first controller method — not reactively after a blocked frontend call or leaked stack trace.
- Frequent-expenses shelf needs an explicit ordering/cap decision (e.g. most-recent vs. most-frequent, capped at N items) — currently undecided, flagged as a near-term UX/architecture decision, not resolved in the PRFAQ.
- A hosting/persistence decision needs to be made before V1 is "done" for actual personal use (not before the PRD) — technical research left deployment explicitly out of scope, but the creator's real goal (a tool they keep using) makes this load-bearing in a way the original brief didn't anticipate.

## Technical Context & Constraints
- Stack: Next.js frontend, Spring Boot REST/JSON API, PostgreSQL via Spring Data JPA/Hibernate. Frontend never touches the database directly. Controller → Service → Repository layering, DTOs at the API boundary, JPA entities never returned directly from controllers.
- Single-user, no login/accounts for V1 — but NOT per-browser local storage; data lives in the backend Postgres instance, so switching devices doesn't lose data as long as the same deployed instance is used.
- Repo layout: flat `frontend/` and `backend/` folders, not a monorepo tool.
- Schema management: Hibernate `ddl-auto` for dev speed; proper migration tooling explicitly deferred, not rejected.
- A transaction's period is determined by its transaction date, not its entry date — backfilling a past-dated expense correctly updates that period's derived totals with no special handling needed. Verified as a genuine architectural strength during Customer FAQ stress-testing, not just a design intention.

## Competitive Intelligence (as of 2026-07-02 research)
- **Koody** (new 2026 entrant) — free, manual-only, no bank linking, few-seconds entry, plus receipt-attach (which this project excludes). Closest direct positioning match found; the concept currently has no proven answer to "why this over Koody" beyond an untested design bet on neutral status language + frequent-expenses shelf.
- **Finny** has pivoted toward AI-parsed natural-language/voice entry rather than pure tap-based quick-add, changing what "fast" means competitively.
- **PocketGuard** shipped a new "Pace" feature (spend-rate alerts) — shows automation-first incumbents are already iterating toward status/pace indicators, narrowing (not closing) the neutral-tone differentiation window.
- No independent (non-vendor) research confirms "day-7 guilt spiral" as a named, studied phenomenon. General mobile retention curves (Day-7 ~15%, Day-30 ~7-10%) and Gen-Z financial-anxiety sentiment support the shape of the claim; the shame-causation mechanism remains this project's synthesized hypothesis, carried into the PRFAQ as an explicit operating theory, not an established fact.
- Full competitor/segment detail already lives in `_bmad-output/planning-artifacts/briefs/brief-bmad-expense-tracker-2026-07-02/addendum.md` and `_bmad-output/planning-artifacts/research/market-personal-expense-tracking-app-market-research-2026-07-02.md` — not re-duplicated here.

## Scope Signals
- **In (V1, confirmed, unchanged from brief):** quick-add expense entry (amount, category, date), default + custom categories, frequent-expenses shelf, monthly budget with neutral traffic-light status, single-user no login.
- **Cut order if timeline slips (user-confirmed):** charts/insights → rollover behavior → category custom-edit polish beyond basic CRUD. Core loop (quick-add, category totals, budget status, frequent-expenses shelf) is never cut.
- **Explicitly out, not deferred:** bank-account linking, receipt capture/OCR, push notifications, multi-user accounts/auth.
- **Deferred, not rejected:** charts/insights (low-cost, reuses V1 aggregation queries), lapse-recovery/streak-forgiveness (the untested differentiator — see Open Questions), rollover behavior (real domain decision, not needed for V1).

## Resource & Timeline Estimates
- Rough estimate: 2–4 weeks of part-time solo work for defined V1 scope. Explicitly a guess, not a commitment — actual weekly hours available were not specified and should be revisited once implementation starts.

## Open Questions / Unknowns Flagged During Internal FAQ
- **Testing gate (highest-priority open item):** the 5–7 real-user usability test has been planned and not executed across three separate BMAD sessions (brainstorming → innovation strategy → this PRFAQ). User committed this round to treating it as a literal blocking gate: no work beyond core V1 scope (especially lapse-recovery) proceeds until either testing happens or a deliberate decision is made to proceed without it. Recommend any downstream PRD/session explicitly check whether this test has actually run before taking "testing is planned" at face value again.
- **Hosting/persistence decision:** still undecided (technical research left it explicitly out of scope). Now has real personal stakes given the "tool I actually use" goal — needs resolving before V1 is "done" for personal use, though not before PRD/architecture work begins.
- **Frequent-expenses shelf ordering/cap:** undecided, small scope, resolve during UX/architecture work.
- **Koody-specific differentiation:** no evidence-backed answer exists yet; current position is an honest "unproven hypothesis," named directly in the Customer FAQ rather than papered over.

## The Verdict (carried forward as actionable items)
- **Forged in steel:** derived-totals/period-assignment domain architecture; the discipline against overclaiming a forgiveness feature that doesn't exist; scope-cut discipline under pressure.
- **Needs more heat:** Koody differentiation is asserted, not evidenced; frequent-expenses shelf UX mechanics undecided; timeline is a rough guess.
- **Cracks in the foundation:** stalled testing gate (3 sessions running, still unexecuted) underpins the entire differentiated value proposition; unreconciled tension between "treat as demo" (Customer FAQ) and "tool I actually use" (Internal FAQ) goals — needs an explicit, even minimal, hosting decision.
