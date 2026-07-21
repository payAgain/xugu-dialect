# TEST-REPORT — I-009 P-004 RP-02

- **invocation_id:** inv-i009-p004-rp02-test
- **branch:** feat/i-009-deferred-matrix-delivery
- **HEAD:** acb3488
- **completed_at:** 2026-07-21T10:24:57+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| package | `mvn package` | 0 | PASS |
| test offline | `mvn test` (`XUGU_RUN_IT` unset) | 0 | PASS |
| live geometric IT | `XUGU_RUN_IT=true` + `mvn -q test -Dtest=XuguGeometricTypeAndFunctionsIT -pl dialect -am` | 1 | SKIPPED_INFRA |

## P-004 focus (A-TYP-017 geometric + A-FUN-020 geometric functions)

| Class / anchor | Run | Fail | Error | Skip |
|----------------|-----|------|-------|------|
| `XuguGeometricTypeTest` | 2 | 0 | 0 | 0 |
| `XuguFunctionRegistryTest#geometricSubsetRegistered_A_FUN_020` | 1 | 0 | 0 | 0 |
| `XuguGeometricTypeAndFunctionsIT` | 2 | 0 | 0 | 2 (offline gate) |

Offline unit anchors: `pointTypeHooksWired_A_TYP_017`, `allDocumentedKindsLocked_A_TYP_017`, `geometricSubsetRegistered_A_FUN_020`.

## Full offline reactor (`mvn test`)

- **dialect:** 178 run / 0 fail / 0 error / 59 skipped (IT gated offline)
- **demo-spring-boot:** 36 run / 0 fail / 0 error / 28 skipped

## Live geometric IT

`127.0.0.1:5138` and `192.168.2.239:5138` unreachable (`live-db-probe.txt`: `TcpTestSucceeded=False`). With `XUGU_RUN_IT=true`, both IT methods failed on JDBC *Connection refused* (E50025) before native SQL could run. **SKIPPED_INFRA** — not a product assertion failure.

IT anchors (pending live DB):

- `com.xugu.dialect.it.XuguGeometricTypeAndFunctionsIT#geometricTypesNativeRoundTrip_A_TYP_017`
- `com.xugu.dialect.it.XuguGeometricTypeAndFunctionsIT#geometricFunctionsNativeSubset_A_FUN_020`

## Observed flow

- **i009-a-typ-017-a-fun-020-spatial-types-and-functions:** offline geometric unit tests green; gated IT skip green; live native round-trip not validated (infra down)

## Verdict

**PASS** — required `mvn package` + `mvn test` green; optional live IT blocked by infrastructure.

## Artifacts

- `harness/evidence/test/I-009/P-004/verification.json`
- `harness/evidence/test/I-009/P-004/TEST-REPORT.md`
- `harness/evidence/test/I-009/P-004/mvn-package.txt`
- `harness/evidence/test/I-009/P-004/mvn-test.txt`
- `harness/evidence/test/I-009/P-004/mvn-test-p004-focus-offline.txt`
- `harness/evidence/test/I-009/P-004/mvn-test-live-geometric-it.txt`
- `harness/evidence/test/I-009/P-004/live-db-probe.txt`
- `harness/evidence/test/I-009/P-004/IT-RESULT.txt`
