import java.sql.*;
public class Probe {
  public static void main(String[] a) throws Exception {
    Class.forName("com.xugu.cloudjdbc.Driver");
    String url = "jdbc:xugu://127.0.0.1:5138/SYSTEM?user=SYSDBA&password=SYSDBA&compatiblemode=NONE";
    try (Connection c = DriverManager.getConnection(url); Statement st = c.createStatement()) {
      try { st.execute("DROP SEQUENCE IF EXISTS HIB_P004_PROBE_SEQ"); } catch(Exception e){}
      st.execute("CREATE SEQUENCE HIB_P004_PROBE_SEQ START WITH 1 INCREMENT BY 1");
      for (String sql : new String[]{
        "ALTER SEQUENCE HIB_P004_PROBE_SEQ RESTART WITH 100",
        "ALTER SEQUENCE HIB_P004_PROBE_SEQ INCREMENT BY 5",
        "SELECT HIB_P004_PROBE_SEQ.NEXTVAL FROM DUAL",
        "CREATE TABLE HIB_P004_ARR (id INT PRIMARY KEY, arr INTEGER ARRAY)",
        "INSERT INTO HIB_P004_ARR VALUES (1, ARRAY[10,20,30])",
        "SELECT arr FROM HIB_P004_ARR WHERE id=1",
        "SELECT JSON_UNQUOTE('\"x\"') FROM DUAL",
        "SELECT JSON_LENGTH('{\"a\":1,\"b\":2}') FROM DUAL",
        "SELECT JSON_TYPE('{\"a\":1}') FROM DUAL"
      }) {
        try {
          if (sql.toUpperCase().startsWith("SELECT")) {
            try (ResultSet rs = st.executeQuery(sql)) { rs.next(); System.out.println("OK " + sql + " => " + rs.getObject(1)); }
          } else {
            st.execute(sql); System.out.println("OK " + sql);
          }
        } catch (Exception e) { System.out.println("FAIL " + sql + " => " + e.getMessage()); }
      }
      try { st.execute("DROP TABLE IF EXISTS HIB_P004_ARR"); } catch(Exception e){}
      try { st.execute("DROP SEQUENCE IF EXISTS HIB_P004_PROBE_SEQ"); } catch(Exception e){}
    }
  }
}
