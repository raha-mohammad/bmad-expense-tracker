---
title: bmad-expense-tracker PRD Addendum
created: 2026-07-03
updated: 2026-07-03
---

# Addendum: bmad-expense-tracker

Technical-how and rejected-alternative detail that doesn't belong in the PRD body, carried forward for the architecture phase. Source: `research/technical-nextjs-spring-boot-postgresql-stack-research-2026-07-02.md` unless noted.

## Architecture shape (from technical research, not re-litigated here)

Monolith, not microservices — faster to build, less infrastructure, easier to test for a solo build validating a product; not treated as a one-way door. Next.js calls the Spring Boot backend via REST/JSON only, never touches PostgreSQL directly.

## Stack wiring pitfalls (build into the initial skeleton, not bolted on later)

- Configure CORS on Spring Boot before the first frontend call.
- Never return JPA entities directly from controllers — always DTOs at the API boundary.
- Add a centralized exception handler (`@ControllerAdvice`) early.
- Keep Controller → Service → Repository layering even for simple endpoints.
- Avoid deep folder nesting on the Next.js side before it's needed.

## Derived-totals guardrail (implementation note)

PRD §5.2 FR-5 and §6 require category/period/budget totals to be computed live, never stored. Enforce this structurally: no totals/remaining column should exist in the schema at all, so a service accidentally caching a total becomes a compile/query error, not just a discouraged pattern. When architecture defines the schema (likely via Hibernate `ddl-auto` for dev), confirm entity-driven schema generation doesn't accidentally reintroduce a stored-total column.

## Auth (deferred branch point, not a decision)

No accounts/auth in v1 (PRD §2.2, §6). If a future version ever adds real user accounts, stateless JWT is the recommended pattern per technical research — not evaluated further here.

## Testing approach (MVP-scope guidance, not a hard requirement)

Basic `@WebMvcTest` controller tests (status → content-type → payload) are sufficient without heavier test-framework investment for MVP scope. Revisit only if the project's complexity grows.

## Rejected / deferred alternatives (from PRFAQ, not re-litigated in the PRD body)

- Home-screen widget shortcut — deferred, not v1.
- "Chill / strict mode" tone toggle for budget-status copy — deferred, not v1.
- Voice-to-log input / freeform NL quick-capture parsing — deferred, not v1 (also in PRD §7 Non-Goals).
- A dedicated Lapse Recovery feature (streak-forgiveness, re-entry screen) was explicitly **not** built into v1. Per the PRFAQ's own gate: only reconsider building it if real dogfooding usage surfaces unprompted, independent signal that lapse-guilt is actually a problem for Raha personally — not before, and not just because the hypothesis is untested.

## Budget carry-forward (architecture-phase resolution, 2026-07-03)

The PRD (§8.2) confirms "no rollover" but only describes *spend* resetting to ₹0 each Period — it never states whether the Budget *figure* itself must be re-entered every Period. Architecture (`ARCHITECTURE-SPINE.md` AD-6) resolved this ambiguity: a new Period auto-copies the prior Period's Budget amount as its starting value (independently editable, spend still starts at ₹0). This means UJ-4's "first use" neutral-prompt flow now literally happens once — from Period 2 onward, Dashboard always shows a Budget Status card, never the "set a budget" prompt, unless the user never set one in Period 1. Not a Rollover of spend; only the number carries forward.

## Copy-tone rationale (why color changed, language didn't)

The PRFAQ's original promise was literally "no red banners." UX design later overrode this to visible green/amber/red budget-status color (confirmed in `project-context.md` as a deliberate override). What survived the override is the deeper rule that the "no red" promise was actually protecting against: the PRFAQ and market research both distinguish **shame** (an identity-level judgment — "you failed") from **guilt-as-information** (a behavior-level, correctable signal — "you're over, here's by how much"). Color is allowed to carry severity because color alone doesn't moralize; copy is held to a stricter bar because language is where shaming actually happens. This is why PRD FR-4 requires copy to stay factual ("Budget exceeded by ₹250") at every severity level regardless of color.
