# Consumer-Path Baseline (Boot-Required SSOT)

> **Status:** **FROZEN** for Initiative Accept prep (P-005) — Layer A/B/C′ **accepted**; Boot-required open gaps = **0**; docs ↔ SSOT ↔ Demo aligned  
> **Initiative:** I-006 — Spring Boot consumer-path coverage  
> **Author role:** architect-contract (P-001); Layers A/B/C′ by implementer (P-002…P-004); docs freeze by implementer (P-005); verification by test; Accept by orchestrator  
> **invocation_id:** `inv-i006-p005-rp01-implementer` (docs freeze); prior Accept `inv-i006-p004-rp03-reviewer`  

> **Sources:** [`production-regression-baseline.md`](production-regression-baseline.md) (I-005), [`harness/evidence/researcher/I-006/P-001/INVENTORY.md`](../harness/evidence/researcher/I-006/P-001/INVENTORY.md), [`GAP-SUMMARY.md`](../harness/evidence/researcher/I-006/P-001/GAP-SUMMARY.md), [`harness/initiatives/I-006/brief.md`](../harness/initiatives/I-006/brief.md)  
> **User guide:** [`docs/user-guide/06-consumer-path.md`](../docs/user-guide/06-consumer-path.md) · pointer [`docs/consumer-path-baseline.md`](../docs/consumer-path-baseline.md)  
> **Scope:** **Boot-required** consumer-path subset only — **41** rows (Layer A=13 / B=9 / C′=19). **Not** a full 94-row Boot mirror of I-005 可实现.  
> **GAV:** `com.xugu:xugu-dialect:7.4.5.Final` (document only; no Ship / bump in this Initiative)  
> **Demo IT gate:** `XuguIntegrationGate.isEnabled()` ← env `XUGU_RUN_IT=true` or JVM `-Dxugu.run.integration=true`  
> **Ownership note:** file listed under demo-spring-boot in `OWNERSHIP.yaml`; **writer** for contracts remains architect-contract; P-005 may only update freeze-status notes

## Purpose

Define the Spring Boot **consumer-path regression surface**: which I-005 matrix rows must have a **demo-spring-boot** entrypoint (offline unit or gated `@SpringBootTest`), tagged by Layer A / B / C′ and Phase `gap_action`. Pure dialect SPI / unit / dialect-IT hooks stay in I-005 dialect suites and are **explicitly excluded** below.

## Status legend (consumer-path)

| status | Meaning |
|---|---|
| **covered** | At least one verified **demo** `@Test` entrypoint exists for this Boot-required row |
| **gap** | Boot-required, but no demo entrypoint yet (or existing demo only partially satisfies the Layer ambition) |
| **dialect-it-only** | Not Boot-required — covered by dialect unit/IT under I-005; appears **only** in the exclusion appendix |

## Layer legend

| layer | Meaning | Gap owner Phase |
|---|---|---|
| **A** | Golden Boot path: validate startup, startup-crud, full CRUD, JPQL+Pageable, optional SPI without explicit dialect | **P-002** |
| **B** | B-both: association entity + SEQUENCE entity; pessimistic lock; UNIQUE exception shape; transaction rollback | **P-003** |
| **C′** | Remaining Boot-required SSOT rows: function subset, optional JSON, one bulk update/delete, representative type fields | **P-004** |

## Gate legend

| gate | Meaning |
|---|---|
| **demo** | `demo-spring-boot` smoke — offline unit **or** gated IT (`XuguIntegrationGate`) |
| **—** | No demo entrypoint yet (`gap` rows) |

## gap_action legend

| gap_action | Meaning |
|---|---|
| **P-002** | Close Layer A Boot golden-path gap |
| **P-003** | Close Layer B B-both model / lock / exception / rollback gap |
| **P-004** | Close Layer C′ remaining Boot-required gap |
| **—** | Already covered; no Phase owner |
| **defer** | Justified deferral (must include rationale; none in primary 41 at publish) |

## Column schema

