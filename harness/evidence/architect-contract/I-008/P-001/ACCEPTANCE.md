# I-008 / P-001 Acceptance Evidence (architect-contract RP-02)

> Phase: `P-001`  
> Initiative: `I-008`  
> Build: `B-001`  
> Role step: `RP-02` / architect-contract  
> invocation_id: `inv-i008-p001-rp02-architect`  
> Result: **ACCEPT PASS** (reviewer RP-03 + orchestrator accept)

## Approved scope

- Task: `harness/tasks/P-001.md`
- Researcher input: `harness/evidence/researcher/I-008/P-001/{INVENTORY,GAP-SUMMARY}.md` (`inv-i008-p001-rp01-researcher`)
- I-005 SSOT: `contracts/production-regression-baseline.md` (summary counts updated)
- I-006 SSOT: `contracts/consumer-path-baseline.md` (FROZEN — not expanded)
- Initiative brief: `harness/initiatives/I-008/brief.md`
- Deliverables: [`PROMOTION-MAP.md`](PROMOTION-MAP.md), [`GAP-SUMMARY.md`](GAP-SUMMARY.md)

## Acceptance criteria

| Criterion | Result | Evidence |
|---|---|---|
| Honest counts published (covered-live / unit-only / known-limit) | **PASS** | § Honest counts; `production-regression-baseline.md` § Summary |
| Every 可实现 row maps to P-003, P-004, closed, or known-limit | **PASS** | § Master promotion map (98 rows; 0 orphans) |
| Q1 dishonest「94 covered-live」sources listed with fix owner | **PASS** | § Q1; [`GAP-SUMMARY.md`](GAP-SUMMARY.md) |
| known-limit-documented candidates locked with reasons | **PASS** | § Known-limit locks (15 rows) |
| Q2/Q3/Q4 cross-linked to P-002/P-005/P-006/P-007 | **PASS** | [`GAP-SUMMARY.md`](GAP-SUMMARY.md) |
| harness_check | **PASS** | [`verification.json`](verification.json) |
| No dialect/demo Java changes | **PASS** | contracts + evidence only |
| GAV / NONE / no Ship / Q5 OUT | **PASS** | documented in promotion map |

## Honest counts (I-008 Q1 baseline)

| Bucket | Count | Notes |
|---|---:|---|
| **Physical 可实现 rows (SSOT)** | **98** | Charter **94** + **4** I-007 Track C promotions |
| **Honest covered-live today** (live path exists) | **79** | 78 IT + 1 demo (`A-XCUT-009`) |
| SSOT `status=covered-live` column today | **5** | Tag lag on 73 live-capable rows |
| **unit-only-without-live** | **19** | Batch A → **P-003** |
| **known-limit-documented (locked target)** | **15** | Batch A subset — see § Known-limit locks |
| **thin live IT required** | **4** | Batch A subset — see § Thin live IT locks |
| **SSOT tag sweep only** | **73** | Batch A′ (58) + Batch B (15) |
| **Already closed (I-007)** | **5** | No P-003/P-004 work |
| **gap** | **0** | Every row has ≥1 verified entrypoint |

**Verdict:** Claims of「**94 covered-live**」are **dishonest**. Honest live-capable count = **79/98** today.

---

## Q1 — Dishonest claim sources

| # | File | Fix owner |
|---|---|---|
| Q1-01 | `docs/verification.md` L72 | **P-002** |
| Q1-02 | `docs/user-guide/04-feature-matrix.md` L64 | **P-002** |
| Q1-03 | `harness/evidence/implementer/I-007/P-006/NOTES.md` L11 | **P-002** (retract) |
| Q1-04 | `harness/session/session-log.md` L24 | **P-002** (goal vs achieved) |
| Q1-05 | `contracts/production-regression-baseline.md` § Summary | **RP-02** — updated |

---

## Promotion batch summary

