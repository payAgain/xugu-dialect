# Production Regression Baseline (SSOT)

> **Status:** Accepted baseline inventory (I-005 / P-001 / RP-02)  
> **Initiative:** I-005 — production regression baseline  
> **Author role:** architect-contract  
> **Sources:** [`feature-matrix-definition-a.md`](feature-matrix-definition-a.md), [`feature-matrix-i003-ruler-c.md`](feature-matrix-i003-ruler-c.md), [`harness/evidence/researcher/I-005/P-001/INVENTORY.md`](../harness/evidence/researcher/I-005/P-001/INVENTORY.md)  
> **Scope:** Definition A **可实现** (78) + Ruler C **可实现** (16) = **94 matrix rows**; plus **negative-only** rows for **文档不允许** / **延后**  
> **IT gate:** `XuguITGate.isEnabled()` ← env `XUGU_RUN_IT=true` or JVM `-Dxugu.run.integration=true`  
> **Demo IT gate:** `XuguIntegrationGate.isEnabled()` (same property/env)

## Status legend

| status | Meaning |
|---|---|
| **covered** | At least one verified test entrypoint (unit / IT / demo) |
| **covered-live** | Live gated IT PASS on an 可实现 row (no waiver) |
| **known-limit-documented** | Code wired; live IT waived with formal SSOT + user-doc known limitation |
| **gap** | No entrypoint, or missing expected live IT on an 可实现 row |
| **negative-only** | 文档不允许 / 延后 — explicit non-support or defer; no positive SQL invention |

## Gate legend

| gate | Meaning |
|---|---|
| **unit** | Runs without live XuguDB |
| **IT** | Gated integration test (`XuguITGate`; see IT gate above) |
| **demo** | `demo-spring-boot` smoke (offline unit or gated `@SpringBootTest`) |
| **none** | No automated entrypoint |

## gap_action legend

| gap_action | Meaning |
|---|---|
| **P-002** | Close live-entry or unit wiring gap in P-002 |
| **P-003** | Negative assertion bundle or consolidation |
| **P-004** | Bulk insert / window-CTE adjacent gap |
| **P-005** | Demo smoke expansion |
| **N/A** | Covered, or deferred with no Phase owner in I-005 first batch |

---

## SSOT — 可实现 rows (94)

Columns: `matrix_id | status | entry_class#method | gate | gap_action`

### Definition A — P-003 Types (14)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-TYP-001 | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-002 | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-003 | covered | `XuguDialectTest#columnTypesMatchXuguDocs` (REAL→float) | unit | N/A |
| A-TYP-004 | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguDialectTest#sizeAndPrecisionDefaults` (CHAR trim); `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-005 | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguDialectTest#booleanLiteralsAreTrueFalse`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-006 | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-007 | covered | `XuguDialectTest#columnTypesMatchXuguDocs` (time `$p`); `XuguTypeRoundTripIT#jdbcTimeRoundTrip_A_TYP_007` | IT | N/A |
| A-TYP-008 | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-009 | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguBinarySchemaExportIT#schemaExportEmitsBareBinaryAndCreatesOnDb`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-010 | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-011 | covered | `XuguDialectTest#columnTypesMatchXuguDocs` (NCLOB→clob); `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-012 | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-013 | covered | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes`; `XuguJsonAggregateIT#jsonColumnRoundTripAndHqlAggregates` | IT | N/A |
| A-TYP-019 | covered | `XuguDialectTest#castPatternDefaultUsesStandardCastSyntax_A_TYP_019` | unit | N/A |

### Definition A — P-003 DDL (6)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-DDL-001 | covered | `XuguDdlIT#schemaExportCreateDropWithPkAndNotNull`; `XuguDialectTest#ddlHelpersMatchXuguSyntax` | IT | N/A |
| A-DDL-002 | covered | `XuguDdlIT#schemaExportCreateDropWithPkAndNotNull` (ALTER ADD COLUMN) | IT | N/A |
| A-DDL-003 | covered | `XuguDdlIT#schemaExportCreateDropWithPkAndNotNull` | IT | N/A |
| A-DDL-004 | covered | `XuguDdlIT#schemaExportCreateDropWithPkAndNotNull` (NOT NULL reject) | IT | N/A |
| A-DDL-005 | covered | `XuguDefaultColumnExportTest#schemaExportEmitsDefaultColumn_A_DDL_005` | unit | N/A |
| A-DDL-006 | covered | `XuguDdlIT#schemaExportCreateDropWithPkAndNotNull`; `XuguDialectTest#ddlHelpersMatchXuguSyntax` | IT | N/A |

