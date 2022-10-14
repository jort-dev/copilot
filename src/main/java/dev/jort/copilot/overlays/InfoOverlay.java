package dev.jort.copilot.overlays;

import dev.jort.copilot.CopilotPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Slf4j
@Singleton
public class InfoOverlay extends OverlayPanel implements CopilotOverlay {

    Client client;

    @Inject
    CopilotPlugin main;

    private boolean enabled = true;

    @Inject
    public InfoOverlay(Client client) {
        this.client = client;
        setPosition(OverlayPosition.TOP_LEFT);
        setResizable(true);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!enabled) {
            return null;
        }
        String text = "Initializing";
        if (main.getRunningScript() != null) {
            text = main.getRunningScript().getAction().getHint();
        }
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Jort's Copilot")
                .leftFont(FontManager.getRunescapeBoldFont())
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left(text)
                .build());

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
