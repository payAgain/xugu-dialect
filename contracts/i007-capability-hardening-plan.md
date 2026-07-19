# I-007 Capability Hardening Plan (SSOT)

> **Status:** Published (I-007 / P-001 / RP-02)  
> **Initiative:** I-007 — capability hardening A → B → C  
> **Author role:** architect-contract  
> **invocation_id:** `inv-i007-p001-rp02-architect`  
> **Branch:** `feat/i-007-capability-hardening-abc`  
> **Sources:** [`production-regression-baseline.md`](production-regression-baseline.md) (I-005), [`consumer-path-baseline.md`](consumer-path-baseline.md) (I-006 FROZEN), [`harness/initiatives/I-007/brief.md`](../harness/initiatives/I-007/brief.md), [`harness/evidence/researcher/I-007/P-001/INVENTORY.md`](../harness/evidence/researcher/I-007/P-001/INVENTORY.md)  
> **GAV:** `com.xugu:xugu-dialect:7.4.5.Final` — **no bump**  
> **compatiblemode:** **NONE** only  
> **Dialect strategy:** Native Xugu — **no** MySQL/Oracle compat inheritance  
> **Ship / Central:** **Out of scope** for I-007

## Purpose

Extend I-005 production-regression and I-006 consumer-path baselines with an **I-007 gap map** for Tracks **A / B / C**, a **binary C-BULK-002 strategy lock**, Accept **live-log evidence** conventions, and Phase routing **P-002…P-005**. This file is the SSOT for I-007 execution; it does **not** re-open I-006 Boot-required 41-row table unless Human Gate approves Scope change.

---

## Track summary

| Track | Intent | Owner Phases |
|---|---|---|
| **A** | C-BULK-002 live unblock **or** permanent-limit binary lock; Accept evidence hardening | **P-002** |
| **B** | Flyway + Demo consumer deepening (incremental; I-006 gaps = **0**) | **P-005** |
| **C** | Deferred matrix promotions: JSON subset + ARRAY + ALTER SEQUENCE | **P-004** |
| **A′** | Urgent deferred not folded into C | **P-003 → thin fold into P-004** (locked) |

---

## C-BULK-002 STRATEGY LOCK (binary)

> **P-001 lock:** **prefer-live-unblock** — P-002 **must** attempt live bulk-insert IT before any permanent-limit re-affirmation.

| Field | Value |
|---|---|
| **matrix_id** | C-BULK-002 |
| **I-005 baseline status (pre-P-002)** | **known-limit-documented** (provisional — re-opened by I-007 Scope) |
| **P-001 strategy lock** | **prefer-live-unblock** |
| **P-002 execution mandate** | Attempt gated live IT on JOINED + IDENTITY bulk **insert** via `LocalTemporaryTableInsertStrategy` path; deposit success **or** failure artifacts |
| **P-002 outcome (exactly one)** | **`covered-live`** — live IT PASS (I-007/P-002 implementer RP-01) |
| **Permanent-limit gate** | Only after good-faith live attempt with **logged** JDBC failure (GetGeneratedKeys / `distillTbName` per I-004/P-005); SSOT row + `docs/user-guide/05-troubleshooting.md` §10 updated in same Phase |
| **Code surface (unchanged in P-001)** | `XuguDialect#getFallbackSqmInsertStrategy()` → `LocalTemporaryTableInsertStrategy` |
| **Offline wiring (existing)** | `XuguBulkMutationSupportTest#fallbackSqmInsertStrategyWired_C_BULK_002` |
| **Related live (C-BULK-001)** | `XuguBulkMutationIT#bulkUpdateOnJoinedInheritanceSucceeds`, `#bulkDeleteOnJoinedInheritanceSucceeds` — temp-table DDL proven; insert never re-attempted post I-005 waiver |

### Decision tree (P-002)

```text
P-002 start
  └─ XUGU_RUN_IT=true: add/run bulk-insert IT (JOINED+IDENTITY)
       ├─ PASS → SSOT status → covered-live; live log archived
       └─ FAIL (reproducible JDBC blocker) → SSOT stays known-limit-documented;
            user doc + failure artifact; no ambiguous dual state
```

**Ambiguity forbidden:** P-002 Accept must record which branch executed. Reviewer rejects if status remains “maybe live later” without artifact.

