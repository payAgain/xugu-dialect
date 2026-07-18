# P-003 Test Report — I-006

> Phase: P-003 | Build: B-001 | Initiative: I-006  
> Role: test | step: RP-02 | invocation_id: `inv-i006-p003-rp02-test`  
> Branch: `feat/i-006-consumer-path-coverage`  
> Date: 2026-07-18

## Session Briefing (test)

- Current Goal: validate Layer B B-both (association + SEQUENCE) offline + live gates after RP-01
- Working Branch: `feat/i-006-consumer-path-coverage` (BRANCH_CHECK PASS)
- GAV: `com.xugu:xugu-dialect:7.4.5.Final` (unchanged)
- Product Java edited by test: **none** (`org/` untouched)

## Commands

| Step | Command | Exit | Result |
|---|---|---:|---|
| branch_check | `python harness/scripts/branch_check.py` | 0 | PASS |
| build | `mvn -q -DskipTests package` | 0 | PASS |
| test (offline) | `mvn -q test` | 0 | PASS |
| live demo IT | `$env:XUGU_RUN_IT='true'; mvn -pl demo-spring-boot -am test` | 0 | PASS |
| harness_check | `python harness/scripts/harness_check.py` | 0 | PASS |
| verify.py | `python harness/scripts/verify.py --phase P-003 --evidence .../verification.json` | 0 | **VERIFY PASS** |

## Offline counts

| Module | run | fail | error | skip |
|---|---:|---:|---:|---:|
| xugu-dialect | 139 | 0 | 0 | 54 |
| demo-spring-boot | **23** | **0** | **0** | **19** |
| reactor (sum) | 162 | 0 | 0 | 73 |

Offline demo: 4 always-on smoke green (`DemoOfflineSmokeTest` incl. Layer B prefix/SEQUENCE smoke); 19 gated IT skipped (gate off) — expected.

Logs: `mvn-package-offline.log`, `mvn-test-offline.txt`, `surefire-summary-offline.txt`

## Live IT

| Field | Value |
|---|---|
| Status | **ran** — **PASS** (not SKIPPED_INFRA) |
| Probe | TCP `127.0.0.1:5138` OK (`live-db-probe.txt`) |
| Command | `XUGU_RUN_IT=true mvn -pl demo-spring-boot -am test` |
| demo | **23 / 0 / 0 / 0** (all Layer A+B gated IT executed) |
| dialect (-am) | 139 / 0 / 0 / 22 |
| Log | `mvn-test-live-demo.log`, `IT-RESULT.txt`, `surefire-summary-live-demo.txt` |

### Layer B live methods exercised

| row_id | entry_class#method | live result |
|---|---|---|
| A-SEQ-003 | `DemoSequenceIT#persistSequenceTicketGeneratesIncreasingIds` | PASS |
| A-SEQ-004 | `DemoSequenceIT#currvalMatchesLastGeneratedIdInSession` | PASS |
| A-SCH-011 | `DemoOfflineSmokeTest#layerBEntitiesUseHibDemoPrefixAndSequence`; `DemoConstraintRollbackIT#uniqueViolationMapsToConstraintViolationException` | PASS |
| A-SCH-012 | `DemoAssociationIT#persistDeptWithMembersAndFindViaFk` | PASS |
| A-LCK-001 | `DemoLockIT#pessimisticWriteLocksPersonRow` | PASS |
| A-LCK-003 | `DemoLockIT#pessimisticWriteWithNowaitTimeoutExecutes` | PASS |
| C-EXC-001 | `DemoConstraintRollbackIT#uniqueViolationMapsToConstraintViolationException` | PASS |
| A-XCUT-004 | `DemoConstraintRollbackIT#forcedFailureRollsBackDurableMemberRow` | PASS |
| C-EXC-002 | `DemoConstraintRollbackIT#notNullViolationExtractsConstraintNameWhenPresent` | PASS |

## SSOT Layer B confirmation

`contracts/consumer-path-baseline.md` — P-003 open gaps **0**; all **9** Layer B rows `covered` with `entry_class#method`.

**B-both:** association (`DemoDept` + `DemoDeptMember`) **AND** SEQUENCE (`DemoSeqTicket` + `HIB_DEMO_SEQ_TICKET_SEQ`) — both present.

Closed gap ids confirmed:

- `A-SEQ-003`, `A-SEQ-004`
- `A-SCH-011`, `A-SCH-012`
- `A-LCK-001`, `A-LCK-003`
- `C-EXC-001`, `A-XCUT-004`, `C-EXC-002`

Status header / changelog updated for RP-02 offline+live verification.

## Product code edits by test

None. No demo/dialect Java; never touched `org/`.

## Observed flow

- Flow: `layer-b-association-sequence-lock-exception-rollback`
- Observation: offline suite green without live DB (gated skips); under `XUGU_RUN_IT=true` association CRUD, SEQUENCE ids, pessimistic lock/NOWAIT, UNIQUE CVE, txn rollback, optional NOT NULL extract all executed and passed
- Evidence: this directory

## Recommendation for reviewer

**PASS** on P-003 technical acceptance (offline green + live Layer B Boot IT green + SSOT Layer B gaps 0 + B-both both landed).

`python harness/scripts/verify.py` → **VERIFY PASS** (harness + build + test all PASS). Next: RP-03 reviewer. No commit by test.
