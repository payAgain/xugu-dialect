# P-002 Test Report — I-006

> Phase: P-002 | Build: B-001 | Initiative: I-006  
> Role: test | step: RP-02 | invocation_id: `inv-i006-p002-rp02-test`  
> Branch: `feat/i-006-consumer-path-coverage`  
> Date: 2026-07-18

## Session Briefing (test)

- Current Goal: validate Layer A Boot golden-path offline + live gates after RP-01
- Working Branch: `feat/i-006-consumer-path-coverage` (BRANCH_CHECK PASS)
- GAV: `com.xugu:xugu-dialect:7.4.5.Final` (unchanged)

## Commands

| Step | Command | Exit | Result |
|---|---|---:|---|
| branch_check | `python harness/scripts/branch_check.py` | 0 | PASS |
| build | `mvn -q -DskipTests package` | 0 | PASS |
| test (offline) | `mvn test` | 0 | PASS |
| live demo IT | `$env:XUGU_RUN_IT='true'; mvn -pl demo-spring-boot -am test` | 0 | PASS |
| harness_check | `python harness/scripts/harness_check.py` | 1 | FAIL (pre-existing P-001 semantic; not P-002) |
| verify.py | `python harness/scripts/verify.py --phase P-002 --evidence .../verification.json` | 1 | VERIFY FAIL (blocked on harness_check) |

## Offline counts

| Module | run | fail | error | skip |
|---|---:|---:|---:|---:|
| xugu-dialect | 139 | 0 | 0 | 54 |
| demo-spring-boot | **14** | **0** | **0** | **11** |
| reactor (sum) | 153 | 0 | 0 | 65 |

Offline demo: 3 always-on smoke green; 11 gated IT skipped (gate off) — expected.

Logs: `mvn-package-offline.log`, `mvn-test-offline.log`, `surefire-summary-offline.txt`

## Live IT

| Field | Value |
|---|---|
| Status | **ran** — **PASS** (not SKIPPED_INFRA) |
| Probe | TCP `127.0.0.1:5138` OK (`live-db-probe.txt`) |
| Command | `XUGU_RUN_IT=true mvn -pl demo-spring-boot -am test` |
| demo | **14 / 0 / 0 / 0** (all Layer A gated IT executed) |
| dialect (-am) | 139 / 0 / 0 / 22 |
| Log | `mvn-test-live-demo.log`, `IT-RESULT.txt` |

## SSOT Layer A confirmation

`contracts/consumer-path-baseline.md` — P-002 open gaps **0**; all 13 Layer A rows `covered` with `entry_class#method`:

| row_id | entry (implementer; confirmed present) |
|---|---|
| A-SPI-002 / A-SPI-003 | `DemoSpiDialectAutoResolveIT#sessionFactoryResolvesXuguDialectWithoutExplicitConfig` (+ offline SPI file smoke) |
| A-IDN-004 | `DemoPersonCrudIT#updateAndDeletePerson` |
| A-SEQ-001 | `DemoValidateStartupIT#validateStartupSucceedsWithPreCreatedSchema` |
| A-DDL-001 | `DemoSchemaSurfaceIT#hibDemoPersonTableExistsAfterUpdateStartup` |
| (+ prior A rows / startup-crud / Pageable) | `DemoStartupCrudIT`, `DemoBootBaselineSmokeTest#pageableSecondPageUsesOffset`, etc. |

No Layer A gap missed by implementer. Status header updated by test to note RP-02 offline+live verification.

## Product code edits by test

None.

## Observed flow

- Flow: `layer-a-golden-path-boot-validate-crud-startup-spi`
- Observation: offline suite green without live DB; under `XUGU_RUN_IT=true` validate/CRUD/JPQL+Pageable/SPI-auto/startup-crud all executed and passed
- Evidence: this directory

## Recommendation for reviewer

**PASS** on P-002 technical acceptance (offline green + live Layer A Boot IT green + SSOT A covered).

Nit: `python harness/scripts/verify.py` reports **VERIFY FAIL** solely because `harness_check` fails on pre-existing I-006/P-001 acceptance metadata (`ACCEPTED WITHOUT APPROVED BUILD` / `INVALID ACCEPTANCE DECISION`). Direct `build` + `test` commands required by the Phase packet **PASS**. Orchestrator may need a separate harness hygiene fix; do not treat as P-002 Layer A product failure.
