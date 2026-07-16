# P-007 Test Checklist (RP-02) — I-003

**Invocation:** `test-p007-20260716`

- [x] Authorization: I-003 / B-007 / P-007
- [x] Branch `feat/i-003-production-capability-parity` (branch_check PASS)
- [x] `mvn -q -DskipTests package` (exit 0)
- [x] `mvn -q test` offline (exit 0; 28 IT skipped via gate)
- [x] `mvn -q test "-Dxugu.run.integration=true"` on live XuguDB (exit 0; 0 skipped)
- [x] `verify.py --phase P-007` — VERIFY PASS
- [x] `harness_check.py` PASS
- [x] `branch_check.py` PASS
- [x] Observed flow: docs-matrix-aligned-full-verify
- [x] Spot-check: docs mention I-003 ruler-C matrix (SSOT + pointers)
- [x] Spot-check: version 7.4.5.Final unchanged
- [x] Evidence under `harness/evidence/test/P-007/`
- [x] Handoff `harness/handoffs/test/P-007.yaml` (passed)
- [x] No product code changes; no git commit; no Accept; no Ship
