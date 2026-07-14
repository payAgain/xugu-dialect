# P-003 Architect NOTES — Types/DDL package & override map

> **Role:** architect-contract  
> **Invocation:** `arch-p003-20260714`  
> **Phase / Build:** P-003 / B-003  
> **Date:** 2026-07-14  

## Session context

- Initiative I-001 major; B-003 approved for **P-003 only**
- Working branch: `feat/i-001-xugu-dialect-major`
- Current stub: `com.xugu.dialect.XuguDialect` extends `Dialect` only (P-001 scaffold)
- Inputs: `harness/tasks/P-003.md`, `contracts/feature-matrix-definition-a.md` (P-003 rows), `contracts/xugu-dialect.contract.md`, Hibernate 7.4.5.Final `Dialect` API (local jar javap)
- Forbidden: no Java implementation; no `E:\Work\java\hibernate-dialect`

## Method

1. Enumerated Hibernate **7.4.5** Dialect hooks for types/DDL/identifiers from `org.hibernate.dialect.Dialect` bytecode/API.
2. Mapped each P-003 matrix ID (`A-TYP-001…013,019`, `A-DDL-001…006`, `A-XCUT-001/002/004/007`) to concrete override/registration points.
3. Kept package footprint minimal: public `XuguDialect` + optional `com.xugu.dialect.internal` helpers — structure inspired by MySQL/Oracle Dialect organization, **not** inheritance.
4. Locked IT gate + connection env defaults from Charter / user packet.

## Outputs

| Path | Role |
|---|---|
| `contracts/xugu-dialect.p003-types-ddl.contract.md` | P-003 implement contract |
| `harness/evidence/architect-contract/P-003/NOTES.md` | This file |
| `harness/evidence/architect-contract/P-003/ACCEPTANCE.md` | RP-01 decision |
| `harness/handoffs/architect-contract/P-003.yaml` | Handoff to implementer |

## Key decisions

| Decision | Rationale |
|---|---|
| No heavy type/ddl subpackages | Hibernate routes via Dialect methods; avoid premature structure |
| Quote char `"` | Matches Xugu documented double-quote identifiers; backtick is DB-alternate |
| Unquoted → UPPER via `buildIdentifierHelper` | NONE mode fold per `identifier.md` |
| Prefer BOOLEAN over BIT | Docs prefer BOOLEAN |
| Prefer TIMESTAMP for Hibernate timestamps | Consistency; DATETIME remains available if docs/IT force revisit — document in implementer evidence |
| IT gated by property/env | Offline `mvn test` must stay green; acceptance still requires real DB when flag on |

## Risks for implementer / test

1. TIMESTAMP vs DATETIME choice may need real-DB confirmation under NONE mode.
2. UUID/`UuidJdbcType` binding to `GUID` may need `contributeTypes` beyond `columnType`.
3. DROP/ALTER column exact grammar must match `alter.md` — adjust string helpers if defaults fail IT.
4. Real XuguDB unreachable → external blocker; do not mock-pass.
