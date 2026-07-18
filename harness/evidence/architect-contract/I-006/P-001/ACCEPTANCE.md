# I-006 / P-001 Acceptance Evidence (architect-contract RP-02)

> Phase: `P-001`  
> Initiative: `I-006`  
> Build: `B-001`  
> Role step: `RP-02` / architect-contract  
> invocation_id: `inv-i006-p001-rp02-architect`  
> Result: **ACCEPT PASS** — reviewer `approve_with_nits` (`inv-i006-p001-rp03-reviewer`)

## Approved scope

- Task: `harness/tasks/P-001.md`
- Researcher input: `harness/evidence/researcher/I-006/P-001/INVENTORY.md`, `GAP-SUMMARY.md`
- I-005 SSOT: `contracts/production-regression-baseline.md`
- Initiative brief: `harness/initiatives/I-006/brief.md`
- Deliverable: `contracts/consumer-path-baseline.md`

## Acceptance criteria

| Criterion | Result | Evidence |
|---|---|---|
| Boot-required SSOT published (~35–50 rows) | **PASS** | Primary table: **41** rows (A=13 / B=9 / C′=19) |
| Every Boot-required row has covered/gap + Layer A\|B\|C′ | **PASS** | 8 covered + 33 gap; all tagged |
| Gaps carry `gap_action` ∈ {P-002,P-003,P-004} or justified defer | **PASS** | P-002×5, P-003×9, P-004×19; no invent-defer |
| Explicit exclusion of dialect-IT-only / pure SPI | **PASS** | Exclusion appendix in SSOT (§ Explicit exclusions) |
| Not a full 94-row Boot mirror | **PASS** | 41 Boot-required; 53 remaining 可实现 stay dialect-it-only |
| Cross-ref I-005 baseline row IDs | **PASS** | `row_id` = `i005_xref` = I-005 `matrix_id` |
| No invented rows without matrix xref | **PASS** | Sourced from researcher inventory + I-005 baseline |
| GAV / no Java changes | **PASS** | Docs only; GAV 7.4.5.Final documented |
| harness_check | see verification.json | `harness/evidence/architect-contract/I-006/P-001/verification.json` |

## Row counts

| Bucket | Count |
|---|---:|
| **Boot-required (primary SSOT)** | **41** |
| Layer A | 13 |
| Layer B | 9 |
| Layer C′ | 19 |
| **covered** | **8** |
| **gap** | **33** |
| dialect-it-only (exclusion appendix; not Boot gaps) | 53 (from 94 − 41) |
| I-005 negative-only (never Boot) | 34 |

### Covered IDs (8)

`A-SPI-001`, `A-XCUT-003`, `A-XCUT-009`, `A-IDN-003`, `A-PAG-001`, `A-PAG-002`, `A-DDL-003`, `A-DDL-004`

### Gap IDs by Phase

| gap_action | Count | IDs |
|---|---:|---|
| **P-002** | 5 | `A-SPI-002`, `A-SPI-003`, `A-IDN-004`, `A-SEQ-001`, `A-DDL-001` (+ non-matrix startup-crud) |
| **P-003** | 9 | `A-SEQ-003`, `A-SEQ-004`, `A-SCH-011`, `A-SCH-012`, `A-LCK-001`, `A-LCK-003`, `C-EXC-001`, `A-XCUT-004`, `C-EXC-002` |
| **P-004** | 19 | Types: `A-TYP-001/002/004/005/006/008/009/010/012/013`; Fun: `A-FUN-001/002/004/007/010/016/017`; `C-JSON-001`; `C-BULK-001` |

## Observed affected flow

- Flow: `consumer-path-boot-required-ssot-mapping`
- Observation: Boot-required subset published with A/B/C′ tags, gap_action Phases, and I-005 xrefs; exclusions explicit
- Artifact: `contracts/consumer-path-baseline.md`

## Role pipeline

| Step | Role | Status | Evidence |
|---|---|---|---|
| RP-01 | researcher | **passed** | `harness/evidence/researcher/I-006/P-001/{INVENTORY,GAP-SUMMARY}.md` (`inv-i006-p001-rp01-researcher`) |
| RP-02 | architect-contract | **passed** | `contracts/consumer-path-baseline.md`, this file (`inv-i006-p001-rp02-architect`) |
| RP-03 | reviewer | **passed** (`approve_with_nits`) | `harness/evidence/reviewer/I-006/P-001/REVIEW.md` (`inv-i006-p001-rp03-reviewer`) |

## Phase Accept

- Orchestrator Accept: **2026-07-18** after reviewer recommendation `accept_phase`
- harness_check: **PASS**
- BRANCH_CHECK: **PASS** (`feat/i-006-consumer-path-coverage`)
- Must-commit follows Accept (exclude `org/` dumps)

## Handoff

- `harness/handoffs/architect-contract/I-006-P-001.yaml`

## Issues for reviewer (RP-03)

1. Confirm primary table is **41** (not silently expanded toward 94).  
2. Confirm exclusion appendix rows are **not** listed as Boot `gap`.  
3. Confirm every gap has actionable `gap_action` ∈ {P-002, P-003, P-004}.  
4. Confirm `A-IDN-004` correctly tagged **gap** (persist+find exists but full CRUD update/delete missing).  
5. Confirm C-EXC-002 stretch remains in Layer B with P-003 (optional within B; not deferred).  
6. Confirm LOB choice note (A-TYP-010 vs A-TYP-011) does not invent a second Boot LOB row.

## Next step

Reviewer RP-03 readonly audit → then orchestrator may advance P-002.

## Acceptance decision

- Decision: `accepted-pending-reviewer` (architect RP-02 complete; Phase Accept waits RP-03)
- Decided by: architect-contract (this step)
- Date: 2026-07-18T22:40:00+08:00
- Phase verification: `harness/evidence/architect-contract/I-006/P-001/verification.json`
- Reviewer audit: pending
