# Innovation Strategy: bmad-expense-tracker (MVP feature set)

**Date:** 2026-07-02
**Strategist:** Raha
**Strategic Focus:** Practice run of the BMAD innovation-strategy workflow, scoped to the manual-first, low-friction expense-tracker MVP concept validated in the 2026-07-02 design-thinking session. Goal is to stress-test the MVP with market/business-model/disruption lenses and surface a sharper scope plus a few bold V2/V3 directions — not to build a commercial strategy for a real business.

---

## 🎯 Strategic Context

### Current Situation

bmad-expense-tracker is a free, solo-built personal project with no users, no revenue, and no company behind it — a learning vehicle for practicing BMAD, Design Thinking, and AI-assisted development. It carries forward validated decisions from two prior sessions:

- **2026-07-01 Brainstorm:** produced the MVP feature list and three design principles — manual-but-fast over automatic-but-complex, pull-based engagement over push-based, and a reusable summary-card component with a color-state prop.
- **2026-07-02 Design Thinking:** validated three concepts against the persona (students/young professionals): one-tap quick-add with smart defaults, neutral/non-judgmental budget-status framing (traffic-light + reframed language), and a frequent-expenses quick-log shelf. Explicit non-goals: bank sync, receipt scanning, push notifications.

MVP scope: log an expense (amount, category, date only), view spending by category, track progress against a monthly budget.

### Strategic Challenge

There is no real market position to defend or disrupt here — this is a personal project, not a company. So "disruption opportunity" and "competitive advantage" have to be reframed for a solo, no-revenue context: instead of asking "how do we win the market," this session asks "does putting this MVP concept under real strategic pressure (market analysis, business-model deconstruction, disruption lenses) surface anything sharper than what brainstorming and design thinking already found — either a tighter MVP scope, or a genuinely differentiated V2/V3 direction worth building toward?" Success is measured in the quality of ideas surfaced and in your own fluency with the frameworks, not in a go-to-market plan.

---

## 📊 MARKET ANALYSIS

### Market Landscape

**Frameworks applied:** Competitive Positioning Map, Market Timing Assessment.

The student/young-professional budgeting-app category is not empty space — it's a crowded, actively-iterating market. Real 2026 landscape (via web research):

| App | Model | Manual-first? | Bank sync? | Offline? | Price |
|---|---|---|---|---|---|
| **Finny** | Freemium | Yes | No | **Yes** | Free tier generous, indefinite |
| YNAB | Subscription | No (syncs) | Yes | No | $14.99/mo (free for students 1yr) |
| PocketGuard | Freemium | No | Yes (free tier) | No | Free / premium |
| Goodbudget | Freemium (envelopes) | Partial | No | Unclear | Free (20 envelopes) / $10 mo |
| Monarch Money | Subscription | No | Yes | No | $3.49-6.99/mo |
| WalletHub | Free | Yes | No | Unclear | Free (ad/cross-sell supported) |

**Critical fact:** Finny already claims the exact positioning this project's brainstorm treated as a differentiator — manual entry, no bank credentials, offline-capable, free indefinitely, built for the student persona. That is not a hypothetical competitor; it's a direct one.

### Competitive Dynamics

- **Two camps, not one continuum:** "sync-and-automate" apps (YNAB, PocketGuard, Monarch — win on completeness, lose on trust/friction/cost) vs. "manual-and-simple" apps (Finny, Goodbudget, WalletHub — win on trust/simplicity, compete on how *little* friction they can achieve).
- bmad-expense-tracker's brainstormed principles (manual-but-fast, pull-based, no bank sync) place it squarely in the second camp — competing directly with Finny and Goodbudget, not against YNAB/Monarch.
- **Substitutes worth naming:** a plain notes app, a spreadsheet, or literally nothing (mental tracking) are the real baseline this category competes against — for the target persona, "do nothing" wins more often than any app, because the churn research below shows setup friction and guilt are bigger killers than feature gaps.
- **Barrier to entry is low** (this whole category is UI/UX + habit design, not proprietary tech), which means "we built a manual expense tracker" alone is not a moat — anyone can and does build one.

