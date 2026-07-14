# Contract: xugu-dialect P-003 Types & DDL

> **Kind:** Phase implement contract (types / DDL / identifier cross-cuts)  
> **Phase:** P-003 / Build B-003 / Initiative I-001  
> **Author role:** architect-contract  
> **Invocation:** `arch-p003-20260714`  
> **Status:** CONFIRMED for implementer (RP-02)  
> **Parent contracts:** [`xugu-dialect.contract.md`](xugu-dialect.contract.md), [`xugu-dialect.scaffold.contract.md`](xugu-dialect.scaffold.contract.md)  
> **Feature matrix SSOT:** [`feature-matrix-definition-a.md`](feature-matrix-definition-a.md) (P-003 rows only)  
> **SQL truth:** `E:\Work\docs\content` (read-only; cite paths; do not rewrite)  
> **Forbidden:** read/port `E:\Work\java\hibernate-dialect`; extend `MySQLDialect` / `OracleDialect`

---

## 1. Purpose

Lock the **package layout**, **Hibernate 7.4.5 Dialect override / registration points**, **connection/IT defaults**, and **matrix ID → method mapping** for P-003 so implementer can implement types + DDL against real XuguDB without inventing structure or SQL.

This contract does **not** authorize pagination, locks, identity, sequence, function registry, schema/catalog extras, or DialectResolver (those are later Phases).

---

## 2. Package structure (MySQL/Oracle *structure* only — NOT inheritance)

Mirror how Hibernate organizes dialect concerns (main Dialect + small helpers), **without** extending MySQL/Oracle Dialects and **without** copying sibling-repo layout.

### 2.1 Required

| FQCN / package | Role |
|---|---|
| `com.xugu.dialect.XuguDialect` | Sole public Dialect entry. Extends `org.hibernate.dialect.Dialect` only. Owns `contributeTypes` / `columnType` (via `registerColumnTypes`) / DDL string helpers / quote + keyword + identifier-helper overrides as mapped below. |

Existing stub path remains: `dialect/src/main/java/com/xugu/dialect/XuguDialect.java`.

### 2.2 Optional helpers (only if size warrants)

Keep helpers under the same root; prefer package-private or `internal`:

| Package | When allowed | Examples (suggested names; implementer MAY rename) |
|---|---|---|
| `com.xugu.dialect` | Small nested/static helpers colocated with Dialect | e.g. nested `SizeStrategy` if tiny |
| `com.xugu.dialect.internal` | Non-public helpers that would clutter `XuguDialect` | e.g. `XuguSizeStrategy`, keyword list constants |

### 2.3 Explicitly NOT required for P-003

Do **not** create heavy subpackages for later Phase concerns (unless a stub is empty and unused):

- `...pagination`, `...lock`, `...identity`, `...sequence`, `...function`, `...temptable`, SPI resolver packages → **P-004+**

No separate `types` / `ddl` package is required: Hibernate 7.4 already routes types/DDL through Dialect methods.

### 2.4 Inheritance (non-negotiable)

```text
XuguDialect extends org.hibernate.dialect.Dialect   ✅
XuguDialect extends MySQLDialect / OracleDialect    ❌ FORBIDDEN
```

---

## 3. Hibernate 7.4.5 registration points (implementer MUST use)

Primary hooks on `XuguDialect` (API from Hibernate **7.4.5.Final**):

