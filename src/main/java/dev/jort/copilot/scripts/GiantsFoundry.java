package dev.jort.copilot.scripts;

import dev.jort.copilot.dtos.Run;
import dev.jort.copilot.dtos.Stage;
import dev.jort.copilot.helpers.GiantsFoundryHelper;
import dev.jort.copilot.other.Script;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
/*
Maximum amount of heat left per stage
STEEL_MITHRIL:
    RED_MAX = 10
    GREEN_MAX = 15
    YELLOW_MAX = 17
 */
public class GiantsFoundry extends Script {
    @Inject
    GiantsFoundryHelper gf;


    @Override
    public int onLoop() {
        if (!config.giantsFoundry()) {
            return Run.DONE;
        }
        action.setGameObjectIds();
        action.setSecondaryGameObjectIds();
        determineHover();
//        determineAction();
        return Run.AGAIN;
    }

    public void hoverWater() {
        action.setSecondaryGameObjectIds(GiantsFoundryHelper.WATERFALL);
    }

    public void hoverLava() {
        action.setSecondaryGameObjectIds(GiantsFoundryHelper.LAVA_POOL);
    }


    public void determineHover() {
        Stage currentStage = gf.getCurrentStage();
        Stage nextStage = gf.getNextStage();

        //we cant determine hover without knowing the current stage
        if (currentStage.equals(Stage.NONE)) {
            return;
        }

        //no hover when no next stage
        if (nextStage.equals(Stage.NONE)) {
            return;
        }

        boolean canFinishStageWithHeat = gf.getHeatLeft() > gf.getActionsLeftInStage() + config.giantsFoundryToolBuffer();
        boolean hasEnoughHeat = gf.getHeatLeft() > 5;
        boolean isNextStageWarmer = currentStage.ordinal() < nextStage.ordinal();
        boolean machineHeatsSword = currentStage.equals(Stage.GRINDSTONE);


        if (gf.isOperatingMachine()) {
            if (canFinishStageWithHeat) {
                if (isNextStageWarmer) {
                    hoverWater();
                }
                else {
                    hoverLava();
                }
            }
            else {
                if (machineHeatsSword) {
                    hoverWater();
                }
                else {
                    hoverLava();
                }
            }
        }
        else if (gf.isModifyingTemperature()) {
            action.setGameObjectIds(currentStage.getObjectId());
        }
        //this gets reached if we are in between actions
        else {
            //if we have enough heat, we should currently click the machine, so the next action is to change temperature
            if (hasEnoughHeat) {
                if (canFinishStageWithHeat) {
                    if (isNextStageWarmer) {
                        hoverLava();
                    }
                    else {
                        hoverWater();
                    }
                }
                else {
                    if (machineHeatsSword) {
                        hoverWater();
                    }
                    else {
                        hoverLava();
                    }
                }
            }
            //else the next action is to click the machine, because we should be changing temperature rn
            else {
                action.setGameObjectIds(currentStage.getObjectId());
            }
        }
    }

    public void determineAction() {
        GiantsFoundryHelper.Action actionNeeded = gf.determineAction();
        String actionString = actionNeeded.name().toLowerCase();
        if (actionString.contains("machine")) {
            action
                    .setName("Click machine")
                    .setGameObjectIds(gf.getCurrentStage().getObjectId());
            return;
        }
        if (actionString.contains("warm")) {
            action
                    .setName("Click lava")
                    .setGameObjectIds(GiantsFoundryHelper.LAVA_POOL);
            return;
        }
        if (actionString.contains("cool")) {
            action
                    .setName("Click water")
                    .setGameObjectIds(GiantsFoundryHelper.WATERFALL);
            return;
        }
    }
}
