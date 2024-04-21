package com.flow.mchest.inventory;

import com.flow.mchest.Mchest;
import com.flow.mchest.config.BukkitConfig;
import com.flow.mchest.config.ChestConfig;
import com.flow.mchest.config.sound.SoundConfig;
import com.flow.mchest.config.sound.SoundSetting;
import com.flow.mchest.database.CachedDataService;
import com.flow.mchest.database.StorageDataService;
import com.flow.mchest.object.Chest;
import com.flow.mchest.util.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ChestInventory implements InventoryHolder, Listener {

    private UUID uuid;
    private UUID viewerUuid;
    private Integer num;
    private ChestConfig chestConfig;
    private Inventory inv;

    public ChestInventory(UUID uuid, UUID viewerUuid, int num) {
        this.uuid = uuid;
        this.viewerUuid = viewerUuid;
        CachedDataService.getChestInventoryList().put(this.viewerUuid, this);
        this.num = num;
        this.chestConfig = BukkitConfig.getChests().get(num);
        this.inv = Bukkit.createInventory(this, chestConfig.getSize(), chestConfig.getTitle());
        initializeItems();
    }

    public void initializeItems() {
        if (CachedDataService.getChestList().get(this.uuid) != null) {
            Chest chest = CachedDataService.getChestList().get(this.uuid);
            ItemStack[] contents = chest.getContents().get(this.num);
            if (contents == null)
                return;
            for (int i = 0; i < contents.length; i++) {
                if (contents[i] != null) {
                    this.inv.setItem(i, contents[i].clone());
                }
            }
        } else {
            StorageDataService.loadData(this.uuid).thenAccept(chest -> {
                ItemStack[] contents = chest.getContents().get(this.num);
                if (contents == null)
                    return;
                for (int i = 0; i < contents.length; i++) {
                    if (contents[i] != null) {
                        int finalI = i;
                        Mchest.runSync(() -> this.inv.setItem(finalI, contents[finalI].clone()));
                    }
                }
            });
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (!(inv.getHolder() instanceof ChestInventory))
            return;
        ItemStack[] contents = inv.getContents();
        Player player = (Player) event.getPlayer();

        ChestInventory chestInventory = CachedDataService.getChestInventoryList().get(player.getUniqueId());
        if (chestInventory == null) {
            Mchest.getInstance().getLogger().severe("데이터를 저장하는 도중 오류가 발생했습니다. 오류 코드 1:" + player.getUniqueId());
            return;
        }

        Chest chest = CachedDataService.getChestList().get(chestInventory.uuid);
        chest.getContents().put(chestInventory.num, contents);
        SoundSetting closeSound = BukkitConfig.getSoundConfig().getCloseSound();
        SoundUtil.playSound(player, closeSound.getSound(), closeSound.getVolume(), closeSound.getPitch());

        CachedDataService.getChestInventoryList().remove(player.getUniqueId());

        List<UUID> viewers = CachedDataService.findInventoryViewer(chestInventory.uuid);
        for (UUID viewerId : viewers) {
            if (Bukkit.getPlayer(viewerId) != null) {
                Player viewer = Bukkit.getPlayer(viewerId);
                Inventory viewerInv = viewer.getOpenInventory().getTopInventory();
                if (viewerInv.getHolder() instanceof ChestInventory)
                    viewerInv.setContents(contents);
            }

        }
    }

    public Inventory getInventory() {
        return this.inv;
    }

    public void openInventory(HumanEntity ent) {
        ent.openInventory(this.inv);
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getViewerUuid() {
        return viewerUuid;
    }

    public Integer getNum() {
        return num;
    }
}
