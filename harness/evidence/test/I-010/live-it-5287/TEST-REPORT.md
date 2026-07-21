# I-010 Live IT @5287 — TEST REPORT

| Field | Value |
|---|---|
| Initiative | I-010 |
| Date | 2026-07-21 |
| JDBC | `jdbc:xugu://192.168.2.239:5287/SYSTEM?compatiblemode=NONE` |
| Gate | `XUGU_RUN_IT=true` + env `XUGU_USER` / `XUGU_PASSWORD` |
| Maven | `C:\Users\admin\tools\apache-maven-3.9.9\bin\mvn.cmd` |

## Results

| Module | Tests | Fail | Err | Skip |
|---|---:|---:|---:|---:|
| dialect | 253 | 0 | 0 | 4 |
| demo-spring-boot | 36 | 0 | 0 | 0 |

## SSOT promotions (honest)

| Suite | Promoted to covered-live |
|---|---|
| xuguefcore parity | XP-001, XP-002, XP-003, XP-004, XP-005, XP-007, XP-008, XP-009 |
| Definition A / baseline | A-TYP-014/016/017, A-FUN-003/005/006/007/009 |

| Still known-limit | Reason |
|---|---|
| A-FUN-021 (XMLTABLE) | cluster empty → assumption skip; HQL xmlelement/xmlquery live PASS |
| XP-006 / XP-010 | unit-only by design |

## Artifacts

- `mvn-test-full-reactor.txt` (password redacted)
- `mvn-b002-it.txt`
- `IT-RESULT.txt`
