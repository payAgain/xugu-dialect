# Handoff: I-010 Initiative Accept

**From:** orchestrator  
**To:** Human Gate  
**When:** 2026-07-21T17:10:00+08:00  
**Branch:** `feat/i-010-orm-hql-quality-completion`

## Decision

Human Gate「真实虚谷跑通后门控 → Initiative Accept I-010」→ Initiative **accepted**  
**NOT Ship** · **NOT Archive**

## Delivered

- Build **B-001** / Phases **P-001 … P-010** all **accepted**
- Build **B-002** / Phases **P-011 … P-017** all **accepted**（xuguefcore parity 10/10）
- Live @5287：dialect **253/0/0/4** · demo **36/0/0/0**
- SSOT 晋升：parity XP-001…005/007…009 → covered-live；A-TYP-014/016/017 + Batch A → covered-live
- Residual：**A-FUN-021** known-limit（XMLTABLE）；XP-006/010 covered-unit
- Charter rollup：**91/98** covered-live + **7** known-limit
- GAV: `com.xugu:xugu-dialect:7.4.5.Final` — no bump；`compatiblemode=NONE`
- **VERIFY PASS**

## Key SHAs

| Checkpoint | SHA |
|---|---|
| B-002 tip | `2368cbbed8beec5891179abb75ea5a2ae269be9d` |
| Live IT + SSOT promote | `51de6e32ba788110ba209f9e0483b7543daba992` |
| Accept must-commit | *(see git log after Accept commit)* |

## Evidence

- `harness/evidence/orchestrator/I-010/ACCEPTANCE.md`
- `harness/evidence/test/I-010/live-it-5287/`
- `harness/initiatives/I-010/brief.md`
- Prior B-002: `harness/handoffs/orchestrator/I-010-B-002-complete.md`

## Ask Human Gate (optional next)

1. **是否 Archive I-010？**
2. **是否授权 Ship？**（tag / push / Maven Central — 需明确授权）
3. 或开新 Initiative

## Warnings

- Root **`org/`** / **`META-INF/`** dumps remain untracked — **not committed**
- **NONE-only**；无 MySQL/Oracle 兼容模式产品线
- **无** 仓库内公开 CI / Central 发布流水线作为本 Accept 门禁
