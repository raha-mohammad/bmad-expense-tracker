---
project_name: 'bmad-expense-tracker'
user_name: 'Raha'
date: '2026-07-03'
sections_completed: ['technology_stack', 'product_domain_analysis', 'ux_interaction_design', 'prd', 'development_workflow_rules', 'critical_donts_miss_rules']
existing_patterns_found: 1
---

# Project Context for AI Agents

_This file contains critical rules and patterns that AI agents must follow when implementing code in this project. Focus on unobvious details that agents might otherwise miss._

---

## Technology Stack & Versions

**Proposed (not yet formally decided in a PRD/architecture doc):** Next.js + Spring Boot + PostgreSQL, per `_bmad-output/planning-artifacts/research/technical-nextjs-spring-boot-postgresql-stack-research-2026-07-02.md`. Treat as the working assumption, not a locked decision — confirm against `_bmad-output/**/architecture.md` once it exists before hard-committing to it.

## Product & Domain Analysis Completed (read before implementing or designing anything)

Brainstorming, market research, domain research, technical research, design thinking, innovation strategy, and a full PRFAQ (Working Backwards) pressure-test are all complete as of 2026-07-02. Full docs live under `_bmad-output/` (see `_bmad-output/planning-artifacts/research/` and the root-level `design-thinking-2026-07-02.md` / `innovation-strategy-2026-07-02.md`). **The PRFAQ (`_bmad-output/planning-artifacts/prfaq-bmad-expense-tracker.md` + its `-distillate.md`) now supersedes the product brief as the primary input for PRD creation** — it carries forward everything in the brief plus later-stage findings (updated competitive landscape, resolved trade-offs, open risks). No PRD or architecture doc exists yet — these are inputs to that, not a substitute for it. Key unobvious rules/decisions agents should already know from this analysis:

- **Domain model:** transaction → category → period → derived totals. Category totals and "budget remaining" must always be **computed from transactions**, never stored/edited independently — this is the one non-negotiable business rule (domain research).
- **MVP transaction fields:** date + amount + category, **plus a required description field** — ⚠️ **superseded 2026-07-03**: the original date+amount+category-only rule (below) was deliberately overridden during UX design to support keyword search. See UX section below.
- **Emotional/UX contract, not just speed:** the product's differentiation is being fast *and* non-punishing after a lapse — no guilt copy, no catch-up burden on return. ⚠️ **Partially superseded 2026-07-03**: visible red/amber/green budget-status color **is** used (the "no red framing" rule below was overridden) — but the non-shaming spirit survives in copy tone, not color choice. See UX section below.
- **Setup must be deferred:** let a user log an expense before asking for budgets/goals/account linking (market research).
- **Validated concepts to carry into UX/architecture:** quick-add entry, a budget status indicator (now green/amber/red, see UX section — supersedes "neutral" below), and a frequent-expenses shelf for one-tap re-entry. A reusable summary-card component with a color-state prop was flagged as a likely shared UI pattern (design thinking + brainstorming).
- **Explicitly out of scope for MVP:** bank sync, receipt capture, reminders. Don't add these speculatively — revisit only if real users ask (design thinking).
- **Rollover behavior (carry budget forward vs. reset each period) is an open decision** — the one domain concept worth a deliberate call even at MVP scope; don't silently pick one (domain research).
- **Stack wiring pitfalls to avoid day one** (technical research): configure CORS on Spring Boot before the first frontend call; never return JPA entities directly from controllers — always DTOs; add a centralized exception handler early; keep Controller → Service → Repository layering even for simple endpoints.
- **Recommended next step per innovation strategy:** run the design-thinking Test phase (5-7 real users) before locking UI details into a PRD/UX spec — the lapse-recovery/guilt hypothesis above is still unvalidated with real users, not just aggregate market research.
- **Testing gate is still unexecuted as of the PRFAQ (2026-07-02) — third session in a row it's been planned and not run** (brainstorming → innovation strategy → PRFAQ all reach the same conclusion). Treat this as a real blocker on lapse-recovery work, not a formality — verify whether it has actually happened before trusting "testing is planned" again in a future session.
- **Competitive landscape shifted since the brief was written:** a new entrant, **Koody**, now occupies the "free, manual-only, no bank sync, fast entry" positioning even more squarely than Finny did — the concept currently has no evidence-backed answer to "why this over Koody," only an untested design bet on neutral status language + frequent-expenses shelf. Don't design or pitch around "manual-only" as a differentiator; it's table stakes now.
- **Derived-totals invariant should be enforced structurally, not procedurally:** never add a totals/remaining column to the schema at all — if the column doesn't exist, a service accidentally caching a total instead of computing it becomes impossible, not just discouraged.
- **Real end goal clarified (PRFAQ Internal FAQ):** the creator wants a tool they personally keep using, not just a portfolio artifact — this makes the still-undecided hosting/persistence question (explicitly left open by technical research) load-bearing before V1 is "done" for personal use, even though it isn't a blocker for PRD/architecture work.
- **Frequent-expenses shelf ordering/cap is undecided** — resolve during UX/architecture work, not before.
- **Cut order if timeline slips (confirmed):** charts/insights → rollover behavior → category custom-edit polish beyond basic CRUD. Core loop (quick-add, category totals, budget status, frequent-expenses shelf) is never cut; lapse-recovery was never in V1 scope to begin with.

