package dev.jort.copilot.overlays;

import dev.jort.copilot.CopilotConfig;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class NotificationOverlay extends Overlay implements CopilotOverlay {


    @Inject
    Client client;

    @Inject
    CopilotConfig config;

    private boolean enabled;

    public NotificationOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if(!enabled){
            return null;
        }
        graphics.setColor(config.overlayColor());
        graphics.fill(new Rectangle(client.getCanvas().getSize()));
        return null;
    }


    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void disable(){
        enabled = false;
    }

    @Override
    public void clear() {
        disable();
    }

    @Override
    public void setEnabled(boolean enable){
        if (enable){
            enable();
            return;
        }
        disable();
    }
}
