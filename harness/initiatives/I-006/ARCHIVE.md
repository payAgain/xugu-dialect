# I-006 Archive

> Initiative: `I-006`  
> Type: `feature`  
> Role: orchestrator (Archive)  
> **NOT Ship** — no tag / push / Maven Central

## Archived

| Field | Value |
|---|---|
| Archived at | `2026-07-19T13:53:00+08:00` |
| Human Gate phrase | 「Archive I-006；类型 feature；其余按推荐；本 Initiative 范围已明确，可以开干」 |
| Final status | **accepted** then **archived** |

## Delivery summary

| Deliverable | Result |
|---|---|
| GAV | `com.xugu:xugu-dialect:7.4.5.Final`（无 bump） |
| SSOT | `contracts/consumer-path-baseline.md`（Boot-required 41；open gaps **0**） |
| Layers | **A** 黄金路径 + **B-both**（关联+SEQUENCE）+ **C′** Boot 必测扫盲 |
| Demo | `@Test` ≈ **28**；live 28/0/0/0 |
| P-001 | 消费者路径 Boot 必测 SSOT |
| P-002 | Layer A 黄金路径覆盖 |
| P-003 | Layer B-both 覆盖 |
| P-004 | Layer C′ 剩余 Boot 必测入口关闭 |
| P-005 | Docs 对齐 + **VERIFY PASS** Accept 准备 |
| Project verification | **VERIFY PASS** |

Phases **P-001 → P-005** accepted under Build **B-001**. Initiative Accept recorded (~2026-07-19T11:07+08:00).

## Branch

- Working branch: `feat/i-006-consumer-path-coverage`
- Base: post I-005 Archive

## Key SHAs

| Checkpoint | SHA |
|---|---|
| P-001 | `e5f2428` |
| P-002 | `9f4cbd6` |
| P-003 | `3aae8f0` |
| P-004 | `929be22` |
| P-005 | `7566e1c` |
| Initiative Accept | `3c14d99` |
| Pre-Archive HEAD | `aa3e9db` |

## Ship deferred (explicit)

- **tag / push / Maven Central:** not performed  
- Ship requires **separate** Human Gate authorization

## Pointers

- Initiative Accept: `harness/evidence/orchestrator/I-006/ACCEPTANCE.md`
- Brief: `harness/initiatives/I-006/brief.md`
- SSOT: `contracts/consumer-path-baseline.md`
- VERIFY: `harness/evidence/test/I-006/P-005/verification.json`

## Next

1. **Ship** — only with new Human Gate authorization  
2. Successor Initiative **I-007** (capability hardening A/B/C) — Plan / Build after this Archive

## Status

`archived` — not shipped
