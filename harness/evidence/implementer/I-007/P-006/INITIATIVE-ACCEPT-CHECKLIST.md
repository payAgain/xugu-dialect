# I-007 Initiative Accept checklist (prep — NOT Ship)

> Prepared by: implementer P-006 / RP-01 (`inv-i007-p006-rp01-implementer`)  
> Date: 2026-07-19T15:45:00+08:00  
> Purpose: Human Gate may **Initiative Accept** I-007 after P-006 accepted + RP-02/RP-03 complete.  
> **Ship / tag / push / Maven Central remain unchecked.**

## Prerequisite Phases

| Phase | Topic | Status | SHA |
|---|---|---|---|
| P-001 | SSOT gap map + C-BULK-002 strategy lock | accepted | `d0c57b1` |
| P-002 | Track A — C-BULK-002 **covered-live** | accepted | `682c65d` |
| P-003 | Thin fold → P-004 | accepted | `0af1e5e` |
| P-004 | Track C — JSON / ARRAY / ALTER SEQUENCE **covered-live** | accepted | `6a3385d` |
| P-005 | Track B — Flyway + Demo deepening | accepted | `f713248` |
| P-006 | Docs align + VERIFY PASS Accept prep | **deposit** (RP-01) | *(pending Accept commit)* |

## Track outcomes (SSOT)

- [x] **A** — C-BULK-002 = **covered-live** (`XuguBulkMutationIT#bulkInsertOnJoinedInheritanceWithIdentitySucceeds_C_BULK_002`)
- [x] **B** — B-FLY-001 / B-DEMO-001…003 implemented; Boot SSOT **41** rows unchanged
- [x] **C** — C-JSON-005 / A-TYP-015 / C-DDL-005 / A-SEQ-006 = **covered-live**
- [x] GAV still `com.xugu:xugu-dialect:7.4.5.Final`
- [x] `compatiblemode=NONE` only (no MySQL/Oracle compat product line)

## Docs ↔ SSOT ↔ user guide

- [x] C-BULK-002 stale **known-limit** removed — [`docs/user-guide/03-verify.md`](../../../../docs/user-guide/03-verify.md), [`04-feature-matrix.md`](../../../../docs/user-guide/04-feature-matrix.md), [`docs/verification.md`](../../../../docs/verification.md)
- [x] Track C pointer — [`docs/p004-track-c-capabilities.md`](../../../../docs/p004-track-c-capabilities.md) xref in user guide
- [x] Track B Flyway/Demo — [`docs/user-guide/06-consumer-path.md`](../../../../docs/user-guide/06-consumer-path.md) § I-007 Track B
- [x] Ruler C rollup nit — [`contracts/feature-matrix-i003-ruler-c.md`](../../../../contracts/feature-matrix-i003-ruler-c.md) (C-JSON-005 / C-DDL-005 no longer 延后)
- [x] Consumer-path Flyway offline method — `DemoOfflineSmokeTest#flywayXuguPluginAndMigrationOnClasspath` aligned in SSOT + user guide
- [x] I-007 plan SSOT — [`contracts/i007-capability-hardening-plan.md`](../../../../contracts/i007-capability-hardening-plan.md)
- [x] Root [`README.md`](../../../../README.md) I-007 pointer

## Verification (RP-02 — test role)

- [ ] `mvn -q -DskipTests package` OK
- [ ] `mvn -q test` offline green
- [ ] `python harness/scripts/verify.py --phase P-006 --evidence harness/evidence/test/I-007/P-006/verification.json` → **VERIFY PASS**
- [ ] When DB available: live evidence pointers under `harness/evidence/test/I-007/P-00*/` audited
- [ ] Reviewer RP-03 readonly audit (NOT Ship)

## Hygiene

- [x] Leftover `dialect/Probe.java` removed (P-004 scratch)
- [x] Root `org/` / root `META-INF/` dumps **not** committed

## Explicitly out of scope (must stay unchecked)

- [ ] Ship / release tag
- [ ] `git push` to protected / publish remotes as Ship gate
- [ ] Maven Central publish
- [ ] GAV bump beyond `7.4.5.Final`

## Human Gate next

1. Complete P-006 RP-02 (test) + RP-03 (reviewer)
2. Orchestrator Accept P-006 + must-commit on working branch
3. Propose **Initiative Accept** for I-007 (**without** Ship)
