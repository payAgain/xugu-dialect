# I-010 / P-012 Test Acceptance (test role RP-01)

> Phase: `P-012`  
> Initiative: `I-010`  
> Build: `B-002`  
> Invocation: `inv-i010-p012-rp01-test`  
> Step: `RP-01`  
> Role: `test`  
> Verdict: **PASS — offline gate green; live SKIPPED_INFRA**  
> Date: 2026-07-21  
> Branch: `feat/i-010-orm-hql-quality-completion`  
> Base HEAD (pre-commit): `4a5ff93148c7d7aafccdcbff4558140d634381b7`

## Scope delivered

| Theme | Class | Entity | Methods |
|---|---|---|---|
| XP-001 乐观锁 | `XuguOptimisticConcurrencyIT` | `I010P012VersionedProduct` (`HIB_I010_P012_PRODUCT`, `@Version Long`) | `staleVersionWriteThrowsOptimisticLockException`, `successfulVersionedWriteIsReadable` |
| XP-002 GroupBy/Count | `XuguHqlGroupByCountIT` | `I010P012Customer` (`HIB_I010_P012_CUSTOMER`) | `scalarCountMaterializesAsNumber`, `groupByCityCountMaterializes` |

Gate: `Assumptions.assumeTrue(XuguITGate.isEnabled(), ...)` — env `XUGU_RUN_IT=true` or `-Dxugu.run.integration=true`.  
Pattern: SessionFactory boot + Schema CREATE/DROP + JDBC cleanup (ref `XuguBulkMutationIT`).  
GAV `7.4.5.Final` · `compatiblemode=NONE` via `XuguTestConnection`.

## Observed flows

| Flow | Offline (gate OFF) | Live |
|---|---|---|
| `i010-optimistic-lock` | 2 methods skipped via assume | SKIPPED_INFRA (TCP :5138 refused) |
| `i010-hql-groupby-count` | 2 methods skipped via assume | SKIPPED_INFRA |

## SSOT

`contracts/xuguefcore-parity-suite.md`:

- XP-001 → **`implemented`** (+ note: gate; not `covered-live`)
- XP-002 → **`implemented`** (+ note: gate; not `covered-live`)

## Verification

| Command | Exit | Result |
|---|---:|---|
| `mvn -q -DskipTests package` | 0 | **PASS** |
| `mvn -q test` (full reactor, gate OFF) | 0 | **PASS** |
| Live IT (`XUGU_RUN_IT=true`) | n/a | **SKIPPED_INFRA** |

Artifacts:

- `verification.json`
- `mvn-package.txt`
- `mvn-test-offline.txt`
- `live-db-probe.txt`
- `IT-RESULT.txt`

## Acceptance decision

- Decision: `accepted` (RP-01 test complete; reviewer RP-02 next)
- Live promotion to `covered-live` deferred until DB available
- NOT Ship
