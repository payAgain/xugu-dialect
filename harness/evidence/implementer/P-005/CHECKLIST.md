# P-005 Implementer Checklist (RP-01)

- [x] XuguIdentityColumnSupport wired from XuguDialect
- [x] getIdentityColumnString → `identity(1,1)`
- [x] supportsIdentityColumns = true
- [x] Identity retrieval: JDBC getGeneratedKeys proven; LAST_INSERT_ID select fallback documented
- [x] XuguSequenceSupport CREATE/DROP/NEXTVAL/CURRVAL/FROM DUAL
- [x] NEXTVAL form locked: `select seq.nextval from dual`
- [x] Unit tests for DDL/SQL fragments
- [x] Gated IT identity + sequence with cleanup
- [x] Evidence NOTES + matrix hints
- [x] verify.py --phase P-005
- [ ] Accept (orchestrator / later)
- [ ] Commit (Human Gate / later)
