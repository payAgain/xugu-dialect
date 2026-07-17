# P-001 Implementer Checklist (RP-01 / I-004 DROP SEQUENCE IF EXISTS)

- [x] `XuguSequenceSupport.getDropSequenceString` → `drop sequence if exists …`
- [x] Class javadoc updated (CREATE defaults; DROP IF EXISTS per XuGu docs)
- [x] Unit `XuguIdentitySequenceTest.createDropSequenceStrings_A_SEQ_001_002_005` expects `drop sequence if exists HIB_P005_SEQ`
- [x] ORM IT `XuguAutoSequenceDropIT` — schema DROP when AUTO sequence absent (no E7002 halt)
- [x] ORM IT `XuguAutoSequenceDropIT` — SessionFactory `create-drop` builds on clean DB
- [x] Entity `I004P001AutoEntity` — `GenerationType.AUTO`, Integer id, prefix `HIB_I004_P001_*`
- [x] Evidence NOTES + CHECKLIST
- [x] Handoff `harness/handoffs/implementer/P-001.yaml` → `completed_awaiting_test_review`
- [ ] Accept (orchestrator / later)
- [ ] Commit (Human Gate / later)
- [ ] P-002 (not started)
