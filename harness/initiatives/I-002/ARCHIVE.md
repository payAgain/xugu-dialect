# I-002 Archive

> Initiative: `I-002`  
> Type: `hotfix`  
> Role: orchestrator (Archive)  
> **NOT Ship** — no tag / push / Maven Central

## Archived

| Field | Value |
|---|---|
| Archived at | `2026-07-16T10:46:00+08:00` |
| Human Gate phrase | 「Archive I-002」 |
| Final status | **accepted** then **archived** |

## Delivery summary

| Deliverable | Result |
|---|---|
| GAV | `com.xugu:xugu-dialect:7.4.5.Final`（同版本行为修复） |
| P0 HQL pagination | `XuguSqlAstTranslator` → `limit ? offset ?`；锁序 FOR UPDATE → LIMIT → WAIT |
| P1 sequence metadata | `all_sequences` + Xugu extractor；`hbm2ddl validate` |
| In-repo gated ITs | HQL pagination + SchemaValidate |
| Docs / matrix | P-003 对齐 |
| Project verification | **VERIFY PASS** |

Phases **P-001 → P-003** accepted under Builds **B-001 → B-003**.

## Branch

- Working branch: `fix/i-002-hql-pagination-sequence-metadata`
- Base: `feat/i-001-xugu-dialect-major` / GitHub Flow

## Key SHAs

| Checkpoint | SHA |
|---|---|
| P-001 Accept | `63a7d6001dbd6845ea10520905c60bb56d2e3d9c` |
| P-002 Accept | `908e7f665c3317beef3665063ebc0d02efc6ed5f` |
| P-003 Accept | `9df8c6242eb2f5593d7fd2fcb1f7f6482e17c8bf` |
| Initiative Accept record | `a009ed2a391289baf19454268755ddbb035ebfbc` |

## Ship deferred (explicit)

- **tag:** not performed  
- **push:** not performed  
- **Maven Central / release:** out of scope for I-002 Archive  
- Ship requires **separate** Human Gate authorization

## Pointers

- Initiative Accept: `harness/evidence/orchestrator/I-002/ACCEPTANCE.md`
- Brief: `harness/initiatives/I-002/brief.md`
- Index: `harness/initiatives/INDEX.md`

## Next

1. I-003 feature（生产能力补齐）via Scope PASS already granted  
2. Ship / tag / push / Central — **only** with new Human Gate authorization
