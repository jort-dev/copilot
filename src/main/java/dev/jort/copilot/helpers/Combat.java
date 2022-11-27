package dev.jort.copilot.helpers;

import net.runelite.api.Client;
import net.runelite.api.VarPlayer;
import net.runelite.api.WorldType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class Combat {

    private static final Pattern WILDERNESS_LEVEL_PATTERN = Pattern.compile("^Level: (\\d+)$");

    @Inject
    Client client;

    public int getCombatLevel() {
        return client.getLocalPlayer().getCombatLevel();
    }

    public int getSpecialAttackPercentage() {
        //100% = 1000
        int value = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT);
        return value / 10;
    }

    public Widget getSpecialAttackOrbWidget() {
        Widget rootSpecWidget = client.getWidget(WidgetInfo.MINIMAP_SPEC_ORB);
        if (rootSpecWidget == null) {
            return null;
        }
        if (rootSpecWidget.getStaticChildren() == null) {
            return null;
        }
        Widget clickBoxWidget = rootSpecWidget.getStaticChildren()[1];
        return clickBoxWidget;
    }

    public int getWildernessLevel() {
        final Widget wildernessLevelWidget = client.getWidget(WidgetInfo.PVP_WILDERNESS_LEVEL);
        if (wildernessLevelWidget == null) {
            return -1;
        }

        final String wildernessLevelText = wildernessLevelWidget.getText();
        final Matcher m = WILDERNESS_LEVEL_PATTERN.matcher(wildernessLevelText);
        if (!m.matches() || WorldType.isPvpWorld(client.getWorldType())) {
            return -1;
        }

        final int wildernessLevel = Integer.parseInt(m.group(1));
        return wildernessLevel;
    }

    public boolean isInWilderness() {
        return getWildernessLevel() != -1;
    }


}
