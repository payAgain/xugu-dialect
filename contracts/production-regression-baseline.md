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
| A-TYP-001 | covered-live | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-002 | covered-live | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-003 | covered-live | `XuguDialectTest#columnTypesMatchXuguDocs` (REAL→float); `XuguTypeRoundTripIT#jdbcRealFloatRoundTrip_A_TYP_003` | IT | N/A |
| A-TYP-004 | covered-live | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguDialectTest#sizeAndPrecisionDefaults` (CHAR trim); `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-005 | covered-live | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguDialectTest#booleanLiteralsAreTrueFalse`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-006 | covered-live | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-007 | covered-live | `XuguDialectTest#columnTypesMatchXuguDocs` (time `$p`); `XuguTypeRoundTripIT#jdbcTimeRoundTrip_A_TYP_007` | IT | N/A |
| A-TYP-008 | covered-live | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-009 | covered-live | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguBinarySchemaExportIT#schemaExportEmitsBareBinaryAndCreatesOnDb`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-010 | covered-live | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-011 | covered-live | `XuguDialectTest#columnTypesMatchXuguDocs` (NCLOB→clob); `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-012 | covered-live | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes` | IT | N/A |
| A-TYP-013 | covered-live | `XuguDialectTest#columnTypesMatchXuguDocs`; `XuguTypeRoundTripIT#typeRoundTripKeyTypes`; `XuguJsonAggregateIT#jsonColumnRoundTripAndHqlAggregates` | IT | N/A |
| A-TYP-019 | covered-live | `XuguDialectTest#castPatternDefaultUsesStandardCastSyntax_A_TYP_019`; `XuguCastPatternIT#castExpressionOnLiveDb_A_TYP_019` | IT | N/A |

### Definition A — P-003 DDL (6)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-DDL-001 | covered-live | `XuguDdlIT#schemaExportCreateDropWithPkAndNotNull`; `XuguDialectTest#ddlHelpersMatchXuguSyntax` | IT | N/A |
| A-DDL-002 | covered-live | `XuguDdlIT#schemaExportCreateDropWithPkAndNotNull` (ALTER ADD COLUMN) | IT | N/A |
| A-DDL-003 | covered-live | `XuguDdlIT#schemaExportCreateDropWithPkAndNotNull` | IT | N/A |
| A-DDL-004 | covered-live | `XuguDdlIT#schemaExportCreateDropWithPkAndNotNull` (NOT NULL reject) | IT | N/A |
| A-DDL-005 | covered-live | `XuguDefaultColumnExportTest#schemaExportEmitsDefaultColumn_A_DDL_005`; `XuguDefaultColumnExportIT#schemaExportEmitsDefaultColumnAndAppliesOnDb_A_DDL_005` | IT | N/A |
| A-DDL-006 | covered-live | `XuguDdlIT#schemaExportCreateDropWithPkAndNotNull`; `XuguDialectTest#ddlHelpersMatchXuguSyntax` | IT | N/A |
| A-DDL-007 | covered-live | `XuguTableDdlExtensionsTest#ifNotExistsPromotedViaC_DDL_001_A_DDL_007`; `XuguTypeDdlDetailsTest#createTableIfNotExists_C_DDL_001`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` | IT | **N/A** (I-009/P-007 closed — SSOT promotion via C-DDL-001) |
| A-DDL-008 | known-limit-documented | `XuguTableDdlExtensionsTest#partitionSqlMatchesPartitionDoc_A_DDL_008`; `XuguTableDdlExtensionsTest#dialectDoesNotClaimPartitionInSchemaExport_A_DDL_008`; `XuguTableDdlExtensionsIT#listPartitionNativeRoundTrip_A_DDL_008` | IT | **N/A** (I-009/P-007 closed) |
| A-DDL-009 | known-limit-documented | `XuguTableDdlExtensionsTest#encryptSqlMatchesCreateDoc_A_DDL_009`; `XuguTableDdlExtensionsTest#dialectDoesNotClaimEncryptInSchemaExport_A_DDL_009`; `XuguTableDdlExtensionsIT#encryptByNativeWhenEncryptorAvailable_A_DDL_009` | IT | **N/A** (I-009/P-007 closed) |

### Definition A — P-004 Pagination (3)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-PAG-001 | covered-live | `XuguPaginationLockTest#limitOnlyUsesBindMarker_A_PAG_001_003`; `XuguPaginationIT#limitAndOffsetReturnExpectedRows`; `XuguHqlPaginationIT#hqlSetFirstResultMaxResultsUsesLimitNotFetchFirst` | IT | N/A |
| A-PAG-002 | covered-live | `XuguPaginationLockTest#limitOffsetStableForm_A_PAG_002`; `XuguPaginationIT#limitAndOffsetReturnExpectedRows`; `XuguHqlPaginationIT#hqlSetFirstResultMaxResultsUsesLimitNotFetchFirst` | IT | N/A |
| A-PAG-003 | covered-live | `XuguPaginationLockTest#limitOnlyUsesBindMarker_A_PAG_001_003`; `XuguPaginationIT#limitAndOffsetReturnExpectedRows` | IT | N/A |

### Definition A — P-004 Locks (3)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-LCK-001 | covered-live | `XuguPaginationLockTest#forUpdateBasic_A_LCK_001`; `XuguLockIT#forUpdateExecutesAndSkipLockedUnsupported`; `XuguHqlPaginationIT#hqlLockAndPageEmitsForUpdateBeforeLimitAndWaitAfter` | IT | N/A |
| A-LCK-002 | covered-live | `XuguPaginationLockTest#forUpdateOf_A_LCK_002`; `XuguLockIT#forUpdateExecutesAndSkipLockedUnsupported` | IT | N/A |
| A-LCK-003 | covered-live | `XuguPaginationLockTest#nowaitAndWaitMilliseconds_A_LCK_003`; `XuguLockIT#forUpdateExecutesAndSkipLockedUnsupported`; `XuguLockIT#limitForUpdateComboExecutes`; `XuguHqlPaginationIT#hqlLockAndPageEmitsForUpdateBeforeLimitAndWaitAfter` | IT | N/A |

