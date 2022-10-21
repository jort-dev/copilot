package dev.jort.copilot.helpers;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.VarPlayer;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class Combat {
    @Inject
    Client client;

    public int getSpecialAttackPercentage(){
        //100% = 1000
        int value = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT);
        return value / 10;
    }

    public Widget getSpecialAttackOrbWidget(){
        Widget rootSpecWidget = client.getWidget(WidgetInfo.MINIMAP_SPEC_ORB);
        if(rootSpecWidget == null){
            return null;
        }
        if(rootSpecWidget.getStaticChildren() == null){
            return null;
        }
        Widget clickBoxWidget = rootSpecWidget.getStaticChildren()[1];
        return clickBoxWidget;
    }

}
