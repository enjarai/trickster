package dev.enjarai.trickster.mixin.client.sodium;

import dev.enjarai.trickster.pond.WorldlyRenderContextDuck;
import net.caffeinemc.mods.sodium.client.world.cloned.ChunkRenderContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChunkRenderContext.class)
public class ChunkRenderContextMixin implements WorldlyRenderContextDuck {
    @Unique
    private World world;

    @Override
    public World trickster$getWorld() {
        return world;
    }

    @Override
    public void trickster$setWorld(World world) {
        this.world = world;
    }
}
