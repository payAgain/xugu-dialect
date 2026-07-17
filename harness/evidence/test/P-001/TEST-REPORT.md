# P-001 Test Report (independent test role)

> Phase: `P-001`  
> Initiative: `I-004`  
> Build: `B-001`  
> Invocation: `test-p001-20260717`  
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
| HEAD (test time) | `87ebc578c9a5b53b33106c6eef4575fe06c3d611` |
| Product code changes by test | none |
| Live DB | XuguDB `jdbc:xugu://127.0.0.1:5138/SYSTEM?...&compatiblemode=NONE` (XuguDB JDBC Driver; XuguDialect; 12.0) |

## Scope under test

RP-01 delivered DROP SEQUENCE IF EXISTS via `XuguSequenceSupport.getDropSequenceString` plus unit + `XuguAutoSequenceDropIT`.
RP-02 independently re-ran package / offline test / gated IT / project verify.

## Commands and exit codes

| # | Command | Exit | Result |
|---|---|---:|---|
| 1 | `mvn -q -DskipTests package` | 0 | PASS |
| 2 | `mvn -q test` | 0 | PASS (IT skipped under gate) |
| 3 | `mvn -q test "-Dxugu.run.integration=true"` | 0 | PASS (live XuguDB) |
| 4 | `python harness/scripts/verify.py --phase P-001 --evidence harness/evidence/test/P-001/verification.json` | 0 | **VERIFY PASS** |
| 5 | `python harness/scripts/harness_check.py` | 0 | HARNESS_CHECK PASS |
| 6 | `python harness/scripts/branch_check.py` | 0 | BRANCH_CHECK PASS |

Focused confirm (surefire after verify offline overwrite):
`mvn -q -pl dialect -am test -Dtest=XuguAutoSequenceDropIT,XuguIdentitySequenceTest -Dxugu.run.integration=true` → EXIT 0  
- `XuguAutoSequenceDropIT`: 2/2 PASS  
- `XuguIdentitySequenceTest`: 7/7 PASS  

## Acceptance mapping

| Criterion | Result | Evidence |
|---|---|---|
| Drop SQL contains `if exists` | PASS | unit + live SQL `drop sequence if exists HIB_I004_P001_AUTO_SEQ` |
| AUTO create-drop / absent sequence no E7002 halt | PASS | [E7002] WARN only; EXIT 0 |
| ORM IT PASS | PASS | `XuguAutoSequenceDropIT` 2/2 |
| Project VERIFY | PASS | `verification.json` status PASS |

## Observed flow

- `orm-auto-sequence-create-drop-idempotent` → **passed** (XuguAutoSequenceDropIT on live DB)

## Project verify evidence

- Path: `harness/evidence/test/P-001/verification.json`
- Status: **PASS** (required `build` + `test`; harness PASS; lint N/A)

## Forbidden respected

- No business/product code by test
- No git commit / Accept / Ship / version bump
- No P-002 scope

## Next

RP-03 reviewer
