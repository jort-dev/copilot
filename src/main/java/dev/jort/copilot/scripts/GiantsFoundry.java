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

    String lastAction = waitAction.getName();


    @Override
    public int onLoop() {
        if (!config.giantsFoundry()) {
            return Run.DONE;
        }
        action.setGameObjectIds();
        action.setSecondaryGameObjectIds();
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

    public void hoverMachine() {
        action.setSecondaryGameObjectIds(gf.getCurrentStage().getObjectId());
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
        Stage nextStage = gf.getNextStage();
        boolean machineHeatsSword = currentStage.equals(Stage.GRINDSTONE);
        boolean machineCoolsSword = currentStage.equals(Stage.TRIP_HAMMER) || currentStage.equals(Stage.POLISHING_WHEEL);
        //if there is no next stage, next stage will be NONE, which is a higher ordinal, so the next stage is 'colder'
        boolean isNextStageWarmer = currentStage.ordinal() > nextStage.ordinal();
        boolean isFinalStage = nextStage.equals(Stage.NONE);


        if (gf.isOperatingMachine()) {
            boolean hasEnoughHeatLeft = gf.getHeatLeft() >= config.giantsFoundryToolBuffer();
            if (hasEnoughHeatLeft) {
                debug("operate -> machine");
                clickMachine();
                if (canFinishStageWithHeat) {
                    if (isNextStageWarmer) {
                        hoverLava();
                    }
                    else {
                        hoverWater();
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
            else {
                hoverMachine();
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

                    if (hasCollectedEnoughTemperature) {
                        if (machineHeatsSword) {
                            hoverWater();
                        }
                        else {
                            hoverLava();
                        }
                    }
                    else if (canFinishStageWithHeat) {
                        if (isNextStageWarmer) {
                            hoverLava();
                        }
                        else {
                            hoverWater();
                        }
                    }
                }
                else {
                    //gather more heat
                    if (machineHeatsSword) {
                        debug("in -> water");
                        clickWater();
                        hoverMachine();
                    }
                    else {
                        debug("in -> lava");
                        clickLava();
                        hoverMachine();
                    }
                }
            }
            else if (range.equals(Range.ABOVE)) {
                debug("above -> water");
                clickWater();
                hoverMachine();
            }
            else if (range.equals(Range.BELOW)) {
                debug("below -> lava");
                clickLava();
                hoverMachine();
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
