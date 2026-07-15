# P-004 Test Checklist (RP-02)

**Invocation:** `test-p004-20260715`

- [x] Independent test context (no product code changes)
- [x] `mvn -q test` PASS (IT skipped when gate off)
- [x] `mvn -q test -Dxugu.run.integration=true` PASS on real XuguDB
- [x] `python harness/scripts/verify.py --phase P-004 --evidence harness/evidence/test/P-004/verification.json` → VERIFY PASS
- [x] Spot-check: LIMIT count OFFSET offset with bind markers
- [x] Spot-check: no FETCH FIRST / SKIP LOCKED / FOR SHARE on supported paths
- [x] Spot-check: FOR UPDATE / NOWAIT / WAIT evidence
- [x] Observed flow: limit-offset-pagination-real-db
- [x] Observed flow: pessimistic-lock-sql-real-db
- [x] TEST-REPORT + handoff written; RP-02 `passed` / `test-p004-20260715`
