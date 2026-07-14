# P-003 Implementer Notes (types + DDL)

**Invocation:** `impl-p003-20260714` (initial) → **`impl-p003-fix-binary-20260714`** (A-TYP-009 fix)  
**Role:** implementer  
**Phase / Build / Initiative:** P-003 / B-003 / I-001  
**Date:** 2026-07-14

## What was delivered

1. Expanded `com.xugu.dialect.XuguDialect` from `org.hibernate.dialect.Dialect` only (no MySQL/Oracle extends).
2. Type mappings A-TYP-001…013 + CAST hooks (A-TYP-019) via `columnType` / `registerColumnTypes` / `contributeTypes`.
3. DDL helpers A-DDL-001…006 (`create table`, `add column`, `drop table`, PK/NOT NULL via exporter defaults + IT).
4. Identifiers: unquoted UPPER (`buildIdentifierHelper`), quote `"`, keyword subset + TCL keywords.
5. Offline unit tests (`XuguDialectTest`) + gated IT (`*IT` under `com.xugu.dialect.it`).
6. Surefire includes `*IT.java`; gate `-Dxugu.run.integration=true` / `XUGU_RUN_IT=true`.
7. Matrix acceptance hints lightly marked ✅ for P-003 可实现 rows in `contracts/feature-matrix-definition-a.md`.

## A-TYP-009 fix (`impl-p003-fix-binary-20260714`)

**Problem:** RP-04 MAJOR — `columnType(BINARY|VARBINARY) → "binary($l)"` is undocumented; XuGu `binary.md` only shows bare `BINARY`.

**Fix:**
- `SqlTypes.BINARY` / `VARBINARY` → bare `"binary"` (no length placeholder).
- Keep `getMaxVarbinaryLength() = 65536` for Hibernate length / binding limits.
- `LONGVARBINARY` / `LONG32VARBINARY` → `"blob"` (large binary path).
- Unit asserts updated in `XuguDialectTest`.
- New gated IT `XuguBinarySchemaExportIT`: SchemaExport of `byte[]` + `@JdbcTypeCode(SqlTypes.VARBINARY)` asserts DDL contains bare `binary` (case-insensitive), rejects `binary(`, and CREATE succeeds on live DB when `-Dxugu.run.integration=true`.

**Not done in this fix:** Accept / commit; RP-04 remains failed until re-test + re-review.

## TIMESTAMP vs DATETIME (A-TYP-008)

**Choice: `TIMESTAMP`** for Hibernate timestamp SqlTypes (`TIMESTAMP`, `TIMESTAMP_UTC`, and `TIMESTAMP WITH TIME ZONE` variants).

- Doc: `E:\Work\docs\content\reference\sql\datatype\datetime.md` documents both `DATETIME` and `TIMESTAMP`.
- TIMESTAMP supports fractional precision 0–6 (default 3) aligning with `getDefaultTimestampPrecision()=3`.
- Caveat documented in dialect Javadoc: Xugu TIMESTAMP may auto-fill current time when the column is omitted on INSERT; IT always binds explicit values.
- `DATE` remains date-only under `compatible_mode=NONE` (not mapped to DATETIME).

## Other mapping notes

| Topic | Mapping | Doc |
|---|---|---|
| BOOLEAN | Prefer `BOOLEAN` (not BIT) | `bool.md` |
| REAL | → `FLOAT` (REAL is FLOAT synonym) | `numerical.md` |
| DOUBLE | `double` (not `double precision`) | `numerical.md` |
| NUMERIC default precision | 12 | `numerical.md` |
| NCHAR/NVARCHAR/NCLOB | → CHAR/VARCHAR/CLOB (synonyms) | `character.md`, `large-object.md` |
| UUID | DDL type `GUID` | `guid.md` |
| JSON | DDL type `JSON` | `json.md` |
| BINARY/VARBINARY | bare `binary` (no `$l`; docs SSOT) ≤64KB via `getMaxVarbinaryLength()` | `binary.md` |
| LONGVARBINARY | `blob` (large binary path) | `large-object.md` |
| Quotes | `"` | `identifier.md` |
| JDBC `compatiblemode` | `NONE` (URL param) | `development/jdbc/.../set-param.md` |

## Integration evidence

- DB reachable: `jdbc:xugu://127.0.0.1:5138/SYSTEM?...&compatiblemode=NONE`
- IT log: `harness/evidence/implementer/P-003/mvn-test-integration.log`
- Observed DDL: `create table HIB_P003_DDL_PROBE (id integer not null, name varchar(64) not null, primary key (id))`
- Binary SchemaExport (fix): `create table HIB_P003_BINARY_PROBE (id integer not null, payload binary not null, primary key (id))`
- Flows: type round-trip (int/decimal/varchar/bool/date/timestamp/binary/blob/clob/guid/json), DDL create/drop + ALTER ADD COLUMN, JDBC commit/rollback, illegal-type failure cleanup

## Commands

| Command | Exit |
|---|---|
| `mvn -q -DskipTests package` | 0 |
| `mvn -q test` (IT skipped) | 0 |
| `mvn -q test -Dxugu.run.integration=true` | 0 |
| `python harness/scripts/verify.py --phase P-003 --evidence harness/evidence/implementer/P-003/verification.json` | 0 → **VERIFY PASS** |

## Explicitly NOT done

- Pagination / locks / identity / sequence / functions / SPI (later Phases)
- A-TYP-014…018, A-DDL-007…009 (延后)
- Reading `E:\Work\java\hibernate-dialect`
- git commit / tag / push
- Accept / RP-04 re-approve (await independent re-test + re-review)

## Next step

Independent **test** re-verify (RP-03 residual) → **reviewer** RP-04 re-review; keep RP-04 failed until approve.
