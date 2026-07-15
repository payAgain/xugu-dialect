# Harness 缺口分析报告：I-001「全绿」与独立实库复测「有条件通过」

| 项 | 内容 |
|----|------|
| 报告目的 | **完善 harness 框架**（非仅修方言 bug） |
| 触发证据 | `E:\Work\java\xugu-hibernate-test\TEST-REPORT.md`（2026-07-15，20/22） |
| 对照交付 | I-001 major：`com.xugu:xugu-dialect:7.4.5.Final`，矩阵 78/78，VERIFY PASS，已 Accept/Archive（Ship 未授权） |
| 分析对象 | hibernate-test 仓库内 harness 流程、契约、验证与角色规则 |
| 日期 | 2026-07-15 |
| 结论等级 | **框架语义缺口（P0）** — 流程合规，验收语义不足 |

---

## 1. 执行摘要

独立工程对已 Accept 的方言 jar 做黑盒实库复测后，暴露两类生产路径失败：

1. **HQL/Criteria 分页** → 生成 ANSI `OFFSET … FETCH FIRST`，虚谷拒绝（`[E19132]`）
2. **`hbm2ddl.auto=validate`** → 抽不到序列（`missing sequence`）

这两项在 I-001 期间**从未被 harness 定义为必须证明的用户入口路径**，因此：

- Phase / Initiative Accept、矩阵 78/78、VERIFY PASS **全部可以合法成立**；
- 同时，**典型应用仍不可用分页，且无法用 validate 管 schema**。

**核心判断：** 问题主因不是「AI 没跑 harness」或「没写测试」，而是 harness 把「能力闭环」绑定在 **Dialect 内部 SPI / 拼装层证据**上，缺少对 **库类产品的应用入口路径（consumer entrypoint）** 的强制验收。完善框架应优先改验收语义与证据规则，其次才是补具体测试用例。

---

## 2. 前因后果（时间线）

```text
[前因] Charter / 定义 A
  能力按「Hibernate Dialect surface」拆行（LimitHandler、SequenceSupport…）
  生产就绪要求「observed affected flow」，但未落到可执行的入口清单
        ↓
[过程] I-001 Plan → Build（P-001…P-011）
  P-004：实现 XuguLimitHandler；IT 直接 processSql + JDBC
  P-005：序列 CREATE/NEXTVAL；无 validate / 元数据抽取
  P-007：schema create/drop/export；无 hbm2ddl=validate
  Reviewer / test：按矩阵与 Packet 验收 LimitHandler 层 → PASS
  verify.py：required = mvn package + mvn test（默认 IT gate OFF）
        ↓
[结果] I-001 Accept / Archive
  78/78 可实现闭环 + VERIFY PASS + must-commit
  文档/用户指南宣称分页与序列能力可用（基于矩阵语义）
        ↓
[暴露] 独立工程 xugu-hibernate-test（未复用本仓单测）
  按应用习惯测：HQL setFirstResult/setMaxResults、hbm2ddl=validate
  20/22 → 有条件通过；确认 P0 分页 + P1 validate
        ↓
[后果] 信任裂缝
  「harness 全绿」≠「消费者可上线」
  需 I-002 hotfix 补实现与本仓入口层 IT
  更关键：harness 若不改规则，同类缺口会在下一版 ORM/下一能力域重演
```

### 2.1 前因（框架如何「允许」漏测）