### Market Opportunities

Despite Finny occupying similar territory, real research on 2026 churn patterns points to *unsolved* problems even the "manual-first" camp hasn't cracked:
- **Setup-phase abandonment** — most churn happens before first meaningful use, from being asked to categorize/configure/connect too much upfront. None of the competitors above are reported as solving zero-setup onboarding.
- **Post-lapse recovery** — the dominant failure mode isn't "never started," it's "missed a few days, backlog feels overwhelming, quit." No competitor in this list markets a specific answer to *recovering* a broken streak without guilt.
- **Guilt-driven interface design** — repeatedly cited as a churn driver industry-wide; the design-thinking session's "neutral/traffic-light framing" concept directly targets this, and it's not something Finny/YNAB/PocketGuard advertise as a design pillar.
- **Consistency over sophistication** is the single most consistent signal across the research: a basic tool used daily beats a sophisticated one abandoned after a week — this validates the MVP's minimalism as strategically correct, not just a scope constraint.

### Critical Insights

- **Brutal truth #1:** "Manual-first, no bank sync" is not a differentiator anymore — Finny already owns that positioning with a generous free tier and offline support. If this project's strategic story is "we're different because we don't ask for your bank login," that story is already told, better-resourced, elsewhere.
- **Brutal truth #2:** The actual open ground isn't in *what data you collect* (manual vs. synced) — it's in *emotional design around lapses and guilt*, and in *reducing setup-to-first-log time to near zero*. That's where this project's design-thinking concepts (neutral framing, frequent-expenses shelf, quick-add) have to do real differentiation work, not the "no bank sync" premise.
- **Brutal truth #3:** Since this is a free personal project with zero distribution, "competing" with funded apps for users is not the realistic goal — the honest opportunity is to treat this analysis as sharpening *which* features matter most (recovery-from-lapse, sub-3-second logging, non-judgmental framing) rather than as a go-to-market thesis.

