# I-007 / P-006 Test Report (test role RP-02)

> Phase: `P-006`  
> Initiative: `I-007`  
> Build: `B-001`  
> Invocation: `inv-i007-p006-rp02-test`  
> Step: `RP-02`  
> Role: `test` (independent of implementer RP-01)  
> Verdict: **PASS**  
> Date: 2026-07-19

## Environment

| Item | Value |
|---|---|
| Maven | Apache Maven 3.9.9 (`C:\Users\admin\tools\apache-maven-3.9.9`) |
| Working directory | `E:\Work\java\hibernate-test` |
| Branch | `feat/i-007-capability-hardening-abc` |
| HEAD | `f713248` |
| Closure path | **Docs align + VERIFY PASS Accept prep** (I-007 Initiative Accept readiness) |
| Product code changes by test | none |
| `org/` touched by test | no |

## P-006 deliverable verification

| Check | Result | Evidence |
|---|---|---|
| Offline build green | **PASS** | `mvn-package-offline.txt` exit 0 |
| Offline reactor test green | **PASS** | dialect 141/0/0/54; demo 32/0/0/26 |
| `verify.py` VERIFY PASS | **PASS** | `verification.json`, `verify.txt` |
| Full reactor live (`XUGU_RUN_IT=true`) | **PASS** | `mvn-test-live-it-final.txt` exit 0 |
| Docs ↔ SSOT aligned (spot-check) | **PASS** | implementer RP-01 artifacts + docs paths below |
| GAV `7.4.5.Final` unchanged | **PASS** | parent/dialect/demo POMs |
| Initiative Accept checklist present | **PASS** | `INITIATIVE-ACCEPT-CHECKLIST.md` |
| Ship out of scope | **PASS** | checklist + task constraints |

## Commands and exit codes

| # | Command | Exit | Result |
|---|---|---:|---|
| 1 | `mvn -q -DskipTests package` | 0 | **PASS** |
| 2 | `mvn -q test` (gate off) | 0 | **PASS** |
| 3 | `$env:XUGU_RUN_IT='true'; mvn -q test` | 0 | **PASS** |
| 4 | `python harness/scripts/verify.py --phase P-006 --evidence harness/evidence/test/I-007/P-006/verification.json` | 0 | **VERIFY PASS** |

## Test counts

### Offline (`XUGU_RUN_IT` unset)

| Module | Tests run | Failures | Errors | Skipped |
|---|---:|---:|---:|---:|
| dialect (excl. stale probe) | 141 | 0 | 0 | 54 |
| demo-spring-boot | 32 | 0 | 0 | 26 |
| reactor sum | 173 | 0 | 0 | 80 |

Gated ITs skipped under gate off — expected.

### Live (`XUGU_RUN_IT=true`, full reactor)

| Field | Value |
|---|---|
| Status | **ran — PASS** (not SKIPPED_INFRA) |
| Probe | TCP `127.0.0.1:5138` OK (`live-db-probe.txt`) |
| Command | `XUGU_RUN_IT=true mvn -q test` |
| dialect (excl. stale probe) | 141 / 0 / 0 / 18 |
| demo | 32 / 0 / 0 / 0 |
| Log | `mvn-test-live-it-final.txt`, `IT-RESULT.txt`, `surefire-summary-live-reactor.txt` |

Prior Phase live evidence (audited pointers):

| Phase | Track | Evidence |
|---|---|---|
| P-002 | A — C-BULK-002 | `harness/evidence/test/I-007/P-002/surefire-summary-live-dialect.txt` |
| P-004 | C — JSON/ARRAY/ALTER SEQ | `harness/evidence/test/I-007/P-004/surefire-summary-live-dialect.txt` |
| P-005 | B — Flyway/Demo deepening | `harness/evidence/test/I-007/P-005/surefire-summary-live-demo.txt` |

## Docs spot-check (read-only)

| Check | Result |
|---|---|
| C-BULK-002 = **covered-live** (not known-limit) in `03-verify.md` / `04-feature-matrix.md` / `verification.md` | **YES** |
| Track C xref `p004-track-c-capabilities.md` | **YES** |
| Track B in `06-consumer-path.md` (Flyway offline method aligned) | **YES** |
| Ruler C rollup — C-JSON-005 / C-DDL-005 not 延后 | **YES** |
| Initiative Accept checklist (NOT Ship) | **YES** |
| GAV `com.xugu:xugu-dialect:7.4.5.Final` in docs/README | **YES** |

## GAV confirmation

| Artifact | Version |
|---|---|
| `com.xugu:xugu-dialect-parent` | `7.4.5.Final` |
| `com.xugu:xugu-dialect` | `7.4.5.Final` |
| `demo-spring-boot` (inherits parent) | `7.4.5.Final` |
| `hibernate.version` property | `7.4.5.Final` |

**Unchanged** — no bump detected.

## Hygiene

| Check | Result |
|---|---|
| Root `org/` touched by test | **no** |
| Root `META-INF/` touched by test | **no** |
| Product Java edited by test | **none** |
| Commit / Ship by test | **none** |

## Artifacts

- `harness/evidence/test/I-007/P-006/mvn-package-offline.txt`
- `harness/evidence/test/I-007/P-006/mvn-test-offline.txt`
- `harness/evidence/test/I-007/P-006/mvn-test-live-it-final.txt`
- `harness/evidence/test/I-007/P-006/surefire-summary-offline.txt`
- `harness/evidence/test/I-007/P-006/surefire-summary-live-reactor.txt`
- `harness/evidence/test/I-007/P-006/IT-RESULT.txt`
- `harness/evidence/test/I-007/P-006/live-db-probe.txt`
- `harness/evidence/test/I-007/P-006/verify.txt`
- `harness/evidence/test/I-007/P-006/verification.json`
- `harness/handoffs/test/I-007-P-006.yaml`

## Verdict

**RP-02 PASS.** Independent verification confirms implementer RP-01 docs-align claim: offline reactor green, `verify.py` **VERIFY PASS**, full reactor live green against reachable XuGuDB, GAV `7.4.5.Final` unchanged, Initiative Accept checklist ready. Advance to RP-03 reviewer. No commit / Accept / Ship by test.
