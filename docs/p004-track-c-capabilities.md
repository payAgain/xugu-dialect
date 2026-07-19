# P-004 Track C — JSON subset / ARRAY / ALTER SEQUENCE

> **Initiative:** I-007 / P-004 · **compatiblemode:** NONE · **GAV:** 7.4.5.Final  
> **Doc refs (read-only):** `reference/function/json-functions/**`, `reference/sql/datatype/array.md`, `reference/object/sequence.md`

## C-JSON-005 — bounded JSON HQL subset

| Function | Doc basis | Hibernate surface | Live IT |
|---|---|---|---|
| `json_unquote` | `reference/function/json-functions/**` | `XuguFunctionRegistrations` | `XuguJsonSubsetDeepenIT` |
| `json_length` | same | same | same |
| `json_type` | same (literal arg path) | same | same |
| *(baseline)* `json_value` / `json_extract` | A-FUN-017 | existing | `XuguFunctionRegistryIT` |

**Out of scope:** full `XuguJsonFunctions` registry; `json_set` and unbounded json_* surface remain unregistered.

## A-TYP-015 / C-DDL-005 — ARRAY

| Surface | Implementation | Notes |
|---|---|---|
| `supportsStandardArrays()` | `XuguDialect` → `true` | Enables Hibernate ARRAY DDL path |
| `getPreferredSqlTypeCodeForArray()` | `SqlTypes.ARRAY` | C-DDL-005 |
| `supportsArrayConstructor()` | `true` | `ARRAY[…]` literals |
| DDL | `integer array` / `integer[]` | Live-proven on Xugu 12 |

**JDBC boundary:** XuGu JDBC 12.3.6 does not implement `Connection.createArrayOf`; ORM entity bind may fail. Live acceptance uses native `INTEGER ARRAY` + `ARRAY[…]` round-trip (`XuguArrayTypeIT`).

## A-SEQ-006 — ALTER SEQUENCE

| Form | XuGu (live) | Hibernate |
|---|---|---|
| `ALTER SEQUENCE … START WITH n` | **Supported** | `XuguSequenceSupport#getRestartSequenceString` |
| `ALTER SEQUENCE … INCREMENT BY n` | **Supported** | `XuguSequenceSupport#alterSequenceIncrementBy` |
| `ALTER SEQUENCE … RESTART WITH n` | **Rejected (E19132)** | **Not emitted** — ANSI default overridden |

Live IT: `XuguAlterSequenceIT#alterSequenceStartWithAndIncrement_A_SEQ_006`.