### Definition A — P-004 Pagination (3)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-PAG-001 | covered | `XuguPaginationLockTest#limitOnlyUsesBindMarker_A_PAG_001_003`; `XuguPaginationIT#limitAndOffsetReturnExpectedRows`; `XuguHqlPaginationIT#hqlSetFirstResultMaxResultsUsesLimitNotFetchFirst` | IT | N/A |
| A-PAG-002 | covered | `XuguPaginationLockTest#limitOffsetStableForm_A_PAG_002`; `XuguPaginationIT#limitAndOffsetReturnExpectedRows`; `XuguHqlPaginationIT#hqlSetFirstResultMaxResultsUsesLimitNotFetchFirst` | IT | N/A |
| A-PAG-003 | covered | `XuguPaginationLockTest#limitOnlyUsesBindMarker_A_PAG_001_003`; `XuguPaginationIT#limitAndOffsetReturnExpectedRows` | IT | N/A |

### Definition A — P-004 Locks (3)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-LCK-001 | covered | `XuguPaginationLockTest#forUpdateBasic_A_LCK_001`; `XuguLockIT#forUpdateExecutesAndSkipLockedUnsupported`; `XuguHqlPaginationIT#hqlLockAndPageEmitsForUpdateBeforeLimitAndWaitAfter` | IT | N/A |
| A-LCK-002 | covered | `XuguPaginationLockTest#forUpdateOf_A_LCK_002`; `XuguLockIT#forUpdateExecutesAndSkipLockedUnsupported` | IT | N/A |
| A-LCK-003 | covered | `XuguPaginationLockTest#nowaitAndWaitMilliseconds_A_LCK_003`; `XuguLockIT#forUpdateExecutesAndSkipLockedUnsupported`; `XuguLockIT#limitForUpdateComboExecutes`; `XuguHqlPaginationIT#hqlLockAndPageEmitsForUpdateBeforeLimitAndWaitAfter` | IT | N/A |

### Definition A — P-005 Identity & Sequence (9)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-IDN-001 | covered | `XuguIdentitySequenceTest#identitySupportWired_A_IDN_001` | unit | N/A |
| A-IDN-002 | covered | `XuguIdentitySequenceTest#identitySupportWired_A_IDN_001` (no AUTO_INCREMENT) | unit | N/A |
| A-IDN-003 | covered | `XuguIdentitySequenceTest#identitySelectFallback_A_IDN_003`; `XuguIdentitySequenceIT#identityPersistBackfillsId_A_IDN_003_004` | IT | N/A |
| A-IDN-004 | covered | `XuguIdentitySequenceIT#identityPersistBackfillsId_A_IDN_003_004`; `DemoPersonCrudIT#persistAndFindPerson` | IT | N/A |
| A-SEQ-001 | covered | `XuguIdentitySequenceTest#createDropSequenceStrings_A_SEQ_001_002_005`; `XuguIdentitySequenceTest#sequenceMetadataQueryAndExtractorWired`; `XuguSchemaValidateIT#schemaValidateSucceedsWhenSequenceExists` | IT | N/A |
| A-SEQ-002 | covered | `XuguIdentitySequenceTest#createDropSequenceStrings_A_SEQ_001_002_005` | unit | N/A |
| A-SEQ-003 | covered | `XuguIdentitySequenceTest#sequenceSupportWired_A_SEQ_001_003_008`; `XuguIdentitySequenceIT#sequenceGeneratorPersist_A_SEQ_003_004_008` | IT | N/A |
| A-SEQ-004 | covered | `XuguIdentitySequenceTest#currvalFunctionForm_A_SEQ_004`; `XuguIdentitySequenceIT#sequenceGeneratorPersist_A_SEQ_003_004_008` | IT | N/A |
| A-SEQ-005 | covered | `XuguIdentitySequenceTest#createDropSequenceStrings_A_SEQ_001_002_005` | unit | N/A |

