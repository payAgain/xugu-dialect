# P-006 Implementer Notes (SQL function registry)

**Invocation:** `impl-p006-20260715`  
**Role:** implementer  
**Phase / Build / Initiative:** P-006 / B-006 / I-001  
**Date:** 2026-07-15  
**Step:** RP-01  
**Accept / commit:** **NOT done** (await RP-02 test + RP-03 reviewer)

## What was delivered

1. `com.xugu.dialect.function.XuguFunctionRegistrations` — XuGu-native contributions.
2. `XuguDialect.initializeFunctionRegistry` → `super` + `XuguFunctionRegistrations.register`.
3. Offline unit tests: `XuguFunctionRegistryTest` (+ `OfflineConnectionProvider`).
4. Gated IT: `XuguFunctionRegistryIT` (`-Dxugu.run.integration=true`).
5. Matrix acceptance hints updated for A-FUN-001..014, 016..018 (015 deferred untouched).

## Coverage (matrix 可实现)

| ID | Status | Notes |
|---|---|---|
| A-FUN-001 concat | ✅ | HQL → `concat(...)`; live IT |
| A-FUN-002 substring/substr | ✅ | both registered; substring IT |
| A-FUN-003 length/char_length | ✅ | Dialect defaults |
| A-FUN-004 lower/upper | ✅ | lower IT |
| A-FUN-005 trim/ltrim/rtrim | ✅ | trim + trim1() |
| A-FUN-006 replace/locate/position | ✅ | + ANSI position |
| A-FUN-007 coalesce/nullif/nvl | ✅ | COALESCE preferred; NVL native |
| A-FUN-008 abs/mod/power/sqrt | ✅ | abs IT |
| A-FUN-009 round/floor/ceil/trunc | ✅ | ceiling_ceil + trunc |
| A-FUN-010 current_*/now | ✅ | current_timestamp IT; now() |
| A-FUN-011 extract/year/month/day | ✅ | extract(year) IT |
| A-FUN-012 to_char/to_date/to_timestamp | ✅ | registered; live probe |
| A-FUN-013 cast | ✅ | cast IT |
| A-FUN-014 avg/sum/min/max/count | ✅ | count/sum IT |
| A-FUN-015 bit_and/bit_or | 延后 | not registered |
| A-FUN-016 UUID | ✅ | **primary = `uuid()`** |
| A-FUN-017 JSON | ✅ | **subset: `json_value` + `json_extract`** |
| A-FUN-018 listagg family | ✅ | HQL listagg → `LISTAGG … WITHIN GROUP` |

## UUID choice (A-FUN-016)

**Primary: `uuid()`**

| Candidate | Live result | Why not primary |
|---|---|---|
| `uuid()` | dashed VARCHAR e.g. `767E0000-…` | **chosen** — docs + standard dashed form |
| `gen_random_uuid()` | dashed VARCHAR | also OK; registered as alternate |
| `sys_guid()` | GUID type hex without dashes | registered alternate; less Hibernate-string-friendly |

Docs cited: `reference/function/uuid-functions/uuid.md`, `sys_guid.md`, `gen_random_uuid.md`.

## JSON choice (A-FUN-017)

**Subset only (not MySQL dump):**

| Function | Registration | Live |
|---|---|---|
| `json_value` | Hibernate `JsonValueFunction` (ANSI `JSON_VALUE(doc, path…)`) | HQL IT OK |
| `json_extract` | named descriptor | native IT OK |
| `json_set` / MySQL dump | **NOT registered** | — |

Docs: `reference/function/json-functions/json_value.md`, `json_extract.md`.

**App note:** Hibernate 7 gates HQL `json_*` behind `hibernate.query.hql.json_functions_enabled=true` (`QuerySettings.JSON_FUNCTIONS_ENABLED`). Dialect still registers descriptors; IT enables the preview flag.

## listagg (A-FUN-018)

Hibernate `listagg` → `ListaggFunction` → SQL  
`listagg(expr, sep) within group (order by …)`  
matches XuGu `reference/function/aggregate-functions/listagg.md`.  
Also register native `string_agg` / `group_concat` names (docs; live-proven).

## Doc citations (families)

- string: `concat.md`, `substring.md`, `substr.md`, `length.md`, `lower.md`, `upper.md`, `trim.md`, `ltrim.md`, `rtrim.md`, `replace.md`, `locate.md`, `position.md`, `nvl.md`
- math: `abs.md`, `mod.md`, `power.md`, `sqrt.md`, `round.md`, `floor.md`, `ceil.md`, `trunc.md`
- date-time: `current_date.md`, `current_timestamp.md`, `now.md`, `extract.md`, `year.md`, `month.md`, `day.md`, `to_char.md`, `to_date.md`, `to_timestamp.md`
- aggregate: `avg.md`, `sum.md`, `min.md`, `max.md`, `listagg.md`, `string_agg.md`, `group_concat.md`
- uuid: `uuid.md`, `sys_guid.md`, `gen_random_uuid.md`
- json: `json_value.md`, `json_extract.md`
- cast: `reference/sql/expression/type_conversion.md`

## Negative / unsupported

- Unit: `xugu_unsupported_fn_xyz` and deferred `bit_and` have **no** descriptors.
- IT: `SELECT xugu_unsupported_fn_xyz() FROM DUAL` fails with diagnosable XuGu SQLException.

## Explicitly NOT done / forbidden

- No MySQL/Oracle Dialect inheritance
- No sibling `hibernate-dialect` port
- No inventing undocumented functions as supported
- No Accept / git commit this turn
- A-FUN-015/019/020/021 remain deferred

## Commands

| Command | Exit |
|---|---|
| `mvn -q test` | **0** |
| `mvn -q test -Dxugu.run.integration=true` | **0** |
| `python harness/scripts/verify.py --phase P-006` | see verification.json |

## Observed flows

- `function-registry-hql-sql-real-db`: HQL concat/substring/lower/abs/current_timestamp/extract/cast/count/sum/uuid/json_value/listagg + native json_extract/uuid on live XuguDB (`compatiblemode=NONE`).
