# Initiative Brief: I-008

> Type: **feature**  
> Updated: 2026-07-20T15:55:00+08:00

## Goal
除性能/并发/多版本外，闭环质量短板 Q1–Q4：
- **Q1**：口径诚实 + **94 可实现行升 covered-live**（不能 live 的须 `known-limit-documented`）
- **Q2**：锁语义文档 + 行为/负向证据（不伪造 SKIP LOCKED/FOR SHARE；`PESSIMISTIC_READ`→排他锁）
- **Q3**：UUID/JSON **Boot 开箱对齐**（依赖 + 配置 + IT）
- **Q4**：Accept 必附全 reactor 真库证据；离线不足以称生产验证
- **Q5**：**不做**（文档可声明未做）

## Human Gate Scope PASS
「本 Initiative 范围已明确，可以开干」(~2026-07-20T15:52+08:00)

| # | Decision |
|---|---|
| 0 | **Archive I-007**（不含 Ship）— 推荐默认，随 Scope PASS 生效 |
| 1 | Type **feature** |
| 2 | 深度：**Q1–Q4 全闭环**（A+B + Boot 开箱 + Accept 真库） |
| 3 | GAV **不升版** `7.4.5.Final` |
| 4 | **不 Ship** |
| 5 | Q5 性能/多版本 **out of scope** |

## Acceptance criteria
- [ ] 用户指南/契约无「94 covered-live」膨胀口径；SSOT 计数诚实
- [ ] 94 可实现行 → covered-live 或明示 known-limit
- [ ] 锁语义专节 + 证据；无伪造禁止语法
- [ ] Demo/Boot UUID+JSON 开箱可跑通（真库或 Boot IT）
- [ ] Initiative Accept 附全 reactor 真库证据
- [ ] `verify.py` VERIFY PASS；GAV 7.4.5.Final；NONE
- [ ] 不要求 Ship

## Related
- Draft: `harness/drafts/I-008-SCOPE-CLARIFYING.md`
- Predecessor: I-007 → archived
- Branch: `feat/i-008-production-quality-gaps`（orchestrator 创建）

## Status
`active` — Plan materialized P-001…P-007；**B-001 draft** → P-001 only；await Human Gate Build approval
