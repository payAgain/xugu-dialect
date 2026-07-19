# I-007 / P-001 — A/B/C Gap Summary (RP-01)

> **Role:** researcher  
> **invocation_id:** `inv-i007-p001-rp01-researcher`  
> **Date:** 2026-07-19  
> **Audience:** architect-contract (RP-02), orchestrator, P-002…P-005 implementers  
> **Source:** [`INVENTORY.md`](INVENTORY.md)  
> **Constraint:** GAV 7.4.5.Final; `compatiblemode=NONE`; native dialect; **NOT Ship**

---

## Executive summary

| Metric | Value |
|---|---:|
| I-005 可实现 baseline | 94 (93 covered + 1 known-limit `C-BULK-002`) |
| I-006 Boot-required | 41 covered / **0 open** |
| I-007 Track A re-open items | **1** (C-BULK-002 strategy + live evidence) |
| I-007 Track B new gaps | **4** (+ 1 optional) |
| I-007 Track C deferred promotions | **4 matrix_ids** (3 themes) |
| P-003 thin-fold recommended | **yes** |

**Verdict:** Inventory complete. Every A/B/C item has a proposed Phase owner or explicit fold note. C-BULK-002 researcher lean: **prefer-live-unblock** (architect locks in RP-02).

---

## Gap table

| gap_id | track | current_status | proposed_phase | notes |
|---|---|---|---|---|
| **C-BULK-002** | A | **known-limit-documented** (I-005); unit wiring only; live IT waived | **P-002** | Re-open per I-007 Scope: prefer live bulk-insert IT PASS; else permanent-limit + SSOT/docs binary lock. Root cause: GetGeneratedKeys/distillTbName on JOINED+IDENTITY + `LocalTemporaryTableInsertStrategy` |
| **EV-LIVE-001** | A | No `harness/evidence/test/I-007/**` live logs | **P-002** | Harden Accept: `mvn-test-live-*.log`, `IT-RESULT.txt`, surefire dump (I-006 pattern) |
| **EV-LIVE-002** | A | No C-BULK-002 live attempt artifact | **P-002** | Success log → `covered-live`; failure log → permanent-limit candidate |
| **B-FLY-001** | B | Flyway **absent** (no dep, migrations, tests, docs) | **P-005** | New Flyway path on Xugu via demo-spring-boot |
| **B-DEMO-001** | B | Demo bulk **update** covered; **delete** not in demo | **P-005** | Dialect has `XuguBulkMutationIT#bulkDelete*`; add Boot entry |
| **B-DEMO-002** | B | `DemoFunctionsIT` baseline exists (I-006 C′) | **P-005** | Deepen function/HQL smoke per brief — extend beyond current subset |
| **B-DEMO-003** | B | No read-only tx smoke | **P-005** (optional) | Brief optional; skip if scope tight |
| **C-JSON-005** | C | **negative-only** / 延后; `@Disabled` stub | **P-004** | JSON subset deepen — not full json_* registry; builds on A-FUN-017 |
| **A-TYP-015** | C | **negative-only** / 延后; `@Disabled` stub | **P-004** | ARRAY type mapping; pairs C-DDL-005 |
| **C-DDL-005** | C | **negative-only** / 延后; `@Disabled` stub | **P-004** | `getPreferredSqlTypeCodeForArray`; pairs A-TYP-015 |
| **A-SEQ-006** | C | **negative-only** / 延后; `@Disabled` stub | **P-004** | ALTER SEQUENCE; doc → impl → live IT → SSOT |
| **P-003-fold** | A′/C | No independent urgent deferred | **P-003 → P-004 thin fold** | Written fold note + reviewer OK; no silent drop |

---

## C-BULK-002 strategy recommendation

| Decision | Researcher lean |
|---|---|
| **prefer-live-unblock** | **Recommended** — wiring exists; update/delete live PASS; insert never re-attempted since I-005 waiver |
| **permanent-limit-candidate** | Only after gated live IT attempt with **logged** JDBC failure (GetGeneratedKeys/distillTbName) and architect binary lock |

Architect-contract **must** lock one path in RP-02 ACCEPTANCE — not both open.

---

## Phase owner rollup

| Phase | Open gaps | Count |
|---|---|---:|
| **P-002** | C-BULK-002, EV-LIVE-001, EV-LIVE-002 | 3 |
| **P-003** | Thin fold only (no independent gaps) | 0 |
| **P-004** | C-JSON-005, A-TYP-015, C-DDL-005, A-SEQ-006 | 4 |
| **P-005** | B-FLY-001, B-DEMO-001, B-DEMO-002, (+ optional B-DEMO-003) | 3–4 |

---

## Blockers

| Item | Severity | Notes |
|---|---|---|
| Live XuguDB for P-002/P-004/P-005 Accept | expected | Same gate as I-005/I-006; offline `mvn test` must stay green |
| C-BULK-002 JDBC blocker | medium | May force permanent-limit if live reproduces I-004 failure |
| **RP-01 research** | — | **No blockers** — ready for architect-contract RP-02 |
