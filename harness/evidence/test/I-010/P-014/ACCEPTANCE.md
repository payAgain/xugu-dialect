# ACCEPTANCE — I-010 P-014 RP-01 (test)

- **invocation_id:** inv-i010-p014-rp01-test
- **branch:** `feat/i-010-orm-hql-quality-completion`
- **completed_at:** 2026-07-21T16:32:00+08:00
- **role:** test
- **NOT Ship**

## Deliverables

| Item | Path | Status |
|---|---|---|
| JSON LOB IT | `dialect/.../it/XuguJsonLobBoundaryIT.java` | landed |
| JSON entity | `dialect/.../it/entities/I010P014JsonDoc.java` | landed |
| Tx atomicity IT | `dialect/.../it/XuguExplicitTxAtomicityIT.java` | landed |
| Tx entity | `dialect/.../it/entities/I010P014TxEntity.java` | landed |
| SSOT XP-004/005 | `contracts/xuguefcore-parity-suite.md` | `skipped-infra` |

## Acceptance checklist

- [x] LOB：成功断言长度 **或** catch 后 document limitation（测试仍 PASS）— 代码双路径已实现；live 未跑
- [x] Tx：commit 可见两行；rollback 后零行 — 代码已实现；`Session.beginTransaction()`；无 Spring
- [x] Gate：`XuguITGate`；gate off → assume/skip；`mvn -q -pl dialect test` **PASS**
- [ ] must-commit — **pending** human/orchestrator（本 RP 未 commit）
- [x] Evidence under `harness/evidence/test/I-010/P-014/**`

## Commands

| Step | Command | Exit | Status |
|------|---------|------|--------|
| focused offline | `mvn -q -pl dialect test -Dtest=XuguJsonLobBoundaryIT,XuguExplicitTxAtomicityIT` (gate unset) | 0 | **PASS** (3 skipped) |
| dialect offline | `mvn -q -pl dialect test` (gate unset) | 0 | **PASS** |
| branch_check | `python harness/scripts/branch_check.py` | 0 | **PASS** |
| live probe | TCP 192.168.2.239:5138 / 127.0.0.1:5138 | — | **refused** |

## Live

**SKIPPED_INFRA** — `live-db-probe.txt`: TcpTestSucceeded=False. No `covered-live` / no LOB known-limit observation claimed.

## Observed flows

- **i010-json-lob:** IT authored; offline skip; live pending
- **i010-tx-atomicity:** IT authored; offline skip; live pending

## Verdict

**PASS** (offline gate) — dialect test green; SSOT honest `skipped-infra`. **NOT Ship.** Reviewer RP-02 next.
