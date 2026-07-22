# Initiative Brief: I-007

> Scoped clarity for one change unit. Not a full product re-charter.
> Type: feature
> Updated: 2026-07-19T17:12:00+08:00

## Goal
- 在 **`compatiblemode=NONE`** 下串行推进框架下一阶段：**A 堵坑** → **B 消费者加深** → **C 延后矩阵增量**。
- GAV 保持 **`com.xugu:xugu-dialect:7.4.5.Final`**（不升版）。
- **不含** MySQL/Oracle 兼容模式（独立产品线，out of scope）。

## Human Gate Scope PASS
「Archive I-006；类型 feature；其余按推荐；本 Initiative 范围已明确，可以开干」(~2026-07-19T13:50+08:00)

| # | Decision |
|---|---|
| 0 | **Archive I-006**（不含 Ship） |
| 1 | Type **feature** |
| 2 | **A**：C-BULK-002 **优先实库打通**（打不通则钉死永久限制+文档）；Accept 证据硬化（`XUGU_RUN_IT` 日志入库）；急项可与 C 主题合并 |
| 3 | **B**：消费者加深 — **Flyway** + Demo bulk update/delete 入口 + 函数/HQL 冒烟；可选只读事务；**本批不做多数据源** |
| 4 | **C**：延后主题默认三件套 — **JSON 深化（C-JSON-005 子集）** + **ARRAY（A-TYP-015 / C-DDL-005）** + **ALTER SEQUENCE（A-SEQ-006）** |
| 5 | 门禁：日常离线绿；Accept 要求真库全绿且 **live 日志入库** |
| 6 | **不升版**（7.4.5.Final） |
| 7 | **原生实现**；`compatiblemode=NONE` only |
| 8 | **不要求**本 Initiative 完成 Ship / Central |

## Layer / track summary

| Track | Scope |
|---|---|
| **A** | C-BULK-002 live path or permanent-limit; verification evidence hardening |
| **B** | Flyway + Demo bulk/function/HQL (+ optional read-only tx smoke) |
| **C** | JSON subset + ARRAY + ALTER SEQUENCE — doc → impl → live IT → SSOT |

## Planned Phases (serial, draft for orchestrator Plan)

| Phase | Intent |
|---|---|
| P-001 | Inventory A/B/C vs I-005/I-006 SSOT; lock C-BULK-002 strategy |
| P-002 | A: C-BULK-002 + evidence hardening |
| P-003 | A′/C prep: any urgent deferred not folded into P-004 |
| P-004 | C: JSON + ARRAY + ALTER SEQUENCE |
| P-005 | B: Flyway + Demo consumer deepening |
| P-006 | Docs + VERIFY PASS + Accept prep |

## Acceptance criteria
- [x] C-BULK-002：live IT PASS **或** 永久限制正式写入 SSOT + 用户文档（二选一钉死）→ **covered-live**
- [x] Accept 证据含可审计 live 日志工件（有库时）
- [x] C 三主题有文档依据 + 实现 + 真库入口 + SSOT 更新
- [x] B：Flyway 路径 + Demo bulk/函数（或 HQL）冒烟可复现
- [x] `verify.py` **VERIFY PASS**；GAV 7.4.5.Final；仍为 NONE
- [ ] **不要求** Ship（保持未勾选）

## Related
- Predecessor: I-006 → Archive as part of this Scope
- Branch: `feat/i-007-capability-hardening-abc`
- SSOT touch: `contracts/production-regression-baseline.md`, `contracts/consumer-path-baseline.md`, feature matrices
- Ship: **out of this Initiative**
- Accept evidence: `(historical harness evidence removed; see Trellis task research/ if present) orchestrator/I-007/ACCEPTANCE.md`

## Status
`completed` / **archived** — 见 `.trellis/tasks/archive/2026-07/ (historical; former harness path) I-007/ARCHIVE.md`（2026-07-20；I-008 Scope PASS）。**NOT Ship**。
