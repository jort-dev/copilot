package dev.jort.copilot.priority_scripts;

import dev.jort.copilot.dtos.IdHolder;
import dev.jort.copilot.dtos.Run;
import dev.jort.copilot.other.PriorityScript;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;

import javax.inject.Singleton;

@Singleton
public class Hp extends PriorityScript {


    int ROCK_CAKE = ItemID.DWARVEN_ROCK_CAKE_7510;
    int OVERLOAD_4 = ItemID.OVERLOAD_4;
    int OVERLOAD_3 = ItemID.OVERLOAD_3;
    int OVERLOAD_2 = ItemID.OVERLOAD_2;
    int OVERLOAD_1 = ItemID.OVERLOAD_1;


    @Override
    public int onLoop() {
        if (config.hpAlert() < 1) {
            return Run.DONE;
        }
        int hp = client.getBoostedSkillLevel(Skill.HITPOINTS);
        int absorptionLeft = client.getVarbitValue(Varbits.NMZ_ABSORPTION);
        if (hp <= config.hpAlert()) {
            return Run.DONE;
        }
        alert.handleAlert(true);

        action = new IdHolder()
                .setName("Lower hp")
                .setItemIds(ROCK_CAKE);

        if (hp > 50) {
            action.setItemIds(ROCK_CAKE, OVERLOAD_1, OVERLOAD_2, OVERLOAD_3, OVERLOAD_4);
        }
        return Run.AGAIN;
    }
}
