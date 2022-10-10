package dev.jort.copilot;


import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
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
        if (event.getMenuOption().equals("Walk here")) {
            lastWalkingTime = System.currentTimeMillis();
        }
        lastMenuClickTime = System.currentTimeMillis();
        lastClickedMenuOption = event.getMenuOption();
        lastClickedId = event.getId();
    }

    public String getLastClickedMenuOption() {
        return lastClickedMenuOption;
    }

    public void setLastClickedMenuOption(String lastClickedMenuOption) {
        this.lastClickedMenuOption = lastClickedMenuOption;
    }

    public int getLastClickedId() {
        return lastClickedId;
    }

    public void setLastClickedId(int lastClickedId) {
        this.lastClickedId = lastClickedId;
    }


}