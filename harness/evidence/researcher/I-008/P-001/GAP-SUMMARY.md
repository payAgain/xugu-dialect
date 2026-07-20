# I-008 / P-001 — Gap Summary (RP-01)

> **Role:** researcher  
> **invocation_id:** `inv-i008-p001-rp01-researcher`  
> **Date:** 2026-07-20  
> **Audience:** architect-contract (RP-02), implementer (P-002/P-005/P-006), orchestrator  
> **Source:** [`INVENTORY.md`](INVENTORY.md)  
> **Constraint:** read-only audit; honest counts only

---

## Executive summary

| Metric | Value |
|---|---:|
| Physical 可实现 rows (SSOT tables) | **98** |
| Charter label (I-005 freeze) | **94** (+4 I-007 promotion drift — reconcile in RP-02) |
| **Honest covered-live** (live path exists) | **79** (78 IT + 1 demo) |
| SSOT `status=covered-live` column today | **5** |
| **unit-only-without-live** | **19** |
| **known-limit-documented** | **0** |
| Dishonest「94 covered-live」doc sources | **4** (+ 1 implementer note) |
| Rows needing promotion work (Batch A) | **19** |
| Rows needing SSOT tag / consumer closure (Batch B) | **16** |

**Verdict:** I-007/P-006 doc pass **inflated** user-facing counts to「94 covered-live」while SSOT still shows **93× `covered` + 5× `covered-live`**. I-008 Q1 must fix docs (P-002) and close **19** unit-only rows (P-003) before Accept.

---

## Q1 — Dishonest「94 covered-live」claims

| # | File | Location | Claim | Honest fact | Fix owner |
|---|---|---|---|---|---|
| Q1-01 | `docs/verification.md` | § I-005 production regression baseline table, L72 | 「**94 covered-live** after I-007/P-002 + P-004」 | **5** SSOT tagged + **73** IT rows untagged + **19** unit-only + **1** demo-live = **79** live-capable, not 94/98 | **P-002** (docs); SSOT counts **RP-02** |
| Q1-02 | `docs/user-guide/04-feature-matrix.md` | § I-005 baseline counts, L64 | 「**94 covered-live**（I-007/P-002 C-BULK-002 + P-004 Track C 四主题）」 | Same inflation — conflates **4** I-007 promotions with **entire** achievable set | **P-002** |
| Q1-03 | `harness/evidence/implementer/I-007/P-006/NOTES.md` | Doc alignment table, L11 | 「User-facing tables now **94 covered-live** (post I-007/P-002)」 | Root cause note — implementer **introduced** inflation during I-007/P-006 | **P-002** retract; historical evidence only |
| Q1-04 | `harness/session/session-log.md` | I-008 Scope PASS entry, L24 | Scope narrative「94 covered-live / 锁证据…」 | **Goal statement**, not audit fact — OK as intent but **must not** be copied into SSOT/user guide as achieved state | **P-002** clarify goal vs achieved |
| Q1-05 | `contracts/production-regression-baseline.md` | § Summary counts, L359–370 | Says **covered 94**, **covered-live 4** (I-007 promoted) — **not**「94 covered-live」 | **Internally inconsistent** with user docs; summary **under-counts** physical **98** rows | **RP-02** architect-contract |

### Not dishonest (correct or goal-only)

| File | Note |
|---|---|
| `harness/initiatives/I-008/brief.md` L8, L27–28 | States **goal** to eliminate inflated claims — correct |
| `harness/drafts/I-008-SCOPE-CLARIFYING.md` | Defines Q1 fix intent |
| `docs/user-guide/03-verify.md` | Mentions **covered-live** only for **C-BULK-002** + Track C themes — **not** whole 94 |
| `docs/user-guide/05-troubleshooting.md` §10 | **C-BULK-002** covered-live — accurate per row |
| `contracts/production-regression-baseline.md` row tables | Per-row `covered` vs `covered-live` mostly honest; problem is **rollup** + external docs |

### SSOT integrity gap (related Q1)

| Issue | Detail | Owner |
|---|---|---|
| Count drift | Physical SSOT lists **98** unique achievable rows; charter/summary still **94** | **RP-02** |
| Status column lag | **73** rows have live IT but SSOT `status=covered` | **P-003/P-004** SSOT sweep |

