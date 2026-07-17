# P-002 Implementer Checklist (RP-01 / I-004 IDENTITY × reserved table)

- [x] `XuguDialect.getDefaultUseGetGeneratedKeys()` → `false`
- [x] `XuguIdentityColumnSupport.getIdentitySelectString` remains `select last_insert_id() from dual`
- [x] Verified `openQuote`/`closeQuote` are `"` (existing + IT SQL shows `"order"`)
- [x] Documented in NOTES why (JDBC RETURN_GENERATED_KEYS × reserved name; not driver fix)
- [x] Unit `dialectPrefersIdentitySelectOverGetGeneratedKeys` → `assertFalse`
- [x] ORM IT `XuguReservedIdentityIT` — Session.persist + flush on reserved table `"order"`
- [x] Entity `I004P002OrderEntity` — `GenerationType.IDENTITY`, Integer id, `@Table(name = "\"order\"")`
- [x] Evidence NOTES + CHECKLIST
- [x] Handoff `harness/handoffs/implementer/P-002.yaml` → `impl-p002-20260717`
- [ ] Accept (orchestrator / later)
- [ ] Commit (Human Gate / later)
