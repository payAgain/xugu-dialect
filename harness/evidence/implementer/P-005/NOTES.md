# P-005 Implementer Notes (Identity & Sequence)

**Invocation:** `impl-p005-20260715`  
**Role:** implementer  
**Phase / Build / Initiative:** P-005 / B-005 / I-001  
**Date:** 2026-07-15  
**Step:** RP-01  
**Accept / commit:** **NOT done** (await RP-02 test + RP-03 reviewer)

## What was delivered

1. `com.xugu.dialect.identity.XuguIdentityColumnSupport` — wired via `XuguDialect.getIdentityColumnSupport()`.
2. `com.xugu.dialect.sequence.XuguSequenceSupport` — wired via `XuguDialect.getSequenceSupport()`.
3. Offline unit tests: `XuguIdentitySequenceTest`.
4. Gated IT: `XuguIdentitySequenceIT` (`-Dxugu.run.integration=true`).
5. Matrix acceptance hints updated for A-IDN-001..004, A-SEQ-001..005, A-XCUT-008.

## Chosen SQL forms (live-proven)

| Capability | Locked form | Evidence |
|---|---|---|
| IDENTITY DDL | `identity(1,1)` after type (e.g. `bigint identity(1,1)`) | Schema export IT; docs prefer IDENTITY over AUTO_INCREMENT in NONE |
| Identity insert | `INSERT … (name) VALUES (?)` omitting id | Hibernate SHOW_SQL + IT backfill |
| Generated keys (primary) | JDBC `Statement.RETURN_GENERATED_KEYS` / `getGeneratedKeys()` | Live probe + Hibernate persist; XuGu JDBC docs |
| Identity select fallback | `select last_insert_id() from dual` | Documented `LAST_INSERT_ID`; live probe OK |
| NEXTVAL | `select <seq>.nextval from dual` | Live: OK; `NEXTVAL('seq')` function **FAIL** (internal/not usable) |
| CURRVAL | `select currval('<name>') from dual` | Live: OK after NEXTVAL; `seq.currval` **FAIL** |
| FROM DUAL | `getFromDual()` → ` from dual` | Required for SequenceSupport select strings (A-XCUT-008) |
| CREATE SEQUENCE | `create sequence name start with N increment by M` | Hibernate SequenceSupport default; matches docs; IT export |
| DROP SEQUENCE | `drop sequence name` | IT export |

### Live probe summary (`compatiblemode=NONE`)

| Probe | Result |
|---|---|
| `getGeneratedKeys` + `RETURN_GENERATED_KEYS` | OK (id returned) |
| `getGeneratedKeys` + column name array | OK |
| `SELECT LAST_INSERT_ID() FROM DUAL` | OK |
| `SELECT seq.NEXTVAL FROM DUAL` | OK |
| `SELECT seq.NEXTVAL` (no DUAL) | OK (not locked; dialect uses DUAL for Hibernate) |
| `SELECT NEXTVAL('seq') FROM DUAL` | **FAIL** E10049 |
| `SELECT CURRVAL('seq') FROM DUAL` | OK |
| `SELECT seq.CURRVAL FROM DUAL` | **FAIL** E10049 |

## Identity retrieval strategy

1. **Primary:** JDBC `getGeneratedKeys` (`Dialect.getDefaultUseGetGeneratedKeys()==true`; IT sets `USE_GET_GENERATED_KEYS=true`).
2. **Fallback SQL:** `getIdentitySelectString` → `select last_insert_id() from dual` (documented; not invented).
3. **A-IDN-005** identity_mode / def_identity_mode: **deferred** (matrix 延后); default insert-omit-column path works under NONE without session knobs.

## Sequence options (A-SEQ-005)

Hibernate `SequenceSupport` maps **START WITH** / **INCREMENT BY** only. MINVALUE / MAXVALUE / CACHE / CYCLE are documented on XuGu but not exposed by Hibernate 7.4 `SequenceSupport` API — N/A via dialect hooks; apps can DDL manually.

## Explicitly NOT done / forbidden

- No MySQL/Oracle `Dialect` inheritance
- No sibling `hibernate-dialect` port
- No inventing undocumented NEXTVAL() binary API
- No Accept / git commit this turn
- A-IDN-005 / A-SEQ-006 remain deferred

## Commands

| Command | Exit |
|---|---|
| `mvn -q test` | **0** |
| `mvn -q test -Dxugu.run.integration=true` | **0** |
| `python harness/scripts/verify.py --phase P-005` | see verification.json |

## Observed flows

- `identity-insert-real-db`: CREATE `… identity(1,1)`; INSERT omit id; id backfilled; cleanup DROP
- `sequence-generator-real-db`: CREATE SEQUENCE; Hibernate `select seq.nextval from dual`; persist two entities with increasing ids; DROP table + sequence
