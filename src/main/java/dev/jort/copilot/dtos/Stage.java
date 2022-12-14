package dev.jort.copilot.dtos;


import dev.jort.copilot.helpers.GiantsFoundryHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Stage {
    TRIP_HAMMER("Hammer", Heat.HIGH, 20, -25, GiantsFoundryHelper.TRIP_HAMMER),
    GRINDSTONE("Grind", Heat.MED, 10, 15, GiantsFoundryHelper.GRINDSTONE),
    POLISHING_WHEEL("Polish", Heat.LOW, 10, -17, GiantsFoundryHelper.POLISHING_WHEEL),
    NONE("None", Heat.NONE, -1, -1, -1);


    private final String name;
    private final Heat heat;
    private final int progressPerAction;
    private final int heatChange;
    private final int objectId;
}
