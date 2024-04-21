package com.flow.mchest.command;

import com.flow.mchest.Mchest;
import com.flow.mchest.config.BukkitConfig;
import com.flow.mchest.config.sound.SoundConfig;
import com.flow.mchest.config.sound.SoundSetting;
import com.flow.mchest.database.CachedDataService;
import com.flow.mchest.database.StorageDataService;
import com.flow.mchest.inventory.ChestInventory;
import com.flow.mchest.object.Chest;
import com.flow.mchest.util.MessageUtil;
import com.flow.mchest.util.PermissionUtil;
import com.flow.mchest.util.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class ChestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Not a player!");
            return true;
        }

        if (args.length < 1 || args[0].isEmpty() || !isNumeric(args[0])) {
            player.sendMessage(MessageUtil.formatMessage("error-not-args"));
            return true;
        }

        Integer num = Integer.parseInt(args[0]);
        if (num > 5) {
            player.sendMessage(MessageUtil.formatMessage("error-not-args"));
            return true;
        }

        UUID targetId;
        if (args.length >= 2) {
            if (!player.hasPermission("mchest.command.manage")) {
                player.sendMessage(MessageUtil.formatMessage("error-not-permission"));
                SoundSetting noPermSound = BukkitConfig.getSoundConfig().getNoPermSound();
                SoundUtil.playSound(player, noPermSound.getSound(), noPermSound.getVolume(), noPermSound.getPitch());
                return true;
            }
            targetId = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
            if (!CachedDataService.findChest(targetId).isPresent() || StorageDataService.loadDataSync(targetId) == null) {
                player.sendMessage(MessageUtil.formatMessage("error-not-target", args[1]));
                return true;
            }
        } else {
            if (!PermissionUtil.permissionCheck(player, num)) {
                player.sendMessage(MessageUtil.formatMessage("error-not-permission"));
                SoundSetting noPermSound = BukkitConfig.getSoundConfig().getNoPermSound();
                SoundUtil.playSound(player, noPermSound.getSound(), noPermSound.getVolume(), noPermSound.getPitch());
                return true;
            }
            if (CachedDataService.getChestList().get(player.getUniqueId()) == null)
                CachedDataService.getChestList().put(player.getUniqueId(), new Chest(player.getUniqueId(), new HashMap<>()));
            targetId = player.getUniqueId();
        }

        SoundSetting openSound = BukkitConfig.getSoundConfig().getOpenSound();
        SoundUtil.playSound(player, openSound.getSound(), openSound.getVolume(), openSound.getPitch());
        ChestInventory chestInventory = new ChestInventory(targetId, player.getUniqueId(), num);
        chestInventory.openInventory(player);
        return true;
    }
    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}
