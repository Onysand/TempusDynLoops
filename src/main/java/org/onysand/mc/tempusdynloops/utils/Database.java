package org.onysand.mc.tempusdynloops.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.onysand.mc.tempusdynloops.TempusDynLoops;

import java.io.File;
import java.nio.ByteBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
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
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS signs (\n" +
                        "    markerID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "    location TEXT UNIQUE,\n" +
                        "    playerUUID BLOB,\n" +
                        "    isLoop BOOLEAN\n" +
                        ");");  // Изменено на BLOB для хранения UUID в бинарном формате

            } catch (SQLException e) {
                logger.severe(e.getMessage());
            } finally {
                closeConnection();
            }
        }
        // Если файл базы данных существует, ничего не делаем - просто загружаем её при необходимости

    }

    public Integer getMarkerID(String location) {
        openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM signs WHERE location = ?");
            statement.setString(1, location);
            var rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getInt("markerID");
            }

            statement.close();
            return null;
        } catch (SQLException e) {
            return null;
        } finally {
            closeConnection();
        }
    }

    public Integer getMarkerIDbyOwner(String ownerName) {
        openConnection();
        try {
            Player owner = Bukkit.getPlayer(ownerName);
            if (owner == null) return null;

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM signs WHERE playerUUID = ?");
            statement.setBytes(1, asBytes(owner.getUniqueId()));
            var rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getInt("markerID");
            }

            statement.close();
            return null;
        } catch (SQLException e) {
            return null;
        } finally {
            closeConnection();
        }
    }

    public ArrayList<String> getMarkerIDsByOwner(String ownerName) {
        ArrayList<String> ids = new ArrayList<>();
        openConnection();
        try {
            UUID uuid = Bukkit.getPlayerUniqueId(ownerName);
            if (uuid == null) return ids;

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM signs WHERE playerUUID = ?");
            statement.setBytes(1, asBytes(uuid));
            var rs = statement.executeQuery();

            while (rs.next()) {
                ids.add(String.valueOf(rs.getInt("markerID")));
            }

            statement.close();
        } catch (SQLException e) {
            return null;
        } finally {
            closeConnection();
        }
        return ids;
    }

    public ArrayList<String> getSignOwners(String arg) {
        ArrayList<String> owners = new ArrayList<>();
        openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM signs");
            var rs = statement.executeQuery();

            while (rs.next()) {
                Player player = Bukkit.getPlayer(fromBytes(rs.getBytes("playerUUID")));
                if (player == null) continue;
                if (player.getName().startsWith(arg)) owners.add(player.getName());
            }

            statement.close();
        } catch (SQLException e) {
            return null;
        } finally {
            closeConnection();
        }
        return owners;
    }

    public Location getMarkerLoc(String id) {
        openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM signs WHERE markerID = ?");
            statement.setString(1, id);
            var rs = statement.executeQuery();

            if (rs.next()) {
                HashMap<String, String> loc = LocUtils.getLocationMap(rs.getString("location"));
                World world = Bukkit.getWorld(loc.get("worldName"));
                int x = Integer.parseInt(loc.get("x"));
                int y = Integer.parseInt(loc.get("y"));
                int z = Integer.parseInt(loc.get("z"));

                return new Location(world, x, y, z);
            }
            statement.close();

        } catch (SQLException e) {
            return null;
        } finally {
            closeConnection();
        }
        return null;
    }

    public boolean isSignOwner(String loc, UUID uuid) {
        openConnection();
        String sql = "SELECT * FROM signs WHERE location = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, loc);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    byte[] playerUUIDBytes = rs.getBytes("playerUUID");
                    UUID playerUUID = fromBytes(playerUUIDBytes);
                    return uuid.equals(playerUUID);
                }
                return false;
            }
        } catch (SQLException e) {
            return false;
        } finally {
            closeConnection();
        }
    }

    public boolean isLoop(String loc) {
        openConnection();
        try {
            String sql = "SELECT * FROM signs WHERE location = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, loc);

            var rs = statement.executeQuery();
            boolean isMarkerLoop = false;

            if (rs.next()) {
                isMarkerLoop = rs.getBoolean("isLoop");
                statement.close();
            }

            return isMarkerLoop;
        } catch (SQLException e) {
            return false;
        } finally {
            closeConnection();
        }
    }

    public HashMap<Integer, ArrayList<String>> listMarkers() {
        openConnection();
        String sql = "SELECT * FROM signs";
        HashMap<Integer, ArrayList<String>> dataMap = new HashMap<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    ArrayList<String> values = new ArrayList<>();
                    values.add(rs.getString("location"));
                    values.add(Bukkit.getPlayer(fromBytes(rs.getBytes("playerUUID"))).getName());
                    values.add(String.valueOf(rs.getBoolean("isLoop")));
                    dataMap.put(rs.getInt("markerID"), values);
                }
                return dataMap;
            }
        } catch (SQLException e) {
            return null;
        } finally {
            closeConnection();
        }
    }

    public void addSignMarker(String stringLoc, UUID playerUUID, boolean isLoop) {
        openConnection();
        try {
            // SQL запрос с учетом новой структуры таблицы
            String sql = "INSERT INTO signs (location, playerUUID, isLoop) VALUES (?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);

            // Установка значений параметров
            pstmt.setString(1, stringLoc); // location остается строкой
            // Преобразование UUID в бинарный формат для playerUUID
            pstmt.setBytes(2, asBytes(playerUUID));
            pstmt.setBoolean(3, isLoop);

            // Выполнение запроса
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        } finally {
            closeConnection();
        }
    }

    public boolean removeMarkerByID(int markerID) {
        openConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM signs WHERE markerID = ?");
            statement.setInt(1, markerID);
            int rs = statement.executeUpdate();

            if (rs > 0) {
                return true;
            }

            statement.close();
            return false;
        } catch (SQLException e) {
            return false;
        } finally {
            closeConnection();
        }
    }

    //Метод ниже автоматически получает самый максимальный Id и заносит его в базу

    public int getNextID() {
        openConnection();
        int newId = 1;
        try {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();

            var rs = statement.executeQuery("SELECT MAX(markerID) FROM signs");
            if (rs.next()) {
                newId = rs.getInt(1) + 1; // Увеличиваем ID на 1
            }
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

    private static UUID fromBytes(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long mostSignificant = bb.getLong();
        long leastSignificant = bb.getLong();
        return new UUID(mostSignificant, leastSignificant);
    }
}