### Definition A — P-006 Functions (17)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-FUN-001 | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |
| A-FUN-002 | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |
| A-FUN-003 | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered` | unit | N/A |
| A-FUN-004 | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |
| A-FUN-005 | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered` | unit | N/A |
| A-FUN-006 | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered` | unit | N/A |
| A-FUN-007 | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered` | unit | N/A |
| A-FUN-008 | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` (abs) | IT | N/A |
| A-FUN-009 | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered` | unit | N/A |
| A-FUN-010 | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |
| A-FUN-011 | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |
| A-FUN-012 | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` (to_char path) | IT | N/A |
| A-FUN-013 | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |
| A-FUN-014 | covered | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |
| A-FUN-016 | covered | `XuguFunctionRegistryTest#uuidPrimaryIsUuid`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |
| A-FUN-017 | covered | `XuguFunctionRegistryTest#jsonSubsetUsesStandardJsonValueNotMysqlDump`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |
| A-FUN-018 | covered | `XuguFunctionRegistryTest#listaggUsesNativeListaggFunction`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |

### Definition A — P-007 Schema / temp / comment / constraints (14)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-SCH-001 | covered | `XuguSchemaTempCommentTest#schemaCreateDropCommands_A_SCH_001`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | IT | N/A |
| A-SCH-002 | covered | `XuguSchemaTempCommentTest#nameQualifierIsSchemaOnly_A_SCH_002_not_003`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | IT | N/A |
| A-SCH-004 | covered | `XuguSchemaTempCommentTest#localTempStrategy_A_SCH_004_006`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | IT | N/A |
| A-SCH-005 | covered | `XuguSchemaTempCommentTest#globalTempStrategy_A_SCH_005_006_preconditionDocumented`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` (gated skip if OFF) | IT | N/A |
| A-SCH-006 | covered | `XuguSchemaTempCommentTest#localTempStrategy_A_SCH_004_006`; `XuguSchemaTempCommentTest#globalTempStrategy_A_SCH_005_006_preconditionDocumented` | unit | N/A |
| A-SCH-008 | covered | `XuguSchemaTempCommentTest#commentOnAndInline_A_SCH_008_009_010`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | IT | N/A |
| A-SCH-009 | covered | same as A-SCH-008 | IT | N/A |
| A-SCH-010 | covered | same as A-SCH-008 | IT | N/A |
| A-SCH-011 | covered | `XuguSchemaTempCommentTest#uniqueFkCheckAlterTruncateIndex_A_SCH_011_to_016`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | IT | N/A |
| A-SCH-012 | covered | same as A-SCH-011 | IT | N/A |
| A-SCH-013 | covered | same as A-SCH-011 | IT | N/A |
| A-SCH-014 | covered | `XuguSchemaTempCommentTest#uniqueFkCheckAlterTruncateIndex_A_SCH_011_to_016`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` (DROP CONSTRAINT live) | IT | N/A |
| A-SCH-015 | covered | `XuguSchemaTempCommentTest#uniqueFkCheckAlterTruncateIndex_A_SCH_011_to_016`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | IT | N/A |
| A-SCH-016 | covered | same as A-SCH-015 | IT | N/A |

### Definition A — P-008 SPI & cross-cutting (12: SPI 4 + cross-cutting 8)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-SPI-001 | covered | `XuguDialectResolverIT#explicitDialect_sessionFactorySimpleQuery` | IT | N/A |
| A-SPI-002 | covered | `XuguDialectServicesResourceTest#servicesFileOnClasspathListsXuguDialectResolver`; `XuguDialectResolverIT#spiAutoResolve_sessionFactoryWithoutExplicitDialect` | IT | N/A |
| A-SPI-003 | covered | `XuguDialectResolverTest#resolvesXuguProductName_withDatabaseVersion`; `XuguDialectResolverIT#spiAutoResolve_sessionFactoryWithoutExplicitDialect` | IT | N/A |
| A-SPI-004 | covered | `XuguDialectResolverTest#returnsNullForMySQL`; `#returnsNullForOracle`; `#returnsNullForPostgreSQL` | unit | N/A |
| A-XCUT-001 | covered | `XuguDialectTest#unquotedIdentifiersFoldToUppercase_A_XCUT_001` | unit | N/A |
| A-XCUT-002 | covered | `XuguDialectTest#quoteCharsAreDoubleQuote` | unit | N/A |
| A-XCUT-003 | covered | `DemoOfflineSmokeTest#applicationYmlDocumentsExplicitDialectAndEnvKeys`; `XuguDialectResolverIT` (jdbcUrl compatiblemode=NONE via `XuguTestConnection`) | IT | N/A |
| A-XCUT-004 | covered | `XuguDialectTest#keywordsIncludeTcl`; `XuguTypeRoundTripIT#jdbcTransactionCommitRollbackSmoke` | IT | N/A |
| A-XCUT-005 | covered | `XuguDialectTest#isolationLevelHooksMatchXuguIsoLevel_A_XCUT_005` | unit | N/A |
| A-XCUT-007 | covered | `XuguDialectTest#keywordsIncludeTcl` | unit | N/A |
| A-XCUT-008 | covered | `XuguIdentitySequenceTest#sequenceSupportWired_A_SEQ_001_003_008` | unit | N/A |
| A-XCUT-009 | covered | `DemoOfflineSmokeTest#applicationYmlDocumentsExplicitDialectAndEnvKeys`; `DemoPersonCrudIT#persistAndFindPerson`; `DemoBootBaselineSmokeTest#sessionFactoryUsesExplicitXuguDialectFromApplicationYml`; `#jpaPersistAndJpqlQueryRoundTrip`; `#pageableFindAllUsesLimitOffset` | demo | N/A |

