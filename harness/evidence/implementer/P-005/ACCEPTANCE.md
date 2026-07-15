# P-005 Acceptance Evidence

> Phase: `P-005`  
> Initiative: `I-001`  
> Build: `B-005`  
> Result: `PASS`  
> Role: orchestrator (Accept)

## Approved scope

- Build: B-005 (P-005 only) — `harness/builds/B-005.json`
- Matrix SSOT: `contracts/feature-matrix-definition-a.md` (A-IDN-001..004, A-SEQ-001..005, A-XCUT-008)
- Human Gate approval: B-005 scope P-005 only

## Role pipeline

| Step | Role | Status | Invocation | Independent | Evidence |
|---|---|---|---|---|---|
| RP-01 | implementer | passed | impl-p005-20260715 | N/A | `harness/evidence/implementer/P-005/` |
| RP-02 | test | passed | test-p005-20260715 | true | `harness/evidence/test/P-005/TEST-REPORT.md` |
| RP-03 | reviewer | passed | rev-p005-20260715 | true | `harness/evidence/reviewer/P-005/REVIEW.md` |

## Locked SQL forms + identity retrieval

| Capability | Locked form |
|---|---|
| IDENTITY DDL | `identity(1,1)` after type (no `auto_increment` under NONE) |
| Identity insert | INSERT omitting id column; id backfilled |
| Generated keys (**primary**) | JDBC `Statement.RETURN_GENERATED_KEYS` / `getGeneratedKeys()` |
| Identity select fallback | `select last_insert_id() from dual` |
| NEXTVAL | `select <seq>.nextval from dual` |
| CURRVAL | `select currval('<name>') from dual` |
| FROM DUAL | ` from dual` |
| CREATE SEQUENCE | `create sequence … start with N increment by M` |
| DROP SEQUENCE | `drop sequence <name>` |

Rejected / not emitted: `NEXTVAL('seq')`, `seq.currval`, MySQL `AUTO_INCREMENT` keyword.

Reviewer MINOR (optional, non-blocking): polish cite to `reference/function/system-infos-functions/last_insert_id.md` in javadoc/NOTES.

## Command verification

- Phase verification evidence: `harness/evidence/test/P-005/verification.json`
- Also: `harness/evidence/implementer/P-005/verification.json` (RP-01)
- Overall status: **VERIFY PASS**
- Required check IDs covered: `build`, `test`

## Observed affected flows

| Flow | Method | Result | Evidence |
|---|---|---|---|
| identity-insert-real-db | `XuguIdentitySequenceIT.identityPersistBackfillsId_A_IDN_003_004` gate ON | PASS | `harness/evidence/test/P-005/mvn-test-integration.log` |
| sequence-generator-real-db | `XuguIdentitySequenceIT.sequenceGeneratorPersist_A_SEQ_003_004_008` gate ON | PASS | same |

## Real DB IT

- Gate: `-Dxugu.run.integration=true`
- Independent test: 11 IT executed / 0 failed / 0 skipped on live XuguDB (`compatiblemode=NONE`)
- Offline: 11 IT skipped; unit green (23)

## Residual risks

- MINOR: last_insert_id.md cite polish (optional)
- Deferred: A-IDN-005 / A-SEQ-006; P-006+ functions / schema / SPI / demo

## Version control checkpoint

- Branch: `feat/i-001-xugu-dialect-major`
- Candidate commit: `PENDING_MUST_COMMIT`
- Deferred reason when no commit: N/A (must-commit on Accept)

## Acceptance decision

- Decision: `accepted`
- Decided by: `orchestrator`
- Date: 2026-07-15
- Blocker reference when not accepted: N/A
- Reviewer decision: `approve` (`rev-p005-20260715`)
- Pipeline: RP-01..RP-03 all `passed`
- Readiness: identity/sequence scope PASS with VERIFY PASS + real-DB IT + locked SQL forms + getGeneratedKeys primary
