# Layout confirmation — P-001 scaffold vs `docs/architecture.md`

> **Role:** architect-contract (RP-01)  
> **Invocation:** `arch-p001-20260714`  
> **Date:** 2026-07-14  
> **Contract:** `contracts/xugu-dialect.scaffold.contract.md`  
> **Architecture SSOT:** `docs/architecture.md` (Status at review: Bootstrap G1 planned — modules not scaffolded yet)

---

## Verdict

**CONFIRMED** — P-001 scaffold contract matches `docs/architecture.md` planned Maven layout and Charter/ADR baselines. No layout deviation required. Implementer may proceed to create parent + `dialect/` + `demo-spring-boot/` per contract.

---

## Checklist vs architecture.md

| Architecture item | Contract decision | Match? |
|---|---|---|
| Parent aggregator at repo root | `packaging=pom` + modules `dialect`, `demo-spring-boot` | YES |
| Module dir `dialect/` (not folder named `xugu-dialect`) | Locked as `dialect/` | YES |
| Artifact `com.xugu:xugu-dialect:7.4.5.Final` | GAV locked | YES |
| Module dir `demo-spring-boot/` | Locked | YES |
| JDBC `xugu-jdbc-12.3.6.jar` at repo root | `systemPath` → `${maven.multiModuleProjectDirectory}/...` or `../xugu-jdbc-12.3.6.jar` | YES |
| `docs/`, `contracts/`, `harness/` not Maven modules | Confirmed; not listed in `<modules>` | YES |
| Hibernate 7.4.5.Final | Locked | YES |
| JDK 17 | Locked | YES |
| Package / class `com.xugu.dialect` / `XuguDialect` | Minimal stub required for compile | YES |
| Do not extend MySQL/Oracle Dialect | Extends `org.hibernate.dialect.Dialect` only | YES |
| FORBIDDEN sibling `hibernate-dialect` | Restated in contract | YES |
| Dep direction `demo → dialect → (Hibernate + JDBC)` | Non-negotiable in contract | YES |
| dialect must not depend on demo | Explicit MUST NOT | YES |

---

## Clarifications locked by I-001 / P-001 (beyond architecture.md “Planned” note)

Architecture.md still says Spring Boot BOM “deferred to Plan”. **I-001 brief + P-001** lock:

| Item | Locked value |
|---|---|
| Spring Boot | **4.1.0** |
| Demo property | **`hibernate.version=7.4.5.Final`** (override Boot default 7.4.1) |

These are in-scope for scaffold POMs; architecture.md status update to “scaffolded” remains **implementer** work after modules exist (per Phase packet).

---

## P-001 minimal surface (intentionally narrower than architecture “Entry Points”)

| Architecture entry (target) | P-001 requirement |
|---|---|
| Explicit `hibernate.dialect=com.xugu.dialect.XuguDialect` | Class must exist (stub OK); wiring docs later |
| DialectResolver SPI | **Not** required this Phase |
| Demo Spring Boot main | Minimal `@SpringBootApplication` for package only |
| Real XuguDB IT | **Not** required this Phase |

---

## Non-deviations

- No alternate module naming.
- No inheritance from `MySQLDialect` / `OracleDialect`.
- No full dialect capability contract in this file (deferred).

---

## Evidence conclusion

Layout confirmation **PASS**. Handoff: `harness/handoffs/architect-contract/P-001.yaml`.
