# 修复验证报告（I-002 hotfix 复测）

| 项 | 内容 |
|----|------|
| 验证日期 | 2026-07-16 |
| 源码工程 | `E:\Work\java\xugu-dialect`（自行 `mvn -pl dialect -am -DskipTests clean package`） |
| 产物 | `dialect/target/xugu-dialect-7.4.5.Final.jar` → 复制至本目录 |
| Hibernate | 7.4.5.Final |
| 数据库 | XuguDB 12.0 实库 |
| 测试工程 | `E:\Work\java\xugu-dialect-independent-it`（独立 IT，非 xugu-dialect 单测） |
| **结论** | **全部通过：22/22** |

---

## 1. 编译与产物确认

```text
cd E:\Work\java\xugu-dialect
mvn -pl dialect -am -DskipTests clean package
# → dialect/target/xugu-dialect-7.4.5.Final.jar
```

运行时探测（新 jar）：

| API | 修复前 | 修复后 |
|-----|--------|--------|
| `getSqlAstTranslatorFactory()` | `null` | 非空（注册 `XuguSqlAstTranslator`） |
| `getQuerySequencesString()` | `null` | `select * from all_sequences` |
| `getSequenceInformationExtractor()` | `SequenceInformationExtractorNoOpImpl` | `SequenceInformationExtractorXuguDatabaseImpl` |
| jar 内类 | 无 `sql/ast` | 含 `com.xugu.dialect.sql.ast.XuguSqlAstTranslator` |

---

## 2. 原失败项复测

| 原问题 | 用例 | 结果 |
|--------|------|------|
| HQL 分页 ANSI OFFSET/FETCH | `QueryOperationsIT.hqlPaginationWorks` | ✅ PASS |
| `hbm2ddl.auto=validate` 找不到序列 | `SchemaValidateIT` | ✅ PASS |
| Native LIMIT 对照 | `PaginationNativeIT` | ✅ PASS |
| all_sequences 诊断 | `SchemaSequenceMetadataIT` | ✅ PASS |

---

## 3. 全量回归（22 项）

| 测试类 | 结果 |
|--------|------|
| DialectBootstrapIT ×3 | ✅ |
| SchemaGenerationIT ×2 | ✅ |
| SequenceGenerationIT | ✅ |
| UuidGenerationIT | ✅ |
| IdentityNativeIT | ✅ |
| IdentityOrmIT ×2 | ✅ |
| TableGeneratorIT | ✅ |
| CrudCompositeKeyIT | ✅ |
| RelationshipMappingIT | ✅ |
| InheritanceMappingIT | ✅ |
| TypeMappingIT ×2 | ✅ |
| QueryOperationsIT ×3（含分页） | ✅ |
| PaginationNativeIT | ✅ |
| SchemaSequenceMetadataIT | ✅ |
| SchemaValidateIT | ✅ |

**合计：Tests run: 22, Failures: 0, Errors: 0, Skipped: 0**

---

## 4. 与历史问题闭环

| 问题 | 首次发现 | 修复验证 |
|------|----------|----------|
| IDENTITY ORM | 2026-07-13 | 此前已过；本次仍 ✅ |
| TABLE 生成器 | 2026-07-13 | 此前已过；本次仍 ✅ |
| HQL 分页 OFFSET/FETCH | 2026-07-15 | **本次 ✅ 已修复** |
| Schema validate 序列元数据 | 2026-07-13 / 07-15 | **本次 ✅ 已修复** |

---

## 5. 结论

从 `../xugu-dialect` 重新编译的 `xugu-dialect 7.4.5.Final` 已修复先前报告的 HQL 分页与 Schema validate 问题；独立实库全量回归 **22/22 通过**，可视为该轮 hotfix **验证通过**。
