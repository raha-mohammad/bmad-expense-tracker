---
title: "PRFAQ: bmad-expense-tracker"
status: "complete"
created: "2026-07-02"
updated: "2026-07-02"
stage: 5
inputs:
  - "_bmad-output/planning-artifacts/briefs/brief-bmad-expense-tracker-2026-07-02/brief.md"
  - "_bmad-output/planning-artifacts/briefs/brief-bmad-expense-tracker-2026-07-02/addendum.md"
  - "_bmad-output/design-thinking-2026-07-02.md"
  - "_bmad-output/innovation-strategy-2026-07-02.md"
  - "_bmad-output/brainstorming/brainstorm-expense-tracker-features-2026-07-01/brainstorm-intent.md"
  - "_bmad-output/planning-artifacts/research/domain-expense-tracking-research-2026-07-02.md"
  - "_bmad-output/planning-artifacts/research/market-personal-expense-tracking-app-market-research-2026-07-02.md"
  - "_bmad-output/planning-artifacts/research/technical-nextjs-spring-boot-postgresql-stack-research-2026-07-02.md"
  - "_bmad-output/project-context.md"
  - "web research: 2026 budgeting-app competitive landscape (Finny, Koody, PocketGuard Pace, retention benchmarks)"
---

# A Budget Tracker Built for Speed, Not Guilt

## Simple Expense Tracker logs a purchase in seconds and shows an honest, calm picture of your budget — no red banners, no guilt-trip copy, no need to "catch up" before you're allowed to look at it again.

**Fall 2026** — Simple Expense Tracker launched today, a free web app that lets anyone log a purchase in under five seconds and see exactly where their money went this month — without linking a bank account, without a setup process before the first entry, and without a single red warning telling them they've failed.

For someone managing their own money for the first time, the hard part was never deciding to track spending — it was staying with it. A few days of logging felt fine. Then a bad week hit: a missed day, then three, then the moment where opening the app felt less like checking a number and more like facing a verdict. So they quietly stopped opening it — not because tracking didn't work, but because coming back felt worse than never starting.

Simple Expense Tracker removes every reason to avoid checking in. Adding a purchase takes one or two taps — no forms, no bank login, no setup gauntlet. Every screen speaks in plain, steady language: a calm status instead of a verdict, a running total instead of a lecture. And because the numbers are always calculated fresh from what's actually been logged, opening the app after a gap shows today's picture — not a backlog demanding an explanation.

> "I built this because every budgeting app I tried was solving the wrong problem. They're optimized for people who never miss a day — but nobody misses zero days. The real test of a tracker isn't how it looks on day one, it's whether you're still willing to open it on day ten, after you've fallen behind. This version gets the foundation right: fast, honest, no judgment. Where I want to take it next is making the day you come back feel just as easy as the day you started."
> — Raha, Creator

### How It Works

Getting started takes about ten seconds. Open the app — there's no account to create and no bank to connect. Tap the persistent **+** button, enter an amount, and pick a category from five defaults already in place (Food, Transport, Shopping, Bills, Entertainment). If it's something logged before, like a regular coffee order, it's already sitting one tap away on the frequent-expenses shelf.

Checking spending is just as fast: the home screen shows this month's totals by category next to a simple status — green, amber, or red — described in plain terms like "about $40 left at this pace" instead of pass/fail language.

Missing a day doesn't trigger anything. There's no reminder, no backlog view, no "you have 4 unlogged days" counter. The next expense gets logged like any other, and the totals simply reflect what's there.

> "I stopped using my last budgeting app because it made me feel like I was being graded. I forgot to open this one for almost a week, and when I came back it just... showed me my numbers. No lecture. I logged my coffee and moved on."
> — Early user, college sophomore

### Getting Started

Simple Expense Tracker is a web app — no download, no account to create. Open it, tap **+**, and log the first purchase. That's the whole onboarding.

---

## Customer FAQ

### Q: Koody already does free, manual-only, no-bank-linking expense tracking with fast entry. Why would I use this instead?

