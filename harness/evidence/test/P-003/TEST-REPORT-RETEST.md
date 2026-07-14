# P-003 Test Report — RETEST (after A-TYP-009 BINARY fix)

> Phase: `P-003`  
> Initiative: `I-001`  
> Build: `B-003`  
> Invocation: `test-p003-retest-20260714`  
> Prior invocation: `test-p003-20260714`  
> Role: `test` (independent re-verify after `impl-p003-fix-binary-20260714`)  
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
| 1 | `mvn -q test` (gate default/off) | 0 | PASS (6 IT skipped) |
| 2 | `mvn -q test -Dxugu.run.integration=true` | 0 | PASS (6 IT executed on real XuguDB) |
| 3 | `python harness/scripts/verify.py --phase P-003 --evidence harness/evidence/test/P-003/verification-retest.json` | 0 | `VERIFY PASS` |

## Surefire counts

### Offline (`xugu.run.integration=false`)

| Suite | tests | failures | errors | skipped |
|---|---:|---:|---:|---:|
| `XuguDialectTest` | 7 | 0 | 0 | 0 |
| `XuguTypeRoundTripIT` | 4 | 0 | 0 | 4 |
| `XuguDdlIT` | 1 | 0 | 0 | 1 |
| `XuguBinarySchemaExportIT` | 1 | 0 | 0 | 1 |
| **Total** | **13** | **0** | **0** | **6** |

### Integration gate ON (real XuguDB)

| Suite | tests | failures | errors | skipped |
|---|---:|---:|---:|---:|
| `XuguDialectTest` | 7 | 0 | 0 | 0 |
| `XuguTypeRoundTripIT` | 4 | 0 | 0 | 0 |
| `XuguDdlIT` | 1 | 0 | 0 | 0 |
| `XuguBinarySchemaExportIT` | 1 | 0 | 0 | 0 |
| **Total** | **13** | **0** | **0** | **0** |

**IT summary:** 6 IT methods executed, 0 failed, 0 skipped (when gate ON). Offline: 6 IT skipped via `Assumptions.assumeTrue(XuguITGate.isEnabled())`.

## Project verify evidence

- Path: `harness/evidence/test/P-003/verification-retest.json`
- Overall status: `PASS`
- Required checks: `build` PASS (exit 0), `test` PASS (exit 0)
- Optional: `lint` NOT_APPLICABLE
- Harness check embedded: `HARNESS_CHECK PASS`

## A-TYP-009 / bare BINARY confirmation (retest focus)

| Check | Expected | Observed | Result |
|---|---|---|---|
| Unit `columnType(BINARY\|VARBINARY)` | bare `"binary"` (no `$l`) | `XuguDialectTest` asserts `"binary"` for BINARY/VARBINARY; LONG* → `"blob"` | PASS |
| SchemaExport DDL | bare `binary`, not `binary(n)` | `create table HIB_P003_BINARY_PROBE (id integer not null, payload binary not null, primary key (id))` | PASS |
| Live CREATE | succeeds under `compatiblemode=NONE` | table created then dropped on live XuguDB | PASS |
| Reject length form | IT asserts no `binary(` | `XuguBinarySchemaExportIT` PASS | PASS |

## Spot checks (regression)

| Check | Expected | Observed | Result |
|---|---|---|---|
| XuguDialect inheritance | extends `Dialect` only | unchanged | PASS |
| TIMESTAMP vs DATETIME (A-TYP-008) | `timestamp($p)` | unit + prior IT still green | PASS |
| IT gate | off by default; ON via property | offline 6 skipped / IT 6 run | PASS |
| Real DB (no mock) | gate ON hits live XuguDB | JDBC URL + `XuguDB JDBC Driver` + `XuguDialect` | PASS |

## Observed affected flows

| Flow | Method | Expected | Observed | Result | Evidence |
|---|---|---|---|---|---|
| type-mapping-roundtrip-real-db | `XuguTypeRoundTripIT` (4 methods) | Key types round-trip on live DB | All 4 PASS | PASS | `mvn-test-integration-retest.log` |
| ddl-generation-matches-xugu-docs | `XuguDdlIT` + `XuguBinarySchemaExportIT` | CREATE NOT NULL+PK; BINARY bare | DDL probe + `payload binary not null` | PASS | `mvn-test-integration-retest.log` |

## Residual notes

- Test role did not modify product code.
- Retest specifically clears RP-04 MAJOR A-TYP-009 after implementer bare-`binary` fix.
- Next: RP-04 reviewer re-review (risk_score=9). No Accept / commit by test.

## Verdict

**PASS** — offline + real-DB IT + verify green; SchemaExport emits bare `binary` (not `binary(n)`); 6/6 IT on live XuguDB.
