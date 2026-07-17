# Handoff: I-005 Plan ready

> Role: orchestrator  
> Initiative: I-005 feature  
> Time: 2026-07-17T15:30:00+08:00  
> Branch: `feat/i-005-production-regression-baseline`

## Plan

Initiative I-005 (feature): 为原生非 MySQL-inherited 虚谷方言建立全量生产回归测试基线（Definition A + I-003 可实现；门控真库全绿；负向断言；Demo 冒烟；bulk insert 钉死）；GAV 7.4.5.Final；不 Ship

Phases:
  P-001 建立生产回归基线 SSOT 清单
      roles: researcher → architect-contract → reviewer
      acceptance: harness/evidence/architect-contract/I-005/P-001/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility]
      command_checks: [harness_check]
      observed_flows: [baseline-inventory-ssot-mapping]
      dependencies: []
  P-002 补齐可实现行真库入口回归用例
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-005/P-002/ACCEPTANCE.md
      readiness: [functional-correctness, reliability, compatibility, maintainability]
      command_checks: [build, test]
      observed_flows: [gated-live-it-all-feasible-rows-covered]
      dependencies: [P-001]
  P-003 添加文档不允许与延后负向断言
      roles: test → implementer (conditional) → reviewer
      acceptance: harness/evidence/test/I-005/P-003/ACCEPTANCE.md
      readiness: [functional-correctness, compatibility, maintainability]
      command_checks: [build, test]
      observed_flows: [negative-assertions-doc-forbidden-deferred]
      dependencies: [P-002]
  P-004 钉死 Bulk insert 基线策略
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-005/P-004/ACCEPTANCE.md
      readiness: [functional-correctness, compatibility, maintainability]
      command_checks: [build, test]
      observed_flows: [bulk-insert-baseline-resolved]
      dependencies: [P-003]
  P-005 纳入 Demo Boot 自动化冒烟
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-005/P-005/ACCEPTANCE.md
      readiness: [functional-correctness, deployment-and-configuration, maintainability]
      command_checks: [build, test]
      observed_flows: [demo-boot-automated-smoke-baseline]
      dependencies: [P-004]
  P-006 基线文档对齐与 VERIFY PASS Accept 准备
      roles: implementer → test → reviewer
      acceptance: harness/evidence/orchestrator/I-005/P-006/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility, deployment-and-configuration]
      command_checks: [build, test, verify.py]
      observed_flows: [baseline-docs-aligned-full-verify-pass]
      dependencies: [P-005]

Next Build: B-001 → P-001
  (default: earliest ready Phase only; serial P-001→P-006)

## Production readiness (from docs/production-readiness.md)

| Dimension | I-005 impact |
|---|---|
| Functional correctness | **required** — all Phases; live IT gate for frozen baseline |
| Maintainability | **required** — SSOT + tests as anti-regression contract |
| Compatibility | **required** — entrypoint-level coverage, not SPI-only |
| Deployment/config | **conditional** — P-005/P-006 document IT gate & env keys |
| Reliability | **conditional** — P-002/P-005 live DB paths |
| Data integrity | **conditional** — DDL/DML IT in P-002/P-004 |
| Security | **conditional** — env credentials; no secrets in repo |
| Performance | not-applicable — no hot-path optimization scope |
| Observability | not-applicable — no new diagnostics |
| Rollback | not-applicable — no Ship/publish in I-005 |

## Files materialized (Plan batch)
- `harness/tasks/P-001.md` … `P-006.md`
- `harness/tasks/archive/I-004/` (superseded I-004 packets)
- `harness/tasks/REGISTRY.yaml` (initiative_id I-005)
- `harness/builds/B-001.json` (status draft)
- `harness/ownership/OWNERSHIP.yaml` (+ baseline SSOT path)
- Session: `current-task.md`, `session-state.json`, `session-log.md`

## Files Likely Affected (Build Phases, not Plan)
- `contracts/production-regression-baseline.md` (P-001)
- `dialect/src/test/java/**` (P-002–P-004)
- `demo-spring-boot/src/test/java/**` (P-005)
- `docs/user-guide/**` (P-006)

## Validation
- Plan batch: no `mvn` implementation
- Future: `XUGU_RUN_IT=true mvn test` + `python harness/scripts/verify.py`

## Risks
- P-001 gap count may exceed estimate → serial P-002 scope grows
- Bulk insert decision (P-004) may block if GetGeneratedKeys unresolved
- Demo live smoke needs reachable XuguDB in CI/local

## Open questions (for implementers, not blocking Plan)
- P-004: live IT vs known-limit — decided during P-004 with Human Gate Scope already locked to 二选一
- Deferred rows: documentation + skip assertions vs minimal live probes — P-003 follows brief 负向断言

## Next
await human Build approval (scope only — not parallel strategy)

**Ask Human Gate:** 是否批准 B-001，范围仅 P-001？
