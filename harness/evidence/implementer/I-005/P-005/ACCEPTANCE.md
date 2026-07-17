# P-005 Acceptance Evidence (Implementer RP-01)

> Phase: `P-005`  
> Initiative: `I-005`  
> Build: `B-001`  
> Role: implementer  
> Branch: `feat/i-005-production-regression-baseline`

## Decision

- Decision: `accepted`
- Added gated `@SpringBootTest` baseline smoke **`DemoBootBaselineSmokeTest`** in `demo-spring-boot`, extending beyond offline `DemoOfflineSmokeTest` and complementing `DemoPersonCrudIT`. Covers consumer-app SessionFactory + explicit dialect from `application.yml`, JDBC pool connectivity, JPQL, and Spring Data `Pageable` pagination.

## Closed gap

| matrix_id | status | entry_class#method | gate |
|---|---|---|---|
| A-XCUT-009 | covered | `DemoBootBaselineSmokeTest#sessionFactoryUsesExplicitXuguDialectFromApplicationYml`; `#datasourceUrlIncludesCompatibleModeNone`; `#jpaPersistAndJpqlQueryRoundTrip`; `#pageableFindAllUsesLimitOffset` | demo |

## Files changed

- `demo-spring-boot/src/test/java/com/xugu/demo/it/DemoBootBaselineSmokeTest.java` (new)
- `contracts/production-regression-baseline.md` (demo smoke SSOT + A-XCUT-009)
- `demo-spring-boot/README.md` (IT class table)
- `harness/evidence/implementer/I-005/P-005/ACCEPTANCE.md`

## Validation (implementer)

- `mvn -q -pl demo-spring-boot -am test` → **exit 0** (demo: 7 run, 2 active, 5 IT skipped)
- `mvn -q test` → **exit 0** (dialect 139 / demo 7; IT gate OFF)

## Observed flows

- **demo-boot-automated-smoke-baseline:** offline YAML/table asserts + gated Spring Boot SessionFactory/JPA/Pageable path wired; live execution requires `XUGU_RUN_IT=true` and reachable XuguDB (same gate as `DemoPersonCrudIT`).

## Optional stretch (deferred)

- hbm2ddl validate, function/HQL, bulk mutation demo paths remain documented optional gaps in SSOT (not required for P-005 closure).
