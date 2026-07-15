# Handoff: B-006 Approved — Dispatch P-006 role_pipeline

> Role: orchestrator  
> Initiative: I-001 (major)  
> Build: B-006  
> Updated: 2026-07-15T10:29:00+08:00  
> Resume: 本文档 + current-task.md + harness/session/session-state.json + harness/tasks/P-006.md

---

## Session Briefing

| 项 | 值 |
|---|---|
| Current Goal | I-001：方言 jar + Spring Boot 4.1.0 demo + docs/user-guide（定义 A 全量） |
| Initiative | I-001 / major / **active** |
| Current Stage | **Build**（B-006 approved） |
| Active Build / Phases | B-006 / **P-006 only** |
| Working Branch | eat/i-001-xugu-dialect-major |
| Base Branch | master |
| Prior | B-005 complete; P-005 accepted (SHA 6864a390032a9352056f8963434a34f34a390f96) |

---

## Approval record

- **Phrase:** 「批准 B-006，范围仅 P-006」
- **approved_at:** 2026-07-15T10:28:00+08:00
- **approved_by:** Human Gate
- **Disk:** harness/builds/B-006.json → status=approved, proposed_phase_ids=[P-006], pproved_phase_ids=[P-006]
- **Phase:** harness/tasks/P-006.md → uild_id=B-006, status=in_progress
- **REGISTRY:** P-006 → in_progress, uild_id=B-006

---

## Next dispatch (serial)

Orchestrator **must not** implement Java. Spawn independent role instances:

| Order | step_id | role | Action |
|------:|---------|------|--------|
| 1 | **RP-01** | **implementer** | Register/contribute SQL functions per definition A matrix |
| 2 | RP-02 | test | Real XuguDB IT for function rendering and execution |
| 3 | RP-03 | reviewer | Required (condition: full_or_risk_ge_8; risk_score=8); readonly review of function coverage vs matrix |

**Immediate next worker:** RP-01 **implementer** for P-006.

Packet SSOT: harness/tasks/P-006.md  
Handoff target for RP-01: harness/handoffs/implementer/P-006.yaml  
Evidence: harness/evidence/implementer/P-006/**  
Acceptance (Phase): harness/evidence/implementer/P-006/ACCEPTANCE.md

### RP-01 notes
- SQL 真相源只读：E:\Work\docs\content；矩阵引用文档路径，不复制整库
- Allowed paths only（见 Phase packet）；Forbidden: 继承 MySQL/Oracle Dialect、改写官方 content、参考旁路实现源码
- risk_score=8 → reviewer 强制（RP-03）
- Observed flows: unction-registry-hql-sql-real-db

---

## Out of scope this Build

- P-007…P-011（未列入 pproved_phase_ids）
- demo 业务扩展、user-guide 正文（非本 Phase Allowed）
- 	ag / push / Central 发布
- git commit（本 turn：Do NOT commit）

---

## Next 3 Steps

1. Spawn **implementer** instance for P-006 RP-01
2. On RP-01 PASS → spawn **test** RP-02
3. Then reviewer RP-03；P-006 ACCEPTANCE 后再请 Human Gate 批下一 Build（通常 P-007）
