# Initiative Brief: I-004

> Scoped clarity for one change unit. Not a full product re-charter.
> Type: hotfix
> Updated: 2026-07-17T15:01:00+08:00

## Goal
- 修复两则可稳定复现缺陷：（1）`DROP SEQUENCE` 非幂等导致 `GenerationType.AUTO` + create-drop 在 `halt_on_error` 下建不动 SessionFactory；（2）`IDENTITY` + 保留字表名在 Hibernate persist 路径失败。GAV 保持 **`7.4.5.Final`**。

## Human Gate Scope PASS
「本 Initiative 范围已明确，可以开干」(~2026-07-17T14:36+08:00)

| # | Decision |
|---|---|
| 1 | Type **hotfix** |
| 2 | Bug 1 + Bug 2 **都进**本 Initiative |
| 3 | **不升版**（7.4.5.Final） |
| 4 | 需要门控真库 **ORM 入口 IT** |
| 5 | **不改 JDBC/驱动**相关问题（Bug 2 仅方言侧缓解） |
| 6 | **原生实现**（禁止移植 sibling / 旧方言源码） |
| 7 | **Initiative Accept** — **确认**（~2026-07-17T15:01+08:00；不含 Ship） |

## Acceptance criteria
- [x] P-001：`drop sequence if exists` 落地；AUTO/create-drop 门控 IT PASS
- [x] P-002：IDENTITY + 保留字表名 persist 门控 IT PASS（方言缓解，非驱动补丁）
- [x] `verify.py` VERIFY PASS；版本仍为 7.4.5.Final
- [ ] **不要求** Ship（保持未勾选）

## Related
- Branch: `fix/i-004-sequence-drop-identity-reserved`
- Accept evidence: `harness/evidence/orchestrator/I-004/ACCEPTANCE.md`
- Ship: **out of this Initiative**

## Status
`archived` — Human Gate Archive via I-005 Scope PASS (~2026-07-17T15:23+08:00); **not shipped**

- Scope PASS: ~2026-07-17T14:36+08:00
- P-001…P-002: accepted under B-001
- Initiative Accept: ~2026-07-17T15:01+08:00
- Archive: ~2026-07-17T15:23+08:00
- Archive doc: `harness/initiatives/I-004/ARCHIVE.md`
