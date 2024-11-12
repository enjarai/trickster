package dev.enjarai.trickster.util;

import dev.enjarai.trickster.cca.SharedManaComponent;
import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.net.SubscribeToPoolPacket;
import dev.enjarai.trickster.spell.mana.SharedManaPool;

public class ClientUtils {
    public static void trySubscribe(ManaComponent manaComponent) {
        // if ever run on the server, will fail -- consider putting a try-catch if it causes an issue with a mod?
        if (manaComponent.pool() instanceof SharedManaPool sharedPool && SharedManaComponent.getInstance().get(sharedPool.uuid()).isEmpty()) {
            ModNetworking.CHANNEL.clientHandle().send(new SubscribeToPoolPacket(sharedPool.uuid()));
        }
    }
}