```text
row_id | layer(A|B|C′) | status(covered|gap|dialect-it-only) | entry_class#method | gate | gap_action | i005_xref
```

| Column | Values |
|---|---|
| `row_id` | I-005 `matrix_id` (primary key; unique in Boot-required table) |
| `layer` | `A` \| `B` \| `C′` |
| `status` | `covered` \| `gap` (primary table); `dialect-it-only` only in exclusion appendix |
| `entry_class#method` | Demo `Class#method` (comma-separated if shared); empty when `gap` |
| `gate` | `demo` when covered; `—` when gap |
| `gap_action` | `P-002` \| `P-003` \| `P-004` \| `—` \| justified `defer` |
| `i005_xref` | Same as `row_id` (cross-ref to [`production-regression-baseline.md`](production-regression-baseline.md)) |

**Uniqueness:** each `row_id` appears once in the Boot-required table (primary Layer owner). Cross-Layer notes go in comments / non-matrix section.

---

## Count summary

| Bucket | Count |
|---|---:|
| **Boot-required (primary SSOT)** | **41** |
| Layer A | 13 |
| Layer B | 9 |
| Layer C′ | 19 |
| **covered** | **41** |
| **gap** | **0** |
| I-005 可实现 (reference) | 94 |
| Remaining 可实现 → dialect-it-only / non-Boot | 53 |
| I-005 negative-only / 延后 (never Boot) | 34 |
| Full 94-row Boot mirror? | **No** |

### Covered today (41)

Layer A (13): `A-SPI-001`, `A-SPI-002`, `A-SPI-003`, `A-XCUT-003`, `A-XCUT-009`, `A-IDN-003`, `A-IDN-004`, `A-PAG-001`, `A-PAG-002`, `A-SEQ-001`, `A-DDL-001`, `A-DDL-003`, `A-DDL-004`

Layer B (9): `A-SEQ-003`, `A-SEQ-004`, `A-SCH-011`, `A-SCH-012`, `A-LCK-001`, `A-LCK-003`, `C-EXC-001`, `A-XCUT-004`, `C-EXC-002`

Layer C′ (19): `A-TYP-001`, `A-TYP-002`, `A-TYP-004`, `A-TYP-005`, `A-TYP-006`, `A-TYP-008`, `A-TYP-009`, `A-TYP-010`, `A-TYP-012`, `A-TYP-013`, `A-FUN-001`, `A-FUN-002`, `A-FUN-004`, `A-FUN-007`, `A-FUN-010`, `A-FUN-016`, `A-FUN-017`, `C-JSON-001`, `C-BULK-001`

### Gap routing

| gap_action | Open Boot-required gaps |
|---|---:|
| **P-002** (Layer A) | **0** |
| **P-003** (Layer B) | **0** |
| **P-004** (Layer C′) | **0** |
| **Total open gaps** | **0** |

---

## SSOT — Boot-required rows (41)

### Layer A — golden path (13)