| Concern | Override / registration point |
|---|---|
| Type contribution entry | `contributeTypes(TypeContributions, ServiceRegistry)` (and/or `contribute(...)` if needed to chain) |
| Column DDL type names | `registerColumnTypes(...)` → override **`columnType(int sqlTypeCode)`** (and `castType` / `narrowCastType` when CAST form differs) |
| JDBC type descriptors | Inside `contributeTypes`: `TypeContributions.contributeJdbcType(...)` / registry adjustments **only when** default descriptors mis-bind Xugu (UUID→GUID, JSON, BOOLEAN) |
| Length / precision | `getSizeStrategy()`, `getMaxVarcharLength()`, `getMaxNVarcharLength()`, `getMaxVarbinaryLength()`, `getDefaultDecimalPrecision()`, `getDefaultTimestampPrecision()`, `getFloatPrecision()`, `getDoublePrecision()`, `getFractionalSecondPrecisionInNanos()`, `getDefaultLobLength()` as needed |
| Boolean preference | `getPreferredSqlTypeCodeForBoolean()`, `toBooleanValueString` / `appendBooleanValueString` |
| National char | `getNationalizationSupport()` if NCHAR/NVARCHAR mapping needs explicit strategy |
| CAST in HQL | `castPattern(CastType, CastType)`, `castType(int)`, `narrowCastType(int)` |
| UUID literals | `appendUUIDLiteral(...)` (column type still via `columnType` / JDBC contribute) |
| LOB binding | `useInputStreamToInsertBlob()`, `useMaterializedLobWhenCapacityExceeded()`, `supportsJdbcConnectionLobCreation(...)` as validated on real driver |
| DDL verbs | `getCreateTableString()`, `getAlterTableString()`, `getAddColumnString()`, `getAddColumnSuffixString()`, `getDropTableString(String)`, `getNullColumnString(...)`, `getAddPrimaryKeyConstraintString(String)`, `getTableTypeString()` only if Xugu needs non-default |
| IF EXISTS | Leave default / do **not** claim `A-DDL-007` (延后) |
| Exporters | Prefer default exporters; override `getTableExporter()` **only if** CREATE/ALTER/DROP cannot be fixed via string helpers |
| Identifiers | `openQuote()`, `closeQuote()`, `toQuotedIdentifier(...)`, **`buildIdentifierHelper(...)`** (unquoted → uppercase under `compatible_mode=NONE`) |
| Keywords | `registerKeyword(String)` / `registerKeywords(DialectResolutionInfo)` / extend default keyword set |
| Transactions | No custom TCL SQL required; JDBC auto-commit off + commit/rollback smoke; register TCL keywords if Hibernate keyword quoting needs them |

Constructor remains versioned (scaffold uses `DatabaseVersion.make(12, 0)`); do not change GAV/version policy.

---

## 4. Matrix ID → concrete override / registration map

Status of all listed IDs: **可实现** (P-003). Deferred type/DDL rows (`A-TYP-014…018`, `A-DDL-007…009`) are **out of scope** — do not implement.

### 4.1 Types (`A-TYP-*`)

| Matrix ID | Capability | Primary Dialect hook(s) | Xugu DDL / notes (doc-backed) |
|---|---|---|---|
| **A-TYP-001** | Integer family | `columnType` for `SqlTypes.TINYINT` / `SMALLINT` / `INTEGER` / `BIGINT` | Map to `TINYINT` / `SMALLINT` / `INTEGER` / `BIGINT` (`reference/sql/datatype/numerical.md`) |
| **A-TYP-002** | Exact decimal | `columnType` for `NUMERIC`/`DECIMAL`; `getSizeStrategy()`; `getDefaultDecimalPrecision()` | `NUMERIC($p,$s)` (DECIMAL/NUMBER synonyms); default NUMERIC(12,0) when unspecified per docs |
| **A-TYP-003** | Floating | `columnType` for `FLOAT`/`DOUBLE`/`REAL`; `getFloatPrecision()` / `getDoublePrecision()` | `FLOAT` / `DOUBLE` (and REAL per docs mapping) |
| **A-TYP-004** | CHAR / VARCHAR | `columnType` for `CHAR`/`VARCHAR`/`NCHAR`/`NVARCHAR`; size strategy; `getMaxVarcharLength()` / `getMaxNVarcharLength()`; optional `getNationalizationSupport()` | Length in CREATE; document CHAR trim semantics in IT notes |
| **A-TYP-005** | BOOLEAN | `columnType(BOOLEAN)`; `getPreferredSqlTypeCodeForBoolean()` → boolean SQL type; `toBooleanValueString` / `appendBooleanValueString` | Prefer **`BOOLEAN`**; avoid BIT unless IT proves necessary (`bool.md` / `bit.md`) |
| **A-TYP-006** | DATE | `columnType(DATE)` | Under **NONE**, `DATE` is date-only (≠ DATETIME). Do **not** map DATE→DATETIME |
| **A-TYP-007** | TIME | `columnType(TIME)` (+ TZ variant only if Hibernate requests and docs allow); precision via default/fractional-second hooks | `TIME` precision 0–3 documented |
| **A-TYP-008** | TIMESTAMP / DATETIME | `columnType` for timestamp codes; `getDefaultTimestampPrecision()`; `getTimeZoneSupport()` | Pick **one** consistent mapping for `TIMESTAMP` vs `DATETIME` (recommend `TIMESTAMP` for Hibernate timestamp; document choice in evidence) |
| **A-TYP-009** | BINARY | `columnType` for `BINARY`/`VARBINARY` → bare `binary` (no `$l`); `getMaxVarbinaryLength()` | Docs (`binary.md`) show bare `BINARY` only; length via Hibernate max/binding, not DDL param |
| **A-TYP-010** | BLOB | `columnType(BLOB)`; LOB binding flags in §3 | Round-trip / stream IT on real DB |
| **A-TYP-011** | CLOB | `columnType(CLOB)` (+ NCLOB if nationalization requires) | Note CLOB/NCLOB synonym behavior per `large-object.md` |
| **A-TYP-012** | GUID / UUID | `columnType` for `SqlTypes.UUID` → **`GUID`**; `contributeTypes` if `UuidJdbcType` needs registration; `appendUUIDLiteral` | Column type `GUID` (`guid.md`); generators remain later Phases |
| **A-TYP-013** | JSON | `columnType` for `SqlTypes.JSON` → **`JSON`**; contribute JDBC type if needed | Store/retrieve JSON string; JSON operators **not** P-003 |
| **A-TYP-019** | CAST | `castPattern` / `castType` / `narrowCastType` | Smoke CAST in HQL/native per `type_conversion.md` |

