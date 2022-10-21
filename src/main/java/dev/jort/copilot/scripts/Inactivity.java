package dev.jort.copilot.scripts;

import dev.jort.copilot.other.IdHolder;
import dev.jort.copilot.other.Script;
import net.runelite.api.Client;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Inactivity extends Script {
    @Override
    public void onLoop() {
        handleAction();
        handleAlert();
    }

    public void handleAction() {
        if (tracker.isAnimating() || tracker.isWalking() || tracker.hasRecentlyClicked()) {
            action = waitAction;
            return;
        }
        action = new IdHolder().setName("Resume activity");
    }

    public void handleAlert() {
        alert.handleAlert(!action.equals(waitAction));
    }
}
