# P-004 Implementer NOTES — Window / WITH(CTE)

> **Invocation:** `impl-p004-20260716` · I-003 / B-004

## Delivered (C-WIN-001 / C-CTE-001)
- `XuguDialect.supportsWindowFunctions()` → `true` (docs: `analyze_func.md`)
- `XuguDialect.supportsWithClause()` → `true` (docs: `with.md`)
- Unit: `XuguWindowCteSupportTest`
- ORM IT: `XuguWindowCteIT` + entity `I003P004WinEntity` (`HIB_I003_P004_WIN`)
  - HQL `row_number() over (partition by …)` — asserts OVER in SQL + ranking
  - HQL `with cte as (…)` — asserts WITH in SQL + result order

## Forbidden respected
No sibling source copy; no harness framework changes; version 7.4.5.Final; no Ship
