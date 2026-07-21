# ACCEPTANCE — I-010 / P-002 (RP-01 implementer)

> Role: implementer · Matrix row: **A-TYP-014 INTERVAL** entity ORM
> Branch: `feat/i-010-orm-hql-quality-completion`
> Completed: 2026-07-21T15:05:00+08:00

## Decision

- **Path:** entity ORM positive (not honest-negative)
- **SSOT:** remain **known-limit-documented** until live entity IT PASS — do **not** claim covered-live offline
- **Rationale:** Hibernate default `DurationJdbcType` binds NUMERIC; XuGu INTERVAL columns are string-valued. Contributed `XuguIntervalJdbcType` binds/extracts documented SQL_STANDARD literals for `SqlTypes.DURATION` / `INTERVAL_SECOND`. The other 11 XuGu subtypes stay tooling / native-SQL only.

## Checklist

| Item | Evidence | Result |
|---|---|---|
| Entity mapping | `I010P002IntervalEntity` (`@JdbcTypeCode` DURATION + INTERVAL_SECOND) | **PASS** |
| Dialect JDBC hook | `XuguIntervalJdbcType` + `XuguDialect#contributeTypes` override | **PASS** |
| Offline unit | `XuguIntervalTypeTest` (4 tests) | **PASS** |
| Gated entity IT | `XuguIntervalTypeIT#intervalEntityOrmRoundTrip_A_TYP_014` | **PASS** (code) / **SKIPPED_INFRA** (live) |
| Native IT retained | `intervalNativeRoundTrip_A_TYP_014` | retained |
| SSOT honesty | baseline § A-TYP-014 updated; not covered-live | **PASS** |

## Delivered files

- `dialect/src/main/java/com/xugu/dialect/type/XuguIntervalJdbcType.java`
- `dialect/src/main/java/com/xugu/dialect/type/XuguIntervalTypeSupport.java` (doc)
- `dialect/src/main/java/com/xugu/dialect/XuguDialect.java` (`contributeTypes`)
- `dialect/src/test/java/com/xugu/dialect/it/entities/I010P002IntervalEntity.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguIntervalTypeIT.java`
- `dialect/src/test/java/com/xugu/dialect/XuguIntervalTypeTest.java`
- `contracts/production-regression-baseline.md`
- `contracts/feature-matrix-definition-a.md`

## Residual

- **docs:** update `docs/user-guide/04-feature-matrix.md` / `05-troubleshooting.md` A-TYP-014 wording (entity path + covered-live gate) — docs ownership
- **live:** re-run `XUGU_RUN_IT=true` when DB up → promote SSOT to covered-live if entity IT PASS
- **RP-03 reviewer:** readonly audit

## Observed flow

- `interval-entity-orm-roundtrip`: offline unit green; gated IT present; live SKIPPED_INFRA (`192.168.2.239:5138` refused)
