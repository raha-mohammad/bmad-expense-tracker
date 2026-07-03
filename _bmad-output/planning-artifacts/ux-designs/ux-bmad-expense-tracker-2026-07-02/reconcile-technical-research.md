# Reconciliation — Technical Research (technical-nextjs-spring-boot-postgresql-stack-research-2026-07-02.md)

## Carried forward faithfully
- Next.js frontend / Spring Boot REST API / PostgreSQL, frontend never touches the database directly — restated correctly in EXPERIENCE.md's Foundation section.
- Single-user, no-auth-for-V1 stance ("auth was explicitly left open in the research... resolved by skipping it entirely") — matches EXPERIENCE.md's "No authentication in V1 — single-user application."
- Backend-persisted data rather than browser local storage — consistent with "Data is backend-persisted via the Spring Boot API (PostgreSQL)."

## Deliberate overrides (confirmed documented in spec)
- **shadcn/ui / Tailwind as the frontend component system.** The technical research is silent on any frontend UI/component library — it discusses only Next.js as a runtime/rendering choice, folder layout, and the Spring Boot side. The later addition of shadcn/ui (memlog: "new information — narrows/extends earlier Next.js+Spring Boot+PostgreSQL stack") is therefore not an override of anything the technical research asserted, and DESIGN.md handles it correctly: it explicitly inherits shadcn defaults wholesale ("All unlisted tokens inherit from shadcn... Standard shadcn components... inherit shadcn's visual specs as-is wherever this file doesn't say otherwise") rather than inventing a parallel token/component system. This is the right treatment — confirmed, no concern here.

## Dropped or under-represented (flag for awareness, not necessarily a defect)
- The bulk of this document (Controller→Service→Repository layering, DTO boundary, CORS configuration, centralized exception handling, `ddl-auto` schema management, monolith-vs-microservices reasoning, `@WebMvcTest` testing guidance) is backend/architecture-layer material with no UX surface — its absence from DESIGN.md/EXPERIENCE.md is expected and correct, not a gap. Flagging only so it's clear this was a deliberate scope boundary (UX spec vs. architecture doc) rather than an oversight.

## Undocumented drift (real concern — spec differs from source with no override rationale visible)
- None found. Nothing in the technical research constrains or contradicts anything asserted in DESIGN.md/EXPERIENCE.md.
