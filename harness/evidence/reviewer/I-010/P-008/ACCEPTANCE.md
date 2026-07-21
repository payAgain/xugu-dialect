# Reviewer ACCEPTANCE — I-010 / P-008

- **verdict:** ACCEPT FAIL (initial) → **fix landed; ready for re-review**
- **failing_commit:** `0b83a3daddb382b86bc87ca43b506284de801269`
- **blocking finding:** A-FUN-003/005/006/007/009 promoted to covered-live under SKIPPED_INFRA (SSOT inflation)
- **IT code:** PASS (5 independent HQL Session methods; no invented SQL) — `XuguBatchAFunctionFamiliesIT` retained
- **fix expected:** revert status to known-limit-documented; keep IT anchors; promote only after live PASS

## Fix note (addresses ACCEPT FAIL)

SSOT inflation from `0b83a3d` is corrected:

1. `contracts/production-regression-baseline.md` — five rows + P-008 section + Batch A waiver notes → **known-limit-documented**; HQL IT methods kept as evidence anchors; gap_action: promote to covered-live only after live PASS
2. `contracts/feature-matrix-definition-a.md` — mirror acceptance hints aligned (no covered-live claim)
3. `harness/evidence/implementer/I-010/P-008/ACCEPTANCE.md` — honest SSOT
4. IT class **not** deleted

**Re-review focus:** confirm five rows are **known-limit-documented** (not covered-live) while IT anchors remain.

| matrix_id | expected status after fix |
|---|---|
| A-FUN-003 | known-limit-documented |
| A-FUN-005 | known-limit-documented |
| A-FUN-006 | known-limit-documented |
| A-FUN-007 | known-limit-documented |
| A-FUN-009 | known-limit-documented |
