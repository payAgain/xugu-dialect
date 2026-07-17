# P-005 Test Acceptance (RP-02)

> Phase: `P-005` | Initiative: `I-005` | Build: `B-001`  
> **Verdict:** PASS (offline gate)

## Commands

| Command | Exit | Result |
|---|---:|---|
| `mvn -q test` (IT gate OFF) | 0 | **PASS** |
| `mvn -q -pl demo-spring-boot -am test` | 0 | **PASS** |
| `mvn -q -pl demo-spring-boot test -Dxugu.run.integration=true` | 1 | **SKIPPED_INFRA** — no live XuguDB (E50027) |

## Demo module counts (offline)

| Class | Run | Fail | Skip |
|---|---:|---:|---:|
| `DemoOfflineSmokeTest` | 2 | 0 | 0 |
| `DemoPersonCrudIT` | 1 | 0 | 1 |
| `DemoBootBaselineSmokeTest` | 4 | 0 | 4 |

## SSOT

- `A-XCUT-009` maps offline + gated demo entrypoints in `contracts/production-regression-baseline.md`
- Demo smoke call-out section documents offline vs gated live classes

## Condition / Action / Result

**Condition:** Default `mvn test` green; SSOT lists demo paths.  
**Action:** Run demo + reactor tests; verify gated IT skipped when gate OFF.  
**Result:** PASS offline; live smoke wiring present, infra not available in this environment.

## Acceptance decision

- Decision: `accepted`
- Decided by: orchestrator
- Date: 2026-07-17T17:45:00+08:00
- Phase verification: `harness/evidence/test/I-005/P-005/verification.json`
- Reviewer audit: `harness/evidence/reviewer/I-005/P-005/REVIEW.md` (PASS)
