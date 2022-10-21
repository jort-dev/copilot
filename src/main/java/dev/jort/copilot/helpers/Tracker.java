package dev.jort.copilot.helpers;


import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.StringJoiner;

@Slf4j
@Singleton
/*
TODO: add hasreceivedxp(long ms)
 */
public class Tracker {

    @Inject
    Client client;

    //animation
    private long lastAnimationTime = 0;

    //walking
    private long lastWalkingTime = 0;
    private LocalPoint lastPlayerLocation;

    //menu item
    private String lastClickedMenuOption = "";

    private String lastClickedMenuTarget = "";
    private int lastClickedId = -1;

    private long lastMenuClickTime = 0;


    private boolean isAnimatingNow() {
        return getAnimation() > -1;
    }

    public boolean hasAnimated(int ms) {
        //return true if we have animated the last X ms
        if (System.currentTimeMillis() - (long) ms > lastAnimationTime) {
            return false;
        }
        return true;
    }

    public boolean isAnimating() {
        return hasAnimated(1000);
    }

    public int getAnimation() {
        Player player = client.getLocalPlayer();
        if (player == null) {
            return -2;
        }
        return player.getAnimation();
    }

    public boolean isWalking() {
        return isWalking(200);
    }

    public boolean isWalking(int ms) {
        if (System.currentTimeMillis() - (long) ms > lastWalkingTime) {
            return false;
        }
        return true;
    }

    @Subscribe
    public void onGameTick(GameTick ignored) {
        if (isAnimatingNow()) {
            lastAnimationTime = System.currentTimeMillis();
        }

        Player player = client.getLocalPlayer();
        if (player != null) {
            LocalPoint playerLocation = player.getLocalLocation();
            if (!playerLocation.equals(lastPlayerLocation)) {
                lastWalkingTime = System.currentTimeMillis();
            }
            lastPlayerLocation = playerLocation;
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        StringJoiner stringJoiner = new StringJoiner(", ");
        int widgetid = -1;
        if (event.getWidget() != null) {
            widgetid = event.getWidget().getId();
        }
        stringJoiner.add("Menu item clicked: ")
                .add("id=" + event.getId())
                .add("itemid=" + event.getItemId())
                .add("widgetid=" + widgetid)
                .add("target=" + event.getMenuTarget())
                .add("option=" + event.getMenuOption());
        log.info(stringJoiner.toString());
        if (event.getMenuOption().equals("Walk here")) {
            lastWalkingTime = System.currentTimeMillis();
        }
        lastMenuClickTime = System.currentTimeMillis();
        lastClickedMenuOption = event.getMenuOption();
        lastClickedMenuTarget = event.getMenuTarget();
        lastClickedId = event.getId();
        if (lastClickedId < 10) {
            lastClickedId = event.getItemId(); //each item is also a widget, prioritize the item id
        }
        if (lastClickedId < 10) {
            if (event.getWidget() != null) {
                lastClickedId = event.getWidget().getId();
            }
        }
    }

    public String getLastClickedMenuOption() {
        return lastClickedMenuOption;
    }

    public int getLastClickedId() {
        return lastClickedId;
    }

    public long getLastMenuClickTime() {
        return lastMenuClickTime;
    }

    public String getLastClickedMenuTarget() {
        return lastClickedMenuTarget;
    }

    public boolean isItemSelected() {
        if (!lastClickedMenuOption.equals("Use")) {
            return false;
        }
        if (lastClickedMenuTarget.contains("->")) {
            return false;
        }
        return true;
    }

    public boolean isItemSelected(String itemName) {
        if (!isItemSelected()) {
            return false;
        }
        if (!getLastClickedMenuTarget().toLowerCase().contains(itemName.toLowerCase())) {
            return false;
        }
        return true;
    }

    public boolean hasRecentlyClicked() {
        return hasRecentlyClicked(1000);
    }

    public boolean hasRecentlyClicked(int ms) {
        return client.getMouseLastPressedMillis() + ms > System.currentTimeMillis();
    }


}
