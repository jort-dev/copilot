package dev.jort.copilot.priority_scripts;

import dev.jort.copilot.dtos.IdHolder;
import dev.jort.copilot.dtos.Run;
import dev.jort.copilot.other.PriorityScript;

import javax.inject.Singleton;

/*
Reminder to use special attack.
 */
@Singleton
public class SpecialAttack extends PriorityScript {
    @Override
    public int onLoop() {
        if (!needsToRun()) {
            action = waitAction;
            return Run.DONE;
        }
        action = new IdHolder()
                .setName("Click special attack")
                .setWidgets(combat.getSpecialAttackOrbWidget());
        alert.handleAlert(true);
        return Run.AGAIN;
    }

    public boolean needsToRun() {
        if (!config.specialAttackAlert()) {
            return false;
        }
        return combat.getSpecialAttackPercentage() == 100;
    }
}
