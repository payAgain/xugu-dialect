# I-008 / P-006 — Reviewer Audit (RP-03)

> **Role:** reviewer  
> **invocation_id:** `inv-i008-p006-rp03-reviewer`  
> **Date:** 2026-07-20  
> **Branch:** `feat/i-008-production-quality-gaps`  
> **Mode:** readonly

## Verdict

**ACCEPT PASS**

## Review Result

```text
Review Result

BLOCKER: (none)
MAJOR: (none)
MINOR:
  - F-01: Live golden-path PASS for DemoUuidJsonOutOfBoxIT not observed — 127.0.0.1:5138 TcpTestSucceeded=false; gated skip 2/2 offline is honest.
  - F-02: P-006 artifacts uncommitted at audit (application.yml, DemoUuidJsonOutOfBoxIT.java, demo-spring-boot.contract.md, smoke extensions).
  - F-03: RP-02 test evidence incomplete — harness/evidence/test/I-008/P-006/ lacks verification.json / test handoff; implementer offline PASS independently re-run (exit 0).
  - F-04: docs/user-guide/02-configuration.md § Q3 still uses future tense「P-006 将实现」/「目标态」 though demo now matches checklist (cosmetic doc lag; out of P-006 allowed paths unless orchestrator schedules doc touch-up).
  - F-05: defaultApplicationYmlExposesUuidJsonChecklist is live-gated only; offline yaml key coverage delegated to DemoOfflineSmokeTest — acceptable split but no Spring-context assert of resolved properties offline.
QUESTION: (none)
Validation Status: reviewer re-run demo-spring-boot focused offline tests PASS (DemoOfflineSmokeTest 8/0/0/0; DemoUuidJsonOutOfBoxIT 2/0/0/2 skipped); full reactor VERIFY PASS deferred to RP-02 test owner.
Recommended Fixes: commit P-006 artifacts; complete RP-02 verification.json; re-run gated live IT when XuGuDB up; optional 02-configuration.md tense update.
```

## Criteria checklist

| # | Criterion (P-006) | Result | Evidence |
|---|---|---|---|
| 1 | Demo default deps/config match P-002 UUID/JSON checklist (4 items) | **PASS** | Cross-check table § below; `application.yml`, `pom.xml`, `DemoTypedSample` / `UuidAsVarcharConverter` |
| 2 | Boot IT proves out-of-box UUID + JSON path without extra Hibernate overrides | **PASS** (offline wiring; live infra skip) | `DemoUuidJsonOutOfBoxIT` — only `xugu.demo.startup-crud=false`; golden path covers A-TYP-012, A-TYP-013, C-JSON-001 |
| 3 | Offline `mvn -q test` green | **PASS** | Implementer ACCEPTANCE exit 0; reviewer re-run `-pl demo-spring-boot -Dtest=DemoOfflineSmokeTest,DemoUuidJsonOutOfBoxIT` exit 0 |
| 4 | No JDBC driver / dialect Java changes | **PASS** | `git diff` scope: demo-spring-boot + contracts only; `pom.xml` xugu-jdbc 12.3.6 system-scope unchanged |
| 5 | Scope within allowed paths | **PASS** | No 94-row mirror; no dialect module edits |

## P-002 checklist cross-check (SSOT: `docs/user-guide/02-configuration.md` § UUID/JSON Boot 必配清单)

| # | P-002 checklist item | Demo default | Offline evidence | Live evidence | Match |
|---|---|---|---|---|---|
| 1 | `spring-boot-starter-jackson` → Hibernate JSON **FormatMapper** | `demo-spring-boot/pom.xml` L68–71 | `DemoOfflineSmokeTest#jacksonOnClasspathForHibernateJsonFormatMapper` (`tools.jackson.databind.ObjectMapper`) | Golden path JSON column persist/load in `uuidAndJsonGoldenPathWithDefaultBootWiring` | **Yes** |
| 2 | UUID → `varchar(36)` + **AttributeConverter** | `DemoTypedSample#guidVal` `@Column(length=36)` + `@Convert(UuidAsVarcharConverter)` | `DemoOfflineSmokeTest#typedSampleGuidUsesUuidAsVarcharConverter` | UUID round-trip in golden-path test | **Yes** |
| 3 | `hibernate.type.preferred_uuid_jdbc_type: VARCHAR` | `application.yml` `spring.jpa.properties.hibernate.type.preferred_uuid_jdbc_type` (**added P-006**) | `DemoOfflineSmokeTest#applicationYmlDocumentsExplicitDialectAndEnvKeys` substring assert | `defaultApplicationYmlExposesUuidJsonChecklist` Environment assert | **Yes** |
| 4 | `hibernate.query.hql.json_functions_enabled=true` | `application.yml` (pre-existing default) | Same offline yaml smoke | Environment assert + `json_arrayagg` HQL in golden-path test | **Yes** |

