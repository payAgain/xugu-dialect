# P-001 Reviewer Evidence (Full + risk>=8)

> Phase: `P-001`  
> Initiative: `I-002`  
> Build: `B-001`  
> Invocation: `rev-p001-20260715`  
> Role: `reviewer` (readonly; land via orchestrator)  
> Decision: **request-changes**  
> Date: 2026-07-15

## Scope reviewed

- Phase packet: `harness/tasks/P-001.md` (risk_score=8, RP-03)
- Build: B-001 approved = P-001 only
- Main: `XuguSqlAstTranslator.java`, `XuguDialect.getSqlAstTranslatorFactory()`
- Compare: `XuguLimitHandler` (`LIMIT count OFFSET offset` + FOR UPDATE/WAIT insert)
- Tests: `XuguSqlAstTranslatorTest`, `XuguHqlPaginationIT` (+ entity)
- Implementer: `harness/evidence/implementer/P-001/` (`impl-p001-20260715`)
- Independent test: `harness/evidence/test/P-001/` + `harness/handoffs/test/P-001.yaml` (`test-p001-20260715`, VERIFY PASS)
- Contract constraint: no `MySQLDialect` / Oracle inheritance; no sibling `hibernate-dialect` port; no P-002 sequence

## Checklist

| Item | Result | Notes |
|---|---|---|
| Approved Build scope (P-001 only) | PASS | Product delta = translator + factory + HQL page IT/unit |
| LimitHandler-stable form `LIMIT count OFFSET offset` | PASS | Uses `renderLimitOffsetClause` (not MySQL `renderCombinedLimitClause` / `LIMIT offset,count`) |
| No ANSI OFFSET/FETCH on HQL pagination | PASS | Live SQL: `… limit ? offset ?`; IT forbids `fetch first` / `rows only` |
| Factory wired non-null | PASS | `StandardSqlAstTranslatorFactory` → `XuguSqlAstTranslator` |
| FOR UPDATE before LIMIT (+ WAIT after LIMIT) on AST path | FAIL (evidence) | Code defers/inserts like LimitHandler; **no unit/IT proves AST/HQL lock+page order** |
| No `MySQLDialect` inheritance | PASS | `XuguDialect extends Dialect`; translator extends `SqlAstTranslatorWithMerge` |
| No sibling `hibernate-dialect` port | PASS | Local implementation; javadoc only contrasts Hibernate core MySQL form |
| No P-002 sequence work mixed in | PASS | No `getQuerySequencesString` / `all_sequences` in `dialect/src/main` |
| Version stays 7.4.5.Final | PASS | Test spot-check + GAV |
| Independent RP-02 | PASS | `test-p001-20260715`; offline + gated IT EXIT 0; VERIFY PASS |

## Findings

### BLOCKER
- None

### MAJOR
1. **AST/HQL path: FOR UPDATE → LIMIT (+ WAIT) order unproven**  
   - `visitOffsetFetchClause` defers LIMIT when pessimistic lock present; `visitForUpdateClause` finally calls `renderLimitOffsetClauseAfterLock` (strip trailing `NOWAIT`/`WAIT`, render LIMIT, re-append) — structurally mirrors `XuguLimitHandler.insert`.  
   - P-001 AC boundary explicitly requires order consistent with LimitHandler, evidence via gated IT + SQL excerpt; RP-03 purpose is this comparison.  
   - Current evidence: unlocked HQL page IT only; unit tests only assert factory non-null / class loadable — **deferral path never asserted**.  
   - P-004 precedent: unproven LIMIT+FOR UPDATE order was MAJOR at risk≥8 (LimitHandler path since closed on live DB).  
   - **Required before approve:** add (a) offline unit/SQL assertion for AST translator lock+limit (+ WAIT if feasible) order, and/or (b) gated IT: HQL/Criteria `setFirstResult`/`setMaxResults` + pessimistic lock, capture SQL proving `for update` before `limit` (and `wait` after `limit` when present), execute on live XuGu.

### MINOR
1. `XuguHqlPaginationIT` asserts `limit` and forbids `fetch first`/`rows only`, but does not assert `offset` / form `limit ? offset ?` (observed in logs, not hardened in asserts).  
2. `harness/evidence/implementer/P-001/ACCEPTANCE.md` still I-001 scaffold content — expected until Accept; orchestrator must not treat it as I-002 Accept evidence.

### QUESTION
- None (grammar already proven on LimitHandler path in I-001 P-004; gap is AST/HQL path evidence only).

## Validation status

- Independent test: **PASS** (`test-p001-20260715`)
- `harness/evidence/test/P-001/verification.json` → **VERIFY PASS** (build + test)
- Observed flow `hql-pagination-offset-fetch-real-db`: PASS — SQL `limit ? offset ?`, window `[5,6,7]`, no E19132
- AST/HQL FOR UPDATE+LIMIT(+WAIT): **not evidenced** → MAJOR 1

## Confirmed constraints (explicit review asks)

| Constraint | Verdict |
|---|---|
| No ANSI OFFSET/FETCH for HQL pagination | **Confirmed** (code + live IT) |
| Form matches LimitHandler `LIMIT count OFFSET offset` | **Confirmed** |
| No `MySQLDialect` inheritance | **Confirmed** |
| No sibling dialect port | **Confirmed** |
| No P-002 sequence metadata mixed in | **Confirmed** |
| FOR UPDATE ordering vs LimitHandler | **Implemented, not evidenced** |

## Recommendation

**request-changes** — P0 HQL ANSI OFFSET/FETCH fix is correct, scoped, and independently tested; **do not Accept** until AST/HQL FOR UPDATE+LIMIT(+WAIT) order is evidenced (unit and/or gated IT).

## Decision

- Decision: `request-changes`
- Invocation: `rev-p001-20260715`
- Decided by: reviewer (readonly)
- Date: 2026-07-15

## Handoff payload (for orchestrator)

```yaml
role: reviewer
phase_id: P-001
build_id: B-001
initiative_id: I-002
invocation_id: rev-p001-20260715
step_id: RP-03
status: request-changes
decision: request-changes
required: true
evidence: harness/evidence/reviewer/P-001/REVIEW.md
next: implementer fix MAJOR — AST/HQL FOR UPDATE before LIMIT (+ WAIT) evidence → re-test → re-review RP-03
```

---

## Note on prior REVIEW.md

I-001 scaffold P-001 approve (`rev-p001-20260714`) lived at this path historically; this file is the **I-002** RP-03 **request-changes** decision.

**Superseded for MAJOR gate:** see `harness/evidence/reviewer/P-001/REVIEW-RECHECK.md` (`rev-p001-recheck-20260715`, decision **approve**).
