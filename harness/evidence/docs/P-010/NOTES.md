# P-010 Docs Notes (user-guide)

**Invocation:** `docs-p010-20260715`  
**Role:** docs  
**Phase / Build / Initiative:** P-010 / B-010 / I-001  
**Date:** 2026-07-15  
**Step:** RP-01  
**Accept / commit:** **NOT done** (await RP-02 test walkthrough; RP-03 optional risk_ge_8 → expect skip)

## What was delivered

Practical bilingual / Chinese-first user guide under `docs/user-guide/`:

| File | Content |
|---|---|
| `README.md` | Index + quick start + contract/matrix/demo links |
| `01-install.md` | GAV `com.xugu:xugu-dialect:7.4.5.Final`, JDK 17, JDBC jar / systemPath note |
| `02-configuration.md` | Explicit dialect + SPI; Boot 4.1.0 + `hibernate.version=7.4.5.Final`; `XUGU_*`; `compatiblemode=NONE`; secrets |
| `03-verify.md` | `mvn verify` / demo `spring-boot:run` / `-Dxugu.run.integration=true` |
| `04-feature-matrix.md` | Links to contracts + docs pointer; 可实现 / 文档不允许 / 延后 |
| `05-troubleshooting.md` | LIMIT vs FOR UPDATE order; bare BINARY; SPI match; DB unreachable; Boot 7.4.1; systemPath |

Cross-links:

- `contracts/xugu-dialect.contract.md` §10 → user-guide README
- `docs/feature-matrix-definition-a.md` → user-guide matrix page

## Forbidden reminders (observed)

- **Did not** write `E:\Work\docs\content`
- **Did not** change dialect Java
- **Did not** tag / push / Central

## Role file

Created minimal `agents/docs.md` (was missing) during B-010 dispatch / RP-01.

## Next

- RP-02 **test**: walkthrough checklist against guide (dry-run or observed) — see prep note in handoff / orchestrator brief
- RP-03 reviewer: **skip** unless risk≥8 (Packet `risk_score=4`, `condition=risk_ge_8`)
- Full Accept only after RP-02 PASS + evidence
