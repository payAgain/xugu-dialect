# P-005 NOTES — Track B (implementer RP-01)

> **Invocation:** `inv-i007-p005-rp01-implementer`  
> **Branch:** `feat/i-007-capability-hardening-abc`  
> **GAV:** `7.4.5.Final` · **compatiblemode:** NONE only · **NOT Ship**

## Deliverables

| gap_id | Change | Entry |
|---|---|---|
| B-FLY-001 | `spring-boot-starter-flyway`; `db/migration/V1__hib_demo_flyway_marker.sql`; default `spring.flyway.enabled=false` | `DemoFlywayIT#flywayMigratesMarkerTableOnXugu`; offline `DemoOfflineSmokeTest#flywayMigrationResourceOnClasspath` |
| B-DEMO-001 | HQL bulk delete on `DemoPerson` | `DemoBulkMutationIT#bulkDeletePersonNames` |
| B-DEMO-002 | Extend `DemoFunctionsIT` with trim, length, locate, case, json_length | `DemoFunctionsIT#hqlFunctionSubsetSmoke` |
| B-DEMO-003 | Read-only transaction query smoke | `DemoReadOnlyTxIT#readOnlyTransactionQueriesPersistedRow` |

## Design notes

- **No multi-datasource.** Single datasource; Flyway runs on the same JDBC URL as JPA.
- **Flyway default off** so existing I-006 gated IT (`ddl-auto=update`) stay unchanged offline/live.
- **Flyway IT** enables Flyway + `ddl-auto=update`: Flyway owns `HIB_DEMO_FLYWAY_MARKER`; Hibernate still manages entity tables.
- **Boot SSOT frozen at 41 rows** — Track B items documented in non-matrix section of `consumer-path-baseline.md`.
- **Dialect unchanged** — no Boot-only defect found.
- **`dialect/Probe.java`** left in place (P-004 leftover; deletion outside P-005 allowed paths).

## Verification (implementer)

| Mode | Command | Result |
|---|---|---|
| Offline | `mvn -q test` | **PASS** (implementer 2026-07-19) |
| Live | `XUGU_RUN_IT=true mvn -q -pl demo-spring-boot -am test` | **PASS** demo 32/0/0/0 |

## Docs

- `docs/user-guide/06-consumer-path.md` — § I-007 Track B deepening + Flyway config
- `demo-spring-boot/README.md` — IT table + Flyway marker table
- `contracts/consumer-path-baseline.md` — non-matrix Track B table + C-BULK-001 xref
