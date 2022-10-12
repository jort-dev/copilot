package dev.jort.copilot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Preferences;
import net.runelite.api.SoundEffectID;
import net.runelite.api.SoundEffectVolume;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Slf4j
public class Sound {
    @Inject
    Client client;

    @Inject
    CopilotConfig config;

    public void missedTick() {
        Preferences preferences = client.getPreferences();
        int previousVolume = preferences.getSoundEffectVolume();

        int volume = config.tickVolume();
        preferences.setSoundEffectVolume(volume);
        int soundEffect = SoundEffectID.GE_INCREMENT_PLOP;
        client.playSoundEffect(soundEffect, volume);
        preferences.setSoundEffectVolume(previousVolume);
    }

}
