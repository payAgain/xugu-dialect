# P-003 Reviewer Evidence (Full + risk>=8)

> Phase: `P-003`  
> Initiative: `I-001`  
> Build: `B-003`  
> Invocation: `rev-p003-20260714`  
> Role: `reviewer` (readonly; landed by orchestrator)  
> Decision: **request-changes**  
> Date: 2026-07-14

## Scope reviewed

- Phase packet: `harness/tasks/P-003.md` (risk_score=9, RP-04)
- Build: `harness/builds/B-003.json` (approved; `approved_phase_ids=[P-003]`)
- Contract: `contracts/xugu-dialect.p003-types-ddl.contract.md`
- Matrix: `contracts/feature-matrix-definition-a.md` (P-003 rows)
- Main: `dialect/src/main/java/com/xugu/dialect/XuguDialect.java`
- Helper: `dialect/src/main/java/com/xugu/dialect/internal/XuguKeywords.java`
- Tests: `dialect/src/test/java/com/xugu/dialect/**` (+ IT)
- Implementer evidence: `harness/evidence/implementer/P-003/`
- Test evidence: `harness/evidence/test/P-003/` (TEST-REPORT, verification.json, IT logs)
- XuGu docs (spot-check): `reference/sql/datatype/{numerical,character,datetime,bool,binary,guid,json,large-object}.md`, `reference/object/table/{create,alter,drop}.md`, `reference/sql/identifier.md`

## Checklist

| Item | Result | Notes |
|---|---|---|
| Approved Build scope respected | PASS | B-003 = P-003 only; no demo-spring-boot business expansion |
| Forbidden inheritance | PASS | `public class XuguDialect extends Dialect` only; no MySQL/Oracle imports/extends |
| Package / helpers | PASS | Sole public Dialect + `internal.XuguKeywords`; no pagination/lock/identity/sequence/function/SPI packages |
| Type mappings vs contract/docs (spot-check) | FAIL | Integers/NUMERIC/FLOAT→REAL→FLOAT/BOOLEAN/DATE/TIMESTAMP/GUID/JSON/CLOB→BLOB/VARCHAR lengths align; **BINARY→`binary($l)` undocumented** |
| DDL helpers vs docs | PASS (with gaps) | CREATE/ALTER ADD COLUMN/DROP/PK/NOT NULL match observed SchemaExport + docs; DEFAULT / binary column export weak |
| Port smell from forbidden sibling | PASS | No sibling-path comments, MySQL engine/backtick-primary, Oracle-ish packages, or Limit/Identity ports |
| IT real-DB flows | PASS | Gate ON: 5/5 IT; flows evidenced; `VERIFY PASS` (test role) |
| Scope creep into later Phases | PASS | No LimitHandler/locks/identity/sequence/functions/DialectResolver; deferred A-TYP-014–018 / A-DDL-007–009 not claimed |
| Blockers | PASS | None that make inheritance or whole Phase invalid |

## Findings

### BLOCKER
- None

### MAJOR
1. **A-TYP-009 BINARY DDL form vs XuGu docs (unproven SchemaExport path)**  
   - Implementation: `columnType(BINARY|VARBINARY) → "binary($l)"` (`XuguDialect.java`).  
   - Docs (`reference/sql/datatype/binary.md` and other CREATE examples) only show bare **`BINARY`** (no length parameter); no `BINARY(n)` examples found under `E:\Work\docs\content`.  
   - Round-trip IT uses hand-written `c_bin BINARY` (no `$l`), not dialect SchemaExport of a binary attribute.  
   - **Required before approve:** either (a) change mapping to bare `binary` (length enforced via `getMaxVarbinaryLength` / binding only), **or** (b) prove live Xugu accepts `BINARY(n)` via Hibernate SchemaExport IT and cite/document the proven form.

### MINOR
1. **A-TYP-007 TIME** — unit asserts `time($p)`; no real-DB TIME round-trip / SchemaExport coverage. Docs: TIME size `[0,3]`; TZ form documented without size — confirm `time($p) with time zone` if used.
2. **A-DDL-005 DEFAULT** — matrix marked via default exporter; no entity/IT asserting `DEFAULT` clause emission/execution.
3. **A-DDL-002** — ALTER ADD COLUMN exercised via raw JDBC matching `getAddColumnString()` (`add column`), not Hibernate SchemaUpdate coordinator.
4. **A-TYP-019 CAST** — `castPattern` delegates to `super` only; no HQL/native CAST smoke IT (noted in implementer residual risks).
5. **A-XCUT-001** — `IdentifierCaseStrategy.UPPER` set; no IT asserting unquoted identifier fold on live DB.
6. **Test helper URL** embeds local default credentials (`XuguTestConnection.DEFAULT_URL`) — acceptable for local charter defaults; keep secrets out of commits beyond this pattern.

### QUESTION
1. Does live XuguDB accept `CREATE TABLE t (b BINARY(16))` / Hibernate-emitted `binary(255)` under `compatible_mode=NONE`? (Drives MAJOR #1 fix choice.)

## Validation status

- Independent test role: **PASS** (`test-p003-20260714`)
- Project verify: `harness/evidence/test/P-003/verification.json` → `PASS` (build + test required)
- Real DB IT: 5 executed / 0 failed (gate ON); offline 5 IT skipped
- Observed CREATE SQL: `create table HIB_P003_DDL_PROBE (id integer not null, name varchar(64) not null, primary key (id))`
- Inheritance / TIMESTAMP choice / IT gate: PASS (agree with test spot checks)

## Recommendation

**request-changes** — core structure, inheritance, scope control, and real-DB IT for integer/decimal/varchar/bool/date/timestamp/lob/guid/json + basic SchemaExport are solid; **do not Accept** until A-TYP-009 BINARY DDL is aligned with XuGu docs **or** proven on live SchemaExport.

## Decision

- Decision: `request-changes`
- Invocation: `rev-p003-20260714`
- Decided by: reviewer (readonly; evidence written by orchestrator)
- Date: 2026-07-14

## Handoff payload (for orchestrator)

```yaml
role: reviewer
phase_id: P-003
build_id: B-003
invocation_id: rev-p003-20260714
step_id: RP-04
status: request-changes
decision: request-changes
required: true
evidence: harness/evidence/reviewer/P-003/REVIEW.md
next: implementer fix MAJOR A-TYP-009 (binary DDL) → re-test IT → re-review RP-04
```


---

## Superseded for MAJOR gate

Prior decision `request-changes` (`rev-p003-20260714`) is **superseded** for Accept by recheck **approve** (`rev-p003-recheck-20260714`).
See `harness/evidence/reviewer/P-003/REVIEW-RECHECK.md` (A-TYP-009 CLOSED via bare `binary` + SchemaExport IT).
