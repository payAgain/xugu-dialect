# P-009 Implementer Checklist (RP-01)

- [x] Module `demo-spring-boot/` Spring Boot 4.1.0
- [x] Force `hibernate.version=7.4.5.Final` (not BOM 7.4.1)
- [x] dependency:tree evidence for hibernate-core 7.4.5.Final
- [x] JDBC systemPath root `xugu-jdbc-12.3.6.jar`; driver `com.xugu.cloudjdbc.Driver`
- [x] Env connection keys + local fallbacks; `compatiblemode=NONE`
- [x] Explicit `com.xugu.dialect.XuguDialect` in application.yml (+ SPI comment)
- [x] Entity CRUD persist/find (`HIB_DEMO_PERSON`) + IT cleanup
- [x] `spring-boot-starter-data-jpa` + test starter
- [x] README documents env keys / how to run
- [x] Gated IT `@EnabledIf` / `xugu.run.integration=true`; offline `mvn test` green
- [x] No dialect implementation inside demo
- [x] No production secrets committed
- [x] `verify.py --phase P-009` → VERIFY PASS
- [ ] Accept (orchestrator / later)
- [ ] Commit (Human Gate / later)
