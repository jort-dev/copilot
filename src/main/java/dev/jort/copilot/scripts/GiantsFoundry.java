package dev.jort.copilot.scripts;

import dev.jort.copilot.helpers.GiantsFoundryHelper;
import dev.jort.copilot.other.IdHolder;
import dev.jort.copilot.other.Script;

import javax.inject.Inject;

public class GiantsFoundry extends Script {
    @Inject
    GiantsFoundryHelper gf;

    @Override
    public void onStart() {
        action = new IdHolder()
                .setName("Click tool")
                .setObjectIds(gf.getCurrentStage().getObjectId());

    }

    @Override
    public void onLoop() {
        if(gf.isOperatingMachine()){

            gf.getHeatLeft();
        }
    }
}