| 前因要素 | 当时状态 | 为何埋雷 |
|----------|----------|----------|
| 能力矩阵行写法 | 写 `LimitHandler` / `supportsLimitOffset`，不写「HQL Pageable」 | AI/角色按字面实现与举证，合法绕过应用入口 |
| A-PAG-005「不得 FETCH FIRST」 | 只在 LimitHandler 输出上断言 | 形成**假阴性**：HQL AST 路径仍发 OFFSET/FETCH |
| 序列矩阵 A-SEQ-* | 覆盖 DDL/NEXTVAL，无 metadata / validate | validate 不在任何 Phase 的 DoD 内 |
| 测试角色规则 | `agents/test.md` 偏「跑命令留证据」 | 无「测用户调用层」硬约束 |
| VERIFY 契约 | `mvn -q test` required；实库 IT 靠 `-Dxugu.run.integration` | Accept 后日常 VERIFY 可不跑 live；且即使 live，P-004 IT 仍测错层 |
| Accept 叙事 | 「矩阵闭环 + VERIFY PASS」即可 Initiative Accept | 缺少库产品「黑盒/金路径」门槛 |
| 学习闭环 | `docs/error-journal.md` 空 | 历史审计坑未编码进 checklist |

### 2.2 直接技术后果（供框架映射，非本报告主修对象）

| 失败项 | 技术根因（简述） | 框架映射 |
|--------|------------------|----------|
| HQL 分页 | Hibernate 7 SQM 走 SqlAstTranslator；仅有 LimitHandler 不够 | **入口路径 ≠ SPI 表面** 未强制 |
| Schema validate | `getQuerySequencesString()` 未覆盖 → NoOp 抽取器 | **能力矩阵覆盖不全** + 无 validate 场景 |

### 2.3 间接产品后果

- 对外可产生「已 Accept / 矩阵全绿」的信心，与独立复测「有条件通过」冲突。
- 分页失败影响面大（几乎所有列表/Pageable）；validate 失败影响 CI/schema 治理。
- Ship 虽未做，但若按现有 Accept 标准直接授权 Ship，会把同一语义缺口带进发布物。

---

## 3. 框架视角的根因分层

站在「完善 harness」而非「修方言」的角度，根因分四层：

### 3.1 契约层：能力定义错层（P0）

定义 A / 契约把「分页」定义为 Dialect 的 LimitHandler 行为，而消费者感知的分页是：

`Session/EntityManager` → HQL/Criteria → `setFirstResult` / `setMaxResults`（及 Spring Data `Pageable`）

**框架缺口：** 矩阵行缺少强制字段 **App entrypoint / 禁止的伪证据**。  
允许「只测 SPI」被记为「能力可实现」。

### 3.2 验证层：证据可自洽但不可外推（P0）

本仓 `XuguPaginationIT` 的模式是：

1. `dialect.getLimitHandler()`
2. `handler.processSql(baseSql, limit)`
3. JDBC 执行

这能证明「LimitHandler 字符串与虚谷语法兼容」，**不能**证明「Hibernate 查询引擎会调用该 Handler」。  
Reviewer 与 test 角色按 Packet 验收该证据 → PASS，系统层面无冲突检测。

**框架缺口：** 无规则区分：

| 证据类型 | 可证明什么 | 能否单独 Accept「应用能力」 |
|----------|------------|------------------------------|
| SPI/单元拼装 | Dialect API 行为 | **否**（库产品） |
| ORM 入口 IT | 用户真实调用链 | **是（最低门槛）** |
| 独立消费者套件 | 发布物黑盒 | 强烈建议（Ship / major Accept） |

### 3.3 门禁层：VERIFY PASS 过宽（P1）

`harness/verification.json` 仅强制离线 `mvn test`。实库 IT 默认 skip。  
即使强制 gate ON，若用例本身测错层，VERIFY 仍会绿。

**框架缺口：**

- VERIFY 不表达「入口路径覆盖率」；
- 无「库类产品」专用检查档位（金路径 / 消费者复测）；
- `production-readiness.md` 要求 observed flow，但未与 VERIFY/Accept checklist 机械绑定。

### 3.4 学习层：失败不回流框架（P2）

独立复测暴露的是可复用的 **ORM 方言陷阱**（Hibernate 6/7 AST 分页、序列元数据与 validate）。  
若只开 hotfix 修代码、不改 skills/agents/矩阵模板，下一 Initiative 仍可能：

