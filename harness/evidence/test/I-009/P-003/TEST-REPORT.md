# TEST-REPORT — I-009 P-003 RP-02

- **invocation_id:** inv-i009-p003-rp02-test
- **branch:** feat/i-009-deferred-matrix-delivery
- **HEAD:** acb3488
- **completed_at:** 2026-07-21T10:16:08+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| package | `mvn -q -DskipTests package` | 0 | PASS |
| test offline | `mvn -q test` (`XUGU_RUN_IT` unset) | 0 | PASS |
| live XML IT | `XUGU_RUN_IT=true` + `XUGU_JDBC_URL=jdbc:xugu://192.168.2.239:5138/...` + `mvn -q test -Dtest=XuguXmlTypeAndFunctionsIT` | 1 | SKIPPED_INFRA |

## P-003 focus (A-TYP-016 XML + A-FUN-021 XML functions)

| Class | Run | Fail | Error | Skip |
|-------|-----|------|-------|------|
| `XuguXmlTypeTest` | 2 | 0 | 0 | 0 |
| `XuguXmlTypeAndFunctionsIT` | 2 | 0 | 0 | 2 (offline gate) |

Offline unit anchors: `xmlTypeHooksWired_A_TYP_016`, `documentedXmlConstantsLocked_A_TYP_016`.

## Full offline reactor

- **dialect:** 175 run / 0 fail / 0 error / 59 skipped (IT gated offline)
- **demo-spring-boot:** 36 run / 0 fail / 0 error / 28 skipped

## Live XML IT

`192.168.2.239:5138` unreachable (`live-db-probe.txt`: `TcpTestSucceeded=False`). With `XUGU_RUN_IT=true`, both IT methods failed on JDBC *Connection refused* (E50025) before native SQL could run. **SKIPPED_INFRA** — not a product assertion failure.

IT anchors (pending live DB):

- `com.xugu.dialect.it.XuguXmlTypeAndFunctionsIT#xmlTypeNativeRoundTrip_A_TYP_016`
- `com.xugu.dialect.it.XuguXmlTypeAndFunctionsIT#xmlFunctionsNativeSubset_A_FUN_021`

## Observed flow

- **i009-a-typ-016-xml-and-a-fun-021-dialect-and-live-it:** offline XML unit tests green; gated IT skip green; live native round-trip not validated (infra down)

## Verdict

**PASS** — required build + offline test green; optional live IT blocked by infrastructure.

## Artifacts

- `harness/evidence/test/I-009/P-003/verification.json`
- `harness/evidence/test/I-009/P-003/TEST-REPORT.md`
- `harness/evidence/test/I-009/P-003/mvn-package.txt`
- `harness/evidence/test/I-009/P-003/mvn-test-offline.txt`
- `harness/evidence/test/I-009/P-003/mvn-test-p003-focus-offline.txt`
- `harness/evidence/test/I-009/P-003/mvn-test-live-xml-it.txt`
- `harness/evidence/test/I-009/P-003/live-db-probe.txt`
- `harness/evidence/test/I-009/P-003/IT-RESULT.txt`
