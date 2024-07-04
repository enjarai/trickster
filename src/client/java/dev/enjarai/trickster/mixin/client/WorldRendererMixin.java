package dev.enjarai.trickster.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.enjarai.trickster.DisguiseUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @ModifyReturnValue(method = "hasBlindnessOrDarkness(Lnet/minecraft/client/render/Camera;)Z", at = @At("RETURN"))
    private boolean applyBlindnessWhenInShadowBlock(boolean original, Camera camera) {
        return original || inShadowBlock(camera);
    }

    @Unique
    private boolean inShadowBlock(Camera camera) {
        if (camera == null)
            return false;

        var world = MinecraftClient.getInstance().world;

        if (world == null)
            return false;

        return DisguiseUtil.inShadowBlock(world, camera.getBlockPos());
    }
}
