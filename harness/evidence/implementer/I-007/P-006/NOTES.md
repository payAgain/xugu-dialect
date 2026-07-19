# P-006 Implementer NOTES — Docs align + Accept prep (I-007)

> **Invocation:** `inv-i007-p006-rp01-implementer` · I-007 / B-001 / P-006 / RP-01  
> **Branch:** `feat/i-007-capability-hardening-abc`

## Doc alignment summary

| Area | Change |
|---|---|
| C-BULK-002 | Stale **known-limit-documented** → **covered-live** in `03-verify.md`, `04-feature-matrix.md`, `verification.md` |
| I-005 baseline counts | User-facing tables now **94 covered-live** (post I-007/P-002) |
| Track C | Xref [`docs/p004-track-c-capabilities.md`](../../../docs/p004-track-c-capabilities.md) in feature matrix user guide |
| Track B | Consumer-path § I-007 already present; fixed offline Flyway method → `flywayXuguPluginAndMigrationOnClasspath`; removed duplicate YAML key |
| Ruler C rollup | `contracts/feature-matrix-i003-ruler-c.md` — 延后 count 5→3; C-BULK-002 acceptance hint live IT |
| Consumer SSOT | `contracts/consumer-path-baseline.md` — C-BULK-002 + Flyway method name |
| README | I-007 capability hardening pointer |

## Hygiene

- Deleted scratch `dialect/Probe.java` (P-004 local probe; not part of product)

## Verify prep (RP-02)

Test role should run from repo root:

```bash
mvn -q -DskipTests package
mvn -q test
python harness/scripts/verify.py --phase P-006 --evidence harness/evidence/test/I-007/P-006/verification.json
```

Phase-bound evidence path (created by test): `harness/evidence/test/I-007/P-006/verification.json`

Implementer offline smoke (prep only, not Accept evidence):

| Command | Role |
|---|---|
| `mvn -q -DskipTests package` | prep |
| `mvn -q test` | prep |
| `python harness/scripts/verify.py` | prep → expect **VERIFY PASS** |

## Not done (by design)

- No git commit
- No Ship / tag / push
- No `org/` dump commits
- Official P-006 verification.json — **test RP-02**
