# P-004 Acceptance Evidence (Implementer RP-01)

> Phase: `P-004`  
> Initiative: `I-005`  
> Build: `B-001`  
> Role: implementer  
> Branch: `feat/i-005-production-regression-baseline`

## Decision

- Decision: `accepted`
- Path: **known-limit-documented** (not live IT)

Live bulk insert IT remains blocked by Xugu JDBC 12.3.6 `GetGeneratedKeys` / `distillTbName` on JOINED + IDENTITY mappings (documented I-004/P-005). Chosen closure: offline unit wiring test + SSOT status + user-guide known limitation.

## Closed gap

| matrix_id | status | entry_class#method | gate |
|---|---|---|---|
| C-BULK-002 | known-limit-documented | `XuguBulkMutationSupportTest#fallbackSqmInsertStrategyWired_C_BULK_002` | unit |

## Files changed

- `dialect/src/test/java/com/xugu/dialect/XuguBulkMutationSupportTest.java`
- `contracts/production-regression-baseline.md`
- `docs/user-guide/05-troubleshooting.md`
- `docs/user-guide/04-feature-matrix.md`
- `harness/evidence/implementer/I-005/P-004/ACCEPTANCE.md`
- `harness/handoffs/implementer/I-005-P-004.yaml`

## Validation (implementer)

- Test: `mvn -q test` → **exit 0** (142 run, 0 failures, 55 IT skipped)
- `XuguBulkMutationSupportTest`: **3/3** PASS (includes new `fallbackSqmInsertStrategyWired_C_BULK_002`)

## Acceptance decision

- Decision: `accepted`
- Decided by: orchestrator
- Date: 2026-07-17T17:45:00+08:00
- Phase verification: `harness/evidence/test/I-005/P-004/verification.json`
- Reviewer audit: `harness/evidence/reviewer/I-005/P-004/REVIEW.md` (PASS)
