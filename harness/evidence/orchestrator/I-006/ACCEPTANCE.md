# I-006 Initiative Acceptance Evidence

> Initiative: `I-006`  
> Type: `feature`  
> Role: orchestrator (Initiative Accept)  
> Date: 2026-07-19

## Decision

- **Decision:** `accepted`
- **Human Gate phrase:** 「Initiative Accept I-006」
- **Timestamp:** ~2026-07-19T11:07+08:00
- **Scope:** Initiative Accept only — **NOT Ship** · **NOT Archive** (optional next)

## Delivery summary

| Deliverable | Result |
|---|---|
| GAV | `com.xugu:xugu-dialect:7.4.5.Final`（无 bump） |
| SSOT | `contracts/consumer-path-baseline.md`（Boot-required 41；open gaps **0**） |
| Layers | **A** 黄金路径 + **B-both**（关联+SEQUENCE）+ **C′** Boot 必测扫盲 |
| Demo | `@Test` ≈ **28**（目标带 25–40）；live 28/0/0/0 |
| P-001 | 消费者路径 Boot 必测 SSOT 盘点/冻结 |
| P-002 | Layer A 黄金路径覆盖 |
| P-003 | Layer B-both 覆盖 |
| P-004 | Layer C′ 剩余 Boot 必测入口关闭 |
| P-005 | Docs 对齐 + **VERIFY PASS** Accept 准备 |
| Project verification | **VERIFY PASS** — `harness/evidence/test/I-006/P-005/verification.json` |

Build **B-001** / Phases **P-001 … P-005** all **accepted**.

## Branch

- Working branch: `feat/i-006-consumer-path-coverage`
- Pre-Accept HEAD: `f438cd3`
- Accept must-commit: `3c14d993d6528da2e0e8094a334762045a0897e5`

## Key delivery SHAs

| Phase | Delivery / Accept SHA |
|---|---|
| P-001 | `e5f2428` |
| P-002 | `9f4cbd6` |
| P-003 | `3aae8f0` |
| P-004 | `929be22` |
| P-005 | `7566e1c` |

## Ship deferred (explicit)

- **tag / push / Maven Central:** not performed  
- Ship requires **separate** Human Gate authorization

## Evidence anchors

- SSOT: `contracts/consumer-path-baseline.md`
- P-005 VERIFY: `harness/evidence/test/I-006/P-005/verification.json`
- Accept checklist: `harness/evidence/implementer/I-006/P-005/INITIATIVE-ACCEPT-CHECKLIST.md`
- Pattern: `harness/evidence/orchestrator/I-005/ACCEPTANCE.md`
- Brief: `harness/initiatives/I-006/brief.md`

## Next (optional)

1. **Archive I-006**
2. **Ship**（另行授权）
3. 新 Initiative
