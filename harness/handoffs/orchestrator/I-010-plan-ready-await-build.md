# Handoff — I-010 Plan ready · await Build scope

- **when:** 2026-07-21T14:40:00+08:00
- **branch:** `feat/i-010-orm-hql-quality-completion`
- **from:** orchestrator (I-010 open)
- **NOT** Build started · **NOT** Ship

## Done
- Archive I-009 → `harness/initiatives/I-009/ARCHIVE.md`；packets → `harness/tasks/archive/I-009/`
- Scope packet：`harness/drafts/I-010-SCOPE-CLARIFYING.md`（P0+P1 自质量缺口分析收窄）
- Plan 物化：P-001…P-010 + `REGISTRY.yaml`
- Brief：`harness/initiatives/I-010/brief.md`（status clarifying / await Scope+Build）

## Plan summary

| Phase | Title | Deps |
|---|---|---|
| P-001 | SSOT/文档口径对齐 | — |
| P-002 | A-TYP-014 INTERVAL 实体 ORM | P-001 |
| P-003 | A-TYP-016 XML 实体 SQLXML | P-002 |
| P-004 | A-TYP-017 POINT 实体 | P-003 |
| P-005 | A-FUN-021 HQL Session + XMLTABLE SSOT | P-004 |
| P-006 | A-FUN-020 几何 HQL live | P-005 |
| P-007 | A-FUN-019 regexp HQL | P-006 |
| P-008 | Batch A 五族独立 HQL | P-007 |
| P-009 | Schema tooling 文档配方 | P-008 |
| P-010 | VERIFY / Accept prep | P-009 |

## Next Build (draft)
**B-001 → P-001** only（默认最早 ready Phase）。Human Gate 可扩大为 P-001~P-010（仍串行）。

## Human Gate phrases needed
1. Scope（若尚未明示）：`本 Initiative 范围已明确，可以开干`
2. Build：`批准 B-001，范围 P-001~P-010`（或 `批准 B-001，范围仅 P-001`）
