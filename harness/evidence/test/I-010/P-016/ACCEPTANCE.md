# ACCEPTANCE — I-010 P-016 RP-01 (test)

- **invocation_id:** inv-i010-p016-rp01-test
- **branch:** `feat/i-010-orm-hql-quality-completion`
- **completed_at:** 2026-07-21T16:36:00+08:00

## Acceptance checklist

| Criterion | Result | Evidence |
|---|---|---|
| 3–5 null cases gated IT | **PASS** | `XuguNullSemanticsIT` — 4 methods (IS NULL / IS NOT NULL / three-valued `=:null` / coalesce); offline gate-skip |
| Temporal projection gated IT (doc-allowed only) | **PASS** | `XuguTemporalProjectionIT` — year/month/day/extract/current_date/current_timestamp; seed 2024-03-15 |
| Lock path evidenced (live or unit+doc) | **PASS** | Unit strengthen `XuguExceptionConversionTest` (10 tests); no live IT — SSOT `covered-unit` + **live-unstable** |
| `mvn -q -pl dialect test` green | **PASS** | exit 0 |
| VERIFY PASS | **PASS** | `verification.json` |
| SSOT XP-008/009/010 updated | **PASS** | `contracts/xuguefcore-parity-suite.md` |

## Observed flows

- **i010-null-semantics** — IT present; offline skip; live SKIPPED_INFRA
- **i010-temporal-proj** — IT present; offline skip; live SKIPPED_INFRA
- **i010-lock-timeout** — Unit PASS (DEADLOCK→LockAcquisitionException; LOCK_TIMEOUT variants→LockTimeoutException)

## Verdict

**PASS** (offline). Live IT **SKIPPED_INFRA**. Awaiting reviewer RP-02. **NOT Ship.**