**Sources:** [Finny — Best Budget Apps for Students 2026](https://getfinny.app/blog/best-budget-apps-for-students-2026) · [WalletHub — Best Budgeting Apps for College Students](https://wallethub.com/answers/b/best-budgeting-apps-for-college-students-2140879258/) · [NerdWallet — Best Budget Apps 2026](https://www.nerdwallet.com/finance/learn/best-budget-apps) · [Onething Design — Budget App Retention](https://www.onething.design/post/budget-app-design) · [Vocal — Why Digital Budgets Fail](https://vocal.media/education/why-digital-budgets-fail-understanding-the-struggle-with-budgeting-apps)

---

## 💼 BUSINESS MODEL ANALYSIS

Before deconstructing anything: with zero revenue and zero users, the standard "business model" question ("how do you create, deliver, and capture value") only works if value-capture is redefined. For this project it's captured twice, for two different beneficiaries, and conflating them is exactly the kind of comfortable illusion this step exists to strip away.

**Frameworks applied:** Value Proposition Canvas (primary), Business Model Canvas (lightweight, adapted for a non-commercial project).

### Current Business Model

Two overlapping "customers" exist, and the model behaves differently for each:

1. **Raha (real, actual user of this exercise):** value created = BMAD workflow fluency, AI-assisted dev practice, a portfolio-worthy shipped project. Value captured = learning outcomes and a demonstrable artifact, not money.
2. **Students/young professionals (hypothetical persona, no real instance yet):** value created = fast, non-judgmental expense awareness. Value delivered = a working app, if and when it ships and reaches them — which it has not; there is no distribution channel, install base, or even a beta tester group today. Value captured for them = free (no revenue model), value captured *back* = none (no telemetry, no monetization, no feedback loop currently planned).

The honest current state: this is a single-sided model (build → learn) wearing the shape of a two-sided one (build → serve users → capture value). Only side 1 is real right now.

### Value Proposition Assessment

For the hypothetical persona, using Value Proposition Canvas:
- **Jobs:** understand "am I OK this month" fast; log a purchase without friction; avoid feeling watched or judged.
- **Pains:** setup friction, bank-credential distrust, guilt-inducing red-banner UX, backlog anxiety after missed days (all confirmed by 2026 churn research in Step 2, not just assumption).
- **Gains sought:** speed (sub-5-second log), calm/neutral feedback, a forgiving path back in after lapsing.
- **Fit today:** the MVP's quick-add, frequent-expenses shelf, and neutral framing address jobs/pains reasonably well *on paper* — but per Step 2, this pain/gain profile is not unique to this project; Finny and Goodbudget are built on overlapping theses. The value proposition is credible, not novel.

### Revenue and Cost Structure

No revenue model exists or is planned — confirmed as a deliberate choice (free personal project), not a gap to fill. Cost structure is entirely Raha's own time; there is no infrastructure, hosting, or acquisition cost being tracked. Because there's no monetization ambition, Revenue Model Innovation and Cost Structure Innovation frameworks aren't applied here — forcing them onto a project with no revenue objective would manufacture a fake problem.

### Business Model Weaknesses

- **No real user has touched this yet.** The 2026-07-02 design-thinking doc explicitly flags the Test phase as not yet run ("User Feedback: to be filled in"). Every value-proposition claim above is still an untested hypothesis, not a validated fit.
- **The differentiation story leans on a premise (Step 2) that's already occupied by Finny.** If "breakthrough success" is sharper V2/V3 ideas, the model as currently articulated doesn't yet explain *why a user would pick this over Finny* beyond "Raha built it."
- **Single-sided value capture means no feedback loop.** Even in a free personal project, without any users or telemetry, there's no mechanism to learn whether the neutral-framing and frequent-shelf bets actually work — the model has no way to self-correct once built.
- **Assumption at risk:** that finishing the MVP build is itself the finish line. If the real goal (per your own success criteria) is sharper ideas and workflow fluency, "ship an MVP no one uses" partially satisfies goal 1 (learning) but not goal 2 (validated differentiation) or the original design-thinking goal (validate with real users).

---

## ⚡ DISRUPTION OPPORTUNITIES

Incremental innovation makes an existing thing better for the people already buying it — a faster keypad, a nicer chart. Disruption means going after people the category currently fails entirely, or reframing what "good enough" means so thoroughly that the incumbents' strengths stop mattering. Step 2 showed the "manual-first, no bank sync" angle is already contested ground — so the question here isn't "how do we out-Finny Finny," it's "who is everyone in this category still failing, and on what dimension is 'good enough' actually undefined?"

**Frameworks applied:** Jobs to be Done, Blue Ocean Strategy, Disruptive Innovation Theory.

### Disruption Vectors (Blue Ocean grid)

- **Eliminate:** bank-account connection entirely (already table stakes for this project, but worth stating as a deliberate elimination, not an omission); restrictive "budget as ceiling" framing; red/warning-toned language.
- **Reduce:** number of fields and decisions required at the moment of logging (already a design-thinking finding); category taxonomy depth — most competitors offer 15-20+ categories, which is itself a friction source.
- **Raise:** speed and warmth of *re-entry after a lapse* — every competitor in Step 2's table treats a missed day as silent failure, not as a moment the product should actively design for.
- **Create:** a first-class "lapse recovery" flow (not just onboarding) — explicitly designed for "I stopped for 4 days, help me start again without guilt or backlog-entry," which no competitor markets as a feature.

### Unmet Customer Jobs

Per the 2026 churn research in Step 2, the single most-cited failure mode isn't "never started" — it's "missed a few days, backlog feels overwhelming, quit." That is a massively underserved job: **"help me resume tracking without punishing myself for the gap."** No competitor (Finny, YNAB, PocketGuard, Goodbudget, Monarch, WalletHub) markets a specific mechanic for this. The MVP's current scope (quick-add, frequent shelf, neutral status) helps the *logging* moment but has nothing yet for the *lapse-and-return* moment — that's a gap in the current design-thinking output, not just the market.

**Non-consumers worth naming:** people who tried and abandoned Finny/Goodbudget/YNAB specifically because of a broken-streak spiral (not because manual entry was too much work) — they already believe in manual, low-friction tracking; they just got shamed out of it by their own gap. They're a more specific, more reachable non-consumer segment than "students in general."

### Technology Enablers

- **Freeform quick-capture via lightweight parsing** ("coffee 5" → amount + category, no dropdown) — a genuinely underused enabler in this specific category; none of the six competitors reviewed lead with this. It captures the *feel* of automatic categorization without any bank-sync trust cost, closing the gap between "manual and trustworthy" and "automatic and fast."
- **Local-first/offline storage** — Finny already does this; not a differentiator on its own, but a baseline worth keeping given the persona's stated aversion to always-online, bank-linked tools.
- **No AI/ML infrastructure is required to test the "lapse recovery" concept** — it's a UX/copy/flow problem, not a technology problem, which makes it cheap to prototype and test even at solo-project scale.

### Strategic White Space

The real white space isn't "simpler than YNAB" (Finny already sits there) — it's **the emotional design of the gap between sessions**: what the product does and says when a user comes back after 3, 7, or 14 days of silence. That's genuinely uncontested territory in the reviewed landscape, it's cheap to build (no new tech), and it directly answers the single most-cited churn cause in the research. If this project has one idea worth carrying past the MVP, this is it.

---

## 🚀 INNOVATION OPPORTUNITIES

Multiple paths get explored here before any get committed to, because the first idea that "feels right" is rarely the strongest one — it's usually just the first one considered.

**Frameworks applied:** Three Horizons Framework (portfolio balance across MVP/near-term/speculative), Lean Startup Methodology (cheap validation of the riskiest bets), Value Chain Analysis (what to own vs. deliberately not build).

### Innovation Initiatives

| # | Initiative | Horizon | Addresses |
|---|---|---|---|
| 1 | **Lapse Recovery flow** — dedicated re-entry screen after N idle days: friendly one-tap resume, no backlog-shaming | H1 (MVP-adjacent) | Step 4's #1 white-space finding |
| 2 | **Streak-with-forgiveness** — gamified streak (already ideated) with explicit "grace day" mechanic so a miss doesn't reset to zero | H1 | Guilt-driven churn (Step 2/4) |
| 3 | **Freeform quick-capture parsing** — single text field ("coffee 5") auto-parses amount + category | H2 | Sub-3-second logging, no dropdown |
| 4 | **Rough-tracking mode** — optional low-fidelity weekly-ballpark mode for users who want awareness, not per-expense precision | H2 | Non-consumer segment (Step 4) |
| 5 | **Build-in-public feedback loop** — share progress with a small real audience (a few actual testers, a dev-log post) instead of building in isolation | H1 | Step 3's "no feedback loop" weakness |
| 6 | **Open-source / portfolio packaging** — explicit decision to publish this as a public repo/case study | H1 | Reframes value capture honestly (see below) |
| 7 | **Value-chain minimalism** — explicit non-build list: no bank integration, no cloud backend, no hosted accounts | H1 (ongoing constraint) | Keeps trust positioning and zero cost |
| 8 | *(speculative)* **Habit-ecosystem export hooks** — treat streak data as exportable to habit-tracking platforms, reframing the product as a habit tool rather than a finance tool | H3 | Long-range repositioning, not a near-term build |

### Business Model Innovation

Since there's no revenue objective, the one real "business model" lever available is **how value capture is framed** (Step 3's core weakness). Initiative 6 makes that explicit: treat "public repo + written case study of the BMAD process" as the actual value-capture mechanism, alongside the learning itself. This costs nothing, and it's the only initiative on this list that directly repairs the single-sided-model gap identified in Step 3 — it converts "build → learn" into "build → learn → show → get real feedback," which is as close to a feedback loop as a zero-revenue project can get.

