package dev.jort.copilot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;

import javax.inject.Singleton;
import java.lang.reflect.Field;
import java.util.ArrayList;

@Slf4j
@Singleton
public class Ids {

    public final int BANK_BOOTH = 10355;

    public final int[] WILLOW_IDS;
    public final int[] BANKER_IDS;
    public final int[] BANKERS_IDS;

    public final int WILLOW_LOGS = ItemID.WILLOW_LOGS;

    public Ids() {
        WILLOW_IDS = determineIds(ObjectID.class, "WILLOW", "STUMP", "DISEASED", "DEAD", "BIRDHOUSE", "BAG");
        BANKER_IDS = determineIds(NpcID.class, "BANKER", "TUTOR");
        BANKERS_IDS = Util.concatArrays(BANKER_IDS,BANK_BOOTH);
        log.info("Hacked: " + Util.arrayToString(BANKERS_IDS));
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
        log.info("Determined IDs for " + contains + ": " + Util.arrayToString(result));
        return result;
    }

}
