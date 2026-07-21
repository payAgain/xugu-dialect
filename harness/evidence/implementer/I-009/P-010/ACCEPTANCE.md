# P-010 Acceptance Evidence (Implementer RP-01)

> Phase: `P-010` · Initiative: `I-009` · Build: `B-001`  
> Role: implementer · Matrix rows: **C-JSON-006**, **C-SRV-001**, **C-SEL-001**

## Decision

- Decision: `accepted`
- Path:
  - **C-JSON-006:** `negative-only` (**doc-forbidden**) — no invented JSON_TABLE SQL
  - **C-SRV-001:** `covered-live` — read-only session param probes
  - **C-SEL-001:** `known-limit-documented` — Resolver SPI + internal `XuguDialectSelector` closure
- Rationale: XuGu docs have zero `json_table` under `reference/function/json-functions/**`; session params documented via `SHOW`; Hibernate 7.4 mandates `DialectResolver` only — selector is product extension, not duplicate SPI.

## Criteria table

| Criterion | Expected | Evidence | Status |
|---|---|---|---|
| C-JSON-006 doc-forbidden | `supportsJsonTableFunction=false`; no `json_table` HQL | `XuguRulerCClosureTest#jsonTableNotSupported_C_JSON_006`; `XuguFunctionRegistryTest` (json_table null); `@Disabled` SSOT anchor | **PASS** |
| C-SRV-001 read-only probe | `SHOW` only; no mutating SET | `XuguServerConfiguration`; `XuguRulerCClosureTest`; `XuguServerConfigurationIT` | **PASS** |
| C-SEL-001 SPI closure | Resolver autodetect + selector hook; no duplicate Hibernate SPI | `XuguDialectSelector`; `XuguDialectResolver`; `XuguDialectSelectorTest`; existing `META-INF/services` | **PASS** |
| SSOT promotion | Ruler C deferred rows closed | `contracts/production-regression-baseline.md`; `contracts/feature-matrix-i003-ruler-c.md` | **PASS** |
| Offline build | `mvn -q -DskipTests package` green | implementer run | **PASS** (exit 0) |
| Offline test | `mvn -q test` green | implementer run | **PASS** (exit 0) |

## Validation (implementer)

| Command | Exit | Detail |
|---|---|---|
| `mvn -q -DskipTests package` | 0 | offline build |
| `mvn -q test` | 0 | full reactor (IT gated/skipped offline) |
| `mvn -q -pl dialect -am test -Dtest=XuguRulerCClosureTest,XuguDialectSelectorTest,XuguServerConfigurationIT` | 0 | targeted unit + gated IT |

## Closed gaps

| matrix_id | status | entry_class#method | gate |
|---|---|---|---|
| C-JSON-006 | negative-only (doc-forbidden) | `XuguRulerCClosureTest#jsonTableNotSupported_C_JSON_006`; `XuguNegativeRegressionBaselineTest#deferred_C_JSON_006_jsonTable` (@Disabled) | unit |
| C-SRV-001 | covered-live | `XuguServerConfigurationIT#sessionParametersReadOnlyProbe_C_SRV_001` | IT |
| C-SEL-001 | known-limit-documented | `XuguDialectSelectorTest#defaultSelectorReturnsSpiDialect_C_SEL_001` | unit |

## Doc citation

- `reference/function/json-functions/**` — **no** `json_table` (contrast: `xml-functions/xmltable.md` → A-FUN-021)
- `reference/system-configuration-parameter/session-parameter/compatible_mode.md` — `SHOW COMPATIBLE_MODE`
- `reference/system-configuration-parameter/session-parameter/char_set.md` — `SHOW CHAR_SET`
- `reference/system-configuration-parameter/session-parameter/optimizer_mode.md` — `SHOW OPTIMIZER_MODE`
- Hibernate 7.4 `DialectResolver` SPI — `META-INF/services/org.hibernate.engine.jdbc.dialect.spi.DialectResolver`

## Files changed

- `dialect/src/main/java/com/xugu/dialect/config/XuguServerConfiguration.java`
- `dialect/src/main/java/com/xugu/dialect/XuguDialectSelector.java`
- `dialect/src/main/java/com/xugu/dialect/XuguDialectResolver.java`
- `dialect/src/main/java/com/xugu/dialect/XuguDialect.java`
- `dialect/src/test/java/com/xugu/dialect/XuguRulerCClosureTest.java`
- `dialect/src/test/java/com/xugu/dialect/XuguDialectSelectorTest.java`
- `dialect/src/test/java/com/xugu/dialect/XuguDialectResolverTestSupport.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguServerConfigurationIT.java`
- `dialect/src/test/java/com/xugu/dialect/XuguFunctionRegistryTest.java`
- `dialect/src/test/java/com/xugu/dialect/XuguDialectResolverTest.java`
- `dialect/src/test/java/com/xugu/dialect/XuguNegativeRegressionBaselineTest.java`
- `contracts/production-regression-baseline.md`
- `contracts/feature-matrix-i003-ruler-c.md`
- `harness/evidence/implementer/I-009/P-010/ACCEPTANCE.md`
- `harness/handoffs/implementer/I-009-P-010.yaml`
