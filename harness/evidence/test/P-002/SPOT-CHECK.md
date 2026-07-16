# P-002 Spot Check

| Check | Result | Notes |
|---|---|---|
| `getQuerySequencesString()` contains `all_sequences` | PASS | Offline unit + Dialect source |
| Extractor is `SequenceInformationExtractorXuguDatabaseImpl` | PASS | Offline unit |
| Column map: `seq_name` / `min_val` / `max_val` / `step_val` | PASS | Matches `all_sequences.md`; catalog/schema/start null |
| No invented schema join | PASS | SCHEMA_ID not joined |
| Prefer `seq_name` over V12-only `sequence_name` | PASS | |
| Validate sees existing sequence | PASS | Live IT |
| Missing sequence still fails | PASS | Live IT diagnostic |
| No sibling dialect port | PASS | Local implementation only |
| Version 7.4.5.Final | PASS | dialect/pom.xml |
| No P-001 SqlAstTranslator redo | PASS | Diff limited to sequence metadata hooks |
