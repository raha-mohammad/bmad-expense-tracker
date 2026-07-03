# Version & Platform Verification Review — ARCHITECTURE-SPINE.md (bmad-expense-tracker)

Reviewed: `_bmad-output/planning-artifacts/architecture/architecture-bmad-expense-tracker-2026-07-03/ARCHITECTURE-SPINE.md`
Lens: adversarial — every committed version/platform claim independently web-verified, not accepted from training data.
Date of review: 2026-07-03 (searches run "as of" this date)

## Verdict

**Pass, with one framing correction required.** Every named technology and version in the Stack table is real, current, and internally coherent for a 2026-07-03 pin — nothing fabricated, nothing stale, nothing from-the-future. Railway and Vercel are both real platforms with the specific plan names and mechanics the spine claims (Hobby $5/mo, non-expiring paid volumes, no default sleep; Vercel Hobby free/non-commercial with a 100GB bandwidth cap). The one substantive problem: **the spine states "Railway (Hobby, $5/mo)" as if that is the expected total bill, but Railway's Hobby plan is a $5 subscription that includes only a $5 *usage credit* — running a Spring Boot JVM plus a Postgres instance 24/7 (which this architecture requires, since Railway Hobby does not sleep by default and the spine explicitly wants that) will very likely exceed $5/mo in metered CPU/RAM/storage, landing realistically in the $10–30/mo range.** This doesn't change the platform decision (Railway is still the right choice over Render's free tier for this use case), but the spine's cost representation should be corrected before anyone budgets against it.

## Findings

### 1. [VERIFIED] Next.js 16.2 LTS (16.2.10) — real, current, correctly labeled
Next.js introduced an official Node-style LTS policy in 2024 (Active LTS until the next major ships, then two years of Maintenance LTS). Version 16.2.10 is confirmed as the current LTS patch as of July 2026 by independent EOL-tracking sources (eosl.date: "16.2.10 LTS – July 2026 Release") and matches npm's latest-published tag. The official `nextjs.org/blog/next-16-2` post corroborates the 16.2 minor line (startup perf, `next start` debugging, View Transitions). One minor imprecision worth a note: LTS status formally attaches to the major line (16.x is Active LTS until 17 ships), not to "16.2" as its own LTS branch — the spine's phrasing "Next.js 16.2 LTS" reads as if 16.2 itself is a distinct LTS designation. Not a fabrication, just slightly loose wording.
**Severity: informational.** No action required beyond an optional wording tweak ("Next.js 16 (Active LTS), pinned at 16.2.10").

### 2. [VERIFIED] Java 25 LTS — real, correctly labeled, on-schedule
JDK 25 reached General Availability September 16, 2025, and is an official LTS release per Oracle's established cadence (17 → 21 → 25), carrying at least 5 years of premier support. Nothing about this is asserted from stale training data — it checks out against openjdk.org and multiple independent trackers.
**Severity: none.**

### 3. [VERIFIED] Spring Boot 4.1.0 / Spring Framework 7 — real and current, not a training-data guess
Spring Boot 4.0.0 GA'd November 20, 2025 on Spring Framework 7.0 (confirmed via spring.io's own blog). Spring Boot 4.1.0 GA'd **June 10, 2026** — confirmed directly from `spring.io/blog/2026/06/10/spring-boot-4/` — built on Spring Framework 7.0.8, requiring Java 17 as a floor. This is the actual latest stable Spring Boot release at the spine's 2026-07-03 authoring date, not a hypothetical/future version and not an outdated one either. This is exactly the kind of number a training-data-only answer would likely get wrong (pre-cutoff knowledge would show Spring Boot 3.x / Framework 6.x as current) — the web check confirms the spine got the major-version jump right.
**Severity: none.**

### 4. [VERIFIED] Spring Boot 4.1 / Spring Framework 7 × Java 25 — no compatibility issue
Spring Framework 7.0 is explicitly built and tested against Java 25 as "the latest LTS," while retaining a Java 17 baseline for compatibility. Java 24+'s new Class-File API is adopted by Spring Framework 7 via a new `ClassFileMetadataReader`. No version-mismatch or unsupported-combination risk found. This pairing is a deliberately-aligned target combination, not an untested edge case.
**Severity: none.**

