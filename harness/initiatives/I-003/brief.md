# Initiative Brief: I-003

> Scoped clarity for one change unit. Not a full product re-charter.
> Type: feature
> Updated: 2026-07-16T17:48:00+08:00

## Goal
- 在**现有 harness 不变**的前提下，按尺子 **C** 补齐方言**生产能力缺口**，使进阶能力对齐完整生产面；GAV 保持 **`com.xugu:xugu-dialect:7.4.5.Final`**。

## Completeness ruler (locked)
**C = A + B**
- **A：** Hibernate **7.4.5** `MySQLDialect` 公开 override / 生产能力面 ∩ `E:\Work\docs\content` 虚谷文档允许项（只对照结构/API，**不继承** MySQL/Oracle Dialect）
- **B：** 对 `E:\Work\java\hibernate-dialect` 做**只读**差集盘点（**禁止移植/复制实现代码**）

## Acceptance criteria
- [x] P-001 产出尺子 C 差集 SSOT，并锁定后续 Phase 实现清单
- [x] P-002…P-006 各能力有实现 + **ORM 入口 IT**（门控真库）证据
- [x] P-007 矩阵/用户指南与行为对齐；`verify.py` **VERIFY PASS**
- [x] 版本仍为 **7.4.5.Final**（无 bump）；工作分支 must-commit SHA 可审
- [ ] **不要求**本 Initiative 完成 Central / Ship（Ship 仍 out of scope — 保持未勾选）

## Decisions (Human Gate 2026-07-16)
1. Archive I-002 — **确认**
2. Type **feature** — **确认**
3. 尺子 **C** — **确认**
4. 首批全做 — **确认**
5. 要入口 IT — **确认**
6. 版本保持 **7.4.5.Final** — **确认**
7. Scope PASS — **确认**
8. **不**修改 harness 框架 — **确认**
9. **Initiative Accept** — **确认**（~2026-07-16T17:46+08:00；不含 Ship）

## Related
- Branch: `feat/i-003-production-capability-parity`
- Matrix SSOT: `contracts/feature-matrix-i003-ruler-c.md` (**CONFIRMED**)
- Accept evidence: `harness/evidence/orchestrator/I-003/ACCEPTANCE.md`
- Ship: **out of this Initiative**

## Status
`archived` — Human Gate「Archive I-003」(~2026-07-17T14:32+08:00); **not shipped**

- Scope PASS: ~2026-07-16T10:46+08:00
- P-001…P-007: all accepted
- Initiative Accept: ~2026-07-16T17:46+08:00
- Archive: ~2026-07-17T14:32+08:00
- Archive doc: `harness/initiatives/I-003/ARCHIVE.md`