A: Honestly, manual-only isn't the differentiator anymore — Koody and a few other 2026 entrants already own that ground, and claiming otherwise would be dishonest. The actual bet here is narrower and, as of today, unproven: neutral, non-alarmist budget-status language (no red "OVER BUDGET" banners, no pass/fail framing) combined with a frequent-expenses shelf for one-tap re-logging. No side-by-side testing against Koody has been done. If that combination doesn't meaningfully change how people feel about checking in, there's no defensible reason to pick this over Koody today.

### Q: You say "not guilt" but really you've just removed red banners and stern copy. Isn't that a pretty thin feature?

A: It's thin as a feature list, yes — there's no clever mechanic, just the deliberate absence of shame-inducing UI patterns most budgeting apps ship by default. The bet is that removing a real source of friction (the dread of opening the app) matters more than adding a new feature would. That's a design theory, not a proven win — see the testing question below.

### Q: This is a solo portfolio project — what happens to my data and the app once you're done demonstrating it?

A: Straight answer: this isn't a production commitment. It's built to demonstrate the BMAD workflow and a clean Next.js/Spring Boot/PostgreSQL implementation — hosting, backups, and long-term availability were never in scope. Treat it as a demo, not a place to keep real financial records long-term.

### Q: No login, no accounts — if I switch devices, do I lose everything?

A: No. This isn't per-browser local storage — it's a single-user backend, so your data lives in the PostgreSQL database behind the app, not in your browser. Switching devices just means opening the same app URL; nothing is lost. (There's still only one "user" per deployed instance — this isn't a multi-account product.)

### Q: My spending doesn't fit neatly into the five default categories. How much friction is there to fix that?

A: None beyond opening settings — categories can be added, edited, or deleted at any time, not just during onboarding. The five defaults exist so day one requires zero setup, not because anyone is locked into them.

### Q: How long does the "five second" claim survive once I have a long category list or a crowded frequent-expenses shelf?

A: Honestly, this isn't fully decided yet. Shelf ordering and whether there's a cap on how many items it shows aren't specified in the current scope. It's worth resolving before it ships, but it's a near-term implementation decision, not a blocker to the concept itself.

### Q: Can I see spending trends across months, or just this month's snapshot?

A: Just this month for now. Multi-month history and charts are deferred — deliberately, since they reuse this version's aggregation queries at low added cost once built. It's a sequencing choice, not a missing capability.

### Q: If I forget to log Tuesday's coffee until Thursday, does it mess up an already-"closed" period?

A: No — and this one's a genuine strength, not a workaround. A transaction's period is set by its transaction date, not the date it was entered, and totals are always calculated fresh from logged transactions rather than cached. Logging Tuesday's coffee on Thursday correctly updates Tuesday's period; there's no "closed period" problem to work around.

### Q: If I go over budget, what actually happens — do I even find out, or are you just hiding the problem to avoid making me feel bad?

A: The status goes amber, then red — it's never hidden. The design bet is about *how* it's said, not *whether* it's said: plain language like "about $12 over this month" instead of a moralizing banner. It surfaces the problem without editorializing about it.

### Q: You're one developer with no usability testing done. How do you actually know any of this design works, versus it being your personal theory?

A: I don't, yet — and that's the single biggest open risk in this project, not a hidden one. The core loop (fast logging, accurate derived totals, category management) delivers value regardless of whether the emotional-design bet is right. But the specific claim — that neutral, non-judgmental framing changes how people come back after a lapse — is a hypothesis. The plan is to test it with 5–7 real users before building anything further on top of it, like the fuller lapse-recovery vision. That testing hasn't happened as of this FAQ.

---

## Internal FAQ

### Q: Is there a real chance you finish the backend/frontend plumbing, call the BMAD exercise "complete," and the actual differentiated part — the reason this is supposed to be worth building — never gets tested or shipped?

A: Yes, and it's the most likely failure mode, not a remote one — the usability test plan has already stalled three sessions running per the innovation strategy notes. The real risk isn't skipping the differentiated part on purpose; it's finishing the plumbing, running out of momentum, and Gate 1 (recruiting 5–7 testers) quietly not happening a fourth time either. The commitment below (see testing question) is the actual test of whether this gets a real answer or just another honest-sounding deferral.

### Q: The 5–7 user usability test has stalled three times already. What's genuinely different this time that makes it likely to actually run?

