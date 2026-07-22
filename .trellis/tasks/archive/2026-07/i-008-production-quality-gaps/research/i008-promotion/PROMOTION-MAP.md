# I-008 / P-001 — Promotion Map (architect-contract RP-02)

> **Role:** architect-contract  
> **invocation_id:** `inv-i008-p001-rp02-architect`  
> **Date:** 2026-07-20  
> **Branch:** `feat/i-008-production-quality-gaps`  
> **Source:** `(historical harness evidence removed; see Trellis task research/ if present) researcher/I-008/P-001/{INVENTORY,GAP-SUMMARY}.md`  
> **Constraint:** GAV `7.4.5.Final`; `compatiblemode=NONE`; Q5 OUT; NOT Ship; no dialect/demo Java

---

## Executive totals

| Batch | Phase | Row count | Action |
|---|---|---:|---|
| **Closed** | — | **5** | I-007 already `covered-live`; no promotion work |
| **A** | **P-003** | **19** | Unit-only closure: thin live IT **or** `known-limit-documented` (locked below) |
| **A′** | **P-003** | **58** | Definition A IT/demo rows: SSOT `covered` → `covered-live` tag sweep (live IT exists) |
| **B** | **P-004** | **16** | Ruler C SSOT tag uplift (15) + `A-XCUT-009` demo-live clarification |
| **Physical total** | | **98** | Charter **94** (I-005 freeze) + **4** I-007 Track C promotions |

**Orphan rows:** **0** — every physical 可实现 row maps to closed, P-003, or P-004.

---

## Count reconciliation (94 vs 98)

| Label | Count | Notes |
|---|---:|---|
| I-005 charter achievable | **94** | Def A 78 + Ruler C 16 at I-005 freeze |
| I-007 Track C promotions | **+4** | `C-JSON-005`, `C-DDL-005`, `A-TYP-015`, `A-SEQ-006` — promoted from negative-only/延后 |
| **Physical SSOT rows today** | **98** | Authoritative for row-level map below |

---

## Closed — no P-003/P-004 work (5)

| matrix_id | SSOT status | Evidence anchor | Notes |
|---|---|---|---|
| C-BULK-002 | covered-live | `XuguBulkMutationIT#bulkInsertOnJoinedInheritanceWithIdentitySucceeds_C_BULK_002` | I-007/P-002 |
| C-JSON-005 | covered-live | `XuguJsonSubsetDeepenIT#jsonSubsetDeepen_Hql_C_JSON_005` | I-007/P-004 |
| C-DDL-005 | covered-live | `XuguArrayTypeIT#arrayColumnRoundTrip_A_TYP_015_C_DDL_005` | I-007/P-004; pairs A-TYP-015 |
| A-TYP-015 | covered-live | same as C-DDL-005 | I-007/P-004 |
| A-SEQ-006 | covered-live | `XuguAlterSequenceIT#alterSequenceStartWithAndIncrement_A_SEQ_006` | I-007/P-004 |

---

## Batch A → P-003 (19) — unit-only closure

| matrix_id | honest_status | **Locked outcome** | Reason / bundle |
|---|---|---|---|
| A-TYP-003 | unit-only | **thin live IT** | Extend `XuguTypeRoundTripIT` REAL→float round-trip |
| A-TYP-019 | unit-only | **thin live IT** | Gated cast/schema-export live IT for `castPattern` |
| A-DDL-005 | unit-only | **thin live IT** | Gated DDL export IT for DEFAULT column |
| A-XCUT-001 | unit-only | **thin live IT** | Thin live identifier folding IT (UPPER) |
| A-IDN-001 | unit-only | **known-limit-documented** | Wiring unit only; live identity persist covered by **A-IDN-003/004** IT bundle |
| A-IDN-002 | unit-only | **known-limit-documented** | No AUTO_INCREMENT wiring unit; live covered by **A-IDN-003/004** |
| A-SEQ-002 | unit-only | **known-limit-documented** | DROP SEQUENCE string unit; live DDL covered by **A-SEQ-001** IT |
| A-SEQ-005 | unit-only | **known-limit-documented** | Sequence options unit; live covered by **A-SEQ-001/003** IT |
| A-FUN-003 | unit-only | **known-limit-documented** | Registry unit slice; live covered by **A-FUN-001/004/010** IT bundle |
| A-FUN-005 | unit-only | **known-limit-documented** | Same bundle |
| A-FUN-006 | unit-only | **known-limit-documented** | Same bundle |
| A-FUN-007 | unit-only | **known-limit-documented** | Same bundle |
| A-FUN-009 | unit-only | **known-limit-documented** | Same bundle |
| A-SCH-006 | unit-only | **known-limit-documented** | Temp strategy flag unit; live covered by **A-SCH-004/005** IT |
| A-SPI-004 | unit-only | **known-limit-documented** | Resolver **non-match** negatives for MySQL/Oracle/PostgreSQL — **no live path by design** |
| A-XCUT-002 | unit-only | **known-limit-documented** | Quote-char constant; no distinct live assertion beyond dialect wiring |
| A-XCUT-005 | unit-only | **known-limit-documented** | Isolation-level hook unit; not independently live-testable without side effects |
| A-XCUT-007 | unit-only | **known-limit-documented** | TCL keyword unit duplicate; live covered by **A-XCUT-004** IT |
| A-XCUT-008 | unit-only | **known-limit-documented** | SequenceSupport flag unit; live covered by **A-SEQ-003** IT |

