package dev.jort.copilot;

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

    public final int[] WILLOW_IDS;
    public final int[] YEW_IDS;
    public final int[] BANKER_IDS;
    public final int[] BANKERS_IDS;

    public final int WILLOW_LOGS = ItemID.WILLOW_LOGS;
    public final int YEW_LOGS = ItemID.YEW_LOGS;
    public final int BANK_CLOSE = 786434; //this is just the bank root container

    public Ids() {
        WILLOW_IDS = determineIds(ObjectID.class, "WILLOW", "STUMP", "DISEASED", "DEAD", "BIRDHOUSE", "BAG");
        BANKER_IDS = determineIds(NpcID.class, "BANKER", "TUTOR");
        YEW_IDS = determineIds(ObjectID.class, "YEW", "STUMP", "DISEASED", "DEAD", "BIRDHOUSE", "BAG", "STRONG");
        BANKERS_IDS = Util.concatArrays(BANKER_IDS, BANK_BOOTH);
        BANK_CHEST_IDS = determineIds(ObjectID.class, "BANK_CHEST");
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
