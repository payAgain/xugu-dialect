# I-008 / P-001 — Gap Summary (architect-contract RP-02)

> Companion to [`PROMOTION-MAP.md`](PROMOTION-MAP.md) and [`ACCEPTANCE.md`](ACCEPTANCE.md)  
> **invocation_id:** `inv-i008-p001-rp02-architect`  
> **Source:** researcher RP-01 + architect SSOT publish  
> **Audience:** P-002 (docs), P-003/P-004 (SSOT + live), P-005 (lock evidence), P-006 (Boot), P-007 (Accept live log)

---

## Executive summary

| Metric | Value |
|---|---:|
| Physical 可实现 rows (SSOT) | **98** |
| Charter label (I-005 freeze) | **94** (+4 I-007 drift — reconciled in promotion map) |
| Honest covered-live today | **79** (78 IT + 1 demo) |
| SSOT `status=covered-live` column today | **5** |
| unit-only-without-live | **19** → **P-003 Batch A** |
| Locked known-limit-documented (Batch A) | **15** |
| Thin live IT required (Batch A) | **4** |
| SSOT tag sweep (Batch A′ + B) | **73** |
| Dishonest「94 covered-live」sources | **4** (+ 1 goal-only narrative) |

---

## Q1 — Dishonest「94 covered-live」claims → P-002 / RP-02

| gap_id | File | Claim | Honest fact | Fix owner |
|---|---|---|---|---|
| Q1-01 | `docs/verification.md` L72 | 「94 covered-live after I-007」 | **79** live-capable; **19** unit-only; **5** SSOT-tagged | **P-002** |
| Q1-02 | `docs/user-guide/04-feature-matrix.md` L64 | 「94 covered-live」 | Same inflation | **P-002** |
| Q1-03 | `harness/evidence/implementer/I-007/P-006/NOTES.md` L11 | Introduced inflation | Retract in P-002; historical only | **P-002** |
| Q1-04 | `harness/session/session-log.md` L24 | Goal narrative | Clarify goal vs achieved | **P-002** |
| Q1-05 | `contracts/production-regression-baseline.md` § Summary | Under-counts physical **98**; legacy rollup | **RP-02** — updated honest counts | **done (this RP-02)** |

**Promotion map cross-ref:** Batch A/B assignments in [`PROMOTION-MAP.md`](PROMOTION-MAP.md). Doc rewrites owned by **P-002**.

---

## Q2 — Lock documentation & live evidence → P-002 / P-005

| gap_id | Current state | I-008 expectation | Owner Phase | Promotion map link |
|---|---|---|---|---|
| Q2-DOC-001 | User guide §1–§2 cover FOR UPDATE order | Dedicated integration section: no SKIP LOCKED, no FOR SHARE, **`PESSIMISTIC_READ`→`FOR UPDATE`** | **P-002** | A-LCK-001…003 Batch A′ tag only — **not** Q2 closure |
| Q2-DOC-002 | `XuguDialect.java` Javadoc + A-LCK-005 hint | Surface in user-guide integration path | **P-002** | A-LCK-005 negative-only row — behavioral proof in **P-005** |
| Q2-DOC-003 | `DemoLockIT` covers pessimistic write/NOWAIT | User doc tying JPA lock modes to XuGu SQL | **P-002** | A-XCUT-009 demo path — **P-004** consumer note |
| Q2-EV-001 | `XuguLockIT` exists | I-008 archived live log under `harness/evidence/test/I-008/P-005/` | **P-005** | EV-I008-LOCK-001 |
| Q2-EV-002 | A-LCK-005 unit asserts no FOR SHARE | **Live** proof `PESSIMISTIC_READ` SQL is **FOR UPDATE** | **P-005** | A-LCK-005 — not satisfied by P-003 tag sweep |
| Q2-EV-003 | A-LCK-004 / C-SKIP-001 negatives exist | Explicit live assertion bundle in Accept evidence | **P-005** | A-LCK-004 negative-only — live IT exists; evidence deposit **P-005** |

**Code anchors (read-only):** `XuguDialect#getReadLockString`, `supportsSkipLocked=false`, `DemoLockIT#pessimisticWriteLocksPersonRow`, `XuguNegativeRegressionBaselineTest#deferred_A_LCK_005`.

---

## Q3 — UUID / JSON Boot out-of-box → P-002 / P-006

