# I-006 / P-001 — Consumer-Path Boot Inventory (RP-01)

> **Role:** researcher  
> **invocation_id:** `inv-i006-p001-rp01-researcher`  
> **Date:** 2026-07-18  
> **Branch:** `feat/i-006-consumer-path-coverage`  
> **Inputs:** `harness/initiatives/I-006/brief.md`, `contracts/production-regression-baseline.md` (I-005 SSOT), `contracts/feature-matrix-definition-a.md`, `contracts/feature-matrix-i003-ruler-c.md`, `demo-spring-boot/src/test/java/**`, sample `dialect/src/test/java/**`, prior `harness/evidence/researcher/I-005/P-001/{INVENTORY,GAP-SUMMARY}.md`  
> **Scope:** propose **Boot-required** consumer-path subset (~35–50), **not** a full 94-row Boot mirror  
> **IT gate (demo):** `XuguIntegrationGate.isEnabled()` ← env `XUGU_RUN_IT=true` or `-Dxugu.run.integration=true`

---

## 1. Existing demo `@Test` inventory (verified)

| # | Class#method | Gate | Kind | What it proves |
|---|---|---|---|---|
| 1 | `DemoOfflineSmokeTest#demoPersonTableUsesHibDemoPrefix` | unit / offline | demo | Entity `@Table` uses `HIB_DEMO_` prefix |
| 2 | `DemoOfflineSmokeTest#applicationYmlDocumentsExplicitDialectAndEnvKeys` | unit / offline | demo | `application.yml` documents `XuguDialect`, `XUGU_JDBC_URL`, `compatiblemode=NONE`, Xugu JDBC driver |
| 3 | `DemoPersonCrudIT#persistAndFindPerson` | gated IT | demo | Spring Boot JPA `save` + `findById`; IDENTITY id backfill |
| 4 | `DemoBootBaselineSmokeTest#sessionFactoryUsesExplicitXuguDialectFromApplicationYml` | gated IT | demo | Explicit dialect property + live `SessionFactory` uses `XuguDialect` |
| 5 | `DemoBootBaselineSmokeTest#datasourceUrlIncludesCompatibleModeNone` | gated IT | demo | JDBC URL contains `compatiblemode=NONE`; pooled connection valid |
| 6 | `DemoBootBaselineSmokeTest#jpaPersistAndJpqlQueryRoundTrip` | gated IT | demo | Repository persist + JPQL `select` round-trip |
| 7 | `DemoBootBaselineSmokeTest#pageableFindAllUsesLimitOffset` | gated IT | demo | Spring Data `Pageable` → LIMIT/OFFSET consumer path |

**Demo `@Test` count:** **7** (across 3 test classes).  
**Non-test support:** `XuguIntegrationGate` (gate helper only — no `@Test`).

### Demo main-code anchors (not tests)

| Component | Role | Boot-path note |
|---|---|---|
| `DemoStartupCrudRunner` | `ApplicationRunner` when `xugu.demo.startup-crud=true` (default) | Live startup persist/find exists in main; **no** `@Test` asserts it (IT currently forces `startup-crud=false`) |
| `DemoPerson` + `DemoPersonRepository` | IDENTITY entity + Spring Data repo | Only Person; no association / SEQUENCE entity yet |
| `application.yml` | `ddl-auto: update`, explicit dialect | Layer A wants **`validate` startup** path — currently **gap** |

---

## 2. Layer tag legend (I-006)

| Tag | Meaning | Phase owner for gaps |
|---|---|---|
| **A** | Golden Boot path: validate startup, startup-crud, full CRUD, JPQL+Pageable, optional SPI without explicit dialect | **P-002** |
| **B** | B-both: association entity + SEQUENCE entity; pessimistic lock; UNIQUE exception shape; transaction rollback | **P-003** |
| **C′** | Remaining Boot-required SSOT rows: function subset, optional JSON, one bulk update/delete, representative type fields | **P-004** |
| **dialect-it-only** | Pure dialect SPI / unit / dialect-IT hooks — **exclude** from Boot-required SSOT (stay in I-005 dialect suite) | — (no Boot gap_action) |

**Status (consumer-path):** `covered` = existing demo `@Test` entrypoint; `gap` = Boot-required but no demo entry yet; `dialect-it-only` = not Boot-required.

---

## 3. Proposed Boot-required subset (crosswalk I-005)

**Proposed Boot-required count: 41** (within ~35–50).  
Columns for architect SSOT draft: `row_id | layer | status | entry_class#method | gate | gap_action | i005_xref`  
(`row_id` = I-005 `matrix_id`; `i005_xref` repeats matrix_id for clarity / future CP-* aliases.)  
**Uniqueness:** each `row_id` appears once (primary Layer owner). Cross-Layer notes go in comments / GAP-SUMMARY.