### Ruler C — 可实现 (16)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| C-EXC-001 | covered | `XuguExceptionMappingIT#sessionUniqueViolationMapsToConstraintViolationException`; `XuguExceptionConversionTest#conversionMapsUniqueViolation` | IT | N/A |
| C-EXC-002 | covered | `XuguExceptionConversionTest#extractorParsesNotNullFieldName`; `#extractorReturnsNullWhenNameAbsent`; `XuguExceptionMappingIT#sessionNotNullViolationExtractsFieldNameWhenPresent` | IT | N/A |
| C-JSON-001 | covered | `XuguJsonAggregateIT#jsonColumnRoundTripAndHqlAggregates`; `XuguJsonAggregateSupportTest#jsonAggFunctionsConstruct` | IT | N/A |
| C-JSON-002 | covered | `XuguJsonAggregateIT#jsonColumnRoundTripAndHqlAggregates` | IT | N/A |
| C-JSON-003 | covered | `XuguJsonAggregateIT#jsonColumnRoundTripAndHqlAggregates`; `XuguJsonAggregateSupportTest#dialectWiresAggregateSupportAndCastingJsonType` | IT | N/A |
| C-JSON-004 | covered | `XuguJsonAggregateIT#jsonColumnRoundTripAndHqlAggregates` | IT | N/A |
| C-WIN-001 | covered | `XuguWindowCteIT#hqlWindowAndWithClauseOnLiveSession`; `XuguWindowCteSupportTest#dialectEnablesWindowAndWithClause` | IT | N/A |
| C-CTE-001 | covered | `XuguWindowCteIT#hqlWindowAndWithClauseOnLiveSession` | IT | N/A |
| C-BULK-001 | covered | `XuguBulkMutationIT#bulkUpdateOnJoinedInheritanceSucceeds`; `#bulkDeleteOnJoinedInheritanceSucceeds`; `XuguBulkMutationSupportTest#localTemporaryTableStrategyForBulkMutation_C_BULK_001` | IT | N/A |
| C-BULK-002 | covered-live | `XuguBulkMutationIT#bulkInsertOnJoinedInheritanceWithIdentitySucceeds_C_BULK_002`; `XuguBulkMutationSupportTest#fallbackSqmInsertStrategyWired_C_BULK_002` | IT | N/A |
| C-BULK-003 | covered | `XuguBulkMutationSupportTest#supportsSubqueryOnMutatingTableIsFalse_C_BULK_003`; `XuguBulkMutationIT#dialectExposesLocalTempBulkStrategyFlags` | IT | N/A |
| C-DDL-001 | covered | `XuguTypeDdlDetailsTest#createTableIfNotExists_C_DDL_001`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` | IT | N/A |
| C-DDL-002 | covered | `XuguTypeDdlDetailsTest#alterColumnType_C_DDL_002`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` | IT | N/A |
| C-DDL-003 | covered | `XuguTypeDdlDetailsTest#datetimeLiteralAndFormat_C_DDL_003`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` | IT | N/A |
| C-CAT-001 | covered | `XuguTypeDdlDetailsTest#catalogCreateDrop_C_CAT_001`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` | IT | N/A |
| C-GUID-001 | covered | `XuguTypeDdlDetailsTest#selectGuidString_C_GUID_001`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` | IT | N/A |
| C-JSON-005 | covered-live | `XuguFunctionRegistryTest#jsonSubsetUsesStandardJsonValueNotMysqlDump`; `XuguJsonSubsetDeepenIT#jsonSubsetDeepen_Hql_C_JSON_005` | IT | N/A |
| C-DDL-005 | covered-live | `XuguArrayTypeTest#arrayTypeHooksWired_A_TYP_015_C_DDL_005`; `XuguArrayTypeIT#arrayColumnRoundTrip_A_TYP_015_C_DDL_005` | IT | N/A |

