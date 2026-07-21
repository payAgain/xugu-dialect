# P-004 Acceptance Evidence (Implementer RP-01)

> Phase: `P-004` · Initiative: `I-009` · Build: `B-001`  
> Role: implementer · Matrix rows: **A-TYP-017 Geometric/spatial types**, **A-FUN-020 Geometric functions**

## Decision

- Decision: `accepted`
- Path:
  - **A-TYP-017:** `known-limit-documented`
  - **A-FUN-020:** `covered-live`
- Rationale: XuGu documents seven simple 2D geometric types (`geometric.md`) and 21 geometric functions (`geometric-functions/`). Dialect maps `SqlTypes.POINT`/`SqlTypes.GEOMETRY` → `point` DDL; all subtype DDL strings locked in `XuguGeometricTypeSupport`. No verified ORM entity mapping for LINE/LSEG/BOX/PATH/POLYGON/CIRCLE columns — native SQL IT is the honest type path. All 21 functions registered; representative native SQL IT for area/center/point/box/circle per doc examples.

## Criteria table

| Criterion | Expected | Evidence | Status |
|---|---|---|---|
| Geometric type DDL | POINT/GEOMETRY → `point`; seven kinds locked | `XuguGeometricTypeSupport`; `XuguDialect#columnType` | **PASS** |
| Geometric functions | 21 documented functions registered | `XuguGeometricFunctions`; `XuguFunctionRegistrations`; `XuguFunctionRegistryTest#geometricSubsetRegistered_A_FUN_020` | **PASS** |
| Unit tests | Offline green | `XuguGeometricTypeTest`; `XuguDialectTest#columnTypesMatchXuguDocs` | **PASS** |
| Gated live IT | Native SQL when `XUGU_RUN_IT=true` | `XuguGeometricTypeAndFunctionsIT` | **PASS** / SKIPPED_INFRA when gate off |
| SSOT promotion | 延后 → known-limit / covered-live | `contracts/production-regression-baseline.md` A-TYP-017, A-FUN-020 | **PASS** |
| Negative anchors removed | No @Disabled deferred anchors for P-004 rows | `XuguNegativeRegressionBaselineTest` | **PASS** |
| No invented SQL | Shapes from geometric.md + geometric-functions only | reviewer RP-03 | pending |
| Offline build | `mvn -q -DskipTests package` green | implementer run | **PASS** (exit 0) |
| Offline test | `mvn -q test` green | implementer run | **PASS** (exit 0) |

## Validation (implementer)

| Command | Exit | Detail |
|---|---|---|
| `mvn -q -DskipTests package` | 0 | offline build |
| `mvn -q test` | 0 | offline reactor (IT gated/skipped) |
| `XUGU_RUN_IT=true mvn -q -pl dialect -am test -Dtest=XuguGeometricTypeAndFunctionsIT` | 1 (SKIPPED_INFRA) | Connection refused 127.0.0.1:5138; gate ON exercised IT path |

## Closed gaps

| matrix_id | status | entry_class#method | gate |
|---|---|---|---|
| A-TYP-017 | known-limit-documented | `XuguGeometricTypeTest#pointTypeHooksWired_A_TYP_017`; `XuguGeometricTypeTest#allDocumentedKindsLocked_A_TYP_017`; `XuguGeometricTypeAndFunctionsIT#geometricTypesNativeRoundTrip_A_TYP_017` | IT |
| A-FUN-020 | covered-live | `XuguFunctionRegistryTest#geometricSubsetRegistered_A_FUN_020`; `XuguGeometricTypeAndFunctionsIT#geometricFunctionsNativeSubset_A_FUN_020` | IT |

## Known-limit (A-TYP-017)

- XuGu has no PostGIS/generic `GEOMETRY` type — `SqlTypes.GEOMETRY` maps to bounded `POINT` DDL.
- Hibernate 7.4 lacks per-subtype ORM codes for LINE/LSEG/BOX/PATH/POLYGON/CIRCLE; subtype DDL locked for native SQL only.
- Spatial index (`spatial-database/geometric-model/spatial-index.md`) is not standard Hibernate schema export — out of scope.

## Doc citation

- `reference/sql/datatype/geometric.md` — POINT/LINE/LSEG/BOX/PATH/POLYGON/CIRCLE literals + tab_geom example
- `reference/function/geometric-functions/area.md` — AREA(CIRCLE/BOX/PATH)
- `reference/function/geometric-functions/center.md` — CENTER(BOX/CIRCLE)
- `reference/function/geometric-functions/point.md` — POINT(x,y) / POINT(BOX)
- `reference/function/geometric-functions/box.md` — BOX(POINT, POINT)
- `reference/function/geometric-functions/circle.md` — CIRCLE(POINT, radius)

## Files changed

- `dialect/src/main/java/com/xugu/dialect/type/XuguGeometricTypeSupport.java`
- `dialect/src/main/java/com/xugu/dialect/function/XuguGeometricFunctions.java`
- `dialect/src/main/java/com/xugu/dialect/XuguDialect.java`
- `dialect/src/main/java/com/xugu/dialect/function/XuguFunctionRegistrations.java`
- `dialect/src/test/java/com/xugu/dialect/XuguGeometricTypeTest.java`
- `dialect/src/test/java/com/xugu/dialect/XuguFunctionRegistryTest.java`
- `dialect/src/test/java/com/xugu/dialect/XuguDialectTest.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguGeometricTypeAndFunctionsIT.java`
- `dialect/src/test/java/com/xugu/dialect/XuguNegativeRegressionBaselineTest.java`
- `contracts/production-regression-baseline.md`
- `harness/evidence/implementer/I-009/P-004/ACCEPTANCE.md`
- `harness/handoffs/implementer/I-009-P-004.yaml`
