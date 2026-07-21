# Bootstrap Task: Fill Project Development Guidelines

**You (the AI) are running this task. The developer does not read this file.**

Populate `.trellis/spec/` from the real Xugu Hibernate dialect codebase (not generic backend scaffolding).

---

## Status

- [x] Analyze repository (dialect + demo-spring-boot + contracts)
- [x] Replace template `backend/` specs with package-aligned `dialect/` + `demo-spring-boot/`
- [x] Adapt thinking guides to this product
- [x] Wire Trellis `packages` in `.trellis/config.yaml`
- [x] Verify no template placeholders remain

---

## Spec tree (final)

### Package: dialect / layer: library

| File | Content |
|------|---------|
| `.trellis/spec/dialect/library/index.md` | Checklist + index |
| `.trellis/spec/dialect/library/directory-structure.md` | Packages / SPI layout |
| `.trellis/spec/dialect/library/dialect-implementation.md` | XuguDialect wiring, SPI, local SQL locks |
| `.trellis/spec/dialect/library/sql-types-functions.md` | Types, functions, AST/pagination |
| `.trellis/spec/dialect/library/error-handling.md` | SQLException conversion |
| `.trellis/spec/dialect/library/testing.md` | Unit vs gated IT |
| `.trellis/spec/dialect/library/quality-guidelines.md` | Forbidden patterns, 91/98+7KL, verify |

### Package: demo-spring-boot / layer: app

| File | Content |
|------|---------|
| `.trellis/spec/demo-spring-boot/app/index.md` | Checklist + index |
| `.trellis/spec/demo-spring-boot/app/directory-structure.md` | Demo layout / Flyway shims |
| `.trellis/spec/demo-spring-boot/app/spring-jpa-patterns.md` | YAML, entities, Hibernate force |
| `.trellis/spec/demo-spring-boot/app/testing.md` | Offline vs gated Boot IT |
| `.trellis/spec/demo-spring-boot/app/quality-guidelines.md` | Consumer-only rules |

### Guides

| File | Content |
|------|---------|
| `.trellis/spec/guides/index.md` | Project triggers |
| `.trellis/spec/guides/code-reuse-thinking-guide.md` | Dialect/demo reuse |
| `.trellis/spec/guides/cross-layer-thinking-guide.md` | Docs↔dialect↔demo↔SSOT |

### Removed (template)

- `.trellis/spec/backend/*` (generic backend scaffolding: routes/services/logging API style)

---

## Architecture Context (analysis notes)

- Product: XuguDB Hibernate Dialect — modules `dialect/` + `demo-spring-boot/`.
- Baselines: Hibernate 7.4.5.Final, JDK 17, JDBC `xugu-jdbc-12.3.6.jar`, `compatiblemode=NONE`.
- Accept口径: **91/98 covered-live + 7 known-limit-documented** (`contracts/production-regression-baseline.md`, I-010).
- GitNexus/ABCoder MCP unavailable in bootstrap session; evidence from source + contracts + docs.
- `docs/architecture.md` still contains historical “scaffolded” wording — specs prefer live source/contracts over that stale status table.

---

## Completion

When Human confirms, finish/archive:

```bash
python ./.trellis/scripts/task.py finish
python ./.trellis/scripts/task.py archive 00-bootstrap-guidelines
```
