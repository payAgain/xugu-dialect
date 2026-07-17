# P-002 Spot Checks (test role)

| Check | Result | Evidence |
|---|---|---|
| Unit prefers identity select (getDefaultUseGetGeneratedKeys=false) | PASS | XuguIdentitySequenceTest 7/7 |
| ORM IT XuguReservedIdentityIT | PASS | 1/1, Skipped 0 under gate ON |
| SQL `create/insert "order"` + `select last_insert_id() from dual` | observed | mvn-test-integration.log |
| No `unexpected ORDER` on IDENTITY persist path | PASS | IT EXIT 0; id backfilled |
| No product code changes by test | true | test only wrote harness/evidence/test + handoff |
| GAV / Ship / Accept / commit by test | forbidden respected | none |

Observed flow: orm-identity-reserved-table-persist → PASS
