# P-002 Acceptance Evidence (Implementer RP-01)

> Phase: `P-002`  
> Initiative: `I-005`  
> Build: `B-001`  
> Role: implementer  
> Branch: `feat/i-005-production-regression-baseline`

## Closed gap IDs

| matrix_id | entry_class#method | gate |
|---|---|---|
| A-TYP-019 | `XuguDialectTest#castPatternDefaultUsesStandardCastSyntax_A_TYP_019` | unit |
| A-DDL-005 | `XuguDefaultColumnExportTest#schemaExportEmitsDefaultColumn_A_DDL_005` | unit |
| A-XCUT-001 | `XuguDialectTest#unquotedIdentifiersFoldToUppercase_A_XCUT_001` | unit |
| A-XCUT-005 | `XuguDialectTest#isolationLevelHooksMatchXuguIsoLevel_A_XCUT_005` | unit |
| A-SCH-014 | `XuguSchemaTempCommentIT#schemaTempCommentFkTruncate_A_SCH` (DROP CONSTRAINT live step) | IT |
| C-EXC-002 | `XuguExceptionMappingIT#sessionNotNullViolationExtractsFieldNameWhenPresent` (+ existing unit extractors) | IT |
| A-TYP-007 (stretch) | `XuguTypeRoundTripIT#jdbcTimeRoundTrip_A_TYP_007` | IT |

## Not in scope (P-004 owner)

- C-BULK-002 — unchanged gap

## Files changed

- `dialect/src/test/java/com/xugu/dialect/XuguDialectTest.java`
- `dialect/src/test/java/com/xugu/dialect/XuguDefaultColumnExportTest.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguSchemaTempCommentIT.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguExceptionMappingIT.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguTypeRoundTripIT.java`
- `dialect/src/test/java/com/xugu/dialect/it/entities/P002DefaultColumnEntity.java`
- `dialect/src/test/java/com/xugu/dialect/it/entities/I005P002NotNullEntity.java`
- `contracts/production-regression-baseline.md`

## Validation (implementer)

- Build: `mvn -q -DskipTests package` → **exit 0**
- Test: `mvn -q test` → **exit 0** (105 run, 0 failures, 32 IT skipped)
