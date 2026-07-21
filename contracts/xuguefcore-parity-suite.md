# xuguefcore Parity Suite (SSOT)

> **Status:** Published (I-010 / B-002 / P-011 / RP-01 architect-contract)  
> **Initiative:** I-010 — ORM/HQL quality + xuguefcore parity  
> **Build:** B-002  
> **Human Gate:** 对照 `E:\Work\C#\xuguefcore` 全部建议用例直接补充进 I-010  
> **GAV:** `7.4.5.Final` · **compatiblemode:** `NONE` · **NOT Ship**  
> **Author role:** architect-contract  
> **Companion:** [`production-regression-baseline.md`](production-regression-baseline.md), [`feature-matrix-definition-a.md`](feature-matrix-definition-a.md)  
> **Evidence:** `harness/evidence/architect-contract/I-010/P-011/ACCEPTANCE.md`

## Purpose

Track B-002’s **10 Human-approved** xuguefcore-mapped themes as a single source of truth.  
This document is **planning / status SSOT only** — it does **not** implement tests and must **not** inflate any row to `covered-live`.

## Constraints

| Constraint | Value |
|---|---|
| GAV | `7.4.5.Final` (no bump) |
| compatiblemode | `NONE` |
| Ship | **NOT Ship** (no tag / push / release / protected-branch merge as Ship) |
| Dialect / Demo Java | Out of P-011; later Phases own test code under allowed ownership |
| Status promotion | Only owning test Phase may move `planned` → `in-progress` → honest outcome; **never** invent `covered-live` here |

## IT / Unit gates

| Gate | How to enable | Meaning |
|---|---|---|
| **IT** | env `XUGU_RUN_IT=true` **or** JVM `-Dxugu.run.integration=true` | Live XuguDB integration (`XuguITGate` / equivalent) |
| **Unit** | default `mvn test` (no live DB) | Offline assertions / SQL baselines / exception conversion |

Gate off → IT assumes/skips; suite must stay green. Gate on + unreachable DB → honest `SKIPPED_INFRA` (not false green).

## Status legend (this suite)

| status | Meaning |
|---|---|
| **planned** | Human-approved; class name reserved; not yet implemented |
| **in-progress** | Owning Phase actively implementing |
| **covered-unit** | Offline unit PASS (no live claim) |
| **covered-live** | Gated IT PASS on live DB — **only** after owning Phase evidence |
| **known-limit-documented** | Honest boundary / failure documented (e.g. JSON LOB) |
| **skipped-infra** | Gate on but DB unavailable; documented, not promoted |

**Initial status for all 10 rows:** `planned`.

## Suite rows (Human-approved · 10)

Columns: `id | pri | theme | layer | planned class(es) | ref (xuguefcore) | status | owning Phase`

| id | pri | theme | layer | planned class(es) | ref (xuguefcore) | status | Phase |
|---|---|---|---|---|---|---|---|
| XP-001 | 高 | `@Version` 乐观锁陈旧写失败 → OptimisticLockException（或 Hibernate 等价） | IT | `XuguOptimisticConcurrencyIT` | OptimisticConcurrencyTests / AffectedRowsProbeTests | implemented | P-012 |
| XP-002 | 高 | HQL GroupBy / Count 投影物化（scalar count + group-by count → Int） | IT | `XuguHqlGroupByCountIT` | RuntimeGapBaselineTests（Count·GroupBy） | implemented | P-012 |
| XP-003 | 中 | HQL bulk 支持/拒绝边界（Hibernate JOINED / SINGLE_TABLE；order by / limit mutation；**勿** EF Owned） | Unit+IT | `XuguHqlBulkBoundaryTest` + `XuguHqlBulkBoundaryIT` | ExecuteBulkBoundaryTests | covered-unit | P-013 |
| XP-004 | 中 | 大 JSON LOB 物化边界（失败则诚实 document / known-limit） | IT | `XuguJsonLobBoundaryIT` | JsonBoundaryTests | skipped-infra | P-014 |
| XP-005 | 中 | ORM 显式事务原子性 multi-persist（或 Demo 加强） | IT | `XuguExplicitTxAtomicityIT` | — | skipped-infra | P-014 |
| XP-006 | 中 | 方言 SQL 金标 LIMIT / 锁序 / IDENTITY DDL | Unit | `XuguNativeSqlBaselineTest` + `src/test/resources` baselines | NativeSqlBaselineTests | covered-unit | P-015 |
| XP-007 | 中 | HQL join fetch / 一对多烟测 | IT | `XuguHqlJoinFetchIT` | RuntimeGap Include | skipped-infra | P-015 |
| XP-008 | 低 | Null 语义子集（IS NULL / 三值 / coalesce；**3–5** 条） | IT | `XuguNullSemanticsIT` (4 methods) | — | known-limit-documented | P-016 |
| XP-009 | 低 | 时间函数投影（**仅**文档允许：`year` / `month` / `day` / `extract` / `current_date` / `current_timestamp`） | IT | `XuguTemporalProjectionIT` | — | known-limit-documented | P-016 |
| XP-010 | 低 | 锁超时 / 死锁（加强 Unit；optional live IT） | Unit(+optional IT) | strengthen `XuguExceptionConversionTest` (no live IT) | — | covered-unit | P-016 |

