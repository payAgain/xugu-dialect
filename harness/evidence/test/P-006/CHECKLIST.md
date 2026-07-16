# P-006 Test Checklist (RP-02)

- [x] Authorization: I-003 / B-006 / P-006
- [x] Branch `feat/i-003-production-capability-parity` (branch_check PASS)
- [x] `mvn -q -DskipTests package` (exit 0)
- [x] `mvn -q test` offline (exit 0 after test-only assert fix)
- [x] `mvn -q test "-Dxugu.run.integration=true"` on live XuguDB (exit 0)
- [x] `verify.py --phase P-006` → VERIFY PASS
- [x] `harness_check.py` PASS
- [x] `branch_check.py` PASS
- [x] Evidence logs under `harness/evidence/test/P-006/`
- [x] Handoff `harness/handoffs/test/P-006.yaml` updated
- [x] No git commit / no Accept by test role
- [x] C-DDL-001 IF NOT EXISTS (unit + IT)
- [x] C-DDL-002 ALTER COLUMN type (unit + IT)
- [x] C-DDL-003 datetime literals/format (unit + IT)
- [x] C-DDL-004 enum null (unit)
- [x] C-DDL-005 skipped (deferred)
- [x] C-CAT-001 catalog create/drop (unit + IT)
- [x] C-GUID-001 select sys_guid() (unit + IT)
