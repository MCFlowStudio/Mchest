package com.flow.mchest.config;

import com.flow.mchest.Mchest;
import com.flow.mchest.config.sound.SoundConfig;
import com.flow.mchest.config.sound.SoundSetting;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class BukkitConfig {
    private static Map<Integer, ChestConfig> chests = new HashMap<>();
    private static DatabaseConfig databaseConfig;
    private static SoundConfig soundConfig;

    public static void loadConfig() {
        Mchest.getInstance().saveDefaultConfig();
        FileConfiguration config = Mchest.getInstance().getConfig();

        // Database configuration
        databaseConfig = new DatabaseConfig(
                config.getString("database.mysql.host"),
                config.getString("database.mysql.port"),
                config.getString("database.mysql.username"),
                config.getString("database.mysql.password"),
                config.getString("database.mysql.database"),
                config.getInt("database.mysql.poolsize")
        );

        // Sound settings
        soundConfig = new SoundConfig(
                new SoundSetting(Sound.valueOf(config.getString("sounds.chest_open_sound.sound")), (float) config.getDouble("sounds.chest_open_sound.volume"), (float) config.getDouble("sounds.chest_open_sound.pitch")),
                new SoundSetting(Sound.valueOf(config.getString("sounds.chest_close_sound.sound")), (float) config.getDouble("sounds.chest_close_sound.volume"), (float) config.getDouble("sounds.chest_close_sound.pitch")),
                new SoundSetting(Sound.valueOf(config.getString("sounds.not_permission_sound.sound")), (float) config.getDouble("sounds.not_permission_sound.volume"), (float) config.getDouble("sounds.not_permission_sound.pitch"))
        );

        // Chest configurations
        config.getConfigurationSection("chests").getKeys(false).forEach(key -> {
            int id = Integer.parseInt(key);
            chests.put(id, new ChestConfig(
                    config.getString("chests." + key + ".inventory-title"),
                    config.getInt("chests." + key + ".inventory-size")
            ));
        });
    }

    public static DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public static SoundConfig getSoundConfig() {
        return soundConfig;
    }

    public static Map<Integer, ChestConfig> getChests() {
        return chests;
    }
}
