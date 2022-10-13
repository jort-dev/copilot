package dev.jort.copilot.overlays;

import dev.jort.copilot.CopilotPlugin;
import dev.jort.copilot.Tracker;
import dev.jort.copilot.scripts.WillowsDraynor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Slf4j
@Singleton
public class InfoOverlay extends OverlayPanel {

    Client client;

    @Inject
    CopilotPlugin main;


    @Inject
    Tracker tracker;

    @Inject
    WillowsDraynor willowsDraynor;

    @Inject
    NotificationOverlay notificationOverlay;

    @Inject
    public InfoOverlay(Client client) {
        this.client = client;
        setPosition(OverlayPosition.TOP_LEFT);
        setResizable(true);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        String text = "Initializing";
        if (main.getRunningScript() != null) {
            text = main.getRunningScript().getAction().getHint();
        }
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Jort's Copilot:")
                .leftFont(FontManager.getRunescapeBoldFont())
                .right(text)
                .build());

        panelComponent.setPreferredSize(new Dimension(300, 100));

        return super.render(graphics);
    }

}
