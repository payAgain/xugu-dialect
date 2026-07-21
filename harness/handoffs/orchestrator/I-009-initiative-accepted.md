# Handoff: I-009 Initiative Accept

**From:** orchestrator  
**To:** Human Gate  
**When:** 2026-07-21T14:15:00+08:00  
**Branch:** `feat/i-009-deferred-matrix-delivery`

## Decision

Human Gate「Initiative Accept I-009」→ Initiative **accepted**  
**NOT Ship** · **NOT Archive**

## Delivered

- Build **B-001** / Phases **P-001 … P-011** all **accepted**
- Deferred matrix **20/20** closed（covered-live 7 / known-limit 12 / doc-forbidden 1）
- Charter honest rollup：**83/98** covered-live + **15** known-limit
- Live @5287 triage：ReservedIdentity **fixed**；ENCRYPT + XMLTABLE **known-limit**；dialect **208/0/0/3**
- GAV: `com.xugu:xugu-dialect:7.4.5.Final` — no bump；`compatiblemode=NONE`
- **VERIFY PASS**（offline + post-triage）

## Key SHAs

| Checkpoint | SHA |
|---|---|
| Pre-Accept HEAD | `01181f2ee62e10d9671cd821df4c8122adda0a8b` |
| Live triage | `8d1de762ff99c2de6883292dca58e8c0c08cc171` |
| P-011 accept | `9c74f53` |
| Accept must-commit | *(filled after commit)* |

## Evidence

- `harness/evidence/orchestrator/I-009/ACCEPTANCE.md`
- `harness/initiatives/I-009/brief.md`
- Live: `harness/evidence/test/I-009/live-it-5287-rerun/`
- Prior B-001: `harness/handoffs/orchestrator/I-009-B-001-complete.md`

## Ask Human Gate (optional next)

1. **是否 Archive I-009？**
2. **是否授权 Ship？**（tag / push / Maven Central — 需明确授权；见 Accept 后发版/生产判定）
3. 或开新 Initiative

## Warnings

- Root **`org/`** / **`META-INF/`** dumps remain untracked — **not committed**
- Charter 仍有 **~15 known-limit**（含高价值深 ORM：INTERVAL/XML/spatial/UDT 等类型映射）
- **NONE-only**；无 MySQL/Oracle 兼容模式产品线
- **无** 仓库内公开 CI / Central 发布流水线作为本 Accept 门禁
