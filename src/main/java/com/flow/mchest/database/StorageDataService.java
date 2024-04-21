package com.flow.mchest.database;

import com.flow.mchest.Mchest;
import com.flow.mchest.database.mysql.DBConnection;
import com.flow.mchest.object.Chest;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static javax.swing.UIManager.put;

public class StorageDataService {


    private static String TABLE_NAME = "chest_data";

    public static void tableSetup() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME  + " ("
                    + "userId VARCHAR(36) NOT NULL, "
                    + "num INT NOT NULL, "
                    + "contents LONGBLOB NOT NULL, "
                    + "PRIMARY KEY (userId, num))";


            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static CompletableFuture<Chest> loadData(UUID uuid) {
        CompletableFuture<HashMap<Integer, ItemStack[]>> futureMap = new CompletableFuture<>();

        Mchest.runAsync(() -> {
            HashMap<Integer, ItemStack[]> contentsMap = new HashMap<>();
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "SELECT num, contents FROM " + TABLE_NAME + " WHERE userId = ?")) {

                pstmt.setString(1, uuid.toString());
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    int num = rs.getInt("num");
                    String encodedData = rs.getString("contents");
                    byte[] data = Base64.getDecoder().decode(encodedData);
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                    BukkitObjectInputStream dataInput = new BukkitObjectInputStream(byteArrayInputStream);

                    int length = dataInput.readInt();
                    ItemStack[] items = new ItemStack[length];
                    for (int i = 0; i < length; i++) {
                        items[i] = (ItemStack) dataInput.readObject();
                    }
                    dataInput.close();
                    contentsMap.put(num, items);
                }
                futureMap.complete(contentsMap);
            } catch (Exception e) {
                futureMap.completeExceptionally(e);
            }
        });

        return futureMap.thenApply(map -> new Chest(uuid, map.isEmpty() ? new HashMap<>() : map));
    }

    public static Chest loadDataSync(UUID uuid) {
        HashMap<Integer, ItemStack[]> contentsMap = new HashMap<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT num, contents FROM " + TABLE_NAME + " WHERE userId = ?")) {

            pstmt.setString(1, uuid.toString());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int num = rs.getInt("num");
                String encodedData = rs.getString("contents");
                byte[] data = Base64.getDecoder().decode(encodedData);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(byteArrayInputStream);

                int length = dataInput.readInt();
                ItemStack[] items = new ItemStack[length];
                for (int i = 0; i < length; i++) {
                    items[i] = (ItemStack) dataInput.readObject();
                }
                dataInput.close();
                contentsMap.put(num, items);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Chest(uuid, new HashMap<>()); // 에러 발생 시 빈 Chest 반환
        }
        return new Chest(uuid, contentsMap.isEmpty() ? new HashMap<>() : contentsMap);
    }

    public static void saveData(Chest chest) {
        UUID uuid = chest.getUuid();
        Map<Integer, ItemStack[]> allContents = chest.getContents();

        Mchest.runAsync(() -> {
            try (Connection conn = DBConnection.getConnection()) {
                for (Map.Entry<Integer, ItemStack[]> entry : allContents.entrySet()) {
                    int num = entry.getKey();
                    ItemStack[] items = entry.getValue();

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(byteArrayOutputStream);
                    dataOutput.writeInt(items.length); // 배열 길이를 먼저 기록
                    for (ItemStack item : items) {
                        dataOutput.writeObject(item);
                    }
                    dataOutput.close();
                    String encodedData = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());

                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO " + TABLE_NAME + " (userId, num, contents) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE contents = ?")) {
                        pstmt.setString(1, uuid.toString());
                        pstmt.setInt(2, num);
                        pstmt.setString(3, encodedData);
                        pstmt.setString(4, encodedData);
                        pstmt.executeUpdate();
                    }
                }
            } catch (SQLException | IOException e) {
                Bukkit.getLogger().severe("Error saving data for UUID " + uuid + ": " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public static void saveDataSync(Chest chest) {
        UUID uuid = chest.getUuid();
        Map<Integer, ItemStack[]> allContents = chest.getContents();

        try (Connection conn = DBConnection.getConnection()) {
            for (Map.Entry<Integer, ItemStack[]> entry : allContents.entrySet()) {
                int num = entry.getKey();
                ItemStack[] items = entry.getValue();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(byteArrayOutputStream);
                dataOutput.writeInt(items.length); // 배열 길이를 먼저 기록
                for (ItemStack item : items) {
                    dataOutput.writeObject(item);
                }
                dataOutput.close();
                String encodedData = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());

                try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO " + TABLE_NAME + " (userId, num, contents) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE contents = ?")) {
                    pstmt.setString(1, uuid.toString());
                    pstmt.setInt(2, num);
                    pstmt.setString(3, encodedData);
                    pstmt.setString(4, encodedData);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException | IOException e) {
            Bukkit.getLogger().severe("Error saving data for UUID " + uuid + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

}
