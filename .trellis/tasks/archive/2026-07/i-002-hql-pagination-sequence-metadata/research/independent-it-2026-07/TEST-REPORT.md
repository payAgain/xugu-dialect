# xugu-dialect 7.4.5.Final 实库集成测试报告

| 项 | 内容 |
|----|------|
| 被测对象 | `com.xugu:xugu-dialect:7.4.5.Final`（`xugu-dialect-7.4.5.Final.jar`） |
| 方言类 | `com.xugu.dialect.XuguDialect` |
| Hibernate | **7.4.5.Final** |
| 数据库 | XuguDB **12.0**（真实实例） |
| JDBC | `xugu-jdbc-12.3.6` / `com.xugu.cloudjdbc.Driver` |
| 测试工程 | `E:\Work\java\xugu-dialect-independent-it`（独立编写，未复用 xugu-dialect 单测） |
| 测试日期 | 2026-07-15 |
| 执行命令 | `mvn test` |
| **结论** | **有条件通过（20/22）** |

---

## 1. 结果总览

| 指标 | 数值 |
|------|------|
| 用例总数 | 22 |
| 通过 | **20** |
| 失败 | **1**（`SchemaValidateIT`） |
| 错误 | **1**（`QueryOperationsIT.hqlPaginationWorks`） |
| 跳过 | 0 |
| 通过率 | **90.9%** |

```
████████████████████░░  20 PASS
█                       1 FAIL  (validate)
█                       1 ERROR (HQL 分页)
```

---

## 2. 测试环境

| 配置 | 值 |
|------|-----|
| JDBC URL | `jdbc:xugu://127.0.0.1:5138/SYSTEM?compatiblemode=NONE&identity_mode=2` |
| 用户名 / 密码 | `SYSDBA` / `SYSDBA` |
| JDK | 21（编译目标 17） |
| DDL 策略 | 多数用例 `create-drop`；validate 专项单独验证 |
| 连接验证 | `select 1 from dual` 成功；product=`XuguDB` |

---

## 3. 用例明细

| 模块 | 测试类 | 用例数 | 结果 | 验证点 |
|------|--------|--------|------|--------|
| 引导 | `DialectBootstrapIT` | 3 | ✅ | 显式方言、SPI 自动识别、基础查询 |
| DDL | `SchemaGenerationIT` | 2 | ✅ | 多实体建表、序列 NEXTVAL |
| 主键 SEQUENCE | `SequenceGenerationIT` | 1 | ✅ | `@SequenceGenerator` persist/load |
| 主键 UUID | `UuidGenerationIT` | 1 | ✅ | `@UuidGenerator` |
| 主键 IDENTITY | `IdentityNativeIT` | 1 | ✅ | Native INSERT + `last_insert_id()` |
| 主键 IDENTITY | `IdentityOrmIT` | 2 | ✅ | ORM persist 回填 ID、`@Version` |
| 主键 TABLE | `TableGeneratorIT` | 1 | ✅ | `@TableGenerator` |
| CRUD | `CrudCompositeKeyIT` | 1 | ✅ | `@EmbeddedId` 增删改查 |
| 关联 | `RelationshipMappingIT` | 1 | ✅ | OneToMany 级联 |
| 继承 | `InheritanceMappingIT` | 1 | ✅ | SINGLE_TABLE 多态加载 |
| 类型 | `TypeMappingIT` | 2 | ✅ | BOOLEAN/枚举/时间/LOB、JSON |
| 查询 | `QueryOperationsIT`（聚合/Bulk） | 2 | ✅ | count/avg、bulk update |
| 查询 | `QueryOperationsIT`（分页） | 1 | ❌ | HQL `setFirstResult/setMaxResults` |
| 分页对照 | `PaginationNativeIT` | 1 | ✅ | Native `LIMIT/OFFSET` |
| Schema | `SchemaSequenceMetadataIT` | 1 | ✅ | `all_sequences` 可见（诊断） |
| Schema | `SchemaValidateIT` | 1 | ❌ | `hbm2ddl.auto=validate` |

---

## 4. 失败项分析