### 4.2 DDL (`A-DDL-*`)

| Matrix ID | Capability | Primary Dialect hook(s) | Notes |
|---|---|---|---|
| **A-DDL-001** | CREATE TABLE basics | `getCreateTableString()`; column types from §4.1; default `getTableExporter()` | Schema export / `hbm2ddl` smoke; syntax per `reference/object/table/create.md` |
| **A-DDL-002** | ALTER add/drop column | `getAlterTableString()`; `getAddColumnString()`; `getAddColumnSuffixString()`; drop-column path via exporter / `supportsAlterColumnType` only if required | Prefer ADD COLUMN path first (`alter.md`) |
| **A-DDL-003** | Primary key inline | Table exporter PK rendering + `getAddPrimaryKeyConstraintString(String)` for alter-add-PK | Inline `PRIMARY KEY` in CREATE and/or ALTER |
| **A-DDL-004** | NOT NULL | `getNullColumnString()` / `getNullColumnString(String)` | Emit / omit NULL correctly; enforce NOT NULL |
| **A-DDL-005** | DEFAULT value | Rely on Hibernate column default rendering; override exporter **only if** Xugu needs non-standard DEFAULT clause | Literals/functions only where documented |
| **A-DDL-006** | DROP TABLE | `getDropTableString(String)`; do not enable IF EXISTS for A-DDL-007 | Export drop works; cleanup after IT |

### 4.3 Cross-cutting in P-003 (`A-XCUT-*`)

| Matrix ID | Capability | Primary Dialect hook(s) | Notes |
|---|---|---|---|
| **A-XCUT-001** | Unquoted identifier case | `buildIdentifierHelper(...)` → unquoted identifiers fold to **UPPER** | Matches `compatible_mode=NONE` / `identifier.md` |
| **A-XCUT-002** | Quoted identifiers | `openQuote()` / `closeQuote()` → **`"`** (double-quote); `toQuotedIdentifier` | DB also allows backtick; Hibernate quote pair MUST be consistent (prefer `"`) |
| **A-XCUT-004** | BEGIN/COMMIT/ROLLBACK | JDBC transaction smoke in IT; `registerKeyword` for `BEGIN`/`COMMIT`/`ROLLBACK` if keyword quoting requires | No custom Dialect TCL executor |
| **A-XCUT-007** | Keyword escaping | `registerKeyword` / `registerKeywords` for reserved words from `reference/sql/keyword.md` as needed by IT | Quoted identifiers protect reserved names |

`A-XCUT-003` (compatible_mode default documentation at integration surface) remains **P-008**; P-003 IT still **runs with** `compatible_mode=NONE` (connection/session default).

---

## 5. Connection & `compatible_mode` defaults (IT / local)

| Item | Default | Override |
|---|---|---|
| JDBC URL | `jdbc:xugu://127.0.0.1:5138/SYSTEM` | env **`XUGU_JDBC_URL`** |
| User | `SYSDBA` | env **`XUGU_USER`** |
| Password | `SYSDBA` | env **`XUGU_PASSWORD`** |
| `compatible_mode` | **NONE** | Do not require MySQL/Oracle compatible mode for P-003 tests |

Secrets MUST NOT be committed. Defaults are local-reference only (Charter).

Optional query/session setup MAY set `compatible_mode=NONE` explicitly if the driver/session does not already default to it — document in IT evidence.

---

