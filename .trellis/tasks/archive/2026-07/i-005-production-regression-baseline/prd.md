# Initiative Brief: I-005

> Scoped clarity for one change unit. Not a full product re-charter.
> Type: feature
> Updated: 2026-07-18T14:23:00+08:00

## Goal
- 为**不继承 / 不移植 MySQLDialect** 的原生虚谷方言，建立**全量生产回归测试基线**：用可复现的真库入口用例验证可行性，并冻结为后续迭代的防回退门槛。
- GAV 保持 **`com.xugu:xugu-dialect:7.4.5.Final`**（本 Initiative 不升版）。

## Human Gate Scope PASS
「本 Initiative 范围已明确，可以开干」(~2026-07-17T15:23+08:00)  
未逐条作答时按澄清轮**推荐项**锁定：

| # | Decision |
|---|---|
| 0 | **Archive I-004**（不含 Ship） |
| 1 | Type **feature** |
| 2 | **全量边界 A**：Definition A 全部「可实现」+ I-003 全部「可实现」→ 每项有可回归真库入口（或等价入口）+ **基线清单 SSOT** |
| 3 | **门禁 A**：基线验收必须 `XUGU_RUN_IT=true`（或等价）**全绿**；日常默认仍可跳过 IT |
| 4 | **延后/文档不允许 A**：进基线为**负向断言**（明确不支持 / 不发明 SQL） |
| 5 | **不升版**（7.4.5.Final） |
| 6 | **Demo A**：增加可自动化用户冒烟（Boot Test 或脚本），纳入基线 |
| 7 | **Bulk insert A**：本 Initiative 内补通实库 IT **或**基线正式标为不支持/已知限制（二选一钉死） |
| 8 | **原生实现**（禁止移植 sibling / 旧方言源码；禁止继承 MySQL/Oracle Dialect） |
| 9 | **不要求**本 Initiative 完成 Ship / Central |
| 10 | **Initiative Accept** — **确认**（~2026-07-18T13:00+08:00；不含 Ship） |

## Acceptance criteria
- [x] 基线清单 SSOT 覆盖 Definition A「可实现」+ I-003「可实现」；每项映射到可执行用例（或显式缺口关闭记录）
- [x] 「文档不允许」/「延后」行有负向断言或基线条目（不发明 SQL）
- [x] 门控真库全量基线在 `XUGU_RUN_IT=true` 下 **PASS**（作为冻结基线）
- [x] Demo/用户冒烟自动化纳入基线并可复现
- [x] Bulk insert：实库 IT PASS **或**正式「不支持/已知限制」写入基线与用户文档（**C-BULK-002 known-limit-documented**）
- [x] `verify.py` **VERIFY PASS**；版本仍为 7.4.5.Final
- [ ] **不要求** Ship（保持未勾选）

## Related
- Predecessor: I-004 archived
- Draft clarify: `harness/drafts/I-005-SCOPE-CLARIFYING.md`
- Branch: `feat/i-005-production-regression-baseline`
- SSOT: `contracts/production-regression-baseline.md`
- Accept evidence: `(historical harness evidence removed; see Trellis task research/ if present) orchestrator/I-005/ACCEPTANCE.md`
- Ship: **out of this Initiative**

## Status
`archived` — Human Gate「Archive I-005」(~2026-07-18T14:23+08:00); **not shipped**

- Scope PASS: ~2026-07-17T15:23+08:00
- B-001 P-001…P-006: all accepted
- Initiative Accept: ~2026-07-18T13:00+08:00
- Archive: ~2026-07-18T14:23+08:00
- Archive doc: `.trellis/tasks/archive/2026-07/ (historical; former harness path) I-005/ARCHIVE.md`
