# TEST-REPORT — I-010 P-013 RP-01

- **invocation_id:** inv-i010-p013-rp01-test
- **branch:** `feat/i-010-orm-hql-quality-completion`
- **base tip (pre-commit):** `4a5ff93148c7d7aafccdcbff4558140d634381b7`
- **completed_at:** 2026-07-21T16:34:18+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| package | `mvn -q -pl dialect -DskipTests package` | 0 | **PASS** |
| targeted | `mvn -q -pl dialect test -Dtest=XuguHqlBulkBoundaryTest,XuguHqlBulkBoundaryIT` | 0 | **PASS** |
| full dialect | `mvn -q -pl dialect test` | 0 | **PASS** |

Maven: `C:\Users\admin\tools\apache-maven-3.9.9\bin\mvn.cmd`

## Targeted surefire

```text
XuguHqlBulkBoundaryIT:  Tests run: 4, Failures: 0, Errors: 0, Skipped: 3
XuguHqlBulkBoundaryTest: Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
Aggregate: Tests run: 9, Failures: 0, Errors: 0, Skipped: 3
BUILD SUCCESS
```

IT skips = `XuguITGate` off (expected offline).

## Live probe

| Probe | Result |
|---|---|
| TCP 192.168.2.239:5138 | closed |
| TCP 127.0.0.1:5138 | closed |
| Live IT | **SKIPPED_INFRA** |

## Observed flow

- **i010-hql-bulk-boundary:** Unit offline PASS; IT gated + infra-blocked; SSOT XP-003 `covered-unit`

## Classes

- `com.xugu.dialect.XuguHqlBulkBoundaryTest`
- `com.xugu.dialect.it.XuguHqlBulkBoundaryIT`
- entities: `I010P013StAnimal`, `I010P013StDog`, `I010P013StCat`

## Verdict

**PASS** — offline gates green; live **SKIPPED_INFRA**. **NOT Ship.**
