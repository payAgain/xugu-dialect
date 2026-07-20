# I-008 Scope Clarifying — 生产质量短板补全（除性能外全闭环）

> Status: **clarifying**（待 PASS 口令）  
> Updated: 2026-07-20T15:52:00+08:00  
> Human Gate：「第五条不考虑先，其余的我需要全部解决。」

## 一句话目标
除**性能/并发/多版本库证据**外，评估所列质量短板 **全部闭环**（产品口径 + 真库证据 + Boot 开箱对齐）；不做 CI/Ship；不做多兼容模式。

## 短板验收定义（本 Initiative 的「解决」含义）

| # | 短板 | 「全部解决」的验收标准 | 不做 |
|---|---|---|---|
| **Q1** | covered ≠ covered-live；指南高估 | ① 用户指南/契约**禁止**再写「94 covered-live」类膨胀口径；② SSOT 公布准确计数（covered / covered-live / unit-only）；③ **全部 94 可实现行**凡可门控真库验证的，升为 **covered-live**（有 `XUGU_RUN_IT` IT 入口且 Accept 时绿）。确因环境条件无法 live 的行须标 `known-limit-documented` + 原因，**不得**静默当 covered-live | 不靠改文案假装已 live |
| **Q2** | 锁语义 | ① 集成须知/排障专节：无 SKIP LOCKED、无 FOR SHARE、`PESSIMISTIC_READ`→排他 `FOR UPDATE`；② 负向/行为真库或等价断言证明方言**不会**发出禁止语法，且 READ 锁 SQL 形态符合文档；③ 矩阵/指南交叉一致 | **不**在引擎外伪造 SKIP LOCKED / FOR SHARE 能力 |
| **Q3** | UUID / JSON 开箱 | ① Demo/Boot **默认依赖与示例配置**已含 UUID 规避路径（Converter + 列映射）与 JSON 所需 Jackson `FormatMapper`；② 门控真库或 Boot IT 证明该开箱路径可跑通；③ 用户指南「必配清单」与 demo 一致 | 不要求改 XuGu JDBC 本身 |
| **Q4** | 离线绿 ≠ 生产证明 | ① `03-verify` 写明：仅离线 `mvn test` **不足以**声称生产验证；② Accept 必附 **全 reactor 真库**证据（与 I-007/P-006 同级计数）；③ 黄金路径清单（分页/锁/SEQUENCE/IDENTITY/UUID/JSON/常用 HQL）真库 PASS | 不做 CI 强制门禁（按你要求不考虑发布流程） |
| **Q5** | 性能/并发/多版本 | **本 Initiative 明确 Out-of-scope**；文档一句声明「未做容量/多版本矩阵」即可 | 不建 perf suite |

## 深度锁定
**A（口径/须知）+ B（94 可实现 → covered-live）+ Boot 开箱对齐（Q3）+ Accept 真库证明（Q4）**  
相对旧「仅深度 A」：**Q1/Q3/Q4 按上表全闭环，不只改文档。**

## Out-of-scope
- 性能基准 / 并发压测 / 多 XuGu 版本矩阵（Q5）  
- CI / Ship / Central  
- 多兼容模式方言补丁  
- 继承 MySQL/Oracle Dialect；空间/XML 等 Charter 延后项  

## 约束
- GAV `7.4.5.Final`；`compatiblemode=NONE`  
- 原生 `XuguDialect`

## 决策（请确认）

| # | 决策 | 取值（按你最新答复） |
|---|---|---|
| 0 | Archive I-007 | **待你选 A/B**（推荐 A 是） |
| 1 | 类型 | **feature**（推荐） |
| 2 | 深度 | **全闭环 Q1–Q4**（上表）；Q5 不做 |
| 3 | GAV | 不升版 `7.4.5.Final` |
| 4 | Ship | 不 Ship（推荐） |

确认后请回复（含是否 Archive I-007），并给口令：  
**「本 Initiative 范围已明确，可以开干」**
