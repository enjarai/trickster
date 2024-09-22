package dev.enjarai.trickster.mixin.client.sodium;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.enjarai.trickster.pond.WorldlyRenderContextDuck;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.caffeinemc.mods.sodium.client.world.cloned.ChunkRenderContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelSlice.class)
public abstract class LevelSliceMixin {
    @ModifyReturnValue(
            method = "prepare",
            at = @At(
                    value = "RETURN",
                    ordinal = 1
            ),
            remap = false
    )
    private static ChunkRenderContext addWorldContext(ChunkRenderContext original, @Local(argsOnly = true) World world) {
        ((WorldlyRenderContextDuck) original).trickster$setWorld(world);
        return original;
    }
}
