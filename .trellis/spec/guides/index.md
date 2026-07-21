# Thinking Guides

> Ask the right questions before changing dialect, demo, contracts, or docs.

---

## Available Guides

| Guide | Purpose | When to Use |
|-------|---------|-------------|
| [Code Reuse Thinking Guide](./code-reuse-thinking-guide.md) | Avoid duplicate SQL/registry/gate helpers | New type/function/support class or test helper |
| [Cross-Layer Thinking Guide](./cross-layer-thinking-guide.md) | Trace dialect → JDBC → demo → contracts/docs | Features spanning modules or SSOT files |

---

## Quick Triggers

### Cross-layer

- [ ] Changing SQL emitted by `XuguDialect` / AST / LimitHandler
- [ ] Matrix row status change (covered-live ↔ known-limit)
- [ ] Demo config or entity mapping for a dialect capability
- [ ] User-guide or contract text that claims behavior

→ [Cross-Layer Thinking Guide](./cross-layer-thinking-guide.md)

### Code reuse

- [ ] New JDBC URL / IT gate helper
- [ ] New function registration family
- [ ] Copying patterns from MySQL/Oracle Hibernate dialects (structure OK; code copy not)
- [ ] Second converter/type helper that overlaps an existing support class

→ [Code Reuse Thinking Guide](./code-reuse-thinking-guide.md)

### AI review false positives (project-specific)

- [ ] “Missing validation” on internal matrix SSOT markdown — trusted project docs, not user input
- [ ] “Bug” that is a documented known-limit with unit + waived live path
- [ ] “Use MySQLDialect” suggestions — **forbidden** by Charter/ADR-0001

**Rule:** verify CRITICAL/WARNING findings against `XuguDialect` javadoc, contracts, and tests before acting.

---

## Pre-Modification Rule

Before changing a SQL fragment, error code, matrix id, or default URL parameter:

```bash
rg -n "exact_token" dialect demo-spring-boot contracts docs
```

Update every SSOT hit (code + test + contract/baseline + user-guide) in the same change set when behavior is consumer-visible.

---

**Core principle:** 30 minutes checking matrix + docs beats a false covered-live claim.
