package dev.jort.copilot.helpers;

import dev.jort.copilot.CopilotConfig;
import dev.jort.copilot.overlays.EntityOverlay;
import dev.jort.copilot.overlays.NotificationOverlay;
import dev.jort.copilot.overlays.WidgetOverlay;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Preferences;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Slf4j
public class Alert {
    @Inject
    Client client;

    @Inject
    CopilotConfig config;

    @Inject
    NotificationOverlay notificationOverlay;

    @Inject
    WidgetOverlay widgetOverlay;

    @Inject
    EntityOverlay entityOverlay;

    int soundAlertsPlayed = 0;
    boolean previousAlertNeeded = false;
    long alertStartTime = -1;


    public void handleAlert(boolean alertNeeded) {
        //if this is the first time alerting, needed for the alert delay
        if (!previousAlertNeeded) {
            alertStartTime = System.currentTimeMillis();
        }
        previousAlertNeeded = alertNeeded;

        //always disable visual overlay when no alert is needed
        if (!alertNeeded) {
            notificationOverlay.disable();
            soundAlertsPlayed = 0;
            alertStartTime = -2;
            return;
        }

        //ignore if we are awaiting the alert delay
        if (System.currentTimeMillis() - alertStartTime < config.alertDelayMs()) {
            return;
        }

        //enable visual alert
        notificationOverlay.enable();

        //ignore if enough sound alerts are played
        if (soundAlertsPlayed >= config.amountOfSoundAlerts()) {
            return;
        }

        //play sound alert
        playAlertSound();
        soundAlertsPlayed++;
    }

    public void playAlertSound() {
        playSound(config.soundId());
    }

    public void playSound(int id){
        Preferences preferences = client.getPreferences();
        int previousVolume = preferences.getSoundEffectVolume();
        int volume = config.alertVolume();
        preferences.setSoundEffectVolume(volume);
        client.playSoundEffect(id, volume);
        preferences.setSoundEffectVolume(previousVolume);
    }
}
