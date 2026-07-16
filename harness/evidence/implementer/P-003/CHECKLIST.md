# P-003 Implementer Checklist

- [x] Matrix A-PAG-* annotated for SqlAstTranslator / LIMIT OFFSET (not ANSI FETCH)
- [x] Matrix A-LCK-* annotated for FOR UPDATE → LIMIT → WAIT
- [x] Matrix A-SEQ-001 annotated for all_sequences + extractor / validate
- [x] Troubleshooting: E19132 unexpected OFFSET + missing sequence (fixed in 7.4.5.Final same GAV)
- [x] User-guide examples / index updated
- [x] Contract §8 I-002 behavior-only note
- [x] README one-liner
- [x] No new Definition A capability IDs
- [x] SeqProbe.java deleted (if present)
- [x] xugu-hibernate-test re-run: **N/A** (explicit in NOTES)
- [x] Version remains 7.4.5.Final
- [ ] Independent RP-02 / RP-03 / Accept (orchestrator)
