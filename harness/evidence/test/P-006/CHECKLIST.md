# P-006 Test Checklist (RP-02)

**Invocation:** `test-p006-20260715`

- [x] Independent test context (no product code changes)
- [x] `mvn -q test` PASS (IT skipped when gate off)
- [x] `mvn -q test -Dxugu.run.integration=true` PASS on real XuguDB
- [x] `python harness/scripts/verify.py --phase P-006 --evidence harness/evidence/test/P-006/verification.json` → VERIFY PASS
- [x] Spot-check: UUID primary = `uuid()`
- [x] Spot-check: JSON subset = `json_value` + `json_extract` (not MySQL dump; `json_set` absent)
- [x] Spot-check: listagg → `LISTAGG(...) WITHIN GROUP (ORDER BY ...)`
- [x] Spot-check: A-FUN-015 `bit_and` not registered
- [x] Observed flow: function-registry-hql-sql-real-db
- [x] TEST-REPORT + handoff written; RP-02 `passed` / `test-p006-20260715`
