# P-009 Test Checklist (RP-02)

**Invocation:** `test-p009-20260715`

- [x] Independent test context (no product code changes)
- [x] `mvn -q test` PASS (IT skipped when gate off)
- [x] `mvn -q -pl demo-spring-boot -am test -Dxugu.run.integration=true` PASS on real XuguDB
- [x] `python harness/scripts/verify.py --phase P-009 --evidence harness/evidence/test/P-009/verification.json` → VERIFY PASS
- [x] Observed flow: spring-boot-demo-starts-against-real-db
- [x] Observed flow: hibernate-version-forced-745
- [x] Spot-check: `hibernate-core:jar:7.4.5.Final:compile` (dependency:tree)
- [x] Spot-check: env overrides `XUGU_JDBC_URL` / `XUGU_USER` / `XUGU_PASSWORD`; local Charter defaults only
- [x] Spot-check: dialect not implemented inside demo
- [x] TEST-REPORT + handoff written; RP-02 `passed` / `test-p009-20260715`