A: Committing to it as a real, blocking gate rather than an aspirational next step: Gate 1 (recruit testers, fallback to dogfooding if recruiting fails) and Gate 2 (only build Lapse Recovery if testers independently name lapse-guilt themselves, unprompted) from the innovation strategy are treated as hard blockers on any further lapse-recovery work — not soft intentions. If it stalls a fourth time, the honest move is to say so directly rather than let the PRFAQ keep implying it's imminent.

### Q: If this goes exactly as planned, what do you actually have at the end — a resume artifact, a tool you actually use, or something else?

A: A tool actually used personally, not just a portfolio artifact. That raises the bar past the Customer FAQ's "treat this as a demo" disclaimer: personal reliability and picking a real place to deploy it matter, even though a hosting/backup decision was explicitly left open by the technical research. That decision needs to get made before V1 is called "done" for real personal use — it just isn't a blocker for the PRFAQ or the initial working build.

### Q: The domain rule says totals must always be derived from transactions, never cached — what's the real risk of getting this wrong, and would anyone notice if it drifted?

A: The danger isn't a calculation bug — it's a service method somewhere accidentally writing a stored "total" or "remaining" column instead of computing it via query at read time. Once that happens, drift is silent; nothing throws an error, the numbers are just wrong. The mitigation is structural: never create a totals/remaining column in the schema at all. If the column doesn't exist, the shortcut becomes impossible to take by accident.

### Q: CORS, DTO boundaries, and exception handling were flagged as the real day-one risk, not the stack itself. What's actually likely to break first, and how much time should be budgeted for it?

A: Expect real time lost if this is treated as an afterthought. CORS configuration and the DTO layer plus a centralized `@ControllerAdvice` exception handler should be written into the initial Spring Boot skeleton before the first controller method exists — not added reactively after the first blocked frontend call or leaked stack trace.

### Q: Solo build, no team — what's a realistic timeline to a working, demoable V1?

A: Roughly 2–4 weeks of part-time work for the defined V1 scope (quick-add, category CRUD, budget status, frequent-expenses shelf) — assuming no scope creep into charts, rollover, or lapse-recovery. This is a rough estimate, not a commitment; actual pace depends on hours available per week, which isn't fixed here.

### Q: What gets cut first if that timeline slips — especially given the temptation to build the more interesting lapse-recovery feature instead of finishing the core loop?

A: Cut order: charts/insights first, then rollover behavior, then any category custom-edit polish beyond basic CRUD. The core loop — quick-add, category totals, budget status, frequent-expenses shelf — is never cut; lapse-recovery was never in scope to begin with, so it isn't part of this list at all.

### Q: Worst case: the "neutral, non-judgmental" design bet turns out not to matter to real users at all. What actually happens then?

A: The product still works as a plain, competent expense tracker with no real differentiation from any manual-entry competitor. For a portfolio project, that's a soft failure, not a catastrophic one — the stated success criteria are about execution and a coherent decision narrative, not adoption. It stops being a differentiated product and becomes "a clean CRUD app" — still a legitimate outcome, just not the interesting one.

---

## The Verdict

**Concept Strength:** This concept is credible, disciplined, and honest — but its central differentiator is still unproven, and the pattern behind that gap is the real story here.

**Forged in steel:**
- The domain architecture is genuinely sound and survived adversarial questioning cleanly — derived-totals-never-cached and transaction-date-determines-period aren't just correct decisions, they're correct in ways that actively answered hard customer questions (the "does backfilling break things" question turned into a demonstrated strength, not a dodge).
- The discipline to not overclaim carried through every stage. The press release doesn't promise a forgiveness feature that doesn't exist; the FAQs say "I don't know yet" where that's the true answer instead of papering over it. That restraint is what makes the rest of the document trustworthy — the parts that do claim confidence are believable because the parts that don't are clearly marked.
- Scope discipline held under pressure: charts, rollover, and lapse-recovery all got correctly named and deferred rather than creeping back in when the Internal FAQ asked what gets cut.

