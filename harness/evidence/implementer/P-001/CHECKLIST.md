# P-001 Implementer Checklist (I-002)

- [x] `XuguSqlAstTranslator` + factory (impl-p001-20260715)
- [x] HQL pagination gated IT (LIMIT not FETCH FIRST)
- [x] MAJOR fix after `rev-p001-20260715` request-changes: AST/HQL FOR UPDATE before LIMIT (+ WAIT after)
- [x] Live SQL captured for lock+page
- [x] `mvn -q test` EXIT 0
- [x] `mvn -q test -Dxugu.run.integration=true` EXIT 0
- [x] VERIFY PASS (implementer evidence)
- [ ] Independent RP-02 retest
- [ ] RP-03 re-review approve
- [ ] Accept / must-commit
