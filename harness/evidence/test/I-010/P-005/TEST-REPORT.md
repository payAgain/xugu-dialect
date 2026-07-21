# TEST-REPORT — I-010 P-005 RP-02

- **invocation_id:** inv-i010-p005-rp02-test
- **branch:** `feat/i-010-orm-hql-quality-completion`
- **completed_at:** 2026-07-21T15:40:00+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| test offline | `mvn -q -pl dialect -Dtest=XuguFunctionRegistryTest#xmlSubsetRegistered_A_FUN_021,XuguXmlTypeTest,XuguXmlTypeAndFunctionsIT test` (`XUGU_RUN_IT` unset) | 0 | **PASS** |
| package | `mvn -q -pl dialect -DskipTests package` | 0 | **PASS** |
| live XML HQL IT | gate ON + live JDBC | n/a | **SKIPPED_INFRA** (TCP :5138 refused; IT not attempted) |
| branch_check | `python harness/scripts/branch_check.py` | 0 | **PASS** |

## P-005 focus (A-FUN-021 HQL Session)

| Class | Run | Fail | Error | Skip |
|-------|-----|------|-------|------|
| `XuguFunctionRegistryTest#xmlSubsetRegistered_A_FUN_021` | 1 | 0 | 0 | 0 |
| `XuguXmlTypeTest` | 4 | 0 | 0 | 0 |
| `XuguXmlTypeAndFunctionsIT` (offline gate) | 4 | 0 | 0 | 4 |

Offline unit anchors:

- `xmlSubsetRegistered_A_FUN_021` — includes `PatternBasedSqmFunctionDescriptor` for `xmlquery`
- `xmlTypeHooksWired_A_TYP_016` / JDBC contribute / constants / normalize (retained)

IT anchors (pending live DB):

- `XuguXmlTypeAndFunctionsIT#xmlFunctionsHqlSession_A_FUN_021` (**new** — HQL Session `xmlelement`/`xmlquery`)
- `XuguXmlTypeAndFunctionsIT#xmlFunctionsNativeSubset_A_FUN_021` (retained; XMLTABLE empty→assumption skip)
- entity/native A-TYP-016 methods retained

## Live XML HQL IT

`192.168.2.239:5138` and `127.0.0.1:5138` unreachable (`live-db-probe.txt`: `TcpTestSucceeded=False`). Live IT not run. **SKIPPED_INFRA** — not a product assertion failure. Do **not** claim covered-live for XMLTABLE.

## SSOT disposition (A-FUN-021)

- **status:** **known-limit-documented** (unchanged)
- **reason:** XMLTABLE single-node / cluster empty → assumption skip; never hard-fail; never covered-live for XMLTABLE
- **HQL Session:** code path present for `xmlelement`/`xmlquery`; live proof pending infra

## Observed flow

- **xml-hql-session-live:** offline green; HQL IT written; live not validated (infra down)

## Artifacts

- `mvn-test-offline.txt`
- `mvn-test-live-xml-hql-it.txt`
- `live-db-probe.txt`
- `IT-RESULT.txt`
