# I-008 / P-003 Acceptance Evidence (implementer RP-01)

> Phase: `P-003`  
> Initiative: `I-008`  
> Build: `B-001`  
> Role step: `RP-01` / implementer  
> invocation_id: `inv-i008-p003-rp01-implementer`  
> Branch: `feat/i-008-production-quality-gaps`  
> Result: **RP-01 artifacts complete** — promotions per P-001 Batch A + A′

## Approved scope

- Task: `harness/tasks/P-003.md`
- Promotion map: `harness/evidence/architect-contract/I-008/P-001/PROMOTION-MAP.md`
- Allowed: `dialect/**`, `contracts/production-regression-baseline.md`
- Forbidden respected: no MySQL/Oracle inheritance, no demo-spring-boot, no commit

## Promotion summary

| Batch | Action | Count | Result |
|---|---|---:|---|
| **A — thin live IT** | Add/extend gated live IT | **4** | **covered-live** |
| **A — known-limit** | SSOT waiver + bundle reason | **15** | **known-limit-documented** |
| **A′ — tag sweep** | `covered` → `covered-live` (IT already existed) | **58** | **covered-live** |
| **Closed (I-007)** | No work | **5** | unchanged |
| **Total Batch A + A′** | | **77** | |

### Batch A — thin live IT (4 → covered-live)

| matrix_id | Live IT anchor |
|---|---|
| A-TYP-003 | `XuguTypeRoundTripIT#jdbcRealFloatRoundTrip_A_TYP_003` |
| A-TYP-019 | `XuguCastPatternIT#castExpressionOnLiveDb_A_TYP_019` |
| A-DDL-005 | `XuguDefaultColumnExportIT#schemaExportEmitsDefaultColumnAndAppliesOnDb_A_DDL_005` |
| A-XCUT-001 | `XuguIdentifierFoldingIT#unquotedIdentifiersFoldToUppercase_A_XCUT_001` |

### Batch A — known-limit-documented (15)

A-IDN-001/002, A-SEQ-002/005, A-FUN-003/005/006/007/009, A-SCH-006, A-SPI-004, A-XCUT-002/005/007/008 — waiver table in `contracts/production-regression-baseline.md` § P-003 Batch A known-limit waivers.

### Batch A′ — SSOT tag sweep (58 → covered-live)

Types 12 · DDL 5 · Pagination 3 · Locks 3 · Identity 2 · Sequence 3 · Functions 12 · Schema 13 · SPI 3 · Cross-cutting 2 — no new tests; honest tag only.

## SSOT post-state (可实现 98 rows)

| Bucket | Count |
|---|---:|
| SSOT `covered-live` | **67** (was 5) |
| SSOT `known-limit-documented` | **15** (was 0 on achievable rows) |
| Honest live-capable paths | **83/98** (82 IT + 1 demo `A-XCUT-009`) |
| unit-only-without-live | **0** |

## Test classes added / extended

| Class | Change |
|---|---|
| `XuguTypeRoundTripIT` | + `jdbcRealFloatRoundTrip_A_TYP_003` |
| `XuguCastPatternIT` | **new** — A-TYP-019 |
| `XuguDefaultColumnExportIT` | **new** — A-DDL-005 |
| `XuguIdentifierFoldingIT` | **new** — A-XCUT-001 |

All live IT gated via `XuguITGate.isEnabled()` (`XUGU_RUN_IT=true` or `-Dxugu.run.integration=true`).

## Validation (implementer)

| Command | Result | Notes |
|---|---|---|
| `mvn -q -DskipTests package` | **SKIPPED_INFRA** | `mvn` not on agent PATH; defer to RP-02 test role |
| `mvn -q test` | **SKIPPED_INFRA** | same; offline gate must pass in RP-02 |

### SKIPPED_INFRA — live IT (expected offline)

When gate OFF, new/extended IT methods assume-skip like existing dialect ITs:

- `XuguCastPatternIT#castExpressionOnLiveDb_A_TYP_019`
- `XuguDefaultColumnExportIT#schemaExportEmitsDefaultColumnAndAppliesOnDb_A_DDL_005`
- `XuguIdentifierFoldingIT#unquotedIdentifiersFoldToUppercase_A_XCUT_001`
- `XuguTypeRoundTripIT#jdbcRealFloatRoundTrip_A_TYP_003`

Live PASS evidence: RP-02 with `XUGU_RUN_IT=true` → `harness/evidence/test/I-008/P-003/`.

## Files changed

- `dialect/src/test/java/com/xugu/dialect/it/XuguTypeRoundTripIT.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguCastPatternIT.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguDefaultColumnExportIT.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguIdentifierFoldingIT.java`
- `contracts/production-regression-baseline.md`

## Acceptance criteria (RP-01)

| Criterion | Result | Evidence |
|---|---|---|
| Batch A rows → covered-live or known-limit-documented | **PASS** | § Promotion summary; SSOT diff |
| Each covered-live promotion has IT anchor | **PASS** | § thin live IT table |
| Batch A′ honest tag sweep (no silent rename without IT) | **PASS** | 58 rows had pre-existing IT gate |
| No forbidden dialect inheritance | **PASS** | no `MySQLDialect`/`OracleDialect` edits |
| Offline `mvn -q test` green | **DEFERRED** | SKIPPED_INFRA — RP-02 |
| No commit | **PASS** | by instruction |

## Role pipeline

| Step | Role | Status | Evidence |
|---|---|---|---|
| RP-01 | implementer | **complete** | this file (`inv-i008-p003-rp01-implementer`) |
| RP-02 | test | **passed** | `harness/evidence/test/I-008/P-003/verification.json` (`inv-i008-p003-rp02-test`) |
| RP-03 | reviewer | **passed** | `harness/evidence/reviewer/I-008/P-003/REVIEW.md` (`inv-i008-p003-rp03-reviewer`) |

## Acceptance decision

- Decision: `accepted`
- Live IT: SKIPPED_INFRA (127.0.0.1:5138 unavailable); offline mvn test PASS

## Handoff

- `harness/handoffs/implementer/I-008-P-003.yaml`
