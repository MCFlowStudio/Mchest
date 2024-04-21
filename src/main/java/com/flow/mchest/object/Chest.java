package com.flow.mchest.object;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class Chest {

    private UUID uuid;
    private HashMap<Integer, ItemStack[]> contents = new HashMap<>();

    public Chest(UUID uuid, HashMap<Integer, ItemStack[]> contents) {
        this.uuid = uuid;
        this.contents = contents;
    }

    public UUID getUuid() {
        return uuid;
    }

    public HashMap<Integer, ItemStack[]> getContents() {
        return contents;
    }

    public void setContents(HashMap<Integer, ItemStack[]> contents) {
        this.contents = contents;
    }
}
