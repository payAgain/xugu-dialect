# P-009 Implementer Notes (Spring Boot demo)

**Invocation:** `impl-p009-20260715`  
**Role:** implementer  
**Phase / Build / Initiative:** P-009 / B-009 / I-001  
**Date:** 2026-07-15  
**Step:** RP-01  
**Accept / commit:** **NOT done** (await RP-02 test + RP-03 reviewer; Human Gate: no commit this turn)

## What was delivered

1. `demo-spring-boot/` Spring Boot **4.1.0** app with `spring-boot-starter-data-jpa` + `spring-boot-starter-test`
2. Forced **`hibernate.version=7.4.5.Final`** (property + DM entry after Boot BOM import; Boot BOM default is 7.4.1.Final)
3. Xugu JDBC via `systemPath` → repo-root `xugu-jdbc-12.3.6.jar`; driver `com.xugu.cloudjdbc.Driver`
4. `application.yml` env placeholders: `XUGU_JDBC_URL` / `XUGU_USER` / `XUGU_PASSWORD` with local SYSDBA fallbacks; `compatiblemode=NONE`
5. Explicit dialect `com.xugu.dialect.XuguDialect` (+ comment that SPI also works)
6. Entity `DemoPerson` → table `HIB_DEMO_PERSON`; startup CRUD runner; gated `@SpringBootTest` IT
7. README documents env keys + run/test commands
8. Dependency-tree evidence: `hibernate-core:jar:7.4.5.Final`

## Hibernate force proof

Boot 4.1.0 BOM: `<hibernate.version>7.4.1.Final</hibernate.version>`  
Module override: `<hibernate.version>7.4.5.Final</hibernate.version>` + explicit `hibernate-core` in `dependencyManagement` after BOM import.

```text
org.hibernate.orm:hibernate-core:jar:7.4.5.Final:compile
```

Evidence: `harness/evidence/implementer/P-009/dependency-tree-hibernate.txt`

## Connection defaults (local reference only)

| Key | Default |
|---|---|
| `XUGU_JDBC_URL` | `jdbc:xugu://127.0.0.1:5138/SYSTEM?compatiblemode=NONE` |
| `XUGU_USER` | `SYSDBA` |
| `XUGU_PASSWORD` | `SYSDBA` |

No production secrets committed.

## Observed IT (real DB)

- Hibernate ORM core version **7.4.5.Final**
- Dialect: **XuguDialect**
- JDBC URL: `jdbc:xugu://127.0.0.1:5138/SYSTEM?compatiblemode=NONE`
- Driver: XuguDB JDBC Driver
- CRUD: insert + select on `HIB_DEMO_PERSON`; `@AfterAll` DROP TABLE IF EXISTS

## Local command results

| Command | Exit |
|---|---|
| `mvn -q test` | **0** |
| `mvn -q -pl demo-spring-boot -am test -Dxugu.run.integration=true` | **0** |
| `mvn -q test -Dxugu.run.integration=true` | **0** |
| `python harness/scripts/verify.py --phase P-009` | **VERIFY PASS** |

Note: `mvn -pl demo-spring-boot test` **without** `-am` fails to resolve reactor sibling `xugu-dialect` unless previously installed to local m2. Prefer `-am` or root reactor.

## Explicitly NOT done

- Dialect core Java changes (none)
- Sibling hibernate-dialect reference
- Accept / git commit / tag / push

## Next step

Independent test RP-02 → reviewer RP-03 (required).
