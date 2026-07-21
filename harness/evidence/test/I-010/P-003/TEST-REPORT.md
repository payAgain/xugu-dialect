# TEST-REPORT — I-010 P-003 RP-02

- **invocation_id:** inv-i010-p003-rp02-test
- **branch:** `feat/i-010-orm-hql-quality-completion`
- **completed_at:** 2026-07-21T15:16:00+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| test offline | `mvn -q -pl dialect -Dtest=XuguXmlTypeTest,XuguXmlTypeAndFunctionsIT test` (`XUGU_RUN_IT` unset) | 0 | **PASS** |
| live XML IT | `XUGU_RUN_IT=true` + `XUGU_JDBC_URL=jdbc:xugu://192.168.2.239:5138/...` + `-Dtest=XuguXmlTypeAndFunctionsIT` | 1 | **SKIPPED_INFRA** |
| branch_check | `python harness/scripts/branch_check.py` | 0 | **PASS** |

## P-003 focus (A-TYP-016 XML entity ORM)

| Class | Run | Fail | Error | Skip |
|-------|-----|------|-------|------|
| `XuguXmlTypeTest` | 4 | 0 | 0 | 0 |
| `XuguXmlTypeAndFunctionsIT` (offline gate) | 3 | 0 | 0 | 3 |

Offline unit anchors:

- `xmlTypeHooksWired_A_TYP_016`
- `documentedXmlConstantsLocked_A_TYP_016`
- `xmlJdbcTypeContributed_A_TYP_016`
- `xmlJdbcTypeNormalize_A_TYP_016`

IT anchors (pending live DB):

- `XuguXmlTypeAndFunctionsIT#xmlTypeNativeRoundTrip_A_TYP_016`
- `XuguXmlTypeAndFunctionsIT#xmlEntityOrmRoundTrip_A_TYP_016`
- `XuguXmlTypeAndFunctionsIT#xmlFunctionsNativeSubset_A_FUN_021` (retained; not P-003 focus)

## Live XML IT

`192.168.2.239:5138` unreachable (`live-db-probe.txt`: `TcpTestSucceeded=False`). With gate ON, all three IT methods failed on JDBC *Connection refused* (E50025) before round-trip. **SKIPPED_INFRA** — not a product assertion failure.

## Path

- **Positive** entity ORM (`XuguXmlJdbcType` string bind) — not honest-negative
- Recommended mapping documented in SSOT + implementer ACCEPTANCE

## Observed flow

- **xml-entity-sqlxml-roundtrip:** offline green; entity ORM IT written; live not validated (infra down)

## Artifacts

- `mvn-test-offline.txt`
- `mvn-test-live-xml-it.txt`
- `live-db-probe.txt`
- `IT-RESULT.txt`
