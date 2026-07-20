# TEST-REPORT — I-008 P-004 RP-02

- **invocation_id:** inv-i008-p004-rp02-test
- **branch:** feat/i-008-production-quality-gaps
- **HEAD:** 54eaf6e
- **completed_at:** 2026-07-20T17:09:30+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| package | `mvn -q -DskipTests package` | 0 | PASS |
| test offline | `mvn -q test` (XUGU_RUN_IT unset) | 0 | PASS |
| test live | `XUGU_RUN_IT=true mvn -q test` | 1 | SKIPPED_INFRA |
| verify | `python harness/scripts/verify.py --phase P-004` (Maven on PATH) | 0 | VERIFY PASS |

## Offline summary

- dialect: 189 run / 0 fail / 0 error / 66 skipped (excludes stale `XuguTrackCProbeIT` report)
- demo-spring-boot: 32 run / 0 fail / 0 error / 26 skipped

## P-004 Batch B gated IT (offline)

Ruler C + consumer demo anchors skipped without `XUGU_RUN_IT` (expected): ExceptionMapping, JsonAggregate, WindowCte, BulkMutation (live methods), TypeDdlDetails, DemoPersonCrud, DemoBootBaselineSmoke.

## Live IT

`127.0.0.1:5138` unreachable (`live-db-probe.txt`). Live reactor failed with JDBC *Connection refused*; Batch B covered-live tag promotions not validated on real DB in this run.

## Observed flow

- **i008-q1-promote-covered-live-ruler-c-consumer-batch:** partial — offline green; live SKIPPED_INFRA

## Artifacts

- `harness/evidence/test/I-008/P-004/verification.json`
- `harness/evidence/test/I-008/P-004/mvn-package-offline.txt`
- `harness/evidence/test/I-008/P-004/mvn-test-offline.txt`
- `harness/evidence/test/I-008/P-004/mvn-test-live-it-final.txt`
- `harness/evidence/test/I-008/P-004/surefire-summary-offline.txt`
- `harness/evidence/test/I-008/P-004/surefire-summary-live-batch-b.txt`
- `harness/evidence/test/I-008/P-004/IT-RESULT.txt`
- `harness/evidence/test/I-008/P-004/live-db-probe.txt`
- `harness/evidence/test/I-008/P-004/verify.txt`
