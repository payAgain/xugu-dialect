# I-005 / P-001 — P-002 Must-Close Gap Summary (Domain Aggregated)

> **Role:** researcher / RP-01  
> **Audience:** P-002 implementer (live IT + entrypoint closure)  
> **Source:** [`INVENTORY.md`](INVENTORY.md)

## Executive summary

Among **94 可实现** matrix rows (78 Definition A + 16 Ruler C), **87 are covered** by at least one verified test entrypoint; **7 have gaps**.  
P-002 should prioritize **live ORM/JDBC IT** gaps and **unit wiring holes** that block consumer-path confidence — not re-test rows already green at unit+IT.

**Blockers for P-002 close:** live XuguDB + `XUGU_RUN_IT=true`; C-BULK-002 live IT remains **waived** (documented GetGeneratedKeys blocker) unless mapping changes.

---

## P-002 must-close by domain

### 1. Types & identifiers (P-003 domain)

| matrix_id | Gap | Current | P-002 action |
|-----------|-----|---------|--------------|
| A-TYP-019 | No `castPattern` test | Implementation in `XuguDialect#castPattern` | Add unit assert default cast SQL pattern |
| A-TYP-007 | TIME column type: unit only | `XuguDialectTest#columnTypesMatchXuguDocs` | Add JDBC TIME round-trip in `XuguTypeRoundTripIT` or dedicated probe |
| A-XCUT-001 | No IdentifierHelper UPPER test | `buildIdentifierHelper` override in dialect | Add unit test: unquoted → uppercase fold |

**Priority:** medium (type mapping confidence; no known production incident)

---

### 2. DDL & schema export

| matrix_id | Gap | Current | P-002 action |
|-----------|-----|---------|--------------|
| A-DDL-005 | No DEFAULT column exporter test | DDL helpers partially in `XuguDialectTest` | Add schema-export script assert or gated IT with DEFAULT column |
| A-SCH-014 | DROP CONSTRAINT not exercised live | Unit strings in `XuguSchemaTempCommentTest` | Add `XuguSchemaTempCommentIT` step: `ALTER TABLE … DROP CONSTRAINT` |

**Priority:** medium

---

### 3. Exception mapping (Ruler C / P-002 theme)

| matrix_id | Gap | Current | P-002 action |
|-----------|-----|---------|--------------|
| C-EXC-002 | Constraint name not asserted on live ORM path | Offline: `XuguExceptionConversionTest#extractorParsesNotNullFieldName` | Extend `XuguExceptionMappingIT` or add NOT NULL violation IT asserting extractor when driver message includes field name |

**Priority:** high (I-003 theme row; ORM entrypoint completeness)

---

### 4. Bulk mutation (Ruler C — explicit call-out)

| matrix_id | Gap | Current | P-002 action |
|-----------|-----|---------|--------------|
| C-BULK-002 | **No test** for insert fallback strategy | Code: `XuguDialect#getFallbackSqmInsertStrategy` → `LocalTemporaryTableInsertStrategy` | **P-004 owner** for unit wiring test (mirror `XuguBulkMutationSupportTest` for mutation). Live bulk-insert IT **N/A** until GetGeneratedKeys/reserved-table blocker resolved — document waiver in P-002 evidence |

**Priority:** high for **unit wiring**; live IT deferred to P-004/P-005 per matrix note

---

### 5. Isolation & session config (P-008 domain bleed)

| matrix_id | Gap | Current | P-002 action |
|-----------|-----|---------|--------------|
| A-XCUT-005 | No isolation-level test | Documented in dialect javadoc only | Add unit: dialect isolation support hooks; optional gated JDBC `SET iso_level` smoke |
| A-XCUT-006 | **Negative-only — no test** | Not claimed in docs | **Defer assert to P-003** (negative bundle); list here because P-002 may add hook audit |

**Priority:** low for P-002; A-XCUT-006 → P-003

---

### 6. Functions (unit-only families — optional P-002 stretch)

