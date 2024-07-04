package dev.enjarai.trickster.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.enjarai.trickster.DisguiseUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export = true)
@Mixin(targets = "net/minecraft/client/render/BackgroundRenderer$StatusEffectFogModifier")
public interface BackgroundRenderer_StatusEffectFogModifierMixin {
    @Shadow
    RegistryEntry<StatusEffect> getStatusEffect();

    @Shadow boolean shouldApply(LivingEntity entity, float tickDelta);

    @ModifyReturnValue(
            method = "shouldApply(Lnet/minecraft/entity/LivingEntity;F)Z",
            at = @At("RETURN")
    )
    default boolean applyBlindnessWhenInShadowBlock(boolean original) {
        return original || (getStatusEffect().equals(StatusEffects.BLINDNESS) && inShadowBlock());
    }

    @ModifyReturnValue(
            method = "applyColorModifier(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/effect/StatusEffectInstance;FF)F",
            at = @At("RETURN")
    )
    default float applyColorModifier(float original, LivingEntity entity, StatusEffectInstance effect, float f, float tickDelta) {
        if (this.shouldApply(entity, tickDelta)) {
            return 0.0f;
        }

        return original;
    }

    @Unique
    default boolean inShadowBlock() {
        var client = MinecraftClient.getInstance();
        var world = client.world;
        var camera = client.cameraEntity;

        if (world == null)
            return false;

        if (camera == null)
            return false;

        return DisguiseUtil.inShadowBlock(world, camera.getBlockPos());
    }
}