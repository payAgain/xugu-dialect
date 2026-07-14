# P-002 Architect NOTES — Definition A matrix derivation

> **Role:** architect-contract  
> **Invocation:** `arch-p002-20260714`  
> **Phase / Build:** P-002 / B-002  
> **Date:** 2026-07-14  

## Session context (brief)

- Initiative I-001 major; B-002 approved for P-002 only  
- Working branch: `feat/i-001-xugu-dialect-major`  
- Inputs: Charter, ADR-0001, I-001 brief, scaffold contract, architecture notes, P-002 packet  

## Method

1. **Hibernate production surface** — Enumerated Dialect responsibilities expected by applications on Hibernate **7.4.5** from public API/javadoc (not from sibling `hibernate-dialect` repo): type registration / size strategy, schema export DDL, `LimitHandler`, locking (`FOR UPDATE` / timeouts), `IdentityColumnSupport`, `SequenceSupport`, function contributions, temporary table strategy, comments, schema qualification, DialectResolver SPI.
2. **Xugu allow-set** — Read-only research under `E:\Work\docs\content` focusing on:
   - `reference/sql/datatype/**`
   - `reference/sql/select/resultset-restricted.md`, `reference/sql/select/select.md`
   - `reference/object/table/{create,alter,lock,truncate}.md`
   - `reference/object/{sequence,constraints,schema,database,indexes}.md`
   - `reference/sql/ddl/comment.md`, `reference/sql/identifier.md`, `reference/sql/tcl.md`
   - `reference/function/**` (string/math/date/aggregate/uuid/json/sequence)
   - `reference/system-configuration-parameter/**` (`compatible_mode`, `iso_level`, `support_global_tab`, identity_mode)
3. **Intersection (Definition A)** — Each row = capability apps expect from MySQL/Oracle-class Dialects **and** (for `可实现`) a citeable Xugu doc path. Missing grammar → `文档不允许`. Documented but out of I-001 Phase priority / Charter non-goal → `延后` with revisit trigger in Acceptance hint / NOTES below.
4. **Forbidden research** — Did **not** open `E:\Work\java\hibernate-dialect`. Did **not** write under `E:\Work\docs\content`.

## Status assignment rules

| Status | Rule applied |
|---|---|
| 可实现 | Documented SQL/type/function exists; fits P-003…P-008 (or demo env row P-009) |
| 文档不允许 | No documented equivalent for expected surface (e.g. `SKIP LOCKED`, `FOR SHARE`, `FETCH FIRST`, READ UNCOMMITTED, temp-table FK, MySQL/Oracle inheritance) |
| 延后 | Allowed or partially allowed in docs, but not required for core I-001 ORM path (spatial, XML, ARRAY, TOP preference, catalog ambiguity, Ship) |

## Key doc findings (samples)

| Topic | Finding | Impact |
|---|---|---|
| Pagination | `LIMIT` / `LIMIT offset,count` / `LIMIT n OFFSET m`; also `TOP` | Prefer LIMIT for Hibernate LimitHandler |
| Row lock | `FOR UPDATE [OF …]` and `FOR READ ONLY`; wait via `NOWAIT`/`WAIT` on SELECT | Map pessimistic write; no SHARE/SKIP LOCKED |
| Identity | `IDENTITY` / `IDENTITY(start,step)` / `AUTO_INCREMENT` synonym | P-005 DDL + generated keys |
| Sequence | `CREATE SEQUENCE` + `NEXTVAL`/`CURRVAL` | P-005 |
| Temp tables | LOCAL/GLOBAL TEMP; global needs `support_global_tab`; **no FK on temp** | P-007 |
| Comments | `COMMENT ON TABLE/COLUMN` | P-007 |
| compatible_mode | Default **NONE** → unquoted identifiers uppercased | Cross-cutting |
| Isolation | 0 READ ONLY / 1 RC / 2 RR / 3 SERIALIZABLE — **no RU** | `文档不允许` for RU |

## Row counts (final)

| Status | Count |
|---|---|
| 可实现 | **78** |
| 文档不允许 | **7** |
| 延后 | **20** |
| **Total** | **105** |

### By Target Phase (all statuses)

| Target Phase | Count | Notes |
|---|---|---|
| P-003 | 24 | All 可实现 (types/DDL/identifiers/TCL smoke) |
| P-004 | 9 | 6 可实现 + 3 文档不允许 (FETCH FIRST, SKIP LOCKED, FOR SHARE) |
| P-005 | 10 | All 可实现 |
| P-006 | 17 | All 可实现 (core functions; advanced regex/geo/xml deferred as 延后 under later) |
| P-007 | 15 | 14 可实现 + 1 文档不允许 (temp FK) |
| P-008 | 7 | 6 可实现 + 1 文档不允许 (READ UNCOMMITTED) |
| P-009 | 1 | Env secrets (demo) |
| later | 19 | 延后 capabilities |
| — | 2 | Forbidden non-goals (inheritance / sibling port) |
| Ship (post I-001) | 1 | Central publish |

> Note: function rows marked 延后 (A-FUN-015/019/020/021) and type delays appear under Target Phase `later`, not under P-006 totals above.

## Outputs

| Path | Role |
|---|---|
| `contracts/xugu-dialect.contract.md` | Public dialect contract |
| `contracts/feature-matrix-definition-a.md` | Definition A SSOT matrix |
| `harness/evidence/architect-contract/P-002/ACCEPTANCE.md` | Decision pending test/orchestrator |
| `harness/handoffs/architect-contract/P-002.yaml` | RP-01 handoff |

## Ask orchestrator (docs cross-ref)

Architect allowed writes exclude root `docs/**` for this role instance. Please add a short cross-ref in:

- `docs/architecture.md` — link Definition A matrix → `contracts/feature-matrix-definition-a.md`
- Optionally `docs/feature-matrix-definition-a.md` as a stub pointer to the contracts SSOT (ACCEPTANCE names contracts path as SSOT)

Do **not** duplicate the full matrix under docs.

## Risks / follow-ups for implementers

1. Confirm exact `FOR UPDATE` + `NOWAIT`/`WAIT ms` combination on real Xugu (docs show separate clauses).
2. Choose one limit SQL form (`LIMIT n OFFSET m` vs `LIMIT m,n`) and stick to it in LimitHandler.
3. Choose primary UUID SQL function (`SYS_GUID` vs `UUID` vs `GEN_RANDOM_UUID`) for Dialect default.
4. Global temp requires server param `support_global_tab`.
5. Catalog vs Xugu DATABASE mapping remains 延后 until JDBC metadata validated.
