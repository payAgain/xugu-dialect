# Handoff: I-002 Plan → Human Gate Build Approval

> Role: orchestrator  
> Initiative: I-002 (hotfix)  
> Updated: 2026-07-15T17:25:00+08:00  
> Resume: 本文件 + `current-task.md` + `harness/session/session-state.json`

---

## Session Briefing（计划完成后）

| 项 | 值 |
|---|---|
| Current Goal | I-002：修复 HQL 分页 OFFSET/FETCH + schema validate 序列元数据；本仓 IT + VERIFY PASS；版本 7.4.5.Final |
| Initiative | I-002 / hotfix / **active** |
| Current Stage | **Plan complete** → 等待 **Build** 批准 |
| Active Build | B-001 **draft**（未批准）→ proposed P-001 only |
| Working Branch | `fix/i-002-hql-pagination-sequence-metadata`（from `feat/i-001-xugu-dialect-major` @ `8136c11`） |
| Base Branch | `feat/i-001-xugu-dialect-major` / ultimately `master` via PR later |

---

## Plan（skills/plan.md 模板）

```text
Plan

Initiative I-002 (hotfix): 修复 HQL 分页 SqlAstTranslator + 序列元数据 getQuerySequencesString；本仓补门控 IT；版本保持 7.4.5.Final

Phases:
  P-001 修复 HQL 分页 SqlAstTranslator
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/P-001/ACCEPTANCE.md
      readiness: [functional-correctness, performance-and-capacity, maintainability, compatibility]
      command_checks: [build, test]
      observed_flows: [hql-pagination-offset-fetch-real-db]
      dependencies: []

  P-002 修复序列元数据 getQuerySequencesString
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/P-002/ACCEPTANCE.md
      readiness: [functional-correctness, data-integrity, maintainability, compatibility]
      command_checks: [build, test]
      observed_flows: [schema-validate-sequence-metadata-real-db]
      dependencies: [P-001]

  P-003 文档矩阵勘误与 Accept 准备
      roles: implementer → test → reviewer
      acceptance: harness/evidence/orchestrator/P-003/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility, deployment-and-configuration]
      command_checks: [build, test]
      observed_flows: [full-verify-pass, matrix-docs-aligned-with-hotfix]
      dependencies: [P-002]

Next Build: B-001 → P-001
  (default: earliest ready Phase only; multi-Phase in one Build still serial unless orchestrator parallel_group)

Files Likely Affected:
  P-001: dialect SqlAstTranslator / factory；HQL 分页门控 IT；相关矩阵勾选
  P-002: getQuerySequencesString (+ extractor)；validate/sequence IT
  P-003: docs/user-guide、feature-matrix、合约勘误；全量 verify；可选 xugu-hibernate-test 笔记

Validation:
  mvn -q -DskipTests package
  mvn -q test
  mvn -q test -Dxugu.run.integration=true
  python harness/scripts/verify.py
  python harness/scripts/branch_check.py
  # optional: E:\Work\java\xugu-hibernate-test mvn test

Risks:
  - 真实 XuguDB 不可达会阻塞门控 IT（P-001/P-002）
  - SqlAstTranslator 须与既有 XuguLimitHandler / FOR UPDATE 顺序一致，否则回归锁+分页
  - 禁止旁路 hibernate-dialect 复制；须按官方 SQL/本仓既有风格实现
  - I-002 重启 P/B 编号；I-001 证据仍在 harness/evidence 历史路径，REGISTRY 已切到 I-002

Next: await human Build approval (scope only — not parallel strategy)
```

---

## Initiative Briefing（skills/initiative.md 输出）

```text
Initiative Briefing

Initiative ID: I-002
Type: hotfix
Phases planned: P-001 … P-003
Next Build: B-001 → P-001
Open Questions: （无 — Scope Decisions 已锁定）
Status: ready-for-build
Next: await Build scope approval (not parallel strategy)
```

---

## 请 Human Gate 明确回答（Build 范围）

**批准 B-001 范围仅 P-001？**

编排建议（默认）：

> **批准 B-001 = 仅 P-001**（`XuguSqlAstTranslator` + HQL `setFirstResult`/`setMaxResults` 门控真实库 IT）。  
> **不要**在同一 Build 中批准 P-002/P-003；待 P-001 验收后再批下一 Build。

可选回复示例：
- 「批准 B-001，范围仅 P-001」
- 或显式扩大范围（仍将由 orchestrator **串行**执行，不问并行）

---

## Disk artifacts this turn

| Path | Action |
|---|---|
| `harness/initiatives/INDEX.md` | I-002 → active；branch 名已填 |
| `fix/i-002-hql-pagination-sequence-metadata` | 已 checkout（无新 commit） |
| `harness/tasks/P-001.md` … `P-003.md` | 物化 |
| `harness/tasks/REGISTRY.yaml` | initiative_id=I-002 |
| `harness/builds/B-001.json` | **draft**，proposed P-001 only |
| `current-task.md` / `session-state.json` / `session-log.md` | 已更新 |
| Java / dialect sources | **未改** |

---

## Forbidden reminders
- 未批准前不派发 implementer
- 不自行 approve Build
- 不问并行/同步
- 不读旁路 `hibernate-dialect` 复制实现
