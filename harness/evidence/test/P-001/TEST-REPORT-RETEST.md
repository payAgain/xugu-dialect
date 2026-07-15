# P-001 Test Report — Retest (after lock+limit AST fix)

> Phase: `P-001`  
> Initiative: `I-002`  
> Build: `B-001`  
> Invocation: `test-p001-retest-20260715`  
> Prior RP-02: `test-p001-20260715`  
> Role: `test` (independent re-verify after `impl-p001-fix-locklimit-20260715`)  
> Verdict: **PASS**  
> Date: 2026-07-15

## Environment

| Item | Value |
|---|---|
| Maven | Apache Maven 3.9.9 (`C:\Users\admin\tools\apache-maven-3.9.9\bin`) |
| Working directory | `E:\Work\java\hibernate-test` |
| Branch | `fix/i-002-hql-pagination-sequence-metadata` |
| Live DB | XuguDB @ 127.0.0.1:5138 (`compatiblemode=NONE`) |
| Product code changes by test | none |

## Commands and exit codes

| # | Command | Exit | Result |
|---|---|---:|---|
| 1 | `mvn -q test` | 0 | PASS (offline) |
| 2 | `mvn -q test "-Dxugu.run.integration=true"` | 0 | PASS (live IT incl. lock+page) |
| 3 | `python harness/scripts/verify.py --phase P-001 --evidence harness/evidence/test/P-001/verification-retest.json` | 0 | **VERIFY PASS** |

## Project verify evidence

- Path: `harness/evidence/test/P-001/verification-retest.json`
- Overall status: `PASS`
- Required checks: `build` PASS (exit 0), `test` PASS (exit 0)
- Optional: `lint` NOT_APPLICABLE
- Embedded harness: PASS

## Request-changes confirmation (MAJOR fix)

| Finding | Expected after fix | Observed | Result |
|---|---|---|---|
| MAJOR — AST/HQL FOR UPDATE → LIMIT (+ WAIT) | `for update` index &lt; `limit` index; WAIT after LIMIT when present; live execute | See SQL below; IT `hqlLockAndPageEmitsForUpdateBeforeLimitAndWaitAfter` PASS | **PASS** |

### Observed SQL (lock + page IT)

```text
select phpe1_0.id from HIB_P001_HQL_PAGE phpe1_0 order by phpe1_0.id for update of phpe1_0.id limit ? offset ?
select phpe1_0.id from HIB_P001_HQL_PAGE phpe1_0 order by phpe1_0.id for update of phpe1_0.id limit ? offset ? wait 2000
```

Order matches `XuguLimitHandler`: **FOR UPDATE → LIMIT → WAIT**.

## Spot checks

| Check | Expected | Observed | Result |
|---|---|---|---|
| HQL pagination SQL | `limit` / `offset`, not `fetch first` | unlocked page IT still green (prior) | PASS |
| Lock+page order | FOR UPDATE before LIMIT | `for update of … limit ? offset ?` | PASS |
| Lock+page+WAIT | FOR UPDATE … LIMIT … WAIT | `… limit ? offset ? wait 2000` | PASS |
| Factory / version | non-null / `7.4.5.Final` | unchanged | PASS |

## Observed affected flows

| Flow | Method | Expected | Observed | Result | Evidence |
|---|---|---|---|---|---|
| hql-pagination-offset-fetch-real-db | `XuguHqlPaginationIT.hqlSetFirstResultMaxResultsUsesLimitNotFetchFirst` | LIMIT form; correct window | PASS (regression) | PASS | `mvn-test-integration-retest.log` |
| hql-lock-page-for-update-order-real-db | `XuguHqlPaginationIT.hqlLockAndPageEmitsForUpdateBeforeLimitAndWaitAfter` | FOR UPDATE before LIMIT (+ WAIT after) | Live SQL as above; EXIT 0 | PASS | `mvn-test-integration-retest.log`, `IT-RESULT-RETEST.txt` |

## Readiness dimensions (test view)

| Dimension | Observation | Result |
|---|---|---|
| functional-correctness | Offline + gated IT green; lock+page order proven | PASS |
| performance-and-capacity | LimitHandler-stable `LIMIT count OFFSET offset` | PASS |
| maintainability | Gated IT covers deferral path | PASS |
| compatibility | Version `7.4.5.Final`; live XuGu accepts combo | PASS |

## Verdict

**PASS** — RP-02 retest `test-p001-retest-20260715`. MAJOR lock+page order evidenced on real DB. Next: RP-03 recheck (`rev-p001-recheck-20260715`).
