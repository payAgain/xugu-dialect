# Current Task

## Goal
I-003 (feature): 尺子 C 生产能力补齐（异常 / JSON+Aggregate / Window+CTE / bulk / 类型 DDL）；入口 IT；版本 **7.4.5.Final**；**不改** harness 框架

## Current Status
I-002 **archived**. I-003 **active** — Plan ready; awaiting Human Gate **批准 B-001，范围仅 P-001**

## Active Batch / Tasks
- Initiative: **I-003** feature — **active**
- Branch: `feat/i-003-production-capability-parity`
- Build: **B-001** `draft`（proposed P-001 only）
- Phase: **P-001** `ready`（gap inventory）；P-002…P-007 `blocked`
- Prior: I-002 archived

## Scope
In progress (Plan locked):
- P-001 ruler-C SSOT → P-002 exception → P-003 JSON/Aggregate → P-004 Window/CTE → P-005 bulk → P-006 type/DDL → P-007 docs/Accept prep
Forbidden:
- Harness framework harden；旁路代码移植；Ship / 升版本

## Plan
1. Human Gate：批准 B-001，范围仅 P-001？
2. P-001 architect-contract → reviewer → Accept + must-commit
3. 再提案 B-002（P-002 only）…

## Validation Commands
```text
python harness/scripts/harness_check.py
python harness/scripts/branch_check.py
# After implement Phases:
mvn -q test
mvn -q test -Dxugu.run.integration=true
python harness/scripts/verify.py
```

## Next 3 Steps
1. Human Gate：是否批准 **B-001**，范围仅 **P-001**？
2. 批准后派发 architect-contract RP-01（差集盘点）
3. 禁止实现 P-002… 直至各自 Build 批准

## Last Updated
2026-07-16T10:46:00+08:00