### 5. [VERIFIED] PostgreSQL 18.4 — real, current, plausible minor-release cadence
PostgreSQL 18.4 was released May 14, 2026 (postgresql.org's own release notes and news post), fixing 11 CVEs and 60+ bugs, part of the routine quarterly minor-release train for the PostgreSQL 18 major line. Consistent with a 2026-07-03 "current" pin — not stale, not fabricated.
**Severity: none.**

### 6. [CONFIRMED, WITH CAVEAT — MEDIUM] Railway Hobby "$5/mo" undersells the realistic bill for this architecture
Confirmed via `docs.railway.com/pricing/plans` and `railway.com/pricing` directly: Hobby is a **$5/month subscription that includes $5 of usage credit** — not a $5 flat fee. Metered rates beyond that credit: RAM ≈ $10/GB/month, CPU ≈ $20/vCPU/month, volume storage ≈ $0.15/GB/month, egress ≈ $0.05/GB, all billed per-second. Crucially, the spine's own design leans on Railway Hobby's default **no-sleep** behavior (services run 24/7 unless the opt-in "Serverless" toggle is enabled) — precisely the behavior the spine wants, to avoid cold starts and match "no expiry/sleep." But 24/7 compute for a JVM Spring Boot process plus an always-on Postgres instance is exactly the workload that exceeds the $5 credit fastest: multiple independent cost breakdowns (Railway's own rate card applied to a minimal 0.5 vCPU/512MB–1GB config for each service) put a small always-on Java-app-plus-Postgres stack at roughly **$10–30/mo**, not $5/mo. The spine's Stack table entry "Railway (Hobby, $5/mo)" and the memlog's implied cost comparison against Render's free tier should be read as "$5/mo subscription floor, real bill likely higher" — as currently written it could mislead whoever owns the budget.
**Severity: MEDIUM — factual/framing correction, not a platform-choice error.**
**Fix:** Reword the Stack table row to something like "Railway (Hobby plan, $5/mo base + metered usage — realistic run cost for an always-on API + Postgres is closer to $10–25/mo)" so nobody is surprised by the first invoice.

### 7. [VERIFIED] Railway persistent volumes / no-expiry claim holds up against Render free tier — but compares paid-vs-free
Two things independently confirmed:
- Render's **free** Postgres tier does expire — 30 days after creation (reduced from 90 days per Render's own changelog), with a 14-day grace period before deletion. This matches what the memlog cites.
- Railway's **paid Hobby** plan does not impose an equivalent auto-expiry on its persistent volumes, and Hobby services do not sleep by default (only the free/trial tier and explicitly-opted-in "Serverless" services sleep).

The comparison is accurate but is comparing a **paid** Railway plan against Render's **free** plan. Render also has paid Postgres tiers without the 30-day expiry (Starter at ~$7/mo for 256MB RAM/1GB storage, Basic at ~$20/mo for anything production-viable) — so the real tradeoff is "pay something on either platform to avoid expiry," and Railway's paid floor happens to be lower and its billing more granular (usage-metered vs. Render's flat tiers). This is a reasonable basis for the decision, but the spine (and presumably the memlog it's sourced from) should be read as "Railway avoids Render's *free*-tier expiry problem," not "Railway solves a problem Render can't solve at any price."
**Severity: LOW — clarify comparison basis, decision itself is sound.**

### 8. [VERIFIED] Vercel free (Hobby) tier is a legitimate long-term fit for this specific use case
Confirmed via Vercel's own docs (`vercel.com/docs/plans/hobby`, `vercel.com/docs/limits`) and Terms of Service: Hobby is free indefinitely, with 100GB/month bandwidth, ~100K function invocations, 10s function timeout (300s if Fluid Compute is enabled), 1 concurrent build, 6,000 build minutes/month. These are generous for a single-user personal expense tracker with a thin Next.js presentation layer that does no server-side DB access (per AD-2, all data access is via REST calls to `apps/api`). Two real constraints worth naming explicitly in the spine, not just implicitly assumed:
- **Non-commercial only.** Vercel's ToS restricts Hobby to personal/non-commercial use — no payments, ads, or paid-employee-authored commercial deployments. Fine for "Sam, solo user," but this should be a conscious constraint, not an accident, if the project's status ever changes (e.g., open-sourcing with a sponsor button, or turning it into a paid product later).
- **Usage-cap pause, not throttle.** Exceeding a Hobby limit (e.g., 100GB bandwidth from a traffic spike, bot crawl, or misconfigured build loop) **pauses the whole project/team** until the next 30-day cycle resets or until upgrading — it does not gracefully degrade. Unlikely for a solo user under normal use, but worth a one-line acknowledgment given the spine already documents other edge-case tradeoffs this precisely (e.g., AD-6's copy-forward rule).

Neither constraint changes the platform choice; both are real and were not previously reality-checked in the spine's text.
**Severity: LOW — informational, optional one-line addition.**

### 9. [Meta-finding] A prior review's assumption about Next.js LTS was itself unverified and turned out wrong
`reviews/review-rubric-walker.md` Finding 6 states: *"Next.js/Vercel has not historically published Node.js-style 'LTS' designated release lines... This may... confuse a future reader..."* — this claim is itself asserted from what reads like pre-2024 training-data knowledge. Next.js did formally adopt an LTS policy in 2024 (Active LTS / Maintenance LTS / EOL phases), so the "16.2 LTS" label is legitimate terminology, not a confusable borrowing from Node. This doesn't invalidate that review's other findings, but it's a concrete example of exactly the failure mode this review was commissioned to check for — flagging it here so it isn't propagated as an open concern.
**Severity: informational — corrects a finding in a sibling review, not the spine itself.**

## Summary table

| Claim | Verified? | Notes |
| --- | --- | --- |
| Next.js 16.2 LTS (16.2.10) | Yes | Real LTS policy since 2024; patch version matches current tracking as of July 2026 |
| Java 25 LTS | Yes | GA'd Sept 2025, standard LTS cadence |
| Spring Boot 4.1.0 / Spring Framework 7 | Yes | GA'd June 10, 2026 — current latest stable, confirmed on spring.io |
| Spring Boot 4.1 × Java 25 compatibility | Yes | Explicitly targeted/tested pairing, no issue |
| PostgreSQL 18.4 | Yes | Released May 14, 2026, routine minor release |
| Railway Hobby $5/mo | Real plan, cost framing misleading | $5 buys a usage credit, not a cap; realistic bill $10–30/mo for this workload |
| Railway persistent volumes, no expiry/sleep | Yes (paid plan only) | Accurate vs. Render's *free* tier; Render also solves this on its paid tiers |
| Render free Postgres expires after 30 days | Yes | Confirmed via Render's own changelog (reduced from 90 days) |
| Vercel Hobby free tier suitable long-term | Yes, with 2 caveats | Non-commercial-only ToS restriction; usage-cap triggers a pause, not a throttle |
