# P-003 test checklist (`test-p003-20260714`)

- [x] Maven PATH prepended (`apache-maven-3.9.9`)
- [x] `mvn -q -DskipTests package` exit 0
- [x] `mvn -q test` exit 0 (IT gated/skipped offline)
- [x] `mvn -q test -Dxugu.run.integration=true` exit 0 on **real** XuguDB
- [x] `verify.py --phase P-003` → `VERIFY PASS`
- [x] Spot: `XuguDialect` does not extend MySQL/Oracle
- [x] Spot: TIMESTAMP chosen (not DATETIME)
- [x] Spot: IT gate default off / ON via property
- [x] Flow `type-mapping-roundtrip-real-db` evidenced
- [x] Flow `ddl-generation-matches-xugu-docs` evidenced
- [x] TEST-REPORT + handoff written; RP-03 `passed` / `test-p003-20260714`
