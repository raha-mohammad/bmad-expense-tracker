# Addendum: Simple Expense Tracker

Supporting depth from prior research that informed the brief but doesn't belong in a 1–2 page executive document. Useful as input to a future PRD or architecture doc.

## Full User Segments (Market Research)

Three segments were identified; the brief names only the primary one.

1. **The Anxious Starter** (primary in brief) — 18–22, low/irregular income, first exposure to money management. Needs near-zero setup and forgiving feedback. High attrition risk if bank-linking, categorization, or goal-setting is asked before any value is shown.
2. **The Overwhelmed Early Professional** — 23–29, first steady income, juggling debt/rent/savings, already churned from at least one prior budgeting app. Wants pattern visibility over categorization discipline.
3. **The Privacy-Conscious Manual Tracker** — mixed age, deliberately avoids bank-linked/automated apps for data control and mindful awareness. Treated as a legacy fallback segment by most modern competitors, which mostly respond to friction with more automation.

## Competitive Positioning Detail

- **Feature-heavy/automation-first**: YNAB, Monarch, Rocket Money, Copilot — bank-linking, zero-based budgeting, full dashboards. Overwhelming for first-timers.
- **Simplicity-first but automation-leaning**: PocketGuard — single "In My Pocket" safe-to-spend number rather than exposed budget mechanics.
- **Manual-first/privacy-first**:
  - Money Manager — on-device, privacy-focused.
  - Monefy and DailyBean — compete purely on entry speed.
  - Spendee — nice charts, free tier capped at 1 manual wallet, pushes premium/bank-linking.
  - Toshl — manual-friendly, differentiates on multi-currency for travelers.
- **"Shame-free" positioning**: claimed by YNAB and The Budgeting App, but neither is manual-first or fast.
- **Finny** already occupies the exact "manual, free, offline, student-focused" position this project's early framing assumed was open — this is why the brief does not lead with "manual-first" as a differentiator.
- Positioning risk noted in research: "fast" alone is already owned (Monefy/DailyBean); "non-judgmental" alone is already owned (YNAB/The Budgeting App). Only the combination, delivered well, is open ground.

## Market Stats (flagged for verification, not carried into the brief as hard facts)

Most of these trace to blog/marketing sources (Softjourn, Expensify, AppFollow, Pushwoosh, MoneyPatrol) rather than primary research — treat as directional, not precise, if reused downstream:

- 88% of 18–34s use some form of monthly budget; 27% of 18–29s prefer app-based tools (WalletHub/PaymentsJournal).
- Millennials = 70% of current budgeting-app users; low-income earners = 49% of current users, 70% of likely future adopters (WalletHub).
- 61% of 18–35s report financial anxiety; 52% of Gen Z cite economic instability as the root cause (EY study — likely more solid than the others).
- 80% of budgeting-app users check weekly (Softjourn/Expensify — unverified).
- 66% use their bank's mobile app as primary touchpoint.
- 77% check reviews before downloading.
- ~40%+ of tax-use tracking users convert to paid tiers.
- Automated/linked apps see 40–68% higher retention industry-wide.
- A 5% retention gain reportedly lifts profit 25–95% industry-wide.
- No overall market-size (TAM/SAM/SOM) figure exists — market sizing was explicitly excluded from the original research scope. Net-new research is needed if a PRD or pitch later requires one.

## Differentiator Strategy — Options Considered

The innovation strategy work considered how hard to lean into "lapse recovery" before it's validated:

- **Option A**: Build V1 modest, treat lapse recovery as the untested vision (chosen approach, reflected in the brief).
- **Option C**: Test the lapse-recovery hypothesis with real users before committing engineering time to it.
- **Chosen strategy = A+C combined**: ship the modest, credible V1; validate the differentiator with real testers before building it. Testing/tester recruitment was called out as "the weakest link in the entire plan" — no interviews have been conducted as of this brief.

## Domain Model Notes (for future architecture work)

- Core model: Transaction → Category → Period → Total. Deliberately lighter than accounting — no double-entry/general ledger.
- Non-negotiable rule: totals and budget-remaining are always *derived* from transactions at read time, never independently stored or edited.
- Every transaction has exactly one category and belongs to exactly one period, determined by transaction date (not entry date).
- Rollover behavior (carry forward vs. reset unspent/overspent budget) is a real decision point flagged as worth resolving deliberately, even though it's out of V1 scope.

## Technical Research Highlights (Next.js + Spring Boot + PostgreSQL)

- Treated as a confirmed/settled stack by the research, not options under evaluation.
- Architecture: Next.js frontend ↔ Spring Boot REST/JSON API ↔ PostgreSQL via Spring Data JPA/Hibernate. Frontend never touches the database directly.
- Layering: Controller → Service → Repository, with DTOs at the API boundary — JPA entities should never be returned directly from controllers.
- Repo layout: two top-level folders (`frontend/`, `backend/`), not a monorepo tool.
- Auth was explicitly left open in the research and resolved in this brief by skipping it entirely for V1 (single-user, no accounts).
- Schema management: Hibernate `ddl-auto` auto-create for dev speed; a proper migration tool is explicitly deferred, not decided against.
- Three concrete beginner pitfalls called out as the real risk (the stack itself was assessed as low-risk):
  1. Exposing JPA entities directly as API responses.
  2. Unconfigured CORS between `localhost:3000` and `localhost:8080` — will silently block the first API call.
  3. Unhandled exceptions leaking raw stack traces to the client.
- Deployment, hosting, and any bank-feed/Plaid-style integration were explicitly out of scope for the technical research and remain undecided.