| gap_id | Current state | Gap | Owner Phase | Promotion map link |
|---|---|---|---|---|
| Q3-DOC-001 | Workarounds in `06-consumer-path.md` | Not in `02-configuration.md` / `01-install.md` as Boot checklist | **P-002** | A-TYP-012/013 dialect live in Batch A′ |
| Q3-DOC-002 | Troubleshooting § JSON functions | No Boot `application.yml` template for JSON + UUID together | **P-002** | C-JSON-001…004 Batch B tag + Boot narrative |
| Q3-DOC-003 | Demo converter + Jackson starter | Not「开箱即用」for integrators | **P-002** + **P-006** | A-XCUT-009 **P-004** demo-live clarification |
| Q3-IMPL-001 | Demo C′ IT PASS (I-006) | Boot alignment: dependency + config + IT as product path | **P-006** | EV-I008-BOOT-001 |
| Q3-EV-001 | Dialect JSON/UUID IT exists | Boot path Accept artifact (Q4 overlap) | **P-006** + **P-007** | Batch B Ruler C JSON rows |

**Boot checklist (P-002 draft, P-006 implement):**

1. `spring-boot-starter-jackson` (Hibernate JSON `FormatMapper`)
2. UUID → `varchar(36)` + `AttributeConverter` (avoid JDBC `[E50044]`)
3. `hibernate.type.preferred_uuid_jdbc_type` alignment with GAV **7.4.5.Final**
4. `JSON_FUNCTIONS_ENABLED` when using HQL JSON aggregates

---

## Q4 — Offline ≠ production proof → P-002 / P-007

| gap_id | Current state | Gap | Owner Phase | Promotion map link |
|---|---|---|---|---|
| Q4-DOC-001 | `docs/verification.md` L83–85 | Offline `verify.py` PASS true for CI; L72 over-claims live | **P-002** | Honest counts in SSOT summary (Q1-05) |
| Q4-DOC-002 | `docs/user-guide/03-verify.md` | No I-008 Accept full-reactor live log requirement | **P-002** | EV-I008-LIVE-001 |
| Q4-DOC-003 | `06-consumer-path.md` L107–108 | Offline VERIFY equated with integration readiness | **P-002** | A-XCUT-009 demo-live — **P-004** |
| Q4-DOC-004 | `docs/production-readiness.md` | No I-008 Accept live-log convention | **P-002** + **P-007** | See live-log paths below |
| Q4-EV-001 | I-007 logs under `harness/evidence/test/I-007/` | No I-008 Accept-level full-reactor live evidence | **P-007** | EV-I008-LIVE-001 |
| Q4-EV-002 | `harness/verification.json` test gate OFF | Accept adds live run artifact; does not replace verify contract | **P-007** | Gate: `XUGU_RUN_IT=true` |

**Required Accept language (P-002):** offline `VERIFY PASS` proves wiring + unit; Initiative Accept requires gated **full reactor** green + deposited log (`SKIPPED_INFRA` documented otherwise).

**Live-log convention (named for P-005/P-007):**

| Phase | Directory | Key files |
|---|---|---|
| P-005 | `harness/evidence/test/I-008/P-005/` | `mvn-test-live-it.log`, `IT-RESULT.txt` |
| P-006 | `harness/evidence/test/I-008/P-006/` | `mvn-test-live-demo.log`, `IT-RESULT.txt` |
| P-007 | `harness/evidence/test/I-008/P-007/` | `mvn-test-live-it-final.log`, `IT-RESULT.txt` |

Gate: `XUGU_RUN_IT=true` or `-Dxugu.run.integration=true`.

---

## Cross-track evidence gaps

| gap_id | Description | Phase |
|---|---|---|
| EV-I008-LIVE-001 | No `harness/evidence/test/I-008/**` live logs | **P-005/P-007** |
| EV-I008-LOCK-001 | No lock semantic behavioral IT artifact bundle | **P-005** |
| EV-I008-BOOT-001 | No I-008 Boot out-of-box verification + live demo log | **P-006/P-007** |

---

## Phase owner rollup

| Phase | Scope from promotion map | Row / gap count |
|---|---|---:|
| **P-002** | Q1 doc honesty; Q2/Q3/Q4 user-facing docs | 4 dishonest sources + doc gaps |
| **P-003** | Batch A (19) + Batch A′ (58) | **77** matrix rows |
| **P-004** | Batch B (16) | **16** matrix rows |
| **P-005** | Q2 live lock evidence | 3 EV gaps |
| **P-006** | Q3 Boot out-of-box implement | 1 impl + doc support |
| **P-007** | Q4 Accept full-reactor live log | 2 EV gaps |

---

## Boundary reminders

- I-006 Boot SSOT: **41/41 FROZEN** — Track B deepens behavior; no silent Boot row expansion.
- GAV **7.4.5.Final**; **NONE** only; native dialect; **NOT Ship**; Q5 **OUT**.
