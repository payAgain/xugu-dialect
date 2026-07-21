# Error Handling — dialect

> This module is a library, not an HTTP API. “Errors” mean JDBC vendor failures mapped to Hibernate exception types.

---

## Ownership

| Class | Role |
|-------|------|
| `exception/XuguErrorCodes` | Documented Xugu numeric codes (unique/FK/check/not-null, deadlock, lock timeout, …) |
| `exception/XuguSQLExceptionConversionDelegate` | Builds `SQLExceptionConversionDelegate`; resolves vendor code |
| `exception/XuguViolatedConstraintNameExtractor` | Parses constraint / field names from messages when present |
| `XuguDialect#buildSQLExceptionConversionDelegate` | Wires the delegate |
| `XuguDialect#getViolatedConstraintNameExtractor` | Wires the extractor |

There is **no** project-wide custom checked exception hierarchy for dialect internals. Prefer Hibernate’s `ConstraintViolationException`, `LockTimeoutException`, `LockAcquisitionException`, etc.

---

## Conversion Pattern

Resolution order for vendor code (`XuguSQLExceptionConversionDelegate#resolveErrorCode`):

1. `SQLException.getErrorCode()` if non-zero
2. `SQLState` prefix `xugu…` numeric suffix
3. Message token `[ENNNNN]`

Then map via `switch` on `XuguErrorCodes` constants. Unknown codes return `null` so Hibernate’s default chain can continue.

Reference unit tests: `XuguExceptionConversionTest`.

---

## What Integrators See

- Constraint failures → `ConstraintViolationException` with `ConstraintKind` set when mapped.
- Deadlock → `LockAcquisitionException`.
- Lock timeout / upgrade conflict → `LockTimeoutException`.

Demo IT that exercises rollback behavior: `demo-spring-boot` `DemoConstraintRollbackIT` (consumer path; dialect mapping must stay consistent).

---

## Logging

The dialect library does **not** use SLF4J for SQLException conversion. Do not add noisy logging inside the hot conversion path. Demo-side logging is separate (see demo package specs).

---

## Anti-Patterns

- Swallowing `SQLException` inside dialect helpers without rethrow / Hibernate conversion.
- Mapping undocumented codes “because the driver once returned them” without updating `XuguErrorCodes` + tests + docs citation.
- Introducing Spring `@ControllerAdvice`-style API error envelopes in this module.
- Logging passwords, full JDBC URLs with embedded credentials, or production secrets in tests.
