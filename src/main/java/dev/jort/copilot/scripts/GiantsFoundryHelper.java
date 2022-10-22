package dev.jort.copilot.scripts;

import dev.jort.copilot.helpers.GiantsFoundry;
import dev.jort.copilot.other.IdHolder;
import dev.jort.copilot.other.Script;

import javax.inject.Inject;

public class GiantsFoundryHelper extends Script {
    @Inject
    GiantsFoundry giantsFoundry;

    @Override
    public void onLoop() {
        action = new IdHolder()
                .setName("Click tool")
                .setObjectIds(giantsFoundry.getCurrentStage().getObjectId());
    }
}
