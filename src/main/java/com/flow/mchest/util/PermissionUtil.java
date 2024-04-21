package com.flow.mchest.util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PermissionUtil {

    public static boolean permissionCheck(Player player, int num) {
        return player.hasPermission("virtualchest." + num);
    }

}
