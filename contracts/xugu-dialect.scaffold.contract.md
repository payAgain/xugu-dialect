# Contract: xugu-dialect Maven scaffold (P-001)

> **Kind:** scaffold / layout contract (NOT full dialect capability contract)  
> **Phase:** P-001 / Build B-001 / Initiative I-001  
> **Author role:** architect-contract  
> **Status:** CONFIRMED for implementer  
> **SSOT baselines:** `PROJECT_CHARTER.md`, `DECISIONS/ADR-0001-hibernate-baseline.md`, `docs/architecture.md`, `.trellis/tasks/archive/2026-07/i-001-xugu-dialect-major/prd.md`  
> **Full dialect API / definition-A matrix:** deferred to later Phase (`contracts/xugu-dialect.contract.md` planned at P-002+)

---

## 1. Purpose

Lock the **Maven multi-module layout**, **GAV**, **JDK/Hibernate/Boot versions**, and **JDBC wiring** so implementer can produce a **minimal compilable/packagable** skeleton.  
This contract does **not** authorize dialect SQL/DDL/Limit/SPI behavior beyond an empty placeholder class.

---

## 2. Repository layout (MUST)

```text
xugu-dialect/                           # parent aggregator POM
├── pom.xml                             # packaging=pom; modules listed below
├── dialect/                            # library module (dir name locked)
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/xugu/dialect/
│       │   └── XuguDialect.java        # minimal stub (see §6)
│       └── test/java/                  # may be empty dir; optional smoke test
├── demo-spring-boot/                   # demo module (dir name locked)
│   ├── pom.xml
│   └── src/main/java/...               # minimal Spring Boot entry ONLY (see §6)
├── xugu-jdbc-12.3.6.jar                # already at repo root (MUST remain)
├── docs/                               # NOT a Maven module
├── contracts/
├── .trellis/                           # Trellis (not on runtime classpath)
└── ...
```

| Path | Maven role | Artifact |
|---|---|---|
| `/` (root) | parent aggregator | `packaging=pom`; coordinates MAY be `com.xugu:xugu-dialect-parent:7.4.5.Final` (or equivalent parent GAV); **MUST** list modules `dialect`, `demo-spring-boot` |
| `dialect/` | library jar | **`com.xugu:xugu-dialect:7.4.5.Final`** (GAV locked by ADR-0001) |
| `demo-spring-boot/` | application jar | e.g. `com.xugu:demo-spring-boot:7.4.5.Final` (version MAY align with parent; not Central-published in this Initiative) |

**Directory names are locked:** module folders are `dialect/` and `demo-spring-boot/` (not `xugu-dialect/` as a folder). Artifact id of the library remains `xugu-dialect`.

---

## 3. Tech baselines (MUST)

| Item | Value | Notes |
|---|---|---|
| JDK | **17** | `maven.compiler.release` / `java.version` = 17 |
| Hibernate | **7.4.5.Final** | dialect module compile dependency; demo MUST override Boot BOM |
| Library GAV | **`com.xugu:xugu-dialect:7.4.5.Final`** | groupId `com.xugu`, artifactId `xugu-dialect`, version `7.4.5.Final` |
| Root package | `com.xugu.dialect` | |
| Main class (stub) | `com.xugu.dialect.XuguDialect` | |
| Spring Boot (demo) | **4.1.0** | exact; use Spring Boot parent or BOM 4.1.0 |
| Hibernate override (demo) | property **`hibernate.version=7.4.5.Final`** | Boot 4.1.0 defaults may ship 7.4.1 — **MUST** force 7.4.5.Final |
| JDBC jar | repo-root **`xugu-jdbc-12.3.6.jar`** | Charter baseline; keep trackable |
| Inheritance | `XuguDialect` extends **`org.hibernate.dialect.Dialect`** only | **FORBIDDEN:** `MySQLDialect`, `OracleDialect` |
| Sibling repo | — | **FORBIDDEN** to read/port `E:\Work\java\hibernate-dialect` |

---

## 4. Parent POM (MUST)

- `packaging`: **`pom`**
- `<modules>`: **`dialect`**, **`demo-spring-boot`** (order: dialect first recommended)
- Manage (via `dependencyManagement` and/or properties) at least:
  - `hibernate.version` = `7.4.5.Final` (or equivalent property used by dialect module)
  - Java 17 toolchain/compiler settings
- Must **not** put demo-only Spring Boot deps on the dialect module classpath via parent pollution that forces dialect → Spring.

---

## 5. Dependency direction (MUST)

```text
demo-spring-boot  →  dialect (com.xugu:xugu-dialect)  →  (Hibernate 7.4.5.Final API + Xugu JDBC)
```

- `dialect` **MUST NOT** depend on `demo-spring-boot`.
- `demo-spring-boot` **MUST** depend on `com.xugu:xugu-dialect` (reactor / sibling module dependency).
- Neither module puts `.trellis/` or `docs/` on the runtime classpath.
- Demo **MUST NOT** host core dialect implementation (no copy of dialect sources under demo).

---

