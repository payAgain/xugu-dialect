# P-005 Implementer NOTES — Bulk mutation fallback (I-003)

> **Invocation:** `impl-p005-20260716` · I-003 / B-005 / RP-01

## Delivered (C-BULK-001…003)

| ID | Surface | Implementation |
|---|---|---|
| C-BULK-001 | `getFallbackSqmMutationStrategy` | `LocalTemporaryTableMutationStrategy` + `TemporaryTable.createIdTable(...)` |
| C-BULK-002 | `getFallbackSqmInsertStrategy` | `LocalTemporaryTableInsertStrategy` + `TemporaryTable.createEntityTable(...)` |
| C-BULK-003 | `supportsSubqueryOnMutatingTable()` | `false` (MySQL-class / Xugu DML docs) |

Temp DDL reuses existing `XuguLocalTemporaryTableStrategy` (`CREATE LOCAL TEMPORARY TABLE … ON COMMIT PRESERVE ROWS`).

## Tests

- Unit: `XuguBulkMutationSupportTest` — flag + LOCAL temp command
- ORM IT: `XuguBulkMutationIT` + entities `I003P005Bulk*` (`HIB_I003_P005_*`)
  - JOINED inheritance (base + Doctor + Engineer subclasses)
  - `session.createMutationQuery("update …")` on live DB — **PASS**
  - `session.createMutationQuery("delete from …")` on live DB — **PASS**

## C-BULK-002 bulk insert — N/A for live IT

`getFallbackSqmInsertStrategy` is wired (same pattern as MySQLDialect / sibling read-only reference). Live bulk insert IT is **not** included because sibling harness documents a known blocker: JOINED bulk insert with IDENTITY root + `GetGeneratedKeys` can trigger Xugu JDBC 12.3.6 `distillTbName` failures. Update/delete paths are proven; insert strategy is unit-wired only until driver/dialect alignment.

## Validation (implementer)

| Command | Exit |
|---|---|
| `mvn -q -pl dialect -am test -Dtest=XuguBulkMutationSupportTest` | **0** |
| `mvn -q -pl dialect -am test -Dtest=XuguBulkMutationIT -Dxugu.run.integration=true` | **0** |

## Forbidden respected

No sibling source copy; no harness framework changes; version 7.4.5.Final; no Accept; no git commit.

## Import note (Hibernate 7.4.5)

Correct packages vs task hint: `RuntimeModelCreationContext` → `org.hibernate.metamodel.spi`; `SqmMultiTableInsertStrategy` → `org.hibernate.query.sqm.mutation.spi`.
