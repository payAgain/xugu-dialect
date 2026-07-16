# I-003 Capability Gap Matrix (Ruler C SSOT)

> **Status:** Draft for P-001 Accept (I-003 / B-001)  
> **Author role:** architect-contract  
> **Invocation:** `arch-p001-20260716`  
> **Ruler C:** (A) Hibernate 7.4.5 MySQLDialect-class production surface ∩ Xugu docs  
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(B) read-only diff vs `E:\Work\java\hibernate-dialect` — **no code port**  
> **Baseline product:** `com.xugu:xugu-dialect:7.4.5.Final` on `feat/i-003-production-capability-parity`  
> **Companion:** [`feature-matrix-definition-a.md`](feature-matrix-definition-a.md) (I-001/I-002 closed rows remain)  
> **Public contract addendum:** [`xugu-dialect.contract.md`](xugu-dialect.contract.md) § I-003

## Status legend

| Status | Meaning |
|---|---|
| **可实现** | Docs allow; in I-003 first-batch Phases P-002…P-006 |
| **文档不允许** | No documented SQL/behavior — MUST NOT invent |
| **延后** | Docs may allow, but out of I-003 first batch; revisit trigger required |
| **已有** | Already delivered in I-001/I-002; listed only for gap closure clarity |

## Columns

`ID | Theme | Capability | Hibernate surface | Xugu doc ref | Sibling (read-only) | Status | Target Phase | app_entrypoint | Acceptance hint`

---

## Theme: Exception mapping → P-002

| ID | Theme | Capability | Hibernate surface | Xugu doc ref | Sibling (read-only) | Status | Target Phase | app_entrypoint | Acceptance hint |
|---|---|---|---|---|---|---|---|---|---|
| C-EXC-001 | Exception | SQLException → Hibernate exception | `Dialect.buildSQLExceptionConversionDelegate()` | `reference/error-code/**` (SQLSTATE/vendor codes) | `XuguSQLExceptionConversionDelegate` | 可实现 | P-002 | Session persist/flush that hits unique/FK/check violation → typed Hibernate exception | ORM IT must assert exception class (not JDBC-only) |
| C-EXC-002 | Exception | Violated constraint name | `getViolatedConstraintNameExtractor()` | `reference/object/constraints.md`; error-code messages | `XuguViolatedConstraintNameExtractor` + `XuguErrorCodes` | 可实现 | P-002 | Same ORM violation path; extractor yields constraint name when present | May be null if driver message lacks name — document boundary |

---

## Theme: JSON deep + AggregateSupport → P-003

| ID | Theme | Capability | Hibernate surface | Xugu doc ref | Sibling (read-only) | Status | Target Phase | app_entrypoint | Acceptance hint |
|---|---|---|---|---|---|---|---|---|---|
| C-JSON-001 | JSON | json_arrayagg | HQL/SQM aggregate + function registry | `reference/function/aggregate-functions/json_arrayagg.md` | `XuguJsonArrayAggFunction` | 可实现 | P-003 | HQL `select json_arrayagg(...)` (or documented HQL name) on live Session | Enable JSON functions if required by Hibernate 7.4 |
| C-JSON-002 | JSON | json_objectagg | HQL/SQM aggregate | `reference/function/aggregate-functions/json_objectagg.md` | `XuguJsonObjectAggFunction` | 可实现 | P-003 | HQL `json_objectagg` path on live Session | Same |
| C-JSON-003 | JSON | AggregateSupport | `Dialect.getAggregateSupport()` | aggregate-functions + json datatype | `XuguAggregateSupport` | 可实现 | P-003 | HQL aggregation over JSON/aggregate column path exercising AggregateSupport | Unit alone insufficient |
| C-JSON-004 | JSON | JSON JDBC casting | `contributeTypes` JSON / JSON array JDBC types | `reference/sql/datatype/json.md` | `XuguCastingJsonJdbcType*` | 可实现 | P-003 | Entity JSON attribute round-trip persist/load via Session | Prefer CAST-safe mapping per docs |
| C-JSON-005 | JSON | Broader json_* HQL set | function registry beyond value/extract | `reference/function/json-functions/**` | `XuguJsonFunctions` (large set) | 延后 | later | — | Revisit if app demand; I-001 already has json_value/extract (A-FUN-017) |
| C-JSON-006 | JSON | json_table | `supportsJsonTableFunction` etc. | (verify docs; no dedicated json_table file found in inventory) | sibling may flag | 延后 | later | — | Confirm doc before promoting |