### 3.1 Layer A — golden path (13)

| row_id | layer | status | entry_class#method | gate | gap_action | i005_xref |
|---|---|---|---|---|---|---|
| A-SPI-001 | A | covered | `DemoBootBaselineSmokeTest#sessionFactoryUsesExplicitXuguDialectFromApplicationYml` | demo | — | A-SPI-001 |
| A-SPI-002 | A | gap | — (need Boot context **without** explicit `hibernate.dialect`, SPI resolve) | demo | **P-002** | A-SPI-002 |
| A-SPI-003 | A | gap | — (may share SPI-auto Boot test with A-SPI-002) | demo | **P-002** | A-SPI-003 |
| A-XCUT-003 | A | covered | `DemoOfflineSmokeTest#applicationYmlDocumentsExplicitDialectAndEnvKeys`; `DemoBootBaselineSmokeTest#datasourceUrlIncludesCompatibleModeNone` | demo | — | A-XCUT-003 |
| A-XCUT-009 | A | covered | `DemoOfflineSmokeTest#*`; `DemoPersonCrudIT#persistAndFindPerson`; `DemoBootBaselineSmokeTest#sessionFactoryUsesExplicitXuguDialectFromApplicationYml`; `#jpaPersistAndJpqlQueryRoundTrip`; `#pageableFindAllUsesLimitOffset` | demo | — | A-XCUT-009 |
| A-IDN-003 | A | covered | `DemoPersonCrudIT#persistAndFindPerson` (id backfill) | demo | — | A-IDN-003 |
| A-IDN-004 | A | gap | `DemoPersonCrudIT#persistAndFindPerson` covers **insert+find only**; need **update + delete** for “完整 CRUD” | demo | **P-002** | A-IDN-004 |
| A-PAG-001 | A | covered | `DemoBootBaselineSmokeTest#pageableFindAllUsesLimitOffset` | demo | — | A-PAG-001 |
| A-PAG-002 | A | covered | `DemoBootBaselineSmokeTest#pageableFindAllUsesLimitOffset` | demo | — | A-PAG-002 |
| A-SEQ-001 | A | gap | — (Boot **`ddl-auto=validate`** startup with sequence metadata visible) | demo | **P-002** | A-SEQ-001 |
| A-DDL-001 | A | gap | — (validate/update schema path proves CREATE TABLE consumer surface under Boot) | demo | **P-002** | A-DDL-001 |
| A-DDL-003 | A | covered | Implied by `DemoPerson` PK + persist IT | demo | — | A-DDL-003 |
| A-DDL-004 | A | covered | Implied by `DemoPerson.name` `nullable=false` + persist IT | demo | — | A-DDL-004 |

**Layer A consumer-path notes (not separate matrix rows, but P-002 must close):**

| Concern | Current | gap_action |
|---|---|---|
| `DemoStartupCrudRunner` live path | Main code exists; IT disables it; **no `@Test`** | **P-002** |
| JPQL (beyond matrix) | Covered by `#jpaPersistAndJpqlQueryRoundTrip` | — |
| Offline config smoke | Covered by `DemoOfflineSmokeTest` (2 methods) | — |

### 3.2 Layer B — B-both model expansion (9)

| row_id | layer | status | entry_class#method | gate | gap_action | i005_xref |
|---|---|---|---|---|---|---|
| A-SEQ-003 | B | gap | — (SEQUENCE-generated entity persist under Boot) | demo | **P-003** | A-SEQ-003 |
| A-SEQ-004 | B | gap | — (currval / post-NEXTVAL session smoke via SEQUENCE entity) | demo | **P-003** | A-SEQ-004 |
| A-SCH-011 | B | gap | — (UNIQUE on association/person graph) | demo | **P-003** | A-SCH-011 |
| A-SCH-012 | B | gap | — (FK association entity; child IDENTITY reuses A-IDN-003/004 capability) | demo | **P-003** | A-SCH-012 |
| A-LCK-001 | B | gap | — (JPA `PESSIMISTIC_WRITE` / `FOR UPDATE` via Boot EM/repo) | demo | **P-003** | A-LCK-001 |
| A-LCK-003 | B | gap | — (NOWAIT or WAIT timeout on Boot lock path — one representative) | demo | **P-003** | A-LCK-003 |
| C-EXC-001 | B | gap | — (UNIQUE violation → `ConstraintViolationException` on Boot path) | demo | **P-003** | C-EXC-001 |
| A-XCUT-004 | B | gap | — (transaction **rollback** observable under Boot `@Transactional`) | demo | **P-003** | A-XCUT-004 |
| C-EXC-002 | B | gap | — (optional: NOT NULL / constraint-name extract on Boot path) | demo | **P-003** | C-EXC-002 |

### 3.3 Layer C′ — remaining Boot-required entries (19)

