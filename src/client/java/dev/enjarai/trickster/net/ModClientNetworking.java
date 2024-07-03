package dev.enjarai.trickster.net;

import dev.enjarai.trickster.mixin.client.WorldRendererAccessor;
import net.minecraft.util.math.ChunkSectionPos;

public class ModClientNetworking {
    @SuppressWarnings("resource")
    public static void register() {
        ModNetworking.CHANNEL.registerClientbound(RebuildChunkPacket.class, (message, access) -> {
            var pos = message.pos();

            ChunkSectionPos.forEachChunkSectionAround(pos, chunk ->
                    ((WorldRendererAccessor) access.runtime().worldRenderer)
                            .trickster$scheduleChunkRender(
                                    ChunkSectionPos.unpackX(chunk),
                                    ChunkSectionPos.unpackY(chunk),
                                    ChunkSectionPos.unpackZ(chunk),
                                    true
                            ));
        });
    }
}
