# P-008 Implementer Checklist (RP-01)

- [x] Prove live JDBC product/driver names
- [x] `XuguDialectResolver` implements Hibernate 7.4 `org.hibernate.engine.jdbc.dialect.spi.DialectResolver`
- [x] Match rule: product/driver contains `xugu` (live: `XuguDB` / `XuguDB JDBC Driver`)
- [x] Non-match MySQL/Oracle/Postgres → null (A-SPI-004)
- [x] Construct `XuguDialect` with `DatabaseVersion` from metadata (A-SPI-003)
- [x] META-INF/services registration (A-SPI-002)
- [x] Isolation RC/RR/SERIALIZABLE documented; RU NOT claimed (A-XCUT-005/006)
- [x] compatible_mode NONE documented (A-XCUT-003)
- [x] Unit tests resolver + services resource
- [x] Gated IT: explicit dialect + SPI auto-resolve
- [x] Jar services listing evidence
- [x] Matrix hints updated
- [x] verify.py --phase P-008
- [ ] Accept (orchestrator / later)
- [ ] Commit (Human Gate / later)
