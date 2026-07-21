# TEST-REPORT — I-009 P-011 RP-02

- **invocation_id:** inv-i009-p011-rp02-test
- **branch:** feat/i-009-deferred-matrix-delivery
- **HEAD:** 882b84e1d902e2f696c722d18aa49ebfb5a72f40
- **completed_at:** 2026-07-21T11:34:00+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| package | `mvn -q -DskipTests package` | 0 | PASS |
| test offline | `mvn -q test` (XUGU_RUN_IT unset) | 0 | PASS |
| verify.py | `python harness/scripts/verify.py --phase P-011 --evidence harness/evidence/test/I-009/P-011/verification.json` | 0 | **VERIFY PASS** |
| live full reactor | `XUGU_RUN_IT=true mvn -q test` @ 192.168.2.239:5138 | 1 | **SKIPPED_INFRA** |

Maven: `C:\Users\admin\tools\apache-maven-3.9.9\bin\mvn.cmd` (on PATH for verify.py)

## Harness prep

- Fixed P-001 ACCEPTANCE `- Decision: \`accepted\`` for harness_check semantic contract
- Stripped UTF-8 BOM from `harness/evidence/test/I-009/P-003/verification.json` and `P-004/verification.json`

## Offline test counts (surefire aggregate, gate OFF)

| Module | Run | Fail | Error | Skip |
|--------|-----|------|-------|------|
| dialect | 208 | 0 | 0 | 60 |
| demo-spring-boot | 36 | 0 | 0 | 28 |

## Live full-reactor IT

| Probe | Result |
|---|---|
| TCP 192.168.2.239:5138 | **closed** (connect_ex=10061) |
| JDBC | Connection refused [E50025] |
| First IT failure | `XuguAlterSequenceIT#alterSequenceStartWithAndIncrement_A_SEQ_006` |

**SKIPPED_INFRA** — XuguDB at `jdbc:xugu://192.168.2.239:5138/SYSTEM?...` unreachable. Offline **VERIFY PASS** satisfies P-011 harness gate; live log archived for Accept prep when infra returns.

Artifacts: `live-db-probe.txt`, `mvn-test-live-full-reactor.txt`, `IT-RESULT.txt`

## Observed flow

- **i009-full-reactor-live-evidence-verify-pass-accept-prep:** offline build + test + **VERIFY PASS**; live **SKIPPED_INFRA**

## Verdict

**PASS** — required offline gates green; live IT infra-blocked (documented).

## Artifacts

- harness/evidence/test/I-009/P-011/verification.json
- harness/evidence/test/I-009/P-011/TEST-REPORT.md
- harness/evidence/test/I-009/P-011/mvn-package.txt
- harness/evidence/test/I-009/P-011/mvn-test-offline.txt
- harness/evidence/test/I-009/P-011/verify-output.txt
- harness/evidence/test/I-009/P-011/live-db-probe.txt
- harness/evidence/test/I-009/P-011/mvn-test-live-full-reactor.txt
- harness/evidence/test/I-009/P-011/IT-RESULT.txt
