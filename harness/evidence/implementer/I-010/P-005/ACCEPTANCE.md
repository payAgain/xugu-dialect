# ACCEPTANCE — I-010 / P-005 (RP-01 implementer)

> Role: implementer · Matrix row: **A-FUN-021** XML HQL Session (`xmlelement` / `xmlquery`) + XMLTABLE SSOT
> Branch: `feat/i-010-orm-hql-quality-completion`
> Completed: 2026-07-21T15:40:00+08:00

## Decision

- **Path:** HQL `Session.createQuery` **positive** for `xmlelement` + `xmlquery`
- **SSOT:** remain **known-limit-documented** — XMLTABLE single-node / empty→assumption skip; do **not** claim covered-live for XMLTABLE (or whole row) while that limit holds
- **Rationale:** Named `xmlelement` already matches XuGu `XMLELEMENT(xmlname[, xmlvalue])`. `xmlquery` must emit `xmlquery(?1 PASSING ?2 RETURNING CONTENT)` per `xmlquery.md` — switched to pattern descriptor. Reuse `I010P003XmlEntity` XML column for PASSING. XMLTABLE stays native IT only.

## HQL surface

```hql
select xmlelement('name', 'xxx')
select xmlquery('/PDRecord/PDName', e.xml) from I010P003XmlEntity e where e.id = 1
```

**Do not** treat HQL `xmltable(...)` as live-covered — native SQL + known-limit skip only.

## Checklist

| Item | Evidence | Result |
|---|---|---|
| `xmlquery` PASSING pattern | `XuguFunctionRegistrations` patternDescriptorBuilder | **PASS** |
| Registry unit | `XuguFunctionRegistryTest#xmlSubsetRegistered_A_FUN_021` (PatternBased) | **PASS** |
| HQL Session IT | `XuguXmlTypeAndFunctionsIT#xmlFunctionsHqlSession_A_FUN_021` | **PASS** (code) / **SKIPPED_INFRA** (live) |
| Native IT retained | `xmlFunctionsNativeSubset_A_FUN_021` (XMLTABLE empty→skip) | retained |
| SSOT honesty | baseline + Definition A = **known-limit-documented**; no covered-live for XMLTABLE | **PASS** |

## Delivered files

- `dialect/src/main/java/com/xugu/dialect/function/XuguFunctionRegistrations.java`
- `dialect/src/main/java/com/xugu/dialect/XuguDialect.java` (doc)
- `dialect/src/test/java/com/xugu/dialect/XuguFunctionRegistryTest.java`
- `dialect/src/test/java/com/xugu/dialect/it/XuguXmlTypeAndFunctionsIT.java`
- `contracts/production-regression-baseline.md`
- `contracts/feature-matrix-definition-a.md`

## Residual

- **docs:** optional user-guide note that HQL Session path exists for `xmlelement`/`xmlquery` (status already known-limit — no drift)
- **live:** re-run `XUGU_RUN_IT=true` when DB up for HQL Session proof; keep SSOT known-limit (XMLTABLE)
- **RP-03 reviewer:** readonly audit (no fake covered-live for XMLTABLE)

## Observed flow

- `xml-hql-session-live`: offline unit/IT gate green; HQL IT written; live SKIPPED_INFRA (`192.168.2.239:5138` / `127.0.0.1:5138` refused)
