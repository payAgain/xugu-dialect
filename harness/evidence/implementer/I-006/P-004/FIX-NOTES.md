# P-004 implementer fix notes (post live IT FAIL)

## UUID (A-TYP-012)

- Failure: `UUIDJdbcType.doExtract` → Xugu JDBC `[E50044] Required type conversion not allowed`
- Pattern from dialect IT (`XuguTypeRoundTripIT`): bind GUID as string, not `UUID.class`
- Fix: `UuidAsVarcharConverter` + column `guid_val varchar(36)` on `DemoTypedSample`
- Schema helper: `DemoXuguJdbc.recreateTypedSampleTable()`; `DemoTypesIT` static init migrates leftover `guid` columns

## JSON FormatMapper (A-TYP-013 / C-JSON-001 / A-FUN-017)

- Failure: Hibernate `Could not find a FormatMapper for the JSON format`
- Fix: add `spring-boot-starter-jackson` to `demo-spring-boot/pom.xml` (Boot 4 Jackson 3; Hibernate detects `tools.jackson.databind.json.JsonMapper`)

## Out of scope / unchanged

- dialect / `org/` — not modified
- GAV `7.4.5.Final`
- A-TYP-011 remains dialect-it-only
- Demo `@Test` count remains 28