Not counted as hard gaps (unit registration exists), but **no live HQL probe** for:

- A-FUN-003 (length/char_length), A-FUN-005 (trim), A-FUN-006 (replace/locate/position), A-FUN-007 (coalesce/nullif/nvl), A-FUN-008 (mod/power/sqrt), A-FUN-009 (round/floor)

**P-002 action (stretch):** extend `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` with one representative HQL per family — or accept unit-only for P-002 close.

**Priority:** low (stretch)

---

## Non-P-002 items (routed)

| Owner | Items |
|-------|-------|
| **P-003** | Negative bundle: A-PAG-005, A-LCK-004/005, A-SCH-007, A-XCUT-006, A-XCUT-010/011, C-DDL-004, C-SKIP-001 |
| **P-004** | C-BULK-002 unit wiring + bulk-insert live IT decision |
| **P-005** | Demo smoke expansion (see below) |
| **later** | All 延后 rows (20 A + 5 C) — no P-002 action |

---

## Demo smoke — P-005 gap list (cross-reference)

Current demo proves: **offline config** + **single CRUD + IDENTITY** (`DemoPersonCrudIT`).

| Consumer path | Matrix touch | Gap |
|---------------|--------------|-----|
| HQL pagination | A-PAG-001/002 | No demo Pageable/setFirstResult test |
| hbm2ddl validate | A-SEQ-001 | No demo validate scenario |
| JSON / functions | A-FUN-017, C-JSON-* | No demo HQL smoke |
| Spring Data repository beyond findById | A-XCUT-009 | Minimal |

**Recommendation:** P-005 adds 1–2 gated `@SpringBootTest` methods mirroring golden paths from `XuguHqlPaginationIT` and `XuguSchemaValidateIT`.

---

## C-BULK-002 detail (must-call-out)

```
Matrix: C-BULK-002 | Bulk multi-table insert fallback | 可实现 | P-005 (I-003)
Code:   XuguDialect#getFallbackSqmInsertStrategy() wired
Tests:  NONE (gap)
Related: XuguBulkMutationIT (update/delete only)
         XuguBulkMutationSupportTest (mutation flags only, not insert strategy)
Blocker: GetGeneratedKeys × reserved table names (see I-004 / matrix acceptance hint)
```

**Minimum P-002/P-004 close:** offline test that dialect returns non-null `LocalTemporaryTableInsertStrategy` when invoked — without live bulk insert.

---

## Negative assertion scope (P-003 input)

P-002 should **not** implement negatives; export list for P-003 worker:

1. **Missing today:** A-XCUT-006 (READ UNCOMMITTED not claimed)
2. **Consolidate existing scattered evidence:** A-PAG-005, A-LCK-004, A-LCK-005, A-SCH-007, C-DDL-004, C-SKIP-001
3. **Documentation-only:** A-XCUT-010, A-XCUT-011 (charter non-goals)

---

## Suggested P-002 Definition of Done

- [ ] Close 4 hard gaps: A-TYP-019, A-DDL-005, A-XCUT-001, C-EXC-002 (live path)
- [ ] Close 2 medium gaps: A-TYP-007 TIME IT, A-SCH-014 DROP CONSTRAINT IT
- [ ] Audit A-XCUT-005 (unit minimum)
- [ ] Hand off C-BULK-002 unit + waiver doc to P-004
- [ ] Hand off demo expansion list to P-005
- [ ] Hand off negative checklist to P-003

**Estimated P-002 test additions:** 6–8 methods across 3–4 existing IT/unit classes (no new Java required for research phase).

---

## Blockers

| Blocker | Impact |
|---------|--------|
| Live XuguDB unavailable in CI | IT rows skip via `Assumptions`; P-002 Accept needs gate ON evidence |
| C-BULK-002 live insert | Cannot close with live IT until GetGeneratedKeys/reserved-table strategy resolved |
| No castPattern/IdentifierHelper test hooks | Implementer must add package-visible or reflection-free public API asserts |
