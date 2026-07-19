# I-007 / P-002 Test Report (test role RP-02)

> Phase: `P-002`  
> Initiative: `I-007`  
> Build: `B-001`  
> Invocation: `inv-i007-p002-rp02-test`  
> Step: `RP-02`  
> Role: `test` (independent of implementer RP-01)  
> Verdict: **PASS**  
> Date: 2026-07-19

## Environment

| Item | Value |
|---|---|
| Maven | Apache Maven 3.9.9 (`C:\Users\admin\tools\apache-maven-3.9.9`) |
| Working directory | `E:\Work\java\hibernate-test` |
| Branch | `feat/i-007-capability-hardening-abc` |
| HEAD | `d0c57b1` |
| Closure path | **covered-live** |
| Product code changes by test | none |
| `org/` touched by test | no |

## P-002 deliverable verification

| Check | Result | Evidence |
|---|---|---|
| Offline build green | **PASS** | `mvn-package-offline.log` exit 0 |
| Offline reactor test green | **PASS** | dialect 140/0/0/55; demo 28/0/0/23 |
| Live dialect `-am` green with `XUGU_RUN_IT=true` | **PASS** | dialect 140/0/0/22 |
| `XuguBulkMutationIT#bulkInsertOnJoinedInheritanceWithIdentitySucceeds_C_BULK_002` live PASS | **PASS** | surefire-live XML + IT-RESULT.txt |
| SSOT `C-BULK-002` = `covered-live` (read-only) | **PASS** | `contracts/production-regression-baseline.md` L177, L248–260 |
| SSOT rewrite needed | **NO** | status already correct |

## Commands and exit codes

| # | Command | Exit | Result |
|---|---|---:|---|
| 1 | `mvn -q -DskipTests package` | 0 | **PASS** |
| 2 | `mvn -q test` | 0 | **PASS** |
| 3 | `$env:XUGU_RUN_IT='true'; mvn -q -pl dialect -am test` | 0 | **PASS** |
| 4 | `$env:XUGU_RUN_IT='true'; mvn test -pl dialect -am -Dtest=XuguBulkMutationIT` | 0 | **PASS** — 4/4 incl. C-BULK-002 |

## Test counts

### Offline (`XUGU_RUN_IT` unset)

| Module | Tests run | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| dialect | 140 | 0 | 0 | 55 |
| demo-spring-boot | 28 | 0 | 0 | 23 |

`XuguBulkMutationIT` offline: 4 run / 3 skipped (gate off) — expected.

### Live (`XUGU_RUN_IT=true`)

| Scope | Tests run | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| dialect `-am` | 140 | 0 | 0 | 22 |
| `XuguBulkMutationIT` | 4 | 0 | 0 | 0 |

C-BULK-002 target method `bulkInsertOnJoinedInheritanceWithIdentitySucceeds_C_BULK_002` executed in ~0.868s with no failure/error in surefire XML.

## Artifacts

- `harness/evidence/test/I-007/P-002/mvn-package-offline.log`
- `harness/evidence/test/I-007/P-002/mvn-test-offline.log`
- `harness/evidence/test/I-007/P-002/mvn-test-live-it.log`
- `harness/evidence/test/I-007/P-002/mvn-test-live-bulk-it-summary.log`
- `harness/evidence/test/I-007/P-002/surefire-summary-offline.txt`
- `harness/evidence/test/I-007/P-002/surefire-summary-live-dialect.txt`
- `harness/evidence/test/I-007/P-002/surefire-live/` (XuguBulkMutationIT txt + xml)
- `harness/evidence/test/I-007/P-002/IT-RESULT.txt`
- `harness/evidence/test/I-007/P-002/verification.json`
- `harness/handoffs/test/I-007-P-002.yaml`

## SSOT read-only audit

`contracts/production-regression-baseline.md` already lists **C-BULK-002** as **covered-live** with live IT reference and gap_action **N/A**. No SSOT rewrite performed by test role.

## Verdict

**RP-02 PASS.** Independent verification confirms implementer RP-01 claim: C-BULK-002 closed **covered-live** via live bulk-insert IT on JOINED+IDENTITY. Advance to RP-03 reviewer. No commit / Accept / Ship by test.
