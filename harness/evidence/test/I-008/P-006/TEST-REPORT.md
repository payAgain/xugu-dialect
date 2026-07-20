# TEST-REPORT — I-008 P-006 RP-02

- **invocation_id:** inv-i008-p006-rp02-test
- **branch:** feat/i-008-production-quality-gaps
- **HEAD:** be81a96
- **completed_at:** 2026-07-20T17:43:37+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| package | `mvn -q -DskipTests package` | 0 | PASS |
| demo Q3 focus | `mvn -q -pl demo-spring-boot test -Dtest=DemoOfflineSmokeTest,DemoUuidJsonOutOfBoxIT` | 0 | PASS |
| test offline | `mvn -q test` (XUGU_RUN_IT unset) | 0 | PASS |
| live Boot IT | `XUGU_RUN_IT=true mvn -q -pl demo-spring-boot test -Dtest=DemoUuidJsonOutOfBoxIT` | 1 | SKIPPED_INFRA |
| harness | `python harness/scripts/harness_check.py` | 1 | FAIL (P-004.md UTF-8 BOM) |
| verify | `python harness/scripts/verify.py --phase P-006` | 1 | VERIFY FAIL (harness gate) |

## Demo Q3 focus (offline)

| Class | Run | Fail | Error | Skip |
|-------|-----|------|-------|------|
| `DemoOfflineSmokeTest` | 8 | 0 | 0 | 0 |
| `DemoUuidJsonOutOfBoxIT` | 2 | 0 | 0 | 2 (gated) |

Offline smoke anchors: `applicationYmlDocumentsExplicitDialectAndEnvKeys`, `jacksonOnClasspathForHibernateJsonFormatMapper`, `typedSampleGuidUsesUuidAsVarcharConverter`.

## Full offline reactor

- dialect: 215 run / 0 fail / 0 error / 67 skipped (stale `XuguTrackCProbeIT` report on disk; `mvn test` exit 0)
- demo-spring-boot: 36 run / 0 fail / 0 error / 28 skipped

## Live Boot UUID/JSON IT

`127.0.0.1:5138` unreachable (`live-db-probe.txt`). With `XUGU_RUN_IT=true`, static init on `DemoUuidJsonOutOfBoxIT` failed on JDBC *Connection refused* before tests could run. Real-DB golden path not validated this run.

IT anchors (pending live DB): `defaultApplicationYmlExposesUuidJsonChecklist`, `uuidAndJsonGoldenPathWithDefaultBootWiring`.

## Observed flow

- **i008-q3-boot-uuid-json-out-of-box-wiring-and-it:** partial — offline Q3 smokes + gated skip green; live Boot IT SKIPPED_INFRA

## Artifacts

- `harness/evidence/test/I-008/P-006/verification.json`
- `harness/evidence/test/I-008/P-006/TEST-REPORT.md`
- `harness/evidence/test/I-008/P-006/mvn-package-offline.txt`
- `harness/evidence/test/I-008/P-006/mvn-test-demo-focused.txt`
- `harness/evidence/test/I-008/P-006/mvn-test-offline.txt`
- `harness/evidence/test/I-008/P-006/mvn-test-live-demo-uuid-json-it.txt`
- `harness/evidence/test/I-008/P-006/surefire-summary-demo-focused.txt`
- `harness/evidence/test/I-008/P-006/IT-RESULT.txt`
- `harness/evidence/test/I-008/P-006/live-db-probe.txt`
- `harness/evidence/test/I-008/P-006/verify-harness.txt`
- `harness/evidence/test/I-008/P-006/verify.txt`
- `harness/evidence/test/I-008/P-006/verification-verifypy.json`
