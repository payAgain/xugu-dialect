# TEST-REPORT — I-010 P-002 RP-02

- **invocation_id:** inv-i010-p002-rp02-test
- **branch:** `feat/i-010-orm-hql-quality-completion`
- **completed_at:** 2026-07-21T15:05:00+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| test offline | `mvn -q -pl dialect -Dtest=XuguIntervalTypeTest,XuguIntervalTypeIT test` (`XUGU_RUN_IT` unset) | 0 | **PASS** |
| live INTERVAL IT | `XUGU_RUN_IT=true` + `XUGU_JDBC_URL=jdbc:xugu://192.168.2.239:5138/...` + same `-Dtest` | 1 | **SKIPPED_INFRA** |
| branch_check | `python harness/scripts/branch_check.py` | 0 | **PASS** |

## P-002 focus (A-TYP-014 INTERVAL entity ORM)

| Class | Run | Fail | Error | Skip |
|-------|-----|------|-------|------|
| `XuguIntervalTypeTest` | 4 | 0 | 0 | 0 |
| `XuguIntervalTypeIT` (offline gate) | 2 | 0 | 0 | 2 |

Offline unit anchors:

- `intervalTypeHooksWired_A_TYP_014`
- `allDocumentedSubtypesLocked_A_TYP_014`
- `intervalJdbcTypesContributed_A_TYP_014`
- `intervalJdbcTypeFormatParseRoundTrip_A_TYP_014`

IT anchors (pending live DB):

- `XuguIntervalTypeIT#intervalNativeRoundTrip_A_TYP_014`
- `XuguIntervalTypeIT#intervalEntityOrmRoundTrip_A_TYP_014`

## Live INTERVAL IT

`192.168.2.239:5138` unreachable (`live-db-probe.txt`: `TcpTestSucceeded=False`). With gate ON, both IT methods failed on JDBC *Connection refused* (E50025) before round-trip. **SKIPPED_INFRA** — not a product assertion failure.

## Observed flow

- **interval-entity-orm-roundtrip:** offline green; entity ORM IT written; live not validated (infra down)

## Artifacts

- `mvn-test-offline.txt`
- `mvn-test-live-interval-it.txt` (if captured)
- `live-db-probe.txt`
- `IT-RESULT.txt`
