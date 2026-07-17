# I-005 / P-001 — Test Inventory Crosswalk (RP-01)

> **Role:** researcher  
> **Date:** 2026-07-17  
> **Inputs:** `contracts/feature-matrix-definition-a.md`, `contracts/feature-matrix-i003-ruler-c.md`, `dialect/src/test/java/**`, `demo-spring-boot/src/test/java/**`, `docs/harness-gap-analysis-i001-external-it.md`  
> **IT gate:** `XuguITGate.isEnabled()` ← `-Dxugu.run.integration=true` or env `XUGU_RUN_IT=true`  
> **Demo IT gate:** `XuguIntegrationGate.isEnabled()` (same property/env)

## Test class inventory (verified)

| Gate | Class | Kind |
|------|-------|------|
| unit | `XuguDialectTest` | Types/DDL/identifiers/TCL keywords |
| unit | `XuguIdentitySequenceTest` | Identity/sequence SPI + metadata query |
| unit | `XuguPaginationLockTest` | Limit/lock SQL fragments + negatives |
| unit | `XuguFunctionRegistryTest` | Function registry + deferred negative |
| unit | `XuguSchemaTempCommentTest` | Schema/temp/comment/FK/truncate/index |
| unit | `XuguDialectResolverTest` | Resolver match/non-match |
| unit | `XuguDialectServicesResourceTest` | META-INF/services SPI resource |
| unit | `XuguSqlAstTranslatorTest` | SqlAstTranslator factory wiring |
| unit | `XuguExceptionConversionTest` | Exception delegate/extractor (C-EXC) |
| unit | `XuguJsonAggregateSupportTest` | AggregateSupport + json agg wiring |
| unit | `XuguWindowCteSupportTest` | Window/CTE flags |
| unit | `XuguBulkMutationSupportTest` | Bulk mutation temp-table wiring (partial) |
| unit | `XuguTypeDdlDetailsTest` | I-003 DDL/catalog/GUID/ENUM null |
| IT | `XuguTypeRoundTripIT` | JDBC type round-trip + TCL smoke |
| IT | `XuguDdlIT` | Schema export create/drop/PK/NOT NULL/ALTER |
| IT | `XuguBinarySchemaExportIT` | VARBINARY → bare BINARY export |
| IT | `XuguPaginationIT` | LimitHandler JDBC live (LimitHandler layer) |
| IT | `XuguHqlPaginationIT` | HQL setFirstResult/maxResults + lock+page AST |
| IT | `XuguLockIT` | FOR UPDATE/NOWAIT/WAIT + limit combo |
| IT | `XuguIdentitySequenceIT` | IDENTITY persist + SEQUENCE NEXTVAL/CURRVAL |
| IT | `XuguFunctionRegistryIT` | HQL function families live |
| IT | `XuguSchemaTempCommentIT` | Schema/temp/comment/FK/UK/CHECK/truncate/index |
| IT | `XuguDialectResolverIT` | Explicit dialect + SPI auto-resolve |
| IT | `XuguSchemaValidateIT` | hbm2ddl validate + sequence metadata (I-002) |
| IT | `XuguExceptionMappingIT` | ORM unique → ConstraintViolationException |
| IT | `XuguJsonAggregateIT` | JSON round-trip + json_arrayagg/objectagg |
| IT | `XuguWindowCteIT` | HQL OVER + WITH CTE |
| IT | `XuguBulkMutationIT` | HQL bulk update/delete on JOINED inheritance |
| IT | `XuguTypeDdlDetailsIT` | IF NOT EXISTS, alter column, catalog, GUID, datetime |
| IT | `XuguReservedIdentityIT` | I-004 reserved table `ORDER` identity path |
| IT | `XuguAutoSequenceDropIT` | I-004 sequence drop E7002 guard |
| demo unit | `DemoOfflineSmokeTest` | application.yml + table prefix |
| demo IT | `DemoPersonCrudIT` | Spring Boot JPA persist/find + IDENTITY |

