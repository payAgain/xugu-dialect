# Quality Guidelines — dialect

> Non-negotiables and review checklist for `com.xugu:xugu-dialect`.

---

## Locked Baselines

| Item | Value | SSOT |
|------|-------|------|
| Hibernate | 7.4.5.Final | ADR-0001, parent POM |
| JDK | 17 | parent `maven.compiler.release` |
| GAV | `com.xugu:xugu-dialect:7.4.5.Final` | Charter |
| JDBC | repo-root `xugu-jdbc-12.3.6.jar` | Charter |
| compatible_mode | NONE | Charter / `XuguTestConnection` |
| Accept rollup (I-010) | **91/98 covered-live + 7 known-limit-documented** | `contracts/production-regression-baseline.md` |

If baselines change, update ADR/contracts first — do not silently bump versions in code only.

---

## Forbidden Patterns

1. Extending `MySQLDialect` / `OracleDialect` (or subclasses).
2. Reading, porting, or diffing against `E:\Work\java\hibernate-dialect` / legacy xugu-dialect.
3. Rewriting `E:\Work\docs\content`.
4. `dialect` → `demo-spring-boot` Maven dependency.
5. Emitting SQL for **文档不允许** rows or inventing undocumented syntax.
6. Claiming covered-live / Accept green without corresponding gated IT evidence.
7. Committing production secrets or removing `compatiblemode=NONE` from defaults without Charter decision.
8. Hibernate 6.x / 8 beta adaptation work in this line.

---

## Required Patterns

1. Capability classes under named packages; wire through `XuguDialect`.
2. Unit test for SQL/registry locks; gated IT for live claims.
3. Cite matrix ids and Xugu doc paths in javadoc for non-obvious SQL choices.
4. Keep SPI file and resolver match rules in sync with tests (`XuguDialectResolverTest`, services resource tests).
5. Prefer env overrides for connection parameters in tests and docs examples.

---

## Code Review Checklist

- [ ] Change maps to matrix / contract / known-limit update
- [ ] No forbidden inheritance or external dialect source influence
- [ ] NONE-mode SQL assumptions still hold
- [ ] Pagination/lock ordering preserved if those paths touched
- [ ] Exception codes updated with tests if conversion changed
- [ ] Offline `mvn -q test` still passes with gate off
- [ ] Live gate run planned when status → covered-live
- [ ] Public docs (`docs/user-guide/`, contracts) updated when consumer-visible

---

## Verify Before Done

```bash
mvn -q -DskipTests package
mvn -q test
# optional live:
XUGU_RUN_IT=true mvn -q test
```

Agent-facing note in `AGENTS.md` matches these commands.
