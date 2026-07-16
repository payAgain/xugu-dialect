# P-006 Test Report (RP-02)

| Field | Value |
|-------|-------|
| Initiative | I-003 |
| Build | B-006 |
| Phase | P-006 — Type/DDL details |
| Role | test / RP-02 |
| Branch | feat/i-003-production-capability-parity |
| HEAD (at run) | 03b58865aa4a55fd4c0cb53ca3c41fbeeeec1daf |
| invocation_id | test-p006-20260716 |
| Completed | 2026-07-16T17:16:00+08:00 |

## Scope

Independent verification of implementer delivery: `XuguDialect` Type/DDL details (IF NOT EXISTS, ALTER COLUMN, datetime literals/format, ENUM null, catalog create/drop, select sys_guid), `XuguTypeDdlDetailsTest` (offline), `XuguTypeDdlDetailsIT` (live XuguDB when IT gate ON).

## Command matrix

| Step | Command | Exit |
|------|---------|------|
| Build | `mvn -q -DskipTests package` | 0 |
| Unit/offline tests (1st) | `mvn -q test` | 1 — stale assert in `XuguDialectTest.ddlHelpersMatchXuguSyntax` expected `create table` |
| Test-only fix | `XuguDialectTest` expect `create table if not exists` (C-DDL-001) | — |
| Unit/offline tests (re-run) | `mvn -q test` | 0 |
| Integration tests | `mvn -q test "-Dxugu.run.integration=true"` | 0 |
| Verify | `python harness/scripts/verify.py --phase P-006 --evidence harness/evidence/test/P-006/verification.json` | 0 — **VERIFY PASS** |
| Harness check | `python harness/scripts/harness_check.py` | 0 |
| Branch check | `python harness/scripts/branch_check.py` | 0 |

### PowerShell note

Unquoted `-Dxugu.run.integration=true` is parsed as a lifecycle phase. Effective integration run used quoted property: `"-Dxugu.run.integration=true"`.

## Observed flows

### C-DDL-001 — CREATE TABLE IF NOT EXISTS

| Check | Result | Evidence |
|-------|--------|----------|
| Offline | PASS | `XuguTypeDdlDetailsTest.createTableIfNotExists_C_DDL_001` |
| Live SchemaExport | PASS | `XuguTypeDdlDetailsIT.typeDdlDetailsOnLiveDb` (CREATE script contains `if not exists`) |

### C-DDL-002 — ALTER COLUMN type

| Check | Result | Evidence |
|-------|--------|----------|
| Offline | PASS | `XuguTypeDdlDetailsTest.alterColumnType_C_DDL_002` |
| Live ALTER integer→varchar | PASS | `XuguTypeDdlDetailsIT.typeDdlDetailsOnLiveDb` |

### C-DDL-003 — datetime format / literals

| Check | Result | Evidence |
|-------|--------|----------|
| Offline | PASS | `XuguTypeDdlDetailsTest.datetimeLiteralAndFormat_C_DDL_003` |
| Live HQL timestamp literal + to_char | PASS | `XuguTypeDdlDetailsIT.typeDdlDetailsOnLiveDb` |

### C-DDL-004 — ENUM declaration null

| Check | Result | Evidence |
|-------|--------|----------|
| Offline | PASS | `XuguTypeDdlDetailsTest.enumTypeDeclarationIsNull_C_DDL_004` (`getEnumTypeDeclaration` → null) |

### C-DDL-005

**Skipped** — deferred per matrix / implementer authorization.

### C-CAT-001 — CREATE/DROP DATABASE catalog

| Check | Result | Evidence |
|-------|--------|----------|
| Offline | PASS | `XuguTypeDdlDetailsTest.catalogCreateDrop_C_CAT_001` |
| Live CREATE/DROP `HIB_I003_P006_CAT` | PASS | `XuguTypeDdlDetailsIT.typeDdlDetailsOnLiveDb` |

### C-GUID-001 — select sys_guid()

| Check | Result | Evidence |
|-------|--------|----------|
| Offline | PASS | `XuguTypeDdlDetailsTest.selectGuidString_C_GUID_001` |
| Live Session native query | PASS | `XuguTypeDdlDetailsIT.typeDdlDetailsOnLiveDb` |

Surefire (integration gate ON): `TEST-com.xugu.dialect.it.XuguTypeDdlDetailsIT.xml` — 1 test, 0 failures; `TEST-com.xugu.dialect.XuguTypeDdlDetailsTest.xml` — 6 tests, 0 failures.

## Product code changes by test

None. Test-only: aligned `XuguDialectTest.ddlHelpersMatchXuguSyntax` with C-DDL-001.

## Verdict

**PASS** — all required commands succeeded; VERIFY PASS; Type/DDL flows confirmed as above.
