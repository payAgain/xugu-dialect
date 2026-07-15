# Definition A Feature Matrix (SSOT)

> **Status:** Accepted SSOT (P-002 / B-002)  
> **Phase:** P-002 / Build B-002 / Initiative I-001  
> **Author role:** architect-contract  
> **Definition A:** MySQL/Oracle Dialect **production capability surface** ∩ XuguDB docs allow  
> **Public contract:** [`contracts/xugu-dialect.contract.md`](xugu-dialect.contract.md)  
> **SQL truth:** paths relative to `E:\Work\docs\content/` (read-only; do not rewrite)  
> **Hibernate surface:** reasoned from Dialect responsibilities in Hibernate **7.4.5** (types, LimitHandler, IdentityColumnSupport, SequenceSupport, locking, schema export, functions, temporary tables, comments) — javadoc/API only; **no** sibling `hibernate-dialect` repo  

## Status legend

| Status | Meaning |
|---|---|
| **可实现** | Docs allow SQL/behavior; in I-001 active Phase scope |
| **文档不允许** | No documented SQL/behavior for the expected Hibernate surface — **MUST NOT** invent SQL |
| **延后** | Docs may allow, but out of current Phase/I-001 priority; revisit trigger required |

## Columns

`ID | Domain | Capability | Hibernate/Dialect surface (what apps expect) | Xugu doc ref | Status | Target Phase | Acceptance hint`

---

## P-003 — Types & DDL mapping

