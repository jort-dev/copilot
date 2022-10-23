package dev.jort.copilot.overlays;

import dev.jort.copilot.CopilotConfig;
import dev.jort.copilot.CopilotPlugin;
import dev.jort.copilot.helpers.Combat;
import dev.jort.copilot.helpers.GiantsFoundryHelper;
import dev.jort.copilot.helpers.Tracker;
import dev.jort.copilot.helpers.Widgets;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
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

    @Inject
    CopilotConfig config;

    @Inject
    Widgets widgets;
    @Inject
    Tracker tracker;
    @Inject
    Combat combat;
    @Inject
    WidgetOverlay widgetOverlay;

    @Inject
    GiantsFoundryHelper giantsFoundryHelper;

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
        renderScriptText();
        renderGiantsFoundryText();
        panelComponent.setPreferredSize(new Dimension(200, 100));
        return null;
    }

    public void renderScriptText() {
        if (main.getCurrentRunningScript() == null) {
            return;
        }

        String step = main.getCurrentRunningScript().getAction().getName();
        String scriptName = main.getCurrentRunningScript().getClass().getSimpleName();
        panelComponent.getChildren().add(LineComponent.builder()
                .right(step)
                .rightFont(FontManager.getRunescapeBoldFont())
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Jort's Copilot")
                .right(scriptName)
                .build());


    }

    public void renderGiantsFoundryText() {
        if (!config.giantsFoundry()) {
            return;
        }

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Heat:")
                .right(giantsFoundryHelper.getCurrentHeat().getName() + " (" + giantsFoundryHelper.getHeatAmount() + ")")
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Actions left:")
                .right(giantsFoundryHelper.getActionsLeftInStage() + "")
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Heat left:")
                .right(giantsFoundryHelper.getHeatLeft() + "")
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Tool to use:")
                .right(giantsFoundryHelper.getCurrentStage().getName() + "")
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Should be busy with: ")
                .right(giantsFoundryHelper.determineAction().name() + "")
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Busy with:")
                .right(giantsFoundryHelper.getOperatingMachine().name() + "")
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Animation:")
                .right(tracker.getAnimation() + "")
                .build());
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
