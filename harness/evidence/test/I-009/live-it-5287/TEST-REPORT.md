# TEST-REPORT — I-009 optional live IT @ 5287

- **branch:** `feat/i-009-deferred-matrix-delivery`
- **HEAD:** `e415600f544c918a4790e4cd87d0d23ba9222c19`
- **completed_at:** 2026-07-21T14:00:22+08:00
- **purpose:** Replace prior **SKIPPED_INFRA** (port **5138**) with live evidence on user cluster ports **5287/5288/5289**
- **NOT** Initiative Accept / Ship / Archive

## Connectivity

All three nodes OK (`SELECT 1 FROM DUAL`); preferred **node1:5287**.

| URL | Result |
|---|---|
| `jdbc:xugu://192.168.2.239:5287/SYSTEM?compatiblemode=NONE` | OK (selected) |
| `jdbc:xugu://192.168.2.239:5288/SYSTEM?compatiblemode=NONE` | OK |
| `jdbc:xugu://192.168.2.239:5289/SYSTEM?compatiblemode=NONE` | OK |

Env: `XUGU_RUN_IT=true` · `XUGU_USER=SYSDBA` · `XUGU_PASSWORD=***` · Maven `C:\Users\admin\tools\apache-maven-3.9.9\bin\mvn.cmd`

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| full reactor live | `XUGU_RUN_IT=true mvn -q test` @ 5287 | 1 | **FAIL** (dialect) |
| dialect install skipTests | `mvn -q -pl dialect -am install -DskipTests` | 0 | PASS |
| demo live | `XUGU_RUN_IT=true mvn -q -pl demo-spring-boot test` @ 5287 | 0 | **PASS** |

## Surefire summaries

| Module | Tests | Failures | Errors | Skipped |
|--------|------:|---------:|-------:|--------:|
| dialect (full reactor gate ON) | 208 | **3** | 0 | 2 |
| demo-spring-boot (gate ON) | 36 | 0 | 0 | 0 |

### dialect failures (3)

1. `XuguReservedIdentityIT#identityPersistOnReservedTableOrderBackfillsId` — `[E16007] 字段NAME不存在`
2. `XuguTableDdlExtensionsIT#encryptByNativeWhenEncryptorAvailable_A_DDL_009` — Assumption aborted (no encryptor / SYSSSO CREATE ENCRYPTOR) wrapped as AssertionFailedError
3. `XuguXmlTypeAndFunctionsIT#xmlFunctionsNativeSubset_A_FUN_021` — `XMLTABLE` row expected true, was false

## Artifacts

- `live-db-probe.txt`
- `mvn-test-live-full-reactor.txt`
- `mvn-test-live-demo.txt`
- `surefire-failures/*.txt`
- `TEST-REPORT.md` (this file)

## Verdict

**LIVE PARTIAL** — connectivity restored on **5287**; **demo-spring-boot 36/36 green**; **dialect 205/208 green** with **3 live failures**. Does **not** clear full-reactor Accept live bar. Human Gate should review failures before Initiative Accept.