### Priority rollup

| Priority | Count | IDs |
|---|---:|---|
| 高 | 2 | XP-001, XP-002 |
| 中 | 5 | XP-003 … XP-007 |
| 低 | 3 | XP-008, XP-009, XP-010 |
| **Total** | **10** | |

## Mapping notes (EF → Hibernate)

- Bulk inheritance themes map to Hibernate **JOINED** / **SINGLE_TABLE** — **not** EF Owned types.
- Optimistic concurrency maps to JPA `@Version` + Hibernate lock exception surface.
- Native SQL baselines assert dialect-emitted forms (LIMIT bind, `FOR UPDATE` ordering, IDENTITY DDL) offline.
- Temporal row **must not** invent undocumented date/time functions.

## P-016 outcome notes (test · XP-008/009/010)

| id | status | note |
|---|---|---|
| XP-008 | known-limit-documented | `XuguNullSemanticsIT` landed (IS NULL / IS NOT NULL / three-valued `=:null` / coalesce); offline gate-skip; promote **covered-live** only after live PASS |
| XP-009 | known-limit-documented | `XuguTemporalProjectionIT` landed (year/month/day/extract/current_date/current_timestamp only; fixed seed 2024-03-15); offline gate-skip; promote **covered-live** only after live PASS |
| XP-010 | covered-unit | `XuguExceptionConversionTest` strengthened (DEADLOCK→LockAcquisitionException; LOCK_TIMEOUT / DETAIL / UPGRADE→LockTimeoutException via JDBC / SQLState / `[E#####]` message). Optional live `XuguLockTimeoutIT` **not** added — **live-unstable** (dual-connection NOWAIT/WAIT not stably reproducible here) |

## Phase status notes

- **XP-001 / XP-002 (P-012):** status `implemented` — gated ITs + entities landed; offline suite green via `Assumptions.assumeTrue(XuguITGate.isEnabled())`. Live DB not required for this status; promote to `covered-live` only after gate-on live PASS. If gate-on + unreachable DB, document `skipped-infra` in Phase evidence (do not invent `covered-live`).
- **XP-003 (P-013):** status `covered-unit` — `XuguHqlBulkBoundaryTest` offline PASS (dialect bulk/temp flags; HQL update/delete + `order by`/`limit` → `SyntaxException`; `MutationQuery` API has no `setMaxResults`; `Query.setMaxResults` soft no-op). Gated `XuguHqlBulkBoundaryIT` + SINGLE_TABLE entities landed; live TCP refused → evidence `SKIPPED_INFRA`. Promote **covered-live** only after gate-on live PASS.
## P-014 outcome notes (test · XP-004/005)

| id | status | note |
|---|---|---|
| XP-004 | skipped-infra | `XuguJsonLobBoundaryIT` + `I010P014JsonDoc` landed (~512KiB JSON `@JdbcTypeCode(JSON)`; success→length assert / fail→document limitation; optional `json_value` scalar). Offline gate-skip green. Live `:5138` refused — **not** `covered-live` / **not** `known-limit-documented` until live run observes either branch. Evidence: `harness/evidence/test/I-010/P-014/` |
| XP-005 | skipped-infra | `XuguExplicitTxAtomicityIT` + `I010P014TxEntity` landed (`Session.beginTransaction()` multi-persist commit→2 rows / rollback→0). Offline gate-skip green. Same infra block. Promote **covered-live** only after live PASS. |

## Out of scope (explicit)

Do **not** port or invent coverage for:

| Exclusion | Reason |
|---|---|
| SKIP LOCKED | 文档不允许 / 矩阵负向已锁 |
| FOR SHARE | 文档不允许 |
| `json_table` / JSON_TABLE | 文档不允许（C-JSON-006） |
| ENUM | 超出本套件批准主题 |
| EF Spec 8500 | EF-only conformance suite |
| Migrations | EF/ADO migration tooling |
| Retry | EF retry policies |
| RETURNING | EF/ADO returning API |
| ADO | .NET data access surface |
| Owned（EF Owned entities）照搬 | Map to Hibernate inheritance / associations instead |
| compatible 双方言 | `compatiblemode=NONE` only |
| Ship / Archive as part of B-002 | Initiative remains **NOT Ship** |

## Docs entry points (docs role · P-011 RP-02)

User-facing pointers (owned by **docs**, not this RP):

- `docs/user-guide/03-verify.md` — cite this suite + IT gate `XUGU_RUN_IT` / `-Dxugu.run.integration=true`
- `docs/user-guide/04-feature-matrix.md` — link B-002 parity suite SSOT

## Orchestrator note

- P-011 lands SSOT (+ docs/reviewer). **No** dialect/demo Java in this Phase.
- After P-011 Accept, orchestrator may schedule P-012…P-016; serial by default; parallel only when dependencies allow.
- P-017 rollup: suite statuses + `verify.py` — still **NOT Ship**.
