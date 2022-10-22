package dev.jort.copilot.scripts.easygiantsfoundry.enums;

import dev.jort.copilot.helpers.GiantsFoundry;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Stage
{


	TRIP_HAMMER("Hammer", Heat.HIGH, 20, -25, GiantsFoundry.TRIP_HAMMER),
	GRINDSTONE("Grind", Heat.MED, 10, 15, GiantsFoundry.GRINDSTONE),
	POLISHING_WHEEL("Polish", Heat.LOW, 10, -17, GiantsFoundry.POLISHING_WHEEL);


	private final String name;
	private final Heat heat;
	private final int progressPerAction;
	private final int heatChange;
	private final int objectId;
}
