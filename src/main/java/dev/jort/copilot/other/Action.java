package dev.jort.copilot.other;
/*
Utility for scripts to indicate the IDs to click when alert is active.
 */


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class Action {

    private String hint = "Action";
    private int[] npcIds = new int[0];
    private int[] objectIds = new int[0];
    private int[] itemIds = new int[0];
    private int[] widgetIds = new int[0];
    private String[] actions = new String[0];

    public boolean matchId(int id) {
        return matchId(id, npcIds) || matchId(id, objectIds) || matchId(id, itemIds) || matchId(id, widgetIds);
    }

    public boolean matchAction(String action){
        for (String a : actions){
            if (a.equals(action)){
                return true;
            }
        }
        return false;
    }

    private boolean matchId(int idToMatch, int... ids) {
        return Util.arrayContains(idToMatch, ids);
    }


    public Action setHint(String hint) {
        this.hint = hint;
        return this;
    }

    //lombok does not seem to support varargs unfortunately, so typed out manually
    public Action setNpcIds(int... npcIds) {
        this.npcIds = npcIds;
        return this;
    }

    public Action setObjectIds(int... objectIds) {
        this.objectIds = objectIds;
        return this;
    }

    public Action setActions(String...actions){
        this.actions = actions;
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
