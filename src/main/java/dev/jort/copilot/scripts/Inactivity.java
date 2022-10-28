package dev.jort.copilot.scripts;

import dev.jort.copilot.dtos.IdHolder;
import dev.jort.copilot.dtos.Run;
import dev.jort.copilot.other.Script;

import javax.inject.Singleton;

@Singleton
public class Inactivity extends Script {
    @Override
    public int onLoop() {
        if (!config.inactivityAlert()) {
            return Run.DONE;
        }
        handleAction();
        handleAlert();
        return Run.AGAIN;
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
