# B-011 Approved — Dispatch P-011

## Session Briefing

Current Goal: I-001 major — Xugu Hibernate dialect (definition A matrix)
Initiative ID / Type: I-001 / major
Current Stage: Build
Active Build / Phases: B-011 / P-011
Working Branch: feat/i-001-xugu-dialect-major
Base Branch: main (GitHub Flow)
Next 3 Steps:
1. Spawn implementer RP-01 (`impl-p011-20260715`) — matrix closure + packaging polish
2. On RP-01 PASS → test RP-02 (`test-p011-20260715`) full suite + verify.py
3. RP-03 reviewer required (condition=null despite risk=7) → Accept + must-commit；再请 Human Gate **Initiative I-001 Accept**（非 Ship）
Relevant Files:
- harness/builds/B-011.json
- harness/tasks/P-011.md
- contracts/feature-matrix-definition-a.md
- docs/user-guide/**
Validation Commands:
- mvn -q -DskipTests package
- mvn -q test
- mvn -q test -Dxugu.run.integration=true
- python harness/scripts/verify.py
Known Risks / Blockers:
- Ship / tag / push / Central forbidden this Build
- Do not invent new 可实现 scope without documenting as 延后
Open Questions: none
Resume From: this handoff → RP-01 implementer

---

## Approval record

- **Phrase:** 「批准 B-011，范围仅 P-011」
- **approved_at:** 2026-07-15T15:58:00+08:00
- **approved_by:** Human Gate
- **Disk:** harness/builds/B-011.json → status=approved, proposed_phase_ids=[P-011], approved_phase_ids=[P-011]
- **Phase:** harness/tasks/P-011.md → build_id=B-011, status=in_progress; RP-03 condition=**null** (Accept-prep review required despite risk_score=7)
- **REGISTRY:** P-011 → in_progress, build_id=B-011

---

## Next dispatch (serial)

| Order | step_id | role | invocation_id | Action |
|------:|---------|------|---------------|--------|
| 1 | **RP-01** | **implementer** | `impl-p011-20260715` | Close Definition A matrix residual gaps; packaging polish; MATRIX-CLOSURE.md |
| 2 | RP-02 | test | `test-p011-20260715` | Full suite + verify.py PASS |
| 3 | RP-03 | reviewer | `rev-p011-20260715` | Accept-prep REVIEW (required; condition=null) |

**Immediate next worker:** RP-01 **implementer** for P-011.

Packet SSOT: harness/tasks/P-011.md  
Handoff target for RP-01: harness/handoffs/implementer/P-011.yaml  
Evidence: harness/evidence/implementer/P-011/**

### RP-01 notes
- Audit every **可实现** row → Phase evidence (P-003..P-010) or fix/document
- Root README → docs/user-guide; AGENTS.md commands match verification.json; no secrets
- Forbidden: Ship; sibling hibernate-dialect; invent new 可实现 without 延后

---

## Out of scope this Build

- Ship / tag / push / Maven Central
- Expanding beyond P-011
- Sibling hibernate-dialect reference

---

## Next 3 Steps

1. Spawn **implementer** `impl-p011-20260715`
2. On RP-01 PASS → **test** `test-p011-20260715`
3. **reviewer** `rev-p011-20260715` → Accept + must-commit → Human Gate Initiative Accept（非 Ship）
