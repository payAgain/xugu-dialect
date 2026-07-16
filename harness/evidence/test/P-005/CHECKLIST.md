# P-005 Test Checklist (RP-02)

- [x] Authorization: I-003 / B-005 / P-005
- [x] Branch `feat/i-003-production-capability-parity` (branch_check PASS)
- [x] `mvn -q -DskipTests package` (exit 0)
- [x] `mvn -q test` offline (exit 0)
- [x] `mvn -q test "-Dxugu.run.integration=true"` on live XuguDB (exit 0)
- [x] `verify.py --phase P-005` → VERIFY PASS
- [x] `harness_check.py` PASS
- [x] `branch_check.py` PASS
- [x] Evidence logs under `harness/evidence/test/P-005/`
- [x] Handoff `harness/handoffs/test/P-005.yaml` updated
- [x] No git commit / no Accept by test role
- [x] C-BULK-001 JOINED bulk update/delete IT executed (gate ON)
- [x] C-BULK-003 `supportsSubqueryOnMutatingTable=false` (unit + IT)
- [x] C-BULK-002 documented N/A (no bulk insert IT)
