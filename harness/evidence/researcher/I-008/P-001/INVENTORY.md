# I-008 / P-001 — 94 可实现 SSOT Honest Inventory (RP-01)

> **Role:** researcher  
> **invocation_id:** `inv-i008-p001-rp01-researcher`  
> **Date:** 2026-07-20  
> **Branch:** `feat/i-008-production-quality-gaps`  
> **Inputs:** `harness/initiatives/I-008/brief.md`, `contracts/production-regression-baseline.md`, `contracts/consumer-path-baseline.md`, `contracts/feature-matrix-definition-a.md`, `contracts/feature-matrix-i003-ruler-c.md`, `harness/initiatives/I-007/ARCHIVE.md`, `docs/production-readiness.md`, `docs/verification.md`, `docs/user-guide/**`, prior `harness/evidence/researcher/I-007/P-001/INVENTORY.md`  
> **Scope:** read-only honest audit of **可实现** rows vs I-005/I-006/I-007 SSOT; promotion batch proposal for P-003/P-004; **no** dialect/demo Java edits  
> **IT gate:** `XuguITGate` / `XuguIntegrationGate` ← env `XUGU_RUN_IT=true` or JVM `-Dxugu.run.integration=true`  
> **GAV:** `com.xugu:xugu-dialect:7.4.5.Final` · **compatiblemode=NONE** only · **NOT Ship**

---

## 1. Executive honest counts

| Bucket | SSOT `status` column (legacy) | **Honest I-008 classification** | Notes |
|---|---:|---:|---|
| **Physical 可实现 rows in SSOT tables** | **98** unique `matrix_id` | **98** | Charter still labels **94** (I-005 freeze); **+4** I-007 Track C promotions (`C-JSON-005`, `C-DDL-005`, `A-TYP-015`, `A-SEQ-006`) not reflected in summary § counts |
| **covered-live** | **5** | **79** | **5** explicit SSOT tag + **73** `gate=IT` rows with gated dialect IT anchor + **1** `gate=demo` (`A-XCUT-009`) with gated Boot IT |
| **covered** (legacy — has entrypoint, not tagged live) | **93** | **0** as terminal state | Legacy label; honest state is **covered-live** (73) or **unit-only** (19) or **demo-live** (1) |
| **unit-only-without-live** | *(embedded in `covered`)* | **19** | `gate=unit` only — **no** `*IT` / gated demo anchor on row |
| **known-limit-documented** | **0** | **0** | C-BULK-002 was known-limit in I-005; closed **covered-live** I-007/P-002 |
| **gap** | **0** | **0** | Every row has ≥1 verified entrypoint |

### Gate breakdown (physical 98)

| `gate` | Count | Honest live path when `XUGU_RUN_IT=true` |
|---|---:|---|
| **IT** | 78 | Dialect gated IT (`XuguITGate`) |
| **unit** | 19 | **None** — offline only |
| **demo** | 1 | Boot gated IT (`XuguIntegrationGate`) on `A-XCUT-009` |

### I-008 closure delta (what Q1 still owes)

| Action class | Row count | Owner Phase |
|---|---:|---|
| **Must add live IT or `known-limit-documented`** | **19** | **P-003** (Batch A — all Definition A unit-only) |
| **SSOT status tag only** (`covered` → `covered-live`; IT already exists) | **73** | **P-003** (Def A IT rows) + **P-004** (Ruler C IT rows + `A-XCUT-009`) |
| **Doc honesty** (remove「94 covered-live」) | — | **P-002** |

**Researcher verdict:** Docs/contracts claiming **「94 covered-live」** are **dishonest**. Honest live-capable count today = **79/98**; **19** rows are genuinely **unit-only-without-live**. Charter **94** vs physical **98** must be reconciled in architect-contract RP-02.

---

## 2. Predecessor SSOT snapshot

