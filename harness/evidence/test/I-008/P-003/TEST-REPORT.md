# TEST-REPORT — I-008 P-003 RP-02

- **invocation_id:** inv-i008-p003-rp02-test
- **branch:** feat/i-008-production-quality-gaps
- **HEAD:** 079ab84
- **completed_at:** 2026-07-20T16:48:40+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| package | `mvn -q -DskipTests package` | 0 | PASS |
| test offline | `mvn -q test` (XUGU_RUN_IT unset) | 0 | PASS |
| test live | `XUGU_RUN_IT=true mvn -q test` | 1 | SKIPPED_INFRA |
| verify | `python harness/scripts/verify.py --phase P-003` (Maven on PATH) | 0 | VERIFY PASS |

## Offline summary

- dialect: 189 run / 0 fail / 0 error / 66 skipped (excludes stale `XuguTrackCProbeIT` report)
- demo-spring-boot: 32 run / 0 fail / 0 error / 26 skipped

## P-003 Batch A gated IT (offline)

All four suites skipped without `XUGU_RUN_IT` (expected): CastPattern, DefaultColumnExport, IdentifierFolding, TypeRoundTrip.

## Live IT

`127.0.0.1:5138` unreachable (`live-db-probe.txt`). Live reactor failed with JDBC *Connection refused*; Batch A promotions not validated on real DB in this run.

## Artifacts

- `harness/evidence/test/I-008/P-003/verification.json`
- `harness/evidence/test/I-008/P-003/mvn-package-offline.txt`
- `harness/evidence/test/I-008/P-003/mvn-test-offline.txt`
- `harness/evidence/test/I-008/P-003/mvn-test-live-it-final.txt`
- `harness/evidence/test/I-008/P-003/surefire-summary-offline.txt`
- `harness/evidence/test/I-008/P-003/surefire-summary-live-batch-a.txt`
- `harness/evidence/test/I-008/P-003/IT-RESULT.txt`
- `harness/evidence/test/I-008/P-003/live-db-probe.txt`
- `harness/evidence/test/I-008/P-003/verify.txt`
