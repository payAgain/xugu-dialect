# Cross-Layer Thinking Guide

> Most Accept/regression bugs here are boundary bugs: dialect SQL ↔ live DB ↔ demo config ↔ matrix SSOT.

---

## Layers in This Repo

```text
Xugu docs (E:\Work\docs\content, read-only)
        ↓ cites
contracts/ + feature matrices + production-regression-baseline
        ↓ implemented by
dialect (XuguDialect + support packages + SPI)
        ↓ consumed by
demo-spring-boot (entities, application.yml, IT)
        ↓ explained by
docs/user-guide/ + demo README
```

Trellis (`.trellis/`) governs AI workflow only — never on the runtime classpath.

---

## Map the Change

Before coding, answer:

1. **Which matrix id(s)?** (`A-*` / `C-*`) — or is it negative-only / docs-only?
2. **What SQL/render path?** (Dialect override, LimitHandler, SqlAstTranslator, function descriptor, JDBC type)
3. **What proves it offline?** (unit string/registry test)
4. **What proves it live?** (gated IT) — required for covered-live
5. **Does the consumer need a knob?** (`application.yml`, converter, `json_functions_enabled`, Flyway)
6. **Which SSOT files update?** (baseline status, user-guide known-limit, contract)

---

## Boundary Checklist

| Boundary | Common failure |
|----------|----------------|
| Docs → Dialect | Inventing SQL not in Xugu content / 文档不允许 |
| Dialect → JDBC | NONE vs compatible modes; WAIT units (ms); identity generated-keys |
| Dialect → Hibernate AST | LimitHandler vs SqlAstTranslator divergence (`FETCH FIRST` sneaking back) |
| Dialect → Demo | Feature works in dialect IT but Boot config missing (JSON HQL flag, UUID jdbc type) |
| Code → Baseline | Status still `gap` / wrong entrypoint after rename |
| Known-limit → Claims | Marketing or PR text says “full support” while SSOT says known-limit |

---

## Status Transitions (honest)

| From → To | Requires |
|-----------|----------|
| gap → covered-live | Gated IT green on real XuguDB + baseline row update |
| covered-live → known-limit | Formal waiver note + user-doc known limitation + keep unit locks |
| any → 文档不允许 SQL | **Forbidden** — remove positive SQL; add negative assertion |

Charter Accept rollup reference (I-010): **91/98 covered-live + 7 KL** — always re-read `contracts/production-regression-baseline.md` § Summary counts before quoting.

---

## Compatible Mode

Default is **NONE** end-to-end:

- Dialect IT: `XuguTestConnection` appends `compatiblemode=NONE`
- Demo: default JDBC URL query param
- Do not “fix” failures by switching to MySQL/Oracle compatible mode unless a future Charter decision says so

---

## Verification Across Layers

```bash
mvn -q -DskipTests package
mvn -q test
# when claiming live / Accept:
XUGU_RUN_IT=true mvn -q test
```

If only demo changed:

```bash
mvn -q -pl demo-spring-boot -am test -Dxugu.run.integration=true
```

---

## Anti-Patterns

- Updating dialect SQL without tests.
- Updating tests without baseline/matrix when Accept-facing.
- Documenting demo-only workarounds as dialect capabilities.
- Assuming `docs/architecture.md` historical “scaffolded” status still describes today’s code — prefer contracts + source.
