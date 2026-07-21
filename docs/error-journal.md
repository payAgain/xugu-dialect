# Error Journal

Use this file to record repeated failures, non-obvious bugs, and lessons learned.

## Format

```markdown
## YYYY-MM-DD - Error Title

### Symptom
### Root Cause
### Fix
### Prevention
### Related Files / Tasks
```

## Known Failure Modes

## 2026-07-15 - Harness「矩阵/VERIFY 全绿」仍漏应用入口路径（库产品）

### Symptom
I-001 Accept 后独立工程 `xugu-hibernate-test` 实库复测 20/22：HQL 分页生成 ANSI `OFFSET/FETCH` 失败；`hbm2ddl.validate` 报 missing sequence。本仓 Phase 证据与 VERIFY PASS 均曾为绿。

### Root Cause（框架）
能力矩阵与 IT 绑定 Dialect SPI（LimitHandler / SequenceSupport），未强制消费者入口（HQL `setFirstResult`、`validate`）。VERIFY 只强制离线 `mvn test`；SPI 证据可自洽关闭「可实现」行。属验收语义缺口，非流程未执行。

### Fix（业务 vs 框架）
- 业务：I-002 hotfix（SqlAstTranslator、sequences metadata、本仓入口 IT）
- 框架：见完整分析 `docs/harness-gap-analysis-i001-external-it.md`（矩阵 entrypoint 字段、test/reviewer 测错层=MAJOR、Accept/Ship 分档、VERIFY 升级）

### Prevention
1. 库产品「可实现」行必须声明 `app_entrypoint` + 禁止伪证据
2. Reviewer：仅 SPI 证据支撑应用能力 → MAJOR request-changes
3. 独立黑盒复测纳入 major Accept / Ship 证据策略
4. 事故必须回流 `.trellis/spec/` 或 journal，不得只修业务代码

### Related Files / Tasks
- `docs/harness-gap-analysis-i001-external-it.md`
- `E:\Work\java\xugu-hibernate-test\TEST-REPORT.md`
- I-001 Archive；I-002 hotfix
- `contracts/feature-matrix-definition-a.md`（A-PAG-* / A-SEQ-*）
- `dialect/.../XuguPaginationIT.java`（测错层样例）