## UX & Interaction Design Completed (2026-07-03, read before architecture or implementation)

Full UX spec pair — `DESIGN.md` (visual identity) + `EXPERIENCE.md` (behavior/IA/flows) — is finalized at `_bmad-output/planning-artifacts/ux-designs/ux-bmad-expense-tracker-2026-07-02/`. These are pre-PRD inputs (no PRD exists yet); treat their flows and decisions as validated UX intent, not something to re-derive. Key decisions and **overrides of earlier research** agents should know:

- **UI stack decided:** Next.js + Tailwind CSS + shadcn/ui (narrows the earlier "Next.js + Spring Boot + PostgreSQL, no frontend library named" stack research). `DESIGN.md` specifies only the brand-layer delta on top of shadcn defaults — primary blue `#2563EB`, background `#F5F9FC`, 16px card radius, budget-status colors. Visual direction name: "Calm Harbor."
- **OVERRIDE — budget status now uses visible green/amber/red**, not neutral-only framing: green < 80% of budget, amber 80–100%, red > 100% (exceeded). This deliberately reverses the earlier "no red banners" market-research recommendation. What's preserved: copy stays factual/non-judgmental at every severity level (e.g. "Budget exceeded by ₹250," never "you failed your budget") — **color communicates severity, language never does.**
- **OVERRIDE — description is now a required field** on every transaction (date + amount + category + description), reversing the earlier "no notes field" MVP-fields rule. Added specifically to support keyword search. Reconciled with quick-add speed via the frequent-expenses shelf: each shelf chip carries a preset description matching its label, so the common/repeat case still logs in one tap with zero typing; only new/one-off manual entries require typing a description.
- **Currency is ₹ (INR) with Indian digit grouping** (e.g. `₹1,00,000`), not $ as used in the original research docs.
- **Quick Add now has an editable date field** (manual-entry path only; defaults to today, tap to change) — added during UX review specifically to satisfy the domain-research transaction-date invariant (period follows the transaction date, not the entry date). The frequent-expense-chip fast-path stays today-only by design.
- **Category rules locked:** 5 defaults (Food, Transport, Shopping, Bills, Entertainment) are non-deletable (rename/re-icon only); user-added categories are fully deletable. Deleting a category with existing transactions auto-reassigns them to an implicit "Uncategorized" bucket — no blocking, no forced manual reassignment.
- **Accessibility floor: WCAG 2.1 AA**, with concrete commitments (not just a goal) — a primary-tinted focus ring token, computed contrast ratios for all three budget-status pairs (~7:1+), `aria-live` on inline status/error messages, and a non-color checkmark badge on the selected-category state (category selection is the known drop-off point).
- **Scope confirmed:** no auth/login in V1 (single-user, backend-persisted, not local storage), no dark mode, English only, always-online (no offline queueing), minimal/no decorative motion, mobile-first responsive (desktop scales up, no separate redesign).
- **Frequent-expenses shelf ordering/cap is still an open implementation detail** — deliberately left unresolved in the UX spec, not a gap to flag as missing.

