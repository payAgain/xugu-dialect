# Xugu Hibernate Dialect (I-001 → I-003)

Hibernate **7.4.5** dialect for **XuguDB**, plus a Spring Boot demo and project docs.

I-002 hotfix (same GAV **7.4.5.Final**): HQL/Criteria pagination emits `LIMIT … [OFFSET …]` (not ANSI FETCH); `hbm2ddl validate` reads sequences from `all_sequences`.

I-003 feature parity (same GAV **7.4.5.Final**): exception mapping, JSON aggregates / AggregateSupport, window + CTE, bulk mutation fallback, DDL details (IF NOT EXISTS, ALTER COLUMN, catalog, GUID). See [contracts/feature-matrix-i003-ruler-c.md](contracts/feature-matrix-i003-ruler-c.md).

I-005 production regression baseline (same GAV **7.4.5.Final**): **94** 可实现 + **34** negative-only rows frozen with test entrypoints; demo smoke in baseline. SSOT: [contracts/production-regression-baseline.md](contracts/production-regression-baseline.md). Frozen IT gate: `XUGU_RUN_IT=true mvn -q test` (see [docs/user-guide/03-verify.md](docs/user-guide/03-verify.md)).

I-006 Spring Boot consumer-path (same GAV **7.4.5.Final**): **41** Boot-required rows (Layer A/B/C′), open gaps = **0**. SSOT: [contracts/consumer-path-baseline.md](contracts/consumer-path-baseline.md). How to run offline + gated live: [docs/user-guide/06-consumer-path.md](docs/user-guide/06-consumer-path.md). **Not** a full 94-row Boot mirror; **Ship out of scope**.

I-007 capability hardening (same GAV **7.4.5.Final**): Track **A** C-BULK-002 **covered-live**; Track **B** Flyway + Demo deepening; Track **C** JSON subset / ARRAY / ALTER SEQUENCE **covered-live**. SSOT: [contracts/i007-capability-hardening-plan.md](contracts/i007-capability-hardening-plan.md). Track C user summary: [docs/p004-track-c-capabilities.md](docs/p004-track-c-capabilities.md). **NOT Ship**.

I-008 production quality gaps (same GAV **7.4.5.Final**): Q1–Q4 closed — honest **83/98** covered-live + **15** known-limit; lock semantics (Q2); Boot UUID/JSON out-of-box (Q3); Accept requires full reactor live evidence (Q4). **Q5 performance / multi-version matrix out of scope.** SSOT: [harness/initiatives/I-008/brief.md](harness/initiatives/I-008/brief.md). Accept prep: [docs/user-guide/03-verify.md](docs/user-guide/03-verify.md) § I-008 Accept. **NOT Ship**.

## Start here

- **User guide:** [docs/user-guide/README.md](docs/user-guide/README.md)
- **Consumer-path (I-006):** [docs/user-guide/06-consumer-path.md](docs/user-guide/06-consumer-path.md)
- **Public contract:** [contracts/xugu-dialect.contract.md](contracts/xugu-dialect.contract.md)
- **Definition A feature matrix (SSOT):** [contracts/feature-matrix-definition-a.md](contracts/feature-matrix-definition-a.md)
- **I-003 ruler C matrix (SSOT):** [contracts/feature-matrix-i003-ruler-c.md](contracts/feature-matrix-i003-ruler-c.md)
- **I-006 consumer-path baseline (SSOT):** [contracts/consumer-path-baseline.md](contracts/consumer-path-baseline.md)
- **Spring Boot demo:** [demo-spring-boot/README.md](demo-spring-boot/README.md)

## Modules

| Module | Artifact | Notes |
|---|---|---|
| `dialect/` | `com.xugu:xugu-dialect:7.4.5.Final` | Dialect + SPI resolver |
| `demo-spring-boot/` | demo app | Spring Boot 4.1.0; forces Hibernate 7.4.5.Final |

## Quick verify

```text
mvn -q -DskipTests package
mvn -q test
python harness/scripts/verify.py
```

Live DB integration (optional gate):

```text
mvn -q test -Dxugu.run.integration=true
mvn -q -pl demo-spring-boot -am test -Dxugu.run.integration=true
# or: XUGU_RUN_IT=true mvn -q test
```

Consumer-path details (Layers A/B/C′, Accept prep): [docs/user-guide/06-consumer-path.md](docs/user-guide/06-consumer-path.md).

## Agent / harness

See [AGENTS.md](AGENTS.md). Ship / Maven Central is a separate Human Gate step (not part of day-to-day verify).

## Credentials

Prefer env overrides (`XUGU_JDBC_URL`, `XUGU_USER`, `XUGU_PASSWORD`). Do **not** commit production secrets; local Charter defaults only.
