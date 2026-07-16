# P-004 REVIEW (I-003)

> **Invocation:** `rev-p004-20260716` · readonly

| Check | Result |
|---|---|
| Flags match XuGu docs (window / WITH) | PASS |
| ORM entrypoint IT (not SPI-only) | PASS — `XuguWindowCteIT` |
| SQL contains OVER / WITH | PASS — StatementInspector asserts |
| No sibling source port | PASS |
| No harness framework rewrite | PASS |
| Version 7.4.5.Final | PASS |

## Decision
**approve**
