# Spring JPA Patterns — demo-spring-boot

> How the demo configures and uses Hibernate + Xugu dialect.

---

## Configuration (`application.yml`)

Local reference defaults only — override in shared/CI environments.

| Setting | Local pattern | Notes |
|---------|---------------|-------|
| Datasource URL | `${XUGU_JDBC_URL:jdbc:xugu://127.0.0.1:5138/SYSTEM?compatiblemode=NONE}` | NONE mode required |
| User / password | `${XUGU_USER:SYSDBA}` / `${XUGU_PASSWORD:SYSDBA}` | Prefer env |
| Driver | `com.xugu.cloudjdbc.Driver` | Matches dialect IT |
| `hibernate.dialect` | `com.xugu.dialect.XuguDialect` | Explicit; SPI also works when unset |
| `ddl-auto` | `update` | Demo convenience |
| `json_functions_enabled` | `true` | Needed for HQL JSON functions |
| `preferred_uuid_jdbc_type` | `VARCHAR` | Aligns with `UuidAsVarcharConverter` |
| Flyway | `enabled: false` by default | `DemoFlywayIT` enables explicitly |
| Startup CRUD | `xugu.demo.startup-crud: true` | Disable in IT via `@TestPropertySource` |

Reference: `demo-spring-boot/src/main/resources/application.yml`.

---

## Hibernate Version Force

Boot 4.1.0 BOM defaults Hibernate to 7.4.1.Final. Demo POM **must** keep:

```xml
<hibernate.version>7.4.5.Final</hibernate.version>
```

and a dependencyManagement entry for `hibernate-core` **after** the Boot BOM import so 7.4.5 wins. Prove with `mvn -pl demo-spring-boot dependency:tree` when touching BOM/versions.

---

## Entities & Repositories

- Prefer simple JPA entities under `com.xugu.demo.entity` with Spring Data repos under `repository`.
- IDENTITY demos: `DemoPerson` (see `DemoPersonCrudIT` for A-IDN-003/004 consumer evidence).
- Sequence demos: `DemoSeqTicket` + `DemoSequenceIT`.
- JSON: `DemoJsonDoc` + `@JdbcTypeCode` / map attributes as existing; keep Jackson on classpath (Boot starter).
- UUID: use `UuidAsVarcharConverter` + varchar(36) alignment — do not switch to a binary UUID strategy without contract/matrix review.
- Associations: `DemoDept` / `DemoDeptMember` patterns in `DemoAssociationIT`.

Demo entities are **examples for integrators**, not a domain model product.

---

## Startup Runner

`DemoStartupCrudRunner`:

- `@ConditionalOnProperty(name = "xugu.demo.startup-crud", havingValue = "true", matchIfMissing = true)`
- Persists/finds `DemoPerson`, logs success with SLF4J: `"Xugu Hibernate demo CRUD OK: {}"`
- IT should set `xugu.demo.startup-crud=false` to avoid interfering with test data

---

## Flyway (demo-only bridge)

- Starter present; default disabled.
- Xugu is recognized via `XuguFlywayDatabaseType` + `org.flywaydb.database.oracle.Xugu*` classes and `META-INF/services/...Plugin`.
- Migrations under `db/migration/` (e.g. `V1__hib_demo_flyway_marker.sql`).
- Keep this bridge in the demo module; do not move Flyway shims into `dialect/`.

---

## Logging

- Use SLF4J (`LoggerFactory`) in demo components when needed.
- Prefer `info` for successful smoke signals; avoid logging passwords or full credentialized URLs.
- SQL visibility: `spring.jpa.show-sql: true` is acceptable for local demo; do not treat it as a production logging standard.

---

## Anti-Patterns

- Re-implementing dialect SQL generation inside demo services.
- Hard-requiring Flyway for all demos/ITs (breaks offline + ddl-auto paths).
- Dropping the Hibernate 7.4.5 force “to simplify the POM”.
- Adding REST controllers / API layers unless a future initiative explicitly expands demo scope — current demo is JPA/repository focused.
