# Definition A Feature Matrix (docs pointer)

> **SSOT (do not duplicate):** [`contracts/feature-matrix-definition-a.md`](../contracts/feature-matrix-definition-a.md)  
> **Public dialect contract:** [`contracts/xugu-dialect.contract.md`](../contracts/xugu-dialect.contract.md)  
> **User guide (status meanings):** [`docs/user-guide/04-feature-matrix.md`](user-guide/04-feature-matrix.md)  
> **Phase:** P-003 / Build B-003 / Initiative I-002 (docs polish; matrix SSOT still under contracts/)  

This file is a short navigation stub only. The full 105-row Definition A matrix lives under `contracts/` so implementer Phases can treat the contract tree as the single source of truth. Integrators: start at [`docs/user-guide/`](user-guide/README.md).

**I-002 hotfix notes (same GAV 7.4.5.Final):** HQL/Criteria pagination via `SqlAstTranslator` → `LIMIT … [OFFSET …]` (not ANSI FETCH); schema validate sequences via `all_sequences`. See matrix A-PAG-* / A-SEQ-001 and [05-troubleshooting.md](user-guide/05-troubleshooting.md).

**I-003:** Definition A remains in force. Capability expansion (C-*) lives in [`docs/feature-matrix-i003-ruler-c.md`](feature-matrix-i003-ruler-c.md) → contracts SSOT.

**I-009 / I-010：** 产品延后行已关闭；Definition A open **延后** 仅 `A-XCUT-012`（Ship）。诚实 live/known-limit：[`contracts/production-regression-baseline.md`](../contracts/production-regression-baseline.md)（A-FUN-021 = **known-limit-documented**）。用户说明：[04-feature-matrix.md § I-009](user-guide/04-feature-matrix.md#i-009-deferred-closure终态)、[05-troubleshooting.md §13–15](user-guide/05-troubleshooting.md#13-encrypt-by--partition--catalog-工具边界)。
