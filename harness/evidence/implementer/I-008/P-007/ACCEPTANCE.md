# I-008 / P-007 Acceptance Evidence (implementer RP-01)

> Phase: `P-007`  
> Initiative: `I-008`  
> Build: `B-001`  
> Role step: `RP-01` / implementer  
> invocation_id: `inv-i008-p007-rp01-implementer`  
> Branch: `feat/i-008-production-quality-gaps`  
> HEAD: `1b2f29b` (uncommitted docs at deposit)  
> Result: **RP-01 artifacts complete** — final docs/SSOT alignment + frozen Initiative Accept checklist (**NOT Initiative Accept**)

## Approved scope

- Task: `harness/tasks/P-007.md`
- Dependencies: P-003, P-004, P-005, P-006 (all accepted)
- Allowed: `docs/**`, `contracts/**`, `README`, `harness/evidence/implementer/I-008/P-007/**`
- Forbidden respected: no new features, no Ship, no commit

## Q1–Q4 outcomes (docs ↔ SSOT aligned)

| Track | Phase owner | Outcome | Doc / SSOT anchor |
|---|---|---|---|
| **Q1** Honest counts | P-003 + P-004 | **83/98** covered-live + **15** known-limit-documented; no「94 covered-live」 | [`contracts/production-regression-baseline.md`](../../../../contracts/production-regression-baseline.md) § Summary; [`docs/user-guide/04-feature-matrix.md`](../../../../docs/user-guide/04-feature-matrix.md) § I-005 baseline counts |
| **Q2** Lock semantics | P-002 + P-005 | No SKIP LOCKED / FOR SHARE; `PESSIMISTIC_READ`→exclusive `FOR UPDATE` | [`docs/user-guide/07-lock-integration.md`](../../../../docs/user-guide/07-lock-integration.md); [`contracts/xugu-dialect.contract.md`](../../../../contracts/xugu-dialect.contract.md) §7.2 |
| **Q3** UUID/JSON Boot out-of-box | P-002 + P-006 | 4-item checklist **implemented** in demo | [`docs/user-guide/02-configuration.md`](../../../../docs/user-guide/02-configuration.md) § UUID/JSON; [`contracts/demo-spring-boot.contract.md`](../../../../contracts/demo-spring-boot.contract.md) |
| **Q4** Accept live evidence | P-002 + P-007 | Full reactor manifest frozen; offline VERIFY ≠ Accept | [`docs/user-guide/03-verify.md`](../../../../docs/user-guide/03-verify.md) § I-008 Accept; [`docs/verification.md`](../../../../docs/verification.md) § I-008 |

### Q5 — out of scope (one line)

**性能基准与 Hibernate 多版本兼容矩阵未做**（I-008 Scope PASS 明示）— published in [`docs/user-guide/README.md`](../../../../docs/user-guide/README.md), [`docs/user-guide/03-verify.md`](../../../../docs/user-guide/03-verify.md), [`docs/verification.md`](../../../../docs/verification.md), [`README.md`](../../../../README.md).

## Deliverables (RP-01)

| Artifact | Change |
|---|---|
| `docs/user-guide/02-configuration.md` | P-006「将实现/目标态」→ **已落地** language + contract xref |
| `docs/user-guide/README.md` | I-005 counts **83/98**; I-008 + Q5 line |
| `docs/user-guide/03-verify.md` | Q1 achieved counts; Q5 line; **§ golden path manifest** (Q4 freeze) |
| `docs/user-guide/04-feature-matrix.md` | Honest rollup post P-003/P-004 |
| `docs/verification.md` | I-005/I-008 count tables + Q5 row |
| `docs/production-readiness.md` | Q5 out-of-scope note |
| `contracts/xugu-dialect.contract.md` | §7.2 honest counts **83/98** |
| `README.md` | I-008 pointer + Q5 |
| `harness/evidence/implementer/I-008/P-007/ACCEPTANCE.md` | this file |
| `harness/evidence/implementer/I-008/P-007/verify.txt` | `verify.py` run record |

