# P-007 Implementer Checklist (RP-01)

- [x] Schema create/drop + current_schema command (A-SCH-001)
- [x] NameQualifierSupport.SCHEMA only (A-SCH-002); catalog deferred (A-SCH-003)
- [x] Local temp strategy + ON COMMIT PRESERVE (A-SCH-004/006)
- [x] Global temp strategy + ON COMMIT DELETE + support_global_tab precondition (A-SCH-005/006)
- [x] No FK on temp tables (A-SCH-007 文档不允许)
- [x] COMMENT ON table/column (A-SCH-008/009) + inline alternate helpers (A-SCH-010)
- [x] UNIQUE / FK / CHECK / ALTER drop constraint (A-SCH-011..014)
- [x] TRUNCATE TABLE (A-SCH-015)
- [x] CREATE [UNIQUE] INDEX (A-SCH-016)
- [x] Unit tests for SQL fragments
- [x] Gated IT with HIB_P007_ prefix + cleanup checklist
- [x] Evidence NOTES + matrix hints
- [x] verify.py --phase P-007
- [ ] Accept (orchestrator / later)
- [ ] Commit (Human Gate / later)
