# B-008 Approved — Dispatch P-008

## Session Briefing

Current Goal: I-001 major — Xugu Hibernate dialect (definition A matrix)
Initiative ID / Type: I-001 / major
Current Stage: Build
Active Build / Phases: B-008 / P-008
Working Branch: feat/i-001-xugu-dialect-major
Base Branch: main (GitHub Flow)
Next 3 Steps:
1. Spawn implementer RP-01 for P-008
2. On RP-01 PASS → test RP-02
3. Then reviewer RP-03; P-008 ACCEPTANCE 后再请 Human Gate 批下一 Build（通常 P-009）
Relevant Files:
- harness/builds/B-008.json
- harness/tasks/P-008.md
- contracts/feature-matrix-definition-a.md
Validation Commands:
- mvn -q test
- mvn -q test -Dxugu.run.integration=true
- python harness/scripts/verify.py
Known Risks / Blockers:
- risk_score=8 → RP-03 reviewer required
- SPI / META-INF/services packaging must be in jar
Open Questions: none
Resume From: this handoff → RP-01 implementer

---

## Approval record

- **Phrase:** 「批准 B-008，范围仅 P-008」
- **approved_at:** 2026-07-15T13:49:00+08:00
- **approved_by:** Human Gate
- **Disk:** harness/builds/B-008.json → status=approved, proposed_phase_ids=[P-008], approved_phase_ids=[P-008]
- **Phase:** harness/tasks/P-008.md → build_id=B-008, status=in_progress; dependencies=[P-007]
- **REGISTRY:** P-008 → in_progress, build_id=B-008

---

## Next dispatch (serial)

Orchestrator **must not** implement Java. Spawn independent role instances:

| Order | step_id | role | Action |
|------:|---------|------|--------|
| 1 | **RP-01** | **implementer** | Implement DialectResolver SPI + META-INF/services wiring; verify explicit dialect config |
| 2 | RP-02 | test | Real XuguDB IT for explicit dialect and SPI auto-detect |
| 3 | RP-03 | reviewer | Required (condition: full_or_risk_ge_8; risk_score=8); readonly review of SPI compatibility / service loader packaging |

**Immediate next worker:** RP-01 **implementer** for P-008.

Packet SSOT: harness/tasks/P-008.md  
Handoff target for RP-01: harness/handoffs/implementer/P-008.yaml  
Evidence: harness/evidence/implementer/P-008/**  
Acceptance (Phase): harness/evidence/implementer/P-008/ACCEPTANCE.md

### RP-01 notes
- SQL 真相源只读：E:\Work\docs\content；矩阵引用文档路径，不复制整本
- Allowed paths only（见 Phase packet）；Forbidden: 旁路移植、demo 完整业务、官方 content 写入
- risk_score=8 → reviewer 强制（RP-03）
- Observed flows: explicit-dialect-config, spi-dialect-resolver-autodetect
- Jar 内 META-INF/services 条目必须可验证

---

## Out of scope this Build

- P-009…P-011（未列入 approved_phase_ids）
- demo 业务扩展、user-guide 正文（非本 Phase Allowed）
- tag / push / Central 发布
- git commit（本 turn：Do NOT commit）

---

## Next 3 Steps

1. Spawn **implementer** instance for P-008 RP-01
2. On RP-01 PASS → spawn **test** RP-02
3. Then reviewer RP-03；P-008 ACCEPTANCE 后再请 Human Gate 批下一 Build（通常 P-009）
