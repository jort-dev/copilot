package dev.jort.copilot.helpers;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

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


}
