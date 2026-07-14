# Handoff: Round A Charter — Human approval required

**Role:** architect-contract  
**When:** 2026-07-14T15:10:00+08:00  
**Stage:** Clarify PASS → **Charter (Round A draft ready)**  
**Next actor:** Human Gate → then orchestrator (promote root Charter + Bootstrap)

## What was done

1. Drafted `harness/drafts/PROJECT_CHARTER.md` from Intent Clarity PASS.
2. Updated `harness/drafts/INTENT-CLARITY.md` Status to record human PASS phrase and Round A readiness.
3. Drafted `harness/drafts/ADR-0001-hibernate-baseline.md` (Hibernate 7.4.5 / GAV / package / no-inherit MySQL|Oracle).

## What human must approve

Approve the **Round A product Charter** (and optionally ADR-0001) so orchestrator may:

- Write root `PROJECT_CHARTER.md` from the approved draft
- Proceed to **Bootstrap (G1)**

**Do not approve** if any locked decision below is wrong.

### Locked decisions to confirm

- From-scratch dialect in this repo; **no** reference/port of `hibernate-dialect` / old xugu-dialect
- Structure like MySQL/Oracle Dialect modules; **do not** extend those Dialects
- SQL truth: `E:\Work\docs\content` (read-only; no rewrite)
- Hibernate **7.4.5.Final** + JDK **17** + GAV **`com.xugu:xugu-dialect:7.4.5.Final`**
- Package `com.xugu.dialect` / `XuguDialect` + DialectResolver SPI
- Feature coverage **definition A**
- Real XuguDB for IT; JDBC `xugu-jdbc-12.3.6.jar`; default `compatible_mode=NONE`
- Deliverables: jar + Spring Boot demo + project-local docs
- Out: Hibernate 6.x / 8 beta; Central publish creds (Ship later)

## Exact approval question

见本 handoff 末尾「Human approval question」——请人类用明确短语回复批准或驳回。

## Files touched (allowed paths only)

- `harness/drafts/PROJECT_CHARTER.md` (new)
- `harness/drafts/INTENT-CLARITY.md` (Status update)
- `harness/drafts/ADR-0001-hibernate-baseline.md` (new)
- `harness/handoffs/architect-contract/round-a-charter.md` (this file)

## Human approval question

**请确认：是否批准 `harness/drafts/PROJECT_CHARTER.md` 作为本产品 Round A Charter，并授权 Orchestrator 将其提升为根 `PROJECT_CHARTER.md` 后进入 Bootstrap？**  
（可选同时批准 `ADR-0001`。）  
批准请回复例如：「批准 Round A Charter，可以 Bootstrap」；驳回请列出需修改条款。
