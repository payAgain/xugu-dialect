# P-002 Implementer Checklist (I-002)

- [x] `getQuerySequencesString()` → `select * from all_sequences`
- [x] `SequenceInformationExtractorXuguDatabaseImpl` column mapping per docs
- [x] Offline unit: query non-null + extractor instance
- [x] Gated IT happy path: validate succeeds when sequence exists
- [x] Gated IT boundary: validate fails when sequence missing
- [x] `mvn -q test` EXIT 0
- [x] `mvn -q test -Dxugu.run.integration=true` EXIT 0
- [x] Independent RP-02 test
- [x] RP-03 reviewer approve
- [x] Accept / must-commit
