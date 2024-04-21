package com.flow.mchest;

import com.flow.mchest.command.ChestCommand;
import com.flow.mchest.config.BukkitConfig;
import com.flow.mchest.config.DatabaseConfig;
import com.flow.mchest.database.CachedDataService;
import com.flow.mchest.database.StorageDataService;
import com.flow.mchest.database.mysql.DBConnection;
import com.flow.mchest.hook.PlaceholderHook;
import com.flow.mchest.inventory.ChestInventory;
import com.flow.mchest.listener.PlayerListener;
import com.flow.mchest.object.Chest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public final class Mchest extends JavaPlugin {

    private static Mchest instance;

    @Override
    public void onEnable() {
        instance = this;
        BukkitConfig.loadConfig();

        DatabaseConfig databaseConfig = BukkitConfig.getDatabaseConfig();
        DBConnection.initialize(databaseConfig.getHost(), databaseConfig.getPort(), databaseConfig.getPassword(), databaseConfig.getUsername(), databaseConfig.getDatabaseName(), databaseConfig.getPoolSize());
        StorageDataService.tableSetup();

        getCommand("chest").setExecutor(new ChestCommand());

        getServer().getPluginManager().registerEvents(new ChestInventory(UUID.randomUUID(), UUID.randomUUID(),  1), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            if ((new PlaceholderHook()).isRegistered())
                (new PlaceholderHook()).unregister();
            (new PlaceholderHook()).register();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            StorageDataService.loadData(player.getUniqueId()).thenAccept(data -> {
                CachedDataService.getChestList().put(player.getUniqueId(), data);
            });
        }
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Optional<Chest> chestOpt = CachedDataService.findChest(player.getUniqueId());
            chestOpt.ifPresent(StorageDataService::saveDataSync);
        }
    }

    public static Mchest getInstance() {
        return instance;
    }

    public static void runSync(Runnable task) {
        new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }.runTask(instance);
    }

    public static void runAsync(Runnable task) {
        new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }.runTaskAsynchronously(getInstance());
    }
}
