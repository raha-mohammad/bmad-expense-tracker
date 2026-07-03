# Rubric Review — ARCHITECTURE-SPINE.md (bmad-expense-tracker)

Reviewed: `_bmad-output/planning-artifacts/architecture/architecture-bmad-expense-tracker-2026-07-03/ARCHITECTURE-SPINE.md`
Against: PRD (`prd.md`), decision record (`.memlog.md`)
Date of review: 2026-07-03

## Verdict

**Conditional pass.** The spine is disciplined, terse, and its 8 ADs are genuinely enforceable and traceable to real FR-level divergence risk (category kind/deletion, budget copy-forward, derived-totals invariant, error shape). But one whole structural dimension the rubric requires (operations/monitoring/observability) is silently missing — not decided, not deferred, not even flagged as an open question — and one confirmed PRD invariant (transaction immutability) has no architectural guardrail despite every other mutable entity (Budget, Category) getting one. Both are fixable with small additions; nothing here requires a structural rework.

## Findings

### 1. [HIGH] Operations/monitoring/observability is completely absent — rubric item 5
Grepped the full document for `monitor|logging|observab|alert|health check|sentry|metrics|tracing` — zero hits outside table-of-contents-style FR references. Deployment & environments describes *where* the app runs (Vercel/Railway) but says nothing about how anyone would know it's broken in production: no logging approach, no error tracking, no health-check endpoint (Railway restart policies typically key off one), no mention of "we're relying on platform-native logs and that's sufficient at solo scale" — which would at least satisfy the rubric's "addressed, even briefly" bar. As written, a future story could ship with zero logging and nothing in the spine would have prevented or even anticipated that. This is the one dimension the rubric explicitly asks about by name and it's silent.
**Fix:** Add one line to Consistency Conventions (or Deferred, if truly punted) — e.g. "Observability: platform-native logs (Vercel/Railway dashboards) only for MVP; no APM/error-tracking tool. Revisit if usage grows" — matching the terse style already used for the Auth and CORS rows.

### 2. [MEDIUM] Transaction immutability has no architectural guardrail — rubric items 1 & 3
PRD Glossary and §8.2 are explicit and confirmed (not tentative): Transactions are immutable, no edit/delete in MVP scope. Budget gets "Set **and edit**" (FR-6) and Category gets full CRUD including delete (AD-5), each backed by an explicit rule. Transaction gets FR-1/FR-2 (create) and FR-3 (period assignment) but nothing in the spine — no AD, no Consistency Convention, no Deferred entry — states "no PUT/DELETE on `/api/transactions`." Nothing currently stops a future story from adding an edit endpoint "for consistency" with Budget/Category, silently reopening a confirmed-excluded feature. This is exactly the kind of divergence-point-for-the-level-below the spine is supposed to fix, and it's the one entity-level rule of the three missing.
**Fix:** One line, either as a Consistency Convention row or folded into the Capability Map: "Transactions are create-only — no update/delete endpoint exists in `apps/api`; a correction is a new offsetting entry, not an edit" (or similar, matching PRD's actual invariant once confirmed with the PM).

### 3. [MEDIUM] AD status tagging is inconsistent
AD-1 through AD-4 each carry an explicit `[ADOPTED]` tag in the heading; AD-5 through AD-8 do not (headings are bare, e.g. "### AD-6 — Budget is a per-Period row, and it copies forward"). Given the document frontmatter marks overall `status: draft`, a reader can't tell whether AD-5–8 are adopted-but-untagged (inconsistent labeling) or deliberately left unadopted/provisional (in which case they shouldn't be binding rules yet). Content-wise AD-5–8 read as fully decided (concrete, `.memlog.md`-backed, "decision by user" entries) — this looks like a tagging omission, not an intentional status difference.
**Fix:** Add `[ADOPTED]` to AD-5–AD-8 headings for consistency, or drop the tag from AD-1–4 if the intent is that tag is redundant with frontmatter status.

### 4. [LOW-MEDIUM] Mermaid Design Paradigm diagram uses risky subgraph IDs
The first diagram (`Design Paradigm` section) declares `subgraph apps/web [apps/web — Next.js]` and `subgraph apps/api [apps/api — Spring Boot]` — using an unquoted `/`-containing string as the subgraph *ID* (not just the label). Mermaid flowchart IDs are conventionally alphanumeric/underscore; a bare `/` in an ID position is atypical and not guaranteed to parse consistently across renderers (`/` has special meaning in node-shape syntax elsewhere in Mermaid, e.g. `id[/text/]`). The later Container-view diagram avoids this by keeping IDs simple (`Web`, `Api`, `PG`) and putting the `apps/web`-style text only inside label brackets, which is the safer pattern.
**Fix:** Rewrite as `subgraph web["apps/web — Next.js"]` / `subgraph api["apps/api — Spring Boot"]`, matching the safer pattern already used in the Container-view diagram.