---

## Crosswalk table

Columns: `matrix_id | matrix_source | status_in_matrix | entry_class#method | gate | gap_action | owner_phase`

Legend — **status:** `covered` = at least one verified test entrypoint; `gap` = no entrypoint or missing expected live IT; `negative-only` = 文档不允许/延后 row (P-003 negative scope).

### Definition A — P-003 Types (可实现)

| matrix_id | matrix_source | status_in_matrix | entry_class#method | gate | gap_action | owner_phase |
|-----------|---------------|------------------|-------------------|------|------------|-------------|
| A-TYP-001 | A | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | unit+IT | — | — |
| A-TYP-002 | A | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | unit+IT | — | — |
| A-TYP-003 | A | covered | `XuguDialectTest#columnTypesMatchXuguDocs` (REAL→float) | unit | Optional TIME-adjacent float IT | P-002 |
| A-TYP-004 | A | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguDialectTest#sizeAndPrecisionDefaults` (CHAR trim); `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | unit+IT | — | — |
| A-TYP-005 | A | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguDialectTest#booleanLiteralsAreTrueFalse`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | unit+IT | — | — |
| A-TYP-006 | A | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | unit+IT | — | — |
| A-TYP-007 | A | covered | `XuguDialectTest#columnTypesMatchXuguDocs` (time `$p`) | unit | Add JDBC TIME round-trip IT | P-002 |
| A-TYP-008 | A | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | unit+IT | — | — |
| A-TYP-009 | A | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguBinarySchemaExportIT#schemaExportEmitsBareBinaryAndCreatesOnDb`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | unit+IT | — | — |
| A-TYP-010 | A | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | unit+IT | — | — |
| A-TYP-011 | A | covered | `XuguDialectTest#columnTypesMatchXuguDocs` (NCLOB→clob); `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | unit+IT | — | — |
| A-TYP-012 | A | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | unit+IT | — | — |
| A-TYP-013 | A | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes`; `XuguJsonAggregateIT#jsonColumnRoundTripAndHqlAggregates` | unit+IT | — | — |
| A-TYP-014 | A | negative-only | — | none | Defer; no negative test required unless promoted | later |
| A-TYP-015 | A | negative-only | — | none | Defer | later |
| A-TYP-016 | A | negative-only | — | none | Defer | later |
| A-TYP-017 | A | negative-only | — | none | Defer | later |
| A-TYP-018 | A | negative-only | — | none | Defer | later |
| A-TYP-019 | A | gap | — | none | Add unit assert on `castPattern` default | P-002 |

### Definition A — P-003 DDL (可实现 / 延后)

| matrix_id | matrix_source | status_in_matrix | entry_class#method | gate | gap_action | owner_phase |
|-----------|---------------|------------------|-------------------|------|------------|-------------|
| A-DDL-001 | A | covered | `XuguDdlIT#schemaExportCreateDropWithPkAndNotNull`; `XuguDialectTest#ddlHelpersMatchXuguSyntax` | unit+IT | — | — |
| A-DDL-002 | A | covered | `XuguDdlIT#schemaExportCreateDropWithPkAndNotNull` (ALTER ADD COLUMN) | IT | — | — |
| A-DDL-003 | A | covered | `XuguDdlIT#schemaExportCreateDropWithPkAndNotNull` | IT | — | — |
| A-DDL-004 | A | covered | `XuguDdlIT#schemaExportCreateDropWithPkAndNotNull` (NOT NULL reject) | IT | — | — |
| A-DDL-005 | A | gap | — | none | Add schema export/default column IT or unit exporter assert | P-002 |
| A-DDL-006 | A | covered | `XuguDdlIT#schemaExportCreateDropWithPkAndNotNull`; `XuguDialectTest#ddlHelpersMatchXuguSyntax` | unit+IT | — | — |
| A-DDL-007 | A | negative-only | `XuguDdlIT` comment only (CREATE_ONLY defers IF NOT EXISTS) | none | Explicit negative if IF NOT EXISTS ever emitted pre-I-003 path | P-003 |
| A-DDL-008 | A | negative-only | — | none | Defer | later |
| A-DDL-009 | A | negative-only | — | none | Defer | later |

