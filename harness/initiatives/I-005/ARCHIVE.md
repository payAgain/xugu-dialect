# I-005 Archive

> Initiative: `I-005`  
> Type: `feature`  
> Role: orchestrator (Archive)  
> **NOT Ship** — no tag / push / Maven Central

## Archived

| Field | Value |
|---|---|
| Archived at | `2026-07-18T14:23:00+08:00` |
| Human Gate phrase | 「Archive I-005」 |
| Final status | **accepted** then **archived** |

## Delivery summary

| Deliverable | Result |
|---|---|
| GAV | `com.xugu:xugu-dialect:7.4.5.Final`（无 bump） |
| SSOT | `contracts/production-regression-baseline.md` |
| P-001 | 基线 SSOT 盘点（Definition A + I-003 可实现） |
| P-002 | Live-entry gap closure（6 hard + stretch） |
| P-003 | `XuguNegativeRegressionBaselineTest`（34 negative-only） |
| P-004 | C-BULK-002 **known-limit-documented** |
| P-005 | `DemoBootBaselineSmokeTest` gated smoke |
| P-006 | Docs align + **VERIFY PASS** |
| Project verification | **VERIFY PASS** |

Phases **P-001 → P-006** accepted under Build **B-001**. Initiative Accept recorded (~2026-07-18T13:00+08:00).

## Branch

- Working branch: `feat/i-005-production-regression-baseline`
- Base: post I-004

## Key SHAs

| Checkpoint | SHA |
|---|---|
| P-001 | `5a40af4` |
| P-002 | `e892e90` |
| P-003 | `2509859` |
| P-004 | `840d797` |
| P-005 | `840d797` |
| P-006 | `8993a03` |
| B-001 closeout | `4af4ebf` |
| Initiative Accept | `32b960b` |
| Pre-Archive HEAD | `21186f9` |

## Ship deferred (explicit)

- **tag / push / Maven Central:** not performed  
- Ship requires **separate** Human Gate authorization

## Pointers

- Initiative Accept: `harness/evidence/orchestrator/I-005/ACCEPTANCE.md`
- Brief: `harness/initiatives/I-005/brief.md`
- SSOT: `contracts/production-regression-baseline.md`
- VERIFY: `harness/evidence/test/I-005/P-006/verification.json`

## Next

1. **Ship** — only with new Human Gate authorization  
2. 新 Initiative

## Status

`archived` — not shipped