---

## Q2 — Lock documentation & evidence gaps

| gap_id | Current state | I-008 expectation | Owner Phase |
|---|---|---|---|
| Q2-DOC-001 | User guide §1–§2 cover **FOR UPDATE before LIMIT** order | Missing dedicated **integration section**: no SKIP LOCKED, no FOR SHARE, **`PESSIMISTIC_READ`→exclusive `FOR UPDATE`** | **P-002** docs |
| Q2-DOC-002 | `XuguDialect.java` Javadoc + matrix A-LCK-005 hint document Hibernate shim | Not surfaced in **user-guide** integration path (only matrix status table) | **P-002** |
| Q2-DOC-003 | `DemoLockIT` covers Boot pessimistic write/NOWAIT | No user doc tying **JPA lock modes** to XuGu SQL shape vs PostgreSQL expectations | **P-002** |
| Q2-EV-001 | `XuguLockIT`, `XuguNegativeRegressionBaselineTest` (unit negatives) | No **I-008** archived live log under `harness/evidence/test/I-008/P-005/` | **P-005** |
| Q2-EV-002 | A-LCK-005: unit asserts `getReadLockString` not FOR SHARE | Brief requires **behavioral/negative live** proof `PESSIMISTIC_READ` SQL is **FOR UPDATE**, not share | **P-005** |
| Q2-EV-003 | A-LCK-004 / C-SKIP-001 negatives exist | Need explicit **live** assertion bundle referenced in Accept evidence (I-008) | **P-005** |

**Code/doc anchors (read-only):** `XuguDialect#getReadLockString`, `supportsSkipLocked=false`, `DemoLockIT#pessimisticWriteLocksPersonRow`, `XuguNegativeRegressionBaselineTest#deferred_A_LCK_005` patterns.

---

## Q3 — UUID / JSON Boot out-of-box gaps

| gap_id | Current state | Gap | Owner Phase |
|---|---|---|---|
| Q3-DOC-001 | UUID/JSON workarounds in `docs/user-guide/06-consumer-path.md` § Mapping notes | **Not** in `02-configuration.md` / `01-install.md` as **required Boot checklist** | **P-002** |
| Q3-DOC-002 | `05-troubleshooting.md` § JSON functions mentions `JSON_FUNCTIONS_ENABLED` | No **Boot `application.yml`** template for JSON + UUID together | **P-002** |
| Q3-DOC-003 | Demo implements `UuidAsVarcharConverter`, `spring-boot-starter-jackson` | Documented as demo mapping notes — **not**「开箱即用」narrative for integrators | **P-002** + **P-006** |
| Q3-IMPL-001 | Demo C′ IT PASS after I-006 rework | I-008 brief requires **Boot 开箱对齐** — dependency + config + IT as **product path**, not hidden demo workaround | **P-006** |
| Q3-EV-001 | Dialect `XuguJsonAggregateIT` / `XuguTypeRoundTripIT` cover dialect JSON/UUID | Boot path evidence must be **reactor Accept** artifact (Q4 overlap) | **P-006** + **P-007** |

**Boot checklist items to document (P-002 draft, P-006 implement):**

1. `spring-boot-starter-jackson` (Hibernate JSON `FormatMapper`)  
2. UUID → `varchar(36)` + `AttributeConverter` (avoid JDBC `[E50044]`)  
3. `hibernate.type.preferred_uuid_jdbc_type` / mapping alignment with GAV **7.4.5.Final**  
4. `JSON_FUNCTIONS_ENABLED` when using HQL JSON aggregates  

---

## Q4 — Offline ≠ production proof gaps

