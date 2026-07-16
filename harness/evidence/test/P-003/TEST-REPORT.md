# P-003 Test Report

> Phase: `P-003`  
> Initiative: `I-002`  
> Build: `B-003`  
> Invocation: `test-p003-20260716`  
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
| Product code changes by test | none (docs-only Phase) |

## Commands and exit codes

| # | Command | Exit | Result |
|---|---|---:|---|
| 1 | `mvn -q -DskipTests package` | 0 | PASS |
| 2 | `mvn -q test` | 0 | PASS (offline) |
| 3 | `mvn -q test "-Dxugu.run.integration=true"` | 0 | PASS (incl. P-001 HQL page + P-002 schema validate IT) |
| 4 | `python harness/scripts/verify.py --phase P-003 --evidence harness/evidence/test/P-003/verification.json` | 0 | **VERIFY PASS** |
| 5 | `python harness/scripts/harness_check.py` | 0 | **HARNESS_CHECK PASS** |
| 6 | `python harness/scripts/branch_check.py` | 0 | **BRANCH_CHECK PASS** |

## Observed flow: full-verify-pass

| Check | Expected | Observed | Result |
|---|---|---|---|
| build | EXIT 0 | EXIT 0 | PASS |
| offline test | EXIT 0 | EXIT 0 | PASS |
| gated IT | EXIT 0 | EXIT 0 | PASS |
| verify.py | VERIFY PASS | PASS in verification.json | PASS |
| harness_check | PASS | PASS (Standard) | PASS |
| branch_check | working branch | `fix/i-002-hql-pagination-sequence-metadata` | PASS |

## Observed flow: matrix-docs-aligned-with-hotfix

Spot-check (docs-only; no dialect rewrite):

| Topic | Doc location | Matches delivery | Result |
|---|---|---|---|
| HQL LIMIT/OFFSET not ANSI FETCH | matrix A-PAG-* + troubleshooting §2 | Yes | PASS |
| FOR UPDATE → LIMIT → WAIT | matrix A-LCK-* + troubleshooting §1 | Yes | PASS |
| all_sequences validate | matrix A-SEQ-001 + troubleshooting §3 | Yes | PASS |
| Same GAV 7.4.5.Final | README / contract §8 | Yes | PASS |

## External: xugu-hibernate-test

**N/A** (not run; non-unique extra evidence only if executed). Primary evidence = this repo IT + VERIFY.

## Project verify evidence

- Path: `harness/evidence/test/P-003/verification.json`
- Overall status: `PASS`
- Required: build PASS, test PASS

## Version

GAV remains **7.4.5.Final** (dialect/pom.xml).

## Harness note

Fixed prior P-002 ACCEPTANCE missing harness line `- Decision: \`accepted\`` so `harness_check` / verify gate can PASS (semantic contract).
