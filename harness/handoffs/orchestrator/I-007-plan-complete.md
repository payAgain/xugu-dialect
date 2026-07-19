# Handoff: I-007 Plan complete

> Role: orchestrator  
> Initiative: I-007 feature  
> Time: 2026-07-19T13:55:00+08:00  
> Branch: `feat/i-007-capability-hardening-abc`  
> Commit: *(filled after Plan must-commit)*

## Prior Archive

- I-006 **archived** (NOT Ship) on `feat/i-006-consumer-path-coverage`
- Archive must-commit: `58d0ce80064dba4b9650c6b54d52b54ae5a7ba07`
- Archive tip before Plan branch: `176990921476ea0e37e3c53af4bc2e380fb40339`

## Plan

Initiative I-007 (feature): 在 `compatiblemode=NONE` 下串行推进 **A 堵坑**（C-BULK-002 打通或永久限制 + Accept 证据硬化）→ **A′/急项**（可 thin fold）→ **C 延后矩阵**（JSON 子集 + ARRAY + ALTER SEQUENCE）→ **B 消费者加深**（Flyway + Demo bulk/函数/HQL）→ docs + VERIFY PASS Accept 准备；GAV 7.4.5.Final；原生方言；不 Ship

Phases:
  P-001 盘点 A/B/C 与 SSOT；锁定 C-BULK-002 策略
      roles: researcher → architect-contract → reviewer
      acceptance: harness/evidence/architect-contract/I-007/P-001/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility]
      command_checks: [harness_check]
      observed_flows: [i007-abc-ssot-inventory-and-c-bulk-002-strategy-lock]
      dependencies: []
  P-002 A：C-BULK-002 打通或永久限制 + 证据硬化
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-007/P-002/ACCEPTANCE.md
      readiness: [functional-correctness, reliability, maintainability, compatibility, observability]
      command_checks: [build, test]
      observed_flows: [track-a-c-bulk-002-live-or-permanent-limit-plus-evidence-hardening]
      dependencies: [P-001]
  P-003 A′/急项（可 thin / fold into P-004）
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-007/P-003/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility]
      command_checks: [build, test]
      observed_flows: [track-a-prime-urgent-deferred-or-thin-fold-into-p004]
      dependencies: [P-002]
  P-004 C：JSON 深化子集 + ARRAY + ALTER SEQUENCE
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-007/P-004/ACCEPTANCE.md
      readiness: [functional-correctness, data-integrity, compatibility, maintainability, reliability]
      command_checks: [build, test]
      observed_flows: [track-c-json-subset-array-alter-sequence-doc-impl-live-ssot]
      dependencies: [P-003]
  P-005 B：Flyway + Demo bulk/函数/HQL 消费者加深
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-007/P-005/ACCEPTANCE.md
      readiness: [functional-correctness, reliability, maintainability, compatibility, deployment-and-configuration]
      command_checks: [build, test]
      observed_flows: [track-b-flyway-demo-bulk-function-hql-consumer-deepening]
      dependencies: [P-004]
  P-006 文档对齐与 VERIFY PASS Accept 准备
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-007/P-006/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility, deployment-and-configuration]
      command_checks: [build, test, verify.py]
      observed_flows: [i007-docs-aligned-verify-pass-accept-prep]
      dependencies: [P-005]

Next Build: B-001 → P-001
  (default: earliest ready Phase only; serial P-001→P-006)

## Production readiness (from docs/production-readiness.md)

| Dimension | I-007 impact |
|---|---|
| Functional correctness | **required** — all Phases |
| Maintainability | **required** — SSOT/docs/tests |
| Compatibility | **required** — NONE native; GAV 7.4.5.Final |
| Reliability | **conditional** — live DB / bulk / Flyway paths (P-002/P-004/P-005) |
| Data integrity | **conditional** — JSON/ARRAY/sequence/Flyway (P-004/P-005) |
| Observability | **conditional** — Accept live-log evidence (P-002+) |
| Deployment/config | **conditional** — Flyway/Boot docs (P-005/P-006) |
| Security | **conditional** — env credentials; no secrets in repo |
| Performance | not-applicable — no hot-path optimization scope |
| Rollback | not-applicable — no Ship/publish in I-007 |

## Files materialized (Plan batch)
- `harness/tasks/P-001.md` … `P-006.md`
- `harness/tasks/archive/I-006/` (superseded I-006 packets + README)
- `harness/tasks/REGISTRY.yaml` (initiative_id I-007)
- `harness/builds/B-001.json` (status draft, approved=false, scope P-001 only)
- `harness/ownership/OWNERSHIP.yaml` (I-007 notes)
- Session: `current-task.md`, `session-state.json`, `session-log.md`
- Brief/INDEX status refresh

## Files Likely Affected (Build Phases, not Plan)
- SSOT: `contracts/production-regression-baseline.md`, `contracts/consumer-path-baseline.md`, feature matrices (P-001/P-002/P-004)
- `dialect/**` (P-002/P-004 as needed)
- `demo-spring-boot/**` + Flyway (P-005)
- `docs/**` (P-002 permanent-limit / P-006 align)

## Validation
- Plan batch: no `mvn` implementation; harness/branch checks below
- Future Builds: `mvn -q test` offline green; Accept: `XUGU_RUN_IT=true` + live logs + `python harness/scripts/verify.py`

## Checks (Plan closeout)
- `python harness/scripts/branch_check.py` — **PASS** (`feat/i-007-capability-hardening-abc`)
- `python harness/scripts/harness_check.py` — **PASS** (level=Standard, layout=tool-agnostic)

## Risks
- C-BULK-002 may remain permanently limited if live path fails → docs must nail closure
- C JSON subset scope may expand during P-001 inventory → keep subset discipline in P-004
- Flyway + live DB coupling may fail offline unless properly gated
- Accidental `org/` dump must stay untracked
- P-004 risk_score=8 → Full reviewer before commit

## Open questions (for implementers, not blocking Plan)
- Exact C-BULK-002 live vs permanent-limit decision — locked in P-001
- Whether P-003 has independent urgent items — decided after P-001 inventory

## Next
await human Build approval (scope only — not parallel strategy)

**Ask Human Gate:** 是否批准 B-001，范围仅 P-001？
