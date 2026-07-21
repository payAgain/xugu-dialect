# Live reactor — xgconsole_local.bat target

- Source: `xgconsole_local.bat` (`server=192.168.2.239`, `port=5138`, `ssl=nssl`, `database=SYSTEM`, `username=SYSDBA`)
- JDBC: `jdbc:xugu://192.168.2.239:5138/SYSTEM?user=SYSDBA&password=SYSDBA&compatiblemode=NONE&ssl=nssl`
- Command: `XUGU_RUN_IT=true mvn -q clean test`
- Exit: **0**
- TcpTestSucceeded: **True**
- Date: 2026-07-21

## Counts (run / fail / error / skip)

| Module | run | fail | error | skip | suites |
|---|---:|---:|---:|---:|---:|
| dialect | 170 | 0 | 0 | 19 | 41 |
| demo-spring-boot | 36 | 0 | 0 | 0 | 18 |

Failed suites: none

## Artifacts

- `live-db-probe.txt`
- `jdbc-url-used.txt`
- `mvn-clean-test-live-stdout.txt`
- `mvn-clean-test-live-stderr2.txt`
- `exit-code-clean.txt`
