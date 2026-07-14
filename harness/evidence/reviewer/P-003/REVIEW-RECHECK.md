# P-003 Reviewer Recheck (after A-TYP-009 fix)

> Phase: `P-003`  
> Initiative: `I-001`  
> Build: `B-003`  
> Invocation: `rev-p003-recheck-20260714`  
> Prior invocation: `rev-p003-20260714` (decision: **request-changes**)  
> Fix under test: `impl-p003-fix-binary-20260714`  
> Independent retest: `test-p003-retest-20260714` (verdict: **PASS**)  
> Role: `reviewer` (readonly; landed by orchestrator)  
> Decision: **approve**  
> Date: 2026-07-14

## Scope of recheck

- Prior MAJOR **A-TYP-009** only (BINARY DDL form), plus regression spot-check that prior PASS checklist items remain intact.
- Sources: `XuguDialect.java` columnType BINARY path; `XuguDialectTest`; `XuguBinarySchemaExportIT` + `P003BinaryEntity`; contract/matrix A-TYP-009; XuGu docs `binary.md`; test RETEST evidence (`TEST-REPORT-RETEST.md`, `verification-retest.json`, IT log / Surefire XML).

## Prior MAJOR disposition

| Finding | Prior | Recheck | Status |
|---|---|---|---|
| **A-TYP-009** `binary($l)` vs docs bare `BINARY`; SchemaExport of binary attr unproven | MAJOR / request-changes | Mapped to bare `"binary"`; SchemaExport IT on live Xugu emits `payload binary not null`; rejects `binary(`; CREATE succeeds under `compatiblemode=NONE` | **CLOSED** |

### Closure evidence (A-TYP-009)

| Check | Evidence | Result |
|---|---|---|
| Implementation | `case SqlTypes.BINARY, SqlTypes.VARBINARY -> "binary"`; LONG* → `"blob"`; `getMaxVarbinaryLength()=65536` | PASS |
| No residual `binary($l)` | Grep under `dialect/` — no matches | PASS |
| Unit asserts | `XuguDialectTest`: BINARY/VARBINARY → `"binary"` | PASS |
| Docs SSOT | `E:\Work\docs\content\reference\sql\datatype\binary.md` — `CREATE … (col1 BINARY)` only | PASS |
| Contract / matrix | `xugu-dialect.p003-types-ddl.contract.md` A-TYP-009 → bare `binary`; matrix ✅ bare BINARY (no `$l`) | PASS |
| SchemaExport observed SQL | `create table HIB_P003_BINARY_PROBE (id integer not null, payload binary not null, primary key (id))` | PASS |
| Live CREATE | `XuguBinarySchemaExportIT` gate ON; table created + insert `X'0102'` + drop | PASS |
| Independent retest | `test-p003-retest-20260714`: offline PASS (6 IT skip); IT 6/6 PASS; `VERIFY PASS` (`verification-retest.json`) | PASS |

**Fix path chosen:** prior option **(a)** — bare `binary` (length via `getMaxVarbinaryLength` / binding), not `(b)` `BINARY(n)`. Prior QUESTION about live acceptance of `BINARY(n)` is **superseded / moot**.

## Checklist (recheck)

| Item | Result | Notes |
|---|---|---|
| Approved Build scope respected | PASS | B-003 = P-003 only; no later-Phase expansion in fix |
| Forbidden inheritance | PASS | Still `extends Dialect` only |
| Type mapping A-TYP-009 vs docs | **PASS** | Was FAIL on prior review; now bare `binary` |
| SchemaExport binary path IT’d | **PASS** | Was gap; now `XuguBinarySchemaExportIT` |
| IT real-DB + verify (retest) | PASS | 6 IT / 0 fail; VERIFY PASS |
| Scope creep | PASS | No pagination/lock/identity/sequence/functions/SPI |
| Prior BLOCKERs | PASS | None then; none now |

## Findings (this recheck)

### BLOCKER
- None

### MAJOR
- None remaining. **A-TYP-009 CLOSED.**

### MINOR (carry-forward; do not block approve)
1. **A-TYP-007 TIME** — unit `time($p)`; no real-DB TIME round-trip / SchemaExport IT.
2. **A-DDL-005 DEFAULT** — no entity/IT asserting DEFAULT clause emission/execution.
3. **A-DDL-002** — ALTER ADD COLUMN via raw JDBC matching `getAddColumnString()`, not SchemaUpdate coordinator.
4. **A-TYP-019 CAST** — `castPattern` → `super` only; no HQL/native CAST smoke IT.
5. **A-XCUT-001** — `IdentifierCaseStrategy.UPPER`; no live unquoted-identifier fold IT.
6. **Test helper URL** embeds local default credentials (`XuguTestConnection.DEFAULT_URL`) — charter-local OK; keep secrets discipline.

### QUESTION
- Prior QUESTION on `BINARY(n)` acceptance — **closed as moot** (docs-aligned bare form proven).

## Validation status

- Independent test retest: **PASS** (`test-p003-retest-20260714`)
- Project verify: `harness/evidence/test/P-003/verification-retest.json` → **PASS**
- Real DB IT (gate ON): 6 executed / 0 failed / 0 skipped
- Observed BINARY DDL: `create table HIB_P003_BINARY_PROBE (id integer not null, payload binary not null, primary key (id))`

## Recommendation

**approve** — MAJOR A-TYP-009 is closed with docs-aligned bare `binary` and live SchemaExport proof; independent retest green. MINORs may remain as follow-ups. Orchestrator may proceed to Accept + must-commit (Human Gate still owns Ship).

## Decision

- Decision: `approve`
- Invocation: `rev-p003-recheck-20260714`
- Prior decision superseded for MAJOR gate: `request-changes` (`rev-p003-20260714`) → **approve** on recheck
- Decided by: reviewer (readonly; evidence written by orchestrator)
- Date: 2026-07-14

## Handoff payload (for orchestrator)

```yaml
role: reviewer
phase_id: P-003
build_id: B-003
initiative_id: I-001
invocation_id: rev-p003-recheck-20260714
prior_invocation_id: rev-p003-20260714
step_id: RP-04
status: passed
decision: approve
required: true
condition: full_or_risk_ge_8
readonly: true
evidence: harness/evidence/reviewer/P-003/REVIEW-RECHECK.md
major_closed:
  - id: A-TYP-009
    disposition: closed
    fix: bare-binary
    observed_sql: "create table HIB_P003_BINARY_PROBE (id integer not null, payload binary not null, primary key (id))"
    retest: test-p003-retest-20260714
minors_deferred: true
next: orchestrator Accept + must-commit (no Ship without Human Gate)
```
