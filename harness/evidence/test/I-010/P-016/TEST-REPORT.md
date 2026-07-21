# TEST-REPORT — I-010 P-016 RP-01

- **invocation_id:** inv-i010-p016-rp01-test
- **branch:** `feat/i-010-orm-hql-quality-completion`
- **base tip (pre-commit):** `4a5ff93148c7d7aafccdcbff4558140d634381b7`
- **completed_at:** 2026-07-21T16:36:00+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| harness_check | `python harness/scripts/harness_check.py` | 0 | **HARNESS_CHECK PASS** |
| branch_check | `python harness/scripts/branch_check.py` | 0 | **BRANCH_CHECK PASS** |
| dialect test | `mvn -q -pl dialect test` | 0 | **PASS** |
| focused | `-Dtest=XuguNullSemanticsIT,XuguTemporalProjectionIT,XuguExceptionConversionTest` | 0 | **PASS** (IT skip 5; Unit 10) |
| verify.py | `python harness/scripts/verify.py --phase P-016 --evidence …/verification.json` | 0 | **VERIFY PASS** |
| live full reactor | `XUGU_RUN_IT=true mvn -q -pl dialect test` | n/a | **SKIPPED_INFRA** |

Maven: `C:\Users\admin\tools\apache-maven-3.9.9\bin\mvn.cmd`

## Deliverables

| Theme | Class | Offline | Live |
|---|---|---|---|
| XP-008 Null | `XuguNullSemanticsIT` (4) | gate-skip PASS | SKIPPED_INFRA |
| XP-009 Temporal | `XuguTemporalProjectionIT` (1) | gate-skip PASS | SKIPPED_INFRA |
| XP-010 Lock | `XuguExceptionConversionTest` (+2 variant methods → 10 total) | Unit PASS | live IT not added (`live-unstable`) |

### Null methods

- `isNullQueryHitsNullableString`
- `isNotNullQueryHitsPresentString`
- `threeValuedEqualsNullParamDoesNotHit`
- `coalesceProjectionFallbackAndPresent` (A-FUN-007)

### Temporal scope

Only: `year` / `month` / `day` / `extract` / `current_date` / `current_timestamp` (seed `2024-03-15 12:30:00`).

### Lock unit variants

- DEADLOCK via JDBC / SQLState `xugu14001` / `[E14001]` → `LockAcquisitionException`
- LOCK_TIMEOUT / DETAIL / UPGRADE via JDBC / SQLState / `[E#####]` → `LockTimeoutException`

## Live probe

| Probe | Result |
|---|---|
| TCP 192.168.2.239:5138 | **closed** |
| TCP 127.0.0.1:5138 | **closed** |

## Verdict

**PASS** — offline gates green; live infra-blocked. **NOT Ship.**

## Artifacts

- `ACCEPTANCE.md`
- `verification.json`
- `IT-RESULT.txt`
- `live-db-probe.txt`
- `mvn-test-offline.txt`
- `verify-output.txt`
