package dev.enjarai.trickster.mixin.client.sodium;

import dev.enjarai.trickster.cca.ModChunkCumponents;
import dev.enjarai.trickster.cca.ShadowDisguiseMapComponent;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.EmptyChunk;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldSlice.class)
public abstract class WorldSliceMixin {
    @Shadow
    @Final
    private ClientWorld world;

    @Unique
    private final Long2ObjectOpenHashMap<ShadowDisguiseMapComponent> componentCache = new Long2ObjectOpenHashMap<>();

    @Inject(
            method = "getBlockState(III)Lnet/minecraft/block/BlockState;",
            at = @At(
                    value = "FIELD",
                    target = "Lme/jellysquid/mods/sodium/client/world/WorldSlice;originX:I"
            ),
            cancellable = true
    )
    private void disguiseBlockState(int x, int y, int z, CallbackInfoReturnable<BlockState> cir) {
        var chunkX = x >> 4;
        var chunkZ = z >> 4;
        var chunkId = ((long) chunkX) << 32 | ((long) chunkZ);

        var disguises = componentCache.get(chunkId);
        if (disguises == null) {
            var chunk = world.getChunk(chunkX, chunkZ);

            if (chunk != null && !(chunk instanceof EmptyChunk)) {
                disguises = ModChunkCumponents.SHADOW_DISGUISE_MAP.get(chunk);
            }

            if (disguises != null) {
                componentCache.put(chunkId, disguises);
            }
        }

        if (disguises != null) {
            var funnyState = disguises.getFunnyState(x, y, z);

            if (funnyState != null) {
                cir.setReturnValue(funnyState);
            }
        }
    }
}
