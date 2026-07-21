# I-009 Deferred Matrix Batch Map (SSOT)

> **Status:** Published (I-009 / P-001 / RP-02 architect-contract)  
> **Initiative:** I-009 — deferred matrix full delivery  
> **Build:** B-001  
> **GAV:** `7.4.5.Final` · **compatiblemode:** `NONE` · **NOT Ship**  
> **Author role:** architect-contract  
> **Research input:** `harness/evidence/researcher/I-009/P-001/{INVENTORY,RESEARCH-NOTES}.md`  
> **Companion matrices:** [`feature-matrix-definition-a.md`](feature-matrix-definition-a.md), [`feature-matrix-i003-ruler-c.md`](feature-matrix-i003-ruler-c.md)  
> **Baseline:** [`production-regression-baseline.md`](production-regression-baseline.md) § I-009  
> **Evidence:** `harness/evidence/architect-contract/I-009/P-001/{ACCEPTANCE,BATCH-MAP}.md`

## Scope

Serial delivery Phases **P-002…P-010** for **20** inventory rows (19 doc-allowed + **1** doc-forbidden).  
**Out of scope:** A-XCUT-012 (Ship OUT); doc-forbidden rows outside inventory (A-PAG-005, A-LCK-004/005, A-SCH-007, A-XCUT-006/010/011, C-SKIP-001, C-DDL-004) — remain skip/negative only.

## Batch summary

| Phase | Row IDs | Theme | Row count |
|---|---|---|---:|
| **P-002** | A-TYP-014 | INTERVAL type | 1 |
| **P-003** | A-TYP-016, A-FUN-021 | XML type + XML functions | 2 |
| **P-004** | A-TYP-017, A-FUN-020 | Geometric/spatial types + functions | 2 |
| **P-005** | A-TYP-018 | UDT | 1 |
| **P-006** | A-FUN-015, A-FUN-019 | bit_and/bit_or + regexp_* | 2 |
| **P-007** | A-DDL-007, A-DDL-008, A-DDL-009 | IF NOT EXISTS / partition / ENCRYPT | 3 |
| **P-008** | A-SCH-003, A-SCH-017 | Catalog qualifier + advanced indexes | 2 |
| **P-009** | A-LCK-006, A-PAG-004, A-PAG-006, A-IDN-005 | LOCK TABLE + TOP/ROWNUM alt + IDENTITY_MODE | 4 |
| **P-010** | C-JSON-006, C-SRV-001, C-SEL-001 | json_table (forbidden) + ServerConfig + DialectSelector | 3 |
| **Total** | | | **20** |

## Doc verdict legend

| Verdict | I-009 rule |
|---|---|
| **doc-allowed** | May implement in assigned Phase using cited Xugu docs only |
| **doc-forbidden** | **MUST NOT** invent SQL; skip/negative-only; matrix status **文档不允许** |

## Doc-forbidden lock (C-JSON-006)

| Field | Value |
|---|---|
| **matrix_id** | C-JSON-006 |
| **Audit** | Repo-wide grep under `E:\Work\docs\content` — **zero** `json_table` / `JSON_TABLE` entries in `reference/function/json-functions/**` (2026-07-21) |
| **Contrast** | XML analogue documented: `reference/function/xml-functions/xmltable.md` (A-FUN-021 — separate row) |
| **Required outcome** | `supportsJsonTableFunction=false`; retain `XuguNegativeRegressionBaselineTest#deferred_C_JSON_006_jsonTable`; **no** invented JSON_TABLE SQL |
| **Matrix SSOT** | Reclassified **文档不允许** in [`feature-matrix-i003-ruler-c.md`](feature-matrix-i003-ruler-c.md) |
| **Acceptance** | P-010 negative anchor reaffirmation only — not covered-live promotion |

## Partial wiring (SSOT alignment)

| matrix_id | Code today | P-007 action |
|---|---|---|
| A-DDL-007 | **C-DDL-001** delivered: `supportsIfExistsBeforeTableName()=true`; `create table if not exists`; `XuguTypeDdlDetailsTest/IT` | **SSOT promotion** — align Definition A A-DDL-007 with Ruler C C-DDL-001; do **not** duplicate DDL implementation |

## Known-limit candidates (locked acceptance wording)

Honest **known-limit-documented** is a valid P-002…P-010 outcome when ORM surface cannot be covered-live without inventing SQL.

| Priority | row_id | Doc citation | Known-limit reason |
|---|---|---|---|
| **High** | C-JSON-006 | — (no doc) | Doc-forbidden — negative only |
| **High** | A-TYP-018 | `reference/sql/datatype/udt.md` | UDT-as-entity-column rarely mappable in Hibernate ORM |
| **High** | A-DDL-009 | `reference/object/encryptor.md` | SYSSSO / `ACL_SSO` required for `CREATE ENCRYPTOR` |
| **Medium** | A-TYP-014 | `reference/sql/datatype/datetime.md` §INTERVAL | 13 subtypes + `DEF_INTERVAL_STYLE` vs single Hibernate interval |
| **Medium** | A-SCH-003 | `reference/object/database.md`; session `database.md` | JDBC catalog vs Xugu database; DATABASE not session-SET |
| **Medium** | A-DDL-008 | `reference/object/table/partition.md` | PARTITION BY unlikely in Hibernate schema export |
| **Medium** | A-FUN-015 | `reference/sql/datatype/bit.md` (VARBIT) | bit aggregates need VARBIT columns |
| **Low** | A-PAG-004 | `reference/sql/select/resultset-restricted.md` (#top) | TOP ⊥ LIMIT; default LimitHandler unchanged |
| **Low** | A-PAG-006 | `reference/sql/select/select.md` §ROWNUM | Oracle-style wrapper; LIMIT remains default |
| **Low** | C-SEL-001 | Product SPI (no Xugu SQL) | `XuguDialectResolver` may suffice — docs closure |
| **Low** | A-SCH-017 | `reference/object/indexes.md` | BITMAP/functional indexes beyond standard exporter |

**Low-risk (expect covered-live):** A-FUN-019, A-LCK-006, A-IDN-005, C-SRV-001, A-FUN-020/021 (with type pairs), A-TYP-016/017 (bounded scope).

## Doc-forbidden reaffirmation (outside 20-row inventory)

These rows **MUST NOT** receive positive SQL invention in I-009:

| row_id | Reason | Doc path |
|---|---|---|
| A-PAG-005 | ANSI FETCH not documented | — |
| A-LCK-004 / C-SKIP-001 | SKIP LOCKED absent from FOR UPDATE grammar | — |
| A-LCK-005 | FOR SHARE absent | — |
| A-SCH-007 | Temp table FK forbidden | `reference/object/table/create.md` |
| A-XCUT-006 | READ UNCOMMITTED not in ISO_LEVEL | `session-parameter/iso_level.md` |
| A-XCUT-010 / A-XCUT-011 | Charter non-goals | ADR / Charter |
| **C-JSON-006** | No json_table grammar | — (audit finding) |

## Constraints (all Phases)

- GAV `7.4.5.Final`; `compatiblemode=NONE`
- Serial P-002…P-010 — orchestrator decides; no Human Gate parallel question
- **No** dialect Java in P-001 (architect-contract)
- **No** MySQL/Oracle inheritance; **no** sibling port; **NOT Ship**

## Orchestrator note

Detail per-row acceptance hints: [`harness/evidence/architect-contract/I-009/P-001/BATCH-MAP.md`](../harness/evidence/architect-contract/I-009/P-001/BATCH-MAP.md).
