# Spot-check P-009 RP-02

## hibernate-version-forced-745
dependency:tree includes=org.hibernate.orm:hibernate-core
-> org.hibernate.orm:hibernate-core:jar:7.4.5.Final:compile
pom property hibernate.version=7.4.5.Final (Boot BOM default 7.4.1.Final overridden)

## env credentials
application.yml:
  url: ${XUGU_JDBC_URL:...}
  username: ${XUGU_USER:SYSDBA}
  password: ${XUGU_PASSWORD:SYSDBA}
Defaults are local Charter SYSDBA only; no production secrets hardcoded.
README documents env override.

## dialect not implemented in demo
demo-spring-boot/src/main/java contains only:
  DemoApplication, DemoStartupCrudRunner, DemoPerson, DemoPersonRepository
No *Dialect class; config references com.xugu.dialect.XuguDialect from xugu-dialect module.

## spring-boot-demo-starts-against-real-db
DemoPersonCrudIT.persistAndFindPerson PASS on live XuguDB (see IT-RESULT.txt)