**Recommended yaml snippet (P-002)** vs **demo `application.yml`:** dialect, `type.preferred_uuid_jdbc_type: VARCHAR`, and `query.hql.json_functions_enabled: true` all present under `spring.jpa.properties.hibernate`. Demo adds operational keys (datasource env placeholders, Flyway default off, UTC timezone) outside checklist — acceptable.

## Artifact audit — `application.yml`

| Property | P-002 requirement | Observed | Audit |
|---|---|---|---|
| `hibernate.dialect` | Explicit or SPI | `com.xugu.dialect.XuguDialect` | **PASS** — also asserted in out-of-box IT |
| `hibernate.type.preferred_uuid_jdbc_type` | VARCHAR aligned with converter | `VARCHAR` under new `type:` block | **PASS** — closes pre-P-006 gap |
| `hibernate.query.hql.json_functions_enabled` | `true` for HQL JSON aggregates | `true` | **PASS** — unchanged from prior demo |
| JDBC driver | No P-006 change | `com.xugu.cloudjdbc.Driver` | **PASS** |

## Artifact audit — `DemoUuidJsonOutOfBoxIT` (new, 2 tests)

| Anchor | Intent | Audit |
|---|---|---|
| `@TestPropertySource` | Prove **default** wiring | **Sound** — sole override `xugu.demo.startup-crud=false`; **does not** repeat `json_functions_enabled` override unlike legacy `DemoJsonIT` |
| `defaultApplicationYmlExposesUuidJsonChecklist` | Spring `Environment` reflects yaml checklist keys | **Sound** — asserts `preferred_uuid_jdbc_type`, `json_functions_enabled`, `dialect`; live-gated (`XuguIntegrationGate`) |
| `uuidAndJsonGoldenPathWithDefaultBootWiring` | End-to-end UUID + JSON + `json_arrayagg` on default Boot context | **Sound** — saves `DemoTypedSample` (UUID) + `DemoJsonDoc` (JSON), flush/clear reload, HQL `json_arrayagg`; table recreate via `DemoXuguJdbc.recreateTypedSampleTable()` consistent with `DemoTypesIT` |
| Complementary ITs | Layer C′ matrix coverage | **Appropriate** — `DemoTypesIT` / `DemoJsonIT` remain; new IT adds explicit **out-of-box config** proof P-002 promised for P-006 |

**Scope drift:** none — test-only + config in allowed module.

## Artifact audit — `DemoOfflineSmokeTest` extensions

| New/extended test | Checklist item | Audit |
|---|---|---|
| `applicationYmlDocumentsExplicitDialectAndEnvKeys` (+ asserts) | Items 3–4 yaml presence | **PASS** — classpath yaml substring checks |
| `jacksonOnClasspathForHibernateJsonFormatMapper` | Item 1 | **PASS** — Jackson 3 class probe |
| `typedSampleGuidUsesUuidAsVarcharConverter` | Item 2 | **PASS** — annotation-level converter binding |

## Contract / doc cross-links

| Surface | Consistent with demo | Notes |
|---|---|---|
| `contracts/demo-spring-boot.contract.md` (new) | Yes | 4-row checklist + evidence anchors mirror implementer table |
| `contracts/consumer-path-baseline.md` | Yes | Q3 IT cross-refs added |
| `demo-spring-boot/README.md` | Yes | Mapping notes cite `preferred_uuid_jdbc_type` + JSON flag |
| `docs/user-guide/02-configuration.md` | Content aligned; tense stale | Checklist items match demo; prose still says P-006 future (F-04) |

## Validation summary

| Command | Exit | Status | Notes |
|---|---:|---|---|
| Reviewer: `mvn -q -pl demo-spring-boot test -Dtest=DemoOfflineSmokeTest,DemoUuidJsonOutOfBoxIT` | 0 | PASS | 8 offline smokes + 2 gated skips |
| Implementer: full reactor `mvn -q test` | 0 | PASS (claimed) | per ACCEPTANCE.md |
| Live: `XUGU_RUN_IT=true` … `DemoUuidJsonOutOfBoxIT` | — | SKIPPED_INFRA | `live-db-probe.txt` TcpTestSucceeded=false |
| RP-02: `harness/evidence/test/I-008/P-006/verification.json` | — | **Pending** | only `started_at.txt`, `live-db-probe.txt` at audit |

## Findings (non-blocking)

- **F-01:** Live PASS not observed; offline wiring + complementary `DemoTypesIT`/`DemoJsonIT` history sufficient for P-006 Accept; re-run live before Ship-level production claims per Q4 language.
- **F-02:** Uncommitted working tree — orchestrator must commit before Build Accept gate.
- **F-03:** Test-owner VERIFY artifact incomplete; does not block reviewer artifact audit PASS.
- **F-04–F-05:** See Review Result MINOR items.

## Decision

`accepted` — Q3 Boot out-of-box path closed at wiring level: demo defaults match P-002 four-item UUID/JSON checklist, missing `preferred_uuid_jdbc_type` gap filled, gated IT correctly proves default yaml without redundant Hibernate property overrides, offline tests green, no JDBC scope violation.
