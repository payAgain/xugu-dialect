# P-001 Test Checklist (RP-02)

- [x] `mvn -q test` EXIT 0 (offline)
- [x] `mvn -q test -Dxugu.run.integration=true` EXIT 0 (live IT)
- [x] `verify.py --phase P-001 --evidence harness/evidence/test/P-001/verification.json` → VERIFY PASS
- [x] Spot-check: HQL SQL uses `limit`/`offset`, not `fetch first`
- [x] Spot-check: `getSqlAstTranslatorFactory()` non-null
- [x] Spot-check: version remains `7.4.5.Final`
- [x] Observed flow `hql-pagination-offset-fetch-real-db` recorded
- [x] TEST-REPORT + handoff written; RP-02 `passed` / `test-p001-20260715`
- [ ] Accept / commit (orchestrator after RP-03 reviewer)
