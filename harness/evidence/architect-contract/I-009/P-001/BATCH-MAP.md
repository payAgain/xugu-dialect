# I-009 / P-001 — Detailed Batch Map (P-002…P-010)

> **Role:** architect-contract (RP-02)  
> **Date:** 2026-07-21  
> **Initiative:** I-009 / Build B-001  
> **SSOT contract:** [`contracts/i009-deferred-batch-map.md`](../../../../contracts/i009-deferred-batch-map.md)  
> **Research input:** `harness/evidence/researcher/I-009/P-001/{INVENTORY,RESEARCH-NOTES}.md`

---

## P-002 — INTERVAL type (1 row)

| row_id | Doc verdict | Primary doc | Acceptance path | Known-limit lock |
|---|---|---|---|---|
| **A-TYP-014** | doc-allowed | `reference/sql/datatype/datetime.md` (§INTERVAL — 13 subtypes; `DEF_INTERVAL_STYLE`) | Map Hibernate interval/Duration if Dialect exposes; native-SQL round-trip IT with explicit column DDL per subtype | **Medium:** if Hibernate 7.4 surface absent → **known-limit-documented** with native SQL only; cite 13-subtype + style parameter complexity |

**Forbidden:** Inventing INTERVAL grammars not in datetime.md.

---

## P-003 — XML type + functions (2 rows)

| row_id | Doc verdict | Primary doc | Acceptance path | Known-limit lock |
|---|---|---|---|---|
| **A-TYP-016** | doc-allowed | `reference/sql/datatype/xml.md` (XML/XMLTYPE, BLOB, 2GB max) | `SQLXML` / XmlJdbcType wiring + entity round-trip or native IT | ORM XML columns uncommon — partial wiring acceptable with doc citation |
| **A-FUN-021** | doc-allowed | `reference/function/xml-functions/` (extract, xmlelement, xmlquery, **xmltable**, …) | Register bounded subset; live IT via native/HQL | **XMLTABLE** cluster note (single-node only) — flag for live IT environment; **not** json_table |

**Pairing:** A-TYP-016 + A-FUN-021 delivered together.

---

## P-004 — Geometric types + functions (2 rows)

| row_id | Doc verdict | Primary doc | Acceptance path | Known-limit lock |
|---|---|---|---|---|
| **A-TYP-017** | doc-allowed | `reference/sql/datatype/geometric.md` (POINT/LINE/LSEG/BOX/PATH/POLYGON/CIRCLE) | Simple 2D geometric literals — **not** PostGIS | Spatial index adjacent: `spatial-database/geometric-model/spatial-index.md` — not standard ORM export |
| **A-FUN-020** | doc-allowed | `reference/function/geometric-functions/` (21 functions) | Register subset aligned to geometric.md types | Depends on A-TYP-017 literals |

**Pairing:** A-TYP-017 + A-FUN-020 delivered together.

---

## P-005 — UDT (1 row)

| row_id | Doc verdict | Primary doc | Acceptance path | Known-limit lock |
|---|---|---|---|---|
| **A-TYP-018** | doc-allowed | `reference/sql/datatype/udt.md` (OBJECT / VARRAY / TABLE) | Native query / structural subset if JDBC type codes absent | **High:** full UDT-as-entity-column **known-limit-documented** unless JDBC mapping exists |

**Forbidden:** Inventing UDT syntax beyond udt.md.

---

## P-006 — Bit aggregates + regexp (2 rows)

| row_id | Doc verdict | Primary doc | Acceptance path | Known-limit lock |
|---|---|---|---|---|
| **A-FUN-015** | doc-allowed | `reference/function/aggregate-functions/bit_and.md`, `bit_or.md`; prerequisite `reference/sql/datatype/bit.md` (VARBIT) | Function registry + native IT | **Medium:** HQL path may be **known-limit** unless VARBIT type hook added |
| **A-FUN-019** | doc-allowed | `reference/function/string-functions/regexp_like.md`, `regexp_replace.md`, `regexp_substr.md` (+ instr/count) | Straightforward registry + live IT | **Low risk** — expect covered-live |

---

## P-007 — DDL extensions (3 rows)

| row_id | Doc verdict | Primary doc | Acceptance path | Known-limit lock |
|---|---|---|---|---|
| **A-DDL-007** | doc-allowed | `reference/object/table/create.md` (`IF NOT EXISTS`) | **SSOT promotion only** — cross-ref **C-DDL-001** evidence (`XuguTypeDdlDetailsTest/IT`); remove Definition A defer anchor | **Partial wiring today** — do **not** duplicate DDL code |
| **A-DDL-008** | doc-allowed | `reference/object/table/partition.md`, `create.md` (PARTITION BY) | Isolated native DDL IT | **Medium:** hbm2ddl auto-export unlikely — **known-limit** for schema-tool |
| **A-DDL-009** | doc-allowed | `create.md` (`ENCRYPT BY`); `reference/object/encryptor.md` | Native DDL IT or formal waiver | **High:** SYSSSO / `ACL_SSO` for encryptor — gated skip or **known-limit** |

**A-DDL-007 alignment:** Reconcile Definition A **延后** with Ruler C C-DDL-001 **可实现** / baseline `covered-live`.

---

## P-008 — Catalog + advanced indexes (2 rows)

