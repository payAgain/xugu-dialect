# I-009 / P-001 — Deferred Matrix Row Inventory (RP-01)

> **Role:** researcher  
> **Date:** 2026-07-21  
> **Initiative:** I-009 feature — deferred matrix delivery  
> **Build:** B-001 / Phase P-001  
> **GAV:** `7.4.5.Final` · **compatiblemode:** `NONE`  
> **Inputs:** `harness/initiatives/I-009/brief.md`, `harness/drafts/I-009-SCOPE-CLARIFYING.md`, `contracts/feature-matrix-definition-a.md`, `contracts/feature-matrix-i003-ruler-c.md`, `contracts/production-regression-baseline.md`, `contracts/consumer-path-baseline.md`, `harness/initiatives/I-008/ARCHIVE.md`, `docs/production-readiness.md`  
> **Xugu docs (read-only):** `E:\Work\docs\content/`  
> **Out of inventory scope:** A-XCUT-012 (Ship OUT per brief)

## Status legend (current SSOT)

| Current SSOT status | Source | Meaning today |
|---|---|---|
| **延后** | Definition A / Ruler C matrix | Docs may allow; not yet delivered in product |
| **negative-only** | `production-regression-baseline.md` | Explicit deferral anchor; no positive SQL invention |
| **partial wiring** | Code exists but matrix row still **延后** | Cross-row delivery (noted per row) |

## Doc verdict legend

| Doc verdict | Meaning for I-009 |
|---|---|
| **doc-allowed** | Xugu docs document SQL/behavior — may implement in assigned Phase |
| **doc-forbidden** | No documented SQL/behavior — **MUST NOT** invent; remain skip/negative |

## Batch map (orchestrator Plan — serial P-002…P-010)

| Phase | Row IDs | Theme |
|---|---|---|
| **P-002** | A-TYP-014 | INTERVAL type |
| **P-003** | A-TYP-016, A-FUN-021 | XML type + XML functions |
| **P-004** | A-TYP-017, A-FUN-020 | Geometric/spatial types + functions |
| **P-005** | A-TYP-018 | UDT |
| **P-006** | A-FUN-015, A-FUN-019 | bit_and/bit_or + regexp_* |
| **P-007** | A-DDL-007, A-DDL-008, A-DDL-009 | IF NOT EXISTS / partition / ENCRYPT |
| **P-008** | A-SCH-003, A-SCH-017 | Catalog qualifier + advanced indexes |
| **P-009** | A-LCK-006, A-PAG-004, A-PAG-006, A-IDN-005 | LOCK TABLE + TOP/ROWNUM alt + IDENTITY_MODE |
| **P-010** | C-JSON-006, C-SRV-001, C-SEL-001 | json_table + ServerConfig + DialectSelector |

---

## Row-level inventory (20 rows)

Columns: `row_id | matrix | current SSOT status | baseline status | doc verdict | primary Xugu doc path | recommended Phase | known-limit candidate notes`