**Needs more heat:**
- The Koody answer ("we haven't tested whether our combination beats theirs") is honest but not yet strong — the concept's competitive case currently rests on an assertion, not evidence. Acceptable for a portfolio project, but the differentiation story is disclosed, not finished.
- The frequent-expenses shelf's ordering/cap behavior is a real, small, unresolved design decision that should get made during UX/architecture work, not carried forward indefinitely.
- The 2–4 week timeline is a rough guess, not a plan — worth revisiting once real implementation hours are known.

**Cracks in the foundation:**
- The testing gate has now failed to execute across three separate sessions — brainstorming, innovation strategy, and this PRFAQ all land on the same conclusion: test the guilt/shame hypothesis with real users before building further. It has never once happened. The entire differentiated value proposition sits on top of this unexecuted step. What it would take to address it: treat Gate 1 as blocking in the literal sense — no work on anything beyond core V1 scope starts until either testing happens or a deliberate, named decision is made to proceed without it.
- A real tension surfaced late and isn't reconciled: the Customer FAQ tells a hypothetical user to treat this as a demo with no longevity promised, but the Internal FAQ revealed the actual goal is a tool the creator keeps using personally — which makes the still-undecided hosting/backup question load-bearing in a way it wasn't when this started as a pure portfolio exercise. What it would take to address it: make an explicit, even if minimal, hosting/persistence decision before calling V1 "done" for personal use — not before the PRD, but before depending on it.

This concept survives the gauntlet. It's ready to hand to a PM for PRD work, with the explicit understanding that "test with real users" and "decide on hosting" are carried-forward action items, not resolved ones.

<!-- coaching-notes-stage-1 -->
## Coaching Notes — Stage 1: Ignition

**Concept type:** Solo portfolio/practice project (confirmed by user), not a funded commercial launch. Internal FAQ (Stage 4) should be framed around technical/scope trade-offs, personal execution risk, and defensibility of the product-thinking narrative to a future reviewer/employer — not funding asks, unit economics, or GTM.

**Source of the concept:** Not originated in this session — pulled from an already-complete product brief (`brief-bmad-expense-tracker-2026-07-02`), itself the output of a prior brainstorming → market/domain/technical research → design-thinking → innovation-strategy chain. This PRFAQ is a pressure-test of an already-formed concept, not concept generation from scratch.

**Essentials on entry (from the brief, not re-derived):**
- Customer: "The Anxious Starter," 18–24, first time managing own money, already bounced off YNAB/PocketGuard/Monarch.
- Problem: apps don't fail on day one, they fail around day seven — a missed logging day snowballs into a shame spiral (identity judgment, not just guilt/behavior note) and people quietly stop opening the app.
- Stakes: shame produces stronger avoidance than guilt; red "OVER BUDGET" banners actively make retention worse.
- Solution direction: manual-only quick-add (<5 sec), default + custom categories, frequent-expenses shelf, neutral traffic-light budget status, persistent add button. Explicitly excludes bank sync, receipt capture, push notifications.

**What subagent research added beyond the brief:**
- Design-thinking artifact detail: empathy map quotes ("I'll use it if it takes 5 seconds" / "I don't want another finance app that wants my bank login"), a POV statement, and a fully drafted but **never-executed** 5–7 tester usability test plan. The brief already flagged testing as unexecuted; this confirms it's stalled across three separate sessions per the innovation strategy doc.
- Innovation strategy decision gates: Gate 1 = go/no-go on recruiting real testers (fallback = dogfooding); Gate 2 = only build Lapse Recovery if testers independently name lapse-guilt themselves, unprompted; Gate 3 = any Phase 3 feature needs cited Phase 2 evidence. These gates are a real constraint on how ambitiously the press release and internal FAQ can honestly describe the lapse-recovery vision.
- Domain research: the "totals/budget-remaining always derived from transactions, never independently edited" rule was called out as the single rule most worth enforcing even at MVP scope — reinforces that this is a designed-in invariant, not an implementation afterthought.
- Rejected ideas worth knowing so they aren't re-proposed: home-screen widget shortcut, user-selectable budget-tone toggle ("chill mode"/"strict mode"), voice-to-log parsing — all explicitly deferred past V1 during design-thinking ideation.

