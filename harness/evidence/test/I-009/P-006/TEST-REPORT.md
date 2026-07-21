# TEST-REPORT — I-009 P-006 RP-02

- **invocation_id:** inv-i009-p006-rp02-test
- **branch:** feat/i-009-deferred-matrix-delivery
- **HEAD:** 5ed442a
- **completed_at:** 2026-07-21T10:47:10+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| package | mvn -q -DskipTests package | 0 | PASS |
| test offline | mvn -q test (XUGU_RUN_IT unset) | 0 | PASS |
| P-006 focus | mvn -q -pl dialect -am test -Dtest=XuguFunctionRegistryTest,XuguRegexpAndBitFunctionsIT | 0 | PASS |
| live regexp/bit IT | XUGU_RUN_IT=true + -Dtest=XuguRegexpAndBitFunctionsIT | 1 | SKIPPED_INFRA |

## P-006 focus (A-FUN-019 + A-FUN-015)

| Class / anchor | Run | Fail | Error | Skip |
|----------------|-----|------|-------|------|
| XuguFunctionRegistryTest | 9 | 0 | 0 | 0 |
| XuguRegexpAndBitFunctionsIT | 2 | 0 | 0 | 2 (offline gate) |

Offline unit anchors: 
egexpSubsetRegistered_A_FUN_019, itAggregateRegistered_A_FUN_015.

## Full offline reactor (mvn -q test)

- **dialect:** 184 run / 0 fail / 0 error / 60 skipped (IT gated offline)
- **demo-spring-boot:** 36 run / 0 fail / 0 error / 28 skipped

## Live regexp/bit IT

127.0.0.1:5138 unreachable (IT-RESULT.txt: JDBC Connection refused E50025). With XUGU_RUN_IT=true, both IT methods fail on JDBC connect before native SQL runs. **SKIPPED_INFRA** — not a product assertion failure. Required offline gates passed in this invocation.

IT anchors (pending live DB):

- com.xugu.dialect.it.XuguRegexpAndBitFunctionsIT#regexpFunctionsNativeSubset_A_FUN_019
- com.xugu.dialect.it.XuguRegexpAndBitFunctionsIT#bitAggregatesNativeSubset_A_FUN_015

## Observed flow

- **i009-a-fun-019-015-regexp-and-bit-aggregates:** offline registry unit + gated IT skip green; live native subset not validated (infra down)

## Verdict

**PASS** — required mvn -q -DskipTests package + mvn -q test exit 0; optional live IT blocked by infrastructure.

## Artifacts

- harness/evidence/test/I-009/P-006/verification.json
- harness/evidence/test/I-009/P-006/TEST-REPORT.md
- harness/evidence/test/I-009/P-006/mvn-package.txt
- harness/evidence/test/I-009/P-006/mvn-test.txt
- harness/evidence/test/I-009/P-006/mvn-test-p006-focus-offline.txt
- harness/evidence/test/I-009/P-006/mvn-test-live-regexp-bit-it.txt
- harness/evidence/test/I-009/P-006/IT-RESULT.txt
