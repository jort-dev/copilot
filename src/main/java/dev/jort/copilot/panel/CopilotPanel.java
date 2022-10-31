package dev.jort.copilot.panel;

import dev.jort.copilot.CopilotPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class CopilotPanel extends PluginPanel {

    public CopilotPlugin plugin;

    private final FixedWidthPanel questListWrapper = new FixedWidthPanel();
    private final JScrollPane scrollableContainer;
    private final FixedWidthPanel questListPanel = new FixedWidthPanel();

    private static final ImageIcon DISCORD_ICON;
    private static final ImageIcon GITHUB_ICON;
    private static final ImageIcon PATREON_ICON;
    private static final ImageIcon SETTINGS_ICON;

    static {
        DISCORD_ICON = Icon.DISCORD.getIcon(img -> ImageUtil.resizeImage(img, 16, 16));
        GITHUB_ICON = Icon.GITHUB.getIcon(img -> ImageUtil.resizeImage(img, 16, 16));
        PATREON_ICON = Icon.PATREON.getIcon(img -> ImageUtil.resizeImage(img, 16, 16));
        SETTINGS_ICON = Icon.SETTINGS.getIcon(img -> ImageUtil.resizeImage(img, 16, 16));
    }

    public CopilotPanel(CopilotPlugin plugin) {
        super(false);
        this.plugin = plugin;

        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setLayout(new BorderLayout());

        /* Setup overview panel */
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        titlePanel.setLayout(new BorderLayout());

        JLabel title = new JLabel();
        title.setText("Jort's Copilot");
        title.setForeground(Color.WHITE);
        titlePanel.add(title, BorderLayout.WEST);

        // Options
        final JPanel viewControls = new JPanel(new GridLayout(1, 3, 10, 0));
        viewControls.setBackground(ColorScheme.DARK_GRAY_COLOR);

        // GitHub button
        JButton githubBtn = new JButton();
        SwingUtil.removeButtonDecorations(githubBtn);
        githubBtn.setIcon(GITHUB_ICON);
        githubBtn.setToolTipText("Report issues or contribute on GitHub");
        githubBtn.setBackground(ColorScheme.DARK_GRAY_COLOR);
        githubBtn.setUI(new BasicButtonUI());
        githubBtn.addActionListener((ev) -> LinkBrowser.browse("https://github.com/jort-dev/copilot"));
        githubBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                githubBtn.setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                githubBtn.setBackground(ColorScheme.DARK_GRAY_COLOR);
            }
        });
        viewControls.add(githubBtn);


        titlePanel.add(viewControls, BorderLayout.EAST);

        JPanel introDetailsPanel = new JPanel();
        introDetailsPanel.setLayout(new BorderLayout());
        introDetailsPanel.add(titlePanel, BorderLayout.NORTH);
        add(introDetailsPanel, BorderLayout.NORTH);


        questListWrapper.setLayout(new BorderLayout());
        questListWrapper.add(questListPanel, BorderLayout.NORTH);

        scrollableContainer = new JScrollPane(questListWrapper);
        scrollableContainer.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollableContainer, BorderLayout.CENTER);
    }
}
