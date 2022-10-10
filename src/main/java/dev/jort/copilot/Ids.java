package dev.jort.copilot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ObjectID;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Singleton
public class Ids {

    public final int BANK_BOOTH = 10355;

    public int[] WILLOW_IDS;

    public Ids() {
        determineWillowIds();
    }

    public void determineWillowIds() {
        final ArrayList<Integer> ids = new ArrayList<>();
        List<String> illegalFields = Arrays.asList("STUMP", "DISEASED", "DEAD", "BIRDHOUSE", "BAG");

        for (Field field : ObjectID.class.getFields()) {
            if(!field.getName().contains("WILLOW")){
                continue;
            }
            if (Util.containsAny(field.getName(), illegalFields)) {
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
        WILLOW_IDS = ids.stream().mapToInt(i -> i).toArray();
    }
}