| row_id | layer | status | entry_class#method | gate | gap_action | i005_xref |
|---|---|---|---|---|---|---|
| A-TYP-001 | C′ | gap | — (integer field on demo entity) | demo | **P-004** | A-TYP-001 |
| A-TYP-002 | C′ | gap | — (decimal/numeric field) | demo | **P-004** | A-TYP-002 |
| A-TYP-004 | C′ | gap | — (varchar already partial via `name`; keep as typed assert) | demo | **P-004** | A-TYP-004 |
| A-TYP-005 | C′ | gap | — (boolean field) | demo | **P-004** | A-TYP-005 |
| A-TYP-006 | C′ | gap | — (date field) | demo | **P-004** | A-TYP-006 |
| A-TYP-008 | C′ | gap | — (timestamp field) | demo | **P-004** | A-TYP-008 |
| A-TYP-012 | C′ | gap | — (UUID/GUID field) | demo | **P-004** | A-TYP-012 |
| A-TYP-013 | C′ | gap | — (JSON column field) | demo | **P-004** | A-TYP-013 |
| A-FUN-001 | C′ | gap | — (HQL string concat family smoke) | demo | **P-004** | A-FUN-001 |
| A-FUN-002 | C′ | gap | — (substring / left-right family — one probe) | demo | **P-004** | A-FUN-002 |
| A-FUN-004 | C′ | gap | — (lower/upper) | demo | **P-004** | A-FUN-004 |
| A-FUN-010 | C′ | gap | — (current_date / current_timestamp) | demo | **P-004** | A-FUN-010 |
| A-FUN-016 | C′ | gap | — (uuid() / GUID function) | demo | **P-004** | A-FUN-016 |
| A-FUN-017 | C′ | gap | — (json_value subset via HQL) | demo | **P-004** | A-FUN-017 |
| C-JSON-001 | C′ | gap | — (optional JSON round-trip / one aggregate — may share entity with A-TYP-013) | demo | **P-004** | C-JSON-001 |
| C-BULK-001 | C′ | gap | — (**one** HQL bulk update **or** delete via Boot EM) | demo | **P-004** | C-BULK-001 |
| A-TYP-009 | C′ | gap | — (optional small BINARY/VARBINARY field) | demo | **P-004** | A-TYP-009 |
| A-TYP-010 | C′ | gap | — (optional BLOB — one LOB representative; pick 010 **or** 011) | demo | **P-004** | A-TYP-010 |
| A-FUN-007 | C′ | gap | — (coalesce/nullif consumer probe) | demo | **P-004** | A-FUN-007 |

---

## 4. Explicit exclusions — dialect-it-only (not Boot SSOT)

These remain covered by dialect unit/IT under I-005 `production-regression-baseline.md`. **Do not** mirror as Boot-required rows.

### 4.1 Pure SPI / dialect wiring / unit hooks

| matrix_id | Why dialect-it-only |
|---|---|
| A-SPI-004 | Resolver non-match for MySQL/Oracle/PG — dialect unit, not Boot consumer |
| A-IDN-001 | IdentityColumnSupport DDL string wiring |
| A-IDN-002 | No AUTO_INCREMENT emit — dialect unit |
| A-SEQ-002 | DROP SEQUENCE string wiring |
| A-SEQ-005 | Sequence options mapping — dialect unit |
| A-XCUT-001 | IdentifierHelper UPPER fold — dialect unit |
| A-XCUT-002 | Quote character — dialect unit |
| A-XCUT-005 | Isolation-level hooks — dialect unit |
| A-XCUT-007 | TCL keywords registry — dialect unit |
| A-XCUT-008 | SequenceSupport wiring flag — dialect unit |
| A-TYP-003 | REAL→float mapping — dialect unit sufficient |
| A-TYP-007 | TIME JDBC — dialect IT (`XuguTypeRoundTripIT`) |
| A-TYP-011 | CLOB — dialect IT (if A-TYP-010 chosen as Boot LOB) |
| A-TYP-019 | `castPattern` unit |
| A-DDL-002 | ALTER ADD COLUMN — dialect schema-export IT |
| A-DDL-005 | DEFAULT column exporter — dialect unit |
| A-DDL-006 | DROP TABLE — dialect IT |
| A-PAG-003 | Limit bind-marker internals — dialect unit/IT |
| A-LCK-002 | `FOR UPDATE OF` — dialect lock IT |

### 4.2 Schema / temp / comment / advanced DDL (dialect IT)

