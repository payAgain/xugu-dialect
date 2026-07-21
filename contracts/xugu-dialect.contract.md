# Contract: xugu-dialect (public dialect capability)

> **Kind:** public dialect contract (definition A SSOT companion)  
> **Phase:** P-002 / Build B-002 / Initiative I-001  
> **Author role:** architect-contract  
> **Status:** CONFIRMED for subsequent implement Phases (P-003…P-008)  
> **Supersedes (scope):** capability gaps left open by `contracts/xugu-dialect.scaffold.contract.md` (scaffold remains authoritative for Maven layout)  
> **SSOT baselines:** `PROJECT_CHARTER.md`, `DECISIONS/ADR-0001-hibernate-baseline.md`, `docs/architecture.md`, `harness/initiatives/I-001/brief.md`  
> **Feature matrix SSOT:** [`contracts/feature-matrix-definition-a.md`](feature-matrix-definition-a.md)

---

## 1. Purpose

Lock the **public integration surface** and **definition A capability boundary** for `com.xugu:xugu-dialect` so implementers, demo authors, and integrators share one contract:

- What to configure (GAV, package, explicit dialect, planned SPI)
- What the dialect module owns vs demo/docs/harness
- Which Hibernate production capabilities are in-scope for I-001 (via the feature matrix)

This contract does **not** authorize reading or porting `E:\Work\java\hibernate-dialect` or any legacy xugu-dialect sources.

---

## 2. Maven coordinates & Java entry

| Item | Value |
|---|---|
| **GAV** | `com.xugu:xugu-dialect:7.4.5.Final` |
| **groupId** | `com.xugu` |
| **artifactId** | `xugu-dialect` |
| **version** | `7.4.5.Final` (tracks Hibernate; see §8) |
| **Root package** | `com.xugu.dialect` |
| **Main class** | `com.xugu.dialect.XuguDialect` |
| **Inheritance** | MUST extend `org.hibernate.dialect.Dialect` only |
| **FORBIDDEN inheritance** | `org.hibernate.dialect.MySQLDialect`, `org.hibernate.dialect.OracleDialect` (and subclasses) |

Module directory remains `dialect/` (scaffold contract); artifact id remains `xugu-dialect`.

---

## 3. Integration modes

### 3.1 Explicit configuration (MUST for I-001)

Integrators MAY set:

```properties
hibernate.dialect=com.xugu.dialect.XuguDialect
```

(or Spring Boot / programmatic equivalent). Explicit configuration MUST work once definition A capabilities for the active Phases are implemented.

### 3.2 DialectResolver SPI (planned P-008)

| Item | Requirement |
|---|---|
| Mechanism | `org.hibernate.engine.jdbc.dialect.spi.DialectResolver` (Hibernate 7.4.5) registered via `META-INF/services/org.hibernate.engine.jdbc.dialect.spi.DialectResolver` |
| Behavior | Auto-detect XuguDB from JDBC `DatabaseMetaData` / product name (and documented heuristics) and resolve to `com.xugu.dialect.XuguDialect` |
| Target Phase | **P-008** |
| Status until P-008 | Planned; not claimed complete by this contract alone |

Both modes are Charter success criteria; SPI is sequenced after core SQL/DDL capabilities (P-003…P-007).

---

## 4. Tech baselines (locked)

| Item | Value | Notes |
|---|---|---|
| Hibernate | **7.4.5.Final** | dialect compile dependency |
| JDK | **17** | `maven.compiler.release` / toolchain |
| JDBC driver | repo-root **`xugu-jdbc-12.3.6.jar`** | `system` + `systemPath` per scaffold contract |
| Spring Boot (demo) | **4.1.0** | MUST force `hibernate.version=7.4.5.Final` |
| SQL truth source | `E:\Work\docs\content` (read-only) | cite paths; do not rewrite |
| Architecture reference | Hibernate MySQL/Oracle Dialect **structure only** | package/capability organization |
| Implementation reference | **FORBIDDEN** | `E:\Work\java\hibernate-dialect` / old xugu-dialect |

---

## 5. Runtime defaults & secrets

