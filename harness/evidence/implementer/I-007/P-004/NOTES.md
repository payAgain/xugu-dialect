# P-004 Implementer NOTES — Track C (I-007)

> **Invocation:** `inv-i007-p004-rp01-implementer` · I-007 / B-001 / P-004 / RP-01  
> **Branch:** `feat/i-007-capability-hardening-abc`

## Delivered themes

| Theme | matrix_ids | Implementation | Live IT |
|---|---|---|---|
| JSON subset deepen | C-JSON-005 | `json_unquote`, `json_length`, `json_type` in `XuguFunctionRegistrations` | `XuguJsonSubsetDeepenIT` |
| ARRAY | A-TYP-015, C-DDL-005 | `supportsStandardArrays`, `getPreferredSqlTypeCodeForArray`, `supportsArrayConstructor` | `XuguArrayTypeIT` (native ARRAY round-trip) |
| ALTER SEQUENCE | A-SEQ-006 | `getRestartSequenceString` → `START WITH` (not RESTART WITH); `alterSequenceIncrementBy` helper | `XuguAlterSequenceIT` |

## Doc basis

See [`docs/p004-track-c-capabilities.md`](../../../docs/p004-track-c-capabilities.md) — cites `reference/function/json-functions/**`, `reference/sql/datatype/array.md`, `reference/object/sequence.md`.

## Live probes (key findings)

| SQL | Result |
|---|---|
| `ALTER SEQUENCE … RESTART WITH` | **Fail E19132** — not emitted |
| `ALTER SEQUENCE … START WITH` | **Pass** |
| `ALTER SEQUENCE … INCREMENT BY` | **Pass** |
| `INTEGER ARRAY` / `ARRAY[…]` | **Pass** |
| JDBC `createArrayOf` | **Unsupported** — IT uses native SQL for ARRAY acceptance |
| `json_type(?, '$.a')` bind | **Fail E10049** — IT uses literal for json_type |

## Commands + results

| Command | Exit | Notes |
|---|---|---|
| `mvn -q test` (offline, gate off) | 0 | full reactor green |
| `XUGU_RUN_IT=true mvn -q -pl dialect test -Dtest=XuguJsonSubsetDeepenIT,XuguArrayTypeIT,XuguAlterSequenceIT` | 0 | 3/3 PASS |

Live log: `harness/evidence/test/I-007/P-004/mvn-test-live-it.txt`

## SSOT updates

- `contracts/production-regression-baseline.md` — 4 rows promoted to covered-live
- `contracts/feature-matrix-definition-a.md` — A-TYP-015, A-SEQ-006
- `contracts/feature-matrix-i003-ruler-c.md` — C-JSON-005, C-DDL-005
- `contracts/i007-capability-hardening-plan.md` — Track C gap map closed

## Not done (by design)

- No git commit / Ship
- P-003 folded items absorbed (same four matrix ids)
- Full `XuguJsonFunctions` registry — out of scope
