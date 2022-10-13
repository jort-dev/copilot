package dev.jort.copilot.other;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ToString
@NoArgsConstructor
public class Action {

    private String hint = "Action";
    private int[] npcIds = new int[0];
    private int[] objectIds = new int[0];
    private int[] itemIds = new int[0];
    private int[] widgetIds = new int[0];

    public boolean match(int id) {
        return match(id, npcIds) || match(id, objectIds) || match(id, itemIds) || match(id, widgetIds);
    }

    private boolean match(int idToMatch, int... ids) {
        for (int id : ids) {
            if (id == idToMatch) {
                return true;
            }
        }
        return false;
    }


    public Action setHint(String hint) {
        this.hint = hint;
        return this;
    }

    //lombok does not seem to support varargs unfortunately, so typed out manually because its clean
    public Action setNpcIds(int... npcIds) {
        this.npcIds = npcIds;
        return this;
    }

    public Action setObjectIds(int... objectIds) {
        this.objectIds = objectIds;
        return this;
    }

    public Action setItemIds(int... itemIds) {
        this.itemIds = itemIds;
        return this;
    }

    public Action setWidgetIds(int... widgetIds) {
        this.widgetIds = widgetIds;
        return this;
    }
}