### Definition A — P-004 Pagination (可实现 / 文档不允许 / 延后)

| matrix_id | matrix_source | status_in_matrix | entry_class#method | gate | gap_action | owner_phase |
|-----------|---------------|------------------|-------------------|------|------------|-------------|
| A-PAG-001 | A | covered | `XuguPaginationLockTest#limitOnlyUsesBindMarker_A_PAG_001_003`; `XuguPaginationIT#limitAndOffsetReturnExpectedRows`; `XuguHqlPaginationIT#hqlSetFirstResultMaxResultsUsesLimitNotFetchFirst` | unit+IT | — | — |
| A-PAG-002 | A | covered | `XuguPaginationLockTest#limitOffsetStableForm_A_PAG_002`; `XuguPaginationIT#limitAndOffsetReturnExpectedRows`; `XuguHqlPaginationIT#hqlSetFirstResultMaxResultsUsesLimitNotFetchFirst` | unit+IT | — | — |
| A-PAG-003 | A | covered | `XuguPaginationLockTest#limitOnlyUsesBindMarker_A_PAG_001_003`; `XuguPaginationIT#limitAndOffsetReturnExpectedRows` | unit+IT | — | — |
| A-PAG-004 | A | negative-only | — | none | Defer (LIMIT preferred) | later |
| A-PAG-005 | A | negative-only | `XuguPaginationLockTest#limitOnlyUsesBindMarker_A_PAG_001_003`; `XuguPaginationIT#limitAndOffsetReturnExpectedRows`; `XuguHqlPaginationIT#hqlSetFirstResultMaxResultsUsesLimitNotFetchFirst` | unit+IT | Consolidate negative assert checklist for P-003 | P-003 |
| A-PAG-006 | A | negative-only | — | none | Defer | later |

### Definition A — P-004 Locks

| matrix_id | matrix_source | status_in_matrix | entry_class#method | gate | gap_action | owner_phase |
|-----------|---------------|------------------|-------------------|------|------------|-------------|
| A-LCK-001 | A | covered | `XuguPaginationLockTest#forUpdateBasic_A_LCK_001`; `XuguLockIT#forUpdateExecutesAndSkipLockedUnsupported`; `XuguHqlPaginationIT#hqlLockAndPageEmitsForUpdateBeforeLimitAndWaitAfter` | unit+IT | — | — |
| A-LCK-002 | A | covered | `XuguPaginationLockTest#forUpdateOf_A_LCK_002`; `XuguLockIT#forUpdateExecutesAndSkipLockedUnsupported` | unit+IT | — | — |
| A-LCK-003 | A | covered | `XuguPaginationLockTest#nowaitAndWaitMilliseconds_A_LCK_003`; `XuguLockIT#forUpdateExecutesAndSkipLockedUnsupported`; `XuguLockIT#limitForUpdateComboExecutes`; `XuguHqlPaginationIT#hqlLockAndPageEmitsForUpdateBeforeLimitAndWaitAfter` | unit+IT | — | — |
| A-LCK-004 | A | negative-only | `XuguPaginationLockTest#skipLockedNotSupported_A_LCK_004`; `XuguLockIT#forUpdateExecutesAndSkipLockedUnsupported`; `XuguPaginationIT#limitAndOffsetReturnExpectedRows` | unit+IT | P-003 negative bundle with C-SKIP-001 | P-003 |
| A-LCK-005 | A | negative-only | `XuguPaginationLockTest#noForShare_A_LCK_005` | unit | Add IT negative if PESSIMISTIC_READ path claimed | P-003 |
| A-LCK-006 | A | negative-only | — | none | Defer | later |

