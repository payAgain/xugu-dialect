# ACCEPTANCE — I-010 P-015 RP-01 (test)

- **Phase:** P-015 · **Build:** B-002 · **Initiative:** I-010
- **invocation_id:** inv-i010-p015-rp01-test
- **branch:** `feat/i-010-orm-hql-quality-completion`
- **base tip (pre-commit):** `4a5ff93148c7d7aafccdcbff4558140d634381b7`
- **completed_at:** 2026-07-21T16:33:18+08:00

## Deliverables

| Item | Path | Status |
|---|---|---|
| Unit goldens | `XuguNativeSqlBaselineTest` + `dialect/src/test/resources/sql-baselines/**` | **PASS** |
| Join fetch IT | `XuguHqlJoinFetchIT` + `I010P015Parent` / `I010P015Child` | **PASS** (gate-skip offline) |
| SSOT | `contracts/xuguefcore-parity-suite.md` XP-006/XP-007 | **updated** |

## Baseline files

| File | Golden (normalized) |
|---|---|
| `limit-offset.sql` | `select id from t order by id limit ? offset ?` |
| `for-update-limit-order.sql` | `select id from t order by id for update limit ?` |
| `identity-column.fragment.sql` | `identity(1,1)` |

Negative: baselines / dialect path do **not** gold SKIP LOCKED or FOR SHARE.

## Acceptance checklist

- [x] Unit goldens offline green (`XuguNativeSqlBaselineTest` 4 methods)
- [x] Join fetch IT gated (`XuguITGate`); offline assume/skip; live **SKIPPED_INFRA**
- [ ] must-commit (working tree ready; commit deferred to Human Gate / orchestrator)

## Observed flows

- **i010-sql-goldens:** offline Equal vs classpath baselines (whitespace-normalized)
- **i010-join-fetch:** HQL `join fetch` one-to-many; IT present, live pending infra

## Verdict

**PASS** for RP-01 offline gates. XP-006=`covered-unit`; XP-007=`skipped-infra`. **NOT Ship.**
