package com.flow.mchest.config.sound;

import org.bukkit.Sound;

public class SoundSetting {
    Sound sound;
    float volume, pitch;

    public SoundSetting(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public Sound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }
}
