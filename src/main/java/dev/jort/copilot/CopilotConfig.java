package dev.jort.copilot;

import net.runelite.api.SoundEffectVolume;
import net.runelite.client.config.*;

import java.awt.*;

/*
Copilot configuration menu.
The positions are in increments of 5, so you can wiggle something between them with position 6 for example.
 */
@ConfigGroup("copilot")
public interface CopilotConfig extends Config {

    @ConfigSection(
            name = "Settings",
            description = "Configure settings which apply to all helpers.",
            position = 5
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
            position = 10
    )
    default int alertVolume() {
        return SoundEffectVolume.HIGH;
    }

    @ConfigItem(
            keyName = "mainSoundId",
            name = "Alert sound ID",
            description = "Configures the sound to play for alerts. For inspiration: search 'sound id osrs'.",
            section = settingsSection,
            position = 15
    )
    default int mainSoundId() {
        return 3929;
    }

    @ConfigItem(
            keyName = "alternativeSoundId",
            name = "Alt alert sound ID",
            description = "Configures the sound to play when the inventory is full. Set to 0 to disable.",
            section = settingsSection,
            position = 20
    )
    default int alternativeSoundId() {
        return 1959;
    }

    @ConfigItem(
            keyName = "alertDelay",
            name = "Alert delay",
            description = "Configure after how many milliseconds of inactivity the alert go off.",
            section = settingsSection,
            position = 25
    )
    default int alertDelayMs() {
        return 1000;
    }

    @ConfigItem(
            keyName = "amountOfAlerts",
            name = "Amount of alerts",
            description = "Configure after how many times the alert sound is played.",
            section = settingsSection,
            position = 30
    )
    default int amountOfSoundAlerts() {
        return 5;
    }

    @ConfigItem(
            keyName = "specialAttackAlert",
            name = "Special attack alert",
            description = "Alerts when the special attack is charged 100%. Useful for skilling tools made of dragon for example.",
            section = settingsSection,
            position = 33
    )
    default boolean specialAttackAlert() {
        return false;
    }

    @ConfigItem(
            keyName = "useSystemNotification",
            name = "Use system notifications",
            description = "In addition to visual and auditory alerts, also fire system notifications, like most plugins do.",
            section = settingsSection,
            position = 35
    )
    default boolean useSystemNotifications() {
        return false;
    }

    @Alpha
    @ConfigItem(
            keyName = "overlayColor",
            name = "Notification color",
            description = "Set the notification overlay color",
            section = settingsSection,
            position = 40
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
            position = 45
    )
    default Color highlightColor() {
        return new Color(0.0f, 1.0f, 1.0f, 0.2f);
    }

    @ConfigSection(
            name = "Activities",
            description = "Set which activity you want assistance with. Only one selected item works.",
            position = 50
    )
    String activitiesSection = "Activities";

    @ConfigItem(
            keyName = "woodcuttingGeneral",
            name = "Woodcutting",
            description = "Highlights trees, logs and banks. Click a tree to set the tree type. Default is willow.",
            section = activitiesSection,
            position = 55

    )
    default boolean woodcutting() {
        return false;
    }

    @ConfigItem(
            keyName = "fishingBarbarianVillage",
            name = "Fishing and Cooking at Barbarians",
            description = "Fish trout and salmon at Barbarian Village, cook it and then drop it.",
            section = activitiesSection,
            position = 60
    )
    default boolean fishingBarbarian() {
        return false;
    }

    @ConfigItem(
            keyName = "crafting",
            name = "Crafting at bank",
            description = "Withdraw resource from bank, craft into product with a tool, deposit product.",
            section = activitiesSection,
            position = 65
    )
    default boolean crafting() {
        return false;
    }

    @ConfigItem(
            keyName = "craftingTool",
            name = "Exact tool name",
            description = "Enter the exact name of the tool you are using, for example 'knife' or 'glassblowing pipe'. Case insensitive.",
            section = activitiesSection,
            position = 66
    )
    default String craftingToolName() {
        return "Glassblowing pipe";
    }

    @ConfigItem(
            keyName = "craftingResource",
            name = "Exact resource name",
            description = "Enter the exact name of the resource you are using, for example 'molten glass' or 'yew logs'. Case insensitive.",
            section = activitiesSection,
            position = 67
    )
    default String craftingResourceName() {
        return "Molten glass";
    }

    @ConfigItem(
            keyName = "craftingProduct",
            name = "Exact product name",
            description = "Enter the exact name of the product you are creating, for example 'yew longbow(u)' or 'lantern lens'. Case insensitive.",
            section = activitiesSection,
            position = 68
    )
    default String craftingProductName() {
        return "Lantern lens";
    }



    @ConfigItem(
            keyName = "inactivityAlert",
            name = "General inactivity alert",
            description = "For unimplemented features you can't find above. Alerts you when you have not animated or moved within the set time, but does not show you what to click.",
            section = activitiesSection,
            position = 70
    )
    default boolean inactivityAlert() {
        return false;
    }

    @ConfigSection(
            name = "Test features",
            description = "Testing features. Multiple selections possible.",
            position = 75
    )
    String testSection = "Test features";

    @ConfigItem(
            keyName = "bogged",
            name = "Bogged",
            description = "Dump it.",
            section = testSection,
            position = 80

    )
    default boolean bogged() {
        return false;
    }

    @ConfigItem(
            keyName = "testSounds",
            name = "Test sound",
            description = "Test sounds by typing the ID in chat.",
            section = testSection,
            position = 85

    )
    default boolean testSounds() {
        return false;
    }


    @ConfigItem(
            keyName = "hideWidgets",
            name = "Hide interface when possible (BREAKS STUFF)",
            description = "Hides all the widgets like the inventory, bank and chatbox when you need to click on an entity, like a tree or monster.",
            section = testSection,
            position = 90
    )
    default boolean hideWidgets() {
        return false;
    }
}
