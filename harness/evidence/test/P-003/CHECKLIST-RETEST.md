# P-003 test checklist RETEST (`test-p003-retest-20260714`)

- [x] Maven PATH prepended (`apache-maven-3.9.9`)
- [x] `mvn -q test` exit 0 (IT gated/skipped offline; 6 skipped)
- [x] `mvn -q test -Dxugu.run.integration=true` exit 0 on **real** XuguDB (6 IT)
- [x] `verify.py --phase P-003 --evidence .../verification-retest.json` → `VERIFY PASS`
- [x] A-TYP-009: SchemaExport DDL uses bare `binary` (not `binary(n)`)
- [x] Unit asserts BINARY/VARBINARY → `"binary"`
- [x] Flow `type-mapping-roundtrip-real-db` evidenced
- [x] Flow `ddl-generation-matches-xugu-docs` evidenced (incl. binary probe)
- [x] TEST-REPORT-RETEST + handoff written; invocation `test-p003-retest-20260714`
