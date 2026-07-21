# Handoff — test → reviewer (I-010 P-015 RP-01)

## Status
RP-01 **complete** (offline PASS). Ready for RP-02 readonly reviewer.

## Delivered
- `XuguNativeSqlBaselineTest` + `sql-baselines/{limit-offset,for-update-limit-order,identity-column.fragment}.sql`
- `XuguHqlJoinFetchIT` + `I010P015Parent` / `I010P015Child` (`HIB_I010_P015_*`)
- SSOT: XP-006=`covered-unit`, XP-007=`skipped-infra`
- Evidence: `harness/evidence/test/I-010/P-015/**`

## Commands observed
- `mvn -q -pl dialect test` → exit 0
- Live IT → SKIPPED_INFRA (TCP 5138 closed)

## Reviewer focus
- Goldens Equal dialect emit (`limit ? offset ?`; FOR UPDATE before LIMIT; IDENTITY not AUTO_INCREMENT)
- No SKIP LOCKED / FOR SHARE positive goldens
- Join fetch asserts `Hibernate.isInitialized` + size; gate `XuguITGate`

## Blockers
- must-commit pending Human Gate / orchestrator
- Live join-fetch re-prove when DB up

## NOT Ship
