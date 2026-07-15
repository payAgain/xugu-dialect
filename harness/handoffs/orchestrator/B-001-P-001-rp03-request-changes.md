# B-001 / P-001 — Orchestrator dispatch after RP-03 request-changes

**Date:** 2026-07-15T17:56:00+08:00  
**Role:** orchestrator  
**Build / Phase:** B-001 / P-001 (I-002)  
**Review invocation:** `rev-p001-20260715`  
**Decision:** `request-changes` (RP-03 `failed`)  
**Accept:** **NOT authorized** — do not Accept, do not commit yet.

## Evidence landed

- `harness/evidence/reviewer/P-001/REVIEW.md`
- `harness/handoffs/readonly-results/P-001-reviewer.yaml`
- `harness/tasks/P-001.md` RP-03 → `failed` / `invocation_id: rev-p001-20260715` / `decision: request-changes`

## Next dispatch: implementer (fix)

**Role instance:** implementer (new invocation)  
**invocation_id:** `impl-p001-fix-locklimit-20260715`  
**Scope:** Allowed paths from `harness/tasks/P-001.md` only.

### Must fix (MAJOR)

1. **AST/HQL FOR UPDATE → LIMIT (+ WAIT)** — Add gated IT and/or unit tests proving SqlAstTranslator path emits `FOR UPDATE` before `LIMIT` (and `WAIT` after `LIMIT` when present), matching `XuguLimitHandler`. Capture SQL. Execute on live DB with `-Dxugu.run.integration=true`.

### After implementer PASS

1. Dispatch **test** (independent) — re-run `mvn test` / `-Dxugu.run.integration=true` / `verify.py --phase P-001`.
2. Dispatch **reviewer** again (new `rev-p001-*`) — re-check MAJOR + prior checklist.
3. Only on reviewer **approve** → orchestrator Accept + must-commit (Human Gate still owns Ship).

### Explicitly out of scope

- Accept / git commit / tag / push
- P-002 `getQuerySequencesString` / sequence metadata
- Sibling `hibernate-dialect` port
- Version bump

## Pipeline snapshot

| Step | Role | Status |
|---|---|---|
| RP-01 | implementer | passed (`impl-p001-20260715`) — **superseded pending fix** |
| RP-02 | test | passed (`test-p001-20260715`) — **must re-run after fix** |
| RP-03 | reviewer | **failed** / request-changes (`rev-p001-20260715`) |
