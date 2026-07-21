# I-010 / P-011 Acceptance Evidence (architect-contract RP-01)

> Phase: `P-011`  
> Initiative: `I-010`  
> Build: `B-002`  
> Role step: `RP-01` architect-contract  
> invocation_id: `inv-i010-p011-architect-contract`  
> Result: **ACCEPT READY** for RP-01 deliverable (docs RP-02 / reviewer RP-03 follow)

- Decision: `accepted` for RP-01 deliverable

## Approved scope

- Task: `harness/tasks/P-011.md`
- Build: `harness/builds/B-002.json`
- Initiative brief: `harness/initiatives/I-010/brief.md`
- Scope: `harness/drafts/I-010-SCOPE-CLARIFYING.md` § B-002
- Constraints: GAV `7.4.5.Final`; `compatiblemode=NONE`; **NOT Ship**; no dialect/demo Java; no covered-live inflation

## Acceptance criteria (RP-01)

| Criterion | Result | Evidence |
|---|---|---|
| Given Human-approved 10 themes, when SSOT lands, then each row has pri / theme / layer / planned class / status=`planned` | **PASS** | `contracts/xuguefcore-parity-suite.md` § Suite rows (XP-001…XP-010) |
| Boundary / failure: no doc-forbidden rows in suite | **PASS** | § Out of scope — SKIP LOCKED / FOR SHARE / json_table / ENUM / Spec8500 / Migrations / Retry / RETURNING / ADO / Owned 照搬 |
| IT gate documented | **PASS** | § IT / Unit gates — `XUGU_RUN_IT=true` / `-Dxugu.run.integration=true` |
| No dialect/demo Java; no Ship; no covered-live claim | **PASS** | contracts + evidence only; all rows `planned` |

## Files created / modified (this RP)

| Path | Action |
|---|---|
| `contracts/xuguefcore-parity-suite.md` | **created** — B-002 parity suite SSOT |
| `harness/evidence/architect-contract/I-010/P-011/ACCEPTANCE.md` | **created** — this file |
| `harness/handoffs/architect-contract/I-010-P-011.yaml` | **created** — handoff `status: passed` |

## Residual for later pipeline steps

1. **RP-02 docs** — point `03-verify` / `04-feature-matrix` at this suite + gate wording.
2. **RP-03 reviewer** — readonly confirm suite matches Human approval + out-of-scope exclusions.
3. Owning test Phases (P-012…P-016) update per-row `status` only; must not invent forbidden SQL.

## Next

- RP-02 docs → RP-03 reviewer → Phase Accept / must-commit (orchestrator / Human Gate)
