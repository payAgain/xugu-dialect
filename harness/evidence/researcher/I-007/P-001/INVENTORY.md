# I-007 / P-001 — A/B/C SSOT Inventory (RP-01)

> **Role:** researcher  
> **invocation_id:** `inv-i007-p001-rp01-researcher`  
> **Date:** 2026-07-19  
> **Branch:** `feat/i-007-capability-hardening-abc`  
> **Inputs:** `harness/initiatives/I-007/brief.md`, `contracts/production-regression-baseline.md` (I-005 SSOT), `contracts/consumer-path-baseline.md` (I-006 SSOT), `contracts/feature-matrix-definition-a.md`, `contracts/feature-matrix-i003-ruler-c.md`, `harness/initiatives/I-005/ARCHIVE.md`, `harness/initiatives/I-006/ARCHIVE.md`, `docs/production-readiness.md`, prior `harness/evidence/researcher/I-006/P-001/{INVENTORY,GAP-SUMMARY}.md`  
> **Scope:** read-only inventory of Track **A / B / C** vs I-005 + I-006 SSOT; propose Phase routing P-002…P-005; **no** dialect/demo Java edits  
> **IT gate:** `XuguITGate` / `XuguIntegrationGate` ← env `XUGU_RUN_IT=true` or JVM `-Dxugu.run.integration=true`  
> **GAV:** `com.xugu:xugu-dialect:7.4.5.Final` · **compatiblemode=NONE** only · native dialect · **NOT Ship**

---

## 1. Predecessor SSOT snapshot

| Source | Status | Relevant to I-007 |
|---|---|---|
| I-005 `production-regression-baseline.md` | **Accepted / archived** | 94 可实现 rows: **93 covered** + **1 known-limit-documented** (`C-BULK-002`); 34 negative-only / 延后 |
| I-006 `consumer-path-baseline.md` | **FROZEN / archived** | Boot-required **41** rows; open gaps **0**; Demo `@Test` ≈ **28**; live 28/0/0/0 |
| I-007 brief | **Scope PASS** | A → B → C serial; C-BULK-002 **prefer live unblock**; Accept **live-log artifact** when DB available |

---

## 2. Track A — C-BULK-002 + Accept evidence hardening

### 2.1 C-BULK-002 current SSOT status

| Field | Value |
|---|---|
| **matrix_id** | `C-BULK-002` |
| **I-005 status** | **known-limit-documented** (closed I-005/P-004) |
| **Code surface** | `XuguDialect#getFallbackSqmInsertStrategy()` → `LocalTemporaryTableInsertStrategy` |
| **Offline test** | `XuguBulkMutationSupportTest#fallbackSqmInsertStrategyWired_C_BULK_002` (unit; gate = **unit**) |
| **Related live tests** | `XuguBulkMutationIT#bulkUpdateOnJoinedInheritanceSucceeds`, `#bulkDeleteOnJoinedInheritanceSucceeds` (**C-BULK-001** update/delete only — **no bulk insert IT**) |
| **Live IT** | **Waived** in I-005 |
| **User doc** | `docs/user-guide/05-troubleshooting.md` §10 |
| **SSOT call-out** | `contracts/production-regression-baseline.md` explicit call-out L248–259 |

### 2.2 Known-limit rationale (locate)

| Root cause | Source |
|---|---|
| JOINED bulk **insert** + IDENTITY root hits Xugu JDBC **12.3.6** `GetGeneratedKeys` / **`distillTbName`** failures | I-004/P-005 evidence; `harness/evidence/implementer/P-005/NOTES.md` |
| Dialect sets `getDefaultUseGetGeneratedKeys=false` for normal persist; bulk-insert **temp-table path unproven** on live DB | `XuguDialect.java` Javadoc; SSOT explicit call-out |
| Strategy **wired** (same pattern as sibling `LocalTemporaryTableInsertStrategy`) but live path never attempted post-wiring | I-005/P-004 ACCEPTANCE; `harness/evidence/test/P-005/IT-RESULT.txt` “C-BULK-002 bulk insert N/A” |
| Production guidance: prefer bulk **update/delete** (C-BULK-001 proven); bulk insert → persist / native SQL / ETL | `docs/user-guide/05-troubleshooting.md` §10 |

