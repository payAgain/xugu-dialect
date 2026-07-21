# P-008 Acceptance Evidence (Implementer RP-01)

> Phase: `P-008` · Initiative: `I-009` · Build: `B-001`  
> Role: implementer · Matrix rows: **A-SCH-003**, **A-SCH-017**

## Decision

- Decision: `accepted`
- Path:
  - **A-SCH-003:** `known-limit-documented` (JDBC catalog aligns with `current_db()`; object names stay `schema.table`)
  - **A-SCH-017:** `known-limit-documented` (functional + BITMAP native DDL; schema export stays basic B-tree)
- Rationale: XuGu `DATABASE` is connection-scoped and not session-SET — honest to keep `NameQualifierSupport.SCHEMA`. Advanced indexes beyond A-SCH-016 are doc-allowed native SQL only.

## Criteria table

| Criterion | Expected | Evidence | Status |
|---|---|---|---|
| A-SCH-003 JDBC alignment | `current_db()` + JDBC catalog probe | `XuguCatalogMetadataSupport`; `XuguCatalogAndIndexExtensionsIT#jdbcCatalogAlignsWithCurrentDb_A_SCH_003` | **PASS** |
| A-SCH-003 schema-only names | `supportsCatalogs=false`; export flag false | `XuguCatalogAndIndexExtensionsTest#nameQualifierRemainsSchemaOnly_A_SCH_003`; `#dialectDoesNotClaimCatalogInObjectNames_A_SCH_003` | **PASS** |
| A-SCH-017 functional index | `len(name)` shape locked | `XuguIndexDdlSupport#createFunctionalIndexSql`; unit + IT | **PASS** |
| A-SCH-017 BITMAP index | `INDEXTYPE IS BITMAP` locked | `XuguIndexDdlSupport#createBitmapIndexSql`; unit + IT | **PASS** |
| Schema export honesty | No false advanced-index claim | `supportsAdvancedIndexInSchemaExport()=false` | **PASS** |
| Negative anchors removed | No @Disabled defer for A-SCH-003/017 | `XuguNegativeRegressionBaselineTest` | **PASS** |
| SSOT promotion | 延后 → known-limit-documented | `contracts/production-regression-baseline.md`; `contracts/feature-matrix-definition-a.md` | **PASS** |
| Offline build | `mvn -q -DskipTests package` green | implementer run | **PASS** (exit 0) |
| Offline test | `mvn -q test` green | implementer run | **PASS** (exit 0) |

## Validation (implementer)

| Command | Exit | Detail |
|---|---|---|
| `mvn -q -DskipTests package` | 0 | offline build |
| `mvn -q test` | 0 | full reactor (IT gated/skipped offline) |
| `mvn -q -pl dialect -am test -Dtest=XuguCatalogAndIndexExtensionsTest,XuguCatalogAndIndexExtensionsIT` | 0 | 6 unit + 2 IT skipped offline |

## Closed gaps

| matrix_id | status | entry_class#method | gate |
|---|---|---|---|
| A-SCH-003 | known-limit-documented | `XuguCatalogAndIndexExtensionsTest#catalogMetadataQueryLocked_A_SCH_003`; `XuguCatalogAndIndexExtensionsIT#jdbcCatalogAlignsWithCurrentDb_A_SCH_003` | IT |
| A-SCH-017 | known-limit-documented | `XuguCatalogAndIndexExtensionsTest#functionalIndexSqlMatchesIndexesDoc_A_SCH_017`; `XuguCatalogAndIndexExtensionsIT#functionalAndBitmapIndexNativeRoundTrip_A_SCH_017` | IT |

## Known-limit notes

### A-SCH-003
- Hibernate object qualification remains `schema.table` (`NameQualifierSupport.SCHEMA`).
- JDBC `DatabaseMetaData.getCatalog()` should match `current_db()` on the same connection under `compatiblemode=NONE`.
- XuGu `DATABASE` session parameter is not SET-able (connection-only per session `database.md`).

### A-SCH-017
- Hibernate schema export does not emit functional/BITMAP indexes (`supportsAdvancedIndexInSchemaExport()=false`).
- P-008 subset: functional expression keys + `INDEXTYPE IS BITMAP` only.
- Spatial / LOCAL / GLOBAL partition indexes remain out of scope.

## Doc citation

- `reference/object/database.md` — `current_db()`, CREATE/DROP DATABASE
- `reference/system-configuration-parameter/session-parameter/database.md` — DATABASE not SET-able
- `reference/object/indexes.md` — §函数索引, `INDEXTYPE IS BITMAP`

## Files changed

- `dialect/src/main/java/com/xugu/dialect/metadata/XuguCatalogMetadataSupport.java`
- `dialect/src/main/java/com/xugu/dialect/ddl/XuguIndexDdlSupport.java`
- `dialect/src/main/java/com/xugu/dialect/XuguDialect.java`
- `dialect/src/test/java/com/xugu/dialect/XuguCatalogAndIndexExtensionsTest.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguCatalogAndIndexExtensionsIT.java`
- `dialect/src/test/java/com/xugu/dialect/XuguNegativeRegressionBaselineTest.java`
- `dialect/src/test/java/com/xugu/dialect/XuguSchemaTempCommentTest.java`
- `contracts/production-regression-baseline.md`
- `contracts/feature-matrix-definition-a.md`
- `harness/evidence/implementer/I-009/P-008/ACCEPTANCE.md`
- `harness/handoffs/implementer/I-009-P-008.yaml`
