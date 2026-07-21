# Dialect Package Guidelines (`com.xugu:xugu-dialect`)

> Coding guidance for the Hibernate dialect library module at `dialect/`.
> Product SSOT remains `PROJECT_CHARTER.md`, `contracts/`, and `DECISIONS/` — this tree teaches *how to implement and test* inside that boundary.

---

## Pre-Development Checklist

- [ ] Read `PROJECT_CHARTER.md` § non-negotiables and `DECISIONS/ADR-0001-hibernate-baseline.md`
- [ ] Confirm the change maps to a Definition A / Ruler C matrix row in `contracts/feature-matrix-definition-a.md` (or an explicit negative-only / known-limit update)
- [ ] SQL shape is backed by `E:\Work\docs\content` (read-only) — do not invent undocumented SQL
- [ ] Inheritance stays `org.hibernate.dialect.Dialect` only — never `MySQLDialect` / `OracleDialect`
- [ ] Do **not** read or port `E:\Work\java\hibernate-dialect` or any legacy xugu-dialect sources
- [ ] Default connection / IT URL uses `compatiblemode=NONE`
- [ ] Skim the guides listed below for the change type

---

## Guidelines Index

| Guide | Description |
|-------|-------------|
| [Directory Structure](./directory-structure.md) | Maven layout, Java packages, SPI resources |
| [Dialect Implementation](./dialect-implementation.md) | `XuguDialect` hub, support classes, SPI resolver |
| [SQL Types & Functions](./sql-types-functions.md) | Type contributions, function registry, AST/pagination |
| [Error Handling](./error-handling.md) | JDBC → Hibernate exception mapping |
| [Testing](./testing.md) | Unit vs gated IT, connection helpers, baselines |
| [Quality Guidelines](./quality-guidelines.md) | Forbidden patterns, verify commands, Accept口径 |

---

## Quality Check (before claiming done)

- [ ] No MySQL/Oracle dialect inheritance or copied implementation from forbidden trees
- [ ] Matrix / contract status updated when behavior or coverage changes (`contracts/production-regression-baseline.md` when Accept-relevant)
- [ ] Offline: `mvn -q -DskipTests package` and `mvn -q test` pass
- [ ] Live (when claiming covered-live): `XUGU_RUN_IT=true mvn -q test` (or `-Dxugu.run.integration=true`)
- [ ] Demo module still compiles: dialect must not depend on `demo-spring-boot`

---

## Thinking Guides

| Guide | When |
|-------|------|
| [Code Reuse](../../guides/code-reuse-thinking-guide.md) | New support class, function registration, type hook |
| [Cross-Layer](../../guides/cross-layer-thinking-guide.md) | Dialect ↔ JDBC ↔ demo ↔ contracts/docs |

---

## Analysis Assumptions

- GitNexus / ABCoder MCP were not available during bootstrap; structure was derived from Maven modules, `docs/architecture.md`, contracts, and direct source reads (2026-07-21).
- Charter Accept rollup cited in quality guidelines: **91/98 covered-live + 7 known-limit-documented** per `contracts/production-regression-baseline.md` § Summary counts (I-010). Re-count from that SSOT if the file has moved on.
