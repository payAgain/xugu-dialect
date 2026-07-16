# P-004 Test Checklist (I-003)

- [x] `mvn -q -DskipTests package` EXIT 0
- [x] `mvn -q test` EXIT 0
- [x] Window/CTE gated IT EXIT 0 (`XuguWindowCteIT`)
- [x] `mvn -q test -Dxugu.run.integration=true` EXIT 0
- [x] `python harness/scripts/verify.py --phase P-004` → VERIFY PASS
- [x] `python harness/scripts/harness_check.py` → HARNESS_CHECK PASS
- [x] `python harness/scripts/branch_check.py` → BRANCH_CHECK PASS
- [x] Version 7.4.5.Final unchanged
- [x] No dialect behavior rewrite by test role
