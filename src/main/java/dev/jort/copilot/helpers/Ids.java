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
import java.util.regex.Pattern;

@Slf4j
@Singleton
public class Ids {

    public final int[] BANK_OBJECT_IDS;

    public final int[] BURNT_FISH_IDS;
    public final int[] COOKED_FISH_IDS;
    public final int[] ROD_FISHING_SPOT_IDS;
    public final int[] RAW_FISH_IDS;

    public final int BANK_CLOSE = 786434; //this is just the bank root container
    public final int SALMON = ItemID.SALMON;
    public final int TROUT = ItemID.TROUT;

    public final int RAW_SALMON = ItemID.RAW_SALMON;
    public final int RAW_TROUT = ItemID.RAW_TROUT;
    public final int BARBARIAN_FIRE = ObjectID.FIRE_43475;


    public Ids() {
        BANK_OBJECT_IDS = determineIds(ObjectID.class, "BANK_CHEST", "BANK_BOOTH");

        BURNT_FISH_IDS = determineIds(ItemID.class, "BURNT");
        COOKED_FISH_IDS = Util.concatArrays(BURNT_FISH_IDS, SALMON, TROUT);
        RAW_FISH_IDS = Util.concatArrays(new int[]{RAW_SALMON}, RAW_TROUT);
        ROD_FISHING_SPOT_IDS = determineIds(NpcID.class, "ROD_FISHING_SPOT");
    }

    public static int[] determineIds(Class source, String... contains) {
        final ArrayList<Integer> ids = new ArrayList<>();

        for (String x : contains) {
            //this regex requires the field to start with X and allow it to end with an underscore with numbers like _3434
            Pattern pattern = Pattern.compile("^" + x + "(_\\d*)?$", Pattern.MULTILINE);
            for (Field field : source.getFields()) {
                if (!field.getName().contains(x)) {
                    continue;
                }
                boolean match = pattern.matcher(field.getName()).find();
                if (!match) {
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
        }
        int[] result = ids.stream().mapToInt(i -> i).toArray();
        log.info("Determined IDs for " + Arrays.toString(contains) + ": " + Arrays.toString(result));
        return result;
    }

}
