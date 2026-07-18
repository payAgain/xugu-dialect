# Verification Guide

## Purpose
Define the executable checks and observed behavior required before agents declare work complete.

## Baseline

All harness levels include the structure check and project verification entrypoint:

```text
python harness/scripts/harness_check.py
python harness/scripts/verify.py
```

Standard and Full projects also include:

```text
python harness/scripts/branch_check.py
```

## Project Verification Contract

Configured in `harness/verification.json`. Each check has:

- `id`: stable, unique check name;
- `required`: whether completion is impossible without this check;
- `command`: command executed from the project root;
- optional `cwd`: working directory relative to the project root.

| Check | Command | Notes |
|---|---|---|
| build | `mvn -q -DskipTests package` | Full reactor package |
| test | `mvn -q test` | Default IT gate **OFF** — gated integration tests skipped |
| lint | `NA` | Optional; not configured |

Do not replace an unknown command with a guessed command. An unconfigured required check produces `VERIFY INCOMPLETE` (exit 2), never `VERIFY PASS`.

## Verification Outcomes

| Outcome | Exit | Meaning |
|---|---:|---|
| `VERIFY PASS` | 0 | Harness structure and every configured required check passed |
| `VERIFY FAIL` | 1 | Harness validation or a required project check failed |
| `VERIFY INCOMPLETE` | 2 | A required command or valid verification configuration is missing |

Optional checks without a configured command are recorded as `NOT_APPLICABLE`. A configured optional check still runs and its result remains visible, but only required checks determine completion.

Each run writes machine-readable evidence to:

```text
harness/evidence/verification-latest.json
```

For Phase acceptance, write Phase-bound evidence instead of relying on the mutable latest pointer:

```text
python harness/scripts/verify.py --phase P-006 --evidence harness/evidence/test/I-005/P-006/verification.json
```

The Packet `verification_evidence` must name that repository-contained file. `verification-latest.json` remains a convenience result for interactive runs and cannot authorize an accepted Phase.

Acceptance evidence must reference the Phase-bound result and any required observed user-flow verification. Running commands is not by itself proof that the affected behavior works.

## I-005 production regression baseline (frozen)

Initiative **I-005** freezes a full regression baseline. SSOT:

[`contracts/production-regression-baseline.md`](../contracts/production-regression-baseline.md)

| Bucket | Count | Accept requirement |
|---|---:|---|
| 可实现 rows | 94 | Each row maps to `entry_class#method` (93 covered + 1 known-limit-documented) |
| negative-only rows | 34 | Explicit non-support or `@Disabled` defer anchors |
| Demo smoke | 7 entrypoints | Offline + gated live paths in SSOT |

### IT gate for frozen baseline

| Gate | Enable | Effect |
|---|---|---|
| JVM property | `-Dxugu.run.integration=true` | Surefire runs gated dialect + demo IT |
| Environment | `XUGU_RUN_IT=true` | Same as property (read by `XuguITGate` / `XuguIntegrationGate`) |

**Daily default:** `mvn -q test` (gate OFF) — sufficient for `verify.py` **VERIFY PASS**.

**Frozen baseline Accept (I-005):** `XUGU_RUN_IT=true mvn -q test` must be **all green** when a reachable XuguDB is available. When no live DB is present, document `SKIPPED_INFRA` in Phase evidence (same pattern as P-002/P-005); wiring is verified offline.

User-facing procedure: [`docs/user-guide/03-verify.md`](user-guide/03-verify.md#frozen-baseline--i-005-冻结基线门控).

### Known limitation (not a gap)

**C-BULK-002** bulk insert: **known-limit-documented** — live IT waived; offline wiring via `XuguBulkMutationSupportTest#fallbackSqmInsertStrategyWired_C_BULK_002`.

## I-006 consumer-path baseline (frozen)

Initiative **I-006** freezes the Spring Boot **consumer-path** subset. SSOT:

[`contracts/consumer-path-baseline.md`](../contracts/consumer-path-baseline.md)

| Bucket | Count | Accept requirement |
|---|---:|---|
| Boot-required rows (A+B+C′) | 41 | Each row maps to demo `entry_class#method`; open gaps = **0** |
| Demo `@Test` | ≈28 | Target band 25–40; not a 94-row Boot mirror |
| GAV | `7.4.5.Final` | Unchanged; no Ship in this Initiative |

### IT gate for consumer-path

| Gate | Enable | Effect |
|---|---|---|
| JVM property | `-Dxugu.run.integration=true` | Surefire runs gated dialect + demo IT |
| Environment | `XUGU_RUN_IT=true` | Same (`XuguIntegrationGate`) |

**Daily default:** `mvn -q test` (gate OFF) — sufficient for `verify.py` **VERIFY PASS**.

**I-006 Initiative Accept (when DB available):** `XUGU_RUN_IT=true mvn -q test` (or `mvn -q -pl demo-spring-boot -am test` with gate ON) must be **all green** for demo. When no live DB, document `SKIPPED_INFRA` in Phase evidence; offline wiring remains required.

User-facing procedure: [`docs/user-guide/06-consumer-path.md`](user-guide/06-consumer-path.md). Docs pointer: [`docs/consumer-path-baseline.md`](consumer-path-baseline.md).

**Ship / Maven Central:** out of I-006 scope (Human Gate may Accept Initiative without Ship).

## Change-Type Matrix

| Change Type | Required Validation |
|---|---|
| Documentation / harness only | harness_check plus relevant document or generated-project checks |
| Single-module code | module build/test plus affected behavior observation |
| API / contract | contract checks plus affected integration tests |
| Multi-module / integration | full configured verification plus end-to-end affected flow |
| Data / migration | migration verification plus rollback rehearsal when applicable |
| Production release | configured verification, deployment readiness, rollback evidence, and human Ship authorization |

## If Validation Cannot Be Run

Record the command not run, reason, risk, and required follow-up in the acceptance evidence and session log. The Phase remains incomplete; do not claim full completion.
