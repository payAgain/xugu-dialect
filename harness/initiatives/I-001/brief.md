# Initiative Brief: I-001

> Scoped clarity for one change unit. Not a full product re-charter.
> Type: major
> Updated: 2026-07-14T15:55:00+08:00

## Goal
- 交付 Hibernate **7.4.5.Final** 虚谷方言制品 **`com.xugu:xugu-dialect:7.4.5.Final`** + **Spring Boot demo** + **项目内生产文档**，功能覆盖 **定义 A 全量**（MySQL/Oracle Dialect 生产能力面 ∩ 虚谷文档可实现）。

## In-scope
- Maven 多模块脚手架：`dialect/` + `demo-spring-boot/`（布局见 `docs/architecture.md`）。
- `com.xugu.dialect.XuguDialect` 及按 MySQL/Oracle 结构拆分的协作模块；**不继承** MySQL/Oracle Dialect；**禁止**参考旁路/旧方言实现。
- DialectResolver SPI 自动识别 + 显式 dialect 配置。
- 定义 A 特性矩阵（Plan 落表）及对应实现与真实 XuguDB 集成测试。
- Spring Boot demo：**Spring Boot 4.1.0** + 强制属性 **`hibernate.version=7.4.5.Final`**；真实库；默认 `compatible_mode=NONE`；连接可用 env 覆盖。
- 项目内生产文档目录：**`docs/user-guide/`**（配置、接入、验证、特性矩阵引用）；**不改写** `E:\Work\docs\content`。
- 填入 `harness/verification.json` 真实 build/test 命令，直至可 `VERIFY PASS`。
- 工作分支上的 must-commit（经 Human Gate 授权后）；Accept 证据链。
- 本 Initiative 交付至 **Accept**；**不含** Maven Central Ship（tag/push/Central 另议）。

## Out-of-scope
- Hibernate 6.x / 8 beta 适配。
- 参考或移植 `E:\Work\java\hibernate-dialect`、旧 `xugu-dialect`。
- 改写官方 `E:\Work\docs\content`。
- Maven Central 账号/签名/正式发布（**Ship 另开**）。
- 定义 A 交叉后明确「文档不允许」的能力（矩阵标注延后，不硬造 SQL）。

## Acceptance criteria
- `com.xugu:xugu-dialect:7.4.5.Final` 可构建并被 demo/本地消费。
- 显式配置与 SPI 自动识别均可接入 XuguDB。
- 定义 A 矩阵中「可实现」项均有实现 + 真实库验收证据。
- Spring Boot **4.1.0** demo 可启动并对真实库演示核心能力（Hibernate 锁定 7.4.5.Final）。
- `docs/user-guide/` 足以完成配置与验证。
- `python harness/scripts/verify.py` → **VERIFY PASS**；Accept 材料齐全；工作分支有提交 SHA。
- **不要求**本 Initiative 完成 Central 发布。

## Non-goals / deferred
- Central 发布与 tag/push（Accept 后另开 Ship）。
- 空间/几何等超出定义 A 的扩展。

## Risks / unknowns
- 定义 A 矩阵在 Plan 才细化为可勾选清单；范围以矩阵为准防漂移。
- 真实库可达性影响集成测试与 demo。
- Boot 4.1.0 BOM 默认 Hibernate 7.4.1，须强制覆盖为 7.4.5.Final（已锁定）。

## Related
- Branch: `feat/i-001-xugu-dialect-major`（已创建；HEAD 仍无 commit）
- Task packets: `harness/tasks/P-001.md` … `P-011.md`；`harness/tasks/REGISTRY.yaml`
- Build draft: `harness/builds/B-001.json`（status=`draft`，待 Human Gate 批准范围）
- ADR / contracts: `DECISIONS/ADR-0001-hibernate-baseline.md`；Plan 已规划 `contracts/xugu-dialect.contract.md` + 特性矩阵（P-002）
- Charter: `PROJECT_CHARTER.md`
- Spring Boot: **4.1.0** + `hibernate.version=7.4.5.Final`
- Docs path: **`docs/user-guide/`**
- Ship: **out of this Initiative**

## Open Questions (Scope)
- （无）— 2026-07-14 Human Gate 已确认 Boot 4.1.0+覆盖、Ship 另开、`docs/user-guide/`。

## Status
`active` — Plan complete； awaiting Human Gate **Build B-001** 范围批准（推荐仅 **P-001**）。
