package dev.enjarai.trickster.mixin.client;

import dev.enjarai.trickster.cca.ModChunkCumponents;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.EmptyChunk;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.jellysquid.mods.sodium.client.world.WorldSlice.getLocalBlockIndex;
import static me.jellysquid.mods.sodium.client.world.WorldSlice.getLocalSectionIndex;

@Debug(export = true) //TODO: remove when done
@Mixin(WorldSlice.class)
public abstract class SodiumLevelSliceMixin {
    @Shadow
    @Final
    private static BlockState EMPTY_BLOCK_STATE;

    @Shadow
    @Final
    private ClientWorld world;
    @Shadow
    @Final
    private BlockState[][] blockArrays;
    @Shadow
    @Final
    private BlockBox volume;
    @Shadow
    private int originX;
    @Shadow
    private int originY;
    @Shadow
    private int originZ;

    @Inject(
            method = "getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void disguiseBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (!this.volume.contains(x, y, z)) {
            cir.setReturnValue(EMPTY_BLOCK_STATE);
        } else {
            int relX = x - this.originX;
            int relY = y - this.originY;
            int relZ = z - this.originZ;

            var chunk = world.getChunk(pos);

            if (chunk instanceof EmptyChunk) {
                return;
            }

            var disguises = ModChunkCumponents.SHADOW_DISGUISE_MAP.get(chunk); // can't cache here
            var funnyState = disguises.getFunnyState(pos);

            if (funnyState != null) {
                this.blockArrays[getLocalSectionIndex(relX >> 4, relY >> 4, relZ >> 4)]
                        [getLocalBlockIndex(relX & 15, relY & 15, relZ & 15)] = funnyState;
            }
        }
    }
}
