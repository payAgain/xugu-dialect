# 05 — Troubleshooting / 故障排查

← [04-feature-matrix.md](04-feature-matrix.md) · [Index](README.md) · Next: [06-consumer-path.md](06-consumer-path.md) · Recipes: [08-schema-tooling-recipes.md](08-schema-tooling-recipes.md)

## 1. LIMIT vs FOR UPDATE 顺序错误

**症状：** 分页 + 悲观锁查询被数据库拒绝；或与「标准」MySQL 风格 `LIMIT … FOR UPDATE` 行为不一致。

**原因：** 虚谷语法要求锁子句在限制子句之前。本方言两条路径均生成：

```text
… FOR UPDATE [OF …] LIMIT … [OFFSET …] [NOWAIT|WAIT …]
```

- **Criteria / native LimitHandler：** `XuguLimitHandler`
- **HQL / Criteria SQL AST：** `XuguSqlAstTranslator`（I-002/P-001）

**不要** 手写或假设 Hibernate 默认的 `LIMIT … FOR UPDATE` 顺序。

**锁语义专节：** 无 SKIP LOCKED、无 FOR SHARE、`PESSIMISTIC_READ`→排他 `FOR UPDATE` — [07-lock-integration.md](07-lock-integration.md)。

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

## 9. HQL `json_arrayagg` / `json_objectagg` 不可用

**症状：** HQL 聚合 JSON 函数报未知函数，或 JSON 功能被禁用。

**处理：** Hibernate 7.4 需开启 JSON 函数（IT / 应用配置示例）：

```properties
hibernate.query.json_functions_enabled=true
```

（或等价 `QuerySettings.JSON_FUNCTIONS_ENABLED`）。方言已注册虚谷原生 `json_arrayagg` / `json_objectagg`（I-003 C-JSON-*）。见矩阵 [`feature-matrix-i003-ruler-c.md`](../../contracts/feature-matrix-i003-ruler-c.md)。

## 10. Bulk insert（JOINED + IDENTITY）

**基线状态（C-BULK-002）：** **covered-live**（I-007/P-002）— 方言 `getFallbackSqmInsertStrategy` → `LocalTemporaryTableInsertStrategy`（与 update/delete 同一本地临时表 DDL 策略）。离线单元测试 `XuguBulkMutationSupportTest#fallbackSqmInsertStrategyWired_C_BULK_002`；门控真库 IT `XuguBulkMutationIT#bulkInsertOnJoinedInheritanceWithIdentitySucceeds_C_BULK_002`（`XUGU_RUN_IT=true`）。

**已验证路径：** bulk **update/delete/insert** 经 `XuguBulkMutationIT` 在门控真库下 PASS（C-BULK-001 + C-BULK-002）。

**集成建议：**

