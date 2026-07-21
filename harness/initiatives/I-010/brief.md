# Initiative Brief: I-010

> Type: **feature**  
> Updated: 2026-07-21T14:40:00+08:00

## Goal
质量完善：ORM/HQL 深度闭环（INTERVAL/XML/POINT 实体 + XML/几何/regexp/Batch A HQL live）+ SSOT/用户文档口径对齐；GAV **7.4.5.Final**；**不 Ship**；不做文档不允许能力。

## Human Gate Scope（提案 / 自动收窄）
来源：质量缺口分析 +「继续完善」。完整 Scope：`harness/drafts/I-010-SCOPE-CLARIFYING.md`。

| # | Decision |
|---|---|
| 0 | **Archive I-009**（不含 Ship） |
| 1 | Type **feature** |
| 2 | **P0 + P1 全做**（见 Scope） |
| 3 | GAV **不升版** `7.4.5.Final` |
| 4 | **不 Ship** |
| 5 | 文档不允许 / 平台上限继续 skip；禁止发明 SQL |

**待确认口令：**「本 Initiative 范围已明确，可以开干」

## In-scope
- P0 SSOT：Definition A 去陈旧「延后」；A-FUN-021/I-009 计数对齐 baseline；Demo smoke 去虚假 gap；`05-troubleshooting` 深类型/工具边界
- P0 ORM：A-TYP-014/016/017 实体 live 或诚实负向；A-FUN-021 HQL Session live
- P1：A-FUN-020/019 HQL；Batch A 五族独立 HQL；schema tooling 文档配方

## Out-of-scope
- SKIP LOCKED / FOR SHARE / ANSI FETCH / ENUM / json_table / 多兼容模式
- Ship / tag / push / Central
- TOP/ROWNUM ORM 分页改默认；UDT 全量 ORM；env ENCRYPT/XMLTABLE 拓扑「必须实现」

## Acceptance criteria
- [ ] 质量口径退出清单 P0+P1 全部勾选（见 Scope）
- [ ] VERIFY PASS；真库证据或诚实 SKIPPED_INFRA
- [ ] 不 Ship

## Status
`active` — B-001 P-001…P-010 **complete**；VERIFY PASS；await **Initiative Accept**；**NOT Ship** · **NOT Archive**

## Branch
`feat/i-010-orm-hql-quality-completion`
