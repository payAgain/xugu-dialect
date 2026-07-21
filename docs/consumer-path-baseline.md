# Consumer-Path Baseline (docs pointer)

> **SSOT (do not duplicate):** [`contracts/consumer-path-baseline.md`](../contracts/consumer-path-baseline.md)  
> **User guide (how to run):** [`docs/user-guide/06-consumer-path.md`](user-guide/06-consumer-path.md)  
> **Demo runbook:** [`demo-spring-boot/README.md`](../demo-spring-boot/README.md)  
> **Initiative:** I-006 — Spring Boot consumer-path coverage  

This file is a short navigation stub only. The full Boot-required matrix (**41** rows: Layer A=13 / B=9 / C′=19) and the `dialect-it-only` exclusion appendix live under `contracts/`.

**Freeze status (I-006 / P-005):** Boot-required open gaps = **0**. GAV `com.xugu:xugu-dialect:7.4.5.Final`. Ship / Central remain out of Initiative scope.

**Verify:**

```text
mvn -q test
mvn -q test
# with live DB:
XUGU_RUN_IT=true mvn -q test
```
