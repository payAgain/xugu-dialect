# Handoff: B-002 Approved — Dispatch P-002 role_pipeline

> Role: orchestrator  
> Initiative: I-001 (major)  
> Build: B-002  
> Updated: 2026-07-14T16:49:00+08:00  
> Resume: 本文档 + `current-task.md` + `harness/session/session-state.json` + `harness/tasks/P-002.md`

---

## Session Briefing

| 项 | 值 |
|---|---|
| Current Goal | I-001：方言 jar + Spring Boot 4.1.0 demo + docs/user-guide（定义 A 全量） |
| Initiative | I-001 / major / **active** |
| Current Stage | **Build**（B-002 approved） |
| Active Build / Phases | B-002 / **P-002 only** |
| Working Branch | `feat/i-001-xugu-dialect-major` |
| Base Branch | `master` |
| Prior | B-001 complete; P-001 accepted (SHA in ACCEPTANCE) |

---

## Approval record

- **Phrase:** 「批准 B-002，范围仅 P-002」
- **approved_at:** 2026-07-14T16:48:00+08:00
- **approved_by:** Human Gate
- **Disk:** `harness/builds/B-002.json` → `status=approved`, `proposed_phase_ids=[P-002]`, `approved_phase_ids=[P-002]`
- **Phase:** `harness/tasks/P-002.md` → `build_id=B-002`, `status=in_progress`
- **REGISTRY:** P-002 → `in_progress`, `build_id=B-002`

---

## Next dispatch (serial)

Orchestrator **must not** implement Java. Spawn independent role instances:

| Order | step_id | role | Action |
|------:|---------|------|--------|
| 1 | **RP-01** | **architect-contract** | Author `contracts/xugu-dialect.contract.md` + definition A feature matrix; evidence/handoff under architect-contract namespace |
| 2 | RP-02 | test | After RP-01 passed: checklist matrix is actionable (IDs, statuses, evidence columns) |
| 3 | RP-03 | reviewer | Optional unless risk≥8 (`condition: risk_ge_8`); readonly review of contract/matrix boundaries |

**Immediate next worker:** RP-01 **architect-contract** for P-002.

Packet SSOT: `harness/tasks/P-002.md`  
Handoff target for RP-01: `harness/handoffs/architect-contract/P-002.yaml`  
Evidence: `harness/evidence/architect-contract/P-002/**`  
Acceptance: `harness/evidence/architect-contract/P-002/ACCEPTANCE.md`

### RP-01 notes
- SQL 真相源只读：`E:\Work\docs\content`；矩阵引用文档路径，不复制整库
- Allowed paths only（见 Phase packet）；Forbidden: 业务 Java 方言实现、改写 docs content、参考旁路实现源码

---

## Out of scope this Build

- P-003…P-011（未列入 `approved_phase_ids`）
- 方言 Java 实现、demo 业务扩展、user-guide 正文
- `tag` / `push` / Central 发布
- git commit（本 turn：Do NOT commit）

---

## Next 3 Steps

1. Spawn **architect-contract** instance for P-002 RP-01
2. On RP-01 PASS → spawn **test** RP-02
3. Evaluate RP-03 reviewer; P-002 ACCEPTANCE → 再请 Human Gate 批下一 Build（通常 P-003）
