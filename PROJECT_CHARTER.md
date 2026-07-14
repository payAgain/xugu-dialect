# PROJECT CHARTER

> **Status:** APPROVED  
> **Promoted from:** `harness/drafts/PROJECT_CHARTER.md`  
> **Approved by Human Gate:** 「批准 Round A Charter，可以 Bootstrap 批准 ADR-0001」  
> **Approved / promoted:** 2026-07-14  
> **Author role (draft):** architect-contract  
> **Promoter role:** orchestrator  
> **Source of truth (intent):** `harness/drafts/INTENT-CLARITY.md` (`Intent Clarity: PASS`)  
> **Harness level:** Standard  
> **This file is the root SSOT** for product charter. Draft retained for audit trail only.

---

## 1. Product name & mission

| Field | Value |
|---|---|
| **Product name** | XuguDB Hibernate Dialect (`xugu-dialect`) |
| **Working repo** | `hibernate-test` (`E:\Work\java\hibernate-test`) |
| **Mission** | 在本仓库从零交付可生产使用的虚谷数据库（XuguDB）Hibernate 方言制品：可发布的 dialect jar、可运行的 Spring Boot demo、以及项目内生产级使用/验证文档；使应用能通过显式 dialect 或 DialectResolver SPI 自动识别接入 XuguDB。 |

---

## 2. In scope

- 本仓库从零实现 Hibernate 方言模块（不得参考/移植旁路仓或旧方言实现）。
- 架构结构**对照** Hibernate 自带 MySQL / Oracle Dialect 的模块拆分方式（包分层、能力面组织）；**禁止**继承 `MySQLDialect` / `OracleDialect`。
- SQL / 语法能力以 `E:\Work\docs\content` 为真相源；项目内文档描述如何使用本方言，**不改写**官方 content。
- 技术基线：Hibernate **7.4.5.Final**，JDK **17**，JDBC 驱动仓库根 `xugu-jdbc-12.3.6.jar`。
- Maven 制品：`com.xugu:xugu-dialect:7.4.5.Final`（版本号与所适配 Hibernate 对齐）。
- Java 包：`com.xugu.dialect`；主类：`com.xugu.dialect.XuguDialect`。
- DialectResolver SPI **自动识别** XuguDB；同时支持显式 `hibernate.dialect=com.xugu.dialect.XuguDialect`。
- 功能覆盖 **定义 A**：MySQL/Oracle Dialect **生产级能力面** ∩ 虚谷文档允许实现的能力；Plan 阶段产出可验收特性矩阵。
- 集成测试必须连接**真实** XuguDB（非纯 mock 替代）。
- 交付 Spring Boot demo 模块，演示核心能力（连接真实库）。
- 项目内生产文档（路径在 Bootstrap / 架构阶段落定，建议 `docs/` 或模块内 `dialect-docs/`）。
- 默认连接侧 `compatible_mode=NONE`；密钥与连接参数优先环境变量覆盖。
- 驾驭架路径：Clarify PASS → Charter（本文件）→ Bootstrap →（之后）Scope / Plan / Build / Accept / Ship。

---

## 3. Out of scope

- 参考、阅读实现、移植或对照编码 `E:\Work\java\hibernate-dialect` 与任何旧 `xugu-dialect` 源码。
- 继承或「伪装为」MySQL/Oracle Dialect 的实现路径。
- Hibernate **6.x** 线、Hibernate **8** beta 适配。
- 改写 `E:\Work\docs\content`（含 `ecosystem/orm/java/hibernate.md` 等官方文档树）。
- Maven Central 账号、签名、发布凭证与发布流水线细节（属 **Ship** 门禁，Accept 后再办）。
- 空间/几何等超出「定义 A」交叉清单之外的扩展（除非 Plan 特性矩阵与文档交叉后明确纳入）。
- 首次 init 阶段询问或锁定 Initiative 类型（hotfix|feature|major）——那是 Bootstrap 之后的 Scope。

---

## 4. Tech baseline (locked)

