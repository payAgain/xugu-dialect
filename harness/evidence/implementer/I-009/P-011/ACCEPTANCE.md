# P-011 Acceptance Evidence (Implementer RP-01)

> Phase: `P-011` · Initiative: `I-009` · Build: `B-001`  
> Role: implementer · Theme: **docs SSOT final align + Accept prep**

## Decision

- Decision: `accepted`
- Path: Final honest counts for I-009 deferred closure; charter **98** rollup **not inflated**; doc-forbidden rows remain negative-only anchors.
- Rationale: P-002…P-010 evidence already closed all 20 deferred inventory rows; P-011 aligns user-facing docs and production-regression-baseline Summary without claiming Ship or extra covered-live on charter 98.

## Criteria table

| Criterion | Expected | Evidence | Status |
|---|---|---|---|
| All deferred rows SSOT honest | Each covered-live or known-limit-documented; C-JSON-006 doc-forbidden negative | `contracts/production-regression-baseline.md` § Summary + I-009 routing | **PASS** |
| Doc-forbidden remain skip/negative | No invented SQL | C-JSON-006, A-PAG-005, A-LCK-004/005, etc. | **PASS** |
| verification.md / user-guide aligned | I-009 Accept prep sections | `docs/verification.md`, `docs/user-guide/{03-verify,04-feature-matrix,README}.md` | **PASS** |
| Honest charter counts | **83/98** covered-live + **15** known-limit; **20/20** deferred closed | Summary counts; no「94 covered-live」 | **PASS** |
| Harness hygiene | P-001 Decision line; P-003/P-004 verification.json BOM removed | harness_check PASS | **PASS** |

## Doc updates (this Phase)

| File | Change |
|---|---|
| `contracts/production-regression-baseline.md` | I-009/P-011 Summary counts rollup; deferred 20/20; negative-only **11** |
| `docs/verification.md` | New § I-009 deferred matrix delivery (Accept prep) |
| `docs/user-guide/03-verify.md` | § I-009 Accept prep — offline vs live, deferred closure table |
| `docs/user-guide/04-feature-matrix.md` | Examples updated (A-TYP-014, C-JSON-006…); § I-009 deferred closure |
| `docs/user-guide/README.md` | I-009 one-liner |

## Deferred inventory final status (20/20)

| Status | Count | Row IDs |
|---|---:|---|
| covered-live | 7 | A-FUN-019, A-FUN-020, A-FUN-021, A-LCK-006, A-IDN-005, A-DDL-007, C-SRV-001 |
| known-limit-documented | 12 | A-TYP-014/016/017/018, A-FUN-015, A-DDL-008/009, A-PAG-004/006, A-SCH-003/017, C-SEL-001 |
| doc-forbidden negative-only | 1 | C-JSON-006 |

Open matrix **延后**: **0** (`A-XCUT-012` Ship defer anchor only).

## Validation (implementer)

| Command | Exit | Detail |
|---|---|---|
| `mvn -q -DskipTests package` | 0 | offline build |
| `mvn -q test` | 0 | offline full reactor (IT gated/skipped) |

## NOT done

- Ship / Maven Central / tag / push
- Live full-reactor green (infra-dependent — RP-02 test)

## Observed flow

- **i009-full-reactor-live-evidence-verify-pass-accept-prep**: docs aligned; offline verify delegated to RP-02
