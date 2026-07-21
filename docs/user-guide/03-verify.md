# 03 — Verify / 验证

← [02-configuration.md](02-configuration.md) · [Index](README.md) · Next: [04-feature-matrix.md](04-feature-matrix.md)

## Offline build / unit tests

从仓库根目录：

```bash
mvn -q -DskipTests package
mvn -q test
# or
mvn -q verify
```

默认 **`xugu.run.integration=false`**：标注为 gated 的真实库 IT 会跳过，离线即可通过单元测试与打包。

Harness 契约（治理用）：

```bash
python harness/scripts/verify.py
```

## Demo：Spring Boot 运行

前置：JDK 17、根目录 JDBC jar、**可达的 XuguDB**（缺库时启动会失败——属外部前置，非方言 jar 损坏）。

```bash
# Windows PowerShell / cmd 示例
set XUGU_JDBC_URL=jdbc:xugu://127.0.0.1:5138/SYSTEM?compatiblemode=NONE
set XUGU_USER=SYSDBA
set XUGU_PASSWORD=SYSDBA

mvn -pl demo-spring-boot -am spring-boot:run
```

成功时日志可见类似 `Xugu Hibernate demo CRUD OK`（`DemoStartupCrudRunner` 写入/查询 `HIB_DEMO_PERSON`）。

关闭启动 CRUD：`--xugu.demo.startup-crud=false`。

更多细节：[`demo-spring-boot/README.md`](../../demo-spring-boot/README.md)。

## Integration tests gate / 集成开关

打开真实库 IT：

```bash
# 全仓库 reactor
mvn -q test -Dxugu.run.integration=true

# 仅 demo（建议加 -am，以便解析同 reactor 的 xugu-dialect）
mvn -q -pl demo-spring-boot -am test -Dxugu.run.integration=true
```

等价环境变量：`XUGU_RUN_IT=true`。

| Gate | Meaning |
|---|---|
| `-Dxugu.run.integration=true` | Maven/Surefire 系统属性 |
| `XUGU_RUN_IT=true` | 环境变量替代 |

**缺真实库时**：不要强开 gate；文档要求 IT 使用真实 XuguDB（无 mock-only 替代）。离线验证以默认 `mvn test` 为准。

## Frozen baseline — I-005 冻结基线门控

I-005 将 **Definition A 可实现 (78)** + **I-003 ruler C 可实现 (16)** = **94 行** 与 **34 negative-only** 行冻结为生产回归基线。SSOT 清单：

[`contracts/production-regression-baseline.md`](../../contracts/production-regression-baseline.md)

### 日常 vs 冻结验收

| Mode | Command | When |
|---|---|---|
| **Daily / offline** | `mvn -q test` | 默认 CI、无真库；gated IT 跳过 |
| **Frozen baseline (I-005 Accept)** | `XUGU_RUN_IT=true mvn -q test` | 冻结基线验收；**全部** gated dialect + demo IT 须全绿 |
| **Harness contract** | `python harness/scripts/verify.py` | 项目治理；Accept 须 **VERIFY PASS** |

### 冻结基线步骤（复现）

1. 确认可达 XuguDB 与 env：`XUGU_JDBC_URL`、`XUGU_USER`、`XUGU_PASSWORD`（见 [02-configuration.md](02-configuration.md)）。
2. 打开 IT gate（二选一）：
   ```bash
   set XUGU_RUN_IT=true
   mvn -q test
   ```
   或 `mvn -q test -Dxugu.run.integration=true`。
3. 期望：dialect 模块 gated IT + demo `DemoPersonCrudIT` / `DemoBootBaselineSmokeTest` 全 PASS；离线单元与 `@Disabled` defer 锚点仍按默认 gate 行为。
4. 运行 `python harness/scripts/verify.py` → **VERIFY PASS**（build + offline test）。
5. **GAV 不变**：`com.xugu:xugu-dialect:7.4.5.Final`。

### I-007 后 C-BULK-002 状态

