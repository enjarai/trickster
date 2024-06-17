package dev.enjarai.trickster.mixin.client;

import dev.enjarai.trickster.Trickster;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(
            method = "renderVignetteOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIFFIIII)V"
            )
    )
    private void changeColorWhenFrozen(DrawContext context, Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity livingEntity &&
                livingEntity.getAttributes().hasModifierForAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED, Trickster.NEGATE_ATTRIBUTE.id())) {
            context.setShaderColor(0.4f, 0.4f, 0f, 1f);
        }
    }
}
