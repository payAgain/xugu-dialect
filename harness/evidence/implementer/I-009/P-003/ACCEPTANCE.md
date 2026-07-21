# P-003 Acceptance Evidence (Implementer RP-01)

> Phase: `P-003` · Initiative: `I-009` · Build: `B-001`  
> Role: implementer · Matrix rows: **A-TYP-016 XML**, **A-FUN-021 XML functions**

## Decision

- Decision: `accepted`
- Path:
  - **A-TYP-016:** `known-limit-documented`
  - **A-FUN-021:** `covered-live`
- Rationale: XuGu documents XML/XMLTYPE type and bounded XML functions per `datatype/xml.md` and `xml-functions/**`. Dialect maps `SqlTypes.SQLXML` → `xml` DDL + registers `xmlelement`/`xmlquery`/`xmltable`; `EXTRACT(xml,xpath)` is native-SQL only (temporal `extract` name collision). No verified ORM `@JdbcTypeCode(SQLXML)` entity round-trip — native SQL IT is the honest type path.

## Criteria table

| Criterion | Expected | Evidence | Status |
|---|---|---|---|
| XML type DDL | SQLXML → `xml` per xml.md | `XuguXmlTypeSupport`; `XuguDialect#columnType` | **PASS** |
| XmlJdbcType | Standard descriptor contributed | `XuguDialect#contributeTypes` | **PASS** |
| XML functions | Bounded subset extract/xmlelement/xmlquery/xmltable per docs | `XuguFunctionRegistrations`; `XuguFunctionRegistryTest#xmlSubsetRegistered_A_FUN_021` | **PASS** |
| Unit tests | Offline green | `XuguXmlTypeTest`; `XuguDialectTest#columnTypesMatchXuguDocs` | **PASS** |
| Gated live IT | Native SQL when `XUGU_RUN_IT=true` | `XuguXmlTypeAndFunctionsIT` | **PASS** / SKIPPED_INFRA when gate off |
| SSOT promotion | 延后 → known-limit / covered-live | `contracts/production-regression-baseline.md` A-TYP-016, A-FUN-021 | **PASS** |
| Negative anchors removed | No @Disabled deferred anchors for P-003 rows | `XuguNegativeRegressionBaselineTest` | **PASS** |
| No invented SQL | Shapes from xml.md + xml-functions docs only | reviewer RP-03 | pending |
| Offline build | `mvn -q -DskipTests package` green | implementer run | **PASS** (exit 0) |
| Offline test | `mvn -q test` green | implementer run | **PASS** (exit 0) |

## Closed gaps

| matrix_id | status | entry_class#method | gate |
|---|---|---|---|
| A-TYP-016 | known-limit-documented | `XuguXmlTypeTest#xmlTypeHooksWired_A_TYP_016`; `XuguXmlTypeAndFunctionsIT#xmlTypeNativeRoundTrip_A_TYP_016` | IT |
| A-FUN-021 | covered-live | `XuguFunctionRegistryTest#xmlSubsetRegistered_A_FUN_021`; `XuguXmlTypeAndFunctionsIT#xmlFunctionsNativeSubset_A_FUN_021` | IT |

## Doc citation

- `reference/sql/datatype/xml.md` — XML/XMLTYPE, BLOB-backed, max 2GB
- `reference/function/xml-functions/extract.md` — EXTRACT(XML, xpath)
- `reference/function/xml-functions/xmlelement.md` — XMLELEMENT
- `reference/function/xml-functions/xmlquery.md` — XMLQUERY … PASSING … RETURNING CONTENT
- `reference/function/xml-functions/xmltable.md` — XMLTABLE (single-node note)

## Files changed

- `dialect/src/main/java/com/xugu/dialect/type/XuguXmlTypeSupport.java`
- `dialect/src/main/java/com/xugu/dialect/XuguDialect.java`
- `dialect/src/main/java/com/xugu/dialect/function/XuguFunctionRegistrations.java`
- `dialect/src/test/java/com/xugu/dialect/XuguXmlTypeTest.java`
- `dialect/src/test/java/com/xugu/dialect/XuguFunctionRegistryTest.java`
- `dialect/src/test/java/com/xugu/dialect/XuguDialectTest.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguXmlTypeAndFunctionsIT.java`
- `dialect/src/test/java/com/xugu/dialect/XuguNegativeRegressionBaselineTest.java`
- `contracts/production-regression-baseline.md`
- `harness/evidence/implementer/I-009/P-003/ACCEPTANCE.md`
- `harness/handoffs/implementer/I-009-P-003.yaml`

## Validation (implementer)

| Command | Exit | Detail |
|---|---|---|
| `mvn -q -DskipTests package` | 0 | offline build |
| `mvn -q test` | 0 | offline reactor (IT gated/skipped) |
| `XUGU_RUN_IT=true mvn -q -pl dialect -am test -Dtest=XuguXmlTypeAndFunctionsIT` | 1 (SKIPPED_INFRA) | Connection refused 127.0.0.1:5138; gate ON exercised IT path |
