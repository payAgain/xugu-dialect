# P-005 Test Report (RP-02)

| Field | Value |
|-------|-------|
| Initiative | I-003 |
| Build | B-005 |
| Phase | P-005 — bulk mutation fallback |
| Role | test / RP-02 |
| Branch | feat/i-003-production-capability-parity |
| HEAD | aac0d27b4c6f12fc7800b403dda7c858214ca1f2 |
| invocation_id | test-p005-20260716 |
| Completed | 2026-07-16T15:13:00+08:00 |

## Scope

Independent verification of implementer delivery: `XuguDialect` bulk mutation fallback wiring, `XuguBulkMutationSupportTest` (offline), `XuguBulkMutationIT` (live XuguDB when IT gate ON).

## Command matrix

| Step | Command | Exit |
|------|---------|------|
| Build | `mvn -q -DskipTests package` | 0 |
| Unit/offline tests | `mvn -q test` | 0 |
| Integration tests | `mvn -q test "-Dxugu.run.integration=true"` | 0 |
| Verify | `python harness/scripts/verify.py --phase P-005 --evidence harness/evidence/test/P-005/verification.json` | 0 → **VERIFY PASS** |
| Harness check | `python harness/scripts/harness_check.py` | 0 |
| Branch check | `python harness/scripts/branch_check.py` | 0 |

### PowerShell note

Unquoted `-Dxugu.run.integration=true` is parsed as a lifecycle phase (exit 1). Effective integration run used quoted property: `"-Dxugu.run.integration=true"`.

## Observed flows

### C-BULK-001 — JOINED bulk update/delete ORM entrypoint (live DB)

| Check | Result | Evidence |
|-------|--------|----------|
| Offline strategy wiring | PASS | `XuguBulkMutationSupportTest.localTemporaryTableStrategyForBulkMutation_C_BULK_001` |
| Live HQL bulk update on JOINED inheritance | PASS | `XuguBulkMutationIT.bulkUpdateOnJoinedInheritanceSucceeds` (gate ON) |
| Live HQL bulk delete on JOINED inheritance | PASS | `XuguBulkMutationIT.bulkDeleteOnJoinedInheritanceSucceeds` (gate ON) |
| Dialect flags on live path | PASS | `XuguBulkMutationIT.dialectExposesLocalTempBulkStrategyFlags` |

Surefire (integration gate ON): `TEST-com.xugu.dialect.it.XuguBulkMutationIT.xml` — 3 tests, 0 failures, 0 skipped, `xugu.run.integration=true`.

### C-BULK-003 — supportsSubqueryOnMutatingTable=false

| Check | Result | Evidence |
|-------|--------|----------|
| Unit assertion | PASS | `XuguBulkMutationSupportTest.supportsSubqueryOnMutatingTableIsFalse_C_BULK_003` |
| IT assertion | PASS | `XuguBulkMutationIT.dialectExposesLocalTempBulkStrategyFlags` |

### C-BULK-002 — bulk insert IT

**N/A** — no dedicated bulk-insert integration test in P-005 scope (acceptable per P-001).

## Product code changes by test

None.

## Verdict

**PASS** — all required commands succeeded; VERIFY PASS; bulk mutation flows confirmed as above.
