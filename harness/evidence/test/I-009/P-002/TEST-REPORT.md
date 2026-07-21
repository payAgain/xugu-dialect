# TEST-REPORT — I-009 P-002 RP-02

- **invocation_id:** inv-i009-p002-rp02-test
- **branch:** feat/i-009-deferred-matrix-delivery
- **HEAD:** dde2fc2
- **completed_at:** 2026-07-21T10:06:29+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| package | `mvn -q -DskipTests package` | 0 | PASS |
| test offline | `mvn -q test` (`XUGU_RUN_IT` unset) | 0 | PASS |
| live INTERVAL IT | `XUGU_RUN_IT=true` + `XUGU_JDBC_URL=jdbc:xugu://192.168.2.239:5138/...` + `mvn -q test -Dtest=XuguIntervalTypeIT` | 1 | SKIPPED_INFRA |

## P-002 focus (A-TYP-014 INTERVAL)

| Class | Run | Fail | Error | Skip |
|-------|-----|------|-------|------|
| `XuguIntervalTypeTest` | 2 | 0 | 0 | 0 |
| `XuguIntervalTypeIT` | 1 | 0 | 0 | 1 (offline gate) |

Offline unit anchors: `intervalTypeHooksWired_A_TYP_014`, `allDocumentedSubtypesLocked_A_TYP_014`.

## Full offline reactor

- **dialect:** reactor exit 0; surefire aggregate 172 run / 57 skipped (IT gated). Prior live attempt may leave stale failure XML on disk — Maven result is authoritative.
- **demo-spring-boot:** 36 run / 0 fail / 0 error / 28 skipped

## Live INTERVAL IT

`192.168.2.239:5138` unreachable (`live-db-probe.txt`: `TcpTestSucceeded=False`). With `XUGU_RUN_IT=true`, `intervalNativeRoundTrip_A_TYP_014` failed on JDBC *Connection refused* (E50025) before native round-trip could run. **SKIPPED_INFRA** — not a product assertion failure.

IT anchor (pending live DB): `com.xugu.dialect.it.XuguIntervalTypeIT#intervalNativeRoundTrip_A_TYP_014`.

## Observed flow

- **i009-a-typ-014-interval-dialect-and-live-it:** offline INTERVAL unit tests green; gated IT skip green; live native round-trip not validated (infra down)

## Artifacts

- `harness/evidence/test/I-009/P-002/verification.json`
- `harness/evidence/test/I-009/P-002/TEST-REPORT.md`
- `harness/evidence/test/I-009/P-002/mvn-package.txt`
- `harness/evidence/test/I-009/P-002/mvn-test-offline-fresh.txt`
- `harness/evidence/test/I-009/P-002/mvn-test-live-interval-it.txt`
- `harness/evidence/test/I-009/P-002/live-db-probe.txt`
- `harness/evidence/test/I-009/P-002/IT-RESULT.txt`
