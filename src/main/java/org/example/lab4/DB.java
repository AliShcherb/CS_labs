package org.example.lab4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    static Connection connection;

    public static void connect() {

        try {
            String url = "jdbc:sqlite:" + Main.dbName;
            connection = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.\n");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void close() {
        try{
            connection.close();
            System.out.println("Connection to SQLite has been established.\n");

        } catch (SQLException throwables) {
            System.out.println(throwables.getMessage());
        }
    }
}
