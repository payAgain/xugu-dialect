# P-002 Test Report

> Phase: `P-002`  
> Initiative: `I-002`  
> Build: `B-002`  
> Invocation: `test-p002-20260716`  
> Role: `test` (independent of implementer evidence writers)  
> Verdict: **PASS**  
> Date: 2026-07-16

## Environment

| Item | Value |
|---|---|
| Maven | Apache Maven 3.9.9 |
| Working directory | `E:\Work\java\hibernate-test` |
| Branch | `fix/i-002-hql-pagination-sequence-metadata` |
| Live DB | XuguDB @ 127.0.0.1:5138 (`compatiblemode=NONE`) |
| Product code changes by test | none |

## Commands and exit codes

| # | Command | Exit | Result |
|---|---|---:|---|
| 1 | `mvn -q test` | 0 | PASS (offline) |
| 2 | `mvn -q test "-Dxugu.run.integration=true"` | 0 | PASS (incl. `XuguSchemaValidateIT`) |
| 3 | `python harness/scripts/verify.py --phase P-002 --evidence harness/evidence/test/P-002/verification.json` | 0 | **VERIFY PASS** |

## Observed flow: schema-validate-sequence-metadata-real-db

| Scenario | Expected | Observed | Result |
|---|---|---|---|
| Sequence exists (`HIB_P002_SEQ_GEN`) | validate succeeds | Action.VALIDATE EXIT without missing-sequence | PASS |
| Sequence visible in catalog | row in `all_sequences` | JDBC `select seq_name from all_sequences where seq_name=?` returns row | PASS |
| Sequence dropped / missing | validate fails diagnostically | exception message mentions missing sequence | PASS |

## Surefire

`XuguSchemaValidateIT`: Tests run: 2, Failures: 0, Errors: 0, Skipped: 0  
Copied to `harness/evidence/test/P-002/`.

## Project verify evidence

- Path: `harness/evidence/test/P-002/verification.json`
- Overall status: `PASS`
- Required: build PASS, test PASS

## Version

GAV remains **7.4.5.Final** (spot-check dialect/pom.xml).
