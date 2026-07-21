# ACCEPTANCE — I-010 / P-011 (RP-02 docs)

> Role: docs · Phase: **P-011** xuguefcore 对照套件 SSOT / 文档入口（RP-02）
> Build: **B-002** · Initiative: **I-010**
> Branch: `feat/i-010-orm-hql-quality-completion`
> Completed: 2026-07-21T16:20:00+08:00

## Decision

- **accepted** (docs RP-02 complete; SSOT file owned by architect-contract RP-01 — link may be placeholder until that file lands)
- User-guide entry for B-002 parity suite: IT gate, offline Unit, SKIPPED_INFRA honesty, **NOT Ship**
- 10 themes table with Unit/IT landing points; pointer to `contracts/xuguefcore-parity-suite.md`
- **NOT Ship** / NOT Archive / no covered-live inflation / no Java / no contracts rewrite

## Checklist

| Acceptance item | Evidence | Result |
|---|---|---|
| B-002 verify section + gate | `docs/user-guide/03-verify.md` § I-010 B-002 xuguefcore parity suite | **PASS** |
| 10-theme matrix + SSOT link | `docs/user-guide/04-feature-matrix.md` § I-010 B-002 + Where to read row | **PASS** |
| Index one-liner | `docs/user-guide/README.md` I-010 B-002 扩展 | **PASS** |
| SSOT pointer (P-011 placeholder OK) | `../../contracts/xuguefcore-parity-suite.md` | **PASS** (link; file may land via architect-contract) |
| No Ship claim | docs + this ACCEPTANCE | **PASS** |

## Delivered files

- `docs/user-guide/03-verify.md`
- `docs/user-guide/04-feature-matrix.md`
- `docs/user-guide/README.md`
- `harness/evidence/docs/I-010/P-011/ACCEPTANCE.md`

## Observed flow

| Flow | Result | Notes |
|---|---|---|
| `i010-b002-xuguefcore-parity-ssot` (docs portion) | **PASS** | Entry + table; SSOT body = architect-contract |

## Constraints honored

- No dialect / demo Java
- No `contracts/**` writes (architect-contract owns SSOT)
- No Ship / push / tag / Central
- No covered-live inflation
