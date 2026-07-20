# demo-spring-boot

Spring Boot **4.1.0** demo that uses `com.xugu:xugu-dialect:7.4.5.Final` and the repo-root Xugu JDBC jar against a real XuguDB.

**I-006 consumer-path:** this module hosts the Boot-required baseline (**41** SSOT rows, Layers A + B-both + C′). Open gaps = **0**. How to run offline / gated live for Accept: [`docs/user-guide/06-consumer-path.md`](../docs/user-guide/06-consumer-path.md). SSOT: [`contracts/consumer-path-baseline.md`](../contracts/consumer-path-baseline.md).

## Requirements

- JDK 17+ (compiler release 17)
- Maven 3.9+
- Repo-root `xugu-jdbc-12.3.6.jar`
- Live XuguDB for runtime / gated IT (offline `mvn test` does not need DB)

## Hibernate version force

Spring Boot 4.1.0 BOM defaults `hibernate.version` to **7.4.1.Final**. This module sets:

```xml
<hibernate.version>7.4.5.Final</hibernate.version>
```

and re-declares `hibernate-core` in `dependencyManagement` after the Boot BOM import.

Proof:

```bash
mvn -pl demo-spring-boot dependency:tree -Dincludes=org.hibernate.orm:hibernate-core
```

Expect `hibernate-core:jar:7.4.5.Final` (not 7.4.1).

## Connection env keys

| Key | Default (local reference only) | Notes |
|---|---|---|
| `XUGU_JDBC_URL` | `jdbc:xugu://127.0.0.1:5138/SYSTEM?compatiblemode=NONE` | Prefer env in CI/shared envs |
| `XUGU_USER` | `SYSDBA` | Local reference; do not commit production secrets |
| `XUGU_PASSWORD` | `SYSDBA` | Local reference; do not commit production secrets |
| `xugu.run.integration` | `false` | Maven/system property gate for IT |
| `XUGU_RUN_IT` | unset | Env alternative to enable IT |

Also accepted via Spring datasource properties if you override `spring.datasource.*` instead of the `XUGU_*` env keys.

Driver: `com.xugu.cloudjdbc.Driver`  
Dialect: explicit `com.xugu.dialect.XuguDialect` in `application.yml` (DialectResolver SPI also works if unset).

## Run the demo app

From repo root (with XuguDB reachable):

```bash
# optional env overrides
set XUGU_JDBC_URL=jdbc:xugu://127.0.0.1:5138/SYSTEM?compatiblemode=NONE
set XUGU_USER=SYSDBA
set XUGU_PASSWORD=SYSDBA

mvn -pl demo-spring-boot -am spring-boot:run
```

On startup, `DemoStartupCrudRunner` persists and finds a `HIB_DEMO_PERSON` row (log: `Xugu Hibernate demo CRUD OK`).

Disable startup CRUD: `--xugu.demo.startup-crud=false`

## Tests

Consumer-path verification modes:

| Mode | Command | Expectation |
|---|---|---|
| Offline | `mvn -q -pl demo-spring-boot -am test` | Gated IT skipped; offline smoke green |
| Live | `$env:XUGU_RUN_IT='true'; mvn -q -pl demo-spring-boot -am test` | Demo ≈32 tests green (I-007 P-005 Track B deepening) |
| Harness | `python harness/scripts/verify.py` (repo root) | **VERIFY PASS** |

```bash
# offline (default) — skips gated IT
mvn -q test
mvn -q -pl demo-spring-boot -am test

# live DB IT (use -am so reactor sibling xugu-dialect resolves)
mvn -q -pl demo-spring-boot -am test -Dxugu.run.integration=true
# or full reactor:
mvn -q test -Dxugu.run.integration=true
# PowerShell env alternative:
#   $env:XUGU_RUN_IT = "true"
#   mvn -q -pl demo-spring-boot -am test
```

IT classes (`@EnabledIf` on `XuguIntegrationGate`):

| Class | Purpose |
|---|---|
| `DemoPersonCrudIT` | Spring Boot + JPA full CRUD (persist/find/update/delete) + IDENTITY |
| `DemoBootBaselineSmokeTest` | SessionFactory explicit dialect, JDBC pool, JPQL, Pageable pagination |
| `DemoSpiDialectAutoResolveIT` | SPI resolve without explicit `hibernate.dialect` |
| `DemoSchemaSurfaceIT` | `ddl-auto=update` → `HIB_DEMO_PERSON` exists |
| `DemoValidateStartupIT` | `ddl-auto=validate` startup with pre-created schema |
| `DemoStartupCrudIT` | `startup-crud=true` ApplicationRunner persist/find |
| `DemoAssociationIT` | Dept ↔ member FK association (Layer B) |
| `DemoSequenceIT` | SEQUENCE entity persist + CURRVAL |
| `DemoLockIT` | `PESSIMISTIC_WRITE` + NOWAIT timeout |
| `DemoConstraintRollbackIT` | UNIQUE CVE, NOT NULL extract, txn rollback |
| `DemoTypesIT` | Layer C′ typed sample round-trip (int/decimal/varchar/bool/date/ts/binary/blob/uuid-as-varchar) |
| `DemoFunctionsIT` | Layer C′ HQL function subset (concat/substring/lower-upper/coalesce/temporal/uuid/json_value) |
| `DemoJsonIT` | JSON column round-trip + `json_arrayagg` (shared `DemoJsonDoc`; needs Jackson on classpath) |
| `DemoBulkMutationIT` | HQL bulk update + bulk delete (C-BULK-001; B-DEMO-001 delete) |
| `DemoFlywayIT` | Flyway migrate on Xugu + marker table (B-FLY-001) |
| `DemoReadOnlyTxIT` | `@Transactional(readOnly=true)` query smoke (B-DEMO-003) |

Cleanup deletes `HIB_DEMO_*` rows after each test (as applicable).

## Tables

| Table / sequence | Purpose |
|---|---|
| `HIB_DEMO_PERSON` | Demo entity (`DemoPerson`) |
| `HIB_DEMO_DEPT` | Association parent (`DemoDept`) |
| `HIB_DEMO_DEPT_MEMBER` | Association child + UNIQUE code (`DemoDeptMember`) |
| `HIB_DEMO_SEQ_TICKET` | SEQUENCE entity (`DemoSeqTicket`) |
| `HIB_DEMO_SEQ_TICKET_SEQ` | Sequence for `DemoSeqTicket` |
| `HIB_DEMO_TYPED_SAMPLE` | Layer C′ typed fields (`DemoTypedSample`; `guid_val` is `varchar(36)` + converter) |
| `HIB_DEMO_JSON_DOC` | Layer C′ JSON column (`DemoJsonDoc`) |
| `HIB_DEMO_FLYWAY_MARKER` | Flyway-managed marker (B-FLY-001; not a JPA entity) |

## Mapping notes (P-004 live IT)

- **UUID (A-TYP-012):** entity uses `UuidAsVarcharConverter` → `varchar(36)` instead of Hibernate `UUIDJdbcType` / SQL `guid`, because Xugu JDBC rejects `getObject(..., UUID.class)` with `[E50044]`. `application.yml` sets `hibernate.type.preferred_uuid_jdbc_type: VARCHAR`.
- **JSON FormatMapper:** `spring-boot-starter-jackson` is on the demo classpath so Hibernate can auto-wire a JSON `FormatMapper` for `@JdbcTypeCode(SqlTypes.JSON)`. `hibernate.query.hql.json_functions_enabled=true` is enabled in default `application.yml`.

## Forbidden reminders

- No production secrets in VCS
- No dialect implementation inside this module
- No reference to sibling `hibernate-dialect` sources