### I-007 / P-004 — promoted from 延后 (Definition A)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-TYP-015 | covered-live | `XuguArrayTypeTest#arrayTypeHooksWired_A_TYP_015_C_DDL_005`; `XuguArrayTypeIT#arrayColumnRoundTrip_A_TYP_015_C_DDL_005` | IT | N/A |
| A-SEQ-006 | covered-live | `XuguIdentitySequenceTest#alterSequenceRestartUsesStartWith_A_SEQ_006`; `XuguAlterSequenceIT#alterSequenceStartWithAndIncrement_A_SEQ_006` | IT | N/A |

---

## SSOT — negative-only rows (文档不允许 / 延后)

Matrix status **文档不允许** or **延后**; baseline records explicit non-support or deferral. Rows with P-003 action require negative assertion consolidation in P-003.

### Definition A — 文档不允许 (7)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-PAG-005 | negative-only | `XuguNegativeRegressionBaselineTest#ansiFetchFirstNotEmitted_A_PAG_005`; `XuguPaginationLockTest#limitOnlyUsesBindMarker_A_PAG_001_003`; `XuguPaginationIT#limitAndOffsetReturnExpectedRows`; `XuguHqlPaginationIT#hqlSetFirstResultMaxResultsUsesLimitNotFetchFirst` | IT | P-003 |
| A-LCK-004 | negative-only | `XuguNegativeRegressionBaselineTest#skipLockedNotSupported_A_LCK_004_C_SKIP_001`; `XuguPaginationLockTest#skipLockedNotSupported_A_LCK_004`; `XuguLockIT#forUpdateExecutesAndSkipLockedUnsupported` | IT | P-003 |
| A-LCK-005 | negative-only | `XuguNegativeRegressionBaselineTest#forShareNotSupported_A_LCK_005`; `XuguPaginationLockTest#noForShare_A_LCK_005` | unit | P-003 |
| A-SCH-007 | negative-only | `XuguNegativeRegressionBaselineTest#tempTableFkNotEmitted_A_SCH_007`; `XuguSchemaTempCommentTest#tempTableExporterDoesNotEmitFk_A_SCH_007`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | IT | P-003 |
| A-XCUT-006 | negative-only | `XuguNegativeRegressionBaselineTest#readUncommittedNotClaimed_A_XCUT_006` | unit | P-003 |
| A-XCUT-010 | negative-only | `XuguNegativeRegressionBaselineTest#charterNoMySqlOracleInheritance_A_XCUT_010` | unit | P-003 |
| A-XCUT-011 | negative-only | `XuguNegativeRegressionBaselineTest#charterNoSiblingDialectPort_A_XCUT_011` | unit | P-003 |

