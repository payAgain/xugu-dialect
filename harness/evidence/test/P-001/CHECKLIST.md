# P-001 Test Checklist (I-004 / RP-02)

- [x] Branch `fix/i-004-sequence-drop-identity-reserved`
- [x] `mvn -q -DskipTests package` EXIT 0
- [x] `mvn -q test` EXIT 0
- [x] `mvn -q test -Dxugu.run.integration=true` EXIT 0
- [x] `python harness/scripts/verify.py --phase P-001 --evidence harness/evidence/test/P-001/verification.json` → VERIFY PASS EXIT 0
- [x] `python harness/scripts/harness_check.py` EXIT 0
- [x] `python harness/scripts/branch_check.py` EXIT 0
- [x] Observed `drop sequence if exists` on live DB
- [x] Evidence under `harness/evidence/test/P-001/**`
- [x] Handoff `harness/handoffs/test/P-001.yaml` (test-p001-20260717, passed)
- [x] No git commit / Accept / business code by test