- **C-BULK-002** bulk insert：**covered-live**（I-007/P-002）— 门控真库 IT `XuguBulkMutationIT#bulkInsertOnJoinedInheritanceWithIdentitySucceeds_C_BULK_002` PASS；见 [05-troubleshooting.md §10](05-troubleshooting.md#10-bulk-insertjoined--identity已知限制) 与 SSOT [`production-regression-baseline.md`](../../contracts/production-regression-baseline.md)。
- I-005 冻结时曾为 **known-limit-documented**；I-007 Track A 已关闭为 **covered-live**（非 gap）。

## Consumer-path baseline — I-006 消费者路径门控

I-006 冻结 Spring Boot **消费者路径**子集（**不是** 94 行 Boot 镜像）。SSOT：

[`contracts/consumer-path-baseline.md`](../../contracts/consumer-path-baseline.md)

| Bucket | Count |
|---|---:|
| Boot-required (A+B+C′) | **41** |
| Open Boot gaps | **0** |
| Demo `@Test` (approx.) | **28** |

### 日常 vs 消费者路径验收

| Mode | Command | When |
|---|---|---|
| **Daily / offline** | `mvn -q test` | 默认 CI、无真库；gated IT 跳过 |
| **Consumer-path live (I-006)** | `XUGU_RUN_IT=true mvn -q test` 或 `mvn -q -pl demo-spring-boot -am test` + gate ON | 有可达 XuguDB；期望 demo 全绿 |
| **Harness contract** | `python harness/scripts/verify.py` | Accept 须 **VERIFY PASS**（offline required checks） |

完整步骤、Layer 说明与边界：[06-consumer-path.md](06-consumer-path.md)。

**GAV 不变：** `com.xugu:xugu-dialect:7.4.5.Final`。**Ship / Central** 不在 I-006 范围。

## I-007 capability hardening — Accept 准备门控

Initiative **I-007**（A/B/C tracks）在 I-005/I-006 基线之上硬化能力；SSOT：[`contracts/i007-capability-hardening-plan.md`](../../contracts/i007-capability-hardening-plan.md)。

| Track | Outcome | User doc pointer |
|---|---|---|
| **A** | C-BULK-002 **covered-live** | 本节 + [05-troubleshooting.md §10](05-troubleshooting.md#10-bulk-insertjoined--identity已知限制) |
| **B** | Flyway + Demo 加深（Boot SSOT **41** 不变） | [06-consumer-path.md § I-007 Track B](06-consumer-path.md#i-007-track-b-deepeningp-005) |
| **C** | JSON 子集 / ARRAY / ALTER SEQUENCE **covered-live** | [`docs/p004-track-c-capabilities.md`](../p004-track-c-capabilities.md) + [04-feature-matrix.md](04-feature-matrix.md) |

| Mode | Command | When |
|---|---|---|
| **Daily / offline** | `mvn -q test` + `python harness/scripts/verify.py` | Accept 须 **VERIFY PASS** |
| **Live (when DB available)** | `XUGU_RUN_IT=true mvn -q test` | dialect + demo gated IT；Accept 证据见 `harness/evidence/test/I-007/P-00*/` |
| **GAV / compat** | `7.4.5.Final` + `compatiblemode=NONE` | 不升版；**NOT Ship** |

## I-008 Accept — 全量 reactor 真库证据（Q4）

Initiative **I-008** 在 I-005/I-006/I-007 基线之上闭环 Q1–Q4。**离线 `mvn test` + `verify.py` VERIFY PASS** 仅证明构建与单元/布线 — **不足以** 宣称「生产已验证」或满足 I-008 **Initiative Accept**。

| Mode | Command | 证明什么 |
|---|---|---|
| **日常 / CI 离线** | `mvn -q test` | 默认 gate OFF；gated IT 跳过 |
| **Harness 契约** | `python harness/scripts/verify.py` | 配置化 build + offline test — **非** 全量真库回归 |
| **I-008 Accept（有可达 XuguDB）** | `XUGU_RUN_IT=true mvn -q test` | **全 reactor** 门控 dialect + demo IT 须全绿 |
| **Accept 归档** | `harness/evidence/test/I-008/P-007/` | `mvn-test-live-it-final.log`、`IT-RESULT.txt`（**P-007** 写入） |

子集证据（Accept 引用，非替代全量 reactor）：

| Phase | 目录 | 内容 |
|---|---|---|
| P-005 | `harness/evidence/test/I-008/P-005/` | 锁语义 live IT |
| P-006 | `harness/evidence/test/I-008/P-006/` | Boot UUID/JSON 开箱 live demo |
| P-007 | `harness/evidence/test/I-008/P-007/` | **Accept 级** 全 reactor 终验 |

无真库时：文档 **`SKIPPED_INFRA`** — 离线绿 **不能** 代替 Accept live log。

诚实计数（Q1，P-003/P-004 已达成）：**83/98** covered-live + **15** known-limit-documented — [04-feature-matrix.md § I-005 baseline counts](04-feature-matrix.md#i-005-baseline-counts冻结)。锁集成（Q2）：[07-lock-integration.md](07-lock-integration.md)。Boot UUID/JSON 开箱（Q3）：[02-configuration.md § UUID/JSON](02-configuration.md#uuid--json-boot-必配清单i-008-q3)。

**Q5 out of scope：** 性能基准与 Hibernate 多版本兼容矩阵 **未做**（I-008 Scope PASS 明示）。

## I-009 Accept prep — 延后矩阵全量交付 + VERIFY PASS

Initiative **I-009** 在 I-005/I-006/I-007/I-008 基线之上交付 **20 行**延后库存（P-002…P-010）。SSOT：[`contracts/production-regression-baseline.md`](../../contracts/production-regression-baseline.md) § I-009 deferred delivery routing。

| Mode | Command | 证明什么 |
|---|---|---|
| **日常 / CI 离线** | `mvn -q test` + `python harness/scripts/verify.py` | 构建 + 单元/布线 — **P-011 必须 VERIFY PASS** |
| **Accept prep 真库（有 DB）** | `XUGU_RUN_IT=true mvn -q test` | **全 reactor** 门控 dialect + demo IT 须全绿 |
| **Accept 归档** | `harness/evidence/test/I-009/P-011/` | `verification.json`、`TEST-REPORT.md`、live log 或 **`SKIPPED_INFRA`** |

### I-009 延后行终态（诚实，不膨胀 98 计数）

| 终态 | 行数 | 示例 |
|---|---:|---|
| **covered-live** | **6** | A-FUN-019/020, A-LCK-006, A-IDN-005, A-DDL-007, C-SRV-001 |
| **known-limit-documented** | **13** | A-TYP-014/016/017/018, A-PAG-004/006, A-SCH-003/017, A-DDL-008/009, A-FUN-015, **A-FUN-021**, C-SEL-001 |
| **doc-forbidden negative-only** | 1 | C-JSON-006（禁止发明 `json_table`） |

> **A-FUN-021：** baseline 行级 SSOT = **known-limit-documented**（XMLTABLE 单节点 / 集群 skip）— 勿写 covered-live 或陈旧 **7/12**。见 [`production-regression-baseline.md`](../../contracts/production-regression-baseline.md) § A-FUN-021。

Charter **98** 可实现诚实 rollup **不变**：**83/98** covered-live + **15** known-limit-documented — 见 [04-feature-matrix.md § I-009](04-feature-matrix.md#i-009-deferred-closure终态)。

**NOT Ship** — Maven Central / tag 不在 I-009 范围。

### I-008 Accept — 黄金路径 manifest（Q4 冻结）

Initiative Accept 须 **`XUGU_RUN_IT=true mvn -q test`** 全 reactor 绿，且下列路径在真库上均有 live IT 覆盖（离线 skip **不足以** Accept）：

| 域 | 矩阵 / 能力 | 主 live IT 锚点 |
|---|---|---|
| **Pagination** | A-PAG-001…003 | `XuguHqlPaginationIT#hqlSetFirstResultMaxResultsUsesLimitNotFetchFirst`; `XuguPaginationIT#limitAndOffsetReturnExpectedRows`; `DemoBootBaselineSmokeTest#pageableFindAllUsesLimitOffset` |
| **Lock** | A-LCK-001/003/005 | `XuguLockIT#pessimisticReadExecutesAsForUpdateNotShare`; `XuguHqlPaginationIT#hqlLockAndPageEmitsForUpdateBeforeLimitAndWaitAfter` |
| **SEQUENCE** | A-SEQ-001/003/004/006 | `XuguIdentitySequenceIT#sequenceGeneratorPersist_A_SEQ_003_004_008`; `XuguSchemaValidateIT#schemaValidateSucceedsWhenSequenceExists`; `XuguAlterSequenceIT#alterSequenceStartWithAndIncrement_A_SEQ_006` |
| **IDENTITY** | A-IDN-003/004 | `XuguIdentitySequenceIT#identityPersistBackfillsId_A_IDN_003_004`; `DemoPersonCrudIT#persistAndFindPerson` |
| **UUID** | A-TYP-012 | `DemoUuidJsonOutOfBoxIT#uuidAndJsonGoldenPathWithDefaultBootWiring` |
| **JSON** | A-TYP-013, C-JSON-001…004 | `DemoUuidJsonOutOfBoxIT#uuidAndJsonGoldenPathWithDefaultBootWiring`; `XuguJsonAggregateIT#jsonColumnRoundTripAndHqlAggregates` |
| **HQL** | A-PAG + Layer A JPQL | `XuguHqlPaginationIT`（分页/锁组合）; `DemoBootBaselineSmokeTest#jpaPersistAndJpqlQueryRoundTrip` |

子集 Phase 证据（**非**替代全量 reactor）：P-005 锁 — `harness/evidence/test/I-008/P-005/`；P-006 Boot UUID/JSON — `harness/evidence/test/I-008/P-006/`。Accept 终验归档 — `harness/evidence/test/I-008/P-007/`（`mvn-test-live-it-final.log`, `IT-RESULT.txt`）。Implementer Accept 准备清单 — `harness/evidence/implementer/I-008/P-007/ACCEPTANCE.md`。

## Checklist（集成方自测）

- [ ] 依赖可解析：`com.xugu:xugu-dialect:7.4.5.Final` + JDBC 驱动在 classpath
- [ ] `hibernate.dialect=com.xugu.dialect.XuguDialect` **或** SPI 自动解析到同一类
- [ ] Boot 应用若使用 4.1.0：`hibernate.version=7.4.5.Final` 已强制
- [ ] URL 含 `compatiblemode=NONE`（或会话等价设置）
- [ ] 凭据来自 env / 密钥库，未提交生产密码
- [ ] （可选）gate ON 时 demo IT / dialect IT 通过
- [ ] （I-006）消费者路径：对照 [06-consumer-path.md](06-consumer-path.md)；Boot open gaps = 0

排障见 [05-troubleshooting.md](05-troubleshooting.md)。
