package dev.jort.copilot.scripts;

import dev.jort.copilot.helpers.GiantsFoundryHelper;
import dev.jort.copilot.other.IdHolder;
import dev.jort.copilot.other.Script;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class GiantsFoundry extends Script {
    @Inject
    GiantsFoundryHelper gf;

    @Override
    public void onLoop() {
        if (gf.isOperatingMachine()) {
            //hover next machine if enough actions left
            if(gf.getHeatLeft() > gf.getActionsLeftInStage() + config.giantsFoundryToolBuffer()){
                action = new IdHolder()
                        .setName("Hover next machine")
                        .setGameObjectIds(gf.getNextStage().getObjectId());
                return;
            }

            //otherwise hover lava/water
            if(gf.determineAction() == GiantsFoundryHelper.Action.USE_MACHINE_TO_LOWER){
                action = new IdHolder()
                        .setName("Hover lava")
                        .setGameObjectIds(GiantsFoundryHelper.LAVA_POOL);
                return;
            }
            if(gf.determineAction() == GiantsFoundryHelper.Action.USE_MACHINE_TO_UPPER){
                action = new IdHolder()
                        .setName("Hover water")
                        .setGameObjectIds(GiantsFoundryHelper.WATERFALL);
                return;
            }

            log.warn("Unknown state, probably wrong machine!");
            alert.playAlternativeAlertSound();
            return;
        }

        if(gf.isModifyingTemperature()){
            action = new IdHolder()
                    .setName("Hover machine")
                    .setGameObjectIds(gf.getCurrentStage().getObjectId());
            return;
        }


        GiantsFoundryHelper.Action actionNeeded = gf.determineAction();
        String actionString = actionNeeded.name().toLowerCase();
        if(actionString.contains("machine")){
            action = new IdHolder()
                    .setName("Click machine")
                    .setGameObjectIds(gf.getCurrentStage().getObjectId());
            return;
        }
        if(actionString.contains("warm")){
            action = new IdHolder()
                    .setName("Click lava")
                    .setGameObjectIds(GiantsFoundryHelper.LAVA_POOL);
            return;
        }
        if(actionString.contains("cool")){
            action = new IdHolder()
                    .setName("Click water")
                    .setGameObjectIds(GiantsFoundryHelper.WATERFALL);
            return;
        }
    }
}
