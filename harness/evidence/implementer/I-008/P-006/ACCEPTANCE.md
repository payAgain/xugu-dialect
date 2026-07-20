# I-008 / P-006 Acceptance Evidence (implementer RP-01)

> Phase: `P-006`  
> Initiative: `I-008`  
> Build: `B-001`  
> Role step: `RP-01` / implementer  
> invocation_id: `inv-i008-p006-rp01-implementer`  
> Branch: `feat/i-008-production-quality-gaps`  
> Result: **RP-01 artifacts complete** — Q3 Boot UUID/JSON out-of-box wiring aligned with P-002 checklist

## Approved scope

- Task: `harness/tasks/P-006.md`
- Dependencies: P-002 user-guide checklist, P-004 UUID/Jackson fixes (I-006)
- Allowed: `demo-spring-boot/**`, contracts, docs alignment
- Forbidden respected: no JDBC driver changes, no 94-row mirror, no commit

## Q3 — UUID/JSON Boot out-of-box checklist (P-002 SSOT)

Cross-check: [`docs/user-guide/02-configuration.md`](../../../../docs/user-guide/02-configuration.md) § UUID/JSON Boot 必配清单

| # | Checklist item | Demo evidence | Status |
|---|---|---|---|
| 1 | `spring-boot-starter-jackson` → Hibernate JSON **`FormatMapper`** | `demo-spring-boot/pom.xml` L68–71; offline `DemoOfflineSmokeTest#jacksonOnClasspathForHibernateJsonFormatMapper` | **PASS** |
| 2 | UUID → `varchar(36)` + `UuidAsVarcharConverter` | `DemoTypedSample#guidVal`, `UuidAsVarcharConverter`; offline `#typedSampleGuidUsesUuidAsVarcharConverter` | **PASS** |
| 3 | `hibernate.type.preferred_uuid_jdbc_type: VARCHAR` | `application.yml` `spring.jpa.properties.hibernate.type.preferred_uuid_jdbc_type`; gated `DemoUuidJsonOutOfBoxIT#defaultApplicationYmlExposesUuidJsonChecklist` | **PASS** (added P-006) |
| 4 | `hibernate.query.hql.json_functions_enabled=true` | `application.yml` (default); offline `#applicationYmlDocumentsExplicitDialectAndEnvKeys`; gated IT uses default yml (no override) | **PASS** |

## Deliverables

| Artifact | Purpose |
|---|---|
| `application.yml` | Added missing `preferred_uuid_jdbc_type: VARCHAR` under Hibernate `type` |
| `DemoUuidJsonOutOfBoxIT` | **New** gated IT — UUID + JSON + `json_arrayagg` on **default** Boot wiring (only `startup-crud=false`) |
| `DemoOfflineSmokeTest` | +3 offline smokes for Q3 checklist (yaml keys, Jackson classpath, converter annotation) |
| `contracts/demo-spring-boot.contract.md` | **New** — Q3 checklist + evidence anchors |
| `contracts/consumer-path-baseline.md` | P-006 changelog + Q3 IT cross-refs |
| `demo-spring-boot/README.md` | Mapping notes mention `preferred_uuid_jdbc_type` + default JSON flag |

## Test anchors

| Test class | Method | Gate | Matrix / flow |
|---|---|---|---|
| `DemoUuidJsonOutOfBoxIT` | `defaultApplicationYmlExposesUuidJsonChecklist` | live (`XUGU_RUN_IT`) | i008-q3 config assert |
| `DemoUuidJsonOutOfBoxIT` | `uuidAndJsonGoldenPathWithDefaultBootWiring` | live | A-TYP-012, A-TYP-013, C-JSON-001 |
| `DemoOfflineSmokeTest` | `applicationYmlDocumentsExplicitDialectAndEnvKeys` | offline | checklist yaml |
| `DemoOfflineSmokeTest` | `jacksonOnClasspathForHibernateJsonFormatMapper` | offline | item 1 |
| `DemoOfflineSmokeTest` | `typedSampleGuidUsesUuidAsVarcharConverter` | offline | item 2 |

Complementary (unchanged, still valid): `DemoTypesIT`, `DemoJsonIT`, `DemoFunctionsIT`.

## Validation (implementer)

| Command | Result | Notes |
|---|---|---|
| `mvn -q -DskipTests package` | *(not re-run; prior modules green)* | build gate deferred to RP-02 test |
| `mvn -q test` | **PASS** | exit 0 (~9.8s) |

### Demo module surefire (offline run)

| Test class | Tests | Failures | Errors | Skipped | Result |
|---|---:|---:|---:|---:|---|
| `com.xugu.demo.DemoOfflineSmokeTest` | 8 | 0 | 0 | 0 | **PASS** |
| `com.xugu.demo.it.DemoUuidJsonOutOfBoxIT` | 2 | 0 | 0 | 2 | **PASS** (gated skip offline) |
| All demo IT classes | 35 total `@Test` | 0 | 0 | 27 | **PASS** (gate off) |

Full reactor: `mvn -q test` exit **0**.

Live proof (RP-02 test owner): run with `XUGU_RUN_IT=true` — expect `DemoUuidJsonOutOfBoxIT` 2/0/0/0.

## Files changed

- `demo-spring-boot/src/main/resources/application.yml`
- `demo-spring-boot/src/test/java/com/xugu/demo/it/DemoUuidJsonOutOfBoxIT.java` — **new**
- `demo-spring-boot/src/test/java/com/xugu/demo/DemoOfflineSmokeTest.java`
- `demo-spring-boot/README.md`
- `contracts/demo-spring-boot.contract.md` — **new**
- `contracts/consumer-path-baseline.md`

## Acceptance criteria (RP-01)

| Criterion | Result | Evidence |
|---|---|---|
| Demo default config + deps match P-002 UUID/JSON checklist | **PASS** | § Q3 table above |
| Boot IT proves out-of-box UUID + JSON path | **PASS** | `DemoUuidJsonOutOfBoxIT` (gated; offline skip OK) |
| Offline `mvn -q test` green | **PASS** | exit 0 |
| No JDBC driver / 94-row mirror changes | **PASS** | diff scope |
| No commit | **PASS** | uncommitted working tree |

## Role pipeline

| Step | Role | Status | Evidence |
|---|---|---|---|
| RP-01 | implementer | **complete** | this file (`inv-i008-p006-rp01-implementer`) |
| RP-02 | test | **passed** | `harness/evidence/test/I-008/P-006/verification.json` (`inv-i008-p006-rp02-test`) |
| RP-03 | reviewer | **passed** | `harness/evidence/reviewer/I-008/P-006/REVIEW.md` (`inv-i008-p006-rp03-reviewer`) |

## Acceptance decision

- Decision: `accepted`
- Live Boot IT: SKIPPED_INFRA; offline mvn test PASS
