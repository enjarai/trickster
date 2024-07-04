package dev.enjarai.trickster.mixin.client;

import com.google.common.base.Suppliers;
import dev.enjarai.trickster.cca.ModChunkCumponents;
import dev.enjarai.trickster.cca.ShadowDisguiseMapComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(WorldSlice.class)
public abstract class SodiumLevelSliceMixin {
    @Shadow
    private ClientWorld world;
    @Shadow
    private int originX;
    @Shadow
    private int originY;
    @Shadow
    private int originZ;
    @Unique
    private final Supplier<ShadowDisguiseMapComponent> disguises =
            Suppliers.memoize(() -> ModChunkCumponents.SHADOW_DISGUISE_MAP.getNullable(world.getChunk(new BlockPos(originX, originY, originZ))));

    public SodiumLevelSliceMixin(ClientWorld world) {
        this.world = world;
    }

    @Inject(
            method = "getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void disguiseBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {

    }
}
