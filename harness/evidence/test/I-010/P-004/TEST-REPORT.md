# TEST-REPORT — I-010 P-004 RP-02

- **invocation_id:** inv-i010-p004-rp02-test
- **branch:** `feat/i-010-orm-hql-quality-completion`
- **completed_at:** 2026-07-21T15:30:00+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| test offline | `mvn -q -pl dialect -Dtest=XuguGeometricTypeTest,XuguGeometricTypeAndFunctionsIT test` (`XUGU_RUN_IT` unset) | 0 | **PASS** |
| live geometric IT | `XUGU_RUN_IT=true` + `XUGU_JDBC_URL=jdbc:xugu://192.168.2.239:5138/...` + `-Dtest=XuguGeometricTypeAndFunctionsIT` | 1 | **SKIPPED_INFRA** |
| branch_check | `python harness/scripts/branch_check.py` | 0 | **PASS** |

## P-004 focus (A-TYP-017 POINT entity ORM)

| Class | Run | Fail | Error | Skip |
|-------|-----|------|-------|------|
| `XuguGeometricTypeTest` | 4 | 0 | 0 | 0 |
| `XuguGeometricTypeAndFunctionsIT` (offline gate) | 3 | 0 | 0 | 3 |

Offline unit anchors:

- `pointTypeHooksWired_A_TYP_017`
- `allDocumentedKindsLocked_A_TYP_017`
- `pointJdbcTypesContributed_A_TYP_017`
- `pointJdbcTypeNormalize_A_TYP_017`

IT anchors (pending live DB):

- `XuguGeometricTypeAndFunctionsIT#geometricTypesNativeRoundTrip_A_TYP_017` (retained)
- `XuguGeometricTypeAndFunctionsIT#pointEntityOrmRoundTrip_A_TYP_017`
- `XuguGeometricTypeAndFunctionsIT#geometricFunctionsNativeSubset_A_FUN_020` (retained; not P-004 focus)

## Live POINT IT

`192.168.2.239:5138` and `127.0.0.1:5138` unreachable (`live-db-probe.txt`: `TcpTestSucceeded=False`). With gate ON, all three IT methods failed on JDBC *Connection refused* (E50025) before round-trip. **SKIPPED_INFRA** — not a product assertion failure.

## Path

- **Positive** entity ORM (`XuguPointJdbcType` string bind) — not honest-negative
- Recommended mapping: `String` + `@JdbcTypeCode(SqlTypes.POINT)` (GEOMETRY→POINT alias supported)
- Non-POINT subtypes (LINE…CIRCLE) remain native/tooling — not forced into ORM

## Observed flow

- **point-entity-orm-roundtrip:** offline green; entity ORM IT written; live not validated (infra down)

## Artifacts

- `mvn-test-offline.txt`
- `mvn-test-live-point-it.txt`
- `live-db-probe.txt`
- `IT-RESULT.txt`
