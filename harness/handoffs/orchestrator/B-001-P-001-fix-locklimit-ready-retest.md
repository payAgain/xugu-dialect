# B-001 / P-001 — Implementer fix landed; ready for re-test

**Date:** 2026-07-15T18:23:00+08:00  
**Role:** orchestrator  
**Build / Phase:** B-001 / P-001 (I-002)  
**Prior RP-03:** `rev-p001-20260715` → **request-changes** (failed)  
**Fix invocation:** `impl-p001-fix-locklimit-20260715` (RP-01b **passed**)  
**Accept:** **NOT authorized** — re-test then re-review first.

## What closed MAJOR

Gated IT `XuguHqlPaginationIT.hqlLockAndPageEmitsForUpdateBeforeLimitAndWaitAfter` proves AST/HQL path:

| Scenario | Observed SQL |
|---|---|
| lock + page | `… for update of phpe1_0.id limit ? offset ?` |
| lock + page + WAIT | `… for update of phpe1_0.id limit ? offset ? wait 2000` |

Order matches `XuguLimitHandler` (FOR UPDATE → LIMIT → WAIT). Live execute EXIT 0.

## Validation (implementer)

- `mvn -q test` → EXIT 0
- `mvn -q test -Dxugu.run.integration=true` → EXIT 0
- `verify.py --phase P-001` (implementer evidence) → **VERIFY PASS**

## Next dispatch

1. **test** (independent) — re-run offline + gated IT + `verify.py --phase P-001` → `harness/evidence/test/P-001/`
2. **reviewer** recheck (new invocation, e.g. `rev-p001-recheck-20260715`) — confirm MAJOR CLOSED
3. Only on approve → Accept + must-commit

## Out of scope

- Accept / commit / Ship
- P-002 sequence metadata
- Sibling dialect port