### Definition A — 延后 (20)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-TYP-014 | negative-only | `XuguNegativeRegressionBaselineTest#deferred_A_TYP_014_interval` (@Disabled) | none | N/A |
| A-TYP-016 | negative-only | `XuguNegativeRegressionBaselineTest#deferred_A_TYP_016_xml` (@Disabled) | none | N/A |
| A-TYP-017 | negative-only | `XuguNegativeRegressionBaselineTest#deferred_A_TYP_017_spatial` (@Disabled) | none | N/A |
| A-TYP-018 | negative-only | `XuguNegativeRegressionBaselineTest#deferred_A_TYP_018_udt` (@Disabled) | none | N/A |
| A-DDL-007 | negative-only | `XuguNegativeRegressionBaselineTest#definitionAIfNotExistsDeferred_A_DDL_007`; `XuguDdlIT` comment (CREATE_ONLY defers IF NOT EXISTS) | unit | P-003 |
| A-DDL-008 | negative-only | `XuguNegativeRegressionBaselineTest#deferred_A_DDL_008_partitioning` (@Disabled) | none | N/A |
| A-DDL-009 | negative-only | `XuguNegativeRegressionBaselineTest#deferred_A_DDL_009_encrypt` (@Disabled) | none | N/A |
| A-PAG-004 | negative-only | `XuguNegativeRegressionBaselineTest#deferred_A_PAG_004_top` (@Disabled) | none | N/A |
| A-PAG-006 | negative-only | `XuguNegativeRegressionBaselineTest#deferred_A_PAG_006_rownum` (@Disabled) | none | N/A |
| A-LCK-006 | negative-only | `XuguNegativeRegressionBaselineTest#deferred_A_LCK_006_lockTable` (@Disabled) | none | N/A |
| A-IDN-005 | negative-only | `XuguNegativeRegressionBaselineTest#deferred_A_IDN_005_identityMode` (@Disabled) | none | N/A |
| A-FUN-015 | negative-only | `XuguFunctionRegistryTest#unsupportedFunctionNotRegistered_negativeNote` | unit | N/A |
| A-FUN-019 | negative-only | `XuguNegativeRegressionBaselineTest#deferred_A_FUN_019_regexp` (@Disabled) | none | N/A |
| A-FUN-020 | negative-only | `XuguNegativeRegressionBaselineTest#deferred_A_FUN_020_geometric` (@Disabled) | none | N/A |
| A-FUN-021 | negative-only | `XuguNegativeRegressionBaselineTest#deferred_A_FUN_021_xmlFunctions` (@Disabled) | none | N/A |
| A-SCH-003 | negative-only | `XuguNegativeRegressionBaselineTest#catalogsNotSupported_negativeOnly_A_SCH_003`; `XuguSchemaTempCommentTest#nameQualifierIsSchemaOnly_A_SCH_002_not_003` (supportsCatalogs=false) | unit | N/A |
| A-SCH-017 | negative-only | `XuguNegativeRegressionBaselineTest#deferred_A_SCH_017_advancedIndexes` (@Disabled) | none | N/A |
| A-XCUT-012 | negative-only | `XuguNegativeRegressionBaselineTest#deferred_A_XCUT_012_mavenCentralPublish` (@Disabled) | none | N/A |

### Ruler C — 文档不允许 / 延后 (7)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| C-DDL-004 | negative-only | `XuguNegativeRegressionBaselineTest#enumDdlNotEmitted_C_DDL_004`; `XuguTypeDdlDetailsTest#enumTypeDeclarationIsNull_C_DDL_004` | unit | P-003 |
| C-SKIP-001 | negative-only | `XuguNegativeRegressionBaselineTest#skipLockedNotSupported_A_LCK_004_C_SKIP_001`; `XuguPaginationLockTest#skipLockedNotSupported_A_LCK_004`; `XuguLockIT#forUpdateExecutesAndSkipLockedUnsupported` | IT | P-003 |
| C-JSON-006 | negative-only | `XuguNegativeRegressionBaselineTest#deferred_C_JSON_006_jsonTable` (@Disabled) | none | N/A |
| C-SRV-001 | negative-only | `XuguNegativeRegressionBaselineTest#deferred_C_SRV_001_serverConfiguration` (@Disabled) | none | N/A |
| C-SEL-001 | negative-only | `XuguNegativeRegressionBaselineTest#deferred_C_SEL_001_dialectSelector` (@Disabled) | none | N/A |

### Ruler C — 已有 (audit anchor, outside 94 可实现 count)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| C-LOCK-001 | covered | `XuguPaginationLockTest#nowaitAndWaitMilliseconds_A_LCK_003` (`supportsNoWait/Wait`); `XuguLockIT#forUpdateExecutesAndSkipLockedUnsupported` | IT | N/A |

---

## Explicit call-out — C-BULK-002 (bulk insert fallback)

