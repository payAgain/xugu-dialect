# I-010 / P-001 Acceptance Evidence (architect-contract RP-01 + docs RP-02 prep)

> Phase: `P-001`  
> Initiative: `I-010`  
> Build: `B-001`  
> Role steps: `RP-01` architect-contract (+ docs deliverables co-landed for serial pipeline)  
> invocation_id: `inv-i010-p001-architect-docs`  
> Result: **ACCEPT READY** (pending reviewer RP-03)

- Decision: `accepted`

## Approved scope

- Task: `harness/tasks/P-001.md`
- Initiative brief: `harness/initiatives/I-010/brief.md`
- Scope: `harness/drafts/I-010-SCOPE-CLARIFYING.md`
- Constraints: GAV `7.4.5.Final`; `compatiblemode=NONE`; **NOT Ship**; no dialect/demo Java

## Acceptance criteria

| Criterion | Result | Evidence |
|---|---|---|
| Given I-009 closed rows, when Definition A is read, then no stale 「延后」 for delivered inventory (except A-XCUT-012) | **PASS** | `contracts/feature-matrix-definition-a.md` — open **延后** = `A-XCUT-012` only; counts **可实现 97 / 文档不允许 7 / 延后 1** |
| Given baseline SSOT, when user docs cite I-009 counts / A-FUN-021, then labels match baseline row-level | **PASS** | A-FUN-021 = **known-limit-documented**; I-009 rollup **6 covered-live · 13 known-limit · 1 doc-forbidden** in baseline + `03-verify` / `04-feature-matrix` / README |
| Given I-007 Track B, when Demo smoke table is read, then validate/HQL/bulk are not false gaps | **PASS** | `production-regression-baseline.md` § Demo smoke — `DemoValidateStartupIT` / `DemoFunctionsIT` / `DemoBulkMutationIT` = **covered-live** |
| Troubleshooting covers INTERVAL / XML / geometry / UDT / PARTITION / catalog / ENCRYPT | **PASS** | `docs/user-guide/05-troubleshooting.md` §13–15 |
| harness_check | **PASS** | [`verification.json`](verification.json) |
| No dialect/demo Java; no Ship/tag/push | **PASS** | contracts + docs + evidence only |

## SSOT changes (architect-contract)

### Definition A (`contracts/feature-matrix-definition-a.md`)

| ID | Was | Now | Acceptance hint points to |
|---|---|---|---|
| A-TYP-014 | 延后 | 可实现 | baseline § A-TYP-014 / `XuguIntervalTypeIT` |
| A-TYP-016 | 延后 | 可实现 | baseline § A-TYP-016 / XML native IT |
| A-TYP-017 | 延后 | 可实现 | geometric known-limit IT |
| A-TYP-018 | 延后 | 可实现 | UDT known-limit IT |
| A-FUN-020 | 延后 | 可实现 | covered-live geometric functions IT |
| A-FUN-021 | 延后 | 可实现 | **known-limit-documented** XMLTABLE; ≠ json_table |
| A-XCUT-012 | 延后 | 延后 (unchanged) | Ship anchor |

### Baseline (`contracts/production-regression-baseline.md`)

- Demo smoke: validate / Function-HQL / bulk → **covered-live** (I-006/I-007); removed false **gap**
- I-009 inventory summary: **6 / 13 / 1** (was stale 7 / 12); A-FUN-021 row remains **known-limit-documented**

## Docs changes (docs role paths)

- `docs/user-guide/03-verify.md` — I-009 6/13 + A-FUN-021 known-limit call-out
- `docs/user-guide/04-feature-matrix.md` — same + A-FUN-021 example row
- `docs/user-guide/05-troubleshooting.md` — §13 ENCRYPT/PARTITION/catalog; §14 INTERVAL/XML/geometry/UDT; §15 A-FUN-021/XMLTABLE
- `docs/user-guide/README.md` — I-009 count line
- `docs/feature-matrix-definition-a.md` — cross-link refresh

## Residual risks for reviewer

1. Historical I-009 evidence under `harness/evidence/**/I-009/**` may still say A-FUN-021 covered-live / 7/12 — **out of P-001 rewrite scope**; current SSOT is baseline + refreshed user-guide.
2. Definition A **可实现** ≠ baseline **covered-live**; known-limit rows stay 可实现 with acceptance hint — intentional legend split.
3. Reviewer RP-03 still required before Phase Accept.

## Next

- RP-03 reviewer (readonly)
- Orchestrator: must-commit SHA already requested by Human Gate for this Phase
