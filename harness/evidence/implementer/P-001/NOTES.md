# P-001 Implementer NOTES — DROP SEQUENCE IF EXISTS (I-004)

> **Invocation:** `impl-p001-20260717` · I-004 / B-001 / RP-01

## Delivered

| Surface | Change |
|---|---|
| `XuguSequenceSupport.getDropSequenceString` | `"drop sequence if exists " + name` |
| Class javadoc | CREATE still Hibernate defaults; DROP documents XuGu `IF EXISTS` (sequence.md §四) |
| Unit | `XuguIdentitySequenceTest.createDropSequenceStrings_A_SEQ_001_002_005` → expects `drop sequence if exists HIB_P005_SEQ` |
| ORM IT | `XuguAutoSequenceDropIT` + `I004P001AutoEntity` (`GenerationType.AUTO`, Integer id, table `HIB_I004_P001_AUTO`) |

## Observed (live XuguDB)

- AUTO physical sequence name: `HIB_I004_P001_AUTO_SEQ`
- Generated SQL: `drop sequence if exists HIB_I004_P001_AUTO_SEQ`
- When sequence absent, XuGu may still emit **warning** `[E7002]` under IF EXISTS, but schema tooling / SessionFactory **does not halt** (EXIT 0)
- SessionFactory with `hbm2ddl.auto=create-drop` builds on clean DB and drops cleanly on close

## Validation (implementer)

| Command | Exit |
|---|---|
| `mvn -q -pl dialect -am test -Dtest=XuguIdentitySequenceTest` | **0** |
| `mvn -q -pl dialect -am test -Dtest=XuguAutoSequenceDropIT -Dxugu.run.integration=true` | **0** |

## Forbidden respected

Native implementation only (no sibling source port); no JDBC driver changes; GAV 7.4.5.Final; no Accept; no git commit; P-002 not started.
