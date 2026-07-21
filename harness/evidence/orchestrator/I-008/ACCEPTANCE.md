# I-008 Initiative Acceptance Evidence

> Initiative: `I-008`  
> Type: `feature`  
> Role: Human Gate / orchestrator closeout  
> Date: 2026-07-21

## Decision

- **Decision:** `accepted`
- **Human Gate phrase:** I-009 Scope PASS 含「先 Accept/Archive I-008（推荐）」(~2026-07-21T09:30+08:00)
- **Scope:** Initiative Accept — **NOT Ship**
- **Archive:** 随本轮生效（见 `harness/initiatives/I-008/ARCHIVE.md`）

## Delivery summary

| Deliverable | Result |
|---|---|
| GAV | `com.xugu:xugu-dialect:7.4.5.Final`（无 bump） |
| Compatible mode | **NONE** only |
| Q1 | 诚实口径；可实现 **83/98 covered-live** + **15 known-limit-documented**（非「94 covered-live」膨胀） |
| Q2 | 锁语义文档 + 行为/负向证据；无 SKIP LOCKED / FOR SHARE；`PESSIMISTIC_READ`→排他 FOR UPDATE |
| Q3 | Boot UUID/JSON 开箱对齐 + IT |
| Q4 | 离线 VERIFY PASS；全 reactor 真库补跑 **PASS**（`192.168.2.239:5138`，见 P-007-live） |
| Q5 | **out of scope**（已文档声明） |
| Build B-001 | P-001…P-007 **all accepted** |

## Branch / SHAs

| Checkpoint | SHA |
|---|---|
| Branch | `feat/i-008-production-quality-gaps` |
| P-001 | `454603c` |
| P-002 | `079ab84` |
| P-003 | `54eaf6e` |
| P-004 | `322f0be` |
| P-005 | `58cff86`（prior `be81a96`） |
| P-006 | `1b2f29b` |
| P-007 | `bfbb396` / checkpoint `e902324` |
| Live evidence | `harness/evidence/test/I-008/P-007-live/`（dialect 170/0/0/19；demo 36/0/0/0） |

## Ship

**NOT** performed（tag / push / Central out of scope）
