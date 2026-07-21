# 08 — Schema tooling recipes / 模式工具配方

← [07-lock-integration.md](07-lock-integration.md) · [Index](README.md) · Related: [05-troubleshooting.md §13](05-troubleshooting.md#13-encrypt-by--partition--catalog-工具边界)

## Purpose

生产侧需要 **PARTITION BY**、**ENCRYPT BY**、**functional / BITMAP 索引**，以及交叉的 **UDT `CREATE TYPE`** 时，用本页配方。

**推荐路径：** Flyway（或等价迁移）+ 方言 **native Support** 拼装 SQL。  
**不要依赖：** `hbm2ddl` / Hibernate `SchemaExport` — 这些工具 **从不 emit** 下列子句（方言显式 known-limit；**不强制**改 Exporter）。

| Matrix ID | Capability | SchemaExport / hbm2ddl | Recommended assembly |
|---|---|---|---|
| **A-DDL-008** | `PARTITION BY` LIST/RANGE/HASH | **never emits** (`supportsPartitionByInSchemaExport()=false`) | Flyway / native via `XuguTableDdlSupport` |
| **A-DDL-009** | `ENCRYPT BY` / encryptor | **never emits** (`supportsEncryptByInSchemaExport()=false`) | Flyway / native via `XuguTableDdlSupport`（需已有 encryptor） |
| **A-SCH-017** | Functional / BITMAP indexes | **never emits** (`supportsAdvancedIndexInSchemaExport()=false`) | Flyway / native via `XuguIndexDdlSupport`；export 仍以 B-tree（**A-SCH-016**）为主 |
| **A-TYP-018** | UDT `CREATE TYPE` | **not ORM / not export claim** | Flyway / native via `XuguUdtTypeSupport`；**不**声称实体列映射 UDT |

SSOT：[`contracts/production-regression-baseline.md`](../../contracts/production-regression-baseline.md) § A-DDL-008 / A-DDL-009 / A-SCH-017 / A-TYP-018；矩阵：[`feature-matrix-definition-a.md`](../../contracts/feature-matrix-definition-a.md)。

## Assembly model（Flyway + Support）

```text
┌─────────────────────┐     ┌──────────────────────────────────┐
│ Hibernate entities  │     │ Flyway V*__*.sql (or Session     │
│ + ddl-auto=validate │  +  │ createNativeQuery)               │
│ / update (basic)    │     │ ← SQL from *Support helpers      │
└─────────────────────┘     └──────────────────────────────────┘
         │                                    │
         ▼                                    ▼
   B-tree tables/indexes              PARTITION / ENCRYPT /
   (A-SCH-016, ordinary DDL)          advanced index / CREATE TYPE
```

1. 用 Hibernate 管普通实体表与 **B-tree** 索引（A-SCH-016）。
2. 用 **Flyway**（demo 路径见 [06-consumer-path.md § B-FLY-001](06-consumer-path.md#i-007-track-b-deepeningp-005)）或启动后 **native SQL** 补齐分区 / 加密 / 高级索引 / UDT。
3. 拼装时优先调用方言 Support 静态方法，避免手写漂移：
   - `com.xugu.dialect.ddl.XuguTableDdlSupport`
   - `com.xugu.dialect.ddl.XuguIndexDdlSupport`
   - `com.xugu.dialect.type.XuguUdtTypeSupport`
4. `hbm2ddl.auto=create|update` **不会**补上这些子句；`validate` 也不会因「缺 PARTITION/ENCRYPT」而替你生成它们。

**Boot + Flyway 提示：** 生产配方常用 `ddl-auto=validate`（或 `none`）+ Flyway `enabled=true`，让迁移拥有分区/加密 DDL；demo 默认 `ddl-auto=update` + Flyway 关，仅作消费者冒烟，勿照搬为分区表生产拓扑。

---

## Recipe A — PARTITION BY（A-DDL-008）

**边界：** 方言锁定 LIST/RANGE/HASH 形状；native IT 覆盖 LIST（`listPartitionNativeRoundTrip_A_DDL_008`）。**SchemaExport 不声称**分区表。

### LIST（文档示例形）

```java
import com.xugu.dialect.ddl.XuguTableDdlSupport;

String sql = XuguTableDdlSupport.createListPartitionTableSql(
    "sales_list",
    "id bigint primary key, region varchar(32), amount int",
    "region",
    "partition p_east values('east'), partition p_west values('west')"
);
// → create table … partition by list (region) partitions(…)
```

### RANGE

```java
String sql = XuguTableDdlSupport.createRangePartitionTableSql(
    "sales_range",
    "id bigint primary key, sale_date date, amount int",
    "sale_date",
    "partition p2024 values less than ('2025-01-01'),\n"
        + "partition pmax values less than (maxvalue)"
);
```

### HASH

```java
String sql = XuguTableDdlSupport.createHashPartitionTableSql(
    "sales_hash",
    "id bigint primary key, sku varchar(64)",
    "id",
    4
);
```

### Flyway 片段（示意）

```sql
-- V2__sales_list_partition.sql  （内容与 Support 输出一致即可）
create table sales_list(
id bigint primary key, region varchar(32), amount int
)
partition by list (region)
partitions(partition p_east values('east'), partition p_west values('west'));
```

**不要：** 指望 `@Table` / `hbm2ddl` 导出 `PARTITION BY`；**不要**为「让 SchemaExport 发出分区」而改 Exporter（本 Initiative **明确不强制**）。

---

## Recipe B — ENCRYPT BY（A-DDL-009）

**两层边界（勿混为一谈）：**

| Layer | Meaning |
|---|---|
| **Dialect known-limit** | `supportsEncryptByInSchemaExport()=false` — SchemaExport / hbm2ddl **never emits** `ENCRYPT BY` |
| **Environment / privilege** | `CREATE ENCRYPTOR` 需 SYSSSO / `ACL_SSO`；`sys_encryptors` 不可见或 E18012 时，native IT **honest skip** — 这是 **环境**限制，不是「方言缺 helper」 |

有可见 encryptor 时再用下列配方；无权限时 **skip / 换环境**，勿把 assumption 包成失败，也勿要求方言在 export 中「假装支持」。

### 建 encryptor（特权账号；示意）

```java
String createEnc = XuguTableDdlSupport.createEncryptorSql("app_enc", /* key from vault */ "…");
// create encryptor 'app_enc' by '…'
```

### 表级 ENCRYPT BY

```java
String sql = XuguTableDdlSupport.createTableWithEncryptBySql(
    "secure_t",
    "id bigint primary key, payload varchar(256)",
    "app_enc"
);
// create table … ) encrypt by 'app_enc'
```

列级子句亦可单独拼：`XuguTableDdlSupport.encryptByClause("app_enc")` → `encrypt by 'app_enc'`（嵌入文档允许的列/表语法；以官方 `create.md` / `encryptor.md` 为准）。

### Flyway 片段（示意）

```sql
-- 前提：encryptor 已由 DBA / 特权迁移创建
create table secure_t(
id bigint primary key, payload varchar(256)
) encrypt by 'app_enc';
```

---

## Recipe C — Advanced indexes（A-SCH-017）

**边界：** SchemaExport 只覆盖普通 **B-tree**（A-SCH-016）。Functional / BITMAP 走 native Support；spatial / LOCAL / GLOBAL 分区索引 **超出**本子集声明面。

### Functional

```java
import com.xugu.dialect.ddl.XuguIndexDdlSupport;

String sql = XuguIndexDdlSupport.createFunctionalIndexSql(
    "tab_test_2", "idx_func_len_name", "len(name)"
);
// create index idx_func_len_name on tab_test_2 (len(name))
```

### BITMAP

```java
String sql = XuguIndexDdlSupport.createBitmapIndexSql(
    "tab_test_2", "idx_bm_birth", "birth"
);
// create index idx_bm_birth on tab_test_2 (birth) indextype is bitmap
```

### Flyway 片段（示意）

```sql
create index idx_func_len_name on tab_test_2 (len(name));
create index idx_bm_birth on tab_test_2 (birth) indextype is bitmap;
```

门控 IT：`functionalAndBitmapIndexNativeRoundTrip_A_SCH_017`。

---

## Recipe D — UDT CREATE TYPE（A-TYP-018，交叉）

与分区/加密相同装配模型：类型 DDL 进迁移或 native；**不**声称 ORM `@JdbcTypeCode` / STRUCT 实体列映射。

```java
import com.xugu.dialect.type.XuguUdtTypeSupport;

String objectType = XuguUdtTypeSupport.createObjectTypeSql(
    "udt_obj_type", "id bigint, label varchar(32)"
);
String varrayType = XuguUdtTypeSupport.createVarrayTypeSql(
    "var_test", 3, "varchar"
);
String nestedTable = XuguUdtTypeSupport.createTableTypeSql(
    "udt_tab_type", "bigint"
);
// later: drop type …
String drop = XuguUdtTypeSupport.dropTypeSql("udt_obj_type");
```

列类型名即已有 UDT 名：`XuguUdtTypeSupport.columnTypeFor("udt_obj_type")`。  
`XuguDialect#supportsJdbcUserDefinedTypes()` = `false`。

---

## Checklist for integrators

- [ ] 确认矩阵行仍为 **known-limit-documented**（A-DDL-008/009、A-SCH-017、A-TYP-018）— 诚实路径是 native/Flyway，不是 export covered-live
- [ ] 生产 DDL 放在 Flyway（或受控 native），实体侧用 `validate`/`none` 避免「假 create」覆盖分区表
- [ ] ENCRYPT：先解决 **环境 encryptor**，再谈应用 SQL；勿要求 SchemaExport 发出 `ENCRYPT BY`
- [ ] 高级索引：普通唯一/B-tree 可走 Hibernate；functional/BITMAP 必须迁移补齐
- [ ] UDT：只做 schema 类型生命周期；不做全量 ORM 实体映射（I-010 明确排除）

## Still stuck?

- 排障短注：[05-troubleshooting.md §13](05-troubleshooting.md#13-encrypt-by--partition--catalog-工具边界)
- Contract / baseline call-outs 如上表 SSOT
- Demo Flyway 开关：[06-consumer-path.md](06-consumer-path.md)
