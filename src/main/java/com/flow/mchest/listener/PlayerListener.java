package com.flow.mchest.listener;

import com.flow.mchest.database.CachedDataService;
import com.flow.mchest.database.StorageDataService;
import com.flow.mchest.object.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        StorageDataService.loadData(player.getUniqueId()).thenAccept(data -> CachedDataService.getChestList().put(player.getUniqueId(), data));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Optional<Chest> chestOpt = CachedDataService.findChest(player.getUniqueId());
        chestOpt.ifPresent(StorageDataService::saveData);
    }

}