| ID | Domain | Capability | Hibernate/Dialect surface (what apps expect) | Xugu doc ref | Status | Target Phase | Acceptance hint |
|---|---|---|---|---|---|---|---|
| A-TYP-001 | Types | Integer family | Map `TINYINT`/`SMALLINT`/`INTEGER`/`BIGINT` (SqlTypes) → Xugu integer types | `reference/sql/datatype/numerical.md` | 可实现 | P-003 | ✅ P-003 unit + IT |
| A-TYP-002 | Types | Exact decimal | Map `NUMERIC`/`DECIMAL`/`NUMBER` with precision/scale | `reference/sql/datatype/numerical.md` | 可实现 | P-003 | ✅ P-003 unit + IT |
| A-TYP-003 | Types | Floating | Map `FLOAT`/`DOUBLE`/`REAL` | `reference/sql/datatype/numerical.md` | 可实现 | P-003 | ✅ P-003 unit (REAL→FLOAT) |
| A-TYP-004 | Types | CHAR / VARCHAR | Map `CHAR`/`VARCHAR`/`NCHAR` lengths (dialect size strategy) | `reference/sql/datatype/character.md` | 可实现 | P-003 | ✅ P-003; CHAR trim noted |
| A-TYP-005 | Types | BOOLEAN | Map `BOOLEAN`/`BIT` boolean semantics | `reference/sql/datatype/bool.md`, `reference/sql/datatype/bit.md` | 可实现 | P-003 | ✅ P-003 prefer BOOLEAN |
| A-TYP-006 | Types | DATE | Map `DATE` (date-only) | `reference/sql/datatype/datetime.md` | 可实现 | P-003 | ✅ P-003 IT |
| A-TYP-007 | Types | TIME | Map `TIME` / optional TZ | `reference/sql/datatype/datetime.md` | 可实现 | P-003 | ✅ P-003 columnType |
| A-TYP-008 | Types | TIMESTAMP / DATETIME | Map `TIMESTAMP`/`DATETIME` (+ optional TZ) | `reference/sql/datatype/datetime.md` | 可实现 | P-003 | ✅ TIMESTAMP chosen |
| A-TYP-009 | Types | BINARY | Map `VARBINARY`/`BINARY` small binary | `reference/sql/datatype/binary.md` | 可实现 | P-003 | ✅ P-003 → bare BINARY (no `$l`) |
| A-TYP-010 | Types | BLOB | Map `BLOB` / materialize binary LOB | `reference/sql/datatype/large-object.md` | 可实现 | P-003 | ✅ P-003 IT |
| A-TYP-011 | Types | CLOB | Map `CLOB` / materialize character LOB | `reference/sql/datatype/large-object.md` | 可实现 | P-003 | ✅ P-003; NCLOB→CLOB |
| A-TYP-012 | Types | GUID / UUID column | Map UUID/`UuidJdbcType` → `GUID` type | `reference/sql/datatype/guid.md` | 可实现 | P-003 | ✅ P-003 IT |
| A-TYP-013 | Types | JSON column | Map JSON/`SqlTypes.JSON` → `JSON` | `reference/sql/datatype/json.md` | 可实现 | P-003 | ✅ P-003 IT |
| A-TYP-014 | Types | INTERVAL | Map Hibernate duration/interval if Dialect exposes | `reference/sql/datatype/datetime.md` | 延后 | later | Revisit if app demand + IntervalJdbcType needed |
| A-TYP-015 | Types | ARRAY | Map SQL ARRAY / Hibernate array types | `reference/sql/datatype/array.md` | 延后 | later | Outside core MySQL/Oracle ORM default surface for I-001 |
| A-TYP-016 | Types | XML | Map SQLXML / XML type | `reference/sql/datatype/xml.md` | 延后 | later | Not required for definition A core |
| A-TYP-017 | Types | Geometric / spatial | Map geometry types | `reference/sql/datatype/geometric.md` | 延后 | later | Charter: spatial out unless matrix includes; revisit on Scope |
| A-TYP-018 | Types | UDT | User-defined types as entity columns | `reference/sql/datatype/udt.md` | 延后 | later | Not production default for Hibernate apps |
| A-TYP-019 | Types | Type conversion CAST | Dialect/app CAST between types | `reference/sql/type_conversion.md`, `reference/sql/expression/type_conversion.md` | 可实现 | P-003 | ✅ castPattern default |
| A-DDL-001 | DDL | CREATE TABLE basics | Schema export `create table` with columns/nullability/defaults | `reference/object/table/create.md` | 可实现 | P-003 | ✅ schema export IT |
| A-DDL-002 | DDL | ALTER TABLE add/drop column | Schema update add/modify column | `reference/object/table/alter.md` | 可实现 | P-003 | ✅ ALTER ADD COLUMN IT |
| A-DDL-003 | DDL | Primary key inline | `PRIMARY KEY` on column/table | `reference/object/constraints.md`, `reference/object/table/create.md` | 可实现 | P-003 | ✅ PK in export IT |
| A-DDL-004 | DDL | NOT NULL | Column nullability | `reference/object/constraints.md` | 可实现 | P-003 | ✅ NOT NULL IT |
| A-DDL-005 | DDL | DEFAULT value | Column default in DDL | `reference/object/table/create.md` | 可实现 | P-003 | ✅ default exporter path |
| A-DDL-006 | DDL | DROP TABLE | Schema drop | `reference/object/table/alter.md` (drop via object lifecycle; see table docs) | 可实现 | P-003 | ✅ drop table IT |
| A-DDL-007 | DDL | IF NOT EXISTS create | Optional IF NOT EXISTS | `reference/object/table/create.md` | 延后 | later | Hibernate rarely requires; revisit if tooling needs |
| A-DDL-008 | DDL | Table partitioning | PARTITION BY in CREATE | `reference/object/table/partition.md`, `reference/object/table/create.md` | 延后 | later | Not required for standard ORM schema export |
| A-DDL-009 | DDL | Column/table ENCRYPT | Encryptor clauses | `reference/object/table/create.md`, `reference/object/encryptor.md` | 延后 | later | Security product feature, not Dialect core |

---

## P-004 — Pagination & locks