---

## Accept evidence hardening (live-log convention)

When live XuguDB is available (`XUGU_RUN_IT=true` or `-Dxugu.run.integration=true`), implementers **must** archive logs under phase-scoped paths — avoid the I-007 “missing-log” pattern (zero files under `harness/evidence/test/I-007/**` today).

### Required artifacts (by Phase)

| Phase | Path prefix | Required files (when live run performed) |
|---|---|---|
| **P-002** | `harness/evidence/test/I-007/P-002/` | `mvn-test-live-it.log` (full reactor or dialect module as run); `IT-RESULT.txt` (summary line per matrix/theme); optional `surefire-summary-live-it.txt`, `surefire-live-it/` dump |
| **P-004** | `harness/evidence/test/I-007/P-004/` | `mvn-test-live-it.log`; `IT-RESULT.txt`; theme notes for C-JSON-005 / ARRAY / A-SEQ-006 |
| **P-005** | `harness/evidence/test/I-007/P-005/` | `mvn-test-live-demo.log`; `IT-RESULT.txt`; optional `surefire-summary-live-demo.txt`, `surefire-live-demo/` |
| **P-006** | `harness/evidence/test/I-007/P-006/` | Accept-level full-reactor: `mvn-test-live-it-final.log` (pattern: I-005/P-006) |

### IT-RESULT.txt minimum fields

```text
phase=P-00x
date=ISO-8601
gate=XUGU_RUN_IT=true
branch=feat/i-007-capability-hardening-abc
gav=7.4.5.Final
compatiblemode=NONE
outcome=PASS|FAIL|SKIP(no-db)
tests_run=…
failures=…
errors=…
skipped=…
notes=…
```

### C-BULK-002-specific evidence (P-002)

| Outcome | Required artifact |
|---|---|
| Live PASS | IT method name + `mvn-test-live-it.log` excerpt showing bulk insert green |
| Permanent-limit | Same logs showing reproducible failure + pointer to SSOT/user-doc update |

**Observability:** P-002 marks live-log convention **required** for Accept prep (aligns with `docs/production-readiness.md` conditional dimension when diagnostics added).

---

## P-003 thin-fold lock

| Decision | Value |
|---|---|
| **P-003 standalone Phase?** | **No** — thin fold into **P-004** |
| **Rationale** | No independent urgent A′ matrix rows outside C themes (researcher RP-01); I-005 negative consolidation closed; brief decision #2 allows merging urgent items into C |
| **P-003 Accept rule** | May Accept with **fold-into-P-004** evidence if no independent urgent items surface in P-002 |
| **Exception path** | If P-002 surfaces urgent deferred **outside** C-JSON-005 / ARRAY / A-SEQ-006, orchestrator documents exception in P-003 handoff — **not** default |

---

## Gap map — Track A (owner **P-002**)

| gap_id | item | current_status | owner | P-002 action |
|---|---|---|---|---|
| **C-BULK-002** | Bulk insert fallback | **covered-live** (P-002 PASS) | **P-002** | Closed: live IT + dialect insert-strategy fix |
| **EV-LIVE-001** | No I-007 live logs | **deposited** | **P-002** | `harness/evidence/test/I-007/P-002/mvn-test-live-it.log` |
| **EV-LIVE-002** | No C-BULK-002 live attempt | **PASS artifact** | **P-002** | `XuguBulkMutationIT#bulkInsertOnJoinedInheritanceWithIdentitySucceeds_C_BULK_002` |

**I-006 consumer-path:** C-BULK-002 remains **dialect-it-only** / known-limit in Boot SSOT until P-002 changes I-005 row status. No silent Boot row addition.

---

## Gap map — Track B (owner **P-005**)

I-006 Boot-required **41/41 covered** — Track B is **incremental deepening**, not reopening the 41-row table.

| gap_id | item | current_status | owner | P-005 action |
|---|---|---|---|---|
| **B-FLY-001** | Flyway integration | **absent** (no dep, migrations, IT, docs) | **P-005** | Add Flyway path on demo-spring-boot; gated IT; user-guide section |
| **B-DEMO-001** | Demo bulk delete | dialect IT only; demo update covered | **P-005** | Add Boot bulk **delete** entry (mirror `DemoBulkMutationIT` update) |
| **B-DEMO-002** | Function/HQL smoke deepen | `DemoFunctionsIT` baseline (I-006 C′) | **P-005** | Extend subset or dedicated HQL smoke per brief |
| **B-DEMO-003** | Read-only tx smoke | absent | **P-005** (optional) | `@Transactional(readOnly=true)` if scope allows |

