# P-009 Test Report (independent test role)

> Phase: `P-009`  
> Initiative: `I-001`  
> Build: `B-009`  
> Invocation: `test-p009-20260715`  
> Role: `test` (independent context from implementer)  
> Verdict: **PASS**  
> Date: 2026-07-15

## Environment

| Item | Value |
|---|---|
| Maven | Apache Maven 3.9.9 (`C:\Users\admin\tools\apache-maven-3.9.9\bin` prepended to PATH) |
| JDK | Oracle 21.0.1 |
| Working directory | `E:\Work\java\hibernate-test` |
| Branch | `feat/i-001-xugu-dialect-major` |
| HEAD (test time) | `2307ab7aba2df225267f39bfef98140d50522b77` |
| Product code changes by test | none |
| Live DB | XuguDB `jdbc:xugu://127.0.0.1:5138/SYSTEM?compatiblemode=NONE` (driver: XuguDB JDBC Driver; dialect: XuguDialect; version: 12.0) |

## Commands and exit codes

| # | Command | Exit code | Result |
|---|---|---|---|
| 1 | `mvn -q test` (gate default/off) | 0 | PASS (live IT skipped) |
| 2 | `mvn -q -pl demo-spring-boot -am test -Dxugu.run.integration=true` | 0 | PASS (demo IT + dialect IT on real XuguDB) |
| 3 | `python harness/scripts/verify.py --phase P-009 --evidence harness/evidence/test/P-009/verification.json` | 0 | `VERIFY PASS` |
| 4 | `mvn -pl demo-spring-boot dependency:tree -Dincludes=org.hibernate.orm:hibernate-core` | 0 | `hibernate-core:jar:7.4.5.Final:compile` |

## Surefire / demo counts

### Offline (`mvn -q test`, gate off)

| Observation | Result |
|---|---|
| Reactor exit | 0 |
| Demo `DemoOfflineSmokeTest` | executes (no Spring/DB) |
| Demo `DemoPersonCrudIT` | skipped via `@EnabledIf` / `XuguIntegrationGate` |
| Dialect IT | skipped when gate off (same gate pattern as prior phases) |

### Integration gate ON (`-pl demo-spring-boot -am`, real XuguDB)

| Suite | tests | failures | errors | skipped |
|---|---:|---:|---:|---:|
| `DemoOfflineSmokeTest` | 2 | 0 | 0 | 0 |
| `DemoPersonCrudIT` | 1 | 0 | 0 | 0 |

**Demo IT:** `persistAndFindPerson` PASS — Boot 4.1.0 starts, Hibernate 7.4.5.Final, dialect `XuguDialect`, insert/select on `HIB_DEMO_PERSON`, `@AfterAll` DROP TABLE.

## Project verify evidence

- Path: `harness/evidence/test/P-009/verification.json`
- Overall status: `PASS`
- Required checks: `build` PASS (exit 0), `test` PASS (exit 0)
- Optional: `lint` NOT_APPLICABLE
- Harness check embedded: `HARNESS_CHECK PASS`

## Spot-check

| Check | Expected | Observed | Result |
|---|---|---|---|
| spring-boot-demo-starts-against-real-db | Boot starts; CRUD on live XuguDB | `DemoPersonCrudIT` Started; insert/select `hib_demo_person`; dialect=`XuguDialect`; Hibernate **7.4.5.Final** | PASS |
| hibernate-core 7.4.5.Final | Forced over Boot BOM 7.4.1 | `org.hibernate.orm:hibernate-core:jar:7.4.5.Final:compile` | PASS |
| env credential overrides | `XUGU_JDBC_URL` / `XUGU_USER` / `XUGU_PASSWORD` | `${…}` placeholders in `application.yml`; defaults local SYSDBA only | PASS |
| no production secrets | Only Charter local defaults | SYSDBA/127.0.0.1 defaults; README warns against prod secrets | PASS |
| dialect not implemented in demo | No dialect Java in demo | demo `src/main/java` = app/entity/repo/runner only; references `com.xugu.dialect.XuguDialect` | PASS |

## Observed affected flows

| Flow | Method | Expected | Observed | Result | Evidence |
|---|---|---|---|---|---|
| spring-boot-demo-starts-against-real-db | `DemoPersonCrudIT.persistAndFindPerson` | App starts against real XuguDB; core CRUD | Boot 4.1.0 + Hibernate 7.4.5.Final + XuguDialect; persist/find OK | PASS | `mvn-test-integration-demo.log`, `IT-RESULT.txt`, Surefire XML |
| hibernate-version-forced-745 | Maven `dependency:tree` | Hibernate 7.4.5.Final not BOM 7.4.1 | `hibernate-core:jar:7.4.5.Final:compile` | PASS | `dependency-tree-hibernate.txt` |

## Readiness dimensions (test view)

| Dimension | Observation | Result |
|---|---|---|
| functional-correctness | Demo IT persist/find on real XuguDB | PASS |
| maintainability | Offline smoke green; IT gated | PASS |
| deployment-and-configuration | Boot 4.1.0 + forced Hibernate 7.4.5; env keys documented | PASS |
| security-and-privacy | Env overrides; only local Charter defaults in VCS | PASS |
| observability | SQL + dialect/version logged in IT output | PASS |

## Residual notes

- Test role did not modify product code.
- Primary IT command uses `-am` so reactor sibling `xugu-dialect` resolves; without `-am`, sibling resolution may fail unless installed to local m2.
- `verify.py` re-runs offline `mvn test` (gate off); demo IT Surefire XML captured from the integration run afterward.
- Next: RP-03 reviewer (required; secrets + Boot/Hibernate alignment).

## Verdict

**PASS** — offline test / demo real-DB IT / verify green; flows `spring-boot-demo-starts-against-real-db` and `hibernate-version-forced-745` evidenced; env overrides present; no dialect implementation inside demo; no production secrets beyond local Charter defaults.
