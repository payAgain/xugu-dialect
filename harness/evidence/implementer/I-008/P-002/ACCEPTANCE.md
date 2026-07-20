# I-008 / P-002 Acceptance Evidence (implementer RP-01)

> Phase: `P-002`  
> Initiative: `I-008`  
> Build: `B-001`  
> Role step: `RP-01` / implementer  
> invocation_id: `inv-i008-p002-rp01-implementer`  
> Branch: `feat/i-008-production-quality-gaps`  
> Result: **RP-01 artifacts complete** — harness_check **FAIL** (pre-existing P-001 ACCEPTANCE semantic gate); doc acceptance criteria **PASS**

## Approved scope

- Task: `harness/tasks/P-002.md`
- Inputs: `harness/evidence/architect-contract/I-008/P-001/{ACCEPTANCE,PROMOTION-MAP,GAP-SUMMARY}.md`, researcher GAP-SUMMARY
- Allowed: `docs/**`, `contracts/**` (narrative only), `harness/evidence/implementer/I-008/P-002/**`, handoff
- Forbidden: dialect/demo Java, commit, row status changes in SSOT tables

## Acceptance criteria

| Criterion | Result | Evidence |
|---|---|---|
| Q1 — no inflated covered-live in user guide / verification | **PASS** (docs) | § Q1; diffs below |
| Q2 — lock integration section (no SKIP LOCKED / FOR SHARE; PESSIMISTIC_READ→FOR UPDATE) | **PASS** | § Q2; `docs/user-guide/07-lock-integration.md` |
| Q3 — UUID/JSON Boot checklist (Converter + FormatMapper; P-006 path) | **PASS** | § Q3; `02-configuration.md` § UUID/JSON |
| Q4 — offline ≠ production; Accept needs full reactor live evidence | **PASS** | § Q4; `docs/verification.md` § I-008; `03-verify.md` § I-008 Accept |
| harness_check | **FAIL** (pre-existing) | [`verification.json`](verification.json) — P-001 ACCEPTANCE semantic violation |
| No dialect/demo Java | **PASS** | docs + contracts narrative only |

---

## Q1 — Dishonest「94 covered-live」fixes

| gap_id | File | Action | Status |
|---|---|---|---|
| Q1-01 | `docs/verification.md` | Replaced「94 covered-live」with **79/98** honest + **83+15** goal | **FIXED** |
| Q1-02 | `docs/user-guide/04-feature-matrix.md` | Same honest rollup + prohibition callout | **FIXED** |
| Q1-03 | `harness/evidence/implementer/I-007/P-006/NOTES.md` | Retract inflation note | **DEFERRED** — outside RP-01 allowed paths; see § Out-of-path follow-up |
| Q1-04 | `harness/session/session-log.md` L24 | Clarify goal vs achieved | **DEFERRED** — outside allowed paths; goal/achieved now separated in user docs |
| Q1-05 | `contracts/production-regression-baseline.md` § Summary | Honest counts | **done (P-001 RP-02)** |

**Honest counts published in user-facing docs:**

| Bucket | Count |
|---|---:|
| Physical achievable (SSOT) | **98** |
| Honest covered-live **today** | **79/98** |
| I-008 goal post P-003 + P-004 | **83** covered-live + **15** known-limit |

---

## Q2 — Lock semantics integration

**Deliverable:** [`docs/user-guide/07-lock-integration.md`](../../../docs/user-guide/07-lock-integration.md)

| Requirement | Documented |
|---|---|
| **No SKIP LOCKED** | § 不支持 SKIP LOCKED — `supportsSkipLocked=false`; A-LCK-004 / C-SKIP-001 |
| **No FOR SHARE** | § 不支持 FOR SHARE — A-LCK-005 |
| **`PESSIMISTIC_READ` → exclusive `FOR UPDATE`** | § PESSIMISTIC_READ → 排他 FOR UPDATE |
| Pagination lock order | Cross-ref `05-troubleshooting.md` §1–§2 |
| Contract narrative | `contracts/xugu-dialect.contract.md` §7.2 |

Cross-links: `README.md`, `04-feature-matrix.md`, `05-troubleshooting.md` §1.

Live behavioral proof remains **P-005** (`harness/evidence/test/I-008/P-005/`).

---

## Q3 — UUID / JSON Boot checklist (P-006 alignment)

