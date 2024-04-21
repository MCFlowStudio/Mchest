package com.flow.mchest.util;

import com.flow.mchest.object.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.flow.mchest.database.CachedDataService.findChest;

public class ChestUtil {

    public static int countItemsInChest(UUID uuid, int num) {
        Optional<Chest> optionalChest = findChest(uuid);
        if (optionalChest.isPresent()) {
            Chest chest = optionalChest.get();
            Map<Integer, ItemStack[]> contents = chest.getContents();
            ItemStack[] items = contents.get(num);
            if (items != null) {
                return countItems(items);
            }
        }
        return 0;  // 창고가 존재하지 않거나 지정된 번호의 창고가 비어 있을 경우 0 반환
    }
    private static int countItems(ItemStack[] items) {
        int count = 0;
        for (ItemStack item : items) {
            if (item != null) {
                count += item.getAmount();
            }
        }
        return count;
    }

}
