# Testing — dialect

> Offline unit tests always run. Live IT requires real XuguDB and an explicit gate.

---

## Test Layers

| Layer | Location | Gate | Purpose |
|-------|----------|------|---------|
| Unit | `src/test/java/com/xugu/dialect/*Test.java` | none | SQL strings, registry wiring, negative assertions |
| Live IT | `src/test/java/com/xugu/dialect/it/*IT.java` | `XuguITGate` | Real JDBC / Session against XuguDB |
| IT entities | `it/entities/` | IT only | Minimal JPA models for ORM round-trips |
| Baselines | `src/test/resources/sql-baselines/` | unit/IT | Native SQL fixtures where used |

Surefire includes `*Test`, `*Tests`, and `*IT` (`dialect/pom.xml`). IT methods still no-op/skip unless the gate is on.

---

## Integration Gate

```text
-Dxugu.run.integration=true
# or
XUGU_RUN_IT=true
```

Implementation: `com.xugu.dialect.support.XuguITGate`.

Typical IT patterns:

- `@EnabledIf("com.xugu.dialect.support.XuguITGate#isEnabled")` on the class, **or**
- `Assumptions.assumeTrue(XuguITGate.isEnabled(), "integration gate off")` at method start

Default `mvn test` must stay green **offline** (gate off → IT skipped).

---

## Connection Helpers

`com.xugu.dialect.support.XuguTestConnection`:

- Default URL embeds local reference credentials and **`compatiblemode=NONE`**
- Override with `XUGU_JDBC_URL` / `XUGU_USER` / `XUGU_PASSWORD`
- Driver: `com.xugu.cloudjdbc.Driver`

Do not invent a second competing URL builder for dialect IT.

---

## What Good Tests Look Like

1. **Matrix-linked** — method or class javadoc cites `A-*` / `C-*` ids.
2. **Unit first** — lock SQL / registry without DB when possible (`XuguDialectTest`, `XuguFunctionRegistryTest`, `XuguPaginationLockTest`).
3. **Live for covered-live** — Session/JDBC round-trip under gate (`XuguTypeRoundTripIT`, `XuguIdentitySequenceIT`, …).
4. **Negative / non-support** — explicit assertions for 文档不允许 or “dialect does not claim …” (`XuguNegativeRegressionBaselineTest`, UDT/partition claim tests).
5. **Known-limit honesty** — assumption skip or unit-only coverage must match SSOT status `known-limit-documented`, not be silently counted as covered-live.

---

## Verification Commands

From repo root:

```bash
mvn -q -DskipTests package
mvn -q test
XUGU_RUN_IT=true mvn -q test
# Windows equivalent property:
# mvn -q test -Dxugu.run.integration=true
```

Module-scoped:

```bash
mvn -q -pl dialect -am test
mvn -q -pl dialect -am test -Dxugu.run.integration=true
```

---

## Coverage SSOT

Do not invent Accept numbers. Read:

- `contracts/production-regression-baseline.md` (status per matrix row; I-010 rollup **91/98 covered-live + 7 KL**)
- `contracts/feature-matrix-definition-a.md`
- `docs/verification.md` / `docs/user-guide/03-verify.md`

---

## Anti-Patterns

- Mock-only substitutes for dialect IT when claiming live coverage.
- Leaving IT ungated so default CI requires a database.
- Duplicating demo’s `XuguIntegrationGate` inside dialect (use `XuguITGate`).
- Flaky IT that depends on leftover tables without cleanup.
