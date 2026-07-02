# Design Thinking Session: bmad-expense-tracker

**Date:** 2026-07-02
**Facilitator:** Raha
**Design Challenge:** Help students and young professionals track daily expenses and stay within their monthly budget without the complexity of traditional finance apps.

---

## 🎯 Design Challenge

**Who:** Students and young professionals who want to manage personal expenses but are put off by complicated budgeting apps.

**What:** A simple, fast, minimal expense tracker that lets users log spending, see it by category, and track progress against a monthly budget — with none of the friction of full accounting software (no bank sync, no advanced finance features).

**Why now:** Existing budgeting tools over-serve this audience — they optimize for completeness (bank integrations, receipt scanning, detailed reports) at the cost of the two-second habit of "just log what I spent." The opportunity is a manual-first, low-friction tool people will actually keep using.

**Constraints:**
- MVP scope only — Should/Could items deferred to V2 (already captured in the 2026-07-01 brainstorm)
- No bank integration or advanced finance features
- Limited development time (learning project)
- Optimize for user experience over production-readiness

**Success looks like:**
- User-facing: users can quickly add an expense, view spending by category, track their monthly budget, and understand where their money is going
- Personal (Raha's): completing the project while learning BMAD, Design Thinking, and AI-assisted development

**Existing context carried forward:** The 2026-07-01 brainstorming session already produced an MVP feature list and three design principles — manual-but-fast over automatic-but-complex, pull-based engagement over push-based, and a reusable summary-card component with color-state. This design thinking pass will stress-test those decisions against real user needs rather than replace them.

---

## 👥 EMPATHIZE: Understanding Users

### User Insights

Synthesized from the 2026-07-01 brainstorm session's design decisions and known characteristics of the student / young-professional persona (no new interviews conducted this session — see Advanced Elicitation note below if deeper validation is wanted later).

- **They've tried "real" budgeting apps and bounced off them.** The explicit "Won't-Have" list from the brainstorm (bank sync, receipt photos, reminder notifications) reads as a reaction to feature bloat they've experienced elsewhere — connecting a bank account feels risky/invasive, photographing receipts is extra steps, and reminders feel naggy rather than helpful.
- **The habit is fragile.** Expense-tracking is a habit most people abandon within days, not because they don't care about money, but because the *cost of logging* exceeds the *perceived value* of the log at the moment of spending.
- **They want awareness, not accounting.** Success for this persona isn't a reconciled ledger — it's a felt sense of "am I OK this month?" Quick glanceable answers matter more than granular precision.
- **They are price-sensitive to friction, not to money-tracking itself.** They're willing to invest a little effort if it's fast and rewarding (duplicate-expense, smart defaults, streak — all "pull" mechanics from the brainstorm) but not if it feels like a chore (push notifications, manual receipt entry).

### Key Observations

- The MVP's minimal-required-fields decision (amount, category, date only) is already a direct response to abandonment risk — every optional field is one more reason to quit mid-entry.
- The "Won't-Have" list is as informative as the "Must-Have" list: it tells us this persona actively distrusts complexity and surveillance-feeling integrations (bank sync).
- The persistent floating "Add Expense" button suggests an assumption that *the moment of spending* is when logging should happen — not a nightly batch-entry ritual. This is worth testing: do people log in the moment, or do they prefer a quick end-of-day catch-up?
- There's an unresolved tension between "manual-first" (deliberate, keeps user in control) and "low friction" (every added step risks drop-off) — this tension is the crux the Define phase should resolve.

### Empathy Map Summary

**Says:** "I don't want another finance app that wants my bank login." / "I just want to know if I'm overspending this month." / "I'll use it if it takes 5 seconds."

**Thinks:** "Am I going to remember to log this later if I don't do it now?" / "Categories and charts are nice, but will I actually look at them?" / "Most budget apps feel like they're built for accountants, not for me."

**Does:** Opens the app right after paying for something (or forgets and abandons the entry entirely). Skims dashboard numbers rather than reading detailed tables. Re-logs similar purchases often (coffee, transit, lunch) — a strong signal for the "duplicate expense" V2 feature.

**Feels:** Mild guilt/anxiety around spending awareness, but resistant to anything that amplifies that guilt (e.g., aggressive red warnings, nagging reminders). Relief and a small sense of control when a budget check-in is quick and non-judgmental.

---

## 🎨 DEFINE: Frame the Problem

### Point of View Statement

Students and young professionals need a way to see where their money is going *without* feeling watched, judged, or burdened, because the moment logging an expense feels like a chore or a guilt-trip, they abandon the habit entirely.

### How Might We Questions

1. How might we make logging an expense feel closer to "5 seconds and done" than "opening a form"?
2. How might we show budget status in a way that feels informative rather than judgmental?
3. How might we reduce the number of decisions a user has to make at the moment of spending (category, amount, date)?
4. How might we turn repeat purchases (coffee, transit, lunch) into near-zero-effort re-logging?
5. How might we make checking "am I OK this month?" a glanceable, low-anxiety moment rather than a deep-dive into a table?
6. How might we build trust without requiring users to hand over bank credentials?

### Key Insights

- The core problem isn't "people don't know how to track expenses" — it's that every existing tool asks for more commitment (data, attention, emotional tolerance for guilt) than this persona is willing to give.
- Speed and non-judgment are the two design pressures the MVP must satisfy simultaneously; features that win on one but lose on the other (e.g., an aggressive red "OVER BUDGET" banner) risk killing retention even if functionally correct.
- The "duplicate expense" and "smart defaults" ideas from the brainstorm are directly answers to HMW #3 and #4 — this Define phase validates rather than contradicts that earlier thinking.

---

## 💡 IDEATE: Generate Solutions

### Selected Methods

**Brainstorming** across the six HMW questions, plus **Analogous Inspiration** (habit-trackers, games, messaging apps) for the friction/reward problem specifically.

### Generated Ideas

*Speed of logging (HMW #1, #3)*
1. One-tap quick-add: floating button opens a single-screen entry with amount as the only required initial tap; category/date default silently
2. Numeric-keypad-first entry (like a calculator) instead of a traditional form with multiple fields
3. Voice-to-log: say "twelve dollars lunch" and it parses amount + category (stretch/wild idea)
4. Swipe gestures on a "recent categories" strip to pick category in one motion
5. Home-screen widget / quick-add shortcut that skips opening the full app entirely (wild — likely V2+)

*Non-judgmental budget status (HMW #2, #5)*
6. Budget shown as a simple progress bar with neutral color gradient (green→amber→red) instead of an alarming banner
7. "Days left at this pace" framing instead of raw over/under numbers (reframes anxiety into a runway concept)
8. Friendly/neutral microcopy instead of warning language ("You're pacing a bit ahead this month" vs "OVER BUDGET")
9. Weekly "gentle check-in" summary instead of always-visible red state (borrowed from habit apps' non-punitive nudges)
10. Let user choose their own budget-status tone/severity ("chill mode" vs "strict mode" toggle) — wild/stretch idea

*Reducing decisions at spend-moment (HMW #3)*
11. Pre-filled smart defaults: last-used category + today's date, user only types amount
12. "Most likely category" prediction based on time of day / day of week (e.g., lunch around noon)
13. Big category icon buttons instead of a dropdown — visual recognition beats reading a list

*Zero-effort repeat logging (HMW #4 — analogous to game "quick-repeat" / messaging "recent contacts")*
14. "Duplicate last expense" one-tap button, prompting only for date confirmation (already in brainstorm — reinforced here)
15. "Frequent expenses" shelf (like frequently-contacted people in a messaging app) — tap a saved chip like "Coffee $5" to log instantly
16. Streak counter styled like a game streak (Duolingo-style flame icon) for consecutive logging days — light gamification, not punitive

*Glanceable "am I OK?" moment (HMW #5)*
17. Single "traffic light" dashboard state (green/amber/red) as the very first thing seen on open, above the detailed table
18. Today/This Month summary cards as the literal first-screen content, no scrolling needed
19. One-sentence daily insight ("You've spent $340 of $800 this month — on pace") instead of raw charts

*Trust without bank credentials (HMW #6)*
20. Explicit "we never connect to your bank" reassurance microcopy near onboarding/settings — trust-building through transparency, not just omission
21. Manual entry framed as a feature ("you're always in control of what's tracked") rather than an apology for lacking bank sync

### Top Concepts

1. **One-tap quick-add with smart defaults** (ideas 1, 2, 11, 12) — collapses the "opening a form" feeling into a near-instant action
2. **Neutral/non-judgmental budget status framing** (ideas 6, 7, 8, 17) — traffic-light + reframed language instead of alarming banners
3. **Frequent-expenses quick-log shelf** (ideas 14, 15) — turns repeat purchases into a tap, directly extends the brainstorm's "duplicate expense" idea

---

## 🛠️ PROTOTYPE: Make Ideas Tangible

### Prototype Approach

Paper Prototyping for the three individual screens, sequenced into a Storyboard of the full "spend → log → check status" journey. Roughness over fidelity — the goal is testing whether the flow *feels* fast and non-judgmental, not visual polish.

### Prototype Description

**Storyboard — 3 scenes:**

1. **Scene 1 — Quick-Add (Concept 1):** User taps the floating "+" button. A single screen opens with a large numeric keypad for amount, today's date pre-filled, last-used category pre-selected as a big icon button (tap to change if needed). User taps a checkmark. Done in under 3 taps.

2. **Scene 2 — Frequent-Expenses Shelf (Concept 3):** Alternate/faster path from Scene 1 — instead of the keypad, the quick-add screen shows a horizontal shelf of chips ("Coffee $5", "Bus $2.50", "Lunch $12") above the keypad. Tapping a chip logs that exact expense immediately with today's date — zero further input.

3. **Scene 3 — Dashboard Status (Concept 2):** User opens the app (or returns to home after logging). First thing visible, above the fold, is a traffic-light progress bar ("$340 of $800 this month — on pace, 12 days left") in neutral colors, not alarming red/warning icons. Detailed table is available by scrolling down, not forced upfront.

### Key Features to Test

- Can a user complete a "log an expense" action in under ~5 seconds using either the keypad path or the chip-shelf path?
- Does the neutral traffic-light framing feel informative rather than anxiety-inducing, compared to a traditional red "OVER BUDGET" banner?
- Do users notice and reuse the frequent-expenses chips, or do they default to the keypad every time (tells us if idea 15 earns its screen space)?
- Does pre-filled category/date ever feel *wrong* enough to be annoying (mis-predicted category), and how much friction does correcting it add?

---

## ✅ TEST: Validate with Users

### Testing Plan

**Who:** 5-7 students/young professionals — ideally people who currently either avoid budgeting apps or have abandoned one before (matches our persona exactly). Friends, classmates, or coworkers are fine for this fidelity.

**Tasks to attempt** (using the storyboard/paper prototype, walked through by the facilitator):
1. "You just bought a coffee for $5. Log it." (tests Scene 1 keypad path and Scene 2 chip-shelf path — try both, note which they reach for)
2. "Check whether you're on track with your budget this month." (tests Scene 3 dashboard status)
3. "You want to log the same lunch you had yesterday." (tests duplicate/chip-reuse behavior)

**Assumptions to explicitly check (Assumption Testing):**
- Neutral/traffic-light framing reduces anxiety vs. a traditional red warning banner
- Users will notice and reuse the frequent-expenses chip shelf rather than defaulting to manual entry every time
- Pre-filled smart defaults (category/date) are correct often enough that correcting them doesn't feel like extra work
- In-the-moment logging (right after spending) is genuinely preferred over end-of-day batch entry

**Questions to ask afterward:**
- "Walk me through what you were thinking as you did that." (think-aloud, don't lead)
- "Did anything feel slow, confusing, or annoying?"
- "How did the budget status make you feel — informed? Anxious? Indifferent?"
- "Would you actually use this daily? Why or why not?"

**How feedback will be captured:** Feedback Capture Grid — four quadrants: Liked / Questions / Ideas / Changes — filled in live during or immediately after each session.

### User Feedback

_To be filled in once usability sessions are conducted — capture raw observations and quotes per the Feedback Capture Grid above (Liked / Questions / Ideas / Changes)._

### Key Learnings

_To be synthesized after testing — specifically revisit the four assumptions above (validated or invalidated?) and note any new insights that emerged._

---

## 🚀 Next Steps

### Refinements Needed

- Since real usability testing hasn't happened yet, the immediate refinement isn't to the design — it's to *run* the testing plan from the Test phase before locking in UI details in a PRD/UX spec.
- The three top concepts (quick-add, neutral budget status, frequent-expenses shelf) should carry forward as the shape of the MVP entry/dashboard experience — they translate the brainstorm's "manual-but-fast, pull-based" principles into concrete interactions.
- Watch the two riskiest assumptions closely once tested: (1) whether the frequent-expenses shelf actually gets used over manual entry, and (2) whether "neutral" framing genuinely reads as non-judgmental rather than just vague.

### Action Items

1. Run the usability sessions from the Test phase with 5-7 people from the target persona
2. Fill in the User Feedback and Key Learnings sections of this document based on real sessions
3. Carry the validated (or revised) concepts into UX specification — the floating quick-add button, chip-shelf, and traffic-light status bar should become concrete UI patterns in the next design pass
4. Feed the reinforced brainstorm insight (reusable summary-card component with color-state prop) forward into architecture/UX work, now backed by the empathy/define work on non-judgmental framing
5. Revisit the "Won't-Have" list (bank sync, receipts, reminders) periodically — if users specifically ask for these later, that's new signal, not scope creep to dismiss automatically

### Success Metrics

- **Task completion:** users can log an expense via keypad or chip shelf in under ~5 seconds without hesitation or confusion, observed directly in testing
- **Sentiment:** post-task interview responses describe the budget status as "informative" or "helpful" rather than "stressful" or "annoying"
- **Adoption signal:** in testing, at least some users spontaneously reach for the frequent-expenses shelf without being prompted
- **Project-level:** Raha completes the MVP build using BMAD workflow end-to-end, with this design-thinking output visibly shaping the UX/architecture decisions that follow

**Next cycle:** The natural next step is executing the Test phase for real, then moving into UX/architecture work (e.g., `bmad-ux` or `bmad-architecture`) carrying these validated concepts forward.

---

_Generated using BMAD Creative Intelligence Suite - Design Thinking Workflow_
