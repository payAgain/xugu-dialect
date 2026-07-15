# P-005 Reviewer Evidence (Full + risk>=8)

> Phase: `P-005`  
> Initiative: `I-001`  
> Build: `B-005`  
> Invocation: `rev-p005-20260715`  
> Role: `reviewer` (readonly; landed by orchestrator)  
> Decision: **approve**  
> Date: 2026-07-15

## Scope reviewed

- Phase packet: `harness/tasks/P-005.md` (risk_score=8, RP-03)
- Build: `harness/builds/B-005.json` (approved; `approved_phase_ids=[P-005]`)
- Matrix: `contracts/feature-matrix-definition-a.md` (A-IDN-001..004, A-SEQ-001..005, A-XCUT-008)
- Main: `dialect/src/main/java/com/xugu/dialect/XuguDialect.java`
- Identity: `dialect/src/main/java/com/xugu/dialect/identity/XuguIdentityColumnSupport.java`
- Sequence: `dialect/src/main/java/com/xugu/dialect/sequence/XuguSequenceSupport.java`
- Tests: `XuguIdentitySequenceTest`, `XuguIdentitySequenceIT`
- Implementer evidence: `harness/evidence/implementer/P-005/` (NOTES, CHECKLIST, IT-RESULT, verification.json)
- Test evidence: `harness/evidence/test/P-005/` (TEST-REPORT, verification.json, IT logs)
- XuGu docs (spot-check): `reference/object/table/create.md`, `reference/object/sequence.md`, `reference/function/sequence-functions/nextval.md`, `reference/function/system-infos-functions/last_insert_id.md`, JDBC getGeneratedKeys

## Checklist

| Item | Result | Notes |
|---|---|---|
| Approved Build scope respected | PASS | B-005 = P-005 only; no functions/SPI/demo |
| Forbidden inheritance | PASS | `XuguDialect extends Dialect` only; no MySQL/Oracle |
| IDENTITY DDL `identity(1,1)` | PASS | Unit + live SHOW_SQL; no `auto_increment` emitted |
| Identity insert omit id + backfill | PASS | Live IT `identityPersistBackfillsId_*` |
| Generated keys primary | PASS | JDBC `getGeneratedKeys` / `USE_GET_GENERATED_KEYS=true`; dialect default true |
| Identity select fallback | PASS | `select last_insert_id() from dual` unit + live smoke |
| NEXTVAL locked form | PASS | `select <seq>.nextval from dual`; not `NEXTVAL('seq')` |
| CURRVAL form | PASS | `currval('name')`; not `seq.currval` |
| CREATE/DROP SEQUENCE | PASS | Hibernate SequenceSupport defaults; live IT |
| FROM DUAL (A-XCUT-008) | PASS | `getFromDual()` → ` from dual` |
| Independent test RP-02 | PASS | `test-p005-20260715`; VERIFY PASS; 11/11 IT |
| Scope creep | PASS | No SQL functions (P-006) / Schema / SPI |

## Findings

### BLOCKER
- None

### MAJOR
- None

### MINOR (optional; do not block approve)
1. **last_insert_id.md cite polish** — Identity fallback correctly uses documented `LAST_INSERT_ID()`; javadoc/NOTES could more explicitly point at `reference/function/system-infos-functions/last_insert_id.md` alongside create.md / JDBC docs (non-blocking documentation polish).
2. Deferred matrix rows A-IDN-005 / A-SEQ-006 remain out of scope (expected).

### QUESTION
- None blocking Accept.

## Validation status

- Independent test role: **PASS** (`test-p005-20260715`)
- Project verify: `harness/evidence/test/P-005/verification.json` — **PASS** (build + test required)
- Real DB IT: 11 executed / 0 failed / 0 skipped (gate ON); P-005 focused 2/2 PASS
- Observed flows: `identity-insert-real-db`, `sequence-generator-real-db` — both PASS on live XuguDB

## Recommendation

**approve** — locked IDENTITY/SEQUENCE SQL forms match docs + live probes; getGeneratedKeys primary with LAST_INSERT_ID fallback; independent test green. Orchestrator may Accept + must-commit (Human Gate still owns Ship / B-006).

## Decision

- Decision: `approve`
- Invocation: `rev-p005-20260715`
- Decided by: reviewer (readonly; evidence landed by orchestrator)
- Date: 2026-07-15

## Handoff payload (for orchestrator)

```yaml
role: reviewer
phase_id: P-005
build_id: B-005
invocation_id: rev-p005-20260715
step_id: RP-03
status: passed
decision: approve
required: true
evidence: harness/evidence/reviewer/P-005/REVIEW.md
locked_sql_forms:
  identity_ddl: identity(1,1)
  identity_keys_primary: JDBC getGeneratedKeys
  identity_select_fallback: select last_insert_id() from dual
  nextval: select <seq>.nextval from dual
  currval: select currval('<name>') from dual
minors_deferred: true
next: orchestrator Accept + must-commit (propose B-006 → P-006 only; no Ship without Human Gate)
```
