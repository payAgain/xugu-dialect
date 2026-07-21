# ACCEPTANCE — I-010 / P-009 (RP-01 docs)

> Role: docs · Phase: **P-009 Schema tooling 文档配方**
> Branch: `feat/i-010-orm-hql-quality-completion`
> Base tip: `0b83a3d` (P-008)
> Completed: 2026-07-21T15:55:00+08:00

## Decision

- **accepted** (docs RP-01 complete; awaiting reviewer RP-02)
- Recipes document PARTITION / ENCRYPT / advanced indexes (+ UDT CREATE TYPE cross-link)
- Emphasize Flyway + native Support assembly; **hbm2ddl / SchemaExport never emits** these
- **No** SchemaExport / dialect Java changes (doc-only)
- Env ENCRYPT privilege boundary kept **separate** from dialect export known-limit

## Checklist

| Acceptance item | Evidence | Result |
|---|---|---|
| Recipes in user-guide with matrix IDs | `docs/user-guide/08-schema-tooling-recipes.md` (A-DDL-008/009, A-SCH-017, A-TYP-018) | **PASS** |
| Flyway / Support assembly emphasized | § Assembly model + Recipes A–D; cross-link B-FLY-001 | **PASS** |
| hbm2ddl/SchemaExport never emits | Explicit table + per-recipe “不要” notes; flags named | **PASS** |
| No forced SchemaExport code change | Diff limited to `docs/user-guide/**` + evidence/handoffs | **PASS** |
| A-TYP-014/016/017 wording refresh | `04-feature-matrix.md` examples; `05-troubleshooting.md` §14 | **PASS** |
| ENCRYPT env ≠ dialect known-limit | Recipe B two-layer table; §13 short note | **PASS** |

## Delivered files

- `docs/user-guide/08-schema-tooling-recipes.md` (**new**)
- `docs/user-guide/README.md` (index + quick-start step 8)
- `docs/user-guide/04-feature-matrix.md` (A-TYP-014/016/017/018 + tooling rows)
- `docs/user-guide/05-troubleshooting.md` (§13 pointer + §14 I-010 refresh)
- `harness/evidence/docs/I-010/P-009/ACCEPTANCE.md`
- `harness/handoffs/docs/I-010-P-009.yaml`

## Observed flow

| Flow | Result | Notes |
|---|---|---|
| `schema-tooling-recipes` | **PASS** | Dedicated guide + troubleshooting pointer; matrix IDs wired |

## Verification

| Command | Result |
|---|---|
| `python harness/scripts/harness_check.py` | **HARNESS_CHECK PASS** (level=Standard) |
| `python harness/scripts/branch_check.py` | **BRANCH_CHECK PASS** (`feat/i-010-orm-hql-quality-completion`) |

Required per packet: `harness_check` only (docs phase).

## Residual for reviewer (RP-02)

1. Confirm **no** dialect/SchemaExport Java slipped into the commit.
2. Confirm ENCRYPT wording keeps **env/privilege skip** separate from **export never-emits** known-limit.
3. Confirm A-TYP-014/016/017 text says entity ORM exists + **covered-live pending** (not fake covered-live).
4. Confirm recipes do not invent SQL beyond Support / documented shapes.

## Version control

- Intended commit message: `docs(I-010/P-009): schema tooling recipes PARTITION ENCRYPT indexes`
- NOT Ship / NOT push / NOT tag
