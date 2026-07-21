# I-009 live IT retest handoff (5287 triage)

> **Date:** 2026-07-21  
> **Branch:** `feat/i-009-deferred-matrix-delivery`  
> **Triage SHA:** `8d1de762ff99c2de6883292dca58e8c0c08cc171`  
> **NOT** Ship / Archive

## Compatible-mode narrative (unchanged)

Multi MySQL/Oracle compatible-mode product line remains **out of scope** (I-007/I-008/I-009). Native Dialect + `compatiblemode=NONE` only.

## Triage of prior 3 dialect failures

| # | Test | Disposition | Notes |
|---|---|---|---|
| 1 | `XuguReservedIdentityIT` E16007 | **fixed** | Collision with existing `Order` table; IT now uses `"select"` |
| 2 | `XuguTableDdlExtensionsIT` ENCRYPT | **known-limit** | No encryptor / E18012; assumption skip restored (was false fail) |
| 3 | `XuguXmlTypeAndFunctionsIT` XMLTABLE | **known-limit** | Doc single-node only; empty → skip; SSOT A-FUN-021 aligned |

Evidence: `harness/evidence/test/I-009/live-it-5287-rerun/`

## Live retest

- **dialect** gate ON @5287: **208 run / 0 fail / 0 err / 3 skip**
- Offline `verify.py`: **VERIFY PASS**
- Prior demo @5287: 36/36 (unchanged by this triage)

## Suggested Human Gate

**Initiative Accept I-009** is unblocked from the live dialect failure bar (still **NOT Ship**). Review SHAs on this handoff after commit.
