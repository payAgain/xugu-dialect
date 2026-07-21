# I-009 / P-001 — Xugu Docs Audit Research Notes

> **Role:** researcher  
> **Date:** 2026-07-21  
> **Audit scope:** 20 deferred matrix rows (A-XCUT-012 excluded — Ship OUT)  
> **Docs root:** `E:\Work\docs\content/` (read-only)

---

## 1. Audit methodology

1. Read I-009 brief + scope clarifying draft for locked rules (doc-forbidden vs deferred delivery).  
2. Cross-check each row against Definition A / Ruler C SSOT status (**延后**) and `production-regression-baseline.md` (**negative-only** anchors).  
3. For each row, locate primary Xugu doc paths under `reference/` (and `spatial-database/` where noted).  
4. Repo-wide content grep for gaps (notably `json_table` / `JSON_TABLE` — **zero hits**).  
5. Inspect current dialect/test anchors (`XuguNegativeRegressionBaselineTest`, `XuguDialect`, C-DDL-001 wiring).  
6. Map rows to orchestrator batch **P-002…P-010** (already in `harness/handoffs/orchestrator/I-009-plan-complete.md`).

**Constraints applied:** GAV `7.4.5.Final`; `compatiblemode=NONE`; no invented SQL for doc-forbidden features.

---

## 2. Key findings by domain

### 2.1 Types (A-TYP-014/016/017/018)

| Row | Doc evidence | Hibernate surface | Finding |
|---|---|---|---|
| A-TYP-014 | `datetime.md` documents 13 INTERVAL subtypes, literals, `DEF_INTERVAL_STYLE` (SQL_STANDARD / ISO_8601 / POSTGRES variants) | `IntervalJdbcType` / duration if exposed | **Doc-allowed.** Complexity: mapping one Hibernate interval to many Xugu subtypes. |
| A-TYP-016 | `xml.md`: XML + XMLTYPE, BLOB-backed, 2GB max; points to xml-functions | `SQLXML` / XmlJdbcType | **Doc-allowed.** Standard ORM XML columns uncommon but documented. |
| A-TYP-017 | `geometric.md`: POINT, LINE, LSEG, BOX, PATH, POLYGON, CIRCLE with literal syntax | Geometry JDBC / user types | **Doc-allowed** for simple 2D types — not full GIS. |
| A-TYP-018 | `udt.md`: OBJECT, VARRAY, TABLE; CREATE TYPE, methods, comparison rules | Entity attribute UDT | **Doc-allowed** at SQL level; **ORM mapping likely partial** — strongest known-limit in type batch. |

### 2.2 Functions (A-FUN-015/019/020/021)

| Row | Doc evidence | Finding |
|---|---|---|
| A-FUN-015 | `aggregate-functions/bit_and.md`, `bit_or.md`; input type **VARBIT** per `bit.md` | **Doc-allowed.** Register aggregates; HQL path depends on VARBIT column mapping (may be native-only). |
| A-FUN-019 | `regexp_like.md`, `regexp_replace.md`, `regexp_substr.md` (+ instr/count) | **Doc-allowed.** Low implementation risk — mirror existing function registry pattern. |
| A-FUN-020 | 21 files under `geometric-functions/` | **Doc-allowed.** Deliver bounded subset tied to A-TYP-017 types. |
| A-FUN-021 | `xml-functions/` incl. extract, xmlelement, xmlquery, **xmltable.md** | **Doc-allowed.** Note XMLTABLE cluster restriction (single-node only) in doc — flag for live IT environment. |

### 2.3 DDL (A-DDL-007/008/009)

| Row | Doc evidence | Finding |
|---|---|---|
| A-DDL-007 | `table/create.md` — `IF NOT EXISTS` on CREATE TABLE / TEMP | **Doc-allowed AND already wired** via I-003 **C-DDL-001** (`supportsIfExistsBeforeTableName`, `create table if not exists`). A-DDL-007 SSOT still **延后** — promotion-only work in P-007. |
| A-DDL-008 | `table/partition.md` + create examples with `PARTITION BY RANGE/LIST/HASH` | **Doc-allowed.** Unlikely in Hibernate auto schema export — native DDL IT. |
| A-DDL-009 | `create.md` `ENCRYPT BY`; `encryptor.md` requires SYSSSO to `CREATE ENCRYPTOR` | **Doc-allowed** but **privileged** — schema export in app context may be **known-limit**. |

### 2.4 Schema / indexes (A-SCH-003/017)

| Row | Doc evidence | Finding |
|---|---|---|
| A-SCH-003 | `object/database.md` (CREATE/DROP DATABASE); session `database.md` (connection context, **not SET-able**) | SQL-level catalog exists; Hibernate `catalog.schema.table` needs JDBC `DatabaseMetaData` alignment. Current dialect: `NameQualifierSupport.SCHEMA`, `supportsCatalogs=false`. **Metadata caveat** — not doc-forbidden. |
| A-SCH-017 | `indexes.md`: functional indexes (`len(name)`), `INDEXTYPE IS BITMAP`, LOCAL/GLOBAL partition indexes | **Doc-allowed** beyond basic B-tree (A-SCH-016 closed). Spatial index: `spatial-database/geometric-model/spatial-index.md`. |

### 2.5 Locks / pagination / identity (A-LCK-006, A-PAG-004/006, A-IDN-005)

