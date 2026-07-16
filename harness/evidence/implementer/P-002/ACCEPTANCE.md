# P-002 ACCEPTANCE — exception mapping

> **Phase / Build:** P-002 / B-002 / I-003  
> **Human Gate:** 「批准 B-002，范围仅 P-002」

## Decision
- Decision: `accepted`

## Evidence
| Item | Result |
|---|---|
| C-EXC-001 conversion delegate | PASS — unit + ORM IT |
| C-EXC-002 name extractor | PASS — unit (E16005 template); IT primary is unique path |
| `mvn -q test` | PASS |
| `mvn -q test -Dxugu.run.integration=true` (ExceptionMappingIT) | PASS (E13001 → ConstraintViolationException UNIQUE) |
| `verify.py --phase P-002` | VERIFY PASS |
| Reviewer | approve |

## Observed behavior
Session persist of duplicate unique `code` raises Hibernate `ConstraintViolationException` with `ConstraintKind.UNIQUE` (not untyped JDBC failure).
