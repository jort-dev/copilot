package dev.jort.copilot.scripts;

import dev.jort.copilot.helpers.GiantsFoundryHelper;
import dev.jort.copilot.other.Script;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class GiantsFoundry extends Script {
    @Inject
    GiantsFoundryHelper gf;

    @Override
    public void onLoop() {
    }

    public void determineHover() {
        if (gf.isOperatingMachine()) {
            //hover next machine if enough actions left
            if (gf.getHeatLeft() > gf.getActionsLeftInStage() + config.giantsFoundryToolBuffer()) {
                action
                        .setName("Hover next machine")
                        .setSecondaryGameObjectIds(gf.getNextStage().getObjectId());
                return;
            }

            //hover lava if machine is cooling sword down
            if (gf.determineAction() == GiantsFoundryHelper.Action.USE_MACHINE_TO_LOWER) {
                action
                        .setName("Hover lava")
                        .setSecondaryGameObjectIds(GiantsFoundryHelper.LAVA_POOL);
                return;
            }

            //hover water if machine is warming sword
            if (gf.determineAction() == GiantsFoundryHelper.Action.USE_MACHINE_TO_UPPER) {
                action
                        .setName("Hover water")
                        .setSecondaryGameObjectIds(GiantsFoundryHelper.WATERFALL);
                return;
            }

            //if this gets reached, we should stop using  this machine
            return;
        }

        //hover machine if changing temperature
        if (gf.isModifyingTemperature()) {
            action
                    .setName("Hover machine")
                    .setSecondaryGameObjectIds(gf.getCurrentStage().getObjectId());
            return;
        }

        //clear hover items
        action.setSecondaryGameObjectIds();
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
