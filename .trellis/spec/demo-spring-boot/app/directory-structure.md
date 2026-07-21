# Directory Structure — demo-spring-boot

> Spring Boot 4.1.0 module that depends on `com.xugu:xugu-dialect` and root JDBC jar.

---

## Module Layout

```text
demo-spring-boot/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── com/xugu/demo/
    │   │   │   ├── DemoApplication.java
    │   │   │   ├── DemoStartupCrudRunner.java
    │   │   │   ├── entity/                 # JPA entities
    │   │   │   ├── repository/             # Spring Data JPA repos
    │   │   │   └── flyway/                 # XuguFlywayDatabaseType bridge
    │   │   └── org/flywaydb/database/oracle/
    │   │       ├── XuguDatabase.java       # Flyway DB shim (demo-local)
    │   │       ├── XuguConnection.java
    │   │       └── XuguSchema.java
    │   └── resources/
    │       ├── application.yml
    │       ├── db/migration/               # Flyway SQL (disabled by default)
    │       └── META-INF/services/
    │           └── org.flywaydb.core.extensibility.Plugin
    └── test/
        └── java/com/xugu/demo/
            ├── DemoOfflineSmokeTest.java   # offline
            ├── it/*IT.java                 # gated SpringBootTest IT
            └── support/
                ├── XuguIntegrationGate.java
                └── DemoXuguJdbc.java
```

---

## Package Ownership

| Area | Owns | Must not |
|------|------|----------|
| `com.xugu.demo` | Boot app, startup CRUD, demo entities/repos | Hibernate Dialect subclasses |
| `com.xugu.demo.flyway` + `org.flywaydb…Xugu*` | Flyway recognition for XuguDB | Replace dialect DDL generation |
| `com.xugu.demo.support` (test) | IT gate + JDBC helpers | Production secrets hardcoding without env override |

Dependency direction (non-negotiable):

```text
demo-spring-boot → xugu-dialect → (Hibernate API + Xugu JDBC)
```

---

## Naming Conventions

- Entities: `Demo*` (`DemoPerson`, `DemoJsonDoc`, …)
- Repositories: `Demo*Repository`
- IT: `Demo*IT` under `it/`, gated with `@EnabledIf(XuguIntegrationGate)`
- Offline: `DemoOfflineSmokeTest`, `DemoBootBaselineSmokeTest` patterns as existing

---

## Examples

- Entry: `DemoApplication`
- Config: `src/main/resources/application.yml`
- Consumer CRUD: `DemoPerson` + `DemoPersonRepository` + `DemoPersonCrudIT`
- SPI auto-resolve IT: `DemoSpiDialectAutoResolveIT`
