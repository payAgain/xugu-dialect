<!-- TRELLIS:START -->
# Trellis Instructions

These instructions are for AI assistants working in this project.

This project is managed by Trellis. The working knowledge you need lives under `.trellis/`:

- `.trellis/workflow.md` — development phases, when to create tasks, skill routing
- `.trellis/spec/` — package- and layer-scoped coding guidelines (read before writing code in a given layer)
- `.trellis/workspace/` — per-developer journals and session traces
- `.trellis/tasks/` — active and archived tasks (PRDs, research, jsonl context)

If a Trellis command is available on your platform (e.g. `/trellis:finish-work`, `/trellis:continue`), prefer it over manual steps. Not every platform exposes every command.

If you're using Codex or another agent-capable tool, additional project-scoped helpers may live in:
- `.agents/skills/` — reusable Trellis skills
- `.codex/agents/` — optional custom subagents

Managed by Trellis. Edits outside this block are preserved; edits inside may be overwritten by a future `trellis update`.

<!-- TRELLIS:END -->

# Project notes (xugu-dialect)

- Product charter: `PROJECT_CHARTER.md`
- Decisions: `DECISIONS/`
- Public contracts: `contracts/`
- User guide: `docs/user-guide/`
- Historical initiative archives (former harness → Trellis): `.trellis/tasks/archive/2026-07/`
- Migrated error journal: `.trellis/workspace/yyyymd/error-journal.md`
- Verify: `mvn -q -DskipTests package` and `mvn -q test` (optional live: `XUGU_RUN_IT=true mvn -q test`)
