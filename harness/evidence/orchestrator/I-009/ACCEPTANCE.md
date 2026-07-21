# I-009 Initiative Acceptance Evidence

> Initiative: `I-009`  
> Type: `feature`  
> Role: orchestrator (Initiative Accept)  
> Date: 2026-07-21

## Decision

- **Decision:** `accepted`
- **Human Gate phrase:** 「Initiative Accept I-009」
- **Timestamp:** ~2026-07-21T14:11+08:00
- **Scope:** Initiative Accept only — **NOT Ship** · **NOT Archive** (optional next)

## Delivery summary

| Deliverable | Result |
|---|---|
| GAV | `com.xugu:xugu-dialect:7.4.5.Final`（无 bump） |
| Compatible mode | **NONE** only（无 MySQL/Oracle 兼容产品线） |
| Deferred matrix | **20/20** closed — open 延后 **0**（`A-XCUT-012` Ship 锚点保留） |
| Charter rollup | **83/98** covered-live + **15** known-limit-documented（诚实，不膨胀） |
| Deferred breakdown | covered-live **7** / known-limit-documented **12** / doc-forbidden negative **1**（C-JSON-006） |
| Live triage @5287 | ReservedIdentity **fixed**；ENCRYPT + XMLTABLE **known-limit**；dialect **208/0/0/3** |
| Project verification | **VERIFY PASS**（offline + post-triage retest） |
| Build B-001 | P-001…P-011 **all accepted** |

### Deferred inventory final (20/20)

| Status | Count | Row IDs |
|---|---:|---|
| covered-live | 7 | A-FUN-019, A-FUN-020, A-FUN-021, A-LCK-006, A-IDN-005, A-DDL-007, C-SRV-001 |
| known-limit-documented | 12 | A-TYP-014/016/017/018, A-FUN-015, A-DDL-008/009, A-PAG-004/006, A-SCH-003/017, C-SEL-001 |
| doc-forbidden negative-only | 1 | C-JSON-006 |

## Branch / SHAs

| Checkpoint | SHA |
|---|---|
| Branch | `feat/i-009-deferred-matrix-delivery` |
| Pre-Accept HEAD | `01181f2ee62e10d9671cd821df4c8122adda0a8b` |
| Live triage | `8d1de762ff99c2de6883292dca58e8c0c08cc171` |
| Accept must-commit | *(filled after commit)* |

### Phase accept SHAs (B-001)

| Phase | Theme | SHA (short) |
|---|---|---|
| P-001 | Deferred inventory + batch map | `dde2fc2` |
| P-002 | A-TYP-014 INTERVAL | `acb3488` |
| P-003 | A-TYP-016 + A-FUN-021 XML | `21e3863` |
| P-004 | A-TYP-017 + A-FUN-020 geometric | `2e39f40` |
| P-005 | A-TYP-018 UDT | `5ed442a` |
| P-006 | A-FUN-019/015 regexp + bit | `fff15f9` |
| P-007 | A-DDL-007/008/009 DDL | `882b84e` |
| P-008 | A-SCH-003/017 catalog + indexes | `cdd6bee` |
| P-009 | A-LCK-006 + A-PAG-004/006 + A-IDN-005 | `a24b3db` |
| P-010 | C-JSON-006 + C-SRV-001 + C-SEL-001 | `d1c1beb` |
| P-011 | Docs + VERIFY PASS Accept prep | `9c74f53` |

## Live / verification evidence

| Gate | Result | Evidence |
|---|---|---|
| dialect live @5287 (post-triage) | **208 run / 0 fail / 0 err / 3 skip** | `harness/evidence/test/I-009/live-it-5287-rerun/` |
| demo prior @5287 | 36/0/0/0（triage 未改动） | `harness/evidence/test/I-009/live-it-5287/` |
| `verify.py` | **VERIFY PASS** | live-it-5287-rerun + `harness/evidence/test/I-009/P-011/verification.json` |
| P-011 offline reactor | VERIFY PASS；full live @5138 **SKIPPED_INFRA** then optional @5287 green | P-011 + live-it-5287-rerun |

Triage dispositions: see `harness/handoffs/orchestrator/I-009-live-it-5287-rerun.md`.

## Ship deferred (explicit)

- **tag / push / Maven Central / GitHub Release:** **not** performed  
- Ship requires **separate** Human Gate authorization  
- `A-XCUT-012` remains the Ship deferral anchor

## Evidence anchors

- Pattern: `harness/evidence/orchestrator/I-007/ACCEPTANCE.md` / `I-008/ACCEPTANCE.md`
- B-001 complete: `harness/handoffs/orchestrator/I-009-B-001-complete.md`
- Live triage: `harness/handoffs/orchestrator/I-009-live-it-5287-rerun.md`
- Brief: `harness/initiatives/I-009/brief.md`
- Hygiene: root `org/` / `META-INF/` dumps **not** committed

## Next (optional)

1. **Archive I-009**（本轮未做）
2. **Ship**（另行授权 — 见 Part B 发版判定）
3. 新 Initiative（兼容模式 / 深 ORM / Central 准备等）