| row_id | Doc verdict | Primary doc | Acceptance path | Known-limit lock |
|---|---|---|---|---|
| **A-SCH-003** | doc-allowed (SQL) / metadata caveat | `reference/object/database.md`; session `database.md` | JDBC metadata study under NONE; may stay SCHEMA-only | **Medium:** DATABASE session param **not SET-able** — catalog unreliable |
| **A-SCH-017** | doc-allowed | `reference/object/indexes.md` (§函数索引, BITMAP, LOCAL/GLOBAL) | Subset: BITMAP + functional index DDL string | **Low:** beyond standard ORM export — **known-limit** acceptable |

---

## P-009 — Locks, pagination alt, identity mode (4 rows)

| row_id | Doc verdict | Primary doc | Acceptance path | Known-limit lock |
|---|---|---|---|---|
| **A-LCK-006** | doc-allowed | `reference/object/table/lock.md` (`LOCK TABLE … IN … MODE`, NOWAIT/WAIT) | Native SQL helper + live IT — **not** JPA LockMode | Distinct from doc-forbidden SKIP LOCKED / FOR SHARE |
| **A-PAG-004** | doc-allowed | `reference/sql/select/resultset-restricted.md` (#top); `select.md` (`opt_top`) | Alternate TOP path — native IT only | **Low:** TOP **cannot** combine with LIMIT — **default LimitHandler unchanged** |
| **A-PAG-006** | doc-allowed | `reference/sql/select/select.md` (§8.3 ROWNUM) | Optional ROWNUM wrapper IT | **Low:** LIMIT preferred; not Hibernate default page path |
| **A-IDN-005** | doc-allowed | `identity_mode.md`; `def_identity_mode.md` | Session `SET IDENTITY_MODE` / dialect hook for NONE-mode edge cases | **Low risk** — expect covered-live for NULL/ZERO modes |

**Hard constraint:** A-PAG-004/006 must **not** change default LimitHandler LIMIT path.

---

## P-010 — Ruler C closure (3 rows)

| row_id | Doc verdict | Primary doc | Acceptance path | Known-limit lock |
|---|---|---|---|---|
| **C-JSON-006** | **doc-forbidden** | **No** `json_table` under `reference/function/json-functions/**` | **Negative only:** `supportsJsonTableFunction=false`; retain `@Disabled` anchor | **High — locked forbidden:** **MUST NOT** invent JSON_TABLE SQL; matrix → **文档不允许** |
| **C-SRV-001** | doc-allowed | `reference/system-configuration-parameter/session-parameter/*.md` (30 params) | Read-only probe (`SHOW` / session getters) — no mutating config | **Low risk** |
| **C-SEL-001** | doc-allowed (product/SPI) | Product SPI; sibling `XuguDialectSelector` | **Resolver closure** unless Hibernate 7.4 mandates Selector | **Low:** A-SPI-001…004 already delivered |

**XML vs JSON disambiguation:** XMLTABLE (`xmltable.md`) supports A-FUN-021 — **not** C-JSON-006.

---

## Row → Phase master table (20 rows)

| row_id | Phase | Doc verdict | Outcome class |
|---|---|---|---|
| A-TYP-014 | P-002 | doc-allowed | covered-live or known-limit |
| A-TYP-016 | P-003 | doc-allowed | covered-live or known-limit |
| A-FUN-021 | P-003 | doc-allowed | covered-live |
| A-TYP-017 | P-004 | doc-allowed | covered-live or known-limit |
| A-FUN-020 | P-004 | doc-allowed | covered-live |
| A-TYP-018 | P-005 | doc-allowed | known-limit likely |
| A-FUN-015 | P-006 | doc-allowed | covered-live or known-limit |
| A-FUN-019 | P-006 | doc-allowed | covered-live |
| A-DDL-007 | P-007 | doc-allowed | SSOT promotion (C-DDL-001) |
| A-DDL-008 | P-007 | doc-allowed | covered-live or known-limit |
| A-DDL-009 | P-007 | doc-allowed | known-limit likely |
| A-SCH-003 | P-008 | doc-allowed | covered-live or known-limit |
| A-SCH-017 | P-008 | doc-allowed | covered-live or known-limit |
| A-LCK-006 | P-009 | doc-allowed | covered-live |
| A-PAG-004 | P-009 | doc-allowed | known-limit (alternate path) |
| A-PAG-006 | P-009 | doc-allowed | known-limit (alternate path) |
| A-IDN-005 | P-009 | doc-allowed | covered-live |
| C-JSON-006 | P-010 | **doc-forbidden** | **negative-only** |
| C-SRV-001 | P-010 | doc-allowed | covered-live |
| C-SEL-001 | P-010 | doc-allowed | docs closure or Selector |

**Orphan rows:** 0

---

## Test anchor cross-ref (today)

| row_id | entry_class#method |
|---|---|
| All `@Disabled` deferred | `XuguNegativeRegressionBaselineTest#deferred_*` |
| A-DDL-007 | `XuguNegativeRegressionBaselineTest#definitionAIfNotExistsDeferred_A_DDL_007` + `XuguTypeDdlDetailsTest/IT` (C-DDL-001) |
| A-SCH-003 | `XuguNegativeRegressionBaselineTest#catalogsNotSupported_negativeOnly_A_SCH_003` |
| A-FUN-015 | `XuguFunctionRegistryTest#unsupportedFunctionNotRegistered_negativeNote` |
| C-JSON-006 | `XuguNegativeRegressionBaselineTest#deferred_C_JSON_006_jsonTable` |
| C-SRV-001 | `XuguNegativeRegressionBaselineTest#deferred_C_SRV_001_serverConfiguration` |
| C-SEL-001 | `XuguNegativeRegressionBaselineTest#deferred_C_SEL_001_dialectSelector` |
