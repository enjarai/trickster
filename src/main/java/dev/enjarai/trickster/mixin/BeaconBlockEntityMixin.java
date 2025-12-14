package dev.enjarai.trickster.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.enjarai.trickster.block.LightBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/DyeColor;getEntityColor()I"))
    private static int colorBlockColor(DyeColor instance, Operation<Integer> original, @Local(ordinal = 1) BlockPos blockPos, @Local(argsOnly = true) World world) {
        if (world.getBlockEntity(blockPos) instanceof LightBlockEntity color) {
            return color.colors[0];
        }
        return original.call(instance);
    }
}
