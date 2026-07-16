# P-007 Implementer NOTES — docs/matrix align + Accept prep (I-003)

> **Invocation:** `impl-p007-20260716` · I-003 / B-007 / RP-01  
> **Role:** implementer (docs only — no dialect behavior rewrite)  
> **Accept / Ship / commit:** **NOT** claimed (await RP-02 test + RP-03 reviewer; orchestrator owns final ACCEPTANCE)

## Goal

Align documentation/matrix with P-002…P-006 delivered behavior; prepare Initiative Accept evidence (**不含 Ship**).

## Docs aligned

| File | Update |
|---|---|
| `contracts/feature-matrix-i003-ruler-c.md` | Status → **CONFIRMED / Accept-ready**; C-EXC / C-JSON Acceptance ✅; delivery table + IT classes; deferred/文档不允许 remain |
| `contracts/xugu-dialect.contract.md` | §7.1 Phase delivery column (P-002…P-006 delivered; P-007 docs) |
| `docs/user-guide/04-feature-matrix.md` | Pointer to I-003 ruler-C SSOT; Definition A remains; C-* examples |
| `docs/user-guide/README.md` | Link I-003 matrix; same-GAV capability expansion note |
| `docs/user-guide/05-troubleshooting.md` | §9 JSON_FUNCTIONS_ENABLED; §10 bulk insert N/A; §11 ENUM null |
| `README.md` | I-003 parity blurb + ruler-C link under **7.4.5.Final** |
| `docs/feature-matrix-i003-ruler-c.md` | New stub → contracts SSOT |
| `harness/initiatives/I-003/brief.md` | P-001…P-006 criteria checked; Ship unchecked; P-007 in progress |

## Delivered C-* (first-batch 可实现) + IT

| Phase | IDs | Key IT |
|---|---|---|
| P-002 | C-EXC-001, C-EXC-002 | `XuguExceptionMappingIT` |
| P-003 | C-JSON-001…004 | `XuguJsonAggregateIT` |
| P-004 | C-WIN-001, C-CTE-001 | `XuguWindowCteIT` |
| P-005 | C-BULK-001…003 | `XuguBulkMutationIT` (bulk insert live IT **N/A** documented) |
| P-006 | C-DDL-001…004, C-CAT-001, C-GUID-001 | `XuguTypeDdlDetailsIT` (ENUM → null) |

## Deferred / 文档不允许 (unchanged)

- **延后:** C-JSON-005, C-JSON-006, C-DDL-005, C-SRV-001, C-SEL-001
- **文档不允许:** C-DDL-004 (ENUM), C-SKIP-001 (SKIP LOCKED)

## Forbidden respected

- No dialect Java rewrite; no harness agents/skills/verification.json changes
- Version remains **7.4.5.Final**; no Ship / tag / push / Accept claim / git commit
- No invented product capabilities

## Next

- RP-02 test: full `verify.py` / gated IT → `harness/evidence/test/P-007/`
- RP-03 reviewer: docs/behavior consistency
- Orchestrator: draft Initiative Accept evidence under orchestrator namespace (**no Ship**)
