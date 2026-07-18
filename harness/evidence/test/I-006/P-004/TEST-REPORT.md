# P-004 Test Report — I-006

> Phase: P-004 | Build: B-001 | Initiative: I-006  
> Role: test | step: RP-02 (re-run after implementer fix) | invocation_id: `inv-i006-p004-rp02-test`  
> Branch: `feat/i-006-consumer-path-coverage`  
> Date: 2026-07-18  
> **Verdict: PASS** (offline green; live Layer C′ IT 28/0/0/0)

## Session Briefing (test)

- Current Goal: re-verify Layer C′ (19 Boot-required rows) offline + live after implementer UUID/Jackson fix
- Working Branch: `feat/i-006-consumer-path-coverage` (BRANCH_CHECK PASS)
- GAV: `com.xugu:xugu-dialect:7.4.5.Final` (unchanged)
- Product Java edited by test: **none** (`org/` untouched)
- Prior RP-02: FAIL (3 Errors — UUID E50044 + missing JSON FormatMapper); this run overwrites FAIL evidence

## Commands

| Step | Command | Exit | Result |
|---|---|---:|---|
| branch_check | `python harness/scripts/branch_check.py` | 0 | PASS |
| build | `mvn -q -DskipTests package` | 0 | PASS |
| test (offline) | `mvn -q test` | 0 | PASS |
| live demo IT | `$env:XUGU_RUN_IT='true'; mvn -pl demo-spring-boot -am test` | 0 | PASS |
| harness_check | `python harness/scripts/harness_check.py` | 0 | PASS |
| verify.py | `python harness/scripts/verify.py --phase P-004 --evidence .../verification.json` | 0 | **VERIFY PASS** |

## Offline counts

| Module | run | fail | error | skip |
|---|---:|---:|---:|---:|
| xugu-dialect | 139 | 0 | 0 | 54 |
| demo-spring-boot | **28** | **0** | **0** | **23** |
| reactor (sum) | 167 | 0 | 0 | 77 |

Offline demo: 5 always-on smoke green (`DemoOfflineSmokeTest` incl. Layer C′ entity prefix smoke); 23 gated IT skipped (gate off) — expected.

Logs: `mvn-package-offline.log`, `mvn-test-offline.txt`, `surefire-summary-offline.txt`

## Live IT

| Field | Value |
|---|---|
| Status | **ran** — **PASS** (not SKIPPED_INFRA) |
| Probe | TCP `127.0.0.1:5138` OK (`live-db-probe.txt`) |
| Command | `XUGU_RUN_IT=true mvn -pl demo-spring-boot -am test` |
| demo | **28 / 0 / 0 / 0** (all Layer A+B+C′ gated IT executed) |
| dialect (-am) | 139 / 0 / 0 / 22 |
| Log | `mvn-test-live-demo.log`, `IT-RESULT.txt`, `surefire-summary-live-demo.txt`, `surefire-live-demo/` |

### Layer C′ live methods exercised

| row_id | entry_class#method | live result |
|---|---|---|
| A-TYP-001…010,012 | `DemoTypesIT#typedSampleRoundTripKeyTypes` | PASS |
| A-TYP-013 / C-JSON-001 | `DemoJsonIT#jsonColumnRoundTripAndArrayAgg` | PASS |
| A-FUN-001…017 subset | `DemoFunctionsIT#hqlFunctionSubsetSmoke` | PASS |
| C-BULK-001 | `DemoBulkMutationIT#bulkUpdatePersonNames` | PASS |
| Offline C′ smoke | `DemoOfflineSmokeTest#layerCEntitiesUseHibDemoPrefix` | PASS |

Prior live Errors (UUID extract + JSON FormatMapper) **cleared** after implementer fix (`UuidAsVarcharConverter` + `spring-boot-starter-jackson`).

## SSOT Layer C′ confirmation

`contracts/consumer-path-baseline.md` —

| Check | Result |
|---|---|
| Boot-required open gaps (P-004 C′) | **0** |
| C′ covered rows | **19** with `entry_class#method` |
| A-TYP-011 | **dialect-it-only** (LOB pick A-TYP-010 BLOB) |
| Demo `@Test` count | **28** (range 25–40) |
| Live Boot path proof for C′ | **PASS** — types/JSON/functions/bulk green |

Closed gap ids confirmed:

- Types: `A-TYP-001`, `A-TYP-002`, `A-TYP-004`, `A-TYP-005`, `A-TYP-006`, `A-TYP-008`, `A-TYP-009`, `A-TYP-010`, `A-TYP-012`, `A-TYP-013`
- Functions: `A-FUN-001`, `A-FUN-002`, `A-FUN-004`, `A-FUN-007`, `A-FUN-010`, `A-FUN-016`, `A-FUN-017`
- JSON / bulk: `C-JSON-001`, `C-BULK-001`

## Product code edits by test

None. No demo/dialect Java; never touched `org/`. SSOT status notes only.

## Observed flow

- Flow: `layer-c-prime-remaining-boot-required-entries`
- Observation: offline suite green without live DB (gated skips); under `XUGU_RUN_IT=true` types (incl. UUID varchar converter), JSON FormatMapper, HQL function subset, and bulk update all executed and passed
- Evidence: this directory

## Recommendation for reviewer

**PASS** on P-004 technical acceptance (offline green + live Layer C′ Boot IT green + SSOT C′ open gaps 0 + A-TYP-011 dialect-it-only + Demo `@Test`=28).

`python harness/scripts/verify.py` → **VERIFY PASS** (harness + build + test all PASS). Next: RP-03 reviewer. No commit by test.
