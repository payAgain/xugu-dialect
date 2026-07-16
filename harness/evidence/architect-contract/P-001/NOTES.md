# P-001 Architect NOTES — I-003 ruler C gap inventory

> **Role:** architect-contract  
> **Invocation:** `arch-p001-20260716`  
> **Phase / Build:** P-001 / B-001 / I-003  
> **Date:** 2026-07-16  

## Inputs

- Human Gate Scope: ruler **C**; first batch all themes; entrypoint IT; version 7.4.5.Final; no harness framework changes
- Charter / ADR-0001 / definition A matrix (I-001 closed)
- Local dialect: `dialect/src/main/java/com/xugu/dialect/**` (12 classes)
- Sibling read-only: `E:\Work\java\hibernate-dialect\dialect\xugudb-hibernate-dialect\src\main\java/**` (21 classes)
- XuGu docs: `E:\Work\docs\content`

## Method

1. Diff `@Override` method names: local 70 vs sibling 72; sibling-only **34**, local-only **32** (API style differences, not all gaps).
2. Map sibling-only / MySQL-class production themes to XuGu doc cites.
3. Assign Status + Target Phase + **app_entrypoint** for every new `可实现` row.
4. **Did not** copy sibling source into this repo.

## Outputs

| Path | Role |
|---|---|
| `contracts/feature-matrix-i003-ruler-c.md` | I-003 gap SSOT |
| `contracts/xugu-dialect.contract.md` §7.1 / §8–11 | Public contract addendum |
| `contracts/feature-matrix-definition-a.md` | Cross-ref to I-003 SSOT |
| This NOTES + ACCEPTANCE | Evidence |

## Key quantitative findings

| Metric | Value |
|---|---|
| New C-* rows | 24 |
| 可实现 (I-003 first batch) | **16** |
| 文档不允许 | 2 |
| 延后 | 5 |
| 已有 (no new work) | 1 |
| Sibling-only overrides (discovery) | 34 |
| Sibling main classes vs local | 21 vs 12 |

## Phase lock (implementable)

| Phase | IDs |
|---|---|
| P-002 | C-EXC-001, C-EXC-002 |
| P-003 | C-JSON-001…004 |
| P-004 | C-WIN-001, C-CTE-001 |
| P-005 | C-BULK-001…003 |
| P-006 | C-DDL-001…003, C-CAT-001, C-GUID-001 |

## Risks

- Sibling used `MySQLDialect.datetimeFormat` helper — local implementers must not extend MySQLDialect; may reimplement format mapping citing XuGu docs.
- Catalog vs schema semantics remain subtle — C-CAT-001 IT must use real DB carefully.
- Bulk insert strategy may be N/A for some mappings — accept with documented N/A evidence if P-001 mapping never triggers.
