# 01 — Install / 安装依赖

← [Index](README.md) · Next: [02-configuration.md](02-configuration.md)

## Requirements

| Item | Value |
|---|---|
| JDK | **17** |
| Hibernate (compile/runtime with dialect) | **7.4.5.Final** |
| Dialect GAV | **`com.xugu:xugu-dialect:7.4.5.Final`** |
| Main class | `com.xugu.dialect.XuguDialect` |
| JDBC driver (this repo) | `xugu-jdbc-12.3.6.jar` at **repository root** |
| Driver class | `com.xugu.cloudjdbc.Driver` |

Contract SSOT: [`contracts/xugu-dialect.contract.md`](../../contracts/xugu-dialect.contract.md) §2 / §4.

## Maven dependency (GAV)

When the dialect is available from your Maven repository (local install / future Central):

```xml
<dependency>
  <groupId>com.xugu</groupId>
  <artifactId>xugu-dialect</artifactId>
  <version>7.4.5.Final</version>
</dependency>
```

Also depend on Hibernate ORM **7.4.5.Final** (or let Spring Boot manage it after forcing the property — see [02-configuration.md](02-configuration.md)).

**Boot + UUID/JSON：** 除方言与 JDBC 外，须 `spring-boot-starter-jackson` 与 UUID `AttributeConverter` + JSON 函数开关 — 完整清单见 [02-configuration.md § UUID/JSON Boot 必配清单](02-configuration.md#uuid--json-boot-必配清单i-008-q3)（**P-006** 开箱对齐）。

From this multi-module reactor, consume the sibling module:

```xml
<dependency>
  <groupId>com.xugu</groupId>
  <artifactId>xugu-dialect</artifactId>
  <version>${project.version}</version>
</dependency>
```

Build the dialect first:

```bash
mvn -q -pl dialect -am -DskipTests package
# or from root:
mvn -q -DskipTests package
```

## JDBC jar note / JDBC 驱动说明

本仓方言模块通过 **system scope + systemPath** 引用仓库根目录的 JDBC jar（脚手架契约）：

```text
${maven.multiModuleProjectDirectory}/xugu-jdbc-12.3.6.jar
```

要点：

1. Clone / 检出本仓后，确认根目录存在 `xugu-jdbc-12.3.6.jar`，否则编译与真实库 IT 会失败。
2. `system` 依赖 **不会** 随方言 jar 自动传递到消费者；你的应用仍须自行提供 Xugu JDBC（同等版本或厂商推荐坐标）。
3. Demo 模块同样用 `systemPath` 指向同一 jar；见 `demo-spring-boot/pom.xml`。
4. 对外发布到 Central 后，驱动与方言的分发方式可能变化——以当时的发布说明与驱动文档为准；本指南描述的是 **本仓库开发期** 布局。

## Local install (optional)

```bash
mvn -q -pl dialect -am -DskipTests install
```

Then other projects on the same machine can resolve `com.xugu:xugu-dialect:7.4.5.Final` from `~/.m2`.

## Verify install

```bash
mvn -q -pl dialect -am test
```

Default gate keeps live-DB IT skipped. See [03-verify.md](03-verify.md).
