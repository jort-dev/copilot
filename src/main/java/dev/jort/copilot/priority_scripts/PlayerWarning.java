package dev.jort.copilot.priority_scripts;

import dev.jort.copilot.dtos.Run;
import dev.jort.copilot.other.PriorityScript;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.WorldService;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;
import net.runelite.http.api.worlds.WorldType;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.EnumSet;
import java.util.List;


@Slf4j
@Singleton
public class PlayerWarning extends PriorityScript {
    @Override
    public int onLoop() {
        if (!config.playerAlert()) {
            //plugin disabled
            return Run.DONE;
        }
//        if (!combat.isInWilderness()) {
//            //we are not in wilderness
//            return Run.DONE;
//        }
        int wildyLevel = combat.getWildernessLevel();
        int ourCb = combat.getCombatLevel();
        int minCb = ourCb - wildyLevel;
        int maxCb = ourCb + wildyLevel;
        log.info("Wildy lvl=" + wildyLevel + ", cb=" + ourCb + ", min=" + minCb + ", max=" + maxCb);
        for (Player player : players.getPlayers()) {
            if (player.equals(players.me())) {
                //me
                continue;
            }
            int cb = player.getCombatLevel();
            if (cb < minCb || cb > maxCb) {
                //player can't attack us
                continue;
            }
            alert.playImportantAlert();
            String msg = "Danger: " + player.getName();
            alert.systemAlert("Danger: " + msg);
            chat.send("Danger: " + msg);
            hop(false);
            return Run.AGAIN;
        }
        return Run.DONE;
    }

    @Inject
    private WorldService worldService;
    @Inject
    private ChatMessageManager chatMessageManager;
    @Inject
    ClientThread clientThread;

    private net.runelite.api.World quickHopTargetWorld;

    private static final int MAX_PLAYER_COUNT = 1950;

    private void hop(boolean previous) {
        WorldResult worldResult = worldService.getWorlds();
        if (worldResult == null || client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        World currentWorld = worldResult.findWorld(client.getWorld());

        if (currentWorld == null) {
            return;
        }

        EnumSet<WorldType> currentWorldTypes = currentWorld.getTypes().clone();
        // Make it so you always hop out of PVP and high risk worlds
        currentWorldTypes.remove(WorldType.PVP);
        currentWorldTypes.remove(WorldType.HIGH_RISK);
        // Don't regard these worlds as a type that must be hopped between
        currentWorldTypes.remove(WorldType.BOUNTY);
        currentWorldTypes.remove(WorldType.SKILL_TOTAL);
        currentWorldTypes.remove(WorldType.LAST_MAN_STANDING);

        List<World> worlds = worldResult.getWorlds();

        int worldIdx = worlds.indexOf(currentWorld);
        int totalLevel = client.getTotalLevel();

        World world;
        do {
			/*
				Get the previous or next world in the list,
				starting over at the other end of the list
				if there are no more elements in the
				current direction of iteration.
			 */
            if (previous) {
                worldIdx--;

                if (worldIdx < 0) {
                    worldIdx = worlds.size() - 1;
                }
            }
            else {
                worldIdx++;

                if (worldIdx >= worlds.size()) {
                    worldIdx = 0;
                }
            }

            world = worlds.get(worldIdx);

            EnumSet<WorldType> types = world.getTypes().clone();

            types.remove(WorldType.BOUNTY);
            // Treat LMS world like casual world
            types.remove(WorldType.LAST_MAN_STANDING);

            if (types.contains(WorldType.SKILL_TOTAL)) {
                try {
                    int totalRequirement = Integer.parseInt(world.getActivity().substring(0, world.getActivity().indexOf(" ")));

                    if (totalLevel >= totalRequirement) {
                        types.remove(WorldType.SKILL_TOTAL);
                    }
                } catch (NumberFormatException ex) {
                    log.warn("Failed to parse total level requirement for target world", ex);
                }
            }

            // Avoid switching to near-max population worlds, as it will refuse to allow the hop if the world is full
            if (world.getPlayers() >= MAX_PLAYER_COUNT) {
                continue;
            }

            if (world.getPlayers() < 0) {
                // offline world
                continue;
            }

            // Break out if we've found a good world to hop to
            if (currentWorldTypes.equals(types)) {
                break;
            }
        }
        while (world != currentWorld);

        if (world == currentWorld) {
            String chatMessage = new ChatMessageBuilder()
                    .append(ChatColorType.NORMAL)
                    .append("Couldn't find a world to quick-hop to.")
                    .build();

            chatMessageManager.queue(QueuedMessage.builder()
                    .type(ChatMessageType.CONSOLE)
                    .runeLiteFormattedMessage(chatMessage)
                    .build());
        }
        else {
            hop(world.getId());
        }
    }

    private void hop(int worldId) {
        WorldResult worldResult = worldService.getWorlds();
        // Don't try to hop if the world doesn't exist
        World world = worldResult.findWorld(worldId);
        if (world == null) {
            return;
        }

        hop(world);
    }

    void hopTo(World world) {
        // this is called from the panel, on edt
        clientThread.invoke(() -> hop(world));
    }

    private void hop(World world) {
        assert client.isClientThread();

        final net.runelite.api.World rsWorld = client.createWorld();
        rsWorld.setActivity(world.getActivity());
        rsWorld.setAddress(world.getAddress());
        rsWorld.setId(world.getId());
        rsWorld.setPlayerCount(world.getPlayers());
        rsWorld.setLocation(world.getLocation());
        rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));

        if (client.getGameState() == GameState.LOGIN_SCREEN) {
            // on the login screen we can just change the world by ourselves
            client.changeWorld(rsWorld);
            return;
        }

        String chatMessage = new ChatMessageBuilder()
                .append(ChatColorType.NORMAL)
                .append("Quick-hopping to World ")
                .append(ChatColorType.HIGHLIGHT)
                .append(Integer.toString(world.getId()))
                .append(ChatColorType.NORMAL)
                .append("..")
                .build();

        chatMessageManager
                .queue(QueuedMessage.builder()
                        .type(ChatMessageType.CONSOLE)
                        .runeLiteFormattedMessage(chatMessage)
                        .build());

        quickHopTargetWorld = rsWorld;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (quickHopTargetWorld == null) {
            return;
        }

        if (client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) == null) {
            client.openWorldHopper();
        }
        else {
            client.hopToWorld(quickHopTargetWorld);
            resetQuickHopper();
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.GAMEMESSAGE) {
            return;
        }

        if (event.getMessage().equals("Please finish what you're doing before using the World Switcher.")) {
            resetQuickHopper();
        }
    }

    private void resetQuickHopper() {
        quickHopTargetWorld = null;
    }

}