### 2.3 I-007 re-open intent (brief decision #2)

Human Gate + `harness/builds/B-001.json`: **prefer live unblock** for C-BULK-002; permanent-limit only if proven impossible. I-005 closure as `known-limit-documented` is **provisional** — I-007 must **binary lock** live-pass **OR** re-affirm permanent-limit + docs in P-002.

### 2.4 Accept evidence hardening — current gaps

| Gap | Current state | I-007 expectation (P-002+) |
|---|---|---|
| **No I-007 live-log artifacts** | `harness/evidence/test/I-007/**` **absent** | Deposit under `harness/evidence/test/I-007/P-002/` per I-006 pattern |
| **Prior live-log pattern (I-006)** | `mvn-test-live-demo.log`, `IT-RESULT.txt`, `surefire-summary-live-demo.txt`, `surefire-live-demo/` | Reuse naming for dialect + demo Accept runs |
| **I-005 full-reactor live log** | `harness/evidence/test/I-005/P-006/mvn-test-live-it-final.log` | Accept-level: `XUGU_RUN_IT=true mvn -q test` + archived log |
| **C-BULK-002-specific live evidence** | **None** — only unit wiring + waiver docs | P-002: either live bulk-insert IT PASS log **or** documented failure artifact proving permanent-limit |
| **Observability dimension** | `docs/production-readiness.md` — conditional when logging/diagnostics added | P-002 marks Observability **required** for live-log convention |

**Missing live logs pattern (summary):** I-007 has zero test evidence yet; architect must name artifact paths in P-001 ACCEPTANCE (e.g. `harness/evidence/test/I-007/P-002/mvn-test-live-it.log`, `IT-RESULT.txt`, optional surefire dump).

### 2.5 C-BULK-002 strategy lean (researcher recommendation)

| Path | Lean | Rationale |
|---|---|---|
| **prefer-live-unblock** | **YES** | Brief Scope PASS + B-001 recommendation; wiring exists; update/delete live PASS proves temp-table DDL works; insert never re-tried on live DB since I-005 waiver |
| **permanent-limit-candidate** | Fallback only | If live IT reproduces GetGeneratedKeys/distillTbName with logged artifact after good-faith attempt |

**What would prove permanent-limit only:** gated live IT attempt on JOINED+IDENTITY bulk insert → consistent JDBC failure log; no dialect-only workaround without driver fix; architect locks SSOT `known-limit-documented` + user doc update (not ambiguous dual state).

### 2.6 Track A Phase owner

| Item | Owner Phase |
|---|---|
| C-BULK-002 live-pass or permanent-limit binary lock | **P-002** |
| Accept live-log artifact convention + first deposit | **P-002** |

---

## 3. Track B — Flyway + Demo consumer deepening

### 3.1 vs I-006 consumer-path SSOT

I-006 closed all **41** Boot-required rows. I-007 Track B is **incremental deepening**, not re-opening the 41-row table unless P-005 adds rows (requires architect SSOT change).

### 3.2 Flyway — absence inventory

| Check | Result |
|---|---|
| `demo-spring-boot/pom.xml` | **No** `flyway-core` / `spring-boot-starter-flyway` dependency |
| Demo resources | **No** `db/migration/**` or Flyway config |
| Demo tests | **No** Flyway migrate/validate IT |
| User guide `06-consumer-path.md` | Documents `ddl-auto` validate/update paths; **no Flyway section** |
| Repo-wide grep | Flyway mentioned only in I-007 Plan/tasks/brief — **zero implementation** |

**Gap:** Flyway integration path **not started** → **P-005**.

### 3.3 Demo bulk update/delete

