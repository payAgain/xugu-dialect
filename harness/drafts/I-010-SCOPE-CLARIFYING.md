# I-010 Scope — 质量完善（ORM/HQL 深度 + SSOT/文档对齐）

> Status: **scope-ready**（由质量缺口分析 + Human Gate「继续完善」自动收窄）  
> Updated: 2026-07-21T14:40:00+08:00  
> Prior analysis: agent-transcript quality-gap explore（NONE 生产方言质量口径）

## 与 I-009 关系
- I-009（延后矩阵全量）**Initiative Accept** 已落盘；**Archive** 随本 Initiative 开启
- I-009 交付层已钉完（gap=0、延后 20/20）；本 Initiative 补 **质量深度**，不是重开延后库存
- **不** Ship / Central；**不**做文档不允许 / 虚谷不支持能力

## 目标（一句话）
在 NONE + Hibernate 7.4.5.Final 下，把「native-only / 标签漂移 / 虚假 gap」收成 **实体 ORM 或 HQL Session 真库证据**（或诚实负向），并统一 SSOT/用户文档口径，使可宣称 **质量口径完成**。

## In-scope（P0 → P1）

### P0 — SSOT / 文档（可先）
| 项 | 验收 |
|---|---|
| Definition A 矩阵 Status | 已封交付行不再标「延后」；与 baseline 终态一致 |
| A-FUN-021 / I-009 计数 | 与 `contracts/production-regression-baseline.md` 一致（含 XMLTABLE known-limit） |
| Demo smoke 表 | 去掉 I-007 已覆盖的 validate / Function-HQL / Bulk 虚假 gap |
| `docs/user-guide/05-troubleshooting.md` | 补 INTERVAL / XML 列 / 几何 / UDT / PARTITION / catalog / ENCRYPT 边界 |

### P0 — ORM / HQL 深度
| ID | 验收 |
|---|---|
| A-TYP-014 | 实体 Duration / INTERVAL_SECOND 真库 round-trip **或** 诚实负向 + 原因 |
| A-TYP-016 | 实体 `@JdbcTypeCode(SQLXML)` 真库 round-trip **或** 诚实负向 + 推荐映射 |
| A-TYP-017 | POINT 实体 persist/load 真库 IT |
| A-FUN-021 | HQL Session live：`xmlelement` / `xmlquery`；XMLTABLE SSOT known-limit 一致 |

### P1 — 加深（P0 后串行）
| ID / 项 | 验收 |
|---|---|
| A-FUN-020 | 2–3 个几何函数 HQL Session live（不仅 native） |
| A-FUN-019 | regexp_* HQL Session live |
| A-FUN-003/005/006/007/009 | 各族独立 HQL live（结束 thin adjacent-live） |
| Schema tooling 文档 | PARTITION / ENCRYPT / 高级索引配方（**不**强制 SchemaExport emit） |

## Out-of-scope（明确排除）
- SKIP LOCKED、FOR SHARE、ANSI FETCH、ENUM、json_table、READ UNCOMMITTED
- 多兼容模式产品线；Ship / tag / push / Maven Central
- 故意 LIMIT-only ORM 分页（TOP/ROWNUM helper 保持）
- UDT 全量 ORM 实体列（无 SqlTypes → 文档钉死即可）
- 非 POINT 几何子类型 ORM；VARBIT 全量 UserType（非本 Initiative 必做）
- ENCRYPT / XMLTABLE **环境拓扑**（权限 / 单节点）当作「方言必须实现」

## 锁定决策（提案，随 Scope PASS 生效）
| # | Decision |
|---|---|
| 0 | **Archive I-009**（不含 Ship） |
| 1 | Type **feature** |
| 2 | 深度：**P0 全做 + P1 全做**（串行 Plan） |
| 3 | GAV **不升版** `7.4.5.Final`；`compatiblemode=NONE` |
| 4 | **不 Ship** |
| 5 | 文档不允许 / 平台上限 **继续 skip / known-limit**；禁止发明 SQL |

## 验收（Initiative 级）
- [ ] P0 SSOT/文档无陈旧「延后」与虚假 Demo gap；A-FUN-021 计数一致
- [ ] A-TYP-014/016/017 实体 ORM live 或诚实负向
- [ ] A-FUN-021 HQL Session live；XMLTABLE known-limit 一致
- [ ] P1：A-FUN-020/019 + Batch A 五族独立 HQL live；tooling 文档配方
- [ ] `python harness/scripts/verify.py` **VERIFY PASS**；有 DB 时 `XUGU_RUN_IT=true` 绿
- [ ] GAV 7.4.5.Final；NONE；不 Ship

## Human Gate 口令
1. Scope：**「本 Initiative 范围已明确，可以开干」**（或确认上表决策）  
2. Build（Plan 已物化后）：**「批准 B-001，范围 P-001~P-010」**（或仅最早 Phase）
