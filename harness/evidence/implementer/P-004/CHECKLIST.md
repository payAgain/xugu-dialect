# P-004 Implementer Checklist (RP-01 / RP-01b)

- [x] XuguLimitHandler + getLimitHandler()
- [x] Stable form documented: LIMIT count OFFSET offset
- [x] Bind parameters (no value concat)
- [x] No FETCH FIRST
- [x] FOR UPDATE / OF / NOWAIT / WAIT ms
- [x] supportsSkipLocked=false; no SKIP LOCKED keyword
- [x] No FOR SHARE (shim docs: exclusive FOR UPDATE; matrix 文档不允许)
- [x] Unit tests (XuguPaginationLockTest) — XuGu FOR UPDATE→LIMIT→WAIT order
- [x] Gated IT (XuguPaginationIT, XuguLockIT + limitForUpdateComboExecutes)
- [x] Matrix hints updated (A-LCK-005 limitations + LIMIT/lock order)
- [x] Evidence + handoff `impl-p004-20260715` then `impl-p004-fix-20260715`
- [x] MAJOR fixes after `rev-p004-20260715` request-changes
- [ ] RP-02 independent re-test (after fix)
- [ ] RP-03 reviewer re-review (new rev-p004-*)
- [ ] Accept / must-commit (Human Gate / orchestrator) — **blocked**