### Definition A — P-005 Identity & Sequence (9)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-IDN-001 | known-limit-documented | `XuguIdentitySequenceTest#identitySupportWired_A_IDN_001` | unit | N/A |
| A-IDN-002 | known-limit-documented | `XuguIdentitySequenceTest#identitySupportWired_A_IDN_001` (no AUTO_INCREMENT) | unit | N/A |
| A-IDN-003 | covered-live | `XuguIdentitySequenceTest#identitySelectFallback_A_IDN_003`; `XuguIdentitySequenceIT#identityPersistBackfillsId_A_IDN_003_004` | IT | N/A |
| A-IDN-004 | covered-live | `XuguIdentitySequenceIT#identityPersistBackfillsId_A_IDN_003_004`; `DemoPersonCrudIT#persistAndFindPerson` | IT | N/A |
| A-SEQ-001 | covered-live | `XuguIdentitySequenceTest#createDropSequenceStrings_A_SEQ_001_002_005`; `XuguIdentitySequenceTest#sequenceMetadataQueryAndExtractorWired`; `XuguSchemaValidateIT#schemaValidateSucceedsWhenSequenceExists` | IT | N/A |
| A-SEQ-002 | known-limit-documented | `XuguIdentitySequenceTest#createDropSequenceStrings_A_SEQ_001_002_005` | unit | N/A |
| A-SEQ-003 | covered-live | `XuguIdentitySequenceTest#sequenceSupportWired_A_SEQ_001_003_008`; `XuguIdentitySequenceIT#sequenceGeneratorPersist_A_SEQ_003_004_008` | IT | N/A |
| A-SEQ-004 | covered-live | `XuguIdentitySequenceTest#currvalFunctionForm_A_SEQ_004`; `XuguIdentitySequenceIT#sequenceGeneratorPersist_A_SEQ_003_004_008` | IT | N/A |
| A-SEQ-005 | known-limit-documented | `XuguIdentitySequenceTest#createDropSequenceStrings_A_SEQ_001_002_005` | unit | N/A |

### Definition A — P-006 Functions (19)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-FUN-001 | covered-live | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |
| A-FUN-002 | covered-live | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |
| A-FUN-003 | known-limit-documented | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered` | unit | N/A |
| A-FUN-004 | covered-live | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |
| A-FUN-005 | known-limit-documented | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered` | unit | N/A |
| A-FUN-006 | known-limit-documented | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered` | unit | N/A |
| A-FUN-007 | known-limit-documented | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered` | unit | N/A |
| A-FUN-008 | covered-live | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` (abs) | IT | N/A |
| A-FUN-009 | known-limit-documented | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered` | unit | N/A |
| A-FUN-010 | covered-live | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |
| A-FUN-011 | covered-live | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |
| A-FUN-012 | covered-live | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` (to_char path) | IT | N/A |
| A-FUN-013 | covered-live | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |
| A-FUN-014 | covered-live | `XuguFunctionRegistryTest#coreAnsiFunctionsRegistered`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |
| A-FUN-015 | known-limit-documented | `XuguFunctionRegistryTest#bitAggregateRegistered_A_FUN_015`; `XuguRegexpAndBitFunctionsIT#bitAggregatesNativeSubset_A_FUN_015` | IT | **N/A** (I-009/P-006 closed) |
| A-FUN-016 | covered-live | `XuguFunctionRegistryTest#uuidPrimaryIsUuid`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |
| A-FUN-017 | covered-live | `XuguFunctionRegistryTest#jsonSubsetUsesStandardJsonValueNotMysqlDump`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |
| A-FUN-018 | covered-live | `XuguFunctionRegistryTest#listaggUsesNativeListaggFunction`; `XuguFunctionRegistryIT#functionFamilies_HqlAndNative_A_FUN` | IT | N/A |
| A-FUN-019 | covered-live | `XuguFunctionRegistryTest#regexpSubsetRegistered_A_FUN_019`; `XuguRegexpAndBitFunctionsIT#regexpFunctionsNativeSubset_A_FUN_019` | IT | **N/A** (I-009/P-006 closed) |

### Definition A — P-007 Schema / temp / comment / constraints (14)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-SCH-001 | covered-live | `XuguSchemaTempCommentTest#schemaCreateDropCommands_A_SCH_001`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | IT | N/A |
| A-SCH-002 | covered-live | `XuguSchemaTempCommentTest#nameQualifierIsSchemaOnly_A_SCH_002_not_003`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | IT | N/A |
| A-SCH-004 | covered-live | `XuguSchemaTempCommentTest#localTempStrategy_A_SCH_004_006`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | IT | N/A |
| A-SCH-005 | covered-live | `XuguSchemaTempCommentTest#globalTempStrategy_A_SCH_005_006_preconditionDocumented`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` (gated skip if OFF) | IT | N/A |
| A-SCH-006 | known-limit-documented | `XuguSchemaTempCommentTest#localTempStrategy_A_SCH_004_006`; `XuguSchemaTempCommentTest#globalTempStrategy_A_SCH_005_006_preconditionDocumented` | unit | N/A |
| A-SCH-008 | covered-live | `XuguSchemaTempCommentTest#commentOnAndInline_A_SCH_008_009_010`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | IT | N/A |
| A-SCH-009 | covered-live | same as A-SCH-008 | IT | N/A |
| A-SCH-010 | covered-live | same as A-SCH-008 | IT | N/A |
| A-SCH-011 | covered-live | `XuguSchemaTempCommentTest#uniqueFkCheckAlterTruncateIndex_A_SCH_011_to_016`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | IT | N/A |
| A-SCH-012 | covered-live | same as A-SCH-011 | IT | N/A |
| A-SCH-013 | covered-live | same as A-SCH-011 | IT | N/A |
| A-SCH-014 | covered-live | `XuguSchemaTempCommentTest#uniqueFkCheckAlterTruncateIndex_A_SCH_011_to_016`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` (DROP CONSTRAINT live) | IT | N/A |
| A-SCH-015 | covered-live | `XuguSchemaTempCommentTest#uniqueFkCheckAlterTruncateIndex_A_SCH_011_to_016`; `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` | IT | N/A |
| A-SCH-016 | covered-live | same as A-SCH-015 | IT | N/A |

