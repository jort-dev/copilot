package dev.jort.copilot.helpers;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

@Slf4j
@Singleton
public class Widgets {

    @Inject
    Client client;

    @Inject
    ClientThread clientThread;

    private boolean hidingWidgets = false;


    public Widget getMakeWidget() {
        return client.getWidget(270, 14);
    }

    public boolean isMakeWidgetVisible() {
        Widget widget = getMakeWidget();
        return widget != null && !widget.isHidden();
    }

    public Widget getWidgetWithText(Widget parentWidget, String containsName) {
        if (parentWidget == null) {
            return null;
        }
        if (parentWidget.getStaticChildren() == null) {
            return null;
        }
        for (Widget child : parentWidget.getStaticChildren()) {

            //check name for text
            if (child.getName().toLowerCase().contains(containsName.toLowerCase())) {
                return child;
            }

            //check text for text
            if (child.getText().toLowerCase().contains(containsName.toLowerCase())) {
                return child;
            }

            //check actions for text
            if (child.getActions() == null) {
                continue;
            }
            for (String action : child.getActions()) {
                if (action.toLowerCase().contains(containsName.toLowerCase())) {
                    return child;
                }
            }
        }
        return null;
    }

    public String widgetToString(Widget widget) {
        return "Widget[id=" + widget.getId() + ", text=" + widget.getText() +
                ", name=" + widget.getName() + ", actions=" + Arrays.toString(widget.getActions()) + "]";
    }

    public Widget getBankCloseWidget() {
        Widget bankBarWidget = client.getWidget(12, 2);
        if (bankBarWidget == null) {
            return null;
        }
        Widget closeButtonWidget = bankBarWidget.getChild(11);
        return closeButtonWidget;
    }

    protected void hideWidgetChildren(Widget root, boolean hide) {
        // The normal GetChildren function seems to always return 0 so we get all the different types
        // of other children instead and merge them into one array
        Widget[] rootDynamicChildren = root.getDynamicChildren();
        Widget[] rootNestedChildren = root.getNestedChildren();
        Widget[] rootStaticChildren = root.getStaticChildren();

        Widget[] rootChildren = new Widget[rootDynamicChildren.length + rootNestedChildren.length + rootStaticChildren.length];
        System.arraycopy(rootDynamicChildren, 0, rootChildren, 0, rootDynamicChildren.length);
        System.arraycopy(rootNestedChildren, 0, rootChildren, rootDynamicChildren.length, rootNestedChildren.length);
        System.arraycopy(rootStaticChildren, 0, rootChildren, rootDynamicChildren.length + rootNestedChildren.length, rootStaticChildren.length);

        for (Widget w : rootChildren) {
            if (w == null) {
                continue;
            }
            // hiding the widget with content type 1337 prevents the game from rendering so let's not do that
            if (w.getContentType() == 1337) {
                continue;
            }
            w.setHidden(hide);
        }
    }


    /*
    Yeeted from: https://github.com/PresNL/hide-widgets/blob/master/src/main/java/com/hidewidgets/HideWidgetsPlugin.java
     */
    public void hideWidgets(boolean hide) {
        hidingWidgets = hide;
        // hiding in fixed mode does not actually hide stuff and might break stuff so let's not do that
        if (hide && !client.isResized()) {
            hideWidgets(false);
            return;
        }
        clientThread.invokeLater(() ->
        {
            // modern resizeable
            Widget root = client.getWidget(164, 65);
            if (root != null)
                hideWidgetChildren(root, hide);

            // classic resizeable
            root = client.getWidget(161, 33);
            if (root != null)
                hideWidgetChildren(root, hide);

            // fix zoom modern resizeable
            // zoom is child widget with the id 2 but if the parent is hidden the child is too
            Widget zoom = client.getWidget(161, 90);
            if (zoom != null)
                zoom.setHidden(false);

            // fix zoom classic resizeable
            // zoom is child widget with the id 2 but if the parent is hidden the child is too
            zoom = client.getWidget(164, 87);
            if (zoom != null)
                zoom.setHidden(false);
        });
    }

    public void onScriptPostFired(ScriptPostFired scriptPostFired) {
        // 903 seems to get called when something opens the inventory like when banking or when opening stores
        if (scriptPostFired.getScriptId() == ScriptID.TOPLEVEL_REDRAW || scriptPostFired.getScriptId() == 903) {
            if (hidingWidgets) {
                hideWidgets(true);
            }
        }
    }
}
