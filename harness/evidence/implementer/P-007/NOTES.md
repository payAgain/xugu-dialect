# P-007 Implementer Notes (schema / temp / comment / FK)

**Invocation:** `impl-p007-20260715`  
**Role:** implementer  
**Phase / Build / Initiative:** P-007 / B-007 / I-001  
**Date:** 2026-07-15  
**Step:** RP-01  
**Accept / commit:** **NOT done** (await RP-02 test + RP-03 reviewer)

## What was delivered

1. `XuguLocalTemporaryTableStrategy` / `XuguGlobalTemporaryTableStrategy`
2. `XuguDialect` overrides: schema create/drop, `NameQualifierSupport.SCHEMA`, temp strategies, COMMENT ON, UNIQUE/FK/CHECK, truncate, CREATE INDEX
3. Offline unit: `XuguSchemaTempCommentTest`
4. Gated IT: `XuguSchemaTempCommentIT` (`HIB_P007_*` prefix + cleanup checklist)
5. Matrix acceptance hints updated for A-SCH-001..016 (003 deferred, 007 文档不允许)

## Locked SQL forms

| ID | Locked form |
|---|---|
| A-SCH-001 | `create schema {name}` / `drop schema {name}` |
| A-SCH-002 | qualifier = schema only (`schema.table`); no catalog |
| A-SCH-004 | `create local temporary table` |
| A-SCH-005 | `create global temporary table` (**precondition:** `support_global_tab=ON`) |
| A-SCH-006 | local: `on commit preserve rows`; global: `on commit delete rows` |
| A-SCH-007 | **NOT emitted** — no FK on temp DDL |
| A-SCH-008 | `comment on table {t} is '{c}'` |
| A-SCH-009 | `comment on column {t}.{c} is '{c}'` |
| A-SCH-010 | alternate inline: ` comment '{c}'` via `inlineTableComment` / `inlineColumnComment` |
| A-SCH-011 | `CreateTableUniqueDelegate` (CREATE / ALTER UNIQUE) |
| A-SCH-012 | ` add constraint {n} foreign key ({cols}) references {table} ({pk})` |
| A-SCH-013 | `supportsColumnCheck` / `supportsTableCheck` = true |
| A-SCH-014 | `drop constraint` (FK/UK) |
| A-SCH-015 | `truncate table {name}` |
| A-SCH-016 | `create index` / `create unique index` |

## Global temp precondition (A-SCH-005)

Server parameter `support_global_tab` default **FALSE**.  
Docs: `reference/system-configuration-parameter/xugu.ini/sql-engine/support_global_tab.md`.  
Dialect always exposes global DDL strings; live IT runs CREATE GLOBAL TEMP only when parameter is ON (probe via `SHOW` / trial CREATE). Live run observed global temp create/use/drop succeeded on this server.

## A-SCH-007 (文档不允许)

Temporary table CREATE path uses Hibernate `StandardTemporaryTableExporter` (columns only). Unit asserts temp create fragments contain no `foreign key` / `references`.

## Deferred / not done

- A-SCH-003 catalog qualifier — 延后
- A-SCH-017 advanced indexes — 延后
- No Accept / git commit this turn

## Cleanup checklist (IT)

Prefix: `HIB_P007_*`

| Object | Cleanup |
|---|---|
| `HIB_P007_CHILD` / `PARENT` | `DROP TABLE IF EXISTS` (child first) |
| `HIB_P007_CMT` / `UK` / `TR` / `IDX` | `DROP TABLE IF EXISTS` |
| `HIB_P007_I1` | `DROP INDEX IF EXISTS HIB_P007_IDX.HIB_P007_I1` |
| `HIB_P007_LTMP` / `GTMP` | `DROP TABLE IF EXISTS` |
| `HIB_P007_SCH.HIB_P007_QT` | `DROP TABLE IF EXISTS schema.table` |
| `HIB_P007_SCH` | `DROP SCHEMA HIB_P007_SCH CASCADE` (no IF EXISTS in XuGu) |

Final IT cleanup: **OK** (schema CASCADE drop succeeded; residual `DROP SCHEMA` without CASCADE skipped as already gone).

## Commands

| Command | Exit |
|---|---|
| `mvn -q test` | **0** |
| `mvn -q test -Dxugu.run.integration=true` (dialect) | **0** |
| `python harness/scripts/verify.py --phase P-007` | see verification.json |

## Observed flows

- `schema-tooling-real-db`: CREATE/DROP SCHEMA + qualified `schema.table` on live XuguDB
- `temp-table-comments-fk-real-db`: local temp + ON COMMIT; COMMENT ON; FK parent/child; UNIQUE; CHECK; TRUNCATE; CREATE INDEX; cleanup
