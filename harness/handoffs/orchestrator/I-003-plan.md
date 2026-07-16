# Handoff: I-003 Plan

> Role: orchestrator  
> Initiative: I-003 feature  
> Time: 2026-07-16T10:46:00+08:00

## Plan

Initiative I-003 (feature): 尺子 C 生产能力补齐；入口 IT；版本 7.4.5.Final；不改 harness 框架

Phases:
  P-001 尺子 C 生产能力差集盘点 SSOT
      roles: architect-contract → reviewer
      acceptance: harness/evidence/architect-contract/P-001/ACCEPTANCE.md
      readiness: [functional-correctness, compatibility, maintainability]
      command_checks: [harness_check]
      observed_flows: [gap-inventory-ruler-c-ssot]
      dependencies: []
  P-002 异常映射与约束名抽取 + 入口 IT
      roles: implementer → test → reviewer
      dependencies: [P-001]
  P-003 JSON 深能力与 AggregateSupport + 入口 IT
      dependencies: [P-002]
  P-004 Window 与 WITH(CTE) + 入口 IT
      dependencies: [P-003]
  P-005 Bulk mutation 回退策略 + 入口 IT
      dependencies: [P-004]
  P-006 类型与 DDL 细节缺口 + 入口 IT
      dependencies: [P-005]
  P-007 矩阵文档对齐与 Accept 准备
      dependencies: [P-006]

Next Build: B-001 → P-001
  (default: earliest ready Phase only)

## Files Likely Affected (later Phases)
- `dialect/src/main/java/com/xugu/dialect/**`
- `dialect/src/test/java/**`（ORM 入口 IT）
- `contracts/**`, `docs/user-guide/**`

## Validation
- Existing: `mvn test`, `-Dxugu.run.integration=true`, `verify.py`
- No harness framework rewrite in this Initiative

## Risks
- 尺子 C 差集可能大于首批预估 → P-001 必须显式 延后/文档不允许
- 禁止旁路移植可能增加实现成本
- 入口 IT 依赖真实库可达性

## Next
await human Build approval (scope only — not parallel strategy)
