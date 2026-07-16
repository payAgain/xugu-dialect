# P-004 TEST REPORT (I-003)

```text
mvn -q -DskipTests package                                               # PASS
mvn -q test                                                              # PASS
mvn -q -pl dialect -am test -Dtest=XuguWindowCteSupportTest,XuguWindowCteIT -Dxugu.run.integration=true  # PASS
mvn -q test -Dxugu.run.integration=true                                  # PASS
python harness/scripts/verify.py --phase P-004 …                         # VERIFY PASS
```

Observed: live Session HQL window ranking (id=2 → row_number 1 in g1); CTE returns ids `[2,1]` for g1 by score desc; generated SQL contains OVER / WITH.
