package dev.jort.copilot.old;

import dev.jort.copilot.dtos.Stage;

public class CodeHolder {
    private final String[] illegalKeywords = {"STUMP", "DISEASED", "DEAD", "BIRDHOUSE",
            "BAG", "STRONG", "BENCH", "CAPE", "CHAIR", "DECORATION", "DRAWER", "FEEDER",
            "HOUSE", "OUTFIT", "NOCKS", "WARDROBE", "TROPHY", "TABLE", "CHEST", "STAND",
            "BRANCH", "DOOR", "PATCH", "CROSSBOW"};

    public void determineHover() {
        Stage currentStage = gf.getCurrentStage();
        Stage nextStage = gf.getNextStage();

        //we cant determine hover without knowing the current stage
        if (currentStage.equals(Stage.NONE)) {
            return;
        }

        canFinishStageWithHeat = gf.getHeatLeft() > gf.getActionsLeftInStage();

        //if we are between machines: how much heat is required for us to click the machine instead of the temperature changer
        hasEnoughHeat = gf.getHeatLeft() > 5;


        //if there is no next stage, next stage will be NONE, which is a higher ordinal, so the next stage is 'colder'
        isNextStageWarmer = currentStage.ordinal() > nextStage.ordinal();
        boolean isFinalStage = nextStage.equals(Stage.NONE);

        machineHeatsSword = currentStage.equals(Stage.GRINDSTONE);
        testBoolean = true;
//        logg(canFinishStageWithHeat, hasEnoughHeat, isNextStageWarmer, machineHeatsSword, isFinalStage);


        if (gf.isOperatingMachine()) {
            if (canFinishStageWithHeat) {
                if (isNextStageWarmer) {
                    hoverLava();
                }
                else {
                    if (isFinalStage) {
                        hoverKovac();
                    }
                    else {
                        hoverWater();
                    }
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
            action.setSecondaryGameObjectIds(currentStage.getObjectId());
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
                        if (isFinalStage) {
                            hoverKovac();
                        }
                        else {
                            hoverWater();
                        }
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
                action.setSecondaryGameObjectIds(currentStage.getObjectId());
            }
        }
    }
}
