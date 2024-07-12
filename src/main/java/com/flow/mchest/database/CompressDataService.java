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

    public static CompletableFuture<byte[]> compressData(ItemStack[] items) {
        byte[] compressedData = null;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(gzipOutputStream)) {

            dataOutput.writeInt(items.length);

            for (ItemStack item : items) {
                if (item != null) {
                    dataOutput.writeObject(item);
                }
            }
            dataOutput.flush();
            gzipOutputStream.finish();

            compressedData = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e); 
        }

        return CompletableFuture.completedFuture(compressedData);
    }

    public static ItemStack[] uncompressData(byte[] data) {
        ItemStack[] items = null;

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
             GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(gzipInputStream)) {

            int length = dataInput.readInt();
            items = new ItemStack[length];
            for (int i = 0; i < length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return items;
    }

}
