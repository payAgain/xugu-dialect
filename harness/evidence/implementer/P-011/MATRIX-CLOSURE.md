# P-011 Definition A Matrix Closure

> Phase: P-011 / Build B-011 / Initiative I-001  
> Invocation: `impl-p011-20260715`  
> Role: implementer  
> SSOT: `contracts/feature-matrix-definition-a.md`  
> Date: 2026-07-15

## Verdict

**CLOSED** — all **78** Definition A rows with status **可实现** cite Phase evidence (P-003…P-010).  
No new 可实现 scope invented. Deferred (**延后**) and **文档不允许** rows remain as previously accepted; A-XCUT-012 Ship stays **延后**.

Residual polish this Phase:
- P-007 acceptance hints lacked ✅ checkmarks → added (evidence already in P-007 Accept)
- Root `README.md` → points to `docs/user-guide/`
- AGENTS.md Real commands already match `harness/verification.json` (`build` / `test`)
- Secrets scan: only Charter-local SYSDBA defaults + env placeholders; no production secrets in tree

---

## Evidence root by Phase

| Phase | Primary evidence | ACCEPTANCE |
|---|---|---|
| P-003 | `harness/evidence/implementer/P-003/`, `harness/evidence/test/P-003/` | implementer ACCEPTANCE |
| P-004 | `harness/evidence/implementer/P-004/`, `harness/evidence/test/P-004/` | implementer ACCEPTANCE |
| P-005 | `harness/evidence/implementer/P-005/`, `harness/evidence/test/P-005/` | implementer ACCEPTANCE |
| P-006 | `harness/evidence/implementer/P-006/`, `harness/evidence/test/P-006/` | implementer ACCEPTANCE |
| P-007 | `harness/evidence/implementer/P-007/`, `harness/evidence/test/P-007/` | `harness/evidence/implementer/P-007/ACCEPTANCE.md` |
| P-008 | `harness/evidence/implementer/P-008/`, `harness/evidence/test/P-008/` | `harness/evidence/implementer/P-008/ACCEPTANCE.md` |
| P-009 | `harness/evidence/implementer/P-009/`, `harness/evidence/test/P-009/` | `harness/evidence/implementer/P-009/ACCEPTANCE.md` |
| P-010 | `harness/evidence/docs/P-010/`, `harness/evidence/test/P-010/` | `harness/evidence/docs/P-010/ACCEPTANCE.md` |

---

## 可实现 ID → evidence pointers

### P-003 — Types & DDL (A-TYP / A-DDL / identifiers)

| ID | Evidence pointer |
|---|---|
| A-TYP-001 | P-003 unit + IT — `harness/evidence/test/P-003/` |
| A-TYP-002 | P-003 unit + IT |
| A-TYP-003 | P-003 unit (REAL→FLOAT) |
| A-TYP-004 | P-003; CHAR trim noted |
| A-TYP-005 | P-003 prefer BOOLEAN |
| A-TYP-006 | P-003 IT |
| A-TYP-007 | P-003 columnType |
| A-TYP-008 | P-003 TIMESTAMP chosen |
| A-TYP-009 | P-003 → bare BINARY |
| A-TYP-010 | P-003 IT |
| A-TYP-011 | P-003; NCLOB→CLOB |
| A-TYP-012 | P-003 IT |
| A-TYP-013 | P-003 IT |
| A-TYP-019 | P-003 castPattern default |
| A-DDL-001 | P-003 schema export IT |
| A-DDL-002 | P-003 ALTER ADD COLUMN IT |
| A-DDL-003 | P-003 PK in export IT |
| A-DDL-004 | P-003 NOT NULL IT |
| A-DDL-005 | P-003 default exporter path |
| A-DDL-006 | P-003 drop table IT |
| A-XCUT-001 | P-003 UPPER via IdentifierHelper |
| A-XCUT-002 | P-003 quote `"` |
| A-XCUT-004 | P-003 JDBC TCL smoke + keywords |
| A-XCUT-007 | P-003 registerKeyword subset |

### P-004 — Pagination & locks

| ID | Evidence pointer |
|---|---|
| A-PAG-001 | P-004 `XuguLimitHandler`; IT — `harness/evidence/test/P-004/` |
| A-PAG-002 | P-004 `LIMIT count OFFSET offset` |
| A-PAG-003 | P-004 bind markers |
| A-LCK-001 | P-004 `for update` + IT |
| A-LCK-002 | P-004 `for update of …` + IT |
| A-LCK-003 | P-004 ms wait / NOWAIT IT |

### P-005 — Identity & Sequence

