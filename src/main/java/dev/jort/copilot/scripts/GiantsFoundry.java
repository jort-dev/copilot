package dev.jort.copilot.scripts;

import dev.jort.copilot.helpers.GiantsFoundryHelper;
import dev.jort.copilot.other.IdHolder;
import dev.jort.copilot.other.Script;

import javax.inject.Inject;

public class GiantsFoundry extends Script {
    @Inject
    GiantsFoundryHelper giantsFoundryHelper;

    @Override
    public void onLoop() {
        action = new IdHolder()
                .setName("Click tool")
                .setObjectIds(giantsFoundryHelper.getCurrentStage().getObjectId());
    }
}
