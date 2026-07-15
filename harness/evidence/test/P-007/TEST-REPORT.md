# P-007 Test Report (independent test role)

> Phase: `P-007`  
> Initiative: `I-001`  
> Build: `B-007`  
> Invocation: `test-p007-20260715`  
> Role: `test` (independent context from implementer)  
> Verdict: **PASS**  
> Date: 2026-07-15

## Environment

| Item | Value |
|---|---|
| Maven | Apache Maven 3.9.9 (`C:\Users\admin\tools\apache-maven-3.9.9\bin` prepended to PATH) |
| JDK | Oracle 21.0.1 |
| Working directory | `E:\Work\java\hibernate-test` |
| Branch | `feat/i-001-xugu-dialect-major` |
| HEAD (test time) | `12d9083e344e8d08fd034e917082a3843751e62c` |
| Product code changes by test | none |
| Live DB | XuguDB `jdbc:xugu://127.0.0.1:5138/SYSTEM?...&compatiblemode=NONE` (driver: XuguDB JDBC Driver; dialect: XuguDialect; version: 12.0) |

## Commands and exit codes

| # | Command | Exit code | Result |
|---|---|---|---|
| 1 | `mvn -q test` (gate default/off) | 0 | PASS (14 IT skipped) |
| 2 | `mvn -q test -Dxugu.run.integration=true` | 0 | PASS (14 IT executed on real XuguDB) |
| 3 | `python harness/scripts/verify.py --phase P-007 --evidence harness/evidence/test/P-007/verification.json` | 0 | `VERIFY PASS` |

## Surefire counts

### Offline (`xugu.run.integration=false`)

| Suite | tests | failures | errors | skipped |
|---|---:|---:|---:|---:|
| `XuguDialectTest` | 7 | 0 | 0 | 0 |
| `XuguFunctionRegistryTest` | 5 | 0 | 0 | 0 |
| `XuguIdentitySequenceTest` | 6 | 0 | 0 | 0 |
| `XuguPaginationLockTest` | 10 | 0 | 0 | 0 |
| `XuguSchemaTempCommentTest` | 7 | 0 | 0 | 0 |
| `XuguTypeRoundTripIT` | 4 | 0 | 0 | 4 |
| `XuguDdlIT` | 1 | 0 | 0 | 1 |
| `XuguBinarySchemaExportIT` | 1 | 0 | 0 | 1 |
| `XuguPaginationIT` | 1 | 0 | 0 | 1 |
| `XuguLockIT` | 2 | 0 | 0 | 2 |
| `XuguIdentitySequenceIT` | 2 | 0 | 0 | 2 |
| `XuguFunctionRegistryIT` | 2 | 0 | 0 | 2 |
| `XuguSchemaTempCommentIT` | 1 | 0 | 0 | 1 |
| **Total** | **49** | **0** | **0** | **14** |

### Integration gate ON (real XuguDB)

| Suite | tests | failures | errors | skipped |
|---|---:|---:|---:|---:|
| `XuguDialectTest` | 7 | 0 | 0 | 0 |
| `XuguFunctionRegistryTest` | 5 | 0 | 0 | 0 |
| `XuguIdentitySequenceTest` | 6 | 0 | 0 | 0 |
| `XuguPaginationLockTest` | 10 | 0 | 0 | 0 |
| `XuguSchemaTempCommentTest` | 7 | 0 | 0 | 0 |
| `XuguTypeRoundTripIT` | 4 | 0 | 0 | 0 |
| `XuguDdlIT` | 1 | 0 | 0 | 0 |
| `XuguBinarySchemaExportIT` | 1 | 0 | 0 | 0 |
| `XuguPaginationIT` | 1 | 0 | 0 | 0 |
| `XuguLockIT` | 2 | 0 | 0 | 0 |
| `XuguIdentitySequenceIT` | 2 | 0 | 0 | 0 |
| `XuguFunctionRegistryIT` | 2 | 0 | 0 | 0 |
| `XuguSchemaTempCommentIT` | 1 | 0 | 0 | 0 |
| **Total** | **49** | **0** | **0** | **0** |

**IT summary:** 14 IT methods executed when gate ON, 0 failed, 0 skipped. Offline: 14 IT skipped via `Assumptions.assumeTrue(XuguITGate.isEnabled())`. Unit: 35 passed both runs (`7+5+6+10+7`). P-007 focused: `XuguSchemaTempCommentIT` 1/1 PASS; `XuguSchemaTempCommentTest` 7/7 PASS.

## Project verify evidence

- Path: `harness/evidence/test/P-007/verification.json`
- Overall status: `PASS`
- Required checks: `build` PASS (exit 0), `test` PASS (exit 0)
- Optional: `lint` NOT_APPLICABLE
- Harness check embedded: `HARNESS_CHECK PASS`

