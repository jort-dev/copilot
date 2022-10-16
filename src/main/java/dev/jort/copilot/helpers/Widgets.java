package dev.jort.copilot.helpers;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class Widgets {

    @Inject
    Client client;

    public Widget getMakeWidget() {
        return client.getWidget(270, 14);
    }

    public boolean isMakeWidgetVisible() {
        Widget widget = getMakeWidget();
        return widget != null && !widget.isHidden();
    }

    public Widget findWidgetWithName(Widget parentWidget, String containsName) {
        if (parentWidget == null) {
            log.warn("No parent widget");
            return null;
        }
        Widget[] childWidgets = parentWidget.getStaticChildren();
        if (childWidgets == null) {
            log.warn("No child widgets");
        }
        for (Widget child : parentWidget.getChildren()) {
            if (child.getName().toLowerCase().contains(containsName.toLowerCase())) {
                return child;
            }
            log.info(child.getName() + " does not contain " + containsName);
        }
        //TODO: problem: children widgets, including static, dont return anything with getName
        return null;
    }

    public Widget getBankCloseWidget() {
        Widget bankBarWidget = client.getWidget(12, 2);
        if (bankBarWidget == null) {
            return null;
        }
        Widget closeButtonWidget = bankBarWidget.getChild(11);
        return closeButtonWidget;
    }


}