| gap_id | Current state | Gap | Owner Phase |
|---|---|---|---|
| Q4-DOC-001 | `docs/verification.md` L83–85 | States offline `mvn test` sufficient for **`verify.py` PASS** — **true** for daily CI — but L72 **over-claims** live coverage | **P-002** |
| Q4-DOC-002 | `docs/user-guide/03-verify.md` | Describes gated IT; **does not** state I-008 Accept requires **full reactor** `XUGU_RUN_IT=true mvn -q test` log artifact | **P-002** |
| Q4-DOC-003 | `docs/user-guide/06-consumer-path.md` L107–108 | Checklist equates offline VERIFY PASS with integration readiness | Needs **explicit**「离线不足以称生产验证」| **P-002** |
| Q4-DOC-004 | `docs/production-readiness.md` | Functional correctness **required** but no I-008 Accept live-log convention | **P-002** + **P-007** |
| Q4-EV-001 | I-007 Accept SHA `95d4739` / logs under `harness/evidence/test/I-007/` | **No I-008** Accept-level full-reactor live evidence yet | **P-007** |
| Q4-EV-002 | `harness/verification.json` `test` = gate OFF | Correct for governance; Accept must **additionally** archive live run — not replace verify contract | **P-007** |

**Required Accept language (P-002):** offline `VERIFY PASS` proves **wiring + unit**; **production / Initiative Accept** requires gated **full reactor** green + deposited log when DB reachable (`SKIPPED_INFRA` documented otherwise).

---

## Promotion map — Batch A vs Batch B

### Batch A → **P-003** (Definition A dialect regression)

**Count: 19 rows** — all **unit-only-without-live**

```
A-TYP-003, A-TYP-019, A-DDL-005,
A-IDN-001, A-IDN-002, A-SEQ-002, A-SEQ-005,
A-FUN-003, A-FUN-005, A-FUN-006, A-FUN-007, A-FUN-009,
A-SCH-006,
A-SPI-004,
A-XCUT-001, A-XCUT-002, A-XCUT-005, A-XCUT-007, A-XCUT-008
```

**Suggested split inside P-003:**

| Sub-bucket | Count | Action |
|---|---:|---|
| Prefer **thin live IT** | **7** | A-TYP-003, A-TYP-019, A-DDL-005, A-XCUT-001, (+ optional A-IDN/SEQ) |
| **`known-limit-documented`** with bundle reason | **12** | A-IDN-001/002, A-SEQ-002/005, A-FUN-003/005/006/007/009, A-SCH-006, A-SPI-004, A-XCUT-002/005/007/008 |

Architect-contract **must** lock per-row outcome in RP-02 (no orphan rows).

### Batch B → **P-004** (Ruler C + consumer remainder)

**Count: 16 rows**

| Sub-bucket | matrix_ids | Count | Action |
|---|---|---:|---|
| Ruler C SSOT tag uplift (`covered`→`covered-live`; IT exists) | C-EXC-001,002, C-JSON-001–004, C-WIN-001, C-CTE-001, C-BULK-001,003, C-DDL-001,002,003, C-CAT-001, C-GUID-001 | **15** | SSOT + contract narrative |
| Demo-live clarification | A-XCUT-009 | **1** | Document demo-live satisfies consumer golden path; optional dialect IT stretch |

**Plus (not extra matrix rows):** Q3 Boot UUID/JSON out-of-box — **P-006**; cross-linked from P-004 consumer notes.

### Already closed (no P-003/P-004 promotion work)

| matrix_id | Evidence |
|---|---|
| C-BULK-002 | I-007/P-002 live IT |
| C-JSON-005, C-DDL-005, A-TYP-015, A-SEQ-006 | I-007/P-004 live IT |

---

## Cross-track evidence gaps (P-002+)

| gap_id | Description | Phase |
|---|---|---|
| EV-I008-LIVE-001 | No `harness/evidence/test/I-008/**` live logs | P-005/P-007 |
| EV-I008-LOCK-001 | No lock semantic behavioral IT artifact bundle | P-005 |
| EV-I008-BOOT-001 | No I-008 Boot out-of-box verification.json + live demo log | P-006/P-007 |

---

## Handoff checklist (architect-contract RP-02)

- [ ] Publish row-level promotion map (Batch A **19**, Batch B **16**)  
- [ ] Fix Q1 dishonest sources (table § Q1) via P-002 scope + SSOT summary  
- [ ] Lock `known-limit-documented` candidates with reasons  
- [ ] Wire Q2→P-005, Q3→P-006, Q4→P-007 in ACCEPTANCE cross-refs  
- [ ] Reconcile **94 vs 98** row count in SSOT summary  
