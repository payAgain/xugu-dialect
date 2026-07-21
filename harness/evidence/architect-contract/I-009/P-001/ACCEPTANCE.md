# I-009 / P-001 Acceptance Evidence (architect-contract RP-02)

> Phase: `P-001`  
> Initiative: `I-009`  
> Build: `B-001`  
> Role step: `RP-02` / architect-contract  
> invocation_id: `inv-i009-p001-rp02-architect`  
> Result: **ACCEPT READY** (pending reviewer RP-03)

## Approved scope

- Task: `harness/tasks/P-001.md`
- Researcher input: `harness/evidence/researcher/I-009/P-001/{INVENTORY,RESEARCH-NOTES}.md`
- I-008 baseline: I-008 archived; all 20 inventory rows remain **negative-only** in production baseline
- Initiative brief: `harness/initiatives/I-009/brief.md`
- Deliverables: [`BATCH-MAP.md`](BATCH-MAP.md), [`contracts/i009-deferred-batch-map.md`](../../../../contracts/i009-deferred-batch-map.md)

## Acceptance criteria

| Criterion | Result | Evidence |
|---|---|---|
| Every deferred row maps to P-002…P-010 or known-limit with doc citation | **PASS** | § Batch map; [`BATCH-MAP.md`](BATCH-MAP.md) master table (20 rows; 0 orphans) |
| Doc-forbidden rows remain skip/negative only; no invented SQL | **PASS** | § Doc-forbidden locks; C-JSON-006 reclassified **文档不允许** |
| C-JSON-006: no json_table doc — doc-forbidden/known-limit | **PASS** | § C-JSON-006 lock; grep audit 2026-07-21 |
| A-DDL-007: SSOT alignment via C-DDL-001 noted for P-007 | **PASS** | § Partial wiring; P-007 promotion-only |
| Batch map published serial P-002…P-010 | **PASS** | § Batch summary |
| harness_check | *see* [`verification.json`](verification.json) | recorded at RP-02 completion |
| No dialect/demo Java changes | **PASS** | contracts + evidence only |
| GAV 7.4.5.Final / NONE / NOT Ship | **PASS** | documented in batch map contract |

---

## Batch map summary (P-002…P-010)

| Phase | Row IDs | Theme | Count |
|---|---|---|---:|
| **P-002** | A-TYP-014 | INTERVAL type | 1 |
| **P-003** | A-TYP-016, A-FUN-021 | XML type + XML functions | 2 |
| **P-004** | A-TYP-017, A-FUN-020 | Geometric/spatial + functions | 2 |
| **P-005** | A-TYP-018 | UDT | 1 |
| **P-006** | A-FUN-015, A-FUN-019 | bit_and/bit_or + regexp_* | 2 |
| **P-007** | A-DDL-007, A-DDL-008, A-DDL-009 | IF NOT EXISTS / partition / ENCRYPT | 3 |
| **P-008** | A-SCH-003, A-SCH-017 | Catalog + advanced indexes | 2 |
| **P-009** | A-LCK-006, A-PAG-004, A-PAG-006, A-IDN-005 | LOCK TABLE + TOP/ROWNUM + IDENTITY_MODE | 4 |
| **P-010** | C-JSON-006, C-SRV-001, C-SEL-001 | json_table (forbidden) + ServerConfig + Selector | 3 |
| **Total** | | | **20** |

Detail: [`BATCH-MAP.md`](BATCH-MAP.md) · SSOT: [`contracts/i009-deferred-batch-map.md`](../../../../contracts/i009-deferred-batch-map.md)

---

## Inventory counts

| Bucket | Count |
|---:|---:|
| Inventory rows (in-scope deferred) | **20** |
| doc-allowed | **19** |
| doc-forbidden (audit) | **1** (C-JSON-006) |
| Partial wiring today | **1** (A-DDL-007 via C-DDL-001) |
| Known-limit candidates (locked) | **10** |
| Orphan rows | **0** |
| Ship OUT | A-XCUT-012 (not in table) |

---

## Doc-forbidden locks

### C-JSON-006 (inventory — reclassified)