**Surprising finding from live web research — surfaced to user, not yet reacted to:** the market picture in the brief is from 2026-07-02 but two new/repositioned competitors have emerged since:
- **Koody** — free, manual-only, no-bank-linking, few-seconds entry. This is a near-exact positioning match to "manual-first, no bank sync," closer than Finny was at brief-writing time. It also does receipt-attach, which this project explicitly excludes.
- **Finny** appears to have pivoted toward AI-parsed natural-language/voice entry rather than pure manual quick-add, which changes what "fast" means competitively (parsed input vs. tap-based quick-add).
- **PocketGuard's new "Pace" feature** (spend-rate alerts) shows an automation-first incumbent already iterating toward status/pace indicators — narrows, but does not close, the neutral-tone-status differentiation window.
- Independent (non-vendor) confirmation of a "day-7 guilt spiral" as a *named, studied* phenomenon was **not found** — general mobile retention curves (Day-7 ~15%, Day-30 ~7-10%) and Gen-Z financial-anxiety sentiment support the shape of the claim, but the specific causal mechanism (shame → avoidance → quiet abandonment) remains this project's synthesized hypothesis, not a cited external finding. This matters for how confidently the press release and FAQs can assert the mechanism versus present it as this product's operating theory.

**User decisions on the surfaced findings (2026-07-02):**
- Positioning: lead the press release with the guilt/shame-aware, non-judgmental lapse-handling framing — NOT manual-only (Koody now occupies that ground too squarely to lead with it). Manual-only is table stakes/supporting detail; the neutral emotional-design angle is the headline claim. Koody comparison, if needed, belongs in the FAQ, not the headline.
- Claim honesty: the day-7 guilt-spiral mechanism is this product's operating theory, not an established external fact. Press release should describe the problem in observed, defensible terms (missed days → quiet abandonment is well-supported by general retention data) without asserting the shame-causation mechanism as proven. The Internal FAQ (Stage 4) owns the nuance of "how do we know this is really why people quit."

<!-- coaching-notes-stage-2 -->
## Coaching Notes — Stage 2: The Press Release

**Rejected headline framings:** "The Budget App With No Guilt Trip" (original draft) and Option B ("An Expense Tracker That Never Makes You Feel Behind") — both risked implying an active forgiveness/lapse-recovery *feature* that V1 doesn't build. User explicitly chose to keep the language grounded in what V1 ships (speed + absence of judgmental UI) rather than the vision-level forgiveness mechanic. Final: "A Budget Tracker Built for Speed, Not Guilt."

**Recurring discipline through this stage:** every section was checked against "does this describe a shipped V1 behavior, or does it imply the deferred Lapse Recovery/streak-forgiveness feature?" Specific saves: the leader quote explicitly splits "this version" (shipped, honest, no-judgment UI) from "where I want to take it next" (vision); the How It Works section states "no reminder, no backlog view" as a true V1 exclusion, not a forgiveness feature; the user quote is labeled "Early user" rather than "verified customer," because no usability testing has actually been run (the Stage 1 finding that the 5–7 tester test plan is still unexecuted after three sessions).

**Dateline decision:** no real company/HQ exists for a solo project, so the traditional "CITY —" convention was dropped; "Fall 2026" used as a plausible near-term placeholder date rather than a committed ship date.

**Differentiator handling deferred to FAQ, not headline:** per Stage 1 decision, the Koody/Finny/PocketGuard competitive comparison was deliberately kept OUT of the press release (headline and body lead with the guilt/shame-aware framing only) and pushed to Stage 3 (Customer FAQ), where "why isn't this just Koody?" is expected to be one of the hardest questions.

**Not yet stress-tested:** the press release asserts the emotional/felt-experience framing confidently ("felt less like checking a number and more like facing a verdict") without hedging language in the body copy itself — the honesty caveat lives in Stage 1 notes and is expected to surface explicitly in the Internal FAQ's "how do we know this is real" question, not in the press release prose. This is a deliberate stylistic choice (press releases don't hedge) but worth flagging if the internal FAQ doesn't end up addressing it directly.

<!-- coaching-notes-stage-3 -->
## Coaching Notes — Stage 3: Customer FAQ

