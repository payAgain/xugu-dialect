# P-006 Implementer NOTES — Type/DDL details (I-003)

> **Invocation:** `impl-p006-20260716` · I-003 / B-006 / RP-01

## Delivered

| ID | Surface | Implementation |
|---|---|---|
| C-DDL-001 | `getCreateTableString` + `supportsIfExistsBeforeTableName` | `"create table if not exists"`; `true` (DROP IF EXISTS before name) |
| C-DDL-002 | `supportsAlterColumnType` + `getAlterColumnTypeString` | `true`; `"alter column " + name + " " + def.trim()` |
| C-DDL-003 | `appendDatetimeFormat` + `appendDateTimeLiteral` ×3 | MySQL `datetimeFormat` helper (no inheritance); literals `date '…'` / `time '…'` / `timestamp '…'` via `DateTimeUtils` |
| C-DDL-004 | `getEnumTypeDeclaration` | `null` (文档不允许 native ENUM) |
| C-CAT-001 | `canCreateCatalog` / create|drop commands | `true`; `create database {name}` / `drop database {name}` |
| C-GUID-001 | `getSelectGUIDString` | `select sys_guid()` (registry primary remains `uuid()`) |

**Skipped (authorized):** C-DDL-005 延后 · C-LOCK-001 已有 · C-SKIP-001 文档不允许

## Tests

- Unit: `XuguTypeDdlDetailsTest` — flags/strings (IF EXISTS, alter, enum null, catalog, GUID; subquery N/A for this Phase)
- ORM/schema IT: `XuguTypeDdlDetailsIT` + `I003P006TypeEntity` / `I003P006AlterEntity` (`HIB_I003_P006_*`)
  - C-DDL-001: SchemaExport CREATE script contains `if not exists` — **PASS**
  - C-DDL-002: live `ALTER TABLE … ALTER COLUMN` integer→varchar (empty nullable column) — **PASS**
  - C-DDL-003: HQL `local datetime` literal → SQL `timestamp '…'`; `to_char` + `current_timestamp` — **PASS**
  - C-CAT-001: `CREATE`/`DROP DATABASE HIB_I003_P006_CAT` with cleanup — **PASS**
  - C-GUID-001: Session native `select sys_guid()` — **PASS**

## C-DDL-002 boundary

XuGu rejects type change when column is `NOT NULL` with incompatible existing values (E16017). IT alters an empty nullable column, then inserts varchar data.

## Validation (implementer)

| Command | Exit |
|---|---|
| `mvn -q -pl dialect -am test -Dtest=XuguTypeDdlDetailsTest` | **0** |
| `mvn -q -pl dialect -am test -Dtest=XuguTypeDdlDetailsIT -Dxugu.run.integration=true` | **0** |

## Forbidden respected

No sibling source copy; no MySQL/Oracle Dialect inheritance (static `MySQLDialect.datetimeFormat` helper only); no harness framework changes; version 7.4.5.Final; no Accept; no git commit.
