# P-009 Reviewer Evidence (required; secrets + Boot/Hibernate alignment)

> Phase: `P-009`  
> Initiative: `I-001`  
> Build: `B-009`  
> Invocation: `rev-p009-20260715`  
> Role: `reviewer` (readonly; landed by orchestrator)  
> Decision: **approve**  
> Date: 2026-07-15

## Scope reviewed

- Phase packet: `harness/tasks/P-009.md` (risk_score=7, RP-03 required; condition=null)
- Build: `harness/builds/B-009.json` (approved; `approved_phase_ids=[P-009]`)
- Demo module: `demo-spring-boot/**`
- Config: `demo-spring-boot/src/main/resources/application.yml`, `demo-spring-boot/pom.xml`, `demo-spring-boot/README.md`
- Implementer evidence: `harness/evidence/implementer/P-009/` (NOTES, CHECKLIST, IT-RESULT, dependency-tree, verification.json)
- Test evidence: `harness/evidence/test/P-009/` (TEST-REPORT, verification.json, IT logs, SPOT-CHECK, dependency-tree)

## Checklist

| Item | Result | Notes |
|---|---|---|
| Approved Build scope respected | PASS | B-009 = P-009 only; no user-guide body (P-010) |
| Spring Boot 4.1.0 | PASS | `spring-boot.version=4.1.0`; IT logs show Boot v4.1.0 |
| Hibernate forced 7.4.5.Final | PASS | property overrides BOM 7.4.1; dependency:tree = `hibernate-core:jar:7.4.5.Final:compile` |
| compatible_mode=NONE default | PASS | JDBC URL default includes `compatiblemode=NONE` |
| Env credential overrides | PASS | `XUGU_JDBC_URL` / `XUGU_USER` / `XUGU_PASSWORD` placeholders |
| No production secrets in VCS | PASS | Local Charter SYSDBA/127.0.0.1 defaults only; README warns |
| Dialect not implemented in demo | PASS | demo Java = app/entity/repo/runner; references `com.xugu.dialect.XuguDialect` |
| Real-DB demo IT | PASS | `DemoPersonCrudIT.persistAndFindPerson` PASS; cleanup DROP TABLE |
| Independent test RP-02 | PASS | `test-p009-20260715`; VERIFY PASS |
| Scope creep | PASS | No dialect core Java; no docs/user-guide |

## Findings

### BLOCKER
- None

### MAJOR
- None

### MINOR (optional; do not block approve)
1. Full user-guide prose (Boot start, env keys, SPI vs explicit) belongs in P-010.
2. Demo IT primary command uses `-pl demo-spring-boot -am` so sibling dialect resolves; document in P-010 if needed.

### QUESTION
- None blocking Accept.

## Validation status

- Independent test role: **PASS** (`test-p009-20260715`)
- Project verify: `harness/evidence/test/P-009/verification.json` → **PASS** (build + test required)
- Real DB IT: `DemoPersonCrudIT` 1/1 PASS on live XuguDB; offline smoke green; IT skipped when gate off
- Observed flows: `spring-boot-demo-starts-against-real-db`, `hibernate-version-forced-745` → PASS
- Secrets: env overrides present; no production secrets committed

## Recommendation

**approve** — Spring Boot 4.1.0 demo with forced Hibernate 7.4.5.Final, env-overridable local defaults, and real-DB CRUD demonstrated. Orchestrator may Accept + must-commit (Human Gate still owns Ship / B-010).

## Decision

- Decision: `approve`
- Invocation: `rev-p009-20260715`
- Decided by: reviewer (readonly; evidence landed by orchestrator)
- Date: 2026-07-15

## Handoff payload (for orchestrator)

```yaml
role: reviewer
phase_id: P-009
build_id: B-009
invocation_id: rev-p009-20260715
step_id: RP-03
status: passed
decision: approve
required: true
evidence: harness/evidence/reviewer/P-009/REVIEW.md
locked_forms:
  spring_boot_version: "4.1.0"
  hibernate_version_forced: "7.4.5.Final"
  boot_bom_default_overridden: "7.4.1.Final"
  dialect_explicit: com.xugu.dialect.XuguDialect
  compatible_mode: NONE
  env_overrides: [XUGU_JDBC_URL, XUGU_USER, XUGU_PASSWORD]
  production_secrets_committed: false
  dialect_implemented_in_demo: false
minors_deferred: true
next: orchestrator Accept + must-commit (propose B-010 to P-010 only; no Ship without Human Gate)
```
