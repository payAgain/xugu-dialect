# P-003 Fold Note — A′ thin fold into P-004

> Phase: `P-003` · Initiative: `I-007` · Build: `B-001`  
> Role: implementer · invocation_id: `inv-i007-p003-rp01-implementer`  
> Branch: `feat/i-007-capability-hardening-abc`  
> Mode: **thin-fold-into-P-004**  
> Date: 2026-07-19

---

## Executive summary

| Field | Value |
|---|---|
| **Independent urgent A′ items** | **0** |
| **P-002 exception surfaced?** | **No** — C-BULK-002 closed `covered-live`; no urgent deferred outside C themes |
| **Fold target Phase** | **P-004** (Track C — JSON + ARRAY + ALTER SEQUENCE) |
| **Silent scope drop** | **None** — all residual deferred matrix rows explicitly mapped below |

---

## Inventory re-check (post P-002)

Sources consulted:

| Source | Finding |
|---|---|
| [`harness/evidence/researcher/I-007/P-001/INVENTORY.md`](../../../researcher/I-007/P-001/INVENTORY.md) §4.5, §5 | No independent urgent A′ outside C themes; P-003 thin-fold recommended |
| [`harness/evidence/researcher/I-007/P-001/GAP-SUMMARY.md`](../../../researcher/I-007/P-001/GAP-SUMMARY.md) | `P-003-fold` row: 0 standalone gaps |
| [`contracts/i007-capability-hardening-plan.md`](../../../../../contracts/i007-capability-hardening-plan.md) § P-003 thin-fold lock | Locked at P-001; no standalone P-003 deliverables |
| [`harness/handoffs/implementer/I-007-P-002.yaml`](../../../../handoffs/implementer/I-007-P-002.yaml) | P-002 closed C-BULK-002 / EV-LIVE-* only; **no** urgent exception documented |
| [`harness/evidence/implementer/I-007/P-002/ACCEPTANCE.md`](../P-002/ACCEPTANCE.md) | P-002 accepted `covered-live`; no A′ spillover |

**Conclusion:** P-003 executes as **documentation-only thin fold**. No dialect/demo Java; no new matrix rows.

---

## Residual deferred items — ownership map (no silent drop)

All items below remain **open** and are **owned by P-004**. P-003 does not close, defer, or delete them.

| matrix_id | theme | current_status (I-005 / inventory) | owner Phase | P-004 action (from SSOT) |
|---|---|---|---|---|
| **C-JSON-005** | JSON HQL subset deepen | negative-only; `@Disabled` stub | **P-004** | Subset beyond A-FUN-017 / `json_value`; not full `XuguJsonFunctions` |
| **A-TYP-015** | ARRAY type mapping | negative-only; `@Disabled` stub | **P-004** | Map SQL ARRAY; pairs C-DDL-005; ref `reference/sql/datatype/array.md` |
| **C-DDL-005** | ARRAY preferred SQL type | negative-only; `@Disabled` stub | **P-004** | `getPreferredSqlTypeCodeForArray`; pairs A-TYP-015 |
| **A-SEQ-006** | ALTER SEQUENCE | negative-only; `@Disabled` stub | **P-004** | ALTER SEQUENCE impl + live IT; ref `reference/object/sequence.md` |

### Theme rollup (P-004)

| P-004 theme | matrix_ids | delivery pattern |
|---|---|---|
| JSON subset deepen | C-JSON-005 | doc → impl → live IT → SSOT |
| ARRAY type + DDL | A-TYP-015, C-DDL-005 | doc → impl → live IT → SSOT |
| ALTER SEQUENCE | A-SEQ-006 | doc → impl → live IT → SSOT |

---

## Items explicitly **not** in P-003 scope

| Category | Rationale |
|---|---|
| C-BULK-002 / Track A | Closed in **P-002** (`covered-live`, SHA context `682c65d` accepted chain) |
| Track B (Flyway, Demo deepening) | Owner **P-005** per gap map |
| Full C “三件套” implementation | Owner **P-004** — must not be pulled forward into P-003 |
| `org/**` | Out of bounds for all I-007 phases |

---

## Brief alignment (decision #2)

I-007 Human Gate Scope decision #2: *「急项可与 C 主题合并」*. With zero independent urgent A′ rows, merge is **by fold into P-004 themes** — not by skipping P-003 documentation.

---

## Implementer deliverables (this Phase)

| Artifact | Purpose |
|---|---|
| This file (`FOLD-NOTE.md`) | Explicit fold + ownership map |
| `ACCEPTANCE.md` | Criteria table; Decision blank for orchestrator |
| `harness/handoffs/implementer/I-007-P-003.yaml` | RP-01 handoff |
| `contracts/i007-capability-hardening-plan.md` | SSOT P-003 status → folded / accepted-pending |

**Code changes:** none (by design).

---

## Next pipeline steps

| Step | Role | Expectation |
|---|---|---|
| RP-02 | test | Confirm offline green unchanged; no P-003 code surface |
| RP-03 | reviewer | Confirm fold note + no silent drop |
| Orchestrator | — | Accept P-003 on fold evidence; dispatch **P-004** |
