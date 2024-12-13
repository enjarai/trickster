package dev.enjarai.trickster.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.TrickHatItem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(
            method = "renderVignetteOverlay", at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIFFIIII)V"
            )
    )
    private void changeColorWhenFrozen(DrawContext context, Entity entity, CallbackInfo ci) {
        if (
            entity instanceof LivingEntity livingEntity &&
                    livingEntity.getAttributes().hasModifierForAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED, Trickster.NEGATE_ATTRIBUTE.id())
        ) {
            context.setShaderColor(0.4f, 0.4f, 0f, 1f);
        }
    }

    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;"))
    private void renderHatHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci, @Local PlayerEntity player) {
        var hatStack = player.getOffHandStack();
        if (hatStack.isIn(ModItems.HOLDABLE_HAT)) {
            var matrices = context.getMatrices();
            matrices.push();
            matrices.translate(0, 0, -500);

            var middle = context.getScaledWindowWidth() / 2;
            var x = player.getMainArm() == Arm.RIGHT ? middle - 109 - 8 : middle + 109 - 8;
            var y = context.getScaledWindowHeight() - 40;

            for (int i = 0; i < 3; i++) {
                var offset = i - 1;
                matrices.push();
                matrices.translate(0, 0, -Math.abs(offset));

                var scrollStack = TrickHatItem.getScrollRelative(hatStack, offset);
                var brightness = offset == 0 ? 1f : 0.6f;
                RenderSystem.setShaderColor(brightness, brightness, brightness, brightness);

                context.drawItem(scrollStack, x + offset * 8, y);

                matrices.pop();
            }

            RenderSystem.setShaderColor(1, 1, 1, 1);
            matrices.pop();
        }
    }
}
