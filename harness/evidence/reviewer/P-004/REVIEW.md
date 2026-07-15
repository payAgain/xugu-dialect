# P-004 Reviewer Evidence (Full + risk>=8)

> Phase: `P-004`  
> Initiative: `I-001`  
> Build: `B-004`  
> Invocation: `rev-p004-20260715`  
> Role: `reviewer` (readonly; landed by orchestrator/coordinator)  
> Decision: **request-changes**  
> Date: 2026-07-15

## Scope reviewed

- Phase packet: `harness/tasks/P-004.md` (risk_score=8, RP-03)
- Build: `harness/builds/B-004.json` (approved; `approved_phase_ids=[P-004]`)
- Matrix: `contracts/feature-matrix-definition-a.md` (A-PAG-*, A-LCK-*)
- Main: `dialect/src/main/java/com/xugu/dialect/XuguDialect.java`
- LimitHandler: `dialect/src/main/java/com/xugu/dialect/pagination/XuguLimitHandler.java`
- LockingSupport: `dialect/src/main/java/com/xugu/dialect/internal/XuguLockingSupport.java`
- Tests: `XuguPaginationLockTest`, `XuguPaginationIT`, `XuguLockIT`
- Implementer evidence: `harness/evidence/implementer/P-004/`
- Test evidence: `harness/evidence/test/P-004/` (TEST-REPORT, verification.json, IT logs)
- XuGu docs (spot-check): `reference/sql/select/resultset-restricted.md`, `reference/sql/select/select.md`

## Checklist

| Item | Result | Notes |
|---|---|---|
| Approved Build scope respected | PASS | B-004 = P-004 only; no demo / later-Phase expansion |
| Forbidden inheritance | PASS | `XuguDialect extends Dialect` only; no MySQL/Oracle |
| Pagination form vs matrix | PASS | Stable `LIMIT count OFFSET offset` with bind markers; no FETCH FIRST |
| Lock fragments FOR UPDATE / OF / NOWAIT / WAIT | PASS | Unit + real-DB IT smoke; ms pass-through |
| SKIP LOCKED not invented | PASS | `supportsSkipLocked=false`; no keyword |
| FOR SHARE not emitted | PASS (doc gap) | `getReadLockString` → FOR UPDATE; **semantics under-documented** (MAJOR 1) |
| LIMIT + FOR UPDATE combo on live DB | FAIL | Unit asserts `limit ? for update` order; **no gated IT proving live execute** (MAJOR 2) |
| Independent test RP-02 | PASS | `test-p004-20260715`; VERIFY PASS; 8/8 IT |
| Scope creep | PASS | No identity/sequence/functions/SPI |

## Findings

### BLOCKER
- None

### MAJOR
1. **A-LCK-005 limitations under-documented (share-lock shim risk)**  
   - Implementation correctly avoids `FOR SHARE` and maps pessimistic read → `FOR UPDATE`.  
   - Current javadoc / NOTES / matrix hint say “no FOR SHARE; read→FOR UPDATE” but do **not** clearly warn that this is a **Hibernate compatibility shim only**, **not** share-lock support.  
   - Apps may assume `PESSIMISTIC_READ` ≈ `FOR SHARE` (concurrent readers OK). Under this dialect, read locks use **exclusive** `FOR UPDATE` semantics — concurrent readers **may block**.  
   - Matrix status must remain **文档不允许** for the FOR SHARE surface.  
   - **Required before approve:** update `XuguDialect` javadoc + implementer NOTES + matrix acceptance hint stating: Hibernate shim only; NOT share-lock; `PESSIMISTIC_READ` = exclusive FOR UPDATE; concurrent readers may block; do not assume FOR SHARE; matrix stays 文档不允许.

2. **LIMIT + FOR UPDATE order unproven on real XuguDB**  
   - Unit test `limitInsertsBeforeForUpdate` expects Hibernate/LimitHandler order `… limit ? for update`.  
   - Separate ITs cover LIMIT and FOR UPDATE alone; **no gated IT** executes the combined clause (ideally also with WAIT ms) under `compatiblemode=NONE`.  
   - **Required before approve:** add gated IT that runs dialect-assembled (or Hibernate-generated) SQL combining LIMIT + FOR UPDATE (+ WAIT if feasible); record observed SQL and success/fail in NOTES. If DB rejects the combo, document as unsupported and adjust strategy; prefer prove it works.

### MINOR
1. Parenthesized NOWAIT/WAIT fallback path is exercised only when inline fails — acceptable, but combined LIMIT+lock parenthesized form not covered.
2. `ACCEPTANCE.md` still draft / awaiting pipeline close — expected under request-changes.

### QUESTION
1. Does live Xugu accept `SELECT … LIMIT n FOR UPDATE [WAIT ms]` (LimitHandler order) under `compatiblemode=NONE`? (Drives MAJOR #2 disposition.)

## Validation status

- Independent test role: **PASS** (`test-p004-20260715`)
- Project verify: `harness/evidence/test/P-004/verification.json` → `PASS` (build + test required)
- Real DB IT: 8 executed / 0 failed (gate ON) for pagination + lock **separately**
- Combined LIMIT+FOR UPDATE live execute: **not evidenced** → MAJOR 2

## Recommendation

**request-changes** — pagination + basic lock SQL are solid and independently tested; **do not Accept** until A-LCK-005 limitation wording is explicit and LIMIT+FOR UPDATE (ideally + WAIT) is proven or documented unsupported on live Xugu.

## Decision

- Decision: `request-changes`
- Invocation: `rev-p004-20260715`
- Decided by: reviewer (readonly; evidence written by orchestrator/coordinator)
- Date: 2026-07-15

## Handoff payload (for orchestrator)

```yaml
role: reviewer
phase_id: P-004
build_id: B-004
invocation_id: rev-p004-20260715
step_id: RP-03
status: request-changes
decision: request-changes
required: true
evidence: harness/evidence/reviewer/P-004/REVIEW.md
next: implementer fix MAJOR A-LCK-005 docs + LIMIT|FOR UPDATE IT → re-test → re-review RP-03
```

---

## Superseded for MAJOR gate

Prior decision 
equest-changes (
ev-p004-20260715) is **superseded** for Accept by recheck **approve** (
ev-p004-recheck-20260715).
See harness/evidence/reviewer/P-004/REVIEW-RECHECK.md (MAJOR 1 A-LCK-005 docs CLOSED; MAJOR 2 LIMIT+FOR UPDATE live combo CLOSED — FOR UPDATE before LIMIT).

