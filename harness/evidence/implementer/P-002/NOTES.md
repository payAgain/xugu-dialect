# P-002 Implementer Notes (I-002 sequence metadata)

**Invocation:** `impl-p002-20260716`  
**Role:** implementer  
**Phase / Build / Initiative:** P-002 / B-002 / I-002  
**Date:** 2026-07-16  
**Accept / commit:** pending independent RP-02 + RP-03

## Root cause

`XuguDialect.getQuerySequencesString()` defaulted to `null` → Hibernate used `SequenceInformationExtractorNoOpImpl` → `hbm2ddl.auto=validate` reported `missing sequence` even when the sequence existed in XuGu `ALL_SEQUENCES`.

## What this fix delivered

1. `SequenceInformationExtractorXuguDatabaseImpl` extending `SequenceInformationExtractorLegacyImpl`:
   - name: `seq_name` (V11+V12; prefer over V12-only `sequence_name`)
   - catalog / schema / start: `null` (no string qualifier / start columns; `SCHEMA_ID` is int)
   - min / max / increment: `min_val` / `max_val` / `step_val`
2. `XuguDialect`:
   - `getQuerySequencesString()` → `select * from all_sequences`
   - `getSequenceInformationExtractor()` → Xugu INSTANCE
3. Offline unit assert in `XuguIdentitySequenceTest.sequenceMetadataQueryAndExtractorWired`
4. Gated IT `XuguSchemaValidateIT`:
   - happy: CREATE_ONLY + validate succeeds; JDBC proves row in `all_sequences`
   - boundary: drop sequence → validate fails diagnostically
5. Matrix note on A-SEQ-001 (no new capability id)
6. Version remains **7.4.5.Final**; no P-001 translator redo; no sibling dialect port

## Validation

```text
mvn -q test                                          → EXIT 0
mvn -q test -Dxugu.run.integration=true              → EXIT 0
```

SQL truth: `E:\Work\docs\content\reference\system-view\all\all_sequences.md`
