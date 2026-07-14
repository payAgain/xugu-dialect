# Handoff: I-001 Plan → Human Gate Build Approval

> Role: orchestrator  
> Initiative: I-001 (major)  
> Updated: 2026-07-14T15:55:00+08:00  
> Resume: 本文件 + `current-task.md` + `harness/session/session-state.json`

---

## Session Briefing（计划完成后）

| 项 | 值 |
|---|---|
| Current Goal | I-001：方言 jar + Spring Boot 4.1.0 demo + docs/user-guide（定义 A 全量） |
| Initiative | I-001 / major / **active** |
| Current Stage | **Plan complete** → 等待 **Build** 批准 |
| Active Build | B-001 **draft**（未批准） |
| Working Branch | `feat/i-001-xugu-dialect-major`（unborn HEAD，无 commit） |
| Base Branch | `master`（同样无 commit） |

---

## Plan（skills/plan.md 模板）

```text
Plan

Initiative I-001 (major): 交付 Hibernate 7.4.5.Final 虚谷方言 jar + Spring Boot 4.1.0 demo + docs/user-guide（定义 A 全量）

Phases:
  P-001 脚手架 Maven 多模块与验证契约
      roles: architect-contract → implementer → test → reviewer
      acceptance: harness/evidence/implementer/P-001/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, deployment-and-configuration, compatibility]
      command_checks: [build, test]
      observed_flows: [parent-and-modules-compile-via-maven, verification-json-real-mvn-commands]
      dependencies: []

  P-002 方言契约与定义 A 特性矩阵
      roles: architect-contract → test → (reviewer?)
      acceptance: harness/evidence/architect-contract/P-002/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility]
      command_checks: [build]
      observed_flows: [definition-a-matrix-reviewable, dialect-contract-published]
      dependencies: [P-001]

  P-003 方言类型映射与 DDL
      roles: architect-contract → implementer → test → reviewer
      acceptance: harness/evidence/implementer/P-003/ACCEPTANCE.md
      readiness: [functional-correctness, data-integrity, maintainability, compatibility]
      command_checks: [build, test]
      observed_flows: [type-mapping-roundtrip-real-db, ddl-generation-matches-xugu-docs]
      dependencies: [P-002]

  P-004 分页与锁语法
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/P-004/ACCEPTANCE.md
      readiness: [functional-correctness, performance-and-capacity, maintainability, compatibility]
      command_checks: [build, test]
      observed_flows: [limit-offset-pagination-real-db, pessimistic-lock-sql-real-db]
      dependencies: [P-003]

  P-005 主键 Identity 与 Sequence
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/P-005/ACCEPTANCE.md
      readiness: [functional-correctness, data-integrity, maintainability, compatibility]
      command_checks: [build, test]
      observed_flows: [identity-insert-real-db, sequence-generator-real-db]
      dependencies: [P-004]

  P-006 SQL 函数注册
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/P-006/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility]
      command_checks: [build, test]
      observed_flows: [function-registry-hql-sql-real-db]
      dependencies: [P-005]

  P-007 Schema / 临时表 / 注释 / 外键
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/P-007/ACCEPTANCE.md
      readiness: [functional-correctness, data-integrity, maintainability, compatibility]
      command_checks: [build, test]
      observed_flows: [schema-tooling-real-db, temp-table-comments-fk-real-db]
      dependencies: [P-006]

  P-008 DialectResolver SPI 与显式配置
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/P-008/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility, deployment-and-configuration]
      command_checks: [build, test]
      observed_flows: [explicit-dialect-config, spi-dialect-resolver-autodetect]
      dependencies: [P-007]

  P-009 Spring Boot Demo
      roles: implementer → test → reviewer
      acceptance: harness/evidence/implementer/P-009/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, deployment-and-configuration, security-and-privacy, observability]
      command_checks: [build, test]
      observed_flows: [spring-boot-demo-starts-against-real-db, hibernate-version-forced-745]
      dependencies: [P-008]

  P-010 项目内用户指南 docs/user-guide/
      roles: docs → test → (reviewer?)
      acceptance: harness/evidence/docs/P-010/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, deployment-and-configuration, security-and-privacy]
      command_checks: [build]
      observed_flows: [user-guide-configure-and-verify-path]
      dependencies: [P-009]

  P-011 硬化与 Accept 准备
      roles: implementer → test → reviewer
      acceptance: harness/evidence/orchestrator/P-011/ACCEPTANCE.md
      readiness: [functional-correctness, maintainability, compatibility, deployment-and-configuration, rollback-and-recovery]
      command_checks: [build, test]
      observed_flows: [full-verify-pass, definition-a-matrix-closed]
      dependencies: [P-010]

Next Build: B-001 → P-001
  (default: earliest ready Phase only; multi-Phase in one Build still serial unless orchestrator parallel_group)

Files Likely Affected:
  P-001: 根 pom.xml, dialect/**, demo-spring-boot/**, harness/verification.json, AGENTS.md
  P-002: contracts/xugu-dialect.contract.md, docs/feature-matrix-definition-a.md
  P-003–P-008: dialect/src/**
  P-009: demo-spring-boot/**
  P-010: docs/user-guide/**
  P-011: 缺口修复 + Accept 证据

Validation:
  python harness/scripts/harness_check.py
  python harness/scripts/branch_check.py
  python harness/scripts/verify.py  (P-001 填入真实 mvn 后才可能 PASS)

Risks:
  - 真实 XuguDB 不可达会阻塞 P-003+ IT
  - Boot 4.1.0 默认 Hibernate 7.4.1，须强制覆盖 7.4.5.Final（P-001 预留 / P-009 验证）
  - 定义 A 矩阵细节在 P-002 才可勾选；实现不得超矩阵
  - HEAD 仍无 commit；须 Human Gate 授权后才 must-commit

Next: await human Build approval (scope only — not parallel strategy)
```