| Field | Lock |
|---|---|
| **Audit** | Zero `json_table` / `JSON_TABLE` under `E:\Work\docs\content/reference/function/json-functions/**` (grep 2026-07-21) |
| **Matrix SSOT** | **文档不允许** (was 延后) in `feature-matrix-i003-ruler-c.md` |
| **Required behavior** | `supportsJsonTableFunction=false`; retain negative anchor; **MUST NOT** invent JSON_TABLE SQL |
| **Phase** | P-010 — negative reaffirmation only |
| **Contrast** | XMLTABLE documented at `reference/function/xml-functions/xmltable.md` → A-FUN-021 (separate) |

### Outside inventory (unchanged — skip/negative only)

| row_id | Reason |
|---|---|
| A-PAG-005 | ANSI FETCH not documented |
| A-LCK-004 / C-SKIP-001 | SKIP LOCKED absent |
| A-LCK-005 | FOR SHARE absent |
| A-SCH-007 | Temp table FK forbidden (`create.md`) |
| A-XCUT-006 | READ UNCOMMITTED not in ISO_LEVEL |
| A-XCUT-010 / A-XCUT-011 | Charter non-goals |

---

## Known-limit locks (10 candidates)

| Priority | row_id | Doc citation | Locked reason |
|---|---|---|---|
| **High** | C-JSON-006 | — | Doc-forbidden — negative only |
| **High** | A-TYP-018 | `udt.md` | UDT ORM entity columns rarely mappable |
| **High** | A-DDL-009 | `encryptor.md` | SYSSSO encryptor prerequisite |
| **Medium** | A-TYP-014 | `datetime.md` §INTERVAL | 13 subtypes + DEF_INTERVAL_STYLE |
| **Medium** | A-SCH-003 | `database.md` | JDBC catalog vs Xugu database |
| **Medium** | A-DDL-008 | `partition.md` | PARTITION BY unlikely in schema export |
| **Medium** | A-FUN-015 | `bit.md` (VARBIT) | Aggregates need VARBIT columns |
| **Low** | A-PAG-004 | `resultset-restricted.md` | TOP ⊥ LIMIT; LimitHandler unchanged |
| **Low** | A-PAG-006 | `select.md` §ROWNUM | LIMIT preferred |
| **Low** | C-SEL-001 | Product SPI | Resolver may suffice |
| **Low** | A-SCH-017 | `indexes.md` | Advanced indexes beyond exporter |

P-002…P-010 implementers may deliver **known-limit-documented** with formal SSOT + doc citation — no silent fake covered-live.

---

## Partial wiring — A-DDL-007 / C-DDL-001

| Field | Value |
|---|---|
| **Code today** | `supportsIfExistsBeforeTableName()=true`; `create table if not exists`; C-DDL-001 covered-live IT |
| **SSOT drift** | Definition A A-DDL-007 still **延后**; baseline negative-only defer anchor |
| **P-007 action** | **Promotion-only** — align A-DDL-007 with C-DDL-001; remove duplicate DDL work |
| **Evidence** | `XuguTypeDdlDetailsTest#createTableIfNotExists_C_DDL_001`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` |

---

## Hard constraints (all delivery Phases)

1. **A-PAG-004/006:** Must **not** change default LimitHandler LIMIT path (doc: TOP ⊥ LIMIT).
2. **A-DDL-009:** Live IT requires privileged encryptor or formal waiver.
3. **C-SEL-001:** Default: Resolver closure unless Hibernate 7.4 mandates Selector.
4. **No** MySQL/Oracle inheritance; **no** sibling port; GAV **7.4.5.Final**; **NOT Ship**.

---

## Contract updates (RP-02)

| File | Change |
|---|---|
| `contracts/i009-deferred-batch-map.md` | **New** — batch map SSOT |
| `contracts/feature-matrix-i003-ruler-c.md` | C-JSON-006 → **文档不允许**; C-SRV/C-SEL target P-010 |
| `contracts/feature-matrix-definition-a.md` | I-009 target Phase notes on 17 deferred A-* rows |
| `contracts/production-regression-baseline.md` | § I-009 batch routing + C-JSON-006 doc-forbidden callout |

---

## Verification

See [`verification.json`](verification.json) for `harness_check` result.

---

## Next step

**RP-03 reviewer** — readonly audit: every deferred row has Phase target or honest known-limit; no scope creep.
