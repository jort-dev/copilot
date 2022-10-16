package dev.jort.copilot.other;
/*
Utility for scripts to indicate the IDs to click when alert is active.
I also use this as a resource set in for example the woodcutting helper.
 */


import dev.jort.copilot.helpers.Ids;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ObjectID;

@Slf4j
@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class IdHolder {

    private String name = "NoName";
    private int[] npcIds = new int[0];
    private int[] objectIds = new int[0];
    private int[] itemIds = new int[0];
    private int[] widgetIds = new int[0];
    private String[] actions = new String[0];

    public IdHolder(String... contains) {
        setObjectIds(Ids.determineIds(ObjectID.class, contains));
        setName(Util.capitalize(contains[0].toLowerCase()));
    }

    public boolean matchId(int id) {
        return matchId(id, npcIds) || matchId(id, objectIds) || matchId(id, itemIds) || matchId(id, widgetIds);
    }

    public boolean matchAction(String action) {
        for (String a : actions) {
            if (a.equals(action)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchId(int idToMatch, int... ids) {
        return Util.arrayContains(idToMatch, ids);
    }


    public IdHolder setName(String name) {
        this.name = name;
        return this;
    }

    //lombok does not seem to support varargs unfortunately, so typed out manually
    public IdHolder setNpcIds(int... npcIds) {
        this.npcIds = npcIds;
        return this;
    }

    public IdHolder setObjectIds(int... objectIds) {
        this.objectIds = objectIds;
        return this;
    }

    public IdHolder setActions(String... actions) {
        this.actions = actions;
        return this;
    }

    public IdHolder setItemIds(int... itemIds) {
        this.itemIds = itemIds;
        return this;
    }

    public IdHolder setWidgetIds(int... widgetIds) {
        this.widgetIds = widgetIds;
        return this;
    }
}
