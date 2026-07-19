# I-007 / P-001 Acceptance Evidence (architect-contract RP-02)

> Phase: `P-001`  
> Initiative: `I-007`  
> Build: `B-001`  
> Role step: `RP-02` / architect-contract  
> invocation_id: `inv-i007-p001-rp02-architect`  
> Result: **ACCEPT PASS** (pending reviewer RP-03)

## Approved scope

- Task: `harness/tasks/P-001.md`
- Researcher input: `harness/evidence/researcher/I-007/P-001/{INVENTORY,GAP-SUMMARY}.md` (`inv-i007-p001-rp01-researcher`)
- I-005 SSOT: `contracts/production-regression-baseline.md`
- I-006 SSOT: `contracts/consumer-path-baseline.md` (FROZEN)
- Initiative brief: `harness/initiatives/I-007/brief.md`
- Deliverable: `contracts/i007-capability-hardening-plan.md`

## Acceptance criteria

| Criterion | Result | Evidence |
|---|---|---|
| Every A/B/C item has owner Phase or fold/defer | **PASS** | Gap map § Track A/B/C in `i007-capability-hardening-plan.md`; P-003 thin-fold documented |
| C-BULK-002 strategy locked binary (not ambiguous) | **PASS** | § C-BULK-002 STRATEGY LOCK — **prefer-live-unblock**; P-002 decides `covered-live` **or** `known-limit-documented` |
| Live-log artifact expectation named for later Phases | **PASS** | § Accept evidence hardening — `harness/evidence/test/I-007/P-00x/` convention |
| harness_check | **PASS** | `verification.json` |
| branch_check | **PASS** | `verification.json` |
| No dialect/demo Java changes | **PASS** | contracts + evidence only |
| GAV / NONE / no Ship | **PASS** | documented in plan |

## C-BULK-002 strategy lock

| Field | Locked value |
|---|---|
| **P-001 primary lock** | **prefer-live-unblock** |
| **P-002 mandate** | Attempt gated live bulk-insert IT on JOINED+IDENTITY before permanent-limit |
| **Allowed P-002 outcomes (exactly one)** | **`covered-live`** **OR** **`known-limit-documented`** |
| **Forbidden** | Dual open state; status change without live attempt artifact |
| **Permanent-limit gate** | Logged JDBC failure after good-faith live IT (GetGeneratedKeys / `distillTbName`) |
| **Cross-ref** | `production-regression-baseline.md` C-BULK-002 call-out I-007 re-open pointer |

## P-003 thin-fold lock

| Decision | Value |
|---|---|
| **Thin fold into P-004?** | **yes** |
| **Standalone P-003 gaps** | **0** |
| **Accept rule** | P-003 may Accept with fold-into-P-004 evidence if no urgent item from P-002 |

## Phase routing

| Phase | Track | Items |
|---|---|---|
| **P-002** | A | C-BULK-002, EV-LIVE-001, EV-LIVE-002 |
| **P-003** | A′ | Thin fold → P-004 (no independent gaps) |
| **P-004** | C | C-JSON-005, A-TYP-015, C-DDL-005, A-SEQ-006 |
| **P-005** | B | B-FLY-001, B-DEMO-001, B-DEMO-002, (+ optional B-DEMO-003) |
| **P-006** | Accept | VERIFY PASS; final live log |

## Gap counts

| Track | Open gaps | Owner Phase |
|---|---:|---|
| **A** | 3 | P-002 |
| **B** | 3–4 | P-005 |
| **C** | 4 matrix_ids (3 themes) | P-004 |
| **P-003 fold** | 0 | fold → P-004 |

## Live-log artifact convention (Accept hardening)

| Phase | Directory | Key files |
|---|---|---|
| P-002 | `harness/evidence/test/I-007/P-002/` | `mvn-test-live-it.log`, `IT-RESULT.txt` |
| P-004 | `harness/evidence/test/I-007/P-004/` | `mvn-test-live-it.log`, `IT-RESULT.txt` |
| P-005 | `harness/evidence/test/I-007/P-005/` | `mvn-test-live-demo.log`, `IT-RESULT.txt` |
| P-006 | `harness/evidence/test/I-007/P-006/` | `mvn-test-live-it-final.log` |

Gate: `XUGU_RUN_IT=true` or `-Dxugu.run.integration=true`.

## Observed affected flow

- Flow: `i007-abc-ssot-inventory-and-c-bulk-002-strategy-lock`
- Observation: A/B/C gap map published; C-BULK-002 prefer-live-unblock locked; live-log paths named; P-003 thin-fold locked
- Artifacts: `contracts/i007-capability-hardening-plan.md`, `production-regression-baseline.md` (minimal C-BULK-002 cross-ref)

## Role pipeline

| Step | Role | Status | Evidence |
|---|---|---|---|
| RP-01 | researcher | **passed** | `harness/evidence/researcher/I-007/P-001/{INVENTORY,GAP-SUMMARY}.md` (`inv-i007-p001-rp01-researcher`) |
| RP-02 | architect-contract | **passed** | `contracts/i007-capability-hardening-plan.md`, this file (`inv-i007-p001-rp02-architect`) |
| RP-03 | reviewer | **passed** (`approve_with_nits`) | `harness/evidence/reviewer/I-007/P-001/REVIEW.md` (`inv-i007-p001-rp03-reviewer`) |

## Handoff

- `harness/handoffs/architect-contract/I-007-P-001.yaml`
- `harness/handoffs/readonly-results/I-007-P-001-reviewer.yaml`

## Issues for reviewer (RP-03)

1. Confirm C-BULK-002 strategy is **binary** — prefer-live-unblock for P-002; not both outcomes open.  
2. Confirm **no** MySQL/Oracle compat scope creep.  
3. Confirm every gap has actionable Phase owner (P-002, P-004, P-005) or P-003 fold note.  
4. Confirm I-006 41-row Boot SSOT not silently expanded.  
5. Confirm `production-regression-baseline.md` does **not** falsely mark C-BULK-002 `covered-live` (P-002 owns status change).  
6. Confirm live-log paths match I-006 naming pattern.

## Phase Accept

- Orchestrator Accept: **2026-07-19T14:32:00+08:00** after reviewer recommendation `accept_with_nits`
- harness_check: **PASS**
- BRANCH_CHECK: **PASS** (`feat/i-007-capability-hardening-abc`)
- Must-commit follows Accept (exclude `org/` dumps)
- NIT-001 deferred to P-002 SSOT tidy (non-blocking)
- NIT-002: never commit `org/`

## Acceptance decision

- Decision: `accepted`
- Decided by: orchestrator
- Date: 2026-07-19T14:32:00+08:00
- Phase verification: `harness/evidence/architect-contract/I-007/P-001/verification.json`
- Reviewer: `approve_with_nits` / `accept_with_nits`

## Next step

Orchestrator advances **P-002** (C-BULK-002 gated live bulk-insert IT per prefer-live-unblock).
