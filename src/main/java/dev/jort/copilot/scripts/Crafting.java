package dev.jort.copilot.scripts;

import dev.jort.copilot.CopilotConfig;
import dev.jort.copilot.dtos.Run;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.events.ConfigChanged;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class Crafting extends InventoryMakeScript {
    @Inject
    CopilotConfig config;

    @Override
    public void onStart() {
        applyConfig();
    }

    @Override
    public int onLoop() {
        if (!config.crafting()) {
            return Run.DONE;
        }
        return super.onLoop();
    }

    public void onConfigChanged(ConfigChanged event) {
        if (!config.crafting()) {
            return;
        }
        applyConfig();
    }

    public void applyConfig() {
        setValues(config.craftingResourceName(), config.craftingProductName(), config.craftingToolName());
    }
}