| matrix_id | Why dialect-it-only |
|---|---|
| A-SCH-001, A-SCH-002 | Schema create/drop commands — dialect IT |
| A-SCH-004, A-SCH-005, A-SCH-006 | Local/global temp strategies — dialect SPI |
| A-SCH-008, A-SCH-009, A-SCH-010 | COMMENT ON / inline — dialect IT |
| A-SCH-013, A-SCH-014, A-SCH-015, A-SCH-016 | CHECK / DROP CONSTRAINT / truncate / index — dialect IT |
| C-DDL-001, C-DDL-002, C-DDL-003 | IF NOT EXISTS / alter column type / datetime literal — dialect IT |
| C-CAT-001 | Catalog create/drop — dialect IT (`supportsCatalogs` false elsewhere) |
| C-GUID-001 | `selectGuidString` dialect helper — dialect IT (Boot uses A-TYP-012 field instead) |

### 4.3 Function families covered by dialect IT only (not every row Boot)

| matrix_id | Why dialect-it-only |
|---|---|
| A-FUN-003, A-FUN-005, A-FUN-006, A-FUN-008, A-FUN-009 | Unit/IT registration; Boot takes representative subset above |
| A-FUN-011, A-FUN-012, A-FUN-013, A-FUN-014, A-FUN-018 | Dialect `XuguFunctionRegistryIT` / listagg — not all required on Boot |

### 4.4 Ruler C advanced / known-limit / flags

| matrix_id | Why dialect-it-only |
|---|---|
| C-JSON-002, C-JSON-003, C-JSON-004 | Extra JSON agg variants — dialect IT; Boot keeps C-JSON-001 optional |
| C-WIN-001, C-CTE-001 | Window/CTE — dialect IT (`XuguWindowCteIT`); out of C′ Boot scope |
| C-BULK-002 | Bulk **insert** fallback — `known-limit-documented`; unit wiring only |
| C-BULK-003 | `supportsSubqueryOnMutatingTable` flag — dialect unit/IT |
| C-LOCK-001 | Audit anchor for NOWAIT/WAIT flags — dialect (Boot uses A-LCK-003) |

### 4.5 Negative-only / 延后 (I-005) — never Boot-required

All **34** negative-only / deferred rows in I-005 baseline (e.g. A-PAG-005, A-LCK-004/005, A-SCH-007, A-XCUT-006/010/011, C-SKIP-001, C-DDL-004, deferred types/functions) stay **dialect negative suite** — **not** Boot SSOT rows.

### 4.6 Count check vs 94 可实现

| Bucket | Count |
|---|---:|
| I-005 可实现 rows | 94 |
| **Proposed Boot-required (A+B+C′)** | **41** (13 A + 9 B + 19 C′) |
| Remaining 可实现 → dialect-it-only (or non-Boot) | 53 |
| Full 94 Boot mirror? | **No** |

---

## 5. Demo ↔ I-005 coverage snapshot (today)

| I-005 touch | Demo evidence | Boot status |
|---|---|---|
| A-SPI-001 | `DemoBootBaselineSmokeTest#sessionFactoryUsesExplicitXuguDialectFromApplicationYml` | covered |
| A-XCUT-003 | offline yml + `#datasourceUrlIncludesCompatibleModeNone` | covered |
| A-XCUT-009 | offline + CRUD IT + Boot smoke suite | covered (partial vs Layer A ambitions) |
| A-IDN-003 / A-IDN-004 | `DemoPersonCrudIT#persistAndFindPerson` | covered insert/find; **CRUD update/delete gap** |
| A-PAG-001 / A-PAG-002 | `#pageableFindAllUsesLimitOffset` | covered |
| A-SEQ-001 validate | — | **gap** |
| A-SPI-002 / A-SPI-003 SPI-auto | — | **gap** |
| Layer B / C′ rows | — | **all gap** |

---

## 6. Suggested architect SSOT columns

Publish `contracts/consumer-path-baseline.md` with:

```text
row_id | layer | status | entry_class#method | gate | gap_action | i005_xref
```

| Column | Values |
|---|---|
| `row_id` | I-005 `matrix_id` (e.g. `A-PAG-002`) |
| `layer` | `A` \| `B` \| `C′` |
| `status` | `covered` \| `gap` \| (`known-limit-documented` only if ever needed) |
| `entry_class#method` | Demo test entrypoint(s); empty when `gap` |
| `gate` | `demo` (offline or gated); never invent dialect gate as Boot evidence |
| `gap_action` | `P-002` \| `P-003` \| `P-004` \| `—` |
| `i005_xref` | Same as `row_id` (or list if one Boot test closes multiple) |

**Separate appendix (not Boot-required table):** `dialect-it-only` exclusions (§4) — reviewer must confirm these are **not** silently added as Boot gaps.

---

## 7. Handoff to architect-contract (RP-02)

1. Promote §3 tables into `contracts/consumer-path-baseline.md` (41 Boot-required rows).  
2. Keep §4 exclusions explicit.  
3. Do **not** invent rows without I-005 / matrix xref.  
4. Gap routing: see sibling [`GAP-SUMMARY.md`](GAP-SUMMARY.md).
