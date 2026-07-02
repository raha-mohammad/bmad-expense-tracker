---
title: "Product Brief: Simple Expense Tracker"
status: final
created: 2026-07-02
updated: 2026-07-02
---

# Product Brief: Simple Expense Tracker

## Executive Summary

Simple Expense Tracker is a free, manual-entry-only web app for logging personal spending and watching it against a monthly budget. It exists to answer one question fast — "where did my money go?" — without asking for a bank login, a receipt photo, or ten minutes of setup before the first entry.

Everything about it is built around one insight from the research: people don't quit expense tracking because the app lacks features. They quit because logging feels like a chore, missing a few days feels like failure, and the backlog of unlogged spending becomes too embarrassing to face. Most competitors respond to friction with more automation (bank sync, receipt scanning); this app responds by staying manual and making the *moment of logging* — and the moment of coming back after a lapse — as low-stakes as possible.

This is a solo build, done as a practice project to work through the BMAD method end-to-end and to produce a clean portfolio piece on a Next.js + Spring Boot + PostgreSQL stack. V1 is intentionally small: quick expense capture, a non-judgmental budget-status view, and shortcuts for repeat spending. The sharper idea the research surfaced — forgiving re-entry after a lapse — is real, but untested, and is named here as where the product is headed rather than promised for day one.

## The Problem

Budgeting apps have a well-documented failure pattern, and it isn't "nobody starts." It's this: someone logs consistently for a few days, then life happens — they miss a day, then three, then a week. The backlog of unlogged spending starts to feel overwhelming, checking the app starts to feel like a confrontation, and they quietly stop opening it. The habit doesn't erode gradually; it snaps.

Two things make this worse than plain guilt:

- **Setup-phase abandonment.** Apps that ask for bank credentials, category customization, or goal-setting before showing any value lose people before the first real session.
- **Shame, not just guilt.** Seeing "$47 on coffee" with no context reads as an identity judgment ("I'm bad with money"), not just a behavior note ("I overspent"). Shame produces stronger avoidance than guilt — red "OVER BUDGET" banners make this worse, not better.

The target users — students and young professionals who've already tried and dropped YNAB, PocketGuard, or Monarch — aren't rejecting the idea of tracking spending. They're rejecting the experience of it. They are friction-sensitive, not effort-averse.

## The Solution

A single-purpose web app with one core loop: **log an expense in under 5 seconds → see it reflected in this month's category totals and budget status → come back tomorrow and do it again.**

- **Quick-add**: amount, category, date — with smart defaults (today pre-filled, last-used category pre-selected) so a routine entry is one or two taps.
- **Default categories with custom control**: ships with five common categories (Food, Transport, Shopping, Bills, Entertainment) so day one requires no setup, but users can add, edit, or delete categories whenever they want.
- **Frequent-expenses shelf**: one-tap re-logging of repeat purchases ("Coffee $5") instead of re-entering the same thing daily.
- **Neutral budget status**: a traffic-light (green → amber → red) view with plain, non-judgmental microcopy ("days left at this pace" rather than "OVER BUDGET").
- **Persistent add button**: logging is always one tap away, never buried in a menu.

No bank-account linking, no receipt-photo capture, no push-notification reminders — these are deliberate exclusions, not deferred features. They pull toward the automation-and-friction pattern the research shows this app is explicitly built to avoid. All totals and budget-remaining figures are *derived* from logged transactions at read time; nothing is independently stored or hand-edited, so the numbers can't drift out of sync with what was actually logged.

Platform direction (validated, not yet locked in detail): Next.js frontend talking to a Spring Boot REST API backed by PostgreSQL, single-user with no login for V1.

## What Makes This Different

Here's the honest version, because the research already pressure-tested the tempting version and it didn't hold up:

**"Manual-first, no bank sync" is not a differentiator on its own.** A competitor (Finny) already owns that exact positioning — manual, free, offline, student-focused. Claiming it as unique would be building on a claim the market research already debunked.

**"Fast" and "non-judgmental" are each already claimed separately too** — Monefy/DailyBean compete on entry speed; YNAB and The Budgeting App claim shame-free framing. Neither combines the two with manual-only entry. The credible position is the *combination*: fast manual capture that treats a lapse as normal instead of a failure, all the way through to helping someone pick back up without a guilt spiral.

**The sharpest idea — a genuinely forgiving "lapse recovery" experience (streak-with-forgiveness, no backlog shame on return) — is not yet validated.** No usability testing has been run on any of this; the empathy map, POV, and prototype are all synthesized from brainstorming, not real users. That's fine for a V1 built to learn and to demonstrate product thinking, but it means this brief treats lapse recovery as the vision to build toward, not a proven advantage to lead with.

In short: the value proposition today is credible, not novel. The path to novel exists and is named below, but it's earned through testing, not asserted here.

## Who This Serves

**Primary: "The Anxious Starter."** Students and early-career professionals (roughly 18–24) managing their own money for the first time — irregular or entry-level income, little to no budgeting experience, and no patience for setup. They've either never tracked spending consistently or tried an app once and bounced off it within days. Success for them looks like: open the app, log something in seconds, see where they stand without feeling judged.

Two related segments surfaced in research but aren't the primary V1 focus: young professionals who've already churned from a "real" budgeting app and want pattern visibility without categorization discipline, and privacy-conscious manual trackers who avoid bank-linked apps by choice. Both are compatible with this design and may adopt it, but V1 isn't built to court them specifically.

## Success Criteria

This is a practice/portfolio project first, so success is judged on execution and completeness rather than adoption metrics:

- The full BMAD workflow — brief through working implementation — is carried out end-to-end on a real, non-trivial product idea.
- The app works cleanly: a user can log an expense in a few taps, see accurate category totals and budget status, and use the frequent-expenses shelf — with the Next.js/Spring Boot/PostgreSQL stack demonstrated correctly (proper DTO boundaries, no leaked JPA entities, working CORS setup).
- The product decisions in this brief — especially the honest differentiation story — hold up as a coherent, defensible case when explained to someone else.
- If personal use follows naturally after the build, that's a bonus signal the design worked, but it isn't the bar this project is measured against.

## Scope

**In for V1:**
- Log an expense: amount, category, date (defaults to today).
- Category management: default set (Food, Transport, Shopping, Bills, Entertainment) plus full create/edit/delete for custom categories.
- Frequent-expenses shelf for one-tap re-logging of repeat entries.
- Monthly budget: set a total, see spend-to-date by category and overall, neutral traffic-light status.
- Single-user, no accounts or login.

**Explicitly out (not deferred — deliberately excluded):**
- Bank-account linking / transaction import.
- Receipt-photo capture or OCR.
- Push notifications or reminders.
- Multi-user accounts, auth/login.

**Deferred to a later version, pending V1 completion and, ideally, real user feedback:**
- Charts/insights (pie/bar breakdowns) — reuses V1's aggregation queries, low-cost add-on.
- Lapse-recovery / streak-with-forgiveness flow — the untested differentiator; it should not be built until there's a way to observe whether it actually changes how someone returns after missing days.
- Rollover behavior for unspent/overspent budget between months — a real domain decision, just not one V1 needs to make.

## Vision

If the core loop proves out, the app grows toward the idea the research pointed at but couldn't validate yet: a tracker that treats missing a few days as a normal, recoverable event rather than a reason to quit — where coming back after a lapse feels as easy and judgment-free as the first entry did. Everything past V1 (insights, rollover, lapse recovery) is in service of that one outcome, not feature growth for its own sake.
