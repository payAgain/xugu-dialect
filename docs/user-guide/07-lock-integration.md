# 07 — Lock integration / 悲观锁集成须知

← [05-troubleshooting.md](05-troubleshooting.md) · [Index](README.md) · Related: [04-feature-matrix.md](04-feature-matrix.md)

面向在 **XuguDB + Hibernate 7.4.5** 上使用 JPA 悲观锁的集成方。矩阵 SSOT：[`contracts/feature-matrix-definition-a.md`](../../contracts/feature-matrix-definition-a.md) A-LCK-* / A-PAG-*；契约：[`contracts/xugu-dialect.contract.md`](../../contracts/xugu-dialect.contract.md) §7.2。

## 集成方必读摘要

| 能力 | Xugu + 本方言 | 勿假设（如 PostgreSQL） |
|---|---|---|
| **悲观写锁** | `FOR UPDATE` / `FOR UPDATE OF …` / `NOWAIT` / `WAIT` | — |
| **SKIP LOCKED** | **不支持** — 方言 `supportsSkipLocked=false` | 不会发出 `SKIP LOCKED` |
| **FOR SHARE / 共享读锁** | **不支持** — 虚谷语法无 `FOR SHARE` | 无 PostgreSQL 式共享锁 |
| **`PESSIMISTIC_READ`** | Hibernate 映射为 **排他 `FOR UPDATE`**，**不是**共享读锁 | 并发读者可能被阻塞 |
| **分页 + 锁** | 语法顺序 **`FOR UPDATE … LIMIT … [WAIT …]`** | 非 `LIMIT … FOR UPDATE` |

真库行为证据归档：**I-008 P-005**（`harness/evidence/test/I-008/P-005/`）。本文档为 **P-002** 用户向专节；不发明未实现 SQL。

---

## JPA 锁模式 → 虚谷 SQL（本方言）

| JPA / Hibernate | 生成的锁子句 | 矩阵 |
|---|---|---|
| `LockModeType.PESSIMISTIC_WRITE` | `FOR UPDATE` [+ `OF` 列] [+ `NOWAIT` / `WAIT`] | A-LCK-001…003 |
| `LockModeType.PESSIMISTIC_READ` | **`FOR UPDATE`**（排他，非 share） | A-LCK-005 |
| `LockOptions.SKIP_LOCKED` | **不发出** — 请求应失败或不走 SKIP 路径 | A-LCK-004 / C-SKIP-001 |
| 分页 + 上述任一带锁模式 | **`FOR UPDATE` → `LIMIT` → `OFFSET` → `WAIT`** | A-PAG-001…002 |

方言实现锚点（只读参考）：`XuguDialect#getReadLockString`（无 `FOR SHARE`）、`supportsSkipLocked=false`、`XuguLimitHandler` / `XuguSqlAstTranslator` 锁顺序。

---

## 不支持 SKIP LOCKED

虚谷 `SELECT … FOR UPDATE` 语法 **无** `SKIP LOCKED` 变体。本方言：

- `supportsSkipLocked()` 返回 **`false`**
- 不会为 `LockOptions.SKIP_LOCKED` 或等价 API 拼接该关键字

**集成建议：** 勿在应用层假设「跳过已锁行」语义；需排队或重试时自行设计业务逻辑。详见矩阵 **A-LCK-004**、**C-SKIP-001**（文档不允许）。

---

## 不支持 FOR SHARE

虚谷文档允许的锁子句为 **`FOR UPDATE`** 等排他形式，**无** ANSI/PostgreSQL 式 **`FOR SHARE`** / **`FOR KEY SHARE`**。

本方言 **不会** 为 pessimistic read 发出 `FOR SHARE`。矩阵 **A-LCK-005** 状态为 **文档不允许**（Hibernate shim only）。

---

## PESSIMISTIC_READ → 排他 FOR UPDATE

Hibernate 在部分数据库上将 `PESSIMISTIC_READ` 映射为共享锁（如 `FOR SHARE`）。**在 Xugu 上，本方言将 read 锁同样映射为排他 `FOR UPDATE`**，以便语句可被数据库接受且语义一致于虚谷语法。

**含义：**

- 调用 `entityManager.lock(entity, LockModeType.PESSIMISTIC_READ)` 时，SQL 仍为 **`… FOR UPDATE …`**
- **不是**「多读不互斥」的共享锁；其他事务读取同一行时 **可能阻塞**
- 若业务需要「只防写、读者不互斥」，虚谷 + 当前方言 **不提供** PostgreSQL 式 FOR SHARE — 须在应用层重新设计

Boot 消费者路径示例（门控 IT）：`DemoLockIT#pessimisticWriteLocksPersonRow`（写锁）；读锁 SQL 形状见方言 IT `XuguLockIT` 与 **P-005** 归档日志。

---

## 分页与锁顺序

虚谷要求 **锁子句在 LIMIT 之前**。本方言在 Criteria / HQL / native 路径统一生成：

```text
… FOR UPDATE [OF …] LIMIT … [OFFSET …] [NOWAIT|WAIT …]
```

**不要** 手写或假设 Hibernate 默认的 `LIMIT … FOR UPDATE`（MySQL 风格）。故障排查：[05-troubleshooting.md §1](05-troubleshooting.md#1-limit-vs-for-update-顺序错误)、[§2 E19132 OFFSET](05-troubleshooting.md#2-e19132-unexpected-offsethql-分页)。

---

## Boot 集成检查清单

- [ ] 显式 `hibernate.dialect=com.xugu.dialect.XuguDialect` 或 SPI 解析到同类
- [ ] 分页 + 锁的 HQL/Criteria 走本方言 `SqlAstTranslator`（GAV **7.4.5.Final**）
- [ ] 应用 **未** 依赖 `SKIP LOCKED` 或 `FOR SHARE`
- [ ] 已将 `PESSIMISTIC_READ` 按 **排他 FOR UPDATE** 理解并发语义
- [ ] （有库）`XUGU_RUN_IT=true` 下 `DemoLockIT` / 方言 `XuguLockIT` 通过

---

## 交叉引用

| 文档 | 内容 |
|---|---|
| [04-feature-matrix.md](04-feature-matrix.md) | A-LCK-* / A-PAG-* 状态与示例 |
| [05-troubleshooting.md §1–§2](05-troubleshooting.md) | LIMIT / FOR UPDATE 顺序、E19132 |
| [06-consumer-path.md](06-consumer-path.md) | Boot Layer B 悲观锁 demo 入口 |
| [`docs/verification.md`](../verification.md) | I-008 真库 Accept 与离线区别 |
| **P-005 证据** | `harness/evidence/test/I-008/P-005/` — 锁语义 live IT 归档 |
