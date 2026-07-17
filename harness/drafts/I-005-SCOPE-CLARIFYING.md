# I-005 Scope Clarifying (draft)

> Status: **clarifying** — not yet Scope PASS  
> Trigger: Human Gate「开始」生产回归基线（2026-07-17）  
> Prerequisite: I-004 still `accepted` / not archived

## Understanding (facts vs assumptions)

**Facts（来自对话）**
- 当前方言为原生实现：`extends Dialect`，不继承 / 不移植 MySQLDialect。
- 需要**全量**测试验证可行性。
- 这批生产向用例将冻结为**回归基线**，供未来迭代反复跑。

**Assumptions（待确认）**
- Initiative 类型倾向 **feature**（非 hotfix；是否 major/升版待确认）。
- 「全量」= Definition A「可实现」+ I-003「可实现」的入口级实库覆盖，并固化基线清单；不是旁路仓 128 全量工程套件。
- 默认 IT 门控仍保留；基线验收要求显式全开真库绿。
- GAV 仍 `7.4.5.Final`（不升版），除非人类要求 major。

## Proposed one-line goal

建立「非 MySQL 继承」方言的**生产回归测试基线**（清单 + 全量门控 IT + 文档化如何跑），作为后续迭代的防回退门槛。

## Open Questions

见 Human Gate 本轮提问（类型 / Archive I-004 / 全量边界 / 门禁 / 延后行 / 升版）。
