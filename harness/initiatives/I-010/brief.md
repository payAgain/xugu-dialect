# Initiative Brief: I-010

> Type: **feature**  
> Updated: 2026-07-21T17:10:00+08:00

## Goal
质量完善：ORM/HQL 深度闭环（INTERVAL/XML/POINT 实体 + XML/几何/regexp/Batch A HQL live）+ SSOT/用户文档口径对齐；**外加**对照 xuguefcore 的高/中/低建议用例全量落地（B-002）；GAV **7.4.5.Final**；**不 Ship**；不做文档不允许能力。

## Human Gate Scope
来源：质量缺口分析 +「继续完善」+ **2026-07-21 xuguefcore 对照全量补充批准**。  
完整 Scope：`harness/drafts/I-010-SCOPE-CLARIFYING.md`。

| # | Decision |
|---|---|
| 0 | **Archive I-009**（不含 Ship） |
| 1 | Type **feature** |
| 2 | **P0 + P1 全做**（B-001）+ **xuguefcore 对照 10 项全做**（B-002） |
| 3 | GAV **不升版** `7.4.5.Final` |
| 4 | **不 Ship** |
| 5 | 文档不允许 / 平台上限继续 skip；禁止发明 SQL |
| 6 | **不新开 Initiative** — 全部补充进 I-010 |

## In-scope
### B-001（complete）
- P0 SSOT / ORM INTERVAL·XML·POINT / A-FUN-021 HQL；P1 几何/regexp/Batch A/schema recipes

### B-002（active）
| Pri | Theme | Layer |
|---|---|---|
| 高 | `@Version` 乐观锁陈旧写失败 | Integration |
| 高 | HQL GroupBy / Count 投影物化 | Integration |
| 中 | HQL bulk 支持/拒绝边界矩阵 | Unit + Integration |
| 中 | 大 JSON 列 LOB 物化边界 | Integration |
| 中 | ORM 显式事务原子性 | Integration / Demo |
| 中 | 方言 SQL 金标（LIMIT/锁序/IDENTITY） | Unit |
| 中 | HQL `join fetch` / 一对多烟测 | Integration |
| 低 | Null 语义小子集 | Integration |
| 低 | 时间函数投影物化（文档允许） | Integration |
| 低 | 锁超时/死锁 live 或加强 Unit | Unit (+ optional IT) |

SSOT：`contracts/xuguefcore-parity-suite.md`（P-011）

## Out-of-scope
- SKIP LOCKED / FOR SHARE / ANSI FETCH / ENUM / json_table / 多兼容模式
- EF Spec 8500、Migrations、Retry API、RETURNING/ADO 探针、EF Owned API 照搬
- Ship / tag / push / Central

## Acceptance criteria
- [x] B-001 质量口径退出清单 P0+P1（VERIFY PASS；live @5287 PASS）
- [x] B-002 10 项用例落地（XP covered-live×8 + covered-unit×2）
- [x] VERIFY PASS；真库证据 `harness/evidence/test/I-010/live-it-5287/`
- [x] 不 Ship

## Status
`accepted` — Initiative Accept（2026-07-21）；live @5287 dialect **253/0/0/4** · demo **36/0/0/0**；Charter **91/98** + **7** known-limit；**NOT Ship** · **NOT Archive**

## Branch
`feat/i-010-orm-hql-quality-completion`
