package dev.jort.copilot;

import net.runelite.api.SoundEffectVolume;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("copilot")
public interface CopilotConfig extends Config {

    @ConfigSection(
            name = "Settings",
            description = "Set which activity you want assistance with. Only one selected item works.",
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

    @Alpha
    @ConfigItem(
            keyName = "highlightColor",
            name = "Highlight color",
            description = "Set the color of the highlight for items to click.",
            section = settingsSection,
            position = 3
    )
    default Color highlightColor() {
        return new Color(0.0f, 1.0f, 1.0f, 1.0f);
    }

    @ConfigSection(
            name = "Activities",
            description = "Set which activity you want assistance with. Only one selected item works.",
            position = 4
    )
    String activitiesSection = "Activities";

    @ConfigItem(
            keyName = "willowsDraynor",
            name = "Willows @ Draynor bank",
            description = "Chop down and bank willows next to the bank in Draynor Village.",
            section = activitiesSection,
            position = 5

    )
    default boolean willowsDraynor() {
        return false;
    }

    @ConfigItem(
            keyName = "fishingBarbarianVillage",
            name = "Fishing & Cooking @ Barbarians",
            description = "Fish trout and salmon at Barbarian Village, cook it and then drop it.",
            section = activitiesSection,
            position = 6
    )
    default boolean fishingBarbarian() {
        return false;
    }

    @ConfigItem(
            keyName = "yewsWoodcuttingGuild",
            name = "Yews @ Woodcutting Guild",
            description = "Chop down and bank yew trees at the Woodcutting Guild",
            section = activitiesSection,
            position = 7
    )
    default boolean yewsGuild() {
        return false;
    }


    //user unchangeable config
    int highlightOpacity = 20; //0-100
}
