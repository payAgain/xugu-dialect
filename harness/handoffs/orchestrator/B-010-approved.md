# B-010 Approved — Dispatch P-010

## Session Briefing

Current Goal: I-001 major — Xugu Hibernate dialect (definition A matrix)
Initiative ID / Type: I-001 / major
Current Stage: Build
Active Build / Phases: B-010 / P-010
Working Branch: feat/i-001-xugu-dialect-major
Base Branch: main (GitHub Flow)
Next 3 Steps:
1. Spawn docs RP-01 for P-010 (`docs/user-guide/`)
2. On RP-01 PASS → test RP-02 walkthrough checklist
3. RP-03 reviewer optional (risk_score=4 < 8 → expect skip); P-010 ACCEPTANCE 后再请 Human Gate 批 B-011 / P-011
Relevant Files:
- harness/builds/B-010.json
- harness/tasks/P-010.md
- docs/user-guide/**
- contracts/xugu-dialect.contract.md
- contracts/feature-matrix-definition-a.md
Validation Commands:
- python harness/scripts/verify.py
- mvn -q test (optional offline smoke for guide commands)
Known Risks / Blockers:
- Docs only; do not rewrite `E:\Work\docs\content`
- Live DB not required for authoring; guide must state IT/demo prerequisites
Open Questions: none
Resume From: this handoff → RP-01 docs

---

## Approval record

- **Phrase:** 「批准 B-010，范围仅 P-010」
- **approved_at:** 2026-07-15T15:38:00+08:00
- **approved_by:** Human Gate
- **Disk:** harness/builds/B-010.json → status=approved, proposed_phase_ids=[P-010], approved_phase_ids=[P-010]
- **Phase:** harness/tasks/P-010.md → build_id=B-010, status=in_progress; dependencies=[P-009]
- **REGISTRY:** P-010 → in_progress, build_id=B-010
- **RP-03:** required=false, condition=risk_ge_8 (optional; risk_score=4 → skip at Accept if unused)

---

## Next dispatch (serial)

Orchestrator **must not** author guide content as Human Gate. Spawn independent role instances:

| Order | step_id | role | Action |
|------:|---------|------|--------|
| 1 | **RP-01** | **docs** | Author `docs/user-guide/` (install, config, verify, matrix, troubleshooting) |
| 2 | RP-02 | test | Walkthrough checklist against guide (dry-run or observed) |
| 3 | RP-03 | reviewer | Optional (risk_ge_8); skip when risk_score &lt; 8 |

**Immediate next worker:** RP-01 **docs** for P-010.

Packet SSOT: harness/tasks/P-010.md  
Handoff target for RP-01: harness/handoffs/docs/P-010.yaml  
Evidence: harness/evidence/docs/P-010/**  
Acceptance (Phase): harness/evidence/docs/P-010/ACCEPTANCE.md

### RP-01 notes
- Allowed: `docs/user-guide/**`, matrix/contract cross-links, P-010 evidence/handoffs
- Forbidden: `E:\Work\docs\content`; dialect Java (except doc path typo); Ship / Central as required work
- Observed flow: user-guide-configure-and-verify-path
- If `agents/docs.md` missing → create minimal docs role file before/during dispatch

---

## Out of scope this Build

- P-011（未列入 approved_phase_ids）
- dialect 核心实现改动
- tag / push / Central 发布
- git commit（本 turn：Do NOT commit unless Human Gate asks）

---

## Next 3 Steps

1. Spawn **docs** instance for P-010 RP-01
2. On RP-01 PASS → spawn **test** RP-02
3. Skip RP-03 if risk&lt;8；P-010 ACCEPTANCE 后再请 Human Gate 批下一 Build（通常 P-011）
