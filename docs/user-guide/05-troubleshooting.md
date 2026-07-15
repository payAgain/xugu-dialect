# 05 — Troubleshooting / 故障排查

← [04-feature-matrix.md](04-feature-matrix.md) · [Index](README.md)

## 1. LIMIT vs FOR UPDATE 顺序错误

**症状：** 分页 + 悲观锁查询被数据库拒绝；或与「标准」MySQL 风格 `LIMIT … FOR UPDATE` 行为不一致。

**原因：** 虚谷语法要求锁子句在限制子句之前。方言 `XuguLimitHandler` 生成顺序为：

```text
… FOR UPDATE [OF …] [NOWAIT|WAIT …] LIMIT … [OFFSET …]
```

**不要** 手写或假设 Hibernate 默认的 `LIMIT … FOR UPDATE` 顺序。

**处理：** 使用本方言；升级到已含 P-004 修复的构建；对照矩阵 A-PAG-* / A-LCK-*。

## 2. BINARY 长度 / bare BINARY

**症状：** DDL 导出或建表时小二进制类型语法报错（例如带长度占位的 `BINARY($l)` 不被接受）。

**原因：** 虚谷侧小二进制映射为 **裸 `BINARY`**（无 `$l` 长度模板）；见矩阵 A-TYP-009。

**处理：** 使用本方言的类型映射；勿按其他库的 `BINARY(n)` / `VARBINARY(n)` 模板硬编码 DDL。大对象用 `BLOB`（A-TYP-010）。

## 3. SPI 未匹配 / 解析到错误方言

**症状：** 未设 `hibernate.dialect` 时落到其他 Dialect，或根本未加载 `XuguDialect`。

**检查清单：**

1. `xugu-dialect` jar 是否在运行 classpath。
2. jar 内是否存在  
   `META-INF/services/org.hibernate.engine.jdbc.dialect.spi.DialectResolver`  
   → 内容为 `com.xugu.dialect.XuguDialectResolver`。
3. JDBC metadata：product / driver 名是否包含 `xugu`（如 `XuguDB` / `XuguDB JDBC Driver`）。
4. 是否显式配置了 **错误的** dialect（会覆盖 SPI 意图）。
5. 驱动是否真是虚谷 JDBC（错误驱动 → 名称不含 xugu → resolver 正确返回不匹配）。

**稳妥做法：** 显式设置 `hibernate.dialect=com.xugu.dialect.XuguDialect`（见 [02-configuration.md](02-configuration.md)）。

## 4. 数据库不可达 / DB unreachable

**症状：** `spring-boot:run` 启动失败、IT 在 gate ON 时连接超时/拒绝、`Communications link` 类错误。

**检查：**

| Check | Action |
|---|---|
| Host/port | `XUGU_JDBC_URL` 是否指向真实实例（默认 `127.0.0.1:5138` 仅本地参考） |
| Credentials | `XUGU_USER` / `XUGU_PASSWORD` |
| Driver on classpath | `xugu-jdbc-12.3.6.jar` / 等价驱动 |
| Firewall / VPN | 网络是否可达 |
| Gate | 无库时 **不要** `-Dxugu.run.integration=true`；用离线 `mvn test` |

文档约定：缺真实库时，阻塞原因应视为 **外部前置**，而不是跳过「需要真库」的验收标准去用 mock 冒充方言 IT。

## 5. Boot 仍解析到 Hibernate 7.4.1

**症状：** dependency tree 显示 `hibernate-core:7.4.1.Final`。

**处理：** 在模块中设置 `<hibernate.version>7.4.5.Final</hibernate.version>`，并在 Boot BOM **之后** 再次管理 `hibernate-core`。见 [02-configuration.md](02-configuration.md) 与 `demo-spring-boot/pom.xml`。

## 6. systemPath JDBC 找不到

**症状：** Maven 报 systemPath / jar 不存在。

**处理：** 确认仓库根目录有 `xugu-jdbc-12.3.6.jar`；从 **reactor 根** 构建（`maven.multiModuleProjectDirectory` 指向根）。见 [01-install.md](01-install.md)。

## Still stuck?

- Contract: [`contracts/xugu-dialect.contract.md`](../../contracts/xugu-dialect.contract.md)  
- Matrix: [`contracts/feature-matrix-definition-a.md`](../../contracts/feature-matrix-definition-a.md)  
- Demo README: [`demo-spring-boot/README.md`](../../demo-spring-boot/README.md)  
- Project verification: `docs/verification.md` / `python harness/scripts/verify.py`
