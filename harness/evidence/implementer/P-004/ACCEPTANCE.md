# P-004 Acceptance Evidence

> Phase: `P-004`  
> Initiative: `I-001`  
> Build: `B-004`  
> Result: `PASS`  
> Role: orchestrator (Accept)

## Approved scope

- Build: B-004 (P-004 only) — `harness/builds/B-004.json`
- Matrix SSOT: `contracts/feature-matrix-definition-a.md` (A-PAG-*, A-LCK-*)
- Human Gate approval: B-004 scope P-004 only

## Role pipeline

| Step | Role | Status | Invocation | Independent | Evidence |
|---|---|---|---|---|---|
| RP-01 | implementer | passed | impl-p004-20260715 | N/A | `harness/evidence/implementer/P-004/` |
| RP-01b | implementer | passed | impl-p004-fix-20260715 | N/A | A-LCK-005 docs + LIMIT|FOR UPDATE IT / LimitHandler order |
| RP-02 | test | passed | test-p004-retest-20260715 | true | `harness/evidence/test/P-004/TEST-REPORT-RETEST.md` |
| RP-03 | reviewer | passed | rev-p004-recheck-20260715 | true | `harness/evidence/reviewer/P-004/REVIEW-RECHECK.md` |

## Pagination / lock + clause-order note

- Prior RP-03 `request-changes` (`rev-p004-20260715`): A-LCK-005 limitation under-documented; LIMIT+FOR UPDATE live combo unproven.
- Fix (`impl-p004-fix-20260715`):
  - A-LCK-005: Hibernate shim only; NOT share-lock; `PESSIMISTIC_READ` → exclusive FOR UPDATE; concurrent readers may block; matrix stays **文档不允许**.
  - **Clause order:** XuGu requires **FOR UPDATE before LIMIT** (WAIT after LIMIT when present). `XuguLimitHandler` inserts LIMIT after FOR UPDATE; Hibernate default `LIMIT…FOR UPDATE` is rejected on live DB.
- Retest (`test-p004-retest-20260715`): **PASS** — 9 IT / 0 fail on real XuguDB; combo IT `limitForUpdateComboExecutes` PASS.
- Recheck (`rev-p004-recheck-20260715`): **approve**; MAJOR 1 + MAJOR 2 **CLOSED**. MINORs deferred.

## Command verification

- Phase verification evidence: `harness/evidence/test/P-004/verification-retest.json`
- Also: `harness/evidence/implementer/P-004/verification.json` (pre-fix / fix path)
- Overall status: **VERIFY PASS**
- Required check IDs covered: `build`, `test`

## Observed affected flows

| Flow | Method | Result | Evidence |
|---|---|---|---|
| limit-offset-pagination-real-db | `XuguPaginationIT` gate ON (real XuguDB) | PASS | `harness/evidence/test/P-004/mvn-test-integration-retest.log` |
| pessimistic-lock-sql-real-db | `XuguLockIT.forUpdateExecutesAndSkipLockedUnsupported` gate ON | PASS | same |
| limit-for-update-combo-real-db | `XuguLockIT.limitForUpdateComboExecutes` gate ON | PASS | `com.xugu.dialect.it.XuguLockIT.txt`; FOR UPDATE before LIMIT |

## Real DB IT

- Gate: `-Dxugu.run.integration=true`
- Retest: 9 IT executed / 0 failed / 0 skipped on live XuguDB (`compatiblemode=NONE`)
- Offline: 9 IT skipped; unit green (17)

## Residual risks

- MINOR carry-forward: parenthesized LIMIT+lock fallback coverage; large-offset engine cost (matrix note).
- Deferred Phases: identity/sequence (P-005+), functions, SPI, demo.

## Version control checkpoint

- Branch: `feat/i-001-xugu-dialect-major`
- Candidate commit: `7b995af4038a8fd3c41ccc90c0b89fa2a4494718`
- Deferred reason when no commit: N/A (must-commit on Accept)

## Acceptance decision

- Decision: `accepted`
- Decided by: `orchestrator`
- Date: 2026-07-15
- Blocker reference when not accepted: N/A
- Reviewer decision: `approve` (`rev-p004-recheck-20260715`; supersedes `request-changes` / `rev-p004-20260715`)
- Pipeline: RP-01..RP-03 all `passed` (incl. RP-01b fix + RP-02 retest)
- Readiness: pagination/locks scope PASS with VERIFY PASS + real-DB IT + FOR UPDATE before LIMIT proven
