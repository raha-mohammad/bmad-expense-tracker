---
stepsCompleted: [1, 2, 3, 4, 5, 6]
inputDocuments: []
workflowType: 'research'
lastStep: 6
research_type: 'technical'
research_topic: 'Next.js + Spring Boot + PostgreSQL stack for a simple expense tracker MVP'
research_goals: 'Focused, minimal research covering: why this stack is suitable for this project, how these technologies work together, recommended project structure, best practices for a simple MVP, and common pitfalls to avoid. Excludes implementation details, code examples, advanced topics, performance optimization, deployment, and alternative technologies unless essential.'
user_name: 'Raha'
date: '2026-07-02'
web_research_enabled: true
source_verification: true
---

# Research Report: technical

**Date:** 2026-07-02
**Author:** Raha
**Research Type:** technical

---

## Research Overview

This research evaluates the already-chosen stack — Next.js (frontend), Spring Boot (backend), PostgreSQL (database) — for a simple, manual-first expense tracker MVP. Per user direction, scope was kept minimal throughout: why the stack fits, how the three pieces interact, recommended project structure, best practices, and common pitfalls, followed by an essential-only pass over integration, architecture, and implementation concerns. Deep dives into performance optimization, deployment/CI-CD, security compliance, and alternative-technology comparisons were intentionally excluded as not relevant to an MVP at this scale.

The core finding: this stack is a comfortable, well-documented fit for the domain (CRUD-heavy, relationally-structured expense data), and the main risks are not technology-choice risks but well-known beginner mistakes — exposing JPA entities directly, unconfigured CORS, and unhandled exceptions leaking stack traces. A single monolithic Spring Boot backend with a clean Controller → Service → Repository layering, DTOs at the API boundary, and REST+JSON communication is sufficient for the entire MVP; no microservices, message queues, or advanced architecture patterns are warranted at this stage.

See **Research Synthesis** below for the consolidated recommendations.

## Technical Research Scope Confirmation

**Research Topic:** Next.js + Spring Boot + PostgreSQL stack for a simple expense tracker MVP
**Research Goals:** Why this stack is suitable, how the pieces work together, recommended project structure, best practices for a simple MVP, common pitfalls to avoid.

**Technical Research Scope (reduced, per user direction):**

- Why This Stack Is Suitable
- How These Technologies Work Together
- Recommended Project Structure
- Best Practices for a Simple MVP
- Common Pitfalls to Avoid

**Explicitly excluded:** implementation details, code examples, advanced architecture patterns, performance optimization, deployment, alternative-technology comparisons (unless essential to explain a decision).

**Scope Confirmed:** 2026-07-02

---

## Stack Fit and Interaction

### Why This Stack Is Suitable

