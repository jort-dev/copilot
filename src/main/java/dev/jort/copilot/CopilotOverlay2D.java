package dev.jort.copilot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Slf4j
@Singleton
public class CopilotOverlay2D extends OverlayPanel {

    private final Client client;
    @Inject
    Players players;

    @Inject
    WillowsDraynor willowsDraynor;

    @Inject
    public CopilotOverlay2D(Client client) {
        this.client = client;
        setPosition(OverlayPosition.TOP_LEFT);
        setResizable(true);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Jort's Copilot")
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left(willowsDraynor.getHint())
                .preferredSize(new Dimension(600, 100))
                .build());

        return super.render(graphics);
    }

}