### Definition A — P-008 SPI & cross-cutting (12: SPI 4 + cross-cutting 8)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-SPI-001 | covered-live | `XuguDialectResolverIT#explicitDialect_sessionFactorySimpleQuery` | IT | N/A |
| A-SPI-002 | covered-live | `XuguDialectServicesResourceTest#servicesFileOnClasspathListsXuguDialectResolver`; `XuguDialectResolverIT#spiAutoResolve_sessionFactoryWithoutExplicitDialect` | IT | N/A |
| A-SPI-003 | covered-live | `XuguDialectResolverTest#resolvesXuguProductName_withDatabaseVersion`; `XuguDialectResolverIT#spiAutoResolve_sessionFactoryWithoutExplicitDialect` | IT | N/A |
| A-SPI-004 | known-limit-documented | `XuguDialectResolverTest#returnsNullForMySQL`; `#returnsNullForOracle`; `#returnsNullForPostgreSQL` | unit | N/A |
| A-XCUT-001 | covered-live | `XuguDialectTest#unquotedIdentifiersFoldToUppercase_A_XCUT_001`; `XuguIdentifierFoldingIT#unquotedIdentifiersFoldToUppercase_A_XCUT_001` | IT | N/A |
| A-XCUT-002 | known-limit-documented | `XuguDialectTest#quoteCharsAreDoubleQuote` | unit | N/A |
| A-XCUT-003 | covered-live | `DemoOfflineSmokeTest#applicationYmlDocumentsExplicitDialectAndEnvKeys`; `XuguDialectResolverIT` (jdbcUrl compatiblemode=NONE via `XuguTestConnection`) | IT | N/A |
| A-XCUT-004 | covered-live | `XuguDialectTest#keywordsIncludeTcl`; `XuguTypeRoundTripIT#jdbcTransactionCommitRollbackSmoke` | IT | N/A |
| A-XCUT-005 | known-limit-documented | `XuguDialectTest#isolationLevelHooksMatchXuguIsoLevel_A_XCUT_005` | unit | N/A |
| A-XCUT-007 | known-limit-documented | `XuguDialectTest#keywordsIncludeTcl` | unit | N/A |
| A-XCUT-008 | known-limit-documented | `XuguIdentitySequenceTest#sequenceSupportWired_A_SEQ_001_003_008` | unit | N/A |
| A-XCUT-009 | covered-live | `DemoOfflineSmokeTest#applicationYmlDocumentsExplicitDialectAndEnvKeys`; `DemoPersonCrudIT#persistAndFindPerson`; `DemoBootBaselineSmokeTest#sessionFactoryUsesExplicitXuguDialectFromApplicationYml`; `#jpaPersistAndJpqlQueryRoundTrip`; `#pageableFindAllUsesLimitOffset` | demo | N/A |

### Ruler C — 可实现 (16)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| C-EXC-001 | covered-live | `XuguExceptionMappingIT#sessionUniqueViolationMapsToConstraintViolationException`; `XuguExceptionConversionTest#conversionMapsUniqueViolation` | IT | N/A |
| C-EXC-002 | covered-live | `XuguExceptionConversionTest#extractorParsesNotNullFieldName`; `#extractorReturnsNullWhenNameAbsent`; `XuguExceptionMappingIT#sessionNotNullViolationExtractsFieldNameWhenPresent` | IT | N/A |
| C-JSON-001 | covered-live | `XuguJsonAggregateIT#jsonColumnRoundTripAndHqlAggregates`; `XuguJsonAggregateSupportTest#jsonAggFunctionsConstruct` | IT | N/A |
| C-JSON-002 | covered-live | `XuguJsonAggregateIT#jsonColumnRoundTripAndHqlAggregates` | IT | N/A |
| C-JSON-003 | covered-live | `XuguJsonAggregateIT#jsonColumnRoundTripAndHqlAggregates`; `XuguJsonAggregateSupportTest#dialectWiresAggregateSupportAndCastingJsonType` | IT | N/A |
| C-JSON-004 | covered-live | `XuguJsonAggregateIT#jsonColumnRoundTripAndHqlAggregates` | IT | N/A |
| C-WIN-001 | covered-live | `XuguWindowCteIT#hqlWindowAndWithClauseOnLiveSession`; `XuguWindowCteSupportTest#dialectEnablesWindowAndWithClause` | IT | N/A |
| C-CTE-001 | covered-live | `XuguWindowCteIT#hqlWindowAndWithClauseOnLiveSession` | IT | N/A |
| C-BULK-001 | covered-live | `XuguBulkMutationIT#bulkUpdateOnJoinedInheritanceSucceeds`; `#bulkDeleteOnJoinedInheritanceSucceeds`; `XuguBulkMutationSupportTest#localTemporaryTableStrategyForBulkMutation_C_BULK_001` | IT | N/A |
| C-BULK-002 | covered-live | `XuguBulkMutationIT#bulkInsertOnJoinedInheritanceWithIdentitySucceeds_C_BULK_002`; `XuguBulkMutationSupportTest#fallbackSqmInsertStrategyWired_C_BULK_002` | IT | N/A |
| C-BULK-003 | covered-live | `XuguBulkMutationSupportTest#supportsSubqueryOnMutatingTableIsFalse_C_BULK_003`; `XuguBulkMutationIT#dialectExposesLocalTempBulkStrategyFlags` | IT | N/A |
| C-DDL-001 | covered-live | `XuguTypeDdlDetailsTest#createTableIfNotExists_C_DDL_001`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` | IT | N/A |
| C-DDL-002 | covered-live | `XuguTypeDdlDetailsTest#alterColumnType_C_DDL_002`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` | IT | N/A |
| C-DDL-003 | covered-live | `XuguTypeDdlDetailsTest#datetimeLiteralAndFormat_C_DDL_003`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` | IT | N/A |
| C-CAT-001 | covered-live | `XuguTypeDdlDetailsTest#catalogCreateDrop_C_CAT_001`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` | IT | N/A |
| C-GUID-001 | covered-live | `XuguTypeDdlDetailsTest#selectGuidString_C_GUID_001`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` | IT | N/A |
| C-JSON-005 | covered-live | `XuguFunctionRegistryTest#jsonSubsetUsesStandardJsonValueNotMysqlDump`; `XuguJsonSubsetDeepenIT#jsonSubsetDeepen_Hql_C_JSON_005` | IT | N/A |
| C-SRV-001 | covered-live | `XuguRulerCClosureTest#serverConfigurationShowQueriesLocked_C_SRV_001`; `XuguRulerCClosureTest#serverConfigurationUrlFallback_C_SRV_001`; `XuguServerConfigurationIT#sessionParametersReadOnlyProbe_C_SRV_001` | IT | **N/A** (I-009/P-010 closed) |
| C-SEL-001 | known-limit-documented | `XuguDialectSelectorTest#defaultSelectorReturnsSpiDialect_C_SEL_001`; `XuguDialectSelectorTest#resolverUsesDefaultSelector_C_SEL_001`; `XuguDialectResolverTest` (A-SPI-*); `XuguDialectServicesResourceTest` | unit | **N/A** (I-009/P-010 closed) |
| C-DDL-005 | covered-live | `XuguArrayTypeTest#arrayTypeHooksWired_A_TYP_015_C_DDL_005`; `XuguArrayTypeIT#arrayColumnRoundTrip_A_TYP_015_C_DDL_005` | IT | N/A |

### I-009 / P-003 — promoted from 延后 (Definition A)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-TYP-016 | known-limit-documented | `XuguXmlTypeTest#xmlTypeHooksWired_A_TYP_016`; `XuguXmlTypeTest#documentedXmlConstantsLocked_A_TYP_016`; `XuguXmlTypeAndFunctionsIT#xmlTypeNativeRoundTrip_A_TYP_016`; `XuguDialectTest#columnTypesMatchXuguDocs` (SQLXML) | IT | N/A |
| A-FUN-021 | known-limit-documented | `XuguFunctionRegistryTest#xmlSubsetRegistered_A_FUN_021`; `XuguXmlTypeAndFunctionsIT#xmlFunctionsNativeSubset_A_FUN_021` | IT (live SKIPPED_INFRA) | N/A |

