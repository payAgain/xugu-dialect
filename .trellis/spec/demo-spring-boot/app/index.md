# Demo Package Guidelines (`demo-spring-boot`)

> Coding guidance for the Spring Boot consumer module that proves `xugu-dialect` against real XuguDB.
> Demo must **consume** the dialect — never host dialect core implementation.

---

## Pre-Development Checklist

- [ ] Change belongs in the consumer path (entity/repo/config/IT/Flyway), not in `dialect/`
- [ ] Hibernate stays **7.4.5.Final** (forced over Boot BOM’s 7.4.1) — see `demo-spring-boot/pom.xml`
- [ ] Defaults keep `compatiblemode=NONE` and env-overridable credentials
- [ ] Offline tests stay green with IT gate off
- [ ] Read `contracts/demo-spring-boot.contract.md` when changing public demo behavior
- [ ] Skim thinking guides if the change spans dialect + demo + docs

---

## Guidelines Index

| Guide | Description |
|-------|-------------|
| [Directory Structure](./directory-structure.md) | Packages, resources, Flyway SPI shims |
| [Spring JPA Patterns](./spring-jpa-patterns.md) | Entities, repos, `application.yml`, startup runner |
| [Testing](./testing.md) | Offline smoke vs gated `@SpringBootTest` IT |
| [Quality Guidelines](./quality-guidelines.md) | Forbidden patterns, verify commands |

---

## Quality Check

- [ ] No dialect implementation code added under `com.xugu.demo` (except intentional consumer adapters like UUID converter / Flyway DB-type bridge)
- [ ] `hibernate.version` property still 7.4.5.Final after POM edits
- [ ] Flyway remains disabled by default; IT enables explicitly
- [ ] `mvn -q -pl demo-spring-boot -am test` passes offline
- [ ] Live claims use `XUGU_RUN_IT=true` / `-Dxugu.run.integration=true`

---

## Thinking Guides

| Guide | When |
|-------|------|
| [Code Reuse](../../guides/code-reuse-thinking-guide.md) | Shared JDBC/gate helpers, entity patterns |
| [Cross-Layer](../../guides/cross-layer-thinking-guide.md) | Dialect feature → demo entity → user-guide |

---

## Analysis Assumptions

- Demo Flyway support uses Oracle Flyway module + local `org.flywaydb.database.oracle.Xugu*` shims because JDBC reports `XuguDB` — treat as demo-only integration, not dialect core.
- Charter Accept dialect rollup lives in dialect SSOT; demo contributes consumer golden-path evidence (e.g. A-XCUT-009 / demo smoke call-outs).
