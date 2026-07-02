---
project_name: 'bmad-expense-tracker'
user_name: 'Raha'
date: '2026-07-02'
sections_completed: ['technology_stack', 'product_domain_analysis', 'development_workflow_rules', 'critical_donts_miss_rules']
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
- **MVP transaction fields:** date + amount + category only. Notes/tags/granular categories are explicitly deferred — category selection (not amount entry) is the known drop-off point (market research).
- **Emotional/UX contract, not just speed:** the product's differentiation is being fast *and* non-punishing after a lapse — no guilt copy, no red "you're over budget" framing, no catch-up burden on return. Favor observational framing ("you've spent $X on dining this week") over pass/fail (design thinking + market research).
- **Setup must be deferred:** let a user log an expense before asking for budgets/goals/account linking (market research).
- **Validated concepts to carry into UX/architecture:** quick-add entry, a neutral (not red/green alarmist) budget status indicator, and a frequent-expenses shelf for one-tap re-entry. A reusable summary-card component with a color-state prop was flagged as a likely shared UI pattern (design thinking + brainstorming).
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

## Critical Implementation Rules

### Development Workflow Rules

- Commit messages follow a short prefix + colon format, e.g. `upd: <summary>` (observed in git history) — keep this format consistent until told otherwise.
- Planning artifacts live under `_bmad-output/` (e.g. `_bmad-output/design-thinking-2026-07-02.md`, `_bmad-output/brainstorming/...`) — don't scatter planning docs elsewhere.
- `docs/` is the designated project-knowledge folder (currently empty) — reference material belongs there, not in `_bmad-output/`.

### Critical Don't-Miss Rules

- This file is a placeholder. Absence of documented rules here does NOT mean no conventions apply — check for an `architecture.md` or PRD before assuming.
- Regenerate this file (run `/bmad-generate-project-context` again) once the tech stack and architecture are decided, so real language/framework/testing rules can be captured.
