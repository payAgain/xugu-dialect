# 05 — Troubleshooting / 故障排查

← [04-feature-matrix.md](04-feature-matrix.md) · [Index](README.md)

## 1. LIMIT vs FOR UPDATE 顺序错误

**症状：** 分页 + 悲观锁查询被数据库拒绝；或与「标准」MySQL 风格 `LIMIT … FOR UPDATE` 行为不一致。

**原因：** 虚谷语法要求锁子句在限制子句之前。本方言两条路径均生成：

```text
… FOR UPDATE [OF …] LIMIT … [OFFSET …] [NOWAIT|WAIT …]
```

- **Criteria / native LimitHandler：** `XuguLimitHandler`
- **HQL / Criteria SQL AST：** `XuguSqlAstTranslator`（I-002/P-001）

**不要** 手写或假设 Hibernate 默认的 `LIMIT … FOR UPDATE` 顺序。

**处理：** 使用本方言（GAV `com.xugu:xugu-dialect:7.4.5.Final`）；对照矩阵 A-PAG-* / A-LCK-*。同版本行为修复，无需升版本号。

## 2. E19132 unexpected OFFSET（HQL 分页）

**症状：** HQL / Criteria 分页报 `[E19132] unexpected OFFSET`；SQL 形如 ANSI：

```text
… offset ? rows fetch first ? rows only
```

**原因（已修复）：** 未安装方言 SqlAstTranslator 时，Hibernate 标准翻译器发出 ANSI `OFFSET…FETCH`，虚谷不接受。

**处理：** 使用含 I-002/P-001 修复的 **7.4.5.Final** 同 GAV（行为修复，**无版本 bump**）。正确形式为：

```text
… limit ? offset ?
```

带锁时：`… for update … limit ? offset ? [wait …]`。见矩阵 A-PAG-002 / A-PAG-005。

## 3. Schema validate 报 missing sequence

**症状：** `hbm2ddl.auto=validate`（或 SchemaValidator）在序列已存在于库中时仍报 `missing sequence`。

**原因（已修复）：** 方言未提供 `getQuerySequencesString()` / 专用 extractor 时，Hibernate 读不到虚谷目录，误判序列缺失。

**处理：** 使用含 I-002/P-002 修复的 **7.4.5.Final** 同 GAV。方言现从 `all_sequences` 读取（`seq_name` 等列）。真正不存在的序列仍应失败且可诊断。见矩阵 A-SEQ-001。

## 4. BINARY 长度 / bare BINARY

**症状：** DDL 导出或建表时小二进制类型语法报错（例如带长度占位的 `BINARY($l)` 不被接受）。

**原因：** 虚谷侧小二进制映射为 **裸 `BINARY`**（无 `$l` 长度模板）；见矩阵 A-TYP-009。

**处理：** 使用本方言的类型映射；勿按其他库的 `BINARY(n)` / `VARBINARY(n)` 模板硬编码 DDL。大对象用 `BLOB`（A-TYP-010）。

## 5. SPI 未匹配 / 解析到错误方言

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

## 6. 数据库不可达 / DB unreachable

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

## 7. Boot 仍解析到 Hibernate 7.4.1

**症状：** dependency tree 显示 `hibernate-core:7.4.1.Final`。

**处理：** 在模块中设置 `<hibernate.version>7.4.5.Final</hibernate.version>`，并在 Boot BOM **之后** 再次管理 `hibernate-core`。见 [02-configuration.md](02-configuration.md) 与 `demo-spring-boot/pom.xml`。

## 8. systemPath JDBC 找不到

**症状：** Maven 报 systemPath / jar 不存在。

**处理：** 确认仓库根目录有 `xugu-jdbc-12.3.6.jar`；从 **reactor 根** 构建（`maven.multiModuleProjectDirectory` 指向根）。见 [01-install.md](01-install.md)。

## Still stuck?

- Contract: [`contracts/xugu-dialect.contract.md`](../../contracts/xugu-dialect.contract.md)  
- Matrix: [`contracts/feature-matrix-definition-a.md`](../../contracts/feature-matrix-definition-a.md)  
- Demo README: [`demo-spring-boot/README.md`](../../demo-spring-boot/README.md)  
- Project verification: `docs/verification.md` / `python harness/scripts/verify.py`
