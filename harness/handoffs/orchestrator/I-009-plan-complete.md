# Handoff: I-009 Plan complete

> Role: orchestrator  
> Initiative: I-009 feature  
> Time: 2026-07-21T09:45:00+08:00  
> Branch: `feat/i-009-deferred-matrix-delivery`  
> Commit: *(pending Plan commit)*

## Prior Archive

- I-008 **accepted + archived** (NOT Ship) on `feat/i-008-production-quality-gaps`
- I-008 Accept SHA: `cdda6c7`
- I-008 P-001…P-007 packets archived to `harness/tasks/archive/I-008/`

## Plan

Initiative I-009 (feature): 在 `compatiblemode=NONE` 下串行交付矩阵全部 **「延后」** 行（类型/函数/DDL/schema/分页锁/identity/Ruler C）；**文档不允许** 继续 skip/负向；GAV 7.4.5.Final；**不 Ship**

Phases:
  P-001 盘点延后矩阵 SSOT 与批次路线图
      roles: researcher → architect-contract → reviewer
      acceptance: harness/evidence/architect-contract/I-009/P-001/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility]
      command_checks: [harness_check]
      observed_flows: [i009-deferred-matrix-inventory-and-batch-map]
      dependencies: []
  P-002 INTERVAL 类型方言 + 门控真库 IT
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-009/P-002/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility, data-integrity]
      command_checks: [build, test]
      observed_flows: [i009-a-typ-014-interval-dialect-and-live-it]
      dependencies: [P-001]
  P-003 XML 类型 + XML 函数
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-009/P-003/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility, data-integrity]
      command_checks: [build, test]
      observed_flows: [i009-a-typ-016-a-fun-021-xml-type-and-functions]
      dependencies: [P-002]
  P-004 空间/几何类型 + 几何函数
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-009/P-004/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility, data-integrity]
      command_checks: [build, test]
      observed_flows: [i009-a-typ-017-a-fun-020-spatial-types-and-functions]
      dependencies: [P-003]
  P-005 UDT 用户定义类型
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-009/P-005/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility, data-integrity]
      command_checks: [build, test]
      observed_flows: [i009-a-typ-018-udt-dialect-and-live-it]
      dependencies: [P-004]
  P-006 regexp_* 与 bit_and/bit_or 聚合
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-009/P-006/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility]
      command_checks: [build, test]
      observed_flows: [i009-a-fun-019-015-regexp-and-bit-aggregates]
      dependencies: [P-005]
  P-007 DDL — IF NOT EXISTS / 分区 / ENCRYPT
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-009/P-007/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility, data-integrity]
      command_checks: [build, test]
      observed_flows: [i009-a-ddl-007-008-009-if-not-exists-partition-encrypt]
      dependencies: [P-006]
  P-008 catalog 限定 + 高级索引
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-009/P-008/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility, data-integrity]
      command_checks: [build, test]
      observed_flows: [i009-a-sch-003-017-catalog-and-advanced-indexes]
      dependencies: [P-007]
  P-009 LOCK TABLE + TOP/ROWNUM 分页备选 + IDENTITY_MODE
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-009/P-009/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility, reliability]
      command_checks: [build, test]
      observed_flows: [i009-lock-pagination-identity-alternatives]
      dependencies: [P-008]
  P-010 Ruler C — json_table / ServerConfig / DialectSelector
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-009/P-010/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility, observability]
      command_checks: [build, test]
      observed_flows: [i009-ruler-c-json-table-serverconfig-dialectselector]
      dependencies: [P-009]
  P-011 文档终对齐 + 全 reactor 真库 + VERIFY PASS Accept 准备
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/I-009/P-011/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility, deployment-and-configuration, observability]
      command_checks: [build, test, verify.py]
      observed_flows: [i009-full-reactor-live-evidence-verify-pass-accept-prep]
      dependencies: [P-010]

Next Build: B-001 → P-001
  (default: earliest ready Phase only; full Plan serial P-001→P-011)

## Production readiness (from docs/production-readiness.md)

| Dimension | I-009 impact |
|---|---|
| Functional correctness | **required** — all Phases |
| Maintainability | **required** — SSOT/docs/tests |
| Compatibility | **required** — NONE native; GAV 7.4.5.Final |
| Data integrity | **conditional** — DDL/type mapping IT (P-002–P-008) |
| Reliability | **conditional** — lock live evidence (P-009) |
| Observability | **conditional** — live IT logs Accept (P-011) |
| Deployment/config | **conditional** — SPI/ServerConfig probe (P-010) |
| Security | **conditional** — env credentials; no secrets in repo |
| Performance | not-applicable — no perf matrix in I-009 |
| Rollback | not-applicable — no Ship in I-009 |

## Files materialized (Plan batch)
- `harness/tasks/P-001.md` … `P-011.md`
- `harness/tasks/archive/I-008/` (superseded I-008 packets + README)
- `harness/tasks/REGISTRY.yaml` (initiative_id I-009)
- `harness/builds/B-001.json` (status draft, approved=false, scope P-001 only)
- `harness/ownership/OWNERSHIP.yaml` (I-009 notes)
- Session: `current-task.md`, `session-state.json`, `session-log.md`
- Brief/INDEX status refresh

## Files Likely Affected (Build Phases, not Plan)
- SSOT: `contracts/feature-matrix-definition-a.md`, `contracts/production-regression-baseline.md`, `contracts/consumer-path-baseline.md`
- `dialect/**` (P-002–P-010)
- `demo-spring-boot/**` (consumer paths as needed)
- `docs/**` (P-011)

## Validation
- Plan batch: harness_check + branch_check
- Future Builds: `mvn -q test` offline green; Accept: `XUGU_RUN_IT` + live logs + `python harness/scripts/verify.py`

## Risks
- Spatial/XML/UDT scope may reveal partial doc support → P-001 must assign honest known-limit
- DDL partition/ENCRYPT may need destructive isolated IT
- Doc-forbidden rows must not get invented SQL (SKIP LOCKED, FOR SHARE, ANSI FETCH, etc.)
- Accidental `org/` dump must stay untracked

## Open questions (for P-001, not blocking Plan)
- Exact row-level batch split and known-limit candidates — locked in P-001 inventory
- json_table / spatial subset depth — decided per Xugu docs in P-001 map

## Next
await human Build approval (scope only — not parallel strategy)

**Ask Human Gate:** 是否批准 B-001，范围仅 P-001？
