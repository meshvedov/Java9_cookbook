package ch6_db;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class Chapter6Database2 {
    public static void main(String[] args) {
        demo12_blob_bytea();
    }

    private static void demo12_blob_bytea() {
        execute("drop table if exists images");
        execute("create table images (id integer, image bytea)");
        System.out.println("demo12================");
        traverseRS("select * from images");
        System.out.println();
        try(Connection conn = getDbConnection1()) {
            conn.setAutoCommit(false);
            String sql = "insert into images (id, image) values (?,?)";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.setInt(1, 100);
                File file = new File("src/main/java/ch6_db/image1.png");
                FileInputStream fis = new FileInputStream(file);
                Blob blob = conn.createBlob();//dont implemented for postgres (only mySql and oracle)
                OutputStream out = blob.setBinaryStream(1);
                int i = -1;
                while ((i = fis.read()) != -1) {
                    out.write(i);
                }
                st.setBlob(2, blob);
                int count = st.executeUpdate();
                System.out.println("Update count = " + count);
                conn.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();
        traverseRS("select * from images");
    }

    private static void traverseRS(String sql){
        System.out.println("traverseRS(" + sql + "):");
        try (Connection conn = getDbConnection1()) {
            try (Statement st = conn.createStatement()) {
                try(ResultSet rs = st.executeQuery(sql)){
                    traverseRS(rs);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void traverseRS(ResultSet rs) throws Exception{
        int cCount = 0;
        Map<Integer, String> cName = new HashMap<>();
        while (rs.next()) {
            if (cCount == 0) {
                ResultSetMetaData rsmd = rs.getMetaData();
                cCount = rsmd.getColumnCount();
                for (int i = 1; i <= cCount; i++) {
                    cName.put(i, rsmd.getColumnLabel(i));
                }
            }
            List<String> l = new ArrayList<>();
            for (int i = 1; i <= cCount; i++) {
                l.add(cName.get(i) + " = " + rs.getString(i));
            }
            System.out.println(l.stream().collect(Collectors.joining(", ")));
        }
    }

    private static void execute(String sql) {
        try(Connection conn = getDbConnection1()) {
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void executeStatement(String sql) {
        try(Connection conn = getDbConnection1()) {
            try (Statement st = conn.createStatement()){
                st.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
}