| Source | Status | Relevant to I-008 |
|---|---|---|
| I-005 `production-regression-baseline.md` | Accepted / archived | Baseline **94** label; legacy **`covered`** on 93 rows |
| I-006 `consumer-path-baseline.md` | FROZEN | Boot **41/41**; not a 94-row mirror |
| I-007 `ARCHIVE.md` | Accepted / archived | **+4** rows promoted **covered-live**; Flyway/Demo deepening |
| I-008 brief | Scope PASS | Q1 honest counts + promote remainder; Q2–Q4 docs/evidence gaps |

---

## 3. Honest classification legend (I-008)

| honest_status | Rule applied |
|---|---|
| **covered-live** | `gate=IT` with dialect `*IT` anchor **or** SSOT already `covered-live` **or** `gate=demo` with gated `@SpringBootTest` on row |
| **unit-only-without-live** | `gate=unit`; no live entry in `entry_class#method` |
| **known-limit-documented** | Formal waiver + user doc (none today) |
| **ambiguous** | Live exists but conditional / bundled / demo-vs-dialect split — per-row note required |

---

## 4. Domain inventory — Definition A (80 rows)

Columns: `matrix_id | SSOT status | gate | honest_status | promotion | notes`

### 4.1 Types (15 — includes I-007 `A-TYP-015`)

| matrix_id | SSOT | gate | honest | promotion | notes |
|---|---|---|---|---|---|
| A-TYP-001 | covered | IT | covered-live | P-004 tag | `XuguTypeRoundTripIT` |
| A-TYP-002 | covered | IT | covered-live | P-004 tag | same bundle |
| A-TYP-003 | covered | unit | **unit-only** | **P-003** | REAL→float; no round-trip IT |
| A-TYP-004 | covered | IT | covered-live | P-004 tag | |
| A-TYP-005 | covered | IT | covered-live | P-004 tag | |
| A-TYP-006 | covered | IT | covered-live | P-004 tag | |
| A-TYP-007 | covered | IT | covered-live | P-004 tag | unit columnType + `jdbcTimeRoundTrip` IT |
| A-TYP-008 | covered | IT | covered-live | P-004 tag | |
| A-TYP-009 | covered | IT | covered-live | P-004 tag | + `XuguBinarySchemaExportIT` |
| A-TYP-010 | covered | IT | covered-live | P-004 tag | |
| A-TYP-011 | covered | IT | covered-live | P-004 tag | |
| A-TYP-012 | covered | IT | covered-live | P-004 tag | UUID via dialect IT; Boot uses converter (Q3) |
| A-TYP-013 | covered | IT | covered-live | P-004 tag | + JSON IT |
| A-TYP-019 | covered | unit | **unit-only** | **P-003** | `castPattern` unit only |
| A-TYP-015 | covered-live | IT | covered-live | — | I-007/P-004; shares `XuguArrayTypeIT` with C-DDL-005 |

### 4.2 DDL (6)

| matrix_id | SSOT | gate | honest | promotion | notes |
|---|---|---|---|---|---|
| A-DDL-001 | covered | IT | covered-live | P-004 tag | |
| A-DDL-002 | covered | IT | covered-live | P-004 tag | |
| A-DDL-003 | covered | IT | covered-live | P-004 tag | |
| A-DDL-004 | covered | IT | covered-live | P-004 tag | |
| A-DDL-005 | covered | unit | **unit-only** | **P-003** | DEFAULT exporter unit; no schema-export IT |
| A-DDL-006 | covered | IT | covered-live | P-004 tag | |

### 4.3 Pagination (3)

All **covered-live** (`XuguPaginationIT`, `XuguHqlPaginationIT`); promotion **P-004 tag** only.

### 4.4 Locks (3)

All **covered-live** (`XuguLockIT`, lock+page AST); promotion **P-004 tag**. Q2 still lacks user-facing **PESSIMISTIC_READ→FOR UPDATE** integration section (P-002/P-005).

### 4.5 Identity & Sequence (10 — includes `A-SEQ-006`)

