# I-008 / P-005 Acceptance Evidence (implementer RP-01)

> Phase: `P-005`  
> Initiative: `I-008`  
> Build: `B-001`  
> Role step: `RP-01` / implementer  
> invocation_id: `inv-i008-p005-rp01-implementer`  
> Branch: `feat/i-008-production-quality-gaps`  
> Result: **RP-01 artifacts complete** — Q2 lock semantics behavioral + negative evidence

## Approved scope

- Task: `harness/tasks/P-005.md`
- Dependency: P-002 lock integration docs (`docs/user-guide/07-lock-integration.md`)
- Allowed: `dialect/**` lock IT/断言, `docs/**` minor alignment
- Forbidden respected: no invented SKIP LOCKED/FOR SHARE, no MySQL/Oracle inheritance, no commit

## Q2 — Lock semantics evidence

### Deliverables

| Artifact | Purpose |
|---|---|
| `dialect/.../XuguLockSemanticsTest` | **New** — P-005 offline behavioral + negative suite (24 tests) |
| `dialect/.../it/XuguLockIT#pessimisticReadExecutesAsForUpdateNotShare` | **New** gated live anchor (A-LCK-005) |
| `docs/user-guide/07-lock-integration.md` | Cross-links to P-005 test anchors |

### Negative assertions (forbidden SQL absent)

| Matrix | Assertion | Test anchor |
|---|---|---|
| A-LCK-004 / C-SKIP-001 | `supportsSkipLocked=false`; no `SKIP LOCKED` in any lock fragment | `XuguLockSemanticsTest#generatedLockSql_neverContainsSkipLockedOrForShare` (17 parameterized cases); `#skipLockedLockMode_stillEmitsPlainForUpdate_A_LCK_004`; `#lockingSupport_declaresSkipLockedFalse_A_LCK_004` |
| A-LCK-005 | No `FOR SHARE` / `FOR KEY SHARE` in read-lock paths | Same parameterized scan + `#pessimisticReadMatchesWriteLockShape_A_LCK_005` |

Complementary existing offline coverage (unchanged, still green):

- `XuguPaginationLockTest#skipLockedNotSupported_A_LCK_004`, `#noForShare_A_LCK_005`
- `XuguNegativeRegressionBaselineTest#skipLockedNotSupported_A_LCK_004_C_SKIP_001`, `#forShareNotSupported_A_LCK_005`

### Behavioral assertions (PESSIMISTIC_READ → exclusive FOR UPDATE)

| Requirement | Test anchor |
|---|---|
| `getReadLockString` same shape as `getWriteLockString` (WAIT / NOWAIT / OF / ms) | `XuguLockSemanticsTest#pessimisticReadMatchesWriteLockShape_A_LCK_005` |
| `LockOptions PESSIMISTIC_READ` == `PESSIMISTIC_WRITE` SQL fragment | `#pessimisticReadLockOptions_sameSqlAsWrite_A_LCK_005` |
| Read lock + pagination preserves XuGu `FOR UPDATE … LIMIT … WAIT` order | `#limitHandlerWithReadLockFragment_preservesXuGuOrder_A_PAG_001` |
| Live DB executes read-lock SQL as `FOR UPDATE` | `XuguLockIT#pessimisticReadExecutesAsForUpdateNotShare` (gated; skipped offline) |

### Doc cross-link (P-002 ↔ P-005)

- User guide § PESSIMISTIC_READ → 排他 FOR UPDATE now references `XuguLockSemanticsTest` + `XuguLockIT#pessimisticReadExecutesAsForUpdateNotShare`
- Cross-ref table adds P-005 offline + live anchor rows

## Validation (implementer)

| Command | Result | Notes |
|---|---|---|
| `mvn -q -DskipTests package` | **PASS** | exit 0 |
| `mvn -q test` (offline) | **PASS** | exit 0 (~10.7s) |

### Lock-related test results (offline run)

| Test class | Tests | Failures | Errors | Skipped | Result |
|---|---:|---:|---:|---:|---|
| `com.xugu.dialect.XuguLockSemanticsTest` | 24 | 0 | 0 | 0 | **PASS** |
| `com.xugu.dialect.XuguPaginationLockTest` | 10 | 0 | 0 | 0 | **PASS** |
| `com.xugu.dialect.it.XuguLockIT` | 3 | 0 | 0 | 3 | **PASS** (gated skip offline) |

Surefire: `dialect/target/surefire-reports/com.xugu.dialect.XuguLockSemanticsTest.txt`

## Files changed

- `dialect/src/test/java/com/xugu/dialect/XuguLockSemanticsTest.java` — **new**
- `dialect/src/test/java/com/xugu/dialect/it/XuguLockIT.java` — + `pessimisticReadExecutesAsForUpdateNotShare`
- `docs/user-guide/07-lock-integration.md` — P-005 test cross-links

## Acceptance criteria (RP-01)

| Criterion | Result | Evidence |
|---|---|---|
| Negative: forbidden SKIP LOCKED / FOR SHARE absent in generated lock SQL | **PASS** | § Negative assertions |
| Behavioral: PESSIMISTIC_READ maps to exclusive FOR UPDATE shape | **PASS** | § Behavioral assertions |
| Cross-check P-002 lock section | **PASS** | `07-lock-integration.md` cross-links |
| Offline `mvn -q test` green | **PASS** | § Validation |
| No forbidden paths / no commit | **PASS** | by instruction |

## Role pipeline

| Step | Role | Status | Evidence |
|---|---|---|---|
| RP-01 | implementer | **complete** | this file (`inv-i008-p005-rp01-implementer`) |
| RP-02 | test | **passed** | `harness/evidence/test/I-008/P-005/verification.json` (`inv-i008-p005-rp02-test`) |
| RP-03 | reviewer | **passed** | `harness/evidence/reviewer/I-008/P-005/REVIEW.md` (`inv-i008-p005-rp03-reviewer`) |

## Acceptance decision

- Decision: `accepted`
- Live lock IT: SKIPPED_INFRA; offline XuguLockSemanticsTest 24/24 PASS

## Handoff

- `harness/handoffs/implementer/I-008-P-005.yaml`
