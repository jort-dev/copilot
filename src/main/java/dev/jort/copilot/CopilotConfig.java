package dev.jort.copilot;

import net.runelite.api.SoundEffectVolume;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("copilot")
public interface CopilotConfig extends Config
{
	int VOLUME_MAX = SoundEffectVolume.HIGH;
	@Range(
			max = VOLUME_MAX
	)
	@ConfigItem(
			keyName = "alertVolume",
			name = "Alert volume",
			description = "Configures the volume the alerts."
	)
	default int tickVolume()
	{
		return SoundEffectVolume.LOW;
	}

	@Alpha
	@ConfigItem(
			keyName = "overlayColor",
			name = "Notification color",
			description = "Set the notification overlay color",
			position = 1
	)
	default Color overlayColor() {
		return new Color(1.0f, 0.0f, 0.0f, 0.5f);
	}

	String sectionName = "Selected skills";

	@ConfigItem(
			keyName = "Barbarian",
			name = "Cooking",
			description = "Causes notifications when the player is not actively cooking",
			position = 21,
			section = sectionName
	)
	default boolean cooking() {
		return false;
	}


	@ConfigItem(
			keyName = "CRAFTING",
			name = "Crafting",
			description = "Causes notifications when the player is not actively crafting",
			position = 22,
			section = sectionName
	)
	default boolean crafting() {
		return false;
	}

}
