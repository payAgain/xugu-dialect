# P-007 Test Report (RP-02) — I-003 docs/matrix align

| Field | Value |
|-------|-------|
| Initiative | I-003 |
| Build | B-007 |
| Phase | P-007 — matrix/docs align + full VERIFY |
| Role | test / RP-02 |
| Branch | feat/i-003-production-capability-parity |
| HEAD (at run) | 7317c995f7a0943e5ba34a8a54f602e1b7a31a06 |
| invocation_id | test-p007-20260716 |
| Completed | 2026-07-16T17:40:00+08:00 |
| Product code changes by test | none |

## Scope

Independent full VERIFY after implementer RP-01 (docs-only): matrix/user-guide/README aligned with I-003 P-002…P-006. Observed flow: `docs-matrix-aligned-full-verify`. No dialect rewrite expected.

## Command matrix

| Step | Command | Exit |
|------|---------|------|
| Build | `mvn -q -DskipTests package` | 0 |
| Unit/offline tests | `mvn -q test` | 0 |
| Integration tests | `mvn -q test "-Dxugu.run.integration=true"` | 0 |
| Verify | `python harness/scripts/verify.py --phase P-007 --evidence harness/evidence/test/P-007/verification.json` | 0 — **VERIFY PASS** |
| Harness check | `python harness/scripts/harness_check.py` | 0 |
| Branch check | `python harness/scripts/branch_check.py` | 0 |

## Surefire totals

| Mode | tests | failures | errors | skipped |
|------|------:|---------:|-------:|--------:|
| Offline (`mvn -q test`) | 99 | 0 | 0 | 28 |
| Integration gate ON | 99 | 0 | 0 | 0 |

Live DB: `jdbc:xugu://127.0.0.1:5138/SYSTEM` (XuguDB 12.0, XuguDialect, Hibernate 7.4.5.Final).

I-003 IT classes exercised under gate ON (among full suite): `XuguExceptionMappingIT`, `XuguJsonAggregateIT`, `XuguWindowCteIT`, `XuguBulkMutationIT`, `XuguTypeDdlDetailsIT`, plus prior ITs and `DemoPersonCrudIT`.

## Observed flow: docs-matrix-aligned-full-verify

| Check | Result | Evidence |
|-------|--------|----------|
| Full package + offline + IT + verify | PASS | `mvn-package.log`, `mvn-test-offline.log`, `mvn-test-integration.log`, `verification.json` |
| Docs mention I-003 ruler-C matrix | PASS | README + `contracts/feature-matrix-i003-ruler-c.md` + user-guide pointers (`spot-check-docs.txt`) |
| GAV / Hibernate **7.4.5.Final** unchanged | PASS | root `pom.xml` / `dialect/pom.xml` version + Hibernate log `HHH000001: … 7.4.5.Final` |

## Product code changes by test

None. Docs-only Phase; no test assertion breakage observed.

## Verdict

**PASS** — all required commands exit 0; **VERIFY PASS**; docs/matrix spot-checks OK; version **7.4.5.Final** unchanged.
