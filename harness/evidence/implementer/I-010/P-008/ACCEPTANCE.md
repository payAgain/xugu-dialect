# ACCEPTANCE — I-010 / P-008 (RP-01 implementer)

> Role: implementer · Matrix rows: **A-FUN-003 / 005 / 006 / 007 / 009** independent HQL Session
> Branch: `feat/i-010-orm-hql-quality-completion`
> Completed: 2026-07-21T15:52:00+08:00
> SSOT fix: 2026-07-21 (revert covered-live inflation under SKIPPED_INFRA)

## Decision

- **Path:** Five independent gated HQL `Session.createQuery` methods (end thin adjacent-live as evidence quality)
- **SSOT:** remain **known-limit-documented** — independent HQL IT anchors present; **promote to covered-live only after live PASS**
- **Rationale:** Registry already had descriptors; Batch A waiver was “unit + A-FUN-001/004/010 bundle”. Each family now has its own dialect IT method. Reuse `P006FunEntity` SessionFactory bootstrap. No dialect registration change expected/needed. Prior `0b83a3d` promotion to covered-live under SKIPPED_INFRA was SSOT inflation (reviewer ACCEPT FAIL) and is reverted.

## Methods per family

| matrix_id | method | HQL surface | SSOT status |
|---|---|---|---|
| A-FUN-003 | `lengthFamilyHqlSession_A_FUN_003` | `length('Alice')`, `length(e.name)`, `length(concat(e.name,'!'))` | **known-limit-documented** |
| A-FUN-005 | `trimFamilyHqlSession_A_FUN_005` | `trim` / `ltrim` / `rtrim` on padded entity name + literal | **known-limit-documented** |
| A-FUN-006 | `replaceLocateHqlSession_A_FUN_006` | `replace` / `locate` entity + `replace` literal | **known-limit-documented** |
| A-FUN-007 | `coalesceNvlHqlSession_A_FUN_007` | `coalesce` / `nvl` present + nullif→null path | **known-limit-documented** |
| A-FUN-009 | `roundTruncHqlSession_A_FUN_009` | `round(1.4|1.6)`, `trunc(1.9)`, `trunc(e.amount)` | **known-limit-documented** |

Class: `XuguBatchAFunctionFamiliesIT` (retained; IT code is good)

## Checklist

| Item | Evidence | Result |
|---|---|---|
| Registry unit | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered` | **PASS** |
| Five independent HQL Session IT methods | `XuguBatchAFunctionFamiliesIT` | **PASS** (code) / **SKIPPED_INFRA** (live) |
| SSOT honesty | baseline + Definition A stay **known-limit-documented**; IT anchors kept; no covered-live under SKIPPED_INFRA | **PASS** (after SSOT fix) |
| No dialect change | registrations already present | **PASS** (no main Java change) |

## Delivered files

- `dialect/src/test/java/com/xugu/dialect/it/XuguBatchAFunctionFamiliesIT.java`
- `contracts/production-regression-baseline.md`
- `contracts/feature-matrix-definition-a.md`

## Residual

- **live:** re-run `XUGU_RUN_IT=true` when DB up; only then promote five rows to **covered-live**
- **out of scope:** position() live (registry unit; A-FUN-006 claims replace|locate); floor/ceil live (A-FUN-009 claims round|trunc)
- **RP-03 reviewer:** confirm SSOT = known-limit-documented with IT anchors; ACCEPT FAIL on inflation addressed

## Observed flow

- `batch-a-function-independent-hql-live`: offline unit/IT gate green; five HQL IT methods written; live SKIPPED_INFRA (`192.168.2.239:5138` / `127.0.0.1:5138` refused); SSOT not inflated
