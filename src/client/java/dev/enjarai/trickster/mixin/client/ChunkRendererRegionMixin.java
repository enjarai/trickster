package dev.enjarai.trickster.mixin.client;

import dev.enjarai.trickster.cca.ModChunkComponents;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.EmptyChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkRendererRegion.class)
public abstract class ChunkRendererRegionMixin {
    @Shadow
    protected final World world;

    protected ChunkRendererRegionMixin(World world) {
        this.world = world;
    }

    @Inject(method = "getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", at = @At(value = "HEAD"), cancellable = true)
    private void getDisguiseBlockState(BlockPos pos, CallbackInfoReturnable cir) {
        var chunk = world.getChunk(pos);

        if (!(chunk instanceof EmptyChunk)) {
            var comp = ModChunkComponents.SHADOW_DISGUISE_MAP.get(chunk);

            //TODO: not returning true, ever?
            if (comp.value().containsKey(pos)) {
                cir.setReturnValue(comp.value().get(pos).getDefaultState());
            }
        }
    }
}
