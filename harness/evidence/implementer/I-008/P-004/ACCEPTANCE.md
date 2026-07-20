# I-008 / P-004 Acceptance Evidence (implementer RP-01)

> Phase: `P-004`  
> Initiative: `I-008`  
> Build: `B-001`  
> Role step: `RP-01` / implementer  
> invocation_id: `inv-i008-p004-rp01-implementer`  
> Branch: `feat/i-008-production-quality-gaps`  
> Result: **RP-01 artifacts complete** — promotions per P-001 Batch B

## Approved scope

- Task: `harness/tasks/P-004.md`
- Promotion map: `harness/evidence/architect-contract/I-008/P-001/PROMOTION-MAP.md` Batch B (16 rows)
- Allowed: `contracts/**` (SSOT tag sweep + demo-live documentation)
- Forbidden respected: no MySQL/Oracle inheritance, no `org/` META-INF/, no commit, no dialect Java changes

## Promotion summary

| Sub-batch | Action | Count | Result |
|---|---|---:|---|
| **Ruler C tag sweep** | SSOT `covered` → `covered-live` (live IT pre-existed) | **15** | **covered-live** |
| **A-XCUT-009 demo-live** | Document gated Boot IT satisfies consumer golden path | **1** | **covered-live** |
| **Total Batch B** | | **16** | |

### Ruler C — SSOT tag sweep (15 → covered-live)

| matrix_id | Live IT anchor |
|---|---|
| C-EXC-001 | `XuguExceptionMappingIT#sessionUniqueViolationMapsToConstraintViolationException` |
| C-EXC-002 | `XuguExceptionMappingIT#sessionNotNullViolationExtractsFieldNameWhenPresent` |
| C-JSON-001..004 | `XuguJsonAggregateIT#jsonColumnRoundTripAndHqlAggregates` |
| C-WIN-001 | `XuguWindowCteIT#hqlWindowAndWithClauseOnLiveSession` |
| C-CTE-001 | `XuguWindowCteIT#hqlWindowAndWithClauseOnLiveSession` |
| C-BULK-001 | `XuguBulkMutationIT#bulkUpdateOnJoinedInheritanceSucceeds`; `#bulkDeleteOnJoinedInheritanceSucceeds` |
| C-BULK-003 | `XuguBulkMutationIT#dialectExposesLocalTempBulkStrategyFlags` |
| C-DDL-001..003 | `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` |
| C-CAT-001 | `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` (catalog create/drop) |
| C-GUID-001 | `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` (selectGuidString) |

No new tests added — honest SSOT tag uplift only; all rows had pre-existing gated dialect IT.

### A-XCUT-009 — demo-live clarification (1 → covered-live)

| Field | Value |
|---|---|
| **Evidence** | Gated `@SpringBootTest` in `demo-spring-boot` (`XuguIntegrationGate`) |
| **Golden path** | Explicit dialect + env secrets; JPA CRUD + IDENTITY; JPQL; Pageable LIMIT-OFFSET |
| **Anchors** | `DemoPersonCrudIT#persistAndFindPerson`; `DemoBootBaselineSmokeTest#sessionFactoryUsesExplicitXuguDialectFromApplicationYml`; `#jpaPersistAndJpqlQueryRoundTrip`; `#pageableFindAllUsesLimitOffset` |
| **Consumer xref** | [`contracts/consumer-path-baseline.md`](../../../../contracts/consumer-path-baseline.md) Layer A — Boot 41/41 FROZEN |
| **SSOT call-out** | [`contracts/production-regression-baseline.md`](../../../../contracts/production-regression-baseline.md) § A-XCUT-009 demo-live |

## SSOT post-state (可实现 98 rows)

| Bucket | Count | Delta (from P-003) |
|---|---:|---|
| SSOT `covered-live` | **83** | +16 |
| SSOT `known-limit-documented` | **15** | — |
| Honest live-capable paths | **83/98** | — (tag now matches honest count) |
| unit-only-without-live | **0** | — |
| SSOT tag sweep remaining (Batch B) | **0** | was 15 |

## Validation (implementer)

| Command | Result | Notes |
|---|---|---|
| `mvn -q -DskipTests package` | **PASS** | exit 0 (~4.6s) |
| `mvn -q test` | **PASS** | exit 0 (~8.3s); offline gate green |

## Files changed

- `contracts/production-regression-baseline.md` — 15 Ruler C + A-XCUT-009 tag sweep; demo-live call-out; summary rollup
- `contracts/consumer-path-baseline.md` — A-XCUT-009 demo-live note + changelog

## Acceptance criteria (RP-01)

| Criterion | Result | Evidence |
|---|---|---|
| Batch B 16 rows closed per Q1 rules | **PASS** | § Promotion summary |
| Ruler C promotions have pre-existing IT anchors | **PASS** | § Ruler C table |
| A-XCUT-009 demo-live documented in SSOT/contracts | **PASS** | production + consumer-path call-outs |
| P-003+P-004 aggregate: no undocumented unit-only 可实现 rows | **PASS** | 83 covered-live + 15 known-limit = 98 |
| No forbidden paths touched | **PASS** | contracts only |
| Offline `mvn -q test` green | **PASS** | exit 0 |
| No commit | **PASS** | by instruction |

## Role pipeline

| Step | Role | Status | Evidence |
|---|---|---|---|
| RP-01 | implementer | **complete** | this file (`inv-i008-p004-rp01-implementer`) |
| RP-02 | test | **passed** | `harness/evidence/test/I-008/P-004/verification.json` (`inv-i008-p004-rp02-test`) |
| RP-03 | reviewer | **passed** | `harness/evidence/reviewer/I-008/P-004/REVIEW.md` (`inv-i008-p004-rp03-reviewer`) |

## Acceptance decision

- Decision: `accepted`
- Live IT: SKIPPED_INFRA (127.0.0.1:5138 unavailable); offline mvn test PASS

## Handoff

- `harness/handoffs/implementer/I-008-P-004.yaml`
