# ACCEPTANCE — I-010 / P-004 (RP-01 implementer)

> Role: implementer · Matrix row: **A-TYP-017 Geometric** POINT entity ORM
> Branch: `feat/i-010-orm-hql-quality-completion`
> Completed: 2026-07-21T15:30:00+08:00

## Decision

- **Path:** entity ORM **positive** (not honest-negative)
- **SSOT:** remain **known-limit-documented** until live entity IT PASS — do **not** claim covered-live while SKIPPED_INFRA
- **Rationale:** XuGu POINT columns are string-valued literals (`(x,y)` per `geometric.md`). Hibernate core has no default `PointJdbcType`. Contributed `XuguPointJdbcType` binds/extracts with `setString`/`getString` for `SqlTypes.POINT` and bounded `SqlTypes.GEOMETRY` → POINT DDL. LINE/LSEG/BOX/PATH/POLYGON/CIRCLE stay native/tooling only.

## Recommended mapping

```java
@JdbcTypeCode(SqlTypes.POINT)
private String point;
```

Bounded GEOMETRY→POINT alias:

```java
@JdbcTypeCode(SqlTypes.GEOMETRY)
private String geometry;
```

**Do not** force ORM for LINE/LSEG/BOX/PATH/POLYGON/CIRCLE — native SQL / tooling only.

## Checklist

| Item | Evidence | Result |
|---|---|---|
| Entity mapping | `I010P004PointEntity` (`String` + `@JdbcTypeCode(POINT|GEOMETRY)`) | **PASS** |
| Dialect JDBC hook | `XuguPointJdbcType` + `XuguDialect#contributeTypes` | **PASS** |
| Offline unit | `XuguGeometricTypeTest` (4 tests) | **PASS** |
| Gated entity IT | `XuguGeometricTypeAndFunctionsIT#pointEntityOrmRoundTrip_A_TYP_017` | **PASS** (code) / **SKIPPED_INFRA** (live) |
| Native IT retained | `geometricTypesNativeRoundTrip_A_TYP_017` | retained |
| Non-POINT subtypes | LINE…CIRCLE remain native/tooling | **PASS** |
| SSOT honesty | baseline § A-TYP-017 updated; not covered-live | **PASS** |

## Delivered files

- `dialect/src/main/java/com/xugu/dialect/type/XuguPointJdbcType.java`
- `dialect/src/main/java/com/xugu/dialect/type/XuguGeometricTypeSupport.java` (doc)
- `dialect/src/main/java/com/xugu/dialect/XuguDialect.java` (`contributeTypes`)
- `dialect/src/test/java/com/xugu/dialect/it/entities/I010P004PointEntity.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguGeometricTypeAndFunctionsIT.java`
- `dialect/src/test/java/com/xugu/dialect/XuguGeometricTypeTest.java`
- `contracts/production-regression-baseline.md`
- `contracts/feature-matrix-definition-a.md`

## Residual

- **docs:** update `docs/user-guide/04-feature-matrix.md` / `05-troubleshooting.md` A-TYP-017 wording (POINT entity path + recommended `String` mapping) — docs ownership
- **live:** re-run `XUGU_RUN_IT=true` when DB up → promote SSOT to covered-live if entity IT PASS
- **RP-03 reviewer:** readonly audit (no fake covered-live; LINE…CIRCLE remain non-ORM known-limit)

## Observed flow

- `point-entity-orm-roundtrip`: offline unit green; gated IT present; live SKIPPED_INFRA (`192.168.2.239:5138` / `127.0.0.1:5138` refused)
