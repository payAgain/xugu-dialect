# I-008 / P-005 — Reviewer Audit (RP-03)

> **Role:** reviewer  
> **invocation_id:** `inv-i008-p005-rp03-reviewer`  
> **Date:** 2026-07-20  
> **Branch:** `feat/i-008-production-quality-gaps`  
> **Audit HEAD (RP-02):** `54eaf6e` (+ uncommitted P-005 working tree)  
> **Mode:** readonly

## Verdict

**ACCEPT PASS**

## Review Result

```text
Review Result

BLOCKER: (none)
MAJOR: (none)
MINOR:
  - F-03: Live anchor `pessimisticReadExecutesAsForUpdateNotShare` checks substring `share` only; offline suite also forbids `for key share` — align live assertion with `assertNoForbiddenLockKeywords` shape when DB available.
  - F-04: Parameterized scan omits `getForUpdateString(Timeout)` direct entry (covered indirectly via `getWriteLockString` / `appendWait` paths).
  - F-05: Duplicate adjacent Javadoc blocks above `pessimisticReadExecutesAsForUpdateNotShare` in `XuguLockIT` (cosmetic).
QUESTION: (none)
Validation Status: offline build + lock-focused + full reactor PASS (`verification.json` VERIFY PASS); live `XuguLockIT` SKIPPED_INFRA (127.0.0.1:5138 unreachable).
Recommended Fixes: commit P-005 artifacts; re-run gated live IT when XuGuDB up (optional F-03 alignment).
```

## Criteria checklist

| # | Criterion (P-005) | Result | Evidence |
|---|---|---|---|
| 1 | Lock suite negative assertions: no `SKIP LOCKED` / `FOR SHARE` / `FOR KEY SHARE` in generated lock SQL | **PASS** | `XuguLockSemanticsTest#generatedLockSql_neverContainsSkipLockedOrForShare` (17 parameterized fragments); `#lockingSupport_declaresSkipLockedFalse_A_LCK_004`; `#skipLockedLockMode_stillEmitsPlainForUpdate_A_LCK_004`; complementary `XuguPaginationLockTest`, `XuguNegativeRegressionBaselineTest` |
| 2 | Behavioral: `PESSIMISTIC_READ` → exclusive `FOR UPDATE` (same shape as write) | **PASS** | `#pessimisticReadMatchesWriteLockShape_A_LCK_005`; `#pessimisticReadLockOptions_sameSqlAsWrite_A_LCK_005`; `#limitHandlerWithReadLockFragment_preservesXuGuOrder_A_PAG_001`; production `XuguDialect#getReadLockString` delegates to `getWriteLockString` (P-002, unchanged) |
| 3 | No invented forbidden SQL capabilities | **PASS** | `supportsSkipLocked=false` via `XuguLockingSupport`; `getForUpdateSkipLockedString` / `appendWait(SKIP_LOCKED)` emit plain ` for update`; no `FOR SHARE` emission path |
| 4 | P-002 lock section cross-check (matrix / user guide / contract) | **PASS** | `docs/user-guide/07-lock-integration.md` references P-005 evidence, `XuguLockSemanticsTest`, `XuguLockIT#pessimisticReadExecutesAsForUpdateNotShare`; A-LCK-004/005 / C-SKIP-001 wording consistent with tests |
| 5 | Offline `mvn -q test` green | **PASS** | `harness/evidence/test/I-008/P-005/verification.json` — build + lock-focused + full reactor exit 0 |
| 6 | Live lock IT (optional) honestly gated | **PASS** (infra skip) | `XUGU_RUN_IT=true` → Connection refused; `IT-RESULT.txt` status `SKIPPED_INFRA`; offline gate skips 3/3 `XuguLockIT` methods with 0 fail |

## Artifact audit — `XuguLockSemanticsTest` (new, 24 tests)

| Anchor | Intent | Audit |
|---|---|---|
| `generatedLockSql_neverContainsSkipLockedOrForShare` | Parameterized forbidden-keyword scan across write/read/`LockOptions`/`UPGRADE_SKIPLOCKED` fragments | **Sound** — central Q2 negative evidence; covers alias, NOWAIT, WAIT ms, OF-column, skip-lock mode |
| `lockingSupport_declaresSkipLockedFalse_A_LCK_004` | `XuguLockingSupport.INSTANCE` + `supportsSkipLocked()` false | **Sound** — ties dialect flag to P-002 matrix |
| `skipLockedLockMode_stillEmitsPlainForUpdate_A_LCK_004` | `UPGRADE_SKIPLOCKED` / `getForUpdateSkipLockedString` still plain FOR UPDATE | **Sound** — proves no keyword invention on forced skip-lock paths |
| `pessimisticReadMatchesWriteLockShape_A_LCK_005` | Read/write lock string parity (WAIT / NOWAIT / OF / ms) | **Sound** — direct behavioral proof of A-LCK-005 shim |
| `pessimisticReadLockOptions_sameSqlAsWrite_A_LCK_005` | `LockOptions PESSIMISTIC_READ` == `PESSIMISTIC_WRITE` fragment | **Sound** — matches `getForUpdateString(aliases, lockOptions)` switch |
| `limitHandlerWithReadLockFragment_preservesXuGuOrder_A_PAG_001` | `FOR UPDATE … LIMIT … WAIT` order with read lock | **Sound** — pagination + read lock combo |
| `p005LockSemanticsChecklist_coversQ2Evidence` | Meta checklist invoking core anchors | **Acceptable** — redundant with individual `@Test`s but documents Q2 bundle |

