package dev.jort.copilot.scripts;

import dev.jort.copilot.dtos.Range;
import dev.jort.copilot.dtos.Run;
import dev.jort.copilot.dtos.Stage;
import dev.jort.copilot.helpers.GiantsFoundryHelper;
import dev.jort.copilot.other.Script;
import dev.jort.copilot.overlays.Painter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.util.StringJoiner;

@Slf4j
/*
Biggest jump when changing temperature with dunking
5
 */ public class GiantsFoundry extends Script implements Painter {
    @Inject
    GiantsFoundryHelper gf;


    public volatile boolean canFinishStageWithHeat;
    public volatile boolean hasEnoughHeat;
    public volatile boolean isNextStageWarmer;
    public volatile boolean machineHeatsSword;
    public volatile boolean testBoolean = false;

    String lastAction = waitAction.getName();


    @Override
    public int onLoop() {
        if (!config.giantsFoundry()) {
            return Run.DONE;
        }
        action.setGameObjectIds();
        action.setSecondaryGameObjectIds();
        determineHover();
//        determineAction();
        determineAction();

        if (!lastAction.equals(action.getName())) {
            lastAction = action.getName();
            alert.playAlternativeAlertSound();
            alert.systemAlert("Clicc");
            log.info("Action changed!");
        }

        return Run.AGAIN;
    }

    public void hoverWater() {
        action.setSecondaryGameObjectIds(GiantsFoundryHelper.WATERFALL);
    }

    public void hoverLava() {
        action.setSecondaryGameObjectIds(GiantsFoundryHelper.LAVA_POOL);
    }

    public void hoverKovac() {
        action.setSecondaryNpcIds(GiantsFoundryHelper.KOVAC);
    }


    public void determineHover() {
        Stage currentStage = gf.getCurrentStage();
        Stage nextStage = gf.getNextStage();

        //we cant determine hover without knowing the current stage
        if (currentStage.equals(Stage.NONE)) {
            return;
        }

        canFinishStageWithHeat = gf.getHeatLeft() > gf.getActionsLeftInStage();

        //if we are between machines: how much heat is required for us to click the machine instead of the temperature changer
        hasEnoughHeat = gf.getHeatLeft() > 5;


        //if there is no next stage, next stage will be NONE, which is a higher ordinal, so the next stage is 'colder'
        isNextStageWarmer = currentStage.ordinal() > nextStage.ordinal();
        boolean isFinalStage = nextStage.equals(Stage.NONE);

        machineHeatsSword = currentStage.equals(Stage.GRINDSTONE);
        testBoolean = true;
//        logg(canFinishStageWithHeat, hasEnoughHeat, isNextStageWarmer, machineHeatsSword, isFinalStage);


        if (gf.isOperatingMachine()) {
            if (canFinishStageWithHeat) {
                if (isNextStageWarmer) {
                    hoverLava();
                }
                else {
                    if (isFinalStage) {
                        hoverKovac();
                    }
                    else {
                        hoverWater();
                    }
                }
            }
            else {
                if (machineHeatsSword) {
                    hoverWater();
                }
                else {
                    hoverLava();
                }
            }
        }
        else if (gf.isModifyingTemperature()) {
            action.setSecondaryGameObjectIds(currentStage.getObjectId());
        }
        //this gets reached if we are in between actions
        else {
            //if we have enough heat, we should currently click the machine, so the next action is to change temperature
            if (hasEnoughHeat) {
                if (canFinishStageWithHeat) {
                    if (isNextStageWarmer) {
                        hoverLava();
                    }
                    else {
                        if (isFinalStage) {
                            hoverKovac();
                        }
                        else {
                            hoverWater();
                        }
                    }
                }
                else {
                    if (machineHeatsSword) {
                        hoverWater();
                    }
                    else {
                        hoverLava();
                    }
                }
            }
            //else the next action is to click the machine, because we should be changing temperature rn
            else {
                action.setSecondaryGameObjectIds(currentStage.getObjectId());
            }
        }
    }

    public void clickMachine() {
        Stage stage = gf.getCurrentStage();
        action.setName("Click " + stage.getName()).setGameObjectIds(stage.getObjectId());
    }

    public void clickLava() {
        action.setName("Click lava").setGameObjectIds(GiantsFoundryHelper.LAVA_POOL);
    }

    public void clickWater() {
        action.setName("Click water").setGameObjectIds(GiantsFoundryHelper.WATERFALL);
    }


    public void debug(Object msg) {
        log.info(msg.toString());
    }

    /*
    when done using tool and should be caching temperature - it shows wrong temperature
    when warming up for grindstone - bell rings too early and temperature can drop again for another bell ring
     */
    public void determineAction() {
        boolean canFinishStageWithHeat = gf.getHeatLeft() - config.giantsFoundryToolBuffer() >= gf.getActionsLeftInStage();
        Stage currentStage = gf.getCurrentStage();
        boolean machineHeatsSword = currentStage.equals(Stage.GRINDSTONE);
        boolean machineCoolsSword = currentStage.equals(Stage.TRIP_HAMMER) || currentStage.equals(Stage.POLISHING_WHEEL);


        if (gf.isOperatingMachine()) {
            boolean hasEnoughHeatLeft = gf.getHeatLeft() >= config.giantsFoundryToolBuffer();
            if (hasEnoughHeatLeft) {
                debug("operate -> machine");
                clickMachine();
            }
            else {
                if (machineHeatsSword) {
                    debug("operate -> water");
                    clickWater();
                }
                else if (machineCoolsSword) {
                    debug("operate -> lava");
                    clickLava();
                }
                else {
                    log.info("Current stage = " + currentStage.name() + " = WRONG");
                }
            }
        }
        //else we are either modifying temperature or between machines
        else {
            Range range = gf.getLevel();
            if (range.equals(Range.IN)) {
                int amountOfHeatToCollect = gf.getMaxHeatLeftInStage() - config.giantsFoundryTemperatureBuffer();
                boolean hasCollectedEnoughTemperature = gf.getHeatLeft() >= amountOfHeatToCollect;
                if (hasCollectedEnoughTemperature || canFinishStageWithHeat) {
                    debug("in -> machine");
                    clickMachine();
                }
                else {
                    //gather more heat
                    if (machineHeatsSword) {
                        debug("in -> water");
                        clickWater();
                    }
                    else {
                        debug("in -> lava");
                        clickLava();
                    }
                }
            }
            else if (range.equals(Range.ABOVE)) {
                debug("above -> water");
                clickWater();
            }
            else if (range.equals(Range.BELOW)) {
                debug("below -> lava");
                clickLava();
            }
            else {
                debug("else -> unknown");
            }
        }
    }

    @Override
    public void onPaint(PanelComponent p) {
        if (!config.giantsFoundry()) {
            return;
        }
        if (!config.debug()) {
            return;
        }
        //TODO: cannot debug local variables here (booleans stay false)
        panelComponent = p;

        draw("Heat:", gf.getHeatAmount() + " (" + gf.getCurrentHeat().name() + ")");
        draw("Actions left:", gf.getActionsLeftInStage());
        draw("Heat left:", gf.getHeatLeft());
        draw("Max heat left: ", gf.getMaxHeatLeftInStage());
        draw("Tool to use:", gf.getCurrentStage().getName());
        draw("Busy with:", gf.getActivity().name().toLowerCase());
        draw("Current stage:", gf.getCurrentStage().name().toLowerCase());
        draw("Next stage:", gf.getNextStage().name().toLowerCase());
        draw("Is using machine:", gf.isOperatingMachine());
        draw("Is modifying temperature:", gf.isModifyingTemperature());
        draw("Heat range level: ", gf.getLevel());
    }

    PanelComponent panelComponent;


    public void draw(Object left, Object right) {
        panelComponent.getChildren().add(LineComponent.builder().left(left.toString()).right(right.toString()).build());
    }

    public void draw(Object left) {
        draw(left, "");
    }

    public void logg(Object... args) {
        StringJoiner stringJoiner = new StringJoiner(",");
        for (Object arg : args) {
            stringJoiner.add(arg.toString());
        }
        log.info("Log: " + stringJoiner);
    }

}
