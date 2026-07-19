# I-007 / P-004 Test Report (test role RP-02)

> Phase: `P-004`  
> Initiative: `I-007`  
> Build: `B-001`  
> Invocation: `inv-i007-p004-rp02-test`  
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
| HEAD | `0af1e5e` |
| Closure path | **covered-live** (Track C three-pack) |
| Product code changes by test | none |
| `org/` touched by test | no |

## P-004 deliverable verification

| Check | Result | Evidence |
|---|---|---|
| Offline build green | **PASS** | `mvn-package-offline.txt` exit 0 |
| Offline reactor test green | **PASS** | dialect 141/0/0/54; demo 28/0/0/23 |
| Live dialect `-am` green with `XUGU_RUN_IT=true` | **PASS** | dialect 141/0/0/18 |
| C-JSON-005 live IT PASS | **PASS** | `XuguJsonSubsetDeepenIT#jsonSubsetDeepen_Hql_C_JSON_005` |
| A-TYP-015 / C-DDL-005 live IT PASS | **PASS** | `XuguArrayTypeIT#arrayColumnRoundTrip_A_TYP_015_C_DDL_005` |
| A-SEQ-006 live IT PASS | **PASS** | `XuguAlterSequenceIT#alterSequenceStartWithAndIncrement_A_SEQ_006` |
| SSOT four rows = `covered-live` (read-only) | **PASS** | `contracts/production-regression-baseline.md` L184–192 |
| SSOT rewrite needed | **NO** | status already correct |

## Commands and exit codes

| # | Command | Exit | Result |
|---|---|---:|---|
| 1 | `mvn -q -DskipTests package` | 0 | **PASS** |
| 2 | `mvn -q test` (gate off) | 0 | **PASS** |
| 3 | `$env:XUGU_RUN_IT='true'; mvn -q -pl dialect -am test` | 0 | **PASS** |
| 4 | `$env:XUGU_RUN_IT='true'; mvn -q -pl dialect test "-Dtest=XuguJsonSubsetDeepenIT,XuguArrayTypeIT,XuguAlterSequenceIT"` | 0 | **PASS** — 3/3 theme ITs |
| 5 | `python harness/scripts/verify.py --phase P-004` | 0 | **PASS** |

## Test counts

### Offline (`XUGU_RUN_IT` unset)

| Module | Tests run | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| dialect | 141 | 0 | 0 | 54 |
| demo-spring-boot | 28 | 0 | 0 | 23 |

P-004 gated ITs offline: each 1 run / 1 skipped (gate off) — expected.

### Live (`XUGU_RUN_IT=true`)

| Scope | Tests run | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| dialect `-am` | 141 | 0 | 0 | 18 |
| P-004 theme ITs (targeted) | 3 | 0 | 0 | 0 |

| Theme | matrix_id | IT method | Live result |
|---|---|---|---|
| JSON subset deepen | C-JSON-005 | `jsonSubsetDeepen_Hql_C_JSON_005` | **PASS** |
| ARRAY type + DDL | A-TYP-015, C-DDL-005 | `arrayColumnRoundTrip_A_TYP_015_C_DDL_005` | **PASS** |
| ALTER SEQUENCE | A-SEQ-006 | `alterSequenceStartWithAndIncrement_A_SEQ_006` | **PASS** |

## Artifacts

- `harness/evidence/test/I-007/P-004/mvn-package-offline.txt`
- `harness/evidence/test/I-007/P-004/mvn-test-offline.txt`
- `harness/evidence/test/I-007/P-004/mvn-test-live-it.txt`
- `harness/evidence/test/I-007/P-004/mvn-test-live-p004-it.txt`
- `harness/evidence/test/I-007/P-004/surefire-summary-offline.txt`
- `harness/evidence/test/I-007/P-004/surefire-summary-live-dialect.txt`
- `harness/evidence/test/I-007/P-004/IT-RESULT.txt`
- `harness/evidence/test/I-007/P-004/TEST-com.xugu.dialect.it.XuguJsonSubsetDeepenIT.xml`
- `harness/evidence/test/I-007/P-004/TEST-com.xugu.dialect.it.XuguArrayTypeIT.xml`
- `harness/evidence/test/I-007/P-004/TEST-com.xugu.dialect.it.XuguAlterSequenceIT.xml`
- `harness/evidence/test/I-007/P-004/verification.json`
- `harness/handoffs/test/I-007-P-004.yaml`

## SSOT read-only audit

`contracts/production-regression-baseline.md` lists all four P-004 themes as **covered-live**:

- **C-JSON-005** — `XuguJsonSubsetDeepenIT#jsonSubsetDeepen_Hql_C_JSON_005`
- **A-TYP-015** — `XuguArrayTypeIT#arrayColumnRoundTrip_A_TYP_015_C_DDL_005`
- **C-DDL-005** — `XuguArrayTypeIT#arrayColumnRoundTrip_A_TYP_015_C_DDL_005`
- **A-SEQ-006** — `XuguAlterSequenceIT#alterSequenceStartWithAndIncrement_A_SEQ_006`

No SSOT rewrite performed by test role.

## Verdict

**RP-02 PASS.** Independent verification confirms implementer RP-01 claim: Track C three-pack closed **covered-live** via live ITs on real XuGuDB. Advance to RP-03 reviewer. No commit / Accept / Ship by test.