### Value Chain Opportunities

Initiative 7 is as much a strategic choice as it is a constraint: deliberately **not** building bank sync, cloud backend, or account infrastructure isn't a limitation to apologize for — Step 2 showed those are exactly the trust-eroding, complexity-adding activities that drive churn in incumbent apps. Owning only the local, manual-entry experience end-to-end (no partial integrations) keeps the value chain short, cheap, and aligned with the persona's actual preference.

### Partnership and Ecosystem Plays

No real partnerships exist or are needed at this stage — the only near-term "ecosystem" move worth taking seriously is Initiative 5 (a handful of real testers, which the design-thinking Test phase already called for and which remains undone). Initiative 8 (habit-app export hooks) is a speculative, later-horizon idea worth naming but not worth planning around yet — it presumes a working, validated MVP that doesn't exist.

---

## 🎲 STRATEGIC OPTIONS

### Option A: Ship-and-Validate (discipline over scope)

Finish the MVP exactly as already scoped by the 2026-07-01 brainstorm and 2026-07-02 design-thinking sessions — quick-add, neutral status, frequent-expenses shelf — and immediately run the Test phase that's already written but not yet executed (5-7 real users, the tasks and questions already drafted). No new initiatives from this session get added to scope yet.

**Pros:** Lowest risk; respects already-completed design work instead of re-litigating it; gets to a *real, tested* artifact fastest; directly satisfies the design-thinking doc's own "Next cycle" recommendation; avoids scope creep before any validation exists.

