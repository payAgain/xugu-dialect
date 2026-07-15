# P-005 Test Checklist (RP-02)

**Invocation:** `test-p005-20260715`

- [x] Independent test context (no product code changes)
- [x] `mvn -q test` PASS (IT skipped when gate off)
- [x] `mvn -q test -Dxugu.run.integration=true` PASS on real XuguDB
- [x] `python harness/scripts/verify.py --phase P-005 --evidence harness/evidence/test/P-005/verification.json` → VERIFY PASS
- [x] Spot-check locked forms: `identity(1,1)`; INSERT omit id; `select <seq>.nextval from dual`; `currval('name')`; FROM DUAL
- [x] Spot-check: no `auto_increment` / no `NEXTVAL('seq')` / no `seq.currval` claimed by dialect
- [x] Observed flow: identity-insert-real-db
- [x] Observed flow: sequence-generator-real-db
- [x] TEST-REPORT + handoff written; RP-02 `passed` / `test-p005-20260715`
