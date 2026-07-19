# P-002 Implementer NOTES — C-BULK-002 live unblock (I-007)

> **Invocation:** `inv-i007-p002-rp01-implementer` · I-007 / B-001 / P-002 / RP-01  
> **Branch:** `feat/i-007-capability-hardening-abc`

## Strategy branch executed

**Outcome: `covered-live`** (prefer-live-unblock PASS)

## What was tried

1. Added gated live IT `XuguBulkMutationIT#bulkInsertOnJoinedInheritanceWithIdentitySucceeds_C_BULK_002` on JOINED + `@GeneratedValue(IDENTITY)` root (`I003P005BulkPerson`).
2. First live run (pre-fix): `IndexOutOfBoundsException: toIndex = 4` in `TemporaryTable.findTemporaryTableColumns` during HQL bulk insert planning — root cause: `XuguDialect#getFallbackSqmInsertStrategy` used `TemporaryTable.createEntityTable(EntityMappingType, …)` overload that delegates to **id-table** columns, not full entity temp table.
3. Dialect fix: align with Hibernate `MySQLDialect` — `return new LocalTemporaryTableInsertStrategy(rootEntityDescriptor, runtimeModelCreationContext);` so PersistentClass binding builds correct ENTITY temp table.
4. Adjusted existing `XuguBulkMutationIT` seed/assertions for IDENTITY (no manual ids; post-update name query).

## Commands + results

| Command | Exit | Notes |
|---|---|---|
| `mvn -q -DskipTests package` | 0 | offline build |
| `mvn -q test` | 0 | offline full reactor green |
| `$env:XUGU_RUN_IT='true'; mvn -q -pl dialect -am test -Dtest=XuguBulkMutationIT` | 0 | **4/4 PASS** incl. C-BULK-002 bulk insert |

Live log: `harness/evidence/test/I-007/P-002/mvn-test-live-it.log`

## Key code changes

- `dialect/src/main/java/com/xugu/dialect/XuguDialect.java` — insert strategy ctor fix
- `dialect/src/test/java/com/xugu/dialect/it/XuguBulkMutationIT.java` — new live IT + IDENTITY seed fixes
- `dialect/src/test/java/com/xugu/dialect/it/entities/I003P005BulkPerson.java` — `@GeneratedValue(IDENTITY)`
- SSOT/docs: `contracts/production-regression-baseline.md`, `contracts/i007-capability-hardening-plan.md`, `docs/user-guide/05-troubleshooting.md` §10

## Not done (by design)

- No git commit (orchestrator/human gate)
- ACCEPTANCE Decision left for orchestrator/reviewer
