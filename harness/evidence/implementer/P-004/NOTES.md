# P-004 Implementer Notes (pagination + locks)

**Invocation:** `impl-p004-fix-20260715` (fix after `rev-p004-20260715` request-changes)  
**Prior invocation:** `impl-p004-20260715`  
**Role:** implementer  
**Phase / Build / Initiative:** P-004 / B-004 / I-001  
**Date:** 2026-07-15  
**Step:** RP-01b  
**Accept / commit:** **NOT done** (await re-test + re-review)

## What was delivered (original RP-01)

1. `com.xugu.dialect.pagination.XuguLimitHandler` — `LIMIT ?` / `LIMIT ? OFFSET ?` with JDBC bind markers; wired via `XuguDialect.getLimitHandler()`.
2. Lock clause methods on `XuguDialect` + `XuguLockingSupport` (`LockingSupportParameterized`: wait/nowait yes, skipLocked **false**).
3. Offline unit tests: `XuguPaginationLockTest` (SQL fragment generation).
4. Gated IT: `XuguPaginationIT`, `XuguLockIT` (`-Dxugu.run.integration=true`).
5. Matrix acceptance hints updated for A-PAG-*/A-LCK-* rows.

## Fix loop (`impl-p004-fix-20260715`) — MAJOR from `rev-p004-20260715`

### MAJOR 1 — A-LCK-005 limitations (documentation) — DONE

Clarified in `XuguDialect` class + `getReadLockString` javadoc, this NOTES section, and matrix acceptance hint:

- **Hibernate shim only** — XuGu has no `FOR SHARE`; matrix status remains **文档不允许** for the FOR SHARE surface.
- **NOT share-lock support** — `PESSIMISTIC_READ` / `getReadLockString` maps to **exclusive** `FOR UPDATE` semantics.
- **Concurrent readers may block** — applications must **not** assume PostgreSQL-style `FOR SHARE` / non-blocking concurrent reads.

### MAJOR 2 — LIMIT + FOR UPDATE order (live proof + strategy adjust) — DONE

Live Xugu under `compatiblemode=NONE` rejects Hibernate’s default `LIMIT … FOR UPDATE`. Docs (`select_no_parens`: `opt_for_update_clause? opt_select_limit?`) and probe require **FOR UPDATE before LIMIT**; when WAIT is also present, order is **FOR UPDATE … LIMIT … WAIT**.

**Strategy change:** `XuguLimitHandler.insert` no longer uses `insertBeforeForUpdate`. It appends LIMIT at end, or inserts LIMIT **before** a trailing `NOWAIT`/`WAIT` clause.

#### Observed SQL / result (real DB IT)

| Combo | Assembled / probed SQL | Result |
|---|---|---|
| Hibernate-default LIMIT→FOR UPDATE | `select id from HIB_P004_LOCK order by id limit 1 for update` | **FAIL** (syntax: unexpected FOR) |
| FOR UPDATE → LIMIT (dialect) | `select id from HIB_P004_LOCK order by id for update limit ?` | **OK** (IT PASS) |
| FOR UPDATE → LIMIT → WAIT (dialect) | `select id from HIB_P004_LOCK order by id for update limit ? wait 2000` | **OK** (IT PASS) |
| FOR UPDATE WAIT → LIMIT (rejected) | `… for update wait 2000 limit 1` | **FAIL** (unexpected LIMIT) — avoided by handler |

Gated IT: `XuguLockIT.limitForUpdateComboExecutes`.

## Stable pagination form (A-PAG-002)

**Choice: `LIMIT count OFFSET offset`** (not `LIMIT offset, count`).

- Both forms are documented in `E:\Work\docs\content\reference\sql\select\resultset-restricted.md`.
- Matches Hibernate `LimitOffsetLimitHandler` bind semantics: `bindLimitParametersInReverseOrder=true` binds count then offset.
- Does **not** emit `FETCH FIRST` (A-PAG-005).

## Lock timeout mapping (A-LCK-003)

| Hibernate | XuGu SQL token | Notes |
|---|---|---|
| `Timeout` / LockOptions ms | `WAIT wait_ms` | XuGu docs: **milliseconds**; Hibernate `Timeout.milliseconds()` also ms → **1:1 pass-through** (no `getTimeoutInSeconds`) |
| `Timeouts.NO_WAIT` / `0` | `NOWAIT` | Appended after `FOR UPDATE` (and after LIMIT when paginated) |
| `Timeouts.WAIT_FOREVER` | (omit wait) | Default forever wait |
| `Timeouts.SKIP_LOCKED` | — | **Not supported**; never emit `SKIP LOCKED` |

Docs also place `opt_wait` on parenthesized selects: `(select_no_parens) NOWAIT|WAIT`. Non-paginated IT tries inline form first, falls back to parenthesized form if needed.

## Explicitly NOT done / forbidden

- No `SKIP LOCKED` (A-LCK-004); `supportsSkipLocked=false`
- No `FOR SHARE` (A-LCK-005); pessimistic read upgrades to exclusive `FOR UPDATE` (**shim only**, not share)
- No `FETCH FIRST` (A-PAG-005)
- No MySQL/Oracle Dialect inheritance; no sibling `hibernate-dialect` read
- No git commit / Accept (await independent re-test + re-review)

## Performance / capacity (readiness)

LimitHandler appends a constant clause + two bind markers — no SQL string concatenation of limit values; offset-only uses documented max `2147483647`. Large offsets inherit XuGu engine cost (matrix note); no dialect-side buffering.

## Commands (fix loop)

| Command | Exit |
|---|---|
| `mvn -q test` (IT skipped) | **0** |
| `mvn -q test -Dxugu.run.integration=true` | **0** (26 tests; includes `limitForUpdateComboExecutes`) |

## Observed flows

- `limit-offset-pagination-real-db`: LIMIT 3 → ids 1,2,3; LIMIT 3 OFFSET 4 → ids 5,6,7
- `pessimistic-lock-sql-real-db`: `FOR UPDATE` / `FOR UPDATE OF` execute; NOWAIT/WAIT smoke; `supportsSkipLocked=false`
- `limit-for-update-combo-real-db`: `FOR UPDATE limit ?` OK; `FOR UPDATE limit ? wait 2000` OK; `limit … for update` rejected