| Surface | I-005 dialect | I-006 demo | I-007 gap |
|---|---|---|---|
| Bulk update | `XuguBulkMutationIT#bulkUpdateOnJoinedInheritanceSucceeds` (live) | `DemoBulkMutationIT#bulkUpdatePersonNames` (live PASS) | Covered |
| Bulk delete | `XuguBulkMutationIT#bulkDeleteOnJoinedInheritanceSucceeds` (live) | **Not duplicated** (comment in `DemoBulkMutationIT`) | **Demo bulk delete entry missing** → **P-005** |
| Bulk insert | C-BULK-002 known-limit | dialect-it-only exclusion | Track A (P-002), not B |

### 3.4 Function / HQL coverage

| Surface | Current | I-007 deepening gap |
|---|---|---|
| HQL function subset | `DemoFunctionsIT#hqlFunctionSubsetSmoke` (A-FUN-001,002,004,007,010,016,017) | Brief asks **函数/HQL 冒烟** — may extend subset or add dedicated HQL-only smoke beyond function registry probes |
| JPQL | `DemoBootBaselineSmokeTest#jpaPersistAndJpqlQueryRoundTrip` | Baseline covered |
| Native query smoke | **None** in demo | Optional stretch for P-005 |
| Dialect function IT | `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | dialect-it-only; not Boot SSOT |

**Gap:** consumer-path **deepening** beyond I-006 C′ floor — not a reopen of 41-row gaps → **P-005**.

### 3.5 Optional read-only transaction smoke

| Check | Result |
|---|---|
| Demo `@Transactional(readOnly=true)` test | **Absent** |
| Brief | Optional — **P-005** if included |

### 3.6 Track B Phase owner

| Item | Owner Phase |
|---|---|
| Flyway dependency + migration path + gated IT | **P-005** |
| Demo bulk **delete** entry | **P-005** |
| Function/HQL smoke deepening | **P-005** |
| Optional read-only tx smoke | **P-005** (optional) |

---

## 4. Track C — deferred matrix rows (JSON + ARRAY + ALTER SEQUENCE)

All four target rows are **negative-only / 延后** in I-005 SSOT with `@Disabled` stubs in `XuguNegativeRegressionBaselineTest`.

### 4.1 C-JSON-005 — broader JSON HQL subset

| Field | Value |
|---|---|
| **matrix_id** | `C-JSON-005` |
| **Matrix status** | **延后** (`feature-matrix-i003-ruler-c.md`) |
| **SSOT status** | **negative-only** |
| **Stub** | `XuguNegativeRegressionBaselineTest#deferred_C_JSON_005_broaderJsonFunctions` (@Disabled) |
| **Partial coverage today** | `A-FUN-017` / `json_value` subset via `XuguFunctionRegistryTest#jsonSubsetUsesStandardJsonValueNotMysqlDump`; `XuguJsonAggregateIT`; `DemoJsonIT` (C-JSON-001, A-TYP-013) |
| **Gap** | Broader `json_*` HQL beyond value/extract — no impl, no live IT, SSOT still **negative-only** |
| **I-007 scope** | JSON **deepen subset** (not full `XuguJsonFunctions` large set) |

### 4.2 A-TYP-015 + C-DDL-005 — ARRAY

| matrix_id | Matrix | SSOT | Stub | Hibernate surface |
|---|---|---|---|---|
| **A-TYP-015** | Types ARRAY | negative-only | `#deferred_A_TYP_015_array` | Map SQL ARRAY / Hibernate array types |
| **C-DDL-005** | Preferred SQL type for array | negative-only | `#deferred_C_DDL_005_arraySqlType` | `getPreferredSqlTypeCodeForArray` |

**Cross-link:** matrix notes “aligns with A-TYP-015”; stubs share disable rationale. **Doc ref:** `reference/sql/datatype/array.md`.

### 4.3 A-SEQ-006 — ALTER SEQUENCE

| Field | Value |
|---|---|
| **matrix_id** | `A-SEQ-006` |
| **Matrix status** | **延后** (“Schema-update rare path”) |
| **SSOT status** | **negative-only** |
| **Stub** | `XuguNegativeRegressionBaselineTest#deferred_A_SEQ_006_alterSequence` (@Disabled) |
| **Partial coverage today** | A-SEQ-001…005 CREATE/NEXTVAL/CURRVAL covered; **no** ALTER SEQUENCE impl or IT |
| **Doc ref** | `reference/object/sequence.md` |

