# P-008 Test Checklist (RP-02)

**Invocation:** `test-p008-20260715`

- [x] Independent test context (no product code changes)
- [x] `mvn -q test` PASS (IT skipped when gate off)
- [x] `mvn -q test -Dxugu.run.integration=true` PASS on real XuguDB
- [x] `python harness/scripts/verify.py --phase P-008 --evidence harness/evidence/test/P-008/verification.json` → VERIFY PASS
- [x] Observed flow: explicit-dialect-config
- [x] Observed flow: spi-dialect-resolver-autodetect
- [x] Spot-check: jar `META-INF/services/...DialectResolver` → `XuguDialectResolver`
- [x] Spot-check: non-Xugu non-match (MySQL/Oracle/PostgreSQL → null)
- [x] Spot-check: no READ UNCOMMITTED claim (A-XCUT-006)
- [x] Spot-check: `HIB_P008_*` leftover probe = 0
- [x] TEST-REPORT + handoff written; RP-02 `passed` / `test-p008-20260715`
