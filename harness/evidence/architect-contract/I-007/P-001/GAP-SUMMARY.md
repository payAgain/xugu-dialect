# I-007 / P-001 — A/B/C Gap Summary (architect-contract RP-02)

> Companion to [`contracts/i007-capability-hardening-plan.md`](../../../../contracts/i007-capability-hardening-plan.md)  
> invocation_id: `inv-i007-p001-rp02-architect`  
> Source: researcher RP-01 + architect SSOT publish

## Executive summary

| Metric | Value |
|---|---:|
| I-005 可实现 baseline | 94 (93 covered + 1 known-limit `C-BULK-002`) |
| I-006 Boot-required | 41 covered / **0 open** (FROZEN) |
| I-007 Track A re-open | **1** (C-BULK-002 + live evidence) |
| I-007 Track B new gaps | **3–4** (+ optional read-only tx) |
| I-007 Track C promotions | **4 matrix_ids** (3 themes) |
| P-003 thin-fold | **yes** → P-004 |
| C-BULK-002 strategy lock | **prefer-live-unblock** (P-002 decides outcome) |

## Gap table (architect-owned rollup)

| gap_id | track | current_status | owner_phase | notes |
|---|---|---|---|---|
| **C-BULK-002** | A | known-limit-documented (I-005 provisional) | **P-002** | Strategy lock: prefer-live-unblock → `covered-live` **or** permanent-limit |
| **EV-LIVE-001** | A | No `harness/evidence/test/I-007/**` | **P-002** | First live-log deposit |
| **EV-LIVE-002** | A | No C-BULK-002 live attempt | **P-002** | Success/failure artifact required |
| **B-FLY-001** | B | Flyway absent | **P-005** | New consumer path |
| **B-DEMO-001** | B | Demo bulk delete missing | **P-005** | Dialect IT exists |
| **B-DEMO-002** | B | Function/HQL baseline only | **P-005** | Deepen per brief |
| **B-DEMO-003** | B | No read-only tx smoke | **P-005** (optional) | Skip if scope tight |
| **C-JSON-005** | C | negative-only / 延后 | **P-004** | JSON subset deepen |
| **A-TYP-015** | C | negative-only / 延后 | **P-004** | ARRAY type; pairs C-DDL-005 |
| **C-DDL-005** | C | negative-only / 延后 | **P-004** | Preferred SQL type for array |
| **A-SEQ-006** | C | negative-only / 延后 | **P-004** | ALTER SEQUENCE |
| **P-003-fold** | A′ | No independent urgent deferred | **P-003 → P-004** | Locked thin fold |

## Phase owner rollup

| Phase | Open gaps | Count |
|---|---|---:|
| **P-002** | C-BULK-002, EV-LIVE-001, EV-LIVE-002 | 3 |
| **P-003** | Thin fold only | 0 |
| **P-004** | C-JSON-005, A-TYP-015, C-DDL-005, A-SEQ-006 | 4 |
| **P-005** | B-FLY-001, B-DEMO-001, B-DEMO-002, (+ B-DEMO-003) | 3–4 |

## C-BULK-002 strategy lock (architect decision)

| Decision | Value |
|---|---|
| **P-001 lock** | **prefer-live-unblock** |
| **P-002 outcome** | Exactly one: **`covered-live`** **OR** **`known-limit-documented`** |
| **Permanent-limit only after** | Good-faith live IT + logged JDBC failure |

## Live-log convention (named for P-002+)

- `harness/evidence/test/I-007/P-002/mvn-test-live-it.log`
- `harness/evidence/test/I-007/P-002/IT-RESULT.txt`
- (P-004/P-005/P-006 paths per plan § Accept evidence hardening)

## Boundary reminders

- I-006 Boot SSOT: **41/41** — Track B deepens behavior; no silent Boot row expansion.
- GAV **7.4.5.Final**; **NONE** only; native dialect; **NOT Ship**.
