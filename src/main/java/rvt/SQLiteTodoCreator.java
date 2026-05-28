package rvt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteTodoCreator {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:todo.db";
        String sql = "CREATE TABLE IF NOT EXISTS todo " +
                "(id INTEGER PRIMARY KEY, task TEXT NOT NULL) STRICT";

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            System.out.println("Table created or already exists.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
