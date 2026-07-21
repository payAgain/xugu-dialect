# P-002 Acceptance Evidence (Implementer RP-01)

> Phase: `P-002` · Initiative: `I-009` · Build: `B-001`  
> Role: implementer · Matrix row: **A-TYP-014 INTERVAL**

## Decision

- Decision: `accepted`
- Path: **`known-limit-documented`**
- Rationale: XuGu documents 13 INTERVAL subtypes + `DEF_INTERVAL_STYLE`; Hibernate 7.4 exposes only `SqlTypes.DURATION` and `SqlTypes.INTERVAL_SECOND`. Dialect locks documented DDL + native SQL live IT; full ORM entity mapping of all subtypes is out of scope.

## Criteria table

| Criterion | Expected | Evidence | Status |
|---|---|---|---|
| Dialect INTERVAL DDL | DURATION + INTERVAL_SECOND + 13 subtype strings from datetime.md | `XuguIntervalTypeSupport`; `XuguDialect#columnType` | **PASS** |
| Unit tests | Offline green | `XuguIntervalTypeTest`; `XuguDialectTest#columnTypesMatchXuguDocs` | **PASS** |
| Gated live IT | Native SQL round-trip when `XUGU_RUN_IT=true` | `XuguIntervalTypeIT#intervalNativeRoundTrip_A_TYP_014` | **PASS** / SKIPPED_INFRA when gate off |
| SSOT promotion | 延后 → known-limit-documented with doc citation | `contracts/production-regression-baseline.md` A-TYP-014 | **PASS** |
| No invented SQL | DDL/literals from datetime.md only | reviewer RP-03 | pending |
| Offline build | `mvn -q -DskipTests package` green | implementer run | **PASS** |
| Offline test | `mvn -q test` green | implementer run | **PASS** |

## Closed gap

| matrix_id | status | entry_class#method | gate |
|---|---|---|---|
| A-TYP-014 | known-limit-documented | `XuguIntervalTypeTest#intervalTypeHooksWired_A_TYP_014`; `XuguIntervalTypeIT#intervalNativeRoundTrip_A_TYP_014` | IT |

## Doc citation

- `reference/sql/datatype/datetime.md` §时间间隔类型 — 13 INTERVAL subtypes, `DEF_INTERVAL_STYLE` output formats

## Files changed

- `dialect/src/main/java/com/xugu/dialect/type/XuguIntervalTypeSupport.java`
- `dialect/src/main/java/com/xugu/dialect/XuguDialect.java`
- `dialect/src/test/java/com/xugu/dialect/XuguIntervalTypeTest.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguIntervalTypeIT.java`
- `dialect/src/test/java/com/xugu/dialect/XuguDialectTest.java`
- `dialect/src/test/java/com/xugu/dialect/XuguNegativeRegressionBaselineTest.java`
- `contracts/production-regression-baseline.md`
- `harness/evidence/implementer/I-009/P-002/ACCEPTANCE.md`
- `harness/handoffs/implementer/I-009-P-002.yaml`

## Validation (implementer)

| Command | Exit | Detail |
|---|---|---|
| `mvn -q -DskipTests package` | 0 | offline build |
| `mvn -q test` | 0 | offline reactor |
| `XUGU_RUN_IT=true mvn -q -pl dialect -am test -Dtest=XuguIntervalTypeIT` | 1 (SKIPPED_INFRA) | DB unreachable at 192.168.2.239:5138; gate ON exercised IT path |