### Definition A — P-005 Identity & Sequence

| matrix_id | matrix_source | status_in_matrix | entry_class#method | gate | gap_action | owner_phase |
|-----------|---------------|------------------|-------------------|------|------------|-------------|
| A-IDN-001 | A | covered | `XuguIdentitySequenceTest#identitySupportWired_A_IDN_001` | unit | — | — |
| A-IDN-002 | A | covered | `XuguIdentitySequenceTest#identitySupportWired_A_IDN_001` (no AUTO_INCREMENT) | unit | — | — |
| A-IDN-003 | A | covered | `XuguIdentitySequenceTest#identitySelectFallback_A_IDN_003`; `XuguIdentitySequenceIT#identityPersistBackfillsId_A_IDN_003_004` | unit+IT | — | — |
| A-IDN-004 | A | covered | `XuguIdentitySequenceIT#identityPersistBackfillsId_A_IDN_003_004`; `DemoPersonCrudIT#persistAndFindPerson` | IT+demo | — | — |
| A-IDN-005 | A | negative-only | — | none | Defer | later |
| A-SEQ-001 | A | covered | `XuguIdentitySequenceTest#createDropSequenceStrings_A_SEQ_001_002_005`; `XuguIdentitySequenceTest#sequenceMetadataQueryAndExtractorWired`; `XuguSchemaValidateIT#schemaValidateSucceedsWhenSequenceExists` | unit+IT | — | — |
| A-SEQ-002 | A | covered | `XuguIdentitySequenceTest#createDropSequenceStrings_A_SEQ_001_002_005` | unit | — | — |
| A-SEQ-003 | A | covered | `XuguIdentitySequenceTest#sequenceSupportWired_A_SEQ_001_003_008`; `XuguIdentitySequenceIT#sequenceGeneratorPersist_A_SEQ_003_004_008` | unit+IT | — | — |
| A-SEQ-004 | A | covered | `XuguIdentitySequenceTest#currvalFunctionForm_A_SEQ_004`; `XuguIdentitySequenceIT#sequenceGeneratorPersist_A_SEQ_003_004_008` | unit+IT | — | — |
| A-SEQ-005 | A | covered | `XuguIdentitySequenceTest#createDropSequenceStrings_A_SEQ_001_002_005` | unit | — | — |
| A-SEQ-006 | A | negative-only | — | none | Defer | later |

### Definition A — P-006 Functions (可实现 / 延后)

| matrix_id | matrix_source | status_in_matrix | entry_class#method | gate | gap_action | owner_phase |
|-----------|---------------|------------------|-------------------|------|------------|-------------|
| A-FUN-001 | A | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | unit+IT | — | — |
| A-FUN-002 | A | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | unit+IT | — | — |
| A-FUN-003 | A | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered` | unit | Optional live length/char_length probe | P-002 |
| A-FUN-004 | A | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | unit+IT | — | — |
| A-FUN-005 | A | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered` | unit | Optional live trim family probe | P-002 |
| A-FUN-006 | A | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered` | unit | Optional live replace/locate/position probe | P-002 |
| A-FUN-007 | A | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered` | unit | Optional live coalesce/nullif/nvl probe | P-002 |
| A-FUN-008 | A | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` (abs) | unit+IT | Optional mod/power/sqrt live | P-002 |
| A-FUN-009 | A | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered` | unit | Optional round/floor live | P-002 |
| A-FUN-010 | A | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | unit+IT | — | — |
| A-FUN-011 | A | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | unit+IT | — | — |
| A-FUN-012 | A | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` (to_char path) | unit+IT | — | — |
| A-FUN-013 | A | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | unit+IT | — | — |
| A-FUN-014 | A | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | unit+IT | — | — |
| A-FUN-015 | A | negative-only | `XuguFunctionRegistryTest#unsupportedFunctionNotRegistered_negativeNote` | unit | Defer explicit HQL negative | later |
| A-FUN-016 | A | covered | `XuguFunctionRegistryTest#uuidPrimaryIsUuid`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | unit+IT | — | — |
| A-FUN-017 | A | covered | `XuguFunctionRegistryTest#jsonSubsetUsesStandardJsonValueNotMysqlDump`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | unit+IT | — | — |
| A-FUN-018 | A | covered | `XuguFunctionRegistryTest#listaggUsesNativeListaggFunction`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | unit+IT | — | — |
| A-FUN-019 | A | negative-only | — | none | Defer | later |
| A-FUN-020 | A | negative-only | — | none | Defer | later |
| A-FUN-021 | A | negative-only | — | none | Defer | later |

