# Testing — demo-spring-boot

> Offline smoke always runs. Live `@SpringBootTest` IT requires real XuguDB + gate.

---

## Test Layers

| Layer | Example | Gate |
|-------|---------|------|
| Offline unit/smoke | `DemoOfflineSmokeTest` | none |
| Live IT | `it/DemoPersonCrudIT`, `it/DemoBootBaselineSmokeTest`, `DemoTypesIT`, `DemoJsonIT`, `DemoFlywayIT`, … | `XuguIntegrationGate` |
| Support | `support/XuguIntegrationGate`, `support/DemoXuguJdbc` | — |

---

## Integration Gate

Same flags as dialect:

```text
-Dxugu.run.integration=true
# or
XUGU_RUN_IT=true
```

Implementation: `com.xugu.demo.support.XuguIntegrationGate` (mirrors dialect `XuguITGate`; keep semantics identical).

Typical IT class annotations:

```java
@SpringBootTest
@EnabledIf("com.xugu.demo.support.XuguIntegrationGate#isEnabled")
@TestPropertySource(properties = { "xugu.demo.startup-crud=false" })
@Transactional
```

See `DemoPersonCrudIT`.

---

## What to Cover in Demo IT

Demo IT proves the **consumer golden path**, not a second copy of every dialect matrix row:

- Boot + JPA CRUD / types / JSON / functions / locks / sequences / associations
- SPI auto-resolve (`DemoSpiDialectAutoResolveIT`)
- Schema validate / surface (`DemoSchemaSurfaceIT`, `DemoValidateStartupIT`)
- Constraint rollback, read-only tx, bulk mutation as existing
- Flyway path when explicitly enabled (`DemoFlywayIT`)
- UUID/JSON out-of-box (`DemoUuidJsonOutOfBoxIT`)

When a dialect matrix row lists a demo entrypoint, keep that method stable or update `contracts/production-regression-baseline.md`.

---

## Verification Commands

```bash
mvn -q -pl demo-spring-boot -am test
mvn -q -pl demo-spring-boot -am test -Dxugu.run.integration=true

# run app (needs live DB):
mvn -pl demo-spring-boot -am spring-boot:run
```

Runnable jar must include system-scope JDBC (`spring-boot-maven-plugin` `includeSystemScope=true` already configured).

---

## Anti-Patterns

- Ungated IT that fails offline CI.
- Using mocks instead of real XuguDB for demo live claims.
- Leaving `startup-crud=true` in IT and fighting leftover rows.
- Diverging gate property names from dialect (`xugu.run.integration` / `XUGU_RUN_IT`).