| Batch | Phase | Count | Action |
|---|---|---:|---|
| Closed | — | 5 | I-007 `covered-live` — no work |
| **A** | **P-003** | 19 | Unit-only → thin live IT (4) or known-limit (15) |
| **A′** | **P-003** | 58 | Def A IT rows: SSOT tag `covered` → `covered-live` |
| **B** | **P-004** | 16 | Ruler C tag (15) + `A-XCUT-009` demo-live note |
| **Total** | | **98** | **0 orphan rows** |

Detail: [`PROMOTION-MAP.md`](PROMOTION-MAP.md)

---

## Known-limit locks (15 — Batch A, P-003)

| matrix_id | Locked reason |
|---|---|
| A-IDN-001 | Wiring unit; live identity persist covered by **A-IDN-003/004** IT bundle |
| A-IDN-002 | No AUTO_INCREMENT wiring unit; live covered by **A-IDN-003/004** |
| A-SEQ-002 | DROP SEQUENCE string unit; live DDL covered by **A-SEQ-001** IT |
| A-SEQ-005 | Sequence options unit; live covered by **A-SEQ-001/003** IT |
| A-FUN-003 | Registry unit slice; live covered by **A-FUN-001/004/010** IT bundle |
| A-FUN-005 | Same bundle |
| A-FUN-006 | Same bundle |
| A-FUN-007 | Same bundle |
| A-FUN-009 | Same bundle |
| A-SCH-006 | Temp strategy flag unit; live covered by **A-SCH-004/005** IT |
| A-SPI-004 | Resolver non-match negatives — **no live path by design** (non-Xugu products) |
| A-XCUT-002 | Quote-char constant; no distinct live assertion |
| A-XCUT-005 | Isolation-level hook unit; not independently live-testable |
| A-XCUT-007 | TCL keyword unit duplicate; live covered by **A-XCUT-004** IT |
| A-XCUT-008 | SequenceSupport flag unit; live covered by **A-SEQ-003** IT |

P-003 must publish formal `known-limit-documented` SSOT + user-doc waiver for each locked row (no silent fake covered-live).

---

## Thin live IT locks (4 — Batch A, P-003)

| matrix_id | Locked action |
|---|---|
| A-TYP-003 | Extend `XuguTypeRoundTripIT` REAL→float round-trip |
| A-TYP-019 | Gated cast/schema-export live IT |
| A-DDL-005 | Gated DDL export IT for DEFAULT column |
| A-XCUT-001 | Thin live identifier folding IT (UPPER) |

---

## Master promotion map (98 rows)

