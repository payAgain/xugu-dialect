# P-001 Implementer Notes (I-002 HQL SqlAstTranslator)

**Invocation:** `impl-p001-fix-locklimit-20260715` (fix after `rev-p001-20260715` request-changes)  
**Prior:** `impl-p001-20260715`  
**Role:** implementer  
**Phase / Build / Initiative:** P-001 / B-001 / I-002  
**Date:** 2026-07-15  
**Accept / commit:** **NOT done** (await independent re-test + re-review)

## Root cause (original)

`XuguDialect.getSqlAstTranslatorFactory()` returned `null` → Hibernate used `StandardSqlAstTranslator`, which emits ANSI:

```text
… offset ? rows fetch first ? rows only
```

XuGu rejects this with `[E19132] unexpected OFFSET`.

## RP-03 MAJOR (rev-p001-20260715)

AST/HQL path already deferred LIMIT until after `FOR UPDATE` (and before trailing `WAIT`), matching `XuguLimitHandler`, but **no IT/unit proved** that order. Required before approve.

## What this fix delivered

1. Gated IT `XuguHqlPaginationIT.hqlLockAndPageEmitsForUpdateBeforeLimitAndWaitAfter`:
   - HQL `setFirstResult`/`setMaxResults` + `PESSIMISTIC_WRITE` → asserts `for update` index &lt; `limit` index; executes on live XuGu
   - Same + `Timeout.milliseconds(2000)` → asserts `FOR UPDATE … LIMIT … WAIT`; executes on live XuGu
2. Hardened unlocked page IT to also require `offset` in SQL
3. No product translator change required (deferral path already correct); evidence gap closed only
4. Version remains **7.4.5.Final**; no P-002 sequence work

## Observed SQL (lock + page) — live IT

| Scenario | SQL |
|---|---|
| HQL page + FOR UPDATE | `select phpe1_0.id from HIB_P001_HQL_PAGE phpe1_0 order by phpe1_0.id for update of phpe1_0.id limit ? offset ?` |
| HQL page + FOR UPDATE + WAIT | `select phpe1_0.id from HIB_P001_HQL_PAGE phpe1_0 order by phpe1_0.id for update of phpe1_0.id limit ? offset ? wait 2000` |
| Unlocked HQL page (prior) | `select … from HIB_P001_HQL_PAGE … order by … limit ? offset ?` |

Log: `harness/evidence/implementer/P-001/mvn-test-integration-locklimit.log` (lines with `for update of … limit`).

## Validation

```text
mvn -q test                                          → EXIT 0
mvn -q test -Dxugu.run.integration=true              → EXIT 0
python harness/scripts/verify.py --phase P-001 \
  --evidence harness/evidence/implementer/P-001/verification.json → VERIFY PASS
```

## Explicitly not done

- Accept / must-commit
- Independent RP-02 retest / RP-03 re-review
- P-002 `getQuerySequencesString`
