# P-008 Implementer Notes (DialectResolver SPI + explicit config)

**Invocation:** `impl-p008-20260715`  
**Role:** implementer  
**Phase / Build / Initiative:** P-008 / B-008 / I-001  
**Date:** 2026-07-15  
**Step:** RP-01  
**Accept / commit:** **NOT done** (await RP-02 test + RP-03 reviewer)

## Product-name match rule (live-proven)

Live JDBC `DatabaseMetaData` against `jdbc:xugu://127.0.0.1:5138/SYSTEM?...&compatiblemode=NONE`:

| Field | Observed value |
|---|---|
| `getDatabaseProductName()` | **`XuguDB`** |
| `getDatabaseProductVersion()` | `XuguDB 12.0.0` |
| `getDatabaseMajorVersion()` / minor | **12** / **0** |
| `getDriverName()` | **`XuguDB JDBC Driver`** |
| `getDriverVersion()` | `12.3.4` |

**Resolver match rule:** product name **or** driver name contains token `xugu` (case-insensitive).  
Non-match (return `null`): MySQL / Oracle / PostgreSQL fake metadata (A-SPI-004).

SPI FQCN (Hibernate 7.4.5 m2): `org.hibernate.engine.jdbc.dialect.spi.DialectResolver`  
Services file: `META-INF/services/org.hibernate.engine.jdbc.dialect.spi.DialectResolver` → `com.xugu.dialect.XuguDialectResolver`

## What was delivered

1. `XuguDialectResolver` + META-INF/services registration (A-SPI-002)
2. `XuguDialect(DatabaseVersion)` / `XuguDialect(DialectResolutionInfo)` (A-SPI-003)
3. Isolation docs + Dialect hooks for RC/RR; **no READ UNCOMMITTED claim** (A-XCUT-005/006)
4. `compatible_mode=NONE` documented; IT URL already uses `compatiblemode=NONE` (A-XCUT-003)
5. Unit: match/non-match + services resource / jar listing
6. Gated IT: explicit dialect + SPI auto-resolve + live metadata assertion

## Isolation mapping (A-XCUT-005 / A-XCUT-006)

XuGu `ISO_LEVEL` (`reference/.../iso_level.md`):

| Value | Level | Dialect claim |
|---|---|---|
| 1 (default) | READ COMMITTED | Supported |
| 2 | REPEATABLE READ | Supported |
| 3 | SERIALIZABLE | Supported |
| 0 | READ ONLY | XuGu-specific; not a Hibernate RU claim |
| — | READ UNCOMMITTED | **文档不允许 — not claimed** |

Hibernate Dialect hooks set: `doesReadCommittedCauseWritersToBlockReaders=false`, `doesRepeatableReadCauseReadersToBlockWriters=false`.

## compatible_mode (A-XCUT-003)

- IT / connection helper: `compatiblemode=NONE`
- No MySQL/Oracle compatible-mode dependency for SPI or explicit config

## Deferred / not done

- Accept / git commit this turn
- P-009+ demo / user-guide

## Cleanup checklist (IT)

Prefix: `HIB_P008_*`

| Object | Cleanup |
|---|---|
| `HIB_P008_PROBE` | `DROP TABLE IF EXISTS` (IT finally) |

Final IT cleanup: **OK**

## Commands

| Command | Exit |
|---|---|
| `mvn -q -pl dialect -DskipTests package` | **0** |
| `mvn -q test` | **0** |
| `mvn -q -pl dialect test -Dxugu.run.integration=true` | **0** |
| `python harness/scripts/verify.py --phase P-008` | **VERIFY PASS** |

## Observed flows

- `explicit-dialect-config`: SessionFactory with `hibernate.dialect=com.xugu.dialect.XuguDialect` + simple HQL query on live XuguDB
- `spi-dialect-resolver-autodetect`: SessionFactory **without** explicit dialect → SPI selects `XuguDialect` version 12.0
