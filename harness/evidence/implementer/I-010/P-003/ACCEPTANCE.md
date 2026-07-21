# ACCEPTANCE — I-010 / P-003 (RP-01 implementer)

> Role: implementer · Matrix row: **A-TYP-016 XML** entity ORM
> Branch: `feat/i-010-orm-hql-quality-completion`
> Completed: 2026-07-21T15:16:00+08:00

## Decision

- **Path:** entity ORM **positive** (not honest-negative)
- **SSOT:** remain **known-limit-documented** until live entity IT PASS — do **not** claim covered-live while SKIPPED_INFRA
- **Rationale:** Hibernate default `XmlJdbcType` binds via `java.sql.SQLXML` (`createSQLXML` / `getSQLXML`). XuGu XML columns are string-valued (native IT). Contributed `XuguXmlJdbcType` binds/extracts with `setString`/`getString` for `SqlTypes.SQLXML`.

## Recommended mapping

```java
@JdbcTypeCode(SqlTypes.SQLXML)
private String xml;
```

**Do not** map `java.sql.SQLXML` / rely on default Hibernate `XmlJdbcType` — Xugu JDBC SQLXML API remains unproven.

## Checklist

| Item | Evidence | Result |
|---|---|---|
| Entity mapping | `I010P003XmlEntity` (`String` + `@JdbcTypeCode(SQLXML)`) | **PASS** |
| Dialect JDBC hook | `XuguXmlJdbcType` + `XuguDialect#contributeTypes` override | **PASS** |
| Offline unit | `XuguXmlTypeTest` (4 tests) | **PASS** |
| Gated entity IT | `XuguXmlTypeAndFunctionsIT#xmlEntityOrmRoundTrip_A_TYP_016` | **PASS** (code) / **SKIPPED_INFRA** (live) |
| Native IT retained | `xmlTypeNativeRoundTrip_A_TYP_016` | retained |
| SSOT honesty | baseline § A-TYP-016 updated; not covered-live | **PASS** |

## Delivered files

- `dialect/src/main/java/com/xugu/dialect/type/XuguXmlJdbcType.java`
- `dialect/src/main/java/com/xugu/dialect/type/XuguXmlTypeSupport.java` (doc)
- `dialect/src/main/java/com/xugu/dialect/XuguDialect.java` (`contributeTypes`)
- `dialect/src/test/java/com/xugu/dialect/it/entities/I010P003XmlEntity.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguXmlTypeAndFunctionsIT.java`
- `dialect/src/test/java/com/xugu/dialect/XuguXmlTypeTest.java`
- `contracts/production-regression-baseline.md`
- `contracts/feature-matrix-definition-a.md`

## Residual

- **docs:** update `docs/user-guide/04-feature-matrix.md` / `05-troubleshooting.md` A-TYP-016 wording (entity path + recommended `String` mapping) — docs ownership
- **live:** re-run `XUGU_RUN_IT=true` when DB up → promote SSOT to covered-live if entity IT PASS
- **RP-03 reviewer:** readonly audit (no fake covered-live)

## Observed flow

- `xml-entity-sqlxml-roundtrip`: offline unit green; gated IT present; live SKIPPED_INFRA (`192.168.2.239:5138` refused)
