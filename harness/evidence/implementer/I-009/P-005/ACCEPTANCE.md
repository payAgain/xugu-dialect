# P-005 Acceptance Evidence (Implementer RP-01)

> Phase: `P-005` · Initiative: `I-009` · Build: `B-001`  
> Role: implementer · Matrix row: **A-TYP-018 UDT**

## Decision

- Decision: `accepted`
- Path: **known-limit-documented**
- Rationale: XuGu documents three UDT families — OBJECT, VARRAY, TABLE (`udt.md`). Hibernate 7.4 exposes no `SqlTypes` for schema-defined UDT entity columns; `XuguDialect#supportsJdbcUserDefinedTypes()` is `false`. Documented CREATE/DROP TYPE helpers locked in `XuguUdtTypeSupport`; gated native SQL IT validates OBJECT constructor insert, VARRAY column, and TABLE nested-table column per doc examples — not ORM UDT attribute mapping.

## Criteria table

| Criterion | Expected | Evidence | Status |
|---|---|---|---|
| UDT doc shapes | OBJECT/VARRAY/TABLE CREATE/DROP locked | `XuguUdtTypeSupport`; `XuguUdtTypeTest#createTypeSqlMatchesUdtDoc_A_TYP_018` | **PASS** |
| Known-limit flag | No false ORM UDT claim | `XuguDialect#supportsJdbcUserDefinedTypes()`; `XuguUdtTypeTest#dialectDoesNotClaimOrmUdtEntityMapping_A_TYP_018` | **PASS** |
| Unit tests | Offline green | `XuguUdtTypeTest` | **PASS** |
| Gated live IT | Native SQL when `XUGU_RUN_IT=true` | `XuguUdtTypeIT#udtNativeRoundTrip_A_TYP_018` | **PASS** / SKIPPED_INFRA when gate off |
| SSOT promotion | 延后 → known-limit-documented | `contracts/production-regression-baseline.md` A-TYP-018 | **PASS** |
| Negative anchor removed | No @Disabled deferred anchor for A-TYP-018 | `XuguNegativeRegressionBaselineTest` | **PASS** |
| No invented SQL | Shapes from udt.md only | reviewer RP-03 | pending |
| Offline build | `mvn -q -DskipTests package` green | implementer run | **PASS** (exit 0) |
| Offline test | `mvn -q test` green | implementer run | **PASS** (exit 0) |

## Validation (implementer)

| Command | Exit | Detail |
|---|---|---|
| `mvn -q -DskipTests package` | 0 | offline build |
| `mvn -q test` | 0 | offline reactor (IT gated/skipped) |
| `mvn -q -pl dialect -am test -Dtest=XuguUdtTypeTest` | 0 | 3 unit tests |
| `XUGU_RUN_IT=true mvn -q -pl dialect -am test -Dtest=XuguUdtTypeIT` | 1 (SKIPPED_INFRA) | Connection refused 127.0.0.1:5138; gate ON exercised IT path |

## Closed gaps

| matrix_id | status | entry_class#method | gate |
|---|---|---|---|
| A-TYP-018 | known-limit-documented | `XuguUdtTypeTest#documentedKindsLocked_A_TYP_018`; `XuguUdtTypeTest#createTypeSqlMatchesUdtDoc_A_TYP_018`; `XuguUdtTypeTest#dialectDoesNotClaimOrmUdtEntityMapping_A_TYP_018`; `XuguUdtTypeIT#udtNativeRoundTrip_A_TYP_018` | IT |

## Known-limit (A-TYP-018)

- Hibernate 7.4 has no `SqlTypes` for XuGu schema UDT columns — dialect does not emit generic UDT DDL via `columnType()`.
- No verified JDBC STRUCT/custom descriptor ORM entity round-trip on Xugu.
- OBJECT inheritance, TYPE BODY methods, and PL/SQL-only assignment paths are out of scope — native SQL IT covers documented SQL subset only.

## Doc citation

- `reference/sql/datatype/udt.md` §结构类型 — `CREATE OR REPLACE TYPE … AS OBJECT`; `obj_tab` constructor insert
- `reference/sql/datatype/udt.md` §数组类型 — `IS VARRAY(3) OF VARCHAR`; `udt_varray_tab` insert
- `reference/sql/datatype/udt.md` §嵌套表类型 — `IS TABLE OF BIGINT`; `tab_type` insert
- `reference/sql/datatype/udt.md` §删除类型 — `DROP TYPE`

## Files changed

- `dialect/src/main/java/com/xugu/dialect/type/XuguUdtTypeSupport.java`
- `dialect/src/main/java/com/xugu/dialect/XuguDialect.java`
- `dialect/src/test/java/com/xugu/dialect/XuguUdtTypeTest.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguUdtTypeIT.java`
- `dialect/src/test/java/com/xugu/dialect/XuguNegativeRegressionBaselineTest.java`
- `contracts/production-regression-baseline.md`
- `harness/evidence/implementer/I-009/P-005/ACCEPTANCE.md`
- `harness/handoffs/implementer/I-009-P-005.yaml`
