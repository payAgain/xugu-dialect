# demo-spring-boot — Consumer Contract

> Module: `demo-spring-boot`  
> GAV parent: `com.xugu:xugu-dialect-parent:7.4.5.Final`  
> Spring Boot: **4.1.0** · Hibernate: **7.4.5.Final** (forced over Boot BOM 7.4.1)

## Purpose

Reference Spring Boot consumer for `xugu-dialect` against real XuguDB. SSOT for Boot wiring lives in
[`docs/user-guide/02-configuration.md`](../docs/user-guide/02-configuration.md) and
[`contracts/consumer-path-baseline.md`](consumer-path-baseline.md).

## I-008 Q3 — UUID / JSON out-of-box checklist

Integrators on Xugu **must** apply the same defaults as this demo (dialect jar alone is insufficient):

| # | Requirement | Demo location |
|---|---|---|
| 1 | `spring-boot-starter-jackson` (Hibernate JSON **`FormatMapper`**) | `demo-spring-boot/pom.xml` |
| 2 | UUID → `varchar(36)` + `UuidAsVarcharConverter` on entity field | `DemoTypedSample#guidVal`, `UuidAsVarcharConverter` |
| 3 | `hibernate.type.preferred_uuid_jdbc_type: VARCHAR` | `application.yml` |
| 4 | `hibernate.query.hql.json_functions_enabled=true` | `application.yml` |

### Evidence (P-006)

| Kind | Anchor |
|---|---|
| Offline smoke | `DemoOfflineSmokeTest#applicationYmlDocumentsExplicitDialectAndEnvKeys`, `#jacksonOnClasspathForHibernateJsonFormatMapper`, `#typedSampleGuidUsesUuidAsVarcharConverter` |
| Gated live IT | `DemoUuidJsonOutOfBoxIT#uuidAndJsonGoldenPathWithDefaultBootWiring` |
| Complementary live | `DemoTypesIT`, `DemoJsonIT` (Layer C′ matrix rows) |

Gate: `-Dxugu.run.integration=true` or env `XUGU_RUN_IT=true`.

## Boundaries

- No dialect implementation in this module.
- JDBC driver: `com.xugu.cloudjdbc.Driver` (system-scope `xugu-jdbc-12.3.6.jar` at repo root).
- Default JDBC URL includes `compatiblemode=NONE` (Charter).
- No production secrets in committed config — env placeholders only.

## Related

- User guide: [`02-configuration.md`](../docs/user-guide/02-configuration.md) § UUID/JSON Boot checklist  
- Consumer path SSOT: [`consumer-path-baseline.md`](consumer-path-baseline.md)
