# I-005 / P-003 Test Acceptance (test role RP-01)

> Phase: `P-003`  
> Initiative: `I-005`  
> Build: `B-001`  
> Invocation: `test-i005-p003-rp01-20260717`  
> Step: `RP-01`  
> Role: `test`  
> Verdict: **PASS — offline negative bundle green; no implementer fixes required**  
> Date: 2026-07-17

## Scope delivered

Added consolidated negative-only regression baseline for all SSOT rows marked **文档不允许** or **延后**:

| Bucket | Count | Evidence |
|---|---:|---|
| Definition A — 文档不允许 | 7 | Active unit/IT assertions + `XuguNegativeRegressionBaselineTest` bundle |
| Definition A — 延后 | 20 | `@Disabled` SSOT anchors + active defer docs (A-DDL-007, A-SCH-003, A-FUN-015) |
| Ruler C — 文档不允许 / 延后 | 7 | Bundle + `@Disabled` defer anchors |
| **Total negative-only rows** | **34** | All rows now have `entry_class#method` in SSOT |

## New test class

`dialect/src/test/java/com/xugu/dialect/XuguNegativeRegressionBaselineTest.java`

| Category | Methods | Notes |
|---|---|---|
| Must-add | `readUncommittedNotClaimed_A_XCUT_006`, `charterNoMySqlOracleInheritance_A_XCUT_010`, `charterNoSiblingDialectPort_A_XCUT_011` | P-003 call-out closed |
| P-003 consolidate | `ansiFetchFirstNotEmitted_A_PAG_005`, `skipLockedNotSupported_A_LCK_004_C_SKIP_001`, `forShareNotSupported_A_LCK_005`, `tempTableFkNotEmitted_A_SCH_007`, `enumDdlNotEmitted_C_DDL_004`, `definitionAIfNotExistsDeferred_A_DDL_007`, `catalogsNotSupported_negativeOnly_A_SCH_003`, `p003NegativeOnlyChecklist_coversDocForbiddenBundle` | Centralizes scattered negatives |
| Deferred `@Disabled` | 22 methods (`deferred_A_*`, `deferred_C_*`) | SSOT pointer in `@Disabled` reason |

Surefire (offline): `33` tests — `11` run, `22` skipped (`@Disabled`), `0` failures.

## SSOT update

Updated `contracts/production-regression-baseline.md` negative-only section: every row now lists `entry_class#method` (including `@Disabled` defer anchors).

## Verification

| Command | Exit | Result |
|---|---:|---|
| `mvn -q test` (offline; IT gate OFF) | 0 | **PASS** |

Log: `harness/evidence/test/I-005/P-003/mvn-test-offline.log`

### Dialect module counts (offline gate)

| Metric | Value |
|---|---:|
| Tests run | 138 |
| Failures | 0 |
| Errors | 0 |
| Skipped | 54 (32 IT gate + 22 deferred `@Disabled`) |

## Notes for RP-02 (implementer)

No dialect false positives observed. Negative assertions pass without product code changes.

Pre-existing live IT issue (`C-EXC-002` / `XuguExceptionMappingIT`) from P-002 is **out of P-003 scope** — not re-run in this RP-01 offline gate.

## Acceptance condition

**Condition:** SSOT non-可实现 rows have negative-only or deferred-documented entrypoints.  
**Action:** Offline `mvn -q test`.  
**Result:** PASS — proceed to RP-03 reviewer.
