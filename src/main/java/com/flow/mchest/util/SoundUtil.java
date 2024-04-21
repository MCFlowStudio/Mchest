package com.flow.mchest.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {

    /**
     * 플레이어에게 특정 소리를 재생합니다.
     *
     * @param player 재생할 플레이어
     * @param sound 재생할 소리
     * @param volume 소리의 볼륨 (1.0이 표준)
     * @param pitch 소리의 피치 (1.0이 표준)
     */
    public static void playSound(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

}
