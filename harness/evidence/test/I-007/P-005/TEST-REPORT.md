# I-007 / P-005 Test Report (test role RP-02)

> Phase: `P-005`  
> Initiative: `I-007`  
> Build: `B-001`  
> Invocation: `inv-i007-p005-rp02-test`  
> Step: `RP-02`  
> Role: `test` (independent of implementer RP-01)  
> Verdict: **PASS**  
> Date: 2026-07-19

## Environment

| Item | Value |
|---|---|
| Maven | Apache Maven 3.9.9 (`C:\Users\admin\tools\apache-maven-3.9.9`) |
| Working directory | `E:\Work\java\hibernate-test` |
| Branch | `feat/i-007-capability-hardening-abc` |
| HEAD | `6a3385d` |
| Closure path | **Track B consumer deepening** (Flyway + bulk delete + HQL + read-only tx) |
| Product code changes by test | none |
| `org/` touched by test | no |

## P-005 deliverable verification

| Check | Result | Evidence |
|---|---|---|
| Offline build green | **PASS** | `mvn-package-offline.txt` exit 0 |
| Offline reactor test green | **PASS** | dialect 141/0/0/54; demo 32/0/0/26 |
| Live demo `-am` green with `XUGU_RUN_IT=true` | **PASS** | demo 32/0/0/0; dialect 141/0/0/18 |
| B-FLY-001 Flyway path | **PASS** | `DemoFlywayIT#flywayMigratesMarkerTableOnXugu`; offline `DemoOfflineSmokeTest` |
| B-DEMO-001 bulk delete | **PASS** | `DemoBulkMutationIT#bulkDeletePersonNames` |
| B-DEMO-002 function/HQL deepen | **PASS** | `DemoFunctionsIT#hqlFunctionSubsetSmoke` |
| B-DEMO-003 read-only tx | **PASS** | `DemoReadOnlyTxIT#readOnlyTransactionQueriesPersistedRow` |
| No multi-datasource | **PASS** | Single `DataSource`; no secondary/multi-datasource config in demo module |
| Boot SSOT still **41** rows (not 94) | **PASS** | `contracts/consumer-path-baseline.md` L76–80, L178–189 |
| `verify.py` VERIFY PASS | **PASS** | `verification.json`, `verify.txt` |

## Commands and exit codes

| # | Command | Exit | Result |
|---|---|---:|---|
| 1 | `mvn -q -DskipTests package` | 0 | **PASS** |
| 2 | `mvn -q test` (gate off) | 0 | **PASS** |
| 3 | `$env:XUGU_RUN_IT='true'; mvn -q -pl demo-spring-boot -am test` | 0 | **PASS** |
| 4 | `python harness/scripts/verify.py --phase P-005 --evidence harness/evidence/test/I-007/P-005/verification.json` | 0 | **VERIFY PASS** |

## Test counts

### Offline (`XUGU_RUN_IT` unset)

| Module | Tests run | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| dialect | 141 | 0 | 0 | 54 |
| demo-spring-boot | 32 | 0 | 0 | 26 |

P-005 gated ITs offline: each skipped under gate off — expected. Offline Flyway classpath smoke runs without live DB.

### Live (`XUGU_RUN_IT=true`)

| Scope | Tests run | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| demo-spring-boot | 32 | 0 | 0 | 0 |
| dialect under `-am` | 141 | 0 | 0 | 18 |

| Theme | gap_id | IT method | Live result |
|---|---|---|---|
| Flyway integration | B-FLY-001 | `flywayMigratesMarkerTableOnXugu` | **PASS** |
| Demo bulk delete | B-DEMO-001 | `bulkDeletePersonNames` | **PASS** |
| Function/HQL deepen | B-DEMO-002 | `hqlFunctionSubsetSmoke` | **PASS** |
| Read-only tx smoke | B-DEMO-003 | `readOnlyTransactionQueriesPersistedRow` | **PASS** |

## Flyway SPI approach (read-only audit)

Track B Flyway uses a **demo-module SPI plugin**, not multi-datasource:

- `XuguFlywayDatabaseType` extends Flyway's `OracleDatabaseType` and maps `jdbc:xugu:` URLs to Oracle-compatible migration plumbing while SQL stays Xugu-native (`compatiblemode=NONE`).
- Registered via `META-INF/services/org.flywaydb.core.extensibility.Plugin` → `com.xugu.demo.flyway.XuguFlywayDatabaseType`.
- **Default `spring.flyway.enabled=false`** so existing I-006 gated IT paths (`ddl-auto=update`) remain unchanged; `DemoFlywayIT` enables Flyway per-test.
- Flyway and JPA share the **same single datasource** — no secondary datasource introduced.

## Multi-datasource audit

| Check | Result |
|---|---|
| Secondary / multi-datasource config in demo | **none found** |
| Multiple `@Primary` / `@Qualifier` DataSource beans | **none** |
| Track B scope constraint | **honored** |

## Boot SSOT audit (read-only)

`contracts/consumer-path-baseline.md`:

| Check | Result |
|---|---|
| Boot-required rows | **41** (A=13 / B=9 / C′=19) — **not** expanded to 94 |
| Primary SSOT `covered` | **41/41** |
| Track B items | Non-matrix table only (B-FLY-001 … B-DEMO-003) |
| SSOT rewrite by test | **none** |

## Artifacts

- `harness/evidence/test/I-007/P-005/mvn-package-offline.txt`
- `harness/evidence/test/I-007/P-005/mvn-test-offline.txt`
- `harness/evidence/test/I-007/P-005/mvn-test-live-demo.txt`
- `harness/evidence/test/I-007/P-005/surefire-summary-offline.txt`
- `harness/evidence/test/I-007/P-005/surefire-summary-live-demo.txt`
- `harness/evidence/test/I-007/P-005/IT-RESULT.txt`
- `harness/evidence/test/I-007/P-005/verify.txt`
- `harness/evidence/test/I-007/P-005/verification.json`
- `harness/evidence/test/I-007/P-005/surefire-live-demo/` (Track B surefire copies)
- `harness/evidence/test/I-007/P-005/TEST-com.xugu.demo.it.DemoFlywayIT.xml`
- `harness/evidence/test/I-007/P-005/TEST-com.xugu.demo.it.DemoBulkMutationIT.xml`
- `harness/evidence/test/I-007/P-005/TEST-com.xugu.demo.it.DemoFunctionsIT.xml`
- `harness/evidence/test/I-007/P-005/TEST-com.xugu.demo.it.DemoReadOnlyTxIT.xml`
- `harness/handoffs/test/I-007-P-005.yaml`

## Verdict

**RP-02 PASS.** Independent verification confirms implementer RP-01 Track B claim: Flyway SPI path, bulk delete, HQL/function deepen, and read-only tx smoke all green live (32/0/0/0). Boot SSOT remains frozen at **41** rows. No multi-datasource. Advance to RP-03 reviewer. No commit / Accept / Ship by test.
