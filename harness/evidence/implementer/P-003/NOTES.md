# P-003 Implementer NOTES — JSON / AggregateSupport

> **Invocation:** `impl-p003-20260716` · I-003 / B-003

## Delivered (C-JSON-001…004)
- `XuguJsonArrayAggFunction` / `XuguJsonObjectAggFunction` — native XuGu render
- `XuguAggregateSupport` — json_extract / json_unquote component paths
- `XuguCastingJsonJdbcType` (+ array constructor) — `cast(? as json)` writes
- Wired via `XuguFunctionRegistrations` + `XuguDialect.contributeTypes` / `getAggregateSupport`
- Test: `jackson-databind` (test scope) for FormatMapper
- ORM IT: `XuguJsonAggregateIT` PASS (round-trip + HQL arrayagg/objectagg)

## Forbidden respected
No sibling source copy; no harness framework changes; version 7.4.5.Final; no Ship
