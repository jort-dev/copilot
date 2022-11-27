package dev.jort.copilot.helpers;

import dev.jort.copilot.CopilotConfig;
import dev.jort.copilot.overlays.EntityOverlay;
import dev.jort.copilot.overlays.NotificationOverlay;
import dev.jort.copilot.overlays.WidgetOverlay;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Preferences;
import net.runelite.client.Notifier;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import java.awt.event.MouseEvent;

@Singleton
@Slf4j
public class Alert {
    @Inject
    Client client;

    @Inject
    Inventory inventory;

    @Inject
    CopilotConfig config;

    @Inject
    NotificationOverlay notificationOverlay;

    @Inject
    WidgetOverlay widgetOverlay;

    @Inject
    EntityOverlay entityOverlay;

    @Inject
    private Notifier notifier;

    @Inject
    private MouseManager mouseManager;

    int soundAlertsPlayed = 0;
    boolean previousInteractionNeeded = false;
    boolean hasFiredSystemAlert = false;
    long alertStartTime = -1;

    @Inject
    public Alert(MouseManager mouseManager) {
        this.mouseManager = mouseManager;

        final MouseListener mouseListener = new MouseAdapter() {
            @Override
            public MouseEvent mousePressed(MouseEvent mouseEvent) {
                if (SwingUtilities.isLeftMouseButton(mouseEvent) && config.disableInput()) {
                    mouseEvent.consume();
                    log.info("Mouse event consumed!");
                }
                return mouseEvent;
            }
        };
        mouseManager.registerMouseListener(mouseListener);
    }


    public void handleAlert(boolean interactionNeeded) {
        //if this is the first time alerting, needed for the alert delay
        if (!previousInteractionNeeded) {
            alertStartTime = System.currentTimeMillis();
        }
        previousInteractionNeeded = interactionNeeded;


        //always disable visual overlay when no alert is needed
        if (!interactionNeeded) {
            notificationOverlay.clear();
            entityOverlay.clear();
            widgetOverlay.clear();
            soundAlertsPlayed = 0;
            alertStartTime = -2;
            hasFiredSystemAlert = false;
            return;
        }

        //ignore if interacted recently or if awaiting the alert delay
        if (Math.max(client.getMouseLastPressedMillis(), alertStartTime) + config.alertDelayMs() > System.currentTimeMillis()) {
            return;
        }

        //enable visual alert
        notificationOverlay.toggleOn();

        //system alert

        if (!hasFiredSystemAlert) {
            systemAlert("Interaction needed.");
            hasFiredSystemAlert = true;
        }

        //ignore if enough sound alerts are played
        if (soundAlertsPlayed >= config.amountOfSoundAlerts()) {
            return;
        }

        //play sound alert
        if (inventory.isFull()) {
            playAlternativeAlertSound();
        }
        else {
            playAlertSound();
        }
        soundAlertsPlayed++;
    }

    public void playAlertSound() {
        playSound(config.mainSoundId());
    }

    public void systemAlert(String text) {
        if (!config.useSystemNotifications()) {
            return;
        }
        notifier.notify("Copilot: " + text);
    }

    public void playAlternativeAlertSound() {
        if (config.alternativeSoundId() == 0) { //set to 0 disables it
            playAlertSound();
            return;
        }
        playSound(config.alternativeSoundId());
    }

    public void playImportantAlert() {
        playSound(config.importantAlertSoundId());
    }

    public void playSound(int id) {
        Preferences preferences = client.getPreferences();
        int previousVolume = preferences.getSoundEffectVolume();
        int volume = config.alertVolume();
        preferences.setSoundEffectVolume(volume);
        client.playSoundEffect(id, volume);
        preferences.setSoundEffectVolume(previousVolume);
    }
}
