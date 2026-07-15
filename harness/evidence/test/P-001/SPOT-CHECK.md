# Spot-check P-001 RP-02 (`test-p001-20260715`)

## HQL pagination uses limit/offset (not fetch first)

Live IT log (`mvn-test-integration.log`):

```text
Hibernate: select phpe1_0.id from HIB_P001_HQL_PAGE phpe1_0 order by phpe1_0.id limit ? offset ?
```

- Contains `limit` and `offset`
- Does **not** contain `fetch first` / `rows only`
- `XuguHqlPaginationIT` asserts the same and window `[5, 6, 7]` for `setFirstResult(4)` / `setMaxResults(3)`

## Factory non-null

- Source: `XuguDialect.getSqlAstTranslatorFactory()` returns non-null `StandardSqlAstTranslatorFactory` building `XuguSqlAstTranslator`
- Offline: `XuguSqlAstTranslatorTest.sqlAstTranslatorFactoryIsNonNullAndBuildsXuguTranslator` (included in `mvn -q test` EXIT 0)
- IT: asserts `new XuguDialect().getSqlAstTranslatorFactory()` non-null before SessionFactory boot

## Version 7.4.5.Final

| Artifact | Evidence |
|---|---|
| Parent POM | `<version>7.4.5.Final</version>`, `hibernate.version=7.4.5.Final` |
| `dialect/pom.xml` | `<version>7.4.5.Final</version>` |
| `demo-spring-boot/pom.xml` | module + `hibernate.version=7.4.5.Final` |
| Built JAR | `dialect/target/xugu-dialect-7.4.5.Final.jar` present (24277 bytes) |
| Runtime | Hibernate log `HHH000001: Hibernate ORM core version 7.4.5.Final` |

## Observed flow

`hql-pagination-offset-fetch-real-db` → PASS (see `IT-RESULT.txt`)
