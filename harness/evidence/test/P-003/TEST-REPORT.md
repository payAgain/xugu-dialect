# P-003 TEST REPORT

```text
mvn -q test                                                              # PASS
mvn -q -pl dialect test -Dtest=XuguJsonAggregateIT -Dxugu.run.integration=true  # PASS
python harness/scripts/verify.py --phase P-003 …                         # VERIFY PASS
```

Observed: JSON column round-trip; HQL `json_arrayagg` / `json_objectagg` return arrays/objects containing expected labels.
