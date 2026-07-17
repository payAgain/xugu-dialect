# P-002 Test Checklist (I-004 / RP-02)

- [x] Branch `fix/i-004-sequence-drop-identity-reserved`
- [x] `mvn -q -DskipTests package` EXIT 0
- [x] `mvn -q test` EXIT 0
- [x] `mvn -q test -Dxugu.run.integration=true` EXIT 0
- [x] `XuguReservedIdentityIT` PASS (1/1) on live XuguDB
- [x] Observed `insert into "order"` + `select last_insert_id() from dual` (no unexpected ORDER)
- [x] Unit `getDefaultUseGetGeneratedKeys=false` covered in `XuguIdentitySequenceTest`
- [x] `python harness/scripts/verify.py --phase P-002 --evidence harness/evidence/test/P-002/verification.json` → VERIFY PASS EXIT 0
- [x] `python harness/scripts/harness_check.py` EXIT 0
- [x] `python harness/scripts/branch_check.py` EXIT 0
- [x] Evidence under `harness/evidence/test/P-002/**`
- [x] Handoff `harness/handoffs/test/P-002.yaml` (test-p002-20260717, passed)
- [x] No git commit / Accept / business code by test
