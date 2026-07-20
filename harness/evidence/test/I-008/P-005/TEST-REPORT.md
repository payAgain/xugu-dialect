# TEST-REPORT — I-008 P-005 RP-02

- **invocation_id:** inv-i008-p005-rp02-test
- **branch:** feat/i-008-production-quality-gaps
- **HEAD:** 54eaf6e
- **completed_at:** 2026-07-20T17:21:30+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| package | `mvn -q -DskipTests package` | 0 | PASS |
| lock suite | `mvn -q -pl dialect -am test -Dtest=XuguLockSemanticsTest,XuguLockIT,XuguPaginationLockTest,XuguNegativeRegressionBaselineTest` | 0 | PASS |
| test offline | `mvn -q test` (XUGU_RUN_IT unset) | 0 | PASS |
| live lock IT | `XUGU_RUN_IT=true mvn -q -pl dialect -am test -Dtest=XuguLockIT` | 1 | SKIPPED_INFRA |
| verify | `python harness/scripts/verify.py --phase P-005` (Maven on PATH) | 0 | VERIFY PASS |

## Lock-focused summary (offline)

| Class | Run | Fail | Error | Skip |
|-------|-----|------|-------|------|
| `XuguLockSemanticsTest` | 24 | 0 | 0 | 0 |
| `XuguPaginationLockTest` | 10 | 0 | 0 | 0 |
| `XuguNegativeRegressionBaselineTest` | 29 | 0 | 0 | 18 |
| `XuguLockIT` | 3 | 0 | 0 | 3 (gated) |

Negative lock anchors in baseline suite: `skipLockedNotSupported_A_LCK_004_C_SKIP_001`, `forShareNotSupported_A_LCK_005`.

## Full offline reactor

- dialect: 214 run / 0 fail / 0 error / 67 skipped (excludes stale `XuguTrackCProbeIT` report)
- demo-spring-boot: 32 run / 0 fail / 0 error / 26 skipped

## Live lock IT

`127.0.0.1:5138` unreachable (`live-db-probe.txt`). With `XUGU_RUN_IT=true`, all three `XuguLockIT` methods failed fast on JDBC *Connection refused* (infra), including `pessimisticReadExecutesAsForUpdateNotShare`. Real-DB lock execution not validated this run.

## Observed flow

- **i008-q2-lock-semantics-behavior-and-negative-evidence:** partial — offline behavioral + negative lock suite green; live lock IT SKIPPED_INFRA

## Artifacts

- `harness/evidence/test/I-008/P-005/verification.json`
- `harness/evidence/test/I-008/P-005/TEST-REPORT.md`
- `harness/evidence/test/I-008/P-005/mvn-package-offline.txt`
- `harness/evidence/test/I-008/P-005/mvn-test-lock-focused.txt`
- `harness/evidence/test/I-008/P-005/mvn-test-offline.txt`
- `harness/evidence/test/I-008/P-005/mvn-test-live-lock-it.txt`
- `harness/evidence/test/I-008/P-005/surefire-summary-lock-focused.txt`
- `harness/evidence/test/I-008/P-005/IT-RESULT.txt`
- `harness/evidence/test/I-008/P-005/live-db-probe.txt`
- `harness/evidence/test/I-008/P-005/verify.txt`
- `harness/evidence/test/I-008/P-005/verification-verifypy.json`
