# P-004 Test Report — Retest (after request-changes fix)

> Phase: `P-004`  
> Initiative: `I-001`  
> Build: `B-004`  
> Invocation: `test-p004-retest-20260715`  
> Prior RP-02: `test-p004-20260715`  
> Role: `test` (independent re-verify after `impl-p004-fix-20260715`)  
> Verdict: **PASS**  
> Date: 2026-07-15

## Environment

| Item | Value |
|---|---|
| Maven | Apache Maven 3.9.9 (`C:\Users\admin\tools\apache-maven-3.9.9\bin`) |
| JDK | Oracle 21.0.1 |
| Working directory | `E:\Work\java\hibernate-test` |
| Branch | `feat/i-001-xugu-dialect-major` |
| Product code changes by test | none |
| Live DB | XuguDB `jdbc:xugu://127.0.0.1:5138/SYSTEM?...&compatiblemode=NONE` (driver: XuguDB JDBC Driver; dialect: XuguDialect; version: 12.0) |

## Commands and exit codes

| # | Command | Exit code | Result |
|---|---|---|---|
| 1 | `mvn -q test` (gate default/off) | 0 | PASS (9 IT skipped) |
| 2 | `mvn -q test -Dxugu.run.integration=true` | 0 | PASS (9 IT on real XuguDB) |
| 3 | `python harness/scripts/verify.py --phase P-004 --evidence harness/evidence/test/P-004/verification-retest.json` | 0 | `VERIFY PASS` |

## Surefire counts

### Offline (`xugu.run.integration=false`)

| Suite | tests | failures | errors | skipped |
|---|---:|---:|---:|---:|
| `XuguDialectTest` | 7 | 0 | 0 | 0 |
| `XuguPaginationLockTest` | 10 | 0 | 0 | 0 |
| `XuguTypeRoundTripIT` | 4 | 0 | 0 | 4 |
| `XuguDdlIT` | 1 | 0 | 0 | 1 |
| `XuguBinarySchemaExportIT` | 1 | 0 | 0 | 1 |
| `XuguPaginationIT` | 1 | 0 | 0 | 1 |
| `XuguLockIT` | 2 | 0 | 0 | 2 |
| **Total** | **26** | **0** | **0** | **9** |

### Integration gate ON (real XuguDB)

| Suite | tests | failures | errors | skipped |
|---|---:|---:|---:|---:|
| `XuguDialectTest` | 7 | 0 | 0 | 0 |
| `XuguPaginationLockTest` | 10 | 0 | 0 | 0 |
| `XuguTypeRoundTripIT` | 4 | 0 | 0 | 0 |
| `XuguDdlIT` | 1 | 0 | 0 | 0 |
| `XuguBinarySchemaExportIT` | 1 | 0 | 0 | 0 |
| `XuguPaginationIT` | 1 | 0 | 0 | 0 |
| `XuguLockIT` | 2 | 0 | 0 | 0 |
| **Total** | **26** | **0** | **0** | **0** |

**IT summary:** 9 IT methods executed when gate ON, 0 failed, 0 skipped. Offline: 9 IT skipped via `Assumptions.assumeTrue(XuguITGate.isEnabled())`.

## Project verify evidence

- Path: `harness/evidence/test/P-004/verification-retest.json`
- Overall status: `PASS`
- Required checks: `build` PASS (exit 0), `test` PASS (exit 0)
- Optional: `lint` NOT_APPLICABLE
- Harness check embedded: `HARNESS_CHECK PASS`

## Request-changes confirmation (MAJOR fixes)

| Finding | Expected after fix | Observed | Result |
|---|---|---|---|
| MAJOR 1 — A-LCK-005 limitation notes | Hibernate shim only; NOT share-lock; `PESSIMISTIC_READ` → exclusive FOR UPDATE; concurrent readers may block; matrix stays **文档不允许** | Present in `XuguDialect` class + `getReadLockString` javadoc; implementer `NOTES.md` § MAJOR 1; matrix hint A-LCK-005 | **PASS** |
| MAJOR 2 — LIMIT + FOR UPDATE order | XuGu order **FOR UPDATE then LIMIT** (then WAIT if present); live execute; Hibernate `LIMIT…FOR UPDATE` rejected | Unit: `… for update limit ?` / `… for update limit ? wait 2000`; IT `limitForUpdateComboExecutes` asserts `for update` index &lt; `limit` index; live execute OK; `limit 1 for update` fails | **PASS** |

## Spot checks

| Check | Expected | Observed | Result |
|---|---|---|---|
| LIMIT form + bind | `LIMIT count OFFSET offset` with `?`; reverse bind | Unit + IT unchanged | PASS |
| No FETCH FIRST / SKIP LOCKED / FOR SHARE | Not emitted | Unchanged; A-LCK-005 docs clarify shim | PASS |
| FOR UPDATE / NOWAIT / WAIT | Fragments + real-DB | Lock IT + combo IT PASS | PASS |
| Clause order with locks | FOR UPDATE → LIMIT → WAIT | Handler `insert` appends LIMIT before trailing wait; unit + IT | PASS |

## Observed affected flows

| Flow | Method | Expected | Observed | Result | Evidence |
|---|---|---|---|---|---|
| limit-offset-pagination-real-db | `XuguPaginationIT.limitAndOffsetReturnExpectedRows` | LIMIT/OFFSET rows; no FETCH FIRST | PASS on live DB | PASS | `mvn-test-integration-retest.log`, `IT-RESULT-RETEST.txt` |
| pessimistic-lock-sql-real-db | `XuguLockIT.forUpdateExecutesAndSkipLockedUnsupported` | FOR UPDATE / OF / NOWAIT / WAIT; no skip locked | PASS | PASS | same |
| limit-for-update-combo-real-db | `XuguLockIT.limitForUpdateComboExecutes` | FOR UPDATE before LIMIT (+ WAIT); Hibernate order rejected | Live OK; default order FAIL | PASS | `com.xugu.dialect.it.XuguLockIT.txt`, NOTES observed table |

## Readiness dimensions (test view)

| Dimension | Observation | Result |
|---|---|---|
| functional-correctness | Unit + real-DB IT including LIMIT\|lock combo | PASS |
| performance-and-capacity | Constant LIMIT clause + binds | PASS |
| maintainability | Gate-skipped offline; docs for A-LCK-005 explicit | PASS |
| compatibility | XuGu grammar order enforced vs Hibernate default | PASS |

## Residual notes

- Test role did not modify product code.
- Suite grew vs prior RP-02: unit 10 (was 9), Lock IT 2 (was 1) — combo coverage added by fix.
- Next: RP-03 re-review (`rev-p004-*`); Accept/commit still blocked until approve.

## Verdict

**PASS** — offline / real-DB IT / verify green; A-LCK-005 limitation notes confirmed; LIMIT+FOR UPDATE order is **FOR UPDATE then LIMIT** and executes on real XuguDB.
