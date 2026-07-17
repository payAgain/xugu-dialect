# I-004 Initiative Acceptance Evidence

> Initiative: `I-004`  
> Type: `hotfix`  
> Role: orchestrator (Initiative Accept)  
> Date: 2026-07-17

## Decision

- **Decision:** `accepted`
- **Human Gate phrase:** 「I-004 Initiative Accept」
- **Timestamp:** ~2026-07-17T15:01+08:00
- **Scope:** Initiative Accept only — **NOT Ship** · **NOT Archive** (optional next)

## Delivery summary

| Deliverable | Result |
|---|---|
| GAV | `com.xugu:xugu-dialect:7.4.5.Final`（无 bump） |
| P-001 | `drop sequence if exists` + `XuguAutoSequenceDropIT` |
| P-002 | `getDefaultUseGetGeneratedKeys=false` + `XuguReservedIdentityIT`（`"order"`） |
| JDBC driver | **unchanged**（Scope locked） |
| Project verification | **VERIFY PASS** (P-001 / P-002) |

Build **B-001** / Phases **P-001 … P-002** all **accepted**.

## Branch

- Working branch: `fix/i-004-sequence-drop-identity-reserved`
- Key SHAs: P-001 `8833a22` · P-002 `96fccbe`

## Ship deferred (explicit)

- **tag / push / Maven Central:** not performed  
- Ship requires **separate** Human Gate authorization

## Evidence anchors

- `harness/evidence/implementer/P-001/ACCEPTANCE.md`
- `harness/evidence/implementer/P-002/ACCEPTANCE.md`
- `harness/initiatives/I-004/brief.md`

## Next (optional)

1. **Archive I-004**
2. **Ship**（另行授权）
3. 新 Initiative
