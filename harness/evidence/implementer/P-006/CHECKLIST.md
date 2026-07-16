# P-006 Implementer Checklist (RP-01 / I-003 Type/DDL details)

- [x] C-DDL-001: `supportsIfExistsBeforeTableName()` → true + `getCreateTableString` → `create table if not exists`
- [x] C-DDL-002: `supportsAlterColumnType` + `getAlterColumnTypeString` per alter.md
- [x] C-DDL-003: `appendDatetimeFormat` + `appendDateTimeLiteral` overrides (XuGu literals)
- [x] C-DDL-004: `getEnumTypeDeclaration` → null (do not emit MySQL ENUM)
- [x] C-CAT-001: `canCreateCatalog` + create/drop database commands
- [x] C-GUID-001: `getSelectGUIDString` → `select sys_guid()`
- [x] Skip C-DDL-005 / C-LOCK-001 / C-SKIP-001 as authorized
- [x] Unit test `XuguTypeDdlDetailsTest`
- [x] ORM/schema IT `XuguTypeDdlDetailsIT` (CREATE if not exists, alter column, datetime HQL, catalog, GUID)
- [x] Evidence NOTES + CHECKLIST overwritten for I-003 Type/DDL Phase
- [x] Handoff `harness/handoffs/implementer/P-006.yaml` → `completed_awaiting_test_review`
- [ ] Accept (orchestrator / later)
- [ ] Commit (Human Gate / later)
