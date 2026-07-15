# P-005 Test Report (independent test role)

> Phase: `P-005`  
> Initiative: `I-001`  
> Build: `B-005`  
> Invocation: `test-p005-20260715`  
> Role: `test` (independent context from implementer)  
> Verdict: **PASS**  
> Date: 2026-07-15

## Environment

| Item | Value |
|---|---|
| Maven | Apache Maven 3.9.9 (`C:\Users\admin\tools\apache-maven-3.9.9\bin` prepended to PATH) |
| JDK | Oracle 21.0.1 |
| Working directory | `E:\Work\java\hibernate-test` |
| Branch | `feat/i-001-xugu-dialect-major` |
| HEAD (test time) | `d925515c2be1ceb94334a63acc1464d467adb3ce` |
| Product code changes by test | none |
| Live DB | XuguDB `jdbc:xugu://127.0.0.1:5138/SYSTEM?...&compatiblemode=NONE` (driver: XuguDB JDBC Driver; dialect: XuguDialect; version: 12.0) |

## Commands and exit codes

| # | Command | Exit code | Result |
|---|---|---|---|
| 1 | `mvn -q test` (gate default/off) | 0 | PASS (11 IT skipped) |
| 2 | `mvn -q test -Dxugu.run.integration=true` | 0 | PASS (11 IT executed on real XuguDB) |
| 3 | `python harness/scripts/verify.py --phase P-005 --evidence harness/evidence/test/P-005/verification.json` | 0 | `VERIFY PASS` |

## Surefire counts

### Offline (`xugu.run.integration=false`)

| Suite | tests | failures | errors | skipped |
|---|---:|---:|---:|---:|
| `XuguDialectTest` | 7 | 0 | 0 | 0 |
| `XuguIdentitySequenceTest` | 6 | 0 | 0 | 0 |
| `XuguPaginationLockTest` | 10 | 0 | 0 | 0 |
| `XuguTypeRoundTripIT` | 4 | 0 | 0 | 4 |
| `XuguDdlIT` | 1 | 0 | 0 | 1 |
| `XuguBinarySchemaExportIT` | 1 | 0 | 0 | 1 |
| `XuguPaginationIT` | 1 | 0 | 0 | 1 |
| `XuguLockIT` | 2 | 0 | 0 | 2 |
| `XuguIdentitySequenceIT` | 2 | 0 | 0 | 2 |
| **Total** | **34** | **0** | **0** | **11** |

### Integration gate ON (real XuguDB)

| Suite | tests | failures | errors | skipped |
|---|---:|---:|---:|---:|
| `XuguDialectTest` | 7 | 0 | 0 | 0 |
| `XuguIdentitySequenceTest` | 6 | 0 | 0 | 0 |
| `XuguPaginationLockTest` | 10 | 0 | 0 | 0 |
| `XuguTypeRoundTripIT` | 4 | 0 | 0 | 0 |
| `XuguDdlIT` | 1 | 0 | 0 | 0 |
| `XuguBinarySchemaExportIT` | 1 | 0 | 0 | 0 |
| `XuguPaginationIT` | 1 | 0 | 0 | 0 |
| `XuguLockIT` | 2 | 0 | 0 | 0 |
| `XuguIdentitySequenceIT` | 2 | 0 | 0 | 0 |
| **Total** | **34** | **0** | **0** | **0** |

**IT summary:** 11 IT methods executed when gate ON, 0 failed, 0 skipped. Offline: 11 IT skipped via `Assumptions.assumeTrue(XuguITGate.isEnabled())`. Unit: 23 passed both runs (`7+6+10`).

## Project verify evidence

- Path: `harness/evidence/test/P-005/verification.json`
- Overall status: `PASS`
- Required checks: `build` PASS (exit 0), `test` PASS (exit 0)
- Optional: `lint` NOT_APPLICABLE
- Harness check embedded: `HARNESS_CHECK PASS`

## Locked SQL forms (spot-check)

| Capability | Locked form | Unit | Live IT (SHOW_SQL) | Result |
|---|---|---|---|---|
| IDENTITY DDL | `identity(1,1)` after type | `getIdentityColumnString` → `identity(1,1)`; no `auto_increment` | `create table HIB_P005_IDN (id bigint identity(1,1), …)` | PASS |
| Identity insert | omit id column | — | `insert into HIB_P005_IDN (name) values (?)` | PASS |
| Generated keys primary | JDBC `getGeneratedKeys` | dialect default + IT `USE_GET_GENERATED_KEYS=true` | id backfilled after persist/flush | PASS |
| Identity select fallback | `select last_insert_id() from dual` | unit exact match | JDBC `SELECT LAST_INSERT_ID() FROM DUAL` after insert | PASS |
| CREATE SEQUENCE | `create sequence … start with N increment by M` | SequenceSupport default path | `create sequence HIB_P005_SEQ_GEN start with 1 increment by 1` | PASS |
| NEXTVAL | `select <seq>.nextval from dual` | unit exact | `select HIB_P005_SEQ_GEN.nextval from dual` (×2) | PASS |
| CURRVAL | `select currval('<name>') from dual` | unit exact | probe `SELECT CURRVAL('HIB_P005_SEQ_GEN') FROM DUAL` → 10 after NEXTVAL 10 | PASS |
| DROP SEQUENCE | `drop sequence <name>` | — | `drop sequence HIB_P005_SEQ_GEN` | PASS |
| FROM DUAL | ` from dual` | `getFromDual()` | present on NEXTVAL selects | PASS |

**Rejected forms (implementer live probe; not re-claimed by dialect):** `NEXTVAL('seq')` FAIL; `seq.CURRVAL` FAIL — dialect does not emit these.

## Observed affected flows

| Flow | Method | Expected | Observed | Result | Evidence |
|---|---|---|---|---|---|
| identity-insert-real-db | `XuguIdentitySequenceIT.identityPersistBackfillsId_A_IDN_003_004` | IDENTITY DDL + insert omit id + id backfill + cleanup | `bigint identity(1,1)`; INSERT `(name)`; positive id backfilled; row readable; DROP | PASS | `mvn-test-integration.log`, `com.xugu.dialect.it.XuguIdentitySequenceIT.txt`, `IT-RESULT.txt` |
| sequence-generator-real-db | `XuguIdentitySequenceIT.sequenceGeneratorPersist_A_SEQ_003_004_008` | CREATE SEQUENCE; NEXTVAL/CURRVAL/DUAL; Hibernate persist increasing ids; DROP | Probe NEXTVAL=10 / CURRVAL=10; Hibernate two `nextval from dual` then inserts; `id2 > id1`; DROP table+seq | PASS | same |

## Readiness dimensions (test view)

| Dimension | Observation | Result |
|---|---|---|
| functional-correctness | Unit + real-DB IT pass for identity persist and sequence generator | PASS |
| data-integrity | Id backfill verified by SELECT; sequence ids strictly increasing; cleanup DROP | PASS |
| maintainability | IT gated; offline suite green without DB | PASS |
| compatibility | Hibernate 7.4 IdentityColumnSupport / SequenceSupport; no MySQL/Oracle Dialect inheritance in this Phase path | PASS |

## Residual notes

- Test role did not modify product code.
- DB unreachable with gate ON would be FAIL/blocker (no mock path); this run connected successfully.
- A-IDN-005 / A-SEQ-006 remain deferred per implementer (matrix 延后) — out of P-005 required flows.
- Next: RP-03 reviewer (risk_score=8, Full review required).

## Verdict

**PASS** — offline test / real-DB IT / verify green; both required observed flows evidenced; locked IDENTITY / SEQUENCE SQL forms confirmed on live XuguDB.
