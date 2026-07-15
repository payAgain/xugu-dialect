# Current Task

## Goal
I-001 (major)：交付 Hibernate 7.4.5 方言正式 jar + Spring Boot demo + 项目文档；定义 A 全能矩阵。

## Current Status
p006_accepted_propose_b007

## Active Batch / Tasks
- Batch: **B-006** complete (scope was **P-006 only**)
- P-006 status: **accepted** (build_id=B-006; SHA PENDING_MUST_COMMIT)
- P-007 status: **ready** (await Human Gate B-007 approval)
- P-008…P-011 still blocked / unapproved

## Scope
Allowed after Human Gate: **B-007 → P-007 only** (schema/temp/comment/FK)
Not allowed:
- P-008+ until P-007 ACCEPTANCE + next Build approval
- tag / push / Central 发布 without Human Gate

## Plan
1. ~~P-006 RP-01/02/03 + ACCEPTANCE~~ DONE
2. Human Gate approve B-007 scope P-007 only
3. Materialize B-007; dispatch P-007 role_pipeline

## Validation Commands
```text
mvn -q test
mvn -q test -Dxugu.run.integration=true
python harness/scripts/verify.py
```

## Acceptance Criteria
- [x] B-006 approved for P-006 only
- [x] P-006 RP-01/02/03 PASS + ACCEPTANCE
- [x] Must-commit on working branch after Accept (PENDING_MUST_COMMIT → fill SHA)
- [ ] Human Gate approve B-007 scope P-007 only

## Risks / Blockers
- risk_score=8 → reviewer (RP-03) required for P-007
- No Java in orchestrator Approve turn

## Next 3 Steps
1. Human Gate：批准 B-007，范围仅 P-007
2. 物化 B-007.json；P-007 in_progress；派发 role_pipeline
3. 勿并行批准 P-008+

## Last Updated
2026-07-15T11:05:00+08:00（角色：orchestrator Accept）
