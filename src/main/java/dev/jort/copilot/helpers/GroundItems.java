package dev.jort.copilot.helpers;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import dev.jort.copilot.dtos.GroundItem;
import dev.jort.copilot.other.RuneLiteUtil;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static net.runelite.api.ItemID.COINS;

@Singleton
@Slf4j
public class GroundItems {

    @Inject
    Client client;

    @Inject
    RuneLiteUtil runeLiteUtil;

    @Inject
    private ItemManager itemManager;

    private final Table<WorldPoint, Integer, GroundItem> collectedGroundItems = HashBasedTable.create();


    public List<GroundItem> filter(Predicate<GroundItem> p) {
        List<GroundItem> result = new ArrayList<>();
        for (GroundItem item : collectedGroundItems.values()) {
            if (item == null) {
                continue;
            }
            if (!p.test(item)) {
                continue;
            }
            result.add(item);
        }
        return result;
    }

    public void onGameStateChanged(final GameStateChanged event) {
        if (event.getGameState() == GameState.LOADING) {
            collectedGroundItems.clear();
        }
    }

    @Subscribe
    public void onItemSpawned(ItemSpawned itemSpawned) {
        TileItem item = itemSpawned.getItem();
        Tile tile = itemSpawned.getTile();

        GroundItem groundItem = buildGroundItem(tile, item);
        GroundItem existing = collectedGroundItems.get(tile.getWorldLocation(), item.getId());
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + groundItem.getQuantity());
            // The spawn time remains set at the oldest spawn
        } else {
            collectedGroundItems.put(tile.getWorldLocation(), item.getId(), groundItem);
        }
    }

    @Subscribe
    public void onItemDespawned(ItemDespawned itemDespawned) {
        TileItem item = itemDespawned.getItem();
        Tile tile = itemDespawned.getTile();

        GroundItem groundItem = collectedGroundItems.get(tile.getWorldLocation(), item.getId());
        if (groundItem == null) {
            return;
        }

        if (groundItem.getQuantity() <= item.getQuantity()) {
            collectedGroundItems.remove(tile.getWorldLocation(), item.getId());
            log.info("removed " + itemDespawned.getItem().getId());
        } else {
            groundItem.setQuantity(groundItem.getQuantity() - item.getQuantity());
            // When picking up an item when multiple stacks appear on the ground,
            // it is not known which item is picked up, so we invalidate the spawn
            // time
            groundItem.setSpawnTime(null);
        }
    }

    private GroundItem buildGroundItem(final Tile tile, final TileItem item) {
        // Collect the data for the item
        final int itemId = item.getId();
        final ItemComposition itemComposition = itemManager.getItemComposition(itemId);
        final int realItemId = itemComposition.getNote() != -1 ? itemComposition.getLinkedNoteId() : itemId;
        final int alchPrice = itemComposition.getHaPrice();

        final GroundItem groundItem = GroundItem.builder()
                .id(itemId)
                .location(tile.getWorldLocation())
                .itemId(realItemId)
                .quantity(item.getQuantity())
                .name(itemComposition.getName())
                .haPrice(alchPrice)
                .height(tile.getItemLayer().getHeight())
                .tradeable(itemComposition.isTradeable())
                .spawnTime(Instant.now())
                .stackable(itemComposition.isStackable())
                .build();

        // Update item price in case it is coins
        if (realItemId == COINS) {
            groundItem.setHaPrice(1);
            groundItem.setGePrice(1);
        } else {
            groundItem.setGePrice(itemManager.getItemPrice(realItemId));
        }

        return groundItem;
    }

}