- Next.js + Spring Boot is a well-established, widely documented pairing — a modern reactive frontend (Next.js) consuming a robust backend REST API (Spring Boot) — used across everything from small apps to enterprise SaaS, so a simple expense tracker MVP is well within its comfortable range rather than pushing the limits of either tool.
- Spring Boot specifically excels at exposing RESTful APIs with minimal boilerplate via Spring Data JPA, which maps directly onto the expense tracker's core need: simple CRUD operations (create/read/update/delete transactions, categories, budgets) over a relational schema.
- PostgreSQL is a natural fit for the domain model already established in prior research (transactions, categories, budget periods, always-derived totals) — it's a mature relational database well suited to structured, relationally-linked data like "transaction belongs to category belongs to budget period," with straightforward aggregate queries (SUM by category/period) for the derived-totals rule.
- The general guidance for whether to use a separate backend at all is instructive here: a separate backend (vs. just Next.js API routes) is worth it when the app needs real business logic, structured data, and a proper database layer — which an expense tracker with budgets, categories, and derived totals genuinely has, even at MVP scale. This confirms the chosen split (Next.js frontend, Spring Boot backend) is the right shape, not over-engineering.
_Source: [Why React/Next.js and Spring Boot Are the Perfect Stack — Selmir](https://selmir.com/blogs/react-nextjs-springboot-perfect-stack), [Guides: Backend for Frontend — Next.js docs](https://nextjs.org/docs/app/guides/backend-for-frontend), [Spring Boot + React/Next.js: How to Build a Full-Stack App — DEV](https://dev.to/dev_tips/spring-boot-reactnextjs-how-to-build-a-full-stack-app-in-2025-906)_

### How These Technologies Work Together

- **Next.js (frontend)** renders the UI and calls the Spring Boot backend over HTTP as a REST client — it does not talk to PostgreSQL directly. This keeps a clean boundary: Next.js owns presentation/UX, Spring Boot owns business logic and data access.
- **Spring Boot (backend)** exposes REST endpoints (Controller layer) that delegate to a Service layer (business logic — e.g., computing budget remaining) which in turn uses a Repository layer (Spring Data JPA) to talk to PostgreSQL. This Controller → Service → Repository layering is the standard, well-documented pattern for this exact combination.
- **PostgreSQL (database)** is accessed only through Spring Data JPA/Hibernate — entity classes map to tables, and `JpaRepository<T, ID>` gives basic CRUD (save/find/delete) without hand-written SQL for the common cases; Hibernate's PostgreSQL dialect setting ensures queries are generated correctly for Postgres specifically.
- The three layers communicate one-directionally in normal operation: Next.js → (HTTP/JSON) → Spring Boot Controller → Service → Repository → PostgreSQL, and the response flows back the same path in reverse.
_Source: [Spring Boot, PostgreSQL, JPA, Hibernate RESTful CRUD API Example — CalliCoder](https://www.callicoder.com/spring-boot-jpa-hibernate-postgresql-restful-crud-api-example/), [RESTful CRUD API using PostgreSQL and Spring Boot — EnterpriseDB](https://www.enterprisedb.com/blog/restful-crud-api-using-postgresql-and-spring-boot-part-2)_

---

## Project Structure, Best Practices, and Pitfalls

### Recommended Project Structure

- Since Next.js (frontend) and Spring Boot (backend) are separate runtimes with different ecosystems (npm vs. Maven/Gradle), the simplest structure for an MVP is two top-level project folders in one repo (e.g. `frontend/` and `backend/`) rather than a full monorepo tool (Turborepo, workspaces) — those tools exist to share code and manage many JS/TS packages, which doesn't apply when the backend isn't even JavaScript.
- Inside `backend/` (Spring Boot), the standard, widely-documented layering is: **Controller** (REST endpoints) → **Service** (business logic) → **Repository** (Spring Data JPA, database access) → **Entity/Model** (JPA-mapped classes), plus a separate **DTO** layer for what actually crosses the API boundary.
- Inside `frontend/` (Next.js), best-practice guidance is to keep the folder structure flat and avoid deep nesting, and to group related UI/logic by feature (e.g. `features/expenses/`, `features/budgets/`) rather than by technical type — this scales better even for a small app and avoids restructuring later.
_Source: [The Ultimate Guide to Organizing Your Next.js Project Structure — Wisp CMS](https://www.wisp.blog/blog/the-ultimate-guide-to-organizing-your-nextjs-15-project-structure), [Spring Boot, PostgreSQL, JPA, Hibernate RESTful CRUD API Example — CalliCoder](https://www.callicoder.com/spring-boot-jpa-hibernate-postgresql-restful-crud-api-example/)_

### Best Practices for a Simple MVP

1. **Use DTOs, not entities, at the API boundary.** Entities belong to the database layer; DTOs belong to the API layer — even for an MVP, this avoids leaking database structure (or sensitive fields) to the frontend and prevents API breakage every time a database column changes.
2. **Centralize exception handling.** A single Spring `@ControllerAdvice`-style handler prevents raw stack traces from leaking to the frontend as confusing/insecure 500 responses.
3. **Let Hibernate manage schema in dev, not by hand.** Spring Boot's `ddl-auto` can auto-create tables from entity classes during development, which is appropriate for MVP-speed iteration (a more controlled migration approach can come later if needed).
4. **Keep the Controller → Service → Repository split even at small scale.** It costs little extra effort and is what makes adding the "budget remaining" computation (business logic) an obvious Service-layer responsibility rather than something that leaks into the Controller or Repository.
5. **Configure CORS deliberately from day one**, since Next.js (e.g. `localhost:3000`) and Spring Boot (e.g. `localhost:8080`) run as separate origins even in local development — this is not an edge case to handle later, it will block the very first API call otherwise.
_Source: [Spring Boot Best Practices: Use DTOs Instead of Entities — JavaGuides](https://www.javaguides.net/2025/02/use-dtos-instead-of-entities-in-api.html), [10 Common Mistakes to Avoid When Building Spring Boot APIs — Medium](https://medium.com/@shaurya-afk/10-common-mistakes-to-avoid-when-building-spring-boot-apis-and-what-to-do-instead-1f6ca883ab1f)_

### Common Pitfalls to Avoid

- **Exposing JPA entities directly as API responses.** This is repeatedly flagged as the most common Spring Boot mistake — it leaks internal structure/sensitive fields, couples the API contract to the database schema, and tends to over-fetch data the frontend doesn't need.
- **Hitting CORS errors and not knowing why.** The frontend (port 3000) calling the backend (port 8080) is a cross-origin request by definition in local dev; without explicit CORS configuration on the Spring Boot side, every request will fail in the browser even though the backend "works" when tested directly.
- **Letting unhandled exceptions reach the client as raw stack traces.** Without centralized exception handling, ordinary errors (e.g. "expense not found") become unhelpful generic 500s that also risk exposing backend internals.
- **Deep, over-organized folder nesting on the Next.js side before it's needed.** Guidance explicitly warns against creating many levels of subfolders prematurely — for an MVP this is wasted structure that gets in the way rather than helping.
_Source: same as above; [Solving CORS Errors for Full-Stack Developers Using React.js and Spring Boot — Medium](https://medium.com/@princekumar161999/solving-cors-errors-for-full-stack-developers-using-react-js-and-spring-boot-83dc6252c519)_

---

## Integration Patterns, Architecture, and Implementation (Essential-Only)

_Scope note: this document originally excluded advanced integration/architecture/implementation topics. Per follow-up user request, these three areas are now covered — but still filtered to what's essential for a Next.js + Spring Boot + PostgreSQL MVP, explicitly skipping GraphQL, gRPC, message queues/Kafka, event sourcing, CQRS, service mesh, microservices patterns, mTLS, service discovery, CI/CD pipelines, and cost/team-organization topics, none of which apply to a single small monolithic app._

### Integration Pattern: REST + JSON (essential only)

- REST over HTTP with JSON is the standard, already-implied communication pattern for this stack (Spring `@RestController` auto-converts responses to JSON) — no other API style (GraphQL, gRPC) is needed or suggested for a simple CRUD app talking to one frontend.
- Authentication is the one integration decision worth flagging even for a minimal MVP: stateless, token-based auth (e.g. JWT) is the standard fit for a REST API called from a separate Next.js frontend, since each request is independent and there's no shared server session between the two runtimes. Whether this is needed at all depends on whether the MVP is single-user/local or has real user accounts — if it's the latter, this is the pattern to reach for; if the former, it can be skipped entirely for now.
_Source: [Basic REST API Security with Spring Security — Medium](https://medium.com/@barbieri.santiago/basic-rest-api-security-with-spring-security-9f5d3a254af8), [Testing an OAuth Secured API with Spring MVC — Baeldung](https://www.baeldung.com/oauth-api-testing-with-spring-mvc)_

### Architecture Pattern: Monolith (essential only)

- For an MVP, a single monolithic Spring Boot backend (not microservices) is the clear, consistently-recommended choice: monoliths are faster to build, require less infrastructure, and are easier to change and test when a team is small and the goal is validating the product — exactly this project's situation.
- Spring Boot supports both monolithic and microservices styles equally well, and starting monolithic does not lock the project out of splitting services later if it ever needs to — so choosing monolith now is not a one-way door.
- No further architectural pattern (event-driven, service mesh, distributed transactions) is relevant at this scale; the Controller → Service → Repository layering already covered in this document is the complete architectural picture needed for the MVP.
_Source: [Monoliths vs Microservices for MVPs — Medium](https://medium.com/mop-developers/monoliths-vs-microservices-for-mvps-c08bc9595975), [Monolith vs Microservices for MVP Apps — ShivLab](https://shivlab.com/blog/monolith-vs-microservices-mvp-apps-cost-speed-growth/)_

### Implementation Approach: Testing (essential only)

- The one implementation practice worth adopting even at MVP scale is basic automated testing of the REST layer: Spring Boot's `@WebMvcTest` allows testing controllers in isolation, and a simple test progression (check response status → check content type → check JSON payload contents) is enough to catch regressions without a heavy testing framework investment.
- CI/CD pipelines, infrastructure-as-code, monitoring/observability, and cost/team-organization planning are all out of scope here — they're deployment/operations concerns, not implementation concerns, and were already excluded from this research by design.
_Source: [Testing Spring Boot Rest APIs with Rest-Assured — Keyhole Software](https://keyholesoftware.com/testing-spring-boot-rest-apis-with-rest-assured/), [Spring Boot Unit Testing REST APIs Tutorial — CodeJava](https://www.codejava.net/frameworks/spring-boot/unit-testing-rest-apis-tutorial)_

---

## Research Synthesis

_Scope note: kept minimal per user direction — no performance/scalability, security-compliance, deployment, or future-outlook sections, since those were excluded by design._

### Executive Summary

Next.js + Spring Boot + PostgreSQL is a well-trodden, appropriately-sized stack for this MVP — the risk in this project is not technology fit, it's a handful of well-known beginner mistakes in how the pieces are wired together. Get the layering, DTO boundary, and CORS config right from the start, and the stack requires no further technical decisions to ship the MVP.

### Key Findings

- The stack is a natural match for the domain model already established (transaction → category → budget period → derived totals) — PostgreSQL's relational structure and Spring Data JPA's repository pattern map directly onto it.
- Next.js and Spring Boot communicate exclusively via REST/JSON over HTTP; PostgreSQL is only ever reached through Spring Boot's Service/Repository layers, never directly from the frontend.
- A monolithic backend with Controller → Service → Repository layering is the right architecture at this scale — not a limitation to work around, but the recommended approach for an MVP.
- The most-cited real risks are implementation mistakes, not architecture mistakes: exposing JPA entities as API responses, missing CORS configuration, and unhandled exceptions leaking raw errors to the client.

### Recommendations

1. Set up CORS configuration on the Spring Boot side before writing the first frontend API call — it will otherwise block everything silently.
2. Define DTOs for every API response from the start; never return JPA entities directly, even for "just an MVP."
3. Add a centralized exception handler early so errors return clean, predictable responses instead of stack traces.
4. Keep the Controller → Service → Repository split even for simple endpoints — it's where the "budget remaining" business logic (from the domain research) naturally lives.
5. Use Hibernate's `ddl-auto` schema generation during development; revisit with a proper migration tool only if/when the schema needs controlled versioning later.

### Next Steps

- Carry these recommendations directly into implementation setup (project scaffolding, CORS config, DTO/entity split) when development begins.
- No further technical research is needed for MVP scope; deeper topics (performance tuning, deployment pipeline, authentication strategy if user accounts are added) can be revisited if/when those become relevant.

---

**Technical Research Completion Date:** 2026-07-02
**Source Verification:** All findings cited with sources inline throughout the document above.
**Scope:** Narrowed by user direction to stack fit, integration, structure, best practices, and pitfalls — with a later essential-only addition of integration/architecture/implementation topics. Performance, security compliance, deployment, and alternative-technology analysis were intentionally excluded.
