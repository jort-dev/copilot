package dev.jort.copilot.helpers;

import dev.jort.copilot.other.Util;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;

import javax.inject.Singleton;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
@Singleton
public class Ids {

    public final int[] BANK_CHEST_IDS;
    public final int BANK_BOOTH = 10355;

    public final int[] WILLOW_TREE_IDS;
    public final int[] YEW_TREE_IDS;
    public final int[] BANKER_IDS;
    public final int[] BANKERS_IDS;
    public final int[] BURNT_FISH_IDS;
    public final int[] COOKED_FISH_IDS;

    public final int WILLOW_LOGS = ItemID.WILLOW_LOGS;
    public final int YEW_LOGS = ItemID.YEW_LOGS;
    public final int BANK_CLOSE = 786434; //this is just the bank root container
    public final int SALMON = ItemID.SALMON;
    public final int TROUT = ItemID.TROUT;

    public final int RAW_SALMON = ItemID.RAW_SALMON;
    public final int RAW_TROUT = ItemID.RAW_TROUT;
    public final int BARBARIAN_FIRE = ObjectID.FIRE_43475;



    public Ids() {
        WILLOW_TREE_IDS = determineIds(ObjectID.class, "WILLOW", "STUMP", "DISEASED", "DEAD", "BIRDHOUSE", "BAG");
        BANKER_IDS = determineIds(NpcID.class, "BANKER", "TUTOR");
        YEW_TREE_IDS = determineIds(ObjectID.class, "YEW", "STUMP", "DISEASED", "DEAD", "BIRDHOUSE", "BAG", "STRONG");
        BANKERS_IDS = Util.concatArrays(BANKER_IDS, BANK_BOOTH);
        BANK_CHEST_IDS = determineIds(ObjectID.class, "BANK_CHEST");
        BURNT_FISH_IDS = determineIds(ItemID.class, "BURNT");
        COOKED_FISH_IDS = Util.concatArrays(BURNT_FISH_IDS, SALMON, TROUT);
    }

    public int[] determineIds(Class source, String contains, String... notContains) {
        final ArrayList<Integer> ids = new ArrayList<>();

        for (Field field : source.getFields()) {
            if (!field.getName().contains(contains)) {
                continue;
            }
            if (Util.containsAny(field.getName(), notContains)) {
                continue;
            }

            int value = -1;
            field.setAccessible(true);
            try {
                value = field.getInt(value); //idk why i need to pass a value but it works
            } catch (IllegalAccessException e) {
                log.warn("Can't access field " + field.getName());
            }
            ids.add(value);
        }
        int[] result = ids.stream().mapToInt(i -> i).toArray();
        log.info("Determined IDs for " + contains + ": " + Arrays.toString(result));
        return result;
    }

}