| matrix_id | SSOT | gate | honest | promotion | notes |
|---|---|---|---|---|---|
| A-IDN-001 | covered | unit | **unit-only** | **P-003** | wiring; live via A-IDN-003/004 — **known-limit candidate** |
| A-IDN-002 | covered | unit | **unit-only** | **P-003** | same bundle |
| A-IDN-003 | covered | IT | covered-live | P-004 tag | |
| A-IDN-004 | covered | IT | covered-live | P-004 tag | **ambiguous:** also lists `DemoPersonCrudIT` (Boot live) |
| A-SEQ-001 | covered | IT | covered-live | P-004 tag | |
| A-SEQ-002 | covered | unit | **unit-only** | **P-003** | DROP SEQUENCE strings — **known-limit candidate** (live via A-SEQ-001) |
| A-SEQ-003 | covered | IT | covered-live | P-004 tag | |
| A-SEQ-004 | covered | IT | covered-live | P-004 tag | |
| A-SEQ-005 | covered | unit | **unit-only** | **P-003** | sequence options unit — **known-limit candidate** |
| A-SEQ-006 | covered-live | IT | covered-live | — | I-007/P-004 `XuguAlterSequenceIT` |

### 4.6 Functions (17)

| matrix_id | SSOT | gate | honest | promotion | notes |
|---|---|---|---|---|---|
| A-FUN-001,002,004,008,010–014,016–018 | covered | IT | covered-live | P-004 tag | `XuguFunctionRegistryIT` / `XuguTypeDdlDetailsIT` |
| A-FUN-003,005,006,007,009 | covered | unit | **unit-only** | **P-003** | registry unit; live via A-FUN-001/004/010 bundle — **known-limit candidates** |

### 4.7 Schema / temp / comment (14)

| matrix_id | SSOT | gate | honest | promotion | notes |
|---|---|---|---|---|---|
| A-SCH-001,002,004,005,008–016 | covered | IT | covered-live | P-004 tag | |
| A-SCH-005 | covered | IT | covered-live | P-004 tag | **ambiguous:** IT may skip if global temp OFF — still gated IT exists |
| A-SCH-006 | covered | unit | **unit-only** | **P-003** | temp strategy flags; live via A-SCH-004/005 IT — **known-limit candidate** |

### 4.8 SPI & cross-cutting (12)

| matrix_id | SSOT | gate | honest | promotion | notes |
|---|---|---|---|---|---|
| A-SPI-001,002,003 | covered | IT | covered-live | P-004 tag | |
| A-SPI-004 | covered | unit | **unit-only** | **P-003** | resolver **non-match** negatives — **known-limit candidate** (no live path by design) |
| A-XCUT-001 | covered | unit | **unit-only** | **P-003** | IdentifierHelper UPPER — prefer thin live IT |
| A-XCUT-002 | covered | unit | **unit-only** | **P-003** | quote char — **known-limit candidate** |
| A-XCUT-003 | covered | IT | covered-live | P-004 tag | + offline demo yml smoke |
| A-XCUT-004 | covered | IT | covered-live | P-004 tag | TCL + tx smoke IT |
| A-XCUT-005 | covered | unit | **unit-only** | **P-003** | isolation hooks unit — **known-limit candidate** |
| A-XCUT-007 | covered | unit | **unit-only** | **P-003** | TCL keywords duplicate of A-XCUT-004 live — **known-limit candidate** |
| A-XCUT-008 | covered | unit | **unit-only** | **P-003** | SequenceSupport flag — **known-limit candidate** (A-SEQ-003 IT) |
| A-XCUT-009 | covered | demo | **covered-live** | **P-004** | **ambiguous:** demo-live only; no dialect IT on row — consumer golden path |

**Definition A unit-only subtotal: 19 rows** (all Batch **P-003**).

---

## 5. Domain inventory — Ruler C (18 rows)

| matrix_id | SSOT | gate | honest | promotion | notes |
|---|---|---|---|---|---|
| C-EXC-001,002 | covered | IT | covered-live | P-004 tag | |
| C-JSON-001–004 | covered | IT | covered-live | P-004 tag | need `JSON_FUNCTIONS_ENABLED` in IT |
| C-JSON-005 | covered-live | IT | covered-live | — | I-007/P-004 |
| C-WIN-001, C-CTE-001 | covered | IT | covered-live | P-004 tag | |
| C-BULK-001,003 | covered | IT | covered-live | P-004 tag | |
| C-BULK-002 | covered-live | IT | covered-live | — | I-007/P-002 |
| C-DDL-001,002,003 | covered | IT | covered-live | P-004 tag | |
| C-DDL-005 | covered-live | IT | covered-live | — | pairs with A-TYP-015 |
| C-CAT-001, C-GUID-001 | covered | IT | covered-live | P-004 tag | |

