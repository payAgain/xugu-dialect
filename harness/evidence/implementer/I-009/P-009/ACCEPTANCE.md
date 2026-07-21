# P-009 Acceptance Evidence (Implementer RP-01)

> Phase: `P-009` · Initiative: `I-009` · Build: `B-001`  
> Role: implementer · Matrix rows: **A-LCK-006**, **A-PAG-004**, **A-PAG-006**, **A-IDN-005**

## Decision

- Decision: `accepted`
- Path:
  - **A-LCK-006:** `covered-live` (native `LOCK TABLE` helper + gated IT)
  - **A-PAG-004:** `known-limit-documented` (TOP alternate; LimitHandler stays LIMIT)
  - **A-PAG-006:** `known-limit-documented` (ROWNUM wrappers; LIMIT remains default)
  - **A-IDN-005:** `covered-live` (session `IDENTITY_MODE` SQL + gated IT)
- Rationale: Doc-backed native SQL helpers per XuGu lock/pagination/identity docs; no SKIP LOCKED / FOR SHARE / ANSI FETCH invented; Hibernate pagination path unchanged.

## Criteria table

| Criterion | Expected | Evidence | Status |
|---|---|---|---|
| A-LCK-006 LOCK TABLE | `lock.md` shapes locked | `XuguLockTableSupport`; unit + IT | **PASS** |
| A-PAG-004 TOP | Alternate only; LIMIT default | `XuguPaginationAlternativesSupport#selectTopSql`; `usesTopPaginationInOrmPath()=false` | **PASS** |
| A-PAG-006 ROWNUM | Subquery wrappers; LIMIT default | `XuguPaginationAlternativesSupport`; `usesRownumPaginationInOrmPath()=false` | **PASS** |
| A-IDN-005 IDENTITY_MODE | SET/SHOW session param | `XuguIdentityModeSupport`; gated IT NULL/ZERO modes | **PASS** |
| Doc-forbidden unchanged | No SKIP LOCKED/FOR SHARE/FETCH | `XuguNegativeRegressionBaselineTest` P-003 bundle | **PASS** |
| Negative anchors removed | No @Disabled defer for P-009 rows | `XuguNegativeRegressionBaselineTest` | **PASS** |
| SSOT promotion | 延后 → closed statuses | `contracts/production-regression-baseline.md`; `contracts/feature-matrix-definition-a.md` | **PASS** |
| Offline build | `mvn -q -DskipTests package` green | implementer run | **PASS** (exit 0) |
| Offline test | `mvn -q test` green | implementer run | **PASS** (exit 0) |

## Validation (implementer)

| Command | Exit | Detail |
|---|---|---|
| `mvn -q -DskipTests package` | 0 | offline build |
| `mvn -q test` | 0 | full reactor (IT gated/skipped offline) |
| `mvn -q -pl dialect -am test -Dtest=XuguLockPaginationIdentityExtensionsTest,XuguLockPaginationIdentityIT` | 0 | 5 unit + 4 IT skipped offline |

## Closed gaps

| matrix_id | status | entry_class#method | gate |
|---|---|---|---|
| A-LCK-006 | covered-live | `XuguLockPaginationIdentityExtensionsTest#lockTableSqlMatchesLockDoc_A_LCK_006`; `XuguLockPaginationIdentityIT#lockTableExclusiveNativeRoundTrip_A_LCK_006` | IT |
| A-PAG-004 | known-limit-documented | `XuguLockPaginationIdentityExtensionsTest#topSqlMatchesResultsetRestrictedDoc_A_PAG_004`; `XuguLockPaginationIdentityIT#topSyntaxNativeRoundTrip_A_PAG_004` | IT |
| A-PAG-006 | known-limit-documented | `XuguLockPaginationIdentityExtensionsTest#rownumSqlMatchesSelectDoc_A_PAG_006`; `XuguLockPaginationIdentityIT#rownumPaginationNativeRoundTrip_A_PAG_006` | IT |
| A-IDN-005 | covered-live | `XuguLockPaginationIdentityExtensionsTest#identityModeSqlMatchesIdentityModeDoc_A_IDN_005`; `XuguLockPaginationIdentityIT#identityModeNullAsAutoIncrement_A_IDN_005` | IT |

## Known-limit notes

### A-PAG-004 / A-PAG-006
- `XuguLimitHandler` remains Hibernate default pagination (`LIMIT` / `LIMIT count OFFSET offset`).
- TOP and ROWNUM are native SQL alternates only — doc: TOP cannot combine with LIMIT.

### A-LCK-006
- Explicit table locks are separate from SELECT `FOR UPDATE` (A-LCK-001…003).
- Not wired to JPA `LockMode`.

## Doc citation

- `reference/object/table/lock.md` — LOCK TABLE IN … MODE, NOWAIT/WAIT
- `reference/sql/select/resultset-restricted.md` — TOP
- `reference/sql/select/select.md` — opt_top, §8.3 ROWNUM
- `reference/system-configuration-parameter/session-parameter/identity_mode.md` — IDENTITY_MODE session values

## Files changed

- `dialect/src/main/java/com/xugu/dialect/lock/XuguLockTableSupport.java`
- `dialect/src/main/java/com/xugu/dialect/pagination/XuguPaginationAlternativesSupport.java`
- `dialect/src/main/java/com/xugu/dialect/identity/XuguIdentityModeSupport.java`
- `dialect/src/main/java/com/xugu/dialect/XuguDialect.java`
- `dialect/src/test/java/com/xugu/dialect/XuguLockPaginationIdentityExtensionsTest.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguLockPaginationIdentityIT.java`
- `dialect/src/test/java/com/xugu/dialect/XuguNegativeRegressionBaselineTest.java`
- `contracts/production-regression-baseline.md`
- `contracts/feature-matrix-definition-a.md`
- `harness/evidence/implementer/I-009/P-009/ACCEPTANCE.md`
- `harness/handoffs/implementer/I-009-P-009.yaml`
