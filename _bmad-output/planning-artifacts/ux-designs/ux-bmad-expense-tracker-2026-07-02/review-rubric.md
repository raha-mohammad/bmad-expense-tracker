# Spine Pair Review — bmad-expense-tracker

## Overall verdict
The spine pair is in strong shape as a downstream contract: canonical section order in both files, all 6 flow instances present with named protagonist/climax/failure, and the visual reference is linked and scoped correctly. It falls short of "clean source-extraction" on two counts — two components used in EXPERIENCE.md have no visual counterpart in DESIGN.md, and EXPERIENCE.md asserts a "DESIGN.md primary-tinted ring" token that does not actually exist in DESIGN.md's frontmatter. Neither is a rewrite-scale problem; both are fixable in a single pass.

## 1. Flow coverage — strong
All 5 scoped flow areas are covered across exactly 6 flow instances: Flow 1 (Add Expense, shelf-chip path), Flow 2 (Add Expense, manual path), Flow 3 (View Dashboard), Flow 4 (Set Monthly Budget), Flow 5 (Manage Categories), Flow 6 (Search & Filter). Every flow has a named protagonist (Sam), numbered steps, a bolded **Climax** beat, and an explicit "Failure:" path (EXPERIENCE.md:134-190). No misses.

### Findings
- None — checked clean.

## 2. Token completeness — adequate
Every color token in frontmatter has a hex value (DESIGN.md:6-24); every `{path.to.token}` reference inside `components:` and body prose resolves to a real frontmatter key (verified `{colors.*}`, `{rounded.*}`, `{spacing.gutter}` all resolve). One reference does not resolve — see Finding 1.

### Findings
- **[high]** EXPERIENCE.md's "Focus (keyboard)" row cites `DESIGN.md` primary-tinted ring" (EXPERIENCE.md:88) and Accessibility Floor repeats "a replacement ring" (EXPERIENCE.md:102), but DESIGN.md's Colors comment explicitly lists `ring` as an *unlisted token inherited from shadcn* (DESIGN.md:7-10) — no `ring` or `focus-ring` key exists anywhere in DESIGN.md's frontmatter or Components section, and nothing states it is primary-tinted. The mockup independently invents `--focus-ring: rgba(37,99,235,.35)` (mockup:103) with no spec token backing it. Fix: add a `ring`/`focus-ring` token to DESIGN.md (value, width, offset) or soften EXPERIENCE.md's claim to "shadcn's inherited ring, unmodified."
- **[medium]** No numeric contrast target is stated anywhere. DESIGN.md:98 and EXPERIENCE.md:108 both assert budget-status pairs "meet AA contrast ratios" without stating the target (4.5:1 normal text) or a computed/verified ratio per pair. Fix: state the numeric target and record computed ratios for the three status pairs (see review-accessibility.md for a plausibility read).

## 3. Component coverage — thin
Every DESIGN.md.Components entry (5: budget-status-card, category-icon-button, frequent-expense-chip, quick-add-save-button, fab) has a matching EXPERIENCE.md.Component Patterns row. The reverse direction fails for 2 of 7 EXPERIENCE.md components.

### Findings
- **[high]** "Category row" (EXPERIENCE.md:71) and "Search result row + running total" (EXPERIENCE.md:72) have behavioral rows in EXPERIENCE.md.Component Patterns but no corresponding visual entry in DESIGN.md.Components (DESIGN.md:125-131 lists only 5 components). A downstream consumer building Categories or Search & Filter has no token guidance beyond the generic card treatment implied in Layout & Spacing. Fix: add visual spec rows for both (or an explicit "inherits generic card styling, no delta" note) to DESIGN.md.

## 4. State coverage — adequate
State Patterns (EXPERIENCE.md:75-88) covers Dashboard (no-budget, no-transactions, reopen-after-gap), Quick Add (empty/filled save button, save succeeds, save fails), Search & Filter (no matches), Categories (deleted-with-transactions), and global focus state — good breadth. Two states exist only inside Key Flows prose, not lifted into the table, and one plausible state is missing entirely.

### Findings
- **[medium]** Dashboard's cold-load skeleton is described only in Flow 3's failure text (EXPERIENCE.md:162), not as a State Patterns table row — the Drift shape example carries an explicit "Cold app load" row for the equivalent case. Fix: add a "Cold app load" row to State Patterns.
- **[medium]** Budget Settings has no State Patterns row at all, despite being a full IA surface (EXPERIENCE.md:37) with a described validation-failure state only in Flow 4's prose (EXPERIENCE.md:172, "Enter an amount greater than ₹0"). Fix: add a Budget Settings row (invalid/zero entry) to State Patterns.
- **[low]** No state is defined for adding a category with an empty or duplicate name (Categories surface) — Flow 5 (EXPERIENCE.md:174-182) only narrates the happy path and the "can't delete a default" non-failure. Fix: add a validation state/failure branch for category-name entry.

## 5. Visual reference coverage — strong
`mockups/calm-harbor-dashboard-quickadd.html` is linked inline from the IA section with a clear description of what it shows (EXPERIENCE.md:44), and "Spine wins on conflict" is stated in both directions (EXPERIENCE.md:44 and mockup:99). No misses.

## Mechanical notes
- Name consistency: component names match verbatim between DESIGN.md prose headers and EXPERIENCE.md table rows for all 5 shared components (only cosmetic difference: "FAB" vs "FAB (`+`)", not a real inconsistency). Frontmatter kebab-case keys map 1:1 to prose names.
- Shape fit: DESIGN.md section order matches the canonical spec exactly (Brand & Style → Colors → Typography → Layout & Spacing → Elevation & Depth → Shapes → Components → Do's and Don'ts). EXPERIENCE.md carries all required sections plus both triggered sections (Inspiration & Anti-patterns, Responsive & Platform) in the same order as the shadcn shape example (Foundation → IA → Voice and Tone → Component Patterns → State Patterns → Interaction Primitives → Accessibility Floor → Responsive & Platform → Inspiration & Anti-patterns → Key Flows). No shape violations.
- Minor implementation-detail mismatch (not a spec defect, but worth a developer's attention): EXPERIENCE.md prescribes `aria-describedby` for the disabled save button's reason (EXPERIENCE.md:105), but the reference mockup instead uses `aria-label="Save expense — add a description first"` (mockup:180). Align one way or note both are acceptable.
- Frontmatter completeness: both files' frontmatter carry all fields the spec/shape examples use (`name`, `status`, `sources`/`description`, `updated`, plus the full token set in DESIGN.md); no missing required keys.
