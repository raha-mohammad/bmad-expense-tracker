---
name: bmad-expense-tracker
description: Calm, minimal manual expense tracker for first-time budgeters. shadcn/ui on Next.js + Tailwind; this DESIGN.md specifies the brand-layer delta only ("Calm Harbor" direction).
status: final
updated: 2026-07-03
colors:
  # Brand overrides on top of shadcn defaults. All unlisted tokens inherit
  # from shadcn (foreground, muted, muted-foreground, popover, popover-foreground,
  # input, secondary, accent, destructive — destructive is for destructive
  # ACTIONS like "delete category", kept distinct from budget-danger below).
  # `ring` IS overridden below (primary-tinted, not shadcn's default) — every
  # focus indicator in the product uses this value.
  background: '#F5F9FC'
  primary: '#2563EB'
  primary-foreground: '#FFFFFF'
  ring: 'rgba(37,99,235,.35)'
  card: '#FFFFFF'
  card-foreground: '#111827'
  border: '#D1D5DB'
  chip-bg: '#EFF6FF'
  category-selected-bg: '#DBEAFE'
  budget-safe: '#065F46'
  budget-safe-bg: '#ECFDF5'
  budget-warning: '#92400E'
  budget-warning-bg: '#FFFBEB'
  budget-danger: '#991B1B'
  budget-danger-bg: '#FEF2F2'
typography:
  # Body, label, and heading all inherit shadcn's system sans stack — no display
  # override. Calm Harbor reads friendly through weight and spacing, not a second typeface.
  body:
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif'
    fontSize: 14px
    fontWeight: '400'
    lineHeight: '1.5'
  label:
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif'
    fontSize: 12px
    fontWeight: '700'
    letterSpacing: 0.02em
  heading:
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif'
    fontSize: 17px
    fontWeight: '700'
  amount-display:
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif'
    fontSize: 40px
    fontWeight: '700'
rounded:
  sm: 10px
  md: 16px
  lg: 20px
  full: 9999px
spacing:
  # shadcn / Tailwind default 4-based scale inherited (4, 8, 12, 16, 20, 24, 32, 40).
  gutter: 16px
  card-pad: 16px
components:
  budget-status-card:
    background-safe: '{colors.budget-safe-bg}'
    foreground-safe: '{colors.budget-safe}'
    background-warning: '{colors.budget-warning-bg}'
    foreground-warning: '{colors.budget-warning}'
    background-danger: '{colors.budget-danger-bg}'
    foreground-danger: '{colors.budget-danger}'
    radius: '{rounded.md}'
    icon-paired-with-color: true
  category-icon-button:
    background: '{colors.card}'
    background-selected: '{colors.category-selected-bg}'
    border-selected: '{colors.primary}'
    radius: '{rounded.md}'
    min-size: 60px
  frequent-expense-chip:
    background: '{colors.chip-bg}'
    foreground: '{colors.card-foreground}'
    radius: '{rounded.full}'
  quick-add-save-button:
    background-enabled: '{colors.primary}'
    foreground-enabled: '{colors.primary-foreground}'
    background-disabled: '#E5E7EB'
    foreground-disabled: '#9CA3AF'
    radius: '{rounded.full}'
  fab:
    background: '{colors.primary}'
    foreground: '{colors.primary-foreground}'
    radius: '{rounded.full}'
    shadow: '0 6px 14px rgba(0,0,0,.28)'
  focus-ring:
    color: '{colors.ring}'
    width: 3px
    offset: 2px
    style: 'solid outline (or equivalent box-shadow), never suppressed'
  category-row:
    background: '{colors.card}'
    radius: '{rounded.md}'
    shadow: 'inherits generic card treatment — see Elevation & Depth'
  search-result-row:
    background: '{colors.card}'
    radius: '{rounded.md}'
    shadow: 'inherits generic card treatment — see Elevation & Depth'
    running-total-emphasis: 'amount-display typography, positioned above the result list'
---

## Brand & Style

bmad-expense-tracker is a manual expense tracker for someone managing their own money for the first time — a student or young professional who wants to know where their money went without adopting a finance-app identity. The brand premise: *calm competence*. Nothing here should look like a bank, a spreadsheet, or a game. "Calm Harbor" — a soft blue-and-white register, airy spacing, rounded cards — carries that premise: trustworthy without being clinical, encouraging without being twee.

The product inherits shadcn/ui defaults wholesale (Next.js + Tailwind). This DESIGN.md specifies only the brand-layer delta — background, primary color, card treatment, corner radii, and the three budget-status colors. Standard shadcn components (`Button`, `Card`, `Input`, `Dialog`, `Badge`) inherit shadcn's visual specs as-is except where this file specifies otherwise.

## Colors

- **Background (`#F5F9FC`)** — a barely-there cool white-blue, not stark white. Used as the page background everywhere; never used for cards or content surfaces.
- **Primary Blue (`#2563EB`)** — the brand color. Used on the FAB, the primary save/CTA button, selected-category border, and active nav/focus states. **Reserved for brand and navigation only — never reused for budget-status meaning.**
- **Budget Safe / Warning / Danger** (green `#065F46` / amber `#92400E` / red `#991B1B`, each on its own tint background) — reserved **exclusively** for budget-status severity: the dashboard status card and its post-save echo. No other component in the product may borrow these hues, including destructive actions (see below). Every status color always ships paired with an icon and factual copy — color is reinforcement, never the sole signal (WCAG 2.1 AA, non-text contrast). Computed contrast ratios (text-on-tint; re-verify once real font weights are implemented): green ≈ **7.3:1**, amber ≈ **6.8:1**, red ≈ **7.6:1** — all clear the 4.5:1 AA minimum for normal text with margin.
- **`destructive`** — inherited from shadcn's default (not redefined here). Used only for genuinely destructive actions (e.g. "Delete category"). Kept visually distinct from `budget-danger` so a user never confuses "you're over budget" with "you're about to delete something."
- **Card (`#FFFFFF`) / Border (`#D1D5DB`)** — cards are solid white on the tinted background, lifted with shadow (see Elevation), not a border, in the primary Dashboard/Quick-Add surfaces. Border color is used sparingly, mainly on inputs and dividers.
- **`ring` (`rgba(37,99,235,.35)`, primary-tinted)** — the one focus-visible token every interactive element uses. Overrides shadcn's default `ring`; see Components → Focus ring.

