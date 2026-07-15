# P-008 Acceptance Evidence

> Phase: `P-008`  
> Initiative: `I-001`  
> Build: `B-008`  
> Result: `PASS`  
> Role: orchestrator (Accept)

## Approved scope

- Build: B-008 (P-008 only) — `harness/builds/B-008.json`
- Matrix SSOT: `contracts/feature-matrix-definition-a.md` (A-SPI-001..004; A-XCUT-003/005; A-XCUT-006 documented-not-allowed)
- Human Gate approval: B-008 scope P-008 only

## Role pipeline

| Step | Role | Status | Invocation | Independent | Evidence |
|---|---|---|---|---|---|
| RP-01 | implementer | passed | impl-p008-20260715 | N/A | `harness/evidence/implementer/P-008/` |
| RP-02 | test | passed | test-p008-20260715 | true | `harness/evidence/test/P-008/TEST-REPORT.md` |
| RP-03 | reviewer | passed | rev-p008-20260715 | true | `harness/evidence/reviewer/P-008/REVIEW.md` |

## Locked / chosen SPI forms

| Capability | Locked form |
|---|---|
| SPI interface (A-SPI-001/002) | `org.hibernate.engine.jdbc.dialect.spi.DialectResolver` |
| Resolver implementation | `com.xugu.dialect.XuguDialectResolver` |
| Services resource | `META-INF/services/org.hibernate.engine.jdbc.dialect.spi.DialectResolver` → resolver FQCN |
| Match rule (live-proven) | product name **or** driver name contains token `xugu` (case-insensitive) |
| Explicit dialect (A-SPI-003) | `hibernate.dialect=com.xugu.dialect.XuguDialect` |
| Non-match (A-SPI-004) | MySQL / Oracle / PostgreSQL / blank → `null` |
| Isolation (A-XCUT-005) | READ_COMMITTED / REPEATABLE_READ / SERIALIZABLE supported |
| READ UNCOMMITTED (A-XCUT-006) | **NOT claimed** (documented-not-allowed) |
| compatible_mode (A-XCUT-003) | IT / docs use `compatiblemode=NONE` |

Reviewer MINOR (optional, non-blocking): SPI vs explicit dialect prose can wait for P-010; demo is P-009.

## Command verification

- Phase verification evidence: `harness/evidence/test/P-008/verification.json`
- Also: `harness/evidence/implementer/P-008/verification.json` (RP-01)
- Overall status: **VERIFY PASS**
- Required check IDs covered: `build`, `test`

## Observed affected flows

| Flow | Method | Result | Evidence |
|---|---|---|---|
| explicit-dialect-config | `XuguDialectResolverIT.explicitDialect_sessionFactorySimpleQuery` gate ON | PASS | `harness/evidence/test/P-008/mvn-test-integration.log` |
| spi-dialect-resolver-autodetect | `XuguDialectResolverIT.spiAutoResolve_sessionFactoryWithoutExplicitDialect` gate ON | PASS | `harness/evidence/test/P-008/IT-RESULT.txt`, `jar-services-listing.txt` |

## Real DB IT

- Gate: `-Dxugu.run.integration=true`
- Independent test: 17 IT executed / 0 failed / 0 skipped on live XuguDB (`compatiblemode=NONE`)
- Offline: 17 IT skipped; unit green (46)
- Cleanup: leftover probe `HIB_P008_*` counts 0
- Jar services: FOUND (`com.xugu.dialect.XuguDialectResolver`)

## Residual risks

- MINOR: user-guide SPI/explicit callout (optional to P-010)
- Deferred: P-009 Spring Boot demo; P-010+ docs/hardening

## Version control checkpoint

- Branch: `feat/i-001-xugu-dialect-major`
- Candidate commit: PENDING_MUST_COMMIT
- Deferred reason when no commit: N/A (must-commit on Accept)

## Acceptance decision

- Decision: `accepted`
- Decided by: `orchestrator`
- Date: 2026-07-15
- Blocker reference when not accepted: N/A
- Reviewer decision: `approve` (`rev-p008-20260715`)
- Pipeline: RP-01..RP-03 all `passed`
- Readiness: DialectResolver SPI + explicit config PASS with VERIFY PASS + real-DB IT + jar services + cleanup + no RU claim