## 6. Integration test gating (MUST)

### 6.1 Default offline `mvn test`

Integration tests that need a live XuguDB **MUST** be gated so default `mvn test` / project verify still passes offline:

- System property: **`-Dxugu.run.integration=true`**, **OR**
- Environment variable: **`XUGU_RUN_IT=true`**

When neither is set, IT classes/methods **skip** (JUnit assumptions/conditions) and do not fail the build.

Suggested location:

```text
dialect/src/test/java/com/xugu/dialect/...   # unit tests always run
dialect/src/test/java/com/xugu/dialect/it/...  # OR same package with clear *IT naming
```

Naming recommendation: `*IT.java` or package `...integration` / `...it`, with a shared gate helper (e.g. `com.xugu.dialect.internal.XuguITGate` or test-scoped utility).

### 6.2 Phase acceptance when flag is ON (hard)

P-003 acceptance **requires** real-DB IT execution:

1. Implementer/test **MUST** run with the gate enabled (`-Dxugu.run.integration=true` and/or `XUGU_RUN_IT=true`).
2. Prove DB reachable (failed connection = **blocker external**, not VERIFY PASS).
3. Exercise observed flows:
   - `type-mapping-roundtrip-real-db`
   - `ddl-generation-matches-xugu-docs`
4. Pure mocks **MUST NOT** substitute for dialect IT acceptance.

Illegal DDL failure path (acceptance criterion): assert diagnosable error and cleanup / documented cleanup so the test DB is not left unrecoverably dirty.

---

## 7. Out of scope (do not implement in P-003)

| Item | Reason |
|---|---|
| `A-TYP-014…018`, `A-DDL-007…009` | 延后 |
| LimitHandler / locks | P-004 |
| Identity / Sequence / DUAL | P-005 |
| Function contributions | P-006 |
| Schema/catalog/temp/COMMENT/FK extras | P-007 |
| DialectResolver SPI / isolation matrix claims | P-008 |
| Demo env wiring beyond dialect IT | P-009 |
| Reading sibling `hibernate-dialect` | Forever forbidden |

---

## 8. Implementer checklist (RP-02)

Copy/track in implementer evidence:

1. [ ] Expand `com.xugu.dialect.XuguDialect` only from `Dialect` (no MySQL/Oracle extends).
2. [ ] Implement `contributeTypes` / `columnType` mappings for **A-TYP-001…013**.
3. [ ] Implement CAST hooks for **A-TYP-019**.
4. [ ] Wire DDL helpers for **A-DDL-001…006** (defaults OK only if IT proves Xugu accepts them).
5. [ ] Identifier helper UPPER + quote `"` for **A-XCUT-001/002**; keywords for **A-XCUT-007**; JDBC TCL smoke for **A-XCUT-004**.
6. [ ] Optional helpers only under `com.xugu.dialect` / `com.xugu.dialect.internal`.
7. [ ] Add gated IT (`xugu.run.integration` / `XUGU_RUN_IT`); default `mvn test` green offline.
8. [ ] Run IT **with flag ON** against `jdbc:xugu://127.0.0.1:5138/SYSTEM` (env overrides); prove DB reachable.
9. [ ] Cite Xugu doc paths for any non-obvious mapping choice (esp. TIMESTAMP vs DATETIME).
10. [ ] Sync matrix checkboxes / `contracts/xugu-dialect.contract.md` status notes only as Phase packet allows.
11. [ ] Do **not** read `E:\Work\java\hibernate-dialect`; do **not** rewrite `E:\Work\docs\content`.
12. [ ] Leave must-commit to orchestrator/Human Gate policy; prepare SHA candidate after VERIFY.

---

## 9. Acceptance mapping (this contract)

| Criterion | Gate |
|---|---|
| Package structure clear | §2 |
| Hibernate 7.4 hooks named | §3 |
| Every P-003 matrix ID mapped | §4 |
| Connection + NONE mode | §5 |
| IT gate + real-DB acceptance rule | §6 |
| Non-goals explicit | §7 |
| Implementer checklist | §8 |

---

## 10. Related documents

| Doc | Role |
|---|---|
| `contracts/xugu-dialect.contract.md` | Public capability contract |
| `contracts/feature-matrix-definition-a.md` | Definition A SSOT |
| `harness/tasks/P-003.md` | Phase packet |
| `harness/handoffs/architect-contract/P-003.yaml` | RP-01 handoff |
| `harness/evidence/architect-contract/P-003/` | Architect evidence |
