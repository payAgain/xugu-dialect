# I-006 / P-001 — Boot-Required Gap Summary (RP-01)

> **Role:** researcher  
> **invocation_id:** `inv-i006-p001-rp01-researcher`  
> **Date:** 2026-07-18  
> **Audience:** architect-contract (RP-02), then P-002 / P-003 / P-004 implementers  
> **Source:** [`INVENTORY.md`](INVENTORY.md)  
> **Constraint:** C′ = Boot-required only — **not** full 94-row Boot mirror

---

## Executive summary

| Metric | Value |
|---|---:|
| Existing demo `@Test` methods | **7** |
| I-005 可实现 baseline rows | 94 |
| **Proposed Boot-required subset** | **41** |
| Already `covered` by demo | **8** |
| Open Boot `gap` rows | **33** |
| dialect-it-only exclusions (from 94) | **53** |
| negative-only / 延后 (never Boot) | 34 (I-005) |

**Verdict:** Inventory + subset sized correctly (~35–50). Gaps are actionable by Phase without inventing a 94-row Boot suite.

---

## Proposed Boot-required count by Layer

| Layer | Rows | Covered today | Gaps | gap_action Phase |
|---|---:|---:|---:|---|
| **A** golden path | 13 | 8 | 5 | **P-002** |
| **B** B-both | 9 | 0 | 9 | **P-003** |
| **C′** remaining Boot | 19 | 0 | 19 | **P-004** |
| **Total Boot-required** | **41** | **8** | **33** | — |

**Covered today (8):**  
`A-SPI-001`, `A-XCUT-003`, `A-XCUT-009`, `A-IDN-003`, `A-PAG-001`, `A-PAG-002`, `A-DDL-003`, `A-DDL-004`

**Layer A gaps (5):**  
`A-SPI-002`, `A-SPI-003`, `A-SEQ-001`, `A-DDL-001`, `A-IDN-004` (needs update+delete beyond persist/find)

---

## Gap buckets for P-002 / P-003 / P-004

### P-002 — Layer A (must close) — **5** SSOT gaps

| row_id | Gap | Suggested Boot evidence | Priority |
|---|---|---|---|
| A-SPI-002 | No Boot SPI-auto path | `@SpringBootTest` **without** explicit `hibernate.dialect`; assert resolved `XuguDialect` | high |
| A-SPI-003 | Same as SPI-auto product resolve | May **share one** test method with A-SPI-002 | high |
| A-SEQ-001 | No `ddl-auto=validate` Boot startup | Test profile: schema pre-created then `validate` starts clean | high |
| A-DDL-001 | Schema consumer surface under Boot | Assert EM/SF starts with `HIB_DEMO_*` tables (validate/update path) | medium |
| A-IDN-004 | Persist+find only | Extend CRUD: **update** + **delete** + re-find empty | high |

**Non-matrix P-002 behavioral gap:**

| Concern | Gap | Action |
|---|---|---|
| Startup-crud runner | `DemoStartupCrudRunner` never asserted; IT forces `startup-crud=false` | Gated test with `startup-crud=true` proving ApplicationRunner persist/find |
| Offline suite | Must stay green without live DB | Keep live cases behind `XuguIntegrationGate` |

**Out of P-002:** association / SEQUENCE / locks / UNIQUE exception / functions / JSON / bulk.

---

### P-003 — Layer B-both (must close) — **9** gaps

| row_id | Gap | Suggested Boot evidence | Priority |
|---|---|---|---|
| A-SCH-012 | No association entity | Person ↔ related entity with FK | high |
| A-SCH-011 | No UNIQUE on Boot model | Unique column + violation setup for C-EXC-001 | high |
| A-SEQ-003 | No SEQUENCE entity | `@GeneratedValue(SEQUENCE)` persist under Boot | high |
| A-SEQ-004 | No currval smoke | Post-NEXTVAL session consistency (or documented path) | medium |
| A-LCK-001 | No pessimistic lock | `LockModeType.PESSIMISTIC_WRITE` via Boot EM | high |
| A-LCK-003 | No NOWAIT/WAIT on Boot | One representative timeout/NOWAIT path | medium |
| C-EXC-001 | No UNIQUE → CVE | Assert `ConstraintViolationException` (or Spring translation) | high |
| A-XCUT-004 | No rollback assert | Force failure mid-tx; assert no durable row | high |
| C-EXC-002 | No NOT NULL name extract | Optional stretch with UNIQUE/NOT NULL suite | low |