| ID | Domain | Capability | Hibernate/Dialect surface (what apps expect) | Xugu doc ref | Status | Target Phase | Acceptance hint |
|---|---|---|---|---|---|---|---|
| A-PAG-001 | Pagination | LIMIT only | `LimitHandler` limit without offset | `reference/sql/select/resultset-restricted.md` | 可实现 | P-004 | ✅ `LIMIT ?` via XuguLimitHandler; with locks appends after FOR UPDATE (XuGu grammar) |
| A-PAG-002 | Pagination | LIMIT + OFFSET | `supportsLimitOffset` | `reference/sql/select/resultset-restricted.md` | 可实现 | P-004 | ✅ stable form `LIMIT count OFFSET offset`; with locks: FOR UPDATE before LIMIT |
| A-PAG-003 | Pagination | Parameter binding | Limit/offset as JDBC parameters | `reference/sql/select/resultset-restricted.md` (`?` / named) | 可实现 | P-004 | ✅ bind markers; reverse order count→offset |
| A-PAG-004 | Pagination | TOP syntax | Alternate top-N | `reference/sql/select/resultset-restricted.md` (#top) | 延后 | later | Prefer LIMIT for Hibernate; TOP not required |
| A-PAG-005 | Pagination | ANSI FETCH FIRST | `FETCH FIRST n ROWS ONLY` | — (not documented under resultset-restricted) | 文档不允许 | P-004 | ✅ not emitted (unit + IT assert) |
| A-PAG-006 | Pagination | ROWNUM pagination | Oracle-style ROWNUM wrappers | `reference/sql/select/select.md` (#ROWNUM) | 延后 | later | Documented but LIMIT preferred; revisit only if LIMIT blocked |
| A-LCK-001 | Locks | FOR UPDATE | Pessimistic write lock clause | `reference/sql/select/select.md` (#opt_for_update_clause) | 可实现 | P-004 | ✅ `for update` + IT |
| A-LCK-002 | Locks | FOR UPDATE OF columns | Optional column list | `reference/sql/select/select.md` | 可实现 | P-004 | ✅ `for update of …` + IT |
| A-LCK-003 | Locks | Lock wait / NOWAIT | `LockOptions.NOWAIT` / timeout | `reference/sql/select/select.md` (`opt_wait`: NOWAIT / WAIT / WAIT ms); also `reference/object/table/lock.md` | 可实现 | P-004 | ✅ ms pass-through; IT smoke |
| A-LCK-004 | Locks | SKIP LOCKED | `LockOptions.SKIP_LOCKED` | — (no SKIP LOCKED in select FOR UPDATE grammar) | 文档不允许 | P-004 | ✅ `supportsSkipLocked=false`; no keyword |
| A-LCK-005 | Locks | FOR SHARE / read lock | Pessimistic read share clause | — (FOR UPDATE / FOR READ ONLY only) | 文档不允许 | P-004 | ✅ 文档不允许 retained; Hibernate shim only — no FOR SHARE; PESSIMISTIC_READ→exclusive FOR UPDATE (not share); concurrent readers may block |
| A-LCK-006 | Locks | LOCK TABLE (explicit) | Rare Dialect helper for table locks | `reference/object/table/lock.md` | 延后 | later | Not required for JPA LockMode path |

---

## P-005 — Identity & Sequence

| ID | Domain | Capability | Hibernate/Dialect surface (what apps expect) | Xugu doc ref | Status | Target Phase | Acceptance hint |
|---|---|---|---|---|---|---|---|
| A-IDN-001 | Identity | IDENTITY column DDL | `IdentityColumnSupport.getIdentityColumnString` | `reference/object/table/create.md` (`IDENTITY` / `AUTO_INCREMENT`) | 可实现 | P-005 | ✅ Emit `identity(1,1)` |
| A-IDN-002 | Identity | AUTO_INCREMENT synonym | MySQL-style keyword | `reference/object/table/create.md` | 可实现 | P-005 | ✅ Equivalent to IDENTITY(1,1); dialect emits IDENTITY only (NONE) |
| A-IDN-003 | Identity | Retrieve generated keys | `getGeneratedKeys` / identity select | `reference/object/table/create.md`; JDBC driver behavior | 可实现 | P-005 | ✅ JDBC getGeneratedKeys primary; `LAST_INSERT_ID()` select fallback |
| A-IDN-004 | Identity | Insert with identity | Insert omitting identity column | `reference/object/table/create.md` | 可实现 | P-005 | ✅ INSERT omits id column; id backfilled |

| A-IDN-005 | Identity | Identity mode session params | Dialect-specific identity_mode knobs | `reference/system-configuration-parameter/session-parameter/identity_mode.md`, `reference/system-configuration-parameter/xugu.ini/compatible/def_identity_mode.md` | 延后 | later | Revisit if generated-key edge cases appear |
| A-SEQ-001 | Sequence | CREATE SEQUENCE | `SequenceSupport.getCreateSequenceString` | `reference/object/sequence.md` | 可实现 | P-005 | ✅ `create sequence … [start with N increment by M]` |
| A-SEQ-002 | Sequence | DROP SEQUENCE | Drop sequence DDL | `reference/object/sequence.md` | 可实现 | P-005 | ✅ `drop sequence name` |
| A-SEQ-003 | Sequence | NEXTVAL | `nextval` for SEQUENCE generator | `reference/object/sequence.md`, `reference/function/sequence-functions/nextval.md` | 可实现 | P-005 | ✅ Locked: `select seq.nextval from dual` (not internal NEXTVAL()) |
| A-SEQ-004 | Sequence | CURRVAL | Current value function | `reference/object/sequence.md`, `reference/function/sequence-functions/currval.md` | 可实现 | P-005 | ✅ `currval('name')` after NEXTVAL same session |
| A-SEQ-005 | Sequence | Sequence options | START/INCREMENT/MIN/MAX/CACHE/CYCLE | `reference/object/sequence.md` | 可实现 | P-005 | ✅ Hibernate maps START/INCREMENT; MIN/MAX/CACHE/CYCLE N/A via SequenceSupport API |

| A-SEQ-006 | Sequence | ALTER SEQUENCE | Alter sequence options | `reference/object/sequence.md` | 延后 | later | Schema-update rare path |

---

## P-006 — Function registry

| ID | Domain | Capability | Hibernate/Dialect surface (what apps expect) | Xugu doc ref | Status | Target Phase | Acceptance hint |
|---|---|---|---|---|---|---|---|
| A-FUN-001 | Functions | concat | `concat` / SQL concatenation operator | `reference/function/string-functions/concat.md`, `reference/sql/operators/concatenation-operators/link.md` | 可实现 | P-006 | ✅ HQL `concat` → `concat(...)`; IT |
| A-FUN-002 | Functions | substring / substr | `substring`, `substr` | `reference/function/string-functions/substring.md`, `reference/function/string-functions/substr.md` | 可实现 | P-006 | ✅ both registered; HQL substring IT |
| A-FUN-003 | Functions | length / char_length | string length | `reference/function/string-functions/length.md`, `reference/function/string-functions/char_length.md` | 可实现 | P-006 | ✅ Dialect default length/char_length |
| A-FUN-004 | Functions | lower / upper | case fold | `reference/function/string-functions/lower.md`, `reference/function/string-functions/upper.md` | 可实现 | P-006 | ✅ HQL lower IT |
| A-FUN-005 | Functions | trim / ltrim / rtrim | trim family | `reference/function/string-functions/trim.md`, `ltrim.md`, `rtrim.md` | 可实现 | P-006 | ✅ trim + ltrim/rtrim registered |
| A-FUN-006 | Functions | replace / locate / position | search/replace | `reference/function/string-functions/replace.md`, `locate.md`, `position.md` | 可实现 | P-006 | ✅ replace/locate + ANSI position |
| A-FUN-007 | Functions | coalesce / nullif / nvl | null-handling | `reference/sql/expression/function.md` (COALESCE), `reference/function/flow-control-functions/nullif.md`, `reference/function/string-functions/nvl.md`, `reference/function/flow-control-functions/ifnull.md` | 可实现 | P-006 | ✅ COALESCE preferred; NVL native registered |
| A-FUN-008 | Functions | abs / mod / power / sqrt | math core | `reference/function/mathematical-functions/abs.md`, `mod.md`, `power.md`, `sqrt.md` | 可实现 | P-006 | ✅ abs IT; mod/power/sqrt registered |
| A-FUN-009 | Functions | round / floor / ceil / trunc | rounding | `reference/function/mathematical-functions/round.md`, `floor.md`, `ceil.md`, `trunc.md` | 可实现 | P-006 | ✅ ceil/ceiling + trunc registered |
| A-FUN-010 | Functions | current_date / current_timestamp / now | temporal current | `reference/function/date-and-time-functions/current_date.md`, `current_timestamp.md`, `now.md`, `sysdate.md` | 可实现 | P-006 | ✅ current_timestamp IT; now() registered |
| A-FUN-011 | Functions | extract / year/month/day | datetime extract | `reference/function/date-and-time-functions/extract.md`, `year.md`, `month.md`, `day.md` | 可实现 | P-006 | ✅ extract(year) IT; year/month/day |
| A-FUN-012 | Functions | to_char / to_date / to_timestamp | format/parse | `reference/function/date-and-time-functions/to_char.md`, `to_date.md`, `to_timestamp.md` | 可实现 | P-006 | ✅ registered; live probe OK |
| A-FUN-013 | Functions | cast | HQL cast | `reference/sql/expression/type_conversion.md` | 可实现 | P-006 | ✅ cast IT |
| A-FUN-014 | Functions | aggregates avg/sum/min/max/count | standard aggregates | `reference/function/aggregate-functions/avg.md`, `sum.md`, `min.md`, `max.md` | 可实现 | P-006 | ✅ count/sum IT |
| A-FUN-015 | Functions | bit_and / bit_or | bitwise aggregates/ops if used | `reference/function/aggregate-functions/bit_and.md`, `bit_or.md` | 延后 | later | Only if app HQL needs |
| A-FUN-016 | Functions | UUID generators | `uuid()`, `sys_guid()`, `gen_random_uuid()` | `reference/function/uuid-functions/uuid.md`, `sys_guid.md`, `gen_random_uuid.md`, `newid.md`, `sys_uuid.md` | 可实现 | P-006 | ✅ primary=`uuid()` (dashed VARCHAR); alts registered |
| A-FUN-017 | Functions | JSON functions | `json_value` / `json_extract` / operators | `reference/function/json-functions/`, `reference/sql/operators/json-operators/` | 可实现 | P-006 | ✅ subset: json_value + json_extract; HQL needs JSON_FUNCTIONS_ENABLED |
| A-FUN-018 | Functions | listagg / string_agg / group_concat | string aggregate | `reference/function/aggregate-functions/listagg.md`, `string_agg.md`, `group_concat.md` | 可实现 | P-006 | ✅ HQL listagg → `LISTAGG … WITHIN GROUP`; string_agg/group_concat named |
| A-FUN-019 | Functions | regexp_* | regex HQL | `reference/function/string-functions/regexp_like.md`, `regexp_replace.md`, `regexp_substr.md` | 延后 | later | Revisit if Criteria/HQL regex used |
| A-FUN-020 | Functions | geometric functions | spatial HQL | `reference/function/geometric-functions/` | 延后 | later | Paired with A-TYP-017 |
| A-FUN-021 | Functions | XML functions | XML HQL | `reference/function/xml-functions/` | 延后 | later | Paired with A-TYP-016 |

---

## P-007 — Schema / temp / comment / constraints / truncate

| ID | Domain | Capability | Hibernate/Dialect surface (what apps expect) | Xugu doc ref | Status | Target Phase | Acceptance hint |
|---|---|---|---|---|---|---|---|
| A-SCH-001 | Schema | CREATE / DROP SCHEMA | Schema management | `reference/object/schema.md` | 可实现 | P-007 | create schema if Dialect exports |
| A-SCH-002 | Schema | Qualified names schema.table | `schema.table` rendering | `reference/object/schema.md`, `reference/sql/identifier.md` | 可实现 | P-007 | multi-schema IT |
| A-SCH-003 | Catalog | Catalog (database) qualifier | `catalog.schema.table` | `reference/object/database.md` | 延后 | later | Xugu “database” ≠ Hibernate catalog always; revisit after JDBC metadata study |
| A-SCH-004 | Temp | Local temporary tables | Hibernate temp table strategy (local) | `reference/object/table/create.md` (#2-OptTemp) | 可实现 | P-007 | `CREATE TEMP/LOCAL TEMPORARY TABLE` |
| A-SCH-005 | Temp | Global temporary tables | Persistent global temp | `reference/object/table/create.md`; `reference/system-configuration-parameter/xugu.ini/sql-engine/support_global_tab.md` | 可实现 | P-007 | Requires `support_global_tab=ON`; document precondition |
| A-SCH-006 | Temp | ON COMMIT DELETE/PRESERVE | Temp table commit behavior | `reference/object/table/create.md` | 可实现 | P-007 | match Hibernate TemporaryTableKind |
| A-SCH-007 | Temp | Temp table FK restriction | FK on temp tables | `reference/object/table/create.md` (temp: no FK) | 文档不允许 | P-007 | Do not emit FK on temp tables |
| A-SCH-008 | Comment | COMMENT ON TABLE | Table comments in schema export | `reference/sql/ddl/comment.md` | 可实现 | P-007 | `COMMENT ON TABLE … IS …` |
| A-SCH-009 | Comment | COMMENT ON COLUMN | Column comments | `reference/sql/ddl/comment.md` | 可实现 | P-007 | `COMMENT ON COLUMN … IS …` |
| A-SCH-010 | Comment | Inline COMMENT on CREATE | CREATE TABLE … COMMENT | `reference/object/table/create.md`, `reference/sql/ddl/comment.md` | 可实现 | P-007 | optional alternate path |
| A-SCH-011 | Constraints | UNIQUE | Unique constraint DDL | `reference/object/constraints.md` | 可实现 | P-007 | unique key export |
| A-SCH-012 | Constraints | FOREIGN KEY | FK create/alter | `reference/object/constraints.md`, `reference/object/table/create.md` | 可实现 | P-007 | FK + ON actions if documented |
| A-SCH-013 | Constraints | CHECK | Check constraints | `reference/object/constraints.md` | 可实现 | P-007 | if Hibernate emits checks |
| A-SCH-014 | Constraints | ALTER ADD/DROP CONSTRAINT | Constraint management | `reference/object/constraints.md`, `reference/object/table/alter.md` | 可实现 | P-007 | |
| A-SCH-015 | Truncate | TRUNCATE TABLE | `truncate` / multi-table clear | `reference/object/table/truncate.md` | 可实现 | P-007 | TRUNCATE [TABLE] name |
| A-SCH-016 | Indexes | CREATE INDEX basics | Index export for unique/non-unique | `reference/object/indexes.md` | 可实现 | P-007 | basic btree index DDL |
| A-SCH-017 | Indexes | Advanced index types | Partial/functional/spatial indexes | `reference/object/indexes.md` | 延后 | later | Beyond standard ORM export |

---

## P-008 — DialectResolver SPI & explicit dialect

| ID | Domain | Capability | Hibernate/Dialect surface (what apps expect) | Xugu doc ref | Status | Target Phase | Acceptance hint |
|---|---|---|---|---|---|---|---|
| A-SPI-001 | SPI | Explicit dialect property | `hibernate.dialect=com.xugu.dialect.XuguDialect` | (product contract; JDBC URL shape driver docs) | 可实现 | P-008 | SessionFactory boots with explicit dialect |
| A-SPI-002 | SPI | DialectResolver registration | META-INF/services resolver → XuguDialect | (Hibernate 7.4 SPI; Xugu product name via JDBC metadata) | 可实现 | P-008 | resolve without explicit property against real Xugu |
| A-SPI-003 | SPI | Database version awareness | `DatabaseVersion` / versioned Dialect | JDBC metadata + Xugu version | 可实现 | P-008 | construct Dialect with detected version |
| A-SPI-004 | SPI | Wrong-DB non-match | Resolver must not claim MySQL/Oracle | — | 可实现 | P-008 | negative test: non-Xugu metadata → no false resolve |

---

## Cross-cutting

| ID | Domain | Capability | Hibernate/Dialect surface (what apps expect) | Xugu doc ref | Status | Target Phase | Acceptance hint |
|---|---|---|---|---|---|---|---|
| A-XCUT-001 | Identifiers | Unquoted identifier case | NONE mode → uppercase fold | `reference/sql/identifier.md`, `reference/system-configuration-parameter/session-parameter/compatible_mode.md`, `reference/system-configuration-parameter/xugu.ini/compatible/def_compatible_mode.md` | 可实现 | P-003 | ✅ UPPER via IdentifierHelper |
| A-XCUT-002 | Identifiers | Quoted identifiers | Double-quote / backtick quoting | `reference/sql/identifier.md` | 可实现 | P-003 | ✅ quote `"` |
| A-XCUT-003 | Identifiers | compatible_mode default NONE | Connection/session default | `reference/system-configuration-parameter/session-parameter/compatible_mode.md` | 可实现 | P-008 | demo/IT docs state NONE; no MySQL mode dependency |
| A-XCUT-004 | Transactions | BEGIN/COMMIT/ROLLBACK | JDBC transactions (Dialect keyword support) | `reference/sql/tcl.md` | 可实现 | P-003 | ✅ JDBC TCL smoke + keywords |
| A-XCUT-005 | Isolation | Isolation levels | READ COMMITTED / REPEATABLE READ / SERIALIZABLE | `reference/system-configuration-parameter/session-parameter/iso_level.md` | 可实现 | P-008 | document mapping; Dialect `supports*` flags if needed |
| A-XCUT-006 | Isolation | READ UNCOMMITTED | Common JDBC level | `reference/system-configuration-parameter/session-parameter/iso_level.md` (0=READ ONLY,1=RC,2=RR,3=SERIALIZABLE) | 文档不允许 | P-008 | No RU level; do not claim support |
| A-XCUT-007 | Keywords | Keyword escaping | Reserved word quoting | `reference/sql/keyword.md`, `reference/sql/identifier.md` | 可实现 | P-003 | ✅ registerKeyword subset |
| A-XCUT-008 | Dual | FROM DUAL (if needed) | Some Dialects use DUAL | `reference/object/sequence.md` examples use `FROM DUAL` | 可实现 | P-005 | ✅ `getFromDual()` → ` from dual` on NEXTVAL/CURRVAL selects |
| A-XCUT-009 | Config | Secrets via env | Connection user/password/host | Charter / I-001 brief | 可实现 | P-009 | demo uses env overrides |
| A-XCUT-010 | Non-goal | Extend MySQL/Oracle Dialect | Inheritance shortcut | ADR-0001 / Charter | 文档不允许 | — | Forbidden forever in I-001 |
| A-XCUT-011 | Non-goal | Port sibling hibernate-dialect | Copy implementation | Charter non-goals | 文档不允许 | — | Forbidden |
| A-XCUT-012 | Ship | Maven Central publish | Release pipeline | I-001 brief | 延后 | Ship (post I-001) | Human Gate Ship initiative |

---

## Phase → ID range summary

| Phase | ID prefixes | Focus |
|---|---|---|
| P-003 | A-TYP-*, A-DDL-*, A-XCUT-001/002/004/007 | Types, DDL, identifiers |
| P-004 | A-PAG-*, A-LCK-* | Limit/offset, FOR UPDATE / wait |
| P-005 | A-IDN-*, A-SEQ-*, A-XCUT-008 | Identity & sequences |
| P-006 | A-FUN-* | Function registry |
| P-007 | A-SCH-* | Schema, temp, comment, constraints, truncate, indexes |
| P-008 | A-SPI-*, A-XCUT-003/005/006 | SPI + explicit + isolation notes |
| P-009 | A-XCUT-009 (demo) | Env secrets in demo |
| later / Ship | 延后 rows + A-XCUT-012 | Deferred |

## Counts (architect RP-01)

| Status | Count |
|---|---|
| 可实现 | 78 |
| 文档不允许 | 7 |
| 延后 | 20 |
| **Total** | **105** |

Derivation detail: `harness/evidence/architect-contract/P-002/NOTES.md`.

## Orchestrator note

Prefer this file as SSOT (ACCEPTANCE path). If product docs should link it, add a short cross-ref in `docs/architecture.md` and/or `docs/feature-matrix-definition-a.md` → point here (architect cannot write those paths in this Phase packet for architect role; see evidence NOTES).
