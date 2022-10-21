package dev.jort.copilot.priority_scripts;

import dev.jort.copilot.other.IdHolder;
import dev.jort.copilot.other.PriorityScript;

import javax.inject.Singleton;

/*
Reminder to use special attack.
 */
@Singleton
public class SpecialAttack extends PriorityScript {
    @Override
    public void onLoop() {
        if (needsToRun()) {
            action = new IdHolder()
                    .setName("Click special attack")
                    .setWidgets(combat.getSpecialAttackOrbWidget());
        } else {
            action = waitAction;
        }
    }

    @Override
    public boolean needsToRun() {
        if(!config.specialAttackAlert()){
            return false;
        }
        return combat.getSpecialAttackPercentage() == 100;
    }
}
