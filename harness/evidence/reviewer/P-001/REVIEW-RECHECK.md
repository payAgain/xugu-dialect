# P-001 Reviewer Recheck (after lock+limit AST fix)

> Phase: `P-001`  
> Initiative: `I-002`  
> Build: `B-001`  
> Invocation: `rev-p001-recheck-20260715`  
> Prior invocation: `rev-p001-20260715` (decision: **request-changes**)  
> Fix under test: `impl-p001-fix-locklimit-20260715`  
> Independent retest: `test-p001-retest-20260715` (verdict: **PASS**)  
> Role: reviewer (readonly; landed by orchestrator)  
> Decision: **approve**  
> Date: 2026-07-15

## Scope of recheck

- Prior MAJOR only: AST/HQL FOR UPDATE → LIMIT (+ WAIT after LIMIT) unproven on SqlAstTranslator path.
- Regression spot-check: unlocked HQL pagination LIMIT form, factory wiring, no ANSI FETCH FIRST, no P-002 creep, GAV `7.4.5.Final`.
- Sources: `XuguSqlAstTranslator.java`; `XuguHqlPaginationIT.hqlLockAndPageEmitsForUpdateBeforeLimitAndWaitAfter`; implementer NOTES; test RETEST evidence (`TEST-REPORT-RETEST.md`, `verification-retest.json`, IT log).

## Prior MAJOR disposition

| Finding | Prior | Recheck | Status |
|---|---|---|---|
| **MAJOR 1** AST/HQL FOR UPDATE before LIMIT (+ WAIT) unproven | MAJOR / request-changes | Gated IT asserts `for update` index &lt; `limit` index; WAIT after LIMIT; live XuGu executes both combos | **CLOSED** |

### Closure evidence (MAJOR 1)

| Check | Evidence | Result |
|---|---|---|
| Deferral strategy | `visitOffsetFetchClause` defers when pessimistic; `visitForUpdateClause` finally renders LIMIT via `renderLimitOffsetClauseAfterLock` (strip trailing WAIT, LIMIT, re-append) | PASS |
| Live lock+page SQL | `… for update of phpe1_0.id limit ? offset ?` | PASS |
| Live lock+page+WAIT SQL | `… for update of phpe1_0.id limit ? offset ? wait 2000` | PASS |
| Order vs LimitHandler | Matches XuGu / `XuguLimitHandler`: FOR UPDATE → LIMIT → WAIT | PASS |
| Independent retest | `test-p001-retest-20260715`: offline PASS; IT EXIT 0; VERIFY PASS | PASS |

## Checklist (recheck)

| Item | Result | Notes |
|---|---|---|
| Approved Build scope (P-001 only) | PASS | B-001 = P-001 only |
| LimitHandler-stable `LIMIT count OFFSET offset` | PASS | unchanged |
| No ANSI OFFSET/FETCH on HQL pagination | PASS | unlocked IT + logs |
| Factory wired non-null | PASS | unchanged |
| FOR UPDATE before LIMIT (+ WAIT) on AST path | **PASS** | Was MAJOR; now gated IT + live SQL |
| No `MySQLDialect` inheritance | PASS | `extends Dialect` |
| No sibling dialect port | PASS | local AST translator |
| No P-002 sequence work mixed in | PASS | no `getQuerySequencesString` in this Phase delta |
| Version stays 7.4.5.Final | PASS | spot-check |
| Independent RP-02 retest | PASS | `test-p001-retest-20260715` |

## Findings (this recheck)

### BLOCKER
- None

### MAJOR
- None remaining. **MAJOR 1 CLOSED.**

### MINOR (carry-forward; do not block approve)
1. Unlocked HQL page IT still does not hard-assert `offset` / form `limit ? offset ?` (observed in logs; lock IT now asserts order).
2. `ACCEPTANCE.md` was I-001 scaffold until this Accept — orchestrator must rewrite for I-002 (expected).

### QUESTION
- None.

## Validation status

- Independent test retest: **PASS** (`test-p001-retest-20260715`)
- Project verify: `harness/evidence/test/P-001/verification-retest.json` — **PASS**
- Observed lock+page order: FOR UPDATE before LIMIT; FOR UPDATE … LIMIT … WAIT OK

## Recommendation

**approve** — MAJOR closed with gated live IT + SQL proof; independent retest green. Orchestrator may Accept + must-commit (Human Gate still owns Ship / next Build).

## Decision

- Decision: `approve`
- Invocation: `rev-p001-recheck-20260715`
- Prior decision superseded for MAJOR gate: `request-changes` (`rev-p001-20260715`) → **approve** on recheck
- Decided by: reviewer (readonly; evidence landed by orchestrator)
- Date: 2026-07-15

## Handoff payload (for orchestrator)

```yaml
role: reviewer
phase_id: P-001
build_id: B-001
initiative_id: I-002
invocation_id: rev-p001-recheck-20260715
prior_invocation_id: rev-p001-20260715
step_id: RP-03
status: passed
decision: approve
required: true
evidence: harness/evidence/reviewer/P-001/REVIEW-RECHECK.md
majors_closed:
  - AST-HQL-FOR-UPDATE-BEFORE-LIMIT
clause_order: FOR UPDATE then LIMIT (then WAIT)
retest: test-p001-retest-20260715
minors_deferred: true
next: orchestrator Accept + must-commit (no Ship without Human Gate)
```
