package dev.jort.copilot;

import net.runelite.api.SoundEffectVolume;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("copilot")
public interface CopilotConfig extends Config {

    String settingsSection = "Settings";
    String activitiesSection = "Activities";

    int VOLUME_MAX = SoundEffectVolume.HIGH;


    @Range(
            max = VOLUME_MAX
    )
    @ConfigItem(
            keyName = "alertVolume",
            name = "Alert volume",
            description = "Configures the volume the alerts.",
            section = settingsSection,
            position = 1
    )
    default int tickVolume() {
        return SoundEffectVolume.LOW;
    }

    @Alpha
    @ConfigItem(
            keyName = "overlayColor",
            name = "Notification color",
            description = "Set the notification overlay color",
            section = settingsSection,
            position = 2
    )
    default Color overlayColor() {
        return new Color(1.0f, 0.0f, 0.0f, 0.1f);
    }

    @ConfigItem(
            keyName = "willowsDraynor",
            name = "Willows @ Draynor bank",
            description = "Chop down and bank willows next to the bank in Draynor Village.",
            section = activitiesSection,
            position = 3

    )
    default boolean willowsDraynor() {
        return false;
    }

    @ConfigItem(
            keyName = "fishingBarbarianVillage",
            name = "Fishing & Cooking @ Barbarians",
            description = "Fish trout and salmon at Barbarian Village, cook it and then drop it.",
            section = activitiesSection,
            position = 4
    )
    default boolean fishingBarbarian() {
        return false;
    }

    @ConfigItem(
            keyName = "yewsWoodcuttingGuild",
            name = "Yews @ Woodcutting Guild",
            description = "Chop down and bank yew trees at the Woodcutting Guild",
            section = activitiesSection,
            position = 4
    )
    default boolean yewsGuild() {
        return false;
    }
}
