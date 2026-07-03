---
title: Input Reconciliation — Technical Stack Research vs. PRD
type: reconciliation-note
target_prd: prd-bmad-expense-tracker-2026-07-03/prd.md
source_input: research/technical-nextjs-spring-boot-postgresql-stack-research-2026-07-02.md
date: 2026-07-03
---

# Reconciliation: Technical Stack Research → PRD

## 1. Scope of this pass

The PRD deliberately keeps technical HOW out of its body — capabilities only, tech choices deferred to an `addendum.md` that does not yet exist anywhere in the repo (confirmed: the only `addendum.md` on disk is `briefs/brief-bmad-expense-tracker-2026-07-02/addendum.md`, which carries market-research depth for the brief and is unrelated to this PRD's architecture-phase addendum). Two spots in the PRD explicitly promise to carry technical rationale to that future addendum:

- §6 Cross-Cutting NFRs → **Persistence** line
- §5.2 FR-5 → **Notes**

This pass checks both against the technical research report, and separately flags research content that isn't captured anywhere yet.

## 2. Do §6 Persistence and §5.2 FR-5 Notes correctly capture the research?

**Verdict: yes, both are correctly scoped, and neither smuggles in implementation detail. But neither is actually sourced from this research document — the overlap is smaller than the PRD's phrasing implies.**

- **§6 Persistence** ("persistent, non-ephemeral storage... not a free-tier setup that expires or resets... hosting provider/choice is an architecture-phase decision"): This research report explicitly excludes deployment/hosting/infrastructure topics from scope ("Implementation Approach: Testing" section states "CI/CD pipelines, infrastructure-as-code, monitoring/observability... were already excluded from this research by design"). Nothing in this document discusses ephemeral vs. non-ephemeral storage, free-tier database expiry, or hosting providers. **The Persistence line is not wrong, but it is not derived from this input — it appears to originate elsewhere (product-brief long-term-use framing) or as a PM-added requirement.** No correction needed against this source; flagged only so a future reader doesn't assume this research backs that claim.
- **§5.2 FR-5 Notes** ("no-stored-totals rule is an architectural guardrail... rationale carried to addendum.md"): Loosely supported by the research's observation that PostgreSQL/Spring Data JPA make "aggregate queries (SUM by category/period) for the derived-totals rule" straightforward — i.e., live-computed totals are a natural fit for this stack, not a workaround. That's the only overlap; the research doesn't discuss schema-level enforcement (no totals column) at all — that specific mechanism is domain-research-derived, not tech-research-derived. The Notes line stays correctly abstract (no mention of JPA/Postgres/schema DDL) and doesn't overreach.

No smuggled implementation detail found in either line, and no PRD-relevant *capability-level* content from this research is missing from them — because this research report is almost entirely implementation-level by design (stack fit, layering, project structure, pitfalls, testing). There is no capability-level finding in it that the PRD fails to surface.

## 3. Research content that is correctly excluded from the PRD body but should not be lost

All of the following are appropriately kept out of the PRD (they are implementation HOW, not capability WHAT) but currently exist **only** in this research report. Since `addendum.md` doesn't exist yet, there is a real risk they get lost or re-derived from scratch once the architecture phase starts, especially the ones the research itself calls out as the project's main actual risk.

1. **Wiring pitfalls the research flags as the project's primary real risk** — not technology-choice risk: exposing JPA entities directly as API responses, missing CORS config (blocks the very first API call silently), unhandled exceptions leaking raw stack traces, and the fix for each (DTOs at the API boundary, explicit CORS config from day one, centralized `@ControllerAdvice` exception handling, and keeping the Controller → Service → Repository split even for simple endpoints). These five items are the report's own "Recommendations" section — should be carried into `addendum.md` verbatim as day-one setup guidance.
2. **Auth open branch point (stateless JWT).** The research explicitly frames authentication as conditional: stateless JWT is "the pattern to reach for" *if* the MVP ever gets real user accounts, and can be "skipped entirely for now" if single-user/local. The PRD resolves the *current* state correctly (§2.2 Non-Users, §6 Identity: no auth/accounts in v1), but the forward-looking technical pattern for if/when that changes (e.g. a v2 multi-user pivot) exists only in this research doc and isn't referenced anywhere in the PRD or elsewhere. Worth a one-line pointer in `addendum.md` so it isn't rediscovered from scratch later.
3. **`ddl-auto` schema-generation guidance for dev, with a caveat.** Research recommends letting Hibernate auto-generate schema from entities during MVP development, revisiting with a real migration tool only if/when controlled versioning is needed. This is fine to carry to the addendum as-is, but it's worth explicitly cross-referencing it against the PRD's own structural guardrail (§6 Data integrity guardrail: "no totals/remaining column exists in the schema at all") when the addendum is written — i.e., confirm entity design simply never includes a total/remaining field, so `ddl-auto` generating schema straight from entities can't accidentally reintroduce a stored-total column.
4. **Testing approach** (`@WebMvcTest`, status → content-type → payload progression) — not mentioned anywhere in the PRD (correctly, it's an implementation practice), and currently exists only in this research doc's "Implementation Approach: Testing" section. Should be captured in the addendum or a future dev/architecture doc so it isn't lost.
5. **Project structure and architecture-pattern decisions** — two top-level folders (`frontend/`, `backend/`) rather than a monorepo tool (not applicable since the backend isn't JS), feature-grouped Next.js folders over deep technical nesting, monolithic Spring Boot (not microservices) as the deliberate MVP choice, and REST+JSON as the sole integration pattern. All correctly absent from the PRD body; all should land in the addendum's project-structure/architecture section rather than being silently dropped.

## 4. Summary

- No PRD-relevant capability-level content from this research is missing from the PRD, and neither of the two flagged touchpoints (§6 Persistence, §5.2 FR-5 Notes) smuggles in implementation detail — both stay correctly abstract.
- The §6 Persistence line's specific concern (non-ephemeral storage, no free-tier expiry) is not actually sourced from this research document, which excludes hosting/deployment topics entirely — not an error, just worth knowing it derives from elsewhere.
- The real gap is downstream, not in the PRD: five categories of implementation guidance (wiring pitfalls/recommendations, the auth stateless-JWT branch point, `ddl-auto` dev-schema guidance with a cross-check against the no-stored-totals guardrail, testing approach, and project-structure/monolith decisions) exist only in this research report and have nowhere to land, because `addendum.md` doesn't exist yet. They should be seeded into that file when the architecture phase begins, ideally before this research report is archived/forgotten.