| Field | Value |
|---|---|
| **matrix_id** | C-BULK-002 |
| **status** | **covered-live** |
| **Code surface** | `XuguDialect#getFallbackSqmInsertStrategy()` → `LocalTemporaryTableInsertStrategy` (EntityMappingType ctor — full entity temp table, aligned with Hibernate MySQLDialect) |
| **Test evidence** | `XuguBulkMutationSupportTest#fallbackSqmInsertStrategyWired_C_BULK_002` (offline wiring); **`XuguBulkMutationIT#bulkInsertOnJoinedInheritanceWithIdentitySucceeds_C_BULK_002`** (live JOINED + IDENTITY bulk insert) |
| **Related tests** | `XuguBulkMutationIT` (update/delete + insert live); `XuguBulkMutationSupportTest` (C-BULK-001/003 flags + C-BULK-002 insert wiring) |
| **Live IT** | **PASS** (I-007/P-002) — gated `XUGU_RUN_IT=true`; dialect fix: use `LocalTemporaryTableInsertStrategy(EntityMappingType, …)` instead of mistaken `TemporaryTable.createEntityTable(EntityMappingType, …)` id-table delegate |
| **User doc** | [`docs/user-guide/05-troubleshooting.md`](../docs/user-guide/05-troubleshooting.md) §10 (updated P-002) |
| **gap_action** | **N/A** — closed **covered-live** in I-007/P-002 |
| **I-007 re-open** | **Closed** — P-002 prefer-live-unblock **PASS**; evidence: `harness/evidence/test/I-007/P-002/`. SSOT: [`i007-capability-hardening-plan.md`](i007-capability-hardening-plan.md) § C-BULK-002 STRATEGY LOCK. |

---

## Explicit call-out — Demo smoke baseline

| Capability | entry_class#method | gate | status | gap_action |
|---|---|---|---|---|
| Env secrets / explicit dialect | `DemoOfflineSmokeTest#applicationYmlDocumentsExplicitDialectAndEnvKeys` | demo | covered | N/A |
| Table prefix convention | `DemoOfflineSmokeTest#demoPersonTableUsesHibDemoPrefix` | demo | covered | N/A |
| Spring Boot + JPA CRUD + IDENTITY | `DemoPersonCrudIT#persistAndFindPerson` | demo | covered | N/A |
| SessionFactory + explicit dialect (consumer path) | `DemoBootBaselineSmokeTest#sessionFactoryUsesExplicitXuguDialectFromApplicationYml`; `#datasourceUrlIncludesCompatibleModeNone` | demo | covered | N/A |
| JPA JPQL smoke | `DemoBootBaselineSmokeTest#jpaPersistAndJpqlQueryRoundTrip` | demo | covered | N/A |
| Spring Data Pageable / LIMIT-OFFSET | `DemoBootBaselineSmokeTest#pageableFindAllUsesLimitOffset` | demo | covered | N/A |
| hbm2ddl validate | — | none | gap | N/A |
| Function / HQL smoke | — | none | gap | N/A |
| Bulk mutation | — | none | gap | N/A |

**Offline (default `mvn test`):** `DemoOfflineSmokeTest` — no Spring context, no live DB.  
**Gated live (`XUGU_RUN_IT=true` or `-Dxugu.run.integration=true`):** `DemoPersonCrudIT`, `DemoBootBaselineSmokeTest`.

---

## Explicit call-out — Negative assertion scope (closed P-003)

All **must-add** and **consolidate** negatives from the P-001 gap audit are **closed in I-005/P-003**:

| matrix_id | Current evidence | Status |
|---|---|---|
| A-XCUT-006 | `XuguNegativeRegressionBaselineTest#readUncommittedNotClaimed_A_XCUT_006` | **Closed P-003** — READ UNCOMMITTED not claimed |
| A-XCUT-010 | `XuguNegativeRegressionBaselineTest#charterNoMySqlOracleInheritance_A_XCUT_010` | **Closed P-003** |
| A-XCUT-011 | `XuguNegativeRegressionBaselineTest#charterNoSiblingDialectPort_A_XCUT_011` | **Closed P-003** |
| A-PAG-005 | `XuguNegativeRegressionBaselineTest#ansiFetchFirstNotEmitted_A_PAG_005` + IT bundle | **Closed P-003** |
| A-LCK-004 / C-SKIP-001 | `XuguNegativeRegressionBaselineTest#skipLockedNotSupported_A_LCK_004_C_SKIP_001` + IT | **Closed P-003** |
| A-LCK-005 | `XuguNegativeRegressionBaselineTest#forShareNotSupported_A_LCK_005` | **Closed P-003** |
| A-SCH-007 | `XuguNegativeRegressionBaselineTest#tempTableFkNotEmitted_A_SCH_007` + IT | **Closed P-003** |
| C-DDL-004 | `XuguNegativeRegressionBaselineTest#enumDdlNotEmitted_C_DDL_004` | **Closed P-003** |

Optional negatives (also covered): A-DDL-007 (IF NOT EXISTS defer), A-FUN-015 (bit_and not registered).

---

## Gap summary by domain (P-002 must-close)

Source: [`harness/evidence/researcher/I-005/P-001/GAP-SUMMARY.md`](../harness/evidence/researcher/I-005/P-001/GAP-SUMMARY.md)

