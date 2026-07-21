# TEST-REPORT — I-010 P-015 RP-01

- **invocation_id:** inv-i010-p015-rp01-test
- **branch:** `feat/i-010-orm-hql-quality-completion`
- **base tip:** `4a5ff93148c7d7aafccdcbff4558140d634381b7`
- **completed_at:** 2026-07-21T16:33:18+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| focus | `mvn -q -pl dialect -Dtest=XuguNativeSqlBaselineTest,XuguHqlJoinFetchIT test` | 0 | **PASS** |
| full dialect | `mvn -q -pl dialect test` | 0 | **PASS** |
| branch_check | `python harness/scripts/branch_check.py` | 0 | **BRANCH_CHECK PASS** |

Maven: `C:\Users\admin\tools\apache-maven-3.9.9\bin\mvn.cmd`

## XP-006 Unit goldens

| Method | Result |
|--------|--------|
| `limitOffsetMatchesBaseline` | **PASS** |
| `forUpdateLimitOrderMatchesBaseline` | **PASS** |
| `identityColumnFragmentMatchesBaseline` | **PASS** |
| `baselinesRejectSkipLockedAndForShareAsPositive` | **PASS** |

## XP-007 Join fetch IT

| Method | Offline (gate off) | Live |
|--------|--------------------|------|
| `hqlJoinFetchInitializesChildrenCollection` | assume/skip | **SKIPPED_INFRA** |

Entities: `I010P015Parent` / `I010P015Child` · tables `HIB_I010_P015_*`  
HQL: `select p from Parent p join fetch p.children where p.id = :id`

## Live probe

See `live-db-probe.txt` — both ports closed.

## Observed flows

- **i010-sql-goldens:** PASS
- **i010-join-fetch:** IT landed; live pending

## Verdict

**PASS** offline. SSOT XP-006=`covered-unit`, XP-007=`skipped-infra`. **NOT Ship.**