### Definition A — P-007 Schema / temp / comment / constraints

| matrix_id | matrix_source | status_in_matrix | entry_class#method | gate | gap_action | owner_phase |
|-----------|---------------|------------------|-------------------|------|------------|-------------|
| A-SCH-001 | A | covered | `XuguSchemaTempCommentTest#schemaCreateDropCommands_A_SCH_001`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | unit+IT | — | — |
| A-SCH-002 | A | covered | `XuguSchemaTempCommentTest#nameQualifierIsSchemaOnly_A_SCH_002_not_003`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | unit+IT | — | — |
| A-SCH-003 | A | negative-only | `XuguSchemaTempCommentTest#nameQualifierIsSchemaOnly_A_SCH_002_not_003` (supportsCatalogs=false) | unit | Defer catalog IT | later |
| A-SCH-004 | A | covered | `XuguSchemaTempCommentTest#localTempStrategy_A_SCH_004_006`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | unit+IT | — | — |
| A-SCH-005 | A | covered | `XuguSchemaTempCommentTest#globalTempStrategy_A_SCH_005_006_preconditionDocumented`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` (gated skip if OFF) | unit+IT | — | — |
| A-SCH-006 | A | covered | `XuguSchemaTempCommentTest#localTempStrategy_A_SCH_004_006`; `XuguSchemaTempCommentTest#globalTempStrategy_A_SCH_005_006_preconditionDocumented` | unit | — | — |
| A-SCH-007 | A | negative-only | `XuguSchemaTempCommentTest#tempTableExporterDoesNotEmitFk_A_SCH_007`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | unit+IT | P-003 negative bundle | P-003 |
| A-SCH-008 | A | covered | `XuguSchemaTempCommentTest#commentOnAndInline_A_SCH_008_009_010`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | unit+IT | — | — |
| A-SCH-009 | A | covered | same as A-SCH-008 | unit+IT | — | — |
| A-SCH-010 | A | covered | same as A-SCH-008 | unit+IT | — | — |
| A-SCH-011 | A | covered | `XuguSchemaTempCommentTest#uniqueFkCheckAlterTruncateIndex_A_SCH_011_to_016`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | unit+IT | — | — |
| A-SCH-012 | A | covered | same | unit+IT | — | — |
| A-SCH-013 | A | covered | same | unit+IT | — | — |
| A-SCH-014 | A | covered | `XuguSchemaTempCommentTest#uniqueFkCheckAlterTruncateIndex_A_SCH_011_to_016` (drop constraint strings) | unit | Add live DROP CONSTRAINT IT | P-002 |
| A-SCH-015 | A | covered | `XuguSchemaTempCommentTest#uniqueFkCheckAlterTruncateIndex_A_SCH_011_to_016`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | unit+IT | — | — |
| A-SCH-016 | A | covered | same | unit+IT | — | — |
| A-SCH-017 | A | negative-only | — | none | Defer | later |

### Definition A — P-008 SPI & cross-cutting

