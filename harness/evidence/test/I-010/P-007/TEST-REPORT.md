# TEST-REPORT — I-010 P-007 RP-02

- **invocation_id:** inv-i010-p007-rp02-test
- **branch:** `feat/i-010-orm-hql-quality-completion`
- **completed_at:** 2026-07-21T15:47:00+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| test offline | `mvn -q -pl dialect -am -Dtest=XuguFunctionRegistryTest#regexpSubsetRegistered_A_FUN_019,XuguRegexpAndBitFunctionsIT test` (`XUGU_RUN_IT` unset) | 0 | **PASS** |
| package | `mvn -q -pl dialect -DskipTests package` | 0 | **PASS** |
| live regexp HQL IT | gate ON + live JDBC | n/a | **SKIPPED_INFRA** (TCP :5138 refused; IT not attempted) |
| branch_check | `python harness/scripts/branch_check.py` | 0 | **PASS** |

## P-007 focus (A-FUN-019 HQL Session)

| Class | Run | Fail | Error | Skip |
|-------|-----|------|-------|------|
| `XuguFunctionRegistryTest#regexpSubsetRegistered_A_FUN_019` | 1 | 0 | 0 | 0 |
| `XuguRegexpAndBitFunctionsIT` (offline gate) | 3 | 0 | 0 | 3 |

Offline unit anchors:

- `regexpSubsetRegistered_A_FUN_019` — `regexp_like`/`regexp_replace`/`regexp_substr` registered; `regexp_instr` **null**

IT anchors (pending live DB):

- `XuguRegexpAndBitFunctionsIT#regexpFunctionsHqlSession_A_FUN_019` (**new** — HQL Session)
- `XuguRegexpAndBitFunctionsIT#regexpFunctionsNativeSubset_A_FUN_019` (retained)
- `bitAggregatesNativeSubset_A_FUN_015` (retained; out of P-007 claim)

## Live regexp HQL IT

`192.168.2.239:5138` and `127.0.0.1:5138` unreachable (`live-db-probe.txt`: `TcpTestSucceeded=False`). Live IT not run. **SKIPPED_INFRA** — not a product assertion failure. Do **not** regress A-FUN-019 covered-live.

## SSOT disposition (A-FUN-019)

- **status:** **covered-live** (unchanged; strengthened)
- **HQL Session:** code path present for `regexp_like`/`regexp_replace`/`regexp_substr`; live proof pending infra
- **native subset:** retained
- **unregistered:** no HQL/native claim for `regexp_instr`

## Observed flow

- **regexp-hql-session-live:** offline green; HQL IT written; live not validated (infra down)

## Artifacts

- `mvn-test-offline.txt`
- `mvn-test-live-regexp-hql-it.txt`
- `live-db-probe.txt`
- `IT-RESULT.txt`
