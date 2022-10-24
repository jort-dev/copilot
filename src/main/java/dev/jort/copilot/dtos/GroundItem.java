package dev.jort.copilot.dtos;


import lombok.Builder;
import lombok.Data;
import net.runelite.api.coords.WorldPoint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

@Data
@Builder
public
class GroundItem {
    private int id;
    private int itemId;
    private String name;
    private int quantity;
    private WorldPoint location;
    private int height;
    private int haPrice;
    private int gePrice;
    private int offset;
    private boolean tradeable;
    @Nullable
    private Instant spawnTime;
    private boolean stackable;

    int getHaPrice() {
        return haPrice * quantity;
    }

    int getGePrice() {
        return gePrice * quantity;
    }
}
