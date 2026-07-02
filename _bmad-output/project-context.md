---
project_name: 'bmad-expense-tracker'
user_name: 'Raha'
date: '2026-07-02'
sections_completed: ['technology_stack', 'development_workflow_rules', 'critical_donts_miss_rules']
existing_patterns_found: 1
---

# Project Context for AI Agents

_This file contains critical rules and patterns that AI agents must follow when implementing code in this project. Focus on unobvious details that agents might otherwise miss._

---

## Technology Stack & Versions

_Not yet decided._ The project is still in the planning phase — brainstorming, design thinking, and innovation strategy are complete, but no PRD or architecture document exists yet. Do not assume a stack; check for `_bmad-output/**/architecture.md` before implementing anything.

## Critical Implementation Rules

### Development Workflow Rules

- Commit messages follow a short prefix + colon format, e.g. `upd: <summary>` (observed in git history) — keep this format consistent until told otherwise.
- Planning artifacts live under `_bmad-output/` (e.g. `_bmad-output/design-thinking-2026-07-02.md`, `_bmad-output/brainstorming/...`) — don't scatter planning docs elsewhere.
- `docs/` is the designated project-knowledge folder (currently empty) — reference material belongs there, not in `_bmad-output/`.

### Critical Don't-Miss Rules

- This file is a placeholder. Absence of documented rules here does NOT mean no conventions apply — check for an `architecture.md` or PRD before assuming.
- Regenerate this file (run `/bmad-generate-project-context` again) once the tech stack and architecture are decided, so real language/framework/testing rules can be captured.