## Initiative Accept checklist (frozen prep — Human Gate only)

> **This checklist prepares Accept evidence requirements. Implementer does NOT claim Initiative Accept.**

### Prerequisite Phases (B-001)

| Phase | Topic | RP status | Evidence |
|---|---|---|---|
| P-001 | Gap map + promotion plan | accepted | `harness/evidence/architect-contract/I-008/P-001/` |
| P-002 | User docs honest counts + lock/UUID/Accept language | accepted | `harness/evidence/implementer/I-008/P-002/` |
| P-003 | Batch A + A′ — **83** covered-live path | accepted | `harness/evidence/implementer/I-008/P-003/` |
| P-004 | Batch B tag sweep + A-XCUT-009 demo-live | accepted | `harness/evidence/implementer/I-008/P-004/` |
| P-005 | Q2 lock behavioral + negative evidence | accepted | `harness/evidence/implementer/I-008/P-005/` |
| P-006 | Q3 Boot UUID/JSON out-of-box | accepted | `harness/evidence/implementer/I-008/P-006/` |
| P-007 | Docs终对齐 + Accept manifest | **accepted** | this file + test/reviewer evidence |

### SSOT / product constraints

- [x] GAV `com.xugu:xugu-dialect:7.4.5.Final` unchanged
- [x] `compatiblemode=NONE` only (Charter / contract default)
- [x] Honest regression rollup: **83** covered-live + **15** known-limit = **98** achievable rows
- [x] Boot consumer-path SSOT **41** rows, open gaps **0** (unchanged by I-008)
- [x] Q5 performance / multi-version matrix **out of scope** (documented)
- [ ] **Initiative Accept** declared by Human Gate (unchecked — not RP-01)

### Verification gates (RP-02 test owner)

- [x] `mvn -q -DskipTests package` — implementer run exit **0**
- [x] `mvn -q test` offline — implementer run exit **0**
- [x] `python harness/scripts/verify.py --phase P-007` → **VERIFY PASS** (RP-02; harness BOM fix in `harness/evidence/test/I-008/P-004/verification.json`)
- [x] Reviewer RP-03 readonly audit (NOT Ship) — **ACCEPT PASS**

### Q4 — full reactor live evidence (required for Initiative Accept)

**Command:** `XUGU_RUN_IT=true mvn -q test` (full reactor, gate ON)

**Archive path:** `harness/evidence/test/I-008/P-007/` — `mvn-test-live-it-final.log`, `IT-RESULT.txt`, surefire summary

**Offline-only is insufficient:** prior Phases P-003…P-006 documented **`SKIPPED_INFRA`** when `127.0.0.1:5138` unreachable. Accept requires live green or honest infra skip with Human Gate acknowledgment.

#### Golden path manifest (must be green on live DB)

| Domain | Matrix / capability | Primary live IT anchors |
|---|---|---|
| **Pagination** | A-PAG-001…003 | `XuguHqlPaginationIT#hqlSetFirstResultMaxResultsUsesLimitNotFetchFirst`; `XuguPaginationIT#limitAndOffsetReturnExpectedRows`; `DemoBootBaselineSmokeTest#pageableFindAllUsesLimitOffset` |
| **Lock** | A-LCK-001/003/005 | `XuguLockIT#pessimisticReadExecutesAsForUpdateNotShare`; `XuguLockIT#forUpdateExecutesAndSkipLockedUnsupported`; `XuguHqlPaginationIT#hqlLockAndPageEmitsForUpdateBeforeLimitAndWaitAfter` |
| **SEQUENCE** | A-SEQ-001/003/004/006 | `XuguIdentitySequenceIT#sequenceGeneratorPersist_A_SEQ_003_004_008`; `XuguSchemaValidateIT#schemaValidateSucceedsWhenSequenceExists`; `XuguAlterSequenceIT#alterSequenceStartWithAndIncrement_A_SEQ_006` |
| **IDENTITY** | A-IDN-003/004 | `XuguIdentitySequenceIT#identityPersistBackfillsId_A_IDN_003_004`; `DemoPersonCrudIT#persistAndFindPerson` |
| **UUID** | A-TYP-012 | `DemoUuidJsonOutOfBoxIT#uuidAndJsonGoldenPathWithDefaultBootWiring` |
| **JSON** | A-TYP-013, C-JSON-001…004 | `DemoUuidJsonOutOfBoxIT#uuidAndJsonGoldenPathWithDefaultBootWiring`; `XuguJsonAggregateIT#jsonColumnRoundTripAndHqlAggregates` |
| **HQL** | A-PAG + Layer A JPQL | `XuguHqlPaginationIT` (pagination + lock combo); `DemoBootBaselineSmokeTest#jpaPersistAndJpqlQueryRoundTrip` |

