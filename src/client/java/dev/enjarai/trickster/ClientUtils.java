package dev.enjarai.trickster;

import dev.enjarai.trickster.cca.ModGlobalComponents;
import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.net.SubscribeToPoolPacket;
import dev.enjarai.trickster.spell.mana.SharedManaPool;
import net.minecraft.client.MinecraftClient;

public class ClientUtils {
    public static void trySubscribe(ManaComponent manaComponent) {
        // if ever run on the server, will fail -- consider putting a try-catch if it causes an issue with a mod?
        if (manaComponent.pool() instanceof SharedManaPool sharedPool &&
                MinecraftClient.getInstance().world != null &&
                ModGlobalComponents.SHARED_MANA.get(MinecraftClient.getInstance().world.getScoreboard()).get(sharedPool.uuid()).isEmpty()) {
            ModNetworking.CHANNEL.clientHandle().send(new SubscribeToPoolPacket(sharedPool.uuid()));
        }
    }
}
