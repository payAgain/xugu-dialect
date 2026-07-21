# Dialect Implementation

> How to extend `XuguDialect` and keep capability classes coherent.

---

## Core Rules

1. **`XuguDialect` extends `org.hibernate.dialect.Dialect` only.** Extending `MySQLDialect` / `OracleDialect` is forbidden (Charter + ADR-0001).
2. **Structure may mirror** Hibernate MySQL/Oracle dialect *module organization*; **implementation must be original** for this repo.
3. **Capability code lives in support classes**; `XuguDialect` wires them via overrides (`contributeTypes`, `initializeFunctionRegistry`, `getLimitHandler`, `buildSQLExceptionConversionDelegate`, …).
4. **SQL truth** is `E:\Work\docs\content`. Cite doc paths in javadoc when locking behavior (see existing comments in `XuguDialect`).
5. **Default session mode is NONE** (`compatiblemode=NONE` on JDBC URLs). Do not assume MySQL/Oracle compatible SQL.

Reference hub: `dialect/src/main/java/com/xugu/dialect/XuguDialect.java`.

---

## Wiring Pattern

When adding a capability:

1. Implement or extend a focused class under the matching package (`type`, `function`, `ddl`, …).
2. Call it from the appropriate `XuguDialect` override / registration method.
3. Add offline unit assertions (`Xugu*Test`) for SQL strings / registry presence.
4. Add gated IT (`Xugu*IT`) when the matrix row needs **covered-live**.
5. Update contract / baseline SSOT if status changes.

Example function wiring:

- Registration: `XuguFunctionRegistrations.register(FunctionContributions)`
- Invoked from dialect function initialization (see `XuguDialect` + `XuguFunctionRegistrations`)

Example type wiring:

- Support constants / DDL: `XuguIntervalTypeSupport`, `XuguXmlTypeSupport`, …
- JDBC binding: `XuguIntervalJdbcType`, `XuguXmlJdbcType`, `XuguPointJdbcType`, …
- Contributed via `contributeTypes` / type contributions on `XuguDialect`

---

## DialectResolver SPI

| Item | Value |
|------|-------|
| Interface | `org.hibernate.engine.jdbc.dialect.spi.DialectResolver` |
| Implementation | `com.xugu.dialect.XuguDialectResolver` |
| Registration | `META-INF/services/org.hibernate.engine.jdbc.dialect.spi.DialectResolver` |
| Match | product or driver name contains `"xugu"` (case-insensitive) |
| Instantiation | `XuguDialectSelector` (default selector) |

Non-Xugu metadata must return `null` (see `XuguDialectResolverTest`).

Explicit config remains valid:

```properties
hibernate.dialect=com.xugu.dialect.XuguDialect
```

---

## Important Local Behaviors (do not “fix” casually)

Documented in `XuguDialect` javadoc — treat as intentional:

| Area | Local behavior | Why |
|------|----------------|-----|
| Pagination | `LIMIT count` / `LIMIT count OFFSET offset` with binds | Not `FETCH FIRST`; not `LIMIT offset,count` |
| Lock + page | `FOR UPDATE` before `LIMIT`; `WAIT` after `LIMIT` when both present | Live Xugu requirement |
| Lock wait | Milliseconds passed through (no sec conversion) | XuGu `WAIT` is ms |
| Identity DDL | `identity(1,1)` | Prefer IDENTITY over `AUTO_INCREMENT` in NONE |
| Generated keys | `getDefaultUseGetGeneratedKeys() == false` | Uses `select last_insert_id() from dual`; avoids reserved-name quote drop |
| Sequences | `select <seq>.nextval from dual`; alter via `START WITH` / `INCREMENT BY` | Not ANSI `RESTART WITH` |
| Timestamp mapping | Hibernate timestamp → Xugu `TIMESTAMP` (not `DATETIME`) | A-TYP-008 |

---

## Anti-Patterns

- Inheriting MySQL/Oracle dialect to “borrow” SQL generation.
- Emitting SQL for matrix rows marked **文档不允许** / inventing undocumented syntax.
- Claiming **covered-live** without gated IT green on real XuguDB.
- Silently changing known-limit rows to covered without SSOT + user-doc updates.
- Adding Spring / demo dependencies to `dialect/pom.xml`.
