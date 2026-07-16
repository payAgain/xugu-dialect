# P-002 TEST REPORT

## Commands
```text
mvn -q -DskipTests package          # PASS
mvn -q test                         # PASS (offline; IT skipped)
mvn -q -pl dialect -am test -Dtest=XuguExceptionMappingIT -Dxugu.run.integration=true  # PASS
python harness/scripts/verify.py --phase P-002 --evidence harness/evidence/test/P-002/verification.json  # VERIFY PASS
```

## Observed
- Live unique violation: ErrorCode 13001 / SQLState xugu13001 → ConstraintViolationException UNIQUE via Session persist entrypoint.
