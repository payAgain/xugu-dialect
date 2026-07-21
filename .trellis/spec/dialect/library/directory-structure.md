# Directory Structure — dialect

> Where dialect code and tests live. Artifact id is `xugu-dialect`; directory name is `dialect/`.

---

## Module Layout

```text
dialect/
├── pom.xml                          # com.xugu:xugu-dialect:7.4.5.Final
└── src/
    ├── main/
    │   ├── java/com/xugu/dialect/
    │   │   ├── XuguDialect.java           # main Dialect subclass (hub)
    │   │   ├── XuguDialectResolver.java   # DialectResolver SPI
    │   │   ├── XuguDialectSelector.java   # selector used by resolver
    │   │   ├── aggregate/                 # AggregateSupport
    │   │   ├── config/                    # server/config helpers
    │   │   ├── ddl/                       # table/index DDL helpers
    │   │   ├── exception/                 # SQLException conversion
    │   │   ├── function/                  # SqmFunctionRegistry contributions
    │   │   ├── identity/                  # identity column support
    │   │   ├── internal/                  # keywords, locking internals
    │   │   ├── lock/                      # lock-table helpers
    │   │   ├── metadata/                  # catalog metadata helpers
    │   │   ├── pagination/                # LimitHandler + pagination alts
    │   │   ├── sequence/                  # SequenceSupport + extractor
    │   │   ├── sql/ast/                   # XuguSqlAstTranslator
    │   │   ├── temptable/                 # temp table strategies
    │   │   └── type/                      # JdbcType / DDL type support
    │   └── resources/META-INF/services/
    │       └── org.hibernate.engine.jdbc.dialect.spi.DialectResolver
    └── test/
        ├── java/com/xugu/dialect/
        │   ├── *Test.java                 # offline unit tests
        │   ├── it/                        # gated live IT (*IT.java)
        │   │   └── entities/              # IT-only JPA entities
        │   └── support/                   # XuguITGate, XuguTestConnection
        └── resources/sql-baselines/       # native SQL baseline fixtures
```

Parent reactor: repository-root `pom.xml` modules `dialect` + `demo-spring-boot`.
JDBC jar: repo-root `xugu-jdbc-12.3.6.jar` via Maven `system` + `systemPath`.

---

## Package Ownership

| Package | Owns | Must not |
|---------|------|----------|
| `com.xugu.dialect` | `XuguDialect`, resolver, selector | Demo entities, Spring types |
| `…type` | SqlTypes ↔ Xugu DDL, custom `JdbcType` | App-level converters (those belong in demo) |
| `…function` | HQL/native function registrations | Full undocumented function surface |
| `…exception` | Vendor code → Hibernate exceptions | Generic app error APIs |
| `…pagination` / `…sql.ast` | `LIMIT`/`OFFSET`, lock+limit order | `FETCH FIRST` as default pagination |
| `…support` (test) | IT gate + JDBC URL helpers | Production runtime code |

---

## Naming Conventions

- Production classes: `Xugu` + capability + role (`XuguLimitHandler`, `XuguIntervalJdbcType`).
- Unit tests: `Xugu*Test` next to the domain; live IT: `Xugu*IT` under `it/`.
- Matrix ids in method names or javadoc when locking a row (`_A_TYP_014`, `C-JSON-001`).
- SPI file content: fully qualified `com.xugu.dialect.XuguDialectResolver` (see `META-INF/services/...DialectResolver`).

---

## Examples (preferred patterns)

- Hub dialect: `dialect/src/main/java/com/xugu/dialect/XuguDialect.java`
- Function registry: `dialect/src/main/java/com/xugu/dialect/function/XuguFunctionRegistrations.java`
- Type support: `dialect/src/main/java/com/xugu/dialect/type/XuguIntervalTypeSupport.java`
- SPI: `dialect/src/main/resources/META-INF/services/org.hibernate.engine.jdbc.dialect.spi.DialectResolver`

---

## Anti-Patterns

- Putting dialect core logic under `demo-spring-boot/`.
- Adding a second root package outside `com.xugu.dialect`.
- Checking in production credentials; use env overrides (`XUGU_JDBC_URL`, `XUGU_USER`, `XUGU_PASSWORD`).
- Creating “utils” dump packages — prefer a named capability package (`type`, `ddl`, `function`, …).
