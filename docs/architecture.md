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

Harness level: **Standard**. Governance under `harness/`.

## Maven multi-module layout

Parent aggregator at repository root:

```text
hibernate-test/                    # parent POM (packaging=pom)
├── dialect/                       # com.xugu:xugu-dialect:7.4.5.Final
├── demo-spring-boot/              # Spring Boot 4.1.0 demo module (entry scaffold)
├── docs/                          # project docs (not a Maven module)
├── contracts/                     # public contract markdown
├── harness/                       # engineering harness (not on runtime classpath)
├── DECISIONS/                     # ADRs
├── xugu-jdbc-12.3.6.jar           # JDBC driver at repo root (Charter baseline)
├── PROJECT_CHARTER.md
└── AGENTS.md
```

| Path | Role | Status |
|---|---|---|
| `dialect/` | Dialect library module (`com.xugu.dialect`, stub `XuguDialect`) | **Scaffolded** (P-001) — definition A / SPI not yet |
| `demo-spring-boot/` | Spring Boot demo consuming dialect + JDBC | **Scaffolded** (P-001) — main entry only; no business demo |
| `docs/` | Production use / verification / readiness docs | Present; matrix pointer [`feature-matrix-definition-a.md`](feature-matrix-definition-a.md) → contracts SSOT |
| `contracts/` | Module public contracts | **P-002:** [`xugu-dialect.contract.md`](../contracts/xugu-dialect.contract.md) + [Definition A matrix SSOT](../contracts/feature-matrix-definition-a.md) (105 rows); scaffold contract retained |
| `harness/` | Clarify→Ship governance | Present |
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
harness           governance only; never on runtime classpath
```

- `dialect` must **not** depend on `demo-spring-boot`.
- Demo must **not** host core dialect implementation.
- Integration tests for dialect must use **real** XuguDB (not mock-only substitute) — deferred past P-001.

## Entry Points (current)

| Kind | Entry | Status |
|---|---|---|
| Dialect explicit | `hibernate.dialect=com.xugu.dialect.XuguDialect` | Stub class exists (P-001); capabilities later |
| Dialect auto | DialectResolver SPI for XuguDB | Planned (not in P-001) |
| Demo | `com.xugu.demo.DemoApplication` in `demo-spring-boot/` | Scaffolded (compile/package only) |
| Harness verify | `python harness/scripts/verify.py` | Present; build/test = real `mvn` commands |
| JDBC | system / module dependency on root jar | Wired in both modules |

## Data / Control Flow (target)

1. Application (or demo) configures Hibernate with explicit dialect or relies on SPI resolution.
2. Dialect generates SQL consistent with XuguDB docs (`E:\Work\docs\content`) within definition A matrix (Plan / later Phases).
3. JDBC driver (`xugu-jdbc-12.3.6.jar`) talks to real XuguDB; connection secrets prefer env override.
4. Tests and demo observe real DB behavior; harness records verification evidence.

## Notes for Future Agents

- Definition A matrix SSOT is `contracts/feature-matrix-definition-a.md` (P-002 accepted). DialectResolver SPI remains out of scope until P-008.
- Do not invent SQL for **文档不允许** rows.
- Do not read or port `E:\Work\java\hibernate-dialect`.
- Record further stable decisions in `DECISIONS/`.
- Build: `mvn -q -DskipTests package` · Test: `mvn -q test` (see `harness/verification.json`).
