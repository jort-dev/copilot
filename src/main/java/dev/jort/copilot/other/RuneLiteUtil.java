package dev.jort.copilot.other;

import net.runelite.api.Client;
import net.runelite.api.ItemComposition;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RuneLiteUtil {

    @Inject
    Client client;

    public String getItemName(int itemId) {
        ItemComposition itemComposition = client.getItemDefinition(itemId);
        return itemComposition.getName();
    }

}
