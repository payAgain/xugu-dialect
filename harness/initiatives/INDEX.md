# Initiatives Index

> 每个 Initiative 是一次有边界的交付（如 feature / 大版本 / hotfix）。  
> 项目只 init 一次；此后按本表循环。详见框架 `protocol/references/lifecycle.md`。

| ID | Type | Title | Branch | Status | Updated |
|---|---|---|---|---|---|
| I-001 | major | 交付 Hibernate 7.4.5 虚谷方言 jar + Spring Boot demo + 项目文档（定义 A 全量） | feat/i-001-xugu-dialect-major | completed | 2026-07-15T16:25:00+08:00（archived） |
| I-002 | hotfix | 修复 HQL 分页 OFFSET/FETCH 与 schema validate 序列元数据（TEST-REPORT 20/22） | fix/i-002-hql-pagination-sequence-metadata | completed | 2026-07-16T10:46:00+08:00（archived） |
| I-003 | feature | 生产能力补齐（尺子 C：MySQLDialect∩虚谷文档 + 只读对照 hibernate-dialect 差集；入口 IT；版本 7.4.5.Final） | feat/i-003-production-capability-parity | completed | 2026-07-17T14:32:00+08:00（archived） |
| I-004 | hotfix | DROP SEQUENCE IF EXISTS + IDENTITY×保留字表名方言缓解（不改 JDBC；7.4.5.Final） | fix/i-004-sequence-drop-identity-reserved | completed | 2026-07-17T15:23:00+08:00（archived） |
| I-005 | feature | 生产回归测试基线（Definition A + I-003 可实现全量；门控真库；未来迭代防回退） | feat/i-005-production-regression-baseline | completed | 2026-07-18T14:23:00+08:00（archived） |
| I-006 | feature | 消费者路径全面覆盖（A 黄金路径 + B 关联/SEQUENCE + C′ Boot 必测扫盲） | feat/i-006-consumer-path-coverage | active | 2026-07-18T15:13:00+08:00（Plan complete；B-001 draft） |

Status: `clarifying` | `active` | `completed` | `abandoned` | `accepted`

### Archived
- I-001：见 `harness/initiatives/I-001/ARCHIVE.md`（2026-07-15）
- I-002：见 `harness/initiatives/I-002/ARCHIVE.md`（2026-07-16）
- I-003：见 `harness/initiatives/I-003/ARCHIVE.md`（2026-07-17）
- I-004：见 `harness/initiatives/I-004/ARCHIVE.md`（2026-07-17）
- I-005：见 `harness/initiatives/I-005/ARCHIVE.md`（2026-07-18）
