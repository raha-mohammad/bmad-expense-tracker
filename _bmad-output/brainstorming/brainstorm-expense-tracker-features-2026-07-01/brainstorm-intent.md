# Brainstorm Intent: Simple Expense Tracker

**Product:** Simple Expense Tracker web app — a BMAD learning project.

## MVP Feature List (Must-Have)

**Entry & Data**
- Minimal required fields to log an expense: amount, category, date (all other fields optional)
- Search and filter expenses (by date range, category, amount range; search by description)

**Categories**
- Default categories (Food, Transport, Shopping, Bills, Entertainment) plus user add/edit/delete for custom categories

**Budget**
- Set a monthly budget
- View spent amount, remaining amount, and within/over-budget status

**Dashboard**
- Summary cards (Today's Spending, This Month's Spending, Remaining Budget)
- Detailed table view of expenses

**Engagement**
- Persistent floating "Add Expense" button visible on every page for one-click access to the entry form

## Version 2 / Later Feature List

**Entry & Data (Should/Could)**
- Smart defaults on the entry form: auto-select today's date, pre-fill last-used category
- Duplicate a past expense (one click, then update the date) instead of a full recurring-transactions engine

**Categories (Could)**
- Optional icons per category

**Budget (Should)**
- Over-budget warning: summary card turns red with a warning message when spending exceeds the monthly budget

**Dashboard (Should/Could)**
- Pie chart by category, bar chart by month
- Monthly insights: top spending categories, month-over-month comparison, save-money suggestions

**Engagement (Could)**
- Daily logging-streak counter for consecutive days of expense entry

## Explicitly Out of Scope (Won't-Have)

- Bank account sync
- Receipt photo uploads
- Evening daily reminder notification

## Design Principles & Insights to Preserve

- **Manual-but-fast over automatic-but-complex**: deliberately cut bank sync, receipts, and reminders; kept duplicate-expense, smart defaults, and streak as lightweight substitutes.
- **Pull-based engagement over push-based**: streak, duplicate, and smart defaults form one consistency-focused theme that pulls users back in, rather than pushing notifications at them.
- **Reusable component insight**: the over-budget red warning card and the dashboard summary cards should be built as the same component with a color-state prop, not separate components.
- **Cheap V2 win**: Monthly insights (Could-have) reuses the same aggregation queries the MVP dashboard already needs, making it a low-cost first V2 addition.
- **Build order**: MVP = Must-haves (CRUD + budget + dashboard core); Should/Could items form the V2 roadmap; Won't items remain out of scope.
