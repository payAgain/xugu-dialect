# P-006 Test Report — I-005

> Phase: P-006 | Build: B-001 | Date: 2026-07-17

## Commands

| Step | Command | Exit | Result |
|---|---|---:|---|
| harness_check | `python harness/scripts/harness_check.py` | 0 | PASS |
| build | `mvn -q -DskipTests package` | 0 | PASS |
| test (offline) | `mvn -q test` | 0 | PASS |
| test (live IT) | `XUGU_RUN_IT=true mvn -q test` | 0 | PASS |
| verify | `python harness/scripts/verify.py --phase P-006 --evidence harness/evidence/test/I-005/P-006/verification.json` | 0 | **VERIFY PASS** |

## Live IT gate

- **Status:** PASS (reachable XuguDB at default `127.0.0.1:5138`)
- **Evidence:** `harness/evidence/test/I-005/P-006/mvn-test-live-it-final.log`
- **Demo fixes:** `DemoBootBaselineSmokeTest` SessionFactory unwrap + removed destructive `@AfterAll DROP TABLE` from demo IT classes

## GAV

`com.xugu:xugu-dialect:7.4.5.Final` — unchanged
