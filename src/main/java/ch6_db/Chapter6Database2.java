package ch6_db;

import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class Chapter6Database2 {
    public static void main(String[] args) {
//        demo12_blob_bytea();
        demo13_blob_bytea_stream();
        demo13_blob_bytea_bytes();
        demo14_blob_oid();
        demo15_clob_text();
        demo16_stored();
    }

    private static void demo16_stored() {
        execute("drop function if exists selectText(int)");
        execute("drop table if exists texts");
        System.out.println("demo16=====================");
        try (Connection conn = getDbConnection1()) {
            //replace(string text, from text, to text)
            String sql = "{ ? = call replace(?, ?, ?) }";
            try (CallableStatement st = conn.prepareCall(sql)) {
                st.registerOutParameter(1, Types.VARCHAR);
                st.setString(2, "Hello, World! Hello!");
                st.setString(3, "llo");
                st.setString(4, "y");
                st.execute();
                String res = st.getString(1);
                System.out.println(res);
            }
            System.out.println();

            execute("create or replace function createTableTexts() returns void as $$ " +
                    "drop table if exists texts; " +
                    "create table texts (id integer, text text); " +
                    "$$ language sql");
            sql = "{ call createTableTexts()}";
            try (CallableStatement st = conn.prepareCall(sql)) {
                st.execute();
            }
            traverseRS("select createTableTexts()");
            traverseRS("select * from createTableTexts()");
            execute("drop function if exists createTableTexts()");
            System.out.println();

            execute("create or replace function insertText(int, varchar) returns void as $$ " +
                    "insert into texts(id,text) values($1, replace($2, 'XX', 'ext')); " +
                    "$$ language sql");
            sql = "{ call insertText(?, ?)}";
            try (CallableStatement st = conn.prepareCall(sql)) {
                st.setInt(1,1);
                st.setString(2, "TXX 1");
                st.execute();
            }
            execute("select insertText(2, 'TXX 2')");
            traverseRS("select * from texts");
            execute("drop function if exists insertText()");
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void demo15_clob_text() {
        execute("drop table if exists texts");
        execute("create table texts (id integer, text text)");
        System.out.println("demo15====================================");
        traverseRS("select * from texts");
        System.out.println();
        try (Connection conn = getDbConnection1()) {
            String sql = "insert into texts (id, text) values (?,?)";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.setInt(1, 100);
                File file = new File("src/main/java/ch6_db/Chapter6Database2.java");
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] bytes = fis.readAllBytes();
                    st.setString(2, new String(bytes, Charset.forName("UTF-8")));

//                    try (Reader reader = new FileReader(file)) {
//                        st.setCharacterStream(2, reader, (int)file.length());
//                    }
                    int count = st.executeUpdate();
                    System.out.println("Update count = " + count);
                }
            }

            sql = "select text from texts where id = ?";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.setInt(1, 100);
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
//                        String str = rs.getString(1);
//                        System.out.println(str);

                        try (Reader reader = rs.getCharacterStream(1)) {
                            char[] chars = new char[160];
                            reader.read(chars);
                            System.out.println(chars);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();
        execute("delete from texts");
    }

    private static void demo14_blob_oid() {
        execute("create table lobs (id integer, lob oid)");
        System.out.println("demo14======================");
        System.out.println();
        try (Connection conn = getDbConnection1()) {
            conn.setAutoCommit(false);
            LargeObjectManager lobm = conn.unwrap(org.postgresql.PGConnection.class).getLargeObjectAPI();
            long lob = lobm.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);
            LargeObject obj = lobm.open(lob, LargeObjectManager.WRITE);
            File file = new File("src/main/java/ch6_db/image1.png");
            try (FileInputStream fis = new FileInputStream(file)) {
                int size = 2048;
                byte[] bytes = new byte[size];
                int len = 0;
                while ((len = fis.read(bytes, 0, size)) > 0) {
                    obj.write(bytes, 0, len);
                }
                obj.close();

                String sql = "insert into lobs (id, lob) values (?,?)";
                try (PreparedStatement st = conn.prepareStatement(sql)) {
                    st.setInt(1, 100);
                    st.setLong(2, lob);
                    st.executeUpdate();
                }
            }
            conn.commit();

            String sql = "select lob from lobs where id = ?";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.setInt(1, 100);
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        Blob blob = rs.getBlob(1);
                        byte[] bytes = blob.getBytes(1, (int) blob.length());
                        System.out.println("bytes = " + bytes);

                        lob = rs.getLong(1);
                        obj = lobm.open(lob, LargeObjectManager.READ);
                        byte[] bytes1 = new byte[obj.size()];
                        obj.read(bytes1, 0, obj.size());
                        System.out.println("bytes = " + bytes1);
                        obj.close();
                    }
                }
            }
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();
        traverseRS("select * from lobs");
        System.out.println();
        execute("select lo_unlink((select lob from lobs where id = 100))");
        execute("drop table if exists lobs");
    }

    private static void demo13_blob_bytea_bytes() {
        execute("drop table if exists images");
        execute("create table images (id integer, image bytea)");
        System.out.println("demo13==================");
        traverseRS("select * from images");
        System.out.println();
        try (Connection conn = getDbConnection1()) {
            String sql = "insert into images (id, image) values (?,?)";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.setInt(1, 100);
                File file = new File("src/main/java/ch6_db/image1.png");
                FileInputStream fis = new FileInputStream(file);
                byte[] bytes = fis.readAllBytes();
                System.out.println("bytes = " + bytes);
                st.setBytes(2, bytes);
                int count = st.executeUpdate();
                System.out.println("Update count = " + count);
            }
            sql = "select image from images where id = ?";
            System.out.println();
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.setInt(1, 100);
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        byte[] bytes = rs.getBytes(1);
                        System.out.println("bytes = " + bytes);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();
        traverseRS("select * from images");
    }

    private static void demo13_blob_bytea_stream() {
        execute("drop table if exists images");
        execute("create table images (id integer, image bytea)");
        System.out.println("demo13==================");
        traverseRS("select * from images");
        System.out.println();
        try (Connection conn = getDbConnection1()) {
            String sql = "insert into images (id, image) values (?,?)";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.setInt(1, 100);
                File file = new File("src/main/java/ch6_db/image1.png");
                FileInputStream fis = new FileInputStream(file);
                st.setBinaryStream(2, fis);
                int count = st.executeUpdate();
                System.out.println("Update count = " + count);
            }
            sql = "select image from images where id = ?";
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.setInt(1, 100);
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        try (InputStream is = rs.getBinaryStream(1)) {
                            int i, count = 0;
                            System.out.print("ints = ");
                            while ((i = is.read()) != -1) {
                                System.out.print(i);
                                count++;
                            }
                            System.out.println();
                            System.out.println("count of bytes = " + count);
                        }
                    }
                }
            }
            System.out.println();
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.setInt(1, 100);
                try (ResultSet rs = st.executeQuery()) {
                    int count = 0;
                    while (rs.next()) {
                        byte[] bytes = rs.getBytes(1);
                        System.out.println("bytes = " + bytes);
                        count++;
                    }
                    System.out.println("count of bytes = " + count);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();
        traverseRS("select * from images");
    }

    private static void demo12_blob_bytea() {
        execute("drop table if exists images");
        execute("create table images (id integer, image bytea)");
        System.out.println("demo12================");
        traverseRS("select * from images");
        System.out.println();
        try (Connection conn = getDbConnection1()) {
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

    private static void traverseRS(String sql) {
        System.out.println("traverseRS(" + sql + "):");
        try (Connection conn = getDbConnection1()) {
            try (Statement st = conn.createStatement()) {
                try (ResultSet rs = st.executeQuery(sql)) {
                    traverseRS(rs);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void traverseRS(ResultSet rs) throws Exception {
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
        try (Connection conn = getDbConnection1()) {
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void executeStatement(String sql) {
        try (Connection conn = getDbConnection1()) {
            try (Statement st = conn.createStatement()) {
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
