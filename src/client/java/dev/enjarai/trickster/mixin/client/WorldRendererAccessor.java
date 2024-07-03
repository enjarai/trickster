package dev.enjarai.trickster.mixin.client;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {
    @Invoker("scheduleChunkRender")
    void trickster$scheduleChunkRender(int x, int y, int z, boolean important);
}