**Boundary:** Do **not** expand I-006 41-row Boot SSOT without Human Gate Scope change. Track B items are **behavioral deepening**, not new Boot-required rows unless architect publishes explicit SSOT amendment post Gate.

---

## Gap map — Track C (owner **P-004**)

All rows **negative-only / 延后** in I-005 SSOT; delivery pattern: **doc → impl → live IT → SSOT update**.

| gap_id | matrix_id | theme | current_status | owner | P-004 action |
|---|---|---|---|---|---|
| **C-JSON-005** | C-JSON-005 | JSON HQL subset deepen | negative-only; `@Disabled` stub | **P-004** | Subset beyond A-FUN-017 / `json_value`; not full `XuguJsonFunctions` |
| **C-ARRAY** | A-TYP-015, C-DDL-005 | ARRAY type + preferred SQL type | negative-only; paired stubs | **P-004** | Map SQL ARRAY / `getPreferredSqlTypeCodeForArray`; ref `reference/sql/datatype/array.md` |
| **C-SEQ-006** | A-SEQ-006 | ALTER SEQUENCE | negative-only; `@Disabled` stub | **P-004** | ALTER SEQUENCE impl + live IT; ref `reference/object/sequence.md` |

**Partial coverage today (not closing gaps):** A-FUN-017, `XuguJsonAggregateIT`, `DemoJsonIT` — remain baseline; C-JSON-005 promotion requires broader `json_*` HQL.

---

## Phase routing rollup (P-002…P-006)

| Phase | Track | Intent | Open gaps | Count |
|---|---|---|---|---:|
| **P-002** | **A** | C-BULK-002 binary execution + first live-log deposit | C-BULK-002, EV-LIVE-001, EV-LIVE-002 | 3 |
| **P-003** | **A′** | Thin fold — no standalone deliverables unless P-002 exception | P-003-fold | 0 |
| **P-004** | **C** | JSON + ARRAY + ALTER SEQUENCE | C-JSON-005, A-TYP-015, C-DDL-005, A-SEQ-006 | 4 |
| **P-005** | **B** | Flyway + Demo deepening | B-FLY-001, B-DEMO-001, B-DEMO-002, (+ optional B-DEMO-003) | 3–4 |
| **P-006** | Docs + Accept | VERIFY PASS; docs alignment; final live log | (Accept prep) | — |

**Serial order (orchestrator):** P-002 → P-003 (fold) → P-004 → P-005 → P-006.

---

## Constraints (non-negotiable)

| Constraint | Value |
|---|---|
| GAV | `com.xugu:xugu-dialect:7.4.5.Final` — no bump |
| compatiblemode | **NONE** only |
| Dialect | Native Xugu — no MySQL/Oracle compat |
| Ship | **Not required** this Initiative |
| I-006 Boot SSOT | **FROZEN** at 41 covered — no silent expansion |
| P-001 writes | contracts + evidence only — **no** dialect/demo Java |

---

## Related contracts

| Document | Relationship |
|---|---|
| [`production-regression-baseline.md`](production-regression-baseline.md) | I-005 SSOT; C-BULK-002 call-out cross-ref I-007 re-open |
| [`consumer-path-baseline.md`](consumer-path-baseline.md) | I-006 FROZEN; Track B deepens without reopening 41 gaps |
| [`feature-matrix-definition-a.md`](feature-matrix-definition-a.md) | Matrix xref for A-TYP-015, A-SEQ-006 |
| [`feature-matrix-i003-ruler-c.md`](feature-matrix-i003-ruler-c.md) | Matrix xref for C-JSON-005, C-DDL-005 |

---

## Change log

| Date | Change |
|---|---|
| 2026-07-19 | Initial publish (I-007 / P-001 / RP-02): gap map A/B/C, C-BULK-002 prefer-live-unblock lock, live-log convention, P-003 thin-fold |
