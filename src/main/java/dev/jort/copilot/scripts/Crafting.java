package dev.jort.copilot.scripts;

import dev.jort.copilot.CopilotConfig;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.events.ConfigChanged;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class Crafting extends InventoryMakeScript{
    @Inject
    CopilotConfig config;

    @Override
    public void onStart() {
        applyConfig();
    }

    public void onConfigChanged(ConfigChanged event) {
        if(!config.crafting()){
            return;
        }
        log.info("Updating crafting with new values.");
        applyConfig();
    }

    public void applyConfig(){
        setValues(config.craftingResourceName(), config.craftingProductName(), config.craftingToolName());
    }
}
