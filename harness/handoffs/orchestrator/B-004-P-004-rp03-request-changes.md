# B-004 / P-004 — Orchestrator dispatch after RP-03 request-changes

**Date:** 2026-07-15T09:25:00+08:00  
**Role:** orchestrator / coordinator  
**Build / Phase:** B-004 / P-004  
**Review invocation:** `rev-p004-20260715`  
**Decision:** `request-changes` (RP-03 `failed`)  
**Accept:** **NOT authorized** — do not Accept, do not commit yet.

## Evidence landed

- `harness/evidence/reviewer/P-004/REVIEW.md`
- `harness/handoffs/readonly-results/P-004-reviewer.yaml`
- `harness/tasks/P-004.md` RP-03 → `failed` / `invocation_id: rev-p004-20260715` / `decision: request-changes`

## Next dispatch: implementer (fix)

**Role instance:** implementer (new invocation; do not reuse `impl-p004-20260715` as closed)  
**Suggested invocation_id:** `impl-p004-fix-20260715`  
**Scope:** Allowed paths from `harness/tasks/P-004.md` only  
(`dialect/**`, matrix/contract notes, `harness/evidence/implementer/P-004/**`, implementer handoff).

### Must fix (MAJOR)

1. **A-LCK-005 limitations** — Update `XuguDialect` javadoc + implementer NOTES + matrix acceptance hint:
   - Hibernate shim only; **NOT** share-lock support
   - `PESSIMISTIC_READ` uses **exclusive** `FOR UPDATE` semantics
   - Concurrent readers **may block**; apps must **not** assume `FOR SHARE`
   - Matrix remains **文档不允许** for FOR SHARE surface

2. **LIMIT + FOR UPDATE order** — Add gated IT that runs Hibernate-generated or dialect-assembled query combining `LIMIT` and `FOR UPDATE` (ideally also `WAIT ms`) against real XuguDB under `compatiblemode=NONE`. Record observed SQL and success/fail in NOTES. If DB rejects `LIMIT … FOR UPDATE`, document as unsupported combo and adjust strategy; prefer prove it works.

### After implementer PASS

1. Dispatch **test** (independent) — re-run `mvn test` / `-Dxugu.run.integration=true` / `verify.py --phase P-004`.
2. Dispatch **reviewer** again (new `rev-p004-*`) — re-check MAJOR 1+2 + prior checklist.
3. Only on reviewer **approve** → orchestrator Accept + must-commit (Human Gate still owns Ship).

### Explicitly out of scope for this fix loop

- Accept / git commit / tag / push
- Later-Phase features (identity, sequence, functions, SPI)
- MINOR findings unless same files already open — not gate-closing unless reviewer elevates

## Pipeline snapshot

| Step | Role | Status |
|---|---|---|
| RP-01 | implementer | passed (`impl-p004-20260715`) — **superseded pending fix** |
| RP-02 | test | passed (`test-p004-20260715`) — **must re-run after fix** |
| RP-03 | reviewer | **failed** / request-changes (`rev-p004-20260715`) |
