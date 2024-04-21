package com.flow.mchest.hook;

import com.flow.mchest.util.ChestUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PlaceholderHook extends PlaceholderExpansion {

    @Override
    public String onRequest(OfflinePlayer player, String params) {

        if (player == null) {
            return "";
        }

        if (params.startsWith("count")) {
            Player onlinePlayer = Bukkit.getPlayer(player.getName());
            if (onlinePlayer == null)
                return null;
            Integer chestNumber = extractChestNumber(params);
            if (chestNumber != null) {
                return String.valueOf(ChestUtil.countItemsInChest(onlinePlayer.getUniqueId(), chestNumber));
            } else {
                return "N/A";
            }
        }
        return null;
    }

    public static Integer extractChestNumber(String params) {
        if (params.startsWith("count")) {
            try {
                String numberStr = params.substring("count_".length());
                return Integer.parseInt(numberStr);
            } catch (NumberFormatException e) {
                // 숫자 형식이 아닐 경우 null을 반환할 수 있습니다.
                return null;
            }
        }
        return null;
    }

    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "mchest";
    }

    @Override
    public @NotNull String getAuthor() {
        return "minhyeok";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

}
