# Current Task

## Goal
I-001 (major)：虚谷 Hibernate 7.4.5 方言正式 jar + Spring Boot demo + 项目文档；定义 A 全能矩阵。

## Current Status
b004_p004_accepted_await_b005

## Active Batch / Tasks
- Batch: **B-004** complete (scope was **P-004 only**)
- P-004 status: **accepted** (`build_id=B-004`; must-commit SHA pending record)
- P-005 status: **ready** (blocked dependency cleared)
- Proposed next: **B-005 → P-005 only** (Identity & Sequence) — awaiting Human Gate
- P-006…P-011 still blocked / unapproved

## Scope
Allowed now: Human Gate Scope for B-005 (P-005 only)
Not allowed:
- P-005+ implementation until B-005 approved
- `tag` / `push` / Central 发布 without Human Gate

## Plan
1. ~~Scope PASS~~ DONE
2. ~~B-004 / P-004 role_pipeline~~ DONE (incl. fix + retest + recheck approve)
3. ~~P-004 ACCEPTANCE + must-commit~~ DONE this turn
4. **Await Human Gate approve B-005 → P-005 only**
5. On approve → materialize B-005; dispatch P-005 pipeline

## Validation Commands
```text
mvn -q test
mvn -q test -Dxugu.run.integration=true
python harness/scripts/verify.py
```

## Acceptance Criteria
- [x] B-004 approved for P-004 only
- [x] P-004 ACCEPTANCE (VERIFY PASS + real DB IT + reviewer approve + must-commit)
- [ ] B-005 approved for P-005 only (Human Gate)

## Risks / Blockers
- None for P-004 close
- P-005 not started until B-005 Scope approval

## Next 3 Steps
1. Human Gate：批准 B-005，范围仅 P-005（Identity & Sequence）
2. 物化 B-005.json；P-005 in_progress；派发 role_pipeline
3. 勿并行批准 P-006+

## Last Updated
2026-07-15T09:45:00+08:00（角色：orchestrator Accept）
