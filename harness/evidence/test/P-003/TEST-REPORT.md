# P-003 Test Report (independent test role)

> Phase: `P-003`  
> Initiative: `I-001`  
> Build: `B-003`  
> Invocation: `test-p003-20260714`  
> Role: `test` (independent context from implementer)  
> Verdict: **PASS**  
> Date: 2026-07-14

## Environment

| Item | Value |
|---|---|
| Maven | Apache Maven 3.9.9 (`C:\Users\admin\tools\apache-maven-3.9.9\bin` prepended to PATH) |
| JDK | Oracle 21.0.1 |
| Working directory | `E:\Work\java\hibernate-test` |
| Product code changes by test | none |
| Live DB | XuguDB `jdbc:xugu://127.0.0.1:5138/SYSTEM?...&compatiblemode=NONE` |

## Commands and exit codes

| # | Command | Exit code | Result |
|---|---|---|---|
| 1 | `mvn -q -DskipTests package` | 0 | PASS |
| 2 | `mvn -q test` (gate default/off) | 0 | PASS (5 IT skipped) |
| 3 | `mvn -q test -Dxugu.run.integration=true` | 0 | PASS (5 IT executed on real XuguDB) |
| 4 | `python harness/scripts/verify.py --phase P-003 --evidence harness/evidence/test/P-003/verification.json` | 0 | `VERIFY PASS` |

## Surefire counts

### Offline (`xugu.run.integration=false`)

| Suite | tests | failures | errors | skipped |
|---|---:|---:|---:|---:|
| `XuguDialectTest` | 7 | 0 | 0 | 0 |
| `XuguTypeRoundTripIT` | 4 | 0 | 0 | 4 |
| `XuguDdlIT` | 1 | 0 | 0 | 1 |
| **Total** | **12** | **0** | **0** | **5** |

### Integration gate ON (real XuguDB)

| Suite | tests | failures | errors | skipped |
|---|---:|---:|---:|---:|
| `XuguDialectTest` | 7 | 0 | 0 | 0 |
| `XuguTypeRoundTripIT` | 4 | 0 | 0 | 0 |
| `XuguDdlIT` | 1 | 0 | 0 | 0 |
| **Total** | **12** | **0** | **0** | **0** |

**IT summary:** 5 IT methods executed, 0 failed, 0 skipped (when gate ON). Offline: 5 IT skipped via `Assumptions.assumeTrue(XuguITGate.isEnabled())`.

## Project verify evidence

- Path: `harness/evidence/test/P-003/verification.json`
- Overall status: `PASS`
- Required checks: `build` PASS (exit 0), `test` PASS (exit 0)
- Optional: `lint` NOT_APPLICABLE
- Harness check embedded: `HARNESS_CHECK PASS`

## Spot checks

| Check | Expected | Observed | Result |
|---|---|---|---|
| XuguDialect inheritance | extends `Dialect` only; NOT MySQL/Oracle | `public class XuguDialect extends Dialect`; no MySQL/Oracle imports/extends | PASS |
| TIMESTAMP vs DATETIME (A-TYP-008) | Hibernate timestamp → Xugu `TIMESTAMP` | `columnType`: `timestamp($p)` / `timestamp($p) with time zone`; unit asserts same; Javadoc documents choice | PASS |
| IT gate | off by default; ON via `-Dxugu.run.integration=true` | `pom.xml` default `false`; `XuguITGate` + `Assumptions.assumeTrue`; offline 5 skipped / IT 5 run | PASS |
| Real DB (no mock) | gate ON must hit live XuguDB | JDBC URL + driver `XuguDB JDBC Driver` + dialect `XuguDialect` in IT log; unreachable would `fail(...)` | PASS |

## Observed affected flows

| Flow | Method | Expected | Observed | Result | Evidence |
|---|---|---|---|---|---|
| type-mapping-roundtrip-real-db | `XuguTypeRoundTripIT` (4 methods) on live JDBC | Key types round-trip; DB reachable; illegal type diagnosable | All 4 IT methods PASS against `127.0.0.1:5138` | PASS | `mvn-test-integration.log`, Surefire `XuguTypeRoundTripIT` |
| ddl-generation-matches-xugu-docs | `XuguDdlIT.schemaExportCreateDropWithPkAndNotNull` | CREATE has NOT NULL + PK; SchemaExport works | `create table HIB_P003_DDL_PROBE (id integer not null, name varchar(64) not null, primary key (id))` then drop | PASS | `mvn-test-integration.log` lines CREATE/DROP |

## Readiness dimensions (test view)

| Dimension | Observation | Result |
|---|---|---|
| functional-correctness | Unit + real-DB IT pass for types/DDL | PASS |
| data-integrity | Round-trip IT binds/reads key types; NOT NULL rejects null | PASS |
| maintainability | IT gated; offline suite green without DB | PASS |
| compatibility | Extends `Dialect` only; TIMESTAMP mapping aligned with docs + Hibernate SqlTypes | PASS |

## Residual notes

- Test role did not modify product code.
- DB unreachable with gate ON would be FAIL/blocker (no mock path); this run connected successfully.
- Next: RP-04 reviewer (risk_score=9, Full review required).

## Verdict

**PASS** — build/test/verify green; 5/5 IT on real XuguDB; both required observed flows evidenced; spot checks satisfied.
