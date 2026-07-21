# ACCEPTANCE — I-010 / P-006 (RP-01 implementer)

> Role: implementer · Matrix row: **A-FUN-020** geometric HQL Session (`area` / `center` / `point`)
> Branch: `feat/i-010-orm-hql-quality-completion`
> Completed: 2026-07-21T15:42:00+08:00

## Decision

- **Path:** HQL `Session.createQuery` **positive** for `area` + `center` + `point` (same shapes as native subset)
- **SSOT:** remain **covered-live** — strengthen with HQL Session anchors; do **not** regress
- **Rationale:** Named geometric descriptors already match XuGu SQL (`area(circle(...))`, `center(box(...))`, `point(x,y)`). Reuse `I010P004PointEntity` SessionFactory bootstrap. Native subset retained.

## HQL surface

```hql
select area(circle('((5, 0), 1)'))
select center(box('(1, 2), (0, 0)'))
select point(23.4, -44.5)
select e.point from I010P004PointEntity e where e.id = 1
```

## Checklist

| Item | Evidence | Result |
|---|---|---|
| Registry unit | `XuguFunctionRegistryTest#geometricSubsetRegistered_A_FUN_020` | **PASS** |
| HQL Session IT | `XuguGeometricTypeAndFunctionsIT#geometricFunctionsHqlSession_A_FUN_020` | **PASS** (code) / **SKIPPED_INFRA** (live) |
| Native IT retained | `geometricFunctionsNativeSubset_A_FUN_020` | retained |
| SSOT honesty | baseline + Definition A = **covered-live** + HQL anchors | **PASS** (no regress) |

## Delivered files

- `dialect/src/test/java/com/xugu/dialect/it/XuguGeometricTypeAndFunctionsIT.java`
- `dialect/src/main/java/com/xugu/dialect/function/XuguGeometricFunctions.java`
- `dialect/src/main/java/com/xugu/dialect/XuguDialect.java` (doc)
- `contracts/production-regression-baseline.md`
- `contracts/feature-matrix-definition-a.md`

## Residual

- **live:** re-run `XUGU_RUN_IT=true` when DB up for HQL Session proof
- **funcs not HQL-live yet:** remaining 18 registered geometric names (box/circle native only; bound_box, diameter, …)
- **RP-03 reviewer:** confirm evidence is HQL Session not only native

## Observed flow

- `geometric-hql-session-live`: offline unit/IT gate green; HQL IT written; live SKIPPED_INFRA (`192.168.2.239:5138` / `127.0.0.1:5138` refused)
