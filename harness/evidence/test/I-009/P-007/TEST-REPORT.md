# TEST-REPORT — I-009 P-007 RP-02

- **invocation_id:** inv-i009-p007-rp02-test
- **branch:** feat/i-009-deferred-matrix-delivery
- **HEAD:** fff15f9
- **completed_at:** 2026-07-21T10:57:00+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| package | mvn -q -DskipTests package | 0 | PASS |
| test offline | mvn -q test (XUGU_RUN_IT unset) | 0 | PASS |
| P-007 focus | mvn -q -pl dialect -am test -Dtest=XuguTableDdlExtensionsTest,XuguTableDdlExtensionsIT | 0 | PASS |
| live partition/encrypt IT | XUGU_RUN_IT=true + -Dtest=XuguTableDdlExtensionsIT | 1 | SKIPPED_INFRA |

## P-007 focus (A-DDL-007 + A-DDL-008 + A-DDL-009)

| Class / anchor | Run | Fail | Error | Skip |
|----------------|-----|------|-------|------|
| XuguTableDdlExtensionsTest | 5 | 0 | 0 | 0 |
| XuguTableDdlExtensionsIT | 2 | 0 | 0 | 2 (offline gate) |

Offline unit anchors: ifNotExistsPromotedViaC_DDL_001_A_DDL_007, partitionSqlMatchesPartitionDoc_A_DDL_008, encryptSqlMatchesCreateDoc_A_DDL_009.

## Full offline reactor (mvn -q test)

- **dialect:** 189 run / 0 fail / 0 error / 60 skipped (IT gated offline)
- **demo-spring-boot:** 36 run / 0 fail / 0 error / 28 skipped

## Live partition/encrypt IT

127.0.0.1:5138 unreachable (IT-RESULT.txt: JDBC Connection refused E50025). With XUGU_RUN_IT=true, both IT methods fail on JDBC connect before native DDL runs. **SKIPPED_INFRA** — not a product assertion failure. Required offline gates passed in this invocation.

IT anchors (pending live DB):

- com.xugu.dialect.it.XuguTableDdlExtensionsIT#listPartitionNativeRoundTrip_A_DDL_008
- com.xugu.dialect.it.XuguTableDdlExtensionsIT#encryptByNativeWhenEncryptorAvailable_A_DDL_009

## Observed flow

- **i009-a-ddl-007-008-009-table-ddl-extensions:** offline unit + gated IT skip green; live native PARTITION/ENCRYPT subset not validated (infra down)

## Verdict

**PASS** — required mvn -q -DskipTests package + mvn -q test exit 0; optional live IT blocked by infrastructure.

## Artifacts

- harness/evidence/test/I-009/P-007/verification.json
- harness/evidence/test/I-009/P-007/TEST-REPORT.md
- harness/evidence/test/I-009/P-007/mvn-package.txt
- harness/evidence/test/I-009/P-007/mvn-test.txt
- harness/evidence/test/I-009/P-007/mvn-test-p007-focus-offline.txt
- harness/evidence/test/I-009/P-007/mvn-test-live-ddl-it.txt
- harness/evidence/test/I-009/P-007/IT-RESULT.txt
