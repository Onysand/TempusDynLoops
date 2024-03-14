package org.onysand.mc.tempusdynloops.utils;

import org.onysand.mc.tempusdynloops.TempusDynLoops;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class Database {
    private final String databasePath;
    private final Logger logger;
    private Connection connection;

    public Database(TempusDynLoops plugin) {
        databasePath = plugin.getDataFolder().getAbsolutePath() + File.separator + "database.db";
        logger = plugin.getLogger();
    }

    private void openConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }
    }

    private void closeConnection() {
        try {
            if (connection != null || !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }
    }

    public void initializeDatabase() {
        File databaseFile = new File(databasePath);

        if (!databaseFile.exists()) {
            // Если файл базы данных не существует, создаем новую базу данных и таблицу prisoners
            try {
                openConnection();
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
                closeConnection();
            } catch (SQLException e) {
                logger.severe(e.getMessage());
            }
        }
        // Если файл базы данных существует, ничего не делаем - просто загружаем её при необходимости

    }
}
