# I-008 / P-007 Test Report (RP-02)

| Field | Value |
|---|---|
| invocation_id | `inv-i008-p007-rp02-test` |
| phase | P-007 |
| role | test / RP-02 |
| branch | feat/i-008-production-quality-gaps |
| HEAD | 1b2f29b |
| build_id | B-001 |

## Harness prep

- `harness_check`: **PASS** after removing UTF-8 BOM from `harness/evidence/test/I-008/P-004/verification.json`.
- Error text referenced phase P-004; P-004.md had no BOM.

## Required commands

| Step | Command | Exit | Status |
|---|---|---:|---|
| package | `mvn -q -DskipTests package` | 0 | PASS |
| test (offline) | `mvn -q test` | 0 | PASS |
| verify.py | `python harness/scripts/verify.py --phase P-007` | 0 | **VERIFY PASS** |

Maven: `C:\Users\admin\tools\apache-maven-3.9.9\bin\mvn.cmd` (on PATH for verify.py)

## Offline test counts

- dialect: 215 / 0 fail / 0 error / 67 skipped
- demo-spring-boot: 36 / 0 fail / 0 error / 28 skipped

## Optional live full reactor

`XUGU_RUN_IT=true mvn -q test` → exit 1, **SKIPPED_INFRA** (see live-db-probe.txt)

## Verdict

**PASS** offline + verify gate; live IT **SKIPPED_INFRA**.
