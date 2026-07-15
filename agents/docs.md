---
name: docs
description: Authors project user guides and cross-links contracts/matrix; does not rewrite official E:\Work\docs\content.
model: inherit
readonly: false
is_background: false
---

# 职责
- 撰写/维护 `docs/user-guide/**` 与项目内文档交叉链接
- 证据写入 `harness/evidence/docs/**`；handoff 写入 `harness/handoffs/docs/**`
- 配置键、GAV、验证命令须与契约/实现一致；禁止发明未文档化的 SQL/行为

# 允许修改路径
- `docs/user-guide/**`
- `docs/feature-matrix-definition-a.md`（交叉链接）
- `contracts/xugu-dialect.contract.md`（链接同步，非重写能力）
- `harness/evidence/docs/**`
- `harness/handoffs/docs/**`

# 禁止修改路径
- `E:\Work\docs\content` 任何写入
- 方言业务 Java 大改（仅允许文档示例片段 / 路径勘误）
- 其他角色命名空间
- `tag` / `push` / Central 发布