---

## Theme: Window / CTE → P-004

| ID | Theme | Capability | Hibernate surface | Xugu doc ref | Sibling (read-only) | Status | Target Phase | app_entrypoint | Acceptance hint |
|---|---|---|---|---|---|---|---|---|---|
| C-WIN-001 | Window | Window functions | `supportsWindowFunctions()` + HQL OVER | `reference/sql/select/analyze_func.md` (OVER / RANK / ROW_NUMBER…) | `supportsWindowFunctions=true` | 可实现 | P-004 | HQL query with `OVER (PARTITION BY …)` on live Session | ✅ I-003/P-004 `XuguWindowCteIT` OVER + ranking |
| C-CTE-001 | CTE | WITH clause | `supportsWithClause()` + HQL CTE | `reference/sql/select/with.md` | `supportsWithClause=true` | 可实现 | P-004 | HQL/Criteria CTE (`with … as`) on live Session | ✅ I-003/P-004 `XuguWindowCteIT` WITH rendered |

---

## Theme: Bulk mutation → P-005

| ID | Theme | Capability | Hibernate surface | Xugu doc ref | Sibling (read-only) | Status | Target Phase | app_entrypoint | Acceptance hint |
|---|---|---|---|---|---|---|---|---|---|
| C-BULK-001 | Bulk | Multi-table mutation fallback | `getFallbackSqmMutationStrategy` (local temp) | `reference/object/table/create.md` (#临时表); existing temp strategies | `LocalTemporaryTableMutationStrategy` wiring | 可实现 | P-005 | HQL bulk `update`/`delete` on inheritance or join path needing temp strategy | ✅ I-003/P-005 `XuguBulkMutationIT` update/delete |
| C-BULK-002 | Bulk | Multi-table insert fallback | `getFallbackSqmInsertStrategy` | same | `LocalTemporaryTableInsertStrategy` | 可实现 | P-005 | Bulk insert path requiring strategy (as applicable to mapping) | ✅ wired; live IT **N/A** (GetGeneratedKeys blocker documented) |
| C-BULK-003 | Bulk | Subquery on mutating table | `supportsSubqueryOnMutatingTable()` | select/subquery + DML docs | sibling returns false-like MySQL | 可实现 | P-005 | Bulk HQL that would self-reference target if unsupported | ✅ `supportsSubqueryOnMutatingTable=false` + temp fallback IT |

---

## Theme: Type / DDL details → P-006

| ID | Theme | Capability | Hibernate surface | Xugu doc ref | Sibling (read-only) | Status | Target Phase | app_entrypoint | Acceptance hint |
|---|---|---|---|---|---|---|---|---|---|
| C-DDL-001 | DDL | IF NOT EXISTS create table | `supportsIfExistsBeforeTableName()` | `reference/object/table/create.md` (`IF NOT EXISTS`) | `supportsIfExistsBeforeTableName=true` | 可实现 | P-006 | Schema export / `hbm2ddl` create with if-exists semantics exercised via SessionFactory schema tool or exporter IT | ORM/schema entrypoint, not string-only |
| C-DDL-002 | DDL | ALTER column type | `supportsAlterColumnType` + `getAlterColumnTypeString` | `reference/object/table/alter.md` (ALTER/MODIFY COLUMN) | sibling true + alter string | 可实现 | P-006 | Schema update / export altering column type on live DB | Failure path documented |
| C-DDL-003 | DDL | Datetime format / literals | `appendDatetimeFormat` / `appendDateTimeLiteral` | `reference/sql/datatype/datetime.md`; `to_char`/`to_date` docs | sibling implements both | 可实现 | P-006 | HQL datetime literal or format function path on Session | No MySQL inheritance; may use format helper patterns without extending MySQLDialect |
| C-DDL-004 | Type | Native ENUM DDL | `getEnumTypeDeclaration` | No clear ENUM datatype doc under `reference/sql/datatype` | sibling returns `null` | 文档不允许 | P-006 | — | Explicitly do **not** emit MySQL ENUM; keep null/non-claim |
| C-DDL-005 | Type | Preferred SQL type for array | `getPreferredSqlTypeCodeForArray` | `reference/sql/datatype/array.md` | sibling override | 延后 | later | — | Aligns with A-TYP-015 延后 |
| C-CAT-001 | Catalog | Create/drop catalog (database) | `canCreateCatalog` / getCreate|DropCatalogCommand | `reference/object/database.md` | sibling true | 可实现 | P-006 | Schema management API creating database/catalog if Dialect exposes | Promotes A-SCH-003 from 延后 for I-003 |
| C-GUID-001 | GUID | Select GUID string | `getSelectGUIDString()` | `reference/function/uuid-functions/**`; datatype guid | sibling override | 可实现 | P-006 | Native/UUID generator or select-guid path via Session if claimed | Coordinate with existing uuid() primary |
| C-LOCK-001 | Lock flags | supportsNoWait / supportsWait / supportsLockTimeouts | Dialect boolean hooks | `reference/sql/select/select.md` (WAIT/NOWAIT) | sibling true | 已有 | — | I-001/I-002 lock IT already covers FOR UPDATE WAIT/NOWAIT via LockingSupport | Ensure flags consistent; no new Phase unless audit finds mismatch |
| C-SKIP-001 | Lock | supportsSkipLocked | Dialect flag | — (no SKIP LOCKED in grammar) | sibling may report true/false | 文档不允许 | — | — | Keep A-LCK-004; do not enable keyword |
| C-SRV-001 | Config | Server configuration probe | optional `XuguServerConfiguration` | session-parameter docs | sibling class | 延后 | later | — | Nice-to-have; not blocking first batch |
| C-SEL-001 | SPI | DialectSelector | Hibernate selector SPI | — | `XuguDialectSelector` | 延后 | later | — | Resolver already delivered (A-SPI-*) |

---

## Counts (P-001 architect)

| Status | Count (new C-* rows) |
|---|---|
| 可实现 | **16** |
| 文档不允许 | **2** (C-DDL-004 ENUM, C-SKIP-001) |
| 延后 | **5** (C-JSON-005, C-JSON-006, C-DDL-005, C-SRV-001, C-SEL-001) |
| 已有 | **1** (C-LOCK-001) |
| **Total new C-* rows** | **24** |

| Target Phase | 可实现 IDs |
|---|---|
| P-002 | C-EXC-001, C-EXC-002 |
| P-003 | C-JSON-001…004 |
| P-004 | C-WIN-001, C-CTE-001 |
| P-005 | C-BULK-001…003 |
| P-006 | C-DDL-001…003, C-CAT-001, C-GUID-001 |

## Method notes (ruler C)

1. **A (MySQL-class surface):** Enumerated Dialect responsibilities commonly overridden for production MySQL-like Dialects on Hibernate 7.4 (exception, aggregate/JSON, window/CTE, multi-table mutation, DDL if-exists/alter type, datetime rendering, catalog). Cross-checked against XuGu docs under `E:\Work\docs\content`.
2. **B (sibling read-only):** Compared `XuguDialect` `@Override` sets and class inventory; **34** sibling-only overrides / **21** sibling main classes vs **12** local. Inventory used for *discovery only* — implementers must write original code citing XuGu docs, not copy sibling sources.
3. **Forbidden:** Extending MySQLDialect/OracleDialect; porting `hibernate-dialect` / old xugu-dialect; inventing undocumented SQL.
4. **Evidence rule:** Each 可实现 row requires ORM `app_entrypoint` IT in its Phase (product rule for I-003; harness framework unchanged).

## Out of I-003 first batch

- Spatial / XML / ARRAY (still 延后 from definition A)
- Full MySQL Parity L1 128/128 engineering suite (sibling Phase 5) — not a deliverable; selective capability parity only
- Harness agents/skills/verification.json upgrades — other thread
- Ship / version bump
