# P-007 Test Checklist (RP-02)

**Invocation:** `test-p007-20260715`

- [x] Independent test context (no product code changes)
- [x] `mvn -q test` PASS (IT skipped when gate off)
- [x] `mvn -q test -Dxugu.run.integration=true` PASS on real XuguDB
- [x] `python harness/scripts/verify.py --phase P-007 --evidence harness/evidence/test/P-007/verification.json` → VERIFY PASS
- [x] Observed flow: schema-tooling-real-db
- [x] Observed flow: temp-table-comments-fk-real-db
- [x] Spot-check: `HIB_P007_*` cleanup (IT checklist + leftover probe = 0)
- [x] Spot-check: A-SCH-007 no temp FK (`StandardTemporaryTableExporter` / IT assert)
- [x] Spot-check: A-SCH-003 deferred (`NameQualifierSupport.SCHEMA` only; matrix 延后)
- [x] TEST-REPORT + handoff written; RP-02 `passed` / `test-p007-20260715`
