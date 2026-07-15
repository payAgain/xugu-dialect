# P-001 Acceptance Evidence (I-002)

> Phase: `P-001`  
> Initiative: `I-002`  
> Build: `B-001`  
> Result: `PASS`  
> Role: orchestrator (Accept)

## Approved scope

- Build: B-001 (P-001 only) — `harness/builds/B-001.json`
- Human Gate approval: 「批准 B-001，范围仅 P-001」
- Goal: HQL/Criteria pagination → XuGu `LIMIT count OFFSET offset` via `XuguSqlAstTranslator`; lock order matches LimitHandler; version **7.4.5.Final**

## Role pipeline

| Step | Role | Status | Invocation | Independent | Evidence |
|---|---|---|---|---|---|
| RP-01 | implementer | passed | `impl-p001-20260715` | N/A | `harness/evidence/implementer/P-001/` |
| RP-01b | implementer | passed | `impl-p001-fix-locklimit-20260715` | N/A | lock+page IT + NOTES |
| RP-02 | test | passed | `test-p001-retest-20260715` | true | `harness/evidence/test/P-001/TEST-REPORT-RETEST.md` |
| RP-03 | reviewer | passed | `rev-p001-recheck-20260715` | true | `harness/evidence/reviewer/P-001/REVIEW-RECHECK.md` |

## Lock / pagination note

- Prior RP-03 `request-changes` (`rev-p001-20260715`): AST/HQL FOR UPDATE→LIMIT(+WAIT) unproven.
- Fix (`impl-p001-fix-locklimit-20260715`): gated IT `hqlLockAndPageEmitsForUpdateBeforeLimitAndWaitAfter`.
- Observed SQL:
  - `… for update of phpe1_0.id limit ? offset ?`
  - `… for update of phpe1_0.id limit ? offset ? wait 2000`
- Retest PASS; recheck **approve**; MAJOR **CLOSED**.

## Command verification

- Phase verification evidence: `harness/evidence/test/P-001/verification-retest.json`
- Also: implementer + prior test `verification.json`
- Overall status: **VERIFY PASS**
- Required check IDs covered: `build`, `test`

## Observed affected flows

| Flow | Method | Result | Evidence |
|---|---|---|---|
| hql-pagination-offset-fetch-real-db | `XuguHqlPaginationIT.hqlSetFirstResultMaxResultsUsesLimitNotFetchFirst` | PASS | test retest log |
| hql-lock-page-for-update-order-real-db | `XuguHqlPaginationIT.hqlLockAndPageEmitsForUpdateBeforeLimitAndWaitAfter` | PASS | `IT-RESULT-RETEST.txt` |

## Residual risks

- MINOR carry-forward: unlocked page IT does not hard-assert `offset` token (logs show `limit ? offset ?`).
- Deferred: P-002 sequence metadata; P-003 docs; Ship; sibling dialect port.

## Version control checkpoint

- Branch: `fix/i-002-hql-pagination-sequence-metadata`
- Candidate commit: *(filled after must-commit)*
- Deferred reason when no commit: N/A (must-commit on Accept)

## Acceptance decision

- Decision: `accepted`
- Decided by: `orchestrator`
- Date: 2026-07-15
- Blocker reference when not accepted: N/A
- Reviewer decision: `approve` (`rev-p001-recheck-20260715`; supersedes `request-changes` / `rev-p001-20260715`)
- Pipeline: RP-01 / RP-01b / RP-02 / RP-03 all `passed`
- Readiness: HQL pagination + AST lock order PASS with VERIFY PASS + real-DB IT
