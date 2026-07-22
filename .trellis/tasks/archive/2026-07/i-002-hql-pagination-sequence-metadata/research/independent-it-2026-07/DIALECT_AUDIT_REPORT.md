# 虚谷 Hibernate 方言包（xugu-dialect 7.4.5.Final）独立复测报告

| 项目 | 内容 |
|------|------|
| 审核对象 | `com.xugu:xugu-dialect:7.4.5.Final`（本地 jar：`xugu-dialect-7.4.5.Final.jar`） |
| 目标 ORM | Hibernate ORM **7.4.5.Final** |
| 目标数据库 | XuguDB 12.0（实库） |
| JDBC 驱动 | `xugu-jdbc-12.3.6.jar` / `com.xugu.cloudjdbc.Driver` |
| 方言类 | `com.xugu.dialect.XuguDialect` |
| 审核日期 | 2026-07-15 |
| 验证工程 | `E:\Work\java\xugu-dialect-independent-it`（独立集成测试，未复用 xugu-dialect 单测） |
| 对照基线 | `E:\Work\java\DIALECT_AUDIT_REPORT.md`（2026-07-13，1.0.0 / Hibernate 7.0） |
| 测试结果 | **22 项：20 通过 / 1 失败 / 1 错误** |
| 审核结论 | **有条件通过** — 上次 P0 IDENTITY、P1 TABLE 已修复；**HQL 分页**与 **Schema validate 序列元数据**仍阻塞 |

---

## 1. 测试环境

| 配置项 | 值 |
|--------|-----|
| JDBC URL | `jdbc:xugu://127.0.0.1:5138/SYSTEM?compatiblemode=NONE&identity_mode=2` |
| 用户/密码 | `SYSDBA` / `SYSDBA` |
| DDL 策略 | 默认 `hibernate.hbm2ddl.auto=create-drop`（validate 专项除外） |
| JDK | 21（`maven.compiler.release=17`） |
| 执行命令 | `mvn test` |

---

## 2. 与上次审计问题对照

| 上次问题 | 等级 | 本次结果 | 说明 |
|----------|------|----------|------|
| IDENTITY ORM `persist` 无回填 ID | P0 | ✅ **已修复** | `IdentityOrmIT` 2/2；含 `@Version` |
| IDENTITY Native `LAST_INSERT_ID` | — | ✅ 仍可用 | `IdentityNativeIT` |
| TABLE 生成器 E16023 | P1 | ✅ **已修复** | `TableGeneratorIT` |
| Schema validate / 序列元数据 | P2 | ❌ **仍失败** | 现象变为 `missing sequence`；根因见 §4.2 |
| SEQUENCE / UUID / 复合主键 | — | ✅ 通过 | 与上次一致 |
| 关联 / 继承 / 类型映射 | 上次被 IDENTITY 阻塞 | ✅ **本次通过** | 改用 SEQUENCE 主键后完整验证；JSON/BOOLEAN/LOB 通过 |
| HQL 聚合 / Bulk DML | 上次被阻塞 | ✅ 通过 | `QueryOperationsIT` 中 2 项 |
| HQL 分页 LIMIT | 源码有 LimitHandler、上次未测通 | ❌ **本次失败（新确认阻塞）** | 生成 ANSI `OFFSET/FETCH`，虚谷不支持 |

---

## 3. 测试明细（22 项）

| # | 测试类 | 结果 | 覆盖能力 |
|---|--------|------|----------|
| 1 | `DialectBootstrapIT` ×3 | ✅ | 显式方言 / SPI 自动识别 / 简单查询 |
| 2 | `SchemaGenerationIT` ×2 | ✅ | create-drop 多实体 DDL + 序列 NEXTVAL |
| 3 | `SequenceGenerationIT` | ✅ | SEQUENCE ORM |
| 4 | `UuidGenerationIT` | ✅ | UUID 主键 |
| 5 | `IdentityNativeIT` | ✅ | Native IDENTITY + last_insert_id |
| 6 | `IdentityOrmIT` ×2 | ✅ | IDENTITY ORM + `@Version` |
| 7 | `TableGeneratorIT` | ✅ | `@TableGenerator` |
| 8 | `CrudCompositeKeyIT` | ✅ | `@EmbeddedId` CRUD |
| 9 | `RelationshipMappingIT` | ✅ | OneToMany 级联 |
| 10 | `InheritanceMappingIT` | ✅ | SINGLE_TABLE 继承 |
| 11 | `TypeMappingIT` ×2 | ✅ | BOOLEAN/枚举/时间/LOB + JSON |
| 12 | `QueryOperationsIT` 聚合/Bulk | ✅ | HQL count/avg、bulk update |
| 13 | `PaginationNativeIT` | ✅ | Native `LIMIT/OFFSET` |
| 14 | `SchemaSequenceMetadataIT` | ✅ | `all_sequences` 可见（诊断） |
| 15 | `QueryOperationsIT.hqlPaginationWorks` | ❌ | HQL setFirstResult/setMaxResults |
| 16 | `SchemaValidateIT` | ❌ | `hbm2ddl.auto=validate` |

