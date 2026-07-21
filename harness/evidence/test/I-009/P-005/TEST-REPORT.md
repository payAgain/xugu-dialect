# TEST-REPORT — I-009 P-005 RP-02

- **invocation_id:** inv-i009-p005-rp02-test
- **branch:** feat/i-009-deferred-matrix-delivery
- **HEAD:** 2e39f40
- **completed_at:** 2026-07-21T10:36:45+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| package | `mvn -q -DskipTests package` | 0 | PASS |
| test offline | `mvn -q test` (`XUGU_RUN_IT` unset) | 0 | PASS |
| P-005 unit focus | `mvn -q -pl dialect -am test -Dtest=XuguUdtTypeTest` | 0 | PASS |
| live UDT IT | `XUGU_RUN_IT=true` + `mvn -q -pl dialect -am test -Dtest=XuguUdtTypeIT` | 1 | SKIPPED_INFRA (prior probe) |

## P-005 focus (A-TYP-018 UDT)

| Class / anchor | Run | Fail | Error | Skip |
|----------------|-----|------|-------|------|
| `XuguUdtTypeTest` | 3 | 0 | 0 | 0 |
| `XuguUdtTypeIT` | 1 | 0 | 0 | 1 (offline gate) |

Offline unit anchors: `documentedKindsLocked_A_TYP_018`, `createTypeSqlMatchesUdtDoc_A_TYP_018`, `dialectDoesNotClaimOrmUdtEntityMapping_A_TYP_018`.

## Full offline reactor (`mvn -q test`)

- **dialect:** 181 run / 0 fail / 0 error / 59 skipped (IT gated offline)
- **demo-spring-boot:** 36 run / 0 fail / 0 error / 28 skipped

## Live UDT IT

`127.0.0.1:5138` unreachable (`IT-RESULT.txt`: JDBC Connection refused E50025). With `XUGU_RUN_IT=true`, IT fails before native SQL can run. **SKIPPED_INFRA** — not a product assertion failure. Live probe from implementer run retained; required offline gates passed in this test invocation.

IT anchor (pending live DB):

- `com.xugu.dialect.it.XuguUdtTypeIT#udtNativeRoundTrip_A_TYP_018`

## Observed flow

- **i009-a-typ-018-udt-dialect-and-live-it:** offline UDT unit + gated IT skip green; live native round-trip not validated (infra down)

## Verdict

**PASS** — required `mvn -q -DskipTests package` + `mvn -q test` exit 0; optional live IT blocked by infrastructure.

## Artifacts

- `harness/evidence/test/I-009/P-005/verification.json`
- `harness/evidence/test/I-009/P-005/TEST-REPORT.md`
- `harness/evidence/test/I-009/P-005/mvn-package.txt`
- `harness/evidence/test/I-009/P-005/mvn-test.txt`
- `harness/evidence/test/I-009/P-005/mvn-test-p005-focus-offline.txt`
- `harness/evidence/test/I-009/P-005/mvn-test-live-udt-it.txt`
- `harness/evidence/test/I-009/P-005/IT-RESULT.txt`