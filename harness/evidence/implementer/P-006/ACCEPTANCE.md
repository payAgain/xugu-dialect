# P-006 Acceptance Evidence

> Phase: P-006  
> Initiative: I-001  
> Build: B-006  
> Result: PASS  
> Role: orchestrator (Accept)

## Approved scope

- Build: B-006 (P-006 only) — harness/builds/B-006.json
- Matrix SSOT: contracts/feature-matrix-definition-a.md (A-FUN-001..014, 016..018; A-FUN-015 deferred)
- Human Gate approval: B-006 scope P-006 only

## Role pipeline

| Step | Role | Status | Invocation | Independent | Evidence |
|---|---|---|---|---|---|
| RP-01 | implementer | passed | impl-p006-20260715 | N/A | harness/evidence/implementer/P-006/ |
| RP-02 | test | passed | test-p006-20260715 | true | harness/evidence/test/P-006/TEST-REPORT.md |
| RP-03 | reviewer | passed | rev-p006-20260715 | true | harness/evidence/reviewer/P-006/REVIEW.md |

## Locked / chosen function forms

| Capability | Locked form |
|---|---|
| UUID primary (A-FUN-016) | uuid() (alternates: gen_random_uuid(), sys_guid()) |
| JSON subset (A-FUN-017) | json_value + json_extract only (no json_set / MySQL dump) |
| listagg (A-FUN-018) | LISTAGG(...) WITHIN GROUP (ORDER BY ...) |
| A-FUN-015 bit_and/bit_or | **not registered** (matrix 延后) |

JSON HQL note: apps need hibernate.query.hql.json_functions_enabled=true.

Reviewer MINOR (optional, non-blocking): P-010 user-guide callout for JSON preview flag.

## Command verification

- Phase verification evidence: harness/evidence/test/P-006/verification.json
- Also: harness/evidence/implementer/P-006/verification.json (RP-01)
- Overall status: **VERIFY PASS**
- Required check IDs covered: uild, 	est

## Observed affected flows

| Flow | Method | Result | Evidence |
|---|---|---|---|
| function-registry-hql-sql-real-db | XuguFunctionRegistryIT.functionFamilies_HqlAndNative_A_FUN (+ negative IT) gate ON | PASS | harness/evidence/test/P-006/mvn-test-integration.log |

## Real DB IT

- Gate: -Dxugu.run.integration=true
- Independent test: 13 IT executed / 0 failed / 0 skipped on live XuguDB (compatiblemode=NONE)
- Offline: 13 IT skipped; unit green (28)

## Residual risks

- MINOR: JSON HQL preview flag docs polish (optional → P-010)
- Deferred: A-FUN-015 / 019 / 020 / 021; P-007+ schema / SPI / demo

## Version control checkpoint

- Branch: eat/i-001-xugu-dialect-major
- Candidate commit: a96f31079e359f5e369d4f4a0c11f3f1ed6e5950
- Deferred reason when no commit: N/A (must-commit on Accept)

## Acceptance decision

- Decision: ccepted
- Decided by: orchestrator
- Date: 2026-07-15
- Blocker reference when not accepted: N/A
- Reviewer decision: pprove (
ev-p006-20260715)
- Pipeline: RP-01..RP-03 all passed
- Readiness: function-registry scope PASS with VERIFY PASS + real-DB IT + uuid/json/listagg forms + A-FUN-015 deferred