Avoid: gradients, more than one brand accent color, tinting chrome (nav, headers, backgrounds) with the budget-status palette.

## Typography

System sans stack throughout (`-apple-system, "Segoe UI", Roboto, Arial`) — no display/serif moment. Calm Harbor's friendliness comes from generous line-height and weight contrast, not a second typeface family, keeping the type ramp trivial to implement on top of shadcn's default (Geist Sans is an acceptable substitute if the project adopts it; the semantic roles below are what matter).

- `body` (14px/1.5, regular) — all content text, category labels, copy.
- `label` (12px, bold, slight letter-spacing) — field labels, section eyebrows ("Frequent — tap to log instantly").
- `heading` (17px, bold) — screen titles, "Hi Sam" greeting.
- `amount-display` (40px, bold) — the live amount on Quick Add only. The one moment where type carries the "this is what matters right now" weight.

All sizes above are the base (100%) reference; implement with relative units (`rem`, not fixed `px`) so text scales cleanly with browser zoom / OS text-scaling up to 200% without truncation or overlap (WCAG 1.4.4).

## Layout & Spacing

Mobile-first: primary layout target is a ~375–414px viewport, single column, thumb-reachable controls in the lower two-thirds of the screen. `{spacing.gutter}` (16px) is the base horizontal padding on every screen. Cards stack vertically with 8–10px gaps (`cat-item` list rhythm). On wider viewports (tablet/desktop), the single column caps at a comfortable reading width and centers rather than stretching category cards edge-to-edge — this is a manual-entry tool, not a data table.

## Elevation & Depth

One elevation step: cards float on the tinted background with a soft, low-contrast shadow (`0 2px 8px rgba(15,23,42,.08)`), never a hard border. The FAB carries a slightly stronger shadow (`0 6px 14px rgba(0,0,0,.28)`) since it's the one element that must read as "always tappable, floating above everything." Nothing else elevates — flat hierarchy keeps the calm register intact.

## Shapes

Rounded throughout, scaled by role: `{rounded.sm}` (10px) for inputs, `{rounded.md}` (16px) for cards and category buttons, `{rounded.lg}` (20px) for the phone-frame screen corners, `{rounded.full}` for the FAB, the save button, and frequent-expense chips (pill shape signals "one tap, done"). No sharp corners anywhere — squareness would read closer to a spreadsheet than a calm personal tool.

## Components

- **Budget status card** — the single most important component in the product. Same position and layout in all three states (safe / warning / danger); only background tint, icon, and copy change. Icon + text always paired with color, never color alone (✅ / ▲ / ✕ or equivalent). Radius `{rounded.md}`.
- **Category icon button** — 60×60px minimum, comfortably beyond common target-size guidance. Icon + short label; selected state shows `{colors.primary}` border, `{colors.category-selected-bg}` fill, **and** a small checkmark badge in the corner — the badge exists so selection never depends on the border-color change alone, given category selection is this product's known friction point. Never a dropdown.
- **Frequent-expense chip** — pill-shaped, horizontally scrollable shelf, `{colors.chip-bg}` fill. Visually distinct from category buttons (different shape — pill vs. rounded-square) so the two "fast paths" (repeat-chip vs. manual-entry) never look identical.
- **Quick-add save button** — circular, `{rounded.full}`. Disabled state (`#E5E7EB` / `#9CA3AF`) is real, not just low-opacity, so it reads clearly on the flat Calm Harbor palette; enabled state uses `{colors.primary}`.
- **FAB** — persistent, bottom-right, circular, `{colors.primary}` fill, always present on the Dashboard.
- **Focus ring** — `{colors.ring}`, `{components.focus-ring.width}`/`{components.focus-ring.offset}` (3px/2px) on every focusable element; see Colors for the token and its rationale.
- **Category row** (Categories screen) / **Search result row** (Search & Filter) — both inherit the same generic card treatment as everything else (`{colors.card}` background, `{rounded.md}`, the single soft-shadow elevation step from `Elevation & Depth`). No bespoke visual delta beyond that; the running total on Search & Filter uses `amount-display` typography so it reads with the same weight as the Quick Add amount.

## Do's and Don'ts

| Do | Don't |
|---|---|
| Use `{colors.primary}` for brand chrome, CTAs, and selection state | Use `{colors.primary}` (or any budget-status color) decoratively elsewhere |
| Pair every budget-status color with an icon and factual copy | Rely on color alone to communicate budget severity |
| Keep destructive actions on shadcn's default `destructive`, distinct from `budget-danger` | Reuse `budget-danger` red for "Delete category" or other destructive actions |
| Round everything — cards, buttons, chips, screens | Introduce sharp corners anywhere in the product |
| One flat elevation step (soft shadow, no borders on primary cards) | Stack multiple shadow/elevation levels |
| Keep the type ramp to one family, weight/size for hierarchy | Add a display/serif typeface for "personality" |