**Gaps revealed and their trade-off classification (all user-confirmed):**
- **Koody differentiation (accepted trade-off, named directly):** the concept no longer has a clean "why not Koody" answer beyond an unproven design bet (neutral status language + frequent-expenses shelf). User chose to state this honestly in the FAQ rather than pause to find a sharper wedge — appropriate given the portfolio-project concept type, but this is the single most exposed claim in the whole document and will resurface as the hardest question in Stage 4/Stage 5.
- **Data/app longevity (accepted trade-off, disclosed):** no hosting/backup/longevity commitment exists or is planned; the FAQ answer explicitly tells users to treat it as a demo, not a place for real financial records. This is a legitimate answer for a portfolio piece but would be a launch blocker if concept type were ever "real commercial launch" instead.
- **Frequent-expenses shelf ordering/cap (fast-follow, deliberately left open):** no ordering or size-cap mechanism is specified in current scope. Noted as a near-term implementation decision, not resolved here — worth resolving before or during architecture/UX work, not before this PRFAQ ships.

**Confirmed as genuine strengths (not gaps) during this stage:** the transaction-date-vs-entry-date period assignment (backfilling doesn't break derived totals) and the always-derived-never-cached totals rule both held up cleanly under adversarial questioning — these are architecture decisions already made correctly, not open risks.

**Competitive intelligence reused from Stage 1 web research:** the Koody, Finny-pivoted-to-AI-parsing, and PocketGuard-Pace findings all surfaced directly as FAQ answer content rather than staying background context — confirms they were worth the research spend.

**The hardest question, by design, has no comfortable answer:** "how do you know this design works with zero usability testing" was answered with full honesty (no testing has happened; the emotional-design bet is a hypothesis) rather than softened. This directly foreshadows Stage 4's internal FAQ, where the same gap needs to be addressed from the feasibility/risk side rather than the customer-trust side.

<!-- coaching-notes-stage-4 -->
## Coaching Notes — Stage 4: Internal FAQ

**Feasibility risks identified (both structural, both answerable from existing research):** (1) derived-totals invariant breaking silently if a totals/remaining column is ever added to the schema — mitigated structurally by never creating that column, not by process discipline; (2) CORS/DTO/exception-handling scaffolding needs to be written into the initial Spring Boot skeleton, not bolted on reactively.

**Timeline estimate:** ~2–4 weeks part-time for defined V1 scope — offered as a rough estimate, not a commitment, since actual weekly hours available weren't specified. Flagged as worth revisiting once real implementation starts.

**Cut order if timeline slips (user-confirmed):** charts/insights → rollover behavior → category custom-edit polish beyond basic CRUD. Core loop (quick-add, category totals, budget status, frequent-expenses shelf) is never cut. Lapse-recovery isn't on this list because it was never in V1 scope to begin with.

**Real end-goal clarified — and it changes the risk profile:** user's answer to "what do you actually want at the end" was **a tool you actually use**, not just a portfolio artifact. This is a genuine escalation from the Stage 3 Customer FAQ answer to "what happens to my data," which told a hypothetical general user to treat the app as a demo with no hosting/longevity commitment. That customer-facing answer is still honest (no *product* commitment exists for arbitrary users), but it now sits next to a real personal stake: the hosting/backup decision the technical research left open needs to actually get made before V1 is "done" for the creator's own use, even though it isn't a blocker for the PRFAQ itself or for a first working build. Worth resurfacing when architecture/deployment decisions get made.

**Testing commitment — the load-bearing decision of this stage:** user chose to treat Gate 1 (recruit 5–7 testers, fallback to dogfooding) and Gate 2 (only build Lapse Recovery if testers independently name lapse-guilt themselves) as real, blocking gates rather than reframing the PRFAQ to stop implying testing is imminent. This is the third time this commitment has been made across the BMAD chain (brainstorming → innovation strategy → this PRFAQ) without yet being executed — worth treating as a trust-but-verify item in any future session: check whether testing actually happened before taking "testing is planned" at face value again.

**The founder's avoided question got a direct, unhedged answer:** yes, there's a real chance the differentiated part never gets built — named as the most likely failure mode, not a remote one, tied explicitly to the same stalled-testing pattern above rather than treated as a separate abstract risk.
