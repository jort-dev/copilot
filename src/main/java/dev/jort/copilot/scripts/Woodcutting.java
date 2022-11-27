package dev.jort.copilot.scripts;

import dev.jort.copilot.CopilotConfig;
import dev.jort.copilot.dtos.IdHolder;
import dev.jort.copilot.dtos.Run;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.events.MenuOptionClicked;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
@Slf4j
public class Woodcutting extends ResourceBankScript {

    List<IdHolder> idHolders = new ArrayList<>();
    @Inject
    CopilotConfig config;

    @Override
    public void onStart() {
        IdHolder willowResource = new IdHolder("WILLOW", "WILLOW_TREE").setItemIds(ItemID.WILLOW_LOGS).setName("Willow");
        idHolders.add(new IdHolder("TREE", "EVERGREEN", "DEAD_TREE").setItemIds(ItemID.LOGS).setName("Tree"));
        idHolders.add(new IdHolder("OAK").setItemIds(ItemID.OAK_LOGS).setName("Oak"));
        idHolders.add(willowResource);
        idHolders.add(new IdHolder("MAPLE_TREE").setItemIds(ItemID.MAPLE_LOGS).setName("Maple"));
        idHolders.add(new IdHolder("YEW", "YEW_TREE").setItemIds(ItemID.YEW_LOGS).setName("Yew"));
        idHolders.add(new IdHolder("MAGIC_TREE").setItemIds(ItemID.MAGIC_LOGS).setName("Magic tree"));
        idHolders.add(new IdHolder("REDWOOD").setItemIds(ItemID.REDWOOD_LOGS).setName("Redwood"));
        idHolders.add(new IdHolder("TEAK").setItemIds(ItemID.TEAK_LOGS).setName("Teak"));
        idHolders.add(new IdHolder("MAHOGANY").setItemIds(ItemID.MAHOGANY_LOGS).setName("Mahogany"));
        setResources(willowResource); //default
    }

    @Override
    public int onLoop() {
        if (!config.woodcutting()) {
            return Run.DONE;
        }
        return super.onLoop();
    }

    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (!config.woodcutting()) {
            return;
        }
        if (!event.getMenuOption().equals("Chop down")) {
            return;
        }
        int id = event.getId();
        for (IdHolder resources : idHolders) {
            if (resources.matchId(id)) {
                log.info(resources.getName());
                super.setResources(resources);
            }
        }
    }
}
