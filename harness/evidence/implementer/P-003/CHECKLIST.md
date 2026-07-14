# P-003 Implementer checklist (RP-02)

1. [x] Expand `com.xugu.dialect.XuguDialect` only from `Dialect` (no MySQL/Oracle extends).
2. [x] Implement `contributeTypes` / `columnType` mappings for **A-TYP-001…013**.
3. [x] Implement CAST hooks for **A-TYP-019**.
4. [x] Wire DDL helpers for **A-DDL-001…006** (defaults OK only if IT proves Xugu accepts them).
5. [x] Identifier helper UPPER + quote `"` for **A-XCUT-001/002**; keywords for **A-XCUT-007**; JDBC TCL smoke for **A-XCUT-004**.
6. [x] Optional helpers only under `com.xugu.dialect` / `com.xugu.dialect.internal`.
7. [x] Add gated IT (`xugu.run.integration` / `XUGU_RUN_IT`); default `mvn test` green offline.
8. [x] Run IT **with flag ON** against real XuguDB; prove DB reachable.
9. [x] Cite Xugu doc paths for TIMESTAMP vs DATETIME (NOTES).
10. [x] Sync matrix acceptance hints for P-003 rows.
11. [x] Do **not** read `E:\Work\java\hibernate-dialect`; do **not** rewrite docs content.
12. [x] Leave must-commit to orchestrator/Human Gate; no commit this turn.