### I-009 / P-002 — promoted from 延后 (Definition A)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-TYP-014 | known-limit-documented | `XuguIntervalTypeTest#intervalTypeHooksWired_A_TYP_014`; `XuguIntervalTypeTest#allDocumentedSubtypesLocked_A_TYP_014`; `XuguIntervalTypeTest#intervalJdbcTypesContributed_A_TYP_014`; `XuguIntervalTypeTest#intervalJdbcTypeFormatParseRoundTrip_A_TYP_014`; `XuguIntervalTypeIT#intervalNativeRoundTrip_A_TYP_014`; `XuguIntervalTypeIT#intervalEntityOrmRoundTrip_A_TYP_014` | IT | N/A (I-010/P-002 entity ORM path; live pending) |

### I-010 / P-002 — A-TYP-014 entity ORM path

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-TYP-014 | known-limit-documented | `XuguIntervalJdbcType` + `I010P002IntervalEntity`; `XuguIntervalTypeIT#intervalEntityOrmRoundTrip_A_TYP_014` (DURATION / INTERVAL_SECOND); native IT retained | IT | **Promote to covered-live only after live entity IT PASS** — this run SKIPPED_INFRA |

### I-009 / P-004 — promoted from 延后 (Definition A)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-TYP-017 | known-limit-documented | `XuguGeometricTypeTest#pointTypeHooksWired_A_TYP_017`; `XuguGeometricTypeTest#allDocumentedKindsLocked_A_TYP_017`; `XuguGeometricTypeAndFunctionsIT#geometricTypesNativeRoundTrip_A_TYP_017`; `XuguDialectTest#columnTypesMatchXuguDocs` (POINT/GEOMETRY) | IT | N/A |
| A-FUN-020 | covered-live | `XuguFunctionRegistryTest#geometricSubsetRegistered_A_FUN_020`; `XuguGeometricTypeAndFunctionsIT#geometricFunctionsNativeSubset_A_FUN_020` | IT | N/A |

### I-009 / P-006 — promoted from 延后 (Definition A)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-FUN-015 | known-limit-documented | `XuguFunctionRegistryTest#bitAggregateRegistered_A_FUN_015`; `XuguRegexpAndBitFunctionsIT#bitAggregatesNativeSubset_A_FUN_015` | IT | N/A |
| A-FUN-019 | covered-live | `XuguFunctionRegistryTest#regexpSubsetRegistered_A_FUN_019`; `XuguRegexpAndBitFunctionsIT#regexpFunctionsNativeSubset_A_FUN_019` | IT | N/A |

### I-009 / P-007 — promoted from 延后 (Definition A)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-DDL-007 | covered-live | `XuguTableDdlExtensionsTest#ifNotExistsPromotedViaC_DDL_001_A_DDL_007`; `XuguTypeDdlDetailsTest#createTableIfNotExists_C_DDL_001`; `XuguTypeDdlDetailsIT#typeDdlDetailsOnLiveDb` | IT | N/A |
| A-DDL-008 | known-limit-documented | `XuguTableDdlExtensionsTest#partitionSqlMatchesPartitionDoc_A_DDL_008`; `XuguTableDdlExtensionsTest#dialectDoesNotClaimPartitionInSchemaExport_A_DDL_008`; `XuguTableDdlExtensionsIT#listPartitionNativeRoundTrip_A_DDL_008` | IT | N/A |
| A-DDL-009 | known-limit-documented | `XuguTableDdlExtensionsTest#encryptSqlMatchesCreateDoc_A_DDL_009`; `XuguTableDdlExtensionsTest#dialectDoesNotClaimEncryptInSchemaExport_A_DDL_009`; `XuguTableDdlExtensionsIT#encryptByNativeWhenEncryptorAvailable_A_DDL_009` | IT | N/A |

### I-009 / P-005 — promoted from 延后 (Definition A)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-TYP-018 | known-limit-documented | `XuguUdtTypeTest#documentedKindsLocked_A_TYP_018`; `XuguUdtTypeTest#createTypeSqlMatchesUdtDoc_A_TYP_018`; `XuguUdtTypeTest#dialectDoesNotClaimOrmUdtEntityMapping_A_TYP_018`; `XuguUdtTypeIT#udtNativeRoundTrip_A_TYP_018` | IT | N/A |

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

