# Handoff: B-003 Approved — Dispatch P-003 role_pipeline

> Role: orchestrator  
> Initiative: I-001 (major)  
> Build: B-003  
> Updated: 2026-07-14T17:26:00+08:00  
> Resume: 本文档 + `current-task.md` + `harness/session/session-state.json` + `harness/tasks/P-003.md`

---

## Session Briefing

| 项 | 值 |
|---|---|
| Current Goal | I-001：方言 jar + Spring Boot 4.1.0 demo + docs/user-guide（定义 A 全量） |
| Initiative | I-001 / major / **active** |
| Current Stage | **Build**（B-003 approved） |
| Active Build / Phases | B-003 / **P-003 only** |
| Working Branch | `feat/i-001-xugu-dialect-major` |
| Base Branch | `master` |
| Prior | B-002 complete; P-002 accepted (SHA in ACCEPTANCE) |

---

## Approval record

- **Phrase:** 「批准 B-003，范围仅 P-003」
- **approved_at:** 2026-07-14T17:25:00+08:00
- **approved_by:** Human Gate
- **Disk:** `harness/builds/B-003.json` → `status=approved`, `proposed_phase_ids=[P-003]`, `approved_phase_ids=[P-003]`
- **Phase:** `harness/tasks/P-003.md` → `build_id=B-003`, `status=in_progress`
- **REGISTRY:** P-003 → `in_progress`, `build_id=B-003`

---

## Next dispatch (serial)

Orchestrator **must not** implement Java. Spawn independent role instances:

| Order | step_id | role | Action |
|------:|---------|------|--------|
| 1 | **RP-01** | **architect-contract** | Map matrix types/DDL rows to package structure; evidence/handoff under architect-contract namespace |
| 2 | RP-02 | implementer | After RP-01 passed: implement XuguDialect types + DDL generation per matrix |
| 3 | RP-03 | test | Real XuguDB IT for type mapping and DDL |
| 4 | RP-04 | reviewer | Required (`condition: full_or_risk_ge_8`; risk_score=9); readonly review of dialect core + SQL correctness |

**Immediate next worker:** RP-01 **architect-contract** for P-003.

Packet SSOT: `harness/tasks/P-003.md`  
Handoff target for RP-01: `harness/handoffs/architect-contract/P-003.yaml`  
Evidence: `harness/evidence/architect-contract/P-003/**`  
Acceptance (Phase): `harness/evidence/implementer/P-003/ACCEPTANCE.md`

### RP-01 notes
- SQL 真相源只读：`E:\Work\docs\content`；矩阵引用文档路径，不复制整库
- Allowed paths only（见 Phase packet）；Forbidden: 继承 MySQL/Oracle Dialect、改写官方 content、参考旁路实现源码
- risk_score=9 → reviewer 强制（RP-04）

---

## Out of scope this Build

- P-004…P-011（未列入 `approved_phase_ids`）
- demo 业务扩展、user-guide 正文（非本 Phase Allowed）
- `tag` / `push` / Central 发布
- git commit（本 turn：Do NOT commit）

---

## Next 3 Steps

1. Spawn **architect-contract** instance for P-003 RP-01
2. On RP-01 PASS → spawn **implementer** RP-02
3. Then test RP-03 → reviewer RP-04；P-003 ACCEPTANCE → 再请 Human Gate 批下一 Build（通常 P-004）
