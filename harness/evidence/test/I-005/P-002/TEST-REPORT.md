# I-005 / P-002 Test Report (test role RP-02)

> Phase: `P-002`  
> Initiative: `I-005`  
> Build: `B-001`  
> Invocation: `test-i005-p002-20260717`  
> Step: `RP-02`  
> Role: `test` (independent of implementer RP-01)  
> Verdict: **PARTIAL — default gate PASS; live IT FAIL**  
> Date: 2026-07-17

## Environment

| Item | Value |
|---|---|
| Maven | Apache Maven 3.9.9 (`C:\Users\admin\tools\apache-maven-3.9.9`) |
| JDK | Oracle 21.0.1 (`D:\app\JDK-21`) |
| Working directory | `E:\Work\java\hibernate-test` |
| Branch | `feat/i-005-production-regression-baseline` |
| HEAD | `5a40af4ede657c8abf53815776cca07bf8ca02f0` |
| Product code changes by test | none |
| Live DB | XuguDB reachable at test time (dialect IT skipped count = 0 under live gate) |

## Scope under test

P-002 gap closure for I-005 production regression baseline: unit + live IT entrypoints for matrix rows previously tagged `gap_action=P-002`. SSOT audit only (test role did not modify `contracts/production-regression-baseline.md`).

## Commands and exit codes

| # | Command | Exit | Result |
|---|---|---:|---|
| 1 | `mvn -q -DskipTests package` | 0 | **PASS** |
| 2 | `mvn -q test` (default gate; IT skipped) | 0 | **PASS** |
| 3 | `XUGU_RUN_IT=true mvn -q test` (live IT) | 1 | **FAIL** — see first failure below |
| 4 | `python harness/scripts/verify.py --phase I-005/P-002 --evidence harness/evidence/test/I-005/P-002/verification-default-gate.json` | 1 | **VERIFY FAIL** (harness_check semantic contract; build/test commands not reached) |

Log artifacts:

- `harness/evidence/test/I-005/P-002/mvn-package.log`
- `harness/evidence/test/I-005/P-002/mvn-test-offline.log`
- `harness/evidence/test/I-005/P-002/mvn-test-live-it.log`
- `harness/evidence/test/I-005/P-002/surefire-summary-offline.txt`
- `harness/evidence/test/I-005/P-002/surefire-summary-live-it.txt`
- `harness/evidence/test/I-005/P-002/IT-RESULT.txt`

## Test counts

### Default gate (`mvn -q test`, IT gate OFF)

| Module | Tests run | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| dialect | 105 | 0 | 0 | 32 |
| Full reactor | (exit 0) | — | — | IT classes skipped under gate |

### Live IT (`XUGU_RUN_IT=true mvn -q test`)

| Module | Tests run | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| dialect | 105 | 1 | 0 | 0 |
| Full reactor | 108 | 1 | 0 | 1 |

## First failure (live IT)

| Field | Value |
|---|---|
| Class | `com.xugu.dialect.it.XuguExceptionMappingIT` |
| Method | `sessionNotNullViolationExtractsFieldNameWhenPresent` |
| Line | 147 |
| Matrix row | `C-EXC-002` |
| Observed | Hibernate raised `PropertyValueException` before JDBC/ORM constraint mapping; assertion expected `ConstraintViolationException` in cause chain |
| Cause excerpt | `not-null property references a null or transient value for entity com.xugu.dialect.it.entities.I005P002NotNullEntity.email` |

Surefire: `dialect/target/surefire-reports/com.xugu.dialect.it.XuguExceptionMappingIT.txt`

## SSOT contract audit (P-002 gaps)

Audited `contracts/production-regression-baseline.md` (read-only):

| Check | Result |
|---|---|
| P-002 hard-gap IDs (`A-TYP-019`, `A-DDL-005`, `A-XCUT-001`, `A-XCUT-005`, `A-SCH-014`, `C-EXC-002`) marked **covered** in SSOT primary table | **PASS** (document state) |
| Only **可实现** gap among former P-002 scope | **C-BULK-002** (`gap_action=P-004`) |
| `gap (可实现)` count | 1 |
| `gap_action` routing: P-002 closed; P-004 owns C-BULK-002 | **PASS** |

**Note:** Live IT failure on `C-EXC-002` contradicts SSOT **covered** status for that row; orchestrator should treat as potential blocker despite SSOT text.

## Live IT status summary

| Gate | Status | Reason |
|---|---|---|
| Default (`mvn -q test`) | **PASS** | exit 0; 32 IT skipped as expected |
| Live (`XUGU_RUN_IT=true`) | **FAIL** | 1 failure in `XuguExceptionMappingIT` (C-EXC-002) |

## Forbidden respected

- No dialect Java implementation changes by test
- No git commit / Accept / Ship
- Evidence written only under authorized test paths

## Next

RP-03 reviewer; orchestrator decision on C-EXC-002 live IT failure vs SSOT covered claim.
