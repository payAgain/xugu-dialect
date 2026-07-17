# I-003 Archive

> Initiative: `I-003`  
> Type: `feature`  
> Role: orchestrator (Archive)  
> **NOT Ship** — no tag / push / Maven Central

## Archived

| Field | Value |
|---|---|
| Archived at | `2026-07-17T14:32:00+08:00` |
| Human Gate phrase | 「Archive I-003」 |
| Final status | **accepted** then **archived** |

## Delivery summary

| Deliverable | Result |
|---|---|
| GAV | `com.xugu:xugu-dialect:7.4.5.Final`（无 bump） |
| Ruler C | MySQLDialect∩XuGu docs + read-only sibling gap；16 可实现首批 |
| P-001 | 尺子 C SSOT |
| P-002 | 异常映射 + ORM IT |
| P-003 | JSON / AggregateSupport + ORM IT |
| P-004 | Window / WITH(CTE) + ORM IT |
| P-005 | Bulk mutation fallback + ORM IT |
| P-006 | Type/DDL details + ORM IT |
| P-007 | Docs/matrix CONFIRMED；VERIFY PASS |
| Project verification | **VERIFY PASS** |

Phases **P-001 → P-007** accepted under Builds **B-001 → B-007**. Initiative Accept recorded.

## Branch

- Working branch: `feat/i-003-production-capability-parity`
- Base: I-002 HEAD / GitHub Flow

## Key SHAs

| Checkpoint | SHA |
|---|---|
| P-004 | `8cb1f49` |
| P-005 | `e4da4cf` |
| P-006 | `e45d8b3` |
| P-007 | `5d9c44d` |
| Initiative Accept | `f3d3ed2` |

## Ship deferred (explicit)

- **tag / push / Maven Central:** not performed  
- Ship requires **separate** Human Gate authorization

## Pointers

- Initiative Accept: `harness/evidence/orchestrator/I-003/ACCEPTANCE.md`
- Brief: `harness/initiatives/I-003/brief.md`
- Matrix: `contracts/feature-matrix-i003-ruler-c.md`

## Next

1. I-004 hotfix（AUTO drop sequence IF EXISTS + IDENTITY×保留字表名）— Scope in progress  
2. Ship — only with new Human Gate authorization
