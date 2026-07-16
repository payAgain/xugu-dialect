# P-005 Implementer Checklist (RP-01 / I-003 bulk mutation)

- [x] C-BULK-003: `supportsSubqueryOnMutatingTable()` → `false`
- [x] C-BULK-001: `getFallbackSqmMutationStrategy` → `LocalTemporaryTableMutationStrategy` + `TemporaryTable.createIdTable`
- [x] C-BULK-002: `getFallbackSqmInsertStrategy` → `LocalTemporaryTableInsertStrategy` + `TemporaryTable.createEntityTable`
- [x] Reuses `XuguLocalTemporaryTableStrategy` (LOCAL temp DDL)
- [x] Unit test `XuguBulkMutationSupportTest`
- [x] ORM IT `XuguBulkMutationIT` — bulk update on JOINED entity (live DB)
- [x] ORM IT `XuguBulkMutationIT` — bulk delete on JOINED entity (live DB)
- [x] C-BULK-002 insert IT documented N/A (driver issue; strategy wired)
- [x] Evidence NOTES + CHECKLIST
- [x] Handoff `harness/handoffs/implementer/P-005.yaml` → `completed_awaiting_test_review`
- [ ] Accept (orchestrator / later)
- [ ] Commit (Human Gate / later)