| row_id | layer | status | entry_class#method | gate | gap_action | i005_xref |
|---|---|---|---|---|---|---|
| A-SPI-001 | A | covered | `DemoBootBaselineSmokeTest#sessionFactoryUsesExplicitXuguDialectFromApplicationYml` | demo | — | A-SPI-001 |
| A-SPI-002 | A | covered | `DemoSpiDialectAutoResolveIT#sessionFactoryResolvesXuguDialectWithoutExplicitConfig`; `DemoOfflineSmokeTest#dialectResolverServicesFileOnClasspath` | demo | — | A-SPI-002 |
| A-SPI-003 | A | covered | `DemoSpiDialectAutoResolveIT#sessionFactoryResolvesXuguDialectWithoutExplicitConfig` | demo | — | A-SPI-003 |
| A-XCUT-003 | A | covered | `DemoOfflineSmokeTest#applicationYmlDocumentsExplicitDialectAndEnvKeys`; `DemoBootBaselineSmokeTest#datasourceUrlIncludesCompatibleModeNone` | demo | — | A-XCUT-003 |
| A-XCUT-009 | A | covered | `DemoOfflineSmokeTest#demoPersonTableUsesHibDemoPrefix`; `DemoOfflineSmokeTest#applicationYmlDocumentsExplicitDialectAndEnvKeys`; `DemoPersonCrudIT#persistAndFindPerson`; `DemoBootBaselineSmokeTest#sessionFactoryUsesExplicitXuguDialectFromApplicationYml`; `#jpaPersistAndJpqlQueryRoundTrip`; `#pageableFindAllUsesLimitOffset`; `#pageableSecondPageUsesOffset` | demo | — | A-XCUT-009 |
| A-IDN-003 | A | covered | `DemoPersonCrudIT#persistAndFindPerson` (IDENTITY id backfill) | demo | — | A-IDN-003 |
| A-IDN-004 | A | covered | `DemoPersonCrudIT#updateAndDeletePerson` | demo | — | A-IDN-004 |
| A-PAG-001 | A | covered | `DemoBootBaselineSmokeTest#pageableFindAllUsesLimitOffset`; `#pageableSecondPageUsesOffset` | demo | — | A-PAG-001 |
| A-PAG-002 | A | covered | `DemoBootBaselineSmokeTest#pageableFindAllUsesLimitOffset`; `#pageableSecondPageUsesOffset` | demo | — | A-PAG-002 |
| A-SEQ-001 | A | covered | `DemoValidateStartupIT#validateStartupSucceedsWithPreCreatedSchema` | demo | — | A-SEQ-001 |
| A-DDL-001 | A | covered | `DemoSchemaSurfaceIT#hibDemoPersonTableExistsAfterUpdateStartup` | demo | — | A-DDL-001 |
| A-DDL-003 | A | covered | Implied by `DemoPerson` PK + `DemoPersonCrudIT#persistAndFindPerson` | demo | — | A-DDL-003 |
| A-DDL-004 | A | covered | Implied by `DemoPerson.name` `nullable=false` + persist IT | demo | — | A-DDL-004 |

**Layer A non-matrix behavioral (closed in P-002; not a separate matrix row):**

| Concern | Current | gap_action |
|---|---|---|
| `DemoStartupCrudRunner` live path | Covered by `DemoStartupCrudIT#startupCrudRunnerPersistsAndFindsPerson` (`startup-crud=true`) | — |
| JPQL select round-trip | Covered by `DemoBootBaselineSmokeTest#jpaPersistAndJpqlQueryRoundTrip` | — |
| Offline config smoke | Covered by `DemoOfflineSmokeTest` (3 methods); must stay green without live DB | — |

### Layer B — B-both model expansion (9)

| row_id | layer | status | entry_class#method | gate | gap_action | i005_xref |
|---|---|---|---|---|---|---|
| A-SEQ-003 | B | covered | `DemoSequenceIT#persistSequenceTicketGeneratesIncreasingIds` | demo | — | A-SEQ-003 |
| A-SEQ-004 | B | covered | `DemoSequenceIT#currvalMatchesLastGeneratedIdInSession` | demo | — | A-SEQ-004 |
| A-SCH-011 | B | covered | `DemoOfflineSmokeTest#layerBEntitiesUseHibDemoPrefixAndSequence`; `DemoConstraintRollbackIT#uniqueViolationMapsToConstraintViolationException` | demo | — | A-SCH-011 |
| A-SCH-012 | B | covered | `DemoAssociationIT#persistDeptWithMembersAndFindViaFk` | demo | — | A-SCH-012 |
| A-LCK-001 | B | covered | `DemoLockIT#pessimisticWriteLocksPersonRow` | demo | — | A-LCK-001 |
| A-LCK-003 | B | covered | `DemoLockIT#pessimisticWriteWithNowaitTimeoutExecutes` | demo | — | A-LCK-003 |
| C-EXC-001 | B | covered | `DemoConstraintRollbackIT#uniqueViolationMapsToConstraintViolationException` | demo | — | C-EXC-001 |
| A-XCUT-004 | B | covered | `DemoConstraintRollbackIT#forcedFailureRollsBackDurableMemberRow` | demo | — | A-XCUT-004 |
| C-EXC-002 | B | covered | `DemoConstraintRollbackIT#notNullViolationExtractsConstraintNameWhenPresent` | demo | — | C-EXC-002 |

