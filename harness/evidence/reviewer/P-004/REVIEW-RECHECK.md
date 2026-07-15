# P-004 Reviewer Recheck (after request-changes fix)

> Phase: P-004  
> Initiative: I-001  
> Build: B-004  
> Invocation: 
ev-p004-recheck-20260715  
> Prior invocation: 
ev-p004-20260715 (decision: **request-changes**)  
> Fix under test: impl-p004-fix-20260715  
> Independent retest: 	est-p004-retest-20260715 (verdict: **PASS**)  
> Role: 
eviewer (readonly; landed by orchestrator)  
> Decision: **approve**  
> Date: 2026-07-15

## Scope of recheck

- Prior MAJOR findings only (A-LCK-005 limitation wording; LIMIT + FOR UPDATE live combo), plus regression spot-check that prior PASS checklist items remain intact.
- Sources: XuguDialect.java / getReadLockString javadoc; XuguLimitHandler.java; XuguPaginationLockTest; XuguLockIT.limitForUpdateComboExecutes; matrix A-LCK-005; implementer NOTES.md; test RETEST evidence (TEST-REPORT-RETEST.md, erification-retest.json, IT logs).

## Prior MAJOR disposition

| Finding | Prior | Recheck | Status |
|---|---|---|---|
| **MAJOR 1** A-LCK-005 under-documented (share-lock shim risk) | MAJOR / request-changes | Hibernate shim only; NOT share-lock; PESSIMISTIC_READ = exclusive FOR UPDATE; concurrent readers may block; matrix stays **文档不允许** | **CLOSED** |
| **MAJOR 2** LIMIT + FOR UPDATE order unproven on real XuguDB | MAJOR / request-changes | Handler emits **FOR UPDATE then LIMIT** (then WAIT); live IT executes combo; Hibernate LIMIT…FOR UPDATE rejected | **CLOSED** |

### Closure evidence (MAJOR 1 — A-LCK-005)

| Check | Evidence | Result |
|---|---|---|
| Class + method javadoc | XuguDialect + getReadLockString: shim only; not share-lock; exclusive FOR UPDATE; may block concurrent readers | PASS |
| Implementer NOTES | NOTES.md § MAJOR 1 | PASS |
| Matrix hint | contracts/feature-matrix-definition-a.md A-LCK-005 | PASS |
| No FOR SHARE emitted | Unit + IT unchanged | PASS |

### Closure evidence (MAJOR 2 — LIMIT + FOR UPDATE)

| Check | Evidence | Result |
|---|---|---|
| Clause order strategy | XuguLimitHandler inserts LIMIT after FOR UPDATE / before trailing WAIT | PASS |
| Unit asserts | …for update limit ? / …for update limit ? wait 2000 | PASS |
| Live combo IT | XuguLockIT.limitForUpdateComboExecutes — FOR UPDATE index before LIMIT; row returned | PASS |
| Hibernate default rejected | limit … for update fails on live XuGu | PASS |
| Independent retest | 	est-p004-retest-20260715: offline PASS; IT 9/9 PASS; VERIFY PASS | PASS |

**Clause order note:** XuGu requires **FOR UPDATE before LIMIT** (WAIT after LIMIT when present). Dialect LimitHandler must not rely on Hibernate default LIMIT…FOR UPDATE order.

## Checklist (recheck)

| Item | Result | Notes |
|---|---|---|
| Approved Build scope respected | PASS | B-004 = P-004 only |
| Forbidden inheritance | PASS | extends Dialect only |
| Pagination form vs matrix | PASS | LIMIT count OFFSET offset |
| Lock fragments FOR UPDATE / OF / NOWAIT / WAIT | PASS | Unit + real-DB IT |
| SKIP LOCKED not invented | PASS | unchanged |
| FOR SHARE / A-LCK-005 docs | **PASS** | Was MAJOR; now explicit shim wording |
| LIMIT + FOR UPDATE combo on live DB | **PASS** | Was MAJOR; now gated IT + clause order proven |
| Independent test RP-02 retest | PASS | 	est-p004-retest-20260715 |
| Scope creep | PASS | No identity/sequence/functions/SPI |

## Findings (this recheck)

### BLOCKER
- None

### MAJOR
- None remaining. **MAJOR 1 and MAJOR 2 CLOSED.**

### MINOR (carry-forward; do not block approve)
1. Parenthesized NOWAIT/WAIT fallback with combined LIMIT+lock parenthesized form still lightly covered.
2. Large-offset engine cost remains a matrix/docs note (no dialect buffering).

### QUESTION
- Prior QUESTION on live SELECT … LIMIT n FOR UPDATE [WAIT ms] under compatiblemode=NONE — **closed**: XuGu accepts **FOR UPDATE then LIMIT** (+ WAIT); rejects Hibernate default order.

## Validation status

- Independent test retest: **PASS** (	est-p004-retest-20260715)
- Project verify: harness/evidence/test/P-004/verification-retest.json — **PASS**
- Real DB IT (gate ON): 9 executed / 0 failed / 0 skipped
- Observed combo order: FOR UPDATE before LIMIT; FOR UPDATE … LIMIT … WAIT OK

## Recommendation

**approve** — both Majors closed with docs + live combo proof; independent retest green. Orchestrator may proceed to Accept + must-commit (Human Gate still owns Ship).

## Decision

- Decision: pprove
- Invocation: 
ev-p004-recheck-20260715
- Prior decision superseded for MAJOR gate: 
equest-changes (
ev-p004-20260715) → **approve** on recheck
- Decided by: reviewer (readonly; evidence landed by orchestrator)
- Date: 2026-07-15

## Handoff payload (for orchestrator)

`yaml
role: reviewer
phase_id: P-004
build_id: B-004
invocation_id: rev-p004-recheck-20260715
prior_invocation_id: rev-p004-20260715
step_id: RP-03
status: passed
decision: approve
required: true
evidence: harness/evidence/reviewer/P-004/REVIEW-RECHECK.md
majors_closed:
  - A-LCK-005-doc
  - LIMIT-FOR-UPDATE-IT
clause_order: FOR UPDATE then LIMIT (then WAIT)
retest: test-p004-retest-20260715
minors_deferred: true
next: orchestrator Accept + must-commit (no Ship without Human Gate)
`
