# 04 — Feature matrix / 特性矩阵

← [03-verify.md](03-verify.md) · [Index](README.md) · Next: [05-troubleshooting.md](05-troubleshooting.md)

## Where to read / 读哪里

| Document | Role |
|---|---|
| **[`contracts/feature-matrix-definition-a.md`](../../contracts/feature-matrix-definition-a.md)** | **SSOT** — 全行 Definition A 矩阵（勿在别处复制全文） |
| [`docs/feature-matrix-definition-a.md`](../feature-matrix-definition-a.md) | 导航 stub / pointer，指向 contracts SSOT |
| **[`contracts/feature-matrix-i003-ruler-c.md`](../../contracts/feature-matrix-i003-ruler-c.md)** | **I-003 ruler C SSOT** — C-* 生产能力扩展（异常/JSON/Window-CTE/bulk/DDL） |
| [`docs/feature-matrix-i003-ruler-c.md`](../feature-matrix-i003-ruler-c.md) | I-003 导航 stub / pointer |
| [`docs/p004-track-c-capabilities.md`](../p004-track-c-capabilities.md) | **I-007 Track C** — C-JSON-005 / ARRAY / ALTER SEQUENCE 用户向摘要 |
| **[`contracts/production-regression-baseline.md`](../../contracts/production-regression-baseline.md)** | **I-005 回归基线 SSOT** — 94 可实现 + 34 negative-only → `entry_class#method` |
| **[`contracts/consumer-path-baseline.md`](../../contracts/consumer-path-baseline.md)** | **I-006 Boot 消费者路径 SSOT** — 41 Boot-required（A/B/C′）；非 94 行镜像 |
| [`docs/consumer-path-baseline.md`](../consumer-path-baseline.md) | I-006 导航 stub / pointer |
| [`contracts/xugu-dialect.contract.md`](../../contracts/xugu-dialect.contract.md) | 公共接入面 + 能力边界绑定矩阵（§7 + §7.1） |

本用户指南 **只解释状态含义并给链接**，不重复 100+ 行矩阵正文。

## Definition A（一句话）

**Definition A** = MySQL/Oracle Dialect **生产能力面** ∩ **虚谷文档允许** 的能力。超出文档允许的 SQL，实现侧 **不得编造**。

## I-003 ruler C（扩展）

**Definition A 仍然有效。** I-003 在同一 GAV **`7.4.5.Final`** 下用尺子 C 扩展可实现面（C-* 行）：异常映射、JSON 聚合、Window/CTE、bulk mutation、DDL 细节等。详见 I-003 矩阵 SSOT；延后 / 文档不允许行仍不得发明。
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
| A-PAG-001 / A-PAG-002 | 可实现 | HQL/Criteria 分页走 `SqlAstTranslator` → `LIMIT … [OFFSET …]`（非 ANSI FETCH）；带锁时 **FOR UPDATE → LIMIT → WAIT**（见排障 §1–§2） |
| A-PAG-001 / A-LCK-001 | 可实现 | 分页 + `FOR UPDATE` 可用；组合时语法顺序为虚谷要求 |
| A-SEQ-001 | 可实现 | `hbm2ddl validate` 经 `all_sequences` 读取序列元数据（I-002/P-002；见排障 §3） |
| A-LCK-004 SKIP LOCKED | 文档不允许 | `supportsSkipLocked=false`；不会发出该关键字 — 专节 [07-lock-integration.md](07-lock-integration.md) |
| A-LCK-005 FOR SHARE / PESSIMISTIC_READ | 文档不允许 | 无 `FOR SHARE`；`PESSIMISTIC_READ`→排他 **`FOR UPDATE`** — [07-lock-integration.md](07-lock-integration.md) |
| A-TYP-014 INTERVAL 等 | 延后 | 暂勿当作已交付能力 |
| C-EXC-* / C-JSON-001…004 / C-WIN-* / C-CTE-* / C-BULK-001/003 / C-DDL-001…003 / C-CAT-001 / C-GUID-001 | 可实现（I-003） | 见 ruler-C 矩阵 Acceptance hint（✅ + IT 类名） |
| C-BULK-002 bulk insert | **covered-live**（I-007/P-002） | 门控真库 IT PASS — 见 [05-troubleshooting.md §10](05-troubleshooting.md#10-bulk-insertjoined--identity已知限制) |
| C-JSON-005 / A-TYP-015 / C-DDL-005 / A-SEQ-006 | **covered-live**（I-007/P-004） | 见 [`p004-track-c-capabilities.md`](../p004-track-c-capabilities.md) |
| C-DDL-004 ENUM / C-SKIP-001 | 文档不允许 | 不发出 MySQL ENUM / SKIP LOCKED |
| C-JSON-006 / C-SRV-001 / C-SEL-001 等 | 延后 | I-003 首批之外 |

## I-005 baseline counts（冻结）

| Bucket | Count | SSOT status / 说明 |
|---|---:|---|
| 可实现 rows（physical SSOT） | **98** | Charter **94** + **4** I-007 Track C 晋升（`C-JSON-005` 等） |
| **诚实 covered-live（I-008 Q1 已达成）** | **83/98** | **82** 门控 dialect/demo IT + **1** demo-live（`A-XCUT-009`）；SSOT `status=covered-live` = **83** |
| known-limit-documented | **15** | **P-003** Batch A 关闭（thin live IT **4** + waiver **15**） |
| unit-only-without-live | **0** | P-003/P-004 后无未文档化的 unit-only 可实现行 |
| negative-only (文档不允许 + 延后) | 34 | All have `entry_class#method` |
| Demo smoke | 7 | See baseline SSOT demo table |

> **禁止**再写「**94 covered-live**」— 该口径把 CHARTER 标签与 live 证据混为一谈。诚实 rollup：[`production-regression-baseline.md`](../../contracts/production-regression-baseline.md) § Summary；晋升图：[`harness/evidence/architect-contract/I-008/P-001/PROMOTION-MAP.md`](../../harness/evidence/architect-contract/I-008/P-001/PROMOTION-MAP.md)。

验证门控：[03-verify.md § Frozen baseline](03-verify.md#frozen-baseline--i-005-冻结基线门控)。

## I-006 consumer-path counts（冻结）

| Bucket | Count | SSOT status |
|---|---:|---|
| Boot-required (Layer A+B+C′) | 41 | All **covered**；open gaps = **0** |
| Layer A / B / C′ | 13 / 9 / 19 | See consumer-path SSOT |
| Remaining I-005 可实现 → dialect-it-only | 53 | Exclusion appendix — not Boot gaps |

如何跑：[06-consumer-path.md](06-consumer-path.md)。验证门控：[03-verify.md § Consumer-path](03-verify.md#consumer-path-baseline--i-006-消费者路径门控)。

## Cross-links

- Contract §7 Capability scope → Definition A matrix；§7.1 → I-003 ruler C  
- I-005 baseline SSOT → [`production-regression-baseline.md`](../../contracts/production-regression-baseline.md)  
- I-006 consumer-path SSOT → [`consumer-path-baseline.md`](../../contracts/consumer-path-baseline.md)  
- User guide index: [README.md](README.md)  
- Demo 证明 **Boot 消费者路径**（41 行），**不以 demo 覆盖** I-005 全量 94 可实现行
