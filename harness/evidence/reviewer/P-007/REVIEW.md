# P-007 Reviewer Evidence (Full + risk>=8)

> Phase: `P-007`  
> Initiative: `I-001`  
> Build: `B-007`  
> Invocation: `rev-p007-20260715`  
> Role: `reviewer` (readonly; landed by orchestrator)  
> Decision: **approve**  
> Date: 2026-07-15

## Scope reviewed

- Phase packet: `harness/tasks/P-007.md` (risk_score=8, RP-03)
- Build: `harness/builds/B-007.json` (approved; `approved_phase_ids=[P-007]`)
- Matrix: `contracts/feature-matrix-definition-a.md` (A-SCH-001..016; A-SCH-003/017 deferred; A-SCH-007 documented-not-allowed)
- Main: `dialect/src/main/java/com/xugu/dialect/XuguDialect.java`
- Temp strategies: `dialect/src/main/java/com/xugu/dialect/temptable/XuguLocalTemporaryTableStrategy.java`, `XuguGlobalTemporaryTableStrategy.java`
- Tests: `XuguSchemaTempCommentTest`, `XuguSchemaTempCommentIT`
- Implementer evidence: `harness/evidence/implementer/P-007/` (NOTES, CHECKLIST, IT-RESULT, verification.json)
- Test evidence: `harness/evidence/test/P-007/` (TEST-REPORT, verification.json, IT logs, leftover-probe)
- XuGu docs (spot-check): schema / CREATE TABLE TEMP / COMMENT / constraints / truncate / indexes / `support_global_tab`

## Checklist

| Item | Result | Notes |
|---|---|---|
| Approved Build scope respected | PASS | B-007 = P-007 only; no SPI/demo |
| Forbidden inheritance | PASS | `XuguDialect extends Dialect` only; no MySQL/Oracle |
| Schema create/drop + qualifier | PASS | `create/drop schema`; `NameQualifierSupport.SCHEMA` only |
| Local/global temp + ON COMMIT | PASS | local preserve / global delete; global needs `support_global_tab=ON` |
| A-SCH-007 no temp FK | PASS | StandardTemporaryTableExporter; unit+IT assert no `foreign key`/`references` |
| COMMENT ON + inline | PASS | `comment on table/column ... is '...'`; inline ` comment '...'` |
| Permanent FK / UNIQUE / CHECK | PASS | live orphan reject; UNIQUE/CHECK; drop constraint |
| TRUNCATE / CREATE INDEX | PASS | live IT |
| Cleanup HIB_P007_* | PASS | CASCADE drop OK; leftover probe counts 0 |
| A-SCH-003 / A-SCH-017 deferred | PASS | matrix deferred; no catalog / advanced indexes claimed |
| Independent test RP-02 | PASS | `test-p007-20260715`; VERIFY PASS; 14/14 IT |
| Scope creep | PASS | No DialectResolver SPI (P-008) / demo |

## Findings

### BLOCKER
- None

### MAJOR
- None

### MINOR (optional; do not block approve)
1. Global temp live success depends on server `support_global_tab=ON`; dialect documents precondition — optional user-guide callout can wait for P-010.
2. Deferred matrix rows A-SCH-003 / A-SCH-017 remain out of scope (expected).

### QUESTION
- None blocking Accept.

## Validation status

- Independent test role: **PASS** (`test-p007-20260715`)
- Project verify: `harness/evidence/test/P-007/verification.json` — **PASS** (build + test required)
- Real DB IT: 14 executed / 0 failed / 0 skipped (gate ON); P-007 focused `XuguSchemaTempCommentIT` 1/1 PASS
- Observed flows: `schema-tooling-real-db`, `temp-table-comments-fk-real-db` — PASS on live XuguDB
- Cleanup: leftover probe ALL_TABLES/ALL_SCHEMAS/USER_TABLES `HIB_P007_*` => 0

## Recommendation

**approve** — schema/temp/comment/FK match matrix; A-SCH-007 correctly not emitted on temp; cleanup verified. Orchestrator may Accept + must-commit (Human Gate still owns Ship / B-008).

## Decision

- Decision: `approve`
- Invocation: `rev-p007-20260715`
- Decided by: reviewer (readonly; evidence landed by orchestrator)
- Date: 2026-07-15

## Handoff payload (for orchestrator)

```yaml
role: reviewer
phase_id: P-007
build_id: B-007
invocation_id: rev-p007-20260715
step_id: RP-03
status: passed
decision: approve
required: true
evidence: harness/evidence/reviewer/P-007/REVIEW.md
locked_forms:
  create_schema: "create schema {name}"
  drop_schema: "drop schema {name}"
  qualifier: SCHEMA_only
  local_temp: "create local temporary table ... on commit preserve rows"
  global_temp: "create global temporary table ... on commit delete rows"
  global_precondition: support_global_tab=ON
  no_temp_fk: true
  comment_table: "comment on table {t} is '{c}'"
  comment_column: "comment on column {t}.{c} is '{c}'"
  fk_alter: " add constraint {n} foreign key ({cols}) references {table} ({pk})"
  truncate: "truncate table {name}"
minors_deferred: true
next: orchestrator Accept + must-commit (propose B-008 to P-008 only; no Ship without Human Gate)
```
