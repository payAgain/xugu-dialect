# Intent Clarity Draft

> Intent Clarity PASS recorded; Round A Charter **APPROVED**; Bootstrap (G1) **COMPLETE**.
> Project: hibernate-test
> Level: Standard
> Updated: 2026-07-14T15:30:00+08:00

## 1. Problem
- 为虚谷数据库（XuguDB）从零交付可生产发布的 Hibernate 方言包。
- 禁止参考/移植 `E:\Work\java\hibernate-dialect` 与旧 `xugu-dialect`；不考虑旧版兼容。
- 架构结构参考 Hibernate 自带 MySQL/Oracle Dialect 模块拆分（不继承）。
- 语法以 `E:\Work\docs\content` 为准。

## 2. Desired outcome / success criteria
- Maven 制品：`com.xugu:xugu-dialect:<hibernate-aligned-version>`（版本号与所适配 Hibernate 一致，当前锁定 **7.4.5.Final**）。
- 方言主包：`com.xugu.dialect`，主类 `com.xugu.dialect.XuguDialect`；支持 **DialectResolver SPI 自动识别**。
- 生产级功能覆盖：**定义 A** — 以 MySQL/Oracle Dialect 生产能力面为对照清单，凡虚谷文档可实现项全部覆盖；真实 XuguDB 集成测试通过。
- 交付：方言 **jar** + **含 Spring Boot 的 demo** + **项目内生产文档**（不改写 `E:\Work\docs\content`）。
- JDBC：仓库根 `xugu-jdbc-12.3.6.jar`；默认兼容模式连接侧按 **NONE**。

## 3. Users & entry points
- 显式：`hibernate.dialect=com.xugu.dialect.XuguDialect`
- 自动：DialectResolver / SPI 识别 XuguDB
- Demo：Spring Boot 应用连接真实库演示核心能力

## 4. In-scope
- 本仓库从零实现方言模块 + 测试（必连真实库）+ Spring Boot demo + 项目内文档
- Hibernate **7.4.5.Final**，JDK **17**
- 默认 `compatible_mode`：**NONE**
- 驾驭架：Clarify PASS → Charter → Bootstrap →（之后）Scope/Plan/Build

## 5. Out-of-scope
- 参考旁路 `hibernate-dialect` / 旧方言源码
- Hibernate 6.x 线、8.x Beta
- 改写 `E:\Work\docs\content\ecosystem\orm\java\hibernate.md`
- （可延后）Maven Central 账号/签名发布流程细节（属 Ship 门禁）
- 空间/几何等非 Hibernate 核心 Dialect 生产能力面之外的扩展（除非对照清单与虚谷文档交叉后明确纳入）

## 6. Constraints
- Tech: Hibernate **7.4.5.Final**；JDK **17**；JDBC **12.3.6**；GAV **`com.xugu:xugu-dialect:7.4.5.Final`**
- SQL 真相源：`E:\Work\docs\content`
- 结构参考：Hibernate MySQL/Oracle Dialect（架构 only）
- 禁止实现参考：`hibernate-dialect` 旁路仓
- 集成测试必须连真实 XuguDB
- 默认兼容模式：**NONE**

## 7. Interfaces & boundaries
- Package: `com.xugu.dialect.*`
- Maven: `com.xugu:xugu-dialect:7.4.5.Final`
- SPI: DialectResolver 自动识别
- Docs: 本仓库 `docs/`（生产使用说明）；官方 content 只读
- Demo: Spring Boot 模块（计划目录 `demo-spring-boot/`）
- Test DB reference（本地；勿将密码提交为生产密钥，可用 env 覆盖）:
  - host=`127.0.0.1` port=`5138` ssl=`nssl` database=`SYSTEM` user=`SYSDBA` password=`SYSDBA`
  - URL 形态参考：`jdbc:xugu://127.0.0.1:5138/SYSTEM?...`（精确参数以驱动文档为准）

## 8. References
- 架构：Hibernate ORM MySQLDialect / OracleDialect 结构
- 语法：`E:\Work\docs\content`
- 驱动：`xugu-jdbc-12.3.6.jar`
- 禁止：`E:\Work\java\hibernate-dialect`、旧 xugu-dialect 实现
- Charter SSOT：`PROJECT_CHARTER.md`
- ADR：`DECISIONS/ADR-0001-hibernate-baseline.md`

## 9. Key options & recommendations
| Topic | Decision |
|---|---|
| 工作仓 | 本仓从零 |
| 旁路参考 | **禁止** |
| Hibernate | **7.4.5.Final** |
| JDK | **17** |
| GAV | **`com.xugu:xugu-dialect:7.4.5.Final`**（版本与 Hibernate 对齐；非 1.0.0） |
| 包名 | `com.xugu.dialect` |
| 旧版兼容 | 不考虑 |
| 功能边界 | **A**：MySQL/Oracle 能力面 ∩ 虚谷可实现 |
| JDBC | 根目录 12.3.6 + 真实库 |
| 文档 | **仅项目内**；不改官方 content |
| Demo | **含 Spring Boot** |
| compatible_mode | **NONE** |
| Resolver | **自动识别** |
| 连接参考 | 127.0.0.1:5138 / SYSTEM / SYSDBA |

## 10. Risks & unknowns
- 「能力面 ∩ 虚谷可实现」需在 Plan 阶段产出可验收特性矩阵（仍属执行细化，不阻塞产品意图）。
- 明文口令仅作本地参考；CI/共享环境应改用环境变量。
- Spring Boot 与 Hibernate 7.4.5 版本对齐需在 Plan 锁定 BOM。

## 11. Open Questions
- （无阻塞产品意图的开放问题）
- **Next Human Gate:** Scope — 选择首个 Initiative 类型（hotfix|feature|major）与目标
- Plan 阶段再展开：特性矩阵条目、Spring Boot 精确版本

## 12. Deferred decisions
| Item | Why deferred | Owner | Revisit trigger |
|---|---|---|---|
| Initiative 类型 | G1 之后 Scope | Human Gate | **Bootstrap 已完成 → 立即 Scope** |
| Phase 并行 | orchestrator | orchestrator | Plan |
| Central 发布 | Ship | Human Gate | Accept 后 |
| 特性矩阵细表 | Plan | architect | Charter 后 / Plan |

## Status
`Intent Clarity: PASS`（人类已确认）

- **Human PASS phrase:** 「目标已明确，可以开始」
- **Confirmed at:** 2026-07-14（Human Gate）
- **Round A Charter:** **APPROVED** → root SSOT `PROJECT_CHARTER.md`
- **ADR-0001:** **Accepted** → `DECISIONS/ADR-0001-hibernate-baseline.md`
- **Bootstrap (G1):** **COMPLETE**（2026-07-14）
- **Next:** Human Gate **Scope** — pick Initiative type (`hotfix` | `feature` | `major`) + goal（推荐 `feature` 或 `major` 作为首次方言交付）
- **Handoff:** `harness/handoffs/orchestrator/bootstrap-g1.md`