**Cons:** Deliberately shelves the two sharpest findings from this session (the lapse-recovery white space, the no-feedback-loop weakness); risks testing something that, per Step 2, isn't meaningfully differentiated from Finny/Goodbudget even if it works well.

### Option B: Differentiation-First (bet on the sharpest insight)

Pull Initiatives 1 and 2 (Lapse Recovery flow, streak-with-forgiveness) into MVP scope *before* testing, on the logic that Step 4 identified this as genuinely uncontested territory and it's cheap to build. Test the differentiated version, not the baseline version.

**Pros:** Acts directly on this session's strongest evidence-backed insight rather than filing it away; makes the eventual user testing far more informative, since it tests a real differentiation hypothesis instead of just "does the basic flow work"; still cheap (no new tech required per Step 4).

**Cons:** Adds scope before any user validation exists at all — exactly the risk Step 3 flagged (an untested model getting bigger, not smaller); risk of building a "clever" feature that solves a researched-industry-wide problem but not necessarily *this* project's actual users' problem, since none have been talked to yet.

### Option C: Build-in-Public (fix the structural gap, not the feature set)

Treat Initiatives 5 and 6 as the priority: recruit a handful of real testers now (even before Option A's MVP is fully polished) and publish the process as a case study/open-source artifact. Value capture gets explicitly reframed around learning + portfolio + real feedback, not "finished app."

**Pros:** Directly repairs Step 3's core structural weakness (single-sided model, no feedback loop) rather than adding more untested features on top of it; cheapest option of the three — no code changes required, only process; best matches your own stated success criteria (workflow fluency + portfolio, not a commercial outcome).

**Cons:** Requires comfort with exposing unfinished/imperfect work; doesn't resolve the product-differentiation question on its own; depends on actually finding willing testers/an audience, which is its own execution risk and easy to under-deliver on.

---

## 🏆 RECOMMENDED STRATEGY

### Strategic Direction

**Recommendation: A + C combined, with B demoted from "feature to build" to "hypothesis to test."**

Finish the MVP exactly as already scoped (Option A's discipline), but this time actually run the Test phase that's been sitting unexecuted since the 2026-07-02 design-thinking session — and use that same round of real user contact to fix Step 3's structural weakness (Option C: a real feedback loop, however small). Do **not** build the Lapse Recovery flow or streak-forgiveness mechanic yet. Instead, add 1-2 interview questions and a paper-prototype sketch of the lapse-recovery concept into the existing Test-phase script, and see whether real testers actually name "missed days → guilt → quit" as their own experience, or whether that's true of the aggregate market but not of *these specific* 5-7 people.

This is the bold call, not the safe one: it means deliberately *not* shipping this session's best idea yet, because shipping an untested differentiator on top of an already-untested MVP would repeat the exact failure mode Step 3 diagnosed — assumptions stacked on assumptions, with no one ever having said them out loud to a real person. The discipline is the differentiator here.

**What makes this credible:** the Test phase script already exists (nothing new to write), the churn evidence behind lapse-recovery came from independent 2026 market research rather than internal assumption, and the cost of testing-before-building is close to zero.

**What's genuinely uncertain:** whether you can actually recruit 5-7 willing, candid testers from a solo learner's network — that's the weakest link in this whole plan, weaker than any of the product ideas. And there's a real risk this becomes a third session in a row that ends with "Next step: run the Test phase" and never does it.

### Key Hypotheses to Validate

1. **The lapse/guilt abandonment pattern is real for *your* testers**, not just an artifact of aggregate 2026 market research — must be confirmed via direct interview, not assumed.
2. **The MVP's core flows (quick-add, frequent-shelf, neutral status) actually achieve sub-5-second logging in practice**, not just in design intent — this was always the Test phase's job and still hasn't been checked.
3. **A lapse-recovery concept, shown as a paper sketch, generates genuine interest/relief from testers** — not just theoretical appeal to you as the designer.
4. **5-7 candid testers can actually be recruited** from your own network within the scope of a solo project — if this fails, the whole "build-in-public feedback loop" premise fails with it, and Option A alone (dogfooding) becomes the honest fallback.

### Critical Success Factors

- **Actually running the Test phase** — the single most important unresolved action item across three sessions now (brainstorm → design thinking → this one). Everything else in this document is speculative until this happens.
- **Treating Lapse Recovery as a probe, not a build** — resist the pull to start coding the "interesting" idea before it's been said out loud by a real tester.
- **A lightweight, low-effort feedback mechanism** — a shared doc, a short call, a quick form — not an elaborate "build-in-public" campaign that becomes its own procrastination.
- **Honesty about the fallback** — if recruiting real testers doesn't work, scale the ambition down to self-testing rather than quietly abandoning validation altogether.

---

## 📋 EXECUTION ROADMAP

### Phase 1: Immediate Impact

- **Finish the MVP** exactly as scoped by the brainstorm/design-thinking sessions (quick-add, neutral traffic-light status, frequent-expenses shelf) — no new features added yet.
- **Amend the existing Test-phase script** (already drafted in the 2026-07-02 doc) to add: a paper-sketch of the Lapse Recovery concept, and 1-2 interview questions probing whether missed-day guilt is something testers actually recognize in themselves.
- **Recruit 5-7 real testers** from your own network — the single riskiest, most important task in this whole roadmap.
- *Resources:* solo dev time only; no infrastructure or spend required.
- *Success metric:* MVP is functional enough to walk a tester through all three Test-phase tasks; testers are actually lined up.
- *Decision gate:* if 5-7 testers can't be found, fall back honestly to self-testing/dogfooding rather than skipping validation.

### Phase 2: Foundation Building

- **Run the usability sessions** using the existing task list (log a coffee purchase, check budget status, re-log yesterday's lunch) plus the new lapse-recovery probe.
- **Capture feedback live** with the Feedback Capture Grid already defined in the design-thinking doc (Liked / Questions / Ideas / Changes).
- **Synthesize findings** against this session's four key hypotheses — explicitly mark each as validated, invalidated, or inconclusive.
- **Stand up the lightweight feedback loop** (a shared doc, form, or short recurring check-in) so future changes have a real input channel instead of none.
- *Resources:* solo time for sessions + synthesis; no new tooling needed.
- *Success metric:* all four hypotheses have a clear verdict, not just impressions.
- *Decision gate:* only proceed to building Lapse Recovery in Phase 3 if testers independently surface lapse/guilt as a real pain point — don't build it on conviction alone.

### Phase 3: Scale & Optimization

- **If validated:** build the Lapse Recovery flow and streak-with-forgiveness mechanic for real, informed directly by what testers actually said (not the original speculative design).
- **If invalidated:** drop Lapse Recovery from the roadmap and refocus effort on tightening the validated core loop (speed, clarity) instead of chasing an unproven differentiator.
- **Either way:** decide deliberately on the open-source/portfolio packaging (Initiative 6) as the value-capture mechanism for this project, since that doesn't depend on which hypothesis wins.
- **Treat Rough-tracking mode, freeform-parsing capture, and habit-ecosystem export hooks as a backlog**, not commitments — revisit only if Phase 2 evidence points there.
- *Resources:* solo dev time, scoped by what Phase 2 evidence actually justifies.
- *Success metric:* the shipped feature set is traceable to real tester feedback, not just this session's speculation.
- *Decision gate:* before adding any Phase 3 feature, be able to point to the specific Phase 2 evidence that justified it.

---

## 📈 SUCCESS METRICS

### Leading Indicators

- Testers actually recruited and sessions actually completed (the process itself happening is the first real signal, given it's been deferred twice already).
- Sub-5-second logging observed directly during sessions, not just claimed by design intent.
- Testers spontaneously name missed-day guilt or backlog-abandonment *without being led* — distinguishes a real signal from a leading question artifact.
- Testers reach for the frequent-expenses shelf unprompted, rather than defaulting to manual entry every time.

### Lagging Indicators

Reframed for a zero-revenue project — these are outcome measures, not business metrics:
- Whether the finished project functions as a credible, evidence-backed portfolio artifact (decisions traceable to real feedback, not assumption).
- Whether you can articulate, with evidence, why each shipped feature exists — the practical test of BMAD workflow fluency.
- If Initiative 6 (open-source/public packaging) is pursued: any organic external engagement (comments, forks, real usage) — tracked honestly as a bonus signal, not a target to chase.

### Decision Gates

- **Gate 1 (end of Phase 1):** go/no-go on real user testing based on whether 5-7 testers were actually recruited; if not, explicitly fall back to dogfooding rather than skipping validation.
- **Gate 2 (end of Phase 2):** go/no-go on building Lapse Recovery — only proceed if testers independently surfaced the lapse/guilt pattern themselves.
- **Gate 3 (ongoing, Phase 3):** before adding any new feature, name the specific piece of Phase 2 evidence that justifies it — no evidence, no build.

---

## ⚠️ RISKS AND MITIGATION

### Key Risks

- **Tester recruitment fails.** A solo learner's network may not yield 5-7 candid participants — this is the single weakest link in the entire plan (flagged already in Step 7).
- **The Test phase stalls a third time.** It was called for after the 2026-07-01 brainstorm's implied next step and explicitly after the 2026-07-02 design-thinking session, and still hasn't run — momentum risk is real, not hypothetical.
- **Facilitator bias.** As both the designer and the interviewer, there's a real risk of unconsciously leading testers toward confirming the lapse/guilt hypothesis rather than letting them volunteer it.
- **Scope creep back into "just build it."** The appeal of the Lapse Recovery idea is real (Step 4/5); the discipline to wait for Gate 2 evidence before coding it is the hardest part of this whole recommendation to actually hold to.
- **Exposure risk from build-in-public.** Sharing unfinished, imperfect work invites a kind of vulnerability that can itself become a reason to quietly not follow through.

### Mitigation Strategies

- **Recruitment:** define a real minimum (even 3 honest testers beats 0) and commit to the dogfooding fallback in advance, so "couldn't find enough people" doesn't quietly become "so I skipped testing."
- **Stalling:** treat running the Test phase as the literal next task after finishing MVP polish — not a someday item, a hard gate before Phase 3 work is allowed to start.
- **Facilitator bias:** use neutral, open-ended interview language; never mention "guilt" or "lapse recovery" first — let testers describe their own experience before showing the paper-sketch probe.
- **Scope creep:** write down the Phase 2 evidence explicitly (in this same document) before writing a line of Lapse Recovery code — make Gate 2 a documented checkpoint, not a vibe.
- **Exposure risk:** start small — a private doc or conversation with 1-2 trusted people before any public/open-source commitment — so the feedback loop doesn't depend on public courage to exist at all.

---

_Generated using BMAD Creative Intelligence Suite - Innovation Strategy Workflow_
