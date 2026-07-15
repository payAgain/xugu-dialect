# P-004 Test Checklist — Retest

**Invocation:** `test-p004-retest-20260715`  
**Prior:** `test-p004-20260715` → fix `impl-p004-fix-20260715`

- [x] Independent test context (no product code changes)
- [x] `mvn -q test` PASS (9 IT skipped when gate off)
- [x] `mvn -q test -Dxugu.run.integration=true` PASS on real XuguDB (9 IT)
- [x] `python harness/scripts/verify.py --phase P-004 --evidence harness/evidence/test/P-004/verification-retest.json` → VERIFY PASS
- [x] Confirm A-LCK-005 limitation notes (javadoc + NOTES + matrix 文档不允许 / shim / exclusive / concurrent readers may block)
- [x] Confirm LIMIT+FOR UPDATE order is FOR UPDATE then LIMIT (unit + live IT; Hibernate LIMIT…FOR UPDATE rejected)
- [x] Observed flow: limit-offset-pagination-real-db
- [x] Observed flow: pessimistic-lock-sql-real-db
- [x] Observed flow: limit-for-update-combo-real-db
- [x] TEST-REPORT-RETEST + handoff updated; verdict PASS
