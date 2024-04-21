package com.flow.mchest.database;

import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressDataService {

    // ItemStack 배열을 비동기로 압축
    public static CompletableFuture<byte[]> compressData(ItemStack[] items) {
        byte[] compressedData = null;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(gzipOutputStream)) {

            dataOutput.writeInt(items.length);  // 배열 길이를 먼저 기록

            for (ItemStack item : items) {
                if (item != null) {
                    dataOutput.writeObject(item);
                }
            }
            dataOutput.flush();
            gzipOutputStream.finish();  // 필요한 경우 GZIP 스트림을 완료하기 위해 flush와 finish를 사용

            compressedData = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e); // 실패한 경우 예외와 함께 실패한 Future 반환
        }

        // Return the compressed data in a completed future if the operation is successful
        return CompletableFuture.completedFuture(compressedData);
    }

    // 비동기로 압축 해제 후 ItemStack 배열로 반환
    public static ItemStack[] uncompressData(byte[] data) {
        ItemStack[] items = null;

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
             GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(gzipInputStream)) {

            int length = dataInput.readInt();  // 배열 길이를 먼저 읽음
            items = new ItemStack[length];
            for (int i = 0; i < length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null; // 실패한 경우 null 반환
        }

        return items;
    }

}
