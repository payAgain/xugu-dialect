# P-003 Test Checklist

- [x] `mvn -q -DskipTests package` EXIT 0
- [x] `mvn -q test` EXIT 0
- [x] `mvn -q test -Dxugu.run.integration=true` EXIT 0
- [x] `python harness/scripts/verify.py` → VERIFY PASS (`harness/evidence/test/P-003/verification.json`)
- [x] `python harness/scripts/harness_check.py` → HARNESS_CHECK PASS
- [x] `python harness/scripts/branch_check.py` → BRANCH_CHECK PASS
- [x] Docs alignment spot-check vs P-001/P-002 delivery
- [x] xugu-hibernate-test re-run: **N/A**
- [x] Version 7.4.5.Final unchanged
- [x] No dialect behavior rewrite by test role
