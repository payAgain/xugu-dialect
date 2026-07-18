# Handoff: I-006 Plan complete

> Role: orchestrator  
> Initiative: I-006 feature  
> Time: 2026-07-18T15:13:00+08:00  
> Branch: `feat/i-006-consumer-path-coverage`  
> Commit: `fd7aa526650de7aee8db4890c098eb83a89fc22e`

## Plan

Initiative I-006 (feature): Spring Boot 消费者路径全面覆盖 = Layer A（黄金路径）+ Layer B-both（关联+SEQUENCE）+ Layer C′（Boot 必测 SSOT 行，非 94 全量镜像）；GAV 7.4.5.Final；原生方言；不 Ship

Phases:
  P-001 建立消费者路径 Boot 必测 SSOT
      roles: researcher → architect-contract → reviewer
      acceptance: harness/evidence/architect-contract/I-006/P-001/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility]
      command_checks: [harness_check]
      observed_flows: [consumer-path-boot-required-ssot-mapping]
      dependencies: []
  P-002 加固黄金路径 A（validate/CRUD/startup/SPI）
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-006/P-002/ACCEPTANCE.md
      readiness: [functional-correctness, reliability, compatibility, maintainability, deployment-and-configuration]
      command_checks: [build, test]
      observed_flows: [layer-a-golden-path-boot-validate-crud-startup-spi]
      dependencies: [P-001]
  P-003 扩展代表性模型 B（关联+SEQUENCE+锁/异常/回滚）
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-006/P-003/ACCEPTANCE.md
      readiness: [functional-correctness, reliability, data-integrity, compatibility, maintainability]
      command_checks: [build, test]
      observed_flows: [layer-b-association-sequence-lock-exception-rollback]
      dependencies: [P-002]
  P-004 扫盲 C′ 剩余 Boot 必测入口
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-006/P-004/ACCEPTANCE.md
      readiness: [functional-correctness, compatibility, maintainability, data-integrity]
      command_checks: [build, test]
      observed_flows: [layer-c-prime-remaining-boot-required-entries]
      dependencies: [P-003]
  P-005 文档对齐与 VERIFY PASS Accept 准备
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-006/P-005/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility, deployment-and-configuration]
      command_checks: [build, test, verify.py]
      observed_flows: [consumer-path-docs-aligned-verify-pass-accept-prep]
      dependencies: [P-004]

Next Build: B-001 → P-001
  (default: earliest ready Phase only; serial P-001→P-005)

## Production readiness (from docs/production-readiness.md)

| Dimension | I-006 impact |
|---|---|
| Functional correctness | **required** — all Phases; Boot consumer entry evidence |
| Maintainability | **required** — consumer-path SSOT + demo tests |
| Compatibility | **required** — SPI/explicit dialect + generators/types |
| Deployment/config | **conditional** — Boot props + IT gate docs (P-002/P-005) |
| Reliability | **conditional** — live DB, lock/rollback paths (P-002/P-003) |
| Data integrity | **conditional** — associations/SEQUENCE/UNIQUE/bulk (P-003/P-004) |
| Security | **conditional** — env credentials; no secrets in repo |
| Performance | not-applicable — no hot-path optimization scope |
| Observability | not-applicable — no new diagnostics |
| Rollback | not-applicable — no Ship/publish in I-006 |

## Files materialized (Plan batch)
- `harness/tasks/P-001.md` … `P-005.md`
- `harness/tasks/archive/I-005/` (superseded I-005 packets + README)
- `harness/tasks/REGISTRY.yaml` (initiative_id I-006)
- `harness/builds/B-001.json` (status draft, approved=false, scope P-001 only)
- `harness/ownership/OWNERSHIP.yaml` (+ planned consumer-path SSOT under demo-spring-boot)
- Session: `current-task.md`, `session-state.json`, `session-log.md`
- Brief/INDEX status refresh

## Files Likely Affected (Build Phases, not Plan)
- `contracts/consumer-path-baseline.md` (P-001)
- `demo-spring-boot/src/test/java/**` (+ entities/config as needed) (P-002–P-004)
- `docs/user-guide/**` / README consumer-path section (P-005)
- `dialect/**` only if Boot-only defect proven

## Validation
- Plan batch: no `mvn` implementation; harness/branch checks below
- Future Builds: `mvn -q test` offline green; Accept: `XUGU_RUN_IT=true` + `python harness/scripts/verify.py`

## Checks (Plan closeout)
- `python harness/scripts/branch_check.py` — **PASS** (`feat/i-006-consumer-path-coverage`)
- `python harness/scripts/harness_check.py` — **PASS** (level=Standard, layout=tool-agnostic)

## Risks
- P-001 Boot-required row count may land outside 35–50 estimate → P-004 scope grows (still not 94 mirror)
- Layer B SEQUENCE/lock behavior may expose Boot-only dialect gaps (minimal dialect fix only if proven)
- Gated live suite needs reachable XuguDB for Accept; offline must stay green without it
- Accidental `org/` dump must stay untracked

## Open questions (for implementers, not blocking Plan)
- Exact Boot-required row IDs — decided in P-001 SSOT, not Human Gate re-clarify
- JSON optional in C′ — include only if P-001 tags it Boot-required

## Next
await human Build approval (scope only — not parallel strategy)

**Ask Human Gate:** 是否批准 B-001，范围仅 P-001？