### 5. [LOW] ERD's self-referencing relationship contradicts its own prose and attribute list
The Core Entity ERD includes `BUDGET_PERIOD ||--|| BUDGET_PERIOD : "copies-from (prior period, on first read)"` — a formal self-referencing 1:1 relationship. But AD-6's rule and the line immediately below the diagram both say this is a one-time Service-layer copy, "a copy, not a live reference" / "no FK to TRANSACTION... never joined and cached" (and by the same logic, never joined to itself either). The `BUDGET_PERIOD` attribute block also has no `prior_period_id` or equivalent FK column, so the diagram depicts a relationship the schema doesn't actually have. This is more a documentation-accuracy nit than a divergence risk (AD-6's prose rule is unambiguous and would win in any conflict), but an ERD is exactly the artifact a future builder skims first, and this entry could be misread as "there's a self-FK to maintain."
**Fix:** Drop the self-referencing relationship line from the ERD; the copy-forward behavior is already fully specified in AD-6's prose and doesn't need (and shouldn't get) a schema-relationship notation.

### 6. [LOW] "Next.js 16.2 LTS" — LTS labeling is atypical for Next.js
Stack table lists "Next.js 16.2 LTS (16.2.10)." Next.js/Vercel has not historically published Node.js-style "LTS" designated release lines — Next.js versions continuously (major.minor.patch) without a formal LTS branch. This may simply mean "the current stable line, treated as long-term-stable for this project," but as written it reads like it's borrowing Node's LTS terminology, which could confuse a future reader into thinking there's an official Next.js LTS channel to track. Not asserting the version number itself is wrong (per instructions, not re-verified) — flagging the "LTS" qualifier as worth a source check.
**Fix:** Either confirm "LTS" is intentional shorthand (and say for what) or drop the qualifier and just state the pinned version.

## Checklist walkthrough

1. **Divergence points fixed / none missed** — Mostly yes (all 10 FRs bound and mapped; category/budget/derived-totals/error-shape divergence points all have enforceable ADs). Gap: Transaction immutability (see Finding 2) is a real entity-level divergence point left unfixed.
2. **AD Rules enforceable, not restatements** — Pass. All 8 Rules add concrete, checkable detail beyond their Prevents clause (schema shape, layering direction, type choices, enum values). None are vague or tautological.
3. **Nothing wrongly deferred** — Pass on the Deferred list itself (Auth, Shelf ordering, icon set, Rollover, staging, API versioning are all genuinely low-risk-to-defer and each has a one-line resolution path). The problem isn't over-deferral, it's under-coverage — see Findings 1 and 2, which are dimensions that got neither an AD nor a Deferred entry.
4. **Named tech plausible/dated 2026-07-03** — Mostly consistent (Java 25 LTS, PostgreSQL 18.4, Spring Boot 4.1/Spring Framework 7 pairing all internally coherent with a mid-2026 date). One flag: "Next.js 16.2 **LTS**" — see Finding 6.
5. **Every structural dimension decided/deferred/open** — Fail on one dimension: operations/monitoring/observability/logging is silently absent (Finding 1). All other checked dimensions (deployment & environments, infra/provider, data model, API contract, testing strategy, CI/CD, error handling, auth) are explicitly addressed.
6. **Stays terse/build-substrate** — Pass. Tables, bullet Rules, and diagrams dominate; no prose essay. AD Rule text occasionally runs long (AD-1, AD-6) but stays decision-dense, not rationale-heavy.
7. **Mermaid diagrams syntactically plausible & non-empty** — Mostly pass; all three diagrams are non-empty and structurally sound. Two nits: risky subgraph ID syntax in the Design Paradigm diagram (Finding 4), and a self-referencing ERD relationship that contradicts the accompanying prose (Finding 5).
8. **AD numbering stable/sequential, Binds/Prevents/Rule present** — Pass on numbering and completeness (AD-1–8, no gaps/dupes, every AD has all three fields). Minor consistency issue: `[ADOPTED]` tag present on AD-1–4 only (Finding 3).
