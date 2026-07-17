# P-001 Spot Checks (test role)

| Check | Result | Evidence |
|---|---|---|
| Unit drop string contains `if exists` | PASS | XuguIdentitySequenceTest 7/7 |
| ORM IT XuguAutoSequenceDropIT | PASS | 2/2, Skipped 0 under gate ON |
| SQL `drop sequence if exists HIB_I004_P001_AUTO_SEQ` | observed | mvn-test-integration.log / mvn-test-p001-focus.log |
| Absent sequence does not halt (E7002 warning OK) | PASS | EXIT 0 with [E7002] WARN |
| create-drop SessionFactory builds | PASS | IT SQL create then drop |
| No product code changes by test | true | git status — test only wrote harness/evidence + handoff |
| GAV / Ship / Accept / commit by test | forbidden respected | none |

Observed flow: orm-auto-sequence-create-drop-idempotent → PASS