**Batch A subtotals:** thin live IT **4** · known-limit-documented **15**

---

## Batch A′ → P-003 (58) — SSOT tag sweep only

Live IT already exists; P-003 updates SSOT `status` from `covered` → `covered-live` (no new test required unless reviewer nit).

| Domain | matrix_ids | Count |
|---|---|---:|
| Types | A-TYP-001, A-TYP-002, A-TYP-004, A-TYP-005, A-TYP-006, A-TYP-007, A-TYP-008, A-TYP-009, A-TYP-010, A-TYP-011, A-TYP-012, A-TYP-013 | 12 |
| DDL | A-DDL-001, A-DDL-002, A-DDL-003, A-DDL-004, A-DDL-006 | 5 |
| Pagination | A-PAG-001, A-PAG-002, A-PAG-003 | 3 |
| Locks | A-LCK-001, A-LCK-002, A-LCK-003 | 3 |
| Identity | A-IDN-003, A-IDN-004 | 2 |
| Sequence | A-SEQ-001, A-SEQ-003, A-SEQ-004 | 3 |
| Functions | A-FUN-001, A-FUN-002, A-FUN-004, A-FUN-008, A-FUN-010, A-FUN-011, A-FUN-012, A-FUN-013, A-FUN-014, A-FUN-016, A-FUN-017, A-FUN-018 | 12 |
| Schema | A-SCH-001, A-SCH-002, A-SCH-004, A-SCH-005, A-SCH-008, A-SCH-009, A-SCH-010, A-SCH-011, A-SCH-012, A-SCH-013, A-SCH-014, A-SCH-015, A-SCH-016 | 13 |
| SPI | A-SPI-001, A-SPI-002, A-SPI-003 | 3 |
| Cross-cutting | A-XCUT-003, A-XCUT-004 | 2 |

**Q2 cross-link:** A-LCK-001…003 tag sweep does **not** satisfy Q2 user-doc / behavioral evidence — see [`GAP-SUMMARY.md`](GAP-SUMMARY.md) → **P-002/P-005**.

---

## Batch B → P-004 (16)

| Sub-batch | matrix_ids | Count | Action |
|---|---|---:|---|
| Ruler C SSOT tag | C-EXC-001, C-EXC-002, C-JSON-001, C-JSON-002, C-JSON-003, C-JSON-004, C-WIN-001, C-CTE-001, C-BULK-001, C-BULK-003, C-DDL-001, C-DDL-002, C-DDL-003, C-CAT-001, C-GUID-001 | **15** | SSOT `covered` → `covered-live`; IT exists (`JSON_FUNCTIONS_ENABLED` where noted) |
| Demo-live clarification | A-XCUT-009 | **1** | Document demo-live satisfies consumer golden path; gated Boot IT is live evidence (Q3/Q4 cross-link **P-006/P-007**) |

**Q3 cross-link:** Ruler C JSON rows + `A-TYP-012/013` Boot UUID/JSON out-of-box — **P-006** implements; **P-002** documents checklist.

---

## Phase closure math (post I-008)

| Target state | Count after P-003 + P-004 |
|---|---:|
| covered-live (thin IT adds) | 79 + 4 = **83** |
| known-limit-documented | **15** |
| **Achievable rows accounted** | **98** (83 + 15) |

---

## Boundary locks

| Lock | Value |
|---|---|
| I-006 Boot SSOT | **41/41 FROZEN** — no silent row expansion |
| GAV | `7.4.5.Final` — no bump |
| compatiblemode | **NONE** only |
| Q5 perf/multi-version | **OUT** |
| MySQL/Oracle/sibling | **OUT** |
| Ship | **NOT** in I-008 scope |