### Definition A — 延后 (11)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| A-TYP-014 | known-limit-documented | `XuguIntervalTypeTest#intervalTypeHooksWired_A_TYP_014`; `XuguIntervalTypeTest#intervalJdbcTypesContributed_A_TYP_014`; `XuguIntervalTypeIT#intervalNativeRoundTrip_A_TYP_014`; `XuguIntervalTypeIT#intervalEntityOrmRoundTrip_A_TYP_014`; `XuguDialectTest#columnTypesMatchXuguDocs` (DURATION/INTERVAL_SECOND) | IT | **I-010/P-002** entity ORM implemented; covered-live after live PASS |
| A-TYP-016 | known-limit-documented | `XuguXmlTypeTest#xmlTypeHooksWired_A_TYP_016`; `XuguXmlTypeTest#documentedXmlConstantsLocked_A_TYP_016`; `XuguXmlTypeAndFunctionsIT#xmlTypeNativeRoundTrip_A_TYP_016`; `XuguDialectTest#columnTypesMatchXuguDocs` (SQLXML) | IT | **N/A** (I-009/P-003 closed) |
| A-TYP-017 | known-limit-documented | `XuguGeometricTypeTest#pointTypeHooksWired_A_TYP_017`; `XuguGeometricTypeTest#allDocumentedKindsLocked_A_TYP_017`; `XuguGeometricTypeAndFunctionsIT#geometricTypesNativeRoundTrip_A_TYP_017`; `XuguDialectTest#columnTypesMatchXuguDocs` (POINT/GEOMETRY) | IT | **N/A** (I-009/P-004 closed) |
| A-TYP-018 | known-limit-documented | `XuguUdtTypeTest#documentedKindsLocked_A_TYP_018`; `XuguUdtTypeTest#createTypeSqlMatchesUdtDoc_A_TYP_018`; `XuguUdtTypeTest#dialectDoesNotClaimOrmUdtEntityMapping_A_TYP_018`; `XuguUdtTypeIT#udtNativeRoundTrip_A_TYP_018` | IT | **N/A** (I-009/P-005 closed) |
| A-PAG-004 | known-limit-documented | `XuguLockPaginationIdentityExtensionsTest#topSqlMatchesResultsetRestrictedDoc_A_PAG_004`; `XuguLockPaginationIdentityExtensionsTest#limitHandlerRemainsDefault_A_PAG_004_006`; `XuguLockPaginationIdentityIT#topSyntaxNativeRoundTrip_A_PAG_004` | IT | **N/A** (I-009/P-009 closed) |
| A-PAG-006 | known-limit-documented | `XuguLockPaginationIdentityExtensionsTest#rownumSqlMatchesSelectDoc_A_PAG_006`; `XuguLockPaginationIdentityExtensionsTest#limitHandlerRemainsDefault_A_PAG_004_006`; `XuguLockPaginationIdentityIT#rownumPaginationNativeRoundTrip_A_PAG_006` | IT | **N/A** (I-009/P-009 closed) |
| A-LCK-006 | covered-live | `XuguLockPaginationIdentityExtensionsTest#lockTableSqlMatchesLockDoc_A_LCK_006`; `XuguLockPaginationIdentityIT#lockTableExclusiveNativeRoundTrip_A_LCK_006` | IT | **N/A** (I-009/P-009 closed) |
| A-IDN-005 | covered-live | `XuguLockPaginationIdentityExtensionsTest#identityModeSqlMatchesIdentityModeDoc_A_IDN_005`; `XuguLockPaginationIdentityIT#identityModeNullAsAutoIncrement_A_IDN_005` | IT | **N/A** (I-009/P-009 closed) |
| A-FUN-020 | covered-live | `XuguFunctionRegistryTest#geometricSubsetRegistered_A_FUN_020`; `XuguGeometricTypeAndFunctionsIT#geometricFunctionsNativeSubset_A_FUN_020` | IT | **N/A** (I-009/P-004 closed) |
| A-FUN-021 | known-limit-documented | `XuguFunctionRegistryTest#xmlSubsetRegistered_A_FUN_021`; `XuguXmlTypeAndFunctionsIT#xmlFunctionsNativeSubset_A_FUN_021` | IT (live SKIPPED_INFRA) | **N/A** (I-009/P-003 closed) |
| A-SCH-003 | known-limit-documented | `XuguCatalogAndIndexExtensionsTest#catalogMetadataQueryLocked_A_SCH_003`; `XuguCatalogAndIndexExtensionsTest#nameQualifierRemainsSchemaOnly_A_SCH_003`; `XuguCatalogAndIndexExtensionsTest#dialectDoesNotClaimCatalogInObjectNames_A_SCH_003`; `XuguCatalogAndIndexExtensionsIT#jdbcCatalogAlignsWithCurrentDb_A_SCH_003` | IT | **N/A** (I-009/P-008 closed) |
| A-SCH-017 | known-limit-documented | `XuguCatalogAndIndexExtensionsTest#functionalIndexSqlMatchesIndexesDoc_A_SCH_017`; `XuguCatalogAndIndexExtensionsTest#bitmapIndexSqlMatchesIndexesDoc_A_SCH_017`; `XuguCatalogAndIndexExtensionsTest#dialectDoesNotClaimAdvancedIndexInSchemaExport_A_SCH_017`; `XuguCatalogAndIndexExtensionsIT#functionalAndBitmapIndexNativeRoundTrip_A_SCH_017` | IT | **N/A** (I-009/P-008 closed) |
| A-XCUT-012 | negative-only | `XuguNegativeRegressionBaselineTest#deferred_A_XCUT_012_mavenCentralPublish` (@Disabled) | none | N/A |

### Ruler C — 文档不允许 / 延后 (7)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| C-DDL-004 | negative-only | `XuguNegativeRegressionBaselineTest#enumDdlNotEmitted_C_DDL_004`; `XuguTypeDdlDetailsTest#enumTypeDeclarationIsNull_C_DDL_004` | unit | P-003 |
| C-SKIP-001 | negative-only | `XuguNegativeRegressionBaselineTest#skipLockedNotSupported_A_LCK_004_C_SKIP_001`; `XuguPaginationLockTest#skipLockedNotSupported_A_LCK_004`; `XuguLockIT#forUpdateExecutesAndSkipLockedUnsupported` | IT | P-003 |
| C-JSON-006 | negative-only (**doc-forbidden**) | `XuguRulerCClosureTest#jsonTableNotSupported_C_JSON_006`; `XuguFunctionRegistryTest#jsonSubsetUsesStandardJsonValueNotMysqlDump` (json_table null); `XuguNegativeRegressionBaselineTest#deferred_C_JSON_006_jsonTable` (@Disabled anchor) | unit | **N/A** (I-009/P-010 closed) |

### Ruler C — 已有 (audit anchor, outside 94 可实现 count)

| matrix_id | status | entry_class#method | gate | gap_action |
|---|---|---|---|---|
| C-LOCK-001 | covered | `XuguPaginationLockTest#nowaitAndWaitMilliseconds_A_LCK_003` (`supportsNoWait/Wait`); `XuguLockIT#forUpdateExecutesAndSkipLockedUnsupported` | IT | N/A |

---


## Explicit call-out — P-003 Batch A known-limit waivers (15)

Formal live IT waived per [harness/evidence/architect-contract/I-008/P-001/PROMOTION-MAP.md](../harness/evidence/architect-contract/I-008/P-001/PROMOTION-MAP.md) Batch A locks. User-doc sync optional via P-002.

| matrix_id | waiver_reason | live_bundle |
|---|---|---|
| A-IDN-001 | Wiring unit only | A-IDN-003/004 IT |
| A-IDN-002 | No AUTO_INCREMENT wiring unit | A-IDN-003/004 IT |
| A-SEQ-002 | DROP SEQUENCE string unit | A-SEQ-001 IT |
| A-SEQ-005 | Sequence options unit | A-SEQ-001/003 IT |
| A-FUN-003 | Registry unit slice | A-FUN-001/004/010 IT |
| A-FUN-005 | Registry unit slice | A-FUN-001/004/010 IT |
| A-FUN-006 | Registry unit slice | A-FUN-001/004/010 IT |
| A-FUN-007 | Registry unit slice | A-FUN-001/004/010 IT |
| A-FUN-009 | Registry unit slice | A-FUN-001/004/010 IT |
| A-SCH-006 | Temp strategy flag unit | A-SCH-004/005 IT |
| A-SPI-004 | Resolver non-match negatives — no live path by design | — |
| A-XCUT-002 | Quote-char constant only | — |
| A-XCUT-005 | Isolation hook not independently live-testable | — |
| A-XCUT-007 | TCL keyword unit duplicate | A-XCUT-004 IT |
| A-XCUT-008 | SequenceSupport flag unit | A-SEQ-003 IT |

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

## Explicit call-out — A-XCUT-009 demo-live (consumer golden path)

