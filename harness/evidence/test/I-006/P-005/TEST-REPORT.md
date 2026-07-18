# P-005 Test Report — I-006

> Phase: P-005 | Build: B-001 | Initiative: I-006  
> Role: test | step: RP-02 | invocation_id: `inv-i006-p005-rp02-test`  
> Branch: `feat/i-006-consumer-path-coverage`  
> Date: 2026-07-19  
> **Verdict: PASS** (docs spot-check OK; offline green; live demo IT 28/0/0/0; VERIFY PASS; GAV unchanged)

## Session Briefing (test)

- Current Goal: verify docs + SSOT freeze + offline/live consumer-path baseline for P-005 Accept prep
- Working Branch: `feat/i-006-consumer-path-coverage` (BRANCH_CHECK PASS)
- GAV: `com.xugu:xugu-dialect:7.4.5.Final` (confirmed in parent/dialect/demo POMs)
- Product Java edited by test: **none** (`org/` untouched)
- Docs: `docs/user-guide/06-consumer-path.md` present with offline + `XUGU_RUN_IT=true` run instructions

## Commands

| Step | Command | Exit | Result |
|---|---|---:|---|
| branch_check | `python harness/scripts/branch_check.py` | 0 | PASS |
| build | `mvn -q -DskipTests package` | 0 | PASS |
| test (offline) | `mvn -q test` (gate OFF) | 0 | PASS |
| live demo IT | `$env:XUGU_RUN_IT='true'; mvn -pl demo-spring-boot -am test` | 0 | PASS |
| harness_check | `python harness/scripts/harness_check.py` | 0 | PASS |
| verify.py | `python harness/scripts/verify.py --phase P-005 --evidence .../verification.json` | 0 | **VERIFY PASS** |

## Offline counts

| Module | run | fail | error | skip |
|---|---:|---:|---:|---:|
| xugu-dialect | 139 | 0 | 0 | 54 |
| demo-spring-boot | **28** | **0** | **0** | **23** |
| reactor (sum) | 167 | 0 | 0 | 77 |

Offline demo: always-on smoke green; 23 gated IT skipped (gate off) — expected.

Logs: `mvn-package-offline.log`, `mvn-test-offline.txt`, `surefire-summary-offline.txt`

## Live IT

| Field | Value |
|---|---|
| Status | **ran** — **PASS** (not SKIPPED_INFRA) |
| Probe | TCP `127.0.0.1:5138` OK (`live-db-probe.txt`) |
| Command | `XUGU_RUN_IT=true mvn -pl demo-spring-boot -am test` |
| demo | **28 / 0 / 0 / 0** |
| dialect (-am) | 139 / 0 / 0 / 22 |
| Log | `mvn-test-live-demo.log`, `IT-RESULT.txt`, `surefire-summary-live-demo.txt`, `surefire-live-demo/` |

## Docs spot-check

| Check | Result |
|---|---|
| `docs/user-guide/06-consumer-path.md` exists | **YES** |
| Documents offline `mvn test` + `verify.py` | **YES** |
| Documents `XUGU_RUN_IT=true` gated live | **YES** |
| States GAV `com.xugu:xugu-dialect:7.4.5.Final` | **YES** |
| Links SSOT `contracts/consumer-path-baseline.md` | **YES** |
| Notes Ship/Central out of scope | **YES** |

## SSOT / GAV confirmation

`contracts/consumer-path-baseline.md` —

| Check | Result |
|---|---|
| Status | **FROZEN** (P-005) |
| Boot-required open gaps | **0** |
| Boot-required rows | **41** (A=13 / B=9 / C′=19) |
| Demo `@Test` count | **28** (range 25–40) |
| GAV | `com.xugu:xugu-dialect:7.4.5.Final` |

POM confirmation: parent `com.xugu:xugu-dialect-parent:7.4.5.Final`; dialect module `com.xugu:xugu-dialect`; demo depends on same version — **unchanged**.

## Product code edits by test

None. No demo/dialect Java; never touched `org/`. Optional SSOT changelog note only.

## Observed flow

- Flow: `consumer-path-docs-aligned-verify-pass-accept-prep`
- Observation: consumer can follow `06-consumer-path.md` for offline + gated live; `verify.py` VERIFY PASS; live demo IT full green against reachable DB
- Evidence: this directory

## Recommendation for reviewer

**PASS** on P-005 technical acceptance (docs present + SSOT FROZEN gaps 0 + offline green + live 28/0/0/0 + GAV 7.4.5.Final + VERIFY PASS). Ship remains out of scope.

`python harness/scripts/verify.py` → **VERIFY PASS**. Next: RP-03 reviewer. No commit by test.
