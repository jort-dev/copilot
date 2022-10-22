package dev.jort.copilot.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.ColorScheme;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/*
When heat in range:
- alert when almost out of range and highlight
 */
@Slf4j
@Singleton
public class GiantsFoundry {

    @Inject
    Client client;

    private final List<Stage> stages = new ArrayList<>();

    //tool game object ids
    public static final int TRIP_HAMMER = 44619;
    public static final int GRINDSTONE = 44620;
    public static final int POLISHING_WHEEL = 44621;

    private static final int WIDGET_HEAT_PARENT = 49414153;
    private static final int WIDGET_MED_HEAT_PARENT = 49414164;
    private static final int VARBIT_HEAT = 13948;
    private static final int VARBIT_PROGRESS = 13949;
    static final int WIDGET_PROGRESS_PARENT = 49414219;
    private static final int SPRITE_ID_TRIP_HAMMER = 4442;
    private static final int SPRITE_ID_GRINDSTONE = 4443;
    private static final int SPRITE_ID_POLISHING_WHEEL = 4444;

    public Heat getCurrentHeat() {
        int heat = getHeatAmount();

        int[] low = getLowHeatRange();
        if (heat > low[0] && heat < low[1]) {
            return Heat.LOW;
        }

        int[] med = getMedHeatRange();
        if (heat > med[0] && heat < med[1]) {
            return Heat.MED;
        }

        int[] high = getHighHeatRange();
        if (heat > high[0] && heat < high[1]) {
            return Heat.HIGH;
        }

        return Heat.NONE;
    }

    public int getHeatAmount() {
        return client.getVarbitValue(VARBIT_HEAT);
    }

    public int[] getLowHeatRange() {
        return new int[]{
                (int) ((1 / 6d - getHeatRangeRatio() / 2) * 1000),
                (int) ((1 / 6d + getHeatRangeRatio() / 2) * 1000),
        };
    }

    public int[] getMedHeatRange() {
        return new int[]{
                (int) ((3 / 6d - getHeatRangeRatio() / 2) * 1000),
                (int) ((3 / 6d + getHeatRangeRatio() / 2) * 1000),
        };
    }

    public int[] getHighHeatRange() {
        return new int[]{
                (int) ((5 / 6d - getHeatRangeRatio() / 2) * 1000),
                (int) ((5 / 6d + getHeatRangeRatio() / 2) * 1000),
        };
    }

    private double heatRangeRatio = 0;

    public double getHeatRangeRatio() {
        if (heatRangeRatio == 0) {
            Widget heatWidget = client.getWidget(WIDGET_HEAT_PARENT);
            Widget medHeat = client.getWidget(WIDGET_MED_HEAT_PARENT);
            if (medHeat == null || heatWidget == null) {
                return 0;
            }

            heatRangeRatio = medHeat.getWidth() / (double) heatWidget.getWidth();
        }

        return heatRangeRatio;
    }

    public int getActionsLeftInStage() {
        int progress = getProgressAmount();
        double progressPerStage = getProgressPerStage();
        double progressTillNext = progressPerStage - progress % progressPerStage;

        Stage current = getCurrentStage();
        return (int) Math.ceil(progressTillNext / current.getProgressPerAction());
    }

    public int getProgressAmount() {
        return client.getVarbitValue(VARBIT_PROGRESS);
    }

    public double getProgressPerStage() {
        return 1000d / getStages().size();
    }

    public Stage getCurrentStage() {
        int index = (int) (getProgressAmount() / 1000d * getStages().size());
        if (index < 0 || index > getStages().size() - 1) {
            return null;
        }

        return getStages().get(index);
    }

    public List<Stage> getStages() {
        if (stages.isEmpty()) {
            Widget progressParent = client.getWidget(WIDGET_PROGRESS_PARENT);
            if (progressParent == null || progressParent.getChildren() == null) {
                return new ArrayList<>();
            }

            for (Widget child : progressParent.getChildren()) {
                switch (child.getSpriteId()) {
                    case SPRITE_ID_TRIP_HAMMER:
                        stages.add(Stage.TRIP_HAMMER);
                        break;
                    case SPRITE_ID_GRINDSTONE:
                        stages.add(Stage.GRINDSTONE);
                        break;
                    case SPRITE_ID_POLISHING_WHEEL:
                        stages.add(Stage.POLISHING_WHEEL);
                        break;
                }
            }
        }

        return stages;
    }
    public int getHeatLeft(){
        return getActionsForHeatLevel();
    }

    public int getActionsForHeatLevel()
    {
        Heat heatStage = getCurrentHeat();
        Stage stage = getCurrentStage();
        if (heatStage != stage.getHeat())
        {
            // not the right heat to start with
            return 0;
        }

        int[] range = getCurrentHeatRange();
        int actions = 0;
        int heat = getHeatAmount();
        while (heat > range[0] && heat < range[1])
        {
            actions++;
            heat += stage.getHeatChange();
        }

        return actions;
    }

    public int[] getCurrentHeatRange()
    {
        switch (getCurrentStage())
        {
            case POLISHING_WHEEL:
                return getLowHeatRange();
            case GRINDSTONE:
                return getMedHeatRange();
            case TRIP_HAMMER:
                return getHighHeatRange();
            default:
                return new int[]{0, 0};
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Heat
    {
        LOW("Low"),
        MED("Medium"),
        HIGH("High"),
        NONE("Not in range");

        private final String name;
    }

    @Getter
    @AllArgsConstructor
    public enum Stage
    {


        TRIP_HAMMER("Hammer", Heat.HIGH, 20, -25, GiantsFoundry.TRIP_HAMMER),
        GRINDSTONE("Grind", Heat.MED, 10, 15, GiantsFoundry.GRINDSTONE),
        POLISHING_WHEEL("Polish", Heat.LOW, 10, -17, GiantsFoundry.POLISHING_WHEEL);


        private final String name;
        private final Heat heat;
        private final int progressPerAction;
        private final int heatChange;
        private final int objectId;
    }
}
