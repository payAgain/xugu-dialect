# Code Reuse Thinking Guide

> Stop before adding a new helper — this repo already has capability packages and test gates.

---

## Search First

| Looking for | Search in |
|-------------|-----------|
| Type / DDL mapping | `dialect/src/main/java/com/xugu/dialect/type`, `ddl` |
| Function registration | `function/XuguFunctionRegistrations` and siblings |
| Exception codes | `exception/XuguErrorCodes` |
| IT gate / JDBC URL | `dialect/.../support/XuguITGate`, `XuguTestConnection`; demo `XuguIntegrationGate` |
| Demo entity patterns | `demo-spring-boot/.../entity`, `repository` |
| Matrix ownership | `contracts/production-regression-baseline.md`, feature matrices |

```bash
rg -n "LimitHandler|compatiblemode|XuguITGate|json_functions_enabled" dialect demo-spring-boot
```

---

## Questions Before New Code

| Question | Prefer |
|----------|--------|
| Does a `Xugu*Support` / `Xugu*JdbcType` already own this? | Extend it; wire via `XuguDialect` |
| Is this SQL already locked in a unit test string assert? | Update the existing test + implementation together |
| Am I about to copy MySQL/Oracle dialect method bodies? | **Stop** — structure reference only; implement for Xugu docs |
| Do dialect and demo both need a gate helper? | Keep two thin mirrors (`XuguITGate` / `XuguIntegrationGate`) with identical semantics — do not invent a third |
| Is this Flyway-specific? | Keep under `demo-spring-boot`, not `dialect` |

---

## Good Reuse Examples (in-repo)

- Function contributions centralized in `XuguFunctionRegistrations` instead of scattering `register` calls.
- Interval / XML / geometric subtype tables living in `*TypeSupport` classes, consumed by dialect + tests.
- Shared default NONE-mode URL construction in `XuguTestConnection` / demo datasource YAML env pattern.

---

## Duplication Anti-Patterns

- Second LimitHandler or parallel pagination SQL in demo “utils”.
- Re-declaring Xugu error codes in tests instead of using `XuguErrorCodes`.
- Copy-pasting JDBC open/URL logic into every IT class.
- New “Util” package that mixes DDL, functions, and test gates.

---

## When Extraction Is Justified

Extract a new class when:

1. The same SQL or binding logic appears in **3+** places, or
2. `XuguDialect` override methods become unreadable, or
3. Unit tests need to assert the helper without constructing a full `SessionFactory`.

Name it after the capability (`XuguIndexDdlSupport`), not `XuguHelpers`.
