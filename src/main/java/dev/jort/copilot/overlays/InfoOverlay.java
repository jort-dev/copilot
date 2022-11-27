package dev.jort.copilot.overlays;

import dev.jort.copilot.CopilotConfig;
import dev.jort.copilot.CopilotPlugin;
import dev.jort.copilot.helpers.*;
import dev.jort.copilot.scripts.GiantsFoundry;
import dev.jort.copilot.scripts.Woodcutting;
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
import java.util.Arrays;

@Slf4j
@Singleton
public class InfoOverlay extends OverlayPanel implements CopilotOverlay {

    Client client;

    @Inject
    CopilotPlugin main;

    @Inject
    CopilotConfig config;

    @Inject
    GiantsFoundry giantsFoundry;

    @Inject
    Widgets widgets;
    @Inject
    Tracker tracker;
    @Inject
    Players players;
    @Inject
    Combat combat;
    @Inject
    WidgetOverlay widgetOverlay;
    @Inject
    CopilotOverlayUtil copilotOverlayUtil;
    @Inject
    Woodcutting woodcutting;

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
        giantsFoundry.onPaint(panelComponent);
        renderDebugText();
        panelComponent.setPreferredSize(new Dimension(300, 100));
        return super.render(graphics);
    }

    public void renderScriptText() {
        String scriptName = "Running";
        if (main.getCurrentRunningScript() != null) {
            String step = main.getCurrentRunningScript().getAction().getName();
            scriptName = main.getCurrentRunningScript().getClass().getSimpleName();
            panelComponent.getChildren().add(LineComponent.builder()
                    .right(step)
                    .rightFont(FontManager.getRunescapeBoldFont())
                    .build());
        }

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Jort's Copilot")
                .right(scriptName)
                .build());
    }

    public void draw(Object left, Object right) {
        panelComponent.getChildren().add(LineComponent.builder().left(left.toString()).right(right.toString()).build());
    }


    public void renderDebugText() {
        if (!config.debug()) {
            return;
        }
        draw("Last ID", tracker.getLastClickedId());
        draw("Alert needed", woodcutting.isAlertNeeded());
//        draw("Bank open", woodcutting.bank.isOpen());
        draw("Walking", tracker.isWalking());
        draw("Walking1000", tracker.isWalking(1000));
        draw("Animation", tracker.getAnimation());
        draw("LocalLocation: ", players.me().getLocalLocation());
        draw("Animating: ", tracker.isAnimating());
        draw("Action: ", woodcutting.action.getName());
        draw("Ids: ", Arrays.toString(woodcutting.action.getGameObjectIds()));
        try {
            draw("Goal correct", woodcutting.isWalkingToCorrectGoal());
        } catch (NullPointerException ignored) {
        }

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
