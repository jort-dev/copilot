package dev.jort.copilot;

import net.runelite.api.SoundEffectVolume;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("copilot")
public interface CopilotConfig extends Config {

    @ConfigSection(
            name = "Settings",
            description = "Configure settings which apply to all helpers.",
            position = 0
    )
    String settingsSection = "Settings";

    @Range(
            max = SoundEffectVolume.HIGH
    )
    @ConfigItem(
            keyName = "alertVolume",
            name = "Alert volume",
            description = "Configures the volume the alerts. Off=0, loudest=127",
            section = settingsSection,
            position = 1
    )
    default int alertVolume() {
        return SoundEffectVolume.HIGH;
    }

    @ConfigItem(
            keyName = "mainSoundId",
            name = "Alert sound ID",
            description = "Configures the sound to play for alerts. For inspiration: search 'sound id osrs'.",
            section = settingsSection,
            position = 2
    )
    default int mainSoundId() {
        return 3929;
    }

    @ConfigItem(
            keyName = "alternativeSoundId",
            name = "Alt alert sound ID",
            description = "Configures the sound to play when the inventory is full. Set to 0 to disable.",
            section = settingsSection,
            position = 3
    )
    default int alternativeSoundId() {
        return 1959;
    }

    @ConfigItem(
            keyName = "alertDelay",
            name = "Alert delay",
            description = "Configure after how many milliseconds of inactivity the alert go off.",
            section = settingsSection,
            position = 4
    )
    default int alertDelayMs() {
        return 1000;
    }

    @ConfigItem(
            keyName = "amountOfAlerts",
            name = "Amount of alerts",
            description = "Configure after how many times the alert sound is played.",
            section = settingsSection,
            position = 4
    )
    default int amountOfSoundAlerts() {
        return 5;
    }


    @Alpha
    @ConfigItem(
            keyName = "overlayColor",
            name = "Notification color",
            description = "Set the notification overlay color",
            section = settingsSection,
            position = 6
    )
    default Color overlayColor() {
        return new Color(1.0f, 0.0f, 0.0f, 0.1f);
    }

    @Alpha
    @ConfigItem(
            keyName = "highlightColor",
            name = "Highlight color",
            description = "Set the color of the highlight for items to click.",
            section = settingsSection,
            position = 7
    )
    default Color highlightColor() {
        return new Color(0.0f, 1.0f, 1.0f, 0.2f);
    }

    @ConfigSection(
            name = "Activities",
            description = "Set which activity you want assistance with. Only one selected item works.",
            position = 8
    )
    String activitiesSection = "Activities";

    @ConfigItem(
            keyName = "woodcuttingGeneral",
            name = "Woodcutting",
            description = "Highlights trees, logs and banks.",
            section = activitiesSection,
            position = 9

    )
    default boolean woodcutting() {
        return false;
    }

    @ConfigItem(
            keyName = "fishingBarbarianVillage",
            name = "Fishing and Cooking at Barbarians",
            description = "Fish trout and salmon at Barbarian Village, cook it and then drop it.",
            section = activitiesSection,
            position = 10
    )
    default boolean fishingBarbarian() {
        return false;
    }

    @ConfigItem(
            keyName = "crafting",
            name = "Crafting at bank",
            description = "Withdraw resource from bank, craft into product with a tool, deposit product.",
            section = activitiesSection,
            position = 11
    )
    default boolean crafting() {
        return false;
    }

    @ConfigItem(
            keyName = "inactivityAlert",
            name = "General inactivity alert",
            description = "For unimplemented features you can't find above. Alerts you when you have not animated or moved within the set time, but does not show you what to click.",
            section = activitiesSection,
            position = 12
    )
    default boolean inactivityAlert() {
        return false;
    }

    @ConfigSection(
            name = "Testing features",
            description = "Testing features. Multiple selections possible.",
            position = 13
    )
    String testSection = "Test features";

    @ConfigItem(
            keyName = "bogged",
            name = "Bogged",
            description = "Pump it.",
            section = testSection,
            position = 14

    )
    default boolean bogged() {
        return false;
    }
}
