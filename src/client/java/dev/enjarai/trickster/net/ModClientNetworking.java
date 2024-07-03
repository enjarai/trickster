package dev.enjarai.trickster.net;

import dev.enjarai.trickster.mixin.client.WorldRendererAccessor;

public class ModClientNetworking {
    @SuppressWarnings("resource")
    public static void register() {
        ModNetworking.CHANNEL.registerClientbound(RebuildChunkPacket.class, (message, access) -> {
            var pos = message.pos();
            var x = pos.getX();
            var y = pos.getY();
            var z = pos.getZ();
            for (int i = z - 1; i <= z + 1; ++i) {
                for (int j = x - 1; j <= x + 1; ++j) {
                    for (int k = y - 1; k <= y + 1; ++k) {
                        ((WorldRendererAccessor) access.runtime().worldRenderer).trickster$scheduleChunkRender(j, k, i, true);
                    }
                }
            }
        });
    }
}