| matrix_id | Batch | Phase | Locked outcome |
|---|---|---|---|
| C-BULK-002 | closed | — | I-007/P-002 covered-live |
| C-JSON-005 | closed | — | I-007/P-004 covered-live |
| C-DDL-005 | closed | — | I-007/P-004 covered-live |
| A-TYP-015 | closed | — | I-007/P-004 covered-live |
| A-SEQ-006 | closed | — | I-007/P-004 covered-live |
| A-TYP-003 | A | P-003 | thin live IT |
| A-TYP-019 | A | P-003 | thin live IT |
| A-DDL-005 | A | P-003 | thin live IT |
| A-XCUT-001 | A | P-003 | thin live IT |
| A-IDN-001 | A | P-003 | known-limit-documented |
| A-IDN-002 | A | P-003 | known-limit-documented |
| A-SEQ-002 | A | P-003 | known-limit-documented |
| A-SEQ-005 | A | P-003 | known-limit-documented |
| A-FUN-003 | A | P-003 | known-limit-documented |
| A-FUN-005 | A | P-003 | known-limit-documented |
| A-FUN-006 | A | P-003 | known-limit-documented |
| A-FUN-007 | A | P-003 | known-limit-documented |
| A-FUN-009 | A | P-003 | known-limit-documented |
| A-SCH-006 | A | P-003 | known-limit-documented |
| A-SPI-004 | A | P-003 | known-limit-documented |
| A-XCUT-002 | A | P-003 | known-limit-documented |
| A-XCUT-005 | A | P-003 | known-limit-documented |
| A-XCUT-007 | A | P-003 | known-limit-documented |
| A-XCUT-008 | A | P-003 | known-limit-documented |
| A-TYP-001 | A′ | P-003 | SSOT tag sweep |
| A-TYP-002 | A′ | P-003 | SSOT tag sweep |
| A-TYP-004 | A′ | P-003 | SSOT tag sweep |
| A-TYP-005 | A′ | P-003 | SSOT tag sweep |
| A-TYP-006 | A′ | P-003 | SSOT tag sweep |
| A-TYP-007 | A′ | P-003 | SSOT tag sweep |
| A-TYP-008 | A′ | P-003 | SSOT tag sweep |
| A-TYP-009 | A′ | P-003 | SSOT tag sweep |
| A-TYP-010 | A′ | P-003 | SSOT tag sweep |
| A-TYP-011 | A′ | P-003 | SSOT tag sweep |
| A-TYP-012 | A′ | P-003 | SSOT tag sweep |
| A-TYP-013 | A′ | P-003 | SSOT tag sweep |
| A-DDL-001 | A′ | P-003 | SSOT tag sweep |
| A-DDL-002 | A′ | P-003 | SSOT tag sweep |
| A-DDL-003 | A′ | P-003 | SSOT tag sweep |
| A-DDL-004 | A′ | P-003 | SSOT tag sweep |
| A-DDL-006 | A′ | P-003 | SSOT tag sweep |
| A-PAG-001 | A′ | P-003 | SSOT tag sweep |
| A-PAG-002 | A′ | P-003 | SSOT tag sweep |
| A-PAG-003 | A′ | P-003 | SSOT tag sweep |
| A-LCK-001 | A′ | P-003 | SSOT tag sweep |
| A-LCK-002 | A′ | P-003 | SSOT tag sweep |
| A-LCK-003 | A′ | P-003 | SSOT tag sweep |
| A-IDN-003 | A′ | P-003 | SSOT tag sweep |
| A-IDN-004 | A′ | P-003 | SSOT tag sweep |
| A-SEQ-001 | A′ | P-003 | SSOT tag sweep |
| A-SEQ-003 | A′ | P-003 | SSOT tag sweep |
| A-SEQ-004 | A′ | P-003 | SSOT tag sweep |
| A-FUN-001 | A′ | P-003 | SSOT tag sweep |
| A-FUN-002 | A′ | P-003 | SSOT tag sweep |
| A-FUN-004 | A′ | P-003 | SSOT tag sweep |
| A-FUN-008 | A′ | P-003 | SSOT tag sweep |
| A-FUN-010 | A′ | P-003 | SSOT tag sweep |
| A-FUN-011 | A′ | P-003 | SSOT tag sweep |
| A-FUN-012 | A′ | P-003 | SSOT tag sweep |
| A-FUN-013 | A′ | P-003 | SSOT tag sweep |
| A-FUN-014 | A′ | P-003 | SSOT tag sweep |
| A-FUN-016 | A′ | P-003 | SSOT tag sweep |
| A-FUN-017 | A′ | P-003 | SSOT tag sweep |
| A-FUN-018 | A′ | P-003 | SSOT tag sweep |
| A-SCH-001 | A′ | P-003 | SSOT tag sweep |
| A-SCH-002 | A′ | P-003 | SSOT tag sweep |
| A-SCH-004 | A′ | P-003 | SSOT tag sweep |
| A-SCH-005 | A′ | P-003 | SSOT tag sweep |
| A-SCH-008 | A′ | P-003 | SSOT tag sweep |
| A-SCH-009 | A′ | P-003 | SSOT tag sweep |
| A-SCH-010 | A′ | P-003 | SSOT tag sweep |
| A-SCH-011 | A′ | P-003 | SSOT tag sweep |
| A-SCH-012 | A′ | P-003 | SSOT tag sweep |
| A-SCH-013 | A′ | P-003 | SSOT tag sweep |
| A-SCH-014 | A′ | P-003 | SSOT tag sweep |
| A-SCH-015 | A′ | P-003 | SSOT tag sweep |
| A-SCH-016 | A′ | P-003 | SSOT tag sweep |
| A-SPI-001 | A′ | P-003 | SSOT tag sweep |
| A-SPI-002 | A′ | P-003 | SSOT tag sweep |
| A-SPI-003 | A′ | P-003 | SSOT tag sweep |
| A-XCUT-003 | A′ | P-003 | SSOT tag sweep |
| A-XCUT-004 | A′ | P-003 | SSOT tag sweep |
| C-EXC-001 | B | P-004 | SSOT tag sweep |
| C-EXC-002 | B | P-004 | SSOT tag sweep |
| C-JSON-001 | B | P-004 | SSOT tag sweep |
| C-JSON-002 | B | P-004 | SSOT tag sweep |
| C-JSON-003 | B | P-004 | SSOT tag sweep |
| C-JSON-004 | B | P-004 | SSOT tag sweep |
| C-WIN-001 | B | P-004 | SSOT tag sweep |
| C-CTE-001 | B | P-004 | SSOT tag sweep |
| C-BULK-001 | B | P-004 | SSOT tag sweep |
| C-BULK-003 | B | P-004 | SSOT tag sweep |
| C-DDL-001 | B | P-004 | SSOT tag sweep |
| C-DDL-002 | B | P-004 | SSOT tag sweep |
| C-DDL-003 | B | P-004 | SSOT tag sweep |
| C-CAT-001 | B | P-004 | SSOT tag sweep |
| C-GUID-001 | B | P-004 | SSOT tag sweep |
| A-XCUT-009 | B | P-004 | demo-live clarification |

