# P-010 Walkthrough Checklist (test RP-02)

> Invocation: `test-p010-20260715`  
> Method: end-to-end dry-run of `docs/user-guide/` (no live-DB demo required; offline build exercised)

## New-integrator path

| # | Capability | Guide section | Observable in docs? | Result |
|---|---|---|---|---|
| 1 | Install GAV / JDK / JDBC jar | [01-install.md](../../../../docs/user-guide/01-install.md) | GAV `com.xugu:xugu-dialect:7.4.5.Final`, JDK 17, root `xugu-jdbc-12.3.6.jar`, local install | PASS |
| 2 | Configure explicit dialect | [02-configuration.md](../../../../docs/user-guide/02-configuration.md) | `hibernate.dialect=com.xugu.dialect.XuguDialect` + Boot YAML | PASS |
| 3 | Configure SPI resolver | same | `XuguDialectResolver` + services file + match rule | PASS |
| 4 | Set env / secrets | same | `XUGU_*`, `compatiblemode=NONE`, secrets guidance | PASS |
| 5 | Run verify / demo | [03-verify.md](../../../../docs/user-guide/03-verify.md) | `mvn package/test/verify`, `spring-boot:run`, IT gate, offline note | PASS |
| 6 | Find feature matrix | [04-feature-matrix.md](../../../../docs/user-guide/04-feature-matrix.md) | SSOT + docs pointer + status legend | PASS |
| 7 | Troubleshoot | [05-troubleshooting.md](../../../../docs/user-guide/05-troubleshooting.md) | LIMIT/FOR UPDATE, BINARY, SPI, DB unreachable, Boot 7.4.1, systemPath | PASS |

## Link integrity

| Check | Result |
|---|---|
| Relative markdown links under `docs/user-guide/` | **44 PASS / 0 FAIL** |
| Key targets (`contracts/*`, `docs/feature-matrix-definition-a.md`, `demo-spring-boot/README.md`) | exist |

## Forbidden tree

| Check | Result |
|---|---|
| No writes under `E:\Work\docs\content` | PASS (0 files touched today; hibernate-test status has no content paths) |

## Commands

| Command | Exit | Result |
|---|---:|---|
| `mvn -q -DskipTests package` | 0 | PASS |
| `python harness/scripts/verify.py --phase P-010 --evidence harness/evidence/test/P-010/verification.json` | 0 | VERIFY PASS |
