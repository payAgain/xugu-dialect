# Handoff: I-008 Plan complete

> Role: orchestrator  
> Initiative: I-008 feature  
> Time: 2026-07-20T16:10:00+08:00  
> Branch: `feat/i-008-production-quality-gaps`  
> Commit: `3305336`

## Prior Archive

- I-007 **archived** (NOT Ship) on `feat/i-007-capability-hardening-abc`
- I-007 Accept SHA: `95d4739`；Branch tip before I-008 Plan: `61191e9`

## Plan

Initiative I-008 (feature): 在 `compatiblemode=NONE` 下串行闭环 **Q1–Q4**（诚实 SSOT + 94 可实现升 covered-live / 锁证据 / Boot UUID·JSON 开箱 / Accept 全 reactor 真库）；**Q5 性能/多版本不做**；GAV 7.4.5.Final；不 Ship

Phases:
  P-001 盘点 SSOT 诚实计数与升 covered-live 路线图
      roles: researcher → architect-contract → reviewer
      acceptance: harness/evidence/architect-contract/I-008/P-001/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility]
      command_checks: [harness_check]
      observed_flows: [i008-q1-ssot-honest-counts-and-promotion-map]
      dependencies: []
  P-002 文档口径诚实 + 锁/UUID/JSON 集成须知 + 生产验证用语
      roles: implementer → reviewer
      acceptance: harness/evidence/implementer/I-008/P-002/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility, deployment-and-configuration]
      command_checks: [harness_check]
      observed_flows: [i008-q1-q2-q4-docs-honesty-and-integration-notes]
      dependencies: [P-001]
  P-003 升 covered-live — 方言回归 / Definition A 批次
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-008/P-003/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility, observability]
      command_checks: [build, test]
      observed_flows: [i008-q1-promote-covered-live-definition-a-batch]
      dependencies: [P-001, P-002]
  P-004 升 covered-live — Ruler C / 消费者路径余量批次
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-008/P-004/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility, observability]
      command_checks: [build, test]
      observed_flows: [i008-q1-promote-covered-live-ruler-c-consumer-batch]
      dependencies: [P-003]
  P-005 锁语义行为与负向真库证据
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-008/P-005/ACCEPTANCE.md
      readiness: [functional-correctness, reliability, maintainability, compatibility, observability]
      command_checks: [build, test]
      observed_flows: [i008-q2-lock-semantics-behavior-and-negative-evidence]
      dependencies: [P-002]
  P-006 Boot UUID/JSON 开箱对齐 + IT
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-008/P-006/ACCEPTANCE.md
      readiness: [functional-correctness, deployment-and-configuration, compatibility, maintainability]
      command_checks: [build, test]
      observed_flows: [i008-q3-boot-uuid-json-out-of-box-wiring-and-it]
      dependencies: [P-002, P-004]
  P-007 文档终对齐 + 全 reactor 真库证据 + VERIFY PASS Accept 准备
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-008/P-007/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility, deployment-and-configuration, observability]
      command_checks: [build, test, verify.py]
      observed_flows: [i008-q4-full-reactor-live-evidence-verify-pass-accept-prep]
      dependencies: [P-003, P-004, P-005, P-006]

Next Build: B-001 → P-001
  (default: earliest ready Phase only; full Plan serial P-001→P-007)

## Production readiness (from docs/production-readiness.md)

| Dimension | I-008 impact |
|---|---|
| Functional correctness | **required** — all Phases |
| Maintainability | **required** — SSOT/docs/tests |
| Compatibility | **required** — NONE native; GAV 7.4.5.Final |
| Reliability | **conditional** — lock live evidence (P-005) |
| Observability | **conditional** — live IT logs Accept (P-003/P-004/P-007) |
| Deployment/config | **conditional** — Boot UUID/JSON defaults (P-006) |
| Data integrity | **conditional** — promotion IT integrity (P-003/P-004) |
| Security | **conditional** — env credentials; no secrets in repo |
| Performance | not-applicable — Q5 explicitly out |
| Rollback | not-applicable — no Ship in I-008 |

## Files materialized (Plan batch)
- `harness/tasks/P-001.md` … `P-007.md`
- `harness/tasks/archive/I-007/` (superseded I-007 packets + README)
- `harness/tasks/REGISTRY.yaml` (initiative_id I-008)
- `harness/builds/B-001.json` (status draft, approved=false, scope P-001 only)
- `harness/ownership/OWNERSHIP.yaml` (I-008 notes)
- Session: `current-task.md`, `session-state.json`, `session-log.md`
- Brief/INDEX status refresh

## Files Likely Affected (Build Phases, not Plan)
- SSOT: `contracts/production-regression-baseline.md`, `contracts/consumer-path-baseline.md` (P-001/P-003/P-004/P-007)
- `dialect/**` (P-003/P-004/P-005)
- `demo-spring-boot/**` (P-006)
- `docs/**` (P-002/P-005/P-007)

## Validation
- Plan batch: harness_check + branch_check
- Future Builds: `mvn -q test` offline green; Accept: `XUGU_RUN_IT` + live logs + `python harness/scripts/verify.py`

## Risks
- 94-row promotion scope may reveal more unit-only rows than expected → P-001 must batch honestly
- known-limit-documented rows must not be silently promoted
- Lock negative tests must not assert non-existent SQL features
- Boot UUID/JSON defaults must stay aligned with P-002 checklist
- Accidental `org/` dump must stay untracked

## Open questions (for P-001, not blocking Plan)
- Exact unit-only row count and batch split — locked in P-001 promotion map
- Which rows remain known-limit after live attempts — decided in P-003/P-004

## Next
await human Build approval (scope only — not parallel strategy)

**Ask Human Gate:** 是否批准 B-001，范围仅 P-001？