- 实现「正确的 LimitHandler」；
- 用「正确的 processSql IT」Accept；
- 再次被黑盒工程打脸。

**框架缺口：** error-journal、reviewer checklist、architect 矩阵模板未形成强制回流。

---

## 4. 本次案例说明「流程成功 ≠ 语义成功」

| 检查项 | I-001 结果 | 是否足以保护消费者 |
|--------|------------|--------------------|
| Clarify / Scope / Plan / Build 仪式 | 完整 | 仪式不解决测错层 |
| Phase role_pipeline + evidence | 完整 | 证据类型错误仍可 PASS |
| Reviewer（含 P-004 request-changes） | 有效发现锁顺序问题 | **未要求 ORM 分页路径** |
| 矩阵 78/78 | 闭环 | 闭环定义偏 SPI |
| VERIFY PASS | 是 | 命令门过宽 + 用例层偏 |
| 独立黑盒复测 | 未纳入 Accept | 事后才发现 P0/P1 |

结论：harness **执行纪律有效**；缺的是 **库产品验收本体论**（什么叫「做完」）。

---

## 5. Harness 完善建议（按优先级）

### 5.1 P0 — 矩阵 / 契约：入口路径强制字段

对「可实现」行增加（建议写入框架模板与 `architect-contract` 规则）：

| 字段 | 要求 |
|------|------|
| `app_entrypoint` | 消费者如何触发（例：`Query#setFirstResult`、`hbm2ddl=validate`） |
| `min_evidence` | 至少一种：`orm-it` / `consumer-it`；禁止仅 `spi-unit` |
| `forbidden_pseudo_evidence` | 例：分页禁止仅 `LimitHandler.processSql` |
| `explicit_non_support` | 若不做某入口，必须标「文档不允许/延后」并写入用户指南风险 |

**对本案的回溯映射（示例）：**

| 能力 | 应有的 app_entrypoint | I-001 实际证据 | 应有裁决 |
|------|----------------------|----------------|----------|
| 分页 | HQL/Criteria 分页 | LimitHandler JDBC | **不得**标 ✅ 可实现 |
| 序列生产使用 | NEXTVAL +（若宣称 schema 管理）validate | 仅 CREATE/NEXTVAL | validate 须单独行或显式延后 |

### 5.2 P0 — 角色规则：test / reviewer「测错层 = MAJOR」

建议写入 `agents/test.md`、`agents/reviewer.md`、`skills/review.md`：

1. 库/中间件类变更：Accept 前必须有 **与 `app_entrypoint` 同层** 的失败可红用例。
2. Reviewer 对「仅 SPI 证据支持应用能力声明」一律 **request-changes（MAJOR）**。
3. 版本敏感陷阱清单（Dialect cookbook）纳入 reviewer 必读：  
   - Hibernate 6/7：HQL 分页 → SqlAstTranslator  
   - SEQUENCE：validate 需要 sequences metadata query/extractor  

### 5.3 P0 — Accept / Ship 分档（库产品）

| 门 | 最低证据 |
|----|----------|
| Phase Accept | 本仓 ORM 入口 IT（gate ON 或等价实库）覆盖本 Phase 的 `app_entrypoint` |
| Initiative Accept（major/feature） | 金路径套件绿；矩阵「可实现」无伪证据 |
| Ship | 建议强制：独立消费者工程或等价黑盒复测相关用例 PASS |

当前项目已把 Ship 与 Accept 分离（正确）；应进一步规定 **Accept 也不能只靠 SPI 闭环**。

### 5.4 P1 — VERIFY 契约升级

可选策略（需 Human Gate 选型后改 `verification.json` / `docs/verification.md`）：

1. **条件 required：** 存在 `xugu.run.integration` 环境时，跑 gate ON IT；或  
2. **Accept 专用 profile：** `verify.py --profile accept-library` 强制金路径模块；日常 `verify` 可保持轻量；或  
3. **证据绑定：** Packet `verification_evidence` 必须引用「入口 IT」日志，禁止只用离线 unit 日志关闭应用能力行。

