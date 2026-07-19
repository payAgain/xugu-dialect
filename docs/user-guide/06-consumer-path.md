# 06 — Consumer-path baseline / 消费者路径基线（I-006）

← [05-troubleshooting.md](05-troubleshooting.md) · [Index](README.md) · Related: [03-verify.md](03-verify.md)

## What this is / 这是什么

**I-006** freezes a **Spring Boot consumer-path** regression surface on top of the I-005 dialect baseline. It is **not** a full 94-row Boot mirror of every I-005 可实现 row.

| Item | Value |
|---|---|
| **SSOT** | [`contracts/consumer-path-baseline.md`](../../contracts/consumer-path-baseline.md) |
| **Docs pointer** | [`docs/consumer-path-baseline.md`](../consumer-path-baseline.md) |
| **GAV** | `com.xugu:xugu-dialect:7.4.5.Final`（本 Initiative **不升版**） |
| **Boot-required rows** | **41**（Layer A=13 / B=9 / C′=19） |
| **Open Boot gaps** | **0**（P-002…P-004 closed） |
| **Demo `@Test`** | ≈ **32**（目标量级 25–40；I-007 P-005 Track B 加深 +4 gated/offline） |
| **Ship / Central** | **out of scope** for I-006 |

Pure dialect SPI / unit / dialect-IT hooks stay in I-005 suites and are listed as **`dialect-it-only`** in the SSOT exclusion appendix — do **not** treat them as Boot gaps.

## Layers A / B / C′

| Layer | Meaning | Demo focus |
|---|---|---|
| **A** | Golden Boot path | validate startup、startup-crud、full CRUD、JPQL+Pageable、SPI 无显式 dialect |
| **B** | B-both model | 关联实体 **+** SEQUENCE 实体；悲观锁；UNIQUE 异常；事务回滚 |
| **C′** | Remaining Boot-required | 类型子集、HQL 函数子集、可选 JSON、一条 bulk update |

Row → `entry_class#method` mapping lives **only** in the SSOT. Demo class list: [`demo-spring-boot/README.md`](../../demo-spring-boot/README.md).

## How to run / 如何跑

### 1) Offline（日常 / CI，无需真库）

Gate **OFF**（默认）。Gated `@SpringBootTest` / IT 会跳过；离线单元与配置 smoke 须绿。

```bash
# 仓库根目录；Windows 可先把 Maven 加入 PATH
# PowerShell 示例:
#   $env:Path = "C:\Users\admin\tools\apache-maven-3.9.9\bin;" + $env:Path

mvn -q -DskipTests package
mvn -q test

# 仅 demo 模块（建议 -am，解析同 reactor 的 xugu-dialect）
mvn -q -pl demo-spring-boot -am test
```

Harness 契约（Accept 门控之一）：

```bash
python harness/scripts/verify.py
```

期望：`VERIFY PASS`（build + offline test）。

### 2) Gated live（有可达 XuguDB 时）

打开 IT gate（二选一）：

```bash
# 环境变量（推荐 Accept 复现命令）
# cmd:
set XUGU_RUN_IT=true
mvn -q test

# PowerShell:
$env:XUGU_RUN_IT = "true"
mvn -q test

# 或 JVM 属性
mvn -q test -Dxugu.run.integration=true

# 仅 demo + 方言依赖
mvn -q -pl demo-spring-boot -am test -Dxugu.run.integration=true
```

| Gate | Meaning |
|---|---|
| `-Dxugu.run.integration=true` | Maven/Surefire 系统属性 |
| `XUGU_RUN_IT=true` | 环境变量（`XuguIntegrationGate.isEnabled()`） |

凭据优先用 env：`XUGU_JDBC_URL`、`XUGU_USER`、`XUGU_PASSWORD`（见 [02-configuration.md](02-configuration.md)）。**不要**提交生产密钥。

**缺真库时**：不要强开 gate；离线以默认 `mvn test` + `verify.py` 为准。有库时 I-006 Initiative Accept 期望 demo 全绿（最近一次：demo **28/0/0/0**）。

### 3) 与 I-005 冻结基线的关系

