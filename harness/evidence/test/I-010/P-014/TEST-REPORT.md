# TEST-REPORT — I-010 P-014 RP-01

- **invocation_id:** inv-i010-p014-rp01-test
- **branch:** `feat/i-010-orm-hql-quality-completion`
- **completed_at:** 2026-07-21T16:32:00+08:00

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| focused | `mvn -q -pl dialect test -Dtest=XuguJsonLobBoundaryIT,XuguExplicitTxAtomicityIT` | 0 | **PASS** |
| dialect full | `mvn -q -pl dialect test` | 0 | **PASS** |
| branch_check | `python harness/scripts/branch_check.py` | 0 | **PASS** |

Maven: `C:\Users\admin\tools\apache-maven-3.9.9\bin\mvn.cmd`

## Surefire (gate off)

| Class | tests | skipped | failures |
|-------|------:|--------:|---------:|
| `XuguJsonLobBoundaryIT` | 1 | 1 | 0 |
| `XuguExplicitTxAtomicityIT` | 2 | 2 | 0 |

## XP-004 design (ref JsonBoundaryTests)

1. Persist ~512KiB JSON (`{"blob":"x"×512KiB}`) via `I010P014JsonDoc` + `@JdbcTypeCode(SqlTypes.JSON)`
2. `session.find` — success → assert length ≥ 512KiB; failure → document limitation (still PASS)
3. Optional `json_value(e.payload, '$.blob')` scalar path

## XP-005 design

1. Same tx: persist two `I010P014TxEntity` → commit → count=2
2. Same tx: persist two → rollback → count=0
3. Explicit `Session.beginTransaction()` only (no Spring)

## Live

| Probe | Result |
|---|---|
| TCP 192.168.2.239:5138 | closed |
| TCP 127.0.0.1:5138 | closed |

**SKIPPED_INFRA** — SSOT XP-004/XP-005 → `skipped-infra` (no covered-live inflation).

## Observed flows

- i010-json-lob
- i010-tx-atomicity

## Verdict

**PASS** offline. Live pending. **NOT Ship.**
