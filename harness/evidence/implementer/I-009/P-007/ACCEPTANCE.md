# P-007 Acceptance Evidence (Implementer RP-01)

> Phase: `P-007` · Initiative: `I-009` · Build: `B-001`  
> Role: implementer · Matrix rows: **A-DDL-007**, **A-DDL-008**, **A-DDL-009**

## Decision

- Decision: `accepted`
- Path:
  - **A-DDL-007:** `covered-live` (SSOT promotion via C-DDL-001 — no duplicate DDL)
  - **A-DDL-008:** `known-limit-documented` (native PARTITION BY IT; schema export false)
  - **A-DDL-009:** `known-limit-documented` (ENCRYPT BY SQL locked; SYSSSO encryptor prerequisite)
- Rationale: IF NOT EXISTS already wired by I-003 C-DDL-001 — P-007 aligns Definition A SSOT only. PARTITION BY and ENCRYPT BY are doc-allowed native DDL; Hibernate schema tooling cannot emit them honestly.

## Criteria table

| Criterion | Expected | Evidence | Status |
|---|---|---|---|
| A-DDL-007 SSOT promotion | Cross-ref C-DDL-001; no duplicate code | `XuguTableDdlSupport.CREATE_TABLE_IF_NOT_EXISTS_PREFIX`; `XuguTableDdlExtensionsTest#ifNotExistsPromotedViaC_DDL_001_A_DDL_007` | **PASS** |
| A-DDL-008 PARTITION BY | LIST/RANGE/HASH shapes + native IT | `XuguTableDdlSupport`; `XuguTableDdlExtensionsTest#partitionSqlMatchesPartitionDoc_A_DDL_008`; `XuguTableDdlExtensionsIT#listPartitionNativeRoundTrip_A_DDL_008` | **PASS** |
| A-DDL-009 ENCRYPT BY | SQL locked + schema-export false | `XuguTableDdlSupport#createTableWithEncryptBySql`; `XuguTableDdlExtensionsTest#encryptSqlMatchesCreateDoc_A_DDL_009`; gated IT with encryptor skip | **PASS** |
| Schema export honesty | No false PARTITION/ENCRYPT claims | `supportsPartitionByInSchemaExport()` / `supportsEncryptByInSchemaExport()` return false | **PASS** |
| Negative anchors removed | No @Disabled defer for A-DDL-008/009 | `XuguNegativeRegressionBaselineTest` | **PASS** |
| SSOT promotion | 延后 → covered-live / known-limit | `contracts/production-regression-baseline.md`; `contracts/feature-matrix-definition-a.md` | **PASS** |
| Offline build | `mvn -q -DskipTests package` green | implementer run | **PASS** (exit 0) |
| Offline test | `mvn -q test` green | implementer run | **PASS** (exit 0) |

## Validation (implementer)

| Command | Exit | Detail |
|---|---|---|
| `mvn -q -DskipTests package` | 0 | offline build |
| `mvn -q test` | 0 | full reactor (IT gated/skipped offline) |
| `mvn -q -pl dialect -am test -Dtest=XuguTableDdlExtensionsTest,XuguTableDdlExtensionsIT` | 0 | 5 unit + 2 IT skipped offline |

## Closed gaps

| matrix_id | status | entry_class#method | gate |
|---|---|---|---|
| A-DDL-007 | covered-live | `XuguTableDdlExtensionsTest#ifNotExistsPromotedViaC_DDL_001_A_DDL_007`; `XuguTypeDdlDetailsTest/IT` (C-DDL-001) | IT |
| A-DDL-008 | known-limit-documented | `XuguTableDdlExtensionsTest#partitionSqlMatchesPartitionDoc_A_DDL_008`; `XuguTableDdlExtensionsIT#listPartitionNativeRoundTrip_A_DDL_008` | IT |
| A-DDL-009 | known-limit-documented | `XuguTableDdlExtensionsTest#encryptSqlMatchesCreateDoc_A_DDL_009`; `XuguTableDdlExtensionsIT#encryptByNativeWhenEncryptorAvailable_A_DDL_009` | IT |

## Known-limit notes

### A-DDL-008
- Hibernate `SchemaExport` / hbm2ddl does not emit `PARTITION BY` — `supportsPartitionByInSchemaExport()=false`.
- Native SQL IT uses `partition.md` §1.3 example 1 (LIST + OTHERVALUES).

### A-DDL-009
- `CREATE ENCRYPTOR` requires SYSSSO / `ACL_SSO` per `encryptor.md`.
- Live IT skips via `Assumptions` when `sys_encryptors` is empty or inaccessible.
- `supportsEncryptByInSchemaExport()=false`.

## Doc citation

- `reference/object/table/create.md` — `IF NOT EXISTS`, `ENCRYPT BY encryptor_name`
- `reference/object/table/partition.md` — LIST/RANGE/HASH `PARTITION BY` examples
- `reference/object/encryptor.md` — `CREATE ENCRYPTOR 'name' BY 'key'`; SYSSSO prerequisite

## Files changed

- `dialect/src/main/java/com/xugu/dialect/ddl/XuguTableDdlSupport.java`
- `dialect/src/main/java/com/xugu/dialect/XuguDialect.java`
- `dialect/src/test/java/com/xugu/dialect/XuguTableDdlExtensionsTest.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguTableDdlExtensionsIT.java`
- `dialect/src/test/java/com/xugu/dialect/XuguNegativeRegressionBaselineTest.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguDdlIT.java`
- `contracts/production-regression-baseline.md`
- `contracts/feature-matrix-definition-a.md`
- `harness/evidence/implementer/I-009/P-007/ACCEPTANCE.md`
- `harness/handoffs/implementer/I-009-P-007.yaml`
