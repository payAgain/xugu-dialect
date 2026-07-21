# SQL Types & Functions

> Patterns for type contributions, function registration, and SQL AST.

---

## Types

### Where work happens

| Concern | Location |
|---------|----------|
| Column type / DDL strings | `XuguDialect` column-type overrides + `type/*TypeSupport` |
| Custom JDBC binding | `type/Xugu*JdbcType.java` |
| JSON casting helpers | `XuguCastingJsonJdbcType`, `XuguCastingJsonArrayJdbcType*` |
| Geometric / XML / INTERVAL / UDT | dedicated `*TypeSupport` + optional `JdbcType` |

### Rules

- Map Hibernate `SqlTypes` only when Xugu docs allow a clear target (Definition A).
- Prefer documenting subtype coverage in support classes even when Hibernate exposes fewer codes (e.g. INTERVAL: 13 Xugu subtypes in `XuguIntervalTypeSupport`; ORM path uses `DURATION` / `INTERVAL_SECOND`).
- UUID consumer path in Boot may prefer VARCHAR — dialect + demo align via `preferred_uuid_jdbc_type` / converters; do not break A-TYP-012 without contract update.
- Native-only tooling SQL is OK for known-limit rows; do not claim ORM entity mapping when tests assert the opposite (see UDT: `XuguUdtTypeTest#dialectDoesNotClaimOrmUdtEntityMapping_A_TYP_018`).

Reference tests: `XuguDialectTest#columnTypesMatchXuguDocs`, `XuguTypeRoundTripIT`, `XuguIntervalTypeIT`, `XuguXmlTypeAndFunctionsIT`, `XuguGeometricTypeAndFunctionsIT`.

---

## Functions

Central registrar: `com.xugu.dialect.function.XuguFunctionRegistrations`.

### Patterns

1. Reuse Hibernate `CommonFunctionFactory` for ANSI-shaped functions when XuGu accepts them.
2. Register XuGu-specific names with `SqmFunctionRegistry` builders / custom `SqmFunctionDescriptor` classes when rendering differs (`XuguJsonArrayAggFunction`, `XuguXmlElementFunction`, …).
3. Keep **bounded subsets** for large families (JSON / XML / geometric / regexp) — match matrix rows and javadoc in `XuguFunctionRegistrations`, not “register everything in the docs tree”.
4. Primary UUID SQL name is `uuid` (`PRIMARY_UUID_FUNCTION`) — live-proven; alternates may exist but do not demote the primary without evidence.
5. HQL JSON functions require `hibernate.query.hql.json_functions_enabled=true` (demo `application.yml` already sets this).

### Anti-Patterns

- Registering functions with no unit registry test and no matrix ownership.
- Treating assumption-skipped native IT (e.g. empty XMLTABLE cluster) as covered-live.
- Copying MySQL/Oracle function renderers wholesale.

---

## Pagination, Locks, AST

| Piece | Class | Notes |
|-------|-------|-------|
| Limit handler | `pagination/XuguLimitHandler` | Bind-marker `LIMIT` / `LIMIT … OFFSET` |
| SQL AST | `sql/ast/XuguSqlAstTranslator` | HQL/Criteria path must not emit ANSI `OFFSET`/`FETCH` by default |
| Locking | `internal/XuguLockingSupport`, dialect lock strings | `NOWAIT` / `WAIT <ms>` |

When changing pagination or lock SQL, update both unit string tests (`XuguPaginationLockTest`, `XuguHqlPaginationIT` under gate) and AST translator behavior together.

---

## DDL / Schema Helpers

- Table/index helpers: `ddl/XuguTableDdlSupport`, `ddl/XuguIndexDdlSupport`
- Sequences: `sequence/XuguSequenceSupport`, `SequenceInformationExtractorXuguDatabaseImpl`
- Temp tables: `temptable/XuguLocalTemporaryTableStrategy`, `XuguGlobalTemporaryTableStrategy`
- Catalog metadata: `metadata/XuguCatalogMetadataSupport` — JDBC catalog awareness without emitting `catalog.schema.table` qualification (known-limit / design lock)

Partition / ENCRYPT / advanced index SQL may exist as **documented helpers + native IT** while schema-export claims stay limited — see known-limit call-outs in `contracts/production-regression-baseline.md`.