**B-both reminder:** deliver **both** association **and** SEQUENCE entities (brief decision #3).

---

### P-004 — Layer C′ remaining (must close) — **19** gaps

| Domain | row_ids | Count | Notes |
|---|---|---:|---|
| Types (entity fields) | A-TYP-001, 002, 004, 005, 006, 008, 009, 010, 012, 013 | 10 | One entity (or small set) can close many rows |
| Functions (HQL subset) | A-FUN-001, 002, 004, 007, 010, 016, 017 | 7 | Representative probes — not full A-FUN-* |
| JSON optional | C-JSON-001 | 1 | May share A-TYP-013 column |
| Bulk one-shot | C-BULK-001 | 1 | **One** bulk update **or** delete only |

**Demo `@Test` budget:** Initiative target ~25–40 total. Today **7**; after P-002–P-004 expect roughly **+18–30** methods if consolidated (multi-assert / shared fixtures), staying ≤40. **Reject** 1:1 “one `@Test` per matrix row” if it trends toward a 94-row Boot mirror.

---

## Explicit exclusions (do not Boot-mirror)

1. **Pure dialect SPI/hooks** — e.g. A-SPI-004, A-IDN-001/002, A-SEQ-002/005, A-XCUT-001/002/005/007/008, A-TYP-019, A-PAG-003, A-LCK-002, A-SCH-004..010 / 013..016, C-DDL-*, C-CAT-001, C-GUID-001, C-BULK-002/003, C-WIN-001, C-CTE-001, remaining A-FUN-* / A-TYP-* not in Boot-required list.  
2. **I-005 negative-only / 延后 (34)** — dialect negative suite only.  
3. **C-BULK-002** — keep `known-limit-documented`; no Boot bulk-insert requirement.  
4. **Full 94-row Boot mirror** — out of scope (brief decisions #2 / #4).

Full exclusion detail: INVENTORY §4.

---

## Suggested columns for architect SSOT

File: `contracts/consumer-path-baseline.md`

```text
row_id | layer | status | entry_class#method | gate | gap_action | i005_xref
```

| Column | Allowed values |
|---|---|
| `row_id` | I-005 matrix_id |
| `layer` | `A` \| `B` \| `C′` |
| `status` | `covered` \| `gap` |
| `entry_class#method` | Demo `Class#method` (comma-separated if shared) |
| `gate` | `demo` (offline or gated IT) |
| `gap_action` | `P-002` \| `P-003` \| `P-004` \| `—` |
| `i005_xref` | Same matrix_id (or multi-xref note) |

**Appendix (required):** dialect-it-only exclusion list (INVENTORY §4) — those rows must **not** appear as Boot `gap` in the consumer-path SSOT.

---

## Gap counts (orchestrator return)

| Owner Phase | Open Boot-required gaps |
|---|---:|
| **P-002** (Layer A) | **5** (+ startup-crud behavioral) |
| **P-003** (Layer B) | **9** |
| **P-004** (Layer C′) | **19** |
| **Total open SSOT gaps** | **33** |

---

## Blockers

| Item | Severity | Notes |
|---|---|---|
| Live XuguDB for Accept | expected | Gaps close with gated IT; offline `mvn test` must stay green |
| None for RP-01 docs | — | Research complete; no Java changes required |
| Scope creep risk | medium | Reviewer (RP-03) must reject any SSOT that silently expands to 94 Boot rows |

**No research blockers** for architect-contract RP-02 to publish `contracts/consumer-path-baseline.md`.
