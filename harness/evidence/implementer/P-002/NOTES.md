# P-002 Implementer NOTES — exception mapping

> **Role:** implementer  
> **Invocation:** `impl-p002-20260716`  
> **Phase / Build:** P-002 / B-002 / I-003  

## Delivered
- `com.xugu.dialect.exception.XuguErrorCodes` — docs e13/e14/e16/e19
- `XuguSQLExceptionConversionDelegate` — unique/FK/check/not-null → ConstraintViolationException; deadlock → LockAcquisitionException; lock timeout → LockTimeoutException
- `XuguViolatedConstraintNameExtractor` — E16005 field template
- Wired on `XuguDialect.buildSQLExceptionConversionDelegate` / `getViolatedConstraintNameExtractor`
- Offline: `XuguExceptionConversionTest`
- ORM entrypoint IT: `XuguExceptionMappingIT` (Session duplicate unique → ConstraintKind.UNIQUE); live gate PASS (ErrorCode 13001)

## Forbidden respected
- No sibling source port; no harness framework changes; version 7.4.5.Final; no Ship
