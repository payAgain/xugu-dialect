# Xugu Hibernate Dialect (I-001)

Hibernate **7.4.5** dialect for **XuguDB**, plus a Spring Boot demo and project docs.

I-002 hotfix (same GAV **7.4.5.Final**): HQL/Criteria pagination emits `LIMIT … [OFFSET …]` (not ANSI FETCH); `hbm2ddl validate` reads sequences from `all_sequences`.

## Start here

- **User guide:** [docs/user-guide/README.md](docs/user-guide/README.md)
- **Public contract:** [contracts/xugu-dialect.contract.md](contracts/xugu-dialect.contract.md)
- **Definition A feature matrix (SSOT):** [contracts/feature-matrix-definition-a.md](contracts/feature-matrix-definition-a.md)
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
