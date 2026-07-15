# P-004 Test Report (independent test role)

> Phase: `P-004`  
> Initiative: `I-001`  
> Build: `B-004`  
> Invocation: `test-p004-20260715`  
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
| Product code changes by test | none |
| Live DB | XuguDB `jdbc:xugu://127.0.0.1:5138/SYSTEM?...&compatiblemode=NONE` (driver: XuguDB JDBC Driver; dialect: XuguDialect; version: 12.0) |

## Commands and exit codes

| # | Command | Exit code | Result |
|---|---|---|---|
| 1 | `mvn -q test` (gate default/off) | 0 | PASS (8 IT skipped) |
| 2 | `mvn -q test -Dxugu.run.integration=true` | 0 | PASS (8 IT executed on real XuguDB) |
| 3 | `python harness/scripts/verify.py --phase P-004 --evidence harness/evidence/test/P-004/verification.json` | 0 | `VERIFY PASS` |

## Surefire counts

### Offline (`xugu.run.integration=false`)

| Suite | tests | failures | errors | skipped |
|---|---:|---:|---:|---:|
| `XuguDialectTest` | 7 | 0 | 0 | 0 |
| `XuguPaginationLockTest` | 9 | 0 | 0 | 0 |
| `XuguTypeRoundTripIT` | 4 | 0 | 0 | 4 |
| `XuguDdlIT` | 1 | 0 | 0 | 1 |
| `XuguBinarySchemaExportIT` | 1 | 0 | 0 | 1 |
| `XuguPaginationIT` | 1 | 0 | 0 | 1 |
| `XuguLockIT` | 1 | 0 | 0 | 1 |
| **Total** | **24** | **0** | **0** | **8** |

### Integration gate ON (real XuguDB)

| Suite | tests | failures | errors | skipped |
|---|---:|---:|---:|---:|
| `XuguDialectTest` | 7 | 0 | 0 | 0 |
| `XuguPaginationLockTest` | 9 | 0 | 0 | 0 |
| `XuguTypeRoundTripIT` | 4 | 0 | 0 | 0 |
| `XuguDdlIT` | 1 | 0 | 0 | 0 |
| `XuguBinarySchemaExportIT` | 1 | 0 | 0 | 0 |
| `XuguPaginationIT` | 1 | 0 | 0 | 0 |
| `XuguLockIT` | 1 | 0 | 0 | 0 |
| **Total** | **24** | **0** | **0** | **0** |

**IT summary:** 8 IT methods executed when gate ON, 0 failed, 0 skipped. Offline: 8 IT skipped via `Assumptions.assumeTrue(XuguITGate.isEnabled())`.

## Project verify evidence

- Path: `harness/evidence/test/P-004/verification.json`
- Overall status: `PASS`
- Required checks: `build` PASS (exit 0), `test` PASS (exit 0)
- Optional: `lint` NOT_APPLICABLE
- Harness check embedded: `HARNESS_CHECK PASS`

## Spot checks

| Check | Expected | Observed | Result |
|---|---|---|---|
| LIMIT form + bind | `LIMIT count OFFSET offset` with `?` markers; reverse bind order | Unit: `limit ?` / `limit ? offset ?`; `bindLimitParametersInReverseOrder=true`; IT binds via `bindLimitParametersAtEndOfQuery` | PASS |
| No FETCH FIRST | Supported paths must not emit `FETCH FIRST` | Unit + IT assert absent; `XuguLimitHandler` only emits `limit`/`offset` | PASS |
| No SKIP LOCKED | Never emit / claim skip-locked | `supportsSkipLocked=false`; `XuguLockingSupport` skipLocked=false; helpers return plain `for update`; unit asserts no `skip locked` | PASS |
| No FOR SHARE | Not emitted on supported paths | `getReadLockString` upgrades to `for update`; unit asserts no `share` | PASS |
| FOR UPDATE / NOWAIT / WAIT | Fragments + real-DB execute | Unit: ` for update`, ` for update nowait`, ` for update wait 2000`; IT executes FOR UPDATE / OF / NOWAIT / WAIT 2000 ms (inline or parenthesized fallback) | PASS |

## Observed affected flows

| Flow | Method | Expected | Observed | Result | Evidence |
|---|---|---|---|---|---|
| limit-offset-pagination-real-db | `XuguPaginationIT.limitAndOffsetReturnExpectedRows` on live JDBC | LIMIT/OFFSET rows correct; no FETCH FIRST | LIMIT 3 → ids `[1,2,3]`; LIMIT 3 OFFSET 4 → ids `[5,6,7]`; SQL contains `limit`/`offset` | PASS | `mvn-test-integration.log`, `com.xugu.dialect.it.XuguPaginationIT.txt`, `IT-RESULT.txt` |
| pessimistic-lock-sql-real-db | `XuguLockIT.forUpdateExecutesAndSkipLockedUnsupported` on live JDBC | FOR UPDATE (+ OF) / NOWAIT / WAIT executable; skip locked unsupported | All lock smokes PASS; `supportsSkipLocked==false`; no `skip locked` in helpers | PASS | `mvn-test-integration.log`, `com.xugu.dialect.it.XuguLockIT.txt`, `IT-RESULT.txt` |

## Readiness dimensions (test view)

| Dimension | Observation | Result |
|---|---|---|
| functional-correctness | Unit + real-DB IT pass for pagination and locks | PASS |
| performance-and-capacity | LimitHandler uses constant clause + bind markers (no value concat); implementer NOTES cover large-offset engine cost | PASS (test confirms bind form) |
| maintainability | IT gated; offline suite green without DB | PASS |
| compatibility | Hibernate 7.4 LimitHandler / LockingSupport; no MySQL/Oracle Dialect inheritance checked via prior phases + P-004 code paths | PASS |

## Residual notes

- Test role did not modify product code.
- DB unreachable with gate ON would be FAIL/blocker (no mock path); this run connected successfully.
- Next: RP-03 reviewer (risk_score=8, Full review required).

## Verdict

**PASS** — offline test / real-DB IT / verify green; both required observed flows evidenced; LIMIT bind form and lock SQL spot checks satisfied.