| ID | Evidence pointer |
|---|---|
| A-IDN-001 | P-005 `identity(1,1)` — `harness/evidence/test/P-005/` |
| A-IDN-002 | P-005 IDENTITY only (NONE) |
| A-IDN-003 | P-005 getGeneratedKeys / LAST_INSERT_ID fallback |
| A-IDN-004 | P-005 INSERT omits id |
| A-SEQ-001 | P-005 create sequence |
| A-SEQ-002 | P-005 drop sequence |
| A-SEQ-003 | P-005 `select seq.nextval from dual` |
| A-SEQ-004 | P-005 `currval('name')` |
| A-SEQ-005 | P-005 START/INCREMENT via SequenceSupport |
| A-XCUT-008 | P-005 `getFromDual()` → ` from dual` |

### P-006 — Functions

| ID | Evidence pointer |
|---|---|
| A-FUN-001 | P-006 concat IT — `harness/evidence/test/P-006/` |
| A-FUN-002 | P-006 substring/substr |
| A-FUN-003 | P-006 length/char_length |
| A-FUN-004 | P-006 lower IT |
| A-FUN-005 | P-006 trim/ltrim/rtrim |
| A-FUN-006 | P-006 replace/locate/position |
| A-FUN-007 | P-006 COALESCE + NVL |
| A-FUN-008 | P-006 abs IT; mod/power/sqrt |
| A-FUN-009 | P-006 ceil/ceiling + trunc |
| A-FUN-010 | P-006 current_timestamp IT; now() |
| A-FUN-011 | P-006 extract(year) IT |
| A-FUN-012 | P-006 to_char/to_date/to_timestamp live probe |
| A-FUN-013 | P-006 cast IT |
| A-FUN-014 | P-006 count/sum IT |
| A-FUN-016 | P-006 uuid() primary |
| A-FUN-017 | P-006 json_value + json_extract |
| A-FUN-018 | P-006 LISTAGG WITHIN GROUP |

### P-007 — Schema / temp / comment / constraints

| ID | Evidence pointer |
|---|---|
| A-SCH-001 | P-007 IT `HIB_P007_SCH` — `harness/evidence/test/P-007/IT-RESULT.txt` |
| A-SCH-002 | P-007 `NameQualifierSupport.SCHEMA` |
| A-SCH-004 | P-007 local temporary table |
| A-SCH-005 | P-007 global temporary; `support_global_tab=ON` |
| A-SCH-006 | P-007 on commit preserve/delete |
| A-SCH-008 | P-007 comment on table |
| A-SCH-009 | P-007 comment on column |
| A-SCH-010 | P-007 inline ` comment '…'` |
| A-SCH-011 | P-007 CreateTableUniqueDelegate |
| A-SCH-012 | P-007 FK permanent tables only |
| A-SCH-013 | P-007 column/table check |
| A-SCH-014 | P-007 drop constraint |
| A-SCH-015 | P-007 truncate table |
| A-SCH-016 | P-007 create [unique] index |

### P-008 — SPI & isolation notes

| ID | Evidence pointer |
|---|---|
| A-SPI-001 | P-008 explicit dialect IT — `harness/evidence/test/P-008/` |
| A-SPI-002 | P-008 META-INF/services + auto-resolve IT |
| A-SPI-003 | P-008 DialectResolutionInfo version |
| A-SPI-004 | P-008 unit non-match MySQL/Oracle/Postgres |
| A-XCUT-003 | P-008 `compatiblemode=NONE` |
| A-XCUT-005 | P-008 ISO_LEVEL 1/2/3 documented |

### P-009 — Demo secrets path

| ID | Evidence pointer |
|---|---|
| A-XCUT-009 | P-009 demo env overrides — `harness/evidence/implementer/P-009/ACCEPTANCE.md`, `demo-spring-boot/` |

### P-010 — User guide (docs closure)

User guide documents matrix navigation and verify path; does not add new 可实现 IDs.  
Evidence: `docs/user-guide/`, `harness/evidence/docs/P-010/ACCEPTANCE.md`.

---

## Count reconciliation

| Status | Matrix count | P-011 closure |
|---|---:|---|
| 可实现 | 78 | **78 closed** with Phase evidence |
| 文档不允许 | 7 | unchanged (correct non-claims) |
| 延后 | 20 | unchanged (incl. A-XCUT-012 Ship) |
| Total | 105 | OK |

---

## Packaging polish checklist

- [x] Root `README.md` → `docs/user-guide/`
- [x] `AGENTS.md` Real commands match `harness/verification.json` (`mvn -q -DskipTests package`, `mvn -q test`)
- [x] No production secrets in VCS (env placeholders + Charter local defaults only)
- [x] Matrix ✅ for P-007 residual rows
- [x] No sibling `hibernate-dialect` reference used
- [x] Ship / Central excluded from this Phase

---

## Bugs fixed this Phase

None discovered requiring dialect Java changes. Residual was documentation/matrix checkmark + packaging README only.
