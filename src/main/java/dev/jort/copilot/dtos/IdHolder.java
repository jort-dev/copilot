package dev.jort.copilot.dtos;
/*
Utility for scripts to indicate the IDs to click when alert is active.
I also use this as a resource set in for example the woodcutting helper.
 */


import dev.jort.copilot.helpers.Ids;
import dev.jort.copilot.other.Util;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ObjectID;
import net.runelite.api.widgets.Widget;

@Slf4j
@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class IdHolder {

    private String name = "NoName";
    private int[] npcIds = new int[0];
    private int[] secondaryNpcIds = new int[0];
    private int[] gameObjectIds = new int[0];
    private int[] secondaryGameObjectIds = new int[0];
    private int[] itemIds = new int[0];
    private Widget[] widgets = new Widget[0];
    private String[] actions = new String[0];

    public IdHolder(String... contains) {
        setGameObjectIds(Ids.determineIds(ObjectID.class, contains));
        setName(Util.capitalize(contains[0].toLowerCase()));
    }

    public boolean matchId(int id) {
        return matchId(id, npcIds) || matchId(id, secondaryNpcIds) || matchId(id, gameObjectIds) || matchId(id, secondaryGameObjectIds) || matchId(id, itemIds) || matchId(id, widgets);
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

    private boolean matchId(int idToMatch, Widget... widgets) {
        if (widgets == null) {
            return false;
        }
        for (Widget widget : widgets) {
            if (widget == null) {
                continue;
            }
            if (widget.getId() != idToMatch) {
                continue;
            }
            return true;
        }
        return false;
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

    public IdHolder setGameObjectIds(int... gameObjectIds) {
        this.gameObjectIds = gameObjectIds;
        return this;
    }

    public IdHolder setSecondaryGameObjectIds(int... gameObjectIds) {
        this.secondaryGameObjectIds = gameObjectIds;
        return this;
    }

    public IdHolder setSecondaryNpcIds(int... secondaryNpcIds) {
        this.secondaryNpcIds = secondaryNpcIds;
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

    public IdHolder setWidgets(Widget... widgets) {
        this.widgets = widgets;
        return this;
    }
}