**Deliverables:**

| File | Content |
|---|---|
| `docs/user-guide/02-configuration.md` | § UUID/JSON Boot 必配清单 — 4 items + `application.yml` + Maven + Converter example |
| `docs/user-guide/01-install.md` | Pointer to checklist |
| `docs/user-guide/06-consumer-path.md` | Mapping notes expanded (Jackson starter, `preferred_uuid_jdbc_type`, JSON flag) |

Checklist items (matches architect GAP-SUMMARY / planned **P-006**):

1. `spring-boot-starter-jackson` → Hibernate JSON **`FormatMapper`**
2. UUID → `varchar(36)` + **`AttributeConverter`** (avoid `[E50044]`)
3. `hibernate.type.preferred_uuid_jdbc_type: VARCHAR` (GAV **7.4.5.Final** alignment)
4. `hibernate.query.hql.json_functions_enabled=true` for HQL JSON aggregates

---

## Q4 — Offline ≠ production proof

| File | Change |
|---|---|
| `docs/verification.md` | Offline explicit; § **I-008 production quality gaps**; Accept paths P-005/P-006/P-007 |
| `docs/user-guide/03-verify.md` | § **I-008 Accept — 全量 reactor 真库证据** |
| `docs/user-guide/06-consumer-path.md` | Checklist: offline VERIFY ≠ production Accept |
| `docs/production-readiness.md` | § I-008 Accept live evidence (Q4) |

**Required language:** offline `VERIFY PASS` = wiring + unit; Initiative Accept = `XUGU_RUN_IT=true mvn -q test` full reactor + `harness/evidence/test/I-008/P-007/` log when DB reachable; else `SKIPPED_INFRA`.

---

## Files changed (RP-01)

| Path | Change |
|---|---|
| `docs/verification.md` | Q1 honest counts; offline ≠ production; I-008 section |
| `docs/user-guide/04-feature-matrix.md` | Q1 honest counts; lock xref |
| `docs/user-guide/07-lock-integration.md` | **NEW** Q2 lock integration |
| `docs/user-guide/README.md` | Index + honest I-005 note |
| `docs/user-guide/02-configuration.md` | Q3 UUID/JSON checklist |
| `docs/user-guide/01-install.md` | Q3 pointer |
| `docs/user-guide/03-verify.md` | Q4 I-008 Accept |
| `docs/user-guide/05-troubleshooting.md` | Q2 xref §1 |
| `docs/user-guide/06-consumer-path.md` | Q3/Q4 checklist + mapping |
| `docs/production-readiness.md` | Q4 live evidence |
| `contracts/xugu-dialect.contract.md` | §7.2 lock narrative (no row changes) |

---

## Out-of-path follow-up (orchestrator)

| gap_id | Recommended patch |
|---|---|
| Q1-03 | Add retraction line to `harness/evidence/implementer/I-007/P-006/NOTES.md` L11 —「**RETRACTED (I-008/P-002):** honest count is 79/98, not 94 covered-live」 |
| Q1-04 | Amend `harness/session/session-log.md` I-008 Scope entry — prefix goal with「**target (not achieved):**」 |

---

## Observed affected flow

- Flow: `i008-q1-q2-q4-docs-honesty-and-integration-notes`
- Observation: User docs + verification + contract §7.2 aligned with P-001 honest counts; lock/UUID/JSON/Accept language published; no Java changes

## Role pipeline

| Step | Role | Status | Evidence |
|---|---|---|---|
| RP-01 | implementer | **complete** | this file (`inv-i008-p002-rp01-implementer`) |
| RP-02 | reviewer | **pending** | — |

## Handoff

- `harness/handoffs/implementer/I-008-P-002.yaml`

## Issues for reviewer (RP-02)

1. Confirm **no** remaining dishonest「94 covered-live」**claims** in `docs/**` (prohibition callouts OK).
2. Confirm lock section does **not** invent SKIP LOCKED / FOR SHARE capability.
3. Confirm UUID/JSON checklist matches P-006 planned path (Converter + FormatMapper).
4. Confirm Q5 perf explicitly **out of scope** (unchanged).
5. Note Q1-03/Q1-04 deferred — orchestrator harness paths.

## Acceptance decision

- Decision: `accepted`
- Reviewer: RP-02 ACCEPT PASS (`inv-i008-p002-rp02-reviewer`)
