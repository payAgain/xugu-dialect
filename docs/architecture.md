# Architecture Notes

> **Status:** P-002 accepted — dialect public contract + Definition A feature matrix published; P-001 scaffold remains.  
> **Updated:** 2026-07-14  
> **SSOT baselines:** `PROJECT_CHARTER.md`, `DECISIONS/ADR-0001-hibernate-baseline.md`, [`contracts/xugu-dialect.contract.md`](../contracts/xugu-dialect.contract.md), [`contracts/feature-matrix-definition-a.md`](../contracts/feature-matrix-definition-a.md) (Definition A matrix SSOT; docs pointer: [`docs/feature-matrix-definition-a.md`](feature-matrix-definition-a.md))  
> Scaffold contract (historical): `contracts/xugu-dialect.scaffold.contract.md`  
> Do not invent unimplemented dialect capabilities as if they already exist — implement only rows marked **可实现** for the active Phase.

## Repository Type

Library + demo product for **XuguDB Hibernate Dialect** (`xugu-dialect`):

- Publishable dialect Maven artifact
- Runnable Spring Boot demo against real XuguDB (demo runtime/IT deferred past P-001)
- Project-internal production documentation under `docs/`

Orchestration: **Trellis** (`.trellis/`). Historical initiative archives: `.trellis/tasks/archive/2026-07/`.

## Maven multi-module layout

Parent aggregator at repository root:

```text
xugu-dialect/                      # parent POM (packaging=pom)
├── dialect/                       # com.xugu:xugu-dialect:7.4.5.Final
├── demo-spring-boot/              # Spring Boot 4.1.0 demo module
├── docs/                          # project docs (not a Maven module)
├── contracts/                     # public contract markdown
├── .trellis/                      # Trellis workflow / specs / task archive
├── DECISIONS/                     # ADRs
├── xugu-jdbc-12.3.6.jar           # JDBC driver at repo root (Charter baseline)
├── PROJECT_CHARTER.md
└── AGENTS.md
```

| Path | Role | Status |
|---|---|---|
| `dialect/` | Dialect library module (`com.xugu.dialect`) | Live — Accept 91/98 covered-live + 7 KL (I-010) |
| `demo-spring-boot/` | Spring Boot demo consuming dialect + JDBC | Live consumer demo + gated IT |
| `docs/` | Production use / verification / readiness docs | Present; matrix pointer [`feature-matrix-definition-a.md`](feature-matrix-definition-a.md) → contracts SSOT |
| `contracts/` | Module public contracts | [`xugu-dialect.contract.md`](../contracts/xugu-dialect.contract.md) + matrices / baselines |
| `.trellis/` | Trellis governance (specs, tasks, workspace) | Present |
| `xugu-jdbc-12.3.6.jar` | Xugu JDBC 12.3.6 | Present at root; wired via Maven `systemPath` |

Directory name is **`dialect/`** (Maven artifact id remains `xugu-dialect` per ADR-0001).

## Tech baselines (locked)

| Item | Value |
|---|---|
| Hibernate | 7.4.5.Final |
| JDK | 17 |
| Maven GAV | `com.xugu:xugu-dialect:7.4.5.Final` |
| Package / main class | `com.xugu.dialect` / `com.xugu.dialect.XuguDialect` |
| JDBC | `xugu-jdbc-12.3.6.jar` (repo root; `system` scope + `systemPath`) |
| compatible_mode default | NONE |
| SQL truth | `E:\Work\docs\content` (read-only) |
| Inheritance | Do **not** extend `MySQLDialect` / `OracleDialect` |
| Implementation reference | **FORBIDDEN:** `E:\Work\java\hibernate-dialect` |
| Architecture reference | Hibernate MySQL/Oracle Dialect **structure only** |
| Spring Boot | **4.1.0** (demo); property `hibernate.version=7.4.5.Final` forced |

## Dependency direction (non-negotiable)

```text
demo-spring-boot  →  dialect  →  (Hibernate 7.4.5 API + Xugu JDBC)
docs              describes contracts; does not depend on demo for truth
.trellis          governance / specs / archives; never on runtime classpath
```

- `dialect` must **not** depend on `demo-spring-boot`.
- Demo must **not** host core dialect implementation.
- Integration tests for dialect must use **real** XuguDB when the IT gate is on (`XUGU_RUN_IT=true`).

## Entry Points (current)

| Kind | Entry | Status |
|---|---|---|
| Dialect explicit | `hibernate.dialect=com.xugu.dialect.XuguDialect` | Live |
| Dialect auto | DialectResolver SPI (`xugu`) | Live |
| Demo | `com.xugu.demo.DemoApplication` in `demo-spring-boot/` | Live |
| Verify | `mvn -q -DskipTests package` · `mvn -q test` | Offline default; gated live IT optional |
| JDBC | system / module dependency on root jar | Wired in both modules |

## Data / Control Flow (target)

1. Application (or demo) configures Hibernate with explicit dialect or relies on SPI resolution.
2. Dialect generates SQL consistent with XuguDB docs (`E:\Work\docs\content`) within definition A matrix.
3. JDBC driver (`xugu-jdbc-12.3.6.jar`) talks to real XuguDB; connection secrets prefer env override.
4. Tests and demo observe real DB behavior; Trellis tasks/archive hold historical Accept notes.

## Notes for Future Agents

- Definition A matrix SSOT is `contracts/feature-matrix-definition-a.md`. Coding conventions live under `.trellis/spec/`.
- Do not invent SQL for **文档不允许** / known-limit rows.
- Do not read or port `E:\Work\java\hibernate-dialect`.
- Record further stable decisions in `DECISIONS/` or `.trellis/spec/`.
- Build: `mvn -q -DskipTests package` · Test: `mvn -q test` (see `docs/verification.md`).