### 1. Types & identifiers

| matrix_id | gap | P-002 action |
|---|---|---|
| A-TYP-019 | No `castPattern` test | Unit assert default cast SQL pattern |
| A-TYP-007 | TIME: unit only | Add JDBC TIME round-trip in `XuguTypeRoundTripIT` |
| A-XCUT-001 | No IdentifierHelper UPPER test | Unit: unquoted → uppercase fold |

### 2. DDL & schema export

| matrix_id | gap | P-002 action |
|---|---|---|
| A-DDL-005 | No DEFAULT column exporter test | Schema-export assert or gated IT |
| A-SCH-014 | DROP CONSTRAINT not exercised live | Add IT step: `ALTER TABLE … DROP CONSTRAINT` |

### 3. Exception mapping (Ruler C)

| matrix_id | gap | P-002 action |
|---|---|---|
| C-EXC-002 | Constraint name not asserted on live ORM path | Extend `XuguExceptionMappingIT` or NOT NULL violation IT |

### 4. Isolation & session config

| matrix_id | gap | Status |
|---|---|---|
| A-XCUT-005 | No isolation-level test | **Closed P-002** — unit dialect hooks |
| A-XCUT-006 | negative-only — no test | **Closed P-003** — `XuguNegativeRegressionBaselineTest#readUncommittedNotClaimed_A_XCUT_006` |

### 5. Bulk mutation (routed)

| matrix_id | gap | Owner |
|---|---|---|
| C-BULK-002 | Bulk insert fallback live IT | **Closed I-007/P-002** — **`covered-live`** via `XuguBulkMutationIT#bulkInsertOnJoinedInheritanceWithIdentitySucceeds_C_BULK_002`; supersedes I-005/P-004 known-limit closure — see [`i007-capability-hardening-plan.md`](i007-capability-hardening-plan.md) § C-BULK-002 STRATEGY LOCK |

### 6. Demo smoke (routed)

| gap | Owner |
|---|---|
| HQL pagination, validate, function/HQL, bulk mutation demo paths | **Closed P-005** — Pageable + SessionFactory/JPA smoke in `DemoBootBaselineSmokeTest`; validate/function/bulk remain optional demo gaps |

### Hard gap IDs (6 total, 可实现 rows — all closed in I-005 first batch)

`A-TYP-019`, `A-DDL-005`, `A-XCUT-001`, `A-XCUT-005`, `A-SCH-014`, `C-EXC-002`

*(C-BULK-002 promoted to **covered-live** in I-007/P-002; A-TYP-007 stretch live TIME IT in P-002.)*

---

## Summary counts

| Bucket | Count |
|---|---:|
| **可实现 rows (SSOT primary)** | **94** |
| covered (可实现) | 94 |
| known-limit-documented (可实现) | 0 |
| gap (可实现) | 0 |
| negative-only (文档不允许 + 延后 + C defer) | 30 |
| I-007/P-004 promoted (covered-live) | 4 |
| Ruler C 已有 (C-LOCK-001) | 1 |
| **Total baseline rows** | **129** |

### gap_action routing (可实现 gaps only)

| gap_action | matrix_ids |
|---|---|
| P-002 | *(closed in I-005/P-002)* |
| P-004 | *(superseded for C-BULK-002 — see I-007/P-002 covered-live)* |
| P-005 | *(closed — A-XCUT-009 demo boot smoke)* |

---

## Supplementary evidence anchors (not separate matrix rows)

| anchor | entry_class#method | maps_to |
|---|---|---|
| I-002 validate hotfix | `XuguSchemaValidateIT#schemaValidateSucceedsWhenSequenceExists` | A-SEQ-001 metadata |
| I-002 HQL pagination | `XuguHqlPaginationIT` | A-PAG-* ORM entry |
| I-004 reserved table identity | `XuguReservedIdentityIT#identityPersistOnReservedTableOrderBackfillsId` | A-IDN-003/004 edge |
| I-004 sequence drop guard | `XuguAutoSequenceDropIT` | A-SEQ-002 drop safety |
| SqlAst translator wiring | `XuguSqlAstTranslatorTest` | A-PAG-001/002 AST path |

---

## Orchestrator note

This file is the **production regression baseline SSOT** for I-005. Implementers close gaps per `gap_action` owner Phase; reviewer audits row completeness against feature matrices without inventing test methods.