| Item | Decision |
|---|---|
| Hibernate | **7.4.5.Final** |
| JDK | **17** |
| JDBC | 仓库根 **`xugu-jdbc-12.3.6.jar`** |
| Maven GAV | **`com.xugu:xugu-dialect:7.4.5.Final`** |
| Package | **`com.xugu.dialect`** |
| Main class | **`com.xugu.dialect.XuguDialect`** |
| Auto-detect | DialectResolver SPI |
| compatible_mode (default) | **NONE** |
| SQL truth | `E:\Work\docs\content` |
| Architecture reference | Hibernate MySQL/Oracle Dialect **structure only** |
| Implementation reference | **FORBIDDEN**: `hibernate-dialect` / old xugu-dialect |
| Inheritance | **DO NOT** extend `MySQLDialect` / `OracleDialect` |
| Spring Boot | Demo 模块；与 Hibernate 7.4.5 对齐的 BOM/版本在 Bootstrap/Plan 锁定精确号 |
| Harness | Standard |

### Connection reference (local / non-secret defaults)

| Param | Reference value | Notes |
|---|---|---|
| host | `127.0.0.1` | Prefer env override |
| port | `5138` | Prefer env override |
| ssl | `nssl` | Prefer env override |
| database | `SYSTEM` | Prefer env override |
| user | `SYSDBA` | Prefer env override |
| password | `SYSDBA` | **本地参考 only**；CI/共享环境必须用环境变量；勿提交生产密钥 |
| URL shape | `jdbc:xugu://127.0.0.1:5138/SYSTEM?...` | 精确参数以驱动文档为准 |

---

## 5. Module boundaries

| Module / area | Responsibility | Depends on | Must not |
|---|---|---|---|
| **dialect** (`com.xugu:xugu-dialect`) | `XuguDialect`、类型/函数/序列/Limit 等方言能力、DialectResolver SPI、单元/集成测试 | Hibernate 7.4.5 API、Xugu JDBC | 依赖 demo；继承 MySQL/Oracle Dialect；引用旁路仓实现 |
| **demo-spring-boot** | 可运行的 Spring Boot 示例应用，验证真实库接入与核心场景 | dialect 制品、Xugu JDBC、Spring Boot（版本对齐 Hibernate 7.4.5） | 承载方言核心实现；成为唯一测试替身 |
| **docs**（项目内） | 生产使用说明、配置示例、验证步骤、特性矩阵引用 | 方言公共契约与 Intent/Charter | 改写 `E:\Work\docs\content` |
| **harness** | Clarify/Charter/Bootstrap/Scope/Plan/Build 治理、验证契约、会话状态 | 无业务运行时依赖 | 混入方言实现代码 |

**Dependency direction (non-negotiable):**  
`demo-spring-boot` → `dialect` → (Hibernate API + Xugu JDBC)  
`docs` 描述契约，不反向依赖 demo。  
`harness` 治理流程，不进入运行时 classpath。

---

## 6. Non-negotiables

1. **从零实现**于本仓；禁止旁路/旧方言源码参考或移植。
2. **结构可参考** Hibernate MySQL/Oracle 模块拆分；**禁止继承** `MySQLDialect` / `OracleDialect`。
3. **SQL 真相源**仅 `E:\Work\docs\content`；官方 content 只读。
4. **版本锁定**：Hibernate 7.4.5.Final + JDK 17 + GAV 版本与 Hibernate 对齐。
5. **功能边界 = 定义 A**（MySQL/Oracle 生产能力面 ∩ 虚谷文档可实现）。
6. **集成测试必须真实 XuguDB**；JDBC jar 使用仓库根 12.3.6。
7. **默认 `compatible_mode=NONE`**；连接密钥优先 env。
8. **交付三件套**：dialect jar + Spring Boot demo + 项目内生产文档。
9. **GitHub Flow**：不在 `main`/`master` 上实现；Ship（tag/push/release/受保护分支）仅 Human Gate 授权。
10. **驾驭架**：无 Intent/Charter PASS 与批准前不写业务 Java；Phases 默认串行，由 orchestrator 决定依赖，不问人类并行。

---

## 7. Success criteria

