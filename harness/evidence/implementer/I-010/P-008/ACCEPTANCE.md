# ACCEPTANCE — I-010 / P-008 (RP-01 implementer)

> Role: implementer · Matrix rows: **A-FUN-003 / 005 / 006 / 007 / 009** independent HQL Session
> Branch: `feat/i-010-orm-hql-quality-completion`
> Completed: 2026-07-21T15:52:00+08:00

## Decision

- **Path:** Five independent gated HQL `Session.createQuery` methods (end thin adjacent-live)
- **SSOT:** promote **known-limit-documented** → **covered-live** with honesty note (live may SKIPPED_INFRA)
- **Rationale:** Registry already had descriptors; Batch A waiver was “unit + A-FUN-001/004/010 bundle”. Each family now has its own dialect IT method. Reuse `P006FunEntity` SessionFactory bootstrap. No dialect registration change expected/needed.

## Methods per family

| matrix_id | method | HQL surface |
|---|---|---|
| A-FUN-003 | `lengthFamilyHqlSession_A_FUN_003` | `length('Alice')`, `length(e.name)`, `length(concat(e.name,'!'))` |
| A-FUN-005 | `trimFamilyHqlSession_A_FUN_005` | `trim` / `ltrim` / `rtrim` on padded entity name + literal |
| A-FUN-006 | `replaceLocateHqlSession_A_FUN_006` | `replace` / `locate` entity + `replace` literal |
| A-FUN-007 | `coalesceNvlHqlSession_A_FUN_007` | `coalesce` / `nvl` present + nullif→null path |
| A-FUN-009 | `roundTruncHqlSession_A_FUN_009` | `round(1.4|1.6)`, `trunc(1.9)`, `trunc(e.amount)` |

Class: `XuguBatchAFunctionFamiliesIT`

## Checklist

| Item | Evidence | Result |
|---|---|---|
| Registry unit | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered` | **PASS** |
| Five independent HQL Session IT methods | `XuguBatchAFunctionFamiliesIT` | **PASS** (code) / **SKIPPED_INFRA** (live) |
| SSOT honesty | baseline + Definition A promoted; waiver rows closed I-010/P-008 | **PASS** |
| No dialect change | registrations already present | **PASS** (no main Java change) |

## Delivered files

- `dialect/src/test/java/com/xugu/dialect/it/XuguBatchAFunctionFamiliesIT.java`
- `contracts/production-regression-baseline.md`
- `contracts/feature-matrix-definition-a.md`

## Residual

- **live:** re-run `XUGU_RUN_IT=true` when DB up for HQL Session proof on all five methods
- **out of scope:** position() live (registry unit; A-FUN-006 claims replace|locate); floor/ceil live (A-FUN-009 claims round|trunc)
- **RP-03 reviewer:** confirm each family has own method; SSOT not claiming live PASS while SKIPPED_INFRA

## Observed flow

- `batch-a-function-independent-hql-live`: offline unit/IT gate green; five HQL IT methods written; live SKIPPED_INFRA (`192.168.2.239:5138` / `127.0.0.1:5138` refused)