## PRD Completed (2026-07-03, read before architecture or epics/stories work)

The MVP PRD is finalized at `_bmad-output/planning-artifacts/prds/prd-bmad-expense-tracker-2026-07-03/` (`prd.md` + `addendum.md` for technical-how/rejected-alternative detail). It builds on the PRFAQ, UX spec, and domain/market/technical research rather than re-deciding them — but it resolved several things those docs left ambiguous or open. Key decisions and **overrides/resolutions** agents should know:

- **Budget is a single overall amount per period, not per-Category** — resolves an ambiguity where domain research's general framing ("a budget applies to a category") conflicted with the UX spec's singular "Set Monthly Budget" flow. Category breakdown on the Dashboard is informational only, never separately budgeted.
- **RESOLVED — no rollover for MVP.** This was flagged in domain research as "the one domain concept worth a deliberate call" — now decided: budgets reset to zero each period, confirmed as a firm exclusion (not a maybe-cut), revisit for v2 based on actual usage.
- **RESOLVED — testing gate descoped.** The PRFAQ's 5-7 user formal usability test stalled across 3 prior sessions (brainstorming → innovation strategy → PRFAQ). The PRD replaces it with self-dogfooding: Raha using the app personally for real spending is now the validation mechanism (see Success Metrics below), not a pre-launch blocker. Watch for this becoming a 4th stall — it's flagged in the PRD's own Open Questions as a real risk, not assumed solved.
- **Hosting/persistence is now a stated NFR constraint**, not just an open gap: the system must run on persistent, non-ephemeral storage suitable for long-term personal use. The specific hosting provider/choice is still deferred to the architecture phase.
- **Success metrics defined for the first time** (none existed upstream — the PRFAQ explicitly had none). Primary: weekly real use, sustained 3+ months. Secondary: sub-5-second quick-add via frequent-expense chip. Counter-metric: don't optimize feature count/breadth at the cost of quick-add's speed or simplicity.
- **Competitive framing sharpened:** market research (2026-07-02) found no competitor combining fast manual entry with non-shaming lapse recovery — but a later PRFAQ-stage research pass (never folded back into the market research doc itself) found **Koody** to be a near-exact positioning match. Differentiation on the non-shaming axis remains an unproven hypothesis, tracked via dogfooding, not a pre-launch blocker.
- **Dashboard also shows days remaining in the current period** alongside budget status (surfaced from EXPERIENCE.md during PRD reconciliation — a real UX detail that hadn't been captured here before).
- **Required-description field is confirmed again**, with its rationale now explicit in the PRD: it deliberately overrides market/domain research's "keep description optional for speed" recommendation, reconciled via the frequent-expense shelf (chips pre-fill description, so only new/one-off entries pay the typing cost).
- **`addendum.md` now holds technical-how detail** the architecture phase should read: stack wiring pitfalls (CORS, DTOs, exception handler, layering — already listed above, now with a home), monolith architecture shape, the deferred auth/JWT branch point, and the full copy-tone (shame vs. guilt) rationale behind the red-banner color override.

## Critical Implementation Rules

### Development Workflow Rules

- Commit messages follow a short prefix + colon format, e.g. `upd: <summary>` (observed in git history) — keep this format consistent until told otherwise.
- Planning artifacts live under `_bmad-output/` (e.g. `_bmad-output/design-thinking-2026-07-02.md`, `_bmad-output/brainstorming/...`) — don't scatter planning docs elsewhere.
- `docs/` is the designated project-knowledge folder (currently empty) — reference material belongs there, not in `_bmad-output/`.

### Critical Don't-Miss Rules

- This file is a placeholder. Absence of documented rules here does NOT mean no conventions apply — check for an `architecture.md` or PRD before assuming.
- Regenerate this file (run `/bmad-generate-project-context` again) once the tech stack and architecture are decided, so real language/framework/testing rules can be captured.
