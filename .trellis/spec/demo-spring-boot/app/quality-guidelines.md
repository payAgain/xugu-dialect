# Quality Guidelines — demo-spring-boot

---

## Role of This Module

Deliver a **runnable consumer** of `xugu-dialect`:

1. Shows explicit dialect + SPI auto-resolve
2. Demonstrates core JPA scenarios on real XuguDB
3. Documents config via `application.yml` + `demo-spring-boot/README.md` + user-guide

It is **not** the dialect implementation home and **not** the Accept matrix SSOT (that is `contracts/production-regression-baseline.md`).

---

## Forbidden Patterns

1. Moving Dialect / LimitHandler / function registry code into demo.
2. Depending on forbidden external dialect sources.
3. Removing `compatiblemode=NONE` from default URL without Charter decision.
4. Letting Boot BOM silently downgrade Hibernate below 7.4.5.Final.
5. Enabling Flyway by default in a way that breaks offline tests.
6. Committing production secrets.

---

## Required Patterns

1. Depend on `com.xugu:xugu-dialect` through the reactor (`-am` when building alone).
2. Keep env-overridable datasource settings.
3. Gate all live `@SpringBootTest` IT with `XuguIntegrationGate`.
4. Disable startup CRUD in IT.
5. Update user-guide / demo README when consumer config changes.

---

## Review Checklist

- [ ] Still a consumer-only change (or Flyway shim intentionally demo-local)
- [ ] Hibernate 7.4.5.Final forced
- [ ] NONE mode + env overrides preserved
- [ ] Offline `mvn -q -pl demo-spring-boot -am test` green
- [ ] Live IT updated/run if claiming demo-live evidence
- [ ] Contracts/baseline entrypoints renamed carefully

---

## Verify

```bash
mvn -q -DskipTests package
mvn -q -pl demo-spring-boot -am test
# optional live:
mvn -q -pl demo-spring-boot -am test -Dxugu.run.integration=true
```
