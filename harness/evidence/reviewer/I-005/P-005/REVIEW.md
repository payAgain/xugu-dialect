# P-005 RP-03 Review — I-005

> **Verdict:** PASS  
> **Date:** 2026-07-17

## Checklist

| Item | Result |
|------|--------|
| Gated `@SpringBootTest` beyond `DemoOfflineSmokeTest` | PASS — `DemoBootBaselineSmokeTest` (4 methods) |
| SessionFactory + explicit `XuguDialect` from consumer `application.yml` | PASS — `#sessionFactoryUsesExplicitXuguDialectFromApplicationYml` |
| JPA path (persist + JPQL) | PASS — `#jpaPersistAndJpqlQueryRoundTrip` |
| Pagination stretch (Pageable / LIMIT-OFFSET) | PASS — `#pageableFindAllUsesLimitOffset` |
| Default `mvn test` skips live (gate OFF) | PASS — 5 demo IT methods skipped, 2 offline green |
| SSOT `A-XCUT-009` + demo smoke call-out updated | PASS |
| No committed secrets | PASS — env keys only in YAML comments/placeholders |
| ACCEPTANCE + verification evidence | PASS |

## Notes

- Live execution requires reachable XuguDB (`XUGU_RUN_IT=true`); environment under test had no DB (E50027). Wiring matches existing `DemoPersonCrudIT` gate — acceptable for baseline closure per I-005/P-002 precedent.
- `@AfterAll` table drop is best-effort (swallows connection failure) to avoid false errors when infra absent.
- Optional demo gaps (validate, function/HQL, bulk) remain documented in SSOT — out of P-005 scope.

## Minor (non-blocking)

- Consider extracting shared JDBC cleanup helper from `DemoPersonCrudIT` / `DemoBootBaselineSmokeTest` in a future hygiene pass (P-006 docs).
