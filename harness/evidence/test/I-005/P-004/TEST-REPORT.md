# I-005 / P-004 Test Report (test role RP-02)

> Phase: `P-004`  
> Initiative: `I-005`  
> Build: `B-001`  
> Invocation: `test-i005-p004-rp02-20260717`  
> Step: `RP-02`  
> Role: `test` (independent of implementer RP-01)  
> Verdict: **PASS** (P-004 scope; reactor `mvn test` blocked by uncommitted demo WIP)  
> Date: 2026-07-17

## Environment

| Item | Value |
|---|---|
| Maven | Apache Maven 3.9.9 (`C:\Users\admin\tools\apache-maven-3.9.9`) |
| JDK | Oracle 21.0.1 (`D:\app\JDK-21`) |
| Working directory | `E:\Work\java\hibernate-test` |
| Branch | `feat/i-005-production-regression-baseline` |
| HEAD | `e892e9015356697948bedbecc0a6509a6f5d0e7a` |
| Chosen closure path | **known-limit-documented** (not live IT) |
| Product code changes by test | none |

## P-004 deliverable verification

| Check | Result | Evidence |
|---|---|---|
| SSOT `C-BULK-002` status = `known-limit-documented` (not ambiguous `gap`) | **PASS** | `contracts/production-regression-baseline.md` L177, explicit call-out L248–259; `gap (可实现)` count = 0 |
| `XuguBulkMutationSupportTest#fallbackSqmInsertStrategyWired_C_BULK_002` exists | **PASS** | `dialect/src/test/java/com/xugu/dialect/XuguBulkMutationSupportTest.java` L188–203 |
| User guide documents bulk insert limitation | **PASS** | `docs/user-guide/05-troubleshooting.md` §10; `docs/user-guide/04-feature-matrix.md` L51 |
| Live bulk insert IT waived (consistent with path) | **PASS** | SSOT gate = `unit`; no live insert IT claimed |

## Commands and exit codes

| # | Command | Exit | Result |
|---|---|---:|---|
| 1 | `mvn -q -DskipTests package` | 0 | **PASS** |
| 2 | `mvn -q test` (full reactor) | 1 | **FAIL** — demo-spring-boot testCompile (see below) |
| 3 | `mvn -q test -pl dialect` | 0 | **PASS** |
| 4 | `mvn -q test -pl dialect -Dtest=XuguBulkMutationSupportTest` | 0 | **PASS** — 3/3 including C-BULK-002 |
| 5 | `python harness/scripts/verify.py` | 1 | **VERIFY FAIL** — harness_check prior-phase semantic contract |

Log artifacts:

- `harness/evidence/test/I-005/P-004/mvn-package.log`
- `harness/evidence/test/I-005/P-004/mvn-test-offline.log`
- `harness/evidence/test/I-005/P-004/mvn-test-dialect.log`
- `harness/evidence/test/I-005/P-004/mvn-test-bulk-mutation.log`
- `harness/evidence/test/I-005/P-004/surefire-summary-dialect.txt`
- `harness/evidence/test/I-005/P-004/com.xugu.dialect.XuguBulkMutationSupportTest.txt`

## Test counts

### Dialect module (`mvn -q test -pl dialect`, IT gate OFF)

| Metric | Value |
|---|---:|
| Tests run | 139 |
| Failures | 0 |
| Errors | 0 |
| Skipped | 54 |

### C-BULK-002 target class

| Class | Tests run | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| `XuguBulkMutationSupportTest` | 3 | 0 | 0 | 0 |

Methods: `supportsSubqueryOnMutatingTableIsFalse_C_BULK_003`, `localTemporaryTableStrategyForBulkMutation_C_BULK_001`, **`fallbackSqmInsertStrategyWired_C_BULK_002`**.

## First failure (full reactor only)

| Field | Value |
|---|---|
| Module | `demo-spring-boot` |
| Phase | testCompile |
| File | `demo-spring-boot/src/test/java/com/xugu/demo/it/DemoBootBaselineSmokeTest.java:73` |
| Error | cannot find symbol `DemoPersonCrudIT.dropDemoTableStatic(String)` |
| Git state | file is **untracked** (`??`) — P-005 demo WIP, **out of P-004 scope** |
| P-004 impact | none — bulk insert closure is offline unit + SSOT/docs |

## SSOT contract audit (C-BULK-002)

| Check | Result |
|---|---|
| Primary table status | `known-limit-documented` |
| `gap_action` | `N/A` (closed I-005/P-004) |
| Explicit call-out section present | **PASS** |
| No dual state (`gap` + `wired`) | **PASS** |
| Summary: `gap (可实现)` | **0** |
| Summary: `known-limit-documented (可实现)` | **1** (C-BULK-002) |

## Forbidden respected

- No dialect Java implementation changes by test
- No git commit / Accept / Ship
- Evidence written only under authorized test paths

## Next

RP-03 reviewer — confirm no ambiguous C-BULK-002 state; orchestrator may defer full-reactor green until P-005 demo WIP lands or untracked file removed.
