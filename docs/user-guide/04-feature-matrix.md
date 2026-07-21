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
| **[`contracts/xuguefcore-parity-suite.md`](../../contracts/xuguefcore-parity-suite.md)** | **I-010 B-002** xuguefcore 对照套件 SSOT（10 主题；**P-011** 占位可先链） |
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
| A-TYP-014 INTERVAL | **covered-live**（I-010/P-002；live @5287） | `XuguIntervalJdbcType` + 实体 IT（DURATION/INTERVAL_SECOND）；11 子类型仍 tooling/native |
| A-TYP-016 XML | **covered-live**（I-010/P-003；live @5287） | 推荐 `String`+`@JdbcTypeCode(SQLXML)`；**勿**用 `java.sql.SQLXML` |
| A-TYP-017 Geometric | **covered-live**（I-010/P-004 POINT；live @5287） | 推荐 `String`+`@JdbcTypeCode(POINT\|GEOMETRY)`；非 POINT 子类型 native/tooling |
| A-TYP-018 UDT | **known-limit-documented**（I-009/P-005） | native/`CREATE TYPE` 配方见 [08-schema-tooling-recipes.md](08-schema-tooling-recipes.md)；**无** ORM 实体列映射 |
| A-DDL-008/009 · A-SCH-017 | **known-limit-documented** | SchemaExport **never emits** — Flyway/Support 配方 [08-schema-tooling-recipes.md](08-schema-tooling-recipes.md)（ENCRYPT/PARTITION 仍为真实 KL） |
| A-FUN-021 XML functions | **known-limit-documented**（I-009/P-003；HQL Session I-010/P-005） | HQL `Session` 正例 `xmlelement`/`xmlquery` live PASS；XMLTABLE **单节点** / 空结果 assumption skip — **非** covered-live |
| A-FUN-003/005/006/007/009 Batch A | **covered-live**（I-010/P-008；live @5287） | `XuguBatchAFunctionFamiliesIT` 五族独立 HQL Session — 已晋升 |
| A-FUN-019 / A-FUN-020 | **covered-live**（I-009；HQL Session I-010/P-006/P-007） | regexp / 几何函数 HQL `Session.createQuery` 加深；勿回退 |
| C-EXC-* / C-JSON-001…004 / C-WIN-* / C-CTE-* / C-BULK-001/003 / C-DDL-001…003 / C-CAT-001 / C-GUID-001 | 可实现（I-003） | 见 ruler-C 矩阵 Acceptance hint（✅ + IT 类名） |
| C-BULK-002 bulk insert | **covered-live**（I-007/P-002） | 门控真库 IT PASS — 见 [05-troubleshooting.md §10](05-troubleshooting.md#10-bulk-insertjoined--identity已知限制) |
| C-JSON-005 / A-TYP-015 / C-DDL-005 / A-SEQ-006 | **covered-live**（I-007/P-004） | 见 [`p004-track-c-capabilities.md`](../p004-track-c-capabilities.md) |
| C-DDL-004 ENUM / C-SKIP-001 | 文档不允许 | 不发出 MySQL ENUM / SKIP LOCKED |
| C-JSON-006 | **doc-forbidden negative-only**（I-009/P-010） | 文档无 `json_table` — 禁止发明 SQL |
| C-SRV-001 | **covered-live**（I-009/P-010） | 只读 `SHOW` 会话参数探测 |
| C-SEL-001 | **known-limit-documented**（I-009/P-010） | `DialectResolver` SPI 足够；`XuguDialectSelector` 为内部扩展 |

## I-005 baseline counts（冻结）→ I-010 Accept 当前口径

| Bucket | Count | SSOT status / 说明 |
|---|---:|---|
| 可实现 rows（physical SSOT） | **98** | Charter **94** + **4** I-007 Track C 晋升（`C-JSON-005` 等） |
| **诚实 covered-live（I-010 Accept）** | **91/98** | live @5287 晋升后；先前 I-008 Q1 冻结曾为 **83/98** |
| known-limit-documented（Charter rollup） | **7** | I-010 Accept；先前 I-008/I-009 曾为 **15**（Batch A） |
| unit-only-without-live | **0** | P-003/P-004 后无未文档化的 unit-only 可实现行 |
| negative-only (文档不允许 + explicit defer) | **11** | Doc-forbidden + `@Disabled` defer anchors; open **延后** = **0** |
| Demo smoke | 7 | See baseline SSOT demo table |

> **禁止**再写「**94 covered-live**」或陈旧「**83/98 + 15 KL**」作为**当前**口径。诚实 rollup：[`production-regression-baseline.md`](../../contracts/production-regression-baseline.md) § Summary；I-008 晋升图（历史）：[`docs/archive/i008-promotion/PROMOTION-MAP.md`](../../docs/archive/i008-promotion/PROMOTION-MAP.md)。

验证门控：[03-verify.md § Frozen baseline](03-verify.md#frozen-baseline--i-005-冻结基线门控)。

## I-009 deferred closure（终态）

| Bucket | Count | 说明 |
|---|---:|---|
| P-001 延后库存 | **20** | P-002…P-010 全部关闭 |
| covered-live（延后交付） | **6** | A-FUN-019/020、A-LCK-006、A-IDN-005、A-DDL-007、C-SRV-001 |
| known-limit-documented（延后交付） | **13** | INTERVAL/XML/几何/UDT、**A-FUN-021**、TOP/ROWNUM、catalog/索引、partition/encrypt、bit_and、C-SEL-001 |
| doc-forbidden negative-only | **1** | C-JSON-006 |
| Open matrix **延后** | **0**（产品交付） | Definition A 仅保留 `A-XCUT-012` Ship defer 锚点 |
| Charter **98** rollup（I-009 当时） | **83/98** + **15** | I-009 Accept 不膨胀；**已被 I-010 Accept 更新为 91/98 + 7** |

验证门控：[03-verify.md § I-009 Accept prep](03-verify.md#i-009-accept-prep--延后矩阵全量交付--verify-pass)。当前口径见 [§ I-010](#i-010-quality-completion退出清单)。

## I-010 quality completion（退出清单）

Initiative **I-010** 在 I-009 之上做 ORM/HQL 深度闭环 + 文档配方，**不**膨胀 Charter **98** 诚实计数。

### P0 + P1 exit checklist

| # | Scope 项 | 终态（诚实） |
|---|---|---|
| 1 | P0 SSOT/文档无陈旧「延后」与虚假 Demo gap；A-FUN-021 计数一致 | **勾选** — P-001 SSOT/docs 对齐 |
| 2 | A-TYP-014/016/017 实体 ORM live 或诚实负向 | **勾选（covered-live）** — live @5287 实体 ORM PASS；已晋升 |
| 3 | A-FUN-021 HQL Session；XMLTABLE known-limit | **勾选** — HQL `xmlelement`/`xmlquery` live PASS；**A-FUN-021** 仍 **known-limit-documented**（XMLTABLE） |
| 4 | P1 A-FUN-020/019 HQL；Batch A 五族独立 HQL；tooling 配方 | **勾选** — A-FUN-020/019 **covered-live**；Batch A（A-FUN-003/005/006/007/009）live @5287 PASS → **covered-live**；[08-recipes](08-schema-tooling-recipes.md) |
| 5 | `verify.py` VERIFY PASS；有 DB 时全 reactor live | **勾选** — VERIFY PASS；live @5287 dialect 253/0/0/4 · demo 36/0/0/0 |
| 6 | GAV 7.4.5.Final；NONE；**不 Ship** | **勾选** — 本 Initiative **NOT Ship** |

### I-010 residual（Accept 须诚实列出）

| Residual | SSOT | 说明 |
|---|---|---|
| A-FUN-021 | **known-limit-documented** | XMLTABLE 单节点/集群上限（空结果 assumption skip）；HQL `xmlelement`/`xmlquery` 已 live 绿 |
| A-DDL-008 / A-DDL-009 | **known-limit-documented** | PARTITION / ENCRYPT — SchemaExport 不声称；native/Flyway 配方仍有效 |
| A-TYP-018 · A-FUN-015 · A-PAG-004/006 · A-SCH-003/017 · C-SEL-001 | **known-limit-documented** | 延后库存诚实 KL — **勿吞掉** |
| Ship / tag / push / Central | **OUT** | Human Gate 仅 Initiative Accept；**NOT Ship** |

Charter rollup（I-010 Accept）：**91/98** covered-live + **7** known-limit-documented。

验证门控：[03-verify.md § I-010 Accept prep](03-verify.md#i-010-accept-prep--ormhql-质量完善--verify-pass)。

## I-010 B-002 xuguefcore parity suite

Build **B-002**（**P-011** SSOT + 文档入口；**P-012…P-016** 落地；**P-017** VERIFY）对照 `xuguefcore` 建议用例 **10** 项。全行状态 / planned class / 禁区：**SSOT** [`contracts/xuguefcore-parity-suite.md`](../../contracts/xuguefcore-parity-suite.md)（**P-011**；若尚不存在则为占位链接）。

| Pri | Theme | Unit / IT | Planned anchor（P-011） |
|---|---|---|---|
| 高 | `@Version` 乐观锁陈旧写 → OptimisticLockException | **IT** | `XuguOptimisticConcurrencyIT` |
| 高 | HQL GroupBy / Count 投影物化 | **IT** | `XuguHqlGroupByCountIT` |
| 中 | HQL bulk 支持/拒绝边界（JOINED/SINGLE_TABLE；order by/limit mutation） | **Unit+IT** | `XuguHqlBulkBoundaryTest` + `XuguHqlBulkBoundaryIT` |
| 中 | 大 JSON LOB 物化边界（失败诚实 document） | **IT** | `XuguJsonLobBoundaryIT` |
| 中 | 显式事务原子性 multi-persist | **IT**（dialect 或 Demo） | `XuguExplicitTxAtomicityIT` / Demo 加强 |
| 中 | 方言 SQL 金标 LIMIT / 锁序 / IDENTITY DDL | **Unit** | `XuguNativeSqlBaselineTest` + baseline files |
| 中 | HQL join fetch / 一对多烟测 | **IT** | `XuguHqlJoinFetchIT` |
| 低 | Null 语义 IS NULL / 三值 / coalesce | **IT** | `XuguNullSemanticsIT` |
| 低 | 时间函数投影（文档允许） | **IT** | `XuguTemporalProjectionIT` |
| 低 | 锁超时 / 死锁 live 或加强 Unit | **Unit(+IT)** | strengthen `XuguExceptionConversionTest` / optional live |

门控与离线说明：[03-verify.md § I-010 B-002](03-verify.md#i-010-b-002-xuguefcore-parity-suite)。Parity suite 行**不**计入 Charter **98**；Definition A live 晋升后 rollup **91/98**；**NOT Ship**。

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
