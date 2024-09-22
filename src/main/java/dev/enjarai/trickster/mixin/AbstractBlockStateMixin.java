package dev.enjarai.trickster.mixin;

import dev.enjarai.trickster.cca.ModChunkComponents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {
    @Inject(
            method = "getMapColor(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/MapColor;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void disguiseBlockOnMap(BlockView view, BlockPos pos, CallbackInfoReturnable<MapColor> cir) {
        if (view instanceof ServerWorld world) {
            var chunk = world.getChunk(pos);
            var component = ModChunkComponents.SHADOW_DISGUISE_MAP.getNullable(chunk);
            if (component != null) {
                var disguise = component.getFunnyState(pos);
                if (disguise != null) {
                    cir.setReturnValue(disguise.getBlock().getDefaultMapColor());
                }
            }
        }
    }
}