- 制品 `com.xugu:xugu-dialect:7.4.5.Final` 可构建、可安装/本地消费。
- `com.xugu.dialect.XuguDialect` 可通过显式配置与 DialectResolver SPI 自动识别接入。
- 定义 A 特性矩阵中「文档允许」项均有实现与验收证据（Plan 落表，Build 闭环）。
- 真实 XuguDB 集成测试通过（连接参数可 env 覆盖；默认 NONE）。
- Spring Boot demo 可启动并对真实库演示核心能力。
- 项目内生产文档足以让集成方按文档完成配置与验证。
- 项目验证契约（`harness/verification.json` + `python harness/scripts/verify.py`）在约定检查配置完整后给出 **VERIFY PASS**。
- 工作分支有 must-commit SHA；Accept 前无 VERIFY FAIL / INCOMPLETE 阻塞项。

---

## 8. Verification expectations

| Layer | Expectation |
|---|---|
| Harness | `python harness/scripts/harness_check.py`；分支检查 `branch_check.py` |
| Project verify | `python harness/scripts/verify.py` — 仅当所需检查均已配置且成功 → **PASS** |
| Build / Test | Bootstrap 后填入真实 build/test 命令（Maven）；集成测试依赖可达的 XuguDB |
| Evidence | 观察行为 + 验证输出 + commit SHA（有变更时）方可宣称完成 |
| Accept gate | `VERIFY PASS` 必需；`VERIFY INCOMPLETE` / `VERIFY FAIL` 阻塞 Accept |
| Ship | Central 发布凭证与 tag/push 不在本 Charter 批准范围内，另需 Human Gate |

**本地连接默认仅作开发参考；CI 必须通过环境变量注入主机、库名、账号口令。**

---

## 9. Risks

| Risk | Impact | Mitigation |
|---|---|---|
| 定义 A 边界在细节上仍需矩阵化 | 范围漂移或验收不清 | Plan 强制产出可勾选特性矩阵；超出矩阵需新 Scope |
| 虚谷文档与 Hibernate 能力面不完全对齐 | 部分能力无法实现 | 矩阵标注「文档不允许 / 延后」；不硬造 SQL |
| Spring Boot 与 Hibernate 7.4.5 版本错配 | Demo 不稳定 | Charter/ADR 锁定 Hibernate；Bootstrap/Plan 锁定 Boot BOM |
| 真实库不可达 | 集成测试红灯 | 文档化连接前置；env 覆盖；CI 标明依赖 |
| 明文口令误入仓库/流水线 | 安全事件 | 禁止提交生产密钥；示例用 env |
| 无意参考旁路仓 | 违反非谈判项 | Code review / ownership 路径约束；architect 抽查 |

---

## 10. Open / deferred items

| Item | Why deferred | Owner | Revisit trigger |
|---|---|---|---|
| Initiative 类型（hotfix\|feature\|major） | 属 Bootstrap 后 Scope，非 Charter | Human Gate | Bootstrap (G1) 完成 |
| Phase 并行/串行编排 | orchestrator 按依赖决定 | orchestrator | Plan |
| 特性矩阵细表 | 执行细化，不阻塞产品意图 | architect (Plan) | Charter 批准后 / Plan |
| 模块目录精确命名（`dialect` vs `xugu-dialect` 等） | Bootstrap 落地布局 | architect + orchestrator | Bootstrap |
| 项目内 docs 最终路径 | 布局落定 | architect | Bootstrap / 首个 docs Phase |
| Spring Boot 精确版本号 | 需与 Hibernate 7.4.5 对齐选型 | architect (Plan) | Plan |
| Maven Central 发布凭证与流水线 | Ship 门禁 | Human Gate | Accept 之后 |
| URL 查询参数精确集 | 以驱动文档为准 | implementer + docs | Build 连接 Phase |

---

## 11. Related ADR

- `DECISIONS/ADR-0001-hibernate-baseline.md` — Hibernate 7.4.5.Final、GAV、包名、禁止继承 MySQL/Oracle（Accepted）。

---

## Approval

**Human Gate approved Round A Charter** on 2026-07-14. Root SSOT is this file. Orchestrator completed Bootstrap (G1) promotion.