**Scope drift:** none — tests assert dialect SQL fragments only; no production dialect edits; no MySQL/Oracle inheritance.

## Artifact audit — `XuguLockIT` changes

| Change | Audit |
|---|---|
| Class Javadoc + `@see` cross-links to P-002 guide and offline suite | **PASS** — doc traceability |
| New `pessimisticReadExecutesAsForUpdateNotShare` | **PASS** — gated (`XuguITGate`); pre-execution asserts read fragment contains `for update` and excludes `share`; executes `SELECT … + getReadLockString(WAIT_FOREVER)` on live JDBC; same table lifecycle as sibling ITs |
| Existing `forUpdateExecutesAndSkipLockedUnsupported` | **Unchanged logic** — still asserts A-LCK-004 at live layer |

Live anchor does **not** re-prove SKIP LOCKED absence (already covered offline + in sibling IT method); appropriate separation.

## Production cross-check (P-002 baseline, not modified in P-005)

| Surface | Observed behavior | Matches tests |
|---|---|---|
| `getReadLockString(*)` | Delegates to `getWriteLockString(*)` | Yes |
| `getForUpdateSkipLockedString(*)` | Plain ` for update` / ` of alias` | Yes |
| `appendWait(SKIP_LOCKED)` | Returns plain FOR UPDATE (no keyword) | Yes |
| `getForUpdateString(alias, LockOptions)` | `PESSIMISTIC_READ` → same `appendWait` path as `PESSIMISTIC_WRITE` | Yes |
| `XuguLockingSupport` | `skipLocked=false` | Yes |

No evidence of invented `SKIP LOCKED` or `FOR SHARE` in production paths reviewed.

## Doc cross-links (P-002 ↔ P-005)

| Doc anchor | Test / evidence anchor | Consistent |
|---|---|---|
| § PESSIMISTIC_READ → 排他 FOR UPDATE | `pessimisticReadMatchesWriteLockShape_A_LCK_005` | Yes |
| A-LCK-004 / C-SKIP-001 不发出 SKIP LOCKED | Parameterized scan + `supportsSkipLocked=false` | Yes |
| A-LCK-005 文档不允许 FOR SHARE | Read→FOR UPDATE shim + forbidden keyword scan | Yes |
| Cross-ref table P-005 rows | `harness/evidence/test/I-008/P-005/` | Yes |

## Validation summary

| Command | Exit | Status | Log |
|---|---:|---|---|
| `mvn -q -DskipTests package` | 0 | PASS | `mvn-package-offline.txt` |
| Lock-focused suite (4 classes) | 0 | PASS | `surefire-summary-lock-focused.txt` |
| `mvn -q test` (offline) | 0 | PASS | `mvn-test-offline.txt` |
| `XUGU_RUN_IT=true … -Dtest=XuguLockIT` | 1 | SKIPPED_INFRA | `IT-RESULT.txt`, `live-db-probe.txt` |
| `verify.py --phase P-005` | 0 | VERIFY PASS | `verification-verifypy.json` |

Lock-focused counts: `XuguLockSemanticsTest` 24/0/0/0; `XuguLockIT` 3/0/0/3 (gated offline).

## Findings (non-blocking)

- **F-01:** Live PASS for `pessimisticReadExecutesAsForUpdateNotShare` not observed (DB down). Offline behavioral + negative evidence is sufficient for P-005 Accept; re-run live IT before Ship-level live claims.
- **F-02:** P-005 code + evidence artifacts are **uncommitted** at audit time (`XuguLockSemanticsTest.java` untracked; `XuguLockIT.java`, `07-lock-integration.md` modified). Accept is on artifact quality; orchestrator must commit before Build Accept gate.
- **F-03–F-05:** See Review Result MINOR items.

## Decision

`accepted` — Q2 lock semantics closed offline: forbidden SQL absent in generated fragments, `PESSIMISTIC_READ` maps to exclusive `FOR UPDATE` per P-002 docs, cross-links honest, verification green; live IT infra skip documented and non-blocking for this Phase.