---

## 4. 仍存在的阻塞项

### 4.1 【P0】HQL/Criteria 分页生成 ANSI OFFSET/FETCH

| 项目 | 内容 |
|------|------|
| **现象** | `[E19132] 语法错误 … unexpected OFFSET` |
| **实际 SQL** | `... order by ... offset ? rows fetch first ? rows only` |
| **对比** | Native `limit ? offset ?` **成功**（`PaginationNativeIT`） |
| **方言现状** | 已提供 `XuguLimitHandler`（`limit ? offset ?`）；`supportsFetchClause(ROWS_ONLY)=false`；但 **`getSqlAstTranslatorFactory()` 为 null** |
| **根因** | Hibernate 7 SQM/SQL AST 默认走 `AbstractSqlAstTranslator.renderOffsetFetchClause`（ANSI）。MySQL 等方言通过自定义 `SqlAstTranslator` 覆盖为 `renderCombinedLimitClause`（LIMIT）。仅有 `LimitHandler` **不足以**改写 HQL 分页 SQL。 |
| **修复建议** | 增加 `XuguSqlAstTranslator`（参考 `MySQLSqlAstTranslator.visitOffsetFetchClause`），并在 `XuguDialect.getSqlAstTranslatorFactory()` 注册。 |

### 4.2 【P1】Schema validate 找不到序列

| 项目 | 内容 |
|------|------|
| **现象** | `Schema validation: missing sequence [AUD_ORDER_SEQ]` |
| **对比** | 序列已创建且 `AUD_ORDER_SEQ.NEXTVAL` 可用；`all_sequences` 可查到 `AUD_ORDER_SEQ` |
| **方言现状** | `getQuerySequencesString() == null` → 默认 `SequenceInformationExtractorNoOpImpl`（不抽取任何序列） |
| **JDBC** | `DatabaseMetaData.getSequences` 不可用（诊断输出 `getSequences:unsupported`） |
| **与上次差异** | 上次是 `sequence_catalog` 列缺失；本次因 NoOp 抽取器直接“看不见”序列 |
| **修复建议** | 覆盖 `getQuerySequencesString()`（如查询 `all_sequences`，列名对齐 `SequenceInformationExtractorLegacyImpl`），或提供自定义 `SequenceInformationExtractor`。 |

---

## 5. 生产可用性评估（更新）

### 可直接使用

- `@GeneratedValue(IDENTITY)`（需 URL `identity_mode=2`）
- `@SequenceGenerator` / `@UuidGenerator` / `@TableGenerator` / `@EmbeddedId`
- `hbm2ddl.auto=create` / `create-drop` / `update`
- 关联、SINGLE_TABLE 继承、JSON/BOOLEAN/LOB、HQL 聚合与 Bulk DML
- Native SQL 分页（`LIMIT/OFFSET`）

### 应避免

- HQL / Criteria / Spring Data 的 `setFirstResult`/`setMaxResults`（Pageable）——当前会生成非法 OFFSET/FETCH
- `hibernate.hbm2ddl.auto=validate`（序列元数据不可见）

### 风险等级

| 等级 | 项 | 影响 |
|------|-----|------|
| 🔴 高 | HQL 分页 | 几乎所有带分页的列表查询不可用 |
| 🟡 中 | Schema validate | CI schema 校验需改用 Flyway/Liquibase 等 |
| 🟢 低 | 上次 P0/P1 | IDENTITY / TABLE 已恢复 |

---

## 6. 总体结论

相对 2026-07-13 报告，`xugu-dialect 7.4.5.Final` 在 **IDENTITY ORM** 与 **TABLE 生成器** 上已跨过当时的生产阻塞门槛，核心 CRUD/DDL/类型映射在实库下验证通过。

当前最大缺口转为：**缺少 SQL AST 层 LIMIT 改写**，导致标准 Hibernate 分页路径失败；其次是 **未提供序列元数据查询**，导致 `validate` 不可用。

**评级：有条件通过（Conditional Pass）** — 无分页且不用 validate 的场景可用；带分页的典型 Web/JPA 应用仍需先修复 SqlAstTranslator。

---

## 附录：复现

```powershell
cd E:\Work\java\xugu-dialect-independent-it
# 确保本目录存在 xugu-dialect-7.4.5.Final.jar 与 xugu-jdbc-12.3.6.jar
$env:Path = "C:\Users\admin\tools\apache-maven-3.9.9\bin;D:\app\JDK-21\bin;" + $env:Path
$env:JAVA_HOME = "D:\app\JDK-21"
mvn test
```
