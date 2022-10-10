package dev.jort.copilot;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class CopilotNotificationOverlay extends Overlay {


    @Inject
    Client client;

    private boolean enabled = false;

    public CopilotNotificationOverlay() {

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if(!enabled){
            return null;
        }
        Color color = new Color(1.0f, 0.0f, 0.0f, 0.1f);
        graphics.setColor(color);
        graphics.fill(new Rectangle(client.getCanvas().getSize()));
        return null;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