### 4.1 HQL 分页（ERROR）— 优先级 P0

| 项 | 说明 |
|----|------|
| 用例 | `QueryOperationsIT.hqlPaginationWorks` |
| 异常 | `[E19132] unexpected OFFSET` |
| 实际 SQL | `... offset ? rows fetch first ? rows only` |
| 对照 | Native `limit 2 offset 1` **成功** |
| 根因 | 方言有 `XuguLimitHandler`，但 `getSqlAstTranslatorFactory()` 为 `null`。Hibernate 7 HQL 走 SQL AST 默认 ANSI `OFFSET/FETCH`，未改写为虚谷 `LIMIT`。 |
| 建议 | 增加 `XuguSqlAstTranslator`（参考 MySQL），在 `visitOffsetFetchClause` 中调用 `renderCombinedLimitClause`，并注册 Factory。 |

### 4.2 Schema validate（FAIL）— 优先级 P1

| 项 | 说明 |
|----|------|
| 用例 | `SchemaValidateIT.validateSucceedsAfterCreate` |
| 异常 | `Schema validation: missing sequence [AUD_ORDER_SEQ]` |
| 对照 | 序列已创建；`NEXTVAL` 可用；`all_sequences` 可查到 |
| 根因 | `getQuerySequencesString()` 未覆盖（为 `null`）→ 使用 `SequenceInformationExtractorNoOpImpl` → validate 抽不到任何序列。 |
| 建议 | 覆盖 `getQuerySequencesString()`（查询 `all_sequences` 并映射列名），或提供自定义 `SequenceInformationExtractor`。 |

---

## 5. 与上次审计（2026-07-13）对照

| 历史问题 | 上次 | 本次 |
|----------|------|------|
| IDENTITY ORM 无回填 ID | ❌ P0 阻塞 | ✅ 已修复 |
| TABLE 生成器 E16023 | ❌ P1 | ✅ 已修复 |
| Schema validate | ❌ P2（`sequence_catalog`） | ❌ 仍失败（NoOp 抽取器） |
| SEQUENCE / UUID / 复合主键 | ✅ | ✅ |
| 关联 / 继承 / 类型 / HQL 聚合·Bulk | 被 IDENTITY 拖死 | ✅ 本次通过 |
| HQL 分页 | 未完整确认 | ❌ **新确认的 P0** |

---

## 6. 能力矩阵（摘要）

| 能力 | 状态 | 备注 |
|------|------|------|
| 方言加载（显式 / SPI） | ✅ | |
| DDL create-drop | ✅ | |
| IDENTITY / SEQUENCE / UUID / TABLE / EmbeddedId | ✅ | IDENTITY 需 `identity_mode=2` |
| 关联 / 继承 / `@Version` | ✅ | |
| BOOLEAN / 枚举 / 时间 / LOB / JSON | ✅ | |
| HQL 聚合 / Bulk DML | ✅ | |
| Native LIMIT 分页 | ✅ | |
| HQL / Criteria / Pageable 分页 | ❌ | 缺 SqlAstTranslator |
| `hbm2ddl.auto=validate` | ❌ | 缺序列元数据查询 |

---

## 7. 结论与建议

**测试结论：有条件通过。**

核心 CRUD、主键策略（含上次阻塞的 IDENTITY/TABLE）、DDL、关联/继承/类型映射在实库下可用。  
上线前需处理：

1. **必须修复**：HQL/AST 分页 → `LIMIT` 改写（否则列表分页场景不可用）。  
2. **建议修复**：序列元数据抽取（否则 `validate` 不可用；可用 Flyway/Liquibase 临时规避）。

**生产建议**：在分页修复前，避免依赖 `setFirstResult`/`setMaxResults`/Spring Data `Pageable`；schema 管理勿使用 `validate`。

---

## 8. 复现方式

```powershell
cd E:\Work\java\xugu-dialect-independent-it
# 目录内需有 xugu-dialect-7.4.5.Final.jar、xugu-jdbc-12.3.6.jar
mvn test
```

详细根因与字节码侧证见同目录 `DIALECT_AUDIT_REPORT.md`。