| row_id | matrix | current SSOT status | baseline status | doc verdict | primary Xugu doc path | recommended Phase | known-limit candidate notes |
|---|---|---|---|---|---|---|---|
| **A-TYP-014** | Definition A | **延后** | negative-only (`@Disabled` anchor) | **doc-allowed** | `reference/sql/datatype/datetime.md` (§INTERVAL — 13 subtypes; `DEF_INTERVAL_STYLE`) | **P-002** | Hibernate 7.4 `Duration`/interval JDBC mapping must pick among 13 Xugu INTERVAL grammars; live IT may need explicit column DDL per subtype. If Hibernate surface absent, **known-limit-documented** with native-SQL round-trip only. |
| **A-TYP-016** | Definition A | **延后** | negative-only | **doc-allowed** | `reference/sql/datatype/xml.md` (XML/XMLTYPE, BLOB storage, max 2GB) | **P-003** | ORM `SQLXML`/XmlJdbcType wiring may be partial; cluster note on `XMLTABLE` (single-node only) is separate from column type. Pair with A-FUN-021. |
| **A-TYP-017** | Definition A | **延后** | negative-only | **doc-allowed** | `reference/sql/datatype/geometric.md` (POINT/LINE/LSEG/BOX/PATH/POLYGON/CIRCLE) | **P-004** | No PostGIS-style types — simple 2D geometric literals only. Spatial index doc lives under `spatial-database/geometric-model/spatial-index.md` (adjacent, not standard ORM export). Pair with A-FUN-020. |
| **A-TYP-018** | Definition A | **延后** | negative-only | **doc-allowed** | `reference/sql/datatype/udt.md` (OBJECT / VARRAY / TABLE) | **P-005** | Full UDT-as-entity-column is **high risk** for Hibernate ORM; honest outcome may be **known-limit-documented** (native query / structural subset) unless JDBC type codes exist for OBJECT. |
| **A-FUN-015** | Definition A | **延后** | negative-only (+ unit: `bit_and` not registered) | **doc-allowed** | `reference/function/aggregate-functions/bit_and.md`, `bit_or.md`; type prerequisite `reference/sql/datatype/bit.md` (VARBIT) | **P-006** | Aggregates require **VARBIT** columns; standard Hibernate entity mappings rarely emit VARBIT — may deliver function registry + native IT only (**known-limit** for HQL unless VARBIT type hook added). |
| **A-FUN-019** | Definition A | **延后** | negative-only | **doc-allowed** | `reference/function/string-functions/regexp_like.md`, `regexp_replace.md`, `regexp_substr.md` (+ `regexp_instr`, `regexp_count`) | **P-006** | Straightforward function registration; live IT via native/HQL. Low known-limit risk. |
| **A-FUN-020** | Definition A | **延后** | negative-only | **doc-allowed** | `reference/function/geometric-functions/` (21 functions: area, center, circle, …) | **P-004** | Depends on A-TYP-017 type literals; register bounded subset aligned to geometric.md types. |
| **A-FUN-021** | Definition A | **延后** | negative-only | **doc-allowed** | `reference/function/xml-functions/` (extract, xmlelement, xmlquery, **xmltable**, …) | **P-003** | **XMLTABLE** documented (`reference/function/xml-functions/xmltable.md`) — not to be confused with missing JSON `json_table`. Pair with A-TYP-016. |
| **A-DDL-007** | Definition A | **延后** | negative-only (active unit anchor) | **doc-allowed** | `reference/object/table/create.md` (`IF NOT EXISTS`) | **P-007** | **Partial wiring today:** `supportsIfExistsBeforeTableName()=true`, `create table if not exists` via **C-DDL-001** (`XuguTypeDdlDetailsTest/IT`). P-007 task is **SSOT promotion** of A-DDL-007 to covered-live (cross-ref C-DDL-001), not green-field DDL. |
| **A-DDL-008** | Definition A | **延后** | negative-only | **doc-allowed** | `reference/object/table/partition.md`, `reference/object/table/create.md` (PARTITION BY examples) | **P-007** | Hibernate schema export rarely emits `PARTITION BY`; likely **known-limit-documented** for hbm2ddl auto-export, with isolated live IT using native DDL. |
| **A-DDL-009** | Definition A | **延后** | negative-only | **doc-allowed** | `reference/object/table/create.md` (`ENCRYPT BY encryptor_name`); `reference/object/encryptor.md` | **P-007** | Encryptor creation requires **SYSSSO** / `ACL_SSO`; live IT may be gated skip without privileged user — **known-limit-documented** for normal app schema export. |
| **A-SCH-003** | Definition A | **延后** | negative-only (active: `supportsCatalogs=false`) | **doc-allowed** (SQL) / **metadata caveat** | `reference/object/database.md`; session `reference/system-configuration-parameter/session-parameter/database.md` | **P-008** | Xugu “database” ≠ Hibernate catalog always; JDBC `DATABASE` session param is **not SET-able** (connection-only). Promotion requires JDBC metadata study — may stay **known-limit-documented** SCHEMA-only if driver catalog unreliable under NONE. |
| **A-SCH-017** | Definition A | **延后** | negative-only | **doc-allowed** | `reference/object/indexes.md` (§函数索引, `INDEXTYPE IS BITMAP`, LOCAL/GLOBAL partition indexes) | **P-008** | Beyond standard ORM `create index`; deliver subset (BITMAP + functional index DDL string) or **known-limit** for schema-tool export. Spatial index: `spatial-database/geometric-model/spatial-index.md`. |
| **A-LCK-006** | Definition A | **延后** | negative-only | **doc-allowed** | `reference/object/table/lock.md` (`LOCK TABLE … IN … MODE`, NOWAIT/WAIT) | **P-009** | Not on JPA `LockMode` path; implement as documented native SQL helper + live IT. Distinct from **doc-forbidden** SELECT `SKIP LOCKED` / `FOR SHARE`. |
| **A-PAG-004** | Definition A | **延后** | negative-only | **doc-allowed** | `reference/sql/select/resultset-restricted.md` (#top); `reference/sql/select/select.md` (`opt_top`) | **P-009** | Doc: TOP **cannot** combine with LIMIT. Hibernate LimitHandler stays LIMIT — TOP is alternate/native path only (**known-limit**: default pagination unchanged). |
| **A-PAG-006** | Definition A | **延后** | negative-only | **doc-allowed** | `reference/sql/select/select.md` (§8.3 ROWNUM pseudo-column) | **P-009** | Oracle-style wrapper pagination; not Hibernate default. **known-limit**: document LIMIT preferred; optional ROWNUM wrapper IT for native SQL. |
| **A-IDN-005** | Definition A | **延后** | negative-only | **doc-allowed** | `reference/system-configuration-parameter/session-parameter/identity_mode.md`; `reference/system-configuration-parameter/xugu.ini/compatible/def_identity_mode.md` | **P-009** | Session `SET IDENTITY_MODE` / `ALTER SESSION SET` documented (v12.0.6+). Dialect hook for NONE-mode identity edge cases; live IT for NULL/ZERO-as-auto-increment modes. |
| **C-JSON-006** | Ruler C | **延后** | negative-only | **doc-forbidden** | **No `json_table` / JSON_TABLE entry** under `reference/function/json-functions/**` (repo-wide grep 2026-07-21) | **P-010** | Matrix already notes “no dedicated json_table file”. Per SSOT rule: **MUST NOT invent** — expect `supportsJsonTableFunction=false` + negative anchor retained; reclassify matrix status to **文档不允许** (architect-contract). Contrast: XML has `xmltable.md`. |
| **C-SRV-001** | Ruler C | **延后** | negative-only | **doc-allowed** | `reference/system-configuration-parameter/session-parameter/*.md` (30 session params incl. `identity_mode`, `iso_level`, `compatible_mode`, `database`, …) | **P-010** | Read-only probe (`SHOW` / session getters) — no mutating server config in dialect. Nice-to-have; low risk. |
| **C-SEL-001** | Ruler C | **延后** | negative-only | **doc-allowed** (product/SPI) | Product SPI (no Xugu SQL); sibling read-only `XuguDialectSelector` | **P-010** | **A-SPI-001…004 / `XuguDialectResolver`** already delivered. Likely **known-limit / docs closure**: add `DialectSelector` only if Hibernate 7.4 SPI requires both; else document Resolver as sufficient autodetect. |

---

## Doc-forbidden reaffirmation (I-009 must NOT implement)

These remain **skip/negative-only** — not in the 20-row inventory but explicitly called out in brief/constraints:

| row_id | Reason | Doc path |
|---|---|---|
| A-PAG-005 | ANSI FETCH not documented | — (not in resultset-restricted) |
| A-LCK-004 / C-SKIP-001 | SKIP LOCKED absent from FOR UPDATE grammar | — |
| A-LCK-005 | FOR SHARE absent | — |
| A-SCH-007 | Temp table FK forbidden | `reference/object/table/create.md` |
| A-XCUT-006 | READ UNCOMMITTED not in ISO_LEVEL | `session-parameter/iso_level.md` |
| A-XCUT-010 / A-XCUT-011 | Charter non-goals | ADR / Charter |
| **C-JSON-006** | **No json_table grammar in Xugu docs** | — (audit finding) |

---

## Summary counts

| Bucket | Count |
|---:|---:|
| **Inventory rows (in-scope deferred)** | **20** |
| doc-allowed | **19** |
| doc-forbidden (audit) | **1** (C-JSON-006) |
| Rows with **partial wiring today** | **1** (A-DDL-007 via C-DDL-001) |
| **known-limit candidates** (honest promotion risk) | **10** (see RESEARCH-NOTES.md §4) |
| Phase assignments | **P-002…P-010** (1 row → P-002; 2 → P-003/P-004/P-006; 1 → P-005; 3 → P-007; 2 → P-008; 4 → P-009; 3 → P-010) |
| Ship OUT | A-XCUT-012 (not in table) |

## Cross-refs (current test anchors)

| row_id | entry_class#method (today) |
|---|---|
| All `@Disabled` deferred | `XuguNegativeRegressionBaselineTest#deferred_*` |
| A-DDL-007 | `XuguNegativeRegressionBaselineTest#definitionAIfNotExistsDeferred_A_DDL_007` + `XuguTypeDdlDetailsTest/IT` (C-DDL-001) |
| A-SCH-003 | `XuguNegativeRegressionBaselineTest#catalogsNotSupported_negativeOnly_A_SCH_003`; `XuguSchemaTempCommentTest#nameQualifierIsSchemaOnly_A_SCH_002_not_003` |
| A-FUN-015 | `XuguFunctionRegistryTest#unsupportedFunctionNotRegistered_negativeNote` |
| C-JSON-006 | `XuguNegativeRegressionBaselineTest#deferred_C_JSON_006_jsonTable` |
| C-SRV-001 | `XuguNegativeRegressionBaselineTest#deferred_C_SRV_001_serverConfiguration` |
| C-SEL-001 | `XuguNegativeRegressionBaselineTest#deferred_C_SEL_001_dialectSelector` |

## Researcher handoff notes (for architect-contract RP-02)

1. **C-JSON-006** should be reclassified **文档不允许** in matrix SSOT — do not assign implementer to invent JSON_TABLE SQL.  
2. **A-DDL-007** P-007 acceptance should reference existing **C-DDL-001** evidence; avoid duplicate DDL implementation.  
3. **A-PAG-004/006** delivery must not change default LimitHandler LIMIT path (doc: TOP ⊥ LIMIT).  
4. **A-DDL-009** live IT needs privileged encryptor setup or formal waiver.  
5. **C-SEL-001** default recommendation: Resolver closure unless Hibernate 7.4 mandates separate Selector registration.
