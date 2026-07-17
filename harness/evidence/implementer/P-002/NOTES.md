# P-002 Implementer NOTES — IDENTITY × reserved table (I-004)

> **Invocation:** `impl-p002-20260717` · I-004 / B-001 / RP-01

## Approach

Dialect mitigation only (no JDBC driver change):

1. Override `XuguDialect.getDefaultUseGetGeneratedKeys()` → **`false`**
2. Hibernate then uses `XuguIdentityColumnSupport.getIdentitySelectString` → `select last_insert_id() from dual`
3. Reserved identifiers continue to use double-quote quoting (`openQuote`/`closeQuote` = `"`)
4. Entity maps physical table via Hibernate quoted form `@Table(name = "\"order\"")` so DDL/DML emit `"order"`

**Why:** JDBC `RETURN_GENERATED_KEYS` re-parses the INSERT and can drop identifier quoting → `unexpected ORDER` on reserved table names. Preferring `LAST_INSERT_ID()` avoids that path. This is **not** a driver fix.

## Delivered

| Surface | Change |
|---|---|
| `XuguDialect.getDefaultUseGetGeneratedKeys` | returns `false` (+ class javadoc) |
| `XuguIdentityColumnSupport` | javadoc: select path is primary; string unchanged |
| Unit | `dialectPrefersIdentitySelectOverGetGeneratedKeys` → `assertFalse` |
| ORM IT | `XuguReservedIdentityIT` + `I004P002OrderEntity` (`GenerationType.IDENTITY`, Integer id, table `"order"`) |

## Observed (live XuguDB)

```
Hibernate: create table if not exists "order" (...)
Hibernate: insert into "order" (name) values (?)
Hibernate: select last_insert_id() from dual
Hibernate: drop table if exists "order"
```

- Persist + flush backfills non-null positive Integer id
- No `unexpected ORDER` / E19132 on insert path

## Validation (implementer)

| Command | Exit |
|---|---|
| `mvn -q -pl dialect -am test -Dtest=XuguIdentitySequenceTest` | **0** |
| `mvn -q -pl dialect -am test -Dtest=XuguReservedIdentityIT -Dxugu.run.integration=true` | **0** |

## Forbidden respected

Native dialect only (no sibling source port); **no JDBC driver changes**; GAV 7.4.5.Final; no Accept; no git commit; no Ship.
