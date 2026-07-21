# Initiative Brief: I-009

> Type: **feature**  
> Updated: 2026-07-21T09:45:00+08:00

## Goal
交付特性矩阵中全部 **「延后」** 且虚谷文档允许的能力（类型/函数/DDL/分页备选/锁/JSON/配置等）；**「文档不允许」** 继续 skip/负向；GAV **7.4.5.Final**；**不 Ship**（含 A-XCUT-012）。

## Human Gate Scope PASS
「1 推荐；2 推荐；3 全做；4 不升版、不 Ship；本 Initiative 范围已明确，可以开干」(~2026-07-21T09:30+08:00)

| # | Decision |
|---|---|
| 0 | **Accept + Archive I-008**（不含 Ship） |
| 1 | Type **feature** |
| 2 | 延后清单 **全做** |
| 3 | GAV **不升版** `7.4.5.Final` |
| 4 | **不 Ship** |
| 5 | 文档不允许 **继续跳过**；禁止发明 SQL |

## In-scope（延后全量）
见 `harness/drafts/I-009-SCOPE-CLARIFYING.md`：INTERVAL / XML(+函数) / 空间(+函数) / UDT / regexp / bit_* / 分区 / ENCRYPT / IF NOT EXISTS / catalog / 高级索引 / LOCK TABLE / TOP / ROWNUM / IDENTITY_MODE / json_table / ServerConfig / DialectSelector 等。

## Out-of-scope
- 文档不允许行（SKIP LOCKED、FOR SHARE、ANSI FETCH、RU、temp FK、继承 MySQL/Oracle Dialect 等）
- Ship / Maven Central（A-XCUT-012）
- 多兼容模式产品线

## Acceptance criteria
- [ ] 延后行 → 可实现 + covered-live（或诚实 known-limit）
- [ ] 负向基线仅保留文档不允许锚点
- [ ] 真库 `XUGU_RUN_IT` 绿；VERIFY PASS
- [ ] GAV 7.4.5.Final；NONE；不 Ship

## Status
`active` — Plan complete (P-001…P-011)；awaiting Build approval

## Branch
`feat/i-009-deferred-matrix-delivery`（orchestrator 创建）
