package ch6_db;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import org.postgresql.ds.PGPoolingDataSource;
import org.postgresql.ds.PGSimpleDataSource;

public class Chapter6Database1 {

    public static void main(String[] args) {
//        demo1_connect();
        demo2_execute_select();
        demo3_execute_insert();
        demo4_traverseRS();
        demo5_select_update_delete_select();
        demo6_prepared();
        demo7_prepared_insert();
        demo8_transaction();
        demo9_transaction();
        demo10_transaction();
        demo11_transaction();
    }

    private static void demo11_transaction() {
        executeUpdate("delete from test");
        executeUpdate("delete from enums where id < 7");
        System.out.println("demo11=====================");
        traverseRS("select * from enums");
        System.out.println();
        try(Connection conn= getDbConnection1()) {
            conn.setAutoCommit(false);
            String[][] values = {{"1", "vehicle", "car"},{"b", "vehicle", "truck"},{"3", "vehicle", "crewcab"}};
            String sql = "insert into enums (id,type, value) values (?,?,?)";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                for (String[] v : values) {
                    try (Statement statement = conn.createStatement()) {
                        System.out.print("id=" + v[0] + ": ");
                        statement.execute("insert into test values ('" + v[2] + "')");
                        st.setInt(1, Integer.parseInt(v[0]));
                        st.setString(2, v[1]);
                        st.setString(3, v[2]);
                        int count = st.executeUpdate();
                        conn.commit();
                        System.out.println("Update count = " + count);
                    } catch (Exception e) {
                        conn.rollback();
                        System.out.println(e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println();
        traverseRS("select * from enums");
        System.out.println();
        traverseRS("select * from test");
    }

    private static void demo10_transaction() {
        executeUpdate("delete from enums where id < 7");
        System.out.println("demo10==================");
        traverseRS("select * from enums");
        System.out.println();
        try(Connection conn = getDbConnection1()) {
            conn.setAutoCommit(false);
            String[][] values = {{"1", "vehicle", "car"}, {"b", "vehicle", "truck"}, {"3", "vehicle", "crewcab"}};
            String sql = "insert into enums (id,type,value) values (?,?,?)";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                for (String[] v : values) {
                    try {
                        System.out.print("id = " + v[0] + ":");
                        st.setInt(1, Integer.parseInt(v[0]));
                        st.setString(2, v[1]);
                        st.setString(3, v[2]);
                        int count = st.executeUpdate();
                        conn.commit();
                        System.out.println("Update count = " + count);
                    } catch (Exception e) {
//                        conn.rollback();
                        System.out.println(e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();
        traverseRS("select * from enums");
    }

    private static void demo9_transaction() {
        executeUpdate("delete from enums where id < 7");
        System.out.println("demo9============================");
        System.out.println();
        try(Connection conn = getDbConnection1()) {
            conn.setAutoCommit(false);
            String sql = "insert into enums (id,type,value) values (1, 'vehicle', 'car')";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                System.out.println(sql);
                System.out.println("Update count = " + st.executeUpdate());
            }
            conn.commit();
            sql = "inseeeert into enums (id,type,value) values (2, 'vehicle', 'car')";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                System.out.println(sql);
                System.out.println("Update count = " + st.executeUpdate());
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println();
        System.out.println("");
        traverseRS("select * from enums");
    }

    private static void demo8_transaction() {
        executeUpdate("delete from enums where id < 7");
        System.out.println("demo8=================");
        System.out.println();
        try(Connection conn = getDbConnection1()) {
            conn.setAutoCommit(false);
            String sql = "insert into enums (id,type,value) values (1,'vehicle','car')";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                System.out.println(sql);
                System.out.println("Update count =" + st.executeUpdate());
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println();
        traverseRS("select * from enums");
    }

    private static void demo7_prepared_insert() {
        executeUpdate("delete from enums where id < 7");
        System.out.println("demo7=====");
        traverseRS("select * from enums");
        System.out.println();
        try(Connection conn = getDbConnection1()) {
            String[][] values = {{"1", "vehicle", "car"}, {"2", "vehicle", "truck"}};
            try (PreparedStatement st = conn.prepareStatement("insert into enums (id, type, value) values (?,?,?)")) {
                for (String[] value : values) {
                    st.setInt(1, Integer.parseInt(value[0]));
                    st.setString(2, value[1]);
                    st.setString(3, value[2]);
                    int count = st.executeUpdate();
                    System.out.println("Update count " + count);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println();
        traverseRS("select * from enums");
    }

    private static void demo6_prepared() {
        executeUpdate("delete from enums where id < 7");
        System.out.println("demo6================");
        try(Connection conn = getDbConnection1()) {
            try (PreparedStatement st = conn.prepareStatement("select id, type, value from enums")) {
                boolean res = st.execute();
                if (res) {
                    ResultSet rs = st.getResultSet();
                    while (rs.next()) {
                        int id = rs.getInt(1); //More efficient than rs.getInt("id")
                        String type = rs.getString(2);
                        String value = rs.getString(3);
                        System.out.println("id = " + id + ", type = " + type + ", value = " + value);
                    }
                } else {
                    int count = st.getUpdateCount();
                    System.out.println("Update count " + count);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void demo5_select_update_delete_select() {
        executeUpdate("delete from enums where id < 7");
        System.out.println("dem5================");
        traverseRS("select * form enums");
        executeUpdate("insert into enums (id,type,value) values (1, 'vehicle', 'car')");
        traverseRS("select * from enums");
        executeUpdate("update enums set value = 'bus' where value = 'car'");
        traverseRS("select * from enums");
        executeUpdate("delete from enums where value = 'bus'");
        traverseRS("select * from enums");
    }

    private static void demo4_traverseRS() {
        System.out.println("DEMO 4 ========================");
        traverseRS("select * from enums");
    }

    private static void traverseRS(String sql) {
        System.out.println("traverseRS(" + sql + "):");
        try(Connection conn = getDbConnection1()) {
            try (Statement st = conn.createStatement()) {
                try (ResultSet rs = st.executeQuery(sql)) {
                    traverseRS(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void traverseRS(ResultSet rs) throws Exception {
        int cCount = 0;
        Map<Integer, String> cName = new HashMap<>();
        while (rs.next()) {
            if (cCount == 0) {
                ResultSetMetaData rsmd = rs.getMetaData();
                cCount= rsmd.getColumnCount();
                for (int i = 1; i <= cCount; i++) {
                    cName.put(i, rsmd.getColumnLabel(i));
                }
            }
            List<String> list = new ArrayList<>();
            for (int i = 1; i <= cCount; i++) {
                list.add(cName.get(i) + " = " + rs.getString(i));
            }
            System.out.println(list.stream().collect(Collectors.joining(", ")));
        }
    }

    private static void demo3_execute_insert() {
        executeUpdate("delete from enums where id < 7");
        System.out.println();
        try(Connection conn = getDbConnection1()) {
            try (Statement st = conn.createStatement()) {
                boolean res = st.execute("insert into enums (id,type,value) values (1, 'vehicle', 'car')");
                if (res) {
                    ResultSet rs = st.getResultSet();
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        String type = rs.getString(2);
                        String value = rs.getString(3);
                        System.out.println("id= " + id + ", type = " + type + ", value = " + value);
                    }
                } else {
                    int count = st.getUpdateCount();
                    System.out.println("Update count = " + count);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void executeUpdate(String sql) {
        try(Connection conn = getDbConnection1()) {
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                int count = st.executeUpdate();
                System.out.println("Update count = " + count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void demo2_execute_select() {
        System.out.println();
        try (Connection connection = getDbConnection1()) {
            try (Statement st = connection.createStatement()){
                boolean res = st.execute("SELECT id, type, value FROM enums");
                if (res) {
                    ResultSet rs = st.getResultSet();
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        String type = rs.getString(2);
                        String value = rs.getString(3);
                        System.out.println("id= " + id + ", type = " + type + ", value = " + value);
                    }
                } else {
                    int count = st.getUpdateCount();
                    System.out.println("Update count = " + count);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void demo1_connect() {
        try (Connection conn = getDbConnection1()){

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection getDbConnection() {
        PGPoolingDataSource source = new PGPoolingDataSource();
        source.setServerName("mic");
        source.setDatabaseName("java9cookbook");
        source.setUser("mic");
        source.setPassword("123456");
        source.setInitialConnections(3);
        source.setMaxConnections(10);
        source.setLoginTimeout(10);

        try {
            return source.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Connection getDbConnection1() {
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

    private static Connection getDbConnection2() {
        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setServerName("mic");
        source.setDatabaseName("java9cookbook");
        source.setUser("mic");
        source.setPassword("123456");
        source.setLoginTimeout(10);

        try {
            return source.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
