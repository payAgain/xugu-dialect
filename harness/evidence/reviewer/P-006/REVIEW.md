# P-006 Reviewer Evidence (Full + risk>=8)

> Phase: P-006  
> Initiative: I-001  
> Build: B-006  
> Invocation: 
ev-p006-20260715  
> Role: 
eviewer (readonly; landed by orchestrator)  
> Decision: **approve**  
> Date: 2026-07-15

## Scope reviewed

- Phase packet: harness/tasks/P-006.md (risk_score=8, RP-03)
- Build: harness/builds/B-006.json (approved; pproved_phase_ids=[P-006])
- Matrix: contracts/feature-matrix-definition-a.md (A-FUN-001..014, 016..018; A-FUN-015 deferred)
- Main: dialect/src/main/java/com/xugu/dialect/XuguDialect.java
- Functions: dialect/src/main/java/com/xugu/dialect/function/XuguFunctionRegistrations.java
- Tests: XuguFunctionRegistryTest, XuguFunctionRegistryIT (+ OfflineConnectionProvider, P006FunEntity)
- Implementer evidence: harness/evidence/implementer/P-006/ (NOTES, CHECKLIST, IT-RESULT, verification.json)
- Test evidence: harness/evidence/test/P-006/ (TEST-REPORT, verification.json, IT logs)
- XuGu docs (spot-check): uuid / json_value / json_extract / listagg families under 
eference/function/

## Checklist

| Item | Result | Notes |
|---|---|---|
| Approved Build scope respected | PASS | B-006 = P-006 only; no schema/SPI/demo |
| Forbidden inheritance | PASS | XuguDialect extends Dialect only; no MySQL/Oracle |
| A-FUN matrix 可实现 coverage | PASS | 001..014, 016..018 implemented; 015 deferred not registered |
| UUID primary uuid() | PASS | Unit + live SHOW_SQL select uuid(); alternates registered |
| JSON subset only | PASS | json_value + json_extract; json_set absent |
| listagg WITHIN GROUP | PASS | Live listagg(...) within group (order by ...) |
| A-FUN-015 not registered | PASS | it_and descriptor null (延后) |
| Negative unregistered FN | PASS | Diagnosable SQLException on live DB |
| Independent test RP-02 | PASS | 	est-p006-20260715; VERIFY PASS; 13/13 IT |
| Scope creep | PASS | No Schema/temp/FK (P-007) / SPI / demo |

## Findings

### BLOCKER
- None

### MAJOR
- None

### MINOR (optional; do not block approve)
1. JSON HQL requires hibernate.query.hql.json_functions_enabled=true — already documented in NOTES/IT; optional user-guide callout can wait for P-010.
2. Deferred matrix rows A-FUN-015 / 019 / 020 / 021 remain out of scope (expected).

### QUESTION
- None blocking Accept.

## Validation status

- Independent test role: **PASS** (	est-p006-20260715)
- Project verify: harness/evidence/test/P-006/verification.json — **PASS** (build + test required)
- Real DB IT: 13 executed / 0 failed / 0 skipped (gate ON); P-006 focused XuguFunctionRegistryIT 2/2 PASS
- Observed flow: unction-registry-hql-sql-real-db — PASS on live XuguDB

## Recommendation

**approve** — function registry matches matrix 可实现 rows; UUID=uuid(), JSON subset, listagg WITHIN GROUP confirmed on live DB; A-FUN-015 correctly deferred. Orchestrator may Accept + must-commit (Human Gate still owns Ship / B-007).

## Decision

- Decision: pprove
- Invocation: 
ev-p006-20260715
- Decided by: reviewer (readonly; evidence landed by orchestrator)
- Date: 2026-07-15

## Handoff payload (for orchestrator)

`yaml
role: reviewer
phase_id: P-006
build_id: B-006
invocation_id: rev-p006-20260715
step_id: RP-03
status: passed
decision: approve
required: true
evidence: harness/evidence/reviewer/P-006/REVIEW.md
locked_forms:
  uuid_primary: uuid()
  json_subset: [json_value, json_extract]
  listagg_sql: LISTAGG(...) WITHIN GROUP (ORDER BY ...)
  a_fun_015: deferred_not_registered
minors_deferred: true
next: orchestrator Accept + must-commit (propose B-007 → P-007 only; no Ship without Human Gate)
`
