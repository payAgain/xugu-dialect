# P-008 Test Report (independent test role)

> Phase: `P-008`  
> Initiative: `I-001`  
> Build: `B-008`  
> Invocation: `test-p008-20260715`  
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
| HEAD (test time) | `c236aa17c1011a5da7e6feb506afa525fbfd5ef7` |
| Product code changes by test | none |
| Live DB | XuguDB `jdbc:xugu://127.0.0.1:5138/SYSTEM?...&compatiblemode=NONE` (driver: XuguDB JDBC Driver; dialect: XuguDialect; version: 12.0) |

## Commands and exit codes

| # | Command | Exit code | Result |
|---|---|---|---|
| 1 | `mvn -q test` (gate default/off) | 0 | PASS (17 IT skipped) |
| 2 | `mvn -q test -Dxugu.run.integration=true` | 0 | PASS (17 IT executed on real XuguDB) |
| 3 | `python harness/scripts/verify.py --phase P-008 --evidence harness/evidence/test/P-008/verification.json` | 0 | `VERIFY PASS` |

## Surefire counts

### Offline (`xugu.run.integration=false`)

| Suite | tests | failures | errors | skipped |
|---|---:|---:|---:|---:|
| `XuguDialectTest` | 7 | 0 | 0 | 0 |
| `XuguFunctionRegistryTest` | 5 | 0 | 0 | 0 |
| `XuguIdentitySequenceTest` | 6 | 0 | 0 | 0 |
| `XuguPaginationLockTest` | 10 | 0 | 0 | 0 |
| `XuguSchemaTempCommentTest` | 7 | 0 | 0 | 0 |
| `XuguDialectResolverTest` | 8 | 0 | 0 | 0 |
| `XuguDialectServicesResourceTest` | 3 | 0 | 0 | 0 |
| `XuguTypeRoundTripIT` | 4 | 0 | 0 | 4 |
| `XuguDdlIT` | 1 | 0 | 0 | 1 |
| `XuguBinarySchemaExportIT` | 1 | 0 | 0 | 1 |
| `XuguPaginationIT` | 1 | 0 | 0 | 1 |
| `XuguLockIT` | 2 | 0 | 0 | 2 |
| `XuguIdentitySequenceIT` | 2 | 0 | 0 | 2 |
| `XuguFunctionRegistryIT` | 2 | 0 | 0 | 2 |
| `XuguSchemaTempCommentIT` | 1 | 0 | 0 | 1 |
| `XuguDialectResolverIT` | 3 | 0 | 0 | 3 |
| **Total** | **63** | **0** | **0** | **17** |

### Integration gate ON (real XuguDB)

| Suite | tests | failures | errors | skipped |
|---|---:|---:|---:|---:|
| `XuguDialectTest` | 7 | 0 | 0 | 0 |
| `XuguFunctionRegistryTest` | 5 | 0 | 0 | 0 |
| `XuguIdentitySequenceTest` | 6 | 0 | 0 | 0 |
| `XuguPaginationLockTest` | 10 | 0 | 0 | 0 |
| `XuguSchemaTempCommentTest` | 7 | 0 | 0 | 0 |
| `XuguDialectResolverTest` | 8 | 0 | 0 | 0 |
| `XuguDialectServicesResourceTest` | 3 | 0 | 0 | 0 |
| `XuguTypeRoundTripIT` | 4 | 0 | 0 | 0 |
| `XuguDdlIT` | 1 | 0 | 0 | 0 |
| `XuguBinarySchemaExportIT` | 1 | 0 | 0 | 0 |
| `XuguPaginationIT` | 1 | 0 | 0 | 0 |
| `XuguLockIT` | 2 | 0 | 0 | 0 |
| `XuguIdentitySequenceIT` | 2 | 0 | 0 | 0 |
| `XuguFunctionRegistryIT` | 2 | 0 | 0 | 0 |
| `XuguSchemaTempCommentIT` | 1 | 0 | 0 | 0 |
| `XuguDialectResolverIT` | 3 | 0 | 0 | 0 |
| **Total** | **63** | **0** | **0** | **0** |

**IT summary:** 17 IT methods executed when gate ON, 0 failed, 0 skipped. Offline: 17 IT skipped via `Assumptions.assumeTrue(XuguITGate.isEnabled())`. Unit: 46 passed both runs (`7+5+6+10+7+8+3`). P-008 focused: `XuguDialectResolverIT` 3/3 PASS; `XuguDialectResolverTest` 8/8 PASS; `XuguDialectServicesResourceTest` 3/3 PASS.

