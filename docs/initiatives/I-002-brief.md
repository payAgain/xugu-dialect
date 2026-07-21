# Initiative Brief: I-002

> Scoped clarity for one change unit. Not a full product re-charter.
> Type: hotfix
> Updated: 2026-07-16T10:28:00+08:00

## Goal
- 根据 `E:\Work\java\xugu-hibernate-test\TEST-REPORT.md`，修复 HQL 分页（ANSI OFFSET/FETCH）与 schema validate 序列元数据缺失；**本仓补充测试用例并验证通过**；制品版本仍为 **`7.4.5.Final`**。

## Problem
1. **P0** HQL `setFirstResult`/`setMaxResults` → `[E19132] unexpected OFFSET`（缺 SqlAstTranslator）
2. **P1** `hbm2ddl.auto=validate` → `missing sequence`（`getQuerySequencesString()` 未覆盖）

## In-scope
- 同时修复 P0 + P1
- `XuguSqlAstTranslator` + `getSqlAstTranslatorFactory()`：offset/fetch → 虚谷 LIMIT（与 LimitHandler / FOR UPDATE 顺序一致）
- `getQuerySequencesString()`（及必要 extractor）→ `all_sequences` 支持 validate
- **本仓补充** HQL 分页 IT + schema validate/sequence metadata IT（门控真实库），并 VERIFY PASS
- 可对照复跑 `xugu-hibernate-test` 作为额外证据（非唯一验收；本仓测试必须绿）
- 版本号沿用 **`7.4.5.Final`**
- 分支：`fix/i-002-hql-pagination-sequence-metadata`
- 文档/矩阵勘误（若需）

## Out-of-scope
- 升版本号；Ship；扩大定义 A；改写官方 content；旁路方言移植

## Acceptance criteria
- [x] 本仓新增/补齐用例覆盖 HQL 分页与 sequence metadata/validate，门控 IT 在真实库 PASS
- [x] 离线 `mvn test` 仍 PASS；`verify.py` VERIFY PASS
- [x] 重建 `com.xugu:xugu-dialect:7.4.5.Final` 后，上述能力可用
- [ ] （可选证据）`xugu-hibernate-test` 复跑相关失败项 PASS — **N/A**（本仓 IT primary；未作为强制验收）

## Decisions (Human Gate 2026-07-15)
1. P0+P1 一起修 — **确认**
2. 补充本仓测试并验证通过 — **确认**
3. 版本沿用 7.4.5.Final — **确认**
4. Scope PASS：「本 Initiative 范围已明确，可以开干」

## Related
- Branch: `fix/i-002-hql-pagination-sequence-metadata`
- Builds: **B-001 … B-003** / Phases **P-001 … P-003** (all accepted)
- Key SHAs: P-001 `63a7d6001dbd6845ea10520905c60bb56d2e3d9c` · P-002 `908e7f665c3317beef3665063ebc0d02efc6ed5f` · P-003 `9df8c6242eb2f5593d7fd2fcb1f7f6482e17c8bf`
- Initiative Accept evidence: `harness/evidence/orchestrator/I-002/ACCEPTANCE.md`
- Ship: **out of this Initiative** (deferred)

## Status
`completed` / **accepted** then **archived**

- Human Gate Accept: 「Accept Initiative I-002」(~2026-07-16T10:28+08:00)
- Human Gate Archive: 「Archive I-002」(~2026-07-16T10:46+08:00)
- Archive record: `harness/initiatives/I-002/ARCHIVE.md`
- Delivery: GAV `com.xugu:xugu-dialect:7.4.5.Final` same-version behavior fix; P0 HQL pagination `limit ? offset ?` + lock order; P1 `all_sequences` validate; in-repo gated ITs; docs/matrix P-003; VERIFY PASS
- Ship / tag / push / Central: **not** done — separate authorization required
