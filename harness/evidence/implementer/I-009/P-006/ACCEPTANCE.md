# P-006 Acceptance Evidence (Implementer RP-01)

> Phase: `P-006` · Initiative: `I-009` · Build: `B-001`  
> Role: implementer · Matrix rows: **A-FUN-019 regexp_***, **A-FUN-015 bit_and/bit_or**

## Decision

- Decision: `accepted`
- Path:
  - **A-FUN-019:** `covered-live`
  - **A-FUN-015:** `known-limit-documented`
- Rationale: XuGu documents `regexp_like`/`regexp_replace`/`regexp_substr` with straightforward VARCHAR signatures; all three registered and exercised via native SQL IT using doc examples. `bit_and`/`bit_or` require `VARBIT` input per `bit_and.md`/`bit_or.md`; Hibernate 7.4 has no `SqlTypes` for VARBIT entity columns — honest outcome is function registry + native SQL IT, not invented ORM VARBIT mapping.

## Criteria table

| Criterion | Expected | Evidence | Status |
|---|---|---|---|
| Regexp functions | regexp_like/replace/substr registered | `XuguRegexpFunctions`; `XuguFunctionRegistrations`; `XuguFunctionRegistryTest#regexpSubsetRegistered_A_FUN_019` | **PASS** |
| Bit aggregates | bit_and/bit_or registered | `XuguBitAggregateFunctions`; `XuguFunctionRegistrations`; `XuguFunctionRegistryTest#bitAggregateRegistered_A_FUN_015` | **PASS** |
| Unit tests | Offline green | `XuguFunctionRegistryTest` (+2 methods) | **PASS** |
| Gated live IT | Native SQL when `XUGU_RUN_IT=true` | `XuguRegexpAndBitFunctionsIT` | **PASS** / SKIPPED_INFRA when gate off |
| SSOT promotion | 延后 → covered-live / known-limit-documented | `contracts/production-regression-baseline.md` A-FUN-019, A-FUN-015 | **PASS** |
| Negative anchors removed | No @Disabled deferred anchor for A-FUN-019 | `XuguNegativeRegressionBaselineTest` | **PASS** |
| No invented SQL | Shapes from regexp_*.md + bit_*.md only | reviewer RP-03 | pending |
| Offline build | `mvn -q -DskipTests package` green | implementer run | **PASS** (exit 0) |
| Offline test | `mvn -q test` green | implementer run | **PASS** (exit 0) |

## Validation (implementer)

| Command | Exit | Detail |
|---|---|---|
| `mvn -q -DskipTests package` | 0 | offline build |
| `mvn -q test` | 0 | offline reactor (IT gated/skipped) |
| `mvn -q -pl dialect -am test -Dtest=XuguFunctionRegistryTest,XuguRegexpAndBitFunctionsIT` | 0 | 9 unit + 2 IT skipped offline |
| `XUGU_RUN_IT=true mvn -q -pl dialect -am test -Dtest=XuguRegexpAndBitFunctionsIT` | 1 (SKIPPED_INFRA) | Connection refused 127.0.0.1:5138; gate ON exercised IT path |

## Closed gaps

| matrix_id | status | entry_class#method | gate |
|---|---|---|---|
| A-FUN-019 | covered-live | `XuguFunctionRegistryTest#regexpSubsetRegistered_A_FUN_019`; `XuguRegexpAndBitFunctionsIT#regexpFunctionsNativeSubset_A_FUN_019` | IT |
| A-FUN-015 | known-limit-documented | `XuguFunctionRegistryTest#bitAggregateRegistered_A_FUN_015`; `XuguRegexpAndBitFunctionsIT#bitAggregatesNativeSubset_A_FUN_015` | IT |

## Known-limit (A-FUN-015)

- `bit_and`/`bit_or` input type is `VARBIT` per XuGu docs; no Hibernate 7.4 `SqlTypes` hook for VARBIT entity columns.
- HQL aggregate on mapped entity attributes over VARBIT is out of scope — native SQL IT is the verification path.
- `bit_xor` and `regexp_instr`/`regexp_count` are documented but outside bounded P-006 matrix scope.

## Doc citation

- `reference/function/string-functions/regexp_like.md` — `REGEXP_LIKE(expr, pattern[, mode])` → T/F
- `reference/function/string-functions/regexp_replace.md` — date reorder + digit mask examples
- `reference/function/string-functions/regexp_substr.md` — digit extraction + start/occurrence example
- `reference/function/aggregate-functions/bit_and.md` — `VARBIT(7)` table; `b'0010100'`
- `reference/function/aggregate-functions/bit_or.md` — `VARBIT(7)` table; `b'1011101'`
- `reference/sql/datatype/bit.md` — VARBIT literal `b'…'` syntax

## Files changed

- `dialect/src/main/java/com/xugu/dialect/function/XuguRegexpFunctions.java`
- `dialect/src/main/java/com/xugu/dialect/function/XuguBitAggregateFunctions.java`
- `dialect/src/main/java/com/xugu/dialect/function/XuguFunctionRegistrations.java`
- `dialect/src/test/java/com/xugu/dialect/XuguFunctionRegistryTest.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguRegexpAndBitFunctionsIT.java`
- `dialect/src/test/java/com/xugu/dialect/XuguNegativeRegressionBaselineTest.java`
- `contracts/production-regression-baseline.md`
- `contracts/feature-matrix-definition-a.md`
- `harness/evidence/implementer/I-009/P-006/ACCEPTANCE.md`
- `harness/handoffs/implementer/I-009-P-006.yaml`