| matrix_id | matrix_source | status_in_matrix | entry_class#method | gate | gap_action | owner_phase |
|-----------|---------------|------------------|-------------------|------|------------|-------------|
| A-SPI-001 | A | covered | `XuguDialectResolverIT#explicitDialect_sessionFactorySimpleQuery` | IT | — | — |
| A-SPI-002 | A | covered | `XuguDialectServicesResourceTest#servicesFileOnClasspathListsXuguDialectResolver`; `XuguDialectResolverIT#spiAutoResolve_sessionFactoryWithoutExplicitDialect` | unit+IT | — | — |
| A-SPI-003 | A | covered | `XuguDialectResolverTest#resolvesXuguProductName_withDatabaseVersion`; `XuguDialectResolverIT#spiAutoResolve_sessionFactoryWithoutExplicitDialect` | unit+IT | — | — |
| A-SPI-004 | A | covered | `XuguDialectResolverTest#returnsNullForMySQL`; `#returnsNullForOracle`; `#returnsNullForPostgreSQL` | unit | — | — |
| A-XCUT-001 | A | gap | — | none | Add IdentifierHelper UPPER fold unit/IT | P-002 |
| A-XCUT-002 | A | covered | `XuguDialectTest#quoteCharsAreDoubleQuote` | unit | — | — |
| A-XCUT-003 | A | covered | `DemoOfflineSmokeTest#applicationYmlDocumentsExplicitDialectAndEnvKeys`; `XuguDialectResolverIT` (jdbcUrl compatiblemode=NONE via `XuguTestConnection`) | unit+IT+demo | — | — |
| A-XCUT-004 | A | covered | `XuguDialectTest#keywordsIncludeTcl`; `XuguTypeRoundTripIT#jdbcTransactionCommitRollbackSmoke` | unit+IT | — | — |
| A-XCUT-005 | A | gap | — | none | Add isolation-level hook unit + optional live JDBC IT | P-002 |
| A-XCUT-006 | A | negative-only | — | none | Add explicit NOT-claimed test (P-003 negative) | P-003 |
| A-XCUT-007 | A | covered | `XuguDialectTest#keywordsIncludeTcl` | unit | — | — |
| A-XCUT-008 | A | covered | `XuguIdentitySequenceTest#sequenceSupportWired_A_SEQ_001_003_008` | unit | — | — |
| A-XCUT-009 | A | covered | `DemoOfflineSmokeTest#applicationYmlDocumentsExplicitDialectAndEnvKeys`; `DemoPersonCrudIT#persistAndFindPerson` | demo | Expand demo smoke (see GAP-SUMMARY) | P-005 |
| A-XCUT-010 | A | negative-only | — | none | Charter non-goal — document-only | P-003 |
| A-XCUT-011 | A | negative-only | — | none | Charter non-goal — document-only | P-003 |
| A-XCUT-012 | A | negative-only | — | none | Ship deferred | later |

### Ruler C — all rows

