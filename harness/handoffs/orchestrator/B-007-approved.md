# B-007 Approved — Dispatch P-007

## Session Briefing

Current Goal: I-001 major — Xugu Hibernate dialect (definition A matrix)
Initiative ID / Type: I-001 / major
Current Stage: Build
Active Build / Phases: B-007 / P-007
Working Branch: feat/i-001-xugu-dialect-major
Base Branch: main (GitHub Flow)
Next 3 Steps:
1. Spawn implementer RP-01 for P-007
2. On RP-01 PASS → test RP-02
3. Then reviewer RP-03; P-007 ACCEPTANCE 后再请 Human Gate 批下一 Build（通常 P-008）
Relevant Files:
- harness/builds/B-007.json
- harness/tasks/P-007.md
- contracts/feature-matrix-definition-a.md
Validation Commands:
- mvn -q test
- mvn -q test -Dxugu.run.integration=true
- python harness/scripts/verify.py
Known Risks / Blockers:
- risk_score=8 → RP-03 reviewer required
- Destructive DDL / FK must clean up on test DB
Open Questions: none
Resume From: this handoff → RP-01 implementer

---

## Approval record

- **Phrase:** 「批准 B-007，范围仅 P-007」
- **approved_at:** 2026-07-15T11:26:00+08:00
- **approved_by:** Human Gate
- **Disk:** harness/builds/B-007.json — status=approved, proposed_phase_ids=[P-007], approved_phase_ids=[P-007]
- **Phase:** harness/tasks/P-007.md — build_id=B-007, status=in_progress; dependencies=[P-006]
- **REGISTRY:** P-007 → in_progress, build_id=B-007

---

## Next dispatch (serial)

Orchestrator **must not** implement Java. Spawn independent role instances:

| Order | step_id | role | Action |
|------:|---------|------|--------|
| 1 | **RP-01** | **implementer** | Implement schema tooling, temp tables, comments, FK per definition A matrix |
| 2 | RP-02 | test | Real XuguDB IT for schema/temp/comments/FK |
| 3 | RP-03 | reviewer | Required (condition: full_or_risk_ge_8; risk_score=8); readonly review of destructive DDL / FK |

**Immediate next worker:** RP-01 **implementer** for P-007.

Packet SSOT: harness/tasks/P-007.md  
Handoff target for RP-01: harness/handoffs/implementer/P-007.yaml  
Evidence: harness/evidence/implementer/P-007/**  
Acceptance (Phase): harness/evidence/implementer/P-007/ACCEPTANCE.md

### RP-01 notes
- SQL 真相源只读：E:\Work\docs\content；矩阵引用文档路径，不复制整份
- Allowed paths only（见 Phase packet）；Forbidden: 继承 MySQL/Oracle Dialect、改写官方 content、参考旁路实现源码
- risk_score=8 → reviewer 强制（RP-03）
- Observed flows: schema-tooling-real-db, temp-table-comments-fk-real-db
- 破坏性 DDL 须可清理；测试库安全

---

## Out of scope this Build

- P-008…P-011（未列入 approved_phase_ids）
- demo 业务扩展、user-guide 正文（非本 Phase Allowed）
- tag / push / Central 发布
- git commit（本 turn：Do NOT commit）

---

## Next 3 Steps

1. Spawn **implementer** instance for P-007 RP-01
2. On RP-01 PASS → spawn **test** RP-02
3. Then reviewer RP-03；P-007 ACCEPTANCE 后再请 Human Gate 批下一 Build（通常 P-008）
