package org.onysand.mc.tempusdynloops.utils;

import org.onysand.mc.tempusdynloops.TempusDynLoops;

import java.io.File;
import java.sql.*;
import java.util.logging.Logger;


public class DatabaseManager {
    private final String databasePath;
    private final Logger logger = TempusDynLoops.getPlugin().getLogger();

    public DatabaseManager(String databasePath) {
        this.databasePath = databasePath;
    }

    // Метод для инициализации базы данных при старте сервера
    public void initializeDatabase() {
        File databaseFile = new File(databasePath);

        if (!databaseFile.exists()) {
            // Если файл базы данных не существует, создаем новую базу данных и таблицу prisoners
            try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath)) {
                Statement statement = connection.createStatement();
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS signowners (\n"
                        + " key TEXT PRIMARY KEY,\n"
                        + " value TEXT\n"
                        + ");\n"

                        + "CREATE TABLE IF NOT EXISTS signlines (\n"
                        + " key TEXT PRIMARY KEY,\n"
                        + " value TEXT\n"
                        + ");\n"

                        + "CREATE TABLE IF NOT EXISTS signmarkers (\n"
                        + " key TEXT PRIMARY KEY,\n"
                        + " value TEXT\n"
                        + ");\n"

                        + "CREATE TABLE IF NOT EXISTS countid (\n"
                        + " id INTEGER \n"
                        + ");");
            } catch (SQLException e) {
                logger.severe(e.getMessage());
            }
        }
        // Если файл базы данных существует, ничего не делаем - просто загружаем её при необходимости

    }

    // Метод для получения соединения с базой данных
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + databasePath);
    }

    public static void add(String locationString, String markerID) {
        try (Connection connection = TempusDynLoops.databaseManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT OR REPLACE INTO signmarkers(key, value) VALUES(?, ?)");
            statement.setString(1, locationString);
            statement.setString(2, markerID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getValueByKey(String tableName, String key) {
        String sql = "SELECT value FROM " + tableName + " WHERE key = ?";
        String value = null;

        try (Connection connection = TempusDynLoops.databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, key);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                value = rs.getString("value");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при получении значения: " + e.getMessage());
        }

        return value;
    }

    // Метод для удаления данных из таблицы
    public static void deleteFromTable(String tableName, String key) {
        String sql = "DELETE FROM " + tableName + " WHERE key = ?";

        try (Connection connection = TempusDynLoops.databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, key);
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Ошибка при удалении данных: " + e.getMessage());
        }
    }

    public static int getCurrentID() {
        String selectSql = "SELECT id FROM countid";
        int value = 0;

        try (Connection connection = TempusDynLoops.databaseManager.getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
            ResultSet rs = selectStatement.executeQuery();

            if (rs.next()) {
                value = rs.getInt("id");
                System.out.println("Current value is " + value);
            } else {
                // Вставляем значение по умолчанию, если таблица пуста
                try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO countid (id) VALUES (?)")) {
                    insertStatement.setInt(1, 0);
                    insertStatement.executeUpdate();
                    System.out.println("Default value (0) inserted into countid table");
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при получении значения: " + e.getMessage());
        }

        return value;
    }

    public static void addOneToID() {
        int currentValue = getCurrentID();
        int newValue = currentValue + 1;
        System.out.println("New value is " + newValue);

        try (Connection connection = TempusDynLoops.databaseManager.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement("UPDATE countid SET id = ?")) {
            updateStatement.setInt(1, newValue);
            updateStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Ошибка при обновлении значения: " + e.getMessage());
        }
    }
}