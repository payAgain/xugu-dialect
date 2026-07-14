# Handoff: B-001 Approved → Dispatch P-001 role_pipeline

> Role: orchestrator  
> Initiative: I-001 (major)  
> Build: B-001  
> Updated: 2026-07-14T16:08:00+08:00  
> Resume: 本文件 + `current-task.md` + `harness/session/session-state.json` + `harness/tasks/P-001.md`

---

## Session Briefing

| 项 | 值 |
|---|---|
| Current Goal | I-001：方言 jar + Spring Boot 4.1.0 demo + docs/user-guide（定义 A 全量） |
| Initiative | I-001 / major / **active** |
| Current Stage | **Build**（B-001 approved） |
| Active Build / Phases | B-001 / **P-001 only** |
| Working Branch | `feat/i-001-xugu-dialect-major`（unborn HEAD，无 commit） |
| Base Branch | `master`（同样无 commit） |

---

## Approval record

- **Phrase:** 「批准 B-001，范围仅 P-001」
- **approved_at:** 2026-07-14T16:08:00+08:00
- **approved_by:** Human Gate
- **Disk:** `harness/builds/B-001.json` → `status=approved`, `approved_phase_ids=[P-001]`
- **Phase:** `harness/tasks/P-001.md` → `build_id=B-001`, `status=in_progress`
- **REGISTRY:** P-001 → `in_progress`

---

## Next dispatch (serial)

Orchestrator **must not** implement Java/pom. Spawn independent role instances:

| Order | step_id | role | Action |
|------:|---------|------|--------|
| 1 | **RP-01** | **architect-contract** | Confirm Maven layout vs `docs/architecture.md` and GAV/JDBC wiring; write evidence/handoff under architect-contract namespace |
| 2 | RP-02 | implementer | After RP-01 passed: scaffold parent + `dialect/` + `demo-spring-boot/`; wire JDBC; fill `verification.json` + AGENTS Real commands |
| 3 | RP-03 | test | After RP-02: verify mvn build/test; harness verify evidence |
| 4 | RP-04 | reviewer | After RP-03: Full + risk≥8 review (readonly); required before commit of scaffold |

**Immediate next worker:** RP-01 **architect-contract** for P-001.

Packet SSOT: `harness/tasks/P-001.md`  
Handoff target for RP-01: `harness/handoffs/architect-contract/P-001.yaml`  
Evidence: `harness/evidence/architect-contract/P-001/**`

---

## Out of scope this Build

- P-002…P-011（未列入 `approved_phase_ids`）
- 定义 A 方言能力实现、完整 demo 业务、user-guide 正文
- `tag` / `push` / Central 发布
- git commit（本 turn 明确：Do NOT commit；后续 must-commit 需 Human Gate / 流程授权）

---

## Next 3 Steps

1. Spawn **architect-contract** instance for P-001 RP-01
2. On RP-01 PASS → spawn **implementer** RP-02
3. Continue RP-03 test → RP-04 reviewer → P-001 ACCEPTANCE → 再请 Human Gate 批下一 Build
