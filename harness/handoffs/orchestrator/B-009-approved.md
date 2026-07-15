# B-009 Approved — Dispatch P-009

## Session Briefing

Current Goal: I-001 major — Xugu Hibernate dialect (definition A matrix)
Initiative ID / Type: I-001 / major
Current Stage: Build
Active Build / Phases: B-009 / P-009
Working Branch: feat/i-001-xugu-dialect-major
Base Branch: main (GitHub Flow)
Next 3 Steps:
1. Spawn implementer RP-01 for P-009
2. On RP-01 PASS → test RP-02
3. Then reviewer RP-03 (required); P-009 ACCEPTANCE 后再请 Human Gate 批下一 Build（通常 P-010）
Relevant Files:
- harness/builds/B-009.json
- harness/tasks/P-009.md
- demo-spring-boot/**
Validation Commands:
- mvn -q test
- mvn -q test -Dxugu.run.integration=true
- python harness/scripts/verify.py
Known Risks / Blockers:
- risk_score=7 but Packet requires RP-03 reviewer (secrets / Boot–Hibernate alignment); condition=null so not skipped
- Credentials must be env-overridable; no production secrets in repo
Open Questions: none
Resume From: this handoff → RP-01 implementer

---

## Approval record

- **Phrase:** 「批准 B-009，范围仅 P-009」
- **approved_at:** 2026-07-15T14:49:00+08:00
- **approved_by:** Human Gate
- **Disk:** harness/builds/B-009.json → status=approved, proposed_phase_ids=[P-009], approved_phase_ids=[P-009]
- **Phase:** harness/tasks/P-009.md → build_id=B-009, status=in_progress; dependencies=[P-008]
- **REGISTRY:** P-009 → in_progress, build_id=B-009
- **RP-03:** required=true, condition=null (keep reviewer despite risk_score=7)

---

## Next dispatch (serial)

Orchestrator **must not** implement Java. Spawn independent role instances:

| Order | step_id | role | Action |
|------:|---------|------|--------|
| 1 | **RP-01** | **implementer** | Implement Spring Boot 4.1.0 demo with hibernate.version=7.4.5.Final; env-overridable connection; compatible_mode=NONE |
| 2 | RP-02 | test | Smoke/IT demo against real XuguDB |
| 3 | RP-03 | reviewer | Required (condition=null; Packet insists despite risk_score=7); readonly review of secrets handling and Boot/Hibernate alignment |

**Immediate next worker:** RP-01 **implementer** for P-009.

Packet SSOT: harness/tasks/P-009.md  
Handoff target for RP-01: harness/handoffs/implementer/P-009.yaml  
Evidence: harness/evidence/implementer/P-009/**  
Acceptance (Phase): harness/evidence/implementer/P-009/ACCEPTANCE.md

### RP-01 notes
- Allowed paths only（见 Phase packet）：`demo-spring-boot/**`, demo-related pom attrs, contracts/demo if needed, P-009 evidence/handoffs
- Forbidden: dialect core Java into demo; production secrets; bypass dialect; rewrite official content
- Observed flows: spring-boot-demo-starts-against-real-db, hibernate-version-forced-745
- Force Hibernate **7.4.5.Final** (not Boot BOM default)

---

## Out of scope this Build

- P-010…P-011（未列入 approved_phase_ids）
- dialect 核心实现改动（非本 Phase Allowed）
- tag / push / Central 发布
- git commit（本 turn：Do NOT commit）

---

## Next 3 Steps

1. Spawn **implementer** instance for P-009 RP-01
2. On RP-01 PASS → spawn **test** RP-02
3. Then reviewer RP-03；P-009 ACCEPTANCE 后再请 Human Gate 批下一 Build（通常 P-010）
