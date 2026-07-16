# Initiative Brief: I-003

> Scoped clarity for one change unit. Not a full product re-charter.
> Type: feature
> Updated: 2026-07-16T17:35:00+08:00

## Goal
- 在**现有 harness 不变**的前提下，按尺子 **C** 补齐方言**生产能力缺口**，使进阶能力对齐完整生产面；GAV 保持 **`com.xugu:xugu-dialect:7.4.5.Final`**。

## Completeness ruler (locked)
**C = A + B**
- **A：** Hibernate **7.4.5** `MySQLDialect` 公开 override / 生产能力面 ∩ `E:\Work\docs\content` 虚谷文档允许项（只对照结构/API，**不继承** MySQL/Oracle Dialect）
- **B：** 对 `E:\Work\java\hibernate-dialect` 做**只读**差集盘点（**禁止移植/复制实现代码**）

## In-scope（首批全做）
1. Gap inventory SSOT（Plan P-001）：尺子 C 差集 → 可验收矩阵/契约增量
2. 异常映射：`SQLExceptionConversionDelegate` + constraint name extractor + **ORM 入口 IT**
3. JSON 深能力 + `AggregateSupport`（含 arrayagg/objectagg 等矩阵确认项）+ **ORM 入口 IT**
4. Window / WITH(CTE) 能力开关与渲染（文档允许范围内）+ **ORM 入口 IT**
5. Bulk mutation 回退策略（临时表等）+ **ORM 入口 IT**
6. 类型/DDL 细节缺口（ENUM、IF EXISTS、ALTER column type、datetime format/literals 等，以 P-001 清单为准）+ **ORM 入口 IT**
7. 矩阵/用户指南对齐 + 全量 VERIFY PASS + Initiative Accept 准备（**不含 Ship**；**不改** harness 框架 agents/skills/verify 契约）

## Evidence rule (product, not harness rewrite)
- 凡标「可实现」的能力，验收必须含 **应用入口 IT**（HQL/Session/Criteria/hbm2ddl 等消费者路径），禁止仅 SPI/unit 伪闭环
- 继续使用现有 `verify.py` / `mvn test` / 门控 `-Dxugu.run.integration=true`；框架硬化另线程

## Out-of-scope
- 修改 harness 框架语义（agents/skills、verification.json Accept 分档、矩阵模板引擎等）— **其他线程**
- 移植/复制 `hibernate-dialect` 或旧方言源码
- 继承 `MySQLDialect` / `OracleDialect`
- 改写 `E:\Work\docs\content`
- 升版本号；Ship / tag / push / Maven Central
- 空间/几何等尺子 C 明确「文档不允许 / 延后」项（除非 P-001 升格）

## Acceptance criteria
- [x] P-001 产出尺子 C 差集 SSOT，并锁定后续 Phase 实现清单
- [x] P-002…P-006 各能力有实现 + **ORM 入口 IT**（门控真库）证据
- [x] P-007 矩阵/用户指南与行为对齐；`verify.py` **VERIFY PASS**
- [x] 版本仍为 **7.4.5.Final**（无 bump）；工作分支 must-commit SHA 可审
- [ ] **不要求**本 Initiative 完成 Central / Ship（Ship 仍 out of scope — 保持未勾选）

## Decisions (Human Gate 2026-07-16)
1. Archive I-002 — **确认**
2. Type **feature** — **确认**
3. 尺子 **C** — **确认**
4. 首批全做（异常 / JSON+Aggregate / Window+CTE / bulk / 类型 DDL 细节）— **确认**
5. 要入口 IT — **确认**
6. 版本保持 **7.4.5.Final** — **确认**
7. Scope PASS：「本 Initiative 范围已明确，可以开干」
8. **不**在本 Initiative 修改 harness 框架 — **确认**

## Related
- Branch: `feat/i-003-production-capability-parity`
- Matrix SSOT: `contracts/feature-matrix-i003-ruler-c.md` (**CONFIRMED / Accept-ready**)
- Ship: **out of this Initiative**

## Status
`active` — **P-007 accepted**; awaiting Human Gate **Initiative Accept** (no Ship)

- Scope PASS: ~2026-07-16T10:46+08:00
- P-001…P-007: all accepted
- Next: Human Gate Initiative Accept → optional Archive; **Ship separate**
