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
            name = "Alert settings",
            description = "Configure sound alert settings.",
            closedByDefault = true,
            position = 5
    )
    String alertSettings = "AlertSettings";

    @Range(
            max = SoundEffectVolume.HIGH
    )
    @ConfigItem(
            keyName = "alertVolume",
            name = "Volume",
            description = "Configures the volume the alerts. Off=0, loudest=127",
            section = alertSettings,
            position = 10
    )
    default int alertVolume() {
        return SoundEffectVolume.HIGH;
    }

    @ConfigItem(
            keyName = "mainSoundId",
            name = "Sound ID",
            description = "Configures the sound to play for alerts. For inspiration: search 'sound id osrs'.",
            section = alertSettings,
            position = 15
    )
    default int mainSoundId() {
        return 3929;
    }

    @ConfigItem(
            keyName = "alternativeSoundId",
            name = "Alt sound ID",
            description = "Configures the sound to play when the inventory is full. Set to 0 to copy above sound.",
            section = alertSettings,
            position = 20
    )
    default int alternativeSoundId() {
        return 1959;
    }

    @ConfigItem(
            keyName = "alertDelay",
            name = "Delay",
            description = "Configure after how many milliseconds of inactivity the alert go off.",
            section = alertSettings,
            position = 25
    )
    default int alertDelayMs() {
        return 1000;
    }

    @ConfigItem(
            keyName = "amountOfAlerts",
            name = "Amount",
            description = "Configure after how many times the alert sound is played.",
            section = alertSettings,
            position = 30
    )
    default int amountOfSoundAlerts() {
        return 5;
    }

    @ConfigItem(
            keyName = "useSystemNotification",
            name = "System notification",
            description = "In addition to visual and auditory alerts, also fire a system notification, like most plugins do.",
            section = alertSettings,
            position = 35
    )
    default boolean useSystemNotifications() {
        return false;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @ConfigSection(
            name = "Overlay settings",
            description = "Configure overlay settings.",
            closedByDefault = true,
            position = 37
    )
    String overlaySettings = "OverlaySettings";

    @Alpha
    @ConfigItem(
            keyName = "overlayColor",
            name = "Notification color",
            description = "Set the notification overlay color",
            section = overlaySettings,
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
            section = overlaySettings,
            position = 45
    )
    default Color highlightColor() {
        return new Color(0.0f, 1.0f, 1.0f, 0.2f);
    }

    @Alpha
    @ConfigItem(
            keyName = "alternativeHighlightColor",
            name = "Alt highlight color",
            description = "Set the color of the highlight for alternative items to click, for example the next action.",
            section = overlaySettings,
            position = 47
    )
    default Color alternativeHighlightColor() {
        return new Color(0.0f, 1.0f, 0.0f, 0.2f);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @ConfigSection(
            name = "Activities - fully guided",
            description = "Set which activity you want assistance with. Only one selected item works.",
            closedByDefault = true,
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
            position = 70
    )
    default String craftingToolName() {
        return "Glassblowing pipe";
    }

    @ConfigItem(
            keyName = "craftingResource",
            name = "Exact resource name",
            description = "Enter the exact name of the resource you are using, for example 'molten glass' or 'yew logs'. Case insensitive.",
            section = activitiesSection,
            position = 75
    )
    default String craftingResourceName() {
        return "Molten glass";
    }

    @ConfigItem(
            keyName = "craftingProduct",
            name = "Exact product name",
            description = "Enter the exact name of the product you are creating, for example 'yew longbow(u)' or 'lantern lens'. Case insensitive.",
            section = activitiesSection,
            position = 80
    )
    default String craftingProductName() {
        return "Lantern lens";
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @ConfigSection(
            name = "Activities - partially guided",
            description = "Set which activity you want assistance with. Only one selected item works.",
            closedByDefault = true,
            position = 85
    )
    String partialActivitiesSection = "Partial activities";


    @ConfigItem(
            keyName = "giantsFoundry",
            name = "Giants foundry",
            description = "Shows you the next thing to click and alerts you when to in the Giants Foundry.",
            section = partialActivitiesSection,
            position = 90
    )
    default boolean giantsFoundry() {
        return false;
    }

    @ConfigItem(
            keyName = "giantsFoundryToolBuffer",
            name = "Tool buffer",
            description = "If set to 1, you will be alerted to click the temperature modifier if there is still 1 temperature left in the sword. Set higher the sword keeps getting damaged due to wrong temperature.",
            section = partialActivitiesSection,
            position = 94
    )
    default int giantsFoundryToolBuffer() {
        return 1;
    }

    @ConfigItem(
            keyName = "giantsFoundryTemperatureBuffer",
            name = "Temperature buffer",
            description = "If set to 3, you will be alerted to click the machine if the sword can still have 3 more heat stored into it. Set to higher if you keep overshooting the temperature.",
            section = partialActivitiesSection,
            position = 97
    )
    default int giantsFoundryTemperatureBuffer() {
        return 3;
    }

    @ConfigItem(
            keyName = "inactivityAlert",
            name = "General inactivity alert",
            description = "For unimplemented features you can't find above. Alerts you when you have not animated or moved within the set time, but does not show you what to click.",
            section = partialActivitiesSection,
            position = 100
    )
    default boolean inactivityAlert() {
        return false;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @ConfigSection(
            name = "Activities - additions",
            description = "These scripts will run before the main running script. Multiple options possible.",
            closedByDefault = true,
            position = 101
    )
    String priorityScriptsSection = "Priority scripts";

    @ConfigItem(
            keyName = "specialAttackAlert",
            name = "Special attack alert",
            description = "Alerts when the special attack is charged 100%. Useful for skilling tools made of dragon for example.",
            section = priorityScriptsSection,
            position = 102
    )
    default boolean specialAttackAlert() {
        return false;
    }

    @ConfigItem(
            keyName = "lootAlert",
            name = "Loot alert",
            description = "Alerts you when loot is seen on the ground.",
            section = priorityScriptsSection,
            position = 103
    )
    default boolean lootAlert() {
        return false;
    }

    @ConfigItem(
            keyName = "lootAlertLoot",
            name = "Loot names",
            description = "Enter the text the loot should contain, case insensitive, separated by commas.",
            section = priorityScriptsSection,
            position = 103
    )
    default String lootAlertLoot() {
        return "bird,clue,fossil,key";
    }

    @ConfigItem(
            keyName = "kittenAlert",
            name = "Kitten alert",
            description = "Alerts you when you need to interact with your kitten.",
            section = priorityScriptsSection,
            position = 104
    )
    default boolean kittenAlert() {
        return false;
    }

    @ConfigItem(
            keyName = "hpAlert",
            name = "Alerts when hp is above threshold.",
            description = "Use it for Nightmare Zone for example. Set to 0 or lower to disable",
            section = priorityScriptsSection,
            position = 104
    )
    default int hpAlert() {
        return 0;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @ConfigSection(
            name = "Test features",
            description = "Testing features. Multiple selections possible.",
            closedByDefault = true,
            position = 105
    )
    String testSection = "Test features";

    @ConfigItem(
            keyName = "bogged",
            name = "Bogged",
            description = "Dump it.",
            section = testSection,
            position = 110

    )
    default boolean bogged() {
        return false;
    }

    @ConfigItem(
            keyName = "testSounds",
            name = "Test sound",
            description = "Test sounds by typing the ID in chat. Search 'osrs sound id' for a list.",
            section = testSection,
            position = 120

    )
    default boolean testSounds() {
        return false;
    }

    @ConfigItem(
            keyName = "testScript",
            name = "Test script",
            description = "Test the script currently in development.",
            section = testSection,
            position = 140
    )
    default boolean testScript() {
        return false;
    }

    @ConfigItem(
            keyName = "debug",
            name = "Debug",
            description = "Enable debug mode with more paint and log messages.",
            section = testSection,
            position = 150
    )
    default boolean debug() {
        return false;
    }


}
