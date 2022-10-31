package dev.jort.copilot.panel;


import dev.jort.copilot.CopilotPlugin;
import net.runelite.client.util.ImageUtil;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.function.UnaryOperator;

public enum Icon {
    CLOSE("/close.png"),
    COLLAPSED("/collapsed.png"),
    DISCORD("/discord.png"),
    PATREON("/patreon.png"),
    GITHUB("/github.png"),
    SETTINGS("/settings_icon.png"),
    EXPANDED("/expanded.png"),
    ICON_BACKGROUND("/icon_background.png"),
    INFO_ICON("/info_icon.png"),
    QUEST_ICON("/quest_icon.png"),
    COPILOT_ICON("/copilot_icon.png"),
    QUEST_STEP_ARROW("/quest_step_arrow.png"),
    QUEST_STEP_ARROW_45("/quest_step_arrow_45.png"),
    QUEST_STEP_ARROW_90("/quest_step_arrow_90.png"),
    QUEST_STEP_ARROW_135("/quest_step_arrow_135.png"),
    QUEST_STEP_ARROW_180("/quest_step_arrow_180.png"),
    QUEST_STEP_ARROW_225("/quest_step_arrow_225.png"),
    QUEST_STEP_ARROW_270("/quest_step_arrow_270.png"),
    QUEST_STEP_ARROW_315("/quest_step_arrow_315.png"),
    START("/start.png"),
    ;

    private final String file;

    Icon(String file) {
        this.file = file;
    }

    /**
     * Get the raw {@link BufferedImage} of this icon.
     *
     * @return {@link BufferedImage} of the icon
     */
    public BufferedImage getImage() {
        return ImageUtil.loadImageResource(CopilotPlugin.class, file);
    }

    /**
     * @return the {@link ImageIcon} with no modifications. Equivalent to {@code getIcon(UnaryOperator.identity())}
     */
    public ImageIcon getIcon() {
        return getIcon(UnaryOperator.identity());
    }

    /**
     * Return this icon.
     * <br>
     * The {@link UnaryOperator} is applied to the {@link BufferedImage}. The {@link ImageIcon}
     * is then created using that modified image.
     *
     * @param func the {@link UnaryOperator} to apply to the image
     * @return the modified {@link ImageIcon}
     */
    public ImageIcon getIcon(@Nonnull UnaryOperator<BufferedImage> func) {
        BufferedImage img = func.apply(getImage());
        return new ImageIcon(img);
    }
}