| Row | Doc evidence | Finding |
|---|---|---|
| A-LCK-006 | `table/lock.md`: `LOCK TABLE` + SHARE/EXCLUSIVE/ROW SHARE/ROW EXCLUSIVE + NOWAIT/WAIT ms | **Doc-allowed.** Explicit table lock — separate from SELECT pessimistic locks (A-LCK-001…003 closed). |
| A-PAG-004 | `resultset-restricted.md` #top; `select.md` opt_top — **TOP ≡ LIMIT top_n; mutually exclusive with LIMIT** | **Doc-allowed** alternate syntax only; default LimitHandler must stay LIMIT. |
| A-PAG-006 | `select.md` §ROWNUM pseudo-column + ORDER BY subquery pattern | **Doc-allowed** Oracle-style pattern; not Hibernate default page path. |
| A-IDN-005 | `identity_mode.md` (v12.0.6): DEFAULT / NULL_AS_AUTO_INCREMENT / ZERO_AS_AUTO_INCREMENT; ties to `def_identity_mode` ini | **Doc-allowed** session parameter — relevant for NONE-mode identity edge cases (explicit NULL/0 insert). |

### 2.6 Ruler C (C-JSON-006, C-SRV-001, C-SEL-001)

| Row | Doc evidence | Finding |
|---|---|---|
| C-JSON-006 | **No** `json_table` under `reference/function/json-functions/` (grep 2026-07-21) | **Doc-forbidden** — matrix note confirmed. Must keep `supportsJsonTableFunction=false`; do **not** invent JSON_TABLE. XML analogue exists: `xml-functions/xmltable.md`. |
| C-SRV-001 | 30 session-parameter docs (`iso_level`, `compatible_mode`, `identity_mode`, …) | **Doc-allowed** read-only introspection (`SHOW`, session metadata). |
| C-SEL-001 | Product SPI; sibling inventory `XuguDialectSelector` | **Resolver delivered** (A-SPI-002, META-INF/services). Selector may be redundant — **docs/SPI closure** candidate unless Hibernate 7.4 requires both interfaces. |

---

## 3. Doc-forbidden vs doc-allowed summary

| Verdict | Row IDs |
|---|---|
| **doc-allowed (19)** | A-TYP-014/016/017/018, A-FUN-015/019/020/021, A-DDL-007/008/009, A-SCH-003/017, A-LCK-006, A-PAG-004/006, A-IDN-005, C-SRV-001, C-SEL-001 |
| **doc-forbidden (1)** | **C-JSON-006** (no Xugu json_table grammar) |

**Explicit out-of-scope (brief):** SKIP LOCKED, FOR SHARE, ANSI FETCH, READ UNCOMMITTED, temp FK, MySQL/Oracle dialect inheritance, **A-XCUT-012 Ship**.

---

## 4. Known-limit candidates (honest promotion paths)

Rows where **covered-live** is plausible but **known-limit-documented** is a valid P-002…P-010 outcome:

| Priority | row_id | Reason |
|---|---|---|
| **High** | C-JSON-006 | No doc → stay negative; reclassify 文档不允许 |
| **High** | A-TYP-018 | UDT ORM entity columns rarely mappable; SQL/PLSQL subset only |
| **High** | A-DDL-009 | SYSSSO encryptor prerequisite for live IT |
| **Medium** | A-TYP-014 | 13 INTERVAL subtypes + style parameter vs single Hibernate interval type |
| **Medium** | A-SCH-003 | JDBC catalog vs Xugu database; DATABASE not session-SET |
| **Medium** | A-DDL-008 | PARTITION BY unlikely in Hibernate schema export |
| **Medium** | A-FUN-015 | VARBIT prerequisite for bit aggregates |
| **Low** | A-PAG-004, A-PAG-006 | Alternate pagination; LIMIT remains default |
| **Low** | C-SEL-001 | Resolver may suffice — documentation closure |
| **Low** | A-SCH-017 | Advanced indexes beyond standard exporter |

**Low risk (expect covered-live):** A-FUN-019, A-LCK-006, A-IDN-005, C-SRV-001, A-FUN-020/021 (with type pairs), A-TYP-016/017 (with bounded scope).

**Promotion without new code:** A-DDL-007 (align SSOT with C-DDL-001).

---

## 5. Partial wiring discovered (SSOT drift)

| row_id | Code today | SSOT still says |
|---|---|---|
| A-DDL-007 | `XuguDialect.supportsIfExistsBeforeTableName()=true`; `getCreateTableString()` → `create table if not exists`; C-DDL-001 covered-live IT | Definition A **延后**; baseline negative-only with explicit defer anchor |

Architect-contract should reconcile Definition A row A-DDL-007 with Ruler C C-DDL-001 on promotion.

---

## 6. XML vs JSON “table” disambiguation

Audit found an important naming trap:

- **XMLTABLE** — documented at `reference/function/xml-functions/xmltable.md` → supports A-FUN-021 / A-TYP-016.  
- **json_table** — **not documented anywhere** in `E:\Work\docs\content` → C-JSON-006 must not be implemented by analogy to XMLTABLE.

---

## 7. I-008 baseline context

- I-008 **accepted + archived** (NOT Ship); live reactor green per ARCHIVE.  
- All 20 inventory rows remain **negative-only** in `production-regression-baseline.md` with `@Disabled` anchors in `XuguNegativeRegressionBaselineTest`.  
- Consumer-path baseline (41 Boot rows) explicitly excludes deferred rows — no Boot SSOT expansion expected in I-009 unless Human Gate changes scope.

---

## 8. Recommended architect-contract actions (RP-02)

1. Publish batch map confirming table in INVENTORY.md (P-002…P-010).  
2. Reclassify **C-JSON-006** → **文档不允许** in `feature-matrix-i003-ruler-c.md`.  
3. Document **A-DDL-007** promotion path via C-DDL-001 cross-ref in acceptance criteria.  
4. Lock **known-limit** acceptance wording for UDT, ENCRYPT, catalog, json_table (forbidden), alternate pagination.  
5. Keep doc-forbidden lock/pagination/isolation rows untouched in negative suite.
