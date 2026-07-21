# I-010 Initiative Acceptance Evidence

> Initiative: `I-010`  
> Type: `feature`  
> Role: orchestrator (Initiative Accept)  
> Date: 2026-07-21

## Decision

- **Decision:** `accepted`
- **Human Gate phrase:** 「把门控用例在真实虚谷上跑通后，再执行 Initiative Accept I-010」
- **Timestamp:** ~2026-07-21T17:10+08:00
- **Scope:** Initiative Accept only — **NOT Ship** · **NOT Archive**

## Delivery summary

| Deliverable | Result |
|---|---|
| GAV | `com.xugu:xugu-dialect:7.4.5.Final`（无 bump） |
| Compatible mode | **NONE** only |
| B-001 | P-001…P-010 **all accepted** — ORM/HQL quality completion |
| B-002 | P-011…P-017 **all accepted** — xuguefcore parity suite 10/10 |
| Live @5287 | dialect **253/0/0/4** · demo **36/0/0/0** |
| Project verification | **VERIFY PASS** |
| Charter rollup | **91/98** covered-live + **7** known-limit-documented（live 晋升后） |

### B-002 parity suite (live promotion)

| Status | Count | IDs |
|---|---:|---|
| covered-live | 8 | XP-001, XP-002, XP-003, XP-004, XP-005, XP-007, XP-008, XP-009 |
| covered-unit | 2 | XP-006, XP-010 |

### Definition A promotions (live @5287)

| Promoted to covered-live | Still known-limit |
|---|---|
| A-TYP-014 / 016 / 017；A-FUN-003 / 005 / 006 / 007 / 009 | A-FUN-021（XMLTABLE cluster empty→skip；HQL xmlelement/xmlquery live PASS） |

## Branch / SHAs

| Checkpoint | SHA |
|---|---|
| Branch | `feat/i-010-orm-hql-quality-completion` |
| B-002 tip | `2368cbbed8beec5891179abb75ea5a2ae269be9d` |
| Live IT + SSOT promote | `51de6e32ba788110ba209f9e0483b7543daba992` |
| Accept must-commit | *(this Accept commit)* |

## Live / verification evidence

| Gate | Result | Evidence |
|---|---|---|
| Live full reactor @5287 | **PASS** 253/0/0/4 + demo 36/0/0/0 | `harness/evidence/test/I-010/live-it-5287/` |
| `verify.py` | **VERIFY PASS** | `harness/evidence/verification-latest.json` |
| B-002 handoff | complete | `harness/handoffs/orchestrator/I-010-B-002-complete.md` |

Live fixes: `XuguXmlElementFunction` (double-quoted xmlname); `xmlquery` IMPLICIT_XML; A-FUN-005 trim IT VARCHAR trailing via literals.

## Ship deferred (explicit)

- **tag / push / Maven Central / GitHub Release:** **not** performed  
- Ship requires **separate** Human Gate authorization  
- **NOT Archive** unless Human Gate separately authorizes

## Evidence anchors

- Live: `harness/evidence/test/I-010/live-it-5287/`
- Suite SSOT: `contracts/xuguefcore-parity-suite.md`
- Baseline: `contracts/production-regression-baseline.md`
- Pattern: `harness/evidence/orchestrator/I-009/ACCEPTANCE.md`