**Phase subset evidence (reference only — does not substitute full reactor):**

| Phase | Focus | Directory |
|---|---|---|
| P-005 | Lock live IT | `harness/evidence/test/I-008/P-005/` |
| P-006 | Boot UUID/JSON live IT | `harness/evidence/test/I-008/P-006/` |
| P-007 | **Accept-level** full reactor | `harness/evidence/test/I-008/P-007/` *(RP-02)* |

SSOT entrypoint rollup: [`contracts/production-regression-baseline.md`](../../../../contracts/production-regression-baseline.md) (pagination/lock/sequence/identity rows); consumer JSON/UUID: [`contracts/consumer-path-baseline.md`](../../../../contracts/consumer-path-baseline.md) Layer C′.

### Explicitly out of scope (must stay unchecked at Accept prep)

- [ ] Ship / release tag / Maven Central
- [ ] GAV bump beyond `7.4.5.Final`
- [ ] Q5 performance benchmarks / multi-version Hibernate matrix

## Validation (implementer)

| Command | Result | Notes |
|---|---|---|
| `mvn -q -DskipTests package` | **PASS** | exit 0 |
| `mvn -q test` | **PASS** | exit 0 (~12s); gate OFF — gated IT skipped |
| `python harness/scripts/verify.py` (implementer pre-fix) | **VERIFY FAIL** | exit 1 — UTF-8 BOM in P-004 verification.json; fixed by RP-02 |

## Acceptance criteria (RP-01)

| Criterion | Result | Evidence |
|---|---|---|
| Docs finalized with Q1–Q4 honest counts and cross-links | **PASS** | § Q1–Q4; deliverables table |
| `02-configuration.md` P-006 implemented language | **PASS** | § UUID/JSON checklist intro + yaml snippet |
| Q5 one-line out-of-scope in docs | **PASS** | § Q5 |
| Accept checklist frozen with full reactor golden path manifest | **PASS** | § Initiative Accept checklist + § Q4 manifest |
| GAV 7.4.5.Final NONE-only; NOT Ship | **PASS** | checklist constraints |
| `verify.py` VERIFY PASS | **PASS** | `harness/evidence/test/I-008/P-007/verification.json` |
| No commit | **PASS** | by instruction |

## Role pipeline

| Step | Role | Status | Evidence |
|---|---|---|---|
| RP-01 | implementer | **complete** | this file (`inv-i008-p007-rp01-implementer`) |
| RP-02 | test | **complete** | `harness/evidence/test/I-008/P-007/verification.json` (`inv-i008-p007-rp02-test`) |
| RP-03 | reviewer | **complete** | `harness/evidence/reviewer/I-008/P-007/REVIEW.md` (`inv-i008-p007-rp03-reviewer`) |

## Acceptance decision

- Decision: `accepted`
- Notes: docs/SSOT aligned; Accept checklist frozen; offline VERIFY PASS; full reactor live **SKIPPED_INFRA**; **NOT** Initiative Accept