| Field | Value |
|---|---|
| **matrix_id** | A-XCUT-009 |
| **status** | **covered-live** |
| **Evidence class** | demo-live — gated `@SpringBootTest` / `@DataJpaTest` IT in `demo-spring-boot` |
| **Live gate** | `XuguIntegrationGate.isEnabled()` ← `XUGU_RUN_IT=true` or `-Dxugu.run.integration=true` |
| **Golden path covered** | Explicit dialect + env secrets (offline smoke); SessionFactory/JPA CRUD + IDENTITY; JPQL round-trip; Spring Data Pageable LIMIT-OFFSET |
| **Gated Boot IT anchors** | `DemoPersonCrudIT#persistAndFindPerson`; `DemoBootBaselineSmokeTest#sessionFactoryUsesExplicitXuguDialectFromApplicationYml`; `#jpaPersistAndJpqlQueryRoundTrip`; `#pageableFindAllUsesLimitOffset` |
| **Consumer-path xref** | Layer A golden path in [`consumer-path-baseline.md`](consumer-path-baseline.md) — Boot-required **41/41 FROZEN**; offline VERIFY does **not** substitute for integration readiness |
| **Q3/Q4 cross-link** | Boot UUID/JSON out-of-box wiring remains **P-006/P-007**; demo-live proves consumer path, not dialect-module defaults |
| **gap_action** | **N/A** — closed **covered-live** in I-008/P-004 (tag sweep; live IT pre-existed) |

---

## Explicit call-out — Demo smoke baseline

| Capability | entry_class#method | gate | status | gap_action |
|---|---|---|---|---|
| Env secrets / explicit dialect | `DemoOfflineSmokeTest#applicationYmlDocumentsExplicitDialectAndEnvKeys` | demo | covered | N/A |
| Table prefix convention | `DemoOfflineSmokeTest#demoPersonTableUsesHibDemoPrefix` | demo | covered | N/A |
| Spring Boot + JPA CRUD + IDENTITY | `DemoPersonCrudIT#persistAndFindPerson` | demo | covered-live | N/A |
| SessionFactory + explicit dialect (consumer path) | `DemoBootBaselineSmokeTest#sessionFactoryUsesExplicitXuguDialectFromApplicationYml`; `#datasourceUrlIncludesCompatibleModeNone` | demo | covered-live | N/A |
| JPA JPQL smoke | `DemoBootBaselineSmokeTest#jpaPersistAndJpqlQueryRoundTrip` | demo | covered-live | N/A |
| Spring Data Pageable / LIMIT-OFFSET | `DemoBootBaselineSmokeTest#pageableFindAllUsesLimitOffset` | demo | covered-live | N/A |
| hbm2ddl validate | `DemoValidateStartupIT#validateStartupSucceedsWithPreCreatedSchema` | demo | covered-live | **N/A** (I-006/I-007 — not a gap) |
| Function / HQL smoke | `DemoFunctionsIT#hqlFunctionSubsetSmoke` | demo | covered-live | **N/A** (I-006/I-007 B-DEMO-002 — not a gap) |
| Bulk mutation | `DemoBulkMutationIT#bulkUpdatePersonNames`; `#bulkDeletePersonNames` | demo | covered-live | **N/A** (I-006/I-007 B-DEMO-001 — not a gap) |

**Offline (default `mvn test`):** `DemoOfflineSmokeTest` — no Spring context, no live DB.  
**Gated live (`XUGU_RUN_IT=true` or `-Dxugu.run.integration=true`):** `DemoPersonCrudIT`, `DemoBootBaselineSmokeTest`, `DemoValidateStartupIT`, `DemoFunctionsIT`, `DemoBulkMutationIT` — satisfy **A-XCUT-009** demo-live, I-006 Layer A/C′ consumer path, and I-007 Track B demo deepen (see [`consumer-path-baseline.md`](consumer-path-baseline.md)).

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
| HQL pagination, validate, function/HQL, bulk mutation demo paths | **Closed** — Pageable + SessionFactory/JPA in `DemoBootBaselineSmokeTest` (I-005/P-005); validate / Function-HQL / bulk closed **covered-live** via `DemoValidateStartupIT` / `DemoFunctionsIT` / `DemoBulkMutationIT` (I-006 + I-007 Track B) — **no open demo gaps** |

### Hard gap IDs (6 total, 可实现 rows — all closed in I-005 first batch)

`A-TYP-019`, `A-DDL-005`, `A-XCUT-001`, `A-XCUT-005`, `A-SCH-014`, `C-EXC-002`

*(C-BULK-002 promoted to **covered-live** in I-007/P-002; A-TYP-007 stretch live TIME IT in P-002.)*

---

## Summary counts

> **I-009/P-011 final rollup** (2026-07-21): P-002…P-010 closed all **20** deferred inventory rows — each **covered-live** or **known-limit-documented**; **C-JSON-006** remains **doc-forbidden negative-only** (no invented `json_table`). Charter **98** 可实现 honest rollup unchanged from I-008: **83/98 covered-live** + **15 known-limit-documented**; **gap = 0**. Deferred delivery is **outside** the frozen 98-count (matrix **延后** rows promoted to honest SSOT status without inflating charter covered-live).

| Bucket | Count |
|---|---:|
| **可实现 rows (SSOT physical)** | **98** |
| Charter label (I-005 freeze) | **94** (+4 I-007 Track C promotions) |
| **Honest covered-live today** (live path exists) | **83** (82 IT + 1 demo) |
| SSOT `status=covered-live` column today | **83** |
| unit-only-without-live | **0** (P-003 Batch A closed) |
| known-limit-documented (P-003 Batch A closed) | **15** |
| thin live IT required (P-003 Batch A) | **0** (closed) |
| SSOT tag sweep only (P-004 Batch B) | **0** (closed) |
| gap (可实现) | **0** |
| **I-009 deferred inventory (P-001)** | **20/20 closed** (**6** covered-live · **13** known-limit · **1** doc-forbidden negative) — row-level: A-FUN-021 = **known-limit-documented** (XMLTABLE cluster); do not cite stale 7/12 |
| Open matrix **延后** (product delivery) | **0** (`A-XCUT-012` Ship defer anchor only) |
| negative-only (文档不允许 + explicit defer anchors) | **11** (7 Def A + 3 Ruler C incl. C-JSON-006 + A-XCUT-012) |
| I-007 closed covered-live (no further promotion) | 5 |
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
| I-004 reserved table identity | `XuguReservedIdentityIT#identityPersistOnReservedTableOrderBackfillsId` (`"select"`; avoids live `Order` collision) | A-IDN-003/004 edge |
| I-004 sequence drop guard | `XuguAutoSequenceDropIT` | A-SEQ-002 drop safety |
| SqlAst translator wiring | `XuguSqlAstTranslatorTest` | A-PAG-001/002 AST path |

---

## I-009 deferred delivery routing (P-001 architect-contract RP-02)

> **Initiative:** I-009 — deferred matrix full delivery  
> **Batch map SSOT:** [`i009-deferred-batch-map.md`](i009-deferred-batch-map.md)  
> **Evidence:** `harness/evidence/architect-contract/I-009/P-001/{ACCEPTANCE,BATCH-MAP}.md`  
> **GAV:** `7.4.5.Final` · **compatiblemode:** `NONE` · **NOT Ship**

