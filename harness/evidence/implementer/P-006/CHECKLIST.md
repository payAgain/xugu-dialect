# P-006 Implementer Checklist (RP-01)

- [x] `XuguDialect.initializeFunctionRegistry` extended
- [x] Helpers under `com.xugu.dialect.function` (`XuguFunctionRegistrations`)
- [x] Matrix A-FUN-001..014, 016..018 covered (015 deferred)
- [x] UUID primary = `uuid()` documented + live-proven
- [x] JSON subset = `json_value` + `json_extract` (not MySQL dump)
- [x] listagg → XuGu `LISTAGG … WITHIN GROUP`
- [x] Unit tests: descriptors / pattern fragments
- [x] Gated IT: representative functions per family
- [x] Negative: unsupported function diagnosable
- [x] Evidence NOTES + matrix hints
- [x] verify.py --phase P-006
- [ ] Accept (orchestrator / later)
- [ ] Commit (Human Gate / later)
