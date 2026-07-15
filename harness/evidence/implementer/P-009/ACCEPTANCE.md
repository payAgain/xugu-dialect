# P-009 Acceptance Evidence

> Phase: `P-009`  
> Initiative: `I-001`  
> Build: `B-009`  
> Result: `PASS`  
> Role: orchestrator (Accept)

## Approved scope

- Build: B-009 (P-009 only) — `harness/builds/B-009.json`
- Goal: Spring Boot **4.1.0** demo with forced **`hibernate.version=7.4.5.Final`**, `compatible_mode=NONE`, env-overridable connection params
- Human Gate approval: B-009 scope P-009 only

## Role pipeline

| Step | Role | Status | Invocation | Independent | Evidence |
|---|---|---|---|---|---|
| RP-01 | implementer | passed | impl-p009-20260715 | N/A | `harness/evidence/implementer/P-009/` |
| RP-02 | test | passed | test-p009-20260715 | true | `harness/evidence/test/P-009/TEST-REPORT.md` |
| RP-03 | reviewer | passed | rev-p009-20260715 | true | `harness/evidence/reviewer/P-009/REVIEW.md` |

## Locked / chosen forms

| Capability | Locked form |
|---|---|
| Spring Boot | **4.1.0** |
| Hibernate (forced) | **7.4.5.Final** (overrides Boot BOM default 7.4.1.Final) |
| Explicit dialect | `hibernate.dialect=com.xugu.dialect.XuguDialect` |
| compatible_mode | `NONE` (JDBC URL default) |
| Env overrides | `XUGU_JDBC_URL` / `XUGU_USER` / `XUGU_PASSWORD` |
| Secrets in VCS | Local Charter defaults only (SYSDBA / 127.0.0.1); **no production secrets** |
| Dialect in demo | **Not implemented** — consumes `xugu-dialect` module |
| Demo capability | Persist/find `DemoPerson` → `HIB_DEMO_PERSON` on real XuguDB |

Reviewer MINOR (optional, non-blocking): user-guide prose deferred to P-010; document `-am` for demo IT if needed.

## Command verification

- Phase verification evidence: `harness/evidence/test/P-009/verification.json`
- Also: `harness/evidence/implementer/P-009/verification.json` (RP-01)
- Overall status: **VERIFY PASS**
- Required check IDs covered: `build`, `test`

## Observed affected flows

| Flow | Method | Result | Evidence |
|---|---|---|---|
| spring-boot-demo-starts-against-real-db | `DemoPersonCrudIT.persistAndFindPerson` gate ON | PASS | `harness/evidence/test/P-009/IT-RESULT.txt`, `mvn-test-integration-demo.log` |
| hibernate-version-forced-745 | `mvn -pl demo-spring-boot dependency:tree -Dincludes=org.hibernate.orm:hibernate-core` | PASS | `harness/evidence/test/P-009/dependency-tree-hibernate.txt` |

## Real DB IT

- Gate: `-Dxugu.run.integration=true`
- Independent test: demo IT 1 executed / 0 failed / 0 skipped on live XuguDB (`compatiblemode=NONE`)
- Offline: demo IT skipped; `DemoOfflineSmokeTest` green
- Cleanup: `@AfterAll` DROP TABLE IF EXISTS `HIB_DEMO_PERSON`
- Dependency tree: `org.hibernate.orm:hibernate-core:jar:7.4.5.Final:compile`

## Residual risks

- MINOR: user-guide Boot/env/SPI callouts (P-010)
- Deferred: P-010 docs/user-guide; P-011 hardening

## Version control checkpoint

- Branch: `feat/i-001-xugu-dialect-major`
- Candidate commit: 7fe9586e597db6cf4480d99b0501e2ee538c6b72
- Deferred reason when no commit: N/A (must-commit on Accept)

## Acceptance decision

- Decision: `accepted`
- Decided by: `orchestrator`
- Date: 2026-07-15
- Blocker reference when not accepted: N/A
- Reviewer decision: `approve` (`rev-p009-20260715`)
- Pipeline: RP-01..RP-03 all `passed`
- Readiness: Spring Boot 4.1.0 demo + Hibernate 7.4.5.Final force + env secrets handling + real-DB CRUD PASS with VERIFY PASS
