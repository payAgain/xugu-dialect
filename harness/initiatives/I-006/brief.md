# Initiative Brief: I-006

> Scoped clarity for one change unit. Not a full product re-charter.
> Type: feature
> Updated: 2026-07-18T15:09:00+08:00

## Goal
- 在 I-005 生产回归基线之上，建立 **Spring Boot 消费者路径全面覆盖**：黄金路径加固（A）+ 代表性模型扩展（B）+ SSOT 消费者相关行扫盲（C′）。
- GAV 保持 **`com.xugu:xugu-dialect:7.4.5.Final`**（本 Initiative 不升版）。
- Demo 用例目标量级约 **25–40**；方言模块原则上不改（除非发现 Boot 独有缺陷）。

## Human Gate Scope PASS
「本 Initiative 范围已明确，可以开干」(~2026-07-18T15:09+08:00)

| # | Decision |
|---|---|
| 1 | Type **feature** |
| 2 | 方案 **3：A → B → C′**（全面；**不**在 Boot 镜像全部 94 可实现行） |
| 3 | Layer B：**B-both** — 关联实体 + SEQUENCE 实体 |
| 4 | C′：P-001 筛出 Boot 必测清单（约 35–50 行）；纯方言钩子仍只留方言 IT |
| 5 | 门禁：日常 `mvn test` 离线绿；Accept 要求 `XUGU_RUN_IT=true` 全绿（有库时） |
| 6 | **不升版**（7.4.5.Final） |
| 7 | **原生实现**（禁止移植 sibling / 继承 MySQL/Oracle Dialect） |
| 8 | **不要求**本 Initiative 完成 Ship / Central |

## Layer summary

| Layer | Scope |
|---|---|
| **A** | validate 启动、startup-crud 真路径、完整 CRUD、JPQL+Pageable、可选 SPI 无显式 dialect |
| **B** | Person+关联实体；SEQUENCE 实体；悲观锁；事务回滚；UNIQUE 异常形态 |
| **C′** | 按消费者 SSOT 补齐剩余 Boot 入口（函数子集、JSON 可选、bulk update/delete 一条、关键类型字段等） |

## Acceptance criteria
- [ ] 消费者路径 SSOT（Boot 必测清单）0 gap；与 I-005 基线交叉引用清晰
- [ ] Layer A/B/C′ 用例落地；Demo 约 25–40 `@Test`；门控真库可全绿
- [ ] 用户文档说明如何跑消费者路径基线
- [ ] `verify.py` **VERIFY PASS**；版本仍为 7.4.5.Final
- [ ] **不要求** Ship（保持未勾选）

## Related
- Predecessor: I-005 archived
- Branch (planned): `feat/i-006-consumer-path-coverage`
- SSOT (planned): `contracts/consumer-path-baseline.md`（或等价扩写）
- Ship: **out of this Initiative**

## Status
`active` — Scope PASS ~2026-07-18T15:09+08:00；Plan complete ~2026-07-18T15:13+08:00；B-001 draft（仅 P-001）待 Human Gate 批准
