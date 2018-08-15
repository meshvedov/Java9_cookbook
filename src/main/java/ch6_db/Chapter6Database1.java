package ch6_db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.postgresql.ds.PGPoolingDataSource;
import org.postgresql.ds.PGSimpleDataSource;

public class Chapter6Database1 {

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