### 4.4 Track C delivery pattern (brief)

Each theme: **doc → impl → live IT → SSOT update** (promote from `negative-only`/`延后` to `covered` or justified deferral).

### 4.5 Track C Phase owner

| Theme | matrix_ids | Owner Phase |
|---|---|---|
| JSON subset deepen | **C-JSON-005** (+ existing A-FUN-017 baseline) | **P-004** |
| ARRAY type + DDL | **A-TYP-015**, **C-DDL-005** | **P-004** |
| ALTER SEQUENCE | **A-SEQ-006** | **P-004** |

**P-003 thin-fold note:** No independent urgent A′ items identified outside these C themes (see §5). Recommend **thin fold → P-004**.

---

## 5. Phase routing proposal (P-002 … P-005)

| Phase | Track | Intent | Items |
|---|---|---|---|
| **P-002** | **A** | C-BULK-002 binary strategy execution + Accept live-log hardening | C-BULK-002 live IT or permanent-limit; first `harness/evidence/test/I-007/P-002/` live artifacts |
| **P-003** | **A′/急项** | Thin fold unless urgent item surfaces in P-002 | **Recommend thin fold → P-004** (no standalone urgent deferred) |
| **P-004** | **C** | JSON subset + ARRAY + ALTER SEQUENCE | C-JSON-005, A-TYP-015, C-DDL-005, A-SEQ-006 |
| **P-005** | **B** | Flyway + Demo consumer deepening | Flyway path; Demo bulk delete; function/HQL smoke; optional read-only tx |
| **P-006** | Docs + Accept | (out of RP-01 scope) | VERIFY PASS; docs alignment |

### P-003 thin-fold recommendation: **yes**

Rationale: I-007 brief lists C “三件套” as P-004; no A′ matrix row requires independent Phase between P-002 and P-004. I-005 negative consolidation already closed. Any “urgent deferred” from brief decision #2 (“急项可与 C 主题合并”) maps directly to P-004 themes.

---

## 6. Evidence gap summary (cross-track)

| gap_id | Track | Description | Proposed Phase |
|---|---|---|---|
| EV-LIVE-001 | A | No I-007 live-log artifacts under `harness/evidence/test/I-007/**` | P-002 |
| EV-LIVE-002 | A | No C-BULK-002 live bulk-insert IT or failure log | P-002 |
| B-FLY-001 | B | Flyway entirely absent from demo | P-005 |
| B-DEMO-001 | B | Demo bulk **delete** not covered (update only) | P-005 |
| B-DEMO-002 | B | Function/HQL consumer deepening beyond I-006 floor | P-005 |
| B-DEMO-003 | B | Optional read-only `@Transactional` smoke absent | P-005 (optional) |
| C-JSON-005 | C | Broader json_* deferred; stub only | P-004 |
| C-ARRAY | C | A-TYP-015 + C-DDL-005 deferred; stubs only | P-004 |
| C-SEQ-006 | C | ALTER SEQUENCE deferred; stub only | P-004 |

---

## 7. Constraints reminder

| Constraint | Value |
|---|---|
| GAV | `com.xugu:xugu-dialect:7.4.5.Final` — **no bump** |
| compatiblemode | **NONE** only |
| Dialect strategy | Native Xugu — **no** MySQL/Oracle compat inheritance |
| Ship / Central | **Out of scope** for I-007 |
| RP-01 writes | `harness/evidence/researcher/I-007/P-001/**` only |
| No edits | `org/**`, dialect/demo Java, `contracts/**`, harness REGISTRY/session |

---

## 8. Handoff to architect-contract (RP-02)

1. Lock C-BULK-002 strategy binary: researcher lean = **prefer-live-unblock**.  
2. Publish I-007 gap map with Phase owners from §5.  
3. Name Accept live-log artifact paths (extend I-006 convention).  
4. Confirm P-003 **thin fold → P-004** or document urgent exception.  
5. Do **not** silently expand I-006 41-row Boot SSOT without Human Gate.
