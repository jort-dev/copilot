package dev.jort.copilot.scripts;

import dev.jort.copilot.other.IdHolder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.events.MenuOptionClicked;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
@Slf4j
public class Woodcutting extends ResourceBankScript {

    List<IdHolder> idHolders = new ArrayList<>();

    public void initialize() {
        idHolders.add(new IdHolder("TREE", "EVERGREEN", "DEAD_TREE").setItemIds(ItemID.LOGS));
        idHolders.add(new IdHolder("OAK").setItemIds(ItemID.OAK_LOGS));
        idHolders.add(new IdHolder("WILLOW").setItemIds(ItemID.WILLOW_LOGS));
        idHolders.add(new IdHolder("MAPLE").setItemIds(ItemID.MAPLE_LOGS));
        idHolders.add(new IdHolder("YEW").setItemIds(ItemID.YEW_LOGS));
        idHolders.add(new IdHolder("MAGIC").setItemIds(ItemID.MAGIC_LOGS));
        idHolders.add(new IdHolder("REDWOOD").setItemIds(ItemID.REDWOOD_LOGS));
        idHolders.add(new IdHolder("TEAK").setItemIds(ItemID.TEAK_LOGS));
        idHolders.add(new IdHolder("MAHOGANY").setItemIds(ItemID.MAHOGANY_LOGS));
    }

    public void onMenuOptionClicked(MenuOptionClicked event) {
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