| matrix_id | matrix_source | status_in_matrix | entry_class#method | gate | gap_action | owner_phase |
|-----------|---------------|------------------|-------------------|------|------------|-------------|
| C-EXC-001 | C | covered | `XuguExceptionMappingIT#sessionUniqueViolationMapsToConstraintViolationException`; `XuguExceptionConversionTest#conversionMapsUniqueViolation` | unit+IT | — | — |
| C-EXC-002 | C | covered | `XuguExceptionConversionTest#extractorParsesNotNullFieldName`; `#extractorReturnsNullWhenNameAbsent` | unit | Add live ORM path asserting constraint name when present | P-002 |
| C-JSON-001 | C | covered | `XuguJsonAggregateIT#jsonColumnRoundTripAndHqlAggregates`; `XuguJsonAggregateSupportTest#jsonAggFunctionsConstruct` | unit+IT | — | — |
| C-JSON-002 | C | covered | `XuguJsonAggregateIT#jsonColumnRoundTripAndHqlAggregates` | IT | — | — |
| C-JSON-003 | C | covered | `XuguJsonAggregateIT#jsonColumnRoundTripAndHqlAggregates`; `XuguJsonAggregateSupportTest#dialectWiresAggregateSupportAndCastingJsonType` | unit+IT | — | — |
| C-JSON-004 | C | covered | `XuguJsonAggregateIT#jsonColumnRoundTripAndHqlAggregates` | IT | — | — |
| C-JSON-005 | C | negative-only | — | none | Defer (A-FUN-017 subset sufficient) | later |
| C-JSON-006 | C | negative-only | — | none | Defer pending doc confirm | later |
| C-WIN-001 | C | covered | `XuguWindowCteIT#hqlWindowAndWithClauseOnLiveSession`; `XuguWindowCteSupportTest#dialectEnablesWindowAndWithClause` | unit+IT | — | — |
| C-CTE-001 | C | covered | `XuguWindowCteIT#hqlWindowAndWithClauseOnLiveSession` | IT | — | — |
| C-BULK-001 | C | covered | `XuguBulkMutationIT#bulkUpdateOnJoinedInheritanceSucceeds`; `#bulkDeleteOnJoinedInheritanceSucceeds`; `XuguBulkMutationSupportTest#localTemporaryTableStrategyForBulkMutation_C_BULK_001` | unit+IT | — | — |
| C-BULK-002 | C | gap | — | none | **No test for `getFallbackSqmInsertStrategy`**; live IT documented N/A (GetGeneratedKeys blocker) | P-004 |
| C-BULK-003 | C | covered | `XuguBulkMutationSupportTest#supportsSubqueryOnMutatingTableIsFalse_C_BULK_003`; `XuguBulkMutationIT#dialectExposesLocalTempBulkStrategyFlags` | unit+IT | — | — |
| C-DDL-001 | C | covered | `XuguTypeDdlDetailsTest#createTableIfNotExists_C_DDL_001`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` | unit+IT | — | — |
| C-DDL-002 | C | covered | `XuguTypeDdlDetailsTest#alterColumnType_C_DDL_002`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` | unit+IT | — | — |
| C-DDL-003 | C | covered | `XuguTypeDdlDetailsTest#datetimeLiteralAndFormat_C_DDL_003`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` | unit+IT | — | — |
| C-DDL-004 | C | negative-only | `XuguTypeDdlDetailsTest#enumTypeDeclarationIsNull_C_DDL_004` | unit | P-003 negative bundle | P-003 |
| C-DDL-005 | C | negative-only | — | none | Defer (aligns A-TYP-015) | later |
| C-CAT-001 | C | covered | `XuguTypeDdlDetailsTest#catalogCreateDrop_C_CAT_001`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` | unit+IT | — | — |
| C-GUID-001 | C | covered | `XuguTypeDdlDetailsTest#selectGuidString_C_GUID_001`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` | unit+IT | — | — |
| C-LOCK-001 | C | covered | `XuguPaginationLockTest#nowaitAndWaitMilliseconds_A_LCK_003` (`supportsNoWait/Wait`); `XuguLockIT#forUpdateExecutesAndSkipLockedUnsupported` | unit+IT | Audit-only (已有) | — |
| C-SKIP-001 | C | negative-only | `XuguPaginationLockTest#skipLockedNotSupported_A_LCK_004`; `XuguLockIT#forUpdateExecutesAndSkipLockedUnsupported` | unit+IT | Same as A-LCK-004 | P-003 |
| C-SRV-001 | C | negative-only | — | none | Defer | later |
| C-SEL-001 | C | negative-only | — | none | Defer (A-SPI-* covers resolver) | later |

### Supplementary IT (not matrix rows — evidence anchors)

| anchor | entry_class#method | maps_to |
|--------|-------------------|---------|
| I-002 validate hotfix | `XuguSchemaValidateIT#schemaValidateSucceedsWhenSequenceExists` | A-SEQ-001 metadata |
| I-002 HQL pagination | `XuguHqlPaginationIT` | A-PAG-* ORM entry |
| I-004 identity on reserved name | `XuguReservedIdentityIT#identityPersistOnReservedTableOrderBackfillsId` | A-IDN-003/004 edge |
| I-004 sequence drop | `XuguAutoSequenceDropIT` | A-SEQ-002 drop safety |
| SqlAst translator | `XuguSqlAstTranslatorTest` | A-PAG-001/002 AST path wiring |

