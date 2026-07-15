# P-008 Reviewer Evidence (Full + risk>=8)

> Phase: `P-008`  
> Initiative: `I-001`  
> Build: `B-008`  
> Invocation: `rev-p008-20260715`  
> Role: `reviewer` (readonly; landed by orchestrator)  
> Decision: **approve**  
> Date: 2026-07-15

## Scope reviewed

- Phase packet: `harness/tasks/P-008.md` (risk_score=8, RP-03)
- Build: `harness/builds/B-008.json` (approved; `approved_phase_ids=[P-008]`)
- Matrix / contract: `contracts/feature-matrix-definition-a.md`, `contracts/xugu-dialect.contract.md` (A-SPI-001..004; A-XCUT-003/005; A-XCUT-006 documented-not-allowed)
- Main: `dialect/src/main/java/com/xugu/dialect/XuguDialectResolver.java`, `XuguDialect.java`
- Services: `dialect/src/main/resources/META-INF/services/org.hibernate.engine.jdbc.dialect.spi.DialectResolver`
- Tests: `XuguDialectResolverTest`, `XuguDialectServicesResourceTest`, `XuguDialectResolverIT`
- Implementer evidence: `harness/evidence/implementer/P-008/` (NOTES, CHECKLIST, IT-RESULT, verification.json, jar listing)
- Test evidence: `harness/evidence/test/P-008/` (TEST-REPORT, verification.json, IT logs, leftover-probe, jar listing)

## Checklist

| Item | Result | Notes |
|---|---|---|
| Approved Build scope respected | PASS | B-008 = P-008 only; no Spring Boot demo (P-009) |
| Forbidden inheritance | PASS | `XuguDialect extends Dialect` only; no MySQL/Oracle |
| DialectResolver SPI FQCN | PASS | Hibernate 7.4 `org.hibernate.engine.jdbc.dialect.spi.DialectResolver` |
| META-INF/services packaging | PASS | Jar entry FOUND → `com.xugu.dialect.XuguDialectResolver` |
| Product/driver match rule | PASS | token `xugu` case-insensitive on product OR driver name; live `XuguDB` / `XuguDB JDBC Driver` |
| Non-Xugu non-match (A-SPI-004) | PASS | MySQL/Oracle/PostgreSQL/blank → null (unit) |
| Explicit dialect config | PASS | Live IT SessionFactory + HQL on real XuguDB |
| SPI auto-detect | PASS | Live IT without explicit dialect → `XuguDialect` v12.0 |
| No READ UNCOMMITTED claim | PASS | A-XCUT-006 documented-not-allowed; isolation hooks RC/RR only |
| Cleanup HIB_P008_* | PASS | leftover probe ALL/USER_TABLES = 0 |
| Independent test RP-02 | PASS | `test-p008-20260715`; VERIFY PASS; 17/17 IT |
| Scope creep | PASS | No demo module / user-guide body |

## Findings

### BLOCKER
- None

### MAJOR
- None

### MINOR (optional; do not block approve)
1. User-guide prose for SPI vs explicit dialect can wait for P-010.
2. Spring Boot auto-config demo remains P-009 (out of this Build).

### QUESTION
- None blocking Accept.

## Validation status

- Independent test role: **PASS** (`test-p008-20260715`)
- Project verify: `harness/evidence/test/P-008/verification.json` → **PASS** (build + test required)
- Real DB IT: 17 executed / 0 failed / 0 skipped (gate ON); P-008 focused `XuguDialectResolverIT` 3/3 PASS
- Observed flows: `explicit-dialect-config`, `spi-dialect-resolver-autodetect` → PASS on live XuguDB
- Cleanup: leftover probe `HIB_P008_*` => 0
- Jar services: FOUND

## Recommendation

**approve** — DialectResolver SPI packaging and explicit config match matrix; non-Xugu non-match and no RU claim verified. Orchestrator may Accept + must-commit (Human Gate still owns Ship / B-009).

## Decision

- Decision: `approve`
- Invocation: `rev-p008-20260715`
- Decided by: reviewer (readonly; evidence landed by orchestrator)
- Date: 2026-07-15

## Handoff payload (for orchestrator)

```yaml
role: reviewer
phase_id: P-008
build_id: B-008
invocation_id: rev-p008-20260715
step_id: RP-03
status: passed
decision: approve
required: true
evidence: harness/evidence/reviewer/P-008/REVIEW.md
locked_forms:
  spi_interface: org.hibernate.engine.jdbc.dialect.spi.DialectResolver
  resolver_class: com.xugu.dialect.XuguDialectResolver
  services_entry: META-INF/services/org.hibernate.engine.jdbc.dialect.spi.DialectResolver
  match_rule: product_or_driver_contains_xugu_ci
  explicit_dialect: com.xugu.dialect.XuguDialect
  isolation_supported: [READ_COMMITTED, REPEATABLE_READ, SERIALIZABLE]
  read_uncommitted_claimed: false
minors_deferred: true
next: orchestrator Accept + must-commit (propose B-009 to P-009 only; no Ship without Human Gate)
```
