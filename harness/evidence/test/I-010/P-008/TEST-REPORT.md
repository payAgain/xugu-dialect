# TEST-REPORT — I-010 P-008 RP-02

- **invocation_id:** inv-i010-p008-rp02-test
- **branch:** `feat/i-010-orm-hql-quality-completion`
- **completed_at:** 2026-07-21T15:52:00+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| test offline | `mvn -q -pl dialect -am -Dtest=XuguFunctionRegistryTest#coreAnsiFunctionsRegistered,XuguBatchAFunctionFamiliesIT test` (`XUGU_RUN_IT` unset) | 0 | **PASS** |
| package | `mvn -q -pl dialect -DskipTests package` | 0 | **PASS** |
| live Batch A HQL IT | gate ON + live JDBC | n/a | **SKIPPED_INFRA** (TCP :5138 refused; IT not attempted) |
| branch_check | `python harness/scripts/branch_check.py` | 0 | **PASS** |

## P-008 focus (independent HQL Session ×5)

| Class / method | Run | Fail | Error | Skip |
|-------|-----|------|-------|------|
| `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered` | 1 | 0 | 0 | 0 |
| `XuguBatchAFunctionFamiliesIT` (offline gate) | 5 | 0 | 0 | 5 |

IT methods (pending live DB):

| matrix_id | method |
|---|---|
| A-FUN-003 | `lengthFamilyHqlSession_A_FUN_003` |
| A-FUN-005 | `trimFamilyHqlSession_A_FUN_005` |
| A-FUN-006 | `replaceLocateHqlSession_A_FUN_006` |
| A-FUN-007 | `coalesceNvlHqlSession_A_FUN_007` |
| A-FUN-009 | `roundTruncHqlSession_A_FUN_009` |

## Live Batch A HQL IT

`192.168.2.239:5138` and `127.0.0.1:5138` unreachable (`live-db-probe.txt`: `TcpTestSucceeded=False`). Live IT not run. **SKIPPED_INFRA** — not a product assertion failure.

## SSOT disposition

- A-FUN-003/005/006/007/009 → **known-limit-documented** (independent HQL IT anchors present; **not** covered-live under SKIPPED_INFRA)
- Promote to covered-live only after live PASS
- Prior inflation in `0b83a3d` reverted (reviewer ACCEPT FAIL fix)

## Observed flow

- **batch-a-function-independent-hql-live:** offline green; five HQL IT methods written; live not validated (infra down)

## Artifacts

- `mvn-test-offline.txt`
- `mvn-package.txt`
- `mvn-test-live-batch-a-hql-it.txt`
- `live-db-probe.txt`
- `IT-RESULT.txt`
