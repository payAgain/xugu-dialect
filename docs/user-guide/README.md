# Xugu Hibernate Dialect — User Guide / 用户指南

项目内接入指南（I-001）。面向要把 **Hibernate 7.4.5** 接到 **XuguDB** 的集成方。

> **Public contract:** [`contracts/xugu-dialect.contract.md`](../../contracts/xugu-dialect.contract.md)  
> **Feature matrix SSOT (Definition A):** [`contracts/feature-matrix-definition-a.md`](../../contracts/feature-matrix-definition-a.md)  
> **I-003 capability matrix (ruler C):** [`contracts/feature-matrix-i003-ruler-c.md`](../../contracts/feature-matrix-i003-ruler-c.md)  
> **I-005 production regression baseline SSOT:** [`contracts/production-regression-baseline.md`](../../contracts/production-regression-baseline.md)  
> **I-006 consumer-path baseline SSOT:** [`contracts/consumer-path-baseline.md`](../../contracts/consumer-path-baseline.md)  
> **Docs pointers:** [`docs/feature-matrix-definition-a.md`](../feature-matrix-definition-a.md) · [`docs/feature-matrix-i003-ruler-c.md`](../feature-matrix-i003-ruler-c.md) · [`docs/consumer-path-baseline.md`](../consumer-path-baseline.md)  
> **Demo:** [`demo-spring-boot/README.md`](../../demo-spring-boot/README.md)

**不改写** 官方树 `E:\Work\docs\content`。Ship / Maven Central 发布不在本指南必做范围（另开 Human Gate / Ship）。

**I-003：** 同一 GAV **`com.xugu:xugu-dialect:7.4.5.Final`** 下扩展生产能力（异常映射、JSON 聚合、Window/CTE、bulk、DDL 细节）；Definition A 仍有效，详见 ruler-C 矩阵。

**I-005：** 生产回归测试基线 — **98** 物理可实现行（Charter **94** + I-007 **+4**）；**诚实 covered-live 今日 79/98**；I-008 目标 **83 + 15 known-limit**；冻结门控见 [03-verify.md § Frozen baseline](03-verify.md#frozen-baseline--i-005-冻结基线门控)。

**I-006：** Spring Boot **消费者路径**基线 — **41** Boot-required 行（A/B/C′），open gaps = **0**；如何离线 / `XUGU_RUN_IT=true` 跑通见 [06-consumer-path.md](06-consumer-path.md)。**不要求** Ship。

## Quick start / 快速开始

1. **依赖** — JDK 17 + GAV `com.xugu:xugu-dialect:7.4.5.Final` + 仓库根目录 Xugu JDBC jar → [01-install.md](01-install.md)
2. **配置** — 显式 dialect 或 SPI；Boot 强制 `hibernate.version`；`XUGU_*` + `compatiblemode=NONE` → [02-configuration.md](02-configuration.md)
3. **验证** — `mvn verify` / demo `spring-boot:run` / 集成开关 → [03-verify.md](03-verify.md)
4. **能力边界** — Definition A + I-003 ruler C 矩阵（可实现 / 文档不允许 / 延后）→ [04-feature-matrix.md](04-feature-matrix.md)
5. **排障** — LIMIT/FOR UPDATE、E19132 OFFSET、missing sequence、BINARY、SPI、连库失败、I-003 JSON/bulk/ENUM → [05-troubleshooting.md](05-troubleshooting.md)
6. **悲观锁集成** — 无 SKIP LOCKED / FOR SHARE；`PESSIMISTIC_READ`→`FOR UPDATE` → [07-lock-integration.md](07-lock-integration.md)
7. **消费者路径（I-006）** — Boot 基线 A/B/C′、离线与 gated live → [06-consumer-path.md](06-consumer-path.md)

## Index

| Doc | Topic |
|---|---|
| [01-install.md](01-install.md) | Maven GAV、JDK、JDBC jar |
| [02-configuration.md](02-configuration.md) | Dialect / SPI、Boot、env、secrets |
| [03-verify.md](03-verify.md) | Build / demo / IT gate |
| [04-feature-matrix.md](04-feature-matrix.md) | 特性矩阵导航与状态含义 |
| [05-troubleshooting.md](05-troubleshooting.md) | 常见失败 |
| [06-consumer-path.md](06-consumer-path.md) | I-006 Boot 消费者路径基线（A/B/C′） |
| [07-lock-integration.md](07-lock-integration.md) | 悲观锁集成（SKIP LOCKED / FOR SHARE / PESSIMISTIC_READ） |

## Prerequisites reminder

| Item | Notes |
|---|---|
| JDK | **17**（`maven.compiler.release`） |
| Maven | 3.9+ recommended |
| JDBC | Repo-root `xugu-jdbc-12.3.6.jar`（本仓 systemPath；对外发布后以驱动文档为准） |
| Live DB | Demo 运行与 gated IT 需要可达的 XuguDB；离线 `mvn test` 默认可跳过 IT |