**Ruler C unit-only: 0.** Batch **P-004** = SSOT tag uplift (15 rows) + consumer/demo cross-links.

---

## 6. Unit-only row register (19) — promotion required

| matrix_id | entry anchor | lean promotion | known-limit bundle (if any) |
|---|---|---|---|
| A-TYP-003 | `XuguDialectTest#columnTypesMatchXuguDocs` | extend `XuguTypeRoundTripIT` REAL/float | — |
| A-TYP-019 | `XuguDialectTest#castPatternDefaultUsesStandardCastSyntax_A_TYP_019` | schema-export or cast live IT | — |
| A-DDL-005 | `XuguDefaultColumnExportTest#schemaExportEmitsDefaultColumn_A_DDL_005` | gated DDL export IT | — |
| A-IDN-001 | `XuguIdentitySequenceTest#identitySupportWired_A_IDN_001` | optional identity DDL IT | **A-IDN-003/004** |
| A-IDN-002 | same test (no AUTO_INCREMENT) | optional | **A-IDN-003/004** |
| A-SEQ-002 | `createDropSequenceStrings` unit | optional DROP live | **A-SEQ-001** |
| A-SEQ-005 | sequence options unit | optional | **A-SEQ-001/003** |
| A-FUN-003,005,006,007,009 | `coreAnsiFunctionsRegistered` | optional per-function IT | **A-FUN-001/004/010 IT** |
| A-SCH-006 | temp strategy unit | optional | **A-SCH-004/005 IT** |
| A-SPI-004 | resolver non-match unit | **known-limit** preferred | non-Xugu products — no live |
| A-XCUT-001 | `unquotedIdentifiersFoldToUppercase` | thin live identifier IT | — |
| A-XCUT-002 | `quoteCharsAreDoubleQuote` | known-limit OK | — |
| A-XCUT-005 | `isolationLevelHooksMatchXuguIsoLevel` | known-limit OK | hooks only |
| A-XCUT-007 | `keywordsIncludeTcl` | known-limit | **A-XCUT-004 IT** |
| A-XCUT-008 | `sequenceSupportWired` | known-limit | **A-SEQ-003 IT** |

---

## 7. Promotion batch proposal (for architect-contract RP-02)

| Batch | Phase | Row count | Content |
|---|---|---:|---|
| **A** | **P-003** | **19** | All Definition A **unit-only-without-live** — live IT **or** `known-limit-documented` + reason |
| **B** | **P-004** | **16** | **15** Ruler C `covered`→`covered-live` SSOT tags + **`A-XCUT-009`** demo-live clarification; consumer UUID/JSON cross-links (Q3) |

**Optional SSOT tag-only (Def A IT rows):** 58 rows — may fold into P-003 SSOT sweep or split; **minimum** closure requires Batch A + B above.

---

## 8. Constraints reminder

| Constraint | Value |
|---|---|
| GAV | `7.4.5.Final` — no bump |
| compatiblemode | **NONE** only |
| Q5 perf/multi-version | **OUT** |
| RP-01 writes | `harness/evidence/researcher/I-008/P-001/**` only |
| No edits | `org/**`, `contracts/**`, other role namespaces |

---

## 9. Handoff to architect-contract (RP-02)

1. Publish promotion map locking Batch **A=19** / **B=16** (or merged SSOT sweep).  
2. Reconcile charter **94** vs physical **98** in SSOT summary counts.  
3. Lock `known-limit-documented` candidates with reasons (§6).  
4. Cross-link Q2/Q3/Q4 gap notes from [`GAP-SUMMARY.md`](GAP-SUMMARY.md).  
5. Do **not** silently expand I-006 **41-row** Boot SSOT.