### Batch summary

| Phase | Row IDs | Theme |
|---|---|---|
| P-002 | A-TYP-014 | INTERVAL |
| P-003 | A-TYP-016, A-FUN-021 | XML |
| P-004 | A-TYP-017, A-FUN-020 | Geometric |
| P-005 | A-TYP-018 | UDT |
| P-006 | A-FUN-015, A-FUN-019 | bit + regexp |
| P-007 | A-DDL-007, A-DDL-008, A-DDL-009 | DDL extensions |
| P-008 | A-SCH-003, A-SCH-017 | Catalog + indexes |
| P-009 | A-LCK-006, A-PAG-004, A-PAG-006, A-IDN-005 | Lock/pagination/identity |
| P-010 | C-JSON-006, C-SRV-001, C-SEL-001 | Ruler C closure |

### C-JSON-006 doc-forbidden callout

| Field | Value |
|---|---|
| **matrix_id** | C-JSON-006 |
| **Audit** | Zero `json_table` under `E:\Work\docs\content/reference/function/json-functions/**` (2026-07-21) |
| **Matrix SSOT** | Reclassified **文档不允许** in `feature-matrix-i003-ruler-c.md` |
| **P-010 outcome** | Negative-only — **MUST NOT** invent JSON_TABLE SQL; `supportsJsonTableFunction=false`; active unit tests + @Disabled SSOT anchor |

### C-SRV-001 Server configuration (I-009/P-010 closed)

| Field | Value |
|---|---|
| **matrix_id** | C-SRV-001 |
| **status** | **covered-live** |
| **Doc citation** | `reference/system-configuration-parameter/session-parameter/*.md` (30 params) |
| **Dialect surface** | `XuguServerConfiguration` read-only `SHOW CHAR_SET` / `SHOW COMPATIBLE_MODE` / `SHOW OPTIMIZER_MODE`; `XuguDialect#getServerConfiguration()` |
| **Live IT** | `XuguServerConfigurationIT#sessionParametersReadOnlyProbe_C_SRV_001` |
| **gap_action** | **N/A** — closed I-009/P-010 |

### C-SEL-001 DialectSelector closure (I-009/P-010 closed)

| Field | Value |
|---|---|
| **matrix_id** | C-SEL-001 |
| **status** | **known-limit-documented** |
| **SPI note** | Hibernate 7.4 autodetect via `DialectResolver` only; `XuguDialectSelector` is internal extension (not duplicate Hibernate SPI) |
| **Unit tests** | `XuguDialectSelectorTest`; existing `XuguDialectResolver` + `META-INF/services` |
| **gap_action** | **N/A** — closed I-009/P-010 |
| **Contrast** | XMLTABLE → A-FUN-021 (`xml-functions/xmltable.md`) |

### A-DDL-007 IF NOT EXISTS (I-009/P-007 closed)

| Field | Value |
|---|---|
| **matrix_id** | A-DDL-007 |
| **status** | **covered-live** |
| **Doc citation** | `reference/object/table/create.md` (`IF NOT EXISTS`) |
| **Dialect surface** | Promotion-only via C-DDL-001 — `getCreateTableString()` / `supportsIfExistsBeforeTableName()`; SSOT constant in `XuguTableDdlSupport` |
| **Live IT** | Existing `XuguTypeDdlDetailsIT` (C-DDL-001) + `XuguTableDdlExtensionsTest#ifNotExistsPromotedViaC_DDL_001_A_DDL_007` |
| **gap_action** | **N/A** — closed I-009/P-007 |

### A-DDL-008 PARTITION BY (I-009/P-007 closed)

| Field | Value |
|---|---|
| **matrix_id** | A-DDL-008 |
| **status** | **known-limit-documented** |
| **Doc citation** | `reference/object/table/partition.md` (LIST/RANGE/HASH); `create.md` (`PARTITION BY`) |
| **Dialect surface** | `XuguTableDdlSupport` locks LIST/RANGE/HASH CREATE shapes; `supportsPartitionByInSchemaExport()` is `false` |
| **Known-limit reason** | Hibernate schema export does not emit `PARTITION BY` — native SQL IT is the honest path |
| **Live IT** | `XuguTableDdlExtensionsIT#listPartitionNativeRoundTrip_A_DDL_008` (partition.md example 1) |
| **gap_action** | **N/A** — closed I-009/P-007 |

### A-DDL-009 ENCRYPT BY (I-009/P-007 closed)

| Field | Value |
|---|---|
| **matrix_id** | A-DDL-009 |
| **status** | **known-limit-documented** |
| **Doc citation** | `reference/object/table/create.md` (`ENCRYPT BY`); `reference/object/encryptor.md` (SYSSSO / `ACL_SSO`) |
| **Dialect surface** | `XuguTableDdlSupport` locks `ENCRYPT BY` / `CREATE ENCRYPTOR` shapes; `supportsEncryptByInSchemaExport()` is `false` |
| **Known-limit reason** | Encryptor creation requires SYSSSO / `ACL_SSO`; `sys_encryptors` may return E18012; schema tooling does not emit encrypt clauses |
| **Live IT** | `XuguTableDdlExtensionsIT#encryptByNativeWhenEncryptorAvailable_A_DDL_009` (honest **skip** when no encryptor visible — must not wrap `TestAbortedException` as failure) |
| **gap_action** | **N/A** — closed I-009/P-007; live-it-5287 triage confirmed skip path |

### A-LCK-006 LOCK TABLE (I-009/P-009 closed)

| Field | Value |
|---|---|
| **matrix_id** | A-LCK-006 |
| **status** | **covered-live** |
| **Doc citation** | `reference/object/table/lock.md` (`LOCK TABLE … IN … MODE`, NOWAIT/WAIT ms) |
| **Dialect surface** | `XuguLockTableSupport` native SQL helpers — not JPA `LockMode` / `FOR UPDATE` |
| **Live IT** | `XuguLockPaginationIdentityIT#lockTableExclusiveNativeRoundTrip_A_LCK_006` |
| **gap_action** | **N/A** — closed I-009/P-009 |

### A-PAG-004 TOP (I-009/P-009 closed)