## Spot-check (schema / temp / comment / FK)

| Capability | Expected / locked | Unit | Live IT | Result |
|---|---|---|---|---|
| Schema create/drop | `create schema` / `drop schema` | `getCreateSchemaCommand` / `getDropSchemaCommand` | CREATE `HIB_P007_SCH` + assert exists; final `DROP SCHEMA … CASCADE` | PASS |
| Qualified names | `schema.table` (`NameQualifierSupport.SCHEMA`) | qualifier = SCHEMA | `HIB_P007_SCH.HIB_P007_QT` insert/select | PASS |
| Local temp + ON COMMIT | `create local temporary table` + `on commit preserve rows` | strategy constants | create/insert/select/drop `HIB_P007_LTMP` | PASS |
| Global temp | `create global temporary table` + `on commit delete rows`; precondition `support_global_tab=ON` | strategy constants | live create/drop `HIB_P007_GTMP` succeeded on this server | PASS |
| A-SCH-007 no temp FK | **Do not emit** FK on temp | `StandardTemporaryTableExporter`; create fragment has no `foreign key`/`references` | IT `assertFalse(localCreate contains "foreign key")` | PASS |
| A-SCH-003 catalog | 延后 | `NameQualifierSupport.SCHEMA` only | matrix row 延后 / later — no catalog claim | PASS |
| COMMENT ON / inline | `comment on table/column … is '…'`; inline ` comment '…'` | helpers + `supportsCommentOn` | CREATE with inline + COMMENT ON table/column | PASS |
| Permanent FK | `add constraint … foreign key (…) references …` | `getAddForeignKeyConstraintString` | parent/child + orphan insert rejected | PASS |
| UNIQUE / CHECK / TRUNCATE / INDEX | matrix locked forms | unit coverage | live UNIQUE reject, CHECK alter, truncate→0, create index | PASS |
| Cleanup `HIB_P007_*` | objects removed after IT | cleanup checklist in finally | final CASCADE OK; independent probe counts **0** | PASS |

## Observed affected flows

| Flow | Method | Expected | Observed | Result | Evidence |
|---|---|---|---|---|---|
| schema-tooling-real-db | `XuguSchemaTempCommentIT.schemaTempCommentFkTruncate_A_SCH` (schema + qualified name portion) | CREATE/DROP SCHEMA + `schema.table` on live XuguDB | Schema created/verified; qualified table R/W; final `DROP SCHEMA HIB_P007_SCH CASCADE` OK | PASS | `mvn-test-integration.log`, `IT-RESULT.txt`, `leftover-probe.txt` |
| temp-table-comments-fk-real-db | same IT (temp/comment/FK/UK/CHECK/truncate/index portion) | Temp/comment/FK per matrix; no temp FK; cleanup | Local+global temp; COMMENT ON; FK enforce; UNIQUE; CHECK; truncate; index; no temp FK; leftovers 0 | PASS | `mvn-test-integration.log`, `com.xugu.dialect.it.XuguSchemaTempCommentIT.txt`, `IT-RESULT.txt` |

## Readiness dimensions (test view)

| Dimension | Observation | Result |
|---|---|---|
| functional-correctness | Unit + real-DB IT pass for schema/temp/comment/FK/truncate/index | PASS |
| data-integrity | Permanent FK rejects orphan; UNIQUE rejects duplicate; cleanup leaves 0 `HIB_P007_*` | PASS |
| maintainability | IT gated; offline suite green without DB; cleanup checklist printed | PASS |
| compatibility | Hibernate 7.4 schema/temp SPI; `StandardTemporaryTableExporter` (no temp FK); no MySQL/Oracle Dialect inheritance in this Phase path | PASS |

## Residual notes

- Test role did not modify product code.
- DB unreachable with gate ON would be FAIL/blocker (no mock path); this run connected successfully.
- A-SCH-003 remains deferred (matrix 延后) — confirmed `NameQualifierSupport.SCHEMA` only.
- A-SCH-007 remains 文档不允许 — confirmed no FK on temp create path.
- A-SCH-017 advanced indexes remain deferred per implementer — out of required flows.
- Pre-body cleanup skips (objects absent) are expected; post-body cleanup succeeded.
- Next: RP-03 reviewer (risk_score=8, Full review required for destructive DDL / FK).

## Verdict

**PASS** — offline test / real-DB IT / verify green; required flows `schema-tooling-real-db` and `temp-table-comments-fk-real-db` evidenced; `HIB_P007_*` cleanup confirmed (probe 0); A-SCH-007 no temp FK and A-SCH-003 deferred confirmed.