| Item | Decision |
|---|---|
| **compatible_mode default** | **NONE** (session/connection; matches Xugu `COMPATIBLE_MODE` / `def_compatible_mode` default) |
| **Secrets & connection params** | Prefer **environment variables** (host, port, database, user, password, ssl). Local reference defaults in Charter are non-secret examples only; **MUST NOT** commit production credentials |
| **Integration tests** | MUST use **real** XuguDB (no mock-only substitute for dialect IT) |

Connection URL shape reference: `jdbc:xugu://…` — exact query parameters follow driver docs, not this contract.

---

## 6. Module boundaries & dependency direction

```text
demo-spring-boot  →  dialect (com.xugu:xugu-dialect)  →  (Hibernate 7.4.5.Final API + Xugu JDBC)
docs              describes this contract + matrix; does not reverse-depend on demo for truth
harness           governance only; never on runtime classpath
```

| Area | Owns | Must not |
|---|---|---|
| **dialect** | `XuguDialect`, type/DDL/limit/lock/identity/sequence/function/schema supports, DialectResolver SPI, unit + real-DB IT | Depend on demo; extend MySQL/Oracle Dialect; port sibling repo |
| **demo-spring-boot** | Runnable demo against real DB | Host core dialect implementation |
| **docs/** (project) | User guide, verification steps, matrix cross-ref | Rewrite `E:\Work\docs\content` |
| **contracts/** | Public contracts + definition A matrix SSOT | Business Java |
| **harness/** | Clarify→Ship process | Dialect runtime code |

---

## 7. Capability scope = Definition A (+ I-003 ruler C)

**Definition A** = MySQL/Oracle Dialect **production capability surface** ∩ capabilities **allowed by XuguDB docs**.

- Actionable checklist: [`contracts/feature-matrix-definition-a.md`](feature-matrix-definition-a.md)
- Every matrix row has Status: `可实现` | `文档不允许` | `延后`
- `文档不允许` → implementers **MUST NOT** invent unsupported SQL
- `延后` → out of active Phases until revisit trigger fires

### 7.1 I-003 feature addendum (ruler C)

I-003 extends the **implementable** production surface using **ruler C**:

- **A:** Hibernate 7.4.5 MySQL-class Dialect production overrides/capabilities ∩ XuGu docs
- **B:** **read-only** inventory vs `E:\Work\java\hibernate-dialect` (discover gaps only)

SSOT for I-003 new rows: [`contracts/feature-matrix-i003-ruler-c.md`](feature-matrix-i003-ruler-c.md)

Rules:

- **Still forbidden:** copying/porting sibling or legacy dialect **source code**; inheriting MySQL/Oracle Dialect
- Each I-003 `可实现` row requires an **ORM `app_entrypoint` IT** in its Phase
- Version remains **`7.4.5.Final`** (no bump)
- Harness framework agents/skills/verification.json **not** changed by I-003

Domains for I-003 Phases:

| Phase | Domain (C-* IDs) | Delivery (I-003) |
|---|---|---|
| P-002 | Exception mapping C-EXC-* | **Delivered** — `XuguExceptionMappingIT` |
| P-003 | JSON deep + AggregateSupport C-JSON-001…004 | **Delivered** — `XuguJsonAggregateIT` |
| P-004 | Window + CTE C-WIN-001 / C-CTE-001 | **Delivered** — `XuguWindowCteIT` |
| P-005 | Bulk mutation C-BULK-* | **Delivered** — `XuguBulkMutationIT` (bulk insert live IT N/A) |
| P-006 | Type/DDL details C-DDL-001…003, C-CAT-001, C-GUID-001 | **Delivered** — `XuguTypeDdlDetailsIT` |
| P-007 | Docs/matrix align + Accept prep | **Docs align** (this Phase) — Ship still out of scope |

GAV remains **`7.4.5.Final`**. Deferred / 文档不允许 rows in the I-003 matrix stay deferred / forbidden.

### 7.2 Lock integration (I-008 Q2)

Integrators using JPA pessimistic locking on XuguDB **must** read [`docs/user-guide/07-lock-integration.md`](../docs/user-guide/07-lock-integration.md):

- **No** `SKIP LOCKED` (`supportsSkipLocked=false`; matrix A-LCK-004 / C-SKIP-001)
- **No** `FOR SHARE` (matrix A-LCK-005 — 文档不允许)
- **`PESSIMISTIC_READ` maps to exclusive `FOR UPDATE`**, not a PostgreSQL-style share lock
- Pagination + lock SQL order: **`FOR UPDATE` → `LIMIT` → `WAIT`**

Live behavioral evidence for I-008 Accept: **P-005** (`harness/evidence/test/I-008/P-005/`).

Honest regression counts (I-010 Accept, live @5287): **91/98** covered-live + **7** known-limit-documented — see [`production-regression-baseline.md`](production-regression-baseline.md) § Summary counts. (I-008 Q1 freeze was **83/98** + **15** KL.)

---

## 8. Versioning policy

- Artifact version **tracks the adapted Hibernate version** (currently `7.4.5.Final`).
- Do **not** use an unrelated `1.0.0`-style product version for this artifact while adapting Hibernate 7.4.5.
- When a future Initiative targets another Hibernate line, GAV version and Charter/ADR must be updated together.
- **I-002 hotfix (behavior-only):** HQL/Criteria pagination (`XuguSqlAstTranslator` → `LIMIT … [OFFSET …]`, lock order FOR UPDATE → LIMIT → WAIT) and sequence catalog for `hbm2ddl validate` (`getQuerySequencesString` → `all_sequences`) ship under the **same** GAV `7.4.5.Final` — no version bump. See matrix A-PAG-* / A-SEQ-001 and user-guide troubleshooting.
- **I-003 feature:** capability parity expansion under ruler C also ships under **`7.4.5.Final`** — no version bump unless Human Gate later authorizes.

---

## 9. Non-goals (this Initiative / this contract)

1. **No** inheritance of `MySQLDialect` / `OracleDialect`.
2. **No** copying or porting `E:\Work\java\hibernate-dialect` or legacy xugu-dialect **implementation sources**. (I-003 ruler C allows **read-only gap inventory** only.)
3. **No** rewrite of official content under `E:\Work\docs\content`.
4. **No** Hibernate 6.x or 8 beta adaptation.
5. **Ship out of I-001/I-003:** Maven Central credentials, signing, `tag` / `push` / release pipelines — Accept first; Ship needs separate Human Gate.
6. Spatial/geometry and other extras **outside** definition A / I-003 matrices (see `延后` / non-goals).
7. Hard-coding fake SQL for `文档不允许` rows.
8. **I-003:** No harness framework harden (agents/skills/verification.json Accept ontology) — other thread.

---

## 10. Related documents

| Doc | Role |
|---|---|
| `contracts/xugu-dialect.scaffold.contract.md` | Maven layout / P-001 scaffold |
| `contracts/feature-matrix-definition-a.md` | Definition A checklist SSOT |
| `contracts/feature-matrix-i003-ruler-c.md` | I-003 ruler C gap SSOT |
| `PROJECT_CHARTER.md` | Product charter |
| `DECISIONS/ADR-0001-hibernate-baseline.md` | Hibernate/GAV/package/non-inheritance |
| `docs/architecture.md` | Layout & dependency notes (orchestrator may add matrix cross-ref) |
| [`docs/user-guide/`](../docs/user-guide/README.md) | Project user guide (P-010): install, config, verify, matrix, troubleshooting |

---

## 11. Acceptance mapping (contract)

| Criterion | Gate |
|---|---|
| Integrator knows GAV + main class | §2 |
| Explicit + SPI planned | §3 |
| Baselines + Boot 4.1.0 | §4 |
| `compatible_mode=NONE` + env secrets | §5 |
| Module boundaries | §6 |
| Definition A bound to matrix | §7 + feature-matrix file |
| I-003 ruler C bound to gap matrix | §7.1 + feature-matrix-i003-ruler-c |
| Version tracks Hibernate | §8 |
| Non-goals respected | §9 |