---

## Summary counts

| Bucket | Count |
|--------|------:|
| **Definition A rows total** | 105 |
| **Definition A 可实现** | 78 |
| **Ruler C rows total** | 24 |
| **Ruler C 可实现** | 16 |
| **Combined unique matrix_ids** | 129 |
| **可实现 rows mapped** | 94 |
| **covered (可实现)** | 87 |
| **gap (可实现)** | 7 |
| **negative-only (文档不允许 + 延后 + C defer/forbidden)** | 34 |

### 可实现 gap IDs (7)

`A-TYP-019`, `A-DDL-005`, `A-XCUT-001`, `A-XCUT-005`, `A-SCH-014` (live DROP CONSTRAINT), `C-BULK-002`, `C-EXC-002` (live ORM constraint-name path)

### P-003 negative assertion scope (Definition A 文档不允许 + selected 延后)

| ID | Current negative evidence | P-003 action |
|----|---------------------------|--------------|
| A-PAG-005 | ✅ unit+IT (no FETCH FIRST) | Consolidate checklist |
| A-LCK-004 | ✅ unit+IT | Bundle with C-SKIP-001 |
| A-LCK-005 | ✅ unit only | Add IT if pessimistic-read path tested |
| A-SCH-007 | ✅ unit+IT (no FK on temp) | Consolidate |
| A-XCUT-006 | ❌ no test | **Add explicit NOT-claimed test** |
| A-XCUT-010 | ❌ charter doc only | Document in negative suite |
| A-XCUT-011 | ❌ charter doc only | Document in negative suite |
| A-DDL-007 | partial (CREATE_ONLY comment) | Optional negative |
| A-FUN-015 | ✅ unit (bit_and not registered) | Optional |

Ruler C negatives: `C-DDL-004` (✅ unit), `C-SKIP-001` (✅ via A-LCK-004).

---

## Explicit call-outs

### C-BULK-002 (bulk insert fallback)

- **Matrix status:** 可实现 (I-003 P-005 delivered in code).
- **Test status:** **gap** — no `@Test` references `getFallbackSqmInsertStrategy` or `LocalTemporaryTableInsertStrategy`.
- **Evidence:** `XuguBulkMutationIT` covers update/delete only; matrix acceptance notes live IT **N/A** due to GetGeneratedKeys blocker on bulk-insert path.
- **Suggested owner_phase:** P-004 (unit wiring assert minimum) + document live IT waiver.

### Demo smoke coverage

| Capability | Demo entrypoint | Status |
|------------|-----------------|--------|
| Env secrets / explicit dialect | `DemoOfflineSmokeTest#applicationYmlDocumentsExplicitDialectAndEnvKeys` | covered (offline) |
| Table prefix convention | `DemoOfflineSmokeTest#demoPersonTableUsesHibDemoPrefix` | covered |
| Spring Boot + JPA CRUD + IDENTITY | `DemoPersonCrudIT#persistAndFindPerson` | covered (gated IT) |
| HQL pagination / Pageable | — | **gap** |
| hbm2ddl validate | — | **gap** |
| Function/HQL smoke | — | **gap** |
| Bulk mutation | — | **gap** |

**owner_phase:** P-005 for expanded demo IT suite.

### Negative assertion scope (P-003)

Primary **must-add** negatives: `A-XCUT-006` (READ UNCOMMITTED not claimed).  
Primary **consolidate** negatives (evidence exists, scattered): `A-PAG-005`, `A-LCK-004/005`, `A-SCH-007`, `C-DDL-004`, `C-SKIP-001`.  
Charter non-goals (`A-XCUT-010/011`): documentation-only unless harness requires explicit regression guard.
