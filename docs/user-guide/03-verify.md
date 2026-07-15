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

## Checklist（集成方自测）

- [ ] 依赖可解析：`com.xugu:xugu-dialect:7.4.5.Final` + JDBC 驱动在 classpath
- [ ] `hibernate.dialect=com.xugu.dialect.XuguDialect` **或** SPI 自动解析到同一类
- [ ] Boot 应用若使用 4.1.0：`hibernate.version=7.4.5.Final` 已强制
- [ ] URL 含 `compatiblemode=NONE`（或会话等价设置）
- [ ] 凭据来自 env / 密钥库，未提交生产密码
- [ ] （可选）gate ON 时 demo IT / dialect IT 通过

排障见 [05-troubleshooting.md](05-troubleshooting.md)。
