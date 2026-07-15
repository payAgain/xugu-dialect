# P-006 Test Report (independent test role)

> Phase: `P-006`  
> Initiative: `I-001`  
> Build: `B-006`  
> Invocation: `test-p006-20260715`  
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
| HEAD (test time) | `4bf40759a2d48c7307a3dcea0bc6f8ebcfaaf3e2` |
| Product code changes by test | none |
| Live DB | XuguDB `jdbc:xugu://127.0.0.1:5138/SYSTEM?...&compatiblemode=NONE` (driver: XuguDB JDBC Driver; dialect: XuguDialect; version: 12.0) |

## Commands and exit codes

| # | Command | Exit code | Result |
|---|---|---|---|
| 1 | `mvn -q test` (gate default/off) | 0 | PASS (13 IT skipped) |
| 2 | `mvn -q test -Dxugu.run.integration=true` | 0 | PASS (13 IT executed on real XuguDB) |
| 3 | `python harness/scripts/verify.py --phase P-006 --evidence harness/evidence/test/P-006/verification.json` | 0 | `VERIFY PASS` |

## Surefire counts

### Offline (`xugu.run.integration=false`)

| Suite | tests | failures | errors | skipped |
|---|---:|---:|---:|---:|
| `XuguDialectTest` | 7 | 0 | 0 | 0 |
| `XuguFunctionRegistryTest` | 5 | 0 | 0 | 0 |
| `XuguIdentitySequenceTest` | 6 | 0 | 0 | 0 |
| `XuguPaginationLockTest` | 10 | 0 | 0 | 0 |
| `XuguTypeRoundTripIT` | 4 | 0 | 0 | 4 |
| `XuguDdlIT` | 1 | 0 | 0 | 1 |
| `XuguBinarySchemaExportIT` | 1 | 0 | 0 | 1 |
| `XuguPaginationIT` | 1 | 0 | 0 | 1 |
| `XuguLockIT` | 2 | 0 | 0 | 2 |
| `XuguIdentitySequenceIT` | 2 | 0 | 0 | 2 |
| `XuguFunctionRegistryIT` | 2 | 0 | 0 | 2 |
| **Total** | **41** | **0** | **0** | **13** |

### Integration gate ON (real XuguDB)

| Suite | tests | failures | errors | skipped |
|---|---:|---:|---:|---:|
| `XuguDialectTest` | 7 | 0 | 0 | 0 |
| `XuguFunctionRegistryTest` | 5 | 0 | 0 | 0 |
| `XuguIdentitySequenceTest` | 6 | 0 | 0 | 0 |
| `XuguPaginationLockTest` | 10 | 0 | 0 | 0 |
| `XuguTypeRoundTripIT` | 4 | 0 | 0 | 0 |
| `XuguDdlIT` | 1 | 0 | 0 | 0 |
| `XuguBinarySchemaExportIT` | 1 | 0 | 0 | 0 |
| `XuguPaginationIT` | 1 | 0 | 0 | 0 |
| `XuguLockIT` | 2 | 0 | 0 | 0 |
| `XuguIdentitySequenceIT` | 2 | 0 | 0 | 0 |
| `XuguFunctionRegistryIT` | 2 | 0 | 0 | 0 |
| **Total** | **41** | **0** | **0** | **0** |

**IT summary:** 13 IT methods executed when gate ON, 0 failed, 0 skipped. Offline: 13 IT skipped via `Assumptions.assumeTrue(XuguITGate.isEnabled())`. Unit: 28 passed both runs (`7+5+6+10`). P-006 focused: `XuguFunctionRegistryIT` 2/2 PASS.

## Project verify evidence

- Path: `harness/evidence/test/P-006/verification.json`
- Overall status: `PASS`
- Required checks: `build` PASS (exit 0), `test` PASS (exit 0)
- Optional: `lint` NOT_APPLICABLE
- Harness check embedded: `HARNESS_CHECK PASS`

## Spot-check (function forms)

| Capability | Expected / locked | Unit | Live IT (SHOW_SQL / native) | Result |
|---|---|---|---|---|
| UUID primary | `uuid()` | `PRIMARY_UUID_FUNCTION=uuid`; descriptor registered | HQL → `select uuid()`; native `SELECT UUID() FROM DUAL` | PASS |
| JSON subset | `json_value` + `json_extract` only | `JsonValueFunction`; `json_extract` registered; `json_set` absent | HQL → `select json_value('{"a":1}', '$.a')`; native `JSON_EXTRACT(...) FROM DUAL` | PASS |
| listagg | `LISTAGG(...) WITHIN GROUP (ORDER BY ...)` | `ListaggFunction` descriptor | `listagg(pfe1_0.name, ',') within group (order by pfe1_0.name)` | PASS |
| A-FUN-015 | not registered (延后) | `bit_and` descriptor **null** | — (deferred; no claim) | PASS |
| Negative | unregistered fails diagnosably | `xugu_unsupported_fn_xyz` null | native `SELECT xugu_unsupported_fn_xyz() FROM DUAL` throws SQLException | PASS |

## Observed affected flows

| Flow | Method | Expected | Observed | Result | Evidence |
|---|---|---|---|---|---|
| function-registry-hql-sql-real-db | `XuguFunctionRegistryIT.functionFamilies_HqlAndNative_A_FUN` (+ negative IT) | Matrix 可实现 families render + execute on live XuguDB; UUID=`uuid()`; JSON subset; listagg WITHIN GROUP; A-FUN-015 not registered | Live SHOW_SQL: concat/substring/lower/abs/current_timestamp/extract/cast/count/sum/`uuid()`/`json_value`/`listagg … within group`; native JSON_EXTRACT + UUID(); bit_and absent in unit; unsupported FN fails | PASS | `mvn-test-integration.log`, `com.xugu.dialect.it.XuguFunctionRegistryIT.txt`, `IT-RESULT.txt` |

## Readiness dimensions (test view)

| Dimension | Observation | Result |
|---|---|---|
| functional-correctness | Unit + real-DB IT pass for function families incl. uuid/json/listagg | PASS |
| maintainability | IT gated; offline suite green without DB; `OfflineConnectionProvider` for unit registry boot | PASS |
| compatibility | Hibernate 7.4 function SPI; no MySQL/Oracle Dialect inheritance in this Phase path; JSON HQL needs `JSON_FUNCTIONS_ENABLED` (documented) | PASS |

## Residual notes

- Test role did not modify product code.
- DB unreachable with gate ON would be FAIL/blocker (no mock path); this run connected successfully.
- A-FUN-015 remains deferred (matrix 延后) — confirmed not registered.
- A-FUN-019/020/021 remain deferred per implementer — out of P-006 required flow.
- Next: RP-03 reviewer (risk_score=8, Full review required).

## Verdict

**PASS** — offline test / real-DB IT / verify green; required observed flow `function-registry-hql-sql-real-db` evidenced; UUID=`uuid()`, JSON subset, listagg WITHIN GROUP, and A-FUN-015 not-registered confirmed.
