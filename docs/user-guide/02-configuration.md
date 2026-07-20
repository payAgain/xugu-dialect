# 02 — Configuration / 配置

← [01-install.md](01-install.md) · [Index](README.md) · Next: [03-verify.md](03-verify.md)

Contract: [`contracts/xugu-dialect.contract.md`](../../contracts/xugu-dialect.contract.md) §3 / §5.

## Explicit dialect（推荐写明）

Hibernate properties:

```properties
hibernate.dialect=com.xugu.dialect.XuguDialect
```

Spring Boot / Spring Data JPA (`application.yml`):

```yaml
spring:
  jpa:
    properties:
      hibernate:
        dialect: com.xugu.dialect.XuguDialect
```

Programmatic (SessionFactory / Persistence unit) — set the same property key `hibernate.dialect`.

## DialectResolver SPI（自动识别）

方言 jar 内置：

| Item | Value |
|---|---|
| Interface | `org.hibernate.engine.jdbc.dialect.spi.DialectResolver` |
| Implementation | `com.xugu.dialect.XuguDialectResolver` |
| Services file | `META-INF/services/org.hibernate.engine.jdbc.dialect.spi.DialectResolver` |

匹配规则（大小写不敏感）：JDBC `DatabaseMetaData` 的 **product name** 或 **driver name** 包含 token `xugu`。实库观测示例：product `XuguDB`，driver `XuguDB JDBC Driver`。

使用方式：

1. 确保 `xugu-dialect` 在 classpath（含 services 资源）。
2. **不要** 强制设成错误的 dialect；可省略 `hibernate.dialect`，让 SPI 解析。
3. 显式配置与 SPI **均可**；demo 为清晰起见使用显式配置。

非 Xugu 库：resolver 应返回不匹配（不抢走其他方言）。若 SPI「不生效」，见 [05-troubleshooting.md](05-troubleshooting.md)。

## Spring Boot 4.1.0 + Hibernate 7.4.5.Final

Spring Boot **4.1.0** BOM 默认 `hibernate.version` 为 **7.4.1.Final**。本方言与 Charter 锁定 **7.4.5.Final**，demo **必须强制**：

```xml
<properties>
  <hibernate.version>7.4.5.Final</hibernate.version>
</properties>
```

并在导入 Boot BOM **之后**，在 `dependencyManagement` 中再声明 `hibernate-core`（见 `demo-spring-boot/pom.xml`）。

校对：

```bash
mvn -pl demo-spring-boot dependency:tree -Dincludes=org.hibernate.orm:hibernate-core
```

期望：`hibernate-core:jar:7.4.5.Final`（不是 7.4.1）。

## Connection env / `XUGU_*`

| Env | Role | Local reference default (非生产) |
|---|---|---|
| `XUGU_JDBC_URL` | JDBC URL | `jdbc:xugu://127.0.0.1:5138/SYSTEM?compatiblemode=NONE` |
| `XUGU_USER` | username | `SYSDBA` |
| `XUGU_PASSWORD` | password | `SYSDBA` |
| `XUGU_RUN_IT` | enable gated IT（可选） | unset / `true` |

Demo `application.yml` 使用 `${XUGU_JDBC_URL:...}` 等占位；也可用 `spring.datasource.*` 覆盖。

Driver class: `com.xugu.cloudjdbc.Driver`.

## compatiblemode=NONE

Charter / 契约默认：**`compatible_mode` / URL `compatiblemode=NONE`**（与虚谷会话默认一致）。

URL 查询参数示例：

```text
jdbc:xugu://127.0.0.1:5138/SYSTEM?compatiblemode=NONE
```

不要在未评估影响的情况下改成其他兼容模式；方言 SQL 按 NONE 语义验证。

## Secrets / 凭据

- **优先环境变量**（或密钥管理）注入 host/user/password；CI / 共享环境勿依赖仓库内默认值。
- 仓库中的 `SYSDBA` / 本地 URL 仅为 **local reference**，**禁止**提交生产密钥。
- 勿把密码写进可提交的 properties / yaml 明文（除非本地-only 且已在 `.gitignore` 策略下——本仓 demo 用 env 占位）。

## Isolation note（简要）

方言侧文档化的隔离级别以契约 / 矩阵为准；**不声称** READ UNCOMMITTED。详见矩阵与 P-008 证据，勿在应用中假设未声明的隔离行为。

## UUID / JSON Boot 必配清单（I-008 Q3）

Hibernate **7.4.5.Final** + Spring Boot 消费者在虚谷上使用 **UUID** 与 **JSON** 类型时，方言 jar **单独**不足以「开箱即用」——须按下列清单配置（**P-006** 将实现 demo 同款 wiring 为产品路径；本节为集成方 SSOT 草案）。

| # | 必配项 | 原因 |
|---|---|---|
| 1 | **`spring-boot-starter-jackson`** | Hibernate JSON 需 classpath 上 Jackson 以提供 **`FormatMapper`**（Boot 4 为 Jackson 3 / `tools.jackson`） |
| 2 | **UUID → `varchar(36)` + `AttributeConverter`** | 避免 Xugu JDBC 对 `UUID.class` 的 **`[E50044]`**；勿依赖默认 `UUIDJdbcType` / SQL `guid` 直绑 |
| 3 | **`hibernate.type.preferred_uuid_jdbc_type`** 与映射一致 | GAV **7.4.5.Final** 下与实体 `@Column` / Converter 对齐（推荐 VARCHAR 路径） |
| 4 | **`hibernate.query.hql.json_functions_enabled=true`** | HQL `json_value` / `json_arrayagg` / `json_objectagg` 等（矩阵 C-JSON-* / A-FUN-017） |

### 推荐 `application.yml` 片段

与 demo 一致的最小模板（**P-006** 目标态；见 `demo-spring-boot`）：

```yaml
spring:
  jpa:
    properties:
      hibernate:
        dialect: com.xugu.dialect.XuguDialect
        type:
          preferred_uuid_jdbc_type: VARCHAR
        query:
          hql:
            json_functions_enabled: true
```

Maven 依赖（除 `xugu-dialect` + JDBC 外）：

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-jackson</artifactId>
</dependency>
```

实体侧 UUID 示例（须自管 Converter，可参考 demo `UuidAsVarcharConverter`）：

```java
@Convert(converter = UuidAsVarcharConverter.class)
@Column(name = "external_id", length = 36)
private UUID externalId;
```

**矩阵：** A-TYP-012 / A-TYP-013（类型）；C-JSON-001…004（JSON 聚合）。消费者路径映射说明：[06-consumer-path.md § Mapping notes](06-consumer-path.md#mapping-notes消费者侧)。排障 JSON：[05-troubleshooting.md §9](05-troubleshooting.md#9-hql-json_arrayagg--json_objectagg-不可用)。

**悲观锁**（与 JSON/UUID 独立）：[07-lock-integration.md](07-lock-integration.md)。