## Project verify evidence

- Path: `harness/evidence/test/P-008/verification.json`
- Overall status: `PASS`
- Required checks: `build` PASS (exit 0), `test` PASS (exit 0)
- Optional: `lint` NOT_APPLICABLE
- Harness check embedded: `HARNESS_CHECK PASS`

## Spot-check (SPI / explicit / jar / non-match / isolation)

| Capability | Expected / locked | Unit | Live IT | Result |
|---|---|---|---|---|
| explicit-dialect-config | `hibernate.dialect=com.xugu.dialect.XuguDialect` → SessionFactory uses `XuguDialect`; simple query works | N/A (config path) | `explicitDialect_sessionFactorySimpleQuery`: insert + HQL select `explicit` | PASS |
| spi-dialect-resolver-autodetect | No explicit dialect; SPI selects `XuguDialect` with live version | offline match helpers | `spiAutoResolve_…`: dialect=`XuguDialect` major=12; count query | PASS |
| Live product/driver match | product `XuguDB` / driver contains `xugu` | `matchesXugu` / `containsXuguToken` | live metadata print + assert | PASS |
| Non-Xugu non-match (A-SPI-004) | MySQL / Oracle / PostgreSQL / blank → `null` | `returnsNullForMySQL/Oracle/PostgreSQL/Blank` | N/A (offline boundary) | PASS |
| Jar services entry (A-SPI-002) | `META-INF/services/org.hibernate.engine.jdbc.dialect.spi.DialectResolver` → `com.xugu.dialect.XuguDialectResolver` | classpath + classes + packaged jar tests | `jar-services-listing.txt`: FOUND | PASS |
| No READ UNCOMMITTED claim (A-XCUT-006) | Dialect must **not** claim RU | class javadoc: "not a XuGu ISO_LEVEL"; matrix 文档不允许 | live JDBC log `Isolation level: READ_COMMITTED` only | PASS |
| Cleanup `HIB_P008_*` | objects removed after IT | cleanup in IT finally | leftover probe ALL/USER_TABLES = **0** | PASS |

## Observed affected flows

| Flow | Method | Expected | Observed | Result | Evidence |
|---|---|---|---|---|---|
| explicit-dialect-config | `XuguDialectResolverIT.explicitDialect_sessionFactorySimpleQuery` | Explicit dialect → `XuguDialect` + core query on live XuguDB | SessionFactory dialect=`XuguDialect`; persist + HQL select OK | PASS | `mvn-test-integration.log`, `com.xugu.dialect.it.XuguDialectResolverIT.txt`, `IT-RESULT.txt` |
| spi-dialect-resolver-autodetect | `XuguDialectResolverIT.spiAutoResolve_sessionFactoryWithoutExplicitDialect` | No explicit dialect → SPI selects `XuguDialect` | dialect=`XuguDialect` v12.0; count=1 | PASS | `mvn-test-integration.log`, `IT-RESULT.txt`, `jar-services-listing.txt` |

## Readiness dimensions (test view)

| Dimension | Observation | Result |
|---|---|---|
| functional-correctness | Explicit + SPI IT pass on real XuguDB; offline match/non-match green | PASS |
| maintainability | IT gated; offline suite green without DB; jar services testable | PASS |
| compatibility | Hibernate 7.4 `DialectResolver` SPI FQCN; non-Xugu returns null; no MySQL/Oracle Dialect inheritance in path | PASS |
| deployment-and-configuration | Packaged jar contains services entry; explicit `hibernate.dialect` path verified | PASS |

## Residual notes

- Test role did not modify product code.
- DB unreachable with gate ON would be FAIL/blocker (no mock path); this run connected successfully.
- A-XCUT-006 remains 文档不允许 — confirmed no READ UNCOMMITTED support claim.
- Live JDBC isolation observed as `READ_COMMITTED` (XuGu ISO_LEVEL default).
- Next: RP-03 reviewer (risk_score=8, Full review of SPI packaging / compatibility).

## Verdict

**PASS** — offline test / real-DB IT / verify green; required flows `explicit-dialect-config` and `spi-dialect-resolver-autodetect` evidenced; jar services entry present; non-Xugu non-match PASS; no READ UNCOMMITTED claim; `HIB_P008_*` leftover probe 0.
