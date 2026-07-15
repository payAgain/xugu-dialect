# P-001 Test Report (independent test role)

> Phase: `P-001`  
> Initiative: `I-002`  
> Build: `B-001`  
> Invocation: `test-p001-20260715`  
> Role: `test` (independent context from implementer `impl-p001-20260715`)  
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
| 2 | `mvn -q test "-Dxugu.run.integration=true"` | 0 | PASS (live IT incl. `XuguHqlPaginationIT`) |
| 3 | `python harness/scripts/verify.py --phase P-001 --evidence harness/evidence/test/P-001/verification.json` | 0 | **VERIFY PASS** |

## Project verify evidence

- Path: `harness/evidence/test/P-001/verification.json`
- Overall status: `PASS`
- Required checks: `build` PASS (exit 0), `test` PASS (exit 0)
- Optional: `lint` NOT_APPLICABLE
- Embedded harness: PASS

## Spot checks

| Check | Expected | Observed | Result |
|---|---|---|---|
| HQL pagination SQL | `limit` / `offset`, not `fetch first` | `select … from HIB_P001_HQL_PAGE … order by … limit ? offset ?` | PASS |
| Factory non-null | `getSqlAstTranslatorFactory()` wired | Returns `StandardSqlAstTranslatorFactory` → `XuguSqlAstTranslator`; unit + IT assert non-null | PASS |
| Artifact version | `7.4.5.Final` | Parent/dialect/demo POMs + JAR `xugu-dialect-7.4.5.Final.jar` (24277 bytes); Hibernate core log `7.4.5.Final` | PASS |
| No ANSI OFFSET/FETCH | no `fetch first` / `rows only` in HQL page SQL | IT asserts + IT log has no `fetch first` | PASS |

## Observed affected flows

| Flow | Method | Expected | Observed | Result | Evidence |
|---|---|---|---|---|---|
| hql-pagination-offset-fetch-real-db | `XuguHqlPaginationIT.hqlSetFirstResultMaxResultsUsesLimitNotFetchFirst` on live XuguDB (`-Dxugu.run.integration=true`) | No E19132; SQL uses LIMIT; window ids 5,6,7 for offset 4 / max 3 | IT PASS; SQL `limit ? offset ?`; window `[5, 6, 7]` | PASS | `mvn-test-integration.log`, `IT-RESULT.txt`, this report |

## Readiness dimensions (test view)

| Dimension | Observation | Result |
|---|---|---|
| functional-correctness | Offline + gated HQL pagination IT green; correct page window | PASS |
| performance-and-capacity | Hot path uses LimitHandler-stable `LIMIT count OFFSET offset` (no extra wrap) | PASS |
| maintainability | Translator + factory wiring covered by unit + IT | PASS |
| compatibility | Version stays `7.4.5.Final`; live XuGu accepts LIMIT form | PASS |

## Verdict

**PASS** — RP-02 `test-p001-20260715`. Flow `hql-pagination-offset-fetch-real-db` observed on real DB. Next: RP-03 reviewer (`rev-p001-20260715`).
