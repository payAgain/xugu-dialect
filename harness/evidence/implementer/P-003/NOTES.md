# P-003 Implementer Notes (I-002 docs / matrix polish)

**Invocation:** `impl-p003-20260716`  
**Role:** implementer  
**Phase / Build / Initiative:** P-003 / B-003 / I-002  
**Date:** 2026-07-16  
**Accept / commit:** pending independent RP-02 + RP-03

## Goal

Align matrix / user-guide / contract notes with delivered P-001 + P-002 hotfix behavior. No dialect Java rewrites. Version remains **7.4.5.Final**.

## Docs aligned with delivery

| Topic | Delivered behavior | Docs touch |
|---|---|---|
| HQL/Criteria pagination | `XuguSqlAstTranslator` → `LIMIT count [OFFSET offset]` (not ANSI OFFSET/FETCH) | Matrix A-PAG-001/002/005; troubleshooting §1–§2; 04 examples |
| Lock + page order | FOR UPDATE → LIMIT → WAIT | Matrix A-LCK-001/003; troubleshooting §1 |
| Schema validate sequences | `getQuerySequencesString` → `all_sequences` + Xugu extractor | Matrix A-SEQ-001; troubleshooting §3; contract §8 I-002 note |
| Same GAV | Behavior fix, no version bump | README one-liner; contract §8; troubleshooting |

## Files changed (allowed paths only)

- `contracts/feature-matrix-definition-a.md` (annotate existing rows only; no new Definition A IDs)
- `contracts/xugu-dialect.contract.md` (§8 I-002 hotfix note)
- `docs/user-guide/05-troubleshooting.md` (E19132 + missing sequence)
- `docs/user-guide/04-feature-matrix.md` (examples)
- `docs/user-guide/README.md` (index line)
- `docs/feature-matrix-definition-a.md` (pointer + I-002 note)
- `README.md` (one-liner)
- Deleted leftover untracked `SeqProbe.java` (P-002 probe residue)

## External re-run: `E:\Work\java\xugu-hibernate-test`

**N/A** — not executed in this Phase. Primary acceptance evidence remains this repo’s gated IT + `verify.py`. If re-run later, treat as **non-unique** extra evidence only.

## Explicitly not done

- Dialect behavior rewrites
- Version bump / Ship / tag / push
- Official `E:\Work\docs\content` writes
- Sibling dialect port
- Independent RP-02 / RP-03 / Accept
