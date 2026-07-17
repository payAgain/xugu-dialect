# demo-spring-boot

Spring Boot **4.1.0** demo that uses `com.xugu:xugu-dialect:7.4.5.Final` and the repo-root Xugu JDBC jar against a real XuguDB.

## Requirements

- JDK 17+ (compiler release 17)
- Maven 3.9+
- Repo-root `xugu-jdbc-12.3.6.jar`
- Live XuguDB for runtime / gated IT (offline `mvn test` does not need DB)

## Hibernate version force

Spring Boot 4.1.0 BOM defaults `hibernate.version` to **7.4.1.Final**. This module sets:

```xml
<hibernate.version>7.4.5.Final</hibernate.version>
```

and re-declares `hibernate-core` in `dependencyManagement` after the Boot BOM import.

Proof:

```bash
mvn -pl demo-spring-boot dependency:tree -Dincludes=org.hibernate.orm:hibernate-core
```

Expect `hibernate-core:jar:7.4.5.Final` (not 7.4.1).

## Connection env keys

| Key | Default (local reference only) | Notes |
|---|---|---|
| `XUGU_JDBC_URL` | `jdbc:xugu://127.0.0.1:5138/SYSTEM?compatiblemode=NONE` | Prefer env in CI/shared envs |
| `XUGU_USER` | `SYSDBA` | Local reference; do not commit production secrets |
| `XUGU_PASSWORD` | `SYSDBA` | Local reference; do not commit production secrets |
| `xugu.run.integration` | `false` | Maven/system property gate for IT |
| `XUGU_RUN_IT` | unset | Env alternative to enable IT |

Also accepted via Spring datasource properties if you override `spring.datasource.*` instead of the `XUGU_*` env keys.

Driver: `com.xugu.cloudjdbc.Driver`  
Dialect: explicit `com.xugu.dialect.XuguDialect` in `application.yml` (DialectResolver SPI also works if unset).

## Run the demo app

From repo root (with XuguDB reachable):

```bash
# optional env overrides
set XUGU_JDBC_URL=jdbc:xugu://127.0.0.1:5138/SYSTEM?compatiblemode=NONE
set XUGU_USER=SYSDBA
set XUGU_PASSWORD=SYSDBA

mvn -pl demo-spring-boot -am spring-boot:run
```

On startup, `DemoStartupCrudRunner` persists and finds a `HIB_DEMO_PERSON` row (log: `Xugu Hibernate demo CRUD OK`).

Disable startup CRUD: `--xugu.demo.startup-crud=false`

## Tests

```bash
# offline (default) — skips gated IT
mvn -q test
mvn -q -pl demo-spring-boot -am test

# live DB IT (use -am so reactor sibling xugu-dialect resolves)
mvn -q -pl demo-spring-boot -am test -Dxugu.run.integration=true
# or full reactor:
mvn -q test -Dxugu.run.integration=true
```

IT classes (`@EnabledIf` on `XuguIntegrationGate`):

| Class | Purpose |
|---|---|
| `DemoPersonCrudIT` | Spring Boot + JPA persist/find + IDENTITY |
| `DemoBootBaselineSmokeTest` | SessionFactory explicit dialect, JDBC pool, JPQL, Pageable pagination |

Cleanup deletes `HIB_DEMO_PERSON` rows after each test; `@AfterAll` drops the demo table.

## Table

| Table | Purpose |
|---|---|
| `HIB_DEMO_PERSON` | Demo entity (`DemoPerson`) |

## Forbidden reminders

- No production secrets in VCS
- No dialect implementation inside this module
- No reference to sibling `hibernate-dialect` sources
