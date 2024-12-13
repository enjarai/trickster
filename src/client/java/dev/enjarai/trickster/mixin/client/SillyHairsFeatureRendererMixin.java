package dev.enjarai.trickster.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import dev.enjarai.trickster.pond.DisguisePlayerDuck;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import nl.enjarai.cicada.util.SillyHairsFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SillyHairsFeatureRenderer.class)
public class SillyHairsFeatureRendererMixin {
    @ModifyExpressionValue(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V", at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getGameProfile()Lcom/mojang/authlib/GameProfile;"
            )
    )
    private GameProfile fixSillyHairs(GameProfile original, @Local(argsOnly = true) AbstractClientPlayerEntity player) {
        DisguisePlayerDuck theFunny = (DisguisePlayerDuck) player;
        var entry = theFunny.trickster$getApplicableEntry();
        if (entry != null) {
            return entry.getProfile();
        }
        return original;
    }
}
