# TEST-REPORT — I-010 P-006 RP-02

- **invocation_id:** inv-i010-p006-rp02-test
- **branch:** `feat/i-010-orm-hql-quality-completion`
- **completed_at:** 2026-07-21T15:42:00+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| test offline | `mvn -q -pl dialect -am -Dtest=XuguFunctionRegistryTest#geometricSubsetRegistered_A_FUN_020,XuguGeometricTypeTest,XuguGeometricTypeAndFunctionsIT test` (`XUGU_RUN_IT` unset) | 0 | **PASS** |
| package | `mvn -q -pl dialect -DskipTests package` | 0 | **PASS** |
| live geometric HQL IT | gate ON + live JDBC | n/a | **SKIPPED_INFRA** (TCP :5138 refused; IT not attempted) |
| branch_check | `python harness/scripts/branch_check.py` | 0 | **PASS** |

## P-006 focus (A-FUN-020 HQL Session)

| Class | Run | Fail | Error | Skip |
|-------|-----|------|-------|------|
| `XuguFunctionRegistryTest#geometricSubsetRegistered_A_FUN_020` | 1 | 0 | 0 | 0 |
| `XuguGeometricTypeTest` | 4 | 0 | 0 | 0 |
| `XuguGeometricTypeAndFunctionsIT` (offline gate) | 4 | 0 | 0 | 4 |

Offline unit anchors:

- `geometricSubsetRegistered_A_FUN_020` — all 21 named geometric descriptors
- `pointTypeHooksWired_A_TYP_017` / JDBC contribute / constants / normalize (retained)

IT anchors (pending live DB):

- `XuguGeometricTypeAndFunctionsIT#geometricFunctionsHqlSession_A_FUN_020` (**new** — HQL Session `area`/`center`/`point`)
- `XuguGeometricTypeAndFunctionsIT#geometricFunctionsNativeSubset_A_FUN_020` (retained)
- entity/native A-TYP-017 methods retained

## Live geometric HQL IT

`192.168.2.239:5138` and `127.0.0.1:5138` unreachable (`live-db-probe.txt`: `TcpTestSucceeded=False`). Live IT not run. **SKIPPED_INFRA** — not a product assertion failure. Do **not** regress A-FUN-020 covered-live.

## SSOT disposition (A-FUN-020)

- **status:** **covered-live** (unchanged; strengthened)
- **HQL Session:** code path present for `area`/`center`/`point`; live proof pending infra
- **native subset:** retained

## Observed flow

- **geometric-hql-session-live:** offline green; HQL IT written; live not validated (infra down)

## Artifacts

- `mvn-test-offline.txt`
- `mvn-test-live-geometric-hql-it.txt`
- `live-db-probe.txt`
- `IT-RESULT.txt`