**B-both reminder (brief decision #3):** deliver **both** association entity **and** SEQUENCE entity — **both landed** (`DemoDept`/`DemoDeptMember` + `DemoSeqTicket`).

### Layer C′ — remaining Boot-required entries (19)

| row_id | layer | status | entry_class#method | gate | gap_action | i005_xref |
|---|---|---|---|---|---|---|
| A-TYP-001 | C′ | covered | `DemoTypesIT#typedSampleRoundTripKeyTypes` | demo | — | A-TYP-001 |
| A-TYP-002 | C′ | covered | `DemoTypesIT#typedSampleRoundTripKeyTypes` | demo | — | A-TYP-002 |
| A-TYP-004 | C′ | covered | `DemoTypesIT#typedSampleRoundTripKeyTypes` | demo | — | A-TYP-004 |
| A-TYP-005 | C′ | covered | `DemoTypesIT#typedSampleRoundTripKeyTypes` | demo | — | A-TYP-005 |
| A-TYP-006 | C′ | covered | `DemoTypesIT#typedSampleRoundTripKeyTypes` | demo | — | A-TYP-006 |
| A-TYP-008 | C′ | covered | `DemoTypesIT#typedSampleRoundTripKeyTypes` | demo | — | A-TYP-008 |
| A-TYP-009 | C′ | covered | `DemoTypesIT#typedSampleRoundTripKeyTypes` | demo | — | A-TYP-009 |
| A-TYP-010 | C′ | covered | `DemoTypesIT#typedSampleRoundTripKeyTypes` (BLOB LOB; A-TYP-011 CLOB stays dialect-it-only) | demo | — | A-TYP-010 |
| A-TYP-012 | C′ | covered | `DemoTypesIT#typedSampleRoundTripKeyTypes` | demo | — | A-TYP-012 |
| A-TYP-013 | C′ | covered | `DemoJsonIT#jsonColumnRoundTripAndArrayAgg` | demo | — | A-TYP-013 |
| A-FUN-001 | C′ | covered | `DemoFunctionsIT#hqlFunctionSubsetSmoke` | demo | — | A-FUN-001 |
| A-FUN-002 | C′ | covered | `DemoFunctionsIT#hqlFunctionSubsetSmoke` | demo | — | A-FUN-002 |
| A-FUN-004 | C′ | covered | `DemoFunctionsIT#hqlFunctionSubsetSmoke` | demo | — | A-FUN-004 |
| A-FUN-007 | C′ | covered | `DemoFunctionsIT#hqlFunctionSubsetSmoke` | demo | — | A-FUN-007 |
| A-FUN-010 | C′ | covered | `DemoFunctionsIT#hqlFunctionSubsetSmoke` | demo | — | A-FUN-010 |
| A-FUN-016 | C′ | covered | `DemoFunctionsIT#hqlFunctionSubsetSmoke` | demo | — | A-FUN-016 |
| A-FUN-017 | C′ | covered | `DemoFunctionsIT#hqlFunctionSubsetSmoke` (shared `DemoJsonDoc`) | demo | — | A-FUN-017 |
| C-JSON-001 | C′ | covered | `DemoJsonIT#jsonColumnRoundTripAndArrayAgg` | demo | — | C-JSON-001 |
| C-BULK-001 | C′ | covered | `DemoBulkMutationIT#bulkUpdatePersonNames` (bulk update only; delete not duplicated) | demo | — | C-BULK-001 |

**C′ implementer note:** consolidated multi-assert / shared fixtures so total demo `@Test` stays ~25–40. **Reject** 1:1 “one `@Test` per matrix row” if it trends toward a 94-row Boot mirror. LOB pick: **A-TYP-010** (BLOB); **A-TYP-011** remains dialect-it-only.

---

## Explicit exclusions — dialect-it-only / pure SPI hooks (not Boot SSOT)

These remain covered by dialect unit/IT under [`production-regression-baseline.md`](production-regression-baseline.md). **Do not** add them as Boot `gap` rows. Status for all rows in this appendix: **`dialect-it-only`**.

### Pure SPI / dialect wiring / unit hooks

| row_id | status | Why dialect-it-only | i005_xref |
|---|---|---|---|
| A-SPI-004 | dialect-it-only | Resolver non-match for MySQL/Oracle/PG — dialect unit | A-SPI-004 |
| A-IDN-001 | dialect-it-only | IdentityColumnSupport DDL string wiring | A-IDN-001 |
| A-IDN-002 | dialect-it-only | No AUTO_INCREMENT emit — dialect unit | A-IDN-002 |
| A-SEQ-002 | dialect-it-only | DROP SEQUENCE string wiring | A-SEQ-002 |
| A-SEQ-005 | dialect-it-only | Sequence options mapping — dialect unit | A-SEQ-005 |
| A-XCUT-001 | dialect-it-only | IdentifierHelper UPPER fold — dialect unit | A-XCUT-001 |
| A-XCUT-002 | dialect-it-only | Quote character — dialect unit | A-XCUT-002 |
| A-XCUT-005 | dialect-it-only | Isolation-level hooks — dialect unit | A-XCUT-005 |
| A-XCUT-007 | dialect-it-only | TCL keywords registry — dialect unit | A-XCUT-007 |
| A-XCUT-008 | dialect-it-only | SequenceSupport wiring flag — dialect unit | A-XCUT-008 |
| A-TYP-003 | dialect-it-only | REAL→float mapping — dialect unit sufficient | A-TYP-003 |
| A-TYP-007 | dialect-it-only | TIME JDBC — dialect IT (`XuguTypeRoundTripIT`) | A-TYP-007 |
| A-TYP-011 | dialect-it-only | CLOB — dialect IT (if A-TYP-010 chosen as Boot LOB) | A-TYP-011 |
| A-TYP-019 | dialect-it-only | `castPattern` unit | A-TYP-019 |
| A-DDL-002 | dialect-it-only | ALTER ADD COLUMN — dialect schema-export IT | A-DDL-002 |
| A-DDL-005 | dialect-it-only | DEFAULT column exporter — dialect unit | A-DDL-005 |
| A-DDL-006 | dialect-it-only | DROP TABLE — dialect IT | A-DDL-006 |
| A-PAG-003 | dialect-it-only | Limit bind-marker internals — dialect unit/IT | A-PAG-003 |
| A-LCK-002 | dialect-it-only | `FOR UPDATE OF` — dialect lock IT | A-LCK-002 |

### Schema / temp / comment / advanced DDL (dialect IT)

| row_id | status | Why dialect-it-only | i005_xref |
|---|---|---|---|
| A-SCH-001 | dialect-it-only | Schema create/drop — dialect IT | A-SCH-001 |
| A-SCH-002 | dialect-it-only | Schema create/drop — dialect IT | A-SCH-002 |
| A-SCH-004 | dialect-it-only | Local temp strategy — dialect SPI | A-SCH-004 |
| A-SCH-005 | dialect-it-only | Global temp strategy — dialect SPI | A-SCH-005 |
| A-SCH-006 | dialect-it-only | Temp strategy flags — dialect SPI | A-SCH-006 |
| A-SCH-008 | dialect-it-only | COMMENT ON / inline — dialect IT | A-SCH-008 |
| A-SCH-009 | dialect-it-only | COMMENT ON / inline — dialect IT | A-SCH-009 |
| A-SCH-010 | dialect-it-only | COMMENT ON / inline — dialect IT | A-SCH-010 |
| A-SCH-013 | dialect-it-only | CHECK / DROP CONSTRAINT / truncate / index — dialect IT | A-SCH-013 |
| A-SCH-014 | dialect-it-only | CHECK / DROP CONSTRAINT / truncate / index — dialect IT | A-SCH-014 |
| A-SCH-015 | dialect-it-only | CHECK / DROP CONSTRAINT / truncate / index — dialect IT | A-SCH-015 |
| A-SCH-016 | dialect-it-only | CHECK / DROP CONSTRAINT / truncate / index — dialect IT | A-SCH-016 |
| C-DDL-001 | dialect-it-only | IF NOT EXISTS / alter column type / datetime literal — dialect IT | C-DDL-001 |
| C-DDL-002 | dialect-it-only | IF NOT EXISTS / alter column type / datetime literal — dialect IT | C-DDL-002 |
| C-DDL-003 | dialect-it-only | IF NOT EXISTS / alter column type / datetime literal — dialect IT | C-DDL-003 |
| C-CAT-001 | dialect-it-only | Catalog create/drop — dialect IT | C-CAT-001 |
| C-GUID-001 | dialect-it-only | `selectGuidString` dialect helper — dialect IT (Boot uses A-TYP-012 field) | C-GUID-001 |

### Function families — dialect IT only (not every row Boot)

| row_id | status | Why dialect-it-only | i005_xref |
|---|---|---|---|
| A-FUN-003 | dialect-it-only | Unit/IT registration; Boot takes representative subset above | A-FUN-003 |
| A-FUN-005 | dialect-it-only | Unit/IT registration; Boot takes representative subset above | A-FUN-005 |
| A-FUN-006 | dialect-it-only | Unit/IT registration; Boot takes representative subset above | A-FUN-006 |
| A-FUN-008 | dialect-it-only | Unit/IT registration; Boot takes representative subset above | A-FUN-008 |
| A-FUN-009 | dialect-it-only | Unit/IT registration; Boot takes representative subset above | A-FUN-009 |
| A-FUN-011 | dialect-it-only | Dialect `XuguFunctionRegistryIT` / listagg — not Boot-required | A-FUN-011 |
| A-FUN-012 | dialect-it-only | Dialect function IT — not Boot-required | A-FUN-012 |
| A-FUN-013 | dialect-it-only | Dialect function IT — not Boot-required | A-FUN-013 |
| A-FUN-014 | dialect-it-only | Dialect function IT — not Boot-required | A-FUN-014 |
| A-FUN-018 | dialect-it-only | listagg — dialect IT; not Boot-required | A-FUN-018 |

### Ruler C advanced / known-limit / flags

| row_id | status | Why dialect-it-only | i005_xref |
|---|---|---|---|
| C-JSON-002 | dialect-it-only | Extra JSON agg variants — dialect IT; Boot keeps C-JSON-001 optional | C-JSON-002 |
| C-JSON-003 | dialect-it-only | Extra JSON agg variants — dialect IT | C-JSON-003 |
| C-JSON-004 | dialect-it-only | Extra JSON agg variants — dialect IT | C-JSON-004 |
| C-WIN-001 | dialect-it-only | Window — dialect IT (`XuguWindowCteIT`); out of C′ Boot scope | C-WIN-001 |
| C-CTE-001 | dialect-it-only | CTE — dialect IT; out of C′ Boot scope | C-CTE-001 |
| C-BULK-002 | dialect-it-only | Bulk **insert** fallback — `known-limit-documented`; no Boot bulk-insert requirement | C-BULK-002 |
| C-BULK-003 | dialect-it-only | `supportsSubqueryOnMutatingTable` flag — dialect unit/IT | C-BULK-003 |
| C-LOCK-001 | dialect-it-only | Audit anchor for NOWAIT/WAIT flags — dialect (Boot uses A-LCK-003) | C-LOCK-001 |

### Negative-only / 延后 (I-005) — never Boot-required

All **34** negative-only / deferred rows in I-005 baseline (e.g. A-PAG-005, A-LCK-004/005, A-SCH-007, A-XCUT-006/010/011, C-SKIP-001, C-DDL-004, deferred types/functions) stay in the **dialect negative suite** — **not** Boot SSOT rows.

---

## Gap summary by Phase (must-close for P-002+)

### P-002 — Layer A (5 SSOT gaps + startup-crud behavioral) — **closed** (RP-01 + RP-02 verified)

| row_id | Gap | Boot evidence |
|---|---|---|
| A-SPI-002 | Boot SPI-auto path | `DemoSpiDialectAutoResolveIT#sessionFactoryResolvesXuguDialectWithoutExplicitConfig` |
| A-SPI-003 | SPI-auto product resolve | shared with A-SPI-002 |
| A-SEQ-001 | `ddl-auto=validate` Boot startup | `DemoValidateStartupIT#validateStartupSucceedsWithPreCreatedSchema` |
| A-DDL-001 | Schema consumer surface under Boot | `DemoSchemaSurfaceIT#hibDemoPersonTableExistsAfterUpdateStartup` |
| A-IDN-004 | Persist+find only | `DemoPersonCrudIT#updateAndDeletePerson` |

Plus: `DemoStartupCrudIT#startupCrudRunnerPersistsAndFindsPerson` (`startup-crud=true`).

**Out of P-002:** association / SEQUENCE / locks / UNIQUE exception / functions / JSON / bulk.

### P-003 — Layer B (9 gaps) — **closed** (RP-01 + RP-02 verified; awaiting RP-03)

| row_id | Boot evidence |
|---|---|
| A-SCH-012 | `DemoAssociationIT#persistDeptWithMembersAndFindViaFk` |
| A-SCH-011 | UNIQUE `UK_HIB_DEMO_DEPT_MEMBER_CODE` + `DemoConstraintRollbackIT#uniqueViolationMapsToConstraintViolationException` |
| A-SEQ-003 | `DemoSequenceIT#persistSequenceTicketGeneratesIncreasingIds` |
| A-SEQ-004 | `DemoSequenceIT#currvalMatchesLastGeneratedIdInSession` |
| A-LCK-001 | `DemoLockIT#pessimisticWriteLocksPersonRow` |
| A-LCK-003 | `DemoLockIT#pessimisticWriteWithNowaitTimeoutExecutes` |
| C-EXC-001 | `DemoConstraintRollbackIT#uniqueViolationMapsToConstraintViolationException` |
| A-XCUT-004 | `DemoConstraintRollbackIT#forcedFailureRollsBackDurableMemberRow` |
| C-EXC-002 | `DemoConstraintRollbackIT#notNullViolationExtractsConstraintNameWhenPresent` |

### P-004 — Layer C′ (19 gaps) — entrypoints closed; live green after implementer rework

| Domain | row_ids | Boot evidence | Live (after UUID/Jackson fix) |
|---|---|---|---|
| Types | A-TYP-001,002,004,005,006,008,009,010,012 | `DemoTypesIT#typedSampleRoundTripKeyTypes` | PASS (UUID→varchar+converter) |
| JSON type | A-TYP-013 | `DemoJsonIT#jsonColumnRoundTripAndArrayAgg` | PASS (Jackson FormatMapper) |
| Functions | A-FUN-001,002,004,007,010,016,017 | `DemoFunctionsIT#hqlFunctionSubsetSmoke` | PASS (Jackson FormatMapper) |
| JSON agg | C-JSON-001 | `DemoJsonIT#jsonColumnRoundTripAndArrayAgg` | PASS (Jackson FormatMapper) |
| Bulk | C-BULK-001 | `DemoBulkMutationIT#bulkUpdatePersonNames` | PASS |

LOB pick: **A-TYP-010** (BLOB). **A-TYP-011** remains dialect-it-only (exclusion appendix).

**Fix notes (implementer rework after RP-02 FAIL):**
- A-TYP-012: `UuidAsVarcharConverter` + `guid_val varchar(36)` — avoids Xugu JDBC `[E50044]` on `UUIDJdbcType` extract.
- JSON: `spring-boot-starter-jackson` on demo classpath for Hibernate JSON `FormatMapper`.
- Dialect module unchanged; GAV `7.4.5.Final`.

---

## Boundary rules (reviewer must enforce)

1. **C′ = Boot-required SSOT rows only** — primary table has **41** rows, not 94.  
2. **No silent expansion** to full I-005 可实现 Boot mirror.  
3. **No invented `row_id`** without I-005 / matrix xref (`i005_xref` must match an existing I-005 baseline ID).  
4. Every **gap** has `gap_action` ∈ {P-002, P-003, P-004} or justified `defer`.  
5. Exclusion appendix rows stay **`dialect-it-only`** — never promoted to Boot `gap` without Human Gate Scope change.  
6. Dialect Java / demo Java unchanged in P-001 (docs/contracts only).  
7. GAV remains **7.4.5.Final**.

## Related contracts

- I-005 production SSOT: [`production-regression-baseline.md`](production-regression-baseline.md)  
- Definition A matrix: [`feature-matrix-definition-a.md`](feature-matrix-definition-a.md)  
- Ruler C matrix: [`feature-matrix-i003-ruler-c.md`](feature-matrix-i003-ruler-c.md)

## Change log

| Date | Change |
|---|---|
| 2026-07-18 | Initial publish (I-006 / P-001 / RP-02): 41 Boot-required rows from researcher inventory |
| 2026-07-18 | P-002 RP-01: Layer A 5 gaps + startup-crud → covered (`inv-i006-p002-rp01-implementer`) |
| 2026-07-18 | P-002 RP-02: offline `mvn test` green; live `XUGU_RUN_IT=true` demo 14/0/0/0 (`inv-i006-p002-rp02-test`) |
| 2026-07-18 | P-003 RP-01: Layer B 9 gaps → covered (B-both association+SEQUENCE; lock; UNIQUE/rollback) (`inv-i006-p003-rp01-implementer`) |
| 2026-07-18 | P-003 RP-02: offline `mvn test` green (demo 23/0/0/19); live `XUGU_RUN_IT=true` demo 23/0/0/0; VERIFY PASS (`inv-i006-p003-rp02-test`) |
| 2026-07-18 | P-004 RP-01: Layer C′ 19 gaps → covered (types/functions/JSON/bulk; LOB=A-TYP-010); Demo `@Test` ~28 (`inv-i006-p004-rp01-implementer`) |
| 2026-07-18 | P-004 RP-02 (first): offline green (demo 28/0/0/23); live FAIL (28/0/3/0) — UUID extract + missing JSON FormatMapper; VERIFY PASS offline; A-TYP-011 dialect-it-only (`inv-i006-p004-rp02-test`) |
| 2026-07-18 | P-004 implementer rework: UUID→varchar+converter; add `spring-boot-starter-jackson`; claimed live demo 28/0/0/0; dialect unchanged |
| 2026-07-18 | P-004 RP-02 re-run: offline 28/0/0/23; live 28/0/0/0 PASS; VERIFY PASS; C′ open gaps 0; A-TYP-011 dialect-it-only; Demo `@Test`=28 (`inv-i006-p004-rp02-test`) |
| 2026-07-19 | P-005 RP-01: docs ↔ SSOT ↔ Demo aligned; user guide `06-consumer-path.md`; freeze status **FROZEN** (Boot open gaps remain **0**); Initiative Accept prep (**NOT Ship**); GAV `7.4.5.Final` (`inv-i006-p005-rp01-implementer`) |
| 2026-07-19 | P-005 RP-02 (test): offline 28/0/0/23; live 28/0/0/0 PASS; VERIFY PASS; docs `06-consumer-path.md` spot-check OK; SSOT remains **FROZEN** gaps **0**; GAV `7.4.5.Final` confirmed (`inv-i006-p005-rp02-test`) |