## 6. JDBC wiring (MUST)

Both modules that need the driver (at minimum **dialect** if tests/compile need it; **demo-spring-boot** for runtime) MUST declare a Maven dependency on the local jar using **`system` scope** (or equivalent documented approach that resolves the root jar without Central).

**Allowed `systemPath` forms (pick one consistently; prefer property-based):**

1. `${maven.multiModuleProjectDirectory}/xugu-jdbc-12.3.6.jar`  ← **preferred**
2. Relative from module POM: `../xugu-jdbc-12.3.6.jar`

Example shape (illustrative):

```xml
<dependency>
  <groupId>com.xugu</groupId>
  <artifactId>xugu-jdbc</artifactId>
  <version>12.3.6</version>
  <scope>system</scope>
  <systemPath>${maven.multiModuleProjectDirectory}/xugu-jdbc-12.3.6.jar</systemPath>
</dependency>
```

**Boundary:** if the jar is missing, `mvn package` failure message should be readable (path-related); do not invent a fake Central coordinate for this Phase.

---

## 7. Minimal Java surface for P-001 (MUST create)

### 7.1 `dialect` module — required for `mvn package`

| Artifact | Requirement |
|---|---|
| `com.xugu.dialect.XuguDialect` | **MUST** exist as a public class |
| Extends | `org.hibernate.dialect.Dialect` (**not** MySQL/Oracle subclasses) |
| Body | Empty / placeholder is enough for P-001 (e.g. call `super(...)` with a minimal `DatabaseVersion` / constructor required by Hibernate 7.4.x API so it **compiles**) |
| DialectResolver SPI | **NOT required** in P-001 |
| Definition A SQL/DDL/Limit/functions | **FORBIDDEN** in P-001 |
| Tests | Optional; empty `src/test/java` OK; no real-DB IT required this Phase |

Implementer may add a package-info.java in addition, but **`XuguDialect` class is mandatory** so the public entry point exists for later Phases.

### 7.2 `demo-spring-boot` module — required for `mvn package`

| Artifact | Requirement |
|---|---|
| Spring Boot entry | One `@SpringBootApplication` main class (package under `com.xugu` / `com.xugu.demo` — implementer choice) |
| Behavior | Placeholder only; **MUST NOT** implement business demo scenarios or require live DB to **compile/package** |
| Properties | Reserve `hibernate.version=7.4.5.Final`; Boot version **4.1.0** |
| Runtime start vs package | P-001 acceptance is **package/build**, not “demo connects to XuguDB” |

---

## 8. Hibernate dependency (dialect module)

- Compile dependency on Hibernate ORM API/core at **7.4.5.Final** (exact artifact names per Hibernate 7.4 Maven coordinates — typically `org.hibernate.orm:hibernate-core:7.4.5.Final`).
- Do not pin a different Hibernate version in dialect than 7.4.5.Final.

---

## 9. Spring Boot + Hibernate override (demo module)

MUST:

1. Use Spring Boot **4.1.0** (parent POM or `spring-boot-dependencies` BOM).
2. Declare property: **`hibernate.version=7.4.5.Final`** so Boot’s managed Hibernate is forced to 7.4.5.Final.
3. Depend on reactor module `com.xugu:xugu-dialect:7.4.5.Final`.
4. Wire Xugu JDBC via `systemPath` as in §6.

---

## 10. Verification / docs obligations (implementer — out of Java)

Per Phase packet (implementer owns these paths; listed here for contract completeness):

- Keep `AGENTS.md` / `docs/verification.md` Maven verify commands consistent with this layout.
- Suggested shape (non-binding exact flags left to implementer, must succeed on JDK 17):
  - build: e.g. `mvn -q -DskipTests package`
  - test: e.g. `mvn test` (empty/minimal tests may pass)
- Update `docs/architecture.md` status Planned → scaffolded **after** modules exist (implementer).

---

## 11. Explicitly out of scope for this contract / P-001

- Definition A capability implementation
- DialectResolver SPI registration (`META-INF/services/...`)
- Full Spring Boot demo business flows / `docs/user-guide/` body
- Reading or porting `E:\Work\java\hibernate-dialect`
- Extending `MySQLDialect` / `OracleDialect`
- Maven Central publish, tag, push
- Writing root `PROJECT_CHARTER.md`

---

## 12. Acceptance mapping (scaffold)

| Criterion | Contract gate |
|---|---|
| Parent + two modules package on JDK 17 | Layout §§2–4 + minimal classes §7 |
| JDBC via systemPath to root jar | §6 |
| GAV `com.xugu:xugu-dialect:7.4.5.Final` | §3 |
| Demo Boot 4.1.0 + `hibernate.version` override | §9 |
| No MySQL/Oracle dialect inheritance | §3, §7.1 |

---

## 13. Related documents

- `docs/architecture.md` — planned layout (this contract confirms it for scaffold)
- `.trellis/tasks/archive/2026-07/i-001-xugu-dialect-major/prd.md` — historical I-001 brief
- `DECISIONS/ADR-0001-hibernate-baseline.md`
