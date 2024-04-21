package com.flow.mchest.database;

import com.flow.mchest.inventory.ChestInventory;
import com.flow.mchest.object.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CachedDataService {

    private static HashMap<UUID, ChestInventory> chestInventoryList = new HashMap<>();
    private static HashMap<UUID, Chest> chestList = new HashMap<>();

    public static HashMap<UUID, ChestInventory> getChestInventoryList() {
        return chestInventoryList;
    }

    public static HashMap<UUID, Chest> getChestList() {
        return chestList;
    }

    public static Optional<ChestInventory> findChestInventory(UUID uuid) {
        return Optional.ofNullable(getChestInventoryList().get(uuid));
    }

    public static Optional<Chest> findChest(UUID uuid) {
        return Optional.ofNullable(getChestList().get(uuid));
    }

    public static List<UUID> findInventoryViewer(UUID uuid) {
        List<UUID> inv = new ArrayList<>();
        for (ChestInventory chestInventory : getChestInventoryList().values()) {
            if (chestInventory.getUuid().equals(uuid))
                inv.add(chestInventory.getViewerUuid());
        }
        return inv;
    }
}
