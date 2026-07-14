# B-003 / P-003 — Orchestrator dispatch after RP-04 request-changes

**Date:** 2026-07-14T18:01:00+08:00  
**Role:** orchestrator  
**Build / Phase:** B-003 / P-003  
**Review invocation:** `rev-p003-20260714`  
**Decision:** `request-changes` (RP-04 `failed`)  
**Accept:** **NOT authorized** — do not Accept, do not commit yet.

## Evidence landed

- `harness/evidence/reviewer/P-003/REVIEW.md`
- `harness/handoffs/readonly-results/P-003-reviewer.yaml`
- `harness/tasks/P-003.md` RP-04 → `failed` / `invocation_id: rev-p003-20260714` / `decision: request-changes`

## Next dispatch: implementer (fix)

**Role instance:** implementer (new invocation; do not reuse `impl-p003-20260714` as closed)  
**Suggested invocation_id:** `impl-p003-fix-binary-20260714`  
**Scope:** Allowed paths from `harness/tasks/P-003.md` only.

### Must fix (MAJOR)

**A-TYP-009 BINARY DDL** — current `columnType(BINARY|VARBINARY|…) → "binary($l)"` is not documented in XuGu `binary.md` (docs show bare `BINARY`). Round-trip IT uses hand-written `BINARY`, not SchemaExport of a binary attribute.

Choose **one**:

1. **Preferred if docs are SSOT:** change mapping to bare `binary`; keep length limits via `getMaxVarbinaryLength()` / binding; update unit asserts + matrix/contract notes; add SchemaExport IT that emits bare `BINARY` (or documents binding-only length).
2. **If live DB accepts length form:** keep `binary($l)` **only after** proving on live Xugu under `compatible_mode=NONE` via Hibernate SchemaExport IT; cite observed SQL + doc/evidence note.

### After implementer PASS

1. Dispatch **test** (independent) — re-run package / offline test / `-Dxugu.run.integration=true` / `verify.py --phase P-003`.
2. Dispatch **reviewer** again (new `rev-p003-*`) — re-check A-TYP-009 + prior checklist.
3. Only on reviewer **approve** → orchestrator Accept + must-commit (Human Gate still owns Ship).

### Explicitly out of scope for this fix loop

- Accept / git commit / tag / push
- Later-Phase features (pagination, locks, identity, sequence, functions, SPI)
- MINOR findings (TIME / DEFAULT / SchemaUpdate / CAST / identifier-case) unless same files are already open — not gate-closing unless reviewer elevates

## Pipeline snapshot

| Step | Role | Status |
|---|---|---|
| RP-01 | architect-contract | passed (`arch-p003-20260714`) |
| RP-02 | implementer | passed (`impl-p003-20260714`) — **superseded pending fix** |
| RP-03 | test | passed (`test-p003-20260714`) — **must re-run after fix** |
| RP-04 | reviewer | **failed** / request-changes (`rev-p003-20260714`) |
