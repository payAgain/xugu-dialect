# Verification Guide

## Purpose

Define the executable checks required before claiming work complete.

## Baseline (Maven)

```text
mvn -q -DskipTests package
mvn -q test
```

Optional live DB integration gate:

```text
XUGU_RUN_IT=true mvn -q test
# or: mvn -q test -Dxugu.run.integration=true
```

Demo module live smoke:

```text
mvn -q -pl demo-spring-boot -am test -Dxugu.run.integration=true
```

## Agent workflow

This repo is managed by **Trellis** (`.trellis/`). Use Trellis tasks / `/trellis:finish-work` for session lifecycle. Product acceptance still rests on the Maven commands above plus contract SSOT under `contracts/`.

## Related docs

- User-facing verify steps: [docs/user-guide/03-verify.md](user-guide/03-verify.md)
- Production regression SSOT: [contracts/production-regression-baseline.md](../contracts/production-regression-baseline.md)
- Historical initiative archives: [.trellis/tasks/archive/2026-07/](../.trellis/tasks/archive/2026-07/INDEX.md)
