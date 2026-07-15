# Handoff: B-005 Approved — Dispatch P-005 role_pipeline

> Role: orchestrator  
> Initiative: I-001 (major)  
> Build: B-005  
> Updated: 2026-07-15T09:57:00+08:00  
> Resume: 本文档 + `current-task.md` + `harness/session/session-state.json` + `harness/tasks/P-005.md`

---

## Session Briefing

| 项 | 值 |
|---|---|
| Current Goal | I-001：方言 jar + Spring Boot 4.1.0 demo + docs/user-guide（定义 A 全量） |
| Initiative | I-001 / major / **active** |
| Current Stage | **Build**（B-005 approved） |
| Active Build / Phases | B-005 / **P-005 only** |
| Working Branch | `feat/i-001-xugu-dialect-major` |
| Base Branch | `master` |
| Prior | B-004 complete; P-004 accepted (SHA `7b995af4038a8fd3c41ccc90c0b89fa2a4494718`) |

---

## Approval record

- **Phrase:** 「批准 B-005，范围仅 P-005」
- **approved_at:** 2026-07-15T09:56:00+08:00
- **approved_by:** Human Gate
- **Disk:** `harness/builds/B-005.json` → `status=approved`, `proposed_phase_ids=[P-005]`, `approved_phase_ids=[P-005]`
- **Phase:** `harness/tasks/P-005.md` → `build_id=B-005`, `status=in_progress`
- **REGISTRY:** P-005 → `in_progress`, `build_id=B-005`

---

## Next dispatch (serial)

Orchestrator **must not** implement Java. Spawn independent role instances:

| Order | step_id | role | Action |
|------:|---------|------|--------|
| 1 | **RP-01** | **implementer** | Implement identity/sequence/generators per definition A matrix |
| 2 | RP-02 | test | Real XuguDB IT for identity and sequences |
| 3 | RP-03 | reviewer | Required (`condition: full_or_risk_ge_8`; risk_score=8); readonly review of ID generation SQL |

**Immediate next worker:** RP-01 **implementer** for P-005.

Packet SSOT: `harness/tasks/P-005.md`  
Handoff target for RP-01: `harness/handoffs/implementer/P-005.yaml`  
Evidence: `harness/evidence/implementer/P-005/**`  
Acceptance (Phase): `harness/evidence/implementer/P-005/ACCEPTANCE.md`

### RP-01 notes
- SQL 真相源只读：`E:\Work\docs\content`；矩阵引用文档路径，不复制整篇
- Allowed paths only（见 Phase packet）；Forbidden: 继承 MySQL/Oracle Dialect、改写官方 content、参考旁路实现源码
- risk_score=8 → reviewer 强制（RP-03）
- Observed flows: `identity-insert-real-db`, `sequence-generator-real-db`

---

## Out of scope this Build

- P-006…P-011（未列入 `approved_phase_ids`）
- demo 业务扩展、user-guide 正文（非本 Phase Allowed）
- `tag` / `push` / Central 发布
- git commit（本 turn：Do NOT commit）

---

## Next 3 Steps

1. Spawn **implementer** instance for P-005 RP-01
2. On RP-01 PASS → spawn **test** RP-02
3. Then reviewer RP-03；P-005 ACCEPTANCE 后再请 Human Gate 批下一 Build（通常 P-006）
