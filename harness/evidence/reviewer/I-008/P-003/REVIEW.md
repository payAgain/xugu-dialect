# I-008 / P-003 — Reviewer Audit (RP-03)

> **Role:** reviewer  
> **invocation_id:** `inv-i008-p003-rp03-reviewer`  
> **Date:** 2026-07-20  
> **Branch:** `feat/i-008-production-quality-gaps`  
> **Audit HEAD (RP-02):** `079ab84` (+ uncommitted P-003 working tree)  
> **Mode:** readonly

## Verdict

**ACCEPT PASS**

## Criteria checklist

| # | Criterion | Result | Evidence |
|---|---|---|---|
| 1 | **Batch A:** 4 thin live IT are real (not silent SSOT rename); 15 known-limit with reasons | **PASS** | Four new/extended gated IT classes use `XuguITGate` + `XuguTestConnection` with substantive JDBC/SchemaExport assertions (see § Batch A thin IT). Fifteen rows → `known-limit-documented` with waiver table in `contracts/production-regression-baseline.md` § P-003 Batch A known-limit waivers; matches PROMOTION-MAP locks. |
| 2 | **Batch A′:** 58 tag sweeps only where live IT exists | **PASS** | All 58 PROMOTION-MAP matrix_ids are `covered-live` + gate `IT` in SSOT; IT classes pre-exist (`XuguTypeRoundTripIT`, `XuguDdlIT`, `XuguPaginationIT`, `XuguLockIT`, `XuguIdentitySequenceIT`, `XuguFunctionRegistryIT`, `XuguSchemaTempCommentIT`, `XuguDialectResolverIT`, etc.). Five rows alias bundles (`same as A-SCH-008/011/015`) — resolved to live IT anchors, not unit-only. No new tests added for A′ (honest tag sweep). |
| 3 | No MySQL/Oracle dialect inheritance | **PASS** | `XuguDialect extends Dialect` (not MySQL/Oracle). `MySQLDialect` import is static-helper only (`datetimeFormat`). Guard: `XuguNegativeRegressionBaselineTest#charterNoMySqlOracleInheritance_A_XCUT_010`. No P-003 edits to production dialect inheritance. |
| 4 | Offline `mvn test` PASS; live `SKIPPED_INFRA` honest if no DB | **PASS** | `verification.json` / `TEST-REPORT.md`: build + offline test exit 0 (189 dialect / 32 demo, 0 fail). Batch A gated IT skipped offline as expected. Live probe `127.0.0.1:5138` unreachable → `test_live_batch_a` **SKIPPED_INFRA** with JDBC *Connection refused* logged (`live-db-probe.txt`, `IT-RESULT.txt`). |
| 5 | GAV `7.4.5.Final` · `compatiblemode=NONE` | **PASS** | Root/dialect/demo `pom.xml` → `7.4.5.Final`. `XuguTestConnection` defaults `compatiblemode=NONE`; PROMOTION-MAP boundary lock unchanged. |

## Batch A thin IT — promotion audit

| matrix_id | IT anchor | Real live assertion (not rename) |
|---|---|---|
| A-TYP-003 | `XuguTypeRoundTripIT#jdbcRealFloatRoundTrip_A_TYP_003` | JDBC CREATE/INSERT/SELECT REAL round-trip on live connection |
| A-TYP-019 | `XuguCastPatternIT#castExpressionOnLiveDb_A_TYP_019` | Unit `castPattern` check + `SELECT CAST('42' AS INTEGER) FROM DUAL` on DB |
| A-DDL-005 | `XuguDefaultColumnExportIT#schemaExportEmitsDefaultColumnAndAppliesOnDb_A_DDL_005` | SchemaExport script asserts DEFAULT; INSERT omitting column proves default on DB |
| A-XCUT-001 | `XuguIdentifierFoldingIT#unquotedIdentifiersFoldToUppercase_A_XCUT_001` | CREATE with lowercase name; `USER_TABLES` metadata + SELECT via folded name |

All four: `Assumptions.assumeTrue(XuguITGate.isEnabled())` — consistent with existing dialect IT gate.

## Batch A known-limit (15)

SSOT status `known-limit-documented` for: A-IDN-001/002, A-SEQ-002/005, A-FUN-003/005/006/007/009, A-SCH-006, A-SPI-004, A-XCUT-002/005/007/008. Waiver reasons + live bundles documented in SSOT § P-003 Batch A known-limit waivers; aligned with PROMOTION-MAP Batch A locks.

## Count reconciliation

| Bucket | Expected (P-001 map) | Observed |
|---|---:|---:|
| Batch A thin live IT | 4 | 4 |
| Batch A known-limit | 15 | 15 |
| Batch A′ tag sweep | 58 | 58 |
| Closed (I-007) | 5 | unchanged |
| unit-only on Batch A rows | 0 | 0 |

SSOT rollup (`production-regression-baseline.md` Summary): honest live-capable **83/98**; `covered-live` tag **67** + **15** known-limit — not inflated to「94 covered-live」.

## Findings (non-blocking)

- **F-01:** Live PASS for the four new thin IT not observed in RP-02 (DB down). Promotions are structurally valid; re-run `XUGU_RUN_IT=true mvn -q -pl dialect test -Dtest=…` when XuGuDB is available before Ship-level live claims.
- **F-02:** P-003 artifacts (IT classes, SSOT diff, evidence) are **uncommitted** at audit time — Accept is on artifact quality; orchestrator must commit before Build Accept gate.
- **F-03:** Five A′ SSOT rows use `same as …` entry aliases — IT bundles resolve correctly; optional doc nit to inline anchors for grep clarity.

## Decision

`accepted` — promotions are real gated live IT or honestly waived known-limit; A′ tag sweep is IT-backed; forbidden inheritance respected; offline verification green; live infra honestly skipped.