---

## Initiative Briefing（skills/initiative.md 输出）

```text
Initiative Briefing

Initiative ID: I-001
Type: major
Phases planned: P-001 … P-011
Next Build: B-001 → P-001
Open Questions: （无）
Status: ready-for-build
Next: await Build scope approval (not parallel strategy)
```

---

## 请 Human Gate 明确回答（Build 范围）

**请批准 Build B-001 的 Phase 范围是哪些？**

编排建议（默认）：

> **批准 B-001 = 仅 P-001**（Maven 父工程 + `dialect/` + `demo-spring-boot/` 脚手架、JDBC systemPath、填入 `harness/verification.json` / `AGENTS.md` 真实 `mvn` 命令）。  
> **不要**在同一 Build 中批准 P-002…P-011；待 P-001 验收后再批下一 Build。

可选回复示例：
- 「批准 B-001，范围仅 P-001」
- 或显式列出多个 Phase（仍将由 orchestrator **串行**执行，不问并行）

---

## Git 状态（Plan 结束时）

- Branch: `feat/i-001-xugu-dialect-major`
- Commits: **无**（unborn HEAD；未做 baseline commit）
- 未授权：`tag` / `push` / Ship

---

## Files written this batch

- `harness/initiatives/I-001/brief.md`
- `harness/initiatives/INDEX.md`
- `harness/tasks/P-001.md` … `P-011.md`
- `harness/tasks/REGISTRY.yaml`
- `harness/builds/B-001.json`（`draft`）
- `harness/handoffs/orchestrator/i-001-plan.md`（本文件）
- `current-task.md`
- `harness/session/session-state.json`
- `harness/session/session-log.md`

---

## Handoff Summary

Completed:
- I-001 Status → active；Plan 物化 P-001…P-011；B-001 draft；功能分支已开

Changed Files:
- 见上「Files written this batch」

Validation:
- 分支创建成功；无业务实现；无 Build 自批

Known Issues:
- VERIFY INCOMPLETE 直至 P-001
- 无 git commit（有意）

Next 3 Steps:
1. Human Gate 批准 B-001 范围（推荐仅 P-001）
2. Orchestrator 派发 P-001 pipeline
3. P-001 完成后请批下一 Build（通常 P-002）

Resume From:
- `harness/handoffs/orchestrator/i-001-plan.md`
