# P-005 ACCEPTANCE — Track B (implementer RP-01)

> **Invocation:** `inv-i007-p005-rp01-implementer`  

## Decision

- Decision: `accepted`
- Decided by: orchestrator
- Date: 2026-07-19T15:40:00+08:00
- Reviewer: `approve_with_nits` (`inv-i007-p005-rp03-reviewer`)
- Phase verification: `harness/evidence/test/I-007/P-005/verification.json`

## Criteria (implementer deposit)

| Criterion | Evidence | Status |
|---|---|---|
| Flyway path reproducible | [`NOTES.md`](NOTES.md); `DemoFlywayIT`; offline migration classpath smoke | **deposit** |
| Demo bulk delete | `DemoBulkMutationIT#bulkDeletePersonNames` | **deposit** |
| Function/HQL smoke deepen | `DemoFunctionsIT#hqlFunctionSubsetSmoke` extensions | **deposit** |
| Read-only tx smoke | `DemoReadOnlyTxIT#readOnlyTransactionQueriesPersistedRow` | **deposit** |
| No multi-datasource | Single datasource only | **deposit** |
| Boot SSOT 41 rows unchanged | Non-matrix Track B table only | **deposit** |
| Offline `mvn -q test` green | implementer run | **pending RP-02** |
| Live demo when DB available | `harness/evidence/test/I-007/P-005/` | **pending RP-02** |
| No commit / no Ship | By instruction | **PASS** |

## Track B rollup

| gap_id | implementer status |
|---|---|
| B-FLY-001 | implemented |
| B-DEMO-001 | implemented |
| B-DEMO-002 | implemented |
| B-DEMO-003 | implemented |
