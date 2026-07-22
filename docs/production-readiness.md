# Production Readiness Profile

This document defines what “production-ready” means for this project. Completed at Bootstrap (G1) for a **library + demo** product; revise when architecture or operating conditions change.

For every dimension, choose exactly one status:

- `required` — every affected Initiative must provide evidence;
- `conditional` — required when the change touches the stated trigger;
- `not-applicable` — include a concrete reason.

Do not leave a dimension undecided before approving a production Build.

## Project context

- Product/runtime type: **library** (publishable Hibernate dialect jar) **+ demo** (Spring Boot sample) **+ docs**
- Deployment environment: Consumer JVM apps / local & CI against real XuguDB; Maven artifact install/publish (Ship later)
- Critical user or system flows:
  - Explicit dialect config and DialectResolver SPI auto-detect
  - Correct SQL generation within definition A ∩ XuguDB docs
  - Demo connects to real XuguDB and exercises core scenarios
- Data classification: Connection credentials are **sensitive**; local Charter defaults are reference-only; no production secrets in repo
- Availability/recovery expectations: Library has no long-running service SLA; consumers own app availability. Demo is non-production sample.

## Readiness dimensions

| Dimension | Status | Trigger or reason | Required evidence |
|---|---|---|---|
| Functional correctness | **required** | All dialect/demo/docs product changes | Automated checks + observed affected flow (explicit dialect and/or SPI; demo or IT against real DB when in scope) |
| Reliability | **conditional** | Timeouts, retries, concurrency, external DB connectivity in dialect/demo/tests | Failure-path and recovery/timeout evidence when those paths change |
| Data integrity | **conditional** | Schema/DDL generation, migrations, type mapping, destructive SQL in tests | Schema/DDL or type-mapping tests; backup/rollback notes if destructive ops |
| Security and privacy | **conditional** | Secrets, env-based credentials, untrusted SQL/input surfaces, connection URL handling | No committed production secrets; env override documented; threat-focused review when auth/secrets touched |
| Performance and capacity | **conditional** | Hot SQL paths, large result Limit/offset, resource-heavy dialect changes | Budget, micro-benchmark, or justified analysis when hot paths change |
| Observability | **conditional** | Logging/diagnostics added for dialect resolution or demo runtime | Logs or diagnostic behavior documented when operated/demoed |
| Deployment and configuration | **conditional** | Build, GAV, JDBC packaging, Hibernate/Boot config, module layout | Reproducible Maven build/install; config examples in project docs |
| Rollback and recovery | **conditional** | Published artifact or consumer-breaking change | Version/rollback or deprecation plan at Ship; irreversible-change gate |
| Compatibility | **required** | Public dialect API, SPI, GAV, Hibernate 7.4.5 alignment, config keys | Compatibility tests / matrix notes; no silent break of documented entry points |
| Maintainability | **required** | All maintained projects | Tests, architecture/ADR updates, Trellis task/spec handoff |

## I-008 Accept live evidence (Q4)

For Initiative **I-008 Accept**, functional correctness on a reachable XuguDB additionally requires:

- Gated **full reactor** green: `XUGU_RUN_IT=true mvn -q test`
- Deposited log under `historical I-008/P-007 evidence (removed)` (`mvn-test-live-it-final.log`, `IT-RESULT.txt`)
- **`SKIPPED_INFRA`** documented when no live DB — offline `mvn -q test` **VERIFY PASS** alone is **insufficient** for production / Accept claims

See [`docs/verification.md`](verification.md) § I-008 and [`docs/user-guide/03-verify.md`](user-guide/03-verify.md) § I-008 Accept.

**Q5 out of scope:** performance / multi-version matrix — not in I-008 scope.

## Project verification commands

The executable command contract is `Maven verify (`mvn -q test`)`.

**Bootstrap note:** Maven parent/modules are **not scaffolded yet**. `build` / `test` commands remain placeholders (`<fill-build-command>` / `<fill-test-command>`). Until a Maven scaffold Initiative fills real commands, `mvn -q test` is expected to report **`VERIFY INCOMPLETE`**. That blocks Accept — do not claim production Build complete without configured required checks.

## Acceptance rule

A Phase affecting a `required` dimension, or matching a `conditional` trigger, cannot be accepted unless its Packet names that dimension and its acceptance evidence records the result. `not-applicable` requires a reason; omission is not equivalent to not applicable.
