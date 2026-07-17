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

### 已知限制（非 gap）

- **C-BULK-002** bulk insert：SSOT 标 **known-limit-documented**；真库 bulk insert IT waived — 见 [05-troubleshooting.md §10](05-troubleshooting.md#10-bulk-insertjoined--identity已知限制)。
- Demo 可选路径（validate / function-HQL / bulk demo）为 SSOT 可选 gap，不阻塞 I-005 Accept。

## Checklist（集成方自测）

- [ ] 依赖可解析：`com.xugu:xugu-dialect:7.4.5.Final` + JDBC 驱动在 classpath
- [ ] `hibernate.dialect=com.xugu.dialect.XuguDialect` **或** SPI 自动解析到同一类
- [ ] Boot 应用若使用 4.1.0：`hibernate.version=7.4.5.Final` 已强制
- [ ] URL 含 `compatiblemode=NONE`（或会话等价设置）
- [ ] 凭据来自 env / 密钥库，未提交生产密码
- [ ] （可选）gate ON 时 demo IT / dialect IT 通过

排障见 [05-troubleshooting.md](05-troubleshooting.md)。
