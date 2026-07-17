# Initiative Brief: I-004

> Scoped clarity for one change unit. Not a full product re-charter.
> Type: hotfix
> Updated: 2026-07-17T14:36:00+08:00

## Goal
- 修复两则可稳定复现缺陷：（1）`DROP SEQUENCE` 非幂等导致 `GenerationType.AUTO` + create-drop 在 `halt_on_error` 下建不动 SessionFactory；（2）`IDENTITY` + 保留字表名在 Hibernate persist 路径失败。GAV 保持 **`7.4.5.Final`**。

## Human Gate Scope PASS
「本 Initiative 范围已明确，可以开干」(~2026-07-17T14:36+08:00)

| # | Decision |
|---|---|
| 1 | Type **hotfix** |
| 2 | Bug 1 + Bug 2 **都进**本 Initiative |
| 3 | **不升版**（7.4.5.Final） |
| 4 | 需要门控真库 **ORM 入口 IT** |
| 5 | **不改 JDBC/驱动**相关问题（Bug 2 仅方言侧缓解） |
| 6 | **原生实现**（禁止移植 sibling / 旧方言源码） |

## In-scope
1. **P-001：** `XuguSequenceSupport.getDropSequenceString` → `drop sequence if exists …`；单元 + ORM IT（`GenerationType.AUTO` + create-drop / SchemaExport 路径，验证不因缺序列 E7002 失败）
2. **P-002：** 方言侧 IDENTITY 回退策略，使保留字表名（如 `order`）在 Hibernate persist 下可工作——**不修改 JDBC 驱动**；可选关闭/绕开 `RETURN_GENERATED_KEYS`、改走 `LAST_INSERT_ID()` 等已有方言钩子；ORM IT 证明

## Out-of-scope
- 修改 JDBC 驱动 / 修 RETURN_GENERATED_KEYS 驱动解析
- 移植 sibling `hibernate-dialect` 源码
- 继承 MySQL/Oracle Dialect
- 升版本；Ship / tag / push / Central
- harness 框架 agents/skills/verification.json 硬化
- I-003 延后 C-* 能力扩展

## Acceptance criteria
- [x] P-001：`drop sequence if exists` 落地；AUTO/create-drop 门控 IT PASS
- [x] P-002：IDENTITY + 保留字表名 persist 门控 IT PASS（方言缓解，非驱动补丁）
- [x] `verify.py` VERIFY PASS；版本仍为 7.4.5.Final
- [ ] **不要求** Ship

## Status
`active` — B-001 complete (P-001+P-002 accepted); awaiting Human Gate **Initiative Accept**
