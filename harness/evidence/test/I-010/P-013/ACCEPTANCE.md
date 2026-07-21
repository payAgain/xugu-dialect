# ACCEPTANCE — I-010 P-013 RP-01 (test)

> Phase: `P-013`  
> Initiative: `I-010` · Build: `B-002`  
> Role: `test` · Step: `RP-01`  
> Branch: `feat/i-010-orm-hql-quality-completion`  
> Completed: 2026-07-21T16:34:18+08:00  
> Verdict: **PASS (offline Unit + gated IT landed; live SKIPPED_INFRA)**

## Goal

HQL bulk support/reject matrix (JOINED / SINGLE_TABLE; order by / limit mutation). No EF Owned.

## Delivered

| Artifact | Path |
|---|---|
| Unit | `dialect/src/test/java/com/xugu/dialect/XuguHqlBulkBoundaryTest.java` |
| IT | `dialect/src/test/java/com/xugu/dialect/it/XuguHqlBulkBoundaryIT.java` |
| SINGLE_TABLE entities | `I010P013StAnimal` / `I010P013StDog` / `I010P013StCat` |
| JOINED reuse | `I003P005Bulk*` (+ cross-ref `XuguBulkMutationIT`) |
| SSOT | `contracts/xuguefcore-parity-suite.md` XP-003 → `covered-unit` |

## Support / reject matrix

| Boundary | Result | Where asserted |
|---|---|---|
| Dialect local-temp bulk flags (`supportsSubqueryOnMutatingTable=false`) | **supported** (wiring) | Unit + IT flag smoke |
| SINGLE_TABLE HQL bulk update/delete | **supported** | IT (gated) |
| JOINED HQL bulk update | **supported** | IT (gated) + `XuguBulkMutationIT` |
| HQL update/delete + `order by` | **rejected** (`SyntaxException` at create) | Unit + IT |
| HQL update/delete + inline `limit` | **rejected** (`SyntaxException` at create) | Unit + IT |
| `MutationQuery.setMaxResults` | **API rejected** (no such method) | Unit |
| `Query.setMaxResults` on mutation HQL | **soft no-op** (limit not applied) | Unit doc + IT (gated) |

## Maven

| Command | Exit |
|---|---:|
| `mvn -q -pl dialect -DskipTests package` | 0 |
| `mvn -q -pl dialect test -Dtest=XuguHqlBulkBoundaryTest,XuguHqlBulkBoundaryIT` | 0 (Unit 5 run; IT 4 run / 3 skipped gate) |
| `mvn -q -pl dialect test` | 0 |

## Live

TCP `:5138` refused → **SKIPPED_INFRA**. Do not promote XP-003 to `covered-live`.

## Checklist

- [x] Unit green offline
- [x] IT gated
- [x] Document supported vs rejected
- [ ] must-commit (Human / orchestrator)