注意：仅把 `mvn test -Dxugu.run.integration=true` 设为 required **不够**——必须同时改用例层，否则仍会绿。

### 5.5 P1 — 生产就绪与矩阵机械绑定

`docs/production-readiness.md` 中 Functional correctness / Compatibility 已为 required，但未生成可勾选的入口清单。建议：

- Bootstrap / Plan 时从矩阵导出 `entrypoint-checklist.md`；
- Accept 时逐条勾选并链到 IT 类名；
- 未勾选不得标矩阵 ✅。

### 5.6 P2 — 学习闭环强制化

每次独立复测或生产事故：

1. 写入 `docs/error-journal.md`（症状 / 根因 / 防再发 / 关联 Phase）；
2. 同步改 agents checklist 或框架 skill（否则不算闭环）；
3. 若属定义漏洞，开 **harness 改进 Initiative**（与业务 hotfix 分离，避免只修代码）。

### 5.7 建议的框架 Initiative 拆分

| 工作 | 建议归属 | 说明 |
|------|----------|------|
| SqlAstTranslator + sequences metadata + 本仓入口 IT | **I-002 hotfix**（业务） | 修当下漏洞 |
| 矩阵字段、agents/skills、VERIFY 分档、error-journal 模板、Accept/Ship 证据规则 | **独立 harness Initiative** | 防止重演；可与 I-002 并行规划，但勿混进「只修方言」的 Accept 叙事 |

---

## 6. 框架已做对的部分（避免矫枉过正）

完善 harness 时保留这些有效设计：

1. **Human Gate / 多角色 / must-commit / Ship 分离** — 纪律层有效；P-004 reviewer 曾抓住锁与 LIMIT 顺序问题。
2. **矩阵 + Phase 边界** — 有利于增量交付；问题在行语义，不在拆 Phase 本身。
3. **实库 IT gate** — 方向正确；缺的是「测什么」，不是「要不要实库」。
4. **独立复测工程** — 已证明其价值；应升级为 Ship/major 的可选或强制证据源，而不是事后偶然。

不要通过「更多仪式、更多 Phase」掩盖语义问题。

---

## 7. 对完善 harness 的一句话原则

> **对库/方言类产品：矩阵「可实现」必须绑定消费者入口路径的可红证据；SPI 层证据只能证明 SPI，不能关闭应用能力行。VERIFY PASS 证明命令门，不证明入口覆盖。Accept 叙事必须区分「流程合规」与「语义完备」。**

---

## 8. 附录：证据锚点

| 锚点 | 路径 |
|------|------|
| 独立复测报告 | `E:\Work\java\xugu-hibernate-test\TEST-REPORT.md` |
| 审计补充 | `E:\Work\java\xugu-hibernate-test\DIALECT_AUDIT_REPORT.md` |
| 测错层 IT | `dialect/src/test/java/com/xugu/dialect/it/XuguPaginationIT.java` |
| 分页矩阵行 | `contracts/feature-matrix-definition-a.md`（A-PAG-*） |
| I-001 Accept | `harness/evidence/orchestrator/I-001/ACCEPTANCE.md` |
| VERIFY 契约 | `harness/verification.json` |
| 生产就绪 | `docs/production-readiness.md` |
| 后续业务修复 | Initiative I-002（hotfix） |

---

## 9. 建议的 Human Gate 下一步（框架向）

1. 确认本报告作为 **harness 改进需求基线**（可归档进 docs / error-journal）。
2. 选型 VERIFY 升级策略（§5.4）。
3. 开独立 Initiative（或明确并入某次 Bootstrap 修订）：落地 §5.1–§5.3 的模板与 agents 规则。
4. I-002 继续修方言与本仓入口 IT（业务），但 **不以 I-002 Accept 替代框架规则落地**。
