package dev.jort.copilot.scripts.easygiantsfoundry;

import dev.jort.copilot.scripts.easygiantsfoundry.enums.Heat;
import dev.jort.copilot.scripts.easygiantsfoundry.enums.Stage;
import net.runelite.api.Client;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class FoundryOverlay2D extends OverlayPanel
{
	private static final int REGION_ID = 13491;
	private final Client client;
	private final EasyGiantsFoundryPlugin plugin;
	private final EasyGiantsFoundryState state;
	private final EasyGiantsFoundryHelper helper;
	private final EasyGiantsFoundryConfig config;

	@Inject
	private FoundryOverlay2D(Client client, EasyGiantsFoundryPlugin plugin, EasyGiantsFoundryState state, EasyGiantsFoundryHelper helper, EasyGiantsFoundryConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.state = state;
		this.helper = helper;
		this.config = config;
		this.setPosition(OverlayPosition.BOTTOM_LEFT);
	}

	private Color getHeatColor(int actions, int heat)
	{
		if (heat >= actions)
		{
			return ColorScheme.PROGRESS_COMPLETE_COLOR;
		}

		if (heat > 0)
		{
			return ColorScheme.PROGRESS_INPROGRESS_COLOR;
		}

		return ColorScheme.PROGRESS_ERROR_COLOR;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (client.getLocalPlayer().getWorldLocation().getRegionID() != REGION_ID)
		{
			return null;
		}
		boolean swordPickedUp = state.isEnabled() && state.getCurrentStage() != null;

		if (config.drawTitle())
		{
			panelComponent.getChildren().add(TitleComponent.builder().text("Easy Giant's Foundry").build());
		}

		if (swordPickedUp) {
			Heat heat = state.getCurrentHeat();
			Stage stage = state.getCurrentStage();

			if (config.drawHeatInfo())
			{
				panelComponent.getChildren().add(
					LineComponent.builder().left("Heat").right(heat.getName() + " (" + state.getHeatAmount() / 10 + "%)").rightColor(heat.getColor()).build()
				);
			}
			if (config.drawStageInfo())
			{
				panelComponent.getChildren().add(
					LineComponent.builder().left("Stage").right(stage.getName() + " (" + state.getProgressAmount() / 10 + "%)").rightColor(stage.getHeat().getColor()).build()
				);
			}

			int actionsLeft = helper.getActionsLeftInStage();
			int heatLeft = helper.getActionsForHeatLevel();

			if (config.drawActionsLeft())
			{
				panelComponent.getChildren().add(
					LineComponent.builder().left("Actions left").right(actionsLeft + "").build()
				);
			}
			if (config.drawHeatLeft())
			{
				panelComponent.getChildren().add(
					LineComponent.builder().left("Heat left").right(heatLeft + "").rightColor(getHeatColor(actionsLeft, heatLeft)).build()
				);
			}
		}

		int points = plugin.getPointsTracker().getShopPoints();
		if (config.drawShopPoints())
		{
			panelComponent.getChildren().add(
					LineComponent.builder().left("Reputation").right(points + "").build()
			);
		}

		return super.render(graphics);
	}
}