- 生产环境可使用 bulk update/delete/**insert** 回退路径（JOINED + IDENTITY 映射）。
- 若遇 JDBC 异常，确认 `compatiblemode=NONE` 与显式 `XuguDialect`；IDENTITY 常规 persist 仍走 `getDefaultUseGetGeneratedKeys=false` + `last_insert_id()`（I-004）。
- 回归基线 SSOT：[`contracts/production-regression-baseline.md`](../../contracts/production-regression-baseline.md) C-BULK-002 行。

## 11. Native ENUM DDL 不会发出

**症状：** 期望 MySQL 风格 `ENUM('a','b')` 列类型。

**原因：** 虚谷无明确 native ENUM 文档；`getEnumTypeDeclaration` 返回 **`null`**（C-DDL-004 文档不允许）。改用字符串/校验约束等文档允许映射。

## 12. 保留字表 IDENTITY：`字段NAME不存在`（E16007）

**症状：** 对保留字物理表做 IDENTITY persist 时报 `[E16007] 字段NAME不存在`；或 `CREATE TABLE IF NOT EXISTS "order"` 日志伴随「表已存在」警告。

**原因：** 目标库若已有应用表 `Order`（列集不同），`CREATE IF NOT EXISTS` 不会重建；且存在依赖时 `DROP "order"` 可能失败（E5025）。插入列与实表不一致即触发 E16007。这与方言 `getDefaultUseGetGeneratedKeys=false` + `last_insert_id()` 修复无关。

**处理：** IT / 示例勿与业务表名硬撞车；方言侧保留字仍须双引号（如 `"select"` / `"order"`）。回归 IT 使用 `"select"` 覆盖同一 quoting + IDENTITY 回填路径。见矩阵 A-IDN-* / `XuguReservedIdentityIT`。

## 13. ENCRYPT BY / PARTITION / catalog 工具边界

**完整配方（Flyway + Support 拼装）：** [08-schema-tooling-recipes.md](08-schema-tooling-recipes.md) — **hbm2ddl / SchemaExport never emits** PARTITION / ENCRYPT / functional·BITMAP；**不强制**改 Exporter。

**ENCRYPT BY（A-DDL-009）：** 方言 **SchemaExport 不 emit**（`supportsEncryptByInSchemaExport()=false`，**known-limit-documented**）。另：**环境**层需已有 encryptor（`CREATE ENCRYPTOR` → SYSSSO / `ACL_SSO`）；不可见或 E18012 时门控 IT **honest skip** — 与「方言缺 helper」分开看。应用侧经 **Flyway / native**（`XuguTableDdlSupport`）使用文档允许的 `ENCRYPT BY`；勿假设 `hbm2ddl` 自动导出。

**PARTITION BY（A-DDL-008）：** LIST/RANGE/HASH 形状由 `XuguTableDdlSupport` 锁定；native LIST IT 存在。**schema export 不声称** 分区表（`supportsPartitionByInSchemaExport()=false`）。生产分区表请用迁移 / native，勿依赖 Hibernate 自动建分区。

**Catalog 限定（A-SCH-003）：** JDBC 可对齐 `current_db()`；对象名渲染保持 **`schema.table`**（DATABASE 非会话 SET）。勿期望 Hibernate 发出 `catalog.schema.table` 三层限定作为默认对象名。

**高级索引（A-SCH-017）：** functional / BITMAP 走 `XuguIndexDdlSupport` + Flyway/native；schema export 仍以 B-tree（A-SCH-016）为主 — 勿把高级索引当作 export 覆盖面。

## 14. INTERVAL / XML 列 / 几何 / UDT 深度类型边界

| 能力 | 矩阵 | 基线状态 | 集成边界（I-010 P-002…P-004） |
|---|---|---|---|
| **INTERVAL** | A-TYP-014 | **covered-live**（I-010 Accept；live @5287） | Hibernate 暴露 DURATION / INTERVAL_SECOND → `XuguIntervalJdbcType` 字符串绑定；实体 IT `intervalEntityOrmRoundTrip_A_TYP_014` PASS。其余 11 虚谷子类型仍 tooling/native；输出受 `DEF_INTERVAL_STYLE` 影响。 |
| **XML 列** | A-TYP-016 | **covered-live**（I-010 Accept；live @5287） | 推荐 `String` + `@JdbcTypeCode(SqlTypes.SQLXML)`（`XuguXmlJdbcType`）；实体 IT `xmlEntityOrmRoundTrip_A_TYP_016` PASS。**勿**映射 `java.sql.SQLXML` / 依赖默认 `XmlJdbcType`。native 往返仍保留。 |
| **几何 / 空间** | A-TYP-017 / A-FUN-020 | 类型与函数均为 **covered-live**（POINT 实体 ORM + HQL Session；live @5287） | 推荐 `String` + `@JdbcTypeCode(POINT\|GEOMETRY)`（`XuguPointJdbcType`）；实体 IT `pointEntityOrmRoundTrip_A_TYP_017` PASS。LINE/LSEG/BOX/PATH/POLYGON/CIRCLE 仍 native/tooling。简单 2D（非 PostGIS）。 |
| **UDT** | A-TYP-018 | **known-limit-documented** | CREATE TYPE / constructor / DROP TYPE 的 native / Flyway 路径（见 [08-recipes](08-schema-tooling-recipes.md#recipe-d--udt-create-typea-typ-018交叉)）；**不**声称 ORM 实体列映射 UDT。 |

排障提示：对照 baseline call-out 与门控 IT（`XuguIntervalTypeIT` / `XuguXmlTypeAndFunctionsIT` / `XuguGeometricTypeAndFunctionsIT` / `XuguUdtTypeIT`）。XMLTABLE（A-FUN-021）/ ENCRYPT（A-DDL-009）等真实 KL 仍见对应小节 — 勿吞掉。

## 15. XML 函数 / XMLTABLE（A-FUN-021）

**基线状态：** **known-limit-documented**（I-009/P-003；HQL Session I-010/P-005）— **不是** covered-live。SSOT：[`production-regression-baseline.md`](../../contracts/production-regression-baseline.md) § A-FUN-021。

**已注册 / 可探测：** HQL `Session.createQuery` 正例 `xmlelement` / `xmlquery`（`xmlquery` 渲染 `PASSING … RETURNING CONTENT`）；`xmltable` 仅注册名；XML `EXTRACT` 走 native SQL（勿与时间 `extract(field from …)` 混淆）。

**XMLTABLE：** 文档注明当前版本 **仅单节点**、不支持集群。单节点/集群上空结果时门控 IT 做 assumption **skip**（永不硬失败）。勿当作 ORM 集群能力或 covered-live。

**对比：** `json_table` **文档不允许**（C-JSON-006）— 禁止发明 SQL；与 XMLTABLE 无关。

## Still stuck?

- Contract: [`contracts/xugu-dialect.contract.md`](../../contracts/xugu-dialect.contract.md)  
- Matrix (Definition A): [`contracts/feature-matrix-definition-a.md`](../../contracts/feature-matrix-definition-a.md)  
- Matrix (I-003 ruler C): [`contracts/feature-matrix-i003-ruler-c.md`](../../contracts/feature-matrix-i003-ruler-c.md)  
- Demo README: [`demo-spring-boot/README.md`](../../demo-spring-boot/README.md)  
- Project verification: `docs/verification.md` / `mvn -q test`
