# TEST-REPORT — I-010 P-010 RP-02

- **invocation_id:** inv-i010-p010-rp02-test
- **branch:** `feat/i-010-orm-hql-quality-completion`
- **base tip (pre-commit):** `48a8fa13de19ed7a24be743bf7d354c73435a6b0`
- **completed_at:** 2026-07-21T16:02:05+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| harness_check | `python harness/scripts/harness_check.py` | 0 | **HARNESS_CHECK PASS** |
| branch_check | `python harness/scripts/branch_check.py` | 0 | **BRANCH_CHECK PASS** |
| verify.py | `python harness/scripts/verify.py --phase P-010 --evidence harness/evidence/test/I-010/P-010/verification.json` | 0 | **VERIFY PASS** |
| live full reactor | `XUGU_RUN_IT=true mvn -q test` | n/a | **SKIPPED_INFRA** |

Maven (via verify.py): `C:\Users\admin\tools\apache-maven-3.9.9\bin\mvn.cmd`

## VERIFY output (tail)

```text
=== HARNESS ===
=== BUILD ===
$ C:\Users\admin\tools\apache-maven-3.9.9\bin\mvn.cmd -q -DskipTests package
=== TEST ===
$ C:\Users\admin\tools\apache-maven-3.9.9\bin\mvn.cmd -q test
=== LINT [NOT_APPLICABLE] ===
Evidence: E:\Work\java\hibernate-test\harness\evidence\test\I-010\P-010\verification.json
VERIFY PASS
```

Full capture: `verify-output.txt`

## Live full-reactor IT

| Probe | Result |
|---|---|
| TCP 192.168.2.239:5138 | **closed** (`TcpTestSucceeded=False`) |
| TCP 127.0.0.1:5138 | **closed** (`TcpTestSucceeded=False`) |
| Live IT | **not attempted** |

**SKIPPED_INFRA** — not a product assertion failure. Offline **VERIFY PASS** satisfies P-010 harness gate.

## Honest residual (SSOT)

| Row / family | Status after P-010 |
|---|---|
| A-TYP-014 / 016 / 017 | **known-limit-documented** (entity ORM IT present; live pending) |
| Batch A A-FUN-003/005/006/007/009 | **known-limit-documented** (independent HQL IT present; live pending) |
| A-FUN-021 | **known-limit-documented** (XMLTABLE) |
| A-FUN-019 / 020 | **covered-live** retained |

## Observed flow

- **i010-quality-completion-accept-prep:** offline build + test + **VERIFY PASS**; live **SKIPPED_INFRA**

## Verdict

**PASS** — required offline gates green; live IT infra-blocked (documented). **NOT Ship.**

## Artifacts

- `verification.json`
- `TEST-REPORT.md`
- `verify-output.txt`
- `live-db-probe.txt`
- `IT-RESULT.txt`
