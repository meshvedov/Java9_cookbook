package ch6_db;

import org.junit.*;

import java.sql.*;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class DbRelatedMethodsTest {
    private DbRelatedMethods dbRelatedMethods = new DbRelatedMethods();

    @BeforeClass
    public static void setupForTheClass() {
        System.out.println("setupForTheClass() is called");
        execute("create table text (id integer not null, text character varying not null)");
    }

    @AfterClass
    public static void cleanUpAfterTheClass() {
        System.out.println("cleanAfterClass() is called");
        execute("drop table text");
    }

    @Before
    public void setupForEachMethod() {
        System.out.println("setupForEachMethod() is called");
        executeUpdate("insert into text (id, text) values (1, ? )", "Text 01");
    }

    @After
    public void cleanUpAfterEachMethod() {
        System.out.println("cleanUpAfterEachMethod() is called");
        execute("delete from text");
    }

    @Test
    public void updateAllTextRecordsTo1() {
        System.out.println("updateAllTextRecordsTo1 is called");
        String testString = "Whatever";
        System.out.println(" Update all records to " + testString);
        dbRelatedMethods.updateAllTextRecordsTo(testString);
        int count = countRecordsWithText(testString);
        assertEquals("Assert number of records with " + testString + ": " , 1, count);
        System.out.println(" " + count + " records are updated to " + testString);
    }

    @Test
    public void updateAllTextRecordsTo2() {
        System.out.println("updateAllTextRecordsTo2() is called");
        String testString = "Whatever";
        System.out.println(" Update All records to Unexpected");
        dbRelatedMethods.updateAllTextRecordsTo("Unexpected");
        executeUpdate("insert into text(id, text) values(2, ?)", "Text 01");
        System.out.println("  Update all records to " + testString);
        dbRelatedMethods.updateAllTextRecordsTo(testString);
        int count = countRecordsWithText(testString);
        assertEquals("Assert number of records with " + testString + ": ", 2, count);
        System.out.println("  " + count + " records are updated to " + testString);
    }

    public int countRecordsWithText(String text) {
        try (Connection conn = getDbConnection()) {
            PreparedStatement st = conn.prepareStatement("select count(*) from text where text = ?");
            st.setString(1, text);
            ResultSet rs = st.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static void execute(String sql){
        System.out.println("  " + sql);
        try (Connection conn = getDbConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void executeUpdate(String sql, String text) {
        System.out.println(" " + sql + ", params = " + text);
        try (Connection conn = getDbConnection()) {
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, text);
            int count = st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection getDbConnection() {
        String url = "jdbc:postgresql://localhost:5432/java9cookbook";
        Properties prop = new Properties();
        prop.put("user", "mic");
        prop.put("password", "123456");

        try {
            return DriverManager.getConnection(url, prop);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class DbRelatedMethods {

        public void updateAllTextRecordsTo(String text) {
            executeUpdate("update text set text = ?", text);
        }

        private void executeUpdate(String sql, String text) {
            try (Connection conn = getDbConnection()) {
                PreparedStatement st = conn.prepareStatement(sql);
                st.setString(1, text);
                st.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private Connection getDbConnection() {
            String url = "jdbc:postgresql://localhost:5432/java9cookbook";
            Properties prop = new Properties();
            prop.put("user", "mic");
            prop.put("password", "123456");

            try {
                return DriverManager.getConnection(url, prop);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
