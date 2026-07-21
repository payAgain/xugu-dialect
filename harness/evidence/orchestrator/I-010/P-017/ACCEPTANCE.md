# ACCEPTANCE — I-010 / P-017 (B-002 rollup)

> Decision: **accepted**  
> Date: 2026-07-21  
> Branch: `feat/i-010-orm-hql-quality-completion`

## Criteria

| Criterion | Evidence | Result |
|---|---|---|
| Suite SSOT 10/10 non-planned | `contracts/xuguefcore-parity-suite.md` | **PASS** |
| `verify.py` VERIFY PASS | `harness/evidence/test/I-010/P-017/verification.json` | **PASS** |
| Offline mvn package/test | verification results build+test exit 0 | **PASS** |
| Live IT | TCP :5138 unreachable | **SKIPPED_INFRA** (honest) |
| NOT Ship | brief / handoff | honored |

## Suite outcome table

| ID | Pri | Theme | Class | Status |
|---|---|---|---|---|
| XP-001 | 高 | @Version stale write | `XuguOptimisticConcurrencyIT` | implemented |
| XP-002 | 高 | HQL GroupBy/Count | `XuguHqlGroupByCountIT` | implemented |
| XP-003 | 中 | HQL bulk boundary | `XuguHqlBulkBoundaryTest`/`IT` | covered-unit |
| XP-004 | 中 | JSON LOB boundary | `XuguJsonLobBoundaryIT` | skipped-infra |
| XP-005 | 中 | Explicit tx atomicity | `XuguExplicitTxAtomicityIT` | skipped-infra |
| XP-006 | 中 | SQL goldens | `XuguNativeSqlBaselineTest` | covered-unit |
| XP-007 | 中 | join fetch | `XuguHqlJoinFetchIT` | skipped-infra |
| XP-008 | 低 | Null semantics | `XuguNullSemanticsIT` | known-limit-documented |
| XP-009 | 低 | Temporal projection | `XuguTemporalProjectionIT` | known-limit-documented |
| XP-010 | 低 | Lock timeout/deadlock | `XuguExceptionConversionTest` | covered-unit (+live-unstable) |

## Next
Human Gate: **Initiative Accept I-010**（仍 NOT Ship · NOT Archive）
