# I-006 Initiative Accept checklist (prep — NOT Ship)

> Prepared by: implementer P-005; refreshed by orchestrator on P-005 Accept  
> Date: 2026-07-19T00:10:00+08:00  
> Purpose: Human Gate may **Initiative Accept** I-006.  
> **Ship / tag / push / Maven Central remain unchecked.**

## Prerequisite Phases

| Phase | Topic | Status | SHA |
|---|---|---|---|
| P-001 | SSOT invent / freeze Boot-required 41 | accepted | `e5f2428` |
| P-002 | Layer A | accepted | `9f4cbd6` |
| P-003 | Layer B (B-both) | accepted | `3aae8f0` |
| P-004 | Layer C′ | accepted | `929be22` |
| P-005 | Docs + VERIFY Accept prep | accepted | `7566e1c` |

## Functional / SSOT

- [x] Boot-required rows = **41** (A=13 / B=9 / C′=19)
- [x] Open Boot-required gaps = **0**
- [x] Not expanded to full 94-row Boot mirror
- [x] Exclusion appendix remains `dialect-it-only` (incl. A-TYP-011)
- [x] Demo `@Test` ≈ **28** (band 25–40)
- [x] GAV still `com.xugu:xugu-dialect:7.4.5.Final`

## Docs ↔ SSOT ↔ Demo

- [x] User guide: [`docs/user-guide/06-consumer-path.md`](../../../../docs/user-guide/06-consumer-path.md)
- [x] Docs pointer: [`docs/consumer-path-baseline.md`](../../../../docs/consumer-path-baseline.md)
- [x] Verify notes: [`docs/user-guide/03-verify.md`](../../../../docs/user-guide/03-verify.md) + [`docs/verification.md`](../../../../docs/verification.md)
- [x] Root README + [`demo-spring-boot/README.md`](../../../../demo-spring-boot/README.md) cross-links
- [x] SSOT status **FROZEN** with user-guide link

## Verification (RP-02)

- [x] `mvn -q -DskipTests package` OK
- [x] `mvn -q test` offline green (demo 28/0/0/23)
- [x] `python harness/scripts/verify.py` → **VERIFY PASS** (`harness/evidence/test/I-006/P-005/verification.json`)
- [x] When DB available: `XUGU_RUN_IT=true mvn -pl demo-spring-boot -am test` green (demo 28/0/0/0)
- [x] Evidence: `harness/evidence/test/I-006/P-005/`

## Explicitly out of scope (must stay unchecked)

- [ ] Ship / release tag
- [ ] `git push` to protected / publish remotes as Ship gate
- [ ] Maven Central publish
- [ ] GAV bump beyond `7.4.5.Final`

## Human Gate next

1. ~~Complete P-005 RP-02/RP-03~~ **done**
2. ~~Orchestrator Accept P-005 + must-commit~~ **in this commit**
3. Propose **Initiative Accept** for I-006 (**without** Ship)
