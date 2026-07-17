# Xugu Hibernate Dialect (I-001 → I-003)

Hibernate **7.4.5** dialect for **XuguDB**, plus a Spring Boot demo and project docs.

I-002 hotfix (same GAV **7.4.5.Final**): HQL/Criteria pagination emits `LIMIT … [OFFSET …]` (not ANSI FETCH); `hbm2ddl validate` reads sequences from `all_sequences`.

I-003 feature parity (same GAV **7.4.5.Final**): exception mapping, JSON aggregates / AggregateSupport, window + CTE, bulk mutation fallback, DDL details (IF NOT EXISTS, ALTER COLUMN, catalog, GUID). See [contracts/feature-matrix-i003-ruler-c.md](contracts/feature-matrix-i003-ruler-c.md).

I-005 production regression baseline (same GAV **7.4.5.Final**): **94** 可实现 + **34** negative-only rows frozen with test entrypoints; demo smoke in baseline. SSOT: [contracts/production-regression-baseline.md](contracts/production-regression-baseline.md). Frozen IT gate: `XUGU_RUN_IT=true mvn -q test` (see [docs/user-guide/03-verify.md](docs/user-guide/03-verify.md)).

## Start here

- **User guide:** [docs/user-guide/README.md](docs/user-guide/README.md)
- **Public contract:** [contracts/xugu-dialect.contract.md](contracts/xugu-dialect.contract.md)
- **Definition A feature matrix (SSOT):** [contracts/feature-matrix-definition-a.md](contracts/feature-matrix-definition-a.md)
- **I-003 ruler C matrix (SSOT):** [contracts/feature-matrix-i003-ruler-c.md](contracts/feature-matrix-i003-ruler-c.md)
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
```

## Agent / harness

See [AGENTS.md](AGENTS.md). Ship / Maven Central is a separate Human Gate step (not part of day-to-day verify).

## Credentials

Prefer env overrides (`XUGU_JDBC_URL`, `XUGU_USER`, `XUGU_PASSWORD`). Do **not** commit production secrets; local Charter defaults only.
