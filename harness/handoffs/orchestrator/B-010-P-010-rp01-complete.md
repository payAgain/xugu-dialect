# B-010 / P-010 RP-01 complete — prep RP-02

## Status
- Human Gate: B-010 approved (P-010 only)
- RP-01 **docs** `docs-p010-20260715`: **passed**
- Guide under `docs/user-guide/` + draft ACCEPTANCE
- **Do not run full Accept yet**

## RP-02 prep (test) — brief

Spawn independent **test** role instance:

| Item | Value |
|---|---|
| Phase / Build | P-010 / B-010 |
| step_id | RP-02 |
| purpose | walkthrough checklist against guide (dry-run or observed) |
| Packet | harness/tasks/P-010.md |
| Guide root | docs/user-guide/README.md |
| Evidence | harness/evidence/test/P-010/ |
| Handoff | harness/handoffs/test/P-010.yaml |
| Observed flow | user-guide-configure-and-verify-path |

### Suggested checklist
1. Links from README → 01…05 resolve; contract + matrix SSOT links work
2. Install page: GAV `com.xugu:xugu-dialect:7.4.5.Final`, JDK 17, JDBC jar note
3. Config page: explicit + SPI, Boot 4.1.0 / hibernate.version=7.4.5.Final, XUGU_*, compatiblemode=NONE, secrets
4. Verify page: mvn verify / spring-boot:run / `-Dxugu.run.integration=true`
5. Matrix page: 可实现 / 文档不允许 / 延后 explained; pointers only (no duplicated SSOT)
6. Troubleshooting: LIMIT vs FOR UPDATE, bare BINARY, SPI, DB unreachable
7. Confirm **no** writes to `E:\Work\docs\content`
8. Optional dry-run: `mvn -q -DskipTests package` (live DB not required for dry-run)

### After RP-02
- RP-03: **skip** (risk_score=4, condition risk_ge_8)
- Then orchestrator Accept + must-commit
- Then propose B-011 / P-011 only

## Out of scope now
- Full Accept
- git commit / tag / push
- P-011 approval
