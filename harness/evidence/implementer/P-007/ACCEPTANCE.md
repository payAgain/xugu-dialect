# P-007 Acceptance Evidence

> Phase: `P-007`  
> Initiative: `I-001`  
> Build: `B-007`  
> Result: `PASS`  
> Role: orchestrator (Accept)

## Approved scope

- Build: B-007 (P-007 only) — `harness/builds/B-007.json`
- Matrix SSOT: `contracts/feature-matrix-definition-a.md` (A-SCH-001..016; A-SCH-003/017 deferred; A-SCH-007 documented-not-allowed)
- Human Gate approval: B-007 scope P-007 only

## Role pipeline

| Step | Role | Status | Invocation | Independent | Evidence |
|---|---|---|---|---|---|
| RP-01 | implementer | passed | impl-p007-20260715 | N/A | `harness/evidence/implementer/P-007/` |
| RP-02 | test | passed | test-p007-20260715 | true | `harness/evidence/test/P-007/TEST-REPORT.md` |
| RP-03 | reviewer | passed | rev-p007-20260715 | true | `harness/evidence/reviewer/P-007/REVIEW.md` |

## Locked / chosen schema forms

| Capability | Locked form |
|---|---|
| Schema create/drop (A-SCH-001) | `create schema {name}` / `drop schema {name}` |
| Qualifier (A-SCH-002) | `NameQualifierSupport.SCHEMA` only (`schema.table`) |
| Local temp (A-SCH-004/006) | `create local temporary table` + `on commit preserve rows` |
| Global temp (A-SCH-005/006) | `create global temporary table` + `on commit delete rows`; precondition `support_global_tab=ON` |
| Temp FK (A-SCH-007) | **NOT emitted** (documented-not-allowed) |
| COMMENT ON (A-SCH-008/009) | `comment on table/column ... is '...'` |
| Inline comment (A-SCH-010) | ` comment '...'` |
| FK alter (A-SCH-012) | ` add constraint {n} foreign key ({cols}) references {table} ({pk})` |
| Truncate (A-SCH-015) | `truncate table {name}` |
| Index (A-SCH-016) | `create index` / `create unique index` |

Deferred: A-SCH-003 catalog qualifier; A-SCH-017 advanced indexes.

Reviewer MINOR (optional, non-blocking): P-010 user-guide callout for `support_global_tab` precondition.

## Command verification

- Phase verification evidence: `harness/evidence/test/P-007/verification.json`
- Also: `harness/evidence/implementer/P-007/verification.json` (RP-01)
- Overall status: **VERIFY PASS**
- Required check IDs covered: `build`, `test`

## Observed affected flows

| Flow | Method | Result | Evidence |
|---|---|---|---|
| schema-tooling-real-db | `XuguSchemaTempCommentIT.schemaTempCommentFkTruncate_A_SCH` (schema + qualified) gate ON | PASS | `harness/evidence/test/P-007/mvn-test-integration.log` |
| temp-table-comments-fk-real-db | same IT (temp/comment/FK/UK/CHECK/truncate/index) gate ON | PASS | `harness/evidence/test/P-007/IT-RESULT.txt`, `leftover-probe.txt` |

## Real DB IT

- Gate: `-Dxugu.run.integration=true`
- Independent test: 14 IT executed / 0 failed / 0 skipped on live XuguDB (`compatiblemode=NONE`)
- Offline: 14 IT skipped; unit green (35)
- Cleanup: leftover probe `HIB_P007_*` counts 0

## Residual risks

- MINOR: `support_global_tab` docs polish (optional to P-010)
- Deferred: A-SCH-003 / A-SCH-017; P-008+ SPI / demo

## Version control checkpoint

- Branch: `feat/i-001-xugu-dialect-major`
- Candidate commit: PENDING_MUST_COMMIT
- Deferred reason when no commit: N/A (must-commit on Accept)

## Acceptance decision

- Decision: `accepted`
- Decided by: `orchestrator`
- Date: 2026-07-15
- Blocker reference when not accepted: N/A
- Reviewer decision: `approve` (`rev-p007-20260715`)
- Pipeline: RP-01..RP-03 all `passed`
- Readiness: schema/temp/comment/FK scope PASS with VERIFY PASS + real-DB IT + cleanup + A-SCH-007 not emitted