| Field | Value |
|---|---|
| **matrix_id** | A-PAG-004 |
| **status** | **known-limit-documented** |
| **Doc citation** | `reference/sql/select/resultset-restricted.md` (#top); `select.md` (`opt_top`) |
| **Dialect surface** | `XuguPaginationAlternativesSupport#selectTopSql`; `usesTopPaginationInOrmPath()=false` — LimitHandler unchanged |
| **Known-limit reason** | TOP ⊥ LIMIT; Hibernate ORM path stays `LIMIT` |
| **Live IT** | `XuguLockPaginationIdentityIT#topSyntaxNativeRoundTrip_A_PAG_004` |
| **gap_action** | **N/A** — closed I-009/P-009 |

### A-PAG-006 ROWNUM (I-009/P-009 closed)

| Field | Value |
|---|---|
| **matrix_id** | A-PAG-006 |
| **status** | **known-limit-documented** |
| **Doc citation** | `reference/sql/select/select.md` (§8.3 ROWNUM) |
| **Dialect surface** | `XuguPaginationAlternativesSupport` ROWNUM wrappers; `usesRownumPaginationInOrmPath()=false` |
| **Known-limit reason** | LIMIT preferred for Hibernate pagination; ROWNUM is native SQL alternate |
| **Live IT** | `XuguLockPaginationIdentityIT#rownumPaginationNativeRoundTrip_A_PAG_006` |
| **gap_action** | **N/A** — closed I-009/P-009 |

### A-IDN-005 IDENTITY_MODE (I-009/P-009 closed)

| Field | Value |
|---|---|
| **matrix_id** | A-IDN-005 |
| **status** | **covered-live** |
| **Doc citation** | `reference/system-configuration-parameter/session-parameter/identity_mode.md`; `def_identity_mode.md` |
| **Dialect surface** | `XuguIdentityModeSupport` — `SET` / `ALTER SESSION SET` / `SHOW IDENTITY_MODE` |
| **Live IT** | `XuguLockPaginationIdentityIT#identityModeNullAsAutoIncrement_A_IDN_005` (NULL/ZERO modes v12.0.6+) |
| **gap_action** | **N/A** — closed I-009/P-009 |

### A-TYP-016 XML known-limit (I-009/P-003)

| Field | Value |
|---|---|
| **matrix_id** | A-TYP-016 |
| **status** | **known-limit-documented** |
| **Doc citation** | `reference/sql/datatype/xml.md` (XML/XMLTYPE synonyms, BLOB-backed, max 2GB) |
| **Dialect surface** | `SqlTypes.SQLXML` → `xml` DDL via `XuguXmlTypeSupport`; standard `XmlJdbcType` contributed |
| **Known-limit reason** | No verified ORM `@JdbcTypeCode(SQLXML)` entity round-trip; Xugu JDBC `java.sql.SQLXML` path unproven — native SQL string round-trip IT is the honest acceptance path |
| **Live IT** | Native SQL CRUD (`XuguXmlTypeAndFunctionsIT#xmlTypeNativeRoundTrip_A_TYP_016`) |
| **gap_action** | **N/A** — closed I-009/P-003 |

### A-FUN-021 XML functions (I-009/P-003)

| Field | Value |
|---|---|
| **matrix_id** | A-FUN-021 |
| **status** | **known-limit-documented** |
| **Doc citation** | `reference/function/xml-functions/{extract,xmlelement,xmlquery,xmltable}.md` |
| **Dialect surface** | HQL registry: `xmlelement`, `xmlquery`, `xmltable`; `EXTRACT(xml,xpath)` native SQL only (temporal `extract(field from …)` keeps Dialect default) |
| **Known-limit reason** | `XMLTABLE` documented **single-node only** (`xmltable.md`); on cluster topologies empty result / non-support is expected — IT skips that subset rather than claiming cluster coverage. EXTRACT / XMLELEMENT / XMLQUERY remain exercised when gate ON. |
| **Cluster note** | Not a cluster-safe claim for `XMLTABLE` |
| **Live IT** | Native SQL subset (`XuguXmlTypeAndFunctionsIT#xmlFunctionsNativeSubset_A_FUN_021`) — XMLTABLE empty → assumption skip |
| **gap_action** | **N/A** — closed I-009/P-003; live-it-5287 triage aligned status |

### A-TYP-014 INTERVAL known-limit (I-009/P-002; entity ORM I-010/P-002)

| Field | Value |
|---|---|
| **matrix_id** | A-TYP-014 |
| **status** | **known-limit-documented** (entity ORM path implemented; **not** covered-live until live IT PASS) |
| **Doc citation** | `reference/sql/datatype/datetime.md` §时间间隔类型 (13 subtypes; `DEF_INTERVAL_STYLE`) |
| **Dialect surface** | `SqlTypes.DURATION` → `interval day to second`; `SqlTypes.INTERVAL_SECOND` → `interval second`; `XuguIntervalJdbcType` string bind/extract for entity `Duration`; all 13 subtype DDL strings in `XuguIntervalTypeSupport` |
| **Known-limit reason** | Hibernate 7.4 exposes only DURATION + INTERVAL_SECOND — not 13 XuGu subtypes; output format depends on server `DEF_INTERVAL_STYLE`. Entity ORM for the two Hibernate codes is implemented via documented SQL_STANDARD string literals (`XuguIntervalJdbcType`); the other 11 subtypes remain tooling / native-SQL only. Live entity round-trip was **SKIPPED_INFRA** this run (DB unreachable) — do not claim covered-live yet. |
| **Live IT** | Native: `XuguIntervalTypeIT#intervalNativeRoundTrip_A_TYP_014`; Entity ORM: `XuguIntervalTypeIT#intervalEntityOrmRoundTrip_A_TYP_014` + `I010P002IntervalEntity` |
| **gap_action** | Re-run with `XUGU_RUN_IT=true` + live DB → if entity IT PASS, promote status to **covered-live** |

### A-TYP-018 UDT known-limit (I-009/P-005)

| Field | Value |
|---|---|
| **matrix_id** | A-TYP-018 |
| **status** | **known-limit-documented** |
| **Doc citation** | `reference/sql/datatype/udt.md` (OBJECT / VARRAY / TABLE; CREATE TYPE, constructor insert, DROP TYPE) |
| **Dialect surface** | `XuguUdtTypeSupport` locks three UDT families + documented CREATE/DROP TYPE SQL; `XuguDialect#supportsJdbcUserDefinedTypes()` is `false` |
| **Known-limit reason** | Hibernate 7.4 has no `SqlTypes` for schema UDT columns; no verified JDBC STRUCT/custom ORM entity mapping — native SQL IT is the honest path |
| **Live IT** | Native SQL round-trip (`XuguUdtTypeIT#udtNativeRoundTrip_A_TYP_018`) — OBJECT/VARRAY/TABLE per udt.md examples |
| **gap_action** | **N/A** — closed I-009/P-005 |

### gap_action routing (20 inventory rows)

All 20 deferred inventory rows now route to **I-009/P-002…P-010** via `gap_action` column above. Implementers close per batch map; honest **known-limit-documented** acceptable with doc citation.

---

## Orchestrator note

This file is the **production regression baseline SSOT** for I-005. Implementers close gaps per `gap_action` owner Phase; reviewer audits row completeness against feature matrices without inventing test methods.