| Baseline | SSOT | Scope |
|---|---|---|
| **I-005** dialect production | [`production-regression-baseline.md`](../../contracts/production-regression-baseline.md) | 94 可实现 + 34 negative-only（方言单元/IT 为主） |
| **I-006** Boot consumer-path | [`consumer-path-baseline.md`](../../contracts/consumer-path-baseline.md) | 41 Boot-required demo 入口 |

跑 `XUGU_RUN_IT=true mvn -q test` 会同时执行方言 gated IT **与** demo IT。消费者路径专项验收可聚焦：

```bash
$env:XUGU_RUN_IT = "true"
mvn -q -pl demo-spring-boot -am test
```

I-005 全量冻结步骤仍见 [03-verify.md § Frozen baseline](03-verify.md#frozen-baseline--i-005-冻结基线门控)。

## Consumer checklist / 集成方自测

- [ ] GAV：`com.xugu:xugu-dialect:7.4.5.Final`
- [ ] Boot 若用 4.1.0：已强制 `hibernate.version=7.4.5.Final`
- [ ] 离线：`mvn -q test` 绿；`python harness/scripts/verify.py` → **VERIFY PASS**
- [ ] （有库）`XUGU_RUN_IT=true` 下 demo IT 全绿
- [ ] SSOT Boot-required open gaps = **0**（对照 contracts 表）
- [ ] 未把 exclusion appendix 的 `dialect-it-only` 误当成 Boot gap
- [ ] **不要求**本路径完成 Ship / Maven Central

## Mapping notes（消费者侧）

与 demo 一致的已知规避（**方言 jar 未改**）：

- **UUID（A-TYP-012）：** `UuidAsVarcharConverter` + `varchar(36)` — 避免 Xugu JDBC 对 `UUID.class` 的 `[E50044]`
- **JSON：** classpath 需 Jackson（demo 使用 `spring-boot-starter-jackson`），以便 Hibernate JSON `FormatMapper`

详见 [`demo-spring-boot/README.md`](../../demo-spring-boot/README.md) § Mapping notes。

## Boundary / 边界

1. C′ = Boot-required SSOT **41** 行，**不是** 94 行 Boot 镜像。  
2. 不得静默扩张到全量 I-005 可实现 Boot 入口。  
3. Ship / tag / push / Central 不在 I-006 Accept 范围。  
4. 能力真相以 contracts SSOT + I-005/I-003 矩阵为准；本页只说明如何跑消费者路径。

排障：[05-troubleshooting.md](05-troubleshooting.md)。

## I-007 Track B deepening（P-005）

在 I-006 **41/41** Boot-required 冻结基线之上，I-007 Track B 做**行为加深**（不新增 SSOT 矩阵行）：

| gap_id | 内容 | Demo 入口 |
|---|---|---|
| **B-FLY-001** | Flyway 迁移路径 | `DemoFlywayIT#flywayMigratesMarkerTableOnXugu`（gated）；离线 `DemoOfflineSmokeTest#flywayXuguPluginAndMigrationOnClasspath` |
| **B-DEMO-001** | Demo bulk delete | `DemoBulkMutationIT#bulkDeletePersonNames` |
| **B-DEMO-002** | 函数/HQL 冒烟加深 | `DemoFunctionsIT#hqlFunctionSubsetSmoke`（trim/length/locate/case/json_length） |
| **B-DEMO-003** | 只读事务冒烟 | `DemoReadOnlyTxIT#readOnlyTransactionQueriesPersistedRow` |

**Flyway 默认关闭**（`spring.flyway.enabled=false`），避免影响现有 `ddl-auto=update` IT。启用示例：

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    # Xugu: demo registers com.xugu.demo.flyway.XuguFlywayDatabaseType (Flyway 12 plugin SPI)
  jpa:
    hibernate:
      ddl-auto: update   # 实体表仍由 Hibernate 管理；Flyway 仅管显式 migration
```

依赖：`spring-boot-starter-flyway` + `flyway-database-oracle`（Oracle 兼容层）+ demo SPI `XuguFlywayDatabaseType`。

迁移文件：`demo-spring-boot/src/main/resources/db/migration/V1__hib_demo_flyway_marker.sql`（`HIB_DEMO_FLYWAY_MARKER`）。

**不做多数据源**；GAV 仍为 `7.4.5.Final`；NONE only。
