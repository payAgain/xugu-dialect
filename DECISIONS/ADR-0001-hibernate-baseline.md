# ADR-0001: Hibernate baseline, GAV, package, and non-inheritance

> **Status:** Accepted  
> **Date:** 2026-07-14  
> **Deciders:** Human Gate (product) / architect-contract (draft) / orchestrator (promote)  
> **Approved by Human Gate:** 「批准 Round A Charter，可以 Bootstrap 批准 ADR-0001」  
> **Promoted from:** `harness/drafts/ADR-0001-hibernate-baseline.md`  
> **Location:** `DECISIONS/` (SSOT)

## Context

本仓库需从零交付 XuguDB 的 Hibernate 方言。Intent Clarity 已锁定 Hibernate 版本、Maven 坐标、包名，并明确架构仅对照 MySQL/Oracle Dialect 模块结构、禁止继承其方言基类，且禁止参考旁路旧实现。

## Decision

1. **Hibernate 版本：** `7.4.5.Final`（不维护 6.x；不做 8 beta）。
2. **JDK：** `17`。
3. **Maven GAV：** `com.xugu:xugu-dialect:7.4.5.Final`（artifact 版本与所适配 Hibernate 对齐，不使用无关的 1.0.0 语义）。
4. **根包与主类：** `com.xugu.dialect` / `com.xugu.dialect.XuguDialect`。
5. **自动识别：** 提供 DialectResolver SPI，使运行时可自动识别 XuguDB。
6. **继承策略：** `XuguDialect` **不得** extends `MySQLDialect` 或 `OracleDialect`；可参考其模块拆分与能力组织方式自行实现。
7. **实现参考禁令：** 不得参考/移植 `E:\Work\java\hibernate-dialect` 或旧 `xugu-dialect` 源码。

## Consequences

### Positive

- 版本与 Hibernate 对齐，降低「方言版本 vs ORM 版本」沟通成本。
- 独立方言层次，避免误用 MySQL/Oracle 专属 SQL 生成路径。
- SPI 降低集成方配置负担。

### Negative / trade-offs

- 不能「借」MySQL/Oracle 已有方法体，实现与测试成本更高（符合从零与定义 A 边界）。
- Boot demo 必须另行锁定与 7.4.5 兼容的 Spring Boot BOM（Plan 细化）。

### Follow-ups

- Plan：定义 A 特性矩阵；Spring Boot 精确版本。
- Bootstrap：模块目录与 `contracts/` 公共契约落盘（目录规划见 `docs/architecture.md`）。
- 本 ADR 已提升至 `DECISIONS/`（Accepted）。
