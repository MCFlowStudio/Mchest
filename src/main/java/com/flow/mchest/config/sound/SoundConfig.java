package com.flow.mchest.config.sound;

public class SoundConfig {
    SoundSetting openSound, closeSound, noPermSound;

    public SoundConfig(SoundSetting openSound, SoundSetting closeSound, SoundSetting noPermSound) {
        this.openSound = openSound;
        this.closeSound = closeSound;
        this.noPermSound = noPermSound;
    }

    public SoundSetting getOpenSound() {
        return openSound;
    }

    public SoundSetting getCloseSound() {
        return closeSound;
    }

    public SoundSetting getNoPermSound() {
        return noPermSound;
    }
}