---

## Q2 / Q3 / Q4 cross-links (later Phases)

| Track | Owner Phases | Evidence |
|---|---|---|
| **Q2** Lock docs + live behavioral proof | **P-002**, **P-005** | [`GAP-SUMMARY.md`](GAP-SUMMARY.md) § Q2 |
| **Q3** UUID/JSON Boot out-of-box | **P-002**, **P-006** | [`GAP-SUMMARY.md`](GAP-SUMMARY.md) § Q3 |
| **Q4** Accept full-reactor live log | **P-002**, **P-007** | [`GAP-SUMMARY.md`](GAP-SUMMARY.md) § Q4 |

---

## Observed affected flow

- Flow: `i008-q1-ssot-honest-counts-and-promotion-map`
- Observation: Honest counts published; 98-row promotion map with 0 orphans; 15 known-limit locks; Q2/Q3/Q4 wired to later Phases
- Artifacts: this file, [`PROMOTION-MAP.md`](PROMOTION-MAP.md), [`GAP-SUMMARY.md`](GAP-SUMMARY.md), `production-regression-baseline.md` § Summary

## Role pipeline

| Step | Role | Status | Evidence |
|---|---|---|---|
| RP-01 | researcher | **passed** | `harness/evidence/researcher/I-008/P-001/{INVENTORY,GAP-SUMMARY}.md` (`inv-i008-p001-rp01-researcher`) |
| RP-02 | architect-contract | **passed** | this file (`inv-i008-p001-rp02-architect`) |
| RP-03 | reviewer | **passed** | `harness/evidence/reviewer/I-008/P-001/REVIEW.md` (`inv-i008-p001-rp03-reviewer`) |

## Handoff

- `harness/handoffs/architect-contract/I-008-P-001.yaml`

## Issues for reviewer (RP-03)

1. Confirm **0 orphan rows** in master map (98 physical rows).  
2. Confirm **15** known-limit locks have explicit reasons (no silent fake covered-live).  
3. Confirm honest counts **do not** claim「94 covered-live」.  
4. Confirm **no** MySQL/Oracle/sibling scope creep.  
5. Confirm I-006 **41-row** Boot SSOT not silently expanded.  
6. Confirm Q2/Q3/Q4 cross-links name correct owner Phases.

## Acceptance decision

- Decision: `accepted`
- Reviewer: RP-03 ACCEPT PASS (`inv-i008-p001-rp03-reviewer`)
- Orchestrator must-commit SHA recorded in session checkpoint
