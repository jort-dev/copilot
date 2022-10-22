package dev.jort.copilot.overlays;

import dev.jort.copilot.CopilotPlugin;
import dev.jort.copilot.helpers.Combat;
import dev.jort.copilot.helpers.Tracker;
import dev.jort.copilot.helpers.Widgets;
import dev.jort.copilot.other.Util;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.Arrays;

@Slf4j
@Singleton
public class InfoOverlay extends OverlayPanel implements CopilotOverlay {

    Client client;

    @Inject
    CopilotPlugin main;

    @Inject
    Widgets widgets;
    @Inject
    Tracker tracker;
    @Inject
    Combat combat;
    @Inject
    WidgetOverlay widgetOverlay;

    private boolean enabled = true;

    @Inject
    public InfoOverlay(Client client) {
        this.client = client;
        setPosition(OverlayPosition.TOP_LEFT);
        setResizable(true);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.LOW);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!enabled) {
            return null;
        }
        String step = "Step";
        String scriptName = "Script";
        if (main.getRunningScript() != null) {
            step = main.getRunningScript().getAction().getName();
            scriptName = main.getRunningScript().getClass().getSimpleName();
        }
        panelComponent.getChildren().add(LineComponent.builder()
                .left(step)
                .leftFont(FontManager.getRunescapeBoldFont())
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left(scriptName)
                .right("Jort's Copilot")
                .build());

//        if(main.getRunningScript() != null){
//            panelComponent.getChildren().add(LineComponent.builder()
//                    .left(Arrays.toString(widgetOverlay.getWidgetsToHighlight()))
//                    .right(Arrays.toString(main.getRunningScript().getAction().getWidgets()))
//                    .build());
//        }

        panelComponent.setPreferredSize(new Dimension(200, 100));

        return super.render(graphics);
    }

    @Override
    public void clear() {
//        disable();
    }

    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void disable() {
        enabled = false;
    }

    @Override
    public void setEnabled(boolean enable) {
        if (enable) {
            enable();
            return;
        }
        disable();
    }

}
