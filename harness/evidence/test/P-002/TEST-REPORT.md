# P-002 Test Report (independent test role)

> Phase: `P-002`  
> Initiative: `I-004`  
> Build: `B-001`  
> Invocation: `test-p002-20260717`  
> Step: `RP-02`  
> Role: `test` (independent of implementer RP-01)  
> Verdict: **PASS**  
> Date: 2026-07-17

## Environment

| Item | Value |
|---|---|
| Maven | Apache Maven 3.9.9 |
| JDK | Oracle 21.0.1 |
| Working directory | `E:\Work\java\hibernate-test` |
| Branch | `fix/i-004-sequence-drop-identity-reserved` |
| HEAD (committed, test time) | `8833a22cd1039ea8237fbfe0206fad940718d81b` |
| Working tree | RP-01 dialect + IT changes uncommitted (verified in-tree) |
| Product code changes by test | none |
| Live DB | XuguDB `jdbc:xugu://127.0.0.1:5138/SYSTEM?...&compatiblemode=NONE` (XuguDB JDBC Driver; XuguDialect; 12.0) |

## Scope under test

RP-01 delivered `getDefaultUseGetGeneratedKeys=false` so Hibernate uses `select last_insert_id() from dual` instead of JDBC RETURN_GENERATED_KEYS, plus unit + `XuguReservedIdentityIT` for reserved table `"order"`.
RP-02 independently re-ran package / offline test / gated IT / project verify.

## Commands and exit codes

| # | Command | Exit | Result |
|---|---|---:|---|
| 1 | `mvn -q -DskipTests package` | 0 | PASS |
| 2 | `mvn -q test` | 0 | PASS (IT skipped under gate) |
| 3 | `mvn -q test "-Dxugu.run.integration=true"` | 0 | PASS (live XuguDB) |
| 4 | `python harness/scripts/verify.py --phase P-002 --evidence harness/evidence/test/P-002/verification.json` | 0 | **VERIFY PASS** |
| 5 | `python harness/scripts/harness_check.py` | 0 | HARNESS_CHECK PASS |
| 6 | `python harness/scripts/branch_check.py` | 0 | BRANCH_CHECK PASS |

## Acceptance mapping

| Criterion | Result | Evidence |
|---|---|---|
| IDENTITY persist on reserved table `"order"` | PASS | XuguReservedIdentityIT 1/1 |
| Id backfilled; no unexpected ORDER | PASS | IT-RESULT + integration log |
| Prefer last_insert_id over getGeneratedKeys | PASS | unit assertFalse + live SQL |
| Project VERIFY | PASS | verification.json status PASS |

## Observed flow

- `orm-identity-reserved-table-persist` → **passed** (XuguReservedIdentityIT on live DB)

## Project verify evidence

- Path: `harness/evidence/test/P-002/verification.json`
- Status: **PASS** (required `build` + `test`; harness PASS; lint N/A)

## Forbidden respected

- No business/product code by test
- No git commit / Accept / Ship / version bump
- No JDBC driver changes

## Next

RP-03 reviewer (risk_score 8)
