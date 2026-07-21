# ACCEPTANCE — I-010 / P-007 (RP-01 implementer)

> Role: implementer · Matrix row: **A-FUN-019** regexp HQL Session (`regexp_like` / `regexp_replace` / `regexp_substr`)
> Branch: `feat/i-010-orm-hql-quality-completion`
> Completed: 2026-07-21T15:47:00+08:00

## Decision

- **Path:** HQL `Session.createQuery` **positive** for registered `regexp_like` + `regexp_replace` + `regexp_substr` (same shapes as native subset)
- **SSOT:** remain **covered-live** — strengthen with HQL Session anchors; do **not** regress
- **Rationale:** Named regexp descriptors already match XuGu SQL. Reuse `P006FunEntity` SessionFactory bootstrap. Native subset retained. **Do not** claim unregistered aliases (`regexp_instr`, `regexp_count`).

## HQL surface

```hql
select regexp_like('England or America', 'l.nd')
select regexp_like('MCA', 'BCA', 'inx')
select regexp_like(e.name, 'l.nd') from P006FunEntity e where e.id = 1
select regexp_replace('2023-08-01', '(\\d{4})-(\\d{2})-(\\d{2})', '\\2/\\3/\\1')
select regexp_replace('1234567890', '\\d(?=\\d{4})', '*')
select regexp_substr('订单ID: 789, 数量: 456', '[0-9]+')
select regexp_substr('a1,b2,c3,d4', '[^,]+', 5, 2)
```

## Checklist

| Item | Evidence | Result |
|---|---|---|
| Registry unit | `XuguFunctionRegistryTest#regexpSubsetRegistered_A_FUN_019` | **PASS** |
| HQL Session IT | `XuguRegexpAndBitFunctionsIT#regexpFunctionsHqlSession_A_FUN_019` | **PASS** (code) / **SKIPPED_INFRA** (live) |
| Native IT retained | `regexpFunctionsNativeSubset_A_FUN_019` | retained |
| No unregistered alias | unit asserts `regexp_instr` null; HQL IT does not call it | **PASS** |
| SSOT honesty | baseline + Definition A = **covered-live** + HQL anchors | **PASS** (no regress) |

## Delivered files

- `dialect/src/test/java/com/xugu/dialect/it/XuguRegexpAndBitFunctionsIT.java`
- `dialect/src/main/java/com/xugu/dialect/function/XuguRegexpFunctions.java`
- `dialect/src/main/java/com/xugu/dialect/XuguDialect.java` (doc)
- `contracts/production-regression-baseline.md`
- `contracts/feature-matrix-definition-a.md`

## Residual

- **live:** re-run `XUGU_RUN_IT=true` when DB up for HQL Session proof
- **out of scope:** `regexp_instr` / `regexp_count` (unregistered)
- **RP-03 reviewer:** confirm evidence is HQL Session not only native; no false alias claims

## Observed flow

- `regexp-hql-session-live`: offline unit/IT gate green; HQL IT written; live SKIPPED_INFRA (`192.168.2.239:5138` / `127.0.0.1:5138` refused)
