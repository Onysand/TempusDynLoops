package org.onysand.mc.tempusdynloops.utils;

import org.onysand.mc.tempusdynloops.TempusDynLoops;

import java.io.File;
import java.nio.ByteBuffer;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Logger;

public class Database {
    private final String databasePath;
    private final Logger logger;
    private Connection connection;

    public Database(TempusDynLoops plugin) {
        databasePath = plugin.getDataFolder().getAbsolutePath() + File.separator + "markers.db";
        logger = plugin.getLogger();
        initializeDatabase();
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
            if (connection != null && !connection.isClosed()) {
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
                //В ПЕРВОЙ ТАБЛИЦЕ ДОБАВИТЬ АЙДИ, ЧТОБЫ УДАЛИТЬ ВТОРУЮ ТАБЛИЦУ
                //ДОБАВИТЬ isLoop boolean ЧТОБЫ РАЗДЕЛЯТЬ, ЧТО ЕСТЬ ЛУПА, А ЧТО МАРКЕР

                //id (он же markerId), location, playerUUID, isLoop
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS signs (\n" + " location TEXT PRIMARY KEY,\n" + " markerId INTEGER,\n" // Изменено на INTEGER для markerId
                        + " playerUUID BLOB\n"  // Изменено на BLOB для хранения UUID в бинарном формате
                        + ");\n"

                        + "CREATE TABLE IF NOT EXISTS countId (\n" + " id INTEGER\n"  // Уточнение: здесь предполагается, что это будет уникальный инкрементируемый идентификатор, возможно, стоит использовать AUTOINCREMENT
                        + ");");
            } catch (SQLException e) {
                logger.severe(e.getMessage());
            } finally {
                closeConnection();
            }
        }
        // Если файл базы данных существует, ничего не делаем - просто загружаем её при необходимости

    }


    public void addSignMarker(String stringLoc, int markerId, UUID playerUUID) {
        openConnection();
        try {
            // SQL запрос с учетом новой структуры таблицы
            String sql = "INSERT INTO signs (location, markerId, playerUUID) VALUES (?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);

            // Установка значений параметров
            pstmt.setString(1, stringLoc); // location остается строкой
            pstmt.setInt(2, markerId); // markerId теперь целочисленное значение
            // Преобразование UUID в бинарный формат для playerUUID
            pstmt.setBytes(3, asBytes(playerUUID));

            // Выполнение запроса
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        } finally {
            closeConnection();
        }
    }


    //Метод ниже автоматически получает самый максимальный Id и заносит его в базу
    public int getMarkerId() {
        openConnection();
        int newId = 1;
        try {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();

            var rs = statement.executeQuery("SELECT MAX(id) FROM countid");
            if (rs.next()) {
                newId = rs.getInt(1) + 1; // Увеличиваем ID на 1
            }
            statement.executeUpdate("INSERT INTO countid (id) VALUES (" + newId + ")");
            connection.commit();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                logger.severe(ex.getMessage());
            }
            logger.severe(e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.severe(e.getMessage());
            }
            closeConnection();
        }
        return newId;
    }

    // Вспомогательный метод для преобразования UUID в массив байтов
    private byte[] asBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
