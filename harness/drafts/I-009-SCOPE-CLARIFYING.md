# I-009 Scope Clarifying — 交付矩阵「延后」项

> Status: **clarifying**  
> Updated: 2026-07-21T09:30:00+08:00  
> Human Gate：「文档不允许的可以跳过，把延后的内容做了」

## 与 I-008 关系
- I-008（质量短板 Q1–Q4）B-001 已完成；真库 live 已绿；**待 Initiative Accept**（推荐先 Accept/Archive I-008，再开 I-009）
- 本 Initiative **不**重开兼容模式；**不**做 Ship/Central（A-XCUT-012 仍归 Ship）

## 规则（锁定）
| 类别 | 处理 |
|---|---|
| **文档不允许** | 继续 skip / 负向断言；**禁止**发明 SQL（SKIP LOCKED、FOR SHARE、ANSI FETCH、READ UNCOMMITTED、temp FK 等） |
| **延后**（虚谷文档有依据） | **本 Initiative 交付**：方言能力 + 单测 + 门控真库 IT（能 live 的升 covered-live） |
| **Ship 流程**（A-XCUT-012） | **仍不做**（非方言能力） |

## 拟交付「延后」清单（来自 Definition A / Ruler C / 负向锚点）

### A. 类型与函数
| ID | 主题 | 文档依据（示意） |
|---|---|---|
| A-TYP-014 | INTERVAL | `datatype/datetime.md` + interval 风格参数 |
| A-TYP-016 + A-FUN-021 | XML 类型 + XML 函数 | `datatype/xml.md` / xml-functions |
| A-TYP-017 + A-FUN-020 | 几何/空间类型 + 几何函数 | geometric datatype + geometric-functions / spatial-database |
| A-TYP-018 | UDT | `datatype/udt.md` |
| A-FUN-019 | regexp_* | regexp_like / replace / substr |
| A-FUN-015 | bit_and / bit_or（矩阵延后，若未在负向表） | aggregate bit_* |

### B. DDL / Schema / 锁 / 分页备选
| ID | 主题 |
|---|---|
| A-DDL-007 | IF NOT EXISTS（若仍延后） |
| A-DDL-008 | 表分区 PARTITION BY |
| A-DDL-009 | ENCRYPT 子句 |
| A-SCH-003 | catalog/database 限定（JDBC 元数据对齐后） |
| A-SCH-017 | 高级索引类型 |
| A-LCK-006 | LOCK TABLE（显式） |
| A-PAG-004 | TOP 语法（LIMIT 仍为默认分页） |
| A-PAG-006 | ROWNUM 分页备选 |
| A-IDN-005 | IDENTITY_MODE 会话参数对接 |

### C. Ruler C 延后
| ID | 主题 |
|---|---|
| C-JSON-006 | json_table（以文档确认后实现） |
| C-SRV-001 | Server configuration 探针（只读/文档允许范围） |
| C-SEL-001 | DialectSelector（若与现有 Resolver 不重复则补齐；已有 SPI 则文档闭环） |

### 明确不做
- 一切 **文档不允许** 行  
- **A-XCUT-012** Maven Central / Ship  
- 继承 MySQL/Oracle Dialect；sibling 移植  

## 验收（提案）
- 上表延后行：实现 + SSOT 从「延后」→ **可实现/covered-live**（或诚实 known-limit + 原因）  
- 负向基线：仅保留 **文档不允许** 的 `@Disabled`/负向；延后锚点改为正向测试  
- `XUGU_RUN_IT=true` 真库绿（连接沿用实库配置）；GAV 仍 `7.4.5.Final`；NONE  
- 不要求 Ship  

## 类型建议
**feature**（能力面扩大；含空间/XML/分区等，产品面大于 hotfix；若你认为产品面过大可选 **major**，仍不升版）

## 请拍板
1. 是否先 **Initiative Accept I-008**（+ Archive）再开 I-009？（推荐：是）  
2. 类型：feature / major？  
3. 上表是否 **全量延后都做**，还是先砍掉某几类（如空间整包 / 分区 / ENCRYPT）？  
4. GAV 不升版？不 Ship？

确认后请给口令：**「本 Initiative 范围已明确，可以开干」**
