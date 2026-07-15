# 04 — Feature matrix / 特性矩阵

← [03-verify.md](03-verify.md) · [Index](README.md) · Next: [05-troubleshooting.md](05-troubleshooting.md)

## Where to read / 读哪里

| Document | Role |
|---|---|
| **[`contracts/feature-matrix-definition-a.md`](../../contracts/feature-matrix-definition-a.md)** | **SSOT** — 全行 Definition A 矩阵（勿在别处复制全文） |
| [`docs/feature-matrix-definition-a.md`](../feature-matrix-definition-a.md) | 导航 stub / pointer，指向 contracts SSOT |
| [`contracts/xugu-dialect.contract.md`](../../contracts/xugu-dialect.contract.md) | 公共接入面 + 能力边界绑定矩阵 |

本用户指南 **只解释状态含义并给链接**，不重复 100+ 行矩阵正文。

## Definition A（一句话）

**Definition A** = MySQL/Oracle Dialect **生产能力面** ∩ **虚谷文档允许** 的能力。超出文档允许的 SQL，实现侧 **不得编造**。

## Status legend / 状态含义

| Status | 中文 | Meaning for integrators |
|---|---|---|
| **可实现** | 可实现 | 文档允许；已在 I-001 相关 Phase 落地或验收。应用可依赖方言生成的对应 SQL/行为（以矩阵 Acceptance hint 为准）。 |
| **文档不允许** | 不允许 | 虚谷文档无对应语法/行为。方言 **不会** 发明 SQL（例如 SKIP LOCKED、ANSI `FETCH FIRST`、FOR SHARE）。应用勿假设这些能力。 |
| **延后** | 延后 | 文档可能允许，但不在当前 Initiative/Phase 优先范围。需新 Scope / revisit trigger 后再做。 |

（矩阵正文使用「文档不允许」；口语可说「不允许」。）

## How to use the matrix

1. 按 Domain / Phase 找到能力行（Types、Pagination、Locks、Identity、Functions、Schema、SPI…）。
2. 看 **Status** 与 **Target Phase**。
3. 看 **Acceptance hint**（是否已有单元/IT 勾选）。
4. 需要 SQL 真相时，跟 **Xugu doc ref** 去官方 content（只读；**不要**改 `E:\Work\docs\content`）。

## Examples（帮助建立直觉）

| ID | Status | Integrator takeaway |
|---|---|---|
| A-PAG-001 / A-LCK-001 | 可实现 | 分页 + `FOR UPDATE` 可用；组合时语法顺序为虚谷要求（见排障） |
| A-LCK-004 SKIP LOCKED | 文档不允许 | `supportsSkipLocked=false`；不会发出该关键字 |
| A-TYP-014 INTERVAL 等 | 延后 | 暂勿当作已交付能力 |

## Cross-links

- Contract §7 Capability scope → matrix  
- User guide index: [README.md](README.md)  
- Demo 仅证明接入路径，**不以 demo 覆盖矩阵全行**
